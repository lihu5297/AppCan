package org.zywx.cooldev.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.GitAuthVO;
import org.zywx.cooldev.entity.app.GitOwnerAuthVO;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.MD5Util;

@Service
public class TeamAuthService extends BaseService {
	
	@Autowired
	private AppService appService;
	public TeamAuth findOne(Long id){
		return this.teamAuthDao.findOne(id);
	}
	
	public TeamAuth updateAuth(Long teamAuthId,String roleEnName) throws Exception{
		TeamAuth ta = this.findOne(teamAuthId);
		TeamMember tm = teamMemberDao.findOne(ta.getMemberId());
		Role oldRole = Cache.getRole(ta.getRoleId());
		String oldRoleEnName = oldRole.getEnName();
		/*List<Permission> listP = oldRole.getPermissions();
		boolean oldMaster = false,oldBranch = false;
		if(null!=listP && listP.size()>0){
			for(Permission p :listP){
				if(p.getEnName().equals("code_upload_master_code")){
					oldMaster= true;
					 break;
				}
				if(p.getEnName().equals("code_update_branch")){
					oldBranch  = true;
				}
			}
			log.info("修改之前是否有主干权限---->"+oldMaster);
			log.info("修改之前是否有分支权限---->"+oldBranch);
		}*/
		Role role = Cache.getRole(roleEnName);
		/*List<Permission> listPer= role.getPermissions();
		boolean nowMaster = false,nowBranch = false;
		if(null!=listPer && listPer.size()>0){
			for(Permission p :listPer){
				if(p.getEnName().equals("code_upload_master_code")){
					nowMaster= true;
					 break;
				}
				if(p.getEnName().equals("code_update_branch")){
					nowBranch  = true;
				}
			}
			log.info("修改之后是否有主干权限---->"+oldMaster);
			log.info("修改之后是否有分支权限---->"+oldBranch);
		}*/
		ta.setRoleId(role.getId());
		this.teamAuthDao.save(ta);
		User user = userDao.findOne(tm.getUserId());
		
		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
		List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
		
		if((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).equals(oldRoleEnName) 
				&& (ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER).equals(roleEnName)){
			//以前是团队管理员,现在变为了团队普通成员,需要减去对应的git权限

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
//							vo.setAuthflag("all");
							vo.setPartnername(user.getAccount());
							vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
							vo.setProject(encodeKey.toLowerCase());
							vo.setProjectid(app.getAppcanAppId());
//							vo.setRef("master");
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
			//删除git权限(项目下的应用不是被删除人创建的应用)
			Map<String,String> map = appService.delGitAuth(listAuth);
			log.info(user.getAccount()+"'s role was by changed  in team id:->"+tm.getTeamId()+" ,and delGitAuth->"+(null==map?null:map.toString()));
			//转让git权限(项目下的应用是被删除人创建的应用)
			map = appService.updateGitAuth(changeOwnerAuth);
			log.info(user.getAccount()+"'s role was by changed in team id:"+tm.getTeamId()+" ,and updateGitAuth->"+(null==map?null:map.toString()));
		
		}
		if((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER).equals(oldRoleEnName) 
				&& (ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).equals(roleEnName)){
			//以前是团队普通成员,现在变为了团队管理员,需要增加对应的git权限

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
				List<App> teamAppList = appDao.findByTeamId(tm.getTeamId());//团队下的应用
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
			//将此团队下的所有应用,的权限
		
			Map<String,String> map = appService.addGitAuth(listAuth);
			log.info(user.getAccount()+" was by changed role's  id->"+tm.getTeamId()+",and shareallgit->"+(map==null?null:map.toString()));
		}
		return ta;
	}
	
	/*public TeamAuth findByMemberIdAndRoleId(long memberId,long roleId){
		return this.teamAuthDao.findByMemberIdAndRoleIdAndDel(memberId, roleId, DELTYPE.NORMAL);
	}*/
	
	public TeamAuth findByMemberIdAndDel(long memberId,DELTYPE delType){
		return this.teamAuthDao.findByMemberIdAndDel(memberId, delType);
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年10月26日 下午8:32:34
	 */
	    
	public void save(TeamAuth teamAuth) {
		teamAuthDao.save(teamAuth);
	}
}
