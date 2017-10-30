/**  
     * @author jingjian.wu
     * @date 2015年9月10日 下午5:58:44
     */
    
package org.zywx.coopman.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.appdo.facade.user.entity.organization.Organization;
import org.zywx.appdo.facade.user.entity.organization.Personnel;
import org.zywx.appdo.facade.user.service.organization.OrganizationFacade;
import org.zywx.appdo.facade.user.service.organization.PersonnelFacade;
import org.zywx.coopman.commons.Enums.EMAIL_STATUS;
import org.zywx.coopman.commons.Enums.USER_STATUS;
import org.zywx.coopman.commons.Enums.UserGender;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.entity.User;
import org.zywx.coopman.entity.auth.Permission;
import org.zywx.coopman.entity.filialeInfo.FilialeInfo;
import org.zywx.coopman.service.FilialeInfoService;
import org.zywx.coopman.service.VideoService;
import org.zywx.coopman.system.Cache;
import org.zywx.coopman.system.InitBean;
import org.zywx.coopman.util.HttpTools;
import org.zywx.coopman.util.HttpUtil;
import org.zywx.coopman.util.MD5Util;
import org.zywx.coopman.util.mail.MailBean;
import org.zywx.coopman.util.mail.MailUtil;
import org.zywx.coopman.util.mail.base.MailSenderInfo;
import org.zywx.coopman.util.mail.base.SendMailTools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


    /**
	 * @author jingjian.wu
	 * @date 2015年9月10日 下午5:58:44
	 */

@Controller
@RequestMapping(value="/user")
public class UserController extends BaseController {
	
	@Autowired(required=false)
	private PersonnelFacade personelFacade;
	
	@Autowired(required=false)
	private OrganizationFacade organizationFacade;
	
	@Autowired
	private SendMailTools sendMailTool;
	@Autowired
	protected FilialeInfoService filialeInfoService;
	
	//导入excel时候,excel默认的存放目录
	@Value("${excelImportPath}")
	private String excelPath;
	
	//下载excel模板的地址
	@Value("${excelModulePath}")
	private String excelModulePath;
	
	@Value("${headImgDir}")
	private String headImgDir;
	
	
	@Value("${EMM.intergration}")
	private String integration;
	
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	@Value("${mail.subject}")
	private String mailSubject;
	
