package org.zywx.coopman.aop;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.dao.OperationLogDao;
import org.zywx.coopman.entity.DailyLog.LogAction;
import org.zywx.coopman.entity.DailyLog.OperationLog;
import org.zywx.coopman.system.Cache;

@Aspect
@Component
public class ControllerLogAspect {
	
	@Autowired
	private OperationLogDao opertionLogDao;

	private static final Pattern ACTION_PATTERN = Pattern.compile("([/a-zA-Z]+)(\\d+)*");
	
	@Before("execution(* org.zywx.coopman.controller.*.*(..)) and (args(..,request) || args(request,..))")
	public void beforeMethod(JoinPoint point, HttpServletRequest request) {
		System.out.println("Method of " + point.getSignature().getName() + ": Before is called");
		System.out.println(request.getQueryString());
	}

	@After("execution(* org.zywx.coopman.controller.*.*(..)) and (args(..,request) || args(request,..))")
	public void afterMethod(JoinPoint point,HttpServletRequest request) {
//		System.out.println("Method of " + point.getSignature().getName() + ": After is called");
	}
	
	@AfterReturning("execution(* org.zywx.coopman.controller.*.*(..)) and (args(..,request) || args(request,..))")
	public void afterReturningMethod(JoinPoint point, HttpServletRequest request) {
		System.out.println("Method of " + point.getSignature().getName() + ": AfterReturning is called");
		OperationLog log = new OperationLog();
		HttpSession session = request.getSession();
		Object username = session.getAttribute("userName");
		log.setAccount(username!=null?username.toString():"XXX");
		log.setIp(getRemoteHost(request));
		String servletPath = request.getServletPath();
		String queryStr = request.getQueryString();
		String method = request.getMethod().toUpperCase();
		
		Matcher matcher = ACTION_PATTERN.matcher(servletPath);
		String operationLog = "";
		Long logActionId = -1L;
		if(!matcher.matches()) {
			// 请求地址不符合接口模式，转存url地址和请求类型
			operationLog = method+" "+servletPath;
		}else{
			String urlPattern  = matcher.group(1);			// url
			String targetIdStr = matcher.group(2);			// 被操作对象的ID（PUT, DELETE）
			if(targetIdStr != null) {
				urlPattern += "{id}";
			}
			for(Enums.OpertionType opt : Enums.OpertionType.values()){
				if(!method.equals(opt.getAlias()))
					continue;
				operationLog +=opt.getName();
			}
			if(queryStr!=null && !queryStr.equals("")){
				urlPattern += "?"+queryStr;
			}
//			LogAction logaction = Cache.getAction(urlPattern);
			LogAction logaction = Cache.getAllMostAction(urlPattern);
			if(logaction == null) {
				// 操作没有配置url
				operationLog = method+" "+servletPath;
			}else{
				operationLog += logaction.getName();
				logActionId = logaction.getId();
			}
		}
		log.setOperationLog(operationLog);
		log.setLogActionId(logActionId);
		log.setMethod(method);
		this.opertionLogDao.save(log);
	}

	@AfterThrowing("execution(* org.zywx.coopman.controller.*.*(..)) and (args(..,request) || args(request,..))")
	public void afterThrowingMethod(JoinPoint point,HttpServletRequest request) {
//		System.out.println("Method of " + point.getSignature().getName() + ": AfterThrowing is called");
	}
	
	
	public String getRemoteHost(javax.servlet.http.HttpServletRequest request){
	    String ip = request.getHeader("x-forwarded-for");
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getHeader("WL-Proxy-Client-IP");
	    }
	    if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
	        ip = request.getRemoteAddr();
	    }
	    return ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	}
}