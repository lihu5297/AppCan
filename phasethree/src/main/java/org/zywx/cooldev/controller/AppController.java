package org.zywx.cooldev.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.AppPackageBuildStatus;
import org.zywx.cooldev.commons.Enums.AppPackageBuildType;
import org.zywx.cooldev.commons.Enums.AppVersionType;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.GIT_OPERATE_TYPE;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppChannel;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.app.AppPatch;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.app.AppWidget;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.service.AppService;
import org.zywx.cooldev.util.Tools;

import net.sf.json.JSONObject;


/**
 * APP相关处理控制器
 * @author yang.li
 * @date 2015-08-29
 *
 */
@Controller
@RequestMapping(value = "/app")
public class AppController extends BaseController {
	
	@Autowired
	private AppService appService;
	
	@Value("${appPackage.certBaseUrl}")
	private String certBaseUrl;
	
	
//	@Value("${shellPath}")
//	private String shellPath;
	
	/**
	 * 根据应用AppcanID获取对应的项目ID
	 * @user jingjian.wu
	 * @date 2015年10月26日 上午11:29:04
	 */
	@ResponseBody
	@RequestMapping(value = "/findPrjId/{appId}",method=RequestMethod.GET)
	public Map<String, Object> findPrjId(
			@PathVariable(value="appId") String appId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
	
			try {
				log.info("通过应用ID获取对应的项目ID,appId===>"+appId);
				App app = this.appService.findByAppcanAppId(appId);
				return this.getSuccessMap(app.getProjectId());
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap("获取项目ID失败");
			}
			
	}

