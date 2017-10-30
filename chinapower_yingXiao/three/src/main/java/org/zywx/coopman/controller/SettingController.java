package org.zywx.coopman.controller;

import java.io.File; 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.license.LicenseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums.INTEGRATE_STATUS;
import org.zywx.coopman.entity.BackupLog;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.system.InitBean;

@Controller
@RequestMapping(value="/setting")
public class SettingController extends BaseController{

	/**
	 * 
	 * @describe 获取平台设置信息	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:27:50	<br>
	 * @param request
	 * @param response
	 * @param type
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping("")
	public ModelAndView getPlatForm(HttpServletRequest request, HttpServletResponse response,String type){
		Setting set = this.settingService.getSetting();
		ModelAndView ma = new ModelAndView();
		ma.setViewName("setting/"+type);
		ma.addObject("set",set);
		return ma;
	}
	
	/**
	 * 
	 * @describe 获取备份数据	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:28:19	<br>
	 * @param request
	 * @param response
	 * @param type
	 * @param pageNo
	 * @param pageSize
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping("/backuplog")
	public ModelAndView getBackupLog(HttpServletRequest request, HttpServletResponse response,String type,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize){
		ModelAndView mv = new ModelAndView();
		Setting set = this.settingService.getSetting();
		if(type.equals("backup")){
			int ipageNo = 0;
			int ipageSize = 10;
			try {
				if (pageNo != null && pageNo > 0) {
					ipageNo = pageNo - 1;
					ipageSize = pageSize;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ModelAndView("").addObject("pageNo or pageSize is illegal");
			}
			PageRequest page = new PageRequest(ipageNo, ipageSize, Direction.DESC, "createdAt");
			Page<BackupLog> page1 = this.backupLogService.getLogList(page);
			if (page1 != null && page1.getContent() != null) {
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
			
		}
		mv.setViewName("setting/"+type);
		mv.addObject("set",set);
		return mv;
	}
	
	/**
	 * 
	 * @describe 删除备份数据	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:28:37	<br>
	 * @param request
	 * @param response
	 * @param ids
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/backuplog/delete")
	public ModelAndView deleteBackupLog(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="ids")List<Long> ids){
		this.backupLogService.deleteByIds(ids);
		return this.getSuccessModel("affected:1");
	}
	
	/**
	 * 
	 * @describe 更新平台设置信息	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:28:56	<br>
	 * @param request
	 * @param response
	 * @param type
	 * @param info
	 * @param set
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public ModelAndView updateInfoPlatForm(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("type")String type,@RequestParam("info")String info,Setting set){
		
		set = this.settingService.updateSetting(set,info);
		
		//刷新缓存
		InitBean.refreshCache();
		
		ModelAndView ma = new ModelAndView();
		ma.addObject("set",set);
		ma.addObject("status","success");
		return ma;
		
	}
	
	/**
	 * 
	 * @describe 启用/停用系统接入	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:29:13	<br>
	 * @param request
	 * @param response
	 * @param SYSStatus
	 * @param id
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/changeStatus",method=RequestMethod.POST)
	public ModelAndView changeStatus(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("SYSStatus")INTEGRATE_STATUS SYSStatus,@RequestParam("id")Long id){
		
		this.settingService.updateStatus(SYSStatus,id);
		return this.getSuccessModel("affected:1");
		
	}
	
	/**
	 * 
	 * @describe 更新接入系统KEY	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:29:52	<br>
	 * @param request
	 * @param response
	 * @param password
	 * @param id
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/changeKey",method=RequestMethod.POST)
	public ModelAndView changeKey(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("password")String password,@RequestParam("id")Long id){
		
		this.settingService.updateKey(password,id);
		return this.getSuccessModel("affected:1");
	}

	/**
	 * 
	 * @describe 测试邮箱服务器	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:30:15	<br>
	 * @param request
	 * @param response
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/testemail",method=RequestMethod.GET)
	public ModelAndView testEmailServer(HttpServletRequest request, HttpServletResponse response){
		Setting setting = this.settingService.getSetting();
		if(null!=setting.getEmailServerType() && null!=setting.getEmailServerUrl() && null!=setting.getEmailServerPort()){
			boolean bool = this.settingService.testEmail(setting);
			return this.getSuccessModel(bool);
		}
		return this.getFailedModel(false);
		
	}
	
	/**
	 * 
	 * @describe 测试个人邮箱	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:30:32	<br>
	 * @param request
	 * @param response
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/testpemail",method=RequestMethod.GET)
	public ModelAndView testPersonalEmail(HttpServletRequest request, HttpServletResponse response){
		Setting setting = this.settingService.getSetting();
		if(null!=setting.getEmailServerType() && null!=setting.getEmailServerUrl() && null!=setting.getEmailServerPort()){
			boolean bool = this.settingService.testPersonalEmail(setting);
			return this.getSuccessModel(bool);
		}
		return this.getFailedModel(false);
		
	}

	/**
	 * 
	 * @describe 手动备份平台	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 下午6:32:40	<br>
	 * @param request
	 * @param response
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/backup",method=RequestMethod.GET)
	public ModelAndView unAutomaticBackupPlat(HttpServletRequest request, HttpServletResponse response){
		try{
			Map<String,String> hostInfo = this.settingService.getHostInfo();
		
			this.settingService.updateBackup(hostInfo);
			return this.getSuccessModel("backup success");
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
		
	}

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
        Map<String, String> map = this.settingService.getProperties();
        String realPath = map.get("license")+randomUUID+"/";
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
                System.out.println("文件原名: " + originalFilename);
                System.out.println("文件名称: " + myfile.getName());
                System.out.println("文件长度: " + myfile.getSize());
                System.out.println("文件类型: " + myfile.getContentType());
                System.out.println("========================================");
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
        out.print("0`"+map.get("license")+ randomUUID+"/" + originalFilename);
        return null;
        		
    }

    /**
     * 这里这里用的是MultipartFile[] myfiles参数,所以前台就要用<input type="file" name="myfiles"/>
     * 上传文件完毕后返回给前台[0`filepath],0表示上传成功(后跟上传后的文件路径),1表示失败(后跟失败描述)
     */
    @RequestMapping(value="/upload/logo")
    public String uploadLogo(@RequestParam MultipartFile[] myfiles, HttpServletRequest request, HttpServletResponse response) throws IOException{
    	//可以在上传文件的同时接收其它参数 
        System.out.println("收到用户[" + request.getSession().getAttribute("userName") + "]的文件上传请求");
        //如果用的是Tomcat服务器，则文件会上传到\\%TOMCAT_HOME%\\webapps\\YourWebProject\\upload\\文件夹中
        //这里实现文件上传操作用的是commons.io.FileUtils类,它会自动判断/upload是否存在,不存在会自动创建
        Map<String, String> map = this.managerService.getProperties();
        String realPath = map.get("logo");
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
                System.out.println("文件原名: " + originalFilename);
                System.out.println("文件名称: " + myfile.getName());
                System.out.println("文件长度: " + myfile.getSize());
                System.out.println("文件类型: " + myfile.getContentType());
                System.out.println("========================================");
                try {
                    FileUtils.copyInputStreamToFile(myfile.getInputStream(), new File(realPath, "logo1.png"));
                } catch (IOException e) {
                    System.out.println("文件[" + originalFilename + "]上传失败,堆栈轨迹如下");
                    e.printStackTrace();
                    out.println("1`文件上传失败，请重试！！");
                    return null;
                }
            }
        }
        out.print("0`"+map.get("logoUri")+"logo1.png");
        return null;
        		
    }
    /**
     * 修改授权信息 并保存session
     * @param request
     * @param response
     * @param id
     * @param authorizePath
     * @return
     */
    @RequestMapping(value="/updateAuthorize",method=RequestMethod.POST)
	public ModelAndView updateAuthorize(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("id")Integer id,@RequestParam("authorizePath")String authorizePath){
    	ModelAndView ma = new ModelAndView();
		
		 Setting set = settingService.getSetting();
		 //解析License文件
		 LicenseUtil licenseUtil=new LicenseUtil();
		 log.info("文件路径  --> "+authorizePath);
		 boolean b = licenseUtil.hasLicenseFile(authorizePath);
		 if(!b){
			 ma.addObject("status","fail");
			 ma.addObject("message","文件不存在");
			 return ma;
		 }
		 //读取结束日期
		 JSONObject jsonObject = licenseUtil.initLicenseParser(authorizePath);
		 log.info("解析文件  --> "+jsonObject);
		 if(jsonObject==null){
			 ma.addObject("status","fail");
			 ma.addObject("message","解密失败");
			 return ma;
		 }
		 //保存
		 String endTime=(String)jsonObject.get("DATE");
		 set.setAuthorizePath(authorizePath); 
		 set.setAuthDeadTime(endTime);
		 set=settingService.updateSetting(set);
		//刷新缓存
		 HttpSession session = request.getSession();
		 session.setAttribute("endTime", endTime);
		 //tomcat传递session
		 ServletContext ContextA =session .getServletContext();
		 ContextA.setAttribute("session", session );
		InitBean.refreshCache();
		
		ma.addObject("set",set);
		ma.addObject("status","success");
		return ma;
		
	}
    public static void main(String[] args) {
    	String authorizePath="E:/普华E家测试版_EMM_2017-10-31_License.dat";
    	 //解析License文件
		 LicenseUtil licenseUtil=new LicenseUtil();
		 boolean b = licenseUtil.hasLicenseFile(authorizePath);
		 if(!b){
			// ma.addObject("status","fail");
			// ma.addObject("message","文件不存在");
			 System.out.println("文件不存在");
		 }
		 //读取结束日期
		 JSONObject jsonObject = licenseUtil.initLicenseParser(authorizePath);
		 System.out.println(jsonObject);
		 System.out.println(jsonObject.get("DATE"));
	}
}