	@Value("${emm3Url}")
	private String emm3Url;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	
	@Autowired
	private MailUtil mailUtil;
	@Autowired
	private JdbcTemplate jdbcTpl;
	/**
	 * 查询用户列表
	 * @user jingjian.wu
	 * @date 2015年9月25日 下午4:33:30
	 */
	@RequestMapping(value="/list")
	public ModelAndView findUserList(HttpServletRequest request, HttpSession session,@RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize,
			String sortType,String search,@RequestParam(value="status",required=false)USER_STATUS status){
		ModelAndView modeAndView = new ModelAndView();
		Long adminId = (Long)session.getAttribute("userId");
		if(adminId == null || adminId < 1){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("flag", "0");
			map.put("msg", "登录失效，请重新登录");
			return new ModelAndView("user/index",map);
		}
		Manager man = managerService.findById(adminId);
		if(man == null){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("flag", "0");
			map.put("msg", "登录管理员不存在");
			return new ModelAndView("user/index",map);
		}
		if (pageNo == null){
			pageNo = 1;
		}
		if (pageSize == null){
			pageSize = 10;
		}
		Page<User> list = null;
		FilialeInfo fi = filialeInfoService.findById(man.getFilialeId());
		if(fi == null){
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("flag", "0");
			map.put("msg", "登录管理员所属网省不存在");
			return new ModelAndView("user/index",map);
		}
		//总部管理员查看所有用户信息
		if(fi.getId() == 1){
			List filialeIdList = new ArrayList();
			if(null!=search&&null!=search.trim()){
				//根据所属单位(分公司名字)模糊查询分公司id列表
				filialeIdList = filialeInfoService.findLikeFilialeName(search);
			}
			if(null!=status && status.equals(USER_STATUS.AUTHSTR)){//AUTHSTR 待审核用户
				list = this.userService.findUserListByAuth(pageNo, pageSize, sortType,search,status,filialeIdList);
				modeAndView.setViewName("user/authstr");//待审核用户进行审核页面
			}else{
				if(null!=status){//NORMAL 正常用户 ;FORBIDDEN 禁用用户
					list = this.userService.findUserListByAuth(pageNo, pageSize, sortType,search,status,filialeIdList);
				}else{//全部
					list = this.userService.findUserList(pageNo, pageSize, sortType,search,filialeIdList);
				}
				modeAndView.setViewName("user/index");//协同用户列表页面
			}
		}else{
			//非总部人员，查看自己网省下面的人员
			if(null!=status && status.equals(USER_STATUS.AUTHSTR)){//AUTHSTR 待审核用户
				list = this.userService.findUserListByAuth(man.getFilialeId(),pageNo, pageSize, sortType,search,status);
				modeAndView.setViewName("user/authstr");
			}else{
				if(null!=status){//NORMAL 正常用户 ;FORBIDDEN 禁用用户
					list = this.userService.findUserListByAuth(man.getFilialeId(),pageNo, pageSize, sortType,search,status);
				}else{//全部
					list = this.userService.findUserList(man.getFilialeId(),pageNo, pageSize, sortType,search);
				}
				modeAndView.setViewName("user/index");
			}
		}
		List<Permission> perList = roleService.findUserInitPermission();
		Map<Long, String> filialeMap = filialeInfoService.findFilialeInfoAllMap();
		for(User u : list.getContent()){
			u.setFilialeName(filialeMap.get(u.getFilialeId()));
		}
		modeAndView.addObject("list", list);
		modeAndView.addObject("hasNext", list.hasNext());
		modeAndView.addObject("hasPrevious", list.hasPrevious());
		modeAndView.addObject("search",search);
		modeAndView.addObject("integration",integration);
		modeAndView.addObject("excelModulePath",excelModulePath);
		modeAndView.addObject("emailStatus",Cache.getSetting("SETTING").getEmailServerStatus());
		modeAndView.addObject("serviceFlag",serviceFlag);
		modeAndView.addObject("userPermission",perList);
		modeAndView.addObject("userStatus",status == null ? "" : status);
		return modeAndView;
	}
	
