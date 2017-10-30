package org.zywx.cooldev.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.appdo.facade.omm.entity.tenant.Enterprise;
import org.zywx.appdo.facade.omm.service.tenant.TenantFacade;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.service.AppService;
import org.zywx.cooldev.service.BaseService;
import org.zywx.cooldev.service.BugMemberService;
import org.zywx.cooldev.service.BugModuleService;
import org.zywx.cooldev.service.BugService;
import org.zywx.cooldev.service.CheckInfoService;
import org.zywx.cooldev.service.DocumentChapterService;
import org.zywx.cooldev.service.DocumentService;
import org.zywx.cooldev.service.DynamicService;
import org.zywx.cooldev.service.EnterpriseService;
import org.zywx.cooldev.service.FilialeInfoService;
import org.zywx.cooldev.service.NoticeService;
import org.zywx.cooldev.service.PermissionInterceptorService;
import org.zywx.cooldev.service.PermissionService;
import org.zywx.cooldev.service.ProcessService;
import org.zywx.cooldev.service.ProjectParentService;
import org.zywx.cooldev.service.ProjectReportService;
import org.zywx.cooldev.service.ProjectService;
import org.zywx.cooldev.service.ResourcesService;
import org.zywx.cooldev.service.SettingService;
import org.zywx.cooldev.service.TagService;
import org.zywx.cooldev.service.TaskService;
import org.zywx.cooldev.service.TeamAnalyService;
import org.zywx.cooldev.service.TeamAuthService;
import org.zywx.cooldev.service.TeamGroupService;
import org.zywx.cooldev.service.TeamMemberService;
import org.zywx.cooldev.service.TeamService;
import org.zywx.cooldev.service.UserAuthService;
import org.zywx.cooldev.service.UserService;
import org.zywx.cooldev.service.VideoService;
import org.zywx.cooldev.util.ProcessClearStream;
import org.zywx.cooldev.util.Tools;
import org.zywx.cooldev.util.emm.TokenUtilProduct;

/**
 * 控制器基类
 * @author yang.li
 * @date 2015-08-06
 *
 */
public class BaseController {
	
	protected static final Pattern emailpattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
	
	protected Log log = LogFactory.getLog(this.getClass().getName());	 
	
	@Autowired
	protected ProjectService projectService;
	
	@Autowired
	protected BaseService baseService;
	
	
	@Autowired
	protected ProcessService processService;
	
	@Autowired
	protected TaskService taskService;
	
	@Autowired
	protected TeamService teamService;
	
	@Autowired
	protected TeamGroupService teamGroupService;
	
	@Autowired
	protected TeamMemberService teamMemberService;
	
	@Autowired
	protected TeamAuthService teamAuthService;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected UserAuthService userAuthService;
	
	@Autowired
	protected DynamicService dynamicService;
	
	@Autowired
	protected NoticeService noticeService;
	
	@Autowired
	protected ResourcesService resourcesService;
	
	@Autowired
	protected TagService tagService;
	
	@Autowired
	protected DocumentChapterService documentChapterService;

	@Autowired
	protected DocumentService documentService;
	@Autowired
	protected SettingService settingService;
	
	@Autowired
	protected AppService appService;

	@Autowired
	protected EnterpriseService enterpriseService;
	
	@Autowired
	protected VideoService videoService;

	@Autowired
	protected ProjectParentService projectParentService;
	@Autowired
	protected FilialeInfoService filialeInfoService;
	
	@Value("${cooldev.validIp}")
	private String validIp; 
	
	//系统版本标识
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	@Autowired(required=false)
	private TenantFacade tenantFacade;
	
	@Autowired
	protected BugService bugService;
	@Autowired
	protected BugModuleService bugModuleService;
	@Autowired
	protected BugMemberService bugMemberService;
	@Autowired
	protected TeamAnalyService teamAnalyService;
	@Autowired
	protected ProjectReportService projectReportService;
	@Autowired
	protected PermissionInterceptorService permissionInterceptorService;
	@Autowired
	protected PermissionService PermissionService;
	@Autowired
	protected CheckInfoService checkInfoService;
	
