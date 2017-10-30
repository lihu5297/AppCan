package org.zywx.coopman.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.module.Module;
import org.zywx.coopman.util.HttpTools;

/**
 * 控制器基类
 * 
 * @author yang.li
 * @date 2015-08-06
 *
 */
@Controller
@RequestMapping(value = "/")
public class LoginController extends BaseController {

	/**
	 * 
	 * @describe 登录入口	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午9:45:55	<br>
	 * @param request
	 * @param manager
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "")
	public String tologin(HttpServletRequest request, Manager manager,HttpSession session) {
//		HashMap<String,Object> mapPro = this.managerService.getValue();
//		if(mapPro.get("intergration").equals("true")){
//			new ModelAndView("SSOlogin");
//			HashMap<String,Object> map = this.managerService.getValue();
//			StringBuffer url = new StringBuffer();
//			url.append(map.get("SSOloginUrl"));
//			url.append("?service="+map.get("service"));
//			return "redirect:"+url;
//		}else{
			if(null!=session.getAttribute("userName")){
				
				return "redirect:"+getFirstPage(session);
			}else{
				return "redirect:login";
			}
				
			
//		}
	}
	
	@RequestMapping(value = "login")
	public ModelAndView toLogin(HttpServletRequest request, Manager manager,HttpSession session) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("login");
		return mv;
	}
	
	

	/**
	 * 
	 * @describe SSO登录回调	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午9:46:15	<br>
	 * @param request
	 * @return  <br>
	 * @returnType String
	 *
	 */
	@RequestMapping(value = "SSOloginCallback")
	public String toSSOloginCallback(HttpServletRequest request) {
		try {
			HashMap<String,Object> mapPro = this.managerService.getValue();
			HttpSession session = request.getSession();
			String ticket = request.getParameter("ticket");
			ticket = null != ticket ? ticket : "";
			String CASCheckUrl = mapPro.get("serviceValidate") + "?ticket=" + ticket + "&service=" + mapPro.get("service");
			String strResult = HttpTools.get(CASCheckUrl);
			HashMap<String,String> resMap = new HashMap<>();
			if (strResult != null) {
				List<org.jdom2.Element> els = HttpTools.xmlElements(strResult);
				for(org.jdom2.Element elm : els){
					resMap.put(elm.getName(),elm.getText());
				}
			}
			if(resMap.get("authenticationFailure") != null){
				return "redirect:unlogin";
			}
			//邮箱
			String username = resMap.get("username");
			//昵称
			String nickName = resMap.get("nickname");
			//用户编号
			String userid = resMap.get("userid");
			//头像
			String icon = resMap.get("user_pic");
			Manager manager = new Manager();
			manager = this.managerService.fingdByAccount(username);
			if(manager==null){
				manager = new Manager();
				manager.setAccount(username);
				manager.setEmail(username);
				manager.setUserName(nickName);
				manager.setIcon(icon);
				this.managerService.saveManager(manager);
			}
			
			session.setAttribute("userName", username);
//			session.setAttribute("nickName", nickName);
			session.setAttribute("userId", userid);
			session.setAttribute("icon", icon);
			List<Module> modules =  this.moduleService.getModule(manager.getId());
			manager.setManageModule(modules);
			session.setAttribute("manager", manager);
			return "redirect:index";
		} catch (org.apache.http.ParseException e) {
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 
	 * @describe 非EMM登录	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午9:52:52	<br>
	 * @param request
	 * @param manager
	 * @param session
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "index")
	public String login(HttpServletRequest request, Manager manager, HttpSession session) {
//		ModelAndView model = new ModelAndView();
		if (null!=session.getAttribute("userName")){
//			model.setViewName("user/index");
			return "redirect:"+getFirstPage(session);
		} else{
			Manager mana = this.managerService.fingdByAccount(manager.getAccount());
			if(null!=mana &&manager.getPassword().equals(mana.getPassword())){
				session.setAttribute("account", mana.getAccount());
				session.setAttribute("userName", mana.getUserName());
				session.setAttribute("userId", mana.getId());
				session.setAttribute("icon", mana.getIcon());

				List<Module> modules =  this.moduleService.getModule(mana.getId());
				mana.setManageModule(modules);
				
				session.setAttribute("manager", mana);
				
//				model.setViewName("user/index");
				return "redirect:"+getFirstPage(session);
			}else{
//				model.setViewName("login");
//				model.addObject("err", "用户名或密码错误");
				return "redirect:unlogin";
			}
		}
	}

	/**
	 * 
	 * @describe 非EMM登出/EMM登出不凑效	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午10:35:52	<br>
	 * @param request
	 * @param session
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "logout")
	public String logout(HttpServletRequest request,HttpSession session){
		
		session.invalidate();
		/*session.removeAttribute("userName");
		session.removeAttribute("account");
		session.removeAttribute("userId");
		session.removeAttribute("icon");

		session.removeAttribute("manager");*/
		
		return "redirect:login";
		
		
	}
	
	/**
	 * 
	 * @describe 错误页	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午10:35:52	<br>
	 * @param request
	 * @param session
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "error")
	public ModelAndView error(HttpServletRequest request,HttpSession session){
		
		ModelAndView model = new ModelAndView();
		model.setViewName("error");
		return model;
		
	}
	
	/**
	 * 
	 * @describe 登录失败	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月8日 上午10:35:52	<br>
	 * @param request
	 * @param session
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "unlogin")
	public ModelAndView unlogin(HttpServletRequest request,HttpSession session){
		
		ModelAndView model = new ModelAndView();
		session.removeAttribute("userName");
		model.setViewName("unlogin");
		return model;
		
	}
	
	public String getFirstPage(HttpSession session){
		Manager manager= (Manager) session.getAttribute("manager");
		if(manager.getManageModule()!=null&&!manager.getManageModule().isEmpty()){
			List<Module> modules = manager.getManageModule();
			for(Module module : modules){
				List<Module> childreModules = module.getChildrenModule();
				for(Module modul : childreModules){
					return modul.getUrl();
				}
			}
		}else{
			session.removeAttribute("userName");
			return " error";
		}
		session.removeAttribute("userName");
		return " error";
	}
	
}
