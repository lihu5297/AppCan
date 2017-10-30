package org.zywx.cooldev.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;
import org.zywx.appdo.facade.omm.entity.tenant.Enterprise;
import org.zywx.appdo.facade.omm.service.tenant.TenantFacade;
import org.zywx.appdo.facade.user.entity.organization.Personnel;
import org.zywx.appdo.facade.user.service.organization.PersonnelFacade;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.commons.Enums.USER_LEVEL;
import org.zywx.cooldev.commons.Enums.USER_TYPE;
import org.zywx.cooldev.dao.UserActiveToolDao;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.GitAuthVO;
import org.zywx.cooldev.entity.app.GitOwnerAuthVO;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.MD5Util;
import org.zywx.cooldev.util.emm.TokenUtilProduct;
import org.zywx.cooldev.util.mail.base.MailSenderInfo;
import org.zywx.cooldev.util.mail.base.SendMailTools;
import org.zywx.cooldev.vo.AskUserVO;

import net.sf.json.JSONObject;

@Service
public class TeamMemberService extends BaseService {
	
	@Value("${xietongHost}")
	private String xietongHost;
	
	@Value("${tenantId}")
	private String tenantId;
	
	@Value("${key}")
	private String key;
	
	@Autowired(required=false)
	private TenantFacade tenantFacade;
	
	//企业版还是大众版标识
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	@Autowired
	private SendMailTools sendMailTool;
	
	@Autowired
	private UserActiveToolDao userActiveToolDao;
	
	@Autowired(required=false)
	private PersonnelFacade personnelFacade;

	@Autowired
	private TeamService teamService;
	
	@Autowired
	protected AppService appService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private TeamAuthService teamAuthService;
	
