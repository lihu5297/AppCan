package org.zywx.cooldev.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.USER_ASKED_TYPE;
import org.zywx.cooldev.dao.project.ProjectDao;
import org.zywx.cooldev.dao.project.ProjectMemberDao;
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
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.MD5Util;
import org.zywx.cooldev.util.mail.MailUtil;
import org.zywx.cooldev.util.mail.base.MailSenderInfo;
import org.zywx.cooldev.util.mail.base.SendMailTools;
import org.zywx.cooldev.vo.AskUserVO;

/**
 * 
    * @ClassName: TeamMemberController
    * @Description:团队成员控制层 
    * @author jingjian.wu
    * @date 2015年8月13日 下午2:31:09
    *
 */
@Controller
public class TeamMemberController extends BaseController {

	@Autowired
	private ProjectDao projectDao;
	
	@Autowired
	private ProjectMemberDao projectMemberDao;
	/**
	    * @Title: 团队邀请成员 (通过邮箱邀请)
	    * @Description:团队邀请成员 (通过邮箱邀请)
	    * @param @param teamId
	    * @param @param groupId
	    * @param @param userStr  邮箱1#NORMAL;邮箱2#MANAGER
	    * @param @param content  描述
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午3:43:33
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamMember",method = RequestMethod.POST)
	public Map<String, Object> askUser(long teamId,
			long groupId, String userStr,String content,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			log.info("=====================================================add member for team------------->loginUserId:"+loginUserId+",userStr:"+userStr+",teamId:"+teamId+",content:"+content);
			String dynamicRef = "";
			List<AskUserVO> askList = new ArrayList<AskUserVO>();
			String[] tmp = userStr.split(";");
			for(String str:tmp){
				String [] temp = str.split("#");
				if(temp.length==2){
					AskUserVO vo = new AskUserVO();
					dynamicRef+=temp[0]+",";
					vo.setEmail(temp[0]);
					vo.setUserAuth(USER_ASKED_TYPE.valueOf(temp[1]));
					askList.add(vo);
				}
			}
			if(askList.size()==0){
				return this.getFailedMap("邀请成员参数错误!");
			}
			Map<String, Object> resultMap = this.teamMemberService.addMember(Long.parseLong(loginUserId),askList, teamId, groupId, content);
			List<TeamMember> addedUsers = (List<TeamMember>) resultMap.get("addedMembers");
			List<String> sendEmailMessage = (List<String>) resultMap.get("sendEmailMessage");
			if(null!=addedUsers && addedUsers.size()==0){
				return this.getFailedMap(sendEmailMessage.size()>0?sendEmailMessage.get(0):"邀请成员失败！");
			}
			//
			Team tm = this.teamService.getOne(teamId);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_ASKUSER,teamId, new Object[]{dynamicRef,tm});
			
			for(TeamMember vo:addedUsers){
				User user = this.userService.findUserById(vo.getUserId());
				if(null!=user){
					User loginUser = this.userService.findUserById(Long.parseLong(loginUserId));
					String loginuserName = loginUser.getUserName()==null?loginUser.getAccount():loginUser.getUserName();
					//增加通知
					this.noticeService.addNotice(Long.parseLong(loginUserId),new Long[]{user.getId()}, NOTICE_MODULE_TYPE.TEAM_ASKUSER, new Object[]{loginuserName,tm});
				}else{
					log.info("team ask user :"+vo.getUserId()+" not exists in table T_User.");
				}
			}
			map.put("status", "success");
			map.put("message", addedUsers);
			map.put("sendEmailMessage", sendEmailMessage);
		} catch (Exception e) {
			map.put("status", "failed");
			if(e.getClass().equals(MessagingException.class) || e.getClass().equals(MailSendException.class)){
				map.put("message", "抱歉，您的邮件发送失败，请重新邀请！");
			}else{
				map.put("message", "邀请失败！");
			}
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 从用户表选人添加到团队,私有部署情况
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午8:19:37
	 */
	@ResponseBody
	@RequestMapping(value="/teamMemberFromAllUser",method = RequestMethod.POST)
	public Map<String, Object> askUserFromUserTable(long teamId,
			long groupId, @RequestParam(value="userStr") List<String> userStrList,String content,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			log.info("=====================================================add member for team from User Table------------->loginUserId:"+loginUserId+",userStr:"+userStrList+",teamId:"+teamId+",content:"+content);
			map= teamMemberService.addTeamMemberFromUserTable(userStrList, teamId, groupId);
			List<TeamMember> listMember = (List<TeamMember>) map.get("members");
			List<Long> userIds = (List<Long>) map.get("userIds");
			if(listMember.size() == 0) {
				return this.getFailedMap("没有成员被成功加入团队");
			}
			
			//
			Team tm = this.teamService.getOne(teamId);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_ASKUSER,teamId, new Object[]{map.get("userNames").toString(),tm});
			
			User loginUser = this.userService.findUserById(Long.parseLong(loginUserId));
			
