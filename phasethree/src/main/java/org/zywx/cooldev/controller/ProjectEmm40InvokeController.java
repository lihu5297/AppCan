package org.zywx.cooldev.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.service.ProjectAuthService;
import org.zywx.cooldev.service.ProjectMemberService;
import org.zywx.cooldev.service.UserActiveToolService;
import org.zywx.cooldev.service.UserService;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.mail.base.SendMailTools;
@Controller
@RequestMapping(value="/projectEmmInvoke")
public class ProjectEmm40InvokeController extends BaseController{
	
	@Autowired
	private ProjectMemberService projectMemberService;
	
	@Autowired 
	private ProjectAuthService projectAuthService;
	
	@Autowired 
	private UserService userService;
	
	//企业版还是大众版标识
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	//针对企业版有固定的企业简称
	@Value("${enterpriseId}")
	private String enterpriseId;
		
	//针对企业版有固定的企业全称
	@Value("${enterpriseName}")
	private String enterpriseName;
	
	/**
	 * 创建新项目
	 * 1. 添加新项目
	 * 2. 添加默认成员和权限
	 * @param p
	 * @param request
	 * @param loginUserId
	 * @return
	 */
//	@ResponseBody
//	@RequestMapping(method=RequestMethod.POST)
//	public Map<String, Object> createProject(Project p, HttpServletRequest request,
//		@RequestHeader(value="currentLoginAccount",required=true) String currentLoginAccount) {
//		log.info("post project called");
//		log.info(p);
//		try {
//			if(p.getName()!=null&&p.getName().length()>1000){
//				return this.getFailedMap("项目名称不能超过1000个字符");
//			}
//			if(p.getDetail()!=null&&p.getDetail().length()>1000){
//				return this.getFailedMap("项目描述不能超过1000个字符");
//			}
//			if(serviceFlag.equals("enterprise")){//企业版
//				if(p.getTeamId()==-1){
//					return this.getFailedMap("请选择对应的团队");
//				}
//				p.setBizCompanyId(enterpriseId);
//				p.setBizCompanyName(enterpriseName);
//				p.setType(PROJECT_TYPE.TEAM);//企业版不允许创建个人项目
//			}
//			User user=new User();
//			user = this.userService.saveUserIfNotExistByEmail(currentLoginAccount);
////			this.projectService.addProject(p);
//			log.info(p);
//			ProjectMember member = new ProjectMember();
//			member.setUserId(user.getId());
//			member.setProjectId(p.getId());
//			member.setType(PROJECT_MEMBER_TYPE.CREATOR);
//			this.projectService.saveProjectMember(member,user);
//			log.info(member);
//			ProjectAuth auth = new ProjectAuth();
//			auth.setMemberId(member.getId());
//			auth.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR).getId());
//			this.projectService.saveProjectAuth(auth);
//			if(null == p.getType() || p.getType().compareTo(PROJECT_TYPE.TEAM)!=0){//PROJECT_TYPE.TEAM 团队项目
//				this.dynamicService.addPrjDynamic(user.getId(), DYNAMIC_MODULE_TYPE.PROJECT_ADD, p.getId(), p.getName());//PROJECT_ADD  创建项目
//			}else{
//				this.dynamicService.addPrjDynamic(user.getId(), DYNAMIC_MODULE_TYPE.TEAM_CREATE_PRJ, p.getId(), p.getName());//TEAM_CREATE_PRJ 创建团队项目
//			}
//			return this.getSuccessMap(p);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return this.getFailedMap(e.getMessage());
//		}
//	}
	/**
	 * EMM4.0调用,向项目中插入用户
	 * @user haijun.cheng
	 * @date 2016年07月04日
	 */
	@ResponseBody
	@RequestMapping(value="/emminvoke/projectMember/{projectId}",method=RequestMethod.POST)
	public Map<String, Object> emminvokeAddMember(@PathVariable("projectId") String uuid,String loginName,
			@RequestHeader(value="currentLoginAccount",required=true) String currentLoginAccount,
			String mobilePhone,String userName,HttpServletRequest request){
		try {
			Project projectTmp = projectService.getByUuid(uuid);
			if(null==projectTmp){
				return this.getFailedMap("项目不存在");
			}
			User user = userService.saveUserIfNotExist(loginName,mobilePhone,userName);
			ProjectMember pm = projectMemberService.findMemberByProjectIdAndUserId(projectTmp.getId(),user.getId());
			if(null==pm){
				ProjectMember projectMember = new ProjectMember();
				projectMember.setProjectId(projectTmp.getId());
				projectMember.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
				projectMember.setUserId(user.getId());
				projectMemberService.save(projectMember);//保存添加的成员为参与者
				return this.getSuccessMap(projectMember);
			}
			return this.getSuccessMap(pm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("添加项目成员失败");
		}
	}
	/**
	 * EMM4.0调用,删除团队成员
	 * @user haijun.cheng
	 * @date 2016年07月04日 
	 */
	@ResponseBody
	@RequestMapping(value="/emminvoke/projectMember/del/{projectId}",method=RequestMethod.POST)
	public Map<String, Object> emminvokeDelMember(@PathVariable("projectId") String uuid,
			@RequestHeader(value="currentLoginAccount",required=true) String currentLoginAccount,
			String loginName,String mobilePhone,String userName ,HttpServletRequest request){
		
		try {
			projectMemberService.updateEmmInvokeDelMember(loginName, mobilePhone, userName, uuid);
			return this.getAffectMap();
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("删除项目成员失败");
		}
		
	}

/**
 * EMM同意/拒绝申请绑定
 * @user haijun.cheng	
 * @date 2016年07月05日 下午4:49:15
 */
@ResponseBody
@RequestMapping(value="/isApproveBind/{projectId}",method=RequestMethod.POST)
public ModelAndView isApproveBind(@PathVariable(value="projectId") String uuid,
		String bizLicense,
		@RequestHeader(value="currentLoginAccount",required=true) String currentLoginAccount,
		HttpServletRequest request){
	try {
		log.info("========>uuid:"+uuid+",bizLincense:"+bizLicense+",currentLoginAccout:"+currentLoginAccount);
		if(bizLicense.equals("0")){//同意绑定企业
			log.info("EMM agree bind project Enterprise ,uuid:"+uuid);
			Project tmp = projectService.getByUuid(uuid);
			if(null!=tmp && tmp.getType().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)){
				return this.getFailedModel("此项目已经被授权,无需重复授权");
			}
			Project pt = this.projectService.bizLicense(tmp.getId());
			if(null==pt){
				return this.getFailedModel("所填信息不完整,企业ID,企业名称不可以为空");
			}
			//添加通知
			List<ProjectMember> members = this.projectMemberService.findByProjectIdAndDel(pt.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(ProjectMember member : members){
				if(member.getType().compareTo(PROJECT_MEMBER_TYPE.CREATOR)==0){
					ids.add(member.getUserId());
				}else{
					List<ProjectAuth> pa = this.projectAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					for(ProjectAuth projectAuth:pa){
						Role role = Cache.getRole(projectAuth.getRoleId());
						if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
							ids.add(member.getUserId());
						}
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROJECT_BIND_ENTERPRISE, new Object[]{pt,pt.getBizCompanyName()});
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("project", pt);
			List<User> list = this.projectService.findAllUserBelongProject(pt.getId());
			ProjectMember projectCrt = projectMemberService.findMemberByProjectIdAndMemberType(pt.getId(), PROJECT_MEMBER_TYPE.CREATOR);
			for(User user:list){
				if(user.getId()==projectCrt.getUserId()){
					user.setProjectCreator(true);
				}
			}
			map.put("users", list);
			return this.getSuccessModel(map);
		}else{//不同意绑定企业
			log.info("EMM don't agree bind project Enterprise : uuid:"+uuid);
			Project tmp = projectService.getByUuid(uuid);
			if(null!=tmp && tmp.getType().equals(TEAMTYPE.NORMAL)){
				return this.getFailedModel("此团队没有绑定企业,无需解除绑定");
			}
			@SuppressWarnings("rawtypes")
			List list= this.projectService.updateUnEnterprise(tmp.getId());
			Project pt = (Project) list.get(0);
			String updateUnEnterprise = (String) list.get(1);
			
			//添加通知
			List<ProjectMember> members = this.projectMemberService.findByProjectIdAndDel(pt.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(ProjectMember member : members){
				if(member.getType().compareTo(PROJECT_MEMBER_TYPE.CREATOR)==0){
					ids.add(member.getUserId());
				}else{
					List<ProjectAuth> ta = this.projectAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					for (ProjectAuth projectAuth:ta){
						Role role = Cache.getRole(projectAuth.getRoleId());
						if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
							ids.add(member.getUserId());
						}
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROJECT_UNBIND_ENTERPRISE, new Object[]{pt,updateUnEnterprise});
			
			return this.getSuccessModel(pt);
		}
	} catch (Exception e) {
		e.printStackTrace();
		return this.getFailedModel(e.getMessage());
	}
}
/**
 * 转让项目
 * @throws Exception 
 * @user haijun.cheng	
 * @date 2016年07月05日 下午4:49:15
 */
@RequestMapping(value="/transfer/{projectId}",method=RequestMethod.POST)
public ModelAndView transferTeam(
		@PathVariable(value="projectId") Long projectId,
		 Long targetUserId,
		@RequestHeader(value="currentLoginAccount",required=true) String currentLoginAccount,
		 String invokeType) throws Exception{
	
	try {
		log.info("transfer project : projectId :"+projectId+",targetUserId:"+targetUserId);
		User user = this.userService.saveUserIfNotExistByEmail(currentLoginAccount);
		Project pt = this.projectService.getOne(projectId);
//		if(tm.getType().equals(TEAMTYPE.ENTERPRISE)){
//			return this.getFailedModel("企业已经授权的团队不允许转让!");
//		}
//		if(tm.getEnterpriseId()!=null){
//			return this.getFailedModel("企业绑定申请中的团队不允许转让!");
//		}
		ProjectMember projectMem = projectMemberService.findMemberByProjectIdAndMemberType(projectId,PROJECT_MEMBER_TYPE.CREATOR);
		if(projectMem==null || projectMem.getUserId()!=user.getId()){//非团队创建者不允许转移团队
			return this.getFailedModel("对不起,您没有权限转移此团队.");
		}
		this.projectService.updateTransferProject(projectId, user.getId(), targetUserId);
		
		//增加通知
		this.noticeService.addNotice(user.getId(), new Long[]{targetUserId}, NOTICE_MODULE_TYPE.PROJECT_TRANSFER, new Object[]{user,pt});
		//发送邮件
		this.baseService.sendEmail(user.getId(), new Long[]{targetUserId}, NOTICE_MODULE_TYPE.PROJECT_TRANSFER, new Object[]{user,pt});
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
 * EMM同意/拒绝解除绑定
 * @user haijun.cheng	
 * @date 2016年07月13日
 */
@ResponseBody
@RequestMapping(value="/isAgreeUnbind/{projectId}",method=RequestMethod.POST)
public ModelAndView isAgreeUnbind(@PathVariable(value="projectId") String uuid,
		String bizLicense,
		@RequestHeader(value="currentLoginAccount",required=true) String currentLoginAccount,
		HttpServletRequest request){
	try {
		log.info("===================>uuid:"+uuid+",bizLicense:"+bizLicense+",currentLoginAccount:"+currentLoginAccount);
		if(bizLicense.equals("0")){//同意解绑
			log.info("EMM agree project unbinld Enterprise ,uuid:"+uuid);
			Project tmp = projectService.getByUuid(uuid);
			if(null!=tmp && tmp.getType().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)){
				return this.getFailedModel("此项目已经解绑,无需重复解绑");
			}
			@SuppressWarnings("rawtypes")
			List list= this.projectService.updateUnEnterprise(tmp.getId());
			Project pt = (Project) list.get(0);
			String updateUnEnterprise = (String) list.get(1);
			
			//添加通知
			List<ProjectMember> members = this.projectMemberService.findByProjectIdAndDel(pt.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(ProjectMember member : members){
				if(member.getType().compareTo(PROJECT_MEMBER_TYPE.CREATOR)==0){
					ids.add(member.getUserId());
				}else{
					List<ProjectAuth> ta = this.projectAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					for(ProjectAuth projectAuth:ta){
						Role role = Cache.getRole(projectAuth.getRoleId());
						if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
							ids.add(member.getUserId());
						}
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROJECT_AGREE_UNBIND_ENTERPRISE, new Object[]{pt,updateUnEnterprise});
			return this.getSuccessModel(pt);
		}else{//不同意解绑
			log.info(" EMM don't agree project unbinld Enterprise: uuid:"+uuid);
			Project tmp = projectService.getByUuid(uuid);
//			if(null!=tmp && tmp.getType().equals(TEAMTYPE.NORMAL)){
//				return this.getFailedModel("此团队没有绑定企业,无需解除绑定");
//			}
			Project pt = this.projectService.bizLicense(tmp.getId());
			if(null==pt){
				return this.getFailedModel("所填信息不完整,企业ID,企业名称不可以为空");
			}
			//增加动态
//			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_BIND_ENTERPRISE, teamId, new Object[]{tm,tm.getEnterpriseName()});
			//添加通知
			List<ProjectMember> members = this.projectMemberService.findByProjectIdAndDel(pt.getId(), DELTYPE.NORMAL);
			List<Long> ids = new ArrayList<>();
			for(ProjectMember member : members){
				if(member.getType().compareTo(PROJECT_MEMBER_TYPE.CREATOR)==0){
					ids.add(member.getUserId());
				}else{
					List<ProjectAuth> pa = this.projectAuthService.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					for (ProjectAuth projectAuth:pa){
						Role role = Cache.getRole(projectAuth.getRoleId());
						if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MANAGER)){
							ids.add(member.getUserId());
						}
					}
				}
			}
			this.noticeService.addNotice(-1L, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROJECT_UNAGREE_UNBIND_ENTERPRISE, new Object[]{pt,pt.getBizCompanyName()});
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("project", pt);
			List<User> list = this.projectService.findAllUserBelongProject(pt.getId());
			ProjectMember projectCrt = projectMemberService.findMemberByProjectIdAndMemberType(pt.getId(), PROJECT_MEMBER_TYPE.CREATOR);
			for(User user:list){
				if(user.getId()==projectCrt.getUserId()){
					user.setProjectCreator(true);
				}
			}
			map.put("users", list);
			return this.getSuccessModel(map);
		}
	} catch (Exception e) {
		e.printStackTrace();
		return this.getFailedModel(e.getMessage());
	}
}
/**
 * EMM调用获取要绑定的项目的成员信息
 * @user haijun.cheng
 * @date 2016-09-20
 */
@ResponseBody
@RequestMapping(value="/isApproveBind/getUserList/{projectId}",method=RequestMethod.POST)
public ModelAndView getUserList(@PathVariable(value="projectId") String uuid, 
		HttpServletRequest request){
	try {
		Map<String,Object> map = new HashMap<String, Object>();
		Project pr = projectService.getByUuid(uuid);
		List<User> list = this.projectService.findAllUserBelongProject(pr.getId());
		map.put("users", list);
		return this.getSuccessModel(map);
	} catch (Exception e) {
		e.printStackTrace();
		return this.getFailedModel(e.getMessage());
	}
}

}
