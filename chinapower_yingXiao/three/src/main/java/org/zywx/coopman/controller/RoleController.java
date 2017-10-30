package org.zywx.coopman.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums.ENTITY_TYPE;
import org.zywx.coopman.commons.Enums.ROLE_TYPE;
import org.zywx.coopman.entity.auth.PermissionType;
import org.zywx.coopman.entity.auth.Role;
import org.zywx.coopman.util.HttpTools;


    

@Controller
@RequestMapping(value="/role")
public class RoleController extends BaseController {
//	@Value("${xietongreloadRoleURL}")
//	private String xietongreloadRoleURL;
	/**
	 * 查询角色列表
	 * @user jingjian.wu
	 * @date 2015年9月18日 上午9:56:02
	 */
	@RequestMapping(value="/list")
	public ModelAndView findRoleList(HttpServletRequest request){
		ModelAndView modeAndView = new ModelAndView();
		try {
			List<Role> list = this.roleService.findRoleList();
			modeAndView.addObject("list", list);
			modeAndView.setViewName("role/index");
		}  catch (Exception e) {
			e.printStackTrace();
		}
		return modeAndView;
	}
	
	//查看角色详情
	@RequestMapping(value="/detailInfo/{roleId}")
	public ModelAndView findRoleList(HttpServletRequest request,@PathVariable(value="roleId")long roleId){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			Role role = this.roleService.findRoleDetail(roleId);
			map.put("role", role);
			map.put("flag", "1");
		}  catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("role/index",map);
	}
	
	
	/**
	 * 修改角色权限
	 * @user jingjian.wu
	 * @date 2015年9月18日 上午9:55:46
	 */
	@RequestMapping(value="/updateRole")
	public ModelAndView updateRole(HttpServletRequest request,Long roleId,String permissionIds){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if(null==roleId){
				map.put("flag", "0");
			}else{
				Map<String,String> t = this.roleService.updateRoleAuth(roleId,permissionIds);
				map.put("flag", "1");
				
				//String getReturn = HttpTools.sendGet(t.get("xietongreloadRoleURL"), "");
				//log.info(t.get("xietongreloadRoleURL")+" reload cache :"+getReturn);
				this.roleService.updateRedisRole(roleId);
				String master = t.get("master");
				String branch = t.get("branch");
				if(null!=master && !"".equals(master)){
					String params = "roleId="+roleId+"&master="+master+"&branch="+branch;
					log.info("updateRole -- updateRole ---params-->"+params);
					String updateGitAuthResult = HttpTools.sendGet(t.get("xietongupdateGitAuthURL"), params);
					log.info(t.get("xietongupdateGitAuthURL")+"---returnResult--->"+updateGitAuthResult);
				}
			}
		}  catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("role/index",map);
	}
	
	
	/**
	 * 列出所有的权限类型(类型下包括权限)
	 * @user jingjian.wu
	 * @date 2015年9月18日 上午9:56:40
	 */
	@RequestMapping(value="/permissionTypes")
	public ModelAndView listPermissionTypes(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			map.put("flag", "1");
			List<PermissionType> listType = this.permissionTypeService.findPermissionTypes();
			map.put("listType", listType);
		}  catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("role/index",map);
	}
	
	
	/**
	 * 添加自定义角色
	 * @user jingjian.wu
	 * @date 2015年9月18日 上午9:55:38
	 */
	@RequestMapping(value="/custom")
	public ModelAndView addRole(HttpServletRequest request,Role role,String permissionIds){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if(null==role || StringUtils.isBlank(role.getEnName()) || StringUtils.isBlank(role.getCnName())){
				map.put("flag", "0");
			}else{
				Map<String,Object> t = this.roleService.addCustomRole(role,permissionIds);
				map.put("flag", "1");
				String getReturn = HttpTools.sendGet(t.get("xietongreloadRoleURL").toString(), "");
				log.info(t.get("xietongreloadRoleURL")+" reload cache :"+getReturn);
			}
		}  catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("role/index",map);
	}
	
	/**
	 * 删除自定义角色
	 * @user jingjian.wu
	 * @date 2015年9月18日 上午10:17:55
	 */
	@RequestMapping(value="/delCustomRole/{roleId}")
	public ModelAndView delCustomRole(HttpServletRequest request,@PathVariable(value="roleId")long roleId){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			String reloadRoleUrl = this.roleService.deleteCustomRole(roleId);
			String getReturn = HttpTools.sendGet(reloadRoleUrl, "");
			log.info(reloadRoleUrl+" reload cache :"+getReturn);
			map.put("flag", "1");
		}  catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("role/index",map);
	}
}
