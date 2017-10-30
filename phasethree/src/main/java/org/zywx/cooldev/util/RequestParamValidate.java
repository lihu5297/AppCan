package org.zywx.cooldev.util;

import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.ant.util.VectorSet;


public class RequestParamValidate {

	/**
	 * 
	 * @describe request请求参数判空校验	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月13日 下午8:54:39	<br>
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 *
	 */
	public static HashMap<Object, Object> ValidatePrama(HttpServletRequest request,HttpServletResponse response,String[] params){
		HashMap<Object, Object> map = new HashMap<>();
		
		@SuppressWarnings("unchecked")
		Enumeration<String> paras = request.getParameterNames();
		int a = 0;
		while(paras.hasMoreElements() && a < params.length ){
			String res = request.getParameter(params[a]);
			
			if(null == res || "".equals(res)){
				map.put("status", "failed");
				map.put("message", params[a]+"参数为空");
				return map;
			}
			a++;
		}
		if(!paras.hasMoreElements()){
			map.put("status", "failed");
			map.put("message", "参数列表为空");
			return map;
		}
		return map;
	}
}
