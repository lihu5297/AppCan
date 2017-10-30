package org.zywx.coopman.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.entity.User;
import org.zywx.coopman.service.FilialeInfoService;
import org.zywx.coopman.system.Cache;
import org.zywx.coopman.system.InitBean;
import org.zywx.coopman.util.MD5Util;
import org.zywx.coopman.util.mail.MailBean;
import org.zywx.coopman.util.mail.MailUtil;
import org.zywx.coopman.util.mail.base.MailSenderInfo;
import org.zywx.coopman.util.mail.base.SendMailTools;

@Controller
@RequestMapping(value="/manager")
public class ManagerController extends BaseController{
	@Autowired
	private SendMailTools sendMailTool;
	@Autowired
	protected FilialeInfoService filialeInfoService;
	@Autowired
	private MailUtil mailUtil;
	
	@RequestMapping
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "queryKey", required = false) String queryKey) {
		ModelAndView mv = new ModelAndView("/admin/list");
		int ipageNo = 0;
		int ipageSize = 10;
		try {
			if (pageNo != null && pageNo != 0) {
				ipageNo = pageNo - 1;
				ipageSize = pageSize;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("").addObject("pageNo or pageSize is illegal");
		}
		PageRequest page = new PageRequest(ipageNo, ipageSize, Direction.DESC, "createdAt");
		Page<Manager> page1 = null;
		
		if(queryKey==null){
			queryKey = "";
		}else{
			try {
				queryKey = URLDecoder.decode(queryKey,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		log.info("queryKey:"+queryKey);
		if ("" != queryKey) {
			page1 = this.managerService.getList(page, "%" + queryKey + "%");
			mv.addObject("queryKey", queryKey);
		} else
			page1 = this.managerService.getList(page);
		if (page1 != null && page1.getContent() != null) {
			Map<Long, String> filialeMap = filialeInfoService.findFilialeInfoAllMap();
			for(Manager u : page1.getContent()){
				u.setFilialeName(filialeMap.get(u.getFilialeId()));
			}
			
			mv.addObject("list", page1.getContent());
			mv.addObject("total", page1.getTotalElements());
			mv.addObject("totalPage", page1.getTotalPages());
			mv.addObject("curPage", ipageNo + 1);
			mv.addObject("pageSize", ipageSize);
		} else {
			mv.addObject("list", null);
			mv.addObject("total", 0);
			mv.addObject("totalPage", 0);
			mv.addObject("curPage", 1);
			mv.addObject("pageSize", ipageSize);
		}
		return mv;
	}
	
	@RequestMapping(value="/{id}")
	public Map<?,?> getManager(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id")Long id){
		Manager manager = this.managerService.getMnager(id);
		return this.getSuccessMap(manager);
		
	}
	
	@RequestMapping(value="/super")
	public ModelAndView getSuperManager(HttpServletRequest request, HttpServletResponse response){
		Manager manager = this.managerService.getSuperMnager();
		ModelAndView mv = new ModelAndView("/superAdmin/index");
		return mv.addObject(manager);
		
	}
	
	@Value("${upload.file}")
	private String upload;
	
	/**
     * 这里这里用的是MultipartFile[] myfiles参数,所以前台就要用<input type="file" name="myfiles"/>
     * 上传文件完毕后返回给前台[0`filepath],0表示上传成功(后跟上传后的文件路径),1表示失败(后跟失败描述)
     */
    @RequestMapping(value="/upload")
    public String upload(@RequestParam MultipartFile[] myfiles, HttpServletRequest request, HttpServletResponse response) throws IOException{
    	//可以在上传文件的同时接收其它参数 
        System.out.println("收到用户[" + request.getSession().getAttribute("userName") + "]的文件上传请求");
        //如果用的是Tomcat服务器，则文件会上传到\\%TOMCAT_HOME%\\webapps\\YourWebProject\\upload\\文件夹中
        //这里实现文件上传操作用的是commons.io.FileUtils类,它会自动判断/upload是否存在,不存在会自动创建
        String randomUUID = UUID.randomUUID().toString();
        Map<String, String> map = this.managerService.getProperties();
        String realPath = map.get("iconUpload")+randomUUID+"/";
        //设置响应给前台内容的数据格式
        response.setContentType("text/plain; charset=UTF-8");
        //设置响应给前台内容的PrintWriter对象
        PrintWriter out = response.getWriter();
        //上传文件的原名(即上传前的文件名字)
        String originalFilename = null;
        //如果只是上传一个文件,则只需要MultipartFile类型接收文件即可,而且无需显式指定@RequestParam注解
        //如果想上传多个文件,那么这里就要用MultipartFile[]类型来接收文件,并且要指定@RequestParam注解
        //上传多个文件时,前台表单中的所有<input type="file"/>的name都应该是myfiles,否则参数里的myfiles无法获取到所有上传的文件
        if(myfiles.length<1){
            out.println("1`请选择文件后上传");
            return null;
        }
        for(MultipartFile myfile : myfiles){
            if(myfile.isEmpty()){
                out.println("1`请选择文件后上传");
                return null;
            }else{
                originalFilename = myfile.getOriginalFilename();
                if(!checkFile(originalFilename)){//jpg,gif,png,bmp,jpeg
                	out.println("1`只能上传[jpg,gif,png,bmp,jpeg]文件");
                    return null;
                };
//                System.out.println("文件原名: " + originalFilename);
//                System.out.println("文件名称: " + myfile.getName());
//                System.out.println("文件长度: " + myfile.getSize());
//                System.out.println("文件类型: " + myfile.getContentType());
//                System.out.println("========================================");
                try {
                    FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, originalFilename));
                } catch (IOException e) {
                    System.out.println("文件[" + originalFilename + "]上传失败,堆栈轨迹如下");
                    e.printStackTrace();
                    out.println("1`文件上传失败，请重试！！");
                    return null;
                }
            }
        }
        out.print("0`"+map.get("iconUri")+ randomUUID+"/" + originalFilename);
        return null;
        		
    }
    
    /**
     * 检查文件格式
     * @param fileName
     * @return
     */
    private boolean checkFile(String fileName) {
        //设置允许上传文件类型
        String suffixList = "jpg,gif,png,bmp,jpeg";
        // 获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".")
                + 1, fileName.length());
        System.out.println("文件后缀: "+suffix);
        if (suffixList.contains(suffix.trim().toLowerCase())) {
            return true;
        }
        return false;
    }
    
    @RequestMapping(value="/edit")
	public Map<?,?> edit(HttpServletRequest request, HttpServletResponse response,
			Manager manager,@RequestParam(value="modules",required=false)List<Long> modules){
    	manager = this.managerService.editManager(manager,modules);
    	if(((Manager)request.getSession().getAttribute("manager")).getId().equals(manager.getId())){
    		this.managerService.reloadManagerModule(request.getSession());
    	}
		return this.getSuccessMap(manager);
		
	}
    
    @RequestMapping(value="/super/edit")
    public Map<?,?> superEdit(HttpServletRequest request, HttpServletResponse response,
    		Manager manager){
    	manager = this.managerService.editSuperEditManager(manager);
    	return this.getSuccessMap(manager);
    	
    }
    
    @RequestMapping(value="/save")
    public Map<?,?> save(HttpServletRequest request, HttpServletResponse response,
    		Manager manager){
    	manager = this.managerService.saveManager(manager);
    	return this.getSuccessMap(manager);
    	
    }
    
    //修复删除bug,修改接收参数的值
    @RequestMapping(value="/delete")
	public ModelAndView deleteBackupLog(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="ids")List<Long> ids){
		this.managerService.deleteByIds(ids);
		return this.getSuccessModel("affected:1");
	}

    @RequestMapping("/updatePWD/{resetid}")
    public ModelAndView rewritePWD(HttpServletRequest request,HttpServletResponse response,
			@PathVariable(value="resetid")Long resetid,String password){
    	Manager manager = this.managerService.getMnager(resetid);
    	manager.setPassword(password);
    	this.managerService.saveManager(manager);
    	
    	return this.getSuccessModel("affected:1");
    }
    
    @RequestMapping("/resetPWD/{resetid}")
    public ModelAndView resetPWD(HttpServletRequest request,HttpServletResponse response,
			@PathVariable(value="resetid")Long resetid){
    	
    	Setting setting = Cache.getSetting("SETTING");
    	
//    	List<String> emailList = new ArrayList<String>();
    	String randomCode =MD5Util.generateCode();
//    	String md5Pass = MD5Util.MD5(randomCode).toLowerCase();
    	String md5Pass = randomCode.toLowerCase();
    	
//    	JavaMailSenderImpl mailSender = (JavaMailSenderImpl)InitBean.getApplicationContext().getBean("mailSender");
//	    mailSender.setHost("smtp."+setting.getEmailServerUrl());
//	    mailSender.setPort(Integer.parseInt(setting.getEmailServerPort()));
//	    mailSender.setUsername(setting.getEmailAccount());
//	    mailSender.setPassword(setting.getEmailPassword());
//    	log.info(String.format(" email propertites host[%s] port[%d] username[%s] password[%s]", 
//    			mailSender.getHost(),
//    			mailSender.getPort(),
//    			mailSender.getUsername(),
//    			mailSender.getPassword())
//    			);
//    	
//    	final MailBean mailBean = new MailBean();
//		mailBean.setFrom(setting.getEmailAccount());
//		mailBean.setFromName(setting.getPlatName());
//		mailBean.setSubject(setting.getPlatName()+"--重置密码");
//		mailBean.setTemplate("<div style='margin:20px'>"+setting.getPlatName()+"<br>    您的新密码是:"+randomCode+"<br><br><br>"
//				+ "<img src='"+setting.getPlatLogo()+"' width='100px' height='100px' style='float:left;'><br>"
//				+ "<div>"+setting.getPlatName()+"</div></div>");
//		System.out.println("密码是:----------->"+randomCode);
//		
//		Manager manager = this.managerService.getMnager(resetid);
//		emailList.add(manager.getEmail());
//		mailBean.setToEmails((String[])emailList.toArray(new String [emailList.size()]));
//		Thread thread = new Thread(new Runnable() {  
//		    public void run() {  
//		        try {  
//		        	boolean sendResult = mailUtil.send(mailBean);
//		        	if(!sendResult){
//		        		sendResult = mailUtil.send(mailBean);
//		        		if(!sendResult){
//		            		sendResult = mailUtil.send(mailBean);
//		            	}
//		        		if(!sendResult){
//		        			log.error("发送邮件内容:["+mailBean.getTemplate()+"========================="+"失败");
//		        		}
//		        	}
//		        } catch (Exception e) {
//		        	e.printStackTrace();
//		        }  
//		    }  
//		});
//		thread.start();
    	
    	Manager manager = this.managerService.getMnager(resetid);

    	MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setContent("Hi,"+manager.getUserName()+"</br>"+"您的新密码是:"+randomCode+"<br><br><br>"+ "<img src='"+setting.getPlatLogo()+"' width='100px' height='100px' style='float:left;background:#e9e9e9'><br><br>"+"<div>"+setting.getPlatName()+"</div></div>");
		mailInfo.setToAddress(manager.getEmail());
		mailInfo.setNick(setting.getPlatName());
		mailInfo.setSubject(setting.getPlatName()+"--重置密码");
		log.info("mail Subject-->"+setting.getPlatName()+"--重置密码");
		sendMailTool.sendMailByAsynchronousMode(mailInfo);
		
		manager.setPassword(md5Pass);
		this.managerService.saveManager(manager);
		return this.getSuccessModel("1");
    }

}
