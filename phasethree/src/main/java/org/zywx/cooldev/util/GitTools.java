package org.zywx.cooldev.util;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.http.protocol.HTTP;

import net.sf.json.JSONObject;

public class GitTools {

	//武晶建 威海token    6968295951ae0037affdd6ed9f2d206702890029
	//程海军 token be1b82a2dd1f7fbe2312b26a7f69256a14ecc198
	public static void main(String[] args) throws Exception {
//		createRepo();//初始化仓库
//		addUser();//添加(注册)用户
//		grantUser();//给用户赋权限
//		deleteUser();//从git库中删除用户(去掉该用户在给定git库中的权限)
//		deleteRepo1();//删除仓库
//		getCollaborators();//获取仓库的用户列表
		createHook();//创建webhook
		
	}
	
	/*public static void cloneRepo() throws InvalidRemoteException, TransportException, GitAPIException{
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider("jingjian.wu@3g2win.com", "123456");
		Git git = Git.cloneRepository()
				  .setURI( "http://git.appcan.cn/011/563/745/x17db8.git" )
				  .setDirectory(new File("F:\\test\\bbb") )
				  .setCredentialsProvider(cp)
				  .call();
	}
	
	public static void addRepo() throws InvalidRemoteException, TransportException, GitAPIException{
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider("jingjian.wu@3g2win.com", "123456");
		Git git = Git.cloneRepository()
				  .setURI( "http://git.appcan.cn/011/563/745/x17db8.git" )
				  .setDirectory(new File("F:\\test\\bbb") )
				  .setCredentialsProvider(cp)
				  .call();
	}*/
	//jiawen  ddf0b8304884d750d6f1b6b6ccfad17207e952b2
	//wjj   3c0a9346b4423663ab6af803fa37dad0b1acc74c
	//chenghaijun be1b82a2dd1f7fbe2312b26a7f69256a14ecc198
	
	public static void createRepo() throws Exception{
		StringBuffer parameters = new StringBuffer();
		JSONObject obj = new JSONObject();
		obj.put("name", "abctest");
		obj.put("private", true);
		obj.put("has_issues", false);
		obj.put("has_wiki", false);
		System.out.println(obj.toString());
		parameters.append(obj.toString());
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "token 6968295951ae0037affdd6ed9f2d206702890029");
		String result = NewGitHttpUtil.httpPostWithJSON("http://newgit.appcan.cn/api/v3/user/repos", parameters.toString(),headers);
		System.out.println("result："+result);
	}
	
	//添加(注册)用户
	public static void addUser() throws Exception{
		StringBuffer parameters = new StringBuffer();
		JSONObject obj = new JSONObject();
		obj.put("username",  URLEncoder.encode("haijun.cheng@zymobi.com", HTTP.UTF_8));
		obj.put("nickname", URLEncoder.encode("zy1008", HTTP.UTF_8));
		System.out.println(obj.toString());
		parameters.append(obj.toString());
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "token be1b82a2dd1f7fbe2312b26a7f69256a14ecc198");
		String result = NewGitHttpUtil.httpPostWithJSON("http://newgit.appcan.cn/api/v3/users", parameters.toString(),headers);
		System.out.println("result："+result);
	}
	
	//给用户赋权限
	public static void grantUser() throws Exception{
		String gitLibrary="/zy1008/abctest/collaborators/";
		String girGrant="zy1008";
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "token be1b82a2dd1f7fbe2312b26a7f69256a14ecc198");
		String result = NewGitHttpUtil.put("http://newgit.appcan.cn/api/v3/repos"+gitLibrary+girGrant,headers);
		System.out.println("result："+result);
	}
	
	//从git库中删除用户(去掉该用户在给定git库中的权限)
	public static void deleteUser() throws Exception{
		String gitLibrary="/zy1008/chenghaijun/collaborators/";
		String girGrant="zy1008";
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "token be1b82a2dd1f7fbe2312b26a7f69256a14ecc198");
		String result = NewGitHttpUtil.delete("http://newgit.appcan.cn/api/v3/repos"+gitLibrary+girGrant,headers);