	@Value("${emm3Url}")
	private String emm3Url;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	/**
	    * @Title: deleteMemberByTeamIdAndUserId
	    * @Description: 从团队中删除某个成员
	    * @param @param teamId
	    * @param @param userId    参数
	    * @return void    返回类型
		* @user wjj
		* @date 2015年8月13日 上午11:14:39
	    * @throws
	 */
	public TeamMember deleteMemberById(long teamMemberId,String token){
		TeamMember tm = this.teamMemberDao.findOne(teamMemberId);
		tm.setDel(DELTYPE.DELETED);
		this.teamMemberDao.save(tm);
		List<TeamMember> listCrt = teamMemberDao.findByTeamIdAndTypeAndDel(tm.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
		long teamCreatorUserId = 0l;
		if(null!=listCrt){
			teamCreatorUserId = listCrt.get(0).getUserId();
		}
		TeamAuth teamAuth = this.teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
		teamAuth.setDel(DELTYPE.DELETED);
		this.teamAuthDao.save(teamAuth);
		//如果被删除的人是团队管理员,则需要将此人创建的团队项目的创建者变为团队创建者
		List<Project> listPrj = projectDao.findByTeamIdAndDel(tm.getTeamId(), DELTYPE.NORMAL);
		if(null!=listPrj && listPrj.size()>0){
			for(Project p:listPrj){
				ProjectMember pm = projectMemberDao.findByProjectIdAndTypeAndDel(p.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
				List<ProjectMember> listMem = projectMemberDao.findByProjectIdAndUserIdAndDel(p.getId(), teamCreatorUserId, DELTYPE.NORMAL);
				
				if(pm.getUserId()==tm.getUserId().longValue()){
					//如果被删除的人是团队项目的创建者,将此项目的创建者改为团队创建者
					pm.setUserId(teamCreatorUserId);
					projectMemberDao.save(pm);
					
					if(null!=listMem && listMem.size()>0){//如果团队创建者以前就曾参与该项目,则需要将以前的成员信息删除
						for(ProjectMember pm1:listMem){
							pm1.setDel(DELTYPE.DELETED);
							projectMemberDao.save(pm1);
							List<ProjectAuth> listAuth = projectAuthDao.findByMemberIdAndDel(pm1.getId(), DELTYPE.NORMAL);
							if(null!=listAuth && listAuth.size()>0){
								for(ProjectAuth auth:listAuth){
									auth.setDel(DELTYPE.DELETED);
									projectAuthDao.save(auth);
								}
							}
						}
					}
				}
			}
		}
		
		
		deleteEmmUser(tm.getTeamId(), tm.getUserId(),token);
		User user = userDao.findOne(tm.getUserId());
		
		
		//-------------------------------------git权限-------------------------------------
		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
		List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
		boolean isAdministrator=false;//标识被删除人是否为团队的管理员
		if(teamAuth.getRoleId()==Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId()){
			isAdministrator=true;
		}
		if(isAdministrator){//退出人是团队管理员,需要判断此人是否参与了团队下的项目,如果没有参与,则应该设定此人无权访问对应项目下的应用git权限
			Role role = Cache.getRole(teamAuth.getRoleId());
			List<Permission> listPermission = role.getPermissions();
			boolean uploadMasterOrBranch = false;//标识此人在团队中是否有上传主干或者分支的权限
			if(null!=listPermission && listPermission.size()>0){
				for(Permission p :listPermission){
					if(p.getEnName().equals("code_upload_master_code") || p.getEnName().equals("code_update_branch")){
						uploadMasterOrBranch = true;
						break;
					}
				}
			}
			if(uploadMasterOrBranch){
				List<Project> projects = this.projectDao.findByTeamIdAndDel(tm.getTeamId(), DELTYPE.NORMAL);
				for(Project project : projects){
					List<ProjectMember> projectMembers = this.projectMemberDao.findByProjectIdAndUserIdAndDel(project.getId(), tm.getUserId(), DELTYPE.NORMAL);
					boolean minusGitAuth = true;//是否减去git访问权限(此人是否实质参与项目)
					for(ProjectMember projectMember :projectMembers){
						if(projectMember.getUserId()==tm.getUserId().longValue()){
							minusGitAuth = false;
							break;
						}
					}
					if(minusGitAuth){//如果此人没有实质参与到对应的项目中,则除去对应的权限
						List<App> listApp = appDao.findByProjectIdAndDel(project.getId(),DELTYPE.NORMAL);
						for(App app:listApp){
							if(app.getUserId()!=tm.getUserId().longValue()){
								GitAuthVO vo = new GitAuthVO();
//								vo.setAuthflag("all");
								vo.setPartnername(user.getAccount());
								vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
								vo.setProject(encodeKey.toLowerCase());
								vo.setProjectid(app.getAppcanAppId());
//								vo.setRef("master");
								listAuth.add(vo);
							}else{
								GitOwnerAuthVO vo = new GitOwnerAuthVO();
								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
								vo.setProject(encodeKey.toLowerCase());
								vo.setUsername(user.getAccount());
								User other = new User();//应用要转给谁
								if(project.getType().equals(PROJECT_TYPE.TEAM)){
									List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
									if(null!=listTm && listTm.size()>0){
										other = userDao.findOne(listTm.get(0).getUserId());
									}
								}else{
									ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
									if(null!=pmCrt){
										other = userDao.findOne(pmCrt.getUserId());
									}
								}
								vo.setOther(other.getAccount());
								app.setUserId(other.getId());
								vo.setProjectid(app.getAppcanAppId());
								appDao.save(app);
								changeOwnerAuth.add(vo);
							}
						}
						
					}
				}
			}
			
		}
		//删除git权限(项目下的应用不是被删除人创建的应用)
		Map<String,String> map = appService.delGitAuth(listAuth);
		log.info(user.getAccount()+" was by deleted from team id:->"+tm.getTeamId()+" ,and delGitAuth->"+(null==map?null:map.toString()));
		//转让git权限(项目下的应用是被删除人创建的应用)
		map = appService.updateGitAuth(changeOwnerAuth);
		log.info(user.getAccount()+"was by deleted from team id:"+tm.getTeamId()+" ,and updateGitAuth->"+(null==map?null:map.toString()));
		//-------------------------------------git权限-------------------------------------
		return tm;
	}
	
	/**
	    * @Title: updateGroupByTeamIdAndUserId
	    * @Description:将用户从一个小组移到另一个小组 
	    * @param @param teamId
	    * @param @param userId
	    * @param @param groupId
	    * @param @return    参数
	    * @return int    返回类型
		* @user wjj
		* @date 2015年8月13日 上午11:40:25
	    * @throws
	 */
	/*public int updateGroupByTeamIdAndUserId(long teamId,long userId,long groupId){
		return this.jdbcTpl.update("update T_TEAM_MEMBER SET groupId=? WHERE teamId=? and userId = ?", new Object[]{groupId,teamId,userId});
	}*/
	
	/**
	 * 退出某个团队
	 * @param teamId
	 * @param userId
	 * @return boolean
	 * @user jingjian.wu
	 * @date 2015年9月6日 下午6:28:06
	 * @throws
	 */
	public boolean delExitTeam(long teamId,long userId,String token){
		TeamMember teamMember = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, userId, DELTYPE.NORMAL);
		TeamAuth ta = this.teamAuthDao.findByMemberIdAndDel(teamMember.getId(), DELTYPE.NORMAL);
		ta.setDel(DELTYPE.DELETED);
		this.teamAuthDao.save(ta);
		boolean isAdministrator=false;//标识退出团队的人是否为团队的管理员
		if(ta.getRoleId()==Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId()){
			isAdministrator=true;
		}
		teamMember.setDel(DELTYPE.DELETED);
		this.teamMemberDao.save(teamMember);
		User user = userDao.findOne(userId);
		//项目创建者转为管理员
		List<Project> projects = this.projectDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
		for(Project project : projects){
			List<ProjectMember> projectMembers = this.projectMemberDao.findByProjectIdAndUserIdAndDel(project.getId(), userId, DELTYPE.NORMAL);
			for(ProjectMember projectMember :projectMembers){
				List<ProjectAuth> auths = this.projectAuthDao.findByMemberIdAndDel(projectMember.getId(), DELTYPE.NORMAL);
				if(projectMember.getType().equals(PROJECT_MEMBER_TYPE.CREATOR)){//如果此人是项目创建者,变为项目管理员
					projectMember.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
					projectMemberDao.save(projectMember);
					for(ProjectAuth auth : auths){
						auth.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR).getId());
						this.projectAuthDao.save(auth);
					}
					
					
					//此项目没有了创建者,需要将团队的创建者指定为项目的创建者
					ProjectMember pm = new ProjectMember();
					pm.setProjectId(projectMember.getProjectId());
					pm.setType(PROJECT_MEMBER_TYPE.CREATOR);
					//获取团队创建者
					List<TeamMember> listMem = teamMemberDao.findByTeamIdAndTypeAndDel(teamId, TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
					if(null!=listMem && listMem.size()>0){
						TeamMember teamCrt = listMem.get(0);

						//判断团队的创建者是否已经在此项目成员中,如果已存在,则先删除
						for(ProjectMember projectMemberTmp :projectMembers){
							if(projectMemberTmp.getUserId()==teamCrt.getUserId()){
								projectMemberTmp.setDel(DELTYPE.DELETED);
								projectMemberDao.save(projectMemberTmp);
								List<ProjectAuth> paList = projectAuthDao.findByMemberIdAndDel(projectMemberTmp.getId(), DELTYPE.NORMAL);
								if(null!=paList && paList.size()>0){
									for(ProjectAuth pa:paList){
										pa.setDel(DELTYPE.DELETED);
										projectAuthDao.save(pa);
									}
								}
							}
						}
						
						pm.setUserId(teamCrt.getUserId());
						projectMemberDao.save(pm);
						ProjectAuth pa = new ProjectAuth();
						pa.setMemberId(pm.getId());
						pa.setRoleId(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR).getId());
						projectAuthDao.save(pa);
					}
				}
			}
		}
			//-------------------------------------git权限-------------------------------------
			List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
			List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
			if(isAdministrator){//退出人是团队管理员,需要判断此人是否参与了团队下的项目,如果没有参与,则应该设定此人无权访问对应项目下的应用git权限
				Role role = Cache.getRole(ta.getRoleId());
				List<Permission> listPermission = role.getPermissions();
				boolean uploadMasterOrBranch = false;//标识此人在团队中是否有上传主干或者分支的权限
				if(null!=listPermission && listPermission.size()>0){
					for(Permission p :listPermission){
						if(p.getEnName().equals("code_upload_master_code") || p.getEnName().equals("code_update_branch")){
							uploadMasterOrBranch = true;
							break;
						}
					}
				}
				if(uploadMasterOrBranch){
					List<Project> projectss = this.projectDao.findByTeamIdAndDel(teamMember.getTeamId(), DELTYPE.NORMAL);
					for(Project project : projectss){
						List<ProjectMember> projectMembers = this.projectMemberDao.findByProjectIdAndUserIdAndDel(project.getId(), teamMember.getUserId(), DELTYPE.NORMAL);
						boolean minusGitAuth = true;//是否减去git访问权限(此人是否实质参与项目)
						for(ProjectMember projectMember :projectMembers){
							if(projectMember.getUserId()==teamMember.getUserId().longValue()){
								minusGitAuth = false;
								break;
							}
						}
						if(minusGitAuth){//如果此人没有实质参与到对应的项目中,则除去对应的权限
							List<App> listApp = appDao.findByProjectIdAndDel(project.getId(),DELTYPE.NORMAL);
							for(App app:listApp){
								if(app.getUserId()!=teamMember.getUserId().longValue()){
									GitAuthVO vo = new GitAuthVO();
//									vo.setAuthflag("all");
									vo.setPartnername(user.getAccount());
									vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
									String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
									vo.setProject(encodeKey.toLowerCase());
									vo.setProjectid(app.getAppcanAppId());
//									vo.setRef("master");
									listAuth.add(vo);
								}else{
									GitOwnerAuthVO vo = new GitOwnerAuthVO();
									String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
									vo.setProject(encodeKey.toLowerCase());
									vo.setUsername(user.getAccount());
									User other = new User();//应用要转给谁
									if(project.getType().equals(PROJECT_TYPE.TEAM)){
										List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
										if(null!=listTm && listTm.size()>0){
											other = userDao.findOne(listTm.get(0).getUserId());
										}
									}else{
										ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
										if(null!=pmCrt){
											other = userDao.findOne(pmCrt.getUserId());
										}
									}
									vo.setOther(other.getAccount());
									app.setUserId(other.getId());
									vo.setProjectid(app.getAppcanAppId());
									appDao.save(app);
									changeOwnerAuth.add(vo);
								}
							}
							
						}
					}
				}
				
			}
			//删除git权限(项目下的应用不是被删除人创建的应用)
			Map<String,String> map = appService.delGitAuth(listAuth);
			log.info(user.getAccount()+" was by deleted from team id:->"+teamMember.getTeamId()+" ,and delGitAuth->"+(null==map?null:map.toString()));
			//转让git权限(项目下的应用是被删除人创建的应用)
			map = appService.updateGitAuth(changeOwnerAuth);
			log.info(user.getAccount()+"was by deleted from team id:"+teamMember.getTeamId()+" ,and updateGitAuth->"+(null==map?null:map.toString()));
			//-------------------------------------git权限-------------------------------------
			
		
		
