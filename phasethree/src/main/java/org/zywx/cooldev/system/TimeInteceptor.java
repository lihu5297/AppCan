	/**  
     * @author jingjian.wu
     * @date 2016年2月26日 上午9:24:31
     */
    
package org.zywx.cooldev.system;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


    /**
     * 拦截controller执行时间
	 * @author jingjian.wu
	 * @date 2016年2月26日 上午9:24:31
	 */

public class TimeInteceptor implements HandlerInterceptor {

	private static final Logger logger = Logger.getLogger(TimeInteceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		long startTime = (Long)request.getAttribute("startTime");
		 
        long endTime = System.currentTimeMillis();
 
        long executeTime = endTime - startTime;
        logger.warn("[" + handler + "] executeTime : " + executeTime + "ms");
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
