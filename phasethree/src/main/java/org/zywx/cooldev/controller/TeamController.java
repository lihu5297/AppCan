package org.zywx.cooldev.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.dao.UserDao;
import org.zywx.cooldev.dao.project.ProjectDao;
import org.zywx.cooldev.dao.project.ProjectMemberDao;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamGroup;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectCategory;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.UserListWrapUtil;
import org.zywx.cooldev.vo.TeamGroupVO;

/**
 * 
    * @ClassName: TeamController
    * @Description:团队 
    * @author wjj
    * @date 2015年8月11日 上午10:30:26
    *
 */
@Controller
@RequestMapping(value="/team")
public class TeamController extends BaseController {
	@Value("${emmValidHost}")
	private String emmValidHost;
	
	//企业版还是大众版标识
	@Value("${serviceFlag}")
	private String serviceFlag;

	//针对企业版有固定的企业简称
	@Value("${enterpriseId}")
	private String enterpriseId;
	
	//针对企业版有固定的企业全称
	@Value("${enterpriseName}")
	private String enterpriseName;
	
	@Autowired
	protected ProjectDao projectDao;

	@Autowired
	protected JdbcTemplate jdbcTpl;
	
	@Autowired
	protected ProjectMemberDao projectMemberDao;
	
	@Autowired
	protected UserDao userDao;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	/**
	 * 
	    * @Title: teamList
	    * @Description:根据条件查询跟我相关团队列表 
	    * @param @param request
	    * @param @param response
	    * @param @return    参数  type,rel
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/related",method = RequestMethod.GET)
	public Map<String,Object> teamList(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String teamName,String creator,String actor,String begin,String end,@RequestParam(required=false) List<TEAMTYPE> type){
		log.info("come into team related search! loginUserId="+loginUserId+",teamName:"+teamName+",creator:"+creator+",actor:"+actor+",begin:"+begin+",end:"+end);
		
		String sPageNo      = request.getParameter("pageNo");
		String sPageSize    = request.getParameter("pageSize");

		int pageNo       = 1;
		int pageSize     = 20;
		
		try {
			if(sPageNo != null) {
				pageNo		= Integer.parseInt(sPageNo);
			}
			if(sPageSize != null) {
				pageSize	= Integer.parseInt(sPageSize);
			}
			
		} catch (NumberFormatException nfe) {				
			return this.getFailedMap(nfe.getMessage() );
		}

		/*Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
		
		*//**
		 * 0普通团队,1企业团队
		 *//*
		String typeTmp = request.getParameter("type");
		String[] typeStr = null;
		if(StringUtils.isNotBlank(typeTmp)){
			typeStr = typeTmp.split(",");
		}
		
		*//**
		 * 0我创建的，1我参与的，2受邀成员(还没同意)
		 *//*
		String relTmp =request.getParameter("rel");
		String[] relStr = null;
		if(StringUtils.isNotBlank(relTmp)){
			relStr = relTmp.split(",");
		}
		List<TEAMTYPE> type = new ArrayList<TEAMTYPE>();
		List<TEAMREALTIONSHIP> rel = new ArrayList<TEAMREALTIONSHIP>();*/
		try {
			/*if(null !=typeStr){
				for(String str:typeStr){
					type.add(TEAMTYPE.valueOf(str));
				}
			}
			
			if(null !=relStr){
				for(String str:relStr){
					rel.add(TEAMREALTIONSHIP.valueOf(str));
				}
			}
			if(type.size()==0){//如果没有勾选团队类型,默认查询所有(0普通团队,1企业团队)
				type.add(Enums.TEAMTYPE.NORMAL);
				type.add(Enums.TEAMTYPE.ENTERPRISE);
			}
			if(rel.size()==0){//如果没有勾选,我创建/参与,默认查询所有(0我创建的团队,1我参与的团队)
				rel.add(Enums.TEAMREALTIONSHIP.CREATE);
				rel.add(Enums.TEAMREALTIONSHIP.ACTOR);
			}*/
			/**
			//一共和我相关的团队
			Page<Team> list = this.teamService.findTeamList(rel, loginUserId, type,  DELTYPE.NORMAL, teamName, creator, actor, begin, end,pageable);
			//下面获取有权限查看的团队
//			String required = (ENTITY_TYPE.TEAM + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
//			Map<Long, Map<String,Integer>> mapPermission = teamService.permissionMapAsMemberWith(required, loginUserId);
			
			List< Map<String, Object> > arr = new ArrayList<Map<String,Object>>();//最终返回的结果
			
			//遍历所有和我相关的团队   返回有权限查看的一部分
			for(Team t:list.getContent()){
//				if(mapPermission.keySet().contains(t.getId())){
					Map<String, Object> map = new HashMap<String, Object>();
					TeamMember tmCreator = teamMemberService.findMemberByTeamIdAndMemberType(t.getId(), TEAMREALTIONSHIP.CREATE);
					User userTeamCreator = userService.findUserById(tmCreator.getUserId());
					t.setCreator(userTeamCreator.getUserName());
					List<Project> listProj = projectDao.findByTeamIdAndDel(t.getId(), DELTYPE.NORMAL);
					t.setProjectSum(listProj.size());
					List<TeamMember> listMembers = teamMemberService.findByTeamIdAndDel(t.getId(), DELTYPE.NORMAL);
					t.setMemberSum(listMembers.size());
					map.put("object", t);
//					map.put("permission", mapPermission.get(t.getId()));
					arr.add(map);
//				}
			}
			Map<String, Object> message = new HashMap<>();
			message.put("list", arr);
			message.put("total", list.getTotalElements());
			return this.getSuccessModel(message);
			**/
			Map<String,Object> map = teamService.findTeamList(loginUserId, type,  teamName, creator, actor, begin, end, pageNo, pageSize);
			return map;
		} catch (Exception nfe) {
			nfe.printStackTrace();
			return this.getFailedMap("查询团队列表失败");
		}
		
	}
	
	/**
	 * 
	    * @Title: addTeam
	    * @Description: 创建团队,并且增加一条记录到团队成员表(我创建的团队)
	    * @param @param team
	    * @param @param request
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView addTeam(Team team,HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		try {
			if(team.getName()!=null&&team.getName().length()>1000){
				return this.getFailedModel("团队名称不能超过1000个字符");
			}
			if(team.getDetail()!=null&&team.getDetail().length()>1000){
				return this.getFailedModel("团队描述不能超过1000个字符");
			}
			//如果invokeType值为emm,则不去调用EMM创建人的接口
			String invokeType = request.getParameter("invokeType");
			log.info("post team:"+team.toStr()+",invokeType:"+invokeType+",loginUserId:"+loginUserId);
			log.info("begin create team :"+"开始创建团队");
			if(serviceFlag.equals("enterprise")||serviceFlag.equals("enterpriseEmm3")){
				team.setType(TEAMTYPE.ENTERPRISE);
				team.setEnterpriseId(enterpriseId);
				team.setEnterpriseName(enterpriseName);
			}else{
				team.setType(TEAMTYPE.NORMAL);
				team.setEnterpriseId(null);
				team.setEnterpriseName(null);
			}
			if(serviceFlag.equals("enterprise")){
				if(StringUtils.isBlank(team.getEnterpriseId()) || StringUtils.isBlank(team.getEnterpriseName())){
					return this.getFailedModel("所填信息不完整");
				}
				/*//20170.06.20 不去调用EMM服务了,EMM服务已停
				 * List<NameValuePair> parameters = new ArrayList<>();
				parameters.add( new BasicNameValuePair("shortName", team.getEnterpriseId()) );
				parameters.add( new BasicNameValuePair("fullName", team.getEnterpriseName() ) );
				log.info("shortName-->"+team.getEnterpriseId());
				log.info("fullName-->"+team.getEnterpriseName());
				if(team.getEnterpriseName().equals("正益移动")){
					log.info("full Name is correct!");
				}else{
					log.info("full Name is not correct!");
				}
				String result = HttpUtil.httpPost(emmValidHost+"/omm/enterprise/validName", parameters);
				log.info("added Enterprise Team ->validName:"+result);
				JSONObject jsonObject = JSONObject.fromObject(result);
				String status = jsonObject.getString("status");
				if("fail".equals(status)){
					log.info("====>info:"+jsonObject.getString("info"));
					return this.getFailedModel("企业标识和企业名称不匹配");
				}*/
			}else if(serviceFlag.equals("online")){
				team.setEnterpriseId(null);
				team.setEnterpriseName(null);
			}
			this.teamService.addTeam(team,loginUserId,serviceFlag,invokeType);
			//增加动态
			this.dynamicService.addTeamDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TEAM_ADD, team.getId(), team);
			return this.getSuccessModel(team);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * 
	    * @Title: emmInvokeaddTeam
	    * @Description: emm调用创建团队,并且增加一条记录到团队成员表(我创建的团队)
	    * @param @param team
	    * @param @param request
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/emmInvokeaddTeam",method = RequestMethod.POST)
	public ModelAndView emmInvokeaddTeam(Team team,HttpServletRequest request,
			@RequestHeader(value="currentLoginAccount",required=true) String currentLoginAccount){
		try {
			if(team.getName()!=null&&team.getName().length()>1000){
				return this.getFailedModel("团队名称不能超过1000个字符");
			}
			if(team.getDetail()!=null&&team.getDetail().length()>1000){
				return this.getFailedModel("团队描述不能超过1000个字符");
			}
			log.info("post team:"+team.toStr()+",currentLoginAccount:"+currentLoginAccount);
			log.info("begin create team :"+"开始创建团队");
			if(serviceFlag.equals("enterprise")){
				team.setType(TEAMTYPE.ENTERPRISE);
				team.setEnterpriseId(enterpriseId);
				team.setEnterpriseName(enterpriseName);
			}else if("online".equals(serviceFlag)){
				team.setType(TEAMTYPE.ENTERPRISE);
				if(StringUtils.isBlank(team.getEnterpriseId()) || StringUtils.isBlank(team.getEnterpriseName())){
					return this.getFailedModel("所填信息不完整");
				}
			}
			if(serviceFlag.equals("enterprise")){
				List<NameValuePair> parameters = new ArrayList<>();
				parameters.add( new BasicNameValuePair("shortName", team.getEnterpriseId()) );
				parameters.add( new BasicNameValuePair("fullName", team.getEnterpriseName() ) );
				log.info("shortName-->"+team.getEnterpriseId());
				log.info("fullName-->"+team.getEnterpriseName());
				if(team.getEnterpriseName().equals("正益移动")){
					log.info("full Name is correct!");
				}else{
					log.info("full Name is not correct!");
				}
				String result = HttpUtil.httpPost(emmValidHost+"/omm/enterprise/validName", parameters);
				log.info("added Enterprise Team ->validName:"+result);
				JSONObject jsonObject = JSONObject.fromObject(result);
				String status = jsonObject.getString("status");
				if("fail".equals(status)){
					log.info("====>info:"+jsonObject.getString("info"));
					return this.getFailedModel("企业标识和企业名称不匹配");
				}
			}
			User  user = this.userService.saveUserIfNotExistByEmail(currentLoginAccount);
			this.teamService.emmAddTeam(team,user.getId(),serviceFlag);
			//增加动态
			this.dynamicService.addTeamDynamic(user.getId(), DYNAMIC_MODULE_TYPE.TEAM_ADD, team.getId(), team);
			return this.getSuccessModel(team);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * 
	    * @Title: updateTeam
	    * @Description:修改团队名称，团队描述 
	    * @param @param id
	    * @param @param team
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/{teamId}",method=RequestMethod.PUT)
	public ModelAndView updateTeam(@PathVariable(value="teamId") Long teamId,Team team,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			if(team.getName()!=null&&team.getName().length()>1000){
				return this.getFailedModel("团队名称不能超过1000个字符");
			}
			if(team.getDetail()!=null&&team.getDetail().length()>1000){
				return this.getFailedModel("团队描述不能超过1000个字符");
			}
			team.setId(teamId);
			Team tm = teamService.getOne(teamId);
			boolean noticeFlag = false;
			if(!tm.getName().equals(team.getName())){
				noticeFlag = true;
			}
			Team t = this.teamService.updateTeam(team,loginUserId);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_EDIT, team.getId(), team);
			
			if(noticeFlag){
				List<TeamMember> listTm = this.teamMemberService.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
				if(null==listTm || listTm.size()==0){
					return this.getSuccessModel(t);
				}
				List<Long> noticeTargetUserIds = new ArrayList<Long>();
				for(TeamMember tmember:listTm){
					if(Long.parseLong(loginUserId)==tmember.getUserId()){
						continue;
					}
					noticeTargetUserIds.add(tmember.getUserId());
				}
				User user = this.userService.findUserById(Long.parseLong(loginUserId));
				String userName = user.getUserName()==null?user.getAccount():user.getUserName();
				//增加通知
				this.noticeService.addNotice(Long.parseLong(loginUserId), (Long[])noticeTargetUserIds.toArray(new Long[noticeTargetUserIds.size()]), NOTICE_MODULE_TYPE.TEAM_UPDATE, new Object[]{userName,tm,team});
			}
			return this.getSuccessModel(t);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * 如果是协同自己调用方法,teamId传team表中的主键,如果是Emm调用接口,teamId传的是uuid
	    * @Title: deleteTeam
	    * @Description: 解散团队
	    * @param @param id
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/{teamId}",method=RequestMethod.DELETE)
	public ModelAndView deleteTeam(@PathVariable(value="teamId") String teamId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId,
			@RequestParam(required=false) String invokeType){
		try {
			log.info(" come into  method deleteTeam :teamId:"+teamId +",loginUserId:"+loginUserId);
//			Team tmpTeam = teamService.getOne(teamId);
			
			if("emm".equals(invokeType)){
				//如果是EMM调用此接口
				Team tmp = teamService.getByUuid(teamId);
				if(null==tmp){
					throw new RuntimeException("团队不存在");
				}
				teamId = tmp.getId().toString();
			}
			TeamMember teamMem = teamMemberService.findMemberByTeamIdAndMemberType(Long.parseLong(teamId), TEAMREALTIONSHIP.CREATE);
			if(teamMem==null || teamMem.getUserId().longValue()!=Long.parseLong(loginUserId)){
				return this.getFailedModel("对不起,您没有权限解散此团队.");
			}
			
			List<TeamMember> listTm = this.teamMemberService.findByTeamIdAndDel(Long.parseLong(teamId), DELTYPE.NORMAL);
			
			Team tm = this.teamService.deleteTeam(Long.parseLong(teamId),loginUserId,invokeType);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_EXPIRE, Long.parseLong(teamId), tm);
			
			List<Long> receiveIds = new ArrayList<Long>();
			for(TeamMember tme:listTm){
				if(tme.getUserId()!=Long.parseLong(loginUserId)){
					receiveIds.add(tme.getUserId());
				}
			}
			User user = this.userService.findUserById(Long.parseLong(loginUserId));
			//增加通知
			this.noticeService.addNotice(Long.parseLong(loginUserId),receiveIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TEAM_REMOVE, new Object[]{user,tm});
			return this.getAffectModel();
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel("解散失败！");
		}
	}
	
	/**
	 * 页面申请企业绑定
	    * @Title: updateEnterprise
	    * @Description:绑定企业ID 
	    * @param @param teamId
	    * @param @param enterpriseId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/enterprise/{teamId}",method=RequestMethod.PUT)
	public ModelAndView updateEnterprise(@PathVariable(value="teamId") Long teamId, String enterpriseId,String enterpriseName,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("bind team Enterprise : enterpriseId:"+enterpriseId+",enterpriseName:"+enterpriseName+",teamId:"+teamId);
			if(StringUtils.isBlank(enterpriseId) || StringUtils.isBlank(enterpriseName)){
				return this.getFailedModel("企业ID和企业名称不可以为空");
			}
			List<NameValuePair> parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("shortName", enterpriseId) );
			parameters.add( new BasicNameValuePair("fullName", enterpriseName ) );
			String result = HttpUtil.httpPost(emmValidHost+"/omm/enterprise/validName", parameters);//判断输入的企业简称和企业名称是否存在，并且匹配
			log.info("teamBind ->validName:"+result);
			JSONObject jsonObject = JSONObject.fromObject(result);
			String status = jsonObject.getString("status");
			if("fail".equals(status)){
				log.info("====>info:"+jsonObject.getString("info"));
				return this.getFailedModel("企业标识和企业名称不匹配");
			}
			Team tm = this.teamService.updateEnterprise(enterpriseId,enterpriseName, teamId,loginUserId);
			if(null==tm){
				return this.getFailedModel("此团队正在处理中,不可以重新申请授权");
			}
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_ASK_BIND, teamId, new Object[]{tm,enterpriseName});
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * 页面申请取消绑定
	 * @user jingjian.wu
	 * @date 2015年10月22日 上午10:26:08
	 */
	@ResponseBody
	@RequestMapping(value="/unenterprise/{teamId}",method=RequestMethod.PUT)
	public ModelAndView unenterprise(@PathVariable(value="teamId") Long teamId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("bind team Enterprise : "+",teamId:"+teamId);
			
			if(this.teamService.findType(teamId).equals(TEAMTYPE.ENTERPRISE)){
				return this.getFailedModel("该团队已授权,不可以取消授权，请刷新页面");
			}
			if(this.teamService.findType(teamId).equals(TEAMTYPE.NORMAL)){
				return this.getFailedModel("该团队授权被拒绝,不可以取消授权，请刷新页面");
			}
			
			List list = this.teamService.updateCancelEnterprise(teamId);
			if(null==list){
				return this.getFailedModel("此团队已经被授权,不可以取消");
			}

			Team tm = (Team) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_CANCEL_BIND, teamId, new Object[]{tm,enterpriseName});
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * EMM调用通过企业绑定
	 * @user jingjian.wu
	 * @date 2015年10月19日 下午4:49:15
	 */
	@ResponseBody
	@RequestMapping(value="/authronized/{teamId}",method=RequestMethod.POST)
	public ModelAndView authronized(@PathVariable(value="teamId") String uuid, 
			HttpServletRequest request){
		try {
//			if(!validateIP(request)){
//				return this.getFailedModel("ip is not available");
//			}
			log.info("EMM bind team Enterprise ,uuid:"+uuid);
			Team tmp = teamService.getByUuid(uuid);
			if(null!=tmp && tmp.getType().equals(TEAMTYPE.ENTERPRISE)){
				return this.getFailedModel("此团队已经被授权,无需重复授权");
			}
			Team tm = this.teamService.updateType(tmp.getId());
			if(null==tm){
				return this.getFailedModel("所填信息不完整,企业ID,企业名称不可以为空");
			}
			//增加动态
//			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_BIND_ENTERPRISE, teamId, new Object[]{tm,tm.getEnterpriseName()});
			//添加通知
			List<TeamMember> members = this.teamMemberService.findByTeamIdAndDel(tm.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(TeamMember member : members){
				if(member.getType().compareTo(TEAMREALTIONSHIP.CREATE)==0){
					ids.add(member.getUserId());
				}else{
					TeamAuth ta = this.teamAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					Role role = Cache.getRole(ta.getRoleId());
					if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
						ids.add(member.getUserId());
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TEAM_BIND_ENTERPRISE, new Object[]{tm,tm.getEnterpriseName()});
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("team", tm);
			List<User> list = this.teamService.findAllUserBelongTeam(tm.getId());
			TeamMember teamCrt = teamMemberService.findMemberByTeamIdAndMemberType(tm.getId(), TEAMREALTIONSHIP.CREATE);
			for(User user:list){
				if(user.getId().longValue()==teamCrt.getUserId().longValue()){
					user.setTeamCreator(true);
				}
			}
			map.put("users", list);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * EMM调用接口解绑企业绑定
	 * @param id
	 * @param loginUserId
	 * @return Map<String,Object>
	 * @user jingjian.wu
	 * @date 2015年8月21日 上午11:57:40
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/unauthronized/{teamId}",method=RequestMethod.POST)
	public ModelAndView updateUnEnterprise(@PathVariable(value="teamId") String uuid,
			HttpServletRequest request){
		try {
//			if(!validateIP(request)){
//				return this.getFailedModel("ip is not available");
//			}
			log.info(" unbind team Enterprise : uuid:"+uuid);
			Team tmp = teamService.getByUuid(uuid);
//			if(null!=tmp && tmp.getType().equals(TEAMTYPE.NORMAL)){
//				return this.getFailedModel("此团队没有绑定企业,无需解除绑定");
//			}
			List list= this.teamService.updateUnEnterprise(tmp.getId());
			Team tm = (Team) list.get(0);
			String updateUnEnterprise = (String) list.get(1);
			
//			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_UNBIND_ENTERPRISE, teamId, new Object[]{tm,updateUnEnterprise});
			//添加通知
			List<TeamMember> members = this.teamMemberService.findByTeamIdAndDel(tmp.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(TeamMember member : members){
				if(member.getType().compareTo(TEAMREALTIONSHIP.CREATE)==0){
					ids.add(member.getUserId());
				}else{
					TeamAuth ta = this.teamAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					Role role = Cache.getRole(ta.getRoleId());
					if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
						ids.add(member.getUserId());
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TEAM_UNBIND_ENTERPRISE, new Object[]{tm,updateUnEnterprise});
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * 
	    * @Title: getMemberInfo
	    * @Description:获取团队成员信息 
	    * @param @param teamId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/member/{teamId}",method=RequestMethod.GET)
	public ModelAndView getMemberInfo(HttpServletRequest request,@PathVariable(value="teamId") Long teamId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(value="type",required=false) String type,
			@RequestParam(value="keywords",required=false) String keywords){
		Map<String, Object> innerMap = new HashMap<String, Object>();
		
		String sPageNo      = request.getParameter("pageNo");
		String sPageSize    = request.getParameter("pageSize");

		int pageNo       = 0;
		int pageSize     = 20;
		
		if(StringUtils.isNotBlank(keywords)){
			keywords="%"+keywords+"%";
		}
		
		try {
			if(sPageNo != null) {
				pageNo		= Integer.parseInt(sPageNo)-1;
			}
			if(sPageSize != null) {
				pageSize	= Integer.parseInt(sPageSize);
			}
			
		} catch (NumberFormatException nfe) {				
			return this.getFailedModel(nfe.getMessage() );
		}

		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
		
		try {
			log.info("team Member detail!,teamId="+teamId+",loginUserId="+loginUserId);
			//查询人loginUserId是不是该团队teamId成员
			Long groupId = this.teamMemberService.findGroupIdByTeamIdAndUserId(teamId, loginUserId);
			if(null==groupId){
				return this.getFailedModel("userId: "+loginUserId+",have no permission for team with Id: "+teamId);
			}
			Team team = this.teamService.getOne(teamId);
			innerMap.put("object", team);
			List<TEAMREALTIONSHIP> typeIds = new ArrayList<TEAMREALTIONSHIP>();
			typeIds.add(TEAMREALTIONSHIP.CREATE);//我创建
			typeIds.add(TEAMREALTIONSHIP.ACTOR);//我参与
			//获取所有已有成员
			Page<User> allUsers=this.userService.findUserListByTeamIdAndRel(typeIds, teamId,pageable,type,keywords);
			List<User> allUser = allUsers.getContent();
			UserListWrapUtil.setNullForPwdFromUserList(allUser);
			typeIds.clear();
			typeIds.add(TEAMREALTIONSHIP.ASK);//被邀请
			//获取已邀请成员
			Page<User> askUsers=this.userService.findUserListByTeamIdAndRel(typeIds, teamId,pageable,type,keywords);
			List<User> askUser = askUsers.getContent();
			UserListWrapUtil.setNullForPwdFromUserList(askUser);
			innerMap.put("total", allUsers.getTotalElements());//所有创建/参与的成员总数(不包括邀请的成员)
			teamService.wrapUserRoleInTeam(allUser, teamId);
			teamService.wrapUserRoleInTeam(askUser, teamId);
			innerMap.put("allUserList", allUser);//所有成员的列表
			innerMap.put("askUsersList", askUser);//所有被邀请的成员列表
			innerMap.put("askUsersTotal", askUsers.getTotalElements());//所有被邀请的成员列表
			//获取团队成员表中,所有的小组,及每个小组的人员总数
			List<TeamGroupVO> listGroup = this.teamGroupService.findGroupInfoByTeamId(teamId,keywords);
			
			//获取团队小组表中所有的组信息
//			List<TeamGroup> listG = this.teamGroupService.findAllByTeamId(teamId);
			List<TeamMember> listMember = new ArrayList<TeamMember>();
			if(StringUtils.isNotBlank(keywords)){
				listMember = this.teamMemberService.findByTeamIdAndDelAndKeywords(teamId, DELTYPE.NORMAL,keywords);
			}else{
				listMember = this.teamMemberService.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
			}
			int unGrouptotal = 0;
			for(TeamMember member:listMember){
				if(member.getGroupId().longValue()==-1 && !member.getType().equals(TEAMREALTIONSHIP.ASK)){
					unGrouptotal++;
				}
			}
			TeamGroupVO v = new TeamGroupVO();
			v.setGroupId(-1);
			v.setTotal(unGrouptotal);
			v.setName("无分组");
			listGroup.add(v);
			
			innerMap.put("groupInfo", listGroup);//团队中所有的小组,并且每个小组的成员个数
			Map<String, Integer> listPermission = this.teamService.findPermissionForTeam(teamId, loginUserId);
			innerMap.put("permissions", listPermission);//返回当前人在该团队下面的权限
			return this.getSuccessModel(innerMap);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	
	
	/**
	 * 获取某个团队基本信息
	    * @Title: findOneById
	    * @Description: 
	    * @param @param teamId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user wjj
		* @date 2015年8月12日 下午6:42:54
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/{teamId}",method=RequestMethod.GET)
	public ModelAndView findOneById(@PathVariable("teamId") long teamId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try {
			log.info("get team baseInfo with teamId="+teamId+",loginUserId="+loginUserId);
			//判断当前登录人是否有权限查询这个团队
			Long groupId = this.teamMemberService.findGroupIdByTeamIdAndUserId(teamId, loginUserId);
			if(null==groupId){
				return this.getFailedModel("userId: "+loginUserId+",have no permission for team with Id: "+teamId);
			}
			Team t = this.teamService.getOne(teamId);
			if(null !=t){
				if(t.getDel().equals(DELTYPE.DELETED)){//已删除
					return this.getFailedModel("not found team with id=" + teamId);
				}else{
					Map<String, Object> innerMap = new HashMap<String, Object>();
					innerMap.put("team", t);//如果是线上版本,则需要判断enterpriseId有值，但是type为个人项目,则代表申请中
					Map<String, Integer> listPermission = this.teamService.findPermissionForTeam(teamId, loginUserId);
					
					if(t.getEnterpriseId()!=null && serviceFlag.equals("online")){//如果是大众版,并且已经绑定企业,或者申请中,或者解绑中
						if(listPermission.containsKey("team_remove")){
							listPermission.remove("team_remove");
						}
						if(listPermission.containsKey("team_transfer")){
							listPermission.remove("team_transfer");
						}
					}
					/*if(t.getEnterpriseId()!=null){//如果团队申请绑定中,或者已经绑定企业,则不允许解散和转让
						if(listPermission.containsKey("team_remove")){
							listPermission.remove(listPermission.get("team_remove"));
						}
						if(listPermission.containsKey("team_transfer")){
							listPermission.remove(listPermission.get("team_transfer"));
						}
					}*/
					innerMap.put("permissions", listPermission);//返回当前人在该团队下面的权限
					return this.getSuccessModel(innerMap);
				}
			}else{
				return this.getFailedModel("not found team with id=" + teamId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
		
	}
	
	
	/** 查询某个团队下面的正式成员
	    * @Title: getMembers
	    * @Description: 根据团队ID查找该团队下面的正式成员信息
	    * @param @param teamId
	    * @param @param loginUserId
	    * @param search  搜索用户昵称,邮箱
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午8:48:40
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/alluser/{teamId}",method=RequestMethod.GET)
	public ModelAndView getMembers(HttpServletRequest request,@PathVariable(value="teamId") Long teamId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String search){
		try {
			log.info("get formal user belong team! with teamId="+teamId);
			
			String sPageNo      = request.getParameter("pageNo");
			String sPageSize    = request.getParameter("pageSize");

			int pageNo       = 0;
			int pageSize     = 20;
			
			try {
				if(sPageNo != null) {
					pageNo		= Integer.parseInt(sPageNo)-1;
				}
				if(sPageSize != null) {
					pageSize	= Integer.parseInt(sPageSize);
				}
				
			} catch (NumberFormatException nfe) {				
				return this.getFailedModel(nfe.getMessage() );
			}

			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
			
			//判断当前登录人是否有权限查询这个团队
			Long groupId = this.teamMemberService.findGroupIdByTeamIdAndUserId(teamId, loginUserId);
			if(null==groupId){
				return this.getFailedModel("userId: "+loginUserId+",have no permission for team with Id: "+teamId);
			}
			List<TEAMREALTIONSHIP> typeIds = new ArrayList<TEAMREALTIONSHIP>();
			typeIds.add(TEAMREALTIONSHIP.CREATE);//我创建
			typeIds.add(TEAMREALTIONSHIP.ACTOR);//我参与
			if(null==search){
				search="";
			}
			//获取所有已有成员
			Page<User> allUser=this.userService.findUserListByTeamIdAndRelAndSearch(typeIds, teamId,"%"+search+"%",pageable);
			List<User> allUsers = allUser.getContent();
			UserListWrapUtil.setNullForPwdFromUserList(allUsers);
			
			Map<String, Object> message = new HashMap<>();
			message.put("list", allUsers);
			message.put("total", allUser.getTotalElements());
			return this.getSuccessModel(message);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	
	/**
	    * @Title: 创建团队项目-createProj
	    * @Description: 创建团队项目
	    * @param @param p
	    * @param @param userStr    jingjian.wu@3g2win.com#ACTOR;jiexiong.liu@3g2win.com#MANAGER
	    * @param @param userIds		3,4  （这个团队下面所选的人的id）
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月15日 下午7:53:26
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/project",method=RequestMethod.POST)
	public ModelAndView createProj(Project p,Long teamId,@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try {
			log.info("create team project,teamId="+teamId+",project:"+p.toStr());
			p.setType(PROJECT_TYPE.TEAM);//团队项目
			p.setTeamId(teamId);
			log.info("create team Project:"+p.toStr());
			p  =this.teamService.addProject(p,loginUserId);
			//增加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TEAM_CREATE_PRJ,p.getId(), p);
			return this.getAffectModel();
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	
	/**
	    * @Title: findProj
	    * @Description: 查询团队下面的项目列表
	    * 如果是团队创建者/管理员,可以查看该团队下面的所有项目,
	    * 如果是团队普通成员,只能查看所参与的此团队下的项目
	    * @param @param teamId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @param creator (项目创建者账号)
		* @param begin 创建开始时间
		* @param end 创建结束时间
		* @param pfdate_begin 计划完成时间(区间的开始时间)  plan finished date
		* @param pfdate_end 计划完成时间(区间的结束时间)
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月15日 下午8:04:25
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/project/{teamId}",method=RequestMethod.GET)
	public ModelAndView findProj(@PathVariable(value="teamId") Long teamId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,HttpServletRequest request,
			@RequestParam(required=false) List<PROJECT_MEMBER_TYPE> memberType,
			@RequestParam(required=false) List<PROJECT_BIZ_LICENSE> bizLicense,
			@RequestParam(required=false) List<PROJECT_STATUS> status,Integer parentId,
			String projName,String creator,String actor,String begin,String end, String pfstime,String pfetime,
			Long categoryId){
		try {
			
			String sPageNo      = request.getParameter("pageNo");
			String sPageSize    = request.getParameter("pageSize");

			int pageNo       = 1;
			int pageSize     = 20;
			
			try {
				if(sPageNo != null) {
					pageNo		= Integer.parseInt(sPageNo);
				}
				if(sPageSize != null) {
					pageSize	= Integer.parseInt(sPageSize);
				}
				
			} catch (NumberFormatException nfe) {				
				return this.getFailedModel(nfe.getMessage());
			}
			
			
			Map<String, Object> innerMap = new HashMap<String, Object>();
			//查询人loginUserId是不是该团队teamId成员
			/*Long groupId = this.teamMemberService.findGroupIdByTeamIdAndUserId(teamId, loginUserId);
			if(null==groupId){
				return this.getFailedModel("userId: "+loginUserId+",have no permission for team with Id: "+teamId);
			}*/
			boolean owner = false;
			TeamMember member = teamMemberService.findMemberByTeamIdAndUserId(teamId, loginUserId);
			if(null!=member){
				if(member.getType().equals(TEAMREALTIONSHIP.CREATE)){
					owner = true;
				}
				TeamAuth teamAuth = teamAuthService.findByMemberIdAndDel(member.getId(),DELTYPE.NORMAL);
				if(null!=teamAuth && teamAuth.getRoleId()==Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId().longValue()){
					owner = true;
				}
			}
			Team team = teamService.getOne(teamId);
			if(null!=team){
				innerMap.put("team", team);
			}
			Map<String, Integer> listPermission = this.teamService.findPermissionForTeam(teamId, loginUserId);
			innerMap.put("permissions", listPermission);//返回当前人在该团队下面的权限
			if(owner){
				listPermission.put("create_team_prj", 1);//如果是团队创建者/管理员,则返回可以创建团队项目的权限
			}
			
			final List<Project> pList = new ArrayList<Project>();
			List<Long> roleIds = new ArrayList<>();//团队下的普通成员是不可以查看团队下项目的,所以下面的角色没有团队成员
			roleIds.add(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR).getId());
			roleIds.add(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId());
			List<Long> projectIds = null;
			
			if(null==memberType || memberType.size()==0){
				memberType = new ArrayList<PROJECT_MEMBER_TYPE>();
				memberType.add(PROJECT_MEMBER_TYPE.CREATOR);
				memberType.add(PROJECT_MEMBER_TYPE.PARTICIPATOR);
			}
			//团队下的项目
			List<Long> projectIdFromTeam = this.projectDao.findLongByTeamIdAndRoleIdAndDel(loginUserId,teamId,roleIds,DELTYPE.NORMAL);
			projectIds = this.projectDao.findByTeamAndUserIdAndTypeAndDel(teamId,loginUserId,memberType,DELTYPE.NORMAL);
			if(null==memberType || memberType.size()==2){
				projectIds.addAll(projectIdFromTeam);
			}
			
			//项目主键去重
			Set<Long> set = new HashSet<>(projectIds);
			StringBuffer projectIdStr = new StringBuffer();
			Iterator<Long> it = set.iterator();
			while (it.hasNext())
			{
				projectIdStr.append(it.next()).append(",");
			}
			
			//项目id为空时 添加伪id
			projectIdStr.append("-99");
			
			String bizLicenseStr = "";
			if(bizLicense == null) {
				bizLicenseStr = "0,1,2,3";
			}else if(bizLicense.size()==1){
				if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.AUTHORIZED){
					bizLicenseStr = "0";
				}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.NOT_AUTHORIZED){
					bizLicenseStr = "1";
				}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.BINDING){
					bizLicenseStr = "2";
				}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.UNBINDING){
					bizLicenseStr = "3";
				}
			}else{
				int i=0;
				for(PROJECT_BIZ_LICENSE biz:bizLicense){
					if(i==0){
						bizLicenseStr+=biz.ordinal();
					}else{
						bizLicenseStr+=","+biz.ordinal();
					}
					i++;
				}
			}
			
			
			String statusStr = "";
			if(status == null) {
				status = new ArrayList<PROJECT_STATUS>();
				statusStr="0,1";
			}else if(status.size()==1){
				if(status.get(0)==PROJECT_STATUS.FINISHED){
					statusStr="0";
				}else if(status.get(0)==PROJECT_STATUS.ONGOING){
					statusStr="1";
				}
			}else{
				statusStr="0,1";
			}
			//执行查询
			StringBuffer baseSql = new StringBuffer();
			baseSql.append("select p.id,p.name,p.detail,p.categoryId,p.createdAt,p.updatedAt,p.type,p.status,p.bizLicense"
					+ " ,p.teamId,p.bizCompanyId,p.bizCompanyName , t.endDate from T_PROJECT p  ")
					.append(" LEFT JOIN  ")
					.append(" (SELECT MAX(endDate) endDate,projectId FROM T_PROCESS WHERE del=0 GROUP BY projectId) t  ON p.id =t.projectId  ");
			StringBuffer sbSql = new StringBuffer();
			sbSql.append(" where p.id in(").append(projectIdStr).append(") ")
			.append(" and p.bizLicense in(").append(bizLicenseStr).append(") ")
			.append(" and p.status in(").append(statusStr).append(")")
			.append(" and p.del=0 ");
			if( StringUtils.isNotBlank(projName)){
				sbSql.append(" and p.name like '%").append(projName).append("%'");
			}
			if( parentId!=null){
				sbSql.append(" and p.parentId=").append(parentId);
			}
			if(StringUtils.isNotBlank(begin)){
//				sbSql.append(" and p.createdAt>= '").append(begin).append("' and p.createdAt<='").append(end).append("'");
				sbSql.append(" and DATE_FORMAT(p.createdAt, '%Y-%m-%d %k:%i:%s') >= '"+begin+"' ");
			}
			if(StringUtils.isNotBlank(end)){
//				sbSql.append(" and p.createdAt>= '").append(begin).append("' and p.createdAt<='").append(end).append("'");
				sbSql.append(" and DATE_FORMAT(p.createdAt,'%Y-%m-%d') <= '"+end+"' ");
			}
			if(categoryId != null){
				sbSql.append(" and p.categoryId =").append(categoryId);
			}
			if(StringUtils.isNotBlank(creator)){
				sbSql.append(" and p.id in( ")
				.append("select distinct pm.projectId from T_PROJECT_MEMBER pm where pm.type=")
				.append(PROJECT_MEMBER_TYPE.CREATOR.ordinal())
				.append(" and pm.del=0 and pm.userId in (select u.id from T_USER u where u.userName like '%").append(creator).append("%')")
				.append(")");
			}
			if(StringUtils.isNotBlank(actor)){
				String actorRoleIds = "";
				actorRoleIds+=Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR).getId();
				actorRoleIds+=","+Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.MEMBER).getId();
				actorRoleIds+=","+Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.OBSERVER).getId();
			
				sbSql.append(" and p.id in( ")
				.append("select distinct pm.projectId from T_PROJECT_MEMBER pm left join T_PROJECT_AUTH ta on ta.memberId=pm.id where ")
				.append("  pm.del=0 and pm.userId in (select u.id from T_USER u where u.userName like '%").append(actor).append("%')")
				.append(" and ta.roleId in ( ").append(actorRoleIds).append(")")
				.append(")");
				
//				sbSql.append(" and p.id in( ")
//				.append("select distinct pm.projectId from T_PROJECT_MEMBER pm where pm.type=")
//				.append(PROJECT_MEMBER_TYPE.PARTICIPATOR.ordinal())
//				.append(" and pm.del=0 and pm.userId in (select u.id from T_USER u where u.userName like '%").append(actor).append("%')")
//				.append(")");
			}
			if(StringUtils.isNotBlank(pfstime)){
				sbSql.append(" and t.endDate>= '").append(pfstime).append("'");
			}
			if(StringUtils.isNotBlank(pfetime)){
				sbSql.append(" and DATE_FORMAT(t.endDate,'%Y-%m-%d')<='").append(pfetime).append("'");
			}
			
			sbSql.append(" order by p.createdAt desc ");
			
			StringBuffer pageSql = new StringBuffer("");
			pageSql.append( " limit " +(pageNo-1)*pageSize + ", "+pageSize);
			String totleSql = "select count(1) from T_PROJECT p LEFT JOIN  "
					+ " (SELECT MAX(endDate) endDate,projectId FROM T_PROCESS WHERE del=0 GROUP BY projectId) t  ON p.id =t.projectId "
					+sbSql.toString();
			
			String lastSql = "SELECT * FROM (" + baseSql.toString()+sbSql.toString()+ ")a "+ 
					" LEFT JOIN (SELECT * FROM T_PROJECT_SORT WHERE userId="+loginUserId+") b ON a.id = b.projectId " + 
					" ORDER BY b.sort DESC " + pageSql.toString();
					
			log.info("totleSql==>"+totleSql);
			log.info("lastSql==>"+lastSql);
			Long totle = jdbcTpl.queryForObject(totleSql, Long.class);
//		   	Long totlePages = (totle-1)/pageSize+1;
		   	
		   	this.jdbcTpl.query(lastSql, 
					new RowCallbackHandler() {
						
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							Project vo  = new Project();
							vo.setId(rs.getLong("id"));
							vo.setName(rs.getString("name"));
							vo.setDetail(rs.getString("detail"));
							vo.setCategoryId(rs.getLong("categoryId"));
							int typeStr = rs.getInt("type");
							if(typeStr==PROJECT_TYPE.TEAM.ordinal()){
								vo.setType(PROJECT_TYPE.TEAM);
							}else if(typeStr==PROJECT_TYPE.PERSONAL.ordinal()){
								vo.setType(PROJECT_TYPE.PERSONAL);
							}
							int statusStr = rs.getInt("status");
							if(statusStr==PROJECT_STATUS.FINISHED.ordinal()){
								vo.setStatus(PROJECT_STATUS.FINISHED);
							}else if(statusStr==PROJECT_STATUS.ONGOING.ordinal()){
								vo.setStatus(PROJECT_STATUS.ONGOING);
							}
							int bizLicenseStr = rs.getInt("bizLicense");
							if(bizLicenseStr ==PROJECT_BIZ_LICENSE.AUTHORIZED.ordinal()){
								vo.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
							}else if(bizLicenseStr ==PROJECT_BIZ_LICENSE.NOT_AUTHORIZED.ordinal()){
								vo.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
							}else if(bizLicenseStr ==PROJECT_BIZ_LICENSE.BINDING.ordinal()){
								vo.setBizLicense(PROJECT_BIZ_LICENSE.BINDING);
							}else if(bizLicenseStr ==PROJECT_BIZ_LICENSE.UNBINDING.ordinal()){
								vo.setBizLicense(PROJECT_BIZ_LICENSE.UNBINDING);
							}
							
							vo.setTeamId(rs.getLong("teamId"));
							vo.setBizCompanyId(rs.getString("bizCompanyId"));
							vo.setBizCompanyName(rs.getString("bizCompanyName"));
							
							
							vo.setCreatedAt(rs.getTimestamp("createdAt"));
							vo.setUpdatedAt(rs.getTimestamp("updatedAt"));
							vo.setEndDate(rs.getString("endDate"));
							pList.add(vo);
						}
					});
		   	
		 // 遍历记录进行扩展
			if(pList != null && pList.size() > 0) {
				for(Project p : pList) {
					// 增加分类数据
					ProjectCategory pc = Cache.getProjectCategory(p.getCategoryId());
					if(pc != null) {
						p.setCategoryName(pc.getName());
					}
					//项目列表要增加  项目进度，项目成员数量，创建者
					List<ProjectMember> listMember = projectMemberDao.findByProjectIdAndDel(p.getId(), DELTYPE.NORMAL);
					p.setMemberSum(listMember.size());
					List<Long> projId = new ArrayList<Long>();
					projId.add(p.getId());
					List<User> creatorUser = userDao.findCreatorForProjects(projId);
					if(null!=creatorUser && creatorUser.size()>0){
						p.setCreator(creatorUser.get(0).getUserName());
					}
				}
			}
		   	innerMap.put("list", pList);
			innerMap.put("total", totle);
			return this.getSuccessModel(innerMap);
			/*if(owner){//是团队创建者或者团队管理员
				Page<Project> listProj = this.projectService.findProjList(teamId,pageable);
				Team team = teamService.getOne(teamId);
				if(null!=team){
					innerMap.put("team", team);
				}
				innerMap.put("list", listProj.getContent());
				innerMap.put("total", listProj.getTotalElements());
				Map<String, Integer> listPermission = this.teamService.findPermissionForTeam(teamId, loginUserId);
				listPermission.put("create_team_prj", 1);//如果是团队创建者/管理员,则返回可以创建团队项目的权限
				innerMap.put("permissions", listPermission);//返回当前人在该团队下面的权限
				return this.getSuccessModel(innerMap);
			}else{
				List<PROJECT_MEMBER_TYPE> types = new ArrayList<>();
				types.add(PROJECT_MEMBER_TYPE.CREATOR);
				types.add(PROJECT_MEMBER_TYPE.PARTICIPATOR);
				Page<Project> listProjPage = this.projectService.findProjList(teamId,loginUserId,types,pageable);
				List<Project> listProj = listProjPage.getContent();
				Team team = teamService.getOne(teamId);
				if(null!=team){
					innerMap.put("team", team);
				}
				innerMap.put("list", listProj);
				innerMap.put("total", listProjPage.getTotalElements());
				Map<String, Integer> listPermission = this.teamService.findPermissionForTeam(teamId, loginUserId);
				innerMap.put("permissions", listPermission);//返回当前人在该团队下面的权限
				return this.getSuccessModel(innerMap);
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	    * @Title: findUserInfo
	    * @Description: 查看团队中某个成员信息
	    * @param @param teamId
	    * @param @param userId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月15日 下午8:21:19
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/user/{teamId}/{userId}",method=RequestMethod.GET)
	public ModelAndView findUserInfo(@PathVariable(value="teamId") Long teamId,@PathVariable(value="userId") Long userId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try {
			log.info("find detail for user belong team,with[teamId="+teamId+",userId="+userId+"]");
			//查询人loginUserId是不是该团队teamId成员
			Long groupId = this.teamMemberService.findGroupIdByTeamIdAndUserId(teamId, loginUserId);
			if(null==groupId){
				return this.getFailedModel("userId: "+loginUserId+",have no permission for team with Id: "+teamId);
			}
			TeamMember tm = this.teamMemberService.findMemberByTeamIdAndUserId(teamId, userId);
//			Map<String, Object> map = new HashMap<String, Object>();
			
//			map.put("joinTime", tm.getJoinTime());//用户同意的时候需要修改member表中的updateAt时间,这个就是加入时间
			
			User user= this.userService.findUserById(userId);
			user.setTeamMemberId(tm.getId());
			TeamAuth ta = this.teamService.findTeamAuthByMemberId(tm.getId());
			user.setTeamAuthId(ta.getId());
			
			
			List<TeamGroup> listGroup = this.teamGroupService.findAllByTeamId(teamId);//查询团队下所有分组
			TeamGroup group = new TeamGroup();
			group.setId(-1l);
			group.setName("无分组");
			group.setTeamId(teamId);
			listGroup.add(group);
			long grouId=this.teamMemberService.findGroupIdByTeamIdAndUserId(teamId, userId);//返回该人在某个团队下的组ID
			for(TeamGroup t:listGroup){
				if(t.getId().intValue()==grouId){
					t.setSelected(true);//设置选中该分组
				}
			}
//			map.put("userInfo", user);
//			map.put("groupInfo", listGroup);
//			map.put("role",Cache.getRole(ta.getRoleId()).getCnName());
			
			tm.setUserInfo(user);
			tm.setGroupInfo(listGroup);
			tm.setRole(Cache.getRole(ta.getRoleId()).getCnName());
			
			Map<String, Integer> listPermission = this.teamService.findPermissionForTeam(teamId, loginUserId);
			//根据当前人和被操作人需要过滤一下角色的权限信息
			TeamMember tmemberCurrent = this.teamMemberService.findMemberByTeamIdAndUserId(teamId, loginUserId);
			TeamAuth operateAuth = this.teamAuthService.findByMemberIdAndDel(tmemberCurrent.getId(), DELTYPE.NORMAL);//操作人的权限
			TeamAuth objectAuth = this.teamAuthService.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);//目标人的权限
			if(null!=listPermission && listPermission.size()>0){
				if(tm.getType()==TEAMREALTIONSHIP.CREATE){//如果被操作人是团队创建者,则不允许修改
					listPermission.remove("team_remove_member");//删除成员
					listPermission.remove("team_change_role");//改变角色
				}
				if(tm.getType().ordinal()<tmemberCurrent.getType().ordinal()){//如果被操作人的type比当前人的type小(type 从小到大分别是 CREATE,ACTOR ,ASK;)
					listPermission.remove("team_remove_member");//删除成员
					listPermission.remove("team_group_allocate");//分配小组
					listPermission.remove("team_change_role");//改变角色
				}else if(tm.getType().ordinal()==tmemberCurrent.getType().ordinal()){//被操作人和操作人都不是创建者,需要比较权限
					if(!tm.getUserId().equals(tmemberCurrent.getUserId())){//不是同一个人(不是自己查看自己的信息)
						//下面判断,如果被操作人是管理员,但是操作人不是创建者,管理员操作的也不是他自己的话，则没有删除成员,分配小组,改变角色的权限.
						if(Cache.getRole((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR)).getId().longValue()==objectAuth.getRoleId()
								&& Cache.getRole((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR)).getId().longValue()!=operateAuth.getRoleId()){
							listPermission.remove("team_remove_member");//删除成员
							listPermission.remove("team_group_allocate");//分配小组
							listPermission.remove("team_change_role");//改变角色
						}
					}
				}
			}
//			map.put("permissions", listPermission);
			tm.setPermissions(listPermission);
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	    * @Description:我创建/管理的团队列表(创建项目时候要用) 
	    * @param @param id
	    * @param @param userId
	    * @param @param loginUserId
	    * @param @return 
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月19日 下午2:32:46
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/mgrcrt",method=RequestMethod.GET)
	public ModelAndView findMgrOrCreatTeamList(@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try {
			log.info("my create or manager team! loginUserId="+loginUserId);
			List<Team> list = this.teamService.findMgrCrtList(loginUserId);
			return this.getSuccessModel(list);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	     * @Description:针对团队项目,在项目下邀请人员时候,需要邀请一个团队下面,没有已经被邀请的用户列表信息 
	     * @param @param teamId  团队ID
	     * @param @param projectId	项目ID
	     * @param @param loginUserId 当前登录人ID
	     * @param  userIds 已经选中的用户id,中间用逗号(,)分割
	     * @param  search 用于搜索 用户名/账号匹配
	     * @param @return 
	     * @return Map<String,Object>    返回类型
		 * @user jingjian.wu
		 * @date 2015年8月20日 下午7:54:37
	     * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/askUserList/{teamId}/{projectId}",method=RequestMethod.GET)
	public ModelAndView findAskUserList(
			@PathVariable(value="teamId") Long teamId,
			@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String userIds,String search){
		
		try {
			long start = System.currentTimeMillis();
			log.info("come into ask user for project which belong team:teamId="+teamId+",projectId="+projectId);
			List<Long> existsUserIds = new ArrayList<Long>();
			if(StringUtils.isNotBlank(userIds)){
				String []ids = userIds.split(",");
				for(String id:ids){
					if(StringUtils.isNotBlank(id)){
						existsUserIds.add(Long.parseLong(id));
					}
				}
			}
			Map<String, Object> map = this.teamService.findUserList(teamId, projectId, existsUserIds, search);
			long end = System.currentTimeMillis();
			log.info("search user from userTable use time--->"+(end-start)+" ms");
			return this.getSuccessModel(map);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * @throws Exception 
	 * 转让团队
	 * @param teamId
	 * @param targetUserId
	 * @param loginUserId
	 * @return ModelAndView
	 * @user jingjian.wu
	 * @date 2015年8月26日 下午6:10:40
	 * @throws
	 */
	@RequestMapping(value="/transfer/{teamId}",method=RequestMethod.PUT)
	public ModelAndView transferTeam(
			@PathVariable(value="teamId") Long teamId,
			@RequestParam(required=true) Long targetUserId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=false) String invokeType) throws Exception{
		
		try {
			log.info("transfer team : teamId :"+teamId+",targetUserId:"+targetUserId);
			Team tm = this.teamService.getOne(teamId);
//			if(tm.getType().equals(TEAMTYPE.ENTERPRISE)){
//				return this.getFailedModel("企业已经授权的团队不允许转让!");
//			}
//			if(tm.getEnterpriseId()!=null){
//				return this.getFailedModel("企业绑定申请中的团队不允许转让!");
//			}
			TeamMember teamMem = teamMemberService.findMemberByTeamIdAndMemberType(teamId, TEAMREALTIONSHIP.CREATE);
			if(teamMem==null || teamMem.getUserId().longValue()!=loginUserId){//非团队创建者不允许转移团队
				return this.getFailedModel("对不起,您没有权限转移此团队.");
			}
			this.teamService.updateTransferTeam(teamId, loginUserId, targetUserId,invokeType);
			
			User user = this.userService.findUserById(loginUserId);
			//增加通知
			this.noticeService.addNotice(loginUserId, new Long[]{targetUserId}, NOTICE_MODULE_TYPE.TEAM_TRANSFER, new Object[]{user,tm});
			//发送邮件
			this.baseService.sendEmail(loginUserId, new Long[]{targetUserId}, NOTICE_MODULE_TYPE.TEAM_TRANSFER, new Object[]{user,tm});
			return this.getAffectModel();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return this.getFailedModel("转让失败！");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return this.getFailedModel("转让失败！");
		} catch (IOException e) {
			e.printStackTrace();
			return this.getFailedModel("转让失败！");
		}
	}
	/**
	 * 退出团队
	 * @user jingjian.wu
	 * @date 2015年9月16日 下午5:03:26
	 */
	@RequestMapping(value="/exit/{teamId}",method=RequestMethod.PUT)
	public ModelAndView exitTeam(
			@PathVariable(value="teamId") Long teamId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId){
		
		try {
			log.info("exit team : teamId :"+teamId+",loginUserId:"+loginUserId);
			TeamMember tmember = this.teamMemberService.findMemberByTeamIdAndUserId(teamId, loginUserId);
			if(tmember.getType().equals(TEAMREALTIONSHIP.CREATE)){
				return this.getFailedModel("团队创建者不能退出团队！");
			}
			TeamMember teammember = this.teamMemberService.findMemberByTeamIdAndMemberType(teamId, TEAMREALTIONSHIP.CREATE);
			this.teamMemberService.delExitTeam(teamId, loginUserId,getProductTokenByTeamId(teamId));
			
			//添加动态
			Team tm = this.teamService.getOne(teamId);
			this.dynamicService.addTeamDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TEAM_EXIT, teamId, new Object[]{tm.getName()});
			
			User user = this.userService.findUserById(loginUserId);
			this.noticeService.addNotice(loginUserId, new Long[]{teammember.getUserId()}, NOTICE_MODULE_TYPE.TEAM_QUIT, new Object[]{user,tm});
			return this.getAffectModel();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return this.getFailedModel("退出团队失败！");
		}
	}
	
	/**
	 * 项目列表中,所属团队筛选,我能查看的团队
	 * 包括我只是参与了某个团队下的项目,但是我并不是该团队的成员,也能看到该团队.
	 */
	@RequestMapping(value="/select")
	public Map<String,Object> findTeamByReleatedSelect(@RequestHeader(value="loginUserId",required=true) Long loginUserId,String teamName){
		try{
			List<Team> teamList = this.teamService.findReleateTeamList(loginUserId,teamName);
			
			return this.getSuccessMap(teamList);
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询失败！");
		}
	}
	
	
	/*@ResponseBody
	@RequestMapping(value="/active",method=RequestMethod.PUT)
	public Map<String, Object> activeUser(@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		this.teamService.updateMemberType(Long.parseLong(loginUserId));
		return map;
		
	}*/
	
	/**
	 * 线上版本,EMM获取需要授权和已经授权的团队列表
	 * @user jingjian.wu
	 * @date 2015年10月19日 下午4:58:13
	 */
	/*@ResponseBody
	@RequestMapping(value="/authList")
	public Map<String, Object> findTeamAuthList(HttpServletRequest request,@RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize,
			String sortType,String search,@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		if (pageNo == null){
			pageNo = 1;
		}
		if (pageSize == null){
			pageSize = 10;
		}
		Page<Team> list =   this.teamService.findTeamAuthList(pageNo, pageSize, sortType,search);
		map.put("list", list);
		map.put("hasNext", list.hasNext());
		map.put("hasPrevious", list.hasPrevious());
//		map.put("search",search);
		return map;
	}*/
	
	/**
	 * EMM调用  获取某个团队下的人员及团队下项目中的人员
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user jingjian.wu
	 * @date 2015年10月23日 上午10:14:09
	 */
	/*@ResponseBody
	@RequestMapping(value="/belongUsers/{teamId}",method=RequestMethod.GET)
	public Map<String, Object> belongUsers(@PathVariable(value="teamId") Long teamId){
		log.info("");
		List<User> list = this.teamService.findAllUserBelongTeam(teamId);
		return this.getSuccessMap(list);
		
	}*/
	public static void main(String[] args) throws ClientProtocolException, IOException {
		/*List<String> list = new ArrayList<String>();
		list.add("aaa");
		list.add("bbb");
		list.add("ccc");
		list.add("ddd");
		list.add("eee");
		list.add("fff");
		list.add("ggg");
		List<String> result = list.subList(2*3, (2+1)*3);
		for(String str:result){
			log.info(str);
		}*/
		
		/*List<NameValuePair> parameters;
		JSONObject jsonObject;
		try {
			parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("shortName", "gutt" ) );
			parameters.add( new BasicNameValuePair("fullName", "谷婷婷") );
			String result = HttpUtil.httpPost("http://192.168.4.28:8086/omm/enterprise/validName", parameters);
			log.info(result);
			jsonObject = JSONObject.fromObject(result);
			log.info(jsonObject.getString("status"));
			log.info(jsonObject.getString("info"));
		} catch (ClientProtocolException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}*/
		
		//===============================
		
		/*try {
			parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("tenantId", "yan10" ) );
			parameters.add( new BasicNameValuePair("entFullName", "yan10" ) );
			parameters.add( new BasicNameValuePair("teamId", "222" ) );
				
			resultStr = HttpUtil.httpPost("http://192.168.4.29:8080/emm/teamAuth/deleteAuthGroup", parameters);
			log.info(resultStr);
			jsonObject = JSONObject.fromObject(resultStr);
			if(!jsonObject.get("returnCode").equals("200")){
				throw new RuntimeException(jsonObject.getString("returnMessage"));
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
		
		
		
		//======================
		/*
		try {
			parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("creator", "jingjian.wu@3g2win.com") );
			parameters.add( new BasicNameValuePair("teamName", "eeee" ) );
			parameters.add( new BasicNameValuePair("teamDesc", "eeee" ) );
			parameters.add( new BasicNameValuePair("tenantId", "gutt" ) );
			parameters.add( new BasicNameValuePair("entFullName", "谷婷" ) );
			parameters.add( new BasicNameValuePair("teamId", "222" ) );
				
			resultStr = HttpUtil.httpPost("http://192.168.4.29:8080/emm/teamAuth/createAuthGroup", parameters);
			log.info(resultStr);
			jsonObject = JSONObject.fromObject(resultStr);
			if(!jsonObject.get("returnCode").equals("200")){
				throw new RuntimeException("调用EMM申请授权失败");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		
		/*Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("teamDevGroupId", "aaaa" );//团队ID
		parameters.put( "uniqueField", "aaaa");//用户唯一标示
		parameters.put("name", "aaaa" ) ;//添加人name
		parameters.put("mobileNo","aaaa" );
		parameters.put("email", "aaaa" ) ;
		parameters.put("creatorName", "aaaa" );//创建者
		String resultStr = HttpUtil.httpsPost("http://192.168.1.224:8080/mum/personnel/savePersonnelXieTong", parameters,"UTF-8");
		log.info("emm3 add user for group -->"+resultStr);*/
		
		String resultStr = HttpUtil.httpGet("http://localhost:8080/project/123");
		System.out.println("emm3 add user for group -->"+resultStr);
		
	}
	
	/**
	 * 查询团队列表的搜索框(团队名称)
	 * @param teamName支持模糊查询
	 * @user jingjian.wu
	 * @date 2016年2月29日 下午3:30:14
	 */
	@RequestMapping(value="/teamSearchConditionForName")
	public Map<String,Object> teamListSearchLikeForName(@RequestHeader(value="loginUserId",required=true) Long loginUserId,String teamName,
			String begin,String end){
		try{
			List<Team> teamList = this.teamService.findTeamList(loginUserId, teamName, begin, end);
			return this.getSuccessMap(teamList);
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询失败！");
		}
	}
	
	/**
	 * 查询和我相关的团队的创建者/参与者
	 * @param keyWords,模糊搜索,账号,真实姓名,邮箱,用户名首拼,用户名全拼
	 * @pram ship  查询团队创建者用CREATE,查询参与者用ACTOR
	 * @user jingjian.wu
	 * @date 2016年2月29日 下午3:33:39
	 */
	@RequestMapping(value="/teamCreatorOrActorForRelationShip")
	public Map<String,Object> teamCreatorOrActorForRelationShip(@RequestHeader(value="loginUserId",required=true) Long loginUserId,String keyWords,
			TEAMREALTIONSHIP ship){
		try{
			List<User> userList = this.teamService.findTeamCreatorOrActorList(loginUserId, keyWords, ship);
			return this.getSuccessMap(userList);
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("查询失败！");
		}
	}
	
	/**
     * @Description:针对团队项目,在项目下邀请人员时候,需要在分组中显示邀请一个团队下面没有被邀请的用户总数量
     * @param @param teamId  团队ID
     * @param @param projectId	项目ID
     * @param @param loginUserId 当前登录人ID
     * @param  userIds 已经选中的用户id,中间用逗号(,)分割
     * @param @return 
     * @return Map<String,Object>    返回类型
	 * @user tingwei.yuan
	 * @date 2016年3月25日 下午6:52:37
     * @throws
     */
	@ResponseBody
	@RequestMapping(value="/findAskUserCount/{teamId}/{projectId}",method=RequestMethod.GET)
	public ModelAndView findAskUserCount(
			@PathVariable(value="teamId") Long teamId,
			@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String userIds,String search){
		
		try {
			long start = System.currentTimeMillis();
			log.info("come into ask user for project which belong team:teamId="+teamId+",projectId="+projectId);
			List<Long> existsUserIds = getUserIdList(userIds);
			Map<String, Object> map = this.teamService.findUserCount(teamId, projectId, existsUserIds,search);
			long end = System.currentTimeMillis();
			log.info("search user from userTable use time--->"+(end-start)+" ms");
			return this.getSuccessModel(map);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
     * @Description:针对团队项目,在项目分组下邀请人员的时候,需要邀请一个团队下面没有被邀请的用户列表信息
     * @param @param teamId  团队ID
     * @param @param projectId	项目ID
     * @param @param groupId	项目ID
     * @param @param loginUserId 当前登录人ID
     * @param  userIds 已经选中的用户id,中间用逗号(,)分割
     * @param @return 
     * @return Map<String,Object>    返回类型
	 * @user tingwei.yuan
	 * @date 2016年3月25日 下午6:52:37
     * @throws
     */
	@ResponseBody
	@RequestMapping(value="/findAskUserGroupList",method=RequestMethod.POST)
	public ModelAndView findAskUserGroupList(
			Long teamId,
			Long projectId,
			String userIds, String groupName,String search,
			@RequestHeader(value="loginUserId",required=true) long loginUserId){
		
		try {
			long start = System.currentTimeMillis();
			log.info("come into ask user for project which belong team:teamId="+teamId+",projectId="+projectId + ",groupName=" + groupName);
			List<Long> existsUserIds = getUserIdList(userIds);
			Map<String, Object> map = null;
			Long groupId = -1l;
			if ("无分组".equals(groupName)) {
				groupId = -1l;
			} else {
				List<TeamGroup> list = this.teamGroupService.findByNameAndDel(groupName, teamId, DELTYPE.NORMAL);
				if (list != null && list.size() > 0) {
					TeamGroup tg = list.get(0);
					groupId = tg.getId();
				} 
			}
			map = this.teamService.findUserGroupList(teamId, groupId,projectId, existsUserIds,search);
			long end = System.currentTimeMillis();
			log.info("search user from userTable use time--->"+(end-start)+" ms");
			return this.getSuccessModel(map);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * 组装前端页面传递过来的用户id,以集合的形式返回
	 * @param userIds
	 * @return
	 * @user tingwei.yuan
	 * @date 2016年3月25日 下午6:52:37
	 */
	private List<Long> getUserIdList(String userIds) {
		List<Long> existsUserIds = new ArrayList<Long>();
		if (StringUtils.isNotBlank(userIds)) {
			String[] ids = userIds.split(",");
			for (String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					existsUserIds.add(Long.parseLong(id));
				}
			}
		}
		return existsUserIds;
	}
	/**
	 * 判断该团队下面是否有项目在申请绑定中,申请解绑中,已绑定
	 * @param teamId
	 * @param loginUserId
	 * @author haijun.cheng
	 * @data 2016年7月11 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/isHaveProject",method=RequestMethod.GET)
	public ModelAndView isHaveProject(Long teamId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try {
			log.info("Is aready have project in this team. teamId="+teamId);
			String bl=this.teamService.findIsHaveProject(teamId);
			return this.getSuccessModel(bl);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 绑定申请通过后页面申请解除绑定
	 * @user haijun.cheng
	 * @date 2016年7月11日
	 */
	@ResponseBody
	@RequestMapping(value="/deleteEnterprise/{teamId}",method=RequestMethod.PUT)
	public ModelAndView deleteEnterprise(@PathVariable(value="teamId") Long teamId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("unbind(deleteEnterprise) team Enterprise : "+",teamId:"+teamId);
			
			List list = this.teamService.updateDeleteEnterprise(teamId);
			if(null==list){
				return this.getFailedModel("此团队已经解除授权,无法再次申请解除授权，请刷新页面");
			}
			Team tm = (Team) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_ASK_UNBIND, teamId, new Object[]{tm,enterpriseName});
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	

	/**
	 * 初始化团队拼音字段值
	 */
	@ResponseBody
	@RequestMapping(value="/addPinYin",method=RequestMethod.GET)
	public Map<String,Object> addPinYin(){
		try{
			int affected=this.teamService.addPinYin();
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);
			return this.getSuccessMap(affectedMap);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 绑定申请通过后页面取消解绑
	 * @user haijun.cheng
	 * @date 2016年7月12日
	 */
	@ResponseBody
	@RequestMapping(value="/cancelDeleteEnterprise/{teamId}",method=RequestMethod.PUT)
	public ModelAndView cancelDeleteEnterprise(@PathVariable(value="teamId") Long teamId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("cancelDeleteEnterprise team Enterprise : "+",teamId:"+teamId);
			
			if(this.teamService.findType(teamId).equals(TEAMTYPE.NORMAL)){
				return this.getFailedModel("该团队已经解除授权，无法恢复授权，请刷新页面");
			}
			if(this.teamService.findType(teamId).equals(TEAMTYPE.ENTERPRISE)){
				return this.getFailedModel("该团队解除授权已经被拒绝，不需要恢复授权，请刷新页面");
			}
			
			List list = this.teamService.deleteEnterpriseCancel(teamId);
			if(null==list){
				return this.getFailedModel("此团队为企业团队,无法取消.");
			}
			Team tm = (Team) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_CANCEL_UNBIND, teamId, new Object[]{tm,enterpriseName});
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * EMM调用，同意解绑企业
	 * @user haijun.cheng
	 * @date 2016年7月13日
	 */
	@ResponseBody
	@RequestMapping(value="/agreeDeleteEnterprise/{teamId}",method=RequestMethod.POST)
	public ModelAndView agreeDeleteEnterprise(@PathVariable(value="teamId") String uuid,
			@RequestHeader(value="currentLoginAccount",required=true) String loginUserId){
		try {
			log.info("agreeDeleteEnterprise team Enterprise : "+",teamId:"+uuid);
			Team tmp = teamService.getByUuid(uuid);
			List list = this.teamService.deleteEnterpriseAgree(tmp.getId());
			if(null==list){
				return this.getFailedModel("此团队未要求解除绑定.");
			}
			Team tm = (Team) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			List<TeamMember> members = this.teamMemberService.findByTeamIdAndDel(tmp.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(TeamMember member : members){
				if(member.getType().compareTo(TEAMREALTIONSHIP.CREATE)==0){
					ids.add(member.getUserId());
				}else{
					TeamAuth ta = this.teamAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					Role role = Cache.getRole(ta.getRoleId());
					if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
						ids.add(member.getUserId());
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TEAM_AGREE_UNBIND_ENTERPRISE, new Object[]{tm,enterpriseName});
//			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_CANCEL_ENTERPRISE, teamId, new Object[]{tm,enterpriseName});
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * EMM调用，不同意解绑企业
	 * @user haijun.cheng
	 * @date 2016年7月13日
	 */
	@ResponseBody
	@RequestMapping(value="/unagreeDeleteEnterprise/{teamId}",method=RequestMethod.POST)
	public ModelAndView unagreeDeleteEnterprise(@PathVariable(value="teamId") String uuid,
			@RequestHeader(value="currentLoginAccount",required=true) String loginUserId){
		try {
			log.info("unagreeDeleteEnterprise team Enterprise : "+",teamId:"+uuid);
			Team tmp = teamService.getByUuid(uuid);
			List list = this.teamService.deleteEnterpriseUnagree(tmp.getId());
			if(null==list){
				return this.getFailedModel("此团队未要求解除绑定.");
			}
			Team tm = (Team) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			List<TeamMember> members = this.teamMemberService.findByTeamIdAndDel(tmp.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(TeamMember member : members){
				if(member.getType().compareTo(TEAMREALTIONSHIP.CREATE)==0){
					ids.add(member.getUserId());
				}else{
					TeamAuth ta = this.teamAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					Role role = Cache.getRole(ta.getRoleId());
					if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
						ids.add(member.getUserId());
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TEAM_UNAGREE_UNBIND_ENTERPRISE, new Object[]{tm,enterpriseName});
//			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_CANCEL_ENTERPRISE, teamId, new Object[]{tm,enterpriseName});
			return this.getSuccessModel(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * EMM调用获取要绑定的团队的成员信息
	 * @user haijun.cheng
	 * @date 2016-09-20
	 */
	@ResponseBody
	@RequestMapping(value="/authronized/getUserList/{teamId}",method=RequestMethod.POST)
	public ModelAndView getUserList(@PathVariable(value="teamId") String uuid, 
			HttpServletRequest request){
		try {
			Map<String,Object> map = new HashMap<String, Object>();
			Team tm = teamService.getByUuid(uuid);
			List<User> list = this.teamService.findAllUserBelongTeam(tm.getId());
			map.put("users", list);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
}
