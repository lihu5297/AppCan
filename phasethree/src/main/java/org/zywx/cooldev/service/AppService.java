package org.zywx.cooldev.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.NotMergedException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.appdo.facade.mam.entity.app.AppBaseInfo;
import org.zywx.appdo.facade.mam.service.app.AppBaseInfoFacade;
import org.zywx.appdo.facade.omm.entity.tenant.Enterprise;
import org.zywx.appdo.facade.omm.service.tenant.TenantFacade;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.AppPackageBuildStatus;
import org.zywx.cooldev.commons.Enums.AppPackageBuildType;
import org.zywx.cooldev.commons.Enums.AppVersionType;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.EngineStatus;
import org.zywx.cooldev.commons.Enums.GIT_OPERATE_TYPE;
import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.PATCH_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.PluginType;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TerminalType;
import org.zywx.cooldev.daemon.NotifyPullThread;
import org.zywx.cooldev.dao.CheckInfoDao;
import org.zywx.cooldev.dao.app.AppChannelDao;
import org.zywx.cooldev.dao.app.AppDao;
import org.zywx.cooldev.dao.app.AppPackageDao;
import org.zywx.cooldev.dao.app.AppPatchDao;
import org.zywx.cooldev.dao.app.AppVersionDao;
import org.zywx.cooldev.dao.app.AppWidgetDao;
import org.zywx.cooldev.dao.builder.PluginDao;
import org.zywx.cooldev.dao.builder.PluginResourceDao;
import org.zywx.cooldev.dao.builder.PluginVersionDao;
import org.zywx.cooldev.entity.CheckInfo;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.GitOperationLog;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppChannel;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.app.AppPatch;
import org.zywx.cooldev.entity.app.AppType;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.app.AppWidget;
import org.zywx.cooldev.entity.app.GitAuthVO;
import org.zywx.cooldev.entity.app.GitOwnerAuthVO;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.builder.Engine;
import org.zywx.cooldev.entity.builder.Plugin;
import org.zywx.cooldev.entity.builder.PluginResource;
import org.zywx.cooldev.entity.builder.PluginVersion;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.project.ProjectParent;
import org.zywx.cooldev.entity.trans.Trans;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.MD5Util;
import org.zywx.cooldev.util.NewGitHttpUtil;
import org.zywx.cooldev.util.VersionUtil;
import org.zywx.cooldev.util.emm.TokenUtilProduct;
import org.zywx.cooldev.util.emm.TokenUtilTest;
import org.zywx.cooldev.vo.Match4Project;
import org.zywx.cooldev.vo.PackagePluginInfo;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * App相关服务
 * @author yang.li
 *
 */
@Service
public class AppService extends BaseService {
	
	private final static Pattern appIdPattern = Pattern.compile("[0-9A-Za-z]{8,10}");
	
	private final static Pattern appKeyPattern = Pattern.compile("[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-[0-9A-Za-z]{4}-[0-9A-Za-z]{4}-[0-9A-Za-z]{12}");
	
	@Autowired
	private TeamMemberService teamMemberService;
	
	@Autowired(required=false)
	private TenantFacade tenantFacade;
	
	@Autowired(required=false)
	@Qualifier(value="appBaseInfoFacade")
	private AppBaseInfoFacade appBaseInfoFacade;
	
	@Autowired(required=false)
	@Qualifier(value="appBaseInfoFacadeTest")
	private AppBaseInfoFacade appBaseInfoFacadeTest;
	
	@Value("${git.initRepoUrl}")
	private String initRepoUrl;
	@Value("${git.shareRepoUrl}")
	private String shareRepoUrl;
	@Value("${git.shareallgitauth}")
	private String shareallgitauth;
	
	@Value("${gitFlag}")
	private String gitFlag;
	@Value("${gitToken}")
	private String gitToken;
	
	@Value("${git.removeRepoUrl}")
	private String removeRepoUrl;
	@Value("${git.removeUserUrl}")
	private String removeUserUrl;
	@Value("${git.changeOwnerUrl}")
	private String changeOwnerUrl;
	@Value("${git.localGitRoot}")
	private String localGitRoot;
	@Value("${git.remoteGitRoot}")
	private String remoteGitRoot;
	@Value("${gitFactoryAccount}")
    private String gitFactoryAccount;
	@Value("${gitFactoryPassword}")
    private String gitFactoryPassword;
	
	@Value("${file}")
	private String copyCodeDir;
	
	@Value("${appVersion.codeZipUrl}")
	private String codeZipUrl;
	@Value("${appVersion.codeZipPath}")
	private String codeZipPath;
	
	@Value("${appPackage.certFileRoot}")
	private String certFileRoot;
	@Value("${appPackage.certBaseUrl}")
	private String certBaseUrl;

	@Value("${appPackage.pkgAccessUrl}")
	private String pkgAccessUrl;
	
	@Value("${appPackage.plistRoot}")
	private String plistRoot;
	@Value("${appPackage.plistBaseUrl}")
	private String plistBaseUrl;
	
	@Value("${appPackage.rabbitMqHost}")
	private String rabbitMqHost;
	@Value("${appPackage.rabbitMqPort}")
	private int rabbitMqPort;
	@Value("${appPackage.rabbitMqExchange}")
	private String rabbitMqExchange;
	@Value("${appPackage.rabbitMqRouteKey}")
	private String rabbitMqRouteKey;
	@Value("${appPackage.rabbitMqQueue}")
	private String rabbitMqQueue;
	@Value("${appPackage.rabbitMqUser}")
	private String rabbitMqUser;
	@Value("${appPackage.rabbitMqPassword}")
	private String rabbitMqPassword;

	@Value("${serviceFlag}")
	private String serviceFlag;
	
	@Value("${gitShellServer}")
	private String gitShellServer;
	
	@Autowired
	private AppDao appDao;
	@Autowired
	private AppVersionDao appVersionDao;
	@Autowired
	private AppPackageDao appPackageDao;
	@Autowired
	private AppChannelDao appChannelDao;
	@Autowired
	private AppWidgetDao appWidgetDao;
	@Autowired
	private AppPatchDao appPatchDao;
	
	@Autowired
	private PluginDao pluginDao;
	@Autowired
	private PluginVersionDao pluginVersionDao;
	@Autowired
	private PluginResourceDao pluginResourceDao;
	
	@Autowired
	private EmmService emmService;

	@Autowired
	private ProjectService projectService;
	
	@Value("${base.appcan}")
	private String baseAppCan;
	
	@Value("${shellPath}")
	private String shellPath;

	@Value("${git.deleteallpartner}")
	private String deleteallpartner;

	@Value("${git.changeallpartner}")
	private String changeallowner;

	@Value("${git.deleteallgit}")
	private String deleteallgit;

	@Value("${git.getalluser}")
	private String getalluser;
	
	@Value("${xtGitHost}")
	private String xtGitHost;
	
	@Value("${engineRepo}")
	private String engineRepo;
	
	@Value("${emm3Url}")
	private String emm3MamUrl;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	
	@Value("${sso.host}")
	private String ssoHost;

	@Value("${xietongHost}")
	private String xietongHost;
	
	@Value("${newgitprefix}")
	private String newGitServer;
	
	
	@Value("${node.id}")
    private String nodeId;
	
	@Value("${node.appIdPrefix}")
    private String appIdPrefix;
	@Value("${accessUrl}")
	String accessUrl;
	@Autowired
    private RedisTemplate<String, String> sequenceIdTemplate;
	//***************************************************
	//    App CRUD interfaces                           *
	//***************************************************
	/**
	 * 获取应用列表
	 * @param pageable
	 * @param projectId
	 * @param type
	 * @param loginUserId
	 * @return
	 */
	public List< Map<String, Object> > getAppList(Pageable pageable, Long projectId, Long type, long loginUserId,String search) {
		List< Map<String, Object> > message = new ArrayList<>();
		
		List<App> appList = null;
		
		if(projectId == null || projectId == -1 ) {//projectId为null或者-1是 默认返回所有的app
			appList = appDao.findByUserIdAndDel(loginUserId,DELTYPE.NORMAL);
		
		} else if(type != null) {
			appList = appDao.findByProjectIdAndAppTypeAndDel(projectId, type, DELTYPE.NORMAL);
		
		} else {
			if(StringUtils.isBlank(search)){
				appList = appDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
			}else{
				appList = appDao.findByProjectIdAndAndNameLikeOrPinYinHeadCharOrPinYinNameAndDel(projectId,"%"+search+"%",search+"%",search+"%",DELTYPE.NORMAL);
			}
		
		}
		if(null==appList || appList.size()==0){
//			Map<String, Object> element = new HashMap<>();
//			message.add(element);
			return message;
		}else{
			// 扩展app信息
			for(App app : appList) {
				
				Project p = projectDao.findOne(app.getProjectId());
				if(p != null) {
					app.setProjectName(p.getName());
				}
				//Trans trans=transDao.findByTransactionsIdAndTranType(app.getId(),String.valueOf(Enums.TRANS_TYPE.APP.ordinal()));
				app.setRemoteRepoUrl(remoteGitRoot + app.getRelativeRepoPath());
				
				
				Map<String, Object> element = new HashMap<>();
				element.put("object", app);
				message.add(element);
				
			}
		}
		return message;
	}
	
	public boolean existByAppName(String appName){
		List<App> appList = appDao.findByNameAndDel(appName, DELTYPE.NORMAL);
		if(null!=appList && appList.size()>0){
			return true;
		}
		return false;
	}

