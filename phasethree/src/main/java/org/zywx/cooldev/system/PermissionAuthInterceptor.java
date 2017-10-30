	/**  
     * @author jingjian.wu
     * @date 2016年2月26日 上午9:24:31
     */
    
package org.zywx.cooldev.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.AppPackageBuildType;
import org.zywx.cooldev.commons.Enums.AppVersionType;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.controller.BaseController;
import org.zywx.cooldev.dao.app.AppPackageDao;
import org.zywx.cooldev.dao.app.AppVersionDao;
import org.zywx.cooldev.dao.auth.RoleAuthDao;
import org.zywx.cooldev.dao.bug.BugModuleDao;
import org.zywx.cooldev.entity.PermissionInterceptor;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.RoleAuth;
import org.zywx.cooldev.entity.bug.BugModule;


    /**
     * 拦截controller执行时间
	 * @author jingjian.wu
	 * @date 2016年2月26日 上午9:24:31
	 */

public class PermissionAuthInterceptor extends BaseController implements HandlerInterceptor  {
	@Autowired
	protected JdbcTemplate jdbcTpl;
	@Autowired
	protected RoleAuthDao roleAuthDao;
	@Autowired
	protected AppVersionDao appVersionDao;
	@Autowired
	protected AppPackageDao appPackageDao;
	@Autowired
	protected BugModuleDao bugModuleDao;
	@Override
    public boolean preHandle(HttpServletRequest request,    
            HttpServletResponse response, Object handler) throws Exception {
		String method = request.getMethod().toUpperCase();
		//查询不拦截
//		if(method.equals("GET")){
//			return true;
//		}
		String requestUrl = request.getServletPath();
		log.info("requestUrl===>"+requestUrl+",method===>"+method);
		PermissionInterceptor pi=this.permissionInterceptorService.isMatchInterceptor(requestUrl, method);
		//没有设置权限，无需拦截
		if(pi==null){
			return true;
		}
		String enName=pi.getEnName();
		//代码发布
		if(enName.equals("code_publish")){
			if(request.getParameter("type").equals("PERSONAL")&&request.getParameter("branchName").equals("master")){
				enName="code_master_create_testpkg";
			}else if(request.getParameter("type").equals("PROJECT")&&request.getParameter("branchName").equals("master")){
				enName="code_master_publish";
			}else if(request.getParameter("type").equals("PERSONAL")&&!request.getParameter("branchName").equals("master")){
				enName="code_branch_create_testpkg";
			}else{
				enName="code_branch_publish";
			}
		}
		String tableName=pi.getTableName();
		String keyName=pi.getKeyName();
		String loginUserIdStr = request.getHeader("loginUserId");
		long loginUserId = -1;
		if(loginUserIdStr != null) {
			loginUserId = Long.parseLong(loginUserIdStr);
		}
		if(loginUserId == -1) {
			// 未找到登录用户ID
			this.printFailedJson("user not login", response);
			return false;	
		}
		String roleSql="";
		long keyId=-1;
		String keyIdType=pi.getKeyIdType();
		Integer keyIdIndex=pi.getKeyIdIndex();
		if(keyIdType.equals("parameter")){
			//打包
			if(enName.equals("create_package")){
				keyName="appId";
				String buildJsonSettings=(String) request.getParameter("buildJsonSettings");
				buildJsonSettings = buildJsonSettings.replace("\\\"", "\"");
				buildJsonSettings = buildJsonSettings.replace("\"{", "{");
				buildJsonSettings = buildJsonSettings.replace("}\"", "}");
				JSONObject settings = JSONObject.fromObject(buildJsonSettings);
				JSONObject parameters = settings.getJSONObject("paramters");
				String buildTypeStr = (String)parameters.get("buildType");
				//获取appId
				keyId=Long.parseLong(parameters.get("appId").toString());
				//测试包、正式包
				String appVersionIdStr = (String)parameters.get("appVersionId");
				Long appVersionId=Long.parseLong(appVersionIdStr);
				AppVersion appVersion =appVersionDao.findOne(appVersionId);
				//版本类型 我的版本  项目版本 PROJECT  PERSONAL
				AppVersionType versionType=appVersion.getType();
				if(AppPackageBuildType.PRODUCTION.name().equals(buildTypeStr)&&versionType.equals(AppVersionType.PROJECT)){
					enName="prjversion_create_producepkg";
				}else if(AppPackageBuildType.TESTING.name().equals(buildTypeStr)&&versionType.equals(AppVersionType.PROJECT)){
					enName="prjversion_create_testpkg";
				}else{
					enName="myversion_create_testpkg";
				}
			}else if(enName.equals("version_remove")){
				keyName="appId";
				//肯定是批量删除同一个应用的版本，所以只要取第一个即可
				List<String> appVersionIdList = java.util.Arrays.asList(request.getParameter("appVersionIdList"));
				AppVersion appVersion=appVersionDao.findOne(Long.parseLong(appVersionIdList.get(0)));
				//获取appId
				keyId=appVersion.getAppId();
				//版本类型 我的版本  项目版本 PROJECT  PERSONAL
				AppVersionType versionType=appVersion.getType();
				if(versionType.equals(AppVersionType.PROJECT)){
					enName="prjversion_remove_product";
				}else{
					enName="myversion_remove";
				}
			}else if(enName.equals("publish_package")){
				keyName="appId";
				String appPackageId=(String) request.getParameter("appPackageId");
				AppPackage appPackage=appPackageDao.findOne(Long.parseLong(appPackageId));
				Long appVersionId=appPackage.getAppVersionId();
				//测试包、正式包
				AppVersion appVersion =appVersionDao.findOne(appVersionId);
				//版本类型 我的版本  项目版本 PROJECT  PERSONAL
				AppVersionType versionType=appVersion.getType();
				if(AppPackageBuildType.PRODUCTION.equals(appPackage.getBuildType())&&versionType.equals(AppVersionType.PROJECT)){
					enName="prjversion_publish_product";
				}else if(AppPackageBuildType.TESTING.equals(appPackage.getBuildType())&&versionType.equals(AppVersionType.PROJECT)){
					enName="prjversion_publish_test";
				}else{
					enName="myversion_publish";
				}
				//获取appId
				keyId=appVersion.getAppId();
			}else if(enName.equals("generte_widget")){
				keyName="appId";
				String appVersionId=(String) request.getParameter("appVersionId");
				AppVersion appVersion=appVersionDao.findOne(Long.parseLong(appVersionId));
				//获取appId
				keyId=appVersion.getAppId();
				//版本类型 我的版本  项目版本 PROJECT  PERSONAL
				AppVersionType versionType=appVersion.getType();
				if(versionType.equals(AppVersionType.PROJECT)){
					enName="prjversion_generte_widget";
				}else{
					enName="myversion_generte_widget";
				}
			}else if(enName.equals("resource_create_file")){
				String type=(String) request.getParameter("type");
				if(type.equals("dir")){
					enName="resource_create_dir";
				}else{
					enName="resource_create_file";
				}
				keyId=Long.parseLong(request.getParameter(keyName));
			}else if(enName.equals("document_add_dir")){
				String type=(String) request.getParameter("type");
				if(type.equals("CHAPTER")){
					enName="document_add_dir";
				}else{
					enName="document_add_content";
				}
				keyId=Long.parseLong(request.getParameter(keyName));
			}else if(enName.equals("document_publish")){
				String opertion=(String) request.getParameter("opertion");
				if(opertion.equals("PUBLISHED")){
					enName="document_publish";
				}else{
					enName="document_recycle";
				}
				keyId=Long.parseLong(request.getParameter(keyName));
			}else if(enName.equals("document_part_publish")){
				String opertion=(String) request.getParameter("opertion");
				if(opertion.equals("PUBLISHED")){
					enName="document_part_publish";
				}else{
					enName="document_part_recycle";
				}
				keyId=Long.parseLong(request.getParameter(keyName));
			}else if(enName.equals("document_edit_dir")){
				String type=(String) request.getParameter("type");
				if(type!=null&&type.equals("CHAPTER")){
					enName="document_edit_dir";
				}else{
					enName="document_edit_content";
				}
				keyId=Long.parseLong(request.getParameter(keyName));
			}else{
				keyId=Long.parseLong(request.getParameter(keyName));
			}
		}else if(keyIdType.equals("pathVariable")){
			try{
				//生成补丁包
				if(enName.equals("create_patch")){
					keyName="appId";
					AppVersion appVersion=appVersionDao.findOne(Long.parseLong(requestUrl.split("/")[keyIdIndex]));
					//获取appId
					keyId=appVersion.getAppId();
					//版本类型 我的版本  项目版本 PROJECT  PERSONAL
					AppVersionType versionType=appVersion.getType();
					if(versionType.equals(AppVersionType.PROJECT)){
						enName="prjversion_create_patch";
					}else{
						enName="myversion_create_patch";
					}
				}else{
					if(enName.equals("bug_module_del")){
						BugModule bm=this.bugModuleDao.findOne(Long.parseLong(requestUrl.split("/")[keyIdIndex]));
						keyId=bm.getProjectId();
					}else{
						keyId=Long.parseLong(requestUrl.split("/")[keyIdIndex]);
					}
				}
			}catch(Exception e){
				e.getStackTrace();
				this.printFailedJson("keyId Index is error", response);
				return false;
			}
			
		}else{
			this.printFailedJson("keyIdType is error", response);
			return false;	
		}
		final List<Long> roleIdList=new ArrayList<Long>();
		if(keyName.equals("memberId")){
			String entityId="";
			if(tableName.equals("T_TEAM")){
				entityId="teamId";
			}else if(tableName.equals("T_PROJECT")){
				entityId="projectId";
			}else if(tableName.equals("T_PROCESS")){
				entityId="processId";
			}else if(tableName.equals("T_BUG")){
				entityId="bugId";
			}else if(tableName.equals("T_TASK")){
				entityId="taskId";
			}
			roleSql += String.format("select roleId from %s_AUTH where del=0 and memberId=(select id from %s_MEMBER where %s=(select %s from %s_MEMBER where del=0 and id=%d) and del=0 and userId=%d)",tableName,tableName,entityId,entityId,tableName,keyId,loginUserId);
			if(tableName.equals("T_PROJECT")){
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from %s where id=(select projectId from %s_MEMBER where del=0 and id=(select id from %s_MEMBER where %s=(select %s from %s_MEMBER where del=0 and id=%d) and del=0 and userId=%d)) and del=0) and userId=%d)",tableName,tableName,tableName,entityId,entityId,tableName,keyId,loginUserId,loginUserId);
			}
			if(tableName.equals("T_PROCESS")){
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from T_PROJECT where id=(select projectId from T_PROCESS where del=0 and id=(select processId from T_PROCESS_MEMBER where del=0 and id=%d)) and del=0) and userId=%d)",keyId,loginUserId);
				//project role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_PROJECT_AUTH where del=0 and memberId=(select distinct(id) from T_PROJECT_MEMBER where del=0 and projectId=(select projectId from %s where del=0 and id=(select processId from %s_MEMBER where del=0 and id=%d)) and userId=%d)",tableName,tableName,keyId,loginUserId);
			}
			if(tableName.equals("T_BUG")||tableName.equals("T_TASK")){
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from T_PROJECT where id=(select projectId from T_PROCESS where del=0 and id=(select processId from %s where del=0 and id=(select %s from %s_MEMBER where del=0 and id=%d))) and del=0) and userId=%d)",tableName,entityId,tableName,keyId,loginUserId);
				//project role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_PROJECT_AUTH where del=0 and memberId=(select distinct(id) from T_PROJECT_MEMBER where del=0 and projectId=(select projectId from T_PROCESS where del=0 and id=(select processId from %s where del=0 and id=(select %s from %s_MEMBER where del=0 and id=%d))) and userId=%d)",tableName,entityId,tableName,keyId,loginUserId);
			}
			
		}else{
			//没有member表的
			if(tableName.equals("T_TASK_LEAF")){
				roleSql += String.format("select roleId from %s_AUTH where del=0 and memberId=(select distinct(id) from %s_MEMBER where del=0 and %s=(select topTaskId from T_TASK_LEAF where del=0 and id=%d) and userId=%d)","T_TASK","T_TASK","taskId",keyId,loginUserId);
			}else if(tableName.equals("T_TASK_GROUP")){
				roleSql += String.format("select roleId from T_PROJECT_AUTH where del=0 and memberId=(select distinct(id) from T_PROJECT_MEMBER where del=0 and projectId=(select projectId from T_TASK_GROUP where del=0 and id=%d) and userId=%d)",keyId,loginUserId);
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from T_PROJECT where id=(select projectId from T_TASK_GROUP where del=0 and id=%d) and del=0) and userId=%d)",keyId,loginUserId);
			}else if(tableName.equals("T_APP")||tableName.equals("T_ENGINE")||tableName.equals("T_PLUGIN")||tableName.equals("T_RESOURCES")||tableName.equals("T_DOCUMENT")){
				roleSql += String.format("select roleId from T_PROJECT_AUTH where del=0 and memberId=(select distinct(id) from T_PROJECT_MEMBER where del=0 and projectId=(select projectId from %s where del=0 and id=%d) and userId=%d)",tableName,keyId,loginUserId);
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from T_PROJECT where id=(select projectId from %s where del=0 and id=%d) and del=0) and userId=%d)",tableName,keyId,loginUserId);
			}else{
				roleSql += String.format("select roleId from %s_AUTH where del=0 and memberId=(select distinct(id) from %s_MEMBER where del=0 and %s=%d and userId=%d)",tableName,tableName,keyName,keyId,loginUserId);
			}
			//有member表的
			if(tableName.equals("T_PROJECT")){
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from %s where id=%d and del=0) and userId=%d)",tableName,keyId,loginUserId);
			}else if(tableName.equals("T_PROCESS")){
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from T_PROJECT where id=(select projectId from %s where del=0 and id=%d) and del=0) and userId=%d)",tableName,keyId,loginUserId);
			    //project role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_PROJECT_AUTH where del=0 and memberId=(select distinct(id) from T_PROJECT_MEMBER where del=0 and projectId=(select projectId from %s where del=0 and id=%d) and userId=%d)",tableName,keyId,loginUserId);
			}else if(tableName.equals("T_BUG")||tableName.equals("T_TASK")||tableName.equals("T_TASK_LEAF")){
				//team role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_TEAM_AUTH where del=0 and memberId=(select distinct(id) from T_TEAM_MEMBER where del=0 and teamId=(select teamId from T_PROJECT where id=(select projectId from T_PROCESS where del=0 and id=(select processId from %s where del=0 and id=%d)) and del=0) and userId=%d)",tableName,keyId,loginUserId);
				//project role
				roleSql+=" union ";
				roleSql += String.format("select roleId from T_PROJECT_AUTH where del=0 and memberId=(select distinct(id) from T_PROJECT_MEMBER where del=0 and projectId=(select projectId from T_PROCESS where del=0 and id=(select processId from %s where del=0 and id=%d)) and userId=%d)",tableName,keyId,loginUserId);
			}
		}
		Permission p=this.PermissionService.getPermissionId(enName);
		if(p==null){
			printFailedJson("权限未找到",response);
			return false;
		}
		log.info("roleSql========>"+roleSql);
		this.jdbcTpl.query(roleSql,
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
					roleIdList.add(rs.getLong("roleId"));
			}
		});
		if(roleIdList.size()==0){
			//角色找不到
			this.printFailedJson("role is error", response);
			return false;
		}
		List<RoleAuth> ra=this.roleAuthDao.findByRoleIdInAndPremissionIdAndDel(roleIdList, p.getId(), DELTYPE.NORMAL);
		if(ra.size()==0){
			//无权限
			this.printFailedJson("无权限操作", response);
			return false;
		}else{
			return true;
		}
	}
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}
	
	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
	}
	private void printFailedJson(String message, ServletResponse servletResponse) throws IOException {
		PrintWriter pw = servletResponse.getWriter();
		pw.println("{\"status\":\"failed\", \"message\":\"" + message + "\"}");
		pw.close();
	}
}
