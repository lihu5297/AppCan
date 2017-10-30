package org.zywx.cooldev.controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.system.Cache;

/**
 * 
    * @ClassName: TeamAuthController
    * @Description:团队授权
    * @author wjj
    *
 */
@Controller
@RequestMapping(value="/teamAuth")
public class TeamAuthController extends BaseController {

	
	/**
	 * @throws Exception 
	 * 修改团队成员权限
	 * @param roleEnName    TEAM_MEMBER   TEAM_ADMINISTRATOR
	 * @param teamAuthId
	 * @param loginUserId
	 * @return Map<String,Object>
	 * @user jingjian.wu
	 * @date 2015年8月25日 下午2:47:32
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/{teamAuthId}",method=RequestMethod.PUT)
	public Map<String, Object> updateAuth(String roleEnName,
			@PathVariable("teamAuthId") Long teamAuthId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) throws Exception{
		
		if(null!=roleEnName && ((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).equals(roleEnName)
				||(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.MEMBER).equals(roleEnName) )){
			log.info("=========================update role for teamMember-->loginUserId:"+loginUserId+",roleEnName:"+roleEnName+",teamAuthId:"+teamAuthId);
			TeamAuth srcAuth = this.teamAuthService.findOne(teamAuthId);//被修改人的teamAuth对象
			if(Cache.getRole((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR)).getId()==srcAuth.getRoleId()){//被修改人是团队创建者,不允许修改
				return this.getFailedMap("不能够改变团队创建者的权限!");
			}
			//如果管理员操作其他管理员的话,就返回错误.
			if(Cache.getRole((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR)).getId()==srcAuth.getRoleId()){//被修改人是团队管理员
				TeamMember teamMember= this.teamMemberService.findOne(srcAuth.getMemberId());//根据被修改人查出对应的团队
				TeamMember operateMember = this.teamMemberService.findMemberByTeamIdAndUserId(teamMember.getTeamId(), loginUserId);//根据团队和 操作人获取操作人的对象
				TeamAuth operateAuth = this.teamAuthService.findByMemberIdAndDel(operateMember.getId(), DELTYPE.NORMAL);//操作人的权限
				if(Cache.getRole((ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR)).getId()==operateAuth.getRoleId()){
					//操作人也是管理员
					if(teamMember.getId().longValue()!=operateMember.getId().longValue()){
						return this.getFailedMap("对不起,您没有权限修改其他管理员的权限!");
					}
				}
			}
			TeamAuth ta= this.teamAuthService.updateAuth(teamAuthId, roleEnName);
			TeamMember tm = this.teamMemberService.findOne(ta.getMemberId());
			User u = this.userService.findUserById(tm.getUserId());
			String userName = u.getUserName()==null?u.getAccount():u.getUserName();
			//增加动态
			this.dynamicService.addTeamDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TEAMAUTH_EDIT,tm.getTeamId(), new Object[]{userName,Cache.getRole(srcAuth.getRoleId()).getCnName(),Cache.getRole(roleEnName).getCnName()});
			
			//增加通知
			Team team = this.teamService.getOne(tm.getTeamId());
			User loguser = this.userService.findUserById(loginUserId);
			this.noticeService.addNotice(loginUserId, new Long[]{tm.getUserId()}, NOTICE_MODULE_TYPE.TEAM_AUTH, new Object[]{loguser,team,Cache.getRole(roleEnName).getCnName()});
		}else{
			return this.getFailedMap("param roleEnName is not available!");
		}
		return this.getAffectMap();
	}
}
