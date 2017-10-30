package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.builder.Setting;
import org.zywx.cooldev.service.SettingService;
import org.zywx.cooldev.system.Cache;
@Controller
@RequestMapping(value="/setting")
public class SettingController extends BaseController{

	@Autowired
	private SettingService settingService;
	
	@RequestMapping(value="",method=RequestMethod.GET)
	public ModelAndView getSetting(){
		Setting setting = this.settingService.getSetting();
		return this.getSuccessModel(setting);
	}
	
	//http://zymobitest.appcan.cn/bookres/springMVC.pdf
	@ResponseBody
	@RequestMapping(value = "/test")
	public Object updateAuthorize(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session1 = request.getSession();
		ServletContext Context = session1.getServletContext();
		// 这里面传递的是项目a的虚拟路径
		ServletContext Context1 = Context.getContext("/coopMan");
		System.out.println(Context1);
		HttpSession session2 = (HttpSession) Context1.getAttribute("session");
		log.info("base传过来的endTime为:" + session2.getAttribute("endTime"));
		return session2.getAttribute("endTime");
	}
	/**
	 * 测试java配合nginx授权下载
	 * @param httpResponse
	 * @param type
	 * @return
	 */
	@RequestMapping(value="/abc",method=RequestMethod.GET)
	public ModelAndView getSetting123(HttpServletResponse httpResponse,String type){
		if("123".equals(type)){
			httpResponse.setHeader("Content-Disposition", "attachment; filename=\"springMVC.pdf\"");
			httpResponse.setHeader("Content-Type","application/octet-stream");
			httpResponse.setHeader("X-Accel-Redirect","/bookres/"+"springMVC.pdf");
			return null;
		}else{
			return this.getFailedModel("无权操作");
		}
	}
	
	/**
	 * 测试获取redis中权限信息
	 * @param roleId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/testRole/{roleId}",method=RequestMethod.GET)
	public Map<String,Object> getPermissionForRole(@PathVariable(value="roleId") Long roleId){
		try{
			Role role = Cache.getRole(roleId);
			List<String> pMap = new ArrayList<String>();
			List<Permission> perms = role.getPermissions();
			for(Permission perm : perms){
				pMap.add(perm.getEnName());
			}
			return this.getSuccessMap(pMap);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
}