	//***************************************************
	//    App CRUD actions                              *
	//***************************************************
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> getAppList(
			@RequestParam(value="projectId", required=false) Long projectId,
			@RequestParam(value="appType", required=false) Long appType,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String search) {
	
			try {
				int pageNo       = 0;
				int pageSize     = 15;

				Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
				
				log.info(String.format("getAppList -> projectId[%s] appType[%s] loginUserId[%d]", projectId, appType, loginUserId));

				List< Map<String, Object> > arr = this.appService.getAppList(pageable, projectId, appType, loginUserId,search);
				
				log.info("getAppList -> " + arr.toString());
				
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}

	@ResponseBody
	@RequestMapping(value="/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getApp(@PathVariable(value="appId") long appId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			Map<String, Object> map = appService.getApp(appId, loginUserId);

			if(map != null) {
				return this.getSuccessMap(map);

			} else {
				return this.getFailedMap("not found Task with id=" + appId);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 根据应用id获取有次应用GIT客户端权限的用户信息
	 */
	@ResponseBody
	@RequestMapping(value="/gitusers/{appCanAppId}", method=RequestMethod.GET)
	public Map<String, Object> getAppGitUser(@PathVariable(value="appCanAppId") String projectId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			Map<String, Object> map = appService.fetchGitUser(projectId);

			if(map != null) {
				return this.getSuccessMap(map);

			} else {
				return this.getFailedMap("not found app with appCanappId=" + projectId);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> addApp(
			App app,@RequestParam(value="isEnterCopy",defaultValue="false") boolean isEnterCopy, //enterprises inserter appid and appkey required isEnterCopy
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		try {
			if(app.getName()!=null&&app.getName().length()>1000){
				return this.getFailedMap("应用名称不能超过1000个字符");
			}
			if(app.getDetail()!=null&&app.getDetail().length()>1000){
				return this.getFailedMap("应用描述不能超过1000个字符");
			}
			log.info("userid-->"+loginUserId+",create app["+app.getName()+","+app.getDetail()+" ]at:"+System.currentTimeMillis());
			/*boolean existName = appService.existByAppName(app.getName());
			if(existName){
				return this.getFailedMap("app名称["+app.getName()+"]已经存在.");
			}*/
			//validate appid and appkey
			if(isEnterCopy){
				//自定义app id和key去空格
				app.setAppcanAppId(app.getAppcanAppId().trim());
				app.setAppcanAppKey(app.getAppcanAppKey().trim());
				
				log.info("user custome appId-->"+app.getAppcanAppId());
				log.info("user custome appKey-->"+app.getAppcanAppKey());
				App appOld = appService.findByAppcanAppIdAndDel(app.getAppcanAppId(),  DELTYPE.NORMAL);
				if(null!=appOld){
					throw new RuntimeException("应用ID已被占用");
				}
				appOld = appService.findByAppcanAppKeyAndDel(app.getAppcanAppKey(),  DELTYPE.NORMAL);
				if(null!=appOld){
					throw new RuntimeException("应用Key已被占用");
				}
				//应用id不存在
				boolean firstChar = appService.validateFirstElementForAppId(app.getAppcanAppId());
				if(!firstChar){
					throw new RuntimeException("应用appCanAppId首字母必须为字母");
				}
				boolean appIdResult = this.appService.validateAppId(app.getAppcanAppId());
				boolean appKeyResult = this.appService.validateAppKey(app.getAppcanAppKey());
				if(!appIdResult){
					return this.getFailedMap("appid格式不正确");
				}
				if(!appKeyResult){
					return this.getFailedMap("appkey格式不正确");
				}
			}
			this.appService.addApp(app, loginUserId);
			//添加动态
			//this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_ADD, app.getProjectId(), new Object[]{app});
			return this.getSuccessMap(app);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/{appId}", method=RequestMethod.PUT)
	public Map<String, Object> editApp(
			App app,
			@PathVariable(value="appId") long appId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			if(app.getName()!=null&&app.getName().length()>1000){
				return this.getFailedMap("应用名称不能超过1000个字符");
			}
			if(app.getDetail()!=null&&app.getDetail().length()>1000){
				return this.getFailedMap("应用描述不能超过1000个字符");
			}
			App old = appService.findOne(appId);
			/*if(!app.getName().equals(old.getName())){
				boolean existName = appService.existByAppName(app.getName());
				if(existName){
					return this.getFailedMap("app名称["+app.getName()+"]已经存在.");
				}
			}*/
			app.setId(appId);
			int affected = this.appService.editApp(app);
			
			app = this.appService.findOne(appId);
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_EDIT, app.getProjectId(), new Object[]{app});
			HashMap<String, Integer> ret = new HashMap<>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	@ResponseBody
	@RequestMapping(value="/{appId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeApp(
			@PathVariable(value="appId") long appId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			App app = this.appService.findOne(appId);

			List<Task> listT =this.taskService.findByAppId(appId);
			if(null!=listT && listT.size()>0){
				return this.getFailedMap("此应用下还有任务,不可以删除");
			}
			this.appService.removeApp(appId);
			
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_DELETE, app.getProjectId(), new Object[]{app});
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("删除应用失败");
		}

	}
	
	//***************************************************
	//    GIT actions                                   *
	//***************************************************
	@ResponseBody
	@RequestMapping(value="/branchList/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getAppBranchList(
			HttpServletRequest request,
			@PathVariable(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {

			try {
				String sPageNo      = request.getParameter("pageNo");
				String sPageSize    = request.getParameter("pageSize");

				int pageNo       = 0;
				int pageSize     = 20;
				
				try {
					if(sPageNo != null) {
						//pageNo		= Integer.parseInt(sPageNo)-1;
						pageNo = Integer.parseInt(sPageNo);
					}
					if(sPageSize != null) {
						pageSize	= Integer.parseInt(sPageSize);
					}
					
				} catch (NumberFormatException nfe) {				
					return this.getFailedMap(nfe.getMessage());
				}

				Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
				
				Map<String, Object> arr = this.appService.getAppBranchList(appId, loginUserId,pageable);
				
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}

	/**
	 * 暂时没用到
	 * 获取应用所有分支列表，提供给IDE使用的接口
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/AllbranchList/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getAppAllBranchList(
			@PathVariable(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {

			try {
				List< Map<String, String> > arr = this.appService.getAppAllBranchList(appId, loginUserId);
				
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}

	/**
	 * 获取分支代码
	 * @param appId
	 * @param treeId
	 * @param branchName
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/branch/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getAppBranch(
			HttpServletRequest request,
			@PathVariable(value="appId") long appId,
			@RequestParam(value="treeId") String treeId,
			@RequestParam(value="branchName") String branchName,
			@RequestHeader(value="loginUserId") long loginUserId) {

			try {
				log.info("getAppBranch parameters: appId->"+appId+",treeId->"+treeId+",branchName->"+branchName+",loginUserId->"+loginUserId);
				String sPageNo      = request.getParameter("pageNo");
				String sPageSize    = request.getParameter("pageSize");

				int pageNo       = 0;
				int pageSize     = 20;
				
				try {
					if(sPageNo != null) {
//						pageNo		= Integer.parseInt(sPageNo)-1;
						pageNo		= Integer.parseInt(sPageNo);
					}
					if(sPageSize != null) {
						pageSize	= Integer.parseInt(sPageSize);
					}
					
				} catch (NumberFormatException nfe) {				
					return this.getFailedMap(nfe.getMessage());
				}

				Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
				
				Map<String, Object> message = this.appService.getAppBranch(appId, branchName, treeId, loginUserId,pageable);
				
				return this.getSuccessMap(message);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}

	@ResponseBody
	@RequestMapping(value="/branch/{appId}", method=RequestMethod.POST)
	public Map<String, Object> addAppBranch(
			@PathVariable(value="appId") long appId,
			@RequestParam(value="srcBranchName") String srcBranchName,
			@RequestParam(value="newBranchName") String newBranchName,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			if(Tools.isChinese(newBranchName)){
				return this.getFailedMap("分支名称不合法");
			}
			boolean createResult = this.appService.createBranch(appId, srcBranchName, newBranchName, loginUserId);
			if(!createResult){//创建分支失败
				return this.getFailedMap("分支["+newBranchName+"] 已经存在");
			}
			App app = this.appService.findOne(appId);
			
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_ADD_BRANCH, app.getProjectId(), new Object[]{app,newBranchName});
			
			this.appService.addGitOperationLog(GIT_OPERATE_TYPE.ADD,loginUserId,app);
			
			return this.getAffectMap();
			
		}  catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());

		}

	}
	
	@ResponseBody
	@RequestMapping(value="/branch/{appId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeAppBranch(
			@PathVariable(value="appId") long appId,
			@RequestParam(value="branchName") String branchName,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			Map<String, Object> message = this.appService.removeBranch(appId, branchName, loginUserId);
			App app = this.appService.findOne(appId);
//			添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_DELETE_BRANCH, app.getProjectId(), new Object[]{app,branchName});
			//添加git日志
			this.appService.addGitOperationLog(GIT_OPERATE_TYPE.REMOVE,loginUserId,app);
			
			return this.getSuccessMap(message);
			
		} catch (RefAlreadyExistsException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (RefNotFoundException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (InvalidRefNameException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (IOException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (GitAPIException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());

		}

	}
	
	@ResponseBody
	@RequestMapping(value="/branch/merge/{appId}", method=RequestMethod.POST)
	public Map<String, Object> mergeAppBranch(
			@PathVariable(value="appId") long appId,
			@RequestParam(value="sourceBranchName") String sourceBranchName,
			@RequestParam(value="targetBranchName") String targetBranchName,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			String message = this.appService.mergeBranch(appId, sourceBranchName, targetBranchName, loginUserId);
			if("failed".equals(message)){
				return this.getFailedMap("合并失败");
			}
			App app = this.appService.findOne(appId);

			this.appService.addGitOperationLog(GIT_OPERATE_TYPE.MERGE,loginUserId,app);
			
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_MERGE_BRANCH, app.getProjectId(), new Object[]{app,sourceBranchName,targetBranchName});
			
			return this.getSuccessMap(message);
			
		} catch (RefAlreadyExistsException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (RefNotFoundException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (InvalidRefNameException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (IOException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		} catch (GitAPIException e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());

		}

	}

	//貌似没用,应该是换成webide了吧
	@ResponseBody
	@RequestMapping(value="/code/preview/{appId}", method=RequestMethod.GET)
	public Map<String, Object> codePreview(
			HttpServletRequest request,
			@PathVariable(value="appId") long appId,
			@RequestParam(value="treeId") String treeId,
			@RequestParam(value="branchName") String branchName,
			@RequestHeader(value="loginUserId") long loginUserId) {

			try {
								
				String path = this.appService.getCodeFilePath(appId, branchName, treeId, loginUserId);
				
				return this.getSuccessMap(path);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}
	
	//***************************************************
	//    AppVersion CRUD actions                       *
	//***************************************************
	@ResponseBody
	@RequestMapping(value="/version", method=RequestMethod.GET)
	public Map<String, Object> getAppVersionList(
			HttpServletRequest request,
			AppVersion match,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
			try {
				String sPageNo      = request.getParameter("pageNo");
				String sPageSize    = request.getParameter("pageSize");

				int pageNo       = 0;
				int pageSize     = 20;
				
				try {
					if(sPageNo != null) {
						pageNo		= Integer.parseInt(sPageNo)-1;
					}
					if(sPageSize != null) {
						pageSize	= Integer.parseInt(sPageSize);
					}
					
				} catch (NumberFormatException nfe) {				
					return this.getFailedMap(nfe.getMessage());
				}

				Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

				Map<String, Object> arr = this.appService.getAppVersionList(pageable, match, loginUserId);
				
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}

	@ResponseBody
	@RequestMapping(value="/version/{appVersionId}", method=RequestMethod.GET)
	public Map<String, Object> getAppVersion(
			@PathVariable(value="appVersionId") long appVersionId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			Map<String, Object> map = appService.getAppVersion(appVersionId, loginUserId);

			if(map != null) {
				return this.getSuccessMap(map);

			} else {
				return this.getFailedMap("not found with id=" + appVersionId);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/version", method=RequestMethod.POST)
	public Map<String, Object> addAppVersion(
			AppVersion appVersion,
			@RequestHeader(value="loginUserId") long loginUserId) {
		try {
			log.info("=================add appversion :"+appVersion.toStr());
			// 创建应用版本，从仓库获取当前版本代码并压缩为lip文件
			AppVersion ret = this.appService.addAppVersion(appVersion, loginUserId);
			if(ret == null) {
				return this.getFailedMap("版本已经存在");
			} else {
				App app = this.appService.findOne(ret.getAppId());
				Project porject = this.appService.findProject(ret.getAppId());
				if(ret.getBranchName().equals("master")){
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_PUBLISH_MASTER_VERSION, porject.getId(), new Object[]{app,ret.getVersionNo()});
				}else{
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_PUBLISH_BRANCH_VERSION, porject.getId(), new Object[]{app,ret.getVersionNo()});
				}
				return this.getSuccessMap(ret);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/version/{appVersionId}", method=RequestMethod.PUT)
	public Map<String, Object> editAppVersion(
			AppVersion appVersion,
			@PathVariable(value="appVersionId") long appVersionId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			AppVersion appVersionOld = this.appService.getAppVersion(appVersionId);
			appVersion.setId(appVersionId);
			int affected = this.appService.editAppVersion(appVersion);
			
			if(!appVersionOld.getVersionDescription().equals(appVersion.getVersionDescription())){
				App app= this.appService.findByAppVersion(appVersionId);
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_EDIT_VERSION_DESCRIBE, app.getProjectId(), new Object[]{app,appVersionOld});
			}
			
			HashMap<String, Integer> ret = new HashMap<>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	@ResponseBody
	@RequestMapping(value="/version", method=RequestMethod.DELETE)
	public Map<String, Object> removeAppVersion(
			@RequestParam(value="appVersionIdList") List<Long> appVersionIdList,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			for(Long appVersionId : appVersionIdList){
				App app = this.appService.findByAppVersion(appVersionId);
				AppVersion appVersionOld = this.appService.getAppVersion(appVersionId);
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_DELTE_VERSION, app.getProjectId(), new Object[]{app,appVersionOld});
			}
			log.info(String.format("removeAppVersion appVersionIdList.size:%d", appVersionIdList.size()));
			
			this.appService.removeAppVersion(appVersionIdList);

			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
		
	//***************************************************
	//    AppPackage CRUD & Build actions               *
	//***************************************************
	@ResponseBody
	@RequestMapping(value="/package", method=RequestMethod.GET)
	public Map<String, Object> getAppPackageList(
			@RequestParam(value="appVersionId") long appVersionId,
			@RequestParam(value="osType") OSType osType,
			@RequestHeader(value="loginUserId") long loginUserId) {
	
			try {
				int pageNo       = 0;
				int pageSize     = 15;
				Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

				List< Map<String, Object> > arr = this.appService.getAppPackageList(pageable, appVersionId, osType, loginUserId);
				
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}

	@ResponseBody
	@RequestMapping(value="/package/{appPackageId}", method=RequestMethod.GET)
	public Map<String, Object> getAppPackage(
			@PathVariable(value="appPackageId") long appPackageId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			Map<String, Object> map = appService.getAppPackage(appPackageId, loginUserId);

			if(map != null) {
				return this.getSuccessMap(map);

			} else {
				return this.getFailedMap("not found package with id=" + appPackageId);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/package", method=RequestMethod.POST)
	public Map<String, Object> addAppPackage(
			@RequestParam(value="buildJsonSettings") String buildJsonSettings,
			@RequestHeader(value="loginUserId") long loginUserId,String newAppCanAppId,String newAppCanAppKey,
			String requestType//原生打包写死native
			) {

		try {
			log.info("add package --->-- newAppCanAppId:"+newAppCanAppId+",newAppCanAppKey:"+newAppCanAppKey);
			AppPackage pack =null;
			if("native".equals(requestType)){
				pack = this.appService.addAppPackageForNative(loginUserId, buildJsonSettings);
			}else{
				pack = this.appService.addAppPackage(loginUserId, buildJsonSettings,newAppCanAppId,newAppCanAppKey);
			}
			
			if(pack == null) {
				return this.getFailedMap("创建安装包失败（应用、版本存在问题）");
			}
			
			AppVersion appVersion = this.appService.getAppVersionByPackage(pack.getAppVersionId());
			App app = this.appService.findByAppVersion(pack.getAppVersionId());
			/**<span>%s</span> 生成了 <span>%s</span> iOS平台的安装包
			 * TRANS_APP_IOS_PACKAGE_ADD
			   TRANS_APP_ANDROID_PACKAGE_ADD
			 */
			String dynamicType = "TRANS_APP_";
			/*if(appVersion.getType().compareTo(AppVersionType.PERSONAL)==0){
				dynamicType += "PERSONAL_";
			}else
				dynamicType += "PROJECT_";*/
			
			if(pack.getOsType().compareTo(OSType.IOS)==0){
				dynamicType += "IOS_";
			}else
				dynamicType += "ANDROID_";
			
			/*if(pack.getBuildType().compareTo(AppPackageBuildType.TESTING)==0){
				dynamicType += "TEST_";
			}else
				dynamicType += "";*/
			
			dynamicType +="PACKAGE_ADD";
			
			DYNAMIC_MODULE_TYPE APPPACKAGETYPE = DYNAMIC_MODULE_TYPE.valueOf(dynamicType);
			
			this.dynamicService.addPrjDynamic(loginUserId, APPPACKAGETYPE, app.getProjectId(), new Object[]{app.getName()});
			
			return this.getSuccessMap(pack);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	

	/**
	 * 为指定代码版本生成widget包
	 * @param appVersionId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/widget", method=RequestMethod.POST)
	public Map<String, Object> addWidget(
			AppWidget appWidget,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			appWidget.setUserId(loginUserId);
			
			appService.addAppWidget(appWidget);
			return this.getSuccessMap(appWidget);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 获取默认Widget版本号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/versionNo/widget/{appVersionId}", method=RequestMethod.GET)
	public Map<String, Object> getNewWidgetVersionNo(@PathVariable(value="appVersionId") long appVersionId) {
		// Widget版本号，和Patch、Package无关联，完全独立
		String versionNo = appService.getNewVersionNo4Widget(appVersionId);
		
		if(versionNo == null) {
			return this.getFailedMap("版本号获取失败");
		} else {
			return this.getSuccessMap(versionNo);
		}

	}
	
	/**
	 * 获取默认Patch版本号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/versionNo/patch/{appVersionId}", method=RequestMethod.GET)
	public Map<String, Object> getNewPatchVersionNo(
			@PathVariable(value="appVersionId") long appVersionId) {
		String versionNo = appService.getNewVersionNo4Patch(appVersionId);
		
		if(versionNo == null) {
			return this.getFailedMap("版本号获取失败");
		} else {
			return this.getSuccessMap(versionNo);
		}

	}
	
	/**
	 * 获取默认Package版本号
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/versionNo/package/{appVersionId}", method=RequestMethod.GET)
	public Map<String, Object> getNewPackageVersionNo(@PathVariable(value="appVersionId") long appVersionId,
			AppPackageBuildType buildType,
			OSType osType) {
		String versionNo = appService.getNewVersionNo4Package(appVersionId, buildType, osType);
		
		if(versionNo == null) {
			return this.getFailedMap("版本号获取失败");
		} else {
			return this.getSuccessMap(versionNo);
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/package/{appPackageId}", method=RequestMethod.PUT)
	public Map<String, Object> editAppPackage(
			AppPackage appPackage,
			@PathVariable(value="appPackageId") long appPackageId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			appPackage.setId(appPackageId);
			int affected = this.appService.editAppPackage(appPackage);
			
			HashMap<String, Integer> ret = new HashMap<>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 删除应用包
	 * @param appPackageId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/package/{appPackageId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeAppPackage(
			@PathVariable(value="appPackageId") long appPackageId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			AppPackage appPackage = this.appService.getSingleAppPackage(appPackageId);
//			AppVersion appVersion = this.appService.getAppVersionByPackage(appPackage.getAppVersionId());
			App app = this.appService.findByAppVersion(appPackage.getAppVersionId());
			//TRANS_APP_IOS_PACKAGE_DEL
			//TRANS_APP_ANDROID_PACKAGE_DEL
			String dynamicType = "TRANS_APP_";
			
			if(appPackage.getOsType().compareTo(OSType.IOS)==0){
				dynamicType += "IOS_";
			}else
				dynamicType += "ANDROID_";
			
			/*if(appPackage.getBuildType().compareTo(AppPackageBuildType.TESTING)==0){
				dynamicType += "TEST_";
			}else
				dynamicType += "";*/
			
			dynamicType +="PACKAGE_DEL";
			
			DYNAMIC_MODULE_TYPE APPPACKAGETYPE = DYNAMIC_MODULE_TYPE.valueOf(dynamicType);
			
			this.dynamicService.addPrjDynamic(loginUserId, APPPACKAGETYPE, app.getProjectId(), new Object[]{app.getName()});
			
			this.appService.removeAppPackage(appPackageId);

			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	/**
	 * 删除Widget包
	 * @param appWidgetId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/widget/{appWidgetId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeAppWidget(
			@PathVariable(value="appWidgetId") long appWidgetId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			
			this.appService.removeWidget(appWidgetId);

			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 删除Patch
	 * @param appPatchId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/patch/{appPatchId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeAppPatch(
			@PathVariable(value="appPatchId") long appPatchId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			
			this.appService.removePatch(appPatchId);

			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	/**
	 * 删除包接口
	 * @param appPackageIdList
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/package/multiple/del", method=RequestMethod.POST)
	public Map<String, Object> removeMultipleAppPackage(
			@RequestParam(value="appPackageIdList") List<Long> appPackageIdList,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			int affected = 0;
			if(appPackageIdList != null && appPackageIdList.size() > 0) {
				
				for(Long appPackageId : appPackageIdList) {
					//添加动态
					this.addPrjDynamicDelPackage(loginUserId, appPackageId);
					
					this.appService.removeAppPackage(appPackageId);
					affected++;
				}
			}

			Map<String, Integer> map = new HashMap<>();
			map.put("affected", affected);
			
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	private void addPrjDynamicDelPackage(long loginUserId,long appPackageId ){
		AppPackage appPackage = this.appService.getSingleAppPackage(appPackageId);
		App app = this.appService.findByAppVersion(appPackage.getAppVersionId());
		//TRANS_APP_IOS_PACKAGE_DEL
		//TRANS_APP_ANDROID_PACKAGE_DEL
		String dynamicType = "TRANS_APP_";
		
		if(appPackage.getOsType().compareTo(OSType.IOS)==0){
			dynamicType += "IOS_";
		}else
			dynamicType += "ANDROID_";
		
		/*if(appPackage.getBuildType().compareTo(AppPackageBuildType.TESTING)==0){
			dynamicType += "TEST_";
		}else
			dynamicType += "";*/
		
		dynamicType +="PACKAGE_DEL";
		
		DYNAMIC_MODULE_TYPE APPPACKAGETYPE = DYNAMIC_MODULE_TYPE.valueOf(dynamicType);
		
		this.dynamicService.addPrjDynamic(loginUserId, APPPACKAGETYPE, app.getProjectId(), new Object[]{app.getName()});
		
	}
	/**
	 * 对外接口 - 打包服务器更新打包状态
	 * @param taskId
	 * @param type
	 * @param filePath
	 * @param fileSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/package/build")
	public Map<String, Object> editPackageBuildInfo(
			@RequestParam(value="taskId") long taskId,
			@RequestParam(value="type") String type,
			@RequestParam(value="filePath") String filePath,
			@RequestParam(value="fileSize") long fileSize,
			@RequestParam(value="versionNo",required=false) String versionNo) {
		try {
			// taskId -> appPackageId, type -> app | log, filePath -> downloadUrl, fileSize -> fileSize
			int affected = appService.editBuildInfo(taskId, type, filePath, fileSize, AppPackageBuildStatus.SUCCESS, "成功构建",versionNo);
			Map<String, Integer> map = new HashMap<>();
			map.put("affected", affected);
			return this.getSuccessMap(map);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 对外接口 - Git仓库更新通知
	 * @param path
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/repo/pushed")
	public Map<String, Object> notifyGitRepoPushed(String path,String payload) {
		try {
			log.info("path:"+path+",payload:"+payload);
			String branchName = "";
			if(StringUtils.isBlank(path)){
				JSONObject json = JSONObject.fromObject(payload);
				String ref = json.getString("ref");
				branchName=ref.replace("refs/heads/", "");
				json = json.getJSONObject("repository");
				path = "/"+json.getString("full_name")+".git";
			}
			log.info("branchName:"+branchName);
			log.info("request --> notifyGitRepoPushed,params:"+path);
			this.appService.updatenotifyRepoPushed(path);
			
			return this.getSuccessMap("ok");

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
//	@ResponseBody
//	@RequestMapping(value="/newgit/repo/pushed")
//	public Map<String, Object> newgitnotifyGitRepoPushed(HttpServletRequest request) {
//		try {
//			JSONObject params = getJSONObject(request.getInputStream());
//			log.info("request --> notifyGitRepoPushed,params:"+params);
////			this.appService.updatenotifyRepoPushed(path);
//			
//			return this.getSuccessMap("ok");
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return this.getFailedMap(e.getMessage());
//		}
//	}
	/**
	 * 修改应用更新状态为更新完成接口
	 * @param appId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/status/{appId}",method=RequestMethod.GET)
	public Map<String, Object> notifyGitRepoPushed(@PathVariable(value="appId") long  appId,String result) {
		try {
			log.info("gitAction update app status :"+appId);
			this.appService.updateAppCodePullStatus(appId,result);
			return this.getSuccessMap("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	

	@ResponseBody
	@RequestMapping(value="/package/upload", method=RequestMethod.POST)
	public Map<String, Object> uploadPackage(HttpServletRequest request) {
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			if(!isMultipart) {
				return this.getFailedMap("not fileupload");
			}
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletContext servletContext = request.getSession().getServletContext();
			File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
			factory.setRepository(repository);
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);
			if(items != null && items.size() > 0) {
				for(FileItem item : items) {
					
					if(item.isFormField()) {
						log.info(String.format("AppController app/package/upload -> formfield fieldName[%s] name[%s] string[%s] value[%s]", item.getFieldName(), item.getName(), item.getString(), request.getParameter(item.getFieldName())));
					} else {
						log.info(String.format("AppController app/package/upload -> file-date fieldName[%s] name[%s] string[%s]", item.getFieldName(), item.getName(), item.getString()));
					}
				}
				return this.getSuccessMap("ok");
			}
			
			return this.getFailedMap("not fileupload1");

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/pkgRequest/{appPackageId}", method=RequestMethod.GET)
	public Map<String, Object> getPkgRequest(
			@PathVariable(value="appPackageId") long appPackageId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String json = appService.getPkgRequest(appPackageId);

			if(json != null) {
				return this.getSuccessMap(json);

			} else {
				return this.getFailedMap("not found Task with id=" + appPackageId);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}	
	
	//getWaitTotalFromRabbitMQ
	@ResponseBody
	@RequestMapping(value="/package/remain", method=RequestMethod.GET)
	public Map<String, Object> getWaitTotalFromRabbitMQ() {
		try {
			int total = this.appService.getWaitTotalFromRabbitMQ();
			Map<String, Object> map = new HashMap<>();
			map.put("count", total);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/qr", method=RequestMethod.GET)
	public Map<String, Object> getAppPackageQr(
			@RequestParam(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {
	
		try {
			int pageNo       = 0;
			int pageSize     = 15;
			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

			List< Map<String, Object> > arr = this.appService.getAppChannelList(pageable, appId, loginUserId);
				
			return this.getSuccessMap(arr);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	//***************************************************
	//    AppChannel CRUD actions                       *
	//***************************************************
	@ResponseBody
	@RequestMapping(value="/channel", method=RequestMethod.GET)
	public Map<String, Object> getAppChannelList(
			@RequestParam(value="appId") long appId,
			@RequestHeader(value="loginUserId") long loginUserId) {
	
			try {
				int pageNo       = 0;
				int pageSize     = 15;
				Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

				List< Map<String, Object> > arr = this.appService.getAppChannelList(pageable, appId, loginUserId);
				
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}

	@ResponseBody
	@RequestMapping(value="/channel/{appChannelId}", method=RequestMethod.GET)
	public Map<String, Object> getAppChannel(
			@PathVariable(value="appChannelId") long appChannelId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			Map<String, Object> map = appService.getAppChannel(appChannelId, loginUserId);

			if(map != null) {
				return this.getSuccessMap(map);

			} else {
				return this.getFailedMap("not found AppChannel with id=" + appChannelId);

			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/channel", method=RequestMethod.POST)
	public Map<String, Object> addAppChannel(
			AppChannel appChannel,
			@RequestHeader(value="loginUserId") long loginUserId) {
		try {

			AppChannel appChannelResutl =  this.appService.addAppChannel(appChannel, loginUserId);
			if(null==appChannelResutl){
				return this.getFailedMap("渠道号已存在");
			}
			return this.getSuccessMap(appChannel);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/channel/{appChannelId}", method=RequestMethod.PUT)
	public Map<String, Object> editAppPackage(
			AppChannel appChannel,
			@PathVariable(value="appChannelId") long appChannelId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			appChannel.setId(appChannelId);
			int affected = this.appService.editAppChannel(appChannel);
			
			HashMap<String, Integer> ret = new HashMap<>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	@ResponseBody
	@RequestMapping(value="/channel/{appChannelId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeAppChannel(
			@PathVariable(value="appChannelId") long appChannelId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			this.appService.removeAppChannel(appChannelId);

			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}	

	//***************************************************
	//    EMM & Build actions                           *
	//***************************************************
	@ResponseBody
	@RequestMapping(value="/cert/validate", method=RequestMethod.GET)
	public Map<String, Object> certValidate(
			@RequestParam(value="p12", required=false) String p12,
			@RequestParam(value="password", required=false) String password,
			@RequestParam(value="mobileprovision", required=false) String mobileprovision,
			@RequestParam(value="androidKeyPassword", required=false) String androidKeyPassword,
			@RequestParam(value="androidStorePassword", required=false) String androidStorePassword,
			@RequestParam(value="androidCertUrl", required=false) String androidCertUrl,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			if(androidCertUrl != null) {
				return this.getSuccessMap(this.appService.validateAndroidCert(androidKeyPassword, androidStorePassword, androidCertUrl));
			} else {
				return this.getSuccessMap(this.appService.validateIOSCert(p12, password, mobileprovision));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		
		
	}
	
	@ResponseBody
	@RequestMapping(value="/cert/generate", method=RequestMethod.POST)
	public Map<String, Object> certGenerate(
			
			String keyStoreAlias,
			String keyPassword,
			String storePassword,
			String duration,
			String name,
			String department,
			String company,
			String city,
			String province,
			String countryCode,
			
			@RequestHeader(value="loginUserId") long loginUserId) {
			try {
				String donwloadUrl = appService.generateAndroidCert(keyStoreAlias, keyPassword, storePassword, duration, name, department, company, city, province, countryCode);

				Map<String, Object> arr = new HashMap<>();
				arr.put("downloadUrl", donwloadUrl);
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}
	
	
	/**
	 * 校验本地证书p12文件
	 * @user jingjian.wu
	 * @date 2016年3月9日 下午6:34:39
	 */
	@ResponseBody
	@RequestMapping(value="/cert/validateLocal", method=RequestMethod.GET)
	public Map<String, Object> certValidateLocal(
			@RequestParam(value="p12", required=false) String p12,
			@RequestParam(value="password", required=false) String password,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			String result = this.appService.validateLocalCert(p12, password);
			if(null==result){
				return this.getFailedMap("本地证书校验失败");
			}
			return this.getSuccessMap(result);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		
		
	}
	
	//***************************************************
	//    patch actions                                 *
	//***************************************************
	
	@ResponseBody
	@RequestMapping(value="/patch/prepare/{appVersionId}", method=RequestMethod.GET)
	public Map<String, Object> preparePath(
			@PathVariable(value="appVersionId") long appVersionId,
			@RequestHeader(value="loginUserId") long loginUserId) {
			try {
				log.info("patch prepare :appVersionId->"+appVersionId+",loginUserId->"+loginUserId);
				Map<String,Object> map = appService.getPreparePatch(appVersionId, loginUserId);
				return this.getSuccessMap(map);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}
	
	@ResponseBody
	@RequestMapping(value="/patch/{appVersionId}", method=RequestMethod.POST)
	public Map<String, Object> addPatch(
			AppPatch appPatch,
			@RequestHeader(value="loginUserId") long loginUserId) {
			try {
				log.info(String.format("create-patch -> AppPatch:", appPatch.toStr()));

				appPatch.setUserId(loginUserId);
				
				AppPatch patch = appService.createPatch(appPatch);
				try {
					//原版本
					AppVersion version1 = appService.findAppVersionByAppId(patch.getBaseAppVersionId());
					String versionNo1 = version1.getVersionNo();
					//新版本
					AppVersion version2 = appService.findAppVersionByAppId(patch.getSeniorAppVersionId());
					String versionNo2 = version2.getVersionNo();
					App app = appService.findOne(version1.getAppId());
					Object[] msg =  new Object[]{app.getName(),versionNo1,versionNo2};
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TRANS_APP_PACKAGE_PATCH_ADD, app.getProjectId(), msg);
				} catch (Exception e) {
					log.info("生成补丁包动态 失败");
					e.printStackTrace();
				}
				return this.getAffectMap();
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
	}
	
	/**
	 * 查询任务列表的时候,需要获取我能看到的应用的名字
	 * @user jingjian.wu
	 * @date 2016年3月7日 下午5:16:18
	 */
	@ResponseBody
	@RequestMapping(value="/name4tasklist",method=RequestMethod.GET)
	public Map<String, Object> getProcessNames(String name,
			@RequestHeader(value="loginUserId") long loginUserId,Long projectId) {
			
		try {
			if(null==name){
				name="";
			}
			Map<String, Object> map = this.appService.getAppList(loginUserId, name,projectId);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
			
	}
	/**
	 * 增加拼音字段
	 */
	@ResponseBody
	@RequestMapping(value="/addPinyin",method=RequestMethod.GET)
	public Map<String,Object> addPinYin(){
		try{
			Map<String,Object> map=this.appService.addPinYin();
			return map;
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 查询所有应用类型
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/findAppType",method=RequestMethod.GET)
	public Map<String,Object> findAppType(){
		List<org.zywx.cooldev.entity.app.AppType> appTypeList = appService.findAppTypeAll();
		return this.getSuccessMap(appTypeList);
	}
}
