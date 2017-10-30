package org.zywx.cooldev.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.bug.BugMember;
import org.zywx.cooldev.entity.process.Process;

/**
 * bugMember控制器
 * @author yongwen.wang
 *
 */
@Controller
@RequestMapping(value="/bugMember")
public class BugMemberController extends BaseController {

	/**
	 * 修改bug指派者
	 * @param bugId
	 * @param assignedPersonUserId
	 */
	@ResponseBody
	@RequestMapping(value="/changeAssignedPerson",method=RequestMethod.PUT)
	public Map<String, Object> changeAssignedPerson(
			@RequestParam(value="bugId") long bugId,
			@RequestParam(value="assignedPersonUserId") long assignedPersonUserId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		
		try {
			log.info("changeAssignedPerson-->loginUserId:"+loginUserId+",bugId:"+bugId+",assignedPersonUserId:"+assignedPersonUserId);
			BugMember oldAssignedPerson = this.bugMemberService.getBugAssignedPerson(bugId);
			int affected = bugMemberService.editBugAssignedPerson(bugId, assignedPersonUserId);
			Bug bug = this.bugService.getSingleBug(bugId);
			Process p =this.processService.findOne(bug.getProcessId());
			User user = this.userService.findUserById(assignedPersonUserId);
			String userName = user.getUserName()==null?user.getAccount():user.getUserName();
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.BUG_CHANGE_ASSIGNEDPERSON, p.getProjectId(), new Object[]{bug,userName});
			user = this.userService.findUserById(loginUserId);
			if(oldAssignedPerson.getUserId()!=loginUserId){
				this.noticeService.addNotice(loginUserId, new Long[]{oldAssignedPerson.getUserId()}, NOTICE_MODULE_TYPE.BUG_REMOVE_MEMBER, new Object[]{user,bug});
				//发送邮件
				this.baseService.sendEmail(loginUserId, new Long[]{oldAssignedPerson.getUserId()}, NOTICE_MODULE_TYPE.BUG_REMOVE_MEMBER, new Object[]{user,bug});
			}
			if(assignedPersonUserId!=loginUserId){
				this.noticeService.addNotice(loginUserId, new Long[]{assignedPersonUserId}, NOTICE_MODULE_TYPE.BUG_UPDATE_ASSIGNEDPERSON, new Object[]{user,bug});
				//发送邮件
				this.baseService.sendEmail(loginUserId, new Long[]{assignedPersonUserId}, NOTICE_MODULE_TYPE.BUG_UPDATE_ASSIGNEDPERSON, new Object[]{user,bug});
			}
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 删除bug参与人
	 * @param memberId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/removeMember", method=RequestMethod.DELETE)
	public Map<String, Object> removeMember(
			@RequestParam(value="memberId") long memberId,
			@RequestHeader(value="loginUserId") long loginUserId) {
		BugMember bugMember = this.bugService.getBugMember(memberId);
		Bug bug = this.bugService.getSingleBug(bugMember.getBugId());
		Process process = this.processService.findOne(bug.getProcessId());
		User user = this.userService.findUserById(bugMember.getUserId());
		User loginUser = this.userService.findUserById(loginUserId);
		
		this.bugMemberService.removeBugMember(memberId,loginUserId);
		
		Map<String, Integer> affected = new HashMap<>();
		affected.put("affected", 1);
		
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.BUG_REMOVE_MEMBER, process.getProjectId(), new Object[]{bug,user});
		this.noticeService.addNotice(loginUserId, new Long[]{user.getId()}, NOTICE_MODULE_TYPE.BUG_REMOVE_MEMBER, new Object[]{loginUser,bug});
		//发送邮件
		this.baseService.sendEmail(loginUserId, new Long[]{user.getId()}, NOTICE_MODULE_TYPE.BUG_REMOVE_MEMBER, new Object[]{loginUser,bug});
		return this.getSuccessMap(affected);
	}
	/**
	 * 添加参与成员
	 * @param member
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/addMember", method=RequestMethod.POST)
	public Map<String, Object> addMember(
			BugMember member,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		
		try {
			log.info("bug add member-->loginUserId:"+loginUserId+",member:"+member.toStr());
			this.bugMemberService.addBugMember(member,loginUserId);
			
			Bug bug = this.bugService.getSingleBug(member.getBugId());
			User user = this.userService.findUserById(loginUserId);
			User targetUser = this.userService.findUserById(member.getUserId());
			
			Process p =this.processService.findOne(bug.getProcessId());
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.BUG_ADD_MEMBER, p.getProjectId(), new Object[]{bug,targetUser.getUserName()});
			//添加通知
			this.noticeService.addNotice(loginUserId, new Long[]{member.getUserId()}, NOTICE_MODULE_TYPE.BUG_ADD_MEMBER, new Object[]{user,bug});
			//发送邮件
			this.baseService.sendEmail(loginUserId, new Long[]{member.getUserId()}, NOTICE_MODULE_TYPE.BUG_ADD_MEMBER, new Object[]{user,bug});
			return this.getSuccessMap(member);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
}
