package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.entity.TeamGroup;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.util.UserListWrapUtil;

/**
    * @ClassName: TeamGroupController
    * @Description: 团队小组
    * @author wjj
    * @date 2015年8月12日 上午10:12:52
    *
 */
@Controller
public class TeamGroupController extends BaseController {

	/**
	    * @Title: findAllTeamGroup
	    * @Description: 查询某个团队下面的所有分组信息 
	    * @param @param teamId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user wjj
		* @date 2015年8月12日 上午10:23:22
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamGroup/allgroup",method = RequestMethod.GET)
	public ModelAndView findAllGroupByTeamId(@RequestParam(required=true) long teamId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		try {
			List<TeamGroup> listGroup = this.teamGroupService.findAllByTeamId(teamId);
			//默认添加一个无分组
			TeamGroup tg = new TeamGroup();
			tg.setId(-1L);
			tg.setName("无分组");
			listGroup.add(tg);
			return this.getSuccessModel(listGroup);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	    * @Title: addTeamGroup
	    * @Description:创建团队小组 
	    * @param @param teamId
	    * @param @param name
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user wjj
		* @date 2015年8月12日 上午10:32:20
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamGroup",method = RequestMethod.POST)
	public Map<String, Object> addTeamGroup(long teamId,String name,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(name)){
				map.put("status", "failed");	
			}else{
				List<TeamGroup> listGroup = teamGroupService.findByNameAndDel(name,teamId, DELTYPE.NORMAL);
				if(null!=listGroup && listGroup.size()>0){
					map.put("status", "failed");
					map.put("message", "小组名称已经存在!");
					return map;
				}
				TeamGroup tg = new TeamGroup();
				tg.setTeamId(teamId);
				tg.setName(name);
				this.teamGroupService.addTeamGroup(tg);
				map.put("message", tg);
				map.put("status", "success");
				//增加动态
				this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAMGROUP_ADD,teamId, new Object[]{tg});
				
			}
		} catch (Exception e) {
			map.put("status", "failed");
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	    * @Title: findUserByGroupId
	    * @Description:查询某个小组下面的用户信息(已有用户,受邀用户) 
	    * @param @param groupId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user wjj
		* @date 2015年8月12日 下午8:00:12
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamGroup/member/{groupId}",method = RequestMethod.GET)
	public ModelAndView findUserByGroupId(HttpServletRequest request,@PathVariable("groupId") long groupId,
			@RequestParam(value="teamId",required=true) long teamId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> mapUser = new HashMap<String, Object>();
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
		
		try {
			List<TEAMREALTIONSHIP> typeIds = new ArrayList<TEAMREALTIONSHIP>();
			typeIds.add(Enums.TEAMREALTIONSHIP.CREATE);
			typeIds.add(Enums.TEAMREALTIONSHIP.ACTOR);
			Page<User> listUsers = this.userService.findUserListByGroupIdAndRel(typeIds, groupId,teamId,pageable);
			List<User> listUser = listUsers.getContent();
			mapUser.put("existList", listUser);//存放已有用户
			teamService.wrapUserRoleInTeam(listUser, teamId);
			mapUser.put("existTotal", listUsers.getTotalElements());
			UserListWrapUtil.setNullForPwdFromUserList(listUser);
			typeIds.clear();
			typeIds.add(Enums.TEAMREALTIONSHIP.ASK);
			Page<User> askUsers = this.userService.findUserListByGroupIdAndRel(typeIds, groupId,teamId,pageable);
			List<User> askUser = askUsers.getContent();
			UserListWrapUtil.setNullForPwdFromUserList(askUser);
			teamService.wrapUserRoleInTeam(askUser, teamId);
			mapUser.put("askList", askUser);//存放受邀用户
			mapUser.put("askTotal", askUsers.getTotalElements());
			return this.getSuccessModel(mapUser);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	    * @Title: deleteByGroupId
	    * @Description: 删除某个小组,并且将该小组下所有成员设置为无分组
	    * @param @param groupId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user wjj
		* @date 2015年8月12日 下午9:14:37
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/teamGroup/{groupId}",method = RequestMethod.DELETE)
	public Map<String, Object> deleteByGroupId(@PathVariable("groupId") long groupId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			TeamGroup tm = this.teamGroupService.delTeamGroupById(groupId);
			map.put("status", "success");
			map.put("message", tm);
			//增加动态
			this.dynamicService.addTeamDynamic(Long.parseLong(loginUserId), DYNAMIC_MODULE_TYPE.TEAMGROUP_DEL,tm.getTeamId(), new Object[]{tm});
			
		} catch (Exception e) {
			map.put("status", "failed");
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	    * @Title: changeGroup
	    * @Description:改变成员所属分组 
	    * @param @param teamId
	    * @param @param userId
	    * @param @param groupId
	    * @param @param loginUserId
	    * @param @return    参数
	    * @return Map<String,Object>    返回类型
		* @user wjj
		* @date 2015年8月13日 上午11:45:30
	    * @throws
	 */
	/*@ResponseBody
	@RequestMapping(value="/teamGroup/{teamId}/{userId}/{groupId}",method = RequestMethod.PUT)
	public Map<String, Object> changeGroup(@PathVariable("teamId") long teamId,
			@PathVariable("userId") long userId,
			@PathVariable("groupId") long groupId,
			@RequestHeader(value="loginUserId",required=true) String loginUserId){
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			this.teamMemberService.updateGroupByTeamIdAndUserId(teamId, userId, groupId);
			map.put("status", "success");
			map.put("message", "change teamGroup for user success");
		} catch (Exception e) {
			map.put("status", "failed");
			e.printStackTrace();
		}
		return map;
	}*/
	
}