			this.noticeService.addNotice(Long.parseLong(loginUserId), (Long[])userIds.toArray(new Long[userIds.size()]), NOTICE_MODULE_TYPE.TEAM_ASKUSER, new Object[]{loginUser,tm});
			
			mapResult.put("status", "success");
			mapResult.put("message", listMember);
		} catch (Exception e) {
			mapResult.put("status", "failed");
			mapResult.put("message", e.getMessage());
			e.printStackTrace();
		}
		return mapResult;
	}
	
	
	/**
	    * @Title: deleteMemberFromTeam
	    * @Description:从团队中删除某个成员 
	    * @param @param teamId
	    * @param @param userId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user wjj
		* @date 2015年8月13日 上午11:20:05
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamMember/{teamMemberId}",method=RequestMethod.DELETE)
	public Map<String, Object> deleteMemberFromTeam(@PathVariable("teamMemberId") long teamMemberId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			log.info("==============================delte member from team-->loginUserId:"+loginUserId+",teamMemberId:"+teamMemberId);
			//------------------判断是否有权限删除------start------------------
			TeamMember teamMemberTarget = this.teamMemberService.findOne(teamMemberId);
			if(teamMemberTarget.getType()==TEAMREALTIONSHIP.CREATE){//如果被操作人是团队创建者,则不允许删除
				return this.getFailedMap("团队创建者不能够被删除!");
			}
			TeamAuth teamAuthTarget = this.teamAuthService.findByMemberIdAndDel(teamMemberTarget.getId(), DELTYPE.NORMAL);
			TeamMember teamMemberSrc = this.teamMemberService.findMemberByTeamIdAndUserId(teamMemberTarget.getTeamId(), Long.parseLong(loginUserId));
			TeamAuth teamAuthSrc = this.teamAuthService.findByMemberIdAndDel(teamMemberSrc.getId(), DELTYPE.NORMAL);
			if(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER).getId()==teamAuthSrc.getRoleId()){//如果操作人是普通成员
				if(teamMemberTarget.getId().longValue()!=teamMemberSrc.getId().longValue()){//不是自己退出团队(自己移除自己)
					return this.getFailedMap("对不起,您没有操作权限!");
				}
			}
			//管理员,不可以删除别的管理员
			if(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId()==teamAuthSrc.getRoleId()
					&&
					Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId()==teamAuthTarget.getRoleId()){
				if(teamMemberTarget.getId().longValue()!=teamMemberSrc.getId().longValue()){//不是自己退出团队(自己移除自己)
					return this.getFailedMap("对不起,您没有权限删除其他管理员!");
				}
			}
			//------------------判断是否有权限删除------end------------------
			TeamMember tm =this.teamMemberService.deleteMemberById(teamMemberId,getProductTokenByTeamId(teamMemberTarget.getTeamId()));
			Team t = this.teamService.getOne(tm.getTeamId());
			User u = this.userService.findUserById(tm.getUserId());
			User loginUser = this.userService.findUserById(Long.parseLong(loginUserId));
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_REMOVE_MEMBER,tm.getTeamId(), new Object[]{t,u.getUserName(),tm});
			//增加通知
			this.noticeService.addNotice(Long.parseLong(loginUserId), new Long[]{tm.getUserId()}, NOTICE_MODULE_TYPE.TEAM_REMOVE_MEMBER, new Object[]{loginUser,t});
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			map.put("status", "failed");
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	    * @Title: findGroupIdByTeamIdAndUserId
	    * @Description: 查询某个人在某个团队下属于哪个分组
	    * @param @param teamId
	    * @param @param userId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午9:10:06
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamMember/{teamId}/{userId}",method=RequestMethod.GET)
	public ModelAndView findGroupIdByTeamIdAndUserId(@PathVariable("teamId") long teamId,
			@PathVariable("userId") long userId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> innermap = new HashMap<String, Object>();
		try {
			long groupId=this.teamMemberService.findGroupIdByTeamIdAndUserId(teamId, userId);
			innermap.put("groupId", groupId);
			return this.getSuccessModel(innermap);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	
	/**
	 * 更改团队所属小组
	 * @param teamMemberId
	 * @param groupId
	 * @param loginUserId
	 * @return Map<String,Object>
	 * @user jingjian.wu
	 * @date 2015年8月25日 下午4:14:57
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamMember/{teamMemberId}",method=RequestMethod.PUT)
	public Map<String, Object> findGroupIdByTeamIdAndUserId(@PathVariable("teamMemberId") long teamMemberId,
			Long groupId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			log.info("=======================================update teamMember group-->loginUserId:"+loginUserId+",teamMemberId:"+teamMemberId+",groupId:"+groupId);
			if(null == groupId || (groupId <0 && groupId!=-1)){
				return this.getFailedMap("param groupId  "+groupId+" is not available!");
			}
			TeamMember teamMem = this.teamMemberService.findOne(teamMemberId);
			if(null==teamMem){
				return this.getFailedMap("param teamMemberId "+teamMemberId+" is not exist!");
			}
			User user= userService.findUserById(teamMem.getUserId());
			TeamGroup srcGroup = this.teamGroupService.findOne(teamMem.getGroupId());
			TeamGroup targetGroup = this.teamGroupService.findOne(groupId);
			if(null ==targetGroup && groupId!=-1){
				return this.getFailedMap("param groupId is not exists!");
			}
			this.teamMemberService.updateGroup(teamMemberId, groupId);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAM_CHANGE_GROUP,teamMem.getTeamId(), new Object[]{user.getUserName()==null?user.getAccount():user.getUserName(),null==srcGroup?"无分组":srcGroup.getName(),null==targetGroup?"无分组":targetGroup.getName()});
			return this.getAffectMap();
		} catch (Exception e) {
			map.put("status", "failed");
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * EMM调用,向团队中插入用户
	 * @user jingjian.wu
	 * @date 2015年10月26日 下午8:11:42
	 */
	@ResponseBody
	@RequestMapping(value="/emminvoke/teamMember/{teamId}",method=RequestMethod.POST)
	public Map<String, Object> emminvokeAddMember(@PathVariable("teamId") String uuid,String loginName
			,String mobilePhone,String userName,HttpServletRequest request){
		try {
//			if(!validateIP(request)){
//				return this.getFailedMap("ip is not available");
//			}
			Team teamTmp = teamService.getByUuid(uuid);
			if(null==teamTmp){
				return this.getFailedMap("团队不存在");
			}
			User user = userService.saveUserIfNotExist(loginName,mobilePhone,userName);
			TeamMember tm = teamMemberService.findMemberByTeamIdAndUserId(teamTmp.getId(),user.getId());
			if(null==tm){
				TeamMember teamMember = new TeamMember();
				teamMember.setGroupId(-1L);
				teamMember.setJoinTime(new Timestamp(System.currentTimeMillis()));
				teamMember.setTeamId(teamTmp.getId());
				teamMember.setType(TEAMREALTIONSHIP.ACTOR);
				teamMember.setUserId(user.getId());
				teamMemberService.save(teamMember);
				TeamAuth teamAuth = new TeamAuth();
				teamAuth.setMemberId(teamMember.getId());
				teamAuth.setRoleId(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER).getId());
				teamAuthService.save(teamAuth);
				return this.getSuccessMap(teamMember);
			}
			return this.getSuccessMap(tm);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("添加团队成员失败");
		}
	}
	
	
	
	
	/**
	 * EMM调用,删除团队成员
	 * @user jingjian.wu
	 * @date 2015年10月26日 下午8:11:42
	 */
	@ResponseBody
	@RequestMapping(value="/emminvoke/teamMember/del/{teamId}",method=RequestMethod.POST)
	public Map<String, Object> emminvokeDelMember(@PathVariable("teamId") String uuid,
			String loginName,String mobilePhone,String userName ,HttpServletRequest request){
		try {
			teamMemberService.updateEmmInvokeDelMember(loginName, mobilePhone, userName, uuid);
			return this.getAffectMap();
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("删除团队成员失败");
		}
	}
	
	@Autowired
	private MailUtil mailUtil;
	
	@Autowired
	private SendMailTools sendMailTool;
	
	@ResponseBody
	@RequestMapping(value="/email",method=RequestMethod.GET)
	public Map<String,Object> email(){
		try {
			log.info("test email=====================>");
			/*MailBean mailBean = new MailBean();
			mailBean.setFrom("1023541187@qq.com");
			mailBean.setFromName("1023541187");
			mailBean.setSubject("AppCan");
			mailBean.setTemplate("aaa");
			mailBean.setToEmails(new String[]{"jingjian.wu@zymobi.com"});
			log.info("团队邀请,准备给用户:jingjian.wu@zymobi.com发送邮件");
			mailUtil.sendMailByAsynchronousMode(mailBean);
			log.info("=========================发送完了==========================================");*/
			
			MailSenderInfo mailInfo = new MailSenderInfo();
			StringBuffer content = new StringBuffer();
			content.append("<p style='padding-left:8px'>Hi，你好：</p>");
			content.append("<p style='padding:4px;word-wrap:break-word'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;感谢您使用协同开发，您正在进行绑定邮箱验证，本次请求的验证码为：<span style='color:#FF9900'>"+123456+"</span> （为保证您账号的安全性，请在1小时内完成验证）</p>");
			content.append("<p style='padding:4px;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;祝您使用愉快！</p>");
			content.append("<p style='text-align:right;padding-right:55px;'>AppCan</p>");
			mailInfo.setContent(content.toString());
			mailInfo.setToAddress("jingjian.wu@zymobi.com");
			mailInfo.setSubject("【AppCan-协同开发】邮箱绑定通知");
			sendMailTool.sendMailByAsynchronousMode(mailInfo);
			log.info("new Mail over");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getAffectMap();
	}
	
}
