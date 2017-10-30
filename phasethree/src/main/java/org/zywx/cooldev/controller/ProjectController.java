package org.zywx.cooldev.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectCategory;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.trans.Trans;
import org.zywx.cooldev.service.TransService;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.UserListWrapUtil;
import org.zywx.cooldev.util.mail.base.SendMailTools;
import org.zywx.cooldev.vo.Match4Project;

/**
 * 项目相关处理控制器
 * @author yang.li
 * @date 2015-08-10
 *
 */
@Controller
@RequestMapping(value = "/project")
public class ProjectController extends BaseController {
	@Value("${emmValidHost}")
	private String emmValidHost;
	
	
	@Value("${xietongHost}")
	private String xietongHost;
	
	
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
	private SendMailTools sendMailTool;
	
	@Autowired
	private TransService transService;
	
	
	
	/**
	 * 获取项目列表
	 * @param request
	 * @param creator (项目创建者账号)
	 * @param begin 创建开始时间
	 * @param end 创建结束时间
	 * @param pfdate_begin 计划完成时间(区间的开始时间)  plan finished date
	 * @param pfdate_end 计划完成时间(区间的结束时间)
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> getProjectList(Match4Project match, HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=false,defaultValue="")String projName,String creator,String actor,String begin,String end,String teamName,
			String pfstime,String pfetime,Integer parentId) {
		
			try {
				log.info("Method getProjectList is called.");

				String sPageNo      = request.getParameter("pageNo");
				String sPageSize    = request.getParameter("pageSize");

				int pageNo       = 1;
				int pageSize     = 20;
				
				try {
					if(sPageNo != null) {
//						pageNo		= Integer.parseInt(sPageNo)-1;
						pageNo		= Integer.parseInt(sPageNo);
					}
					if(sPageSize != null) {
						pageSize	= Integer.parseInt(sPageSize);
					}
					
				} catch (NumberFormatException nfe) {				
					return this.getFailedMap( nfe.getMessage() );
				}

				Map<String, Object> retMap = this.projectService.getProjectList(pageNo,pageSize, match, loginUserId, 
						projName, creator,actor, begin, end,teamName,pfstime,pfetime,parentId);
				
				return this.getSuccessMap(retMap);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}
	
	/**
	 * 获取项目详情
	 * @param projectId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{projectId}", method=RequestMethod.GET)
	public Map<String, Object> getProject(@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		log.info(String.format("get project detail by projectId:[%d],loginUserId[%d]", projectId,loginUserId));
		try {
			Map<String, Object> map = this.projectService.getProject(projectId, loginUserId);
			if(map != null) {
				return this.getSuccessMap(map);
			} else {
				return this.getFailedMap("not found project with id=" + projectId);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 创建新项目<br>
	 * 1. 添加新项目
	 * 2. 添加默认成员和权限
	 * @param p
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> createProject(Project p, HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		log.info("post project called");
		log.info(p);
		try {
			if(p.getName()!=null&&p.getName().length()>1000){
				this.getFailedMap("项目名称不能超过1000个字符");
			}
			if(p.getDetail()!=null&&p.getDetail().length()>1000){
				this.getFailedMap("项目描述不能超过1000个字符");
			}
			if(serviceFlag.equals("enterprise")||serviceFlag.equals("enterpriseEmm3")){//企业版
				if(p.getTeamId()==-1){
					return this.getFailedMap("请选择对应的团队");
				}
				p.setBizCompanyId(enterpriseId);
				p.setBizCompanyName(enterpriseName);
				p.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
				p.setType(PROJECT_TYPE.TEAM);//企业版不允许创建个人项目
			}
			if(serviceFlag.equals("online")&&this.projectService.isTeamBind(p.getTeamId())){//大众版且所属团队已绑定
				Team team=teamService.findOne(p.getTeamId());
				p.setBizCompanyId(team.getEnterpriseId());
				p.setBizCompanyName(team.getEnterpriseName());
				if(team.getType().equals(TEAMTYPE.ENTERPRISE)){
					p.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
				}
				if(team.getType().equals(TEAMTYPE.UNBINDING)){
					p.setBizLicense(PROJECT_BIZ_LICENSE.UNBINDING);
				}
				p.setType(PROJECT_TYPE.TEAM);
			}
			this.projectService.addProject(p);
			log.info(p);
			ProjectMember member = new ProjectMember();
			member.setUserId(loginUserId);
			member.setProjectId(p.getId());
			member.setType(PROJECT_MEMBER_TYPE.CREATOR);
			User loginUser = this.userService.findUserById(loginUserId);
			this.projectService.saveProjectMember(member,loginUser);
			log.info(member);
			ProjectAuth auth = new ProjectAuth();
			auth.setMemberId(member.getId());
			auth.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR).getId());
			this.projectService.saveProjectAuth(auth);
			if(null == p.getType() || p.getType().compareTo(PROJECT_TYPE.TEAM)!=0){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_ADD, p.getId(), p.getName());
			}else{
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TEAM_CREATE_PRJ, p.getId(), p.getName());
			}
			return this.getSuccessMap(p);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}


	/**
	 * 云平台调用创建名字叫“正益工作定制项目”的项目
	 * @param p
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cloud",method=RequestMethod.POST)
	public Map<String, Object> createProjectForCloudPlat(Project p, HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		try {
			if(serviceFlag.equals("enterprise")){//企业版
				return this.getFailedMap("操作不允许");
			}
			Map<String,Object> map = this.projectService.addProjectForCloud(p, loginUserId);
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 云平台调用,为某个项目创建两个应用
	 * @param projectId
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cloud/app",method=RequestMethod.POST)
	public Map<String, Object> addAppForCloudPlat(Long projectId, HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		try {
			if(serviceFlag.equals("enterprise")){//企业版
				return this.getFailedMap("操作不允许");
			}
			List<Long> appIds = this.projectService.addTwoAppForCloud(projectId, loginUserId);
			return this.getSuccessMap(appIds);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 更新项目
	 * @param projectId
	 * @param newProject
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{projectId}", method=RequestMethod.PUT)
	public Map<String, Object> editProject(
			Project newProject,
			@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			if(newProject.getName()!=null&&newProject.getName().length()>1000){
				this.getFailedMap("项目名称不能超过1000个字符");
			}
			if(newProject.getDetail()!=null&&newProject.getDetail().length()>1000){
				this.getFailedMap("项目描述不能超过1000个字符");
			}
			Project project=this.projectService.findOne(projectId);
			project.setName(newProject.getName());
			project.setDetail(newProject.getDetail());
			project.setCategoryId(newProject.getCategoryId());
			int affected = this.projectService.editProject(project, loginUserId);

			Map<String, Integer> map = new HashMap<>();
			map.put("affected", affected);
			return this.getSuccessMap(affected);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("创建项目失败:"+e.getMessage());
		}

	}
	
	/**
	 * 删除项目
	 * @param projectId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{projectId}", method=RequestMethod.DELETE)
	public Map<String, Object> deleteProject(@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		log.info("delete project --> projectId:"+projectId+",loginUserId:"+loginUserId);
		try {
			Project p  = projectService.getProject(projectId);
			if(p == null) {
				return this.getFailedMap("项目不存在");
			}
			
			if(p.getTeamId()!=-1){//团队项目
				TeamMember teamMem = teamMemberService.findMemberByTeamIdAndMemberType(p.getTeamId(), TEAMREALTIONSHIP.CREATE);
				if(teamMem==null || teamMem.getUserId().longValue()!=loginUserId){
					return this.getFailedMap("对不起,您没有权限删除此项目.");
				}
			}
			
			
			this.projectService.removeProject(projectId,getProductTokenByProjectId(projectId));
			log.info("delete project success--> projectId:"+projectId+",loginUserId:"+loginUserId);
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_REMOVE, p.getId(), p.getName());
			
			//添加通知
			User user = this.userService.findUserById(loginUserId);
			List<Long> members = this.projectService.getProjectMemberIdList(projectId, loginUserId);
			Set<Long> ids=new HashSet<Long>();         
		    ids.addAll(members);
			this.noticeService.addNotice(loginUserId,ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROJECT_DELETE,new Object[]{user,p});
			
			log.info("delete project ,add notice finished--> projectId:"+projectId+",loginUserId:"+loginUserId);
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("删除失败");
		}

	}
	
	/**
	 * 获取项目成员列表
	 * @param loginUserId
	 * exceptUserIds 除了指定的人之外
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/member/{projectId}", method=RequestMethod.GET)
	public Map<String, Object> getProjectMemberList(
			HttpServletRequest request,
			@PathVariable(value="projectId") long projectId,
			@RequestParam(value="keyWords",defaultValue="") String keyWords,
			@RequestParam(value="typeList", required=false) List<PROJECT_MEMBER_TYPE> typeList,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="exceptUserIds", required=false) List<Long> exceptUserIds,
			@RequestParam(value="type",required=false) String type) {

		try {
			
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
				return this.getFailedMap(nfe.getMessage());
			}

			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
			
			log.info("keyWords:["+keyWords+"],projectId:["+projectId+"],typeList:["+typeList+"],loginUserId:["+loginUserId+"]");
			HashMap<String, Object> pms =  this.projectService.getProjectMemberList(projectId, typeList, keyWords,exceptUserIds,pageable,loginUserId,type) ;
			Map<String, Object> map = new HashMap<>();
			map.put("list", pms.get("list"));
			List<Permission> permissions = this.projectService.getPermissionList(loginUserId, projectId);
			Map<String, Integer> permissionMap = new HashMap<>();
			if(permissions != null && permissions.size() > 0) {
				for(Permission permission : permissions) {
					permissionMap.put(permission.getEnName(), 1);
				}
			}
			Project project = this.projectService.getProject(projectId);
			map.put("permissions", permissionMap);
			map.put("project", project);
			map.put("total", pms.get("total"));
			map.put("invitedTotal", pms.get("invitedTotal"));
			map.put("invitingTotal", pms.get("invitingTotal"));
			map.put("creatorOrAdministrator", pms.get("creatorOrAdministrator"));
			return this.getSuccessMap(map);
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/member/single/{memberId}", method=RequestMethod.GET)
	public Map<String, Object> getSingleMember(
			@PathVariable(value="memberId") long memberId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			
			ProjectMember member = this.projectService.getProjectMember(memberId, loginUserId);
			return this.getSuccessMap(member);
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 添加(邀请)项目成员
	 * @param member
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/member", method=RequestMethod.POST)
	public Map<String, Object> addProjectMember(
			@RequestParam(value="projectId") long projectId,
			@RequestParam(value="userStr") List<String> userStrList,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(required=false) String content) {
				
		try {
			log.info("project ask member--> projectId:"+projectId+",userStrList:"+userStrList+",loginUserId:"+loginUserId);
			Map<String, Object> affectedMap = this.projectService.addProjectMember(projectId,userStrList,loginUserId,content);
			return affectedMap;
		} catch (Exception e) {
			e.printStackTrace();
			if(e.getClass().equals(MessagingException.class) || e.getClass().equals(MailSendException.class)){
				return this.getFailedMap("抱歉，您的邮件发送失败，请重新邀请！");
			}else{
				return this.getFailedMap("邀请失败！");
			}
		}
	}

	/**
	 * 删除项目成员
	 * @param memberId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/member/{memberId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeProjectMember(
			@PathVariable(value="memberId") long memberId,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="transferUserId",required=false) Long transferUserId) {
				
		try {
			
			ProjectMember pm = this.projectService.findProjectMemberByMemberId(memberId);
			int affected = this.projectService.removeProjectMember(memberId,getProductTokenByProjectId(pm.getProjectId()),transferUserId);
			
			if(affected !=1){
				if(affected==-1){
					return this.getFailedMap("该成员有负责的任务，请更换任务负责人后，再移除该成员！");
				}else if (affected==-2){
					return this.getFailedMap("该成员有负责的流程，请更换流程负责人后，再移除该成员！");
				}else
					return this.getFailedMap("移除该成员失败！");
			}
			
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);

			//添加通知
			User user = this.userService.findUserById(loginUserId);
			User memberUser = this.userService.findUserById(pm.getUserId());
			Project project = this.projectService.getProject(pm.getProjectId());
			this.noticeService.addNotice(loginUserId, new Long[]{pm.getUserId()}, NOTICE_MODULE_TYPE.PROJECT_REMOVE_MEMBER, new Object[]{user,project});
			String username = memberUser.getUserName()==null?memberUser.getAccount():memberUser.getUserName();
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_REMOVE_MEMBER, pm.getProjectId(), new Object[]{username});
			return this.getSuccessMap(affectedMap);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @describe 编辑成员角色	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月28日 上午10:17:05	<br>
	 * @param memberId
	 * @param roleType
	 * @param loginUserId
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/member/role/{memberId}", method=RequestMethod.PUT)
	public Map<String, Object> editProjectMemberRole(
			@PathVariable(value="memberId") long memberId,
			@RequestParam("roleType") ROLE_TYPE roleType,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			int affected = this.projectService.editProjectMemberRole(memberId, roleType, loginUserId);
			if(affected==1){
				ProjectMember pm = this.projectService.getProjectMember(memberId, loginUserId);
				Project project = this.projectService.getProject(pm.getProjectId());
				User user = this.userService.findUserById(pm.getUserId());
				User loginUser = this.userService.findUserById(loginUserId);
				//CREATOR, ADMINISTRATOR, MANAGER, MEMBER,OBSERVER;
				if(roleType.equals(ROLE_TYPE.MEMBER)){
					
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_UPDATE_MEMBER, pm.getProjectId(), new Object[]{user});
					this.noticeService.addNotice(loginUserId, new Long[]{pm.getUserId()}, NOTICE_MODULE_TYPE.PROJECT_UPDATE_MEMBER, new Object[]{loginUser,project});
				}else if(roleType.equals(ROLE_TYPE.ADMINISTRATOR)){
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_UPDATE_LEADER, pm.getProjectId(), new Object[]{user});
					this.noticeService.addNotice(loginUserId, new Long[]{pm.getUserId()}, NOTICE_MODULE_TYPE.PROJECT_UPDATE_LEADER, new Object[]{loginUser,project});
				}
				Map<String, Integer> affectedMap = new HashMap<>();
				affectedMap.put("affected", affected);
				return this.getSuccessMap(affectedMap);
			}else{
				return this.getFailedMap("您没有权限操作");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		
	}
	
	
	
	/**(企业版从项目添加成员,从后台添加,和从团队添加)
	 * 从团队中添加人员,并且设置每个人员的权限
	 * 从项目邀请人员接口复制过来,防止以后上面的接口可能需要发送邮件.而此处从团队中添加人,应该不需要发送邮件,此处传过来用户的id
	 * @user jingjian.wu
	 * @date 2015年10月16日 下午8:28:40
	 */
	@ResponseBody
	@RequestMapping(value="/addMemberFromTeam", method=RequestMethod.POST)
	public Map<String, Object> addMemberFromTeam(
			Long projectId,
			@RequestParam(value="userStr") List<String> userStrList,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			log.info("project add member from team--> projectId:"+projectId+",userStrList:"+userStrList+",loginUserId:"+loginUserId);
			
//			Map<String, Object> map = this.projectService.addProjectMemberFromTeam(projectId,loginUserId,userStrList);
			Map<String, Object> map = this.projectService.addProjectMember(projectId,loginUserId,userStrList);
			@SuppressWarnings("unchecked")
			List<Long> memberUserIdArr = (List<Long>) map.get("memberUserIdArr");
			int membersSize =  (int) map.get("size");
			//添加通知
			//人员去重
			Set<Long> set = new HashSet<>(memberUserIdArr);
			memberUserIdArr.clear();
			Iterator<Long> it = set.iterator();
			while (it.hasNext())
			{
				memberUserIdArr.add(it.next());
			}
					
			User user = this.userService.findUserById(loginUserId);
			Project project = this.projectService.getProject(projectId);
			
			String userNames = map.get("dynamicUserNames").toString();
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_INVITE_MEMBER, projectId, new Object[]{userNames});
			