	public String getProductTokenByProjectId(long projectId) {
		if(serviceFlag.equals("enterpriseEmm3") || null==tenantFacade){
			return "";
		}
		String[] params = new String[2];
		Project prj = projectService.getProject(projectId);
		if(prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
				|| prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING)){
			Enterprise enterprise = tenantFacade.getEnterpriseByShortName(prj.getBizCompanyId());
			params[0] = enterprise.getId().toString();
			params[1] = "dev";
			return TokenUtilProduct.getToken(enterprise.getEntkey(), params);
		}else{
			return null;
		}
	}
	
	
	public String getProductTokenByTeamId(long teamId) {
		if(tenantFacade==null){
			return "";
		}
		if(serviceFlag.equals("enterpriseEmm3")){
			return "";
		}
		String[] params = new String[2];
		Team team = teamService.findOne(teamId);
		if(team.getType().equals(TEAMTYPE.ENTERPRISE)){
			Enterprise enterprise = tenantFacade.getEnterpriseByShortName(team.getEnterpriseId());
			params[0] = enterprise.getId().toString();
			params[1] = "dev";
			return TokenUtilProduct.getToken(enterprise.getEntkey(), params);
		}else{
			return null;
		}
		
	}
	
	/*public String getTestToken() {
		String[] params = new String[2];
		params[0] = tenantId;
		params[1] = "dev";
		return TokenUtilTest.getToken(key, params);
	}*/
	/**
	 * 返回jsonData的公用代码段
	 * 
	 * @param pw
	 * @param jsonData
	 */
	protected void retJson(PrintWriter pw, JSONObject jsonData) {
		pw.print(jsonData.toString());
		pw.flush();
		pw.close();
		return;
	}
	
	protected PrintWriter getPrintWriter(HttpServletResponse response, String charset, String contentType){
    	
		response.setCharacterEncoding(charset);
		response.setContentType(contentType);
        PrintWriter pw=null;
        try {
            pw = response.getWriter();
        } catch (IOException e) {
            return null;
        }
        return pw;
    }
	
	protected PrintWriter getPrintWriter(HttpServletResponse response){		
		return getPrintWriter(response, "utf-8", "text/html");
    }
	
	protected PrintWriter getPrintWriter4Json(HttpServletResponse response) {
		return getPrintWriter(response, "utf-8", "application/json");
	}

	
	protected Map<String, Object> getFailedMap(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "failed");
		ret.put("message", message);
		return ret;
	}
	
	protected Map<String, Object> getSuccessMap(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", message);
		return ret;
	}
	
	protected Map<String, Object> getWarningMap(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "warning");
		ret.put("message", message);
		return ret;
	}
	
	protected Map<String, Object> getAffectMap() {
		Map<String, Object> affect = new HashMap<>();
		affect.put("affect", 1);
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", affect);
		return ret;
	}
	
	
	
	protected ModelAndView getFailedModel(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "failed");
		ret.put("message", message);
		return new ModelAndView("",ret);
	}
	
	protected ModelAndView getSuccessModel(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", message);
		return new ModelAndView("",ret);
	}
	
	protected ModelAndView getAffectModel() {
		Map<String, Object> affect = new HashMap<>();
		affect.put("affect", 1);
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", affect);
		return new ModelAndView("",ret);
	}
	
	protected boolean validateIP(HttpServletRequest request){
		
		//IP验证
		String srcIp = Tools.getRequestSrcIp(request);
		List<String> validIps = Arrays.asList(validIp.split(","));
		if(!Tools.isValidRequestIp(srcIp, validIps)){
			log.error("Error: " + srcIp + " not contains: " + validIps);
			return false;
		}
		return true;
	}
	
	protected String execShell(String command) {          
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        try {  
            Process p = run.exec(command);
//            
//            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
//            String line;  
//            while ((line = in.readLine()) != null) {
//            	ret += (line + "\n\r");
//            	log.info(line);
//            }
//            in.close();
            new ProcessClearStream(p.getInputStream(),"BaseService-INFO",ret).start();
            new ProcessClearStream(p.getErrorStream(),"BaseService-ERROR", ret).start();
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret.toString();
 	}
	
	/**
 	 * 由于空格等为题,执行shell报错
 	 * @param command
 	 * @param dir
 	 * @return
 	 */
 	protected String syncExecShell(String[] command,File dir) {  
 		StringBuilder sb = new StringBuilder();
 		for(String str:command){
 			sb.append(",").append(str);
 		}
 		sb.deleteCharAt(0);
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        try {  
            Process p = run.exec(command,null,dir);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {
            	ret.append(line + "\n\r");
            }
            in.close();
            
            
            BufferedReader inErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = inErr.readLine()) != null) {
            	ret.append(line + "\n\r");
            }
            inErr.close();
            
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
            log.info("exec cmd["+sb+" in "+dir.getAbsolutePath()+"],result:["+ret.toString()+"]");
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret.toString();
 	}

 	
 	protected JSONObject getJSONObject(InputStream inputStream) {
		
		
		try {
			BufferedReader in = new BufferedReader( new InputStreamReader(inputStream) );
			String data = "";
			String line = null;
			while((line = in.readLine()) != null) {
				data += line;
			}
			JSONObject obj = JSONObject.fromObject(data);
			in.close();
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
 	
 	/*
 	 * 分页sql代码处理
 	 */
 	protected String getPageSql(String page,String size){
 		
 		if(page == null || "".equals(page) || "0".equals(page)){
			page = "1";
		}
		if(size == null || "".equals(size)){
			size = "10";
		}
 		//当前页数
		Integer pageint = Integer.valueOf(page);
		
		//每页数量
		Integer sizeint =Integer.valueOf( size);
		if(sizeint == 0){
			sizeint = 10;
		}
		//起始数量
		Integer startNum =(pageint-1)*sizeint;
		
		String limitSql = " limit "+startNum+","+size;
 		return limitSql;
 	}
}