	/**
	 * 获取应用详情
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	public Map<String, Object> getApp(long appId, long loginUserId) {

		App app = appDao.findOne(appId);

		if(app == null) {
			return null;
		}
		
		if(app.getAppSource() != null && app.getAppSource() != -1L){
			App appSource = appDao.findOne(app.getAppSource());
			if(appSource != null) {
				app.setAppSourceName(appSource.getName());
			}
		}
		AppType appType = appTypeDao.findOne(app.getAppType());
		app.setAppTypeName(appType.getTypeName());
		User u = userDao.findOne(app.getUserId());
		if(null!=u){
			app.setUserName(u.getUserName());
		}
		Project project = this.projectDao.findOne(app.getProjectId());
		
		Map<String, Integer> permissionMap = new HashMap<>();
		if(project != null) {
			ProjectParent projectParent = projectParentDao.findOne(project.getParentId());
			app.setProjectBizLicense(project.getBizLicense());
			app.setProjectName(project.getName());
			app.setProjectType(project.getType());
			app.setProjectParentId(project.getParentId());
			app.setProjectParentName(projectParent.getProjectName());
			List<Permission> permissions = projectService.getPermissionList(loginUserId, project.getId());
			if(permissions != null && permissions.size() > 0) {
				for(Permission permission : permissions) {
					permissionMap.put(permission.getEnName(), 1);
				}
			}

		}
		
		app.setRemoteRepoUrl(remoteGitRoot + app.getRelativeRepoPath());
		
		//==============================================================================================	
		/*
		 * 只有已经发布过的才做判断，未发布的不做处理
		 */
		if(serviceFlag.equals("enterpriseEmm3")){//如果是EMM3.3走下面的判断
			if(project == null) {
				log.info(String.format("appcanAppId:[%s], appcanAppKey:[%s],该应用所在项目不存在",app.getAppcanAppId(), app.getAppcanAppKey()));
			}
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("appId", app.getAppcanAppId());
			String resultStr="";
			try {
				if (app.getPublished().equals(IfStatus.YES)) {
					resultStr = HttpUtil.httpsPost(emm3MamUrl + "/mam/xieTongInter/validApp", parameters, "UTF-8");
					JSONObject jsonObject = JSONObject.fromObject(resultStr);
					if (jsonObject.get("status").equals("ok")) {//应用不存在
						app.setPublished(IfStatus.NO);
						String sql = "update T_APP set published = %d where id = %d ";
						int a = this.jdbcTpl.update(String.format(sql, IfStatus.NO.ordinal(), app.getId()));
						if (a == 1) {
							log.info("update app published to no");
						}
					}
				}
				if (app.getPublishedTest().equals(IfStatus.YES)) {
					resultStr = HttpUtil.httpsPost(emm3TestUrl + "/mam/xieTongInter/validApp", parameters, "UTF-8");
					JSONObject jsonObject = JSONObject.fromObject(resultStr);
					if (jsonObject.get("status").equals("ok")) {//应用不存在
						app.setPublishedTest(IfStatus.NO);
						String sql = "update T_APP set publishedTest = %d where id = %d ";
						int a = this.jdbcTpl.update(String.format(sql, IfStatus.NO.ordinal(), app.getId()));
						if (a == 1) {
							log.info("update app publishedTest to no");
						}
					}
				} 
			} catch (Exception e) {
				log.info("get app detail from emm3 error:",e);
			}
		}else{
			if(project.getBizCompanyId()!=null){
				if(app.getPublished().equals(IfStatus.YES) || app.getPublishedTest().equals(IfStatus.YES)){
					if(project == null) {
						log.info(String.format("appcanAppId:[%s], appcanAppKey:[%s],该应用所在项目不存在",app.getAppcanAppId(), app.getAppcanAppKey()));
					}
//					TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
					User user = userDao.findOne(emmService.findCreateUserId(project));
					if(null!=tenantFacade){//只有部署了EMM的情况下才去调用EMM接口,如果单独部署协同.那么调用会报错
						Enterprise enterprise = tenantFacade.getEnterpriseByShortName(project.getBizCompanyId());
						String[] params = new String[]{enterprise.getId().toString(),"dev"};
						String token = "";
						if(app.getPublished().equals(IfStatus.YES)){
							//判断生产环境
							token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);
							log.info("判断应用是否存在:token:"+token+",appId:"+app.getAppcanAppId()+",account-->"+user.getAccount());
							//判断应用是否已经发布
							AppBaseInfo appbase = appBaseInfoFacade.getByAppIdAndCreator(token, app.getAppcanAppId(),user.getAccount());
							if(null==appbase){//应用不存在
								app.setPublished(IfStatus.NO);
								String sql = "update T_APP set published = %d where id = %d ";
								int a = this.jdbcTpl.update(String.format(sql,IfStatus.NO.ordinal(),app.getId()));
								if(a==1){
									log.info("update app published to no");
								}
							}
						}
						
						if(app.getPublishedTest().equals(IfStatus.YES)){
							//判断测试环境
							token = TokenUtilTest.getToken(enterprise.getEntkey(), params);
							//判断应用是否已经发布
							log.info("判断应用是否存在:token::"+token+",appId:"+app.getAppcanAppId()+",account-->"+user.getAccount());
							AppBaseInfo appbase = appBaseInfoFacadeTest.getByAppIdAndCreator(token, app.getAppcanAppId(),user.getAccount());
							if(null==appbase){//应用不存在
								app.setPublishedTest(IfStatus.NO);
								String sql = "update T_APP set publishedTest = %d where id = %d ";
								int a = this.jdbcTpl.update(String.format(sql,IfStatus.NO.ordinal(),app.getId()));
								if(a==1){
									log.info("update app publishedTest to no");
								}
							}
						}
					}
				}
			}
		}
		
		//==============================================================================================
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(appId, ENTITY_TYPE.APP, DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for(EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		Map<String, Object> message = new HashMap<>();
		if(resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			app.setResourcesTotal(resources.size());  
			app.setResources(resources);
		}else{
			app.setResources(new ArrayList<Resource>());
			app.setResourcesTotal(0);
		} 
		message.put("object", app);
		message.put("permission", permissionMap);
		
		return message;
	}
	
	/**
	 * 添加新应用<br>
	 * 创建远程GIT版本库<br>
	 * 授权给相关角色<br>
	 * 克隆至本地
	 * @param app
	 * @param loginUserId
	 * @return
	 * @throws Exception 
	 */
	public App addApp(App app, long loginUserId) throws Exception {
		
		long startCreateAppTime = System.currentTimeMillis();
		String status = "";
		String info = "";
		String path = "";
		
		// 添加新应用
		app.setDel(DELTYPE.DELETED);
		app.setUserId(loginUserId);
		app.setPublished(IfStatus.NO);//默认未发布
		app.setPublishedAppCan(IfStatus.NO);
		app.setPublishedTest(IfStatus.NO);
		//增加拼音
		app.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(app.getName()==null?"":app.getName()));
		app.setPinYinName(ChineseToEnglish.getPingYin(app.getName()==null?"":app.getName()));

		User user = userDao.findOne(loginUserId);
	    //外网ip改了，ip is not valid
		String appId = obtainAppCanAppId(appIdPrefix);
		String appKey = obtainAppCanAppKey();
		app.setAppcanAppId(appId);
		app.setAppcanAppKey(appKey);
		app = appDao.save(app);
		
		long startCreateAppGitRepoTime = System.currentTimeMillis();
		
		// 创建远程GIT版本库
		log.info(String.format("addApp --> userId[%d] appId[%d] appAppcanAppId[%s] appkey[%s] initRepo[%s] CreateAppGirRepoTime[%d]", loginUserId, app.getId(),app.getAppcanAppId(),app.getAppcanAppKey(), initRepoUrl,startCreateAppGitRepoTime-startCreateAppTime));
		User loginUser = userDao.findOne(loginUserId);
		
		if(gitFlag.equals("new")){
			//gitBucket版本
			Map<String, String> initRet = this.initRepoNew(loginUser, app);
			status = initRet.get("status");
			info = initRet.get("info");
			path = initRet.get("path");
		}else{
			//apache版本
			Map<String, String> initRet = this.initRepo(loginUser, app);
			status = initRet.get("status");
			info = initRet.get("info");
			path = initRet.get("path");
		}
		
		
		long endCreateAppGirRepoTime = System.currentTimeMillis();
		
		// 版本库地址更新至应用
		log.info(String.format("addApp -> ret status[%s] info[%s] path[%s] endCreateAppGirRepoTime[%d]", status, info, path,endCreateAppGirRepoTime-startCreateAppGitRepoTime));
		app.setRelativeRepoPath(path);
		app.setRemoteRepoUrl(remoteGitRoot + path);
		appDao.save(app);
		
		
		// 版本库授权
		Map<String, Object> shareRet = this.shareRepo(loginUser, app);
		log.info("AppService -> addApp -> shareRet:" + shareRet.toString());
		
		Map<String,String> params = new HashMap<String,String>();
		if(app.getAppType()==0)
			params.put("appType", "MOBILE");
		else if(app.getAppType()==6 || app.getAppType()==7)
			params.put("appType", "JAVA");
		else
			params.put("appType", "OTHER");
		
		params.put("appName", app.getName());
		params.put("appcanAppId", app.getAppcanAppId());
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		if(app.getAppType()== 1) {
			if(null!=app.getAppSource() && -1!=app.getAppSource().longValue()){
				App sourceApp = appDao.findOne(app.getAppSource());
				if(null!=sourceApp){
					params.put("sourceRelativeRepoPath", sourceApp.getRelativeRepoPath());
				}else{
					throw new RuntimeException("关联应用不合法");
				}
			}
			
		}
		params.put("sourceGitRepo", app.getSourceGitRepo());
		log.info(params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/clone", params);
		log.info(String.format("GitAction -> addApp --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException("创建应用失败");
		}
		if(gitFlag.equals("new")){
			//创建webHook
			StringBuffer parameters = new StringBuffer();
			JSONObject WebHookobj = new JSONObject();
			HashSet<String> events=new HashSet<String>();
			events.add("push");
			WebHookobj.put("url", xietongHost+"/cooldev/app/repo/pushed");
			WebHookobj.put("ctype", "form");
			WebHookobj.put("events", events);
			parameters.append(WebHookobj.toString());
			Map<String,String> headers = new HashMap<String,String>();
			headers.put("Authorization", "token "+gitToken);
			String result;
			try {
				result = NewGitHttpUtil.httpPostWithJSON(newGitServer+"/api/v3/repos"+app.getRelativeRepoPath().replace(".git", "")+"/hooks", parameters.toString(),headers);
				log.info("----------->create webHook result："+result);
				JSONObject resultObj = JSONObject.fromObject(result);
				if(!resultObj.get("status").equals("OK")){
					throw new RuntimeException("创建webHook失败");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		// 克隆至本地
		String cmd = "";
		String ret = "";
		if(app.getAppType().equals(AppType.MOBILE)){
			cmd = "sh "+shellPath+"coopdev_git/cloneMobileStep1.sh " + app.getRelativeRepoPath();
			ret = this.execShell(cmd);
			log.info(String.format("addApp cloneStep1 cmd[%s] ret[%s]", cmd, ret));
			try {
				updatePhoneConfig(app.getRelativeRepoPath(),app.getAppcanAppId(),app.getName());
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("创建应用失败");
			}
			
			cmd = "sh "+shellPath+"coopdev_git/cloneMobileStep2.sh " + app.getRelativeRepoPath();
			ret = this.execShell(cmd);
			log.info(String.format("addApp cloneStep2 cmd[%s] ret[%s]", cmd, ret));
		}else{
			cmd = "sh "+shellPath+"coopdev_git/clone.sh " + app.getRelativeRepoPath();
			ret = this.execShell(cmd);
			log.info(String.format("addApp clone cmd[%s] ret[%s]", cmd, ret));
		}
		
		long endCloneAppGirRepoTime = System.currentTimeMillis();
		log.info(String.format("addApp clone cmd[%s] ret[%s] endCloneAppGirRepoTime[%d]", cmd, ret,endCloneAppGirRepoTime-endCreateAppGirRepoTime));
		
		// 网页应用 -> 复制源应用主干代码
		if(app.getAppType().equals(AppType.HTML)) {
			if(null!=app.getAppSource() && -1!=app.getAppSource().longValue()){
				App sourceApp = appDao.findOne(app.getAppSource());
				if(null!=sourceApp){
					log.info(app.getRelativeRepoPath()+""+sourceApp.getRelativeRepoPath());
					cmd = "sh "+shellPath+"coopdev_git/cpSource.sh " + app.getRelativeRepoPath() + " " + sourceApp.getRelativeRepoPath();
					ret = this.execShell(cmd);
					log.info(String.format("addApp copy cmd[%s] ret[%s]", cmd, ret));
				}else{
					throw new RuntimeException("关联应用不合法");
				}
			}
			
		}

		long endCopyAppGirRepoTime = System.currentTimeMillis();
		log.info(String.format("addApp copy endCloneAppGirRepoTime[%d]", endCopyAppGirRepoTime-endCloneAppGirRepoTime));
		*/
		
		return app;
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年11月25日 下午9:45:12
	 */
	/*private void updatePhoneConfig(String relativeRepoPath,String appId,String name) throws Exception{
		log.info(String.format("start update Phone config,rrpoPath[%s],appId[%s],name[%s]", relativeRepoPath,appId,name));
		String path = localGitRoot+relativeRepoPath+"/phone/config.xml";
		
		SAXReader reader = new SAXReader();  
        // 设置读取文件内容的编码  
        reader.setEncoding("UTF-8");  
        Document doc = reader.read(path);  
        
        List<Attribute> attrList = doc.selectNodes("widget/@appId");  
        Iterator<Attribute> i = attrList.iterator();  
        while (i.hasNext())  
        {  
            Attribute attribute = i.next();  
            attribute.setValue(appId);  
        }  
        List<Element> eleList = doc.selectNodes("widget/name");  
        Iterator<Element> eleIter = eleList.iterator();  
        if (eleIter.hasNext())  
        {  
            Element ownerElement = eleIter.next();  
            ownerElement.setText(name);  
        } 
        OutputFormat format = OutputFormat.createPrettyPrint();  
        // 利用格式化类对编码进行设置  
        format.setEncoding("UTF-8");  
        FileOutputStream output = new FileOutputStream(new File(path));  
        XMLWriter writer = new XMLWriter(output, format);  
        writer.write(doc);  
        writer.flush();  
        writer.close();
        log.info("end update Phone config");
	}*/

	/**
	 * 编辑应用基本信息
	 * @param app
	 * @return
	 */
	public int editApp(App app) {
		
		String settings = "";
		if(app.getDetail() != null) {
			settings += String.format(",detail='%s'", app.getDetail());
		}
		if(app.getName() != null) {
			settings += String.format(",name='%s',pinYinHeadChar='%s',pinYinName='%s'", app.getName(),ChineseToEnglish.getPinYinHeadChar(app.getName()==null?"":app.getName()),ChineseToEnglish.getPingYin(app.getName()==null?"":app.getName()));
		}
		if(settings.length() > 0) {
			settings = settings.substring(1);
		} else {
			return 0;
		}
		String sql = "update T_APP set " + settings + " where id=" + app.getId();
		return this.jdbcTpl.update(sql);

	}
	
	/**
	 * 移除应用
	 * @param appId
	 */
	public void removeApp(long appId) {
		App app = appDao.findOne(appId);
//		appDao.delete(appId);
		GitAuthVO vo = new GitAuthVO();
		vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
		String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
		vo.setProject(encodeKey);
		vo.setProjectid(app.getAppcanAppId());
		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
		listAuth.add(vo);
		Map<String,String> map =delAllGitRepo(listAuth);
		log.info("del app gitRepo-->"+(null==map?null:map.toString()));
	}

	public Map<Long, App> appPrivilege(long loginUserId) {
		Map<Long, App> appMap = new HashMap<>();
		
		Set<Long> projectIdSet = new HashSet<>();	// 可以访问的项目编号Set
		
		// 作为团队创建者和管理员可以访问的应用列表
		
		List<Long> teamIdList = new ArrayList<>();
		List<TeamMember> tmList = teamMemberDao.findByUserIdAndDel(loginUserId, DELTYPE.NORMAL);
		if(tmList != null && tmList.size() > 0) {
			for(TeamMember tm : tmList) {
				
			}
		}
		
		// 作为项目成员可以访问的应用列表
		List<ProjectMember> pmList = projectMemberDao.findByUserIdAndDel(loginUserId, DELTYPE.NORMAL);
		return null;
		
	}
	
	
	//***************************************************
	//    GIT interfaces                                *
	//***************************************************
	/**
	 * GIT仓库添加分支
	 * @param appId
	 * @param newBranchName
	 * @param loginUserId
	 * @return
	 * @throws IOException
	 * @throws RefAlreadyExistsException
	 * @throws RefNotFoundException
	 * @throws InvalidRefNameException
	 * @throws GitAPIException
	 */
	public synchronized boolean createBranch(long appId, String srcBranchName, String newBranchName, long loginUserId) 
			throws IOException, 
			RefAlreadyExistsException, 
			RefNotFoundException, 
			InvalidRefNameException, 
			GitAPIException {

		App app = appDao.findOne(appId);

		if(app == null) {
			return false;
		}
		
		/*String checkCmd = "sh "+shellPath+"coopdev_git/checkout_branch.sh " + app.getRelativeRepoPath() + " " + srcBranchName;
		String checkRet = this.execShell(checkCmd);
		log.info(String.format("createBranch checkout cmd[%s] ret[%s]", checkCmd, checkRet));
		
		String cmd = "sh "+shellPath+"coopdev_git/create_branch.sh " + app.getRelativeRepoPath() + " " + newBranchName;
		String ret = this.execShell(cmd);
		log.info(String.format("createBranch create cmd[%s] ret[%s]", cmd, ret));
		if(ret.contains("exists")){//分支名称已经存在
			return false;
		}else{
			//创建应用的时候,已经给有分支权限的人分配过权限
			// 新分支授权
			User owner = userDao.findOne(app.getUserId());
			Map<String, Object> shareBranchRet = this.shareBranch(owner, app, newBranchName);
			log.info("shareBranchRet:" + shareBranchRet);
			return true;
		}*/
		Map<String,String> params = new HashMap<String,String>();
		params.put("srcBranchName", srcBranchName);
		params.put("newBranchName", newBranchName);
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		log.info(params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/branch/create", params);
		log.info(String.format("GitAction -> createBranch --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException(obj.getString("message"));
		}
		return true;
	
	}
	
	/**
	 * GIT仓库删除分支
	 * @param appId
	 * @param branchName
	 * @param loginUserId
	 * @return
	 * @throws IOException
	 * @throws RefAlreadyExistsException
	 * @throws RefNotFoundException
	 * @throws InvalidRefNameException
	 * @throws GitAPIException
	 */
	public Map<String, Object> removeBranch(long appId, String branchName, long loginUserId) 
			throws IOException, 
			RefAlreadyExistsException, 
			RefNotFoundException, 
			InvalidRefNameException, 
			GitAPIException {

		App app = appDao.findOne(appId);

		if(app == null) {
			return null;
		}
		
		/*String cmd = "sh "+shellPath+"coopdev_git/delete_branch.sh " + app.getRelativeRepoPath() + " " + branchName;
		String ret = this.execShell(cmd);
		log.info(String.format("removeBranch cmd[%s] ret[%s]", cmd, ret));
	
		Map<String, Object> message = new HashMap<>();
		
		
			
		message.put("affected", 1);*/
		Map<String,String> params = new HashMap<String,String>();
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		params.put("branchName", branchName);
		log.info(params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/branch/delete", params);
		log.info(String.format("GitAction -> removeBranch --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException("删除分支失败");
		}else{
			//同步远程被删的分支信息到WEBIDE仓库
			String cmd = String.format(shellPath + "coopdev_git/webide_branch_prune.sh %s%s %s %s", loginUserId, app.getRelativeRepoPath(),gitFactoryAccount,gitFactoryPassword);
			this.syncExecShell(cmd);
			cmd = String.format(shellPath + "coopdev_git/webide_branch_del.sh %s %s %s", loginUserId, app.getRelativeRepoPath(), branchName);
			this.syncExecShell(cmd);
		}
		Map<String, Object> message = new HashMap<>();
		message.put("affected", 1);
		return message;

	}

	/**
	 * GIT仓库获取分支列表（排除master分支）
	 * @param appId
	 * @param loginUserId
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public Map<String, Object> getAppBranchList(long appId, long loginUserId,Pageable pageable) throws ClientProtocolException, IOException {

		App app = appDao.findOne(appId);

		if(app == null) {
			return null;
		}
		/*List< Map<String, String> > mapList = new ArrayList<>();

		String dir = localGitRoot + app.getRelativeRepoPath();
        String cmd = String.format("sh "+shellPath+"coopdev_git/list_branch.sh %s", dir);  
 
        String ret = this.execShell(cmd);
        ret = ret.replaceAll("[\n\r]", "");
        log.info(String.format("getAppBranchList cmd[%s] ret[%s]", cmd, ret));

        int num = 0;
        String[] items = ret.split(" ");
        if(items.length > 0) {
        	for(String item :items) {
        		if(item.endsWith("master")) {
        			continue;
        		}
        		if(item.startsWith("origin/")) {
        			
        			if(num>=pageable.getPageNumber()*pageable.getPageSize() && num<(pageable.getPageNumber()+1)*pageable.getPageSize()){
        			
	        			Map<String, String> map = new HashMap<>();
	        			map.put("branchName", item.substring(7));
	        			mapList.add(map);
        			}
        			
        			num++;
        		}
        	}
        }
        
        Map<String, Object> message = new HashMap<>();
		
		message.put("object", mapList);
		message.put("total", num);*/

		Map<String,String> params = new HashMap<String,String>();
		params.put("pageNo", pageable.getPageNumber()+"");
		params.put("pageSize",pageable.getPageSize()+"");
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		log.info("get branchs list:"+params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/branch/listApp", params);
		log.info(String.format("GitAction ->listBranch jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException("获取分支列表失败");
		}
		 Map<String, Object> message = new HashMap<>();
		JSONObject objMsg = obj.getJSONObject("message");
		message.put("total", objMsg.get("total"));
		message.put("object", objMsg.get("object"));
		return message;
	}
	
	/**
	 * 提供给IDE使用的接口，获取应用所有分支
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	public List< Map<String, String> > getAppAllBranchList(long appId, long loginUserId) {

		App app = appDao.findOne(appId);

		if(app == null) {
			return null;
		}
		List< Map<String, String> > mapList = new ArrayList<>();

		String dir = localGitRoot + app.getRelativeRepoPath();
        String cmd = String.format("sh "+shellPath+"coopdev_git/list_branch.sh %s", dir);  
 
        String ret = this.execShell(cmd);
        ret = ret.replaceAll("[\n\r]", "");
        log.info(String.format("getAppBranchList cmd[%s] ret[%s]", cmd, ret));

        String[] items = ret.split(" ");
        if(items.length > 0) {
        	for(String item :items) {
        		if(item.startsWith("origin/")) {
        			Map<String, String> map = new HashMap<>();
        			map.put("branchName", item.substring(7));
        			mapList.add(map);
        		}
        		
        	}
        }
        

		return mapList;
	}
	
	/**
	 * GIT仓库获取分支代码
	 * @param appId
	 * @param branchName
	 * @param treeId
	 * @param loginUserId
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public Map<String, Object> getAppBranch(long appId, String branchName, String treeId, long loginUserId,Pageable pageable) throws ClientProtocolException, IOException {

		App app = appDao.findOne(appId);

		if(app == null) {
			return null;
		}

			/*String path = localGitRoot + app.getRelativeRepoPath();
			
			String cmd = "sh "+shellPath+"coopdev_git/checkout_branch.sh " + app.getRelativeRepoPath() + " " + branchName;
			String ret = this.execShell(cmd);
			log.info(String.format("getAppBranch cmd[%s] ret[%s]", cmd, ret));
			
			List< Map<String, Object> > mapList = new ArrayList<>();
			if(treeId == null) {
				treeId = "";
			}
			
			//文件总数
			int num = 0;
			File dir = new File(path + treeId);
			File[] fileList = dir.listFiles();
			if(null!=fileList){
				for(File file : fileList) {
					
					if(".git".equals(file.getName())) {
						continue;
					}
					
					//分页
					if(num>=pageable.getPageNumber()*pageable.getPageSize() && num<(pageable.getPageNumber()+1)*pageable.getPageSize()){
					
						Map<String, Object> item = new HashMap<>();
						item.put("branchName", branchName);
						item.put("fileName", file.getName());
						item.put("type", file.isDirectory() ? "DIRECTORY" : "FILE");
						item.put("treeId", treeId + "/" + file.getName());
						if( file.isDirectory() ) {
							Date lastModified = new Date(file.lastModified());
							item.put("lastModified", RelativeDateFormat.format(lastModified));
							long totalKB = file.length()/1000;
							item.put("kBfileSize", totalKB);
							
						} else {
							// 添加部分文件属性
							Date lastModified = new Date(file.lastModified());
							item.put("lastModified", RelativeDateFormat.format(lastModified));
							long totalKB = file.length()/1000;
							item.put("kBfileSize", totalKB);
						}
						
						mapList.add(item);
					}
					num++;
				}
			}
			
			
			// 生成导航器
			List< Map<String, String> > navigator = this.anlysisTreeId(treeId, branchName);
			
			Map<String, Object> message = new HashMap<>();
			
			message.put("object", mapList);
			message.put("total", num);
			message.put("navigator", navigator);
			message.put("status", app.getCodePullStatus());
			*/
		Map<String, Object> message = new HashMap<>();
		Map<String,String> params = new HashMap<String,String>();
		params.put("treeId", treeId);
		params.put("branchName", branchName);
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		params.put("pageNo", pageable.getPageNumber()+"");
		params.put("pageSize",pageable.getPageSize()+"");
		log.info(params.toString());
		long requestStart = System.currentTimeMillis();
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/branch/code", params);
		long requestEnd = System.currentTimeMillis();
		log.info("getAppBranchCode_requestTime:"+(requestEnd-requestStart));
		log.info(String.format("GitAction -> getAppCode --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException("获取分支代码失败");
		}
		obj= obj.getJSONObject("message");
		message.put("object",obj.getJSONArray("object"));
		message.put("total", obj.getString("total"));
		message.put("navigator", obj.getJSONArray("navigator"));
		message.put("status", app.getCodePullStatus());
		return message;

	}
	
	public void updatenotifyRepoPushed(String relativeRepoPath) {
		App app = appDao.findByRelativeRepoPathAndDel(relativeRepoPath, DELTYPE.NORMAL);
		if(null!=app){
			app.setCodePullStatus("ing");
			appDao.save(app);
			log.info("start --> notifyRepoPushed,relativeRepoPath:"+relativeRepoPath);
			new NotifyPullThread(relativeRepoPath,app.getId(),gitShellServer,xtGitHost).start();
		}else{
			log.info("notifyRepoPushed failed for path->"+relativeRepoPath);
		}

	}
	
	/**
	 * 提供给IDC机房git钩子回调结束后修改应用的更新状态
	 * @param relativeRepoPath
	 */
	public void updateAppCodePullStatus(Long appId,String result) {
		App app = appDao.findOne(appId);
		if(null!=app){
			app.setCodePullStatus(null!=result?result:"finish");
			appDao.save(app);
		}else{
			log.info("GitAction failed for appId->"+appId);
		}

	}
	
	public String getCodeFilePath(long appId, String branchName, String treeId,
			long loginUserId) {
		App app = appDao.findOne(appId);

		if (app == null) {
			return null;
		}

		String path = localGitRoot + app.getRelativeRepoPath();

		String cmd = "sh " + shellPath + "coopdev_git/checkout_branch.sh "
				+ app.getRelativeRepoPath() + " " + branchName;
		String ret = this.execShell(cmd);
		log.info(String.format("getCodeFile cmd[%s] ret[%s]", cmd,
				ret));

		if (treeId == null || "".equals(treeId)) {
			return null;
		}

		File codeFile = new File(path + treeId);
		if (codeFile.isFile()) {
			return codeFile.getAbsolutePath();
		} else {
			return null;
		}
	}
	
	/**
	 * GIT仓库合并分支
	 * @param appId
	 * @param sourceBranchName
	 * @param targetBranchName
	 * @param loginUserId
	 * @return
	 * @throws IOException
	 * @throws NotMergedException
	 * @throws CannotDeleteCurrentBranchException
	 * @throws GitAPIException
	 */
	public synchronized String mergeBranch(long appId, String sourceBranchName, String targetBranchName, long loginUserId)
			throws IOException,
			NotMergedException,
			CannotDeleteCurrentBranchException,
			GitAPIException {

		App app = appDao.findOne(appId);

		if(app == null) {
			return null;
		}
		
		/*String dir = localGitRoot +app.getRelativeRepoPath();

        String cmd = String.format("sh "+shellPath+"coopdev_git/merge.sh %s %s %s", dir, targetBranchName, sourceBranchName);  
          
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        try {  
            Process p = run.exec(cmd);
            new ProcessClearStream(p.getInputStream(),"AppService-INFO",ret).start();
            new ProcessClearStream(p.getErrorStream(),"AppService-ERROR").start();
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
		
		log.info("merge branch -->"+ret);	
		if(ret.toString().contains("failed")){
			return "failed";
		}*/
		Map<String,String> params = new HashMap<String,String>();
		params.put("sourceBranchName",sourceBranchName);
		params.put("targetBranchName",targetBranchName);
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		log.info(params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/branch/merge", params);
		log.info(String.format("GitAction -> mergeBranch --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		
		if(!"success".equals(obj.getString("status"))){
			return "failed";
		}
		return "合并成功";
			
	}
	
	//***************************************************
	//    AppVersion CRUD interfaces                    *
	//***************************************************
	public Map<String, Object> getAppVersionList(Pageable pageable, AppVersion match, long loginUserId) {

		log.info(String.format("getAppVersionList -> appId[%s] type[%s] loginUserId[%s]", match.getAppId(), match.getType(), loginUserId));
		
		List< Map<String, Object> > message = new ArrayList<>();
		final AppVersion finalMatch = match;
		final long finalLoginUserId = loginUserId;
		
		Specification<AppVersion> spec = new Specification<AppVersion>() {
			@Override
			public Predicate toPredicate(
					Root<AppVersion> root,
					CriteriaQuery<?> query,
					CriteriaBuilder cb) {  
				
				List<Predicate> list = new ArrayList<Predicate>();
				if(finalMatch.getAppId() != -1) {
					list.add(cb.equal(root.get("appId").as(Integer.class), finalMatch.getAppId()));
				}
				
				if(finalMatch.getType() != null && !finalMatch.getType().equals(AppVersionType.PUBLISH)) {
					
					list.add(cb.equal(root.get("type").as(AppVersionType.class), finalMatch.getType()));
					
					if(finalMatch.getType().equals(AppVersionType.PERSONAL)) {
						list.add(cb.equal(root.get("userId").as(Long.class), finalLoginUserId));
					}
				}else{
					list.add(cb.equal(root.get("haveApplyPublish").as(Integer.class), 1));
				}
				//又改为非物理删除了,因为物理删除之后,查询版本列表,获取补丁包原先appVersion时候空指针
				list.add(cb.equal(root.get("del").as(AppVersionType.class), 0l));//删除是物理删除,所以没必要加这个条件
				if(null!=finalMatch.getVersionNo()){
					list.add(cb.equal(root.get("versionNo").as(String.class), finalMatch.getVersionNo()));
				}
				

				Predicate[] p = new Predicate[list.size()];
			    //return cb.and(list.toArray(p));
			    return query.where(list.toArray(p)).getRestriction();
			}  
		}; 

		pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), new Sort(Direction.DESC,"createdAt"));
		Page<AppVersion> appVersionList = appVersionDao.findAll(spec,pageable);
		log.info(String.format("getAppVersionList -> ret appVersionList.total[%d] appVersionList.size[%d]", appVersionList.getTotalElements(), appVersionList.getSize()));

		App app = appDao.findOne(match.getAppId());
		if(app == null) {
			return null;
		}
		
		//版本号列表
		List<String> appVersionNos = this.getAppversionNoList(match,loginUserId);
		
		try {
			for(AppVersion appVersion : appVersionList.getContent()) {
				// 扩展版本信息
				appVersion.setBranchZipUrl(codeZipUrl + "/" + appVersion.getBranchZipName());
				appVersion.setAppName(app.getName());

				// 全量包列表
				List<AppPackage> appPackageList = appPackageDao.findByAppVersionIdAndDelOrderByCreatedAtDesc(appVersion.getId(), DELTYPE.NORMAL);
				List<AppPackage> iosList = new ArrayList<>();
				List<AppPackage> androidList = new ArrayList<>();
				for(AppPackage appPackage : appPackageList) {
					JSONObject settings = JSONObject.fromObject(appPackage.getBuildJsonSettings());
					
					appPackage.setSettings(settings);
					//appPackage.setBuildJsonSettings(null);
					// 根据时间返回打包失败信息
					if(appPackage.getBuildStatus().equals(AppPackageBuildStatus.ONGOING)) {
						long createdAtMills = appPackage.getCreatedAt().getTime();
						long currentMills = new Date().getTime();
						long duration = currentMills - createdAtMills;
						if(duration > 3600 * 1000) {
							appPackage.setBuildStatus(AppPackageBuildStatus.FAILED);
							appPackage.setBuildMessage("打包失败");
						}
					}
					if(appPackage.getOsType().equals(OSType.ANDROID)) {
						androidList.add(appPackage);
					} else if(appPackage.getOsType().equals(OSType.IOS)) {
						iosList.add(appPackage);
					}
				}
				appVersion.setAndroidPackages(androidList);
				appVersion.setIosPackages(iosList);
				
				// widget包（每个版本一个）
				List<AppWidget> appWidgetList = appWidgetDao.findByAppVersionIdAndDel(appVersion.getId(), DELTYPE.NORMAL);
				if(appWidgetList != null && appWidgetList.size() > 0) {
					AppWidget widget = appWidgetList.get(0);
					widget.setDownloadUrl(codeZipUrl + "/" + widget.getFileName());
					appVersion.setAppWidget(widget);
				}
				
				// 补丁包
				List<AppPatch> appPatchList = appPatchDao.findByBaseAppVersionIdAndDel(appVersion.getId(), DELTYPE.NORMAL);
				if(appPatchList != null && appPatchList.size() > 0) {
					for(AppPatch patch : appPatchList) {
						patch.setDownloadUrl(codeZipUrl + "/" + patch.getFileName());
						AppVersion baseV   = appVersionDao.findOne(patch.getBaseAppVersionId());
						AppVersion seniorV = appVersionDao.findOne(patch.getSeniorAppVersionId());
						//如果删除了版本AppVersion则补丁包就没有了.
						if(null!=baseV){
							patch.setBaseAppVersionNo(baseV.getVersionNo());
							patch.setBaseAppVersionDescription(baseV.getVersionDescription());
						}
						if(null!=seniorV){
							patch.setSeniorAppVersionNo(seniorV.getVersionNo());
							patch.setSeniorAppVersionDescription(seniorV.getVersionDescription());
						}
					}
					
					appVersion.setAppPatches(appPatchList);
				}
				
				
				User user = userDao.findOne(appVersion.getUserId());
				if(user != null) {
					appVersion.setUserName(user.getUserName());
					appVersion.setUserIcon(user.getIcon());
				}
				CheckInfo checkInfo = new CheckInfo();
				List<CheckInfo> checkInfoList =checkInfoDao.findCheckResultAndCheckFilePathByVersionIdOrderByCreatedAtDesc(appVersion.getId());
				if(checkInfoList!=null&&checkInfoList.size()>0){
					checkInfo = checkInfoList.get(0);
					checkInfo.setCheckFilePath(accessUrl+checkInfo.getCheckFilePath());
				}
					
				appVersion.setCheckInfo(checkInfo);
				//如果是已申请发版的版本，查询申请编号
				if(appVersion.isHaveApplyPublish() && appVersion.getTransId() != null && appVersion.getTransId() > 0){
					Trans tran = transDao.findOne(appVersion.getTransId());
					if(tran != null){
						appVersion.setApplyNum(tran.getApplyNum());
						appVersion.setApplyStatus(tran.getStatus());
						appVersion.setApplyNode(tran.getNode());
					}else{
						appVersion.setApplyNum("-------");
						appVersion.setApplyStatus("");
						appVersion.setApplyNode("");
					}
				}else{
					appVersion.setApplyNum("");
					appVersion.setApplyStatus("");
					appVersion.setApplyNode("");
				}
				Map<String, Object> element = new HashMap<>();
				element.put("object", appVersion);
				message.add(element);
				
			}
		} catch(Exception e) {
			log.info("getAppVersionList -> spread info exception:" + ExceptionUtils.getStackTrace(e));
		}
		
		log.info("getAppVersionList -> message:" + message);
		
		Map<String, Object> messages = new HashMap<>();
		messages.put("list", message);
		messages.put("total", appVersionList.getTotalElements());
		messages.put("appVersionNos", appVersionNos);
		
		return messages;
	}

	private List<String> getAppversionNoList(AppVersion match, long loginUserId) {
		return this.appVersionDao.findAppVersionByAppIdAndUserIdAndDel(match.getAppId(),loginUserId, DELTYPE.NORMAL);
	}
	

	public Map<String, Object> getAppVersion(long appVersionId, long loginUserId) {

		AppVersion appVersion = appVersionDao.findOne(appVersionId);

		if(appVersion == null) {
			return null;
		}
		
		appVersion.setBranchZipUrl(codeZipUrl + "/" + appVersion.getBranchZipName());
		
		Map<String, Object> message = new HashMap<>();
		message.put("object", appVersion);

		return message;
	}
	
	private boolean isVersionExist(String versionNo, long appId,AppVersionType versionType,Long userId) {
		long count = 0l;
		if(versionType.equals(AppVersionType.PROJECT)){
			count = appVersionDao.countByAppIdAndVersionNoAndTypeAndDel(appId, versionNo,versionType, DELTYPE.NORMAL);
			
		}else{
			count = appVersionDao.countByAppIdAndVersionNoAndTypeAndUserIdAndDel(appId, versionNo, versionType, userId, DELTYPE.NORMAL);
		}
		if(count > 0) {
			return true;
		} else {
			return false;
		}
		
	}

	/**
	 * 添加版本（项目版本或个人版本）
	 * @param appVersion
	 * @param loginUserId
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public AppVersion addAppVersion(AppVersion appVersion, long loginUserId) throws ClientProtocolException, IOException {
		
		if(isVersionExist(appVersion.getVersionNo(), appVersion.getAppId(),appVersion.getType(),loginUserId)) {
			throw new RuntimeException("版本号已存在");
		}
		
		App app = appDao.findOne(appVersion.getAppId());
		if(app == null) {
			throw new RuntimeException("应用不存在");
		}
		
		String tagName = null;
		if( AppVersionType.PERSONAL.equals( appVersion.getType() ) ) {
			tagName = String.format( "v_%s_%s", loginUserId, appVersion.getVersionNo() );

		} else if( AppVersionType.PROJECT.equals( appVersion.getType() ) ) {
			tagName = String.format( "v_prj_%s", appVersion.getVersionNo() );
		}
		
		if(tagName == null) {
			throw new RuntimeException("版本类型不正确");
		}

		appVersion.setUserId(loginUserId);
		appVersion.setTagName(tagName);
		appVersionDao.save(appVersion);
		
		// codeZipPath  - 文件存放路径 /usr/local/zipfile/appcode
		// codeZipUrl   - 文件下载路径 http://192.168.1.83:9100/appcode
		// localGitRoot - 版本仓库路径 /home/gitroot

		/*// 检出到打包分支,并建立标签为版本号
		String cmd = "sh "+shellPath+"coopdev_git/addTag.sh " + app.getRelativeRepoPath() + " " + appVersion.getBranchName() + "  " + tagName;
		this.execShell(cmd);
		
		// 生成code压缩包
		String branchZipName = String.format("code_%d_%d_%s.zip", app.getId(), appVersion.getId(), appVersion.getBranchName());
		String src = localGitRoot + app.getRelativeRepoPath();
		String dest = codeZipPath + "/" + branchZipName;
		Set<String> exceptList = new HashSet<>();
		exceptList.add(src + "/.git");
		ZipUtil.zipExcept(src, dest, exceptList);

		// 创建版本时不再考虑一同生成widget包，将单独创建
		appVersion.setBranchZipName(branchZipName);*/
		Map<String,String> params = new HashMap<String,String>();
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		params.put("branchName", appVersion.getBranchName());
		params.put("tagName", tagName);
		params.put("appId",  app.getId().toString());
		params.put("appVersionId",appVersion.getId().toString());
		
		log.info(params.toString());
		long requestStart = System.currentTimeMillis();
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/version/create", params);
		long requestEnd = System.currentTimeMillis();
		log.info("addAppVersion_requestTime:"+(requestEnd-requestStart));
		log.info(String.format("GitAction -> addAppVersion --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException("发布版本失败");
		}
		JSONObject objMsg = obj.getJSONObject("message");
		appVersion.setBranchZipName(objMsg.getString("branchZipName"));
		appVersionDao.save(appVersion);
		return appVersion;

	}

	public AppVersion saveAppversion(AppVersion version){
		return appVersionDao.save(version);
	}
	public int editAppVersion(AppVersion appVersion) {
		String settings = "";
		if(appVersion.getVersionNo() != null) {
			settings += String.format(",versionNo='%s'", appVersion.getVersionNo());
		}
		if(appVersion.getVersionDescription() != null) {
			settings += String.format(",versionDescription='%s'", appVersion.getVersionDescription());
		}
		if(settings.length() > 0) {
			settings = settings.substring(1);
		} else {
			return 0;
		}
		String sql = "update T_APP_VERSION set " + settings + " where id=" + appVersion.getId();
		return this.jdbcTpl.update(sql);

	}
	
	public void removeAppVersion(List<Long> appVersionIdList) throws ClientProtocolException, IOException {
		for(Long appVersionId : appVersionIdList) {
			log.info(String.format("removeAppVersion appVersionId[%d]", appVersionId));
			AppVersion appVer = appVersionDao.findOne(appVersionId);
			App app = appDao.findOne(appVer.getAppId());
			String repoPath = app.getRelativeRepoPath();
			String tagName = appVer.getTagName();
			/*String cmd = "sh "+shellPath+"coopdev_git/delTag.sh " + repoPath + " " + tagName;
			String ret = this.execShell(cmd);
			log.info(String.format("del Tag cmd[%s] ret[%s]", cmd, ret));*/
			Map<String,String> params = new HashMap<String,String>();
			params.put("relativeRepoPath", repoPath);
			params.put("tagName", tagName);
			log.info(params.toString());
			String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/version/delete", params);
			log.info(String.format("GitAction -> removeAppVersion --> shell for jsonStr[%s]", jsonStr));
			JSONObject obj = JSONObject.fromObject( jsonStr );
			if(!"success".equals(obj.getString("status"))){
				throw new RuntimeException("删除版本失败");
			}
			//这块如果物理删除了数据,查询版本列表获取补丁包的时候,根据补丁包查询对应的appVersion时候就空指针报错了
//			appVersionDao.delete(appVersionId);
			List<AppPackage> apList=this.appPackageDao.findByAppVersionIdAndDel(appVersionId,DELTYPE.NORMAL);
			for(AppPackage ap:apList){
				this.appPackageDao.delete(ap);
			}
			appVer.setDel(DELTYPE.DELETED);
			appVersionDao.save(appVer);
			
		}
	}

	//***************************************************
	//    AppPackage CRUD & build interfaces            *
	//***************************************************
	public List< Map<String, Object> > getAppPackageList(Pageable pageable, long appVersionId, OSType osType, long loginUserId) {
		List< Map<String, Object> > message = new ArrayList<>();
		
		List<AppPackage> appPackageList = appPackageDao.findByAppVersionIdAndOsTypeAndDel(appVersionId, osType, DELTYPE.NORMAL);
	
		for(AppPackage appPackage : appPackageList) {
			JSONObject settings = JSONObject.fromObject(appPackage.getBuildJsonSettings());
			appPackage.setSettings(settings);

			Map<String, Object> element = new HashMap<>();
			element.put("object", appPackage);
			message.add(element);
			
		}
		return message;
	}

	public Map<String, Object> getAppPackage(long appPackageId, long loginUserId) {

		AppPackage pack = appPackageDao.findOne(appPackageId);

		if(pack == null) {
			return null;
		}

		JSONObject settings = new JSONObject(); 
		settings = JSONObject.fromObject(pack.getBuildJsonSettings());
		

		if(settings.containsKey("cert")){//原生应用
			AppVersion appVersion= appVersionDao.findOne(pack.getAppVersionId());
			App app = appDao.findOne(appVersion.getAppId());
			settings.put("iconPath", app.getIcon());//增加应用图标;注意如果app.getIcon()为空,则最终也不会有iconPath
			pack.setSettings(settings);
		}else{//混合应用
			pack.setSettings(settings);
			try {
				// 扩展插件信息
				JSONObject plugin = settings.getJSONObject("plugin");
				JSONArray versionIdList = plugin.getJSONArray("versionIdList");
				JSONArray versionList = new JSONArray();
				Iterator<?> it = versionIdList.iterator();
				List<PackagePluginInfo> listPluginInfo = new ArrayList<PackagePluginInfo>();
				while(it.hasNext()) {
					String pvIdStr = (String)it.next();
					if("".equals(pvIdStr)) {
						continue;
					}
					long pvId = Long.parseLong(pvIdStr);
					PluginVersion pv = pluginVersionDao.findOne(pvId);
					if(! ( pack.getOsType().equals(pv.getOsType()) ) ) {
						continue;
					}
					
					Plugin p = pluginDao.findOne(pv.getPluginId());
//				Map<String, Object> map = new HashMap<>();
					PackagePluginInfo info = new PackagePluginInfo();
//				map.put("pluginVersionId", pvId);
//				map.put("pluginVersionNo", pv.getVersionNo());
//				map.put("pluginCnName", p.getCnName());
//				map.put("pluginEnName", p.getEnName());
//				map.put("pluginType", p.getType().name());
					info.setPluginVersionId(pvId);
					info.setPluginVersionNo(pv.getVersionNo());
					info.setPluginCnName( p.getCnName());
					info.setPluginEnName(p.getEnName());
					info.setPluginType(p.getType().name());
					listPluginInfo.add(info);
				}
//			versionList.add(map);
				Collections.sort(listPluginInfo);
				if(null!=listPluginInfo && listPluginInfo.size()>0){
					for(PackagePluginInfo p:listPluginInfo){
						versionList.add(p);
					}
				}
//			versionList.add(listPluginInfo);
				plugin.put("pluginVersionList", versionList);
				
				// 扩展引擎信息
//			 "engine": {
//		        "wgOneVersion_ios": "163",
//		        "wgOneVersion_android": "144"
//		    },
				JSONObject engineObj = settings.getJSONObject("engine");
				
				long engineId = -1;
				if( pack.getOsType().equals(OSType.ANDROID) ) {
					engineId = Long.parseLong( engineObj.getString("wgOneVersion_android") );
				} else if( pack.getOsType().equals(OSType.IOS) ) {
					engineId = Long.parseLong( engineObj.getString("wgOneVersion_ios") );
				}
				Engine engine = engineDao.findOne(engineId);
				engineObj.put("type", engine.getType());
				engineObj.put("versionNo", engine.getVersionNo());
				
			} catch(Exception e) {
				e.printStackTrace();
				;
			}
		}
		
		
		
//		JSONObject pluginObj = settings.getJSONObject("plugin");
//		JSONArray versionIdArray = pluginObj.getJSONArray("versionIdList");
//		List<String> versionIdList = new ArrayList<>();
//		for(Object obj : versionIdArray) {
//			versionIdList
//		}
		
		AppVersion appVersion = appVersionDao.findOne(pack.getAppVersionId());
		if(appVersion != null) {
			pack.setAppVersionNo(appVersion.getVersionNo());
			pack.setAppVersionDescription(appVersion.getVersionDescription());
			pack.setBranchZipUrl(codeZipUrl + "/" + appVersion.getBranchZipName());
			App app = appDao.findOne(appVersion.getAppId());
			if(app != null) {
				pack.setRemoteRepoPath(this.remoteGitRoot + app.getRelativeRepoPath());
			}
			//如果打包的时候,没有填写新的appid和appkey则需要返回应用的appid和appKey
			if(StringUtils.isBlank(pack.getNewAppCanAppId())){
				pack.setNewAppCanAppId(app.getAppcanAppId());
			}
			if(StringUtils.isBlank(pack.getNewAppCanAppKey())){
				pack.setNewAppCanAppKey(app.getAppcanAppKey());
			}
		}
		
		Map<String, Object> message = new HashMap<>();
		
		message.put("object", pack);

		return message;
	}
	
	/**
	 * 创建安装包
	 * @param loginUserId
	 * @param versionNo
	 * @param versionDescription
	 * @param buildType
	 * @param buildJsonSettings
	 * @param appVersionId
	 * @param appId
	 * @param branchName
	 * @return
	 */
	public AppPackage addAppPackage(long loginUserId, String buildJsonSettings,String newAppCanAppId,String newAppCanAppKey) {
		// 转换并解析前端传递的打包配置
		log.info(String.format("AppService -> addAppPackage -> buildJsonSettings original : %s", buildJsonSettings));
		buildJsonSettings = buildJsonSettings.replace("\\\"", "\"");
		buildJsonSettings = buildJsonSettings.replace("\"{", "{");
		buildJsonSettings = buildJsonSettings.replace("}\"", "}");
		log.info(String.format("AppService -> addAppPackage -> buildJsonSettings proceeded : %s", buildJsonSettings));
		JSONObject settings = JSONObject.fromObject(buildJsonSettings);
		log.info(String.format("AppService -> addAppPackage -> buildJsonSettings -> settings : %s", settings));


		// 创建包的必要参数buildType, appVersionId
		JSONObject parameters = settings.getJSONObject("paramters");

		String buildTypeStr = (String)parameters.get("buildType");
		if(buildTypeStr == null) {
			log.info("AppService -> addAppPackage -> buildJsonSettings -> parameters - > buildType not found");
			return null;
		}
		AppPackageBuildType buildType = AppPackageBuildType.PRODUCTION.name().equals(buildTypeStr) ? AppPackageBuildType.PRODUCTION : AppPackageBuildType.TESTING;		
		
		long appVersionId = -1;
		String appVersionIdStr = (String)parameters.get("appVersionId");
		try {
			if(appVersionIdStr != null && !"".equals(appVersionIdStr)) {
				appVersionId = Long.parseLong(appVersionIdStr);
			}
		} catch(NumberFormatException nfe) {
			;	// 不做处理，前端可能传递"";
		}

		AppVersion appVersion = null;	// 创建包时，代码版本必须存在

		appVersion = appVersionDao.findOne(appVersionId);
		if(appVersion == null) {
			log.info("AppService -> addAppPackage -> appVersion with id= " + appVersionId + " not found");
			return null;
		}
		App app = appDao.findOne(appVersion.getAppId());
		if(app == null) {
			log.info("AppService -> addAppPackage -> app with id= " + appVersion.getAppId() + " not found");
			return null;
		}
		//更新app的icon
		log.info("start to updating app icon");
		if(appVersion.getType().compareTo(AppVersionType.PROJECT)==0){
			JSONObject icon = settings.getJSONObject("icon");
			if(null!=icon && null!=icon.getString("iconPath") && !"".equals(icon.getString("iconPath"))){
				app.setIcon(icon.getString("iconPath"));
				appDao.save(app);
				log.info("updated app icon to :"+icon.getString("iconPath"));
			}
		}
		
		AppPackage pack = new AppPackage();
		String versionNo = (String)parameters.get("versionNo");
		if (!versionNo.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
			throw new RuntimeException("版本号不合法");
		}
		String versionDescription = (String)parameters.get("versionDescription");
		pack.setVersionNo(versionNo);
		pack.setVersionDescription(versionDescription);
		
//		打包结束更新的参数
//		private long fileSize;
//		private String downloadUrl;
		
		
		JSONObject icon = settings.getJSONObject("icon");
		JSONObject statusBar = settings.getJSONObject("statusBar");
		JSONObject startSet = settings.getJSONObject("startSet");
		JSONObject engine = settings.getJSONObject("engine");
		JSONObject plugin = settings.getJSONObject("plugin");
		JSONObject packageObj = settings.getJSONObject("packageObj");
		JSONObject switchObj = settings.getJSONObject("switchObj");
		JSONObject certificate = settings.getJSONObject("certificate");
		
		// 前端提交的参数
		pack.setBuildType(buildType);
		pack.setBuildJsonSettings(buildJsonSettings);
		pack.setUserId(loginUserId);
		pack.setAppVersionId(appVersion.getId());
		// 创建时默认生成的参数
		pack.setBuildMessage("正在构建中");
		pack.setBuildStatus(AppPackageBuildStatus.ONGOING);
		
		// 在buildJsonSettings中获取的参数
		String channelCode = packageObj.getString("channelCode");
		String ptSelected = packageObj.getString("ptSelected");
		String hardwareAccelerated = packageObj.getString("hardwareAccelerated");
		OSType osType = "ANDROID".equals(ptSelected.toUpperCase()) ? OSType.ANDROID : OSType.IOS;
		String terminalType = packageObj.getString("terminalType");
		String pushIF = (String)switchObj.get("pushIF");
		terminalType = (terminalType == null ? TerminalType.IPHONE.name() : terminalType);
		
		String increUpdateIF = switchObj.containsKey("increUpdateIF")?switchObj.getString("increUpdateIF"):"0";

		Project project = projectDao.findOne(app.getProjectId());
		//如果是3.0打包,pushIF,和increUpdateIF需要从packageObj里面取
		if(project.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)
				|| project.getBizLicense().equals(PROJECT_BIZ_LICENSE.BINDING)  ) {
			pushIF = packageObj.containsKey("pushIF")?(String)packageObj.get("pushIF"):"0";
			increUpdateIF = packageObj.containsKey("increUpdateIF")?packageObj.getString("increUpdateIF"):"0";
		}
		pushIF = (pushIF == null ? "0" : pushIF);
		pack.setChannelCode(channelCode);
		pack.setOsType(osType);
		pack.setTerminalType(TerminalType.valueOf(terminalType));
		pack.setPushIF(Integer.parseInt(pushIF));
		if(null!=hardwareAccelerated && ("true".equals(hardwareAccelerated) || "1".equals(hardwareAccelerated))){
			pack.setHardwareAccelerated(1);
		}else{
			pack.setHardwareAccelerated(0);
		}
		pack.setIncreUpdateIF(Integer.parseInt(increUpdateIF));
		pack.setUpdateSwith(Integer.parseInt(increUpdateIF));
		pack.setPublised(IfStatus.NO);
		pack.setPublisedTest(IfStatus.NO);
		pack.setPublisedAppCan(IfStatus.NO);
		
		pack.setNewAppCanAppId(null!=newAppCanAppId?newAppCanAppId.trim():newAppCanAppId);
		pack.setNewAppCanAppKey(null!=newAppCanAppKey?newAppCanAppKey.trim():newAppCanAppKey);
		
		
		// 版本 + 平台 + 终端类型 + 渠道 + 包类型（测试包|正式包）不可重复
		// osType + terminalType + channelCode + buildType
				
		String sql = "select p.versionNo from T_APP_PACKAGE as p left join T_APP_VERSION as v on p.appVersionId=v.id "
				+ "where v.appId= " + app.getId() + " "
						+ "and p.versionNo='"	+ pack.getVersionNo() + "' "
						+ "and p.osType="			+ pack.getOsType().ordinal() + "  "
						+ "and p.terminalType="	+ pack.getTerminalType().ordinal() + "  "
						+ "and p.channelCode='"	+ pack.getChannelCode() + "'  "
						+ "and p.buildType="		+ pack.getBuildType().ordinal() + "  "
						+ "and p.del=" + DELTYPE.NORMAL.ordinal();
		List<?> tbl = this.jdbcTpl.queryForList(sql);

		if(tbl != null && tbl.size() > 0) {
			throw new RuntimeException("已存在同类型的安装包");

		}
		
		// 添加新版本
		appPackageDao.save(pack);
		pack.setSettings(settings);	// settings并不存储至数据库，为@Transient字段
		String pkgRequest = makePkgRequest(pack);
		appPackageDao.save(pack);
		this.sendPkgRequest(pkgRequest);
		
		return pack;
	}

	
	/**
	 * 
	 * @param loginUserId
	 * @param buildJsonSettings
	 * 	typeCert:publishCert,developeCert,adhocCert//证书类型
		//iosPub1,   //发布证书-> p12
		//iosPub2 ,  //发布证书说明文件 -> mobileprovision
		//iosPub3 ,  // 发布证书iphoneAppIds
		//iosPub4 ,  //发布证书密码
		 * 
		//iosEnterprice1   //企业证书 -> p12
		//iosEnterprice2  //企业证书说明文件 -> mobileprovision
		//iosEnterprice3  //企业证书 iphoneAppIds
		//iosEnterprice4  //企业证书证书密码
		 * 
		 * //adhoc1,   //adhoc证书 -> p12
		//adhoc2 ,  //adhoc证书说明文件 -> mobileprovision
		//adhoc3 ,  // adhoc证书iphoneAppIds
		//adhoc4 ,  //adhoc证书密码
		 * 
		//"scheme":"",            //打包scheme名称(用户填写)
		//"projectname":"",      //主工程名称(用户填写)
		//"appVersionId":12,    //版本ID(基于哪个版本打包的)
		//"buildTypeStr":"TESTING 或者 PRODUCTION",//正式包,测试包
		//"versionDescription":"版本描述"
		{"cert":{"typeCert":"","appCertPwd":"","appCertURL":"","iosPub1":"","iosPub2":"","iosPub3":"","iosPub4":"",
		      "iosEnterprice1":"","iosEnterprice2":"","iosEnterprice3":"","iosEnterprice4":"",
		      "adhoc1":"","adhoc2":"","adhoc3":"","adhoc4":""},
			  "scheme":"",       
			  "projectname":"", 
			  "appVersionId":12, 
			  "buildTypeStr":"",
			  "versionDescription":"版本描述"
	  	}
	 * @return
	 */
	public AppPackage addAppPackageForNative(long loginUserId, 
			String buildJsonSettings
			) {

		log.info("native package buildJsonSettings >>>"+buildJsonSettings);
		JSONObject json = JSONObject.fromObject(buildJsonSettings);
		Long appVersionId = json.getLong("appVersionId");
		String buildTypeStr = json.getString("buildTypeStr");
		String versionDescription = json.getString("versionDescription");
		AppPackageBuildType buildType = AppPackageBuildType.valueOf(buildTypeStr); 
		

		AppVersion appVersion = null;	// 创建包时，代码版本必须存在

		appVersion = appVersionDao.findOne(appVersionId);
		if(appVersion == null) {
			log.info("AppService -> addAppPackage -> appVersion with id= " + appVersionId + " not found");
			return null;
		}
		App app = appDao.findOne(appVersion.getAppId());
		if(app == null) {
			log.info("AppService -> addAppPackage -> app with id= " + appVersion.getAppId() + " not found");
			return null;
		}
		
		AppPackage pack = new AppPackage();
		pack.setVersionDescription(versionDescription);
		
		
		// 前端提交的参数
		pack.setBuildType(buildType);
		pack.setBuildJsonSettings(buildJsonSettings);
		pack.setUserId(loginUserId);
		pack.setAppVersionId(appVersion.getId());
		// 创建时默认生成的参数
		pack.setBuildMessage("正在构建中");
		pack.setBuildStatus(AppPackageBuildStatus.ONGOING);
		
		//设置终端类型,native包要么是Android，要么是iphone
		if(app.getPlatForm().equals("Android")){
			pack.setTerminalType(TerminalType.ANDROID);
		}else if(app.getPlatForm().equals("iOS")){
			pack.setTerminalType(TerminalType.IPHONE);
		}
		
		
		OSType osType = "ANDROID".equals(app.getPlatForm().toUpperCase()) ? OSType.ANDROID : OSType.IOS;
		
		pack.setOsType(osType);
		pack.setPublised(IfStatus.NO);
		pack.setPublisedTest(IfStatus.NO);
		pack.setPublisedAppCan(IfStatus.NO);
		
		
		// 添加新版本
		appPackageDao.save(pack);
		JSONObject settings = JSONObject.fromObject(buildJsonSettings);
		pack.setSettings(settings);	// settings并不存储至数据库，为@Transient字段
		String pkgRequest = makePkgRequestForNative(pack);
//		appPackageDao.save(pack);
		this.sendPkgRequest(pkgRequest);
		
		return pack;
	}
	public int editAppPackage(AppPackage appPackage) {

		appPackageDao.save(appPackage);
		return 1;

	}
	public List<AppPackage> getAppPackageList(long appVersionId) {
		return appPackageDao.findByAppVersionIdAndDelOrderByCreatedAtDesc(appVersionId,DELTYPE.NORMAL);
	}
	
	public void removeAppPackage(long appPackageId) {
		
		// 现在个人代码版本需要保留，删除测试包时不需要删除代码版本
		
		appPackageDao.delete(appPackageId);
	}
	
	public int editBuildInfo(long appPackageId, String type, String downloadUrl, long fileSize, AppPackageBuildStatus buildStatus, String buildMessage,String versionNo) {
		log.info(String.format("AppService -> editBuildInfo -> appPackageId[%d],type[%s],downloadUrl[%s],fileSize[%d],buildStatus[%s], buildMessage[%s],versionNo[%s]", appPackageId, type, downloadUrl,  fileSize,  buildStatus,  buildMessage,versionNo));		
		if("app".equals(type)) {
			//春梁那边目前无法判断 fileSize=0的是否成功失败，我们这边先不接受fileSize=0的打包结果
			if(fileSize<=0){
				return 0;
			}
			
			if(downloadUrl.toLowerCase().endsWith("apk") || downloadUrl.toLowerCase().endsWith("zip")) {
//				String sql = String.format("update T_APP_PACKAGE set fileSize=%d, downloadUrl='%s', qrCode='%s', buildStatus=%d, buildMessage='%s', updatedAt='%s' where id=%d",
//						fileSize/1024, downloadUrl, downloadUrl, buildStatus.ordinal(), buildMessage,new Timestamp(System.currentTimeMillis()), appPackageId);
				
//				int affected = this.jdbcTpl.update(sql);

				AppPackage pack = appPackageDao.findOne(appPackageId);
				pack.setFileSize(fileSize/1024);
				pack.setDownloadUrl(downloadUrl);
				pack.setQrCode(xietongHost+"/cooldev/qr?data="+downloadUrl);
				pack.setBuildStatus(buildStatus);
				pack.setBuildMessage(buildMessage);
				pack.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				if(StringUtils.isNotBlank(versionNo)){
					pack.setVersionNo(versionNo);
				}
				appPackageDao.save(pack);
				return 1;
			} else if(downloadUrl.toLowerCase().endsWith("ipa")) {
				AppPackage pack = appPackageDao.findOne(appPackageId);
				AppVersion version = appVersionDao.findOne(pack.getAppVersionId());
				App app = appDao.findOne(version.getAppId());
				
				// 获取pack配置信息
				String buildJsonSettings = pack.getBuildJsonSettings();
				JSONObject settings = JSONObject.fromObject(buildJsonSettings);
				
				String bundleIdentifier = "com.zywx.appcan" + app.getAppcanAppId(); 
				String displayImage = "";
				String fullSizeImage = "";
				if(settings.containsKey("cert")){//原生打包
					JSONObject cert = settings.getJSONObject("cert");
					String iphoneAppIds = cert.getString("iosPub3");
					String testCertFileId = cert.getString("iosEnterprice3");
					String adhocCertFileId = cert.getString("adhoc3");
					String typeCert = cert.getString("typeCert");
					if("publishCert".equals(typeCert)) {
						bundleIdentifier = iphoneAppIds;
					} else if("developeCert".equals(typeCert)) {
						bundleIdentifier = testCertFileId;
					}	else if("adhocCert".equals(typeCert)){
						bundleIdentifier = adhocCertFileId;
					}
					displayImage = app.getIcon();
					fullSizeImage = app.getIcon();
					//TODO 这里能取到icon吗
				}else{//混合打包
					JSONObject certificate = settings.getJSONObject("certificate");
					JSONObject packageObj = settings.getJSONObject("packageObj");
					String typeCert = packageObj.getString("typeCert");
					// 苹果证书设定
					// 发布：publishCert  -> 发布 -> absSignatureFileLoc
					// 开发：developeCert -> 企业 -> absTestSignatureFileLoc
					// 越狱：noCert
					// 解析certificate配置		
					String iphoneAppIds = certificate.getString("iosPub3");				// iphoneAppIds
					String testCertFileId = certificate.getString("iosEnterprice3");
					if("publishCert".equals(typeCert) ||"adhocCert".equals(typeCert)) {
						bundleIdentifier = iphoneAppIds;
					} else if("developeCert".equals(typeCert)) {
						bundleIdentifier = testCertFileId;
					}				
					// icon信息
				    JSONObject icon = settings.getJSONObject("icon");
			 		String iconPath = icon.getString("iconPath");
			 		String absIconLoc = (iconPath == null ? "" : iconPath);
				    
					displayImage = absIconLoc;
					fullSizeImage = absIconLoc;
				}
				
			    pack.setFileSize(fileSize/1024);
				pack.setDownloadUrl(downloadUrl);
				pack.setQrCode(xietongHost+"/cooldev/qr?data="+"itms-services://?action=download-manifest&url=" + this.plistBaseUrl + "/install_" + appPackageId + ".plist");
				pack.setBuildStatus(buildStatus);
				pack.setBuildMessage(buildMessage);
				pack.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				if(StringUtils.isNotBlank(versionNo)){
					pack.setVersionNo(versionNo);
				}
				

				String xmlStr = "<?xml version='1.0' encoding='UTF-8'?><!DOCTYPE plist PUBLIC '-//Apple//DTD PLIST 1.0//EN' 'http://www.apple.com/DTDs/PropertyList-1.0.dtd'><plist version='1.0'>";
				xmlStr += "<dict><key>items</key><array><dict><key>assets</key><array>";
				// 填写下载地址
				xmlStr += "<dict><key>kind</key><string>software-package</string><key>url</key><string>" + downloadUrl + "</string></dict>";
				// 填写display-image
				xmlStr += "<dict><key>kind</key><string>display-image</string><key>needs-shine</key><true/><key>url</key><string>" + displayImage + "</string></dict>";
				// 填写full-size-image
				xmlStr += "<dict><key>kind</key><string>full-size-image</string><key>needs-shine</key><true/><key>url</key><string>" + fullSizeImage + "</string></dict>";
				xmlStr += "</array><key>metadata</key><dict>";					
				// 填写其他参数
				xmlStr += "<key>bundle-identifier</key><string>" + bundleIdentifier + "</string>";
				xmlStr += "<key>bundle-version</key><string>" + pack.getVersionNo() + "</string>";
				xmlStr += "<key>kind</key><string>software</string>";
				xmlStr += "<key>subtitle</key><string>" + app.getName() + "</string>";
				xmlStr += "<key>title</key><string>" + app.getName() + "</string>";
				xmlStr += "</dict></dict></array></dict></plist>";
				File plist = new File(this.plistRoot + "/install_" + appPackageId + ".plist");
				try {
					plist.createNewFile();
					PrintWriter pw = new PrintWriter(new FileWriter(plist));
					pw.print(xmlStr);
					pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				
				appPackageDao.save(pack);
				log.info("ipa---->"+appPackageId);
				return 1;
			
			} else {
				return 0;

			}

		} else if("log".equals(type)) {
			
			AppPackage pack = appPackageDao.findOne(appPackageId);
			if(pack == null) {
				log.info("AppService -> editBuildInfo -> type[log] AppPackage not found with appPackageId=" + appPackageId);
				return 0;
			}
			
			//----------------------add by wjj---------------begin---------
			if(fileSize<=0){
				pack.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				pack.setBuildStatus(AppPackageBuildStatus.FAILED);
				pack.setBuildMessage("构建失败["+fileSize+"]");
				pack.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				//pack.setFileSize(fileSize);//由于前台会显示此值,暂不记录,失败时候会为一个负数
			}
			//----------------------add by wjj--------------end----------
//			long createdAtMills = pack.getUpdatedAt().getTime();
//			long currentMills = new Date().getTime();
//			long duration = currentMills - createdAtMills;
//			if(duration > 5 * 1000 && pack.getBuildStatus().equals(AppPackageBuildStatus.ONGOING)) {
//				pack.setBuildStatus(AppPackageBuildStatus.FAILED);
//				pack.setBuildMessage("打包失败");
//			}
			if(StringUtils.isNotBlank(versionNo)){
				pack.setVersionNo(versionNo);
			}
			pack.setBuildLogUrl(downloadUrl);
			appPackageDao.save(pack);
			return 1;
			
		} else {
			return 0;

		}

	}
	
	public String getPkgRequest(long appPackageId) {
		AppPackage pack = appPackageDao.findOne(appPackageId);
		String buildJsonSettings = pack.getBuildJsonSettings();
		JSONObject settings = JSONObject.fromObject(buildJsonSettings);
		pack.setSettings(settings);
		return this.makePkgRequest(pack);
	}
	
	public int getWaitTotalFromRabbitMQ() {
		ConnectionFactory factory = new ConnectionFactory();
 		factory.setHost(rabbitMqHost);
 		factory.setPort(rabbitMqPort);
	 	factory.setUsername(rabbitMqUser);
	 	factory.setPassword(rabbitMqPassword);
 		
 		
 		Connection connection = null;
 		Channel channel = null;
		try {
 			connection = factory.newConnection();
 			channel = connection.createChannel();
 			
 			channel.exchangeDeclare(rabbitMqExchange, "fanout",true);
 		    DeclareOk  dok =channel.queueDeclare(rabbitMqQueue, true, false, false, null);
 			
 			return dok.getMessageCount();

 		} catch (IOException e) {
 			log.info("getWaitTotalFromRabbitMQ -> Rabbmit IOException host:" + factory.getHost() + " port:" + factory.getPort());

 		} catch (TimeoutException e) {
			e.printStackTrace();
		} finally {
			boolean isChannelOpen = channel.isOpen();
			boolean isConnectionOpen = connection.isOpen();
			
			log.info(String.format("getWaitTotalFromRabbitMQ -> finally -> isChannelOpen[%s] isConnectionOpen[%s]", isChannelOpen, isConnectionOpen));
			
 		    try {
 		    	if( isChannelOpen ) {
 		    		channel.close();
 		    	}
 		    	
 		    	if( isConnectionOpen ) {
 		    		connection.close();
 		    	}
 		    	
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
 		    
		}
		return -1;
	}
	
	/**
	 * 检查ios证书（配置打包时）
	 * @param p12
	 * @param password
	 * @param mobileprovision
	 * @return
	 */
	public String validateIOSCert(String p12, String password, String mobileprovision) {
		
		log.info(String.format("AppService -> validateIOSCert -> p12[%s] password[%s] mobileprovision[%s]", p12, password, mobileprovision));
		
		String p12Path = certFileRoot + p12.replaceAll(certBaseUrl, "");
		String mobileprovisionPath = certFileRoot + mobileprovision.replaceAll(certBaseUrl, "");
		
		String commond = String.format("sh "+shellPath+"coopdev_cert/getcertinfo.sh %s %s PKCS12", p12Path, password);
		
		String cmdRet = this.execShell(commond);
		
		log.info("AppService -> validateIOSCert -> commond[" + commond + "] cmdRet[" + cmdRet + "]");
		
		if(cmdRet.indexOf("所有者") == -1 && cmdRet.indexOf("Owner") == -1) {
			return null;
		}
		
		// 获取appId
		BufferedReader in = null;
		try {
			File file = new File(mobileprovisionPath);
			in = new BufferedReader(new FileReader(file));
			String key = "<key>application-identifier</key>";
			String line = null;
			while((line = in.readLine()) != null) {
				if(line.indexOf(key) != -1) {
					log.info("AppService -> validateCert -> find line -> " + line);
					// 找到key描述，直接读取下一行
					line = in.readLine();
					if(line == null) {
						return null;	// 下一行不存在，文件不合法
					}
					//<string>2GM7ZL62GN.com.tcl.mo</string>
					line = line.trim();
					int start = line.indexOf(">");
					int end = line.lastIndexOf("<");
					if(start != -1 && end != -1 && end > start) {
						String val = line.substring(start + 1, end);
						int idx = val.indexOf(".");
						if(idx != -1) {
							val = val.substring(idx + 1);
							log.info("AppService -> validateCert -> find val -> " + val);
							return val;
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return null;
	}
	/**
	 * 检查Andorid证书（配置打包时）
	 * @param p12
	 * @param password
	 * @param mobileprovision
	 * @return
	 */
	public String validateAndroidCert(String androidKeyPassword, String androidStorePassword, String androidCertUrl) {
		
		log.info(String.format("AppService -> validateCert -> androidKeyPassword[%s] androidStorePassword[%s] andoridCertUrl[%s]",
				androidKeyPassword, androidStorePassword, androidCertUrl));

		String certPath = certFileRoot + androidCertUrl.replaceAll(certBaseUrl, "");
		String commond = String.format("sh "+shellPath+"coopdev_cert/getcertinfo.sh %s %s JKS", certPath, androidStorePassword);
		String cmdRet = this.execShell(commond);
		
		log.info("AppService -> validateCert -> getcertinfo -> commond:[" + commond + "] cmdRet:" + cmdRet);
		
		int idxCn = cmdRet.indexOf("别名");
		int idxEn = cmdRet.indexOf("Alias name");
		
		if(idxCn == -1 && idxEn == -1) {
			return null;
		}
		
		String aliasName = null;
		String[] lines = cmdRet.split("\n\r");
		log.info("AppService -> validateCert -> getcertinfo -> lines.length -> " + lines.length);
		for(String line : lines) {
			log.info("AppService -> validateCert -> getcertinfo -> lines -> " + line);
			if(line.startsWith("别名") || line.startsWith("Alias name")) {
				int idx = line.indexOf(":");
				log.info("AppService -> validateCert -> getcertinfo -> found -> idx -> " + idx);
				if(idx != -1) {
					aliasName = line.substring(idx + 1).trim(); 
				}
			}
		}
		
		if(aliasName == null || "".equals(aliasName)) {
			return null;
		}
		
		//checkandcert.sh $and_cert $android_sotre_pass $android_key_pass $aliasName
		String checkCommand = String.format("sh "+shellPath+"coopdev_cert/checkandcert.sh %s %s %s %s",
				certPath, androidStorePassword, androidKeyPassword, aliasName);
		
		log.info(String.format("AppService -> validateCert -> checkandcert -> cmd[%s]", checkCommand));
		String checkRet = this.execShell(checkCommand);
		if(checkRet.indexOf("jar signed") != -1 || checkRet.indexOf("jar 已签名") != -1) {
			return aliasName;
		} else {
			return null;
		}
		
		
		
	}

	public String generateAndroidCert(
			String aliasName,
			String keypass,
			String storepass,
			String duration,
			String username,
			String department,
			String company,
			String city,
			String province,
			String country) {
		
		if(aliasName == null || aliasName.equals("")) {
			return null;
		}
		if(storepass == null || storepass.equals("")) {
			return null;
		}
		if(keypass == null || keypass.equals("")) {
			keypass = storepass;
		}
		if(duration == null || duration.equals("")) {
			duration = "" + (365 * 99);
		}
		
		if(username == null || username.equals("")) {
			username = "unknown";
		}
		if(department == null || department.equals("")) {
			department = "unknown";
		}
		if(company == null || company.equals("")) {
			company = "unknown";
		}
		if(city == null || city.equals("")) {
			city = "unknown";
		}
		if(province == null || province.equals("")) {
			province = "unknown";
		}
		if(country == null || country.equals("")) {
			country = "unknown";
		}

		String hex = Long.toHexString(System.nanoTime());
		String localPath = certFileRoot + "/" + hex + ".cer";
		String downloadUrl = certBaseUrl + "/" + hex + ".cer";
		
		log.info(String.format("AppService -> generateAndroidCert %s %s %s %s %s %s %s %s %s %s %s", 
				aliasName, keypass, storepass, duration, localPath, username, department, company, city, province, country));
		
		String commond = String.format("sh "+shellPath+"coopdev_cert/createcert.sh %s %s %s %s %s %s %s %s %s %s %s",
				aliasName, keypass, storepass, duration, localPath, username, department, company, city, province, country);
		String cmdRet = this.execShell(commond);
		
		log.info("AppService -> generateAndroidCert -> " + cmdRet);
		
		return downloadUrl;		
	}
	
	/**
	 * 添加Widget包
	 * @param widget
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public AppWidget addAppWidget(AppWidget widget) throws ClientProtocolException, IOException {
		
		if(widget == null) {
			throw new RuntimeException("添加请求不正确:widget为null");
		}
		
		AppVersion appVersion = appVersionDao.findOne(widget.getAppVersionId());
		if(appVersion == null) {
			throw new RuntimeException("代码版本不存在 appVersionId:" + widget.getAppVersionId());
		}
		
		log.info(String.format("addAppWidget -> widget %s", widget));
		
		App app = appDao.findOne(appVersion.getAppId());
		if(app == null) {
			throw new RuntimeException("应用不存在");
		}
		/*
		String src = localGitRoot + app.getRelativeRepoPath();
		Set<String> exceptList = new HashSet<>();
		exceptList.add(src + "/.git");
		
		//widget包
		String copyCode = "sh "+shellPath+"coopdev_git/copyCode.sh " + localGitRoot+app.getRelativeRepoPath() + "/*  " + copyCodeDir + "/" + app.getAppcanAppId() + "/" + app.getAppcanAppId();
		String ret = this.execShell(copyCode);
		log.info(String.format("addAppWidget -> runCommand cmd[%s] ret[%s]", copyCode, ret));
		
		String widgetBranchZipName = String.format("widget_code_%d_%d_%s.zip", app.getId(), appVersion.getId(), appVersion.getBranchName());
		String srcWidget = copyCodeDir + "/" + app.getAppcanAppId();
		String destWidget = codeZipPath + "/" + widgetBranchZipName;
		exceptList.clear();
		exceptList.add(srcWidget + "/" + app.getAppcanAppId()+ "/.git");
		log.info(String.format("addAppWidget -> zip widgetCode -> zxipExcept src[%s] desc[%s] exceptList[%s]", srcWidget, destWidget, exceptList));
		ZipUtil.zipExcept( srcWidget , destWidget, exceptList);
		
		//压缩完之后删除 临时文件
		//FileUtil.deleteDir(new File(srcWidget));

		widget.setFileName(widgetBranchZipName);
		File zipFile = new File(destWidget);
		widget.setFileSize(zipFile.length());*/
		Map<String,String> params = new HashMap<String,String>();
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		params.put("appcanAppId", app.getAppcanAppId());
		params.put("appId", app.getId().toString());
		params.put("appVersionId", appVersion.getId().toString());
		params.put("branchName", appVersion.getBranchName());
		params.put("tagName", appVersion.getTagName());
		params.put("versionNo", widget.getVersionNo());
		
		
		log.info(params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/widget/create", params);
		log.info(String.format("GitAction -> addAppWidget --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException("添加Widget包失败");
		}
		JSONObject objMsg = obj.getJSONObject("message");
		widget.setFileName(objMsg.getString("fileName"));
		widget.setFileSize(objMsg.getLong("fileSize"));
		
		appWidgetDao.save(widget);
		
		return widget;		
	}
	
	/**
	 * 删除Widget包
	 * @param widgetId
	 */
	public void removeWidget(long widgetId) {
		appWidgetDao.delete(widgetId);
	}
	
	public synchronized String getNewVersionNo4Widget(long appVersionId) {
		AppVersion appVersion = appVersionDao.findOne(appVersionId);
		if(appVersion == null) {
			return "00.00.0000";
		}
		
		String sql = "select w.versionNo from T_APP_WIDGET AS w left join T_APP_VERSION AS v "
				+ "on w.appVersionId = v.id where v.appId=" + appVersion.getAppId() + " order by w.versionNo desc limit 1";
		
		List<?> tbl = this.jdbcTpl.queryForList(sql);
		if(tbl != null && tbl.size() > 0) {
			Map<?,?> rec = (Map<?,?>)tbl.get(0);
			String versionNo =  (String)rec.get("versionNo");
			
			return VersionUtil.plusVersionNo(versionNo);
		} else {
			return "00.00.0000";
		}
		
	}
	
	public synchronized String getNewVersionNo4Package(long appVersionId, AppPackageBuildType buildType, OSType osType) {
		AppVersion appVersion = appVersionDao.findOne(appVersionId);
		if(appVersion == null) {
			return "00.00.0000";
		}
		
		String patchVersionNo = null;
		String packageVersionNo = null;
		
		String patchSql = "select p.versionNo from T_APP_PATCH AS p left join T_APP_VERSION AS v "
				+ "on p.baseAppVersionId = v.id where v.appId=" + appVersion.getAppId() + " order by p.versionNo desc limit 1";
		List<?> patchTbl = this.jdbcTpl.queryForList(patchSql);
		if(patchTbl != null && patchTbl.size() > 0) {
			Map<?,?> rec = (Map<?,?>)patchTbl.get(0);
			patchVersionNo =  (String)rec.get("versionNo");
		}
		
		String packageSql = "select p.versionNo from T_APP_PACKAGE AS p left join T_APP_VERSION AS v on p.appVersionId = v.id "
				+ "where v.appId=" + appVersion.getAppId() + 
					" and p.buildType=" + buildType.ordinal() + 
					" and p.osType=" + osType.ordinal() + " "
				+ "order by p.versionNo desc limit 1";
		
		List<?> packageTbl = this.jdbcTpl.queryForList(packageSql);
		if(packageTbl != null && packageTbl.size() > 0) {
			Map<?,?> rec = (Map<?,?>)packageTbl.get(0);
			packageVersionNo =  (String)rec.get("versionNo");
		}
		
		
		//patch版本号和package不能重复

		
		if(patchVersionNo == null) {
			return packageVersionNo == null ? "00.00.0000" : VersionUtil.plusVersionNo(packageVersionNo);
		}
		
		if(packageVersionNo == null) {
			return patchVersionNo == null ? "00.00.0000" : VersionUtil.plusVersionNo(patchVersionNo);
		}
		
		if( VersionUtil.compare( patchVersionNo, packageVersionNo ) ) {
			return VersionUtil.plusVersionNo( packageVersionNo );
		} else {
			return VersionUtil.plusVersionNo( patchVersionNo );
		}		
	}
	
	

	public synchronized String getNewVersionNo4Patch(long appVersionId) {
		AppVersion appVersion = appVersionDao.findOne(appVersionId);
		if(appVersion == null) {
			return "00.00.0000";
		}
		
		String patchVersionNo = null;
		String packageVersionNo = null;
		
		String patchSql = "select p.versionNo from T_APP_PATCH AS p left join T_APP_VERSION AS v "
				+ "on p.baseAppVersionId = v.id where v.appId=" + appVersion.getAppId() + " order by p.versionNo desc limit 1";
		List<?> patchTbl = this.jdbcTpl.queryForList(patchSql);
		if(patchTbl != null && patchTbl.size() > 0) {
			Map<?,?> rec = (Map<?,?>)patchTbl.get(0);
			patchVersionNo =  (String)rec.get("versionNo");
		}
		
		String packageSql = "select p.versionNo from T_APP_PACKAGE AS p left join T_APP_VERSION AS v on p.appVersionId = v.id "
				+ "where v.appId=" + appVersion.getAppId() + " order by p.versionNo desc limit 1";
		
		List<?> packageTbl = this.jdbcTpl.queryForList(packageSql);
		if(packageTbl != null && packageTbl.size() > 0) {
			Map<?,?> rec = (Map<?,?>)packageTbl.get(0);
			packageVersionNo =  (String)rec.get("versionNo");
		}
		
		
		//patch版本号和package不能重复

		
		if(patchVersionNo == null) {
			return packageVersionNo == null ? "00.00.0000" : VersionUtil.plusVersionNo(packageVersionNo);
		}
		
		if(packageVersionNo == null) {
			return patchVersionNo == null ? "00.00.0000" : VersionUtil.plusVersionNo(patchVersionNo);
		}
		
		if( VersionUtil.compare( patchVersionNo, packageVersionNo ) ) {
			return VersionUtil.plusVersionNo( packageVersionNo );
		} else {
			return VersionUtil.plusVersionNo( patchVersionNo );
		}


	}
	
	//***************************************************
	//    AppChannel CRUD interfaces                    *
	//***************************************************
	public List< Map<String, Object> > getAppChannelList(Pageable pageable, long appId, long loginUserId) {
		List< Map<String, Object> > message = new ArrayList<>();
		
		List<AppChannel> appChannelList = appChannelDao.findByAppIdAndDel(appId, DELTYPE.NORMAL);
	
		for(AppChannel appChannel : appChannelList) {
			Map<String, Object> element = new HashMap<>();
			element.put("object", appChannel);
			message.add(element);
			
		}
		return message;
	}

	public Map<String, Object> getAppChannel(long appChannelId, long loginUserId) {

		AppChannel appChannel = appChannelDao.findOne(appChannelId);

		if(appChannel == null) {
			return null;
		}
		
		Map<String, Object> message = new HashMap<>();
		message.put("object", appChannel);

		return message;
	}

	public AppChannel addAppChannel(AppChannel appChannel, long loginUserId) {
		AppChannel appChannelOld = appChannelDao.findByAppIdAndCodeAndDel(appChannel.getAppId(), appChannel.getCode(), DELTYPE.NORMAL);
		if(null==appChannelOld){
			return appChannelDao.save(appChannel);
		}
		return null;

	}

	public int editAppChannel(AppChannel appChannel) {

		appChannelDao.save(appChannel);
		return 1;

	}
	
	public void removeAppChannel(long appChannelId) {
		appChannelDao.delete(appChannelId);
	}
	
	public AppChannel findAppChannelByCode(long appId,String code){
		return appChannelDao.findByAppIdAndCodeAndDel(appId,code,DELTYPE.NORMAL);
	}

	//***************************************************
	//    private methods of AppService                 *
	//***************************************************
	/**
	 * 初始化版本库
	 * @param loginUserId
	 * @param appId
	 * @return
	 */
 	private Map<String, String> initRepo(User loginUser, App app) {
 		Map<String, String> map = new HashMap<>();
 		
		// 创建仓库
		// 注意：在平台一方以应用为单位创建repo
 		String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("username", loginUser.getAccount().toLowerCase()) );
		parameters.add( new BasicNameValuePair("projectId", "" + app.getAppcanAppId()) );
		parameters.add( new BasicNameValuePair("project", encodeKey.toLowerCase()) );
		
		log.info(String.format("AppService -> addApp --> initRepo parameters[%s]", parameters.toString()));
		try {
			String jsonStr = HttpUtil.httpPost(initRepoUrl, parameters);
			log.info("jsonStr--->"+jsonStr);
			JSONObject obj = JSONObject.fromObject( jsonStr );
			
			String status = obj.getString("status");
			String info = obj.getString("info");

			/*{"status":"error","info":"projectId is exist"}*/
			if("error".equals(status)){
				throw new RuntimeException(info);
			}
			String path = "";
			//创建失败 不存在path主键
			if(obj.containsKey("path")){
				path = obj.getString("path");
			}
			
			map.put("status", status);
			map.put("info", info);
			map.put("path", path);
			return map;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
 	/**
	 * 初始化版本库
	 * @param loginUserId
	 * @param appId
	 * @author haijun.cheng
	 * @data 2016-10-13
	 * @return
 	 * @throws Exception 
	 */
 	private Map<String, String> initRepoNew(User loginUser, App app) throws Exception {
	 		Map<String, String> map = new HashMap<>();
			// 创建仓库
			// 注意：在平台一方以应用为单位创建repo
	 		StringBuffer parameters = new StringBuffer();
			JSONObject obj = new JSONObject();
			obj.put("name", app.getAppcanAppId());
			obj.put("private", true);
			obj.put("has_issues", false);
			obj.put("has_wiki", false);
			log.info("------------>initRepoNew params:"+obj.toString());
			parameters.append(obj.toString());
			Map<String,String> headers = new HashMap<String,String>();
			headers.put("Authorization", "token "+gitToken);
	  try{
			String result = NewGitHttpUtil.httpPostWithJSON(newGitServer+"/api/v3/user/repos", parameters.toString(),headers);
			log.info("----------->result:"+result);
			JSONObject obj1 = JSONObject.fromObject( result );
			JSONObject obj2 = JSONObject.fromObject( obj1.getString("data") );
			map.put("path", "/"+obj2.getString("full_name")+".git");
			map.put("status", obj1.getString("status"));
			return map;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
 	private Map<String, Object> shareRepo(User loginUser, App app) throws Exception { 		
 		List<ProjectMember> memberList4Master = this.projectService.getMemberListWithPermissionRequired(app.getProjectId(), "code_upload_master_code");
 		
//		parameters.add( new BasicNameValuePair("username", loginUser.getAccount().toLowerCase()) );
//		parameters.add( new BasicNameValuePair("projectId", "" + app.getAppcanAppId()) );
//		parameters.add( new BasicNameValuePair("project", encodeKey.toLowerCase()) );
 		Map<String, String> masterRet = new HashMap<String, String>();
 		if(gitFlag.equals("new")){
	 		 masterRet = callShareRepoInterfaceNew(
	 				 loginUser.getAccount().toLowerCase(),
	 				 memberList4Master, app.getId(), "all", "master");
 		}else{
 	 		 masterRet = callShareRepoInterface(
 	 				 loginUser.getAccount().toLowerCase(),
 	 				 memberList4Master, app.getId(), "all", "master");
 		}

 		//Map<String, String> branchRet = callShareRepoInterface(loginUser.getAccount(), memberList4Branch, app.getId(), "branch");
 		
 		Map<String, Object> ret = new HashMap<>();
 		
 		ret.put("master", masterRet);
 		
 		
 		//share分支权限
 		if(gitFlag.equals("new")){
// 			shareAllBranchNew(loginUser,app,memberList4Master);
 		}else{
 			shareAllBranch(loginUser,app,memberList4Master);
 		}
 		
 		
	
 		return ret;
 	}
 	
 	/**
 	 * 创建应用的时候给有分支权限的人分配权限
 	 * @user jingjian.wu
 	 * @date 2015年12月14日 下午4:43:01
 	 */
 	private Map<String, String> shareAllBranch(User loginUser, App app,List<ProjectMember> memberList4Master) { 		
 		//创建应用的时候给有分支权限的人分配权限
 		log.info("after create app 分配权限给有分支权限的人");
 		List<ProjectMember> memberList4Branch = this.projectService.getMemberListWithPermissionRequired(app.getProjectId(), "code_update_branch");
 		
 		Map<String, String> map = new HashMap<>();
 		if(null==memberList4Branch || memberList4Branch.size() < 1) {
			return null;
		}
 		
// 		App app = appDao.findOne(appId);
//		String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
//		parameters.add( new BasicNameValuePair("project",  encodeKey.toLowerCase()) );
 		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
 		String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
 		for(ProjectMember pm:memberList4Branch){
 			boolean exist = false;//标识此人在添加主干的时候是否已经添加过权限
 			if(null!=memberList4Master && memberList4Master.size()>0){
 				for(ProjectMember masterMem:memberList4Master){
 					if(pm.getUserId()==masterMem.getUserId() || loginUser.getId().longValue()==pm.getUserId()){
 						//如果有分支权限的人,也有主干权限,或者是应用的创建者,则不需要再给此人授权
 						exist = true;
 						break;
 					}
 				}
 			}
 			if(!exist){
 				GitAuthVO vo = new GitAuthVO();
 				vo.setAuthflag("allbranch");
 				vo.setUsername(loginUser.getAccount());
 				vo.setProject(encodeKey.toLowerCase());
 				vo.setPartnername(userDao.findOne(pm.getUserId()).getAccount());
 				vo.setProjectid(app.getAppcanAppId());
 				listAuth.add(vo);
 			}
 		}
 		JSONArray jsonArray = JSONArray.fromObject(listAuth);
 		if(listAuth.size()==0){
 			map.put("status", "ok");
			map.put("info", "no member need to share");
			return map;
 		}
 		String params = "{\"share\":"+jsonArray.toString()+"}";
 		
 		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("params", params) );
 		
		log.info(String.format("AppService -> shareAllBranch -->  parameters[%s] shareallgitauth[%s]",
				parameters.toString(), shareallgitauth));
		try {
			String jsonStr = HttpUtil.httpPost(shareallgitauth, parameters);
			log.info(String.format("AppService -> shareAllBranch --> shareallgitauth jsonStr[%s]", jsonStr));
			
			JSONObject obj = JSONObject.fromObject( jsonStr );
			String status = obj.getString("status");
			String info = obj.getString("info");
			
			map.put("status", status);
			map.put("info", info);
			return map;
		} catch (Exception e) {
			
			log.info(String.format("AppService -> AddGitAuth --> shareallgitauth IOException [%s]", ExceptionUtils.getStackTrace(e)));
			
			return null;
		}
	
 	}
 	/**
 	 * 创建应用的时候给有分支权限的人分配权限
 	 * @throws Exception 
 	 * @user haijun.cheng	
 	 * @date 2016年10月17日
 	 */
 	private Map<String, String> shareAllBranchNew(User loginUser, App app,List<ProjectMember> memberList4Master) throws Exception { 		
 		//创建应用的时候给有分支权限的人分配权限
 		log.info("after create app 分配权限给有分支权限的人");
 		
 		List<ProjectMember> memberList4Branch = this.projectService.getMemberListWithPermissionRequired(app.getProjectId(), "code_update_branch");
 		
 		Map<String, String> map = new HashMap<>();
 		if(null==memberList4Branch || memberList4Branch.size() < 1) {
			return null;
		}
 		
 		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
 		String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
 		for(ProjectMember pm:memberList4Branch){
 			boolean exist = false;//标识此人在添加主干的时候是否已经添加过权限
 			if(null!=memberList4Master && memberList4Master.size()>0){
 				for(ProjectMember masterMem:memberList4Master){
 					if(pm.getUserId()==masterMem.getUserId() || loginUser.getId().longValue()==pm.getUserId()){
 						//如果有分支权限的人,也有主干权限,或者是应用的创建者,则不需要再给此人授权
 						exist = true;
 						break;
 					}
 				}
 			}
 			if(!exist){
 				GitAuthVO vo = new GitAuthVO();
 				vo.setAuthflag("allbranch");
 				vo.setUsername(loginUser.getAccount());
 				vo.setProject(encodeKey.toLowerCase());
 				vo.setPartnername(userDao.findOne(pm.getUserId()).getAccount());
 				vo.setProjectid(app.getAppcanAppId());
 				listAuth.add(vo);
 			}
 		}
 		JSONArray jsonArray = JSONArray.fromObject(listAuth);
 		if(listAuth.size()==0){
 			map.put("status", "ok");
			map.put("info", "no member need to share");
			return map;
 		}
 		for(GitAuthVO auth:listAuth){
	 	 		StringBuffer parameters = new StringBuffer();
	 	 		Map<String,String> headers = new HashMap<String,String>();
	 			String gitLibrary=app.getRelativeRepoPath().replace(".git", "")+"/collaborators/";
	 			Map<String,String> headers1 = new HashMap<String,String>();
	 			User user = this.userDao.findByAccountAndDel(auth.getPartnername(), DELTYPE.NORMAL);
	 			if (user.getNickName() == null) {
	 				throw new RuntimeException("用户昵称为空");
	 			}else{
	 				JSONObject obj = new JSONObject();
	 				obj.put("username", user.getAccount());
	 				obj.put("nickname", user.getNickName());
	 				parameters.append(obj.toString());
	 				headers.put("Authorization", "token "+gitToken);
	 				String result = NewGitHttpUtil.httpPostWithJSON(newGitServer+"/api/v3/users", parameters.toString(),headers);
	 				JSONObject resultObj = JSONObject.fromObject(result);
	 				if(!resultObj.getString("status").equals("OK")){
	 					throw new RuntimeException("为该"+app.getRelativeRepoPath()+"库添加用户失败");
	 				}
	 				
	 				headers1.put("Authorization", "token "+gitToken);
	 				String result1 = NewGitHttpUtil.put(newGitServer+"/api/v3/repos"+gitLibrary+user.getNickName(),headers1);
	 				JSONObject returnObj = JSONObject.fromObject( result1 );
	 				String status = returnObj.getString("status");
	 				if(!status.equals("OK")){
	 					String errCode = returnObj.getString("errCode");
	 					if(!errCode.equals("ERR-1000")){
	 						throw new RuntimeException("为该"+app.getRelativeRepoPath()+"库添加用户失败");
	 					}
	 				}
	 			}
			}
 		map.put("status", "ok");
		return map;
	
 	}
 	
 	/**
 	 * 供管理后台修改了GIT权限之后调用
 	 * @param roleId  修改了哪个角色的GIT权限,此GIT角色只能为  (只有团队创建者,团队管理员,项目创建者,项目管理员,项目参与人,项目观察员会修改git权限)
 	 * @param master  对主干权限进行变更   1:增加主干权限    0：不修改   -1：删除主干权限
 	 * @param branch 对分支权限进行变更   1:增加分支权限    0：不修改   -1：删除分支权限
 	 * @throws Exception 
 	 * @user jingjian.wu
 	 * @date 2015年12月18日 下午8:51:22
 	 */
 	public Map<String, String> invokeGitAuthForDaemon(int roleId,int master,int branch) throws Exception { 	
 		Map<String, String> map = new HashMap<>();
 		log.info("daemon update gitAuth coming!");
 		if(master==0 && branch ==0){
 			log.info("不用修改git权限");
 			return map;
 		}
 		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
 		List<GitAuthVO> delAuth = new ArrayList<GitAuthVO>();
 		List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
 		Role role = roleDao.findOne(Long.parseLong(roleId+""));
 		
 		if("TEAM_CREATOR".equals(role.getEnName()) || "TEAM_ADMINISTRATOR".equals(role.getEnName()) ){
 			//找到所有此角色的团队成员
 			List<TeamMember> listTeamMember = teamMemberDao.findByRoleIdAndDel(roleId, DELTYPE.NORMAL);
 			if(null!=listTeamMember && listTeamMember.size()>0){
 				//遍历团队成员
 				for(TeamMember mem:listTeamMember){
 					//找到此团队下的应用
		 			List<App> appList = appDao.findByTeamId(mem.getTeamId());
		 			if(null!=appList && appList.size()>0){
		 				for(App app:appList){
		 					boolean projectMember4app_master = false;//作为项目成员是否有master权限
		 					boolean projectMember4app_branch = false;//作为项目成员是否有branch权限
		 					//找到团队下的人在应用所在的项目里的权限情况
		 					List<ProjectMember> listProjectMember = projectMemberDao.findByProjectIdAndUserIdAndDel(app.getProjectId(), mem.getUserId(), DELTYPE.NORMAL);
		 					if(null!=listProjectMember){
		 						for(ProjectMember pm:listProjectMember){
		 							List<ProjectAuth> listProjectAuth = projectAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);
		 							if(null!=listProjectAuth){
		 								for(ProjectAuth pa:listProjectAuth){
		 									List<Permission> listPermission = Cache.getRole(pa.getRoleId()).getPermissions();
		 									if(null!=listPermission){
		 										for(Permission p:listPermission){
		 											if(p.getEnName().equals("code_upload_master_code")){
		 												projectMember4app_master = true;
		 												break;
		 											}
		 											if(p.getEnName().equals("code_update_branch")){
		 												projectMember4app_branch = true;
		 											}
		 										}
		 									}
		 								}
		 							}
		 						}
		 					}
		 					if(projectMember4app_master){
		 						break;
		 					}else{
		 						if(master==1){//增加主干
		 							if(app.getUserId()!=mem.getUserId().longValue()){
		 								
		 								User user = userDao.findOne(mem.getUserId());
		 								User owner = userDao.findOne(app.getUserId());
		 								GitAuthVO vo = new GitAuthVO();
		 								vo.setAuthflag("all");
		 								vo.setUsername(owner.getAccount());
		 								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
		 								vo.setProject(encodeKey.toLowerCase());
		 								vo.setPartnername(user.getAccount());
		 								vo.setProjectid(app.getAppcanAppId());
		 								listAuth.add(vo);
		 							}
		 	 					}else if(master==-1){
		 	 						if(app.getUserId()!=mem.getUserId().longValue()){
		 	 							
		 	 							User user = userDao.findOne(mem.getUserId());
		 	 							User owner = userDao.findOne(app.getUserId());
		 	 							GitAuthVO vo = new GitAuthVO();
		 	 							vo.setAuthflag("all");
		 	 							vo.setUsername(owner.getAccount());
		 	 							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
		 	 							vo.setProject(encodeKey.toLowerCase());
		 	 							vo.setPartnername(user.getAccount());
		 	 							vo.setProjectid(app.getAppcanAppId());
		 	 							delAuth.add(vo);
		 	 						}else{
		 	 							//改变仓库拥有者
		 	 							GitOwnerAuthVO vo = new GitOwnerAuthVO();
		 	 							vo.setProjectid(app.getAppcanAppId());
		 	 							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
		 	 							vo.setProject(encodeKey.toLowerCase());
		 	 							User owner = userDao.findOne(app.getUserId());
		 	 							vo.setUsername(owner.getAccount());
		 	 							User other = new User();
		 	 							List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(mem.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
	 									if(null!=listTm && listTm.size()>0){
	 										other = userDao.findOne(listTm.get(0).getUserId());
	 									}
		 	 							vo.setOther(other.getAccount());
		 	 							app.setUserId(other.getId());
		 	 							appDao.save(app);
		 	 							changeOwnerAuth.add(vo);
		 	 						}
		 	 						
		 	 					}
		 						
		 						//上面主要依赖主干判断,下面判断单独修改分支的情况
		 						if(!projectMember4app_branch && master !=1 &&  branch==1){
 	 								//添加分支权限
	 	 							User user = userDao.findOne(mem.getUserId());
	 	 							User owner = userDao.findOne(app.getUserId());
	 	 							
	 								GitAuthVO vobranch = new GitAuthVO();
	 								vobranch.setAuthflag("allbranch");
	 								vobranch.setUsername(owner.getAccount());
	 								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
	 								vobranch.setProject(encodeKey.toLowerCase());
	 								vobranch.setPartnername(user.getAccount());
	 								vobranch.setProjectid(app.getAppcanAppId());
	 								listAuth.add(vobranch);
 	 							}else if(!projectMember4app_branch && master !=1 && branch==-1){
 	 								User user = userDao.findOne(mem.getUserId());
	 	 							User owner = userDao.findOne(app.getUserId());
	 	 							GitAuthVO vo = new GitAuthVO();
	 	 							vo.setAuthflag("allbranch");
	 	 							vo.setUsername(owner.getAccount());
	 	 							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
	 	 							vo.setProject(encodeKey.toLowerCase());
	 	 							vo.setPartnername(user.getAccount());
	 	 							vo.setProjectid(app.getAppcanAppId());
	 	 							delAuth.add(vo);
 	 							}
		 					}
		 					
		 				}
		 			}
		 			
 				}
 			}
 			
 			
 		}else if("PROJECT_CREATOR".equals(role.getEnName())
 				|| "PROJECT_ADMINISTRATOR".equals(role.getEnName())
 				|| "PROJECT_MEMBER".equals(role.getEnName())
 				|| "PROJECT_OBSERVER".equals(role.getEnName())){
 			//找到所有此角色的项目成员
 			List<ProjectMember> listProjectMember = projectMemberDao.findByRoleIdAndDel(roleId, DELTYPE.NORMAL);
 			if(null!=listProjectMember && listProjectMember.size()>0){
 				for(ProjectMember pm:listProjectMember){
 					boolean masterInTeam = false;//标识在团队中是否有master权限
 					boolean branchInTeam = false;//标识在团队中是否有branch权限
 					Project project = projectDao.findOne(pm.getProjectId());
 					if(null!=project && project.getTeamId()!=-1){
 						TeamMember teamMember = teamMemberDao.findByTeamIdAndUserIdAndDel(project.getTeamId(), pm.getUserId(), DELTYPE.NORMAL);
 						if(null!=teamMember){
 							TeamAuth teamAuth = teamAuthDao.findByMemberIdAndDel(teamMember.getId(), DELTYPE.NORMAL);
 							if(null!=teamAuth){
 								List<Permission> listPermission = Cache.getRole(teamAuth.getRoleId()).getPermissions();
 								if(null!=listPermission && listPermission.size()>0){
 									for(Permission p:listPermission){
 										if(p.getEnName().equals("code_upload_master_code")){
 											masterInTeam = true;
 											break;
 										}
 										if(p.getEnName().equals("code_update_branch")){
 											branchInTeam = true;
 										}
 									}
 								}
 							}
 						}
 					}
 					if(masterInTeam){
 						break;
 					}
 					List<App> listApp = appDao.findByProjectIdAndDel(pm.getProjectId(), DELTYPE.NORMAL);
 					if(null!=listApp && listApp.size()>0){
 						for(App app:listApp){
 							if(master==1){
 								User user = userDao.findOne(pm.getUserId());
 	 							User owner = userDao.findOne(app.getUserId());
 	 							
 								GitAuthVO voMaster = new GitAuthVO();
 								voMaster.setAuthflag("all");
 								voMaster.setUsername(owner.getAccount());
 								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
 								voMaster.setProject(encodeKey.toLowerCase());
 								voMaster.setPartnername(user.getAccount());
 								voMaster.setProjectid(app.getAppcanAppId());
 								listAuth.add(voMaster);
 							}else if(master==-1){
 								
 								if(app.getUserId()!=pm.getUserId()){
	 	 							
	 	 							User user = userDao.findOne(pm.getUserId());
	 	 							User owner = userDao.findOne(app.getUserId());
	 	 							GitAuthVO vo = new GitAuthVO();
	 	 							vo.setAuthflag("all");
	 	 							vo.setUsername(owner.getAccount());
	 	 							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
	 	 							vo.setProject(encodeKey.toLowerCase());
	 	 							vo.setPartnername(user.getAccount());
	 	 							vo.setProjectid(app.getAppcanAppId());
	 	 							delAuth.add(vo);
	 	 						}else{
	 	 							//改变仓库拥有者
	 	 							GitOwnerAuthVO vo = new GitOwnerAuthVO();
	 	 							vo.setProjectid(app.getAppcanAppId());
	 	 							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
	 	 							vo.setProject(encodeKey.toLowerCase());
	 	 							User owner = userDao.findOne(app.getUserId());
	 	 							vo.setUsername(owner.getAccount());
	 	 							User other = new User();
	 	 							if(project.getType().equals(PROJECT_TYPE.TEAM)){
	 									List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
	 									if(null!=listTm && listTm.size()>0){
	 										other = userDao.findOne(listTm.get(0).getUserId());
	 									}
	 								}else{
	 									ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
	 									if(null!=pmCrt){
	 										other = userDao.findOne(pmCrt.getUserId());
	 									}
	 								}
	 	 							vo.setOther(other.getAccount());
	 	 							app.setUserId(other.getId());
	 	 							appDao.save(app);
	 	 							changeOwnerAuth.add(vo);
	 	 						}
 							}
 							
 							if(!branchInTeam && master!=1 && branch==1){
 								User user = userDao.findOne(pm.getUserId());
 	 							User owner = userDao.findOne(app.getUserId());
 	 							
 								GitAuthVO voMaster = new GitAuthVO();
 								voMaster.setAuthflag("allbranch");
 								voMaster.setUsername(owner.getAccount());
 								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
 								voMaster.setProject(encodeKey.toLowerCase());
 								voMaster.setPartnername(user.getAccount());
 								voMaster.setProjectid(app.getAppcanAppId());
 								listAuth.add(voMaster);
 							}else if(!branchInTeam && master!=1 && branch==-1){
 								User user = userDao.findOne(pm.getUserId());
 								log.info("=====================================userId------>"+app.getUserId());
 	 							User owner = userDao.findOne(app.getUserId());
 	 							GitAuthVO vo = new GitAuthVO();
 	 							vo.setAuthflag("allbranch");
 	 							vo.setUsername(owner.getAccount());
 	 							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
 	 							vo.setProject(encodeKey.toLowerCase());
 	 							vo.setPartnername(user.getAccount());
 	 							vo.setProjectid(app.getAppcanAppId());
 	 							delAuth.add(vo);
 							}
 						}
 					}
 				}
 			}
 			
 		}
 		//调用修改GIT权限的接口(下面三个顺序,最好别动,防止添加的权限又被删掉)
 		map = this.delGitAuth(delAuth);
 		log.info("daemon update Role ---updateGitAuth----delAuth--->"+map.toString());
 		map =this.updateGitAuth(changeOwnerAuth);
 		log.info("daemon update Role ---updateGitAuth----updateGitAuth--->"+map.toString());
 		map =this.addGitAuth(listAuth);
 		log.info("daemon update Role ---updateGitAuth----addGitAuth--->"+map.toString());
 		
		return map;
 	}
 	
 	/**
 	 * 通过GIT仓库的project(也就是appcanappid)获取对应的git仓库有权限的人员
 	 * @throws IOException 
 	 * @throws ClientProtocolException 
 	 * @user jingjian.wu
 	 * @date 2015年12月16日 下午9:43:28
 	 */
 	public Map<String, Object> fetchGitUser(String projectId) throws ClientProtocolException, IOException { 	
 		if(gitFlag.equals("new")){
 			//创建应用的时候给有分支权限的人分配权限
 	 		log.info("获取应用ID为->"+projectId+",有权限访问此GIT仓库的用户信息");
 	 		Map<String,String> headers = new HashMap<String,String>();
 	 		App app = this.appDao.findOne(projectId);
 	 		Map<String, Object> map = new HashMap<>();
 			try {
 	 			headers.put("Authorization", "token "+gitToken);
 	 			String result = NewGitHttpUtil.get(newGitServer+"/api/v3/repos"+app.getRelativeRepoPath().replace(".git", "")+"/collaborators", headers);
 				
 				JSONObject obj = JSONObject.fromObject( result );
 				String status = obj.getString("status");
 				JSONArray data = obj.getJSONArray("data");
 				map.put("data", data);
 				map.put("status", status);
 				return map;
 			} catch (Exception e) {
 				
 				log.info(String.format("AppService -> fetchGitUser --> getalluser IOException [%s]", ExceptionUtils.getStackTrace(e)));
 				
 				return null;
 			}
 		}else{
 			//创建应用的时候给有分支权限的人分配权限
 	 		log.info("获取应用ID为->"+projectId+",有权限访问此GIT仓库的用户信息");
 	 		
 	 		Map<String, Object> map = new HashMap<>();
 	 		
 	 		List<NameValuePair> parameters = new ArrayList<>();
 			parameters.add( new BasicNameValuePair("projectId", projectId) );
 	 		
 			log.info(String.format("AppService -> fetchGitUser -->  parameters[%s] getalluser[%s]",
 					parameters.toString(), getalluser));
 			try {
 				String jsonStr = HttpUtil.httpPost(getalluser, parameters);
 				log.info(String.format("AppService -> fetchGitUser --> getalluser jsonStr[%s]", jsonStr));
 				
 				JSONObject obj = JSONObject.fromObject( jsonStr );
 				String status = obj.getString("status");
 				String info = obj.getString("info");
 				JSONArray params = obj.getJSONArray("params");
 				JSONObject owner = params.getJSONObject(0);
 				String gitOwner = owner.getString("owner");
 				log.info("git owner--->"+gitOwner);
 				map.put("owner", gitOwner);
 				params.remove(0);
 				map.put("others", params);
 				for(int i =0;i<params.size();i++){
 					JSONObject jsonObj = params.getJSONObject(i);
 					String ref = jsonObj.getString("ref");
 					String partner = jsonObj.getString("partner");
 					String authflag = jsonObj.getString("authflag");
 					log.info("partner-->"+partner+",authflag-->"+authflag+",ref-->"+ref);
 				}
 				map.put("status", status);
 				map.put("info", info);
 				return map;
 			} catch (Exception e) {
 				
 				log.info(String.format("AppService -> fetchGitUser --> getalluser IOException [%s]", ExceptionUtils.getStackTrace(e)));
 				
 				return null;
 			}
 		}
 		
 	}
 	
 	private Map<String, String> callShareRepoInterface(String userAccount, List<ProjectMember> partnerMemberList, long appId, String authFlag, String branchName) {
 		Map<String, String> map = new HashMap<>();
 		
 		String partnerAccountStr = "";
 		
 		if(partnerMemberList != null && partnerMemberList.size() > 0) {
 			for(ProjectMember member : partnerMemberList) {
 				if(!member.getUserAccount().equals(userAccount)){//排除创建者
 					partnerAccountStr += "," + member.getUserAccount();
 				}
 			}
 		}
 		
 		if(partnerAccountStr.length() > 0) {
			partnerAccountStr = partnerAccountStr.substring(1);
		} else {
			return null;
		}
 		
 		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("username", userAccount) );
		parameters.add( new BasicNameValuePair("partnername", partnerAccountStr) );
		App app = appDao.findOne(appId);
		String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
//		parameters.add( new BasicNameValuePair("project", "app_" + appId) );
		parameters.add( new BasicNameValuePair("project",  encodeKey.toLowerCase()) );
		parameters.add( new BasicNameValuePair("authflag", authFlag) );
		parameters.add( new BasicNameValuePair("ref", branchName) ); 
		parameters.add( new BasicNameValuePair("projectid", "" + app.getAppcanAppId()) );
 		
		log.info(String.format("AppService -> addApp --> callShareRepoInterface parameters[%s] shareRepoUrl[%s]",
				parameters.toString(), shareRepoUrl));
		try {
			String jsonStr = HttpUtil.httpPost(shareRepoUrl, parameters);
			JSONObject obj = JSONObject.fromObject( jsonStr );
			log.info(String.format("AppService -> addApp --> callShareRepoInterface jsonStr[%s]", jsonStr));
			
			String status = obj.getString("status");
			String info = obj.getString("info");
			
			map.put("status", status);
			map.put("info", info);
			return map;
		} catch (Exception e) {
			
			log.info(String.format("AppService -> addApp --> callShareRepoInterface IOException [%s]", ExceptionUtils.getStackTrace(e)));
			
			return null;
		}
 	}
	/**
	 * @author haijun.cheng
	 * @data 2016-10-13
	 * @param userAccount
	 * @param partnerMemberList
	 * @param appId
	 * @param authFlag
	 * @param branchName
	 * @return
	 * @throws Exception 
	 */
 	private Map<String, String> callShareRepoInterfaceNew(String userAccount, List<ProjectMember> partnerMemberList, long appId, String authFlag, String branchName) throws Exception {
 		App app = appDao.findOne(appId);
 		Map<String, String> map = new HashMap<>();
 		if(partnerMemberList.size()>0){
 			for(ProjectMember member:partnerMemberList){
 				StringBuffer parameters = new StringBuffer();
 	 	 		Map<String,String> headers = new HashMap<String,String>();
 	 			String gitLibrary=app.getRelativeRepoPath().replace(".git", "")+"/collaborators/";
 	 			User user=this.userDao.findByAccountAndDel(member.getUserAccount(), DELTYPE.NORMAL);
 	 			
 	 			if (user.getNickName() == null) {
 	 				log.info("-------------->nickName is not exist");
 	 				throw new RuntimeException("该"+member.getUserAccount()+"帐号昵称不存在,请联系管理员");
 	 			}else{
 	 				JSONObject obj = new JSONObject();
 	 				obj.put("username", user.getAccount());
 	 				obj.put("nickname", user.getNickName());
 	 				log.info("----------->userName and nickname:"+obj.toString());
 	 				parameters.append(obj.toString());
 	 				headers.put("Authorization", "token "+gitToken);
 	 				log.info("------------>headers:"+headers);
 	 				String result = NewGitHttpUtil.httpPostWithJSON(newGitServer+"/api/v3/users", parameters.toString(),headers);
 	 				log.info("------------>result:"+result);
 	 				JSONObject resultObj = JSONObject.fromObject(result);
 	 				if(!resultObj.getString("status").equals("OK")){
 	 					throw new RuntimeException("git添加用户失败");
 	 				}
 	 				String result1 = NewGitHttpUtil.put(newGitServer+"/api/v3/repos"+gitLibrary+user.getNickName(),headers);
 	 				JSONObject returnObj = JSONObject.fromObject( result1 );
 	 				String status = returnObj.getString("status");
 	 				if(!status.equals("OK")){
 	 					String errCode = returnObj.getString("errCode");
 	 					if(!errCode.equals("ERR-1000")){
 	 						throw new RuntimeException("git用户赋权限失败");
 	 					}
 	 				}
 	 			}
 			}
			map.put("status", "ok");
			return map;
 		}else{
 			return null;
 		}
 		
 	}
 	public Map<String, String> addGitAuth(List<GitAuthVO> listAuth) throws Exception {
 		log.info("------------------------------come into addGitAuth Method!");
 		Map<String, String> map = new HashMap<>();
 		if(null==listAuth || listAuth.size() < 1) {
			return map;
		}
 		log.info("-------------->listAuthSize:"+listAuth.size());
 		if(gitFlag.equals("new")){
 			for(GitAuthVO auth:listAuth){
 				App app = appDao.findByAppcanAppIdAndDel(auth.getProjectid(),DELTYPE.NORMAL);
 				if(app!=null){
 					StringBuffer parameters = new StringBuffer();
 	 	 	 		Map<String,String> headers = new HashMap<String,String>();
 	 	 			String gitLibrary=app.getRelativeRepoPath().replace(".git","")+"/collaborators/";
 	 	 			Map<String,String> headers1 = new HashMap<String,String>();
 	 	 			log.info("------------>UserName:"+auth.getPartnername());
 	 	 			User user = this.userDao.findByAccountAndDel(auth.getPartnername(), DELTYPE.NORMAL);
 	 	 			if (user.getNickName() == null) {
 	 	 				throw new RuntimeException("用户昵称为空");
 	 	 			}else{
 	 	 				JSONObject obj = new JSONObject();
 	 	 				obj.put("username", user.getAccount());
 	 	 				obj.put("nickname", user.getNickName());
 	 	 				parameters.append(obj.toString());
 	 	 				headers.put("Authorization", "token "+gitToken);
 	 	 				String result = NewGitHttpUtil.httpPostWithJSON(newGitServer+"/api/v3/users", parameters.toString(),headers);
 	 	 				JSONObject resultObj = JSONObject.fromObject(result);
 	 	 				if(!resultObj.getString("status").equals("OK")){
 	 	 					throw new RuntimeException("为该"+app.getRelativeRepoPath()+"库添加用户失败");
 	 	 				}
 	 	 				
 	 	 				headers1.put("Authorization", "token "+gitToken);
 	 	 				String result1 = NewGitHttpUtil.put(newGitServer+"/api/v3/repos"+gitLibrary+user.getNickName(),headers1);
 	 	 				JSONObject returnObj = JSONObject.fromObject( result1 );
 	 	 				String status = returnObj.getString("status");
 	 	 				if(!status.equals("OK")){
 	 	 					if(!returnObj.getString("errCode").contains("ERR-1000")){
 	 	 						throw new RuntimeException("为该"+app.getRelativeRepoPath()+"库用户赋权限失败");
 	 	 					}
 	 	 				}
 	 	 			}
 				}
 			}
 			map.put("status", "ok");
 			return map;
 		}else{
 			JSONArray jsonArray = JSONArray.fromObject(listAuth);
 	 		String params = "{\"share\":"+jsonArray.toString()+"}";
 	 		
 	 		List<NameValuePair> parameters = new ArrayList<>();
 			parameters.add( new BasicNameValuePair("params", params) );
 	 		
 			log.info(String.format("AppService -> AddGitAuth -->  parameters[%s] shareallgitauth[%s]",
 					parameters.toString(), shareallgitauth));
 			try {
 				String jsonStr = HttpUtil.httpPost(shareallgitauth, parameters);
 				log.info(String.format("AppService -> AddGitAuth --> shareallgitauth jsonStr[%s]", jsonStr));
 				
 				JSONObject obj = JSONObject.fromObject( jsonStr );
 				String status = obj.getString("status");
 				String info = obj.getString("info");
 				
 				map.put("status", status);
 				map.put("info", info);
 				return map;
 			} catch (Exception e) {
 				
 				log.info(String.format("AppService -> AddGitAuth --> shareallgitauth IOException [%s]", ExceptionUtils.getStackTrace(e)));
 				
 				return  new HashMap<>();
 			}
 		}
 		
 	}
 	
 	/**
 	 * 删除伙伴的git权限
 	 * @user jingjian.wu
 	 * @date 2015年12月14日 下午8:16:57
 	 */
 	public Map<String, String> delGitAuth(List<GitAuthVO> listAuth) {
 		log.info("------------------------------come into delGitAuth Method!");
 		Map<String, String> map = new HashMap<>();
 		if(null==listAuth || listAuth.size() < 1) {
			return map;
		}
 		if(gitFlag.equals("new")){
 			for(GitAuthVO auth:listAuth){
 				App app=this.appDao.findByAppcanAppIdAndDel(auth.getProjectid(), DELTYPE.NORMAL);
 				User user=this.userDao.findByAccountAndDel(auth.getPartnername(),DELTYPE.NORMAL);
 	 			//防止测试环境有getNickName为空的情况，导致删除异常
 				if(user.getNickName()==null){
 	 				continue;
 	 			}
 				Map<String,String> headers = new HashMap<String,String>();
 	 			headers.put("Authorization", "token "+gitToken);
 	 			log.info("----------->nickName:"+user.getNickName()+",userName:"+auth.getPartnername()+",listAuth.size():"+listAuth.size());
 	 			log.info("----------->relativeRepoPath:"+app.getRelativeRepoPath());
 	 			String result = NewGitHttpUtil.delete(newGitServer+"/api/v3/repos"+app.getRelativeRepoPath().replace(".git", "")+"/collaborators/"+user.getNickName(),headers);
 	 			log.info("----->result:"+result);
 	 			JSONObject obj = JSONObject.fromObject( result );
 	 			if(!obj.getString("status").equals("OK")){
 	 				throw new RuntimeException("删除git中用户失败");
 	 			}
 			}
 			map.put("status", "ok");
 			return map;
 		}else{
// 	 		App app = appDao.findOne(appId);
// 			String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
// 			parameters.add( new BasicNameValuePair("project",  encodeKey.toLowerCase()) );
 	 		
 	 		JSONArray jsonArray = JSONArray.fromObject(listAuth);
 	 		String params = "{\"delete\":"+jsonArray.toString()+"}";
 	 		
 	 		List<NameValuePair> parameters = new ArrayList<>();
 			parameters.add( new BasicNameValuePair("params", params) );
 	 		
 			log.info(String.format("AppService -> delGitAuth -->  parameters[%s] deleteallpartner[%s]",
 					parameters.toString(), deleteallpartner));
 			try {
 				String jsonStr = HttpUtil.httpPost(deleteallpartner, parameters);
 				log.info(String.format("AppService -> delGitAuth --> deleteallpartner jsonStr[%s]", jsonStr));
 				
 				JSONObject obj = JSONObject.fromObject( jsonStr );
 				String status = obj.getString("status");
 				String info = obj.getString("info");
 				
 				map.put("status", status);
 				map.put("info", info);
 				return map;
 			} catch (Exception e) {
 				
 				log.info(String.format("AppService -> delGitAuth --> deleteallpartner IOException [%s]", ExceptionUtils.getStackTrace(e)));
 				
 				return new HashMap<>();
 			}
 		}

 	}
 	
 	public Map<String, String> delAllGitRepo(List<GitAuthVO> listAuth) {
 		log.info("------------------------------come into delAllGitRepo Method!");
 		Map<String, String> map = new HashMap<>();
 		if(null==listAuth || listAuth.size() < 1) {
			return map;
		}
 		if(gitFlag.equals("new")){
 			log.info("------------------------------come into delAllGitNew Method!");
 			Map<String,String> headers = new HashMap<String,String>();
	 		headers.put("Authorization", "token "+gitToken);
	 		String status="";
	 		for(GitAuthVO vo:listAuth){
	 			App app=this.appDao.findByAppcanAppIdAndDel(vo.getProjectid(),DELTYPE.NORMAL);
				app.setDel(DELTYPE.DELETED);
				appDao.save(app);
				String result = NewGitHttpUtil.delete(newGitServer+"/api/v3/repos"+app.getRelativeRepoPath().replace(".git", ""), headers);
				log.info("-------->result:"+result);
	 			try {
	 				JSONObject obj = JSONObject.fromObject( result );
	 				status =status+obj.getString("status");
	 			} catch (Exception e) {
	 				log.info(String.format("AppService -> delAllGitRepo --> deleteallgit IOException [%s]", ExceptionUtils.getStackTrace(e)));
	 				return   new HashMap<>();
	 			}
	 		}
	 		map.put("status", status);
			return map;
 		}else{
 			log.info("------------------------------come into delAllGitRepo Method!");
 			for(GitAuthVO vo:listAuth){
 				App app=this.appDao.findByAppcanAppIdAndDel(vo.getProjectid(),DELTYPE.NORMAL);
 	 			app.setDel(DELTYPE.DELETED);
 				appDao.save(app);
 			}
// 	 		App app = appDao.findOne(appId);
// 			String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
// 			parameters.add( new BasicNameValuePair("project",  encodeKey.toLowerCase()) );
 	 		
 	 		JSONArray jsonArray = JSONArray.fromObject(listAuth);
 	 		String params = "{\"delete\":"+jsonArray.toString()+"}";
 	 		
 	 		List<NameValuePair> parameters = new ArrayList<>();
 			parameters.add( new BasicNameValuePair("params", params) );
 	 		
 			log.info(String.format("AppService -> delAllGitRepo -->  parameters[%s] deleteallgit[%s]",
 					parameters.toString(), deleteallgit));
 			try {
 				String jsonStr = HttpUtil.httpPost(deleteallgit, parameters);
 				log.info(String.format("AppService -> delAllGitRepo --> deleteallgit jsonStr[%s]", jsonStr));
 				
 				JSONObject obj = JSONObject.fromObject( jsonStr );
 				String status = obj.getString("status");
 				String info = obj.getString("info");
 				
 				map.put("status", status);
 				map.put("info", info);
 				return map;
 			} catch (Exception e) {
 				
 				log.info(String.format("AppService -> delAllGitRepo --> deleteallgit IOException [%s]", ExceptionUtils.getStackTrace(e)));
 				
 				return   new HashMap<>();
 			}
 		}
 	}
 	
 	/**
 	 * 修改应用创建者git权限
 	 * @user jingjian.wu
 	 * @date 2015年12月14日 下午8:17:14
 	 */
 	public Map<String, String> updateGitAuth(List<GitOwnerAuthVO> listAuth) {
 		Map<String, String> map = new HashMap<>();
	 		if(null==listAuth || listAuth.size() < 1) {
				return map;
			}
 		if(gitFlag.equals("new")){
 	 		log.info("------------------------------come into updateGitAuthNew Method!");
 	 		for(GitOwnerAuthVO auth:listAuth){
 	 			User user=this.userDao.findByAccountAndDel(auth.getUsername(), DELTYPE.NORMAL);
 	 			App app=this.appDao.findByAppcanAppIdAndDel(auth.getProjectid(), DELTYPE.NORMAL);
 	 			String gitLibrary=app.getRelativeRepoPath().replace(".git", "")+"/collaborators/";
 	 			String girGrant=user.getNickName();
 	 			Map<String,String> headers = new HashMap<String,String>();
 	 			headers.put("Authorization", "token "+gitToken);
 	 			String result = NewGitHttpUtil.delete(newGitServer+"/api/v3/repos"+gitLibrary+girGrant,headers);
 	 			JSONObject obj = JSONObject.fromObject( result );
 	 			if(!obj.getString("status").equals("OK")){
 	 				throw new RuntimeException("从git仓库中删除用户失败");
 	 			}
// 	 			System.out.println(obj.getString("status"));
 	 		}
 			map.put("status", "ok");
 			return map;
 		}else{
 			log.info("------------------------------come into updateGitAuth Method!");
 	 		
 	 		
// 	 		App app = appDao.findOne(appId);
// 			String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
// 			parameters.add( new BasicNameValuePair("project",  encodeKey.toLowerCase()) );
 	 		
 	 		JSONArray jsonArray = JSONArray.fromObject(listAuth);
 	 		String params = "{\"change\":"+jsonArray.toString()+"}";
 	 		
 	 		List<NameValuePair> parameters = new ArrayList<>();
 			parameters.add( new BasicNameValuePair("params", params) );
 	 		
 			log.info(String.format("AppService -> updateGitAuth -->  parameters[%s] changeallowner[%s]",
 					parameters.toString(), changeallowner));
 			try {
 				String jsonStr = HttpUtil.httpPost(changeallowner, parameters);
 				log.info(String.format("AppService -> updateGitAuth --> changeallowner jsonStr[%s]", jsonStr));
 				
 				JSONObject obj = JSONObject.fromObject( jsonStr );
 				String status = obj.getString("status");
 				String info = obj.getString("info");
 				
 				map.put("status", status);
 				map.put("info", info);
 				return map;
 			} catch (Exception e) {
 				
 				log.info(String.format("AppService -> updateGitAuth --> changeallowner IOException [%s]", ExceptionUtils.getStackTrace(e)));
 				
 				return map;
 			}
 		}
 	}
 	
 	/**
 	 * 生成打包消息
 	 * @param appPackage
 	 * @param icon
 	 * @param statusBar
 	 * @param startSet
 	 * @param engine
 	 * @param plugin
 	 * @param packageObj
 	 * @param switchObj
 	 * @param certificate
 	 */
 	private String makePkgRequest(AppPackage appPackage) {
 		if(appPackage == null) {
 			return null;
 		}
 		
 		JSONObject settings = appPackage.getSettings();
 		
 		if(settings == null) {
 			return null;
 		}

		JSONObject icon = settings.getJSONObject("icon");
		JSONObject statusBar = settings.getJSONObject("statusBar");
		JSONObject startSet = settings.getJSONObject("startSet");
		JSONObject engine = settings.getJSONObject("engine");
		JSONObject plugin = settings.getJSONObject("plugin");
		JSONObject packageObj = settings.getJSONObject("packageObj");
		JSONObject switchObj = settings.getJSONObject("switchObj");
		JSONObject certificate = settings.getJSONObject("certificate");
//		String atsType = packageObj.getString("atsType");
		
 		
 		if(icon == null || statusBar == null || startSet == null || engine == null || 
 				plugin == null || packageObj == null || switchObj == null || certificate == null) {
 			log.info(String.format("makePkgRequest -> param null pack[%s] icon[%s] statusBar[%s] "
 					+ "startSet[%s] engine[%s] plugin[%s] packageObj[%s] switchObj[%s] certificate[%s]", 
 					appPackage  == null ? "NULL" : "Object",
 					icon        == null ? "NULL" : "Object",
 					statusBar   == null ? "NULL" : "Object",
 					startSet    == null ? "NULL" : "Object",
 					engine      == null ? "NULL" : "Object",
 					plugin      == null ? "NULL" : "Object",
 					packageObj  == null ? "NULL" : "Object",
 					switchObj   == null ? "NULL" : "Object",
 					certificate == null ? "NULL" : "Object"
 			));
 			
 			return null;
 		}
 	
 		//加密开关
 		String obfuscationflag = packageObj.containsKey("obfuscationflag")?packageObj.getString("obfuscationflag"):"default";
 		//debug调试开关
 		String debug = packageObj.containsKey("debug")?packageObj.getString("debug"):"false";
 		// 调试服务器地址
 		String logserverip = packageObj.containsKey("logserverip")?packageObj.getString("logserverip"):"";
 		//引擎kernel
 		String kernel = "";
 		
 		// 获取应用相关信息
 		AppVersion versionObj = appVersionDao.findOne(appPackage.getAppVersionId());
 		App app = appDao.findOne(versionObj.getAppId());
// 		String appVersion = versionObj.getVersionNo();
 		String appVersion = appPackage.getVersionNo();//打出来的包名称
 		String channelCode = appPackage.getChannelCode();
 		String appId = app.getAppcanAppId();
 		String appKey = app.getAppcanAppKey();
 		log.info("add package --->-- newAppCanAppId:"+appPackage.getNewAppCanAppId()+",newAppCanAppKey:"+appPackage.getNewAppCanAppKey());
 		if(StringUtils.isNotBlank(appPackage.getNewAppCanAppId()) && StringUtils.isNotBlank(appPackage.getNewAppCanAppKey())){
 			appId = appPackage.getNewAppCanAppId();
 			appKey = appPackage.getNewAppCanAppKey();
 		}
 		/**
 		 * 如果创建应用时候有指定的appID和appKey则打包还的用原先的.例如cloud调用接口创建的应用
 		 */
 		if(StringUtils.isNotBlank(app.getSpecialAppCanAppId()) && StringUtils.isNotBlank(app.getSpecialAppCanAppKey())){
 			appId = app.getSpecialAppCanAppId();
 			appKey = app.getSpecialAppCanAppKey();
 		}
 		String appName = app.getName();
 		
 		String appPackageVersionNo = appPackage.getVersionNo();
 		
 		Project project = projectDao.findOne(app.getProjectId());

	    TerminalType tType = appPackage.getTerminalType();
	    String deviceFamily = 
	    		tType.equals(TerminalType.ANDROID)     ? "android"    : ( 
	    		tType.equals(TerminalType.IPHONE)      ? "iphone"     : ( 
	    		tType.equals(TerminalType.IPAD)        ? "pad"	      : (
	    		tType.equals(TerminalType.IPHONE_IPAD) ? "iphone/pad" : ""	) ) );
 		
 		// 解析packageObj配置
 		String ptSelected = packageObj.getString("ptSelected");	// android, iphone
 		String hardwareAccelerated = packageObj.getString("hardwareAccelerated");
 		String hardwareAcceleratedStr = "false";
 		if(hardwareAccelerated.equals("1") || hardwareAccelerated.equals("true")){
 			hardwareAcceleratedStr = "true";
 		}
 		// platform ：iphone | andorid
 		String platform = (ptSelected == null ? "android" : ( "ANDROID".equals(ptSelected.toUpperCase()) ? "android" : "iphone") );
 		String msgType  = (ptSelected == null ? "PkgAndroid" : ( "ANDROID".equals(ptSelected.toUpperCase()) ? "PkgAndroid" : "PkgIphone") );
 		String typeCert = packageObj.getString("typeCert");
 		String packageName = packageObj.getString("packageName");
 		
// 		String atsContent = "";
		String ats="";
		//ATS配置开始
	    log.info("ATS配置开始>platform:"+platform);
	    if("iphone".equals(platform)){
//	    		atsContent=packageObj.getString("ats");
				ats ="\\\"ats\\\":\\\""+replaceBlank(packageObj.getString("ats"))+"\\\",";
				log.info("----->atsContent:"+replaceBlank(packageObj.getString("ats")));
	    }
 		//增加Android键盘模式
 		String inputType = packageObj.containsKey("inputType")?packageObj.getString("inputType"):"pan";
 
 		
 		// 解析icon配置
 		String iconPath = icon.getString("iconPath");
 		String absIconLoc = (iconPath == null ? "" : iconPath);
 		
 		// 解析statusBar配置
 		String androidBar = statusBar.getString("androidBar");
 		String iphoneBar = statusBar.getString("iphoneBar");
 		boolean showAndroidBar = "1".equals(androidBar) ? true : false;
 		boolean showStatusBar = "android".equals(platform) ? showAndroidBar : true;	// 苹果平台不读取此项配置
 		String startscreen = iphoneBar;	// 苹果平台配置此项
 		String runscreen   = iphoneBar; // 苹果平台配置此项
 		
 		// 解析startSet配置
 		String iphone3ImgLoc = startSet.getString("iphone3ImgLoc");
 	    String absIphone3ImgLoc =  (iphone3ImgLoc == null ? "" : iphone3ImgLoc);
 	    
 		String iphone4ImgLoc = startSet.getString("iphone4ImgLoc");
 	    String absIphone4ImgLoc =  (iphone4ImgLoc == null ? "" : iphone4ImgLoc);
 	    
 		String iphone5ImgLoc = startSet.getString("iphone5ImgLoc");
 	    String absIphone5ImgLoc =  (iphone5ImgLoc == null ? "" : iphone5ImgLoc);
 	    
 		String iphone6ImgLoc = startSet.getString("iphone6ImgLoc");
 	    String absIphone6ImgLoc =  (iphone6ImgLoc == null ? "" : iphone6ImgLoc);
 	    
 	    String ipadPortraitImg1024Loc = startSet.getString("ipadPortraitImg1024Loc");
 	    String absIpadPortraitImg1024Loc = (ipadPortraitImg1024Loc == null ? "" : ipadPortraitImg1024Loc);
 	    
 	    String ipadPortraitImg2048Loc = (String)startSet.get("ipadPortraitImg2048Loc");
 	    String absIpadPortraitImg2048Loc = (ipadPortraitImg2048Loc == null ? "" : ipadPortraitImg2048Loc);
 	                  
 		String absStartupBgColor = "#000000";
 		String absStartupWaterImgLoc = "";

 		String startupFrontImg480Loc = startSet.getString("startupFrontImg480Loc");
 		String absStartupFrontImg480Loc = (startupFrontImg480Loc == null ? "" : startupFrontImg480Loc);
 			
 		String startupBgImgLoc = startSet.getString("startupBgImgLoc");
 		String absStartupBgImgLoc = (startupBgImgLoc == null ? "" : startupBgImgLoc);

		String widgetDir = remoteGitRoot + app.getRelativeRepoPath() + "?tag=" + versionObj.getTagName();
		
		
		String fileSuffix = (ptSelected == null ? ".apk" : ( "ANDROID".equals(ptSelected.toUpperCase()) ? ".apk" : ".ipa") );
		String pgkFileName = "http://127.0.0.1/" + appPackage.getId() + "_" + appPackage.getOsType() + "_" + appVersion + "_" + channelCode + fileSuffix;
//		String pgkFileName = "";//此参数没有用
//        "uiOrientation_android": "UIInterfaceOrientationPortrait",
//        "uiOrientation_ios": "UIInterfaceOrientationPortrait",
		
		String uiOrientation = "UIInterfaceOrientationPortrait";
		if( platform.equals("android") ) {
			uiOrientation = startSet.getString("uiOrientation_android");
		} else {
			uiOrientation = startSet.getString("uiOrientation_ios");
		}
		
		
		
		// 引擎配置
		long engineId = -1;
		String engineStr = "ios_Engine";
		if( platform.equals("android") ) {
			engineId = Long.parseLong( engine.containsKey("wgOneVersion_android")?engine.getString("wgOneVersion_android"):"-99" );
			engineStr = "android_Engine";
		} else {
			engineId = Long.parseLong( engine.containsKey("wgOneVersion_ios")?engine.getString("wgOneVersion_ios"):"-99" );
			
		}
		Engine engineEntity = engineDao.findOne(engineId);
		if(null==engineEntity){
			throw new RuntimeException("引擎不存在");
		}
		//如果引擎被禁用提示选择新引擎
		if(engineEntity.getStatus().equals(EngineStatus.DISABLE)){
			throw new RuntimeException("引擎已被禁用,请选择新引擎");
		}
		String wgOneVersion ="";
		if(null!=engineEntity){
			String enginePackage = engineEntity.getPackageDescription();
			if(enginePackage == null || "".equals(enginePackage)) {
				enginePackage = engineEntity.getVersionNo().replaceAll("sdksuit", engineStr);
			}
			
			/*wgOneVersion = String.format("{\\\"repo\\\":\\\"%s%s\\\", \\\"path\\\":\\\"d_engine_%d_%s_%s_%s/%s\\\"}",
					remoteGitRoot, buildRepoRelativePath, engineEntity.getId(), engineEntity.getType(), engineEntity.getOsType(), engineEntity.getVersionNo(), enginePackage);*/
			kernel = engineEntity.getVersionNo()+"__"+engineEntity.getKernel();//引擎版本号加kernel
			wgOneVersion = String.format("{\\\"repo\\\":\\\"\\\", \\\"path\\\":\\\"%s/%s/%s\\\"}",
					engineRepo,engineEntity.getFilePath(), enginePackage);
		}else{
			log.error("makePkgRequest Info--> failed , engineId with:"+engineId+" is not exist ");
			throw new RuntimeException("引擎不存在");
		}
		
		// 解析plugin配置
		JSONArray pvIdArray = plugin.getJSONArray("versionIdList");
		Set<Long> pvIdSet = new HashSet<>();
		try {
			if(pvIdArray != null) {
				Iterator<?> it = pvIdArray.iterator();
				while(it.hasNext()) {
					String pvId = (String)it.next();
					if("".equals(pvId)) {
						continue;
					}
					pvIdSet.add(Long.parseLong(pvId));
				}
			}
		} catch(Exception e) {
			;
		}
		

//		JSONArray pluginNameList = new JSONArray();
		List<Long> pvIdList = new ArrayList<>(pvIdSet);
		
		String engineVersion = engineEntity.getVersionNo().replace("sdksuit_", "").substring(0, 1);
		//如果打包时候选择debug调试,需要默认将插件uexLog传给打包服务器
		if(debug.equals("true")){
			OSType osType = OSType.ANDROID;
			if( platform.equals("android") ){//android
				osType = OSType.ANDROID;
			}else{//ios
				osType = OSType.IOS;
			}
			String uexLogSql = "select v.id from T_PLUGIN_VERSION v LEFT JOIN T_PLUGIN p on p.id = v.pluginId WHERE p.enName='uexLog' and v.del=0 " 
				    + " and v.osType =  "+osType.ordinal()
				+ " and v.uploadStatus=1 and v.status=0 and p.type =0 "
				+ " and v.versionNo like '"+engineVersion+"%'"
				 + " ORDER BY v.versionNo DESC limit 1 ";
			try {
				log.info("uexLogId-----sql=======>    "+uexLogSql);
				
				
				final List<Long> uexLogLong=new ArrayList<Long>();
				this.jdbcTpl.query(uexLogSql.toString(),
						new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						uexLogLong.add(rs.getLong("id"));
					}
				});
				
				
//				long uexLogId = jdbcTpl.queryForLong(uexLogSql);
				log.info("uexLogId=========>"+(uexLogLong.size()>0?uexLogLong.get(0):"none"));
				if(uexLogLong.size()>0 ){
					long uexLogId =uexLogLong.get(0);
					pvIdList.add(uexLogId);
					StringBuffer buildSetting = new StringBuffer(appPackage.getBuildJsonSettings());
					int lastPluginIdendIndex = buildSetting.indexOf("]},\"switchObj");
					String flag = buildSetting.charAt(lastPluginIdendIndex-1)+"";
					String appendStr = "";//待插入的uexLog插件的ID
					if(flag.equals("[")){//如果没有选择其他插件
						appendStr ="\""+uexLogId+"\"";
					}else{
						appendStr =",\""+uexLogId+"\"";
					}
					buildSetting.insert(lastPluginIdendIndex, appendStr);
//					if(!flag.equals("[")){
//						if(pluginVersionList != null && pluginVersionList.size() > 0) {
//							for(PluginVersion pv : pluginVersionList) {
//								if(Integer.parseInt(pv.getVersionNo().subSequence(0, 1).toString())>Integer.parseInt(engineVersion)){
//									buildSetting.delete(buildSetting.indexOf(pv.getVersionNo().toString())-1, buildSetting.indexOf(pv.getVersionNo().toString())+pv.getVersionNo().length()+2);
//								}
//							}
//						}
//					}
					appPackage.setBuildJsonSettings(buildSetting.toString());
				}
				
			} catch (DataAccessException e) {
				log.error("no found right uexLog");
				e.printStackTrace();
			}
		}
		log.info(String.format("AppService -> sendPkgMsg -> pvIdArray[%s] pvIdSet[%s] pvIdSet.size[%d]",
				pvIdArray.toString(), pvIdSet.toString(), pvIdSet.size()));
		
		List<PluginVersion> pluginVersionList = pluginVersionDao.findByIdIn(pvIdList);
		log.info(String.format("AppService -> sendPkgMsg -> pluginVersionList.size[%d]", pluginVersionList.size()));

		//"{\"name\":\"uexDiaper\",\"pluginUrl\":\"null\",\"pluginResUrl\":\"null\"}"
		String pluginString = "";
		String versionNO="";
		Boolean Flag=false;
		if(pluginVersionList != null && pluginVersionList.size() > 0) {
			Flag=true;
			for(PluginVersion pv : pluginVersionList) {
				// 判断插件和platform是否匹配
				OSType pvOSType = platform.equals("android") ? OSType.ANDROID : OSType.IOS;
				if(pv.getDel().equals(DELTYPE.DELETED) || ! pv.getOsType().equals(pvOSType) ) {
					continue;
				}
//				log.info("----->pv.getVersionNo():"+pv.getVersionNo()+",pv.getPluginId()"+pv.getPluginId());
//				log.info("----->engineVersion:"+engineVersion);
				//判断插件版本号是不是比引擎版本号大，如果大把插件过滤掉
				if(Integer.parseInt(pv.getVersionNo().substring(0, 1))>Integer.parseInt(engineVersion)){
					log.info("pv.getVersionNo().substring(0, 1)):"+pv.getVersionNo().substring(0, 1)+"++++engineVersion:"+engineVersion);
					continue;
				}
				
				Plugin p = pluginDao.findOne(pv.getPluginId());
				if(p == null) {
					continue;
				}
				
//				String pluginPath = String.format("d_pluginVersion_%d_%s_%s_%s_%s", pv.getId(), p.getType(), pv.getOsType(), pv.getVersionNo(), p.getEnName());
				String pluginPath = engineRepo+"/"+pv.getFilePath();
				
//				pv.setPkgGitRepoUrl(remoteGitRoot + buildRepoRelativePath + "/" + pluginPath);
				
//				JSONObject obj = new JSONObject();
//				obj.put("name", p.getEnName());
//				obj.put("pluginUrl", pv.getPkgGitRepoUrl());
//				pluginNameList.add( obj.toString() );	// 打包服务器这里要求的是字符串！
				
				String pluginResPath = "";
				
				if(p.getType().equals(PluginType.PROJECT)) {
					// 项目插件，查找个人的自定义资源包
					PluginResource pr = pluginResourceDao.findOneByPluginVersionIdAndUserId(pv.getId(), appPackage.getUserId());
					if(pr != null) {
//						pluginResPath  = String.format("pluginResource_%d_%d_%s_%s_%s_%s",
//								pr.getId(), pv.getId(), p.getType(), pv.getOsType(), pv.getVersionNo(), p.getEnName());
						pluginResPath = engineRepo+"/"+pr.getFilePath();
					}
				}
				
//				pluginString += ",\"{\\\"repo\\\":\\\"" + remoteGitRoot + buildRepoRelativePath + "\\\",\\\"name\\\":\\\"" + p.getEnName() + "\\\",\\\"pluginPath\\\":\\\"" + pluginPath + "\\\",\\\"pluginResPath\\\":\\\"" + pluginResPath + "\\\"}\"";
				pluginString += ",\"{\\\"repo\\\":\\\"" +  "\\\",\\\"name\\\":\\\"" + p.getEnName() + "\\\",\\\"pluginPath\\\":\\\"" + pluginPath + "\\\",\\\"pluginResPath\\\":\\\"" + pluginResPath + "\\\"}\"";
				versionNO=versionNO+"\""+pv.getId()+"\",";
			}
		}
		if(!versionNO.equals("")){
			StringBuffer buildSetting1 = new StringBuffer(appPackage.getBuildJsonSettings());
			int firstPluginIdendIndex = buildSetting1.indexOf("\"versionIdList\":[");
			int lastPluginIdendIndex = buildSetting1.indexOf("]},\"switchObj");
			String startbuildSetting = buildSetting1.substring(0,firstPluginIdendIndex+17);
//			log.info("--------->startbuildSetting:"+startbuildSetting);
			String endbuildSetting = buildSetting1.substring(lastPluginIdendIndex);
//			log.info("--------->endbuildSetting:"+endbuildSetting);
//			log.info("------------versionNO.substring(0, versionNO.length()-1):"+versionNO.substring(0, versionNO.length()-1));
			appPackage.setBuildJsonSettings(startbuildSetting+versionNO.substring(0, versionNO.length()-1)+endbuildSetting);
		}else if(versionNO.equals("")&&Flag){
			StringBuffer buildSetting1 = new StringBuffer(appPackage.getBuildJsonSettings());
			int firstPluginIdendIndex = buildSetting1.indexOf("\"versionIdList\":[");
			int lastPluginIdendIndex = buildSetting1.indexOf("]},\"switchObj");
			String startbuildSetting = buildSetting1.substring(0,firstPluginIdendIndex+17);
			String endbuildSetting = buildSetting1.substring(lastPluginIdendIndex);
			appPackage.setBuildJsonSettings(startbuildSetting+versionNO+endbuildSetting);
		}
		
		String pushURL = (String)switchObj.get("pushURL");
		String reportURL = (String)switchObj.get("reportURL");
		String increUpdateIF    = (String)switchObj.get("increUpdateIF");	// 增量更新开关
		String pushIF			= (String)switchObj.get("pushIF");
		// 未授权包需要发布至Emm3.0
		if(project.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)
				|| project.getBizLicense().equals(PROJECT_BIZ_LICENSE.BINDING)) {
			// 需要添加默认统计分析插件
//			String pName = platform.equals("android") ? "d_default_emm3.0_plugin_android" : "d_default_emm3.0_plugin_ios";
//			pluginString += ",\"{\\\"name\\\":\\\"uexDataAnalysis\\\",\\\"pluginUrl\\\":\\\"" + remoteGitRoot + "/000/000/000/build.git/" + pName + "\\\",\\\"pluginResUrl\\\":\\\"\\\"}\"";
			
//			String pName = platform.equals("android") ? engineRepo+"/d_default_emm3.0_plugin_android" : engineRepo+"/d_default_emm3.0_plugin_ios";
//			pluginString += ",\"{\\\"repo\\\":\\\"" +  "\\\",\\\"name\\\":\\\"uexDataAnalysis\\\",\\\"pluginPath\\\":\\\"" +  pName + "\\\",\\\"pluginResPath\\\":\\\"\\\"}\"";
			// 修改emm相关地址与开关设定
			pushURL = "http://newpush.appcan.cn/";
			reportURL = "http://newdc.appcan.cn/";
			increUpdateIF = appPackage.getIncreUpdateIF()+"";
			pushIF = appPackage.getPushIF()+"";
			
		}
		
		if(pluginString.length() > 0) {
			pluginString = pluginString.substring(1);
		}
		
		// 解析switchObj配置
		
		// 打包服务器解析的开关 - 共17项
		//		01. appIF			- 应用开关
		//		02. updateIF		- 升级开关
		//		03. onlineIF		- 在线参数
		//		04. pushIF			- 推送开关
		//		05. analizeIF		- 数据统计
		//		06. authIF			- ！！！（协同平台没有）
		//		07. breakPrisonIF	- 越狱检验开关
		//		08. appCertIF		- 应用证书开关
		//		09. increUpdateIF	- ！！！（协同平台没有）
		//		10. mdmIF			- 设备管理开关
		//		11. mcmIF			- 内容管理开关
		//		12. emmIF			- 失联开关
		//		13. validControlIF	- 证书校验 - 新增
		//		14. httpsIF			- ！！！（协同平台没有）
		//		15. emmofflineIF	- ！！！（协同平台没有）
		//		16. sandBoxIF		- 沙箱存储开关 - 新增
		//      17. reportIF
		
		// 协同前段提供，打包服务器未处理的
		//      01. signVerifyIF	- 校验开关
		
		
		String appIF			= (String)switchObj.get("appIF");
		String updateIF			= (String)switchObj.get("updateIF");		// 升级开关
		String onlineIF			= (String)switchObj.get("onlineIF");
		String analizeIF		= (String)switchObj.get("analizeIF");
		
		String breakPrisonIF	= (String)switchObj.get("breakPrisonIF");
		String appCertIF		= (String)switchObj.get("appCertIF");
		
		String mdmIF			= (String)switchObj.get("mdmIF");
		String mcmIF			= (String)switchObj.get("mcmIF");
		String emmIF			= (String)switchObj.get("emmIF");
		
		
		
		
		String signVerifyIF     = (String)switchObj.get("signVerifyIF");
		
		
		
		String reportIF = "1";	// 打包服务器会判定（1为自动上报），协同平台这项配置默认为1

		
		String androidPushURL     = (String)switchObj.get("androidPushURL");
		String analizeURL     = (String)switchObj.get("analizeURL");
		String mdmURL = (String)switchObj.get("mdmURL");
		String mcmURL = (String)switchObj.get("mcmURL");
		String appCertPwd = (String)switchObj.get("appCertPwd");
		String appCertURL = (String)switchObj.get("appCertURL");
		if(StringUtils.isBlank(appCertURL)){
			appCertURL = "";
		}
		
		
		String validControlIF = switchObj.getString("validControlIF");
		String tenantID = switchObj.getString("tenantID");
		String storeHost = switchObj.getString("storeHost");
		String mBassHost = switchObj.getString("mBassHost");
		String imXMPPHost = switchObj.getString("imXMPPHost");
		String imHTTPHost = switchObj.getString("imHTTPHost");
		String taskSubmitHost = switchObj.getString("taskSubmitHost");
		String sandBoxIF = switchObj.getString("sandBoxIF");
		String emmofflineIF = switchObj.containsKey("emmofflineIF")?switchObj.getString("emmofflineIF"):"0";
		
		
		if("0".equals(storeHost)) {
			storeHost = "";
		}
		if("0".equals(mBassHost)) {
			mBassHost = "";
		}
		if("0".equals(imXMPPHost)) {
			imXMPPHost = "";
		}
		if("0".equals(imHTTPHost)) {
			imHTTPHost = "";
		}
		if("0".equals(taskSubmitHost)) {
			taskSubmitHost = "";
		}
		if("0".equals(tenantID)) {
			tenantID = "";
		}

		
		
		// 解析certificate配置		
		String absCertFileLoc = certificate.getString("iosPub1");	 		// 发布证书 -> p12
		String absSignatureFileLoc = certificate.getString("iosPub2");		// 说明文件 -> mobileprovision
		String iphoneAppIds = certificate.getString("iosPub3");				// iphoneAppIds
		String certFilePasswd = certificate.getString("iosPub4");			// 证书密码

		String absTestCertFileLoc = certificate.getString("iosEnterprice1");
		String absTestSignatureFileLoc = certificate.getString("iosEnterprice2");
		String testCertFileId = certificate.getString("iosEnterprice3");
		String testCertFilePasswd = certificate.getString("iosEnterprice4");
			
        String certFile = certificate.getString("androidCertificate1");
		String alias = certificate.getString("androidCertificate2");
		String storepass = certificate.getString("androidCertificate3");
		String keypass = certificate.getString("androidCertificate4");
		
		
		if(appIF == null) appIF = "0";
		if(emmIF == null) emmIF = "0";
		if(signVerifyIF == null) signVerifyIF = "0";
		if(updateIF == null) updateIF = "0";
		if(onlineIF == null) onlineIF = "0";
		if(pushIF == null) pushIF = "0";
		if(analizeIF == null) analizeIF = "0";
		if(mdmIF == null) mdmIF = "0";
		if(mcmIF == null) mcmIF = "0";
		if(breakPrisonIF == null) breakPrisonIF = "0";
		if(appCertIF == null) appCertIF = "0";
		if(emmofflineIF == null) emmofflineIF = "0";
	
			    String str = "{";
			    str +=	"\"pkgRequest\": {";
			    str +=		"\"platform\": \""		+ platform + "\",";	// 平台 android, iphone
			    str +=		"\"appId\": \""			+ appId + "\",";
			    str +=		"\"appName\": \""		+ appName + "\",";
			    str +=		"\"sequenceId\": 0,";
			    str +=      "\"wgOneVersion\": \""	+ wgOneVersion + "\",";	// 引擎版本
			    str +=      "\"deviceFamily\": \""	+ deviceFamily + "\",";	// android, iphone, pad, iphone/pad -> 针对platform的细分
			    str +=      "\"showStatusBar\": "	+ showStatusBar +",";	// 是否全屏
			    str +=      "\"channelCode\": \""	+ channelCode + "\",";
			    str +=      "\"appKey\": \""		+ appKey + "\",";
			    str +=		"\"useBgImg\": true,";
			    str +=		"\"uiOrientation\": \""	+ uiOrientation + "\",";	// 横屏竖屏
			    str +=		"\"absWidgetDir\": \""	+ widgetDir + "\",";	// 资源上传存储路径
			    str +=		"\"pkgExtraCtrlParams\": \"{"
			    				+ "\\\"requestType\\\":\\\"hybrid\\\","
			    				+ "\\\"startscreen\\\":\\\"" + startscreen + "\\\","
			    				+ "\\\"runscreen\\\":\\\"" + runscreen + "\\\","
			    				+ "\\\"ios7Flag\\\":\\\"true\\\","
			    				+ "\\\"reportIF\\\":\\\"" + reportIF + "\\\","
			    				+ "\\\"appIF\\\":\\\"" + appIF + "\\\","
			    				+ "\\\"updateIF\\\":\\\"" + updateIF + "\\\","				// 更新开关
			    				+ "\\\"onlineIF\\\":\\\"" + onlineIF + "\\\","
			    				+ "\\\"pushIF\\\":\\\"" + pushIF + "\\\","					// 是否推送
			    				+ "\\\"analizeIF\\\":\\\"" + analizeIF + "\\\","
			    				+ "\\\"breakPrisonIF\\\":\\\"" + breakPrisonIF + "\\\","	// 是否越狱
			    				+ "\\\"myspaceIF\\\":\\\"0\\\","
			    				+ "\\\"appCertIF\\\":\\\"" + appCertIF + "\\\","
			    				+ "\\\"increUpdateIF\\\":\\\"" + increUpdateIF + "\\\","	// 增量更新
			    				+ "\\\"hardwareAccelerated\\\":\\\"" + hardwareAcceleratedStr + "\\\","	// 硬件加速
			    				+ "\\\"reportURL\\\":\\\"" + reportURL + "\\\","
			    				+ "\\\"analizeURL\\\":\\\"" + analizeURL + "\\\","
			    				+ "\\\"pushURL\\\":\\\"" + pushURL + "\\\","
			    				+ "\\\"androidPushURL\\\":\\\"" + androidPushURL + "\\\","
			    				+ "\\\"immediatePushURL\\\":\\\"null\\\","
			    				+ "\\\"appCertPwd\\\":\\\"" + appCertPwd + "\\\","
			    				+ "\\\"appCertURL\\\":\\\""+appCertURL+"\\\","
			    				+ "\\\"pkgCount\\\":\\\""+getAndroidPkgCountFromAppVersion(appPackageVersionNo)+"\\\","
			    				
			    				// 常规

			    				// 新增打包设定 - 2015-12-24
			    				+ "\\\"validControlIF\\\":\\\"" + validControlIF + "\\\","
			    				+ "\\\"tenantID\\\":\\\"" + tenantID + "\\\","
			    				+ "\\\"storeHost\\\":\\\"" + storeHost + "\\\","
			    				+ "\\\"mBassHost\\\":\\\"" + mBassHost + "\\\","
			    				+ "\\\"imXMPPHost\\\":\\\"" + imXMPPHost + "\\\","
			    				+ "\\\"imHTTPHost\\\":\\\"" + imHTTPHost + "\\\","
			    				+ "\\\"taskSubmitHost\\\":\\\"" + taskSubmitHost + "\\\","
			    				+ "\\\"sandBoxIF\\\":\\\"" + sandBoxIF + "\\\","
			    				+ "\\\"signVerifyIF\\\":\\\"" + signVerifyIF + "\\\","
			    				
			    				
								 + "\\\"mdmIF\\\":\\\"" + mdmIF + "\\\","
								 + "\\\"mcmIF\\\":\\\"" + mcmIF + "\\\","
								 + "\\\"emmIF\\\":\\\"" + emmIF + "\\\","
								 + "\\\"mdmURL\\\":\\\"" + mdmURL + "\\\","
								 + "\\\"mcmURL\\\":\\\"" + mcmURL + "\\\","
								 + "\\\"emmofflineIF\\\":\\\"" + emmofflineIF + "\\\","
								 
		 
			    				// 安卓证书设定			    				
			    				+ "\\\"certFile\\\":\\\"" + certFile + "\\\","
			    				+ "\\\"alias\\\":\\\"" + alias + "\\\","
			    				+ "\\\"keypass\\\":\\\"" + keypass + "\\\","
			    				+ "\\\"storepass\\\":\\\"" + storepass + "\\\","
			    				
			    				+ "\\\"inputType\\\":\\\""+inputType+"\\\","
			    				
			    				//新增加密,debug开关(开始)
			    				+ "\\\"obfuscationflag\\\":\\\""+obfuscationflag +"\\\","
			    				+ "\\\"debug\\\":\\\""+debug +"\\\","
			    				+ "\\\"logserverip\\\":\\\""+logserverip+"\\\","
			    				+ "\\\"kernel\\\":\\\""+kernel+"\\\","
			    				+ ats
			    				//新增加密,debug开关(结束)
			    				+ "\\\"packageName\\\":\\\"" + packageName + "\\\"}\",";
			    str +=		"\"pluginNameList\": [";
			    str +=      pluginString;
			    str +=		"],";
			    str +=		"\"appVersion\": \""	+ appPackageVersionNo + "\",";
			    str +=		"\"createdAt\": null,";
			    str +=		"\"taskId\": \"" + appPackage.getId() + "\",";
			    str +=		"\"pkgAccessUrl\": \""			+ pkgAccessUrl + "\",";
			    str +=		"\"absPkgFileName\": \""		+ pgkFileName + "\",";
			    str +=		"\"absIconLoc\": \""			+ absIconLoc + "\",";
			    str +=		"\"absStartupFrontImg240Loc\": \"\",";
			    str +=		"\"absStartupFrontImg480Loc\": \"" + absStartupFrontImg480Loc + "\",";
			    str +=		"\"absStartupBgImgLoc\": \""	+ absStartupBgImgLoc + "\",";
			    str +=		"\"absStartupBgColor\": \""		+ absStartupBgColor + "\",";
			    str +=		"\"absStartupWaterImgLoc\": \"" + absStartupWaterImgLoc + "\",";
			    str +=		"\"absIphone3ImgLoc\": \"" + absIphone3ImgLoc + "\",";
			    str +=		"\"absIphone4ImgLoc\": \"" + absIphone4ImgLoc + "\",";
			    str +=		"\"absIphone5ImgLoc\": \"" + absIphone5ImgLoc + "\",";
//			    str +=		"\"absIphone6ImgLoc\": \"" + absIphone6ImgLoc + "\",";
//			    str +=		"\"absIpadLandscapeLeftImg1024Loc\": \"\",";//横屏ipad
			    str +=		"\"absIpadLandscapeLeftImg1024Loc\": \""+absIpadPortraitImg2048Loc+"\",";//横屏ipad
			    str +=		"\"absIpadLandscapeLeftImg2048Loc\": \"\",";//没有用
			    str +=		"\"absIpadPortraitImg1024Loc\": \"" + absIphone6ImgLoc + "\",";//iphone6
			    str +=		"\"absIpadPortraitImg2048Loc\": \"" + absIpadPortraitImg1024Loc + "\",";
			    // 苹果证书设定
			    // 发布：publishCert  -> 发布 -> absSignatureFileLoc
			    // 开发：developeCert -> 企业 -> absTestSignatureFileLoc
			    // 越狱：noCert
			    
			    // 安卓证书设定
			    // AppCan证书：noCert
			    // 自定义证书：  defineAndroid
			    if("iphone".equals(platform) && "publishCert".equals(typeCert)) {
				    str +=		"\"absSignatureFileLoc\": \"" + absSignatureFileLoc + "\",";
				    str +=		"\"absCertFileLoc\": \"" + absCertFileLoc + "\",";
				    str +=		"\"certFilePasswd\": \"" + certFilePasswd + "\",";
				    str +=		"\"iphoneAppIds\": \"" + iphoneAppIds + "\",";

			    } else if("iphone".equals(platform) && "developeCert".equals(typeCert)) {
				    str +=		"\"absTestSignatureFileLoc\": \"" + absTestSignatureFileLoc + "\",";
				    str +=		"\"absTestCertFileLoc\": \"" + absTestCertFileLoc + "\",";
				    str +=		"\"certFilePasswd\": \"" + testCertFilePasswd + "\",";
				    str +=		"\"iphoneAppIds\": \"" + testCertFileId + "\",";
				    str +=		"\"testCertFilePasswd\": \"" + testCertFilePasswd + "\",";
				    str +=		"\"testCertFileId\": \"" + testCertFileId + "\",";

			    }
			    str +=		"\"typeCert\": \"" + typeCert + "\"";
			    str +=	"},";
			    str +=	"\"msgType\": \"" + msgType + "\",";
			    str +=	"\"sender\": null";
			    str +=	"}";		    

		return str;

 	}
 	
 	/**
 	 * 原生应用打包
 	 * typeCert:publishCert,developeCert,adhocCert//证书类型
		//iosPub1,   //发布证书-> p12
		//iosPub2 ,  //发布证书说明文件 -> mobileprovision
		//iosPub3 ,  // 发布证书iphoneAppIds
		//iosPub4 ,  //发布证书密码
		 * 
		//iosEnterprice1   //企业证书 -> p12
		//iosEnterprice2  //企业证书说明文件 -> mobileprovision
		//iosEnterprice3  //企业证书 iphoneAppIds
		//iosEnterprice4  //企业证书证书密码
		 * 
		 * //adhoc1,   //adhoc证书 -> p12
		//adhoc2 ,  //adhoc证书说明文件 -> mobileprovision
		//adhoc3 ,  // adhoc证书iphoneAppIds
		//adhoc4 ,  //adhoc证书密码
		 * 
		//"scheme":"",            //打包scheme名称(用户填写)
		//"projectname":"",      //主工程名称(用户填写)
		//"appVersionId":12,    //版本ID(基于哪个版本打包的)
		//"buildTypeStr":"TESTING 或者 PRODUCTION",//正式包,测试包
		//"versionDescription":"版本描述"
		{"cert":["typeCert":"","appCertPwd":"","appCertURL":"","iosPub1":"","iosPub2":"","iosPub3":"","iosPub4":"",
		      "iosEnterprice1":"","iosEnterprice2":"","iosEnterprice3":"","iosEnterprice4":"",
		      "adhoc1":"","adhoc2":"","adhoc3":"","adhoc4":""],
			  "scheme":"",       
			  "projectname":"", 
			  "appVersionId":12, 
			  "buildTypeStr":"",
			  "versionDescription":"版本描述"
	  	}
 	 * @param appPackage
 	 * @return
 	 */
 	private String makePkgRequestForNative(AppPackage appPackage) {
 		if(appPackage == null) {
 			return null;
 		}
 		
 		JSONObject settings = appPackage.getSettings();
 		
 		if(settings == null) {
 			return null;
 		}

		JSONObject cert = settings.containsKey("cert")?settings.getJSONObject("cert"):null;
		String scheme = settings.getString("scheme");
		String projectname = settings.getString("projectname");
		
 	
 		
 		// 获取应用相关信息
 		AppVersion versionObj = appVersionDao.findOne(appPackage.getAppVersionId());
 		App app = appDao.findOne(versionObj.getAppId());
 		
// 		String appPackageVersionNo = appPackage.getVersionNo();
 		
// 		Project project = projectDao.findOne(app.getProjectId());

 		// platform ：iphone | andorid
 		String platform = (app.getPlatForm() == null ? "android" : ( "ANDROID".equals(app.getPlatForm().toUpperCase()) ? "android" : "iphone") );
 		String msgType  = (app.getPlatForm() == null ? "PkgAndroid" : ( "ANDROID".equals(app.getPlatForm().toUpperCase()) ? "PkgAndroid" : "PkgIphone") );
 		String typeCert = "noCert";
 		if(null !=cert && cert.containsKey("typeCert")){
 			typeCert = cert.getString("typeCert");
 		}
 		

		String widgetDir = remoteGitRoot + app.getRelativeRepoPath() + "?tag=" + versionObj.getTagName();
		
		
		String fileSuffix = (app.getPlatForm() == null ? ".apk" : ( "ANDROID".equals(app.getPlatForm().toUpperCase()) ? ".apk" : ".ipa") );
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		String pgkFileName = "http://127.0.0.1/" + appPackage.getId() + "_" + appPackage.getOsType() + "_" + versionObj.getId() + "_" + sdf.format(Calendar.getInstance().getTime()) + fileSuffix;
		
		
		
		String appCertPwd = "";
		if(null !=cert && cert.containsKey("appCertPwd")){
 			appCertPwd =(String)cert.get("appCertPwd");
 		}
		
		String appCertURL = "";
		if(null !=cert && cert.containsKey("appCertURL")){
			appCertURL =(String)cert.get("appCertURL");
 		}
		
		
		
		// 解析certificate配置		
		String absCertFileLoc = cert.getString("iosPub1");	 		// 发布证书 -> p12
		String absSignatureFileLoc = cert.getString("iosPub2");		// 说明文件 -> mobileprovision
		String iphoneAppIds = cert.getString("iosPub3");				// iphoneAppIds
		String certFilePasswd = cert.getString("iosPub4");			// 证书密码

		String absTestCertFileLoc = cert.getString("iosEnterprice1");
		String absTestSignatureFileLoc = cert.getString("iosEnterprice2");
		String testCertFileId = cert.getString("iosEnterprice3");
		String testCertFilePasswd = cert.getString("iosEnterprice4");
		
		String adhocCertFileLoc = cert.getString("adhoc1");
		String adhocSignatureFileLoc = cert.getString("adhoc2");
		String adhocCertFileId = cert.getString("adhoc3");
		String adhocCertFilePasswd = cert.getString("adhoc4");
			
//        String certFile = cert.getString("androidCertificate1");
//		String alias = cert.getString("androidCertificate2");
//		String storepass = cert.getString("androidCertificate3");
//		String keypass = cert.getString("androidCertificate4");
		
		
			    String str = "{";
			    str +=	"\"pkgRequest\": {";
			    str +=		"\"platform\": \""		+ platform + "\",";	// 平台 android, iphone
			    str +=		"\"appId\": \""			+ app.getAppcanAppId() + "\",";
			    str +=		"\"appName\": \""		+ app.getName() + "\",";
			    str +=		"\"sequenceId\": 0,";
			    str +=      "\"appKey\": \""		+ app.getAppcanAppKey() + "\",";
			    str +=		"\"useBgImg\": true,";
			    str +=		"\"absWidgetDir\": \""	+ widgetDir + "\",";	// 资源上传存储路径
			    str +=		"\"pkgExtraCtrlParams\": \"{"
			    				+ "\\\"ios7Flag\\\":\\\"true\\\","
			    				+ "\\\"myspaceIF\\\":\\\"0\\\","
			    				+ "\\\"immediatePushURL\\\":\\\"null\\\","
			    				+ "\\\"appCertPwd\\\":\\\"" + appCertPwd + "\\\","
			    				+ "\\\"appCertURL\\\":\\\""+appCertURL+"\\\","
//			    				+ "\\\"pkgCount\\\":\\\""+getAndroidPkgCountFromAppVersion(appPackageVersionNo)+"\\\","
								//原生打包特有选项
			    				
			    				+ "\\\"requestType\\\":\\\"native\\\","
								+ "\\\"scheme\\\":\\\""+scheme+"\\\","
								+ "\\\"projectname\\\":\\\""+projectname+"\\\"}\",";
//			    				+ "\\\"packageName\\\":\\\"" + packageName + "\\\"}\",";
//			    str +=		"\"appVersion\": \""	+ appPackageVersionNo + "\",";
			    str +=		"\"createdAt\": null,";
			    str +=		"\"taskId\": \"" + appPackage.getId() + "\",";
			    str +=		"\"pkgAccessUrl\": \""			+ pkgAccessUrl + "\",";
			    str +=		"\"absPkgFileName\": \""		+ pgkFileName + "\",";
			    // 苹果证书设定
			    // 发布：publishCert  -> 发布 -> absSignatureFileLoc
			    // 开发：developeCert -> 企业 -> absTestSignatureFileLoc
			    // 越狱：noCert
			    
			    // 安卓证书设定
			    // AppCan证书：noCert
			    // 自定义证书：  defineAndroid
			    if("iphone".equals(platform) && "publishCert".equals(typeCert)) {
				    str +=		"\"absSignatureFileLoc\": \"" + absSignatureFileLoc + "\",";
				    str +=		"\"absCertFileLoc\": \"" + absCertFileLoc + "\",";
				    str +=		"\"certFilePasswd\": \"" + certFilePasswd + "\",";
				    str +=		"\"iphoneAppIds\": \"" + iphoneAppIds + "\",";

			    } else if("iphone".equals(platform) && "developeCert".equals(typeCert)) {
				    str +=		"\"absTestSignatureFileLoc\": \"" + absTestSignatureFileLoc + "\",";
				    str +=		"\"absTestCertFileLoc\": \"" + absTestCertFileLoc + "\",";
				    str +=		"\"certFilePasswd\": \"" + testCertFilePasswd + "\",";
				    str +=		"\"iphoneAppIds\": \"" + testCertFileId + "\",";
				    str +=		"\"testCertFilePasswd\": \"" + testCertFilePasswd + "\",";
				    str +=		"\"testCertFileId\": \"" + testCertFileId + "\",";

			    }else if("iphone".equals(platform) && "adhocCert".equals(typeCert)) {
			    	typeCert = "publishCert";
			    	str +=		"\"absSignatureFileLoc\": \"" + adhocSignatureFileLoc + "\",";
				    str +=		"\"absCertFileLoc\": \"" + adhocCertFileLoc + "\",";
				    str +=		"\"certFilePasswd\": \"" + adhocCertFilePasswd + "\",";
				    str +=		"\"iphoneAppIds\": \"" + adhocCertFileId + "\",";

			    }
			    str +=		"\"typeCert\": \"" + typeCert + "\"";
			    str +=	"},";
			    str +=	"\"msgType\": \"" + msgType + "\",";
			    str +=	"\"sender\": null";
			    str +=	"}";		    

		return str;

 	}
 	
 	/**
 	 * 发送打包消息至RabbitMQ消息队列
 	 * @param pkgRequest
 	 */
 	private void sendPkgRequest(String pkgRequest) {
 			try {
 				
 				log.info( String.format("sendPkgMsg -> rabbitMqHost[%s] rabbitMqPort[%s] rabbitMqExchange[%s], rabbitMqRouteKey[%s] username[%s] password[%s]",
 						rabbitMqHost, rabbitMqPort, rabbitMqExchange, rabbitMqRouteKey, rabbitMqUser, rabbitMqPassword) );
 				
 		 		ConnectionFactory factory = new ConnectionFactory();
 		 		factory.setHost(rabbitMqHost);
 		 		factory.setPort(rabbitMqPort);
 		 		
 		 		factory.setUsername(rabbitMqUser);
 		 		factory.setPassword(rabbitMqPassword);
 		 		
 				Connection connection = factory.newConnection();
 				log.info("RabbmitMq get Connection ok!");
 				Channel channel = connection.createChannel();
 				    
 				    byte[] messageBodyBytes = pkgRequest.getBytes("UTF-8");
 				    
 				    Map<String, Object> headers = new HashMap<String, Object>();
 				    headers.put("__TypeId__", "org.zywx.appdo.core.pkg.pojo.PkgRequestMessage");
 				    
 				    try {
 					    //需要绑定路由键  
 					    channel.basicPublish(
 					    	rabbitMqExchange,
 					    	rabbitMqRouteKey,
 					    	new AMQP.BasicProperties(
 					    		"application/json",
 					    		"UTF-8", headers, null,null, null, null, null, null, null, null, null, null, null), 
 					    		messageBodyBytes
 					    );
 					    
 					    log.info("sendPkgRequest -> " + pkgRequest);
 				    } catch (Exception e) {
 				    	log.info("sendPkgRequest Error -> " + e.getStackTrace());
 				    } finally {
 				    	boolean isChannelOpen = channel.isOpen();
 						boolean isConnectionOpen = connection.isOpen();
 						
 						log.info(String.format("sendPkgMsg -> finally -> isChannelOpen[%s] isConnectionOpen[%s]", isChannelOpen, isConnectionOpen));
 						
 			 		    try {
 			 		    	if( isChannelOpen ) {
 			 		    		channel.close();
 			 		    	}
 			 		    	
 			 		    	if( isConnectionOpen ) {
 			 		    		connection.close();
 			 		    	}
 			 		    	
 						} catch (IOException e) {
 							e.printStackTrace();
 						} catch (TimeoutException e) {
 							e.printStackTrace();
 						}
 				    }
 				
 			    
 				//channel.close();
 				//connection.close();

 		} catch (IOException e) {
 			log.info("IOException:" + ExceptionUtils.getStackTrace(e));

 		} catch (TimeoutException e) {
			e.printStackTrace();
		} 		
 	}
 	
 	/**
 	 * 分析分支节点
 	 * @param oriTreeId
 	 * @param branchName
 	 * @return
 	 */
 	private List<Map<String, String>> anlysisTreeId(String oriTreeId, String branchName) {
 		// "/a/b/c"
 		// "/a/b"
 		// "/a"
 		// ""
 		List<Map<String, String>> retArr = new ArrayList<>();
		Map<String, String> rootItem = new HashMap<>();
		rootItem.put("branchName", branchName);
		rootItem.put("treeId", "");
		rootItem.put("directory", branchName);
		retArr.add(rootItem);
 		
 		
 		int nextSepIdx = oriTreeId.indexOf("/");
 		while(nextSepIdx != -1) {
 			int encloseSepIdx = oriTreeId.indexOf("/", nextSepIdx + 1);
 			if(encloseSepIdx != -1) {
 				String directory = oriTreeId.substring(nextSepIdx + 1, encloseSepIdx);
 				String treeId = oriTreeId.substring(0, encloseSepIdx);
 				Map<String, String> map = new HashMap<>();
 				map.put("branchName", branchName);
 				map.put("treeId", treeId);
 				map.put("directory", directory);
 				retArr.add(map);
 				
 				nextSepIdx = encloseSepIdx;
 				
 			} else {
 				String treeId = oriTreeId;
 				String directory = oriTreeId.substring(nextSepIdx + 1);
 				Map<String, String> map = new HashMap<>();
 				map.put("branchName", branchName);
 				map.put("treeId", treeId);
 				map.put("directory", directory);
 				retArr.add(map);
 				break;
 			}
 		}
 		
 		return retArr;
 	}
  	
	public App findOne(Long appId){
		return this.appDao.findOne(appId);
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		/*JSONObject obj = new JSONObject();
		obj = JSONObject.fromObject("{\"icon\":{\"type\":\"project\",\"appId\":\"258\",\"iconPath\":\"https://zymobi.appcan.cn/zymobiResource/icon/130/258/1451284377898.png\"},\"statusBar\":{\"androidBar\":\"0\",\"iphoneBar\":\"0\"},\"startSet\":{\"uiOrientation_android\":\"UIInterfaceOrientationPortrait\",\"uiOrientation_ios\":\"UIInterfaceOrientationPortrait\",\"iphone3ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_1.png\",\"iphone4ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_2.png\",\"iphone5ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_3.png\",\"iphone6ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_4.png\",\"startupBgImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/andorid-y1.png\",\"startupFrontImg480Loc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/andorid-y2.png\",\"ipadPortraitImg1024Loc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/pad-x.png\",\"ipadPortraitImg2048Loc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/pad-y.png\"},\"engine\":{\"type\":\"project\",\"appId\":\"258\",\"iosName\":\"sdksuit_3.2_151113_01\",\"androidName\":\"sdksuit_3.1_151109_01\",\"wgOneVersion_ios\":\"16\",\"wgOneVersion_android\":\"24\",\"iosDes\":\"当前引擎描述：当前引擎描述：*现在在root页面加载完成后 经过500ms的延时 启动图才会关闭。解决在部分设备上，应用启动之后会黑屏闪一下的问题。\",\"andDes\":\"当前引擎描述：当前引擎描述：1.Window支持侧滑关闭 2.优化openMultiPopover：优先加载默认页面索引的网页，待其加载完成后，再加载剩下的网页 3.支持锚点链接的跳转，支持ionic\"},\"plugin\":{\"versionIdList\":[]},\"switchObj\":{\"reportURL\":\"\",\"appIF\":\"0\",\"emmIF\":\"0\",\"signVerifyIF\":\"0\",\"updateIF\":\"0\",\"onlineIF\":\"0\",\"pushIF\":\"0\",\"analizeIF\":\"0\",\"mdmIF\":\"0\",\"mcmIF\":\"0\",\"authIF\":\"0\",\"breakPrisonIF\":\"0\",\"updateSwitch\":\"0\",\"appCertIF\":\"0\",\"validControlIF\":\"0\",\"sandBoxIF\":\"0\",\"pushURL\":\"\",\"androidPushURL\":\"\",\"analizeURL\":\"\",\"mdmURL\":\"\",\"mcmURL\":\"\",\"mamURL\":\"\",\"appCertPwd\":\"\",\"tenantID\":\"\",\"storeHost\":\"\",\"mBassHost\":\"\",\"imXMPPHost\":\"\",\"imHTTPHost\":\"\",\"taskSubmitHost\":\"\"},\"certificate\":{\"iosEnterprice1\":\"\",\"iosEnterprice2\":\"\",\"iosEnterprice3\":\"\",\"iosEnterprice4\":\"\",\"iosPub1\":\"\",\"iosPub2\":\"\",\"iosPub3\":\"\",\"iosPub4\":\"\",\"androidCertificate1\":\"\",\"androidCertificate2\":\"\",\"androidCertificate3\":\"\",\"androidCertificate4\":\"\"},\"packageObj\":{\"type\":\"project\",\"appId\":\"258\",\"packageName\":\"\",\"webapp\":\"0\",\"channelCode\":\"11111\",\"version\":\"11.11.1111\",\"typeCert\":\"noCert\",\"appName\":\"aaaa\",\"appKey\":\"21429465-ecaa-4e1d-9a2f-547d68f1fa2e\",\"creator\":\"谷婷婷\",\"ptSelected\":\"iphone\",\"appPath\":\"/coopDevelopment/package/package\",\"inputType\":\"pan\",\"terminalType\":\"IPAD\",\"description\":\"111111111111111111111111111\",\"hardwareAccelerated\":\"0\",\"buildType\":\"PRODUCTION\",\"appVersionId\":\"384\",\"versionNo\":\"11.11.1111\",\"versionDescription\":\"111111111111111111111111111\",\"branchName\":\"\"},\"paramters\":{\"buildType\":\"PRODUCTION\",\"appId\":\"258\",\"versionNo\":\"11.11.1111\",\"branchName\":\"\",\"versionDescription\":\"111111111111111111111111111\",\"appVersionId\":\"384\"}}");
		obj.put("t", "abc");
		JSONObject abc = obj.getJSONObject("icon");
		log.info(abc.getString("iconPath"));
		
		String regexId = "a123afd3";
		String regexKey = "2sadasdf-1s34-1234-1243-sakghk334567";
		log.info(appIdPattern.matcher(regexId).matches());
		log.info(appKeyPattern.matcher(regexKey).matches());
		
		  Scanner sc = new Scanner(System.in);
		  log.info("请输入一串数字，通过空格分开，负数作为结束：");
		  double n = sc.nextDouble();
		  while (n >= 0) {
		   NumberFormat nf = NumberFormat.getInstance();
		   nf.setMinimumFractionDigits(1);
		   log.info(n + "的平方根是：" + (double)Math.round(Math.sqrt(n)*10)/10);
		   log.info(n + "除以3：" + n/3);
		   n = sc.nextDouble();
		  }  log.info("输入负数，结束程序");
		  
		  log.info((double)Math.round((double)12341/1024*10)/10);*/
//		  log.info(Pattern.compile("^[a-zA-Z]\\w*").matcher("a22").matches());
//		  log.info(Pattern.compile("^[a-zA-Z]\\w*").matcher("22ffabc").matches());
//		  log.info("abc".matches("^[a-zA-Z]\\w*"));
//		  log.info(appIdPattern.matcher("789654123").matches());
		/*String str = URLEncoder.encode("011/532/581/xfa648.git");
		System.out.println(str);
		Map<String,String> params = new HashMap<String,String>();
		String jsonStr = HttpUtil.httpPost("http://192.168.1.69:8080/git/branch/listApp/"+str,params);
		System.out.println(jsonStr);*/
//		String engineVersion = "sdksuit_4.0_151113_01".replace("sdksuit_", "").substring(0, 1);
//		System.out.println(engineVersion);
//		int str = new AppService().getAndroidPkgCountFromAppVersion("01.00.9687");
//		System.out.println(str);
		/*StringBuffer buildSetting = new StringBuffer("\"versionIdList\":[]},\"switchObj\":{\"type\":\"project\",\"appId\":\"1594\",\"reportURL\":");
		int lastPluginIdendIndex = buildSetting.indexOf("]},\"switchObj");
		String flag = buildSetting.charAt(lastPluginIdendIndex-1)+"";
		String appendStr = "";//待插入的uexLog插件的ID
		if(flag.equals("[")){//如果没有选择其他插件
			appendStr ="\""+99999+"\"";
		}else{
			appendStr =",\""+99999+"\"";
		}
		buildSetting.insert(lastPluginIdendIndex, appendStr);
		System.out.println(buildSetting);*/
//		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
//		System.out.println(sdf.format(Calendar.getInstance().getTime()));
		JSONObject obj = new JSONObject();
		obj = JSONObject.fromObject("{\"icon\":{\"type\":\"project\",\"appId\":\"258\",\"iconPath\":\"https://zymobi.appcan.cn/zymobiResource/icon/130/258/1451284377898.png\"},\"statusBar\":{\"androidBar\":\"0\",\"iphoneBar\":\"0\"},\"startSet\":{\"uiOrientation_android\":\"UIInterfaceOrientationPortrait\",\"uiOrientation_ios\":\"UIInterfaceOrientationPortrait\",\"iphone3ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_1.png\",\"iphone4ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_2.png\",\"iphone5ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_3.png\",\"iphone6ImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/iphone2_4.png\",\"startupBgImgLoc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/andorid-y1.png\",\"startupFrontImg480Loc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/andorid-y2.png\",\"ipadPortraitImg1024Loc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/pad-x.png\",\"ipadPortraitImg2048Loc\":\"https://zymobi.appcan.cn/zymobiResource/icon/defaultIcon/pad-y.png\"},\"engine\":{\"type\":\"project\",\"appId\":\"258\",\"iosName\":\"sdksuit_3.2_151113_01\",\"androidName\":\"sdksuit_3.1_151109_01\",\"wgOneVersion_ios\":\"16\",\"wgOneVersion_android\":\"24\",\"iosDes\":\"当前引擎描述：当前引擎描述：*现在在root页面加载完成后 经过500ms的延时 启动图才会关闭。解决在部分设备上，应用启动之后会黑屏闪一下的问题。\",\"andDes\":\"当前引擎描述：当前引擎描述：1.Window支持侧滑关闭 2.优化openMultiPopover：优先加载默认页面索引的网页，待其加载完成后，再加载剩下的网页 3.支持锚点链接的跳转，支持ionic\"},\"plugin\":{\"versionIdList\":[]},\"switchObj\":{\"reportURL\":\"\",\"appIF\":\"0\",\"emmIF\":\"0\",\"signVerifyIF\":\"0\",\"updateIF\":\"0\",\"onlineIF\":\"0\",\"pushIF\":\"0\",\"analizeIF\":\"0\",\"mdmIF\":\"0\",\"mcmIF\":\"0\",\"authIF\":\"0\",\"breakPrisonIF\":\"0\",\"updateSwitch\":\"0\",\"appCertIF\":\"0\",\"validControlIF\":\"0\",\"sandBoxIF\":\"0\",\"pushURL\":\"\",\"androidPushURL\":\"\",\"analizeURL\":\"\",\"mdmURL\":\"\",\"mcmURL\":\"\",\"mamURL\":\"\",\"appCertPwd\":\"\",\"tenantID\":\"\",\"storeHost\":\"\",\"mBassHost\":\"\",\"imXMPPHost\":\"\",\"imHTTPHost\":\"\",\"taskSubmitHost\":\"\"},\"certificate\":{\"iosEnterprice1\":\"\",\"iosEnterprice2\":\"\",\"iosEnterprice3\":\"\",\"iosEnterprice4\":\"\",\"iosPub1\":\"\",\"iosPub2\":\"\",\"iosPub3\":\"\",\"iosPub4\":\"\",\"androidCertificate1\":\"\",\"androidCertificate2\":\"\",\"androidCertificate3\":\"\",\"androidCertificate4\":\"\"},\"packageObj\":{\"type\":\"project\",\"appId\":\"258\",\"packageName\":\"\",\"webapp\":\"0\",\"channelCode\":\"11111\",\"version\":\"11.11.1111\",\"typeCert\":\"noCert\",\"appName\":\"aaaa\",\"appKey\":\"21429465-ecaa-4e1d-9a2f-547d68f1fa2e\",\"creator\":\"谷婷婷\",\"ptSelected\":\"iphone\",\"appPath\":\"/coopDevelopment/package/package\",\"inputType\":\"pan\",\"terminalType\":\"IPAD\",\"description\":\"111111111111111111111111111\",\"hardwareAccelerated\":\"0\",\"buildType\":\"PRODUCTION\",\"appVersionId\":\"384\",\"versionNo\":\"11.11.1111\",\"versionDescription\":\"111111111111111111111111111\",\"branchName\":\"\"},\"paramters\":{\"buildType\":\"PRODUCTION\",\"appId\":\"258\",\"versionNo\":\"11.11.1111\",\"branchName\":\"\",\"versionDescription\":\"111111111111111111111111111\",\"appVersionId\":\"384\"}}");
		obj.put("t", "abc");
		System.out.println(obj.toString());
		obj.put("cc", "dd");
		System.out.println(obj.toString());
	}

	
	/**
	 * 
	 * @describe 添加分支時，添加操作記錄	<br>
	 * @author jiexiong.liu	<br>
	 * @param add 
	 * @date 2015年10月23日 上午11:20:28	<br>
	 * @param loginUserId
	 * @param app  <br>
	 * @returnType void
	 *
	 */
	public void addGitOperationLog(GIT_OPERATE_TYPE type, long loginUserId, App app) {
		GitOperationLog gitOperationLog = new GitOperationLog();
		gitOperationLog.setUserId(loginUserId);
		User user = this.userDao.findOne(loginUserId);
		gitOperationLog.setAccount(user!=null?user.getAccount():loginUserId+"");
		gitOperationLog.setGitRemoteUrl(remoteGitRoot+app.getRelativeRepoPath());
		gitOperationLog.setAppId(app.getId());
		gitOperationLog.setType(type);
		this.gitOperationLogDao.save(gitOperationLog);
	}

	
	public Project findProject(long appId) {
		App app = this.findOne(appId);
		return this.projectDao.findOne(app.getProjectId());
	}

	
	public App findByAppVersion(long appVersionId) {
		AppVersion appversion = this.appVersionDao.findOne(appVersionId);
		return this.findOne(appversion.getAppId());
	}

	public App findAppById(Long id) {
		App app = this.appDao.findByIdAndDel(id, DELTYPE.NORMAL);
		return app;
	}
	
	public App findByAppcanAppId(String appId){
		App app = this.appDao.findByAppcanAppIdAndDel(appId, DELTYPE.NORMAL);
		return app;
	}
	
	
	public AppVersion getAppVersionByPackage(long appVersionId) {
		return this.appVersionDao.findOne(appVersionId);
	}

	public AppVersion getAppVersion(long appVersionId) {
		return this.appVersionDao.findOne(appVersionId);
	}

	public AppPackage getSingleAppPackage(long appPackageId) {
		return this.appPackageDao.findOne(appPackageId);
	}
	
	//---------------------------------补丁包相关--------------------------------------
	
	/**
	 * 准备生成补丁包
	 * 根据版本id获取可以进行补丁包的以前的版本信息及标签名
	 * @user jingjian.wu
	 * @date 2015年10月30日 下午12:14:54
	 */
	public Map<String,Object> getPreparePatch(long appVersionId,long loginUserId){ 
		Map<String,Object> map = new HashMap<String, Object>();
		AppVersion appVersion = appVersionDao.findOne(appVersionId);
		String currentTag = appVersion.getTagName();
		map.put("currentTag", currentTag);
		List<AppVersion> listVersion = new ArrayList<AppVersion>();
		if(appVersion.getType().equals(AppVersionType.PROJECT)){
			listVersion = appVersionDao.findByAppIdAndDelAndBranchNameAndTypeAndCreatedAtGreaterThan(appVersion.getAppId(), DELTYPE.NORMAL,appVersion.getBranchName(),AppVersionType.PROJECT,appVersion.getCreatedAt());
		}else if(appVersion.getType().equals(AppVersionType.PERSONAL)){
			listVersion = appVersionDao.findByAppIdAndDelAndBranchNameAndTypeAndUserIdAndCreatedAtGreaterThan(appVersion.getAppId(), DELTYPE.NORMAL,appVersion.getBranchName(),AppVersionType.PERSONAL,appVersion.getUserId(),appVersion.getCreatedAt());
		}

		map.put("seniorTag", listVersion);
		return map;
	}
	
	/**
	 * 生成补丁包
	 * @param currentAppVersionId 当前选中的版本的id
	 * @param oldTag 旧标签
	 * @param patchName 补丁包名称(不包含后缀.zip)
	 * @param patchType 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user jingjian.wu
	 * @date 2015年10月30日 下午12:40:45
	 */
	public synchronized AppPatch createPatch(AppPatch patch) throws ClientProtocolException, IOException {
		String patchName = "patch_"+System.currentTimeMillis();
		if(patch.getType().compareTo(PATCH_TYPE.AppCanWgt) == 0){
			patchName = "widget_patch_"+System.currentTimeMillis();
		}
		
		AppVersion seniorAppVersion = appVersionDao.findOne(patch.getSeniorAppVersionId());
		App app = appDao.findOne(seniorAppVersion.getAppId());
		String gitRelativePath =app.getRelativeRepoPath();
		String seniorTag = seniorAppVersion.getTagName();
		
		if(isVersionExist(patch.getVersionNo(), seniorAppVersion.getAppId(), seniorAppVersion.getType(), patch.getUserId())) {
			throw new RuntimeException("版本号已存在");
		}
		
		AppVersion baseAppVersion = appVersionDao.findOne(patch.getBaseAppVersionId());
		String baseTag = baseAppVersion.getTagName();

		/*String cmd = "";
		if(patch.getType().equals(PATCH_TYPE.AppCanWgt)){
			cmd = "sh "+shellPath+"coopdev_git/patchWidget.sh " + gitRelativePath + " " + baseTag + "  "+seniorTag + "  "+codeZipPath+"/"+patchName + "   " + app.getAppcanAppId();
			log.info(cmd);

		}else if(patch.getType().equals(PATCH_TYPE.AppCanNative)){
			cmd = "sh "+shellPath+"coopdev_git/patch.sh " + gitRelativePath + " " + baseTag + "  "+seniorTag + "  "+codeZipPath+"/"+patchName;
			log.info(cmd);
		}
		String result = this.execShell(cmd);
		log.info("patch.sh result==>  "+result);
		
		patch.setFileName(patchName + ".zip");
		
		
		if(result.toLowerCase().contains("Nothing".toLowerCase())){
			File file = new File(codeZipPath+"/"+patchName+".zip");
			file.delete();
			throw new RuntimeException("两个版本无差异");
		}
		log.info("patch file -->"+codeZipPath+"/"+patchName+ ".zip");
		File fileCountSize = new File(codeZipPath+"/"+patchName+ ".zip");
		patch.setFileSize(fileCountSize.length());
		
		*/
		Map<String,String> params = new HashMap<String,String>();
		params.put("baseTag", baseTag);
		params.put("seniorTag", seniorTag);
		params.put("appcanAppId", app.getAppcanAppId());
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		params.put("patchName", patchName);
		params.put("patchType", patch.getType().toString());
		
		
		log.info(params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer+"/git/patch/create", params);
		log.info(String.format("GitAction -> createPatch --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		
		if(!"success".equals(obj.getString("status"))){
			throw new RuntimeException(obj.getString("message"));
		}
		JSONObject objMsg = obj.getJSONObject("message");
		patch.setFileName(patchName+ ".zip");
		patch.setFileSize(objMsg.getLong("fileSize"));
		return appPatchDao.save(patch);
		
		
	}
	
	/**
	 * 删除补丁包
	 * @param patchId
	 */
	public void removePatch(long patchId) {
		appPatchDao.delete(patchId);
	}
	
	//---------------------------------补丁包相关--------------------------------------
	
	/**
	 * 发布补丁包时候,根据应用id获取此应用以前都打包使用过的渠道
	 * @user jingjian.wu
	 * @date 2015年11月10日 上午10:30:27
	 */
	public List<String> getChannelListByAppId(long appId){
		Set<String> set = new HashSet<String>();
		List<AppPackage> listPkg = appPackageDao.findChannelCodeByAppId(appId);
		if(null!=listPkg && listPkg.size()>0){
			for(AppPackage pk:listPkg){
				set.add(pk.getChannelCode());
			}
		}
		return new ArrayList<String>(set);
		
	}
	
	/**
	 * 
	 * @describe 校验用户输入的appid格式是否正确	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年11月20日 上午9:57:13	<br>
	 * @param appcanAppId
	 * @param appcanAppKey
	 * @return  <br>
	 * @returnType boolean
	 *
	 */
	public boolean validateAppId(String appcanAppId){
//		return true;
		if(null!=appcanAppId && !"".equals(appcanAppId)){
			if(appIdPattern.matcher(appcanAppId).matches()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 校验首字母为英文
	 * @user jingjian.wu
	 * @date 2016年1月14日 上午11:06:27
	 */
	public boolean validateFirstElementForAppId(String appcanAppId){
//		return true;
		if(null!=appcanAppId && !"".equals(appcanAppId)){
			if(Pattern.compile("^[a-zA-Z]\\w*").matcher(appcanAppId).matches()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @describe 校验用户输入的appkey格式是否正确	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年11月20日 上午9:57:13	<br>
	 * @param appcanAppId
	 * @param appcanAppKey
	 * @return  <br>
	 * @returnType boolean
	 *
	 */
	public boolean validateAppKey(String appcanAppKey){
//		return true;
		if(null!=appcanAppKey && !"".equals(appcanAppKey)){
			if(appKeyPattern.matcher(appcanAppKey).matches()){
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 校验本地证书
	 * @user jingjian.wu
	 * @date 2016年3月9日 下午6:31:19
	 */
	public String validateLocalCert(String p12, String password) {
			
		log.info(String.format("AppService -> validateLocalCert -> p12[%s] password[%s] ", p12, password));
		
		String p12Path = certFileRoot + p12.replaceAll(certBaseUrl, "");
		
		String commond = String.format("sh "+shellPath+"coopdev_cert/getcertinfo.sh %s %s PKCS12", p12Path, password);
		
		String cmdRet = this.execShell(commond);
		
		log.info("AppService -> validateIOSCert -> commond[" + commond + "] cmdRet[" + cmdRet + "]");
		
		if(cmdRet.indexOf("所有者") == -1 && cmdRet.indexOf("Owner") == -1) {
			return null;
		}
		return "ok";
	}
	
	/**
	 * @user jingjian.wu
	 * 查询任务的时候   获取我能看到的应用的的名字,需要根据应用名字模糊匹配
	 * @date 2016年3月7日 下午3:14:40
	 */
	    
	public Map<String, Object> getAppList(long loginUserId, String name,Long projectId) {
		List<Long> projectIds = new ArrayList<Long>();
		Match4Project matchObj = new Match4Project();
		Map<String,Object> map = projectService.getProjectList(1, 10000, matchObj, loginUserId,null, null, null, null, null, null, null, null, null);
		List<Map<String, Object>> projectMapList = (List<Map<String, Object>>) map.get("list");
		for(Map<String,Object> ma:projectMapList){
			Project pp = (Project) ma.get("object");
			projectIds.add(pp.getId());
		}
		if(null!=projectId && projectIds.contains(projectId)){
			projectIds.clear();
			projectIds.add(projectId);
		}else if(null!=projectId && !projectIds.contains(projectId)){
			log.info("user with ID:"+loginUserId+",has no permission for projectId :"+projectId);
			return this.getSuccessMap(new ArrayList<String>());
		}
		
		List<App> listAppName = appDao.findByProjectIdInAndDelAndNameLike(projectIds, DELTYPE.NORMAL,"%"+name+"%");
		if(null!=listAppName && listAppName.size()>15){
			listAppName = listAppName.subList(0, 10);
		}
		return this.getSuccessMap(listAppName);
	}

	public Map<String, Object> addPinYin() {
		List<App> apps = this.appDao.findByDel(DELTYPE.NORMAL);
		for(App app : apps){
			app.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(app.getName()==null?"":app.getName()));
			app.setPinYinName(ChineseToEnglish.getPingYin(app.getName()==null?"":app.getName()));
		}
		this.appDao.save(apps);
		return this.getSuccessMap("affected "+apps.size());
	}
	
	/**
	 * android打包的时候需要版本号,原先都是默认的5,现在根据用户输入的版本号来订
	 * a.b.c
	 * a*1000000 + b*10000 + c
	 * @param version
	 * @return
	 */
	public int getAndroidPkgCountFromAppVersion(String version){
		log.info("android pkg version====>"+version);
		if(StringUtils.isBlank(version)){
			return 0;
		}else{
			String placeArr[] = version.split("\\.");
			if(null!=placeArr && placeArr.length==3){
				return Integer.parseInt(placeArr[0])*1000000+Integer.parseInt(placeArr[1])*10000+Integer.parseInt(placeArr[2]);
			}
			return 0;
		}
	}
	//去掉字符串的换行和空格
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
	
	/**
	 * 生成应用appcanappId
	 * @return
	 */
	public String obtainAppCanAppId(String key){
        return this.appIdPrefix + sequenceIdTemplate.opsForValue().increment(this.nodeId + ":" + key, 1);
	}
	/**
	 * 生成应用Key
	 * @return
	 */
	public static String obtainAppCanAppKey(){
		return UUID.randomUUID().toString();
	}

	public App findByAppcanAppIdAndDel(String appcanAppId, DELTYPE delType) {
		return this.appDao.findByAppcanAppIdAndDel(appcanAppId, delType);
	}
	
	public App findByAppcanAppKeyAndDel(String appcanAppKey, DELTYPE delType) {
		return this.appDao.findByAppcanAppKeyAndDel(appcanAppKey, delType);
	}
	
	public List<App> findAppByProjectId(long projectId){
		return appDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
	}
	
	public List<org.zywx.cooldev.entity.app.AppType> findAppTypeAll(){
		return appTypeDao.findByDel(DELTYPE.NORMAL);
	}
	
	public org.zywx.cooldev.entity.app.AppType findAppTypeName(String typeName){
		List<org.zywx.cooldev.entity.app.AppType> typeList = appTypeDao.findByTypeName(typeName);
		if(typeList != null && !typeList.isEmpty())
			return appTypeDao.findByTypeName(typeName).get(0);
		else
			return null;
	}
	/**
	 * 查找应用版本信息
	 * @param appId
	 * @param versionNo
	 * @param delType
	 * @return
	 */
	public AppVersion findAppVersionByAppIdAndVersionNoAndDel(long appId, String versionNo){
		List<AppVersion> appVerList = appVersionDao.findByAppIdAndVersionNoAndDel(appId, versionNo, DELTYPE.NORMAL);
		if(appVerList != null){
			return appVerList.get(0);
		}else{
			return null;
		}
	}
	
	
	
	/**
	 * 项目应用之 添加新应用<br>
	 * 
	 * @param app
	 * @param loginUserId
	 * @return
	 * @throws Exception 
	 */
	public App addProAppByApp(App app, long loginUserId) throws Exception {
		
		// 添加新应用
		app.setDel(DELTYPE.DELETED);
		app.setUserId(loginUserId);
		app.setPublished(IfStatus.NO);//默认未发布
		app.setPublishedAppCan(IfStatus.NO);
		app.setPublishedTest(IfStatus.NO);
		//增加拼音
		app.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(app.getName()==null?"":app.getName()));
		app.setPinYinName(ChineseToEnglish.getPingYin(app.getName()==null?"":app.getName()));

	    //外网ip改了，ip is not valid
		String appId = obtainAppCanAppId(appIdPrefix);
		String appKey = obtainAppCanAppKey();
		app.setAppcanAppId(appId);
		app.setAppcanAppKey(appKey);
		app = appDao.save(app);
		
		
		return app;
	}
	/**
	 * 项目应用之  
	 * 创建远程GIT版本库<br>
	 * 授权给相关角色<br>
	 * @param app
	 * @throws Exception
	 */
	public void updateProAppByApp(long appId, Project newPro) throws Exception{
		long startCreateAppGitRepoTime = System.currentTimeMillis();
		App app = appDao.findOne(appId);
		app.setProjectId(newPro.getId());
		app.setProjectParentId(newPro.getParentId());
		//修改项目编号
		Long loginUserId = app.getUserId();
		long startCreateAppTime = System.currentTimeMillis();
		String status = "";
		String info = "";
		String path = "";
		// 创建远程GIT版本库
		log.info(String
				.format("addApp --> userId[%d] appId[%d] appAppcanAppId[%s] appkey[%s] initRepo[%s] CreateAppGirRepoTime[%d]",
						loginUserId, app.getId(), app.getAppcanAppId(),
						app.getAppcanAppKey(), initRepoUrl,
						startCreateAppGitRepoTime - startCreateAppTime));
		User loginUser = userDao.findOne(loginUserId);

		if (gitFlag.equals("new")) {
			// gitBucket版本
			Map<String, String> initRet = this.initRepoNew(loginUser, app);
			status = initRet.get("status");
			info = initRet.get("info");
			path = initRet.get("path");
		} else {
			// apache版本
			Map<String, String> initRet = this.initRepo(loginUser, app);
			status = initRet.get("status");
			info = initRet.get("info");
			path = initRet.get("path");
		}

		long endCreateAppGirRepoTime = System.currentTimeMillis();

		// 版本库地址更新至应用
		log.info(String
				.format("addApp -> ret status[%s] info[%s] path[%s] endCreateAppGirRepoTime[%d]",
						status, info, path, endCreateAppGirRepoTime
								- startCreateAppGitRepoTime));
		app.setRelativeRepoPath(path);
		app.setRemoteRepoUrl(remoteGitRoot + path);
		appDao.save(app);

		// 版本库授权
		Map<String, Object> shareRet = this.shareRepo(loginUser, app);
		log.info("AppService -> addApp -> shareRet:" + shareRet.toString());

		Map<String, String> params = new HashMap<String, String>();
		params.put("appType", app.getAppType() == 0 ? "MOBILE" : "OTHER");
		params.put("appName", app.getName());
		params.put("appcanAppId", app.getAppcanAppId());
		params.put("relativeRepoPath", app.getRelativeRepoPath());
		if (app.getAppType() == 1) {
			if (null != app.getAppSource()
					&& -1 != app.getAppSource().longValue()) {
				App sourceApp = appDao.findOne(app.getAppSource());
				if (null != sourceApp) {
					params.put("sourceRelativeRepoPath",
							sourceApp.getRelativeRepoPath());
				} else {
					throw new RuntimeException("关联应用不合法");
				}
			}

		}
		params.put("sourceGitRepo", app.getSourceGitRepo());
		log.info(params.toString());
		String jsonStr = HttpUtil.httpPost(gitShellServer + "/git/clone",
				params);
		log.info(String.format("GitAction -> addApp --> shell for jsonStr[%s]",
				jsonStr));
		JSONObject obj = JSONObject.fromObject(jsonStr);
		if (!"success".equals(obj.getString("status"))) {
			throw new RuntimeException("创建应用失败");
		}
		if (gitFlag.equals("new")) {
			// 创建webHook
			StringBuffer parameters = new StringBuffer();
			JSONObject WebHookobj = new JSONObject();
			HashSet<String> events = new HashSet<String>();
			events.add("push");
			WebHookobj.put("url", xietongHost + "/cooldev/app/repo/pushed");
			WebHookobj.put("ctype", "form");
			WebHookobj.put("events", events);
			parameters.append(WebHookobj.toString());
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Authorization", "token " + gitToken);
			String result;
			try {
				result = NewGitHttpUtil.httpPostWithJSON(
						newGitServer + "/api/v3/repos"
								+ app.getRelativeRepoPath().replace(".git", "")
								+ "/hooks", parameters.toString(), headers);
				log.info("----------->create webHook result：" + result);
				JSONObject resultObj = JSONObject.fromObject(result);
				if (!resultObj.get("status").equals("OK")) {
					throw new RuntimeException("创建webHook失败");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void delProAppByApp(String[]  split) {
		List<App> list = new ArrayList<App>();
		for (String appId : split) {
			App app = appDao.findOne(Long.parseLong(appId));
			list.add(app);
		}
		appDao.delete(list);
	}

	public void editProAppByApp(App app, long appId) {
		App old = appDao.findOne(appId);
		old.setId(appId);
		old.setUpdatedAt(new Timestamp(new Date().getTime()));
		old.setName(app.getName());
		old.setDetail(app.getDetail());
		old.setAppType(app.getAppType());
		appDao.save(old);
	}
	
	public AppType findAppTypeById(Long id){
		return appTypeDao.findOne(id);
	}

	public void updateApp(App app) {
		appDao.save(app);
	}

	public AppVersion findAppVersionByAppId(long transactionsId) {
		return appVersionDao.findOne(transactionsId);
	}
}


