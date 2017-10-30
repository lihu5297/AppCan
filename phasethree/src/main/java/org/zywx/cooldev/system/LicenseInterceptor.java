/**  
 * @author jingjian.wu
 * @date 2016年2月26日 上午9:24:31
 */

package org.zywx.cooldev.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.controller.BaseController;

/**
 * 拦截controller 判断License文件
 * 
 * @author jingjian.wu
 * @date 2016年2月26日 上午9:24:31
 */

public class LicenseInterceptor extends BaseController implements
		HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		// 获取后台中的endTime
		String endTime = this.coopManSession(request, response);
		log.info("获取后台传送过来的 endTime为:"+ endTime);
		// 获取Cache中endTime
		String endTimeMap = Cache.getEndTimeMap("endTime");
		log.info("获取Cache中endTime :"+ endTimeMap);
		if(endTimeMap==null&&endTime==null){
			log.info("license文件结束时间为null");
			this.printFailedJson("license文件读取失败", response);
			return false;
		}
		if(endTimeMap==null&&endTime!=null){
			Cache.addEndTime(endTime);
			endTimeMap = endTime;
		}
		if (endTime != null && !endTime.equals(endTimeMap)) {
			Cache.addEndTime(endTime);
			endTimeMap = endTime;
		}
		// 如果不相等 说明后台setting文件有修改,重新刷新cache

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(endTimeMap);
		if (date.before(new Date())) {
			// 停止tomcat启动
			try {
				log.info("license過期,強制停止tomcat服務");
				this.printFailedJson("license過期,強制停止tomcat服務", response);
				// System.exit(0);
				return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}

	private void printFailedJson(String message, ServletResponse servletResponse)
			throws IOException {
		PrintWriter pw = servletResponse.getWriter();
		pw.println("{\"status\":\"failed\", \"message\":\"" + message + "\"}");
		pw.close();
	}

	/**
	 * 获取后台传送过来的session-endTime
	 * 
	 * @throws IOException
	 */
	private String coopManSession(HttpServletRequest request,
			HttpServletResponse response)  {
		try {
			HttpSession session1 = request.getSession();
			ServletContext Context = session1.getServletContext();
			// 这里面传递的是项目a的虚拟路径
			ServletContext Context1 = Context.getContext("/coopMan");
			if (Context1 == null) {
				log.info("获取后台传送过来的session : 无Context");
				return null;
			}
			HttpSession session2 = (HttpSession) Context1.getAttribute("session");
			if (session2 == null) {
				log.info("获取后台传送过来的session : 无Session");
				return null;
			}

			log.info("获取后台传送过来的session-endTime为:"
					+ session2.getAttribute("endTime"));
			String endTime = (String) session2.getAttribute("endTime");
			if (endTime == null) {
				log.info("获取后台传送过来的session-endTime : endTime获取不到");
				return null;
			}
			return endTime;
		} catch (Exception e) {
			log.error("获取后台传送过来的session-endTime异常 :"+e.getMessage());
			return null;
		}
	}
}
