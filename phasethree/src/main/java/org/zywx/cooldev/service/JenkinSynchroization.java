package org.zywx.cooldev.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.util.HttpUtil;

@Service
public class JenkinSynchroization {

	@Value("${jenkinUrl}")
	String jenkinUrl;
	
	public String synchroizationUser(Map<String,String> map){
		try{
			String jenkinUserUrl = jenkinUrl + "securityRealm/createAccount";
			return HttpUtil.httpPost(jenkinUserUrl, map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public String checkProjectExists (String projectName){
		try{
			String jenkinUserUrl = jenkinUrl + "/view/all/checkJobName?value="+projectName;
			return HttpUtil.httpGet(jenkinUserUrl);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public String synchroizationProject(Map<String,String> map){
		try{
			String jenkinUserUrl = jenkinUrl + "view/all/createItem";
			return HttpUtil.httpPost(jenkinUserUrl, map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	
	public static void main(String[] args) {
		
		String jenkinUrl = "http://192.168.1.211:8080/";
		Map<String,String> map = new HashMap<String,String>();
		String retStr = "";
		/**
		map.put("username", "zhangsan");
		map.put("password1", "123456");
		map.put("password2", "1234567");
		map.put("fullname", "zhangsan");
		map.put("email", "zhangsan@qq.com");
		map.put("Submit", "注册");
		try{
			String jenkinUserUrl = jenkinUrl + "securityRealm/createAccount";
			retStr = HttpUtil.httpPost(jenkinUserUrl, map);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(retStr.contains("<div id=\"main-panel\"><a name=\"skip2content\"></a><h1>操作成功")){
			System.out.println("操作成功");
		}else if(retStr.contains("<div class=\"error\" style=\"margin-bottom:1em\">")){
			System.out.println(retStr.split("<div class=\"error\" style=\"margin-bottom:1em\">")[1].split("</div><table><tr><td>用户名:</td><td><input id=\"username\"")[0]);
		}
		**/
		try{
			String jenkinUserUrl = jenkinUrl + "view/all/checkJobName?value=dss";
			retStr = HttpUtil.httpPost(jenkinUserUrl, map);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(retStr);
//		String s = "<div class=\"error\" style=\"margin-bottom:1em\">User name is already taken</div><table><tr><td>";
//		String[] ss = s.split("<div class=\"error\" style=\"margin-bottom:1em\">");
//		System.out.println(ss[1].split("</div>")[0]);
	}
}