//		JSONObject obj = JSONObject.fromObject( result );
//		System.out.println(obj.getString("status"));
		System.out.println("result："+result);
	}
	
	//删除仓库
	public static void deleteRepo1() throws Exception{
		String gitLibrary="/zy1008/chenghaijun";
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "token be1b82a2dd1f7fbe2312b26a7f69256a14ecc198");
		String result = NewGitHttpUtil.delete("http://newgit.appcan.cn/api/v3/repos"+gitLibrary,headers);
		JSONObject obj = JSONObject.fromObject( result );
		System.out.println(obj.getString("status"));
		System.out.println("result："+result);
	}
	
	//获取仓库的用户列表
	public static void getCollaborators() throws Exception{
		String gitLibrary="/zy1008/11151/collaborators";
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "token be1b82a2dd1f7fbe2312b26a7f69256a14ecc198");
		String result = NewGitHttpUtil.get("http://newgit.appcan.cn/api/v3/repos"+gitLibrary,headers);
		System.out.println("result："+result);
	}
	//创建一个webHook
	public static void createHook(){
		StringBuffer parameters = new StringBuffer();
		JSONObject obj = new JSONObject();
		HashSet<String> events=new HashSet<String>();
		events.add("push");
		obj.put("url", "http://192.168.2.239:8080/phasethree/app/repo/pushed");
		obj.put("ctype", "form");
		obj.put("events", events);
		parameters.append(obj.toString());
		System.out.println("obj:"+obj.toString());
		Map<String,String> headers = new HashMap<String,String>();
		headers.put("Authorization", "token 6968295951ae0037affdd6ed9f2d206702890029");
		String result;
		try {
			result = NewGitHttpUtil.httpPostWithJSON("http://newgit.appcan.cn/api/v3/repos/driving_life/11300/hooks", parameters.toString(),headers);
			System.out.println("result："+result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

//	public static void loginUserFirst() throws Exception{
//		StringBuffer parameters = new StringBuffer();
//		JSONObject obj = new JSONObject();
//		obj.put("username", URLEncoder.encode("jiawen.liu@3g2win.com", HTTP.UTF_8));
//		obj.put("nickname", URLEncoder.encode("冰糖雪梨", HTTP.UTF_8));
//		System.out.println(obj.toString());
//		parameters.append(obj.toString());
//		Map<String,String> headers = new HashMap<String,String>();
//		headers.put("Authorization", "token 3c0a9346b4423663ab6af803fa37dad0b1acc74c");
//		String result = NewGitHttpUtil.httpPostWithJSON("http://newgit.appcan.cn/api/v3/users", parameters.toString(),headers);
//		System.out.println("result："+result);
//	}
//	
//	public static void addUserForRepo() throws Exception{
//		StringBuffer parameters = new StringBuffer();
//		parameters.append("");
//		Map<String,String> headers = new HashMap<String,String>();
//		headers.put("Authorization", "token 3c0a9346b4423663ab6af803fa37dad0b1acc74c");
//		String result = NewGitHttpUtil.httpPostWithJSON("http://newgit.appcan.cn/api/v3/repos/driving_life/forjiawen/collaborators/jiawen.liu@3g2win.com", parameters.toString(),headers);
//		System.out.println("result："+result);
//	}
//	
//	public static void deleteRepo() throws Exception{
//		Map<String,String> headers = new HashMap<String,String>();
//		headers.put("Authorization", "token 3c0a9346b4423663ab6af803fa37dad0b1acc74c");
//		String result = NewGitHttpUtil.delete("http://newgit.appcan.cn/api/v3/repos/driving_life/forjiawen", headers);
//		System.out.println("result："+result);
//	}
//	
//	public static void getUsersForRepo() throws Exception{
//		Map<String,String> headers = new HashMap<String,String>();
//		headers.put("Authorization", "token 3c0a9346b4423663ab6af803fa37dad0b1acc74c");
//		String result = NewGitHttpUtil.get("http://newgit.appcan.cn/api/v3/repos/driving_life/forjiawen/collaborators", headers);
//		System.out.println("result："+result);
//	}
}