		deleteEmmUser(teamId, userId,token);
		return true;
	}
	
	public void deleteEmmUser(long teamId,long userId,String token){
		/*log.info("prepare delete EMM User:userId->"+userId+",teamId->"+teamId+",token->"+token);
		Team team = teamDao.findOne(teamId);
		if(team.getType().equals(TEAMTYPE.ENTERPRISE)||team.getType().equals(TEAMTYPE.UNBINDING)){
			//如果是授权通过的项目,还需要依情况看是否需要调用EMM接口,删除其对应的用户信息
			List<User> list = this.teamService.findAllUserBelongTeam(teamId);
			log.info("team member size: -- >"+list.size());
			if(null!=list){
				for(User u :list){
					log.info("already in team user:--->"+u.getId());
				}
			}
			boolean flag = false;
			boolean delFlag = false;
			if(null!=list && list.size()>0){
				for(User u:list){
					if(u.getId().longValue()==userId){
						flag = true;
						log.info("don't to delete emm user");
						break;
					}
				}
				if(!flag){
					//调用EMM删除
					delFlag = true;
				}
			}else{
				//调用EMM删除
				delFlag = true;
			}
			log.info("judge deleteEmm user -->"+delFlag);
			if(delFlag){
				log.info("=====begin delete emm user=======:");
				TeamMember tmCreator = findMemberByTeamIdAndMemberType(teamId, TEAMREALTIONSHIP.CREATE);
				User userTeamCreator = userDao.findOne(tmCreator.getUserId());
				User delUser = userDao.findOne(userId);
				log.info(" deleteTeamUser params:token:"+token+",teamId:"+team.getUuid()+",actorUser:"+userTeamCreator.getAccount()+",delUser:"+delUser.getAccount());
				String resultFlag = "";
				if(serviceFlag.equals("online")){//大众版
					resultFlag = personnelFacade.deleteAdminUser(token, team.getUuid(), delUser.getAccount());
					log.info("返回:"+resultFlag);
				}else if(serviceFlag.equals("enterprise")){//企业版
					resultFlag = personnelFacade.deleteTeamUser(token, team.getUuid(), delUser.getAccount());
					log.info("返回:"+resultFlag);
				}
				if(serviceFlag.equals("online") || serviceFlag.equals("enterprise")){//非EMM3
					if(StringUtils.isNotBlank(resultFlag)){
						throw new RuntimeException("EMM del user failed,"+resultFlag);
					}
				}
				
			}
		}*/
	}
	
	/**
	 * @throws MessagingException 
	 * @throws UnsupportedEncodingException 
	    * @Title: addMember
	    * @Description: 团队邀请成员
	    * @param @param listUser
	    * @param @param teamId
	    * @param @param groupId
	    * @param @param content    参数
	    * @return void    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午4:46:11
	    * @throws
	 */
	public Map<String, Object> addMember(Long loginUserId,List<AskUserVO> listUser,long teamId,long groupId,String content) throws UnsupportedEncodingException, MailSendException,MessagingException{
		Map<String, Object> map = new HashMap<String, Object>();
		List<TeamMember> addedUsers = new ArrayList<TeamMember>();
		List<String> sendEmailMessage = new ArrayList<>();
		for(AskUserVO vo:listUser){
			User user = this.userDao.findByAccountAndDel(vo.getEmail(), DELTYPE.NORMAL);
			TeamMember tm = new TeamMember();
			tm.setGroupId(groupId);
			tm.setTeamId(teamId);
			//设置团队成员关系为:受邀请用户
			tm.setType(Enums.TEAMREALTIONSHIP.ASK);
			
			if(null==user){//通过email判断,该user并非本系统用户
				Matcher matcher = pattern.matcher(vo.getEmail());
				log.info("email is right:"+matcher.matches());
				if(!matcher.matches()){
					sendEmailMessage.add(vo.getEmail()+"邮箱格式不对");
					continue;
				}
				user = new User();
				user.setAccount(vo.getEmail());
				user.setEmail(vo.getEmail());
				user.setUserName(vo.getEmail());
				user.setUserlevel(USER_LEVEL.ADVANCE);
				/**
				 * 应该调用AppCan接口,查询这个用户是否是注册用户，
				 * 1.如果是非注册用户，那么类型为Enums.USER_STATUS.NOREGISTER.getVal()
				 * 2.入股是注册用户,但是未认证开发者,那么类型为Enums.USER_STATUS.NOAUTHENTICATION.getVal()
				 * 3.如果是注册用户,并且已经是认证开发者,那么类型为Enums.USER_STATUS.AUTHENTICATION.getVal()
				 */
				user.setType(USER_TYPE.NOREGISTER);
				this.userDao.save(user);
				
				log.info("团队邀请,准备给用户:"+user.getAccount()+"发送邮件");
				MailSenderInfo mailInfo = new MailSenderInfo();
				mailInfo.setContent(content+"</br>点击以下链接登录协同开发 :<a href=\""+xietongHost+"\">"+xietongHost+"</a>");
				mailInfo.setToAddress(user.getAccount());
				sendMailTool.sendMailByAsynchronousMode(mailInfo);
				
				
				//邮件发送成功则添加到team中
				tm.setUserId(user.getId());
				this.teamMemberDao.save(tm);//保存团队成员
				
			}else{
				//判断此人vo 是否已经是该团队teamId下面的成员了.如果已经是,则此操作跳过
				//如果存在此人则跳过,如果此人状态为del=1(删除状态),则需要重新添加此人
				//如果团队下面不存在此人需要添加记录到团队成员表(teamMember)
				TeamMember tmember = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, user.getId(),DELTYPE.NORMAL);
				if(null !=tmember){
					map.put(vo.getEmail(), "already is a member of the team!");
					sendEmailMessage.add(vo.getEmail()+"用户已存在");
					continue;
				}else{//邀请的人员
					log.info("团队邀请,准备给用户:"+user.getAccount()+"发送邮件");
					MailSenderInfo mailInfo = new MailSenderInfo();
					mailInfo.setContent(content+"</br>点击以下链接登录协同开发:<a href=\""+xietongHost+"\">"+xietongHost+"</a>");
					mailInfo.setToAddress(user.getAccount());
					sendMailTool.sendMailByAsynchronousMode(mailInfo);
					
					//邮件发送成功则添加到team中
					tm.setUserId(user.getId());
					this.teamMemberDao.save(tm);//保存团队成员
				}
				
			}
			String roleEnName = "";
			if(Enums.USER_ASKED_TYPE.MANAGER==vo.getUserAuth()){
				roleEnName = ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR;
			}else if(Enums.USER_ASKED_TYPE.ACTOR==vo.getUserAuth()){
				roleEnName = ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER;
			}
			TeamAuth teamAuth = new TeamAuth();
			teamAuth.setMemberId(tm.getId());
			teamAuth.setRoleId(Cache.getRole(roleEnName).getId());
			this.teamAuthDao.save(teamAuth);
			addedUsers.add(tm);
		}
		map.put("addedMembers", addedUsers);//添加进来的用户信息
		map.put("sendEmailMessage", sendEmailMessage);//添加进来的用户信息
		return map;
	}
	
	/**
	    * @Title: findGroupIdByTeamIdAndUserId
	    * @Description: 根据团队id,和用户id查找用户在此团队下,属于哪个分组,(方便在团队下面查看某个用户时候,选中对应的小组)
	    * @param @param teamId
	    * @param @param userId
	    * @param @return    参数
	    * @return long    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午9:05:09
	    * @throws
	 */
	public Long findGroupIdByTeamIdAndUserId(long teamId,long userId){
		TeamMember tm = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, userId,DELTYPE.NORMAL);
		if(null !=tm){
			return tm.getGroupId();
		}
		return null;
	}
	
	public TeamMember findMemberByTeamIdAndUserId(long teamId,long userId){
		TeamMember tm = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, userId,DELTYPE.NORMAL);
		return tm;
	}
	
	/**
	 * 改变成员所属分组
	 * @param teamMemberId
	 * @param groupId void
	 * @user jingjian.wu
	 * @date 2015年8月25日 下午4:13:26
	 * @throws
	 */
	public void updateGroup(long teamMemberId,long groupId){
		TeamMember tm = this.teamMemberDao.findOne(teamMemberId);
		tm.setGroupId(groupId);
		this.teamMemberDao.save(tm);
	}
	
	public TeamMember findOne(Long teamMemberId){
		return this.teamMemberDao.findOne(teamMemberId);
	}
	
	public List<TeamMember> findByTeamIdAndDel(long teamId,DELTYPE del){
		List<TeamMember> listTm = new ArrayList<TeamMember>();
		listTm = this.teamMemberDao.findByTeamIdAndDel(teamId, del);
		return listTm;
	}
	
	public List<TeamMember> findByTeamIdAndDelAndKeywords(long teamId,DELTYPE del,String keywords){
		List<TeamMember> listTm = new ArrayList<TeamMember>();
		listTm = this.teamMemberDao.findByTeamIdAndDelAndKeywords(teamId,del,keywords);
		return listTm;
	}

	
	public TeamMember findMemberByTeamIdAndMemberType(Long teamId, TEAMREALTIONSHIP create) {
		List<TeamMember> teamms = this.teamMemberDao.findByTeamIdAndTypeAndDel(teamId,create,DELTYPE.NORMAL);
		if(null != teamms && teamms.size() > 0){
			return teamms.get(0);
		}else
			return null;
	}
	
	/**
	 * 私有部署,从用户表中选取人加入到团队中
	 * @throws Exception 
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午8:43:27
	 */
	public Map<String, Object> addTeamMemberFromUserTable(List<String> userStrList,Long teamId,Long groupId) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		List<TeamMember> listResult = new ArrayList<TeamMember>();
		List<Long> memberUserIdArr = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
		for(String userStr : userStrList) {
			String[] items = userStr.split("#");
			if(items.length != 2) {
				continue;
			}
			String userId = items[0];
			String roleTypeStr = items[1].toUpperCase();
			if( !roleTypeStr.equals(ROLE_TYPE.ADMINISTRATOR.name())  &&
				!roleTypeStr.equals(ROLE_TYPE.MEMBER.name())) {
				continue;
			}
			
			User user = userDao.findOne(Long.parseLong(userId));
			if(user != null) {
				TeamMember teamMember = findMemberByTeamIdAndUserId(teamId, user.getId());
				if(teamMember!=null){
					continue;
				}
				
				TeamMember member = new TeamMember();
				member.setGroupId(groupId);
				member.setJoinTime(new Timestamp(System.currentTimeMillis()));
				member.setTeamId(teamId);
				member.setType(TEAMREALTIONSHIP.ACTOR);
				member.setUserId(user.getId());
				listResult.add(member);
				memberUserIdArr.add(user.getId());
				teamMemberDao.save(member);
				sb.append(null!=user.getUserName()?user.getUserName():user.getAccount()).append(",");
				TeamAuth auth = new TeamAuth();
				auth.setMemberId(member.getId());
				Role role = Cache.getRole(ENTITY_TYPE.TEAM + "_" + roleTypeStr);
				if(role == null) {
					continue;
				}
				auth.setRoleId(role.getId());
				teamAuthDao.save(auth);
				//-------------------------增加git权限---------------------begin---------------
				if(roleTypeStr.equals(ROLE_TYPE.ADMINISTRATOR.name())){//如果加进来的人是团队管理员,需要给分配git权限
					//如果是团队管理员
					List<Permission> listP = role.getPermissions();
					boolean master= false;
					boolean branch = false;
					if(null!=listP && listP.size()>0){
						for(Permission p :listP){
							if(p.getEnName().equals("code_upload_master_code")){
								 master= true;
								 break;
							}
							if(p.getEnName().equals("code_update_branch")){
								branch  = true;
							}
						}
						log.info("是否有主干权限---->"+master);
						log.info("是否有分支权限---->"+branch);
						List<App> teamAppList = appDao.findByTeamId(teamId);//团队下的应用
						if(null!=teamAppList){
							if(master){//团队管理员有主干权限的话
								for(App app:teamAppList){
									if(app.getUserId()!=user.getId().longValue()){
										
										GitAuthVO vo = new GitAuthVO();
										vo.setAuthflag("all");
										vo.setPartnername(user.getAccount());
										vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
										String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
										vo.setProject(encodeKey.toLowerCase());
										vo.setRef("master");
										vo.setProjectid(app.getAppcanAppId());
										listAuth.add(vo);
									}
								}
							}else if(branch){//团队管理员有分支权限的话
								for(App app:teamAppList){
									if(app.getUserId()!=user.getId().longValue()){
										GitAuthVO vo = new GitAuthVO();
										vo.setAuthflag("allbranch");
										vo.setPartnername(user.getAccount());
										vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
										String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
										vo.setProject(encodeKey.toLowerCase());
										vo.setProjectid(app.getAppcanAppId());
										listAuth.add(vo);
									}
								}
							}
						}
					}
					
					
				}
				//-------------------------增加git权限---------------------end---------------
				
				
				//添加团队成员到emm
				/*Personnel personnel = new Personnel();
				personnel.setName(user.getAccount());
				TeamMember teamCreator = this.findMemberByTeamIdAndMemberType(member.getTeamId(), TEAMREALTIONSHIP.CREATE);
				User teamCrt = userDao.findOne(teamCreator.getUserId());
				personnel.setCreatorId(teamCrt.getAccount());
				personnel.setTeamGroupId(teamDao.findOne(member.getTeamId()).getUuid());
				personnel.setMobileNo(user.getCellphone());
				personnel.setEmail(user.getAccount());
				personnel.setGroupName(teamDao.findOne(member.getTeamId()).getName());
				personnel.setTeamDevAddress(xietongHost);
				String token = "";
				String[] params = new String[2];
				
//				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(teamToken.getEnterpriseId());
				params[0] = tenantId;
				params[1] = "dev";
				if(!serviceFlag.equals("enterpriseEmm3")){
					token= TokenUtilProduct.getToken(key, params);
				}
				log.info("teamMember sync to EMM-->"+personnel.getName());
				String flag = "";
				if(serviceFlag.equals("online")){//线上版本
					flag = personnelFacade.createAdminUser(token, personnel);
				}else if(serviceFlag.equals("enterprise")){//企业版
					flag = personnelFacade.createTeamUser(token, personnel);
				}
				if(serviceFlag.equals("online")||serviceFlag.equals("enterprise")){//线上版本
					log.info("teamMember sync to EMM-->:"+flag);
					if(StringUtils.isNotBlank(flag)){
						throw new RuntimeException("从用户表选人加入团队,此时添加EMM成员时候失败,"+flag);
					}
				}*/
			}
		}
		
		Map<String,String> mapResult = appService.addGitAuth(listAuth);
		log.info(userStrList+" join team: teamId->"+teamId+",and shareallgit->"+(null!=mapResult?mapResult.toString():null));
		map.put("members", listResult);
		map.put("userNames", sb.toString());
		map.put("userIds", memberUserIdArr);
		
		return map;
	}

	
	public void save(TeamMember tm){
		this.teamMemberDao.save(tm);
	}

	public boolean  updateEmmInvokeDelMember(String loginName,String mobilePhone,String userName,String uuid) throws ClientProtocolException, IOException{

//		if(!validateIP(request)){
//			return this.getFailedMap("ip is not available");
//		}
		User user = userService.saveUserIfNotExist(loginName,mobilePhone,userName);
		Team team = teamService.getByUuid(uuid);
		TeamMember tm = this.findMemberByTeamIdAndUserId(team.getId(),user.getId());
		if(null!=tm){
			tm.setDel(DELTYPE.DELETED);
			this.save(tm);
			TeamAuth teamAuth = teamAuthService.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
			if(null!=teamAuth){
				teamAuth.setDel(DELTYPE.DELETED);
				teamAuthService.save(teamAuth);
			}
			//-------------------------------------git权限-------------------------------------
			List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
			List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
			boolean isAdministrator=false;//标识被删除人是否为团队的管理员
			if(teamAuth.getRoleId()==Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId()){
				isAdministrator=true;
			}
			if(isAdministrator){//退出人是团队管理员,需要判断此人是否参与了团队下的项目,如果没有参与,则应该设定此人无权访问对应项目下的应用git权限
				Role role = Cache.getRole(teamAuth.getRoleId());
				List<Permission> listPermission = role.getPermissions();
				boolean uploadMasterOrBranch = false;//标识此人在团队中是否有上传主干或者分支的权限
				if(null!=listPermission && listPermission.size()>0){
					for(Permission p :listPermission){
						if(p.getEnName().equals("code_upload_master_code") || p.getEnName().equals("code_update_branch")){
							uploadMasterOrBranch = true;
							break;
						}
					}
				}
				if(uploadMasterOrBranch){
					List<Project> projects = this.projectDao.findByTeamIdAndDel(tm.getTeamId(), DELTYPE.NORMAL);
					for(Project project : projects){
						List<ProjectMember> projectMembers = this.projectMemberDao.findByProjectIdAndUserIdAndDel(project.getId(), tm.getUserId(), DELTYPE.NORMAL);
						boolean minusGitAuth = true;//是否减去git访问权限(此人是否实质参与项目)
						for(ProjectMember projectMember :projectMembers){
							if(projectMember.getUserId()==tm.getUserId().longValue()){
								minusGitAuth = false;
								break;
							}
						}
						if(minusGitAuth){//如果此人没有实质参与到对应的项目中,则除去对应的权限
							List<App> listApp = appDao.findByProjectIdAndDel(project.getId(),DELTYPE.NORMAL);
							for(App app:listApp){
								if(app.getUserId()!=tm.getUserId().longValue()){
									GitAuthVO vo = new GitAuthVO();
//									vo.setAuthflag("all");
									vo.setPartnername(user.getAccount());
									vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
									String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
									vo.setProject(encodeKey.toLowerCase());
									vo.setProjectid(app.getAppcanAppId());
//									vo.setRef("master");
									listAuth.add(vo);
								}else{
									GitOwnerAuthVO vo = new GitOwnerAuthVO();
									String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
									vo.setProject(encodeKey.toLowerCase());
									vo.setUsername(user.getAccount());
									User other = new User();//应用要转给谁
									if(project.getType().equals(PROJECT_TYPE.TEAM)){
										List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
										if(null!=listTm && listTm.size()>0){
											other = userDao.findOne(listTm.get(0).getUserId());
										}
									}else{
										ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
										if(null!=pmCrt){
											other = userDao.findOne(pmCrt.getUserId());
										}
									}
									vo.setOther(other.getAccount());
									app.setUserId(other.getId());
									vo.setProjectid(app.getAppcanAppId());
									appDao.save(app);
									changeOwnerAuth.add(vo);
								}
							}
							
						}
					}
				}
				
			}
			//删除git权限(项目下的应用不是被删除人创建的应用)
			Map<String,String> map = appService.delGitAuth(listAuth);
			log.info(user.getAccount()+" was by deleted(EMM) from team id:->"+tm.getTeamId()+" ,and delGitAuth->"+(null==map?null:map.toString()));
			//转让git权限(项目下的应用是被删除人创建的应用)
			map = appService.updateGitAuth(changeOwnerAuth);
			log.info(user.getAccount()+"was by deleted(EMM) from team id:"+tm.getTeamId()+" ,and updateGitAuth->"+(null==map?null:map.toString()));
			
			//---------------------删除对应的git权限---------------------end---------
		}
		return true;
	
	}
	
	
	
}
