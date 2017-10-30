package org.zywx.cooldev.service;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.appdo.facade.omm.entity.tenant.Enterprise;
import org.zywx.appdo.facade.omm.service.tenant.TenantFacade;
import org.zywx.appdo.facade.user.entity.organization.Personnel;
import org.zywx.appdo.facade.user.service.organization.PersonnelFacade;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.dao.project.ProjectDao;
import org.zywx.cooldev.dao.task.TaskGroupDao;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamGroup;
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
import org.zywx.cooldev.entity.task.TaskGroup;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.MD5Util;
import org.zywx.cooldev.util.UserListWrapUtil;
import org.zywx.cooldev.util.emm.TokenUtilProduct;

@Service
public class TeamService extends AuthService {
	
	@Value("${emmUrl}")
	private String emmUrl;
	
	@Value("${emm3Url}")
	private String emm3Url;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	
	@Autowired
	protected TeamMemberService teamMemberService;
	
	@Autowired(required=false)
	private TenantFacade tenantFacade;
	
	@Value("${xietongHost}")
	private String xietongHost;
	
	@Value("${emmInvokeTeamUrl}")
	private String emmInvokeTeamUrl;
	
	@Value("${tenantId}")
	private String tenantId;
	
	@Value("${key}")
	private String key;
	
	@Autowired(required=false)
	private PersonnelFacade personnelFacade;
	
	@Autowired
	protected AppService appService;
	
	@Autowired
	private TeamAuthService teamAuthService;
	//企业版还是大众版标识
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private TaskGroupDao taskGroupDao;
	
	@Autowired
	private ProjectDao projectDao;
	
	private static String DELETE_TEAMAUTH_BY_TEAMID = "update T_TEAM_AUTH set del=1 where memberId in (select id from T_TEAM_MEMBER where teamId = ?)";
	private static String DELETE_TEAMMEMBER_BY_TEAMID = "update T_TEAM_MEMBER set del=1 where teamId = ?";
	private static String DELETE_TEAMGROUP_BY_TEAMID = "update T_TEAM_GROUP set del=1 where teamId = ?";
	/**
	 * @throws ParseException 
	 * 
	    * @Title: findTeamList
	    * @Description: 根据项目类型(个人项目,团队项目)及某人创建/某人参与.,团队名称，创建者姓名，参与者姓名，创建开始时间，创建结束时间 查找对应的团队信息,并且按创建时间倒叙排列 
	    * @param @param type 0我创建 1我参与
	    * @param @param ids   0个人团队  1项目团队
	    * @param @return    参数
	    * @return List<Team>    返回类型
	    * @throws
	 */
	/*public Page<Team> findTeamList(List<TEAMREALTIONSHIP> rel,long userId,List<TEAMTYPE> type,DELTYPE del,
			String teamName,String creator,String actor,String begin,String end,Pageable pageable) throws ParseException{
		if(StringUtils.isBlank(teamName)){
			teamName="%%";
		}else{
			teamName = "%"+teamName+"%";
		}
		if(StringUtils.isBlank(creator)){
			creator="%%";
		}else{
			creator="%"+creator+"%";
		}
		if(StringUtils.isBlank(actor)){
			actor="%%";
		}else{
			actor = "%"+actor+"%";
		}
		if(StringUtils.isBlank(begin)){
			begin="2016-01-01";
		}
		if(StringUtils.isBlank(end)){
			end="2116-12-31";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return this.teamDao.findTeamList(rel, userId, type, del, teamName, creator, actor, sdf.parse(begin), sdf.parse(end), pageable);
	}*/
	
	public Map<String, Object> findTeamList(long userId,List<TEAMTYPE> type,
			String teamName,String creator,String actor,String begin,String end,int pageNo,int pageSize) throws ParseException{
		StringBuffer sbSql = new StringBuffer(" from T_TEAM t ");
		//左连接团队项目个数
		sbSql.append(" left join (SELECT COUNT(1) projsum,teamId FROM T_PROJECT WHERE del=0 AND teamId!=-1 GROUP BY teamId ) proj on t.id = proj.teamId ");
		//左连接团队成员个数
		sbSql.append(" left join ( SELECT COUNT(1) membersum, teamId FROM T_TEAM_MEMBER WHERE del=0  AND TYPE IN (0,1) GROUP BY teamid) teamMember on t.id=teamMember.teamId ");
		//左连接团队创建者,如果没有真实姓名,取账号
		sbSql.append(" left join ( SELECT member.teamId,IFNULL(u.userName,u.account) creator FROM T_TEAM_MEMBER member LEFT JOIN T_USER u ON member.userId=u.id WHERE member.type=0 AND member.del=0 ) teamCreator on t.id=teamCreator.teamId ");
		sbSql.append(" where t.del=0 ");
		if(StringUtils.isNotBlank(teamName)){
			sbSql.append(" and (t.name like '%").append(teamName).append("%' or t.pinYinHeadChar like '").append(teamName).append("%' or t.pinYinName like '").append(teamName).append("%')");
		}
		if(StringUtils.isNotBlank(creator)){
			sbSql.append(" and t.id in (")
			.append("select teamId from T_TEAM_MEMBER where del=0 and userId in(")
			.append("select id from T_USER where del=0 and userName like '%").append(creator).append("%'")
			.append(" ) and  type=0  ").append(")");
		}
		if(StringUtils.isNotBlank(actor)){
			String actorRoleIds = "";
			actorRoleIds+=Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId();
			actorRoleIds+=","+Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER).getId();
		
			sbSql.append(" and t.id in (")
			.append("select tm.teamId from T_TEAM_MEMBER tm left join T_TEAM_AUTH ta on tm.id =ta.memberId where tm.del=0 and ta.del=0  and tm.userId in(")
			.append("select id from T_USER where del=0 and userName like '%").append(actor).append("%'")
			.append(" )   and ta.roleId in(  ").append(actorRoleIds).append(")")
			.append(")");
		}
		if(StringUtils.isNotBlank(begin)){
			sbSql.append(" and DATE_FORMAT(t.createdAt,'%Y-%m-%d')>= '").append(begin).append("'");
		}
		if(StringUtils.isNotBlank(end)){
			sbSql.append(" and DATE_FORMAT(t.createdAt,'%Y-%m-%d')<= '").append(end).append("'");
		}
		if(null!=type && type.size()==1){
			sbSql.append(" and t.type= ").append(type.get(0).ordinal());
		}
		if(null!=type && type.size()==2){
			sbSql.append(" and t.type in (").append(type.get(0).ordinal()).append(",").append(type.get(1).ordinal()).append(")");
		}
		sbSql.append(" and t.id in (select teamId from T_TEAM_MEMBER where type in (0,1) and userId = "+userId+" and del=0 ) order by t.createdat desc ");
		StringBuffer pageSql = new StringBuffer("");
		pageSql.append( " limit " +(pageNo-1)*pageSize + ", "+pageSize);
		
		String totalSql = " select count(1) "+sbSql.toString();
		String baseSql = "select t.id,t.name,t.detail,t.type,t.createdAt,proj.projsum,teamMember.membersum,teamCreator.creator "+sbSql.toString()+ pageSql.toString();
		
		log.info("baseSql====>"+baseSql);
		Long total = jdbcTpl.queryForObject(totalSql, Long.class);
		Map<String,Object> obj = new HashMap<String, Object>();
		obj.put("total", total);
		final List<Team> teamList = new ArrayList<Team>();
		
