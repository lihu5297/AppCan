package org.zywx.cooldev.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.appdo.facade.mam.entity.application.MdmAplctiongrp;
import org.zywx.appdo.facade.mam.service.appGroup.MdmAplctiongrpFacade;
import org.zywx.cooldev.entity.Advice;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.service.AdviceService;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.emm.TokenUtilProduct;

import net.sf.json.JSONObject;


/**
 * 意见反馈相关处理控制器
 * @author yang.li
 * @date 2015-09-18
 *
 */
@Controller
@RequestMapping(value = "/advice")
public class AdviceController extends BaseController {
	@Autowired
	private AdviceService adviceService;

	@Value("${statistic.url}")
	private String staisticUrl;
	
	@Value("${serviceFlag}")
	private String source;
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> addAdvice(
			Advice advice,
			@RequestHeader(value="loginUserId") long loginUserId) {
		try {
			advice.setUserId(loginUserId);
			this.adviceService.addAdvice(advice);
			User user = this.userService.findUserById(loginUserId);
			if(null!=user){
				final String param = String.format("account=%s&userName=%s&content=%s&source=%s", user.getAccount(),user.getUserName(),advice.getContent(),source); 
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						//第一次发送
						try {
							String result1 = AdviceController.sendPost(staisticUrl,param);
							log.info("post1 user advice to "+staisticUrl+" ：result  = "+result1);
							JSONObject obj = JSONObject.fromObject(result1);
							if(obj.get("status").equals("success")){
								log.info("post1 user advice to "+staisticUrl+" ：success = " + param);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							log.info("post1 user advice to "+staisticUrl+" ： 异常 =Exception:"+e1.getMessage());
							//第二次发送
							try {
								String result2 = AdviceController.sendPost(staisticUrl,param);
								log.info("post2 user advice to "+staisticUrl+" ：result  = "+result2);
								JSONObject obj = JSONObject.fromObject(result2);
								if(obj.get("status").equals("success")){
									log.info("post2 user advice to "+staisticUrl+" ：success  = "+ param);
								}
							} catch (Exception e2) {
								
								e2.printStackTrace();
								log.info("post2 user advice to "+staisticUrl+" ： 异常 =Exception:"+e2.getMessage());
								//第三次发送
								try {
									String result3  = AdviceController.sendPost(staisticUrl,param);
									log.info("post3 user advice to "+staisticUrl+" ：result  = "+result3);
									JSONObject obj = JSONObject.fromObject(result3);
									if(obj.get("status").equals("success")){
										log.info("post3 user advice to "+staisticUrl+" ：success  = "+ param);
									}
								} catch (Exception e3) {
									e3.printStackTrace();
									log.info("post3 user advice to "+staisticUrl+" ： 异常 =Exception:"+e3.getMessage());
								}
							}
							
						}
					}
				});
				thread.start();
			
			
			}
			return this.getSuccessMap(advice);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/list",method=RequestMethod.GET)
	public List<Map<String, Object>> getAdvice(){
		List<Map<String, Object>> list = this.adviceService.findByPage();
		return list;
		
	}
	
	@Autowired(required=false)
	private MdmAplctiongrpFacade mdmAplctiongrpFacade;
	
	@ResponseBody
	@RequestMapping(value="/test", method=RequestMethod.POST)
	public Map<String, Object> test() {
		
		String[] params = new String[]{"205","dev"};
		String token = TokenUtilProduct.getToken("8357513f", params);
		String loginName = "xtkf";
		MdmAplctiongrp group = new MdmAplctiongrp();
		group.setName("白名单组");
		group.setTenantId(new Long(205));
		group.setCreateUser(loginName);
		
		mdmAplctiongrpFacade.create(token, group);
		
		
		List<MdmAplctiongrp> list = mdmAplctiongrpFacade.getByCreateUser(token, loginName);
		

		return this.getSuccessMap(list);
	}

	public static void main(String args[]){
		final List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("account","794034833@qq.com"));
		parameters.add(new BasicNameValuePair("userName","liujiexiong"));
		parameters.add(new BasicNameValuePair("content","其实没什么意见"));
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//第一次发送
				try {
					String result1 = HttpUtil.httpPost("http://siteadm.appcan.cn/Daemon/coopadvice/add.json", parameters);
					System.out.println("post1 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ：result  = "+result1);
					JSONObject obj = JSONObject.fromObject(result1);
					if(obj.get("status").equals("success")){
						System.out.println("post1 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ：success  = "+parameters.get(2).getValue());
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					System.out.println("post1 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ： 异常 =Exception:"+e1.getMessage());
					//第二次发送
					try {
						String result2 = HttpUtil.httpPost("http://siteadm.appcan.cn/Daemon/coopadvice/add.json", parameters);
						System.out.println("post2 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ：result  = "+result2);
						JSONObject obj = JSONObject.fromObject(result2);
						if(obj.get("status").equals("success")){
							System.out.println("post2 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ：success  = "+parameters.get(2).getValue());
						}
					} catch (Exception e2) {
						
						e2.printStackTrace();
						System.out.println("post2 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ： 异常 =Exception:"+e2.getMessage());
						//第三次发送
						try {
							String result3  = HttpUtil.httpPost("http://siteadm.appcan.cn/Daemon/coopadvice/add.json", parameters);
							System.out.println("post3 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ：result  = "+result3);
							JSONObject obj = JSONObject.fromObject(result3);
							if(obj.get("status").equals("success")){
								System.out.println("post3 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ：success  = "+parameters.get(2).getValue());
							}
						} catch (Exception e3) {
							e3.printStackTrace();
							System.out.println("post3 user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ： 异常 =Exception:"+e3.getMessage());
						}
					}
					
				}
				System.out.println("post user advice to "+"http://siteadm.appcan.cn/Daemon/coopadvice/add.json"+" ：success  = "+parameters.get(2).getValue());
			}
		});
		thread.start();
		
		AdviceController advice =new AdviceController();
		String result = advice.sendPost("http://siteadm.appcan.cn/Daemon/coopadvice/add.json", "account=794034833@qq.com&userName=liujiexiong&content=其实没什么意见");
		System.out.println(result);
	}
	
	/**
	 * 向指定URL发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是name1=value1&name2=value2的形式。
	 * @return URL所代表远程资源的响应
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result +=  line;
			}
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
