package org.zywx.coopman.system;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.module.Module;

public class LoginFilter implements Filter {
	List<String> list = new ArrayList<>();

	Logger log = Logger.getLogger(this.getClass());

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		list = Arrays.asList(filterConfig.getInitParameter("filterExclude").split(";"));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 获得在下面代码中要用的request,response,session对象
		HttpServletRequest servletRequest = (HttpServletRequest) request;
		HttpServletResponse servletResponse = (HttpServletResponse) response;
		HttpSession session = servletRequest.getSession(true);

		// 获得用户请求的URI
		String path = servletRequest.getContextPath();
		String urlPattern = servletRequest.getServletPath();
		String queryStr = servletRequest.getQueryString();
		if (queryStr != null && !queryStr.equals("")) {
			urlPattern += "?" + queryStr;
		}
		log.info("当前访问path:" + urlPattern);

		// 登录/登出页面放过
		for (String str : list) {
			if (urlPattern.contains(str)) {
				chain.doFilter(servletRequest, servletResponse);
				return;
			}
		}
		
		// 没有登录，跳转登陆页面
		if (null == session.getAttribute("manager")) {
			servletResponse.sendRedirect(path + "/login");
			return;
		}

		Module logaction1 = Cache.getModule(urlPattern.substring(1));
		if (logaction1 == null) {
			// 没有配置权限的uri，一律放过
			chain.doFilter(servletRequest, servletResponse);
			return;
		}

		// 过滤访问的路径
		Manager manager = (Manager) session.getAttribute("manager");
		for (Module module : manager.getManageModule()) {
			for (Module modul : module.getChildrenModule()) {
				if (null != modul.getUrl() && urlPattern.substring(1).equals(modul.getUrl())) {
					chain.doFilter(servletRequest, servletResponse);
					return;
				}
			}

		}

		// 否则 跳转错误页面
		servletResponse.sendRedirect(path + "/error");
		return;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
