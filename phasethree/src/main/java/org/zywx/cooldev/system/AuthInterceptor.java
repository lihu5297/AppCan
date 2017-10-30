package org.zywx.cooldev.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.entity.auth.Action;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.service.AuthService;
import org.zywx.cooldev.service.ProjectService;

public class AuthInterceptor extends HandlerInterceptorAdapter {
	private static final Pattern ACTION_PATTERN = Pattern.compile("([/a-zA-Z]+)(\\d+)*");

	private static Logger log = Logger.getLogger(AuthInterceptor.class.getName());
	@Autowired
	private ProjectService projectService;

	@Override
    public boolean preHandle(HttpServletRequest request,    
            HttpServletResponse response, Object handler) throws Exception {
		
		log.info("Method of AuthInterceptor : preHandle is called");

		String servletPath = request.getServletPath();
		String method = request.getMethod().toUpperCase();
		
		Matcher matcher = ACTION_PATTERN.matcher(servletPath);
		if(!matcher.matches()) {
			// 请求地址不符合接口模式，返回错误信息
			this.printFailedJson("not found this interface", response);
			return false;
		}

		String urlPattern  = method + matcher.group(1);	// 和请求方法合并到一起
		String targetIdStr = matcher.group(2);			// 被操作对象的ID（PUT, DELETE）
		if(targetIdStr != null) {
			urlPattern += "{id}";
		}

		Action action = Cache.getAction(urlPattern);
		if(action == null) {
			// 接口不在鉴权范围之内，直接通过
			return true;
		}
		
		String loginUserIdStr = request.getHeader("loginUserId");
		long loginUserId = -1;
		if(loginUserIdStr != null) {
			loginUserId = Long.parseLong(loginUserIdStr);
		}
		if(loginUserId == -1) {
			// 未找到登录用户ID
			this.printFailedJson("user not login", response);
			return false;	
		}
		
		// 判定权限关联的对象ID
		String relatedIdStr = null;
		if("POST".equals(method)) {
			String relatedkey = action.getAuthRelatedType().name().toLowerCase() + "Id";
			relatedIdStr = request.getParameter(relatedkey);
			
		} else if( "PUT".equals(method) || "DELETE".equals(method)) {
			// PUT,DELETE 权限关联对象与被操作对象一致
			relatedIdStr = targetIdStr;
		}
		long relatedId = -1;
		if(relatedIdStr != null) {
			relatedId = Long.parseLong(relatedIdStr);
		}
		
		AuthService authService = this.getEntityAuthService(action.getAuthRelatedType());
		if(authService == null) {
			this.printFailedJson("not found authService", response);
			return false;
		}
		
		List<Permission> permissionList = authService.getPermissionList(loginUserId, relatedId);
		HashSet<String> ownedPermissions = new HashSet<>();
		for(Permission p : permissionList) {
			ownedPermissions.add(p.getEnName());
		}
		
		// 判定请求需要的许可
		boolean doNext = true;
		List<String> permissionNames = getPermissionsRequired(request, action);
		for(String enName : permissionNames) {
			if( ! ownedPermissions.contains(enName) ) {
				doNext = false;
				break;
			}
		}
				
		if(!doNext) {
			// 鉴权失败
			this.printFailedJson("without permission", response);
		
		}
		return doNext;

	}
	
	
	
	
	private List<String> getPermissionsRequired(HttpServletRequest request, Action action) {
		List<String> permissions = new ArrayList<>();
		String method = request.getMethod();
		if( "POST".equals(method) || "DELETE".equals(method) ) {
			String reqPermission = method.toUpperCase() + "_" + action.getAuthRelatedType().name();
			if( !action.getAuthRelatedType().equals(action.getTargetType()) ) {
				reqPermission += "_" + action.getTargetType();
			}
			permissions.add(reqPermission);
		
		} else if("PUT".equals(method)) {
			Enumeration<?> e = request.getParameterNames();
			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String reqPermission = method.toUpperCase() + "_" + key.toUpperCase();
				permissions.add(reqPermission);
			}

		}	
		return permissions;
	}
	
	private AuthService getEntityAuthService(ENTITY_TYPE entityType) {
		switch(entityType) {
			case PROJECT:
				return projectService;
			case PROCESS:

			case TASK:

			case TEAM:

			case TOPIC:

			default:
				return null;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

		log.info("Method of AuthInterceptor : postHandle is called");
		log.info(modelAndView);
	}
	
	
	private void printFailedJson(String message, ServletResponse servletResponse) throws IOException {
		PrintWriter pw = servletResponse.getWriter();
		pw.println("{\"status\":\"failed\", \"message\":\"" + message + "\"}");
		pw.close();
	}

}
