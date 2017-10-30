package org.zywx.cooldev.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class URLDecoderPutParameter {

	/**
	 * 
	 * @describe put方法提交 辅助获取参数	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月12日 上午10:30:48	<br>
	 * @param request
	 * @param response
	 * @return
	 *
	 */
	public static HashMap<String, String> getPutParameter(HttpServletRequest request,HttpServletResponse response){
		HashMap<String, String> map = new HashMap<String, String>();
		String queryString = "";
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
			    queryString += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		queryString = URLDecoder.decode(queryString);
		String[] str = queryString.split("&");
		if(str.length>0 && str[0]!=""){
			for(String stri : str){
				map.put(stri.split("=")[0], stri.split("=")[1]);
			}
		}
		return map;
	}
}