	/**
	 * 导出所有用户到excel
	 * @user jingjian.wu
	 * @date 2015年9月25日 下午4:33:47
	 */
	@ResponseBody
	@RequestMapping(value="/export")
	public void  exportList(HttpServletResponse response){
		try {
			HSSFWorkbook wb=userService.export();
			response.setContentType("application/x-msdownload;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename="+"userInfo"+".xls" );
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//创建，修改用户信息
	@ResponseBody
	@RequestMapping(value="/create",method={RequestMethod.POST})
	public ModelAndView  createUser(HttpServletRequest request,User user){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			if(!user.getEmail().matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")){
				map.put("flag", "0");
	    		map.put("msg", "邮箱不合法.");
				return  new ModelAndView("user/index",map);
            }
			if(StringUtils.isNotBlank(user.getCellphone())){
				if(!user.getCellphone().matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
					map.put("flag", "0");
		    		map.put("msg", "手机号格式错误.");
					return  new ModelAndView("user/index",map);
	            }
			}
			if(user.getFilialeId() < 1){
				map.put("flag", "0");
	    		map.put("msg", "所属单位不能为空");
				return  new ModelAndView("user/index",map);
			}
			//姓名不可以为空
			if(null==user.getUserName().trim()||"".equals(user.getUserName().trim())){
				map.put("flag", "0");
	    		map.put("msg", "姓名不可以为空");
				return  new ModelAndView("user/index",map);
			}
			User userOld = null;
			if(null != user.getId() && user.getId()!=-1 && user.getId()!=0){//修改用户
				userOld = this.userService.findOne(user.getId());
				if(user.getIcon()==null){
					user.setIcon(userOld.getIcon());
				}
				user.setPassword(userOld.getPassword());
				user.setReceiveMail(userOld.getReceiveMail());
				user.setStatus(userOld.getStatus());
				user.setType(userOld.getType());
				
				user = userService.save(user);
				jdbcTpl.update("update T_RESOURCES set userName= ? where userId = ?", new Object[]{user.getUserName(),user.getId()});
				map.put("flag", "1");
			}else{//新增用户
				if(Cache.getSetting("SETTING").getEmailServerStatus()==EMAIL_STATUS.CLOSE){
					if(StringUtils.isBlank(user.getPassword())){
						//邮件服务关闭,需要管理员手动输入密码,但是密码为空
						map.put("flag", "0");
			    		map.put("msg", "密码不能为空");
						return  new ModelAndView("user/index",map);
					}
				}
				//邮箱服务器开启
				String randomCode =(null==user.getPassword()||"".equals(user.getPassword()))?MD5Util.generateCode():user.getPassword();
		    	String md5Pass = MD5Util.MD5(randomCode).toLowerCase();
		    	user.setPassword(md5Pass);
//		    	判断用户名，邮箱是否有重复
		    	if(userService.existByProperty("account", user.getAccount()) || userService.existByProperty("email", user.getEmail())){
		    		map.put("flag", "0");
		    		map.put("msg", "邮箱或者用户名已经存在.");
		    	}else{
		    		user.setIcon(headImgDir);
		    		user = userService.save(user);
		    		if(serviceFlag.equals("enterpriseEmm3")){
		    			Map<String,String> parameters=new HashMap<String,String>();
		    			parameters.put("email", user.getEmail());
		    			parameters.put("creatorName", "admin");
		    			parameters.put("userSex", user.getGender().name().equals(UserGender.MALE)?"m":"w");
		    			String resultStr="";
		    			/**
		    			if(StringUtils.isNotBlank(emm3Url)){
		    				resultStr=HttpUtil.httpsPost(emm3Url+"/mum/personnel/savePersonnelXieTong", parameters,"UTF-8");
			    			JSONObject obj=JSONObject.fromObject(resultStr);
			    			if(!obj.get("status").equals("ok")){
			    				map.put("flag", "0");
			    				map.put("msg", "该用户在协同开发中已添加成功，但EMM中添加失败");
			    				return new ModelAndView("user/index",map);
			    			}
		    			}
		    			if(StringUtils.isNotBlank(emm3TestUrl)){
		    				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mum/personnel/savePersonnelXieTong", parameters,"UTF-8");
			    			JSONObject obj=JSONObject.fromObject(resultStr);
			    			if(!obj.get("status").equals("ok")){
			    				map.put("flag", "0");
			    				map.put("msg", "该用户在协同开发中已添加成功，但EMM中添加失败");
			    				return new ModelAndView("user/index",map);
			    			}
		    			}
		    			**/
		    			map.put("flag", "1");
	    				map.put("msg", "添加成功");
		    			return new ModelAndView("user/index",map);
		    		}
		    		if(Cache.getSetting("SETTING").getEmailServerStatus().compareTo(EMAIL_STATUS.OPEN)==0){
						//使用平台设置中的邮箱发送邮件
						Setting setting = Cache.getSetting("SETTING");
						JavaMailSenderImpl mailSender = (JavaMailSenderImpl)InitBean.getApplicationContext().getBean("mailSender");
				    	mailSender.setHost(setting.getEmailServerUrl());
				    	mailSender.setPort(Integer.parseInt(setting.getEmailServerPort().toString()));
				    	mailSender.setUsername(setting.getEmailAccount());
				    	mailSender.setPassword(setting.getEmailPassword());
				    	
//				    	MailBean mailBean = new MailBean();
//				    	mailBean.setFrom(setting.getEmailAccount());
//						mailBean.setFromName(setting.getPlatName());
//						mailBean.setSubject(mailSubject);
//						mailBean.setTemplate("管理员已重置您的密码,您的密码是:"+randomCode+","+"协同网址:"+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>");
//						String loginUserName =request.getSession(true).getAttribute("userName").toString();
//						mailBean.setTemplate("Hi,"+user.getAccount()+"</br>"+loginUserName+"通知您加入协同开发啦！</br>地址:"+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>"+"</br>用户名:"+user.getAccount()+"</br>密码:"+randomCode+","+"</br>祝您使用愉快!");
//						log.info("userId:"+user.getId()+",pwd is :"+randomCode);
//						mailBean.setToEmails(new String[]{user.getEmail()});
//						mailUtil.send(mailBean);
						
						MailSenderInfo mailInfo = new MailSenderInfo();
						String loginUserName =request.getSession(true).getAttribute("userName").toString();
						mailInfo.setContent("Hi,"+user.getAccount()+"</br>"+loginUserName+"通知您加入协同开发啦！</br>地址:"+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>"+"</br>用户名:"+user.getAccount()+"</br>密码:"+randomCode+","+"</br>祝您使用愉快!");
						mailInfo.setNick(setting.getPlatName());
						mailInfo.setSubject(mailSubject);
						mailInfo.setToAddress(user.getEmail());
						log.info("userId:"+user.getId()+",pwd is :"+randomCode);
						sendMailTool.sendMailByAsynchronousMode(mailInfo);
		    		}
					map.put("flag", "1");
		    	}
				
			}
				
			/*if(user.getUserlevel().compareTo(USER_LEVEL.ADVANCE)==0){
				UserAuth ua = new UserAuth();
				ua.setUserId(user.getId());
				//PROJECT_CREATOR  
				Role role = this.roleService.findByName(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR);
				ua.setRoleId(role.getId().toString());
				this.userAuthService.addUserAuth(ua);
				
				UserAuth ua1 = new UserAuth();
				ua1.setUserId(user.getId());
				//TASK_CREATOR
				Role role1 = this.roleService.findByName(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.CREATOR);
				ua1.setRoleId(role1.getId().toString());
				this.userAuthService.addUserAuth(ua1);
			}*/
			
		}catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	/**
	 *高效版创建用户
	 *创建用户时候会调用sso接口，将用增加到sso 
	 *@auth haijun.cheng
	 *@data 2016-10-27
	 */
	@ResponseBody
	@RequestMapping(value="/createEfficient",method={RequestMethod.POST})
	public ModelAndView  createUserEfficient(HttpServletRequest request,User user){
		Map<String, Object> map = new HashMap<String,Object>();
		try {
			if(!user.getEmail().matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")){
				map.put("flag", "0");
	    		map.put("msg", "邮箱不合法.");
				return  new ModelAndView("user/index",map);
            }
			if(StringUtils.isNotBlank(user.getCellphone())){
				if(!user.getCellphone().matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
					map.put("flag", "0");
		    		map.put("msg", "手机号格式错误.");
					return  new ModelAndView("user/index",map);
	            }
			}
			if(StringUtils.isNotBlank(user.getQq())){
				if(!user.getQq().matches("^[0-9]*$")){
					map.put("flag", "0");
		    		map.put("msg", "QQ格式错误.");
					return  new ModelAndView("user/index",map);
	            }
			}
			User userOld = null;
			if(null != user.getId() && user.getId()!=-1 && user.getId()!=0){//修改用户
				userOld = this.userService.findOne(user.getId());
				if(user.getIcon()==null){
					user.setIcon(userOld.getIcon());
				}
				user.setPassword(userOld.getPassword());
				user.setReceiveMail(userOld.getReceiveMail());
				user.setStatus(userOld.getStatus());
				user.setType(userOld.getType());
				
				user = userService.save(user);
				jdbcTpl.update("update T_RESOURCES set userName= ? where userId = ?", new Object[]{user.getUserName(),user.getId()});
				map.put("flag", "1");
			}else{//新增用户
				if(Cache.getSetting("SETTING").getEmailServerStatus()==EMAIL_STATUS.CLOSE){
					if(null==user.getPassword() || "".equals(user.getPassword())){
						//邮件服务关闭,需要管理员手动输入密码,但是密码为空
						throw new Exception("邮箱服务器关闭,需要手动设置密码.");
					}
				}
				//邮箱服务器开启
				String randomCode =(null==user.getPassword()||"".equals(user.getPassword()))?MD5Util.generateCode():user.getPassword();
		    	String md5Pass = MD5Util.MD5(randomCode).toLowerCase();
		    	user.setPassword(md5Pass);
//		    	判断用户名，邮箱是否有重复
		    	if(userService.existByProperty("account", user.getAccount()) || userService.existByProperty("email", user.getEmail())){
		    		map.put("flag", "0");
		    		map.put("msg", "邮箱或者用户名已经存在.");
		    	}else{
		    		user.setIcon(headImgDir);
		    		user = userService.save(user);
		    		userService.addUserToSSO(user);
		    		if(Cache.getSetting("SETTING").getEmailServerStatus().compareTo(EMAIL_STATUS.OPEN)==0){
						//使用平台设置中的邮箱发送邮件
						Setting setting = Cache.getSetting("SETTING");
						JavaMailSenderImpl mailSender = (JavaMailSenderImpl)InitBean.getApplicationContext().getBean("mailSender");
				    	mailSender.setHost(setting.getEmailServerUrl());
				    	mailSender.setPort(Integer.parseInt(setting.getEmailServerPort().toString()));
				    	mailSender.setUsername(setting.getEmailAccount());
				    	mailSender.setPassword(setting.getEmailPassword());
						
						MailSenderInfo mailInfo = new MailSenderInfo();
						String loginUserName =request.getSession(true).getAttribute("userName").toString();
						mailInfo.setContent("Hi,"+user.getAccount()+"</br>"+loginUserName+"通知您加入协同开发啦！</br>地址:"+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>"+"</br>用户名:"+user.getAccount()+"</br>密码:"+randomCode+","+"</br>祝您使用愉快!");
						mailInfo.setNick(setting.getPlatName());
						mailInfo.setSubject(mailSubject);
						mailInfo.setToAddress(user.getEmail());
						log.info("userId:"+user.getId()+",pwd is :"+randomCode);
						sendMailTool.sendMailByAsynchronousMode(mailInfo);
		    		}
					map.put("flag", "1");
		    	}
				
			}
			
		}catch(MessagingException e1){//发送邮件失败
			map.put("flag", "0");
			e1.printStackTrace();
		}catch(UnsupportedEncodingException e2){//发送邮件失败
			map.put("flag", "0");
			e2.printStackTrace();
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	//查询用户信息
	@ResponseBody
	@RequestMapping(value="/{userId}",method={RequestMethod.GET})
	public ModelAndView  findUser(@PathVariable(value="userId")long userId){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			User user = userService.findOne(userId);
			map.put("user", user);
			map.put("flag", "1");
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	//审核用户信息
	@ResponseBody
	@RequestMapping(value="/check/{userId}",method={RequestMethod.GET})
	public ModelAndView  checkUserInfo(@PathVariable(value="userId")long userId){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			User user = userService.updateOrFindOne(userId);
			map.put("user", user);
			map.put("flag", "1");
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	/**
	 * 无EMM情况下,导入excel
	 * @user jingjian.wu
	 * @date 2015年9月23日 下午3:32:19
	 */
	@RequestMapping(value="/import",method={RequestMethod.POST})
	public Map<String, Object>  importExcel(HttpServletRequest request,MultipartFile file){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			FileOutputStream os = null;  
	    	byte[] bytes = file.getBytes();
	    	String fileName = "";
	        if (file.getOriginalFilename().toLowerCase().endsWith("xls")) {  
	        	fileName = excelPath+System.currentTimeMillis()+".xls";
	        } else if (file.getOriginalFilename().toLowerCase().endsWith("xlsx")) {  
	        	fileName = excelPath+System.currentTimeMillis()+".xlsx";
	        } else{
	    		map.put("flag", "2");
	    		map.put("msg", "文件格式不符合");
	    		return  map;
	        }
	        
			File fileNameFile = new File(fileName);
			if(!fileNameFile.getParentFile().exists()){
				fileNameFile.getParentFile().mkdirs();//创建""文件夹
			}
			fileNameFile.createNewFile();
			
			os = new FileOutputStream(fileName);
			os.write(bytes);
			os.flush();
			os.close();
	    	String message=userService.saveUsersFromExcel(new File(fileName));
	    	if(message.equals("ok")){
	    		map.put("flag", "1");
	    	}else{
	    		map.put("flag", "2");
	    		map.put("msg", "列表第"+message+"个用户导入失败,其它已成功");
	    		return  map;
	    	}
			
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  map;
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
//		String returnStr = HttpTools.doPost("https://192.168.1.224:18443/mum/organize/indexOrg",new HashMap(), "utf-8");
//		System.out.println(returnStr);
		Map<String,String> map = new HashMap<String,String>();
		map.put("uniqueField", "wangtian.miao@3g2win.com");
		
//		String personStr = HttpTools.doPost("https://192.168.1.224:18443/mum/personnel/getPersonelByUnique", map, "utf-8");
//		System.out.println(personStr);
		
		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("uniqueField","wangtian.miao@3g2win.com"));
		String re = HttpUtil.httpPost("http://192.168.1.224:8080/mum/personnel/getPersonelByUnique", parameters);
		System.out.println(re);
		
		System.out.println(HttpUtil.httpPost("http://192.168.1.224:8080/mum/organize/indexOrg", parameters));
	}
	 
	/**
	 * 从EMM获取组织机构
	 * @user jingjian.wu
	 * @date 2015年9月25日 下午4:34:39
	 */
	@ResponseBody
	@RequestMapping(value="/fromemm",method={RequestMethod.GET})
	public ModelAndView  emmImport(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if(serviceFlag.equals("enterpriseEmm3")){
//				String returnStr = HttpTools.doPost(emm3Url+"/mum/organize/indexOrg",new HashMap(), "utf-8");
				String returnStr = HttpTools.doPost(emm3Url+"/contact/getAllOrg",new HashMap(), "utf-8");
				/*returnStr ="{"
						+"    \"zTreeData\": ["
						+"        {"
						+"            \"id\": 4912,"
						+"            \"createdAt\": 1498010312000,"
						+"            \"name\": \"test组织机构\","
						+"            \"orgId\": \"ad89b039-b474-438f-a546-a617061c2174\","
						+"            \"parentId\": \"0\","
						+"            \"uniqueField\": 1232,"
						+"            \"pushapp\": false,"
						+"            \"updateStatus\": false,"
						+"            \"androidCount\": 0,"
						+"            \"iPhoneCount\": 0,"
						+"            \"iPadCount\": 0,"
						+"            \"children\": [],"
						+"            \"hasDevice\": false"
						+"        }"
						+"    ]"
						+"}";*/
				log.info("returnStr==> "+returnStr);
//				JSONObject json = JSONObject.fromObject(returnStr);
//				JSONArray arr = json.getJSONArray("zTreeData");
//				JSONArray newArr = new JSONArray();
//				if(null!=arr && arr.size()>0){
//					for(int i=0;i<arr.size();i++){
//						JSONObject o = arr.getJSONObject(i);
//						
//					}
//				}
//				map.put("listOrg", newArr);
				map.put("listOrg", returnStr);
				map.put("flag", "1");
			}else{
//				List<Organization> listOrg = organizationFacade.getTopLevelOrg(getToken());
				List<Organization> listOrg = organizationFacade.getAllOrg(getToken());
				map.put("listOrg", listOrg);
				map.put("flag", "1");
			}
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	/**
	 * 根据某个组织机构从EMM获取该机构下面的人员
	 * @user jingjian.wu
	 * @date 2015年9月25日 下午4:59:10
	 */
	@ResponseBody
	@RequestMapping(value="/emmDetail/{orgId}",method={RequestMethod.GET})
	public ModelAndView  emmUser(HttpServletRequest request,@PathVariable("orgId") String orgId){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if(serviceFlag.equals("enterpriseEmm3")){
				Map<String,String> mapParam = new HashMap<String,String>();
				mapParam.put("orgId", orgId);
				log.info("personnelListOut  params:"+mapParam.toString());
				String returnStr = HttpTools.doPost(emm3Url+"/mum/personnel/personnelListOut",mapParam, "utf-8");
				map.put("listPerson", returnStr);
				map.put("flag", "1");
			}else{
				List<Personnel> listperson = personelFacade.getListByOrgId(getToken(), orgId);
				map.put("listPerson", listperson);
				map.put("flag", "1");
			}
			
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	/**
	 * 搜索EMM4人员
	 * @user jingjian.wu
	 * @date 2015年9月25日 下午4:59:27
	 */
	@ResponseBody
	@RequestMapping(value="/searchEmmPerson")
	public ModelAndView  searchEmmPerson(HttpServletRequest request,String formKeyWords){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			
			List<Personnel> listperson = personelFacade.getListByKeyWord(getToken(), formKeyWords);
			map.put("listPerson", listperson);
			map.put("flag", "1");
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	/**
	 * 从EMM选择的用户,添加到本地,及插入SSO
	 * @user jingjian.wu
	 * @date 2015年9月24日 下午4:02:28
	 */
	@ResponseBody
	@RequestMapping(value="/postUser",method={RequestMethod.POST})
	public ModelAndView  postUser(HttpServletRequest request,@RequestParam(required=false) List<Long> personIds,@RequestParam(required=false) List<String> uniqueFields){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			List<List<String>> list = userService.addUserFromEMM(getToken(), personIds,uniqueFields,request);
			List<String> emailList = list.get(0);
			List<String> alreadyEmail = list.get(1);
			map.put("emailList", emailList);
			map.put("alreadyEmail", alreadyEmail);
			map.put("flag", "1");
		} catch (Exception e) {
			map.put("flag", "0");
			map.put("error", e.getMessage());
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	/**
	 * 待审核用户,通过/拒绝
	 * @user jingjian.wu
	 * @date 2015年10月8日 上午11:58:54
	 */
	@ResponseBody
	@RequestMapping(value="/check",method=RequestMethod.GET)
	public ModelAndView getAuthstrUser(HttpServletRequest request,HttpServletResponse response,Long id,String operate){
		try{
			if(null!=operate && operate.equals("pass")){
				this.userService.updateUserStatus(id,USER_STATUS.NORMAL);
				User user = this.userService.findOne(id);
				
				MailSenderInfo mailInfo = new MailSenderInfo();
				mailInfo.setContent("Hi,"+user.getAccount()+":</br>    "+Cache.getSetting("SETTING").getPlatName()+"通知您加入协同开发啦！</br>    地址:"+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>"+"</br>    使用您AppCan账号登陆即可，祝您使用愉快！");
				log.info("mail Subject-->"+mailSubject);
				mailInfo.setToAddress(user.getEmail());
				sendMailTool.sendMailByAsynchronousMode(mailInfo);
			}else{
				this.userService.updateUserStatus(id,USER_STATUS.UNPASS);
			}
				
			return this.getSuccessModel("success");
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedModel("failed");
		}
	}
	
	
	/**
	 * 启用,停用用户
	 * @user jingjian.wu
	 * @date 2015年10月8日 上午11:59:43
	 */
	@ResponseBody
	@RequestMapping(value="/statusChange",method=RequestMethod.POST)
	public ModelAndView updateUserEnable(HttpServletRequest request,HttpServletResponse response,Long id,String operate){
		try{
			if(null!=operate && operate.equals("enable")){
				this.userService.updateUserStatus(id,USER_STATUS.NORMAL);
			}else if(null!=operate && operate.equals("disable")){
				this.userService.updateUserStatus(id,USER_STATUS.FORBIDDEN);
			}
				
			return this.getSuccessModel("success");
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedModel("failed");
		}
	}
	
	/**
	 * 重置密码
	 * @user jingjian.wu
	 * @date 2015年10月8日 下午3:34:27
	 */
	@ResponseBody
	@RequestMapping(value="/resetPwd",method=RequestMethod.POST)
	public ModelAndView updateUserPwd(HttpServletRequest request,HttpServletResponse response,Long id,String pwd){
		try{
			this.userService.updatePwd(id,pwd);
			return this.getSuccessModel("success");
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedModel("failed");
		}
	}
	
	/**
	 * 删除用户
	 * @user jingjian.wu
	 * @date 2015年10月8日 下午8:20:19
	 */
	@ResponseBody
	@RequestMapping(value="/delUsers",method={RequestMethod.POST})
	public ModelAndView  delUsers(HttpServletRequest request,@RequestParam List<Long> userIds){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			int count = userService.deleteUsers(userIds);
			map.put("count", count);
			map.put("flag", "1");
		} catch (Exception e) {
			map.put("flag", "0");
			e.printStackTrace();
		}
		return  new ModelAndView("user/index",map);
	}
	
	@ResponseBody
	@RequestMapping(value="/findUserInitPermision",method={RequestMethod.POST})
	public Map<String,Object>  findUserInitPermision(HttpServletRequest request, HttpServletResponse response){
		List<Permission> perList = roleService.findUserInitPermission();
		return this.getSuccessMap(perList);
	}
}