			this.noticeService.addNotice(loginUserId, memberUserIdArr.toArray(new Long[memberUserIdArr.size()]), NOTICE_MODULE_TYPE.PROJECT_ADD_MEMBER, new Object[]{user,project});
			
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", membersSize);
			return this.getSuccessMap(affectedMap);

			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 
	 *  退出项目	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年11月11日 下午2:40:09	<br>
	 * @param projectId
	 * @param loginUserId
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/quit", method=RequestMethod.PUT)
	public Map<String, Object> quit(
			@RequestParam(value="projectId") long projectId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			int affected = this.projectService.updateToQuitProject(projectId, loginUserId);
			
			if(affected !=1){
				if(affected==-1){
					return this.getFailedMap("你有负责的任务，请更换任务负责人后，再退出项目！");
				}else if (affected==-2){
					return this.getFailedMap("你有负责的流程，请更换流程负责人后，再退出项目！");
				}else
					return this.getFailedMap("退出项目失败！");
			}
			Map<String, Integer> map = new HashMap<>();
			map.put("affected", affected);
			//添加通知
			ProjectMember proM = this.projectService.getProjectCreator(projectId);
			Project pro = this.projectService.getProject(projectId);
			User user = userService.findUserById(loginUserId);
			this.noticeService.addNotice(loginUserId, new Long[]{proM.getUserId()}, NOTICE_MODULE_TYPE.PROJECT_QUIT, new Object[]{user,pro});
			
			return this.getSuccessMap(map);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("退出项目失败！");
		}
	}
	
	/**
	 * 项目企业授权绑定
	 * @param projectId
	 * @param p
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/bizBind/{projectId}", method=RequestMethod.PUT)
	public Map<String, Object> bizBind(
			@RequestParam(value="bizCompanyId") String bizCompanyId,
			@RequestParam(value="bizCompanyName") String bizCompanyName,
			@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId") long loginUserId) {
	
		try {
			Project p = new Project();
			p.setId(projectId);
			p.setBizCompanyId(bizCompanyId);
			p.setBizCompanyName(bizCompanyName);
			p.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
	
			int affected = this.projectService.editProject(p, loginUserId);
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);
			
			Project pro = this.projectService.getProject(projectId);
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_BIZ_BIND, pro.getId(), pro.getName());

			//添加通知
			ProjectMember pm = this.projectService.getProjectCreator(projectId);
			List<ProjectMember> pms = this.projectService.getProjectManager(projectId);
			
			List<Long> recievedIds = new ArrayList<>();
			if(pm!=null){
				recievedIds.add(pm.getUserId());
			}
			if(null!=pms){
				for(ProjectMember prom : pms){
					recievedIds.add(prom.getUserId());
				}
			}
			this.noticeService.addNotice(loginUserId, recievedIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROJECT_ALLOW_AUTHORIZED, new Object[]{pro,bizCompanyId});
			
			return this.getSuccessMap(affectedMap);
	
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}
	
	}

	/**
	 * 项目企业授权解绑
	 * @param projectId
	 * @param p
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/bizUnBind/{projectId}", method=RequestMethod.PUT)
	public Map<String, Object> bizUnBind(
			@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			Project p = new Project();
			p.setId(projectId);
			p.setBizCompanyId(null);
			p.setBizCompanyName(null);
			p.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);

			int affected = this.projectService.editProject(p, loginUserId);
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_BIZ_UNBIND, p.getId(), p.getName());

			return this.getSuccessMap(affectedMap);

		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 项目转让
	 * @param projectId
	 * @param p
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/transfer/{projectId}", method=RequestMethod.PUT)
	public Map<String, Object> transfer(
			@RequestParam(value="targetUserId") long targetUserId,
			@PathVariable(value="projectId") long projectId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			
			Project p = projectService.getProject(projectId);
			if(p == null) {
				return this.getFailedMap("project with id=" + projectId + " is not exist");
			}
			//绑定的团队项目不许转让
			if(p.getType().equals(PROJECT_TYPE.TEAM) ){
				if(p.getTeamId()!=-1){//团队项目
					TeamMember teamMem = teamMemberService.findMemberByTeamIdAndMemberType(p.getTeamId(), TEAMREALTIONSHIP.CREATE);
					if(teamMem==null || teamMem.getUserId().longValue()!=loginUserId){
						return this.getFailedMap("对不起,您没有权限转让该项目.");
					}
				}
			}
			
			int affected = projectService.transferProject(projectId, loginUserId, targetUserId);
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);
			
			User targetUser =this.userService.findUserById(targetUserId);
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_TRANSFER, projectId, new Object[]{p.getName(),targetUser.getUserName()});
			//添加通知
			User loginUser = this.userService.findUserById(loginUserId);
			this.noticeService.addNotice(loginUserId, new Long[]{targetUserId}, NOTICE_MODULE_TYPE.PROJECT_TRANSFER, new Object[]{loginUser,p});
			//发送邮件
			this.baseService.sendEmail(loginUserId, new Long[]{targetUserId}, NOTICE_MODULE_TYPE.PROJECT_TRANSFER, new Object[]{loginUser,p});
			return this.getSuccessMap(affectedMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/category", method=RequestMethod.GET)
	public Map<String, Object> category(
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			List<ProjectCategory> list = this.projectService.findCategoryList();
			return this.getSuccessMap(list);

		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}
	
	/**
	 * 获取拥有某个权限的项目列表,search为项目名称
	 * @param required
	 * @param loginUserId
	 * @param search
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/select", method=RequestMethod.GET)
	public ModelAndView getProjectList(
			@RequestParam(value="queryType")String required,
			@RequestHeader(value="loginUserId") long loginUserId,
			String search) {
		
		try {
			required = required.toLowerCase();
			// 项目成员权限
			Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required,loginUserId);
			Set<Long> proIds = pMapAsProjectMember.keySet();
			List<Project> list = new ArrayList<>();
			if(null!=proIds && proIds.size()>0){
				if(StringUtils.isNotBlank(search)){
					list = this.projectService.findProjectListByIdsAndName(proIds, search);
				}else{
					list = this.projectService.findProjectListByIds(proIds);
				}
			}
			return this.getSuccessModel(list);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		
	}
	
	/**
	 * 通过应用appcanId获取对应的项目下的正式成员列表
	 * @user jingjian.wu
	 * @date 2015年10月23日 下午5:34:46
	 */
	@ResponseBody
	@RequestMapping(value="/mobilePrjMembers/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getProjectList(@PathVariable(value="appId")String appId){
		try {
			App app = this.appService.findByAppcanAppId(appId);
			if(null==app){
				return this.getFailedMap("不存在与"+appId+"对应的应用");
			}
			List<Long> userIds = projectService.getProjectOfficalMemberUserIdList(app.getProjectId());
			List<User> listUser = userService.findUserByIds(userIds);
			UserListWrapUtil.setNullForPwdFromUserList(listUser);
			return this.getSuccessMap(listUser);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("获取项目成员失败");
		}
	}
	
	@RequestMapping(value="/workplat")
	public Map<String, Object> getWorkPlatList(Match4Project match, HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try {
			String sPageNo      = request.getParameter("pageNo");
			String sPageSize    = request.getParameter("pageSize");

			int pageNo       = 0;
			int pageSize     = 4;
			
			try {
				if(sPageNo != null) {
					pageNo		= Integer.parseInt(sPageNo);
				}
				if(sPageSize != null) {
					pageSize	= Integer.parseInt(sPageSize);
				}
				
			} catch (NumberFormatException nfe) {				
				return this.getFailedMap( nfe.getMessage() );
			}

			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

			Map<String,Object> map = this.projectService.getWorkPlatList(pageable, match, loginUserId);
			
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 创建或者编辑用户项目排序表
	 * @param loginUserId
	 * @param projectId
	 * @author tingwei.yuan
	 * @date 2016/3/31
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/editProjectSort/{projectId}")
	public Map<String,Object> editProjectSort(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@PathVariable(value="projectId") long projectId) {
		log.info("Method editUserProjectSort is called.");
		Map<String, Object> res = new HashMap<>();
		try {
			long num = this.projectService.editProjectSort(loginUserId, projectId);
			res.put("num", num);//返回排序字段sort的最大值
			return this.getSuccessMap(res);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 项目转移、删除校验用户名和密码
	 * @param loginUserId
	 * @param account
	 * @param password
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/ssoValidaton", method=RequestMethod.GET)
	public Map<String, Object> ssoValidaton(
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="account") String account,
			@RequestParam(value="password") String password) {
		log.info("Method ssoValidaton is called........................account:"+account+",password:"+password+",loginUserId:"+loginUserId);
		JSONObject jsonObject = null;
		try {
			String resultStr = this.projectService.ssoValidaton(account, password);
			jsonObject = JSONObject.fromObject(resultStr);
			String retCode = jsonObject.get("retCode").toString();
			String retMsg = jsonObject.get("retMsg").toString();
			if (StringUtils.equals("ok",retCode)) {
				return this.getSuccessMap(retMsg);
			} else {
				return this.getFailedMap(retMsg);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return this.getFailedMap("校验失败");
		} catch (IOException e) {
			e.printStackTrace();
			return this.getFailedMap("校验失败");
		}

	}
	/**
	 * 
	 * @describe 验证邮箱格式	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年11月10日 下午3:36:06	<br>
	 * @param args  <br>
	 * @returnType void
	 *
	 */
	public static void main(String args[]){

		 Matcher matcher = emailpattern.matcher("a@aa.com");

		 System.out.println(matcher.matches());
	}
	/**
	 * 交接任务
	 * 
	 */
	@ResponseBody
	@RequestMapping(value="/memberTransfer/{memberId}",method=RequestMethod.GET)
	public Map<String, Object> memberTransfer(
			@PathVariable(value="memberId") long memberId,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="transferUserId") long transferUserId) {
		try {
			int affected = this.projectService.updateMemberTransfer(memberId,transferUserId);
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);
			return this.getSuccessMap(affectedMap);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 页面申请绑定企业
	    * @Title: updateEnterprise
	    * @Description:绑定企业ID 
	    * @param bizCompanyId
	    * @param  bizCompanyName
	    * @param  loginUserId
	    * @return    参数
	    * @return Map<String,Object>    返回类型
	    * @author haijun.cheng
	    * @Data 2016年07月08日 
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/enterprise/{projectId}",method=RequestMethod.PUT)
	public ModelAndView updateEnterprise(@PathVariable(value="projectId") Long projectId, String bizCompanyId,String bizCompanyName,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("bind projectId Enterprise : bizCompanyId:"+bizCompanyId+",bizCompanyName:"+bizCompanyName+",projectId:"+projectId);
			if(StringUtils.isBlank(bizCompanyId) || StringUtils.isBlank(bizCompanyName)){
				return this.getFailedModel("企业ID和企业名称不可以为空");
			}
			List<NameValuePair> parameters = new ArrayList<>();
			parameters.add( new BasicNameValuePair("shortName", bizCompanyId) );
			parameters.add( new BasicNameValuePair("fullName", bizCompanyName ) );
			String result = HttpUtil.httpPost(emmValidHost+"/omm/enterprise/validName", parameters);//判断企业是否合法
			log.info("projectBind ->validName:"+result);
			JSONObject jsonObject = JSONObject.fromObject(result);
			String status = jsonObject.getString("status");
			if("fail".equals(status)){
				log.info("====>info:"+jsonObject.getString("info"));
				return this.getFailedModel("企业标识和企业名称不匹配");
			}
			Project pt = this.projectService.updateEnterprise(bizCompanyId,bizCompanyName, projectId,loginUserId);
			if(null==pt){
				return this.getFailedModel("此项目已经被授权,不可以重新申请授权");
			}
			//增加动态
			this.dynamicService.addPrjDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.PROJECT_ASK_BIND, projectId, new Object[]{pt,bizCompanyName});
			return this.getSuccessModel(pt);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * 页面申请取消绑定
	 * @user haijun.cheng
	 * @date 2016年07月08日 
	 */
	@ResponseBody
	@RequestMapping(value="/unenterprise/{projectId}",method=RequestMethod.PUT)
	public ModelAndView unenterprise(@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("unenterprise project Enterprise : "+",projectId:"+projectId);
			boolean bl=this.projectService.findIsTeamUnenterprise(projectId);
			if(bl){
				return this.getFailedModel("项目所属团队申请授权时，项目无法取消授权");
			}
			if(projectService.findBizLicense(projectId).equals(PROJECT_BIZ_LICENSE.AUTHORIZED)){
				return this.getFailedModel("该项目已授权,不可以取消授权，请刷新页面");
			}
			if(projectService.findBizLicense(projectId).equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)){
				return this.getFailedModel("该项目授权被拒绝,不可以取消授权，请刷新页面");
			}
			@SuppressWarnings("rawtypes")
			List list = this.projectService.updateCancelEnterprise(projectId);
			if(null==list){
				return this.getFailedModel("此项目已经被授权,不可以取消");
			}
			Project pt = (Project) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			this.dynamicService.addPrjDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.PROJECT_CANCEL_BIND, projectId, new Object[]{pt,enterpriseName});
			return this.getSuccessModel(pt);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * 项目绑定成功后申请解绑
	 * @user haijun.cheng
	 * @date 2016年07月08日 
	 */
	@ResponseBody
	@RequestMapping(value="/deleteBind/{projectId}",method=RequestMethod.PUT)
	public ModelAndView deleteBind(@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("deleteBind project Enterprise : "+",projectId:"+projectId);
			Team team=this.projectService.findIsTeamDeleteBind(projectId);
			if(null!=team && team.getType().equals(TEAMTYPE.UNBINDING)){
				return this.getFailedModel("项目所属团队申请取消授权时，项目无法申请取消授权");
			}
			if(null!=team && team.getType().equals(TEAMTYPE.ENTERPRISE)){
				return this.getFailedModel("此项目所属团队已授权，项目无法解除授权");
			}
			List list = this.projectService.updateDeleteEnterprise(projectId);
			if(null==list){
				return this.getFailedModel("此项目已经被取消授权,无法申请解绑");
			}
			Project pt = (Project) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			this.dynamicService.addPrjDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.PROJECT_ASK_UNBIND, projectId, new Object[]{pt,enterpriseName});
			return this.getSuccessModel(pt);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * 项目绑定成功后取消解绑
	 * @user haijun.cheng
	 * @date 2016年07月08日 
	 */
	@ResponseBody
	@RequestMapping(value="/cancelDeleteBind/{projectId}",method=RequestMethod.PUT)
	public ModelAndView cancelDeleteBind(@PathVariable(value="projectId") Long projectId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			log.info("cancelDeleteBind project Enterprise : "+",projectId:"+projectId);
			boolean bl=this.projectService.findIsTeamUnenterprise(projectId);
			if(bl){
				return this.getFailedModel("该项目无法取消解绑，请先对所属团队取消解绑");
			}
			
			if(projectService.findBizLicense(projectId).equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)){
				return this.getFailedModel("该项目已经解除授权，无法恢复授权，请刷新页面");
			}
			if(projectService.findBizLicense(projectId).equals(PROJECT_BIZ_LICENSE.AUTHORIZED)){
				return this.getFailedModel("该项目已经解除授权被拒绝，不需要恢复授权，请刷新页面");
			}
			
			List list = this.projectService.deleteEnterpriseCancel(projectId);
			if(null==list){
				return this.getFailedModel("此项目已经取消解绑,不可以再次取消");
			}
			Project pt = (Project) list.get(0);
			String enterpriseName = (String) list.get(1);
			//增加动态
			this.dynamicService.addPrjDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.PROJECT_CANCEL_UNBIND, projectId, new Object[]{pt,enterpriseName});
			return this.getSuccessModel(pt);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	/**
	 * 创建项目--审批通过后
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/projectChildCreate",method=RequestMethod.POST)
	public Map<String,Object> cancelDeleteBind(@RequestParam(value="applyNum",required=false) String applyNum,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		if(StringUtils.isBlank(applyNum) ){
			return getFailedMap("审批流水号不能为空！");
		}
		Trans trans = transService.findByApplyNum(applyNum);
		if(trans == null ||trans.getId()==0 ){
			return getFailedMap("未找到子项目的审批记录！");
		}
		String applyStatus = trans.getStatus();
		if(!"3".equals(applyStatus)){
			return getFailedMap("子项目未审批通过不能创建！");
		}
		Project pro = projectService.findOne(trans.getTransactionsId());
		if(pro == null){
			return getFailedMap("子项目信息未找到！");
		}
		pro.setDel(DELTYPE.NORMAL);
		Project newPro = projectService.saveProject(pro);
		if(newPro != null){
			return this.getSuccessMap("创建成功");
		}else{
			return getFailedMap("创建失败！");
		}
		
	}
	/**
	 * 增加项目拼音字段
	 */
	@ResponseBody
	@RequestMapping(value="/addPinyin",method=RequestMethod.GET)
	public Map<String, Object> addPinyin(HttpServletRequest request){
			
			Map<String,Object> map = this.projectService.addPinyin();
			return map;
		}
	
	/**
	 * 初始化项目进度
	 * @return
	 */
	// 2016-07-20 初始化项目进度
	@ResponseBody
	@RequestMapping(value="/initProgress",method=RequestMethod.GET)
	public Map<String, Object> addPinyin(){
			List<Project> listProject = projectService.findByDel(DELTYPE.NORMAL);
			for(Project project:listProject){
				this.projectService.updateProjProgressAndStatus(project.getId());
			}
			return this.getAffectMap();
	}

}