		this.jdbcTpl.query(baseSql, 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						Team vo  = new Team();
						vo.setId(rs.getLong("id"));
						vo.setName(rs.getString("name"));
						vo.setDetail(rs.getString("detail"));
						int type = rs.getInt("type");
						if(type==TEAMTYPE.NORMAL.ordinal()){
							vo.setType(TEAMTYPE.NORMAL);
						}
						if(type==TEAMTYPE.ENTERPRISE.ordinal()){
							vo.setType(TEAMTYPE.ENTERPRISE);
						}
						if(type==TEAMTYPE.BINDING.ordinal()){
							vo.setType(TEAMTYPE.BINDING);
						}
						if(type==TEAMTYPE.UNBINDING.ordinal()){
							vo.setType(TEAMTYPE.UNBINDING);
						}
						vo.setCreatedAt(rs.getTimestamp("createdAt"));
						
						//团队下项目个数
						vo.setProjectSum(rs.getInt("projsum"));
						//团队下人员个数
						vo.setMemberSum(rs.getInt("membersum"));
						//团队创建者,如果没有真实姓名的话取账号
						vo.setCreator(rs.getString("creator"));
						teamList.add(vo);
					}
				});
		
		List< Map<String, Object> > arr = new ArrayList<Map<String,Object>>();//最终返回的结果,为了配合前台,返回的结构不改变,拼装一下结构
		for(Team te:teamList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("object", te);
			map.put("permission", "");
			arr.add(map);
		}
		obj.put("list", arr);
		return this.getSuccessMap(obj);
	}
	
	public Team addTeam(Team team,Long userId,String serviceFlag,String invokeType) throws ClientProtocolException, IOException{
		//保存团队
		//增加拼音字段值
		team.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(team.getName()==null?"":team.getName()));
		team.setPinYinName(ChineseToEnglish.getPinYinHeadChar(team.getName()==null?"":team.getName()));
		this.teamDao.save(team);
		//保存团队成员
		TeamMember tm = new TeamMember();
		tm.setUserId(userId);
		tm.setTeamId(team.getId());
		tm.setType(TEAMREALTIONSHIP.CREATE);
		tm.setGroupId(-1L);
		tm.setDel(DELTYPE.NORMAL);
		tm.setJoinTime(new Timestamp(System.currentTimeMillis()));
		this.teamMemberDao.save(tm);
		
		//保存成员权限
		TeamAuth ta = new TeamAuth();
		ta.setDel(DELTYPE.NORMAL);
		ta.setMemberId(tm.getId());
		Role role = Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		ta.setRoleId(role.getId());
		this.teamAuthDao.save(ta);
		
		/*User user = userDao.findOne(userId);
		if("enterprise".equals(serviceFlag) || "emm".equals(invokeType)) {
			//企业版同时还需要EMM授权通过
			List<NameValuePair> parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("tenantId", team.getEnterpriseId()) );//企业简称
			parameters.add( new BasicNameValuePair("entFullName", team.getEnterpriseName() ));
			parameters.add( new BasicNameValuePair("teamId", team.getUuid() ) );
			parameters.add( new BasicNameValuePair("teamName", team.getName() ) );
			parameters.add( new BasicNameValuePair("teamDesc", team.getDetail() ) );
			parameters.add( new BasicNameValuePair("creator", user.getAccount() ) );
			parameters.add( new BasicNameValuePair("domainUrl", xietongHost ) );
			
			log.info("bindAuthGroup--> tenantId:"+team.getEnterpriseId()+",entFullName:"+team.getEnterpriseName()+
					",teamId:"+team.getUuid()+",teamName:"+team.getName()+",teamDesc:"+team.getDetail()
					+",creator:"+user.getAccount()+",domainUrl:"+xietongHost);
			String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/bindAuthGroup", parameters);
			log.info("bindAuthGroup-->"+resultStr);
			JSONObject jsonObject = JSONObject.fromObject(resultStr);
			if(!jsonObject.getString("returnCode").equals("200")){
				throw new RuntimeException(jsonObject.getString("returnMessage"));
			}
		}
		if("enterprise".equals(serviceFlag) && !"emm".equals(invokeType)){
			Personnel personnel = new Personnel();
			
			personnel.setName(user.getAccount());
			TeamMember teamCreator = teamMemberService.findMemberByTeamIdAndMemberType(tm.getTeamId(), TEAMREALTIONSHIP.CREATE);
			User teamCrt = userDao.findOne(teamCreator.getUserId());
			personnel.setCreatorId(teamCrt.getAccount());
			personnel.setTeamGroupId(team.getUuid());
			personnel.setMobileNo(user.getCellphone());
			personnel.setEmail(user.getAccount());
			personnel.setGroupName(team.getName());
			personnel.setTeamDevAddress(xietongHost);
			String token = "";
			String[] params = new String[2];
			
			params[0] = tenantId;
			params[1] = "dev";
			token= TokenUtilProduct.getToken(key, params);
			log.info("teamMember sync to EMM-->"+personnel.getName()+", tenantId -->"+tenantId+", key -->"+key);
			String flag = personnelFacade.createTeamUser(token, personnel);
			
			log.info("teamMember sync to EMM-->:"+flag);
			if(StringUtils.isNotBlank(flag)){
				throw new RuntimeException("企业版,创建团队,此时添加团队创建者到EMM时候失败,"+flag);
			}
		}*/
		return team;
	}
	public Team emmAddTeam(Team team,Long userId,String serviceFlag) throws ClientProtocolException, IOException{
		//保存团队
		this.teamDao.save(team);
		//保存团队成员
		TeamMember tm = new TeamMember();
		tm.setUserId(userId);
		tm.setTeamId(team.getId());
		tm.setType(TEAMREALTIONSHIP.CREATE);
		tm.setGroupId(-1L);
		tm.setDel(DELTYPE.NORMAL);
		tm.setJoinTime(new Timestamp(System.currentTimeMillis()));
		this.teamMemberDao.save(tm);
		
		//保存成员权限
		TeamAuth ta = new TeamAuth();
		ta.setDel(DELTYPE.NORMAL);
		ta.setMemberId(tm.getId());
		Role role = Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		ta.setRoleId(role.getId());
		this.teamAuthDao.save(ta);
		
		User user = userDao.findOne(userId);
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("tenantId", team.getEnterpriseId()) );//企业简称
		parameters.add( new BasicNameValuePair("entFullName", team.getEnterpriseName() ));
		parameters.add( new BasicNameValuePair("teamId", team.getUuid() ) );
		parameters.add( new BasicNameValuePair("teamName", team.getName() ) );
		parameters.add( new BasicNameValuePair("teamDesc", team.getDetail() ) );
		parameters.add( new BasicNameValuePair("creator", user.getAccount() ) );
		parameters.add( new BasicNameValuePair("domainUrl", xietongHost ) );
		
		log.info("bindAuthGroup--> tenantId:"+team.getEnterpriseId()+",entFullName:"+team.getEnterpriseName()+
				",teamId:"+team.getUuid()+",teamName:"+team.getName()+",teamDesc:"+team.getDetail()
				+",creator:"+user.getAccount()+",domainUrl:"+xietongHost);
		String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/bindAuthGroup", parameters);
		log.info("bindAuthGroup-->"+resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if(!jsonObject.getString("returnCode").equals("200")){
			throw new RuntimeException(jsonObject.getString("returnMessage"));
		}
		return team;
	}
	/**
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * 
	    * @Title: updateTeam
	    * @Description:更新团队 
	    * @param @param team
	    * @param @param userId
	    * @param @return    参数
	    * @return Team    返回类型
	    * @throws
	 */
	public Team updateTeam(Team team,String userId) throws ClientProtocolException, IOException{
		Team t = this.teamDao.findOne(team.getId());
		t.setDetail(team.getDetail());
		t.setName(team.getName());
		//修改拼音字段
		t.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(t.getName()==null?"":t.getName()));
		t.setPinYinName(ChineseToEnglish.getPinYinHeadChar(t.getName()==null?"":t.getName()));
		t.setUpdatedAt(team.getUpdatedAt());
		this.teamDao.save(t);
		if(t.getType().equals(TEAMTYPE.ENTERPRISE)){
			TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(team.getId(), TEAMREALTIONSHIP.CREATE);
			User user = userDao.findOne(teammember.getUserId());
			if("enterprise".equals(serviceFlag) || "online".equals(serviceFlag)){
				List<NameValuePair> parameters = new ArrayList<>();
				parameters.add( new BasicNameValuePair("creator", user.getAccount()) );
				parameters.add( new BasicNameValuePair("teamName", t.getName() ) );
				parameters.add( new BasicNameValuePair("teamDesc", t.getDetail() ) );
				parameters.add( new BasicNameValuePair("tenantId", t.getEnterpriseId() ) );
				parameters.add( new BasicNameValuePair("entFullName", t.getEnterpriseName() ));
				parameters.add( new BasicNameValuePair("teamId", t.getUuid() ) );
				log.info("updateTeamInfo_params-->"+parameters.toString());
				String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/updateTeamInfo", parameters);
				log.info("updateEMMTeamInfo-->"+resultStr);
				JSONObject jsonObject = JSONObject.fromObject(resultStr);
				if(jsonObject.get("returnCode").equals("200")){//如果修改团队的时候提示团队信息不存在,则再次去调用绑定的接口
					log.info("team update info to emm success!");
				}else if(jsonObject.get("returnCode").equals("500") && jsonObject.getString("returnMessage").contains("select teamInfo is null")){
					log.info("team bind again for team info .");
					parameters = new ArrayList<>();
					parameters.add( new BasicNameValuePair("tenantId", t.getEnterpriseId()) );//企业简称
					parameters.add( new BasicNameValuePair("entFullName", t.getEnterpriseName() ));
					parameters.add( new BasicNameValuePair("teamId", t.getUuid() ) );
					parameters.add( new BasicNameValuePair("teamName", t.getName() ) );
					parameters.add( new BasicNameValuePair("teamDesc", t.getDetail() ) );
					parameters.add( new BasicNameValuePair("creator", user.getAccount() ) );
					parameters.add( new BasicNameValuePair("domainUrl", xietongHost ) );
					
					log.info("bindAuthGroup--> tenantId:"+t.getEnterpriseId()+",entFullName:"+t.getEnterpriseName()+
							",teamId:"+t.getUuid()+",teamName:"+t.getName()+",teamDesc:"+t.getDetail()
							+",creator:"+user.getAccount()+",domainUrl:"+xietongHost);
					resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/bindAuthGroup", parameters);
					log.info("bindAuthGroup-->"+resultStr);
					jsonObject = JSONObject.fromObject(resultStr);
					if(!jsonObject.getString("returnCode").equals("200")){
						throw new RuntimeException("调用EMM修改团队信息失败");
					}
				}else{
					throw new RuntimeException("调用EMM修改团队信息失败");
				}
			}
//			else if("enterpriseEmm3".equals(serviceFlag)){
//				Map<String,String> parameters = new HashMap<String,String>();
//				parameters.put("teamDevGroupId", t.getUuid()  );//群组ID
//				parameters.put("groupName", t.getName() );//群组名称
//				parameters.put("groupDesc", t.getDetail() );//群组简介
//				parameters.put("creatorName", user.getAccount() );//创建者
//				String resultStr = HttpUtil.httpsPost(emm3Url+"/mum/personnelGroup/saveOrUpdateXieTong", parameters,"UTF-8");
//				log.info("emm3 update group-->"+resultStr);
//				JSONObject jsonObject = JSONObject.fromObject(resultStr);
//				if(!jsonObject.getString("status").equals("ok")){
//					throw new RuntimeException("修改EMM用户组失败");
//				}
//				
//				if(StringUtils.isNotBlank(emm3TestUrl)){
//					resultStr = HttpUtil.httpsPost(emm3TestUrl+"/mum/personnelGroup/saveOrUpdateXieTong", parameters,"UTF-8");
//					log.info("emm3 test update group-->"+resultStr);
//					jsonObject = JSONObject.fromObject(resultStr);
//					if(!jsonObject.getString("status").equals("ok")){
//						throw new RuntimeException("测试环境修改EMM用户组失败");
//					}
//				}
//			}
			
		}
		return t;
	}
	/**
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * 
	    * @Title: deleteTeam
	    * @Description:删除团队 
	    * @param @param teamId
	    * @param @param userId
	    * @param @return    参数
	    * @return Team    返回类型
	    * @throws
	 */
	public Team deleteTeam(long teamId,String userId,String invokeType) throws ClientProtocolException, IOException{
		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
		List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
		
		Team team = this.teamDao.findOne(teamId);
		team.setDel(Enums.DELTYPE.DELETED);
		this.teamDao.save(team);
		
		
		List<TeamMember> listCrt = teamMemberDao.findByTeamIdAndTypeAndDel(teamId, TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
		long teamCreatorUserId = 0l;
		if(null!=listCrt && listCrt.size()>0){
			teamCreatorUserId = listCrt.get(0).getUserId();
		}else{
			log.info("团队创建者不存在");
			throw new RuntimeException("团队不存在创建者");
		}
		//将团队下面的项目的创建者都变为团队的创建者
		List<Project> listProj = projectDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
		
		if("enterprise".equals(serviceFlag)){//企业版
			if(null!=listProj && listProj.size()>0){
				for(Project prj:listProj){
					//第二个参数token设置为null,是因为projectService.removeProject方法中判断如果此值为null,则不进行EMM接口删除,在删除团队的方法最后会调用EMM接口删除
					projectService.removeProject(prj.getId(), null);
				}
			}
		}else if("online".equals(serviceFlag)){//大众版
			if(null!=listProj && listProj.size()>0){
				for(Project prj:listProj){
					//解散团队的时候,团队项目变为个人项目.
					prj.setBizCompanyId(null);
					prj.setBizCompanyName(null);
					prj.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
					prj.setTeamId(-1l);
					prj.setType(PROJECT_TYPE.PERSONAL);
					projectDao.save(prj);
					
					//如果团队项目的创建者并不是团队的创建者,则需要将团队项目的创建者改为团队创建者
					ProjectMember pm = projectMemberDao.findByProjectIdAndTypeAndDel(prj.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
					List<ProjectMember> listMem = projectMemberDao.findByProjectIdAndUserIdAndDel(prj.getId(), teamCreatorUserId, DELTYPE.NORMAL);
					
					List<App> listAppBelongProj = appDao.findByProjectIdAndDel(prj.getId(),DELTYPE.NORMAL);//项目下的应用
					
					//----------------判断此团队下面的管理员是否参与到具体的团队项目当中,如果没有参与,则删除对应的git权限-------------begin-------------
					//第1步:找到此项目下的所有成员
					List<ProjectMember> listProjectMember = projectMemberDao.findByProjectIdAndDel(prj.getId(), DELTYPE.NORMAL);
					//第2步:找到团队的管理员
					List<TeamMember> listManager = new ArrayList<TeamMember>();
					List<TeamMember> listTeamActor = teamMemberDao.findByTeamIdAndTypeAndDel(teamId, TEAMREALTIONSHIP.ACTOR, DELTYPE.NORMAL);
					for(TeamMember tm:listTeamActor){
						TeamAuth ta = teamAuthDao.findByMemberIdAndRoleIdAndDel(tm.getId(), Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId(), DELTYPE.NORMAL);
						if(null!=ta){
							listManager.add(tm);
						}
					}
					//第3步:用每个管理员去遍历每个项目,看是否参与到实际项目当中,如果没有参与到项目当中,则第4步应该删除此管理员在此项目下的所有应用的git权限
					if(null!=listManager && listManager.size()>0){
						for(TeamMember tm:listManager){
							boolean joinFlag = false;//标识团队管理员是否参与到实际项目当中
							for(ProjectMember pmember:listProjectMember){
								if(tm.getUserId().longValue()==pmember.getUserId()){
									joinFlag = true;
									break;
								}
							}
							if(!joinFlag){
								if(null!=listAppBelongProj && listAppBelongProj.size()>0){
									for(App app:listAppBelongProj){
										if(tm.getUserId().longValue()!=app.getUserId()){
											
											GitAuthVO vo = new GitAuthVO();
											vo.setPartnername(userDao.findOne(tm.getUserId()).getAccount());
											vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
											String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
											vo.setProject(encodeKey.toLowerCase());
											vo.setProjectid(app.getAppcanAppId());
											listAuth.add(vo);
										}else{
											GitOwnerAuthVO vo = new GitOwnerAuthVO();
											String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
											vo.setProject(encodeKey.toLowerCase());
											vo.setUsername(userDao.findOne(tm.getUserId()).getAccount());
											User ucrt = userDao.findOne(teamCreatorUserId);//应用转给团队创建者
											vo.setOther(ucrt.getAccount());
											app.setUserId(ucrt.getId());
											vo.setProjectid(app.getAppcanAppId());
											appDao.save(app);
											changeOwnerAuth.add(vo);
										}
									}
								}
							}
						}
					}

					
					//----------------判断此团队下面的管理员是否参与到具体的团队项目当中,如果没有参与,则删除对应的git权限-------------end-------------
					if(pm.getUserId()!=teamCreatorUserId){
						//如果团队项目的创建者并不是团队的创建者,则需要将团队项目的创建者改为团队创建者
						long oldUserId = pm.getUserId();//原来的项目创建者
						pm.setUserId(teamCreatorUserId);
						projectMemberDao.save(pm);
						
						//将原来的团队创建者变为管理员
						ProjectMember pmManager = new ProjectMember();
						pmManager.setUserId(oldUserId);
						pmManager.setProjectId(pm.getProjectId());
						pmManager.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
						projectMemberDao.save(pmManager);
						
						ProjectAuth projectAuth = new ProjectAuth();
						projectAuth.setMemberId(pmManager.getId());
						projectAuth.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR).getId());
						projectAuthDao.save(projectAuth);
						
						
						if(null!=listMem && listMem.size()>0){//如果团队创建者以前就曾参与该项目,则需要将以前的成员信息删除
							for(ProjectMember pm1:listMem){
								pm1.setDel(DELTYPE.DELETED);
								projectMemberDao.save(pm1);
								List<ProjectAuth> listProjAuth = projectAuthDao.findByMemberIdAndDel(pm1.getId(), DELTYPE.NORMAL);
								if(null!=listProjAuth && listProjAuth.size()>0){
									for(ProjectAuth auth:listProjAuth){
										auth.setDel(DELTYPE.DELETED);
										projectAuthDao.save(auth);
									}
								}
							}
						}
					}
				}
				
			}
			//删除git权限(项目下的应用不是被删除人创建的应用)
			Map<String,String> map = appService.delGitAuth(listAuth);
			log.info("解散团队:teamId->"+teamId+" 删除没有参与到项目中的管理员的git权限,and delGitAuth->"+(null==map?null:map.toString()));
			//转让git权限(项目下的应用是被删除人创建的应用)
			map = appService.updateGitAuth(changeOwnerAuth);
			log.info( "解散团队: team id:"+teamId+" 将没有参与到实际项目中却在项目中创建了应用的团队管理员的git权限的owner权限去掉,owner改为团队创建者,and updateGitAuth->"+(null==map?null:map.toString()));
			//-------------------------------------git权限-------------------------------------
		}
		
		if("enterprise".equals(serviceFlag)){
			//调用EMM接口删除对于的信息
			List<NameValuePair> parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("teamId", team.getUuid()) );
			parameters.add( new BasicNameValuePair("tenantId", team.getEnterpriseId() ) );
			parameters.add( new BasicNameValuePair("entFullName", team.getEnterpriseName() ));
			
			log.info("delete bindTeam parameters-->"+parameters.toString());
			String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/deleteEntBindTeam", parameters);
			log.info("delete bindTeam-->"+resultStr);
			JSONObject jsonObject = JSONObject.fromObject(resultStr);
			if(!jsonObject.get("returnCode").equals("200")){
				throw new RuntimeException(jsonObject.getString("returnMessage"));
			}
		}
		log.info("删除团队授权信息:"+this.jdbcTpl.update(DELETE_TEAMAUTH_BY_TEAMID,new Object[]{teamId}));//删除团队授权信息
		log.info("删除团队成员信息:"+this.jdbcTpl.update(DELETE_TEAMMEMBER_BY_TEAMID,new Object[]{teamId}));//删除团队成员信息
		log.info("删除团队小组信息:"+this.jdbcTpl.update(DELETE_TEAMGROUP_BY_TEAMID, new Object[]{teamId}));//删除团队小组信息
		
		
		
		return team;
	}
	/**
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * 
	    * @Title: updateEnterprise
	    * @Description: 为团队绑定企业ID
	    * @param @param enterpriseId
	    * @param @param teamId
	    * @param @return    参数
	    * @return Team    返回类型
	    * @throws
	 */
	public Team updateEnterprise(String enterpriseId,String enterpriseName,long teamId,String loginUserId) throws ClientProtocolException, IOException{
		final List<String> list=new ArrayList<String>();
		Team tm = this.teamDao.findOne(teamId);
		if(tm.getType().equals(TEAMTYPE.ENTERPRISE)
				||tm.getType().equals(TEAMTYPE.BINDING)
				|| tm.getType().equals(TEAMTYPE.UNBINDING)){
			return null;
		}
		tm.setEnterpriseId(enterpriseId);
		tm.setEnterpriseName(enterpriseName);
		tm.setType(TEAMTYPE.BINDING);//申请绑定中
		this.teamDao.save(tm);
		
		
		//更新团队下面的项目申请状态
		String updateSql="update T_PROJECT set  bizCompanyId='"+enterpriseId+"', bizCompanyName='"+enterpriseName+"', bizLicense= 2 where teamId="+teamId;
		this.jdbcTpl.update(updateSql);
		
		
		TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(teamId, TEAMREALTIONSHIP.CREATE);
		User user = userDao.findOne(teammember.getUserId());
		
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("creator", user.getAccount()) );
		parameters.add( new BasicNameValuePair("teamName", tm.getName() ) );
		parameters.add( new BasicNameValuePair("teamDesc", tm.getDetail() ) );
		parameters.add( new BasicNameValuePair("tenantId", enterpriseId ) );
		parameters.add( new BasicNameValuePair("entFullName", enterpriseName ));
		parameters.add( new BasicNameValuePair("teamId", tm.getUuid()) );
		
		// 添加创建者个人信息（姓名，手机号码）
		parameters.add( new BasicNameValuePair("domainUrl", emmInvokeTeamUrl) );
		
		log.info("parameters-->creator:"+user.getAccount()+",teamName:"+tm.getName()+",teamDesc:"+tm.getDetail()+",tenantId:"+enterpriseId+",entFulName:"+enterpriseName+",teamId:"+tm.getUuid());
		String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/createAuthGroup", parameters);
		log.info("createAuthGroup-->"+resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if(!jsonObject.get("returnCode").equals("200")){
			throw new RuntimeException("调用EMM申请授权失败");
		}
		
		return tm;
	}
	
	/**
	 * 页面申请,取消团队绑定
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user jingjian.wu
	 * @date 2015年10月22日 上午10:28:50
	 */
	public List<Object> updateCancelEnterprise(long teamId) throws ClientProtocolException, IOException{
		List<Object> list = new ArrayList<Object>();
		Team tm = this.teamDao.findOne(teamId);
		if(tm.getType().equals(TEAMTYPE.ENTERPRISE)){
			return null;
		}
		String enterpriseName = tm.getEnterpriseName();
		String enterpriseId = tm.getEnterpriseId();
		tm.setEnterpriseId(null);
		tm.setEnterpriseName(null);
		tm.setType(TEAMTYPE.NORMAL);//将团队状态改为未绑定状态(普通团队)
		this.teamDao.save(tm);
		list.add(tm);
		list.add(enterpriseName);
		
		//更新团队下面的项目申请状态
		String updateSql="update T_PROJECT set  bizCompanyId=null, bizCompanyName=null, bizLicense= 1 where teamId="+teamId;
		this.jdbcTpl.update(updateSql);
		
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("tenantId", enterpriseId ) );
		parameters.add( new BasicNameValuePair("entFullName", enterpriseName ) );
		parameters.add( new BasicNameValuePair("teamId", tm.getUuid() ) );
			
		String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/deleteAuthGroup", parameters);
		log.info("deleteAuthGroup-->"+resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if(!jsonObject.get("returnCode").equals("200")){
			throw new RuntimeException(jsonObject.getString("returnMessage"));
		}
		return list;
	}
	/**
	 * 页面申请,团队绑定申请通过后申请解除绑定
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user haijun.cheng
	 * @date 2016年7月11日 
	 */
	public List<Object> updateDeleteEnterprise(long teamId) throws ClientProtocolException, IOException{
		List<Object> list = new ArrayList<Object>();
		Team tm = this.teamDao.findOne(teamId);
		if(tm.getType().equals(TEAMTYPE.NORMAL)){
			return null;
		}
		String enterpriseName = tm.getEnterpriseName();
		String enterpriseId = tm.getEnterpriseId();
		tm.setType(TEAMTYPE.UNBINDING);//将团对改为解绑审核中状态
		this.teamDao.save(tm);
		list.add(tm);
		list.add(enterpriseName);
		
		//更新团队下面的项目申请状态
		String updateSql="update T_PROJECT set bizLicense= 3 where teamId="+teamId;
		this.jdbcTpl.update(updateSql);
		
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("tenantId", enterpriseId ) );
		parameters.add( new BasicNameValuePair("entFullName", enterpriseName ) );
		parameters.add( new BasicNameValuePair("teamId", tm.getUuid() ) );
		parameters.add( new BasicNameValuePair("isUnbunding", "true" ) );//解除绑定 true ,取消解绑 false
		
		String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/unbundingTeam", parameters);
		log.info("deleteAuthGroup-->"+resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if(!jsonObject.get("returnCode").equals("200")){
			throw new RuntimeException(jsonObject.getString("returnMessage"));
		}
		return list;
	}
	/**
	 * 页面申请,团队绑定申请通过后，取消解绑
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user haijun.cheng
	 * @date 2016年7月11日 
	 */
	public List<Object> deleteEnterpriseCancel(long teamId) throws ClientProtocolException, IOException{
		List<Object> list = new ArrayList<Object>();
		Team tm = this.teamDao.findOne(teamId);
		if(tm.getType().equals(TEAMTYPE.ENTERPRISE)){
			return null;
		}
		String enterpriseName = tm.getEnterpriseName();
		String enterpriseId = tm.getEnterpriseId();
		tm.setType(TEAMTYPE.ENTERPRISE);
		this.teamDao.save(tm);
		list.add(tm);
		list.add(enterpriseName);
		
		//更新团队下面的项目申请状态
		String updateSql="update T_PROJECT set bizLicense= 0 where teamId="+teamId;
		this.jdbcTpl.update(updateSql);
		
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("tenantId", enterpriseId ) );
		parameters.add( new BasicNameValuePair("entFullName", enterpriseName ) );
		parameters.add( new BasicNameValuePair("teamId", tm.getUuid() ) );
		parameters.add( new BasicNameValuePair("isUnbunding", "false" ) );//解除绑定 true ,取消解绑 false
			
		String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/unbundingTeam", parameters);
		log.info("deleteAuthGroup-->"+resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if(!jsonObject.get("returnCode").equals("200")){
			throw new RuntimeException(jsonObject.getString("returnMessage"));
		}
		return list;
	}
	/**
	 * EMM调用企业授权通过
	 * @user jingjian.wu
	 * @date 2015年10月19日 下午4:53:51
	 */
	public Team updateType(long teamId){
		Team tm = this.teamDao.findOne(teamId);
		if(tm.getEnterpriseId()==null || tm.getEnterpriseName()==null){
			return null;
		}
		tm.setType(TEAMTYPE.ENTERPRISE);
		this.teamDao.save(tm);
		jdbcTpl.update("update T_PROJECT set bizCompanyId=? ,bizCompanyName=? , bizLicense=? where teamId=?", new Object[]{tm.getEnterpriseId(),tm.getEnterpriseName(),PROJECT_BIZ_LICENSE.AUTHORIZED.ordinal(),tm.getId()});
		return tm;
	}
	/**
	 * 解绑企业
	 * @param teamId
	 * @return Team
	 * @user jingjian.wu
	 * @date 2015年8月21日 上午11:57:50
	 * @throws
	 */
	public List<Object> updateUnEnterprise(long teamId){
		List<Object> list = new ArrayList<Object>();
		Team tm = this.teamDao.findOne(teamId);
		
		list.add(tm);
		list.add(tm.getEnterpriseName());
		
		tm.setEnterpriseId(null);
		tm.setEnterpriseName(null);
		tm.setType(TEAMTYPE.NORMAL);
		this.teamDao.save(tm);
		
		jdbcTpl.update("update T_PROJECT set bizCompanyId=null ,bizCompanyName=null , bizLicense=? where teamId=?", new Object[]{PROJECT_BIZ_LICENSE.NOT_AUTHORIZED.ordinal(),tm.getId()});
		
		
		return list;
	}
	
	/**
	    * @Title: getOne
	    * @Description: 根据id获取某个团队信息
	    * @param @param id
	    * @param @return    参数
	    * @return Team    返回类型
		* @user wjj
		* @date 2015年8月12日 下午6:42:09
	    * @throws
	 */
	public Team getOne(long id){
		return this.teamDao.findOne(id);
	}
	
	public Team getByUuid(String uuid){
		return this.teamDao.findByUuidAndDel(uuid, DELTYPE.NORMAL);
	}
	
	/**
	    * @Description:获取我创建/管理的团队 
	    * @param @param userId
	    * @param @return 
	    * @return List<Team>    返回类型
		* @user jingjian.wu
		* @date 2015年8月19日 下午3:59:45
	    * @throws
	 */
	public List<Team> findMgrCrtList(long userId){
		List<Long> roleId = new ArrayList<Long>();
		Role roleCreat = Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		Role roleMgr = Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR);
		roleId.add(roleCreat.getId());
		roleId.add(roleMgr.getId());
		return this.teamDao.findMgrCrtTeamList(userId, roleId);
	}
	
	/** 查询一个团队里面的成员(除去项目中已有的成员,和一些额外的其他人员)  
     * @Description: 团队项目邀请成员时候,需要调用方法
     * @param @param teamId
     * @param @param projectId
     * @param @param existUserids
     * @param @return 
     * @return List<User>    返回类型
	 * @user jingjian.wu
	 * @date 2015年8月20日 下午7:30:40
     * @throws
	 */
	public Map<String, Object> findUserList(Long teamId,Long projectId,List<Long> existUserIds,String search){
		existUserIds.add(-9999L);//填充一个默认值,防止existUserIds为null时候报错
		List<TeamGroup> listGroup = this.teamGroupDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isBlank(search)){
			search = "";
		}
		search = "%"+search+"%";
		for(TeamGroup tg:listGroup){
			map.put(tg.getName(), this.userDao.findUserForAskUserLimit(teamId, projectId, existUserIds, tg.getId(),search));
		}
		map.put("无分组", this.userDao.findUserForAskUserLimit(teamId, projectId, existUserIds, -1L,search));
		return map;
	}
	/**
	 * 创建团队项目
	 * @param p
	 * @param userId void
	 * @user jingjian.wu
	 * @date 2015年8月25日 上午10:18:41
	 * @throws
	 */
	public Project addProject(Project p,long userId){
		p.setStatus(PROJECT_STATUS.ONGOING);
		Team team = this.teamDao.findOne(p.getTeamId());
		if(null==team){
			throw new RuntimeException("所在团队不存在.");
		}
		if(team.getType().equals(TEAMTYPE.ENTERPRISE)){
			p.setBizCompanyId(team.getEnterpriseId());
			p.setBizCompanyName(team.getEnterpriseName());
			p.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
		}else if(team.getType().equals(TEAMTYPE.NORMAL)){
			p.setBizCompanyId(null);
			p.setBizCompanyName(null);
			p.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
		}else if(team.getType().equals(TEAMTYPE.BINDING)){
			p.setBizCompanyId(team.getEnterpriseId());
			p.setBizCompanyName(team.getEnterpriseName());
			p.setBizLicense(PROJECT_BIZ_LICENSE.BINDING);
		}else if(team.getType().equals(TEAMTYPE.UNBINDING)){
			p.setBizCompanyId(team.getEnterpriseId());
			p.setBizCompanyName(team.getEnterpriseName());
			p.setBizLicense(PROJECT_BIZ_LICENSE.UNBINDING);
		}
		//增加拼音字段
		p.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(p.getName()==null?"":p.getName()));
		p.setPinYinName(ChineseToEnglish.getPingYin(p.getName()==null?"":p.getName()));
		this.projectDao.save(p);
		
		for(int i =0;i<6;i++){
			TaskGroup tg = new TaskGroup();
			tg.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			tg.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			tg.setProjectId(p.getId());
			switch (i) {
			case 0:
				tg.setName("待进行");
				break;
			case 1:
				tg.setName("进行中");
				break;
			case 2:
				tg.setName("被驳回");
				break;
			case 3:
				tg.setName("已完成");
				break;
			case 4:
				tg.setName("已搁置");
				break;
			case 5:
				tg.setName("已关闭");
				break;
			default:
				break;
			}
			tg.setSort(i);
			tg.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(tg.getName() == null ? "" : tg.getName()));
			tg.setPinYinName(ChineseToEnglish.getPingYin(tg.getName() == null ? "" : tg.getName()));
			taskGroupDao.save(tg);
		}
		
		ProjectMember pm = new ProjectMember();
		pm.setProjectId(p.getId());
		pm.setUserId(userId);
		pm.setType(PROJECT_MEMBER_TYPE.CREATOR);
		this.projectMemberDao.save(pm);
		
		ProjectAuth pa = new ProjectAuth();
		pa.setMemberId(pm.getId());
		Role r = Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR);
		pa.setRoleId(r.getId());
		this.projectAuthDao.save(pa);
		
		return p;
		
	}

	public TeamAuth findTeamAuthByMemberId(Long memberId){
		return this.teamAuthDao.findByMemberIdAndDel(memberId, DELTYPE.NORMAL);
	}
	
	/**
	 * @throws Exception 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * 转让团队
	 * @param teamId
	 * @param srcUserId  原先团队创建者ID
	 * @param targetUserId  要转让给哪个用户
	 * @user jingjian.wu
	 * @date 2015年8月26日 下午6:00:46
	 * @throws
	 */
	public void updateTransferTeam(long teamId,long srcUserId,long targetUserId,String invokeType) throws Exception{
		//---git--
		//标识是否需要给接收者增加git权限-------
		boolean addGitAuth = true;
		//---git--
		TeamMember tMemberTarget = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, targetUserId,DELTYPE.NORMAL);
		TeamMember tMemberSrc = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, srcUserId, DELTYPE.NORMAL);
		tMemberSrc.setType(TEAMREALTIONSHIP.ACTOR);
		tMemberTarget.setType(TEAMREALTIONSHIP.CREATE);
		this.teamMemberDao.save(tMemberSrc);
		this.teamMemberDao.save(tMemberTarget);
		
		TeamAuth teamAuthTarget = this.teamAuthDao.findByMemberIdAndDel(tMemberTarget.getId(), DELTYPE.NORMAL);
		TeamAuth teamAuthSrc = this.teamAuthDao.findByMemberIdAndDel(tMemberSrc.getId(), DELTYPE.NORMAL);
		//---git--
		if(teamAuthTarget.getRoleId()==Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId()){
			//接收者以前是团队的管理员
			addGitAuth = false;
		}
		//--git--
		teamAuthSrc.setRoleId(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId());
		Role ro = Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		teamAuthTarget.setRoleId(ro.getId());
		
		this.teamAuthDao.save(teamAuthSrc);
		this.teamAuthDao.save(teamAuthTarget);
		
		Team team = teamDao.findOne(teamId);
		User receiveTeamUser = userDao.findOne(targetUserId);
		if(("enterprise".equals(serviceFlag) || "online".equals(serviceFlag)) && projectService.isTeamBind(teamId)
				&&StringUtils.isBlank(invokeType)){
			//调用EMM接口删除对于的信息
			List<NameValuePair> parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("teamId", team.getUuid()) );
			parameters.add( new BasicNameValuePair("tenantId", team.getEnterpriseId() ) );
			parameters.add( new BasicNameValuePair("entFullName", team.getEnterpriseName() ));
			parameters.add( new BasicNameValuePair("handOverEmail", receiveTeamUser.getAccount() ));
			log.info("handOverTeam  parameters-->"+parameters.toString());
			String resultStr = HttpUtil.httpPost(emmUrl+"/emm/teamAuth/handOverTeam", parameters);
			log.info("handOverTeam-->"+resultStr);
			JSONObject jsonObject = JSONObject.fromObject(resultStr);
			if(!jsonObject.get("returnCode").equals("200")){
				throw new RuntimeException(jsonObject.getString("returnMessage"));
			}
		}
		//------------------git权限----------------
		if(addGitAuth){
			List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
			User currUser = userDao.findOne(tMemberTarget.getUserId());
			//如果是团队管理员
			List<Permission> listP = ro.getPermissions();
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
							if(app.getUserId()!=currUser.getId().longValue()){
								
								GitAuthVO vo = new GitAuthVO();
								vo.setAuthflag("all");
								vo.setPartnername(currUser.getAccount());
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
							if(app.getUserId()!=currUser.getId().longValue()){
								GitAuthVO vo = new GitAuthVO();
								vo.setAuthflag("allbranch");
								vo.setPartnername(currUser.getAccount());
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
			
			Map<String,String> map = appService.addGitAuth(listAuth);
			log.info(currUser.getAccount()+" 被转让成了团队 id为->"+teamId+"的管理员,and shareallgit->"+(null==map?null:map.toString()));		
		}
		//------------------git权限----------------
	}
	
	
	    /* (非 Javadoc)
	     * 
	     * 
	     * @param loginUserId
	     * @param entityId
	     * @return
	     * @see org.zywx.cooldev.service.AuthService#getPermissionList(long, long)
	     */
	    
	@Override
	public List<Permission> getPermissionList(long userId, long teamId) {
		List<Permission> listPermission = new ArrayList<Permission>();
		TeamMember tMember = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, userId, DELTYPE.NORMAL);
		if(null==tMember){
			return listPermission;
		}
		TeamAuth teamAuth = this.teamAuthDao.findByMemberIdAndDel(tMember.getId(), DELTYPE.NORMAL);
		if(null == teamAuth){
			return listPermission;
		}
		Role role = Cache.getRole(teamAuth.getRoleId());
		return role.getPermissions();
	}
	
	/**
	 * 获取当前登录人在团队下面的权限
	 * @param teamId
	 * @param loginUserId
	 * @return List<Permission>
	 * @user jingjian.wu
	 * @date 2015年8月28日 下午8:21:32
	 * @throws
	 */
	/*public List<Permission> findPermissionForTeam(long teamId,long loginUserId){
		TeamMember tmember = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, loginUserId,DELTYPE.NORMAL);
		TeamAuth teamAuth = this.teamAuthDao.findByMemberIdAndDel(tmember.getId(),DELTYPE.NORMAL);
		List<Permission> listPermission = Cache.getRole(teamAuth.getRoleId()).getPermissions();
		return listPermission;
	}*/
	public Map<String, Integer> findPermissionForTeam(long teamId,long loginUserId){
		Map<String, Integer> map = new HashMap<String, Integer>();
		TeamMember tmember = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, loginUserId,DELTYPE.NORMAL);
		if(null==tmember){
			log.info("user for id:"+loginUserId +",is not the member for team with id:"+teamId);
			return map;
		}
		TeamAuth teamAuth = this.teamAuthDao.findByMemberIdAndDel(tmember.getId(),DELTYPE.NORMAL);
		if(null!=teamAuth){
			List<Permission> listPermission = Cache.getRole(teamAuth.getRoleId()).getPermissions();
			if(listPermission!=null && listPermission.size()>0){
				for(Permission p:listPermission){
					map.put(p.getEnName(),1);
				}
			}
		}
		return map;
	}
	
	/**
	 * 将团队被邀请的成员,修改为团队参与者
	 * @throws Exception 
	 * @user jingjian.wu
	 * @date 2015年9月11日 下午11:15:54
	 */
	public void updateMemberType(long userId) throws Exception{
		log.info("user login--> agree join team ");
		List<TeamMember> teamMember = this.teamMemberDao.findByUserIdAndDel(userId, DELTYPE.NORMAL);
		log.info("user login--> teamMember Count--> "+teamMember.size());
		if(null!=teamMember && teamMember.size()>0){
			for(TeamMember tm:teamMember){
				if(tm.getType().equals(TEAMREALTIONSHIP.ASK)){
					log.info("user login --> join team  :"+tm.getTeamId()+",userId:"+userId);
					boolean existEmm = isAlreadyExistInEMM(tm.getTeamId(), userId);
					log.info("user login --> joined team :"+tm.getTeamId()+",userId:"+userId + ",and existEmm:"+existEmm);
					tm.setType(TEAMREALTIONSHIP.ACTOR);
					tm.setJoinTime(new Timestamp(System.currentTimeMillis()));
					this.teamMemberDao.save(tm);
					//---------------------------增加对应的git权限------begin-----------------
					User currUser = userDao.findOne(userId);//当前人
					List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
					TeamAuth teamA =teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
					Role ro = Cache.getRole(teamA.getRoleId());
					if(null!=teamA && ro.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR)){
						//如果是团队管理员
						List<Permission> listP = ro.getPermissions();
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
										if(app.getUserId()!=currUser.getId().longValue()){
											
											GitAuthVO vo = new GitAuthVO();
											vo.setAuthflag("all");
											vo.setPartnername(currUser.getAccount());
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
										if(app.getUserId()!=currUser.getId().longValue()){
											GitAuthVO vo = new GitAuthVO();
											vo.setAuthflag("allbranch");
											vo.setPartnername(currUser.getAccount());
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
					}
					Map<String,String> map = appService.addGitAuth(listAuth);
					log.info(currUser.getAccount()+" join team id->"+tm.getTeamId()+",and shareallgit->"+map.toString());
					//---------------------------增加对应的git权限------end-----------------
					Team teamToken = teamDao.findOne(tm.getTeamId());
					if(teamToken.getType().equals(TEAMTYPE.ENTERPRISE)){
						//如果是企业授权的团队,还需要将此人添加到EMM那边
						if(!existEmm){
							Personnel personnel = new Personnel();
							User user = userDao.findOne(userId);
							personnel.setName(user.getAccount());
							TeamMember teamCreator = teamMemberService.findMemberByTeamIdAndMemberType(tm.getTeamId(), TEAMREALTIONSHIP.CREATE);
							User teamCrt = userDao.findOne(teamCreator.getUserId());
							personnel.setCreatorId(teamCrt.getAccount());
							personnel.setTeamGroupId(teamDao.findOne(tm.getTeamId()).getUuid());
							personnel.setMobileNo(user.getCellphone());
							personnel.setEmail(user.getAccount());
							personnel.setGroupName(teamDao.findOne(tm.getTeamId()).getName());
							personnel.setTeamDevAddress(xietongHost);
							String token = "";
							String[] params = new String[2];
							
							Enterprise enterprise = tenantFacade.getEnterpriseByShortName(teamToken.getEnterpriseId());
							params[0] = enterprise.getId().toString();
							params[1] = "dev";
							token= TokenUtilProduct.getToken(enterprise.getEntkey(), params);
							log.info("teamMember sync to EMM-->"+personnel.getName());
							String flag = "";
							if(serviceFlag.equals("online")){//线上版本
								flag = personnelFacade.createAdminUser(token, personnel);
							}else if(serviceFlag.equals("enterprise")){//企业版
								flag = personnelFacade.createTeamUser(token, personnel);
							}
							
							log.info("teamMember sync to EMM-->:"+flag);
							if(StringUtils.isNotBlank(flag)){
								throw new RuntimeException("用户登录协同,同意加入被邀请的团队,此时添加EMM成员时候失败,"+flag);
							}
						}
						
					}
				}
			}
		}
	}
	
	/**
	 * 判断一个用户是否是一个团队下面的正式成员
	 * @user jingjian.wu
	 * @date 2015年10月28日 下午6:08:32
	 */
	public boolean isAlreadyExistInEMM(long teamId,long userId){
		List<User> userList = findAllUserBelongTeam(teamId);
		if(null!=userList && userList.size()>0){
			for(User user:userList){
				if(user.getId().longValue()==userId){
					return true;
				}
			}
			return false;
		}else{
			return false;
		}
	}
	
	/**
	 * 根据权限,查看哪些团队下面有该权限,并且返回这些团队下面的权限
	 * @user jingjian.wu
	 * @date 2015年9月25日 下午8:22:12
	 */
	public Map<Long, Map<String, Integer>> permissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		Map<Long, Map<String, Integer>> permissionsMapAsMember = new HashMap<>();

		List<TeamMember> tMembers = this.teamMemberDao.findByUserIdAndDel(loginUserId, DELTYPE.NORMAL);
		if(null==tMembers || tMembers.size()==0){
			return permissionsMapAsMember;
		}
		for(TeamMember tm:tMembers){
			// 遍历角色
			Map<Long, String> permissionUnionMap = new HashMap<>();
			TeamAuth teamAuth = this.teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
			if(null!=teamAuth){
				Role role =  Cache.getRole(teamAuth.getRoleId());
				List<Permission> listPermission = (role == null) ? new ArrayList<Permission>() : role.getPermissions();
				for(Permission p:listPermission){
					permissionUnionMap.put(p.getId(), p.getEnName());
				}
			}
			// 生成权限并集
			List<String> permissionUnionArr = new ArrayList<>(permissionUnionMap.values());
			Map<String, Integer> permissionUnionMaps = new HashMap<String, Integer>();
			// 判定是否存在读取权限
			boolean hasRequiredPermission = false;
			for(String p : permissionUnionArr) {
				permissionUnionMaps.put(p, 1);
				if( permissionEnName.equals( p ) ) {
					hasRequiredPermission = true;
					//break;
				}
			}

			if(hasRequiredPermission) {
				permissionsMapAsMember.put(tm.getTeamId(), permissionUnionMaps);
			}
		}
		
		return permissionsMapAsMember;
	}
	
	/**
	 * 拥有git成员权限的
	 * @user jingjian.wu
	 * @date 2015年12月15日 下午7:19:28
	 */
	public List<TeamMember> membersPermissionWithGit(String required,long teamId){
		List<TeamMember> result = new ArrayList<TeamMember>();
		List<TeamMember> listMember = teamMemberService.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
		if(null!=listMember && listMember.size()>0){
			for(TeamMember mem:listMember){
				TeamAuth auth = teamAuthDao.findByMemberIdAndDel(mem.getId(), DELTYPE.NORMAL);
				Role role = Cache.getRole(auth.getRoleId());
				if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER)){
					continue;//如果是团队普通成员,则跳过
				}
				List<Permission> listPermission = role.getPermissions();
				if(null!=listPermission && listPermission.size()>0){
					for(Permission p:listPermission){
						if(p.getEnName().equals(required)){
							result.add(mem);
							break;
						}
					}
				}
				
			}
		}
		return result;
	}

	/**
	 * 获取已经授权的团队列表,和申请授权的团队列表
	 * @user jingjian.wu
	 * @date 2015年10月19日 下午5:06:16
	 */
	public Page<Team> findTeamAuthList(Integer pageNumber, Integer pageSize, String sortType, String search) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
		Page<Team> list=teamDao.findByTypeOrEnterpriseIdIsNotNull(TEAMTYPE.ENTERPRISE, pageRequest);
		return list;
	}
	
	private PageRequest buildPageRequest(int pageNumber,int pageSize,String sortType){
		Sort sort = null;
        if ("id".equals(sortType)) {
            sort = new Sort(Direction.DESC, "id");
        } else if ("name".equals(sortType)) {
        	sort = new Sort(Direction.DESC, "name");
        }else if("enterpriseName".equals(sortType))
        	sort = new Sort(Direction.DESC, "enterpriseName");
        else {
        	sort = new Sort(Direction.DESC, "createdAt");
        }
        return new PageRequest(pageNumber-1, pageSize, sort);
	}

	
	/**
	 * 获取某个团队下的正式成员和团队下项目中的正式成员
	 * @user jingjian.wu
	 * @date 2015年10月23日 上午10:15:45
	 */
	    
	public List<User> findAllUserBelongTeam(Long teamId) {
		List<User> list = this.userDao.findUserBelongTeam(teamId);
		UserListWrapUtil.setNullForPwdFromUserList(list);//将密码置为空
		return list;
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年11月4日 上午9:38:47
	 */
	    
	public Team findOne(long teamId) {
		Team team = teamDao.findOne(teamId);
		return team;
	}

	
	public boolean isExistInTeam(long teamId, long userId) {
		TeamMember member = this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, userId, DELTYPE.NORMAL);
		if(null!=member.getId()){
			return false;
		}
		return true;
	}

	
	/**
	 * 项目列表中,所属团队筛选,我能查看的团队
	 * 包括我只是参与了某个团队下的项目,但是我并不是该团队的成员,也能看到该团队.
	 * @user jingjian.wu
	 * @date 2016年3月7日 下午2:42:12
	 */
	public List<Team> findReleateTeamList(Long loginUserId,String teamName) {
		List<String> typeList = new ArrayList<>();
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR);
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER);
		List<Long> teamIds = this.teamMemberDao.findByUserIdAndTypeInAndDel(loginUserId,typeList,DELTYPE.NORMAL);
		
		List<PROJECT_MEMBER_TYPE> typeList1 = new ArrayList<>();
		typeList1.add(PROJECT_MEMBER_TYPE.CREATOR);
		typeList1.add(PROJECT_MEMBER_TYPE.PARTICIPATOR);
		List<Long> teamIdsFromProject = this.projectMemberDao.findByUserIdAndTypeInAndDel(loginUserId, typeList1,DELTYPE.NORMAL);
		
		Set<Long> set = new HashSet<>(teamIds);
		set.addAll(teamIdsFromProject);
		
		teamIds = new ArrayList<>(set);
		
		List<Team> list = new ArrayList<Team>();
		if(StringUtils.isNotBlank(teamName)){
			list = this.teamDao.findByIdInAndDelAndNameLikeAndPinYinHeadCharLikeAndPinYinName(teamIds,DELTYPE.NORMAL,"%"+teamName+"%",teamName+"%",teamName+"%");
		}else{
			list = this.teamDao.findByIdInAndDel(teamIds,DELTYPE.NORMAL);
		}
		return list;
	}
	
	
	/**
	 * 查询团队列表时候,筛选条件(团队名称)
	 * loginUserId是团队的成员
	 * @throws ParseException 
	 * @user jingjian.wu
	 * @date 2016年2月29日 上午11:56:22
	 */
	public List<Team> findTeamList(Long loginUserId,String teamName,String begin,String end) throws ParseException {
		List<String> typeList = new ArrayList<>();
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR);
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER);
		List<Long> teamIds = this.teamMemberDao.findByUserIdAndTypeInAndDel(loginUserId,typeList,DELTYPE.NORMAL);
		
		
		List<Team> list = new ArrayList<Team>();
		if(StringUtils.isNotBlank(teamName) && (StringUtils.isBlank(begin) || StringUtils.isBlank(end))){
			list = this.teamDao.findByIdInAndDelAndNameLikeAndPinYinHeadCharLikeAndPinYinName(teamIds,DELTYPE.NORMAL,"%"+teamName+"%",teamName+"%",teamName+"%");
		}else if(StringUtils.isBlank(teamName) && (StringUtils.isBlank(begin) || StringUtils.isBlank(end))){
			list = this.teamDao.findByIdInAndDel(teamIds,DELTYPE.NORMAL);
		}else if(StringUtils.isNotBlank(teamName) && (StringUtils.isNotBlank(begin) || StringUtils.isNotBlank(end))){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			list = this.teamDao.findByIdInAndDelAndNameLikeAndCreatedAtBetween(teamIds,DELTYPE.NORMAL,"%"+teamName+"%",sdf.parse(begin),sdf.parse(end));
		}
		return list;
	}
	
	/**
	 * 查询团队列表时候,筛选条件(团队创建者/团队参与者)
	 * @user jingjian.wu
	 * @param loginUserId 在团队列表中登录人的ID(跟此人相关的团队)
	 * @param creator 关键字查询(人员的账号,邮箱,拼音简称,饮品首字母)
	 * @param ship CREATE,ACTOR(团队创建者/团队参与人)
	 * @date 2016年2月29日 上午11:56:22
	 */
	public List<User> findTeamCreatorOrActorList(Long loginUserId,String keyWords,TEAMREALTIONSHIP ship) {
		List<String> typeList = new ArrayList<>();
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR);
		typeList.add(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER);
		List<Long> teamIds = this.teamMemberDao.findByUserIdAndTypeInAndDel(loginUserId,typeList,DELTYPE.NORMAL);
		
		List<User> list = new ArrayList<User>();
		if(StringUtils.isNotBlank(keyWords)){
			list = userDao.findUserListByTeamIdsAndTeamRelationAndUserLike(ship, teamIds, "%"+keyWords+"%", DELTYPE.NORMAL);
		}else{
			list = userDao.findUserListByTeamIdsAndTeamRelation(ship, teamIds, DELTYPE.NORMAL);
		}
		return list;
	}
	
	/**
	 * 封装用户在团队中的角色信息
	 * @param users
	 * @param teamId
	 */
	public void wrapUserRoleInTeam(List<User> users,Long teamId){
		TeamMember tm = null;
		TeamAuth ta = null;
		for(User u:users){
			tm = teamMemberService.findMemberByTeamIdAndUserId(teamId, u.getId());
			ta = teamAuthService.findByMemberIdAndDel(tm.getId(),DELTYPE.NORMAL);
			u.setRoleNameInTeam(Cache.getRole(ta.getRoleId()).getCnName());
		}
	}
	
	/** 查询一个团队里面的成员数量(除去项目中已有的成员,和一些额外的其他人员)  
     * @Description: 团队项目邀请成员时候,需要调用方法
     * @param @param teamId
     * @param @param projectId
     * @param @param existUserids
     * @param @return 
     * @return List<User>    返回类型
	 * @user tingwei.yuan
	 * @date 2016年3月25日 下午7:30:40
     * @throws
	 */
	public Map<String, Object> findUserCount(Long teamId,Long projectId,List<Long> existsUserIds,String search){
		existsUserIds.add(-9999L);//填充一个默认值,防止existUserIds为null时候报错
		List<TeamGroup> listGroup = this.teamGroupDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
		Map<String, Object> map = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(search)) {
			search = "%" + search +"%";
			for(TeamGroup tg:listGroup){
				map.put(tg.getName(), this.userDao.findUserCount(teamId, tg.getId(), projectId, existsUserIds,search));
			}
			map.put("无分组", this.userDao.findUserCount(teamId,-1L, projectId, existsUserIds,search));
		} else {
			for(TeamGroup tg:listGroup){
				map.put(tg.getName(), this.userDao.findUserCount(teamId, tg.getId(), projectId, existsUserIds));
			}
			map.put("无分组", this.userDao.findUserCount(teamId,-1L, projectId, existsUserIds));
		}
		
		return map;
	}
	
	/** 根据分组id查询一个团队里面的成员列表信息(除去项目中已有的成员,和一些额外的其他人员)  
     * @Description: 团队项目邀请成员时候,需要调用方法
     * @param @param teamId
     * @param @param projectId
     * @param @param existUserids
     * @param @return 
     * @return List<User>    返回类型
	 * @user tingwei.yuan
	 * @date 2016年3月25日 下午7:30:40
     * @throws
	 */
	public Map<String, Object> findUserGroupList(Long teamId,Long groupId,Long projectId,List<Long> existsUserIds,String search){
		//1、填充一个默认值,防止existUserIds为null时候报错
		existsUserIds.add(-9999L);
		//2、根据groupId获取的团队分组的唯一信息
		TeamGroup tg = this.teamGroupDao.findOne(groupId);
		Map<String, Object> map = new HashMap<String, Object>();
		//3、获取每一组的列表信息
        if (StringUtils.isNotBlank(search)) {
        	search = "%"+search+"%";
        	if (tg != null) {
        		map.put(tg.getName(), this.userDao.findUserGroupList(teamId, tg.getId(), projectId,existsUserIds,search));
        	} else {
        		map.put("无分组", this.userDao.findUserGroupList(teamId, groupId, projectId,existsUserIds,search));
        	}
        	
		} else {
			if (tg != null) {
				map.put(tg.getName(), this.userDao.findUserGroupList(teamId, tg.getId(), projectId,existsUserIds));
			} else {
				map.put("无分组", this.userDao.findUserGroupList(teamId, groupId, projectId,existsUserIds));
			}
			
		}
		
		return map;
	}
	/**
	 * 判断该团队下面是否有项目在申请绑定中,申请解绑中,已绑定
	 * @param teamId
	 * @return
	 */
	public String findIsHaveProject(Long teamId) {
		String sql="select count(1) from T_PROJECT where bizLicense!=1 and teamId="+teamId;
		@SuppressWarnings("deprecation")
		int count=this.jdbcTpl.queryForInt(sql);
		if(count>0){
			return "yes";
		}else		
			return "no";
	}

	/**
	 * EMM调用，同意解绑企业
	 * @user haijun.cheng
	 * @date 2016年7月13日
	 * @param teamId
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public List<Object> deleteEnterpriseAgree(Long teamId) {
		List<Object> list = new ArrayList<Object>();
		Team tm = this.teamDao.findOne(teamId);
		if(tm.getType().equals(TEAMTYPE.BINDING)||tm.getType().equals(TEAMTYPE.NORMAL)){
			return null;
		}
		String enterpriseName = tm.getEnterpriseName();
		tm.setEnterpriseId(null);
		tm.setEnterpriseName(null);
		tm.setType(TEAMTYPE.NORMAL);
		this.teamDao.save(tm);
		list.add(tm);
		list.add(enterpriseName);
		
		jdbcTpl.update("update T_PROJECT set bizCompanyId=null ,bizCompanyName=null , bizLicense=? where teamId=?", new Object[]{PROJECT_BIZ_LICENSE.NOT_AUTHORIZED.ordinal(),tm.getId()});

		return list;
	}

	public List<Object> deleteEnterpriseUnagree(Long teamId) {
		List<Object> list = new ArrayList<Object>();
		Team tm = this.teamDao.findOne(teamId);
		if(!tm.getType().equals(TEAMTYPE.UNBINDING)){
			return null;
		}
		String enterpriseName = tm.getEnterpriseName();
		tm.setType(TEAMTYPE.ENTERPRISE);
		this.teamDao.save(tm);
		list.add(tm);
		list.add(enterpriseName);

		return list;
	}


	public int addPinYin() {
		List<Team> tList=this.teamDao.findByDel(DELTYPE.NORMAL);
		for(Team t:tList){
			t.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(t.getName()==null?"":t.getName()));
			t.setPinYinName(ChineseToEnglish.getPingYin(t.getName()==null?"":t.getName()));
		}
		teamDao.save(tList);
		int affect =tList.size();
		return affect;
	}

	public TEAMTYPE findType(Long teamId) {
		Team team=this.teamDao.findOne(teamId);
		return team.getType();
	}
}
