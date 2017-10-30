package org.zywx.cooldev.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zywx.appdo.facade.mam.entity.app.AppBaseInfo;
import org.zywx.appdo.facade.mam.entity.pkg.PkgFileInfo;
import org.zywx.appdo.facade.mam.enums.AppCategory;
import org.zywx.appdo.facade.mam.service.app.AppBaseInfoFacade;
import org.zywx.appdo.facade.mam.service.pkg.PkgFileInfoFacade;
import org.zywx.appdo.facade.omm.entity.tenant.Enterprise;
import org.zywx.appdo.facade.omm.service.tenant.TenantFacade;
import org.zywx.cooldev.commons.Enums.AppPackageBuildType;
import org.zywx.cooldev.commons.Enums.AppVersionPatchOrFull;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.EMMAppSource;
import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TerminalType;
import org.zywx.cooldev.dao.app.AppDao;
import org.zywx.cooldev.dao.app.AppPackageDao;
import org.zywx.cooldev.dao.app.AppPatchDao;
import org.zywx.cooldev.dao.app.AppVersionDao;
import org.zywx.cooldev.dao.app.AppWidgetDao;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppChannel;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.app.AppPatch;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.app.AppWidget;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.emm.TokenUtilProduct;
import org.zywx.cooldev.util.emm.TokenUtilTest;

import net.sf.json.JSONObject;

/**
 * @
 * @author yang.li
 *
 */
@Service
public class EmmService extends BaseService {

	@Autowired
	private AppDao appDao;
	@Autowired
	private AppVersionDao appVersionDao;
	@Autowired
	private AppPackageDao appPackageDao;
	@Autowired
	private AppWidgetDao appWidgetDao;
	@Autowired
	private AppPatchDao appPatchDao;
	@Autowired
	private TeamService teamService;
	
	@Autowired(required=false)
	@Qualifier(value="appBaseInfoFacade")
	private AppBaseInfoFacade appBaseInfoFacade;
	
	@Autowired(required=false)
	@Qualifier(value="appBaseInfoFacadeTest")
	private AppBaseInfoFacade appBaseInfoFacadeTest;
	@Autowired(required=false)
	@Qualifier(value="pkgFileInfoFacade")
	private PkgFileInfoFacade pkgFileInfoFacade;
	
	@Autowired(required=false)
	@Qualifier(value="pkgFileInfoFacadeTest")
	private PkgFileInfoFacade pkgFileInfoFacadeTest;
	
	@Autowired(required=false)
	private TenantFacade tenantFacade;
	
	@Value("${git.remoteGitRoot}")
	private String remoteGitRoot;

	@Value("${emm.appPublishUrl}")
	private String appPublishUrl;
	
	@Value("${emm.appPublishToBaasUrl}")
	private String appPublishToBaasUrl;
	
	@Value("${emm.packPublishUrl}")
	private String packPublishUrl;
	
	@Value("${appVersion.codeZipUrl}")
	private String codeZipUrl;
	
	@Autowired
	private TeamMemberService teamMemberService;
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ProjectMemberService projectMemberService;
	
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	@Value("${emm3Url}")
	private String emm3MamUrl;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	public Map<String,Object> updateOrIsAppExist(String appcanAppId, String appcanAppKey, boolean productEnv,String serviceFlag,String emm3MamUrl,String emm3TestUrl) {
		if(serviceFlag.equals("enterpriseEmm3")){
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("appId", appcanAppId);
			String resultStr="";
			if(productEnv){
				resultStr=HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/validApp", parameters,"UTF-8");
			}else{
				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mam/xieTongInter/validApp", parameters,"UTF-8");
			}
			JSONObject jsonObject=JSONObject.fromObject(resultStr);
			if(jsonObject.get("status").equals("ok")){
				return this.getFailedMap("no");
			}else{
				return this.getSuccessMap("yes");
			}
		}else{
			List<App> appList = this.appDao.findByAppcanAppIdAndAppcanAppKeyAndPublishedAndDel(appcanAppId, appcanAppKey, IfStatus.YES, DELTYPE.NORMAL);
			if(appList != null && appList.size() > 0) {
				App app = appList.get(0);
				
				Project project = projectDao.findOne(app.getProjectId());
				if(project == null) {
					log.info(String.format("appcanAppId:[%s], appcanAppKey:[%s],该应用所在项目不存在",appcanAppId, appcanAppKey));
					return this.getFailedMap("项目不存在");
				}
				
//				TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
				User user = userDao.findOne(findCreateUserId(project));
				
				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(project.getBizCompanyId());
				String[] params = new String[]{enterprise.getId().toString(),"dev"};
				String token = "";
				if(productEnv){
					token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);
					log.info("判断应用是否存在:token:"+token+",appId:"+app.getAppcanAppId()+",account-->"+user.getAccount());
					//判断应用是否已经发布
					AppBaseInfo appbase = appBaseInfoFacade.getByAppIdAndCreator(token, app.getAppcanAppId(),user.getAccount());
					if(null==appbase){
						app.setPublished(IfStatus.NO);
						appDao.save(app);
						return this.getFailedMap("no");//应用不存在
					}
				}else{
					token = TokenUtilTest.getToken(enterprise.getEntkey(), params);
					//判断应用是否已经发布
					log.info("判断应用是否存在:token::"+token+",appId:"+app.getAppcanAppId()+",account-->"+user.getAccount());
					AppBaseInfo appbase = appBaseInfoFacadeTest.getByAppIdAndCreator(token, app.getAppcanAppId(),user.getAccount());
					if(null==appbase){
						app.setPublishedTest(IfStatus.NO);
						appDao.save(app);
						return this.getFailedMap("no");
					}
				}
				return this.getSuccessMap("yes");
			} else {
				return this.getFailedMap("no");
			}
		}
	}
	
	
	/**
	 * 发布应用<br>
	 * 分类处理：<br>
	 * 1. 未绑定团队：发布至EMM3.0, 发布到线上唯一环境
	 * 2. 已绑定团队：发布至EMM4.0, 测试包发测试环境，正式包发正式环境
	 * 
	 * @param appcanAppId
	 * @param appcanAppKey
	 * @param emmAppTypeId
	 * @param emmAppGroupId
	 * @param emmAppSource
	 * @param imageUrls
	 * @param app_category 
	 * @return
	 */
	public Map<String,Object> publishApp(
			String serviceFlag,
			String appcanAppId,
			String appcanAppKey,
			int emmAppTypeId,
			int emmAppGroupId,
			EMMAppSource emmAppSource,
			List<String> imageUrls,
			long loginUserId,
			boolean productEnv, 
			AppCategory app_category,
			String emm3MamUrl,
			String emm3TestUrl) { 
		
		log.info(String.format("EmmService -> publishApp -> appcanAppId[%s] appcanAppKey[%s] emmAppTypeId[%d] emmAppGroupId[%d] emmAppSource[%s] imageUrls[%s] appCategory[%s]",
				appcanAppId, appcanAppKey, emmAppTypeId, emmAppGroupId, emmAppSource.name(), imageUrls!=null?imageUrls.toString():null,app_category));

		App appCoop = appDao.findByAppcanAppIdAndDel(appcanAppId, DELTYPE.NORMAL);
		if(null==appCoop){
			log.info("app不存在,appcanAppId:"+appcanAppId);
			return this.getFailedMap("应用不存在");
		}
		Project project = projectDao.findOne(appCoop.getProjectId());
		if(project == null) {
			log.info("应用所对应的项目不存在");
			return this.getFailedMap("应用所在项目不存在");
		}
		if(serviceFlag.equals("enterpriseEmm3")){
			TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
			User user = userDao.findOne(teammember.getUserId());
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("iconLoc1", appCoop.getIcon());//icon路径
			parameters.put("appName",appCoop.getName());
			parameters.put("appId",appCoop.getAppcanAppId());
			parameters.put("appKey",appCoop.getAppcanAppKey());
			parameters.put("appGroupId",Integer.toString(emmAppGroupId));
			parameters.put("appTypeId", Integer.toString(emmAppTypeId));
			parameters.put("appSource", emmAppSource.name().toLowerCase());//来源
			parameters.put("pkgFileLoc", "");//包路径
			parameters.put("description", appCoop.getDetail());
			parameters.put("tag", "");//应用标记
			parameters.put("appCategory", app_category.name());//应用类型 区分wgt 跟appcanNative(AppCanNative/AppCanWgt)
			parameters.put("adminLoginName", user.getAccount());
			log.info("parameters:----->iconLoc1:"+appCoop.getIcon()+",appName:"+appCoop.getName()+",appId:"+appCoop.getAppcanAppId()+",appKey:"+appCoop.getAppcanAppId()+",appGroupId:"+Integer.toString(emmAppGroupId)
			        +",appTypeId:"+Integer.toString(emmAppTypeId)+",appSource:"+emmAppSource.name().toLowerCase()+",pkgFileLoc:,description:"+ appCoop.getDetail()+",tag:,appCategory"+app_category.name()+",adminLoginName:"+user.getAccount());
			if(imageUrls != null && imageUrls.size() > 0) {
				for(int i = 0; i < imageUrls.size(); i++) {
					if(i == 0) {
						parameters.put("icon1img",imageUrls.get(i));
					} else if(i == 1) {
						parameters.put("icon2img",imageUrls.get(i));
					} else if(i == 2) {
						parameters.put("icon3img",imageUrls.get(i));
					}
				}
			}
			String resultStr="";
			if(productEnv){
				resultStr=HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/addAppCanNativeApp", parameters,"UTF-8");
				appCoop.setAppCategory(app_category.name());
				appCoop.setPublished(IfStatus.YES);
				appDao.save(appCoop);
			}else{
				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mam/xieTongInter/addAppCanNativeApp", parameters,"UTF-8");
				appCoop.setAppCategory(app_category.name());
				appCoop.setPublishedTest(IfStatus.YES);
				appDao.save(appCoop);
			}
			JSONObject jsonObject=JSONObject.fromObject(resultStr);
			log.info("status:"+jsonObject.getString("status")+",info:"+jsonObject.getString("info"));
			if(jsonObject.getString("status").equals("ok")){
				return this.getSuccessMap("ok");
			}else{
				return this.getFailedMap(jsonObject.getString("info"));
			}
		}else{
			if( project.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED) 
					||project.getBizLicense().equals(PROJECT_BIZ_LICENSE.BINDING) ) {
				User loginUser = userDao.findOne(loginUserId);
				if(loginUser == null) {
					log.info("无法获取当前登录人");
					return this.getFailedMap("无法获取当前登录人");
				}
				// 判定App是否绑定团队 -> 未绑定发布至EMM3.0
				//判断是否已经发送过3.0
				List<App> appList = appDao.findByAppcanAppIdAndAppcanAppKeyAndPublishedAppCanAndDel(appcanAppId, appcanAppKey, IfStatus.NO, DELTYPE.NORMAL);
				if(appList == null || appList.size() == 0) {
					log.info("此应用已经发布到3.0");
					if(appCoop.getPublishedAppCan().equals(IfStatus.NO)){
						appCoop.setPublishedAppCan(IfStatus.YES);
						appDao.save(appCoop);
					}
					return this.getSuccessMap("此应用已经发布到3.0");
				}
				String ret = this.publishAppToEmm3(appCoop, project, loginUser);
				log.info(String.format("EmmService -> publishApp -> to EMM3.0 ret:[%s]", ret));
				JSONObject json = JSONObject.fromObject(ret);
				if("error".equals(json.getString("info"))){
					return this.getFailedMap(json.getString("msg"));
				}
				
				appCoop.setPublishedAppCan(IfStatus.YES);
				appDao.save(appCoop);
				return this.getSuccessMap("ok");
			}
			
			// 已绑定团队，发布至EMM4.0系统
			
			
			AppBaseInfo appInfo = new AppBaseInfo();
			appInfo.setName(appCoop.getName());
			appInfo.setAppId(appcanAppId);
			appInfo.setAppKey(appcanAppKey);
			appInfo.setAppType(emmAppTypeId + "");
			appInfo.setAppGroupId(emmAppGroupId);
//			appInfo.setIconLoc(iconLoc);//icon
			appInfo.setAppSource(emmAppSource.name().toLowerCase());
			appInfo.setPkgUrl("");
			appInfo.setDescription(appCoop.getDetail());
			//如果团队绑定传送团队uuid和团队名称，如果团队没绑定项目绑定了传送项目uuid和项目名称
			if(this.projectService.isTeamBind(project.getTeamId())){
				Team team=this.teamService.findOne(project.getTeamId());
				appInfo.setXtProjectId(team.getUuid());
				appInfo.setXtProjectName(team.getName());
			}else{
				appInfo.setXtProjectId(project.getUuid());
				appInfo.setXtProjectName(project.getName());
			}
			if(imageUrls != null && imageUrls.size() > 0) {
				for(int i = 0; i < imageUrls.size(); i++) {
					if(i == 0) {
						appInfo.setShortImg1(imageUrls.get(i));
					} else if(i == 1) {
						appInfo.setShortImg2(imageUrls.get(i));
					} else if(i == 2) {
						appInfo.setShortImg3(imageUrls.get(i));
					}
				}
			}
			
			User user = userDao.findOne(findCreateUserId(project));
			
			try {
				
				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(project.getBizCompanyId());
				String[] params = new String[]{enterprise.getId().toString(),"dev"};
				
				if(productEnv){
					String token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);
					//发往正式环境
					AppBaseInfo appbase = appBaseInfoFacade.getByAppIdAndCreator(token, appcanAppId,user.getAccount());
					if(null!=appbase){
						appCoop.setPublished(IfStatus.YES);
						appDao.save(appCoop);
						return this.getSuccessMap("此应用已经发布到EMM正式环境");
					}
					appCoop.setAppCategory(app_category.name());
					
					log.info("正式环境发包,租户ID"+enterprise.getId()+",企业key:"+enterprise.getEntkey()+",token-->"+token+",用户->"+user.getAccount());
					Map<String, Object> mapResult = appBaseInfoFacade.createWebApp(token, appInfo, app_category.name(), user.getAccount());
					log.info("EmmService -> publishApp ret :" + mapResult.toString());
					if("error".equals(mapResult.get("status").toString())){
						if(mapResult.get("info").toString().equals("应用id已存在")){
							throw new RuntimeException("此应用不能在重新发布,请联系管理员");
						}
						throw new RuntimeException(mapResult.get("info").toString());
//						return this.getFailedMap(mapResult.get("info").toString());
					}
					
					 appCoop.setPublished(IfStatus.YES);
					 appDao.save(appCoop);
					 return this.getSuccessMap("");
				}else{
					//发往测试环境
					String token = TokenUtilTest.getToken(enterprise.getEntkey(), params);
					AppBaseInfo appbase = appBaseInfoFacadeTest.getByAppIdAndCreator(token, appcanAppId,user.getAccount());
					if(null!=appbase){
						appCoop.setPublishedTest(IfStatus.YES);
						appDao.save(appCoop);
						return this.getSuccessMap("此应用已经发布到EMM测试环境");
					}
					appCoop.setAppCategory(app_category.name());
					
					
					log.info("测试环境发包,租户ID"+enterprise.getId()+"，企业key:"+enterprise.getEntkey()+",token-->"+token+",用户->"+user.getAccount());
					Map<String, Object> mapResult = appBaseInfoFacadeTest.createWebApp(token, appInfo, app_category.name(), user.getAccount());
					log.info("EmmService -> publishApp ret :" + mapResult.toString());
					if("error".equals(mapResult.get("status").toString())){
						throw new RuntimeException(mapResult.get("info").toString());
//						return this.getFailedMap(mapResult.get("info").toString());
					}
					
					
					appCoop.setPublishedTest(IfStatus.YES);
					appDao.save(appCoop);
					return this.getSuccessMap("ok");
				}
				
			} catch(Exception e) {
				e.printStackTrace();
				log.info("EmmService -> publishApp :" + e.getMessage());
				log.info(ExceptionUtils.getStackTrace(e));
				return this.getFailedMap(e.getMessage());
			}
		}
	}


	public Map<String,Object> publishPackage(
			String serviceFlag,
			long appPackageId,
			String detail,
			IfStatus forceUpgrade,
			IfStatus confirmUpgrade,
			String packageName,
			String bundleIdentifier,
			String upgradeTip,
			String shutdownTip,
			String emm3MamUrl,
			String emm3TestUrl
			) {
		
		log.info(String.format("EmmService -> publishPackage -> appPackageId[%d] detail[%s] forceUpgrade[%s] "
				+ "confirmUpgrade[%s] packageName[%s] bundleIdentifier[%s] upgradeTip[%s] shutdownTip[%s]", 
				appPackageId, detail, forceUpgrade, confirmUpgrade, packageName, bundleIdentifier, upgradeTip, shutdownTip));
		
		AppPackage pack = appPackageDao.findOne(appPackageId);
		if(pack == null) {
			log.info("对应包不存在");
			return this.getFailedMap("对应包不存在");
		}
		
		AppVersion appVersion = appVersionDao.findOne(pack.getAppVersionId());
		if(appVersion == null) {
			log.info("对应包版本不存在");
			return this.getFailedMap("对应版本不存在");
		}
		
		App appCoop = appDao.findOne(appVersion.getAppId());
		//if(appCoop == null || appCoop.getPublished().equals(IfStatus.YES)) {
		if(appCoop == null) {
			log.info("应用不存在");
			return this.getFailedMap("应用不存在");
		}
		if(null!=appCoop.getAppCategory() && !AppCategory.AppCanNative.toString().equals(appCoop.getAppCategory())){
			log.info(String.format("app appcategory[%s] package type[%s]",appCoop.getAppCategory(),AppCategory.AppCanNative));
			return this.getFailedMap(String.format("应用分类和包分类不匹配，应用：[%s],包[%s]", appCoop.getAppCategory(),AppCategory.AppCanNative));
		}
		Project project = projectDao.findOne(appCoop.getProjectId());
		if(project == null) {
			log.info("项目不存在");
			return this.getFailedMap("项目不存在");
		}
		if(serviceFlag.equals("enterpriseEmm3")){
			TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
			User user = userDao.findOne(teammember.getUserId());
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("adminLoginName",user.getAccount());//当前登陆员管理帐号
			parameters.put("celue", "");//策略id
			parameters.put("appId3",appCoop.getAppcanAppId());//应用appid
			parameters.put("appCategoryId2","AppCanNative");//应用类型 区分wgt 跟appcanNative(AppCanNative/AppCanWgt)
			parameters.put("version2", pack.getVersionNo());//版本
			parameters.put("bigversion",pack.getVersionNo()!=""&&pack.getVersionNo()!=null? pack.getVersionNo().substring(0,5):"");//大版本
			parameters.put("channelCode2", pack.getChannelCode());//渠道号
			parameters.put("ipad2", pack.getTerminalType().equals(TerminalType.IPAD)||pack.getTerminalType().equals(TerminalType.IPHONE_IPAD)?"true":"");
			parameters.put("iphone2", pack.getTerminalType().equals(TerminalType.IPHONE)||pack.getTerminalType().equals(TerminalType.IPHONE_IPAD)?"true":"");
			parameters.put("Android2", pack.getTerminalType().equals(TerminalType.ANDROID)?"true":"");
			parameters.put("plat2", "");//是否PC端，true是，空为否
			parameters.put("isquanliang_buding2", "false");//是否是全量包，false全量、true补丁
			parameters.put("forceUpdate", forceUpgrade.equals(IfStatus.YES)?"true":"false");//false否/true是+
			parameters.put("updateEnd2", confirmUpgrade.equals(IfStatus.YES)?"true":"false");//升级确认(true/false)
			parameters.put("isPkgUrl2", "pkg");//string	传入（pkg或者空）为判断是否安装包
//			parameters.put("downloadUrl2", pack.getDownloadUrl());//下载地址（http://开头）
//			parameters.put("pkgFileUrl2", pack.getDownloadUrl());//安装包地址
			if(pack.getOsType().equals(OSType.ANDROID)){
				parameters.put("downloadUrl2", pack.getDownloadUrl());//下载地址（http://开头）
				parameters.put("pkgFileUrl2", pack.getDownloadUrl());//安装包地址
			}else{
				parameters.put("downloadUrl2", pack.getDownloadUrl());//下载地址（http://开头）
				parameters.put("pkgFileUrl2", pack.getDownloadUrl());//安装包地址
			}
			parameters.put("pkgSize2", String.valueOf(pack.getFileSize()));//包大小
			parameters.put("pkgName2", packageName);//包名
			parameters.put("bundle2", bundleIdentifier);//ibound
			parameters.put("widgetUpdateHints2", upgradeTip);//升级提示语
			parameters.put("widgetCloseHints2", shutdownTip);//关闭提示语
			parameters.put("description2", pack.getVersionDescription());//描述
			String resultStr="";
			if(pack.getBuildType().equals(AppPackageBuildType.PRODUCTION)){
				resultStr=HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/saveAppCanNativePkg", parameters,"UTF-8");
				pack.setPublised(IfStatus.YES);
				appPackageDao.save(pack);
			}else{
				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mam/xieTongInter/saveAppCanNativePkg", parameters,"UTF-8");
				pack.setPublisedTest(IfStatus.YES);
				appPackageDao.save(pack);
			}
			log.info("EMM3 publish package params:"+parameters.toString()+"  result>> "+resultStr);
			JSONObject jsonObject=JSONObject.fromObject(resultStr);
			if(jsonObject.getString("status").equals("ok")){
				return this.getSuccessMap("ok");
			}else{
				return this.getFailedMap(jsonObject.getString("info"));
			}
		}else{
			// 判定App是否绑定团队 -> 未绑定发布至EMM3.0

			if( project.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED) 
					||project.getBizLicense().equals(PROJECT_BIZ_LICENSE.BINDING)) {
				
				String ret = this.publishPackToEmm3(appCoop, appVersion, pack);
				
				log.info(String.format("EmmService -> publishAppPackage -> to EMM3.0 ret:[%s]", ret));
				JSONObject json = JSONObject.fromObject(ret);
				if("error".equals(json.getString("info"))){
					return this.getFailedMap(json.getString("msg"));
				}
				if( json.getString("info").contains("invalid")){
					return this.getFailedMap(json.getString("msg"));
				}
				pack.setPublisedAppCan(IfStatus.YES);
				appPackageDao.save(pack);
				return this.getSuccessMap("ok");
			}

			PkgFileInfo fileInfo = new PkgFileInfo();
			fileInfo.setAppId(appCoop.getAppcanAppId());
			fileInfo.setVersion(pack.getVersionNo());
			fileInfo.setChannelCode(pack.getChannelCode());
			AppChannel appChannel = appService.findAppChannelByCode(appCoop.getId().longValue(),pack.getChannelCode());
			if(null!=appChannel){
				fileInfo.setChannelName(appChannel.getName());
			}
			
			String terminalTypeStr = pack.getTerminalType().name().toLowerCase();
			if("iphone_ipad".equals(terminalTypeStr)) {
				terminalTypeStr = "iphone,ipad";
			}
			fileInfo.setPlatform(terminalTypeStr);
			
			fileInfo.setPackageName(packageName);
			fileInfo.setIsPatchFile(false);
			fileInfo.setForceUpdate(forceUpgrade.equals(IfStatus.YES));
			fileInfo.setNeedConfirm(confirmUpgrade.equals(IfStatus.YES));
			log.info("public package downUrl-->"+pack.getDownloadUrl());
//			fileInfo.setPkgFileUrl(pack.getDownloadUrl());
//			fileInfo.setPkgFileLoc(pack.getDownloadUrl());
			if(pack.getOsType().equals(OSType.ANDROID)){
				fileInfo.setPkgFileUrl(pack.getDownloadUrl());
				fileInfo.setPkgFileLoc(pack.getDownloadUrl());
			}else{
				fileInfo.setPkgFileUrl(pack.getDownloadUrl());
				fileInfo.setPkgFileLoc(pack.getDownloadUrl());
			}
			fileInfo.setBundleId(bundleIdentifier);
			fileInfo.setWidgetUpdateHints(upgradeTip);
			fileInfo.setWidgetCloseHints(shutdownTip);
			fileInfo.setDescription(detail);
			fileInfo.setAppName(appCoop.getName());
			fileInfo.setAppCategory(AppCategory.AppCanNative.ordinal());
			fileInfo.setDownloadCnt(0);	// 下载次数
			
			JSONObject jsonObject = JSONObject.fromObject(pack.getBuildJsonSettings());
			jsonObject = jsonObject.getJSONObject("icon");
			String iconPath = jsonObject.getString("iconPath");
			log.info("publish icon -->"+iconPath);
			fileInfo.setPkgIconLoc(iconPath);
			
			log.info(String.format("EmmService -> publishPackage -> fileInfo.version[%s]", fileInfo.getVersion()));
			
			try {
				
//				TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
				User user = userDao.findOne(findCreateUserId(project));
				
				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(project.getBizCompanyId());
				String[] params = new String[]{enterprise.getId().toString(),"dev"};
				String token = "";
				if(pack.getBuildType().equals(AppPackageBuildType.PRODUCTION)){
					token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);
					log.info("正式环境发包之前,判断应用是否存在:token:"+token+",appId:"+appCoop.getAppcanAppId()+",account-->"+user.getAccount());
					//判断应用是否已经发布
					AppBaseInfo appbase = appBaseInfoFacade.getByAppIdAndCreator(token, appCoop.getAppcanAppId(),user.getAccount());
					if(null==appbase){
						appCoop.setPublished(IfStatus.NO);
						appDao.save(appCoop);
						return this.getFailedMap("no");//应用不存在
					}
					
					log.info("正式环境发包,租户ID"+enterprise.getId()+",企业key:"+enterprise.getEntkey()+",token-->"+token);
					Map<String, Object> mapResult = pkgFileInfoFacade.createAppCanNativePkg(token, fileInfo, "appCanNative");
					log.info(String.format("EmmService -> publishPackage -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					pack.setPublised(IfStatus.YES);
				}else{
					token = TokenUtilTest.getToken(enterprise.getEntkey(), params);
					//判断应用是否已经发布
					log.info("测试环境发包之前,判断应用是否存在:token::"+token+",appId:"+appCoop.getAppcanAppId()+",account-->"+user.getAccount());
					AppBaseInfo appbase = appBaseInfoFacadeTest.getByAppIdAndCreator(token, appCoop.getAppcanAppId(),user.getAccount());
					if(null==appbase){
						appCoop.setPublishedTest(IfStatus.NO);
						appDao.save(appCoop);
						return this.getFailedMap("no");
					}
					
					log.info("测试环境发包,租户ID"+enterprise.getId()+"，企业key:"+enterprise.getEntkey()+",token-->"+token);
					Map<String, Object> mapResult = pkgFileInfoFacadeTest.createAppCanNativePkg(token, fileInfo, "appCanNative");
					log.info(String.format("EmmService -> publishPackage -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					pack.setPublisedTest(IfStatus.YES);
				}
				appPackageDao.save(pack);
			} catch(Exception e) {
				e.printStackTrace();
				log.info("EmmService -> publishPackage exception " + ExceptionUtils.getStackTrace(e));
				throw new RuntimeException("发布Native包失败");
			}
			
		}
		
		return this.getSuccessMap("ok");
	}
	
	/**
	 * 发布Widget包
	 * @param appWidgetId
	 * @param detail
	 * @param productEnv
	 * @param terminalType
	 * @param forceUpgrade
	 * @param confirmUpgrade
	 * @param packageName
	 * @param bundleIdentifier
	 * @param upgradeTip
	 * @param shutdownTip
	 * @param loginUserId
	 * @return
	 */
	public Map<String, Object> publishWidget(
			String serviceFlag,
			long appWidgetId, 
			String detail,
			boolean productEnv,
			String terminalType,
			IfStatus forceUpgrade,
			IfStatus confirmUpgrade,
			String packageName,
			String bundleIdentifier, 	
			String upgradeTip, 
			String shutdownTip,
			long loginUserId,
			String emm3MamUrl,
			String emm3TestUrl) {
		log.info(String.format("EmmService -> publishPackage -> appWidgetId[%d] detail[%s] productEnv[%s] "
				+ "terminalType[%s] forceUpgrade[%s] confirmUpgrade[%s]  packageName[%s] bundleIdentifier[%s] upgradeTip[%s] shutdownTip[%s]", 
				appWidgetId,detail,productEnv,terminalType, forceUpgrade, confirmUpgrade,packageName, bundleIdentifier, upgradeTip, shutdownTip));
		
		AppWidget widget = appWidgetDao.findOne(appWidgetId);
		if(widget == null) {
			log.info("Widget不存在(id=" + appWidgetId + ")");
			return this.getFailedMap("Widget不存在(id=" + appWidgetId + ")");
		}
		
		AppVersion appVersion = appVersionDao.findOne(widget.getAppVersionId());
		if(appVersion == null) {
			log.info("对应包版本不存在");
			return this.getFailedMap("对应版本不存在");
		}
		
		App appCoop = appDao.findOne(appVersion.getAppId());
		if(appCoop == null) {
			log.info("应用不存在");
			return this.getFailedMap("应用不存在");
		}
		
		if(null!=appCoop.getAppCategory() && !AppCategory.AppCanWgt.toString().equals(appCoop.getAppCategory())){
			log.info(String.format("app appcategory[%s] package type[%s]",appCoop.getAppCategory(),AppCategory.AppCanWgt));
			return this.getFailedMap(String.format("应用分类和包分类不匹配，应用：[%s],包[%s]", appCoop.getAppCategory(),AppCategory.AppCanWgt));
		}
		
		// 判定App是否绑定团队
		Project project = projectDao.findOne(appCoop.getProjectId());
		if(project == null) {
			log.info("项目不存在");
			return this.getFailedMap("项目不存在");
		}
		if( project.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED) 
				||project.getBizLicense().equals(PROJECT_BIZ_LICENSE.BINDING)) {
			return this.getFailedMap("应用对应项目未经授权,无法发送widget包");
		}
		if(serviceFlag.equals("enterpriseEmm3")){
			TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
			User user = userDao.findOne(teammember.getUserId());
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("adminLoginName",user.getAccount());//当前登陆员管理帐号
			parameters.put("celue", "");
			parameters.put("appId3",appCoop.getAppcanAppId());
			parameters.put("appCategoryId2","AppCanWgt");
			parameters.put("version2", widget.getVersionNo());
			parameters.put("bigversion",widget.getVersionNo()!=null&&widget.getVersionNo()!=""?widget.getVersionNo().substring(0,5):"");
			parameters.put("channelCode2","0000");
			parameters.put("ipad2", terminalType.toLowerCase().contains("ipad")?"true":"");
			parameters.put("iphone2", terminalType.toLowerCase().contains("iphone")?"true":"");
			parameters.put("Android2", terminalType.toLowerCase().contains("android")?"true":"");
			parameters.put("plat2","");//是否PC端，true是，空为否
			parameters.put("isquanliang_buding2", "false");//是否是全量包，false全量、true补丁
			parameters.put("forceUpdate", forceUpgrade.equals(IfStatus.YES)?"true":"false");//false否/true是
			parameters.put("updateEnd2", confirmUpgrade.equals(IfStatus.YES)?"true":"false");//升级确认(true/false)
			parameters.put("isPkgUrl2", "pkg");//是否是安装包 是（pkg） 空url
			parameters.put("downloadUrl2", codeZipUrl + "/" + widget.getFileName());
			parameters.put("pkgFileUrl2", codeZipUrl + "/" + widget.getFileName());
			parameters.put("pkgSize2", String.valueOf(widget.getFileSize()));//包大小
			parameters.put("pkgName2", String.valueOf(widget.getFileName()));//包名
			parameters.put("bundle2", bundleIdentifier);//ibound
			parameters.put("widgetUpdateHints2", upgradeTip);//升级提示语
			parameters.put("widgetCloseHints2", shutdownTip);//关闭提示语
			parameters.put("description2", widget.getVersionDescription());
			String resultStr="";
			if(productEnv){
				resultStr=HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/saveAppCanWgtPkg", parameters,"UTF-8");
				widget.setPublised(IfStatus.YES);
				appWidgetDao.save(widget);
			}else{
				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mam/xieTongInter/saveAppCanWgtPkg", parameters,"UTF-8");
				widget.setPublisedTest(IfStatus.YES);
				appWidgetDao.save(widget);
			}
			log.info("Emm3 publish widget params:"+parameters.toString()+" ,result>> "+resultStr);
			JSONObject jsonObject=JSONObject.fromObject(resultStr);
			if(jsonObject.getString("status").equals("ok")){
				return this.getSuccessMap("ok");
			}else{
				return this.getFailedMap(jsonObject.getString("info"));
			}
		}else{
			PkgFileInfo fileInfo = new PkgFileInfo();
			fileInfo.setAppId(appCoop.getAppcanAppId());
			fileInfo.setVersion(widget.getVersionNo());
			fileInfo.setPlatform(terminalType.toLowerCase());
			fileInfo.setPackageName(packageName);
			fileInfo.setIsPatchFile(false);
			fileInfo.setForceUpdate(forceUpgrade.equals(IfStatus.YES));
			fileInfo.setNeedConfirm(confirmUpgrade.equals(IfStatus.YES));
			fileInfo.setPkgFileUrl(codeZipUrl + "/" + widget.getFileName());
			fileInfo.setPkgFileLoc(codeZipUrl + "/" + widget.getFileName());
			
			fileInfo.setBundleId(bundleIdentifier);
			fileInfo.setWidgetUpdateHints(upgradeTip);
			fileInfo.setWidgetCloseHints(shutdownTip);
			fileInfo.setDescription(detail);
			fileInfo.setAppName(appCoop.getName());
			fileInfo.setAppCategory(AppCategory.AppCanWgt.ordinal());
			fileInfo.setDownloadCnt(0);	// 下载次数
			
			log.info(String.format("EmmService -> publishPackage -> fileInfo.version[%s]", fileInfo.getVersion()));
			
			try {
				
				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(project.getBizCompanyId());
				String[] params = new String[]{enterprise.getId().toString(),"dev"};
				String token = "";
				if(productEnv){
					token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);
					log.info("正式环境发widget包,租户ID"+enterprise.getId()+",企业key:"+enterprise.getEntkey()+",token-->"+token);
					Map<String, Object> mapResult = pkgFileInfoFacade.createAppCanNativePkg(token, fileInfo, "appCanWgt");
					
					log.info(String.format("EmmService -> publishWidget -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					widget.setPublised(IfStatus.YES);
				}else{
					token = TokenUtilTest.getToken(enterprise.getEntkey(), params);
					log.info("测试环境发widget包,租户ID"+enterprise.getId()+"，企业key:"+enterprise.getEntkey()+",token-->"+token);
					Map<String, Object> mapResult = pkgFileInfoFacadeTest.createAppCanNativePkg(token, fileInfo, "appCanWgt");
					log.info(String.format("EmmService -> publishWidget -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					widget.setPublisedTest(IfStatus.YES);
				}
				appWidgetDao.save(widget);
			} catch(Exception e) {
				e.printStackTrace();
				log.info("EmmService -> publishWidget exception " + ExceptionUtils.getStackTrace(e));
				throw new RuntimeException("发布Widget包失败");
			}
			
			// 添加动态
			String dynamicType = "APP_PUBLISH_";
			if(!productEnv){
				dynamicType += "TEST_";
			}else{
				dynamicType += "";
			}
			dynamicType += "WIDGET_PACKAGE";
			DYNAMIC_MODULE_TYPE APPPACKAGETYPE = DYNAMIC_MODULE_TYPE.valueOf(dynamicType);
			
			this.addPrjDynamic(loginUserId, APPPACKAGETYPE, appCoop.getProjectId(), new Object[]{appCoop});
			
			return this.getSuccessMap("ok");
		}
	
	}
	
	
	private String publishAppToEmm3(App app, Project project, User loginUser) {
		
		User createUser = userDao.findOne(app.getUserId());
		Long memberId = null;
		if(project.getType().equals(PROJECT_TYPE.PERSONAL)){//个人项目发给项目创建者
			ProjectMember member = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
			memberId = member.getUserId();
		}else{//团队项目发给团队创建者
			List<TeamMember> members = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(),TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
			memberId = members.get(0).getUserId();
		}
		User manageUser = userDao.findOne(memberId);
		
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("appid", app.getAppcanAppId()) );
		parameters.add( new BasicNameValuePair("key", app.getAppcanAppKey()) );
		parameters.add( new BasicNameValuePair("create_account", createUser.getAccount()) );
		parameters.add( new BasicNameValuePair("manage_account", manageUser.getAccount()) );
		parameters.add( new BasicNameValuePair("dev_account", loginUser.getAccount()) );
	
		parameters.add( new BasicNameValuePair("name", app.getName()) );
		parameters.add( new BasicNameValuePair("icon", "") );
		parameters.add( new BasicNameValuePair("url", remoteGitRoot + app.getRelativeRepoPath()) );
		parameters.add( new BasicNameValuePair("description", app.getDetail()) );
		parameters.add( new BasicNameValuePair("create_time", "" + app.getCreatedAt().getTime()) );
		
		log.info("publishAppToEmm3 ->" + parameters.toString());
		
		try {
			return HttpUtil.httpPost(this.appPublishUrl, parameters);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

	}
	
	
	/*private String publishAppToBaasAfterEmm(long tenantId,String appCanAppId, String appCanAppKey, String name,String category,String appOwner) {
		
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("appId", tenantId+":"+appCanAppId) );
		parameters.add( new BasicNameValuePair("appKey", appCanAppKey) );
		parameters.add( new BasicNameValuePair("name", name) );
		parameters.add( new BasicNameValuePair("sysName", "EMM") );
		parameters.add( new BasicNameValuePair("mode", category ));
		parameters.add( new BasicNameValuePair("appType", "common" ));
		
		parameters.add( new BasicNameValuePair("appOwner", appOwner) );
		
		log.info("publishAppToBaasAfterEmm ->" + parameters.toString());
		
		try {
			return HttpUtil.httpPost(this.appPublishToBaasUrl, parameters);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

	}*/

	private String publishPackToEmm3(App app, AppVersion version, AppPackage pack) {
//		appid	INT	是	应用ID	11900059
//		version	Char	是	应用版本号	0013
//		plat	Char	是	应用平台android或ios	Android
//		channel_code	Char	否	渠道编号	0001
//		channel_desc	char	否	新建渠道号的描述 不填默认为渠道号	
//		icon	Char	是	Icon图标地址	/upload/www/icon.png 或http://dashboard.appcan.cn/upload/www/icon.png
//		version_des	Char	否	版本描述	
//		ios_terminal	Char	Plat为ios是必填	ios终端	Iphone 或 ipad 或iphone&ipad
//		android_wiget_update	INT	Plat为android是必填	是否支持增量更新（android）1支持，0不支持	
//		ios_wiget_update	INT	Plat为ios是必填	是否支持增量更新（IOS）1支持，0不支持	
//		android_is_push	INT	Plat为android是必填	是否支持推送（android）1支持，0不支持	
//		ios_is_push	INT	Plat为ios是必填	是否支持推送（IOS）1支持，0不支持	
//		time	INT	是	时间 Uninx时间戳	1442373451
//		url	Char	是	下载地址	http://fs.appcan.cn/uploads/2015/10/20//11900044_android_00.00.0009_000_93485_0.apk
//		index_url	Char	否	下载页面地址	http://fs.appcan.cn/uploads/2015/10/20//11900044_android_00.00.0009_000_93485_0.html
//		pack_status	INT	是	打包状态1.打包中 2.打包成功 3.打包失败	
//		file_size	INT	是	包的大小单位b	
		
		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("appid", app.getAppcanAppId()) );
		parameters.add( new BasicNameValuePair("version", pack.getVersionNo()) );
		parameters.add( new BasicNameValuePair("plat", pack.getOsType().name()) );
		parameters.add( new BasicNameValuePair("channel_code", pack.getChannelCode()) );
		parameters.add( new BasicNameValuePair("channel_desc", "") );
		parameters.add( new BasicNameValuePair("icon", "") );
		
		parameters.add( new BasicNameValuePair("version_des", version.getVersionDescription()) );
		parameters.add( new BasicNameValuePair("ios_terminal", pack.getTerminalType().name().replaceAll("&", "_")) );
		
		parameters.add( new BasicNameValuePair("android_wiget_update", "" + pack.getUpdateSwith()) );
		parameters.add( new BasicNameValuePair("android_is_push", "" + pack.getPushIF()) );
		parameters.add( new BasicNameValuePair("ios_wiget_update", "" + pack.getUpdateSwith()) );
		parameters.add( new BasicNameValuePair("ios_is_push", "" + pack.getPushIF()) );
		
		parameters.add( new BasicNameValuePair("time", "" + pack.getCreatedAt().getTime()) );
		parameters.add( new BasicNameValuePair("url", pack.getDownloadUrl()) );
		parameters.add( new BasicNameValuePair("index_url", pack.getQrCode()) );
		parameters.add( new BasicNameValuePair("pack_status", "2") );	// 打包成功才允许发布，所以赋值2（打包成功）
		parameters.add( new BasicNameValuePair("file_size", "" + pack.getFileSize() * 1024) );
		
		log.info("publishPackToEmm3 -> " + parameters.toString());
		
		try {
			return HttpUtil.httpPost(packPublishUrl, parameters);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

	}
	
	
	
	/**
	 * 发布AppPackage补丁包
	 * @user jingjian.wu
	 * @date 2015年11月9日 下午10:23:15
	 */
	public Map<String,Object> publishPackagePatch(
			long appPatchId,
			String detail,
			IfStatus forceUpgrade,
			IfStatus confirmUpgrade,
			String bundleIdentifier,
			String upgradeTip,
			String shutdownTip,
			String channelCode,
			String terminalType,
			boolean productEnv
			) {
		
		log.info(String.format("EmmService -> publishPackage -> appPatchId[%d] detail[%s] forceUpgrade[%s] "
				+ "confirmUpgrade[%s]  bundleIdentifier[%s] upgradeTip[%s] shutdownTip[%s]  channelCode[%s]  terminalType[%s] productEnv[%s]", 
				appPatchId, detail, forceUpgrade, confirmUpgrade, bundleIdentifier, upgradeTip, shutdownTip,channelCode,terminalType,productEnv));
		
		AppPatch patch = appPatchDao.findOne(appPatchId);
		if(patch == null) {
			log.info("补丁包不存在");
			return this.getFailedMap("补丁包不存在");
		}
		
		AppVersion appVersion = appVersionDao.findOne(patch.getBaseAppVersionId());
		
		if(appVersion == null) {
			log.info("对应包版本不存在");
			return this.getFailedMap("对应版本不存在");
		}
		
		App appCoop = appDao.findOne(appVersion.getAppId());
		if(appCoop == null) {
			log.info("应用不存在");
			return this.getFailedMap("应用不存在");
		}
		if(serviceFlag.equals("enterpriseEmm3")){
			Project project = projectDao.findOne(appCoop.getProjectId());
			TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
			User user = userDao.findOne(teammember.getUserId());
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("adminLoginName",user.getAccount());//当前登陆员管理帐号
			parameters.put("celue", "");//策略id
			parameters.put("appId3",appCoop.getAppcanAppId());//应用appid
			parameters.put("appCategoryId2","AppCanNative");//应用类型 区分wgt 跟appcanNative(AppCanNative/AppCanWgt)
			parameters.put("version2", patch.getVersionNo());//版本
			parameters.put("bigversion",patch.getVersionNo()!=""&&patch.getVersionNo()!=null? patch.getVersionNo().substring(0,5):"");//大版本
			parameters.put("channelCode2",channelCode);//渠道号
			parameters.put("ipad2", terminalType.equals(TerminalType.IPAD.name())||terminalType.equals("IPHONE,IPAD")?"true":"");
			parameters.put("iphone2", terminalType.equals(TerminalType.IPHONE.name())||terminalType.equals("IPHONE,IPAD")?"true":"");
			parameters.put("Android2", terminalType.equals(TerminalType.ANDROID.name())?"true":"");
			parameters.put("plat2", "");//是否PC端，true是，空为否
			parameters.put("isquanliang_buding2", "true");//是否是全量包，false全量、true补丁
			parameters.put("forceUpdate", forceUpgrade.equals(IfStatus.YES)?"true":"false");//false否/true是+
			parameters.put("updateEnd2", confirmUpgrade.equals(IfStatus.YES)?"true":"false");//升级确认(true/false)
			parameters.put("isPkgUrl2", "pkg");//string	传入（pkg或者空）为判断是否安装包
			parameters.put("downloadUrl2", codeZipUrl + "/" +appVersion.getBranchZipName());//下载地址（http://开头）
			parameters.put("pkgFileUrl2", codeZipUrl + "/" +appVersion.getBranchZipName());//安装包地址
			parameters.put("pkgSize2", String.valueOf(patch.getFileSize()));//包大小
			parameters.put("pkgName2", appCoop.getName());//包名
			parameters.put("bundle2", bundleIdentifier);//ibound
			parameters.put("widgetUpdateHints2", upgradeTip);//升级提示语
			parameters.put("widgetCloseHints2", shutdownTip);//关闭提示语
			parameters.put("description2", detail);//描述
			String resultStr="";
			if(productEnv){
				resultStr=HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/saveAppCanNativePkg", parameters,"UTF-8");
				patch.setPublished(IfStatus.YES);
				appPatchDao.save(patch);
			}else{
				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mam/xieTongInter/saveAppCanNativePkg", parameters,"UTF-8");
				patch.setPublishedTest(IfStatus.YES);
				appPatchDao.save(patch);
			}
			JSONObject jsonObject=JSONObject.fromObject(resultStr);
			if(jsonObject.getString("status").equals("ok")){
				return this.getSuccessMap("ok");
			}else{
				return this.getFailedMap(jsonObject.getString("info"));
			}
		} else{
			PkgFileInfo fileInfo = new PkgFileInfo();
			fileInfo.setAppId(appCoop.getAppcanAppId());
			fileInfo.setVersion(patch.getVersionNo());
			fileInfo.setChannelCode(channelCode);
			fileInfo.setPlatform(terminalType.toLowerCase());
			fileInfo.setIsPatchFile(true);
			fileInfo.setForceUpdate(forceUpgrade.equals(IfStatus.YES));
			fileInfo.setNeedConfirm(confirmUpgrade.equals(IfStatus.YES));
			fileInfo.setPkgFileUrl(codeZipUrl + "/" +appVersion.getBranchZipName());
			fileInfo.setBundleId(bundleIdentifier);
			fileInfo.setWidgetUpdateHints(upgradeTip);
			fileInfo.setWidgetCloseHints(shutdownTip);
			fileInfo.setDescription(detail);
			fileInfo.setAppName(appCoop.getName());
			fileInfo.setAppCategory(AppCategory.AppCanNative.ordinal());
			fileInfo.setDownloadCnt(0);	// 下载次数
			
			log.info(String.format("EmmService -> publishPackage -> fileInfo.version[%s]", fileInfo.getVersion()));
			
			try {
				Project prj = projectDao.findOne(appCoop.getProjectId());
				if(null==prj || prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)
						||prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.BINDING)){
					return this.getFailedMap("应用所在项目未绑定");
				}
//				Team team = teamDao.findOne(prj.getTeamId());
				
				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(prj.getBizCompanyId());
				String[] params = new String[]{enterprise.getId().toString(),"dev"};
				String token = "";
				if(productEnv){
					token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);
					Map<String, Object> mapResult = pkgFileInfoFacade.createAppCanNativePkg(token, fileInfo, "AppCanNative");
					
					log.info(String.format("EmmService -> publishPackage -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					
					patch.setPublished(IfStatus.YES);
				}else{
					token = TokenUtilTest.getToken(enterprise.getEntkey(), params);
					Map<String, Object> mapResult = pkgFileInfoFacadeTest.createAppCanNativePkg(token, fileInfo, "AppCanNative");
					log.info(String.format("EmmService -> publishPackage -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					patch.setPublishedTest(IfStatus.YES);
				}
				appPatchDao.save(patch);
			} catch(Exception e) {
				e.printStackTrace();
				log.info("EmmService -> publishPackage exception " + ExceptionUtils.getStackTrace(e));
				throw new RuntimeException("发布Native补丁包失败");
			}
		}
		return this.getSuccessMap("ok");
	}
	
	/**
	 * 发布Widget补丁包
	 * @param appPatchId
	 * @param detail
	 * @param forceUpgrade
	 * @param confirmUpgrade
	 * @param bundleIdentifier
	 * @param upgradeTip
	 * @param shutdownTip
	 * @param terminalType
	 * @param productEnv
	 * @return
	 */
	public Map<String, Object> publishWidgetPatch(
			long appPatchId, 
			String detail, 
			IfStatus forceUpgrade,
			IfStatus confirmUpgrade, 
			String bundleIdentifier, 
			String upgradeTip, 
			String shutdownTip, 
			String terminalType,
			boolean productEnv) {
		log.info(String.format("EmmService -> publishPackage -> appPatchId[%d] detail[%s] forceUpgrade[%s] "
				+ "confirmUpgrade[%s]  bundleIdentifier[%s] upgradeTip[%s] shutdownTip[%s]  terminalType[%s] productEnv[%s]", 
				appPatchId, detail, forceUpgrade, confirmUpgrade, bundleIdentifier, upgradeTip, shutdownTip,terminalType,productEnv));
		
		AppPatch patch = appPatchDao.findOne(appPatchId);
		if(patch == null) {
			log.info("补丁包不存在");
			return this.getFailedMap("补丁包不存在");
		}
		
		AppVersion appVersion = appVersionDao.findOne(patch.getBaseAppVersionId());
		
		if(appVersion == null) {
			log.info("对应包版本不存在");
			return this.getFailedMap("对应版本不存在");
		}
		
		App appCoop = appDao.findOne(appVersion.getAppId());
		if(appCoop == null) {
			log.info("应用不存在");
			return this.getFailedMap("应用不存在");
		}
		if(serviceFlag.equals("enterpriseEmm3")){
			Project project = projectDao.findOne(appCoop.getProjectId());
			TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
			User user = userDao.findOne(teammember.getUserId());
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("adminLoginName",user.getAccount());//当前登陆员管理帐号
			parameters.put("celue", "");//策略id
			parameters.put("appId3",appCoop.getAppcanAppId());//应用appid
			parameters.put("appCategoryId2","AppCanNative");//应用类型 区分wgt 跟appcanNative(AppCanNative/AppCanWgt)
			parameters.put("version2", patch.getVersionNo());//版本
			parameters.put("bigversion",patch.getVersionNo()!=""&&patch.getVersionNo()!=null? patch.getVersionNo().substring(0,5):"");//大版本
			parameters.put("channelCode2","0000");//渠道号
			parameters.put("ipad2", terminalType.equals(TerminalType.IPAD.name())||terminalType.equals("IPHONE,IPAD")?"true":"");
			parameters.put("iphone2", terminalType.equals(TerminalType.IPHONE.name())||terminalType.equals("IPHONE,IPAD")?"true":"");
			parameters.put("Android2", terminalType.equals(TerminalType.ANDROID.name())?"true":"");
			parameters.put("plat2", "");//是否PC端，true是，空为否
			parameters.put("isquanliang_buding2", "true");//是否是全量包，false全量、true补丁
			parameters.put("forceUpdate", forceUpgrade.equals(IfStatus.YES)?"true":"false");//false否/true是+
			parameters.put("updateEnd2", confirmUpgrade.equals(IfStatus.YES)?"true":"false");//升级确认(true/false)
			parameters.put("isPkgUrl2", "pkg");//string	传入（pkg或者空）为判断是否安装包
			parameters.put("downloadUrl2", patch.getDownloadUrl());//下载地址（http://开头）
			parameters.put("pkgFileUrl2", patch.getDownloadUrl());//安装包地址
			parameters.put("pkgSize2", String.valueOf(patch.getFileSize()));//包大小
			parameters.put("pkgName2", String.valueOf(patch.getFileName()));//包名
			parameters.put("bundle2", bundleIdentifier);
			parameters.put("widgetUpdateHints2", upgradeTip);//升级提示语
			parameters.put("widgetCloseHints2", shutdownTip);//关闭提示语
			parameters.put("description2", detail);//描述
			String resultStr="";
			if(productEnv){
				resultStr=HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/saveAppCanNativePkg", parameters,"UTF-8");
				patch.setPublished(IfStatus.YES);
				appPatchDao.save(patch);
			}else{
				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mam/xieTongInter/saveAppCanNativePkg", parameters,"UTF-8");
				patch.setPublishedTest(IfStatus.YES);
				appPatchDao.save(patch);
			}
			JSONObject jsonObject=JSONObject.fromObject(resultStr);
			if(jsonObject.getString("status").equals("ok")){
				return this.getSuccessMap("ok");
			}else{
				return this.getFailedMap(jsonObject.getString("info"));
			}
		}else{
			PkgFileInfo fileInfo = new PkgFileInfo();
			fileInfo.setAppId(appCoop.getAppcanAppId());
			fileInfo.setVersion(patch.getVersionNo());
			fileInfo.setPlatform(terminalType.toLowerCase());
			fileInfo.setIsPatchFile(true);
			fileInfo.setForceUpdate(forceUpgrade.equals(IfStatus.YES));
			fileInfo.setNeedConfirm(confirmUpgrade.equals(IfStatus.YES));
			fileInfo.setPkgFileUrl(codeZipUrl + "/" + patch.getFileName());
			fileInfo.setBundleId(bundleIdentifier);
			fileInfo.setWidgetUpdateHints(upgradeTip);
			fileInfo.setWidgetCloseHints(shutdownTip);
			fileInfo.setDescription(detail);
			fileInfo.setAppName(appCoop.getName());
			fileInfo.setAppCategory(AppCategory.AppCanWgt.ordinal());
			fileInfo.setDownloadCnt(0);	// 下载次数
			
			log.info(String.format("EmmService -> publishPackage -> fileInfo.version[%s]", fileInfo.getVersion()));
			
			try {
				Project prj = projectDao.findOne(appCoop.getProjectId());
				if(null==prj || prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)
						||prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.BINDING) ){
					return this.getFailedMap("应用所在项目未绑定");
				}
//				Team team = teamDao.findOne(prj.getTeamId());
				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(prj.getBizCompanyId());
				String[] params = new String[]{enterprise.getId().toString(),"dev"};
				String token = "";
				if(productEnv){
					token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);
					Map<String, Object> mapResult = pkgFileInfoFacade.createAppCanNativePkg(token, fileInfo, "AppCanWgt");
					log.info(String.format("EmmService -> publishPackage -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					patch.setPublished(IfStatus.YES);
				}else{
					token = TokenUtilTest.getToken(enterprise.getEntkey(), params);
					Map<String, Object> mapResult = pkgFileInfoFacadeTest.createAppCanNativePkg(token, fileInfo, "AppCanWgt");
					log.info(String.format("EmmService -> publishPackage -> ret[%s]", mapResult.toString()));
					if("error".equals(mapResult.get("status").toString())){
						return this.getFailedMap(mapResult.get("info").toString());
					}
					patch.setPublishedTest(IfStatus.YES);
				}
				appPatchDao.save(patch);
			} catch(Exception e) {
				e.printStackTrace();
				log.info("EmmService -> publishPackage exception " + ExceptionUtils.getStackTrace(e));
				throw new RuntimeException("发布Widget补丁包失败");
			}	
		}
		return this.getSuccessMap("ok");
	}
	/**
	 * 如果是大众版且项目所属团对未绑定，则取项目创建者Id
	 * haijun.cheng
	 * 2016-08-12
	 * @param project
	 * @return
	 */
	public Long findCreateUserId(Project project) {
		if("online".equals(serviceFlag)&&!projectService.isTeamBind(project.getTeamId())){
			ProjectMember projectmember=projectMemberService.findMemberByProjectIdAndMemberType(project.getId(), PROJECT_MEMBER_TYPE.CREATOR);
		    return projectmember.getUserId();
		}else{
			TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
			return teammember.getUserId();
		}
	}
	
	/**
	 * 发布native包到EMM3
	 * @param packageName    报名
	 * @param bundleIdentifier   iphone和ipad时候,需要填写
	 * @param appPackageId     包唯一标识
	 * @param emmAppTypeId    
	 * @param emmAppGroupId
	 * @param description
	 * @param emmAppSource
	 * @param imageUrls     截图
	 * @param loginUserId
	 * @param app_category
	 * @param terminalType      android,pad,ios,ipad
	 * @param emm3MamUrl
	 * @param emm3TestUrl
	 * @return
	 */
	public Map<String,Object> publishNativePkg(
			String packageName,
			String bundleIdentifier,
			long appPackageId,
			int emmAppTypeId,
			int emmAppGroupId,
			String description,
			EMMAppSource emmAppSource,
			List<String> imageUrls,
			long loginUserId,
			AppCategory app_category,
			String terminalType,//发包时候的终端类型android,还是pad,还是iphone还是ipad
			String emm3MamUrl,
			String emm3TestUrl) { 
		
		log.info(String.format("Emm3407Service -> publishNativeApp -> packageName[%s] bundleIdentifier[%s]appPackageId[%d] emmAppTypeId[%d] emmAppGroupId[%d]description[%s] emmAppSource[%s] imageUrls[%s] appCategory[%s] terminalType[%s]",
				packageName, bundleIdentifier, appPackageId, emmAppTypeId, emmAppGroupId,description,emmAppSource.name(), imageUrls!=null?imageUrls.toString():null,app_category,terminalType));

		AppPackage pack = appPackageDao.findOne(appPackageId);
		if(pack == null) {
			log.info("对应包不存在");
			return this.getFailedMap("对应包不存在");
		}
		
		AppVersion appVersion = appVersionDao.findOne(pack.getAppVersionId());
		if(appVersion == null) {
			log.info("对应包版本不存在");
			return this.getFailedMap("对应版本不存在");
		}
		
		App appCoop = appDao.findOne(appVersion.getAppId());
		if(appCoop == null) {
			log.info("应用不存在");
			return this.getFailedMap("应用不存在");
		}
		Project project = projectDao.findOne(appCoop.getProjectId());
		if(project == null) {
			log.info("应用所对应的项目不存在");
			return this.getFailedMap("应用所在项目不存在");
		}
		TeamMember teammember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
		User user = userDao.findOne(teammember.getUserId());
		Map<String,String> parameters = new HashMap<String,String>();
		parameters.put("iconLoc1", appCoop.getIcon());//icon路径
		parameters.put("appName",appCoop.getName());
		parameters.put("appId",appCoop.getAppcanAppId());
		parameters.put("appKey",appCoop.getAppcanAppKey());
		parameters.put("appGroupId",Integer.toString(emmAppGroupId));
		parameters.put("appTypeId", Integer.toString(emmAppTypeId));
		parameters.put("appSource", emmAppSource.name().toLowerCase());//来源
		parameters.put("pkgFileLoc", "");//包路径
		parameters.put("description",description );
		parameters.put("version",pack.getVersionNo());
		parameters.put("tag", "");//应用标记
		parameters.put("adminLoginName", user.getAccount());
		if(imageUrls != null && imageUrls.size() > 0) {
			for(int i = 0; i < imageUrls.size(); i++) {
				if(i == 0) {
					parameters.put("icon1img",imageUrls.get(i));
				} else if(i == 1) {
					parameters.put("icon2img",imageUrls.get(i));
				} else if(i == 2) {
					parameters.put("icon3img",imageUrls.get(i));
				}
			}
		}
		
		if(terminalType.toLowerCase().equals("android")){
			parameters.put("Android", "pkg");
			parameters.put("pkgFileUrlAndroid", pack.getDownloadUrl());
			parameters.put("pkgSizeAndroid", String.valueOf(pack.getFileSize()));
			parameters.put("AndroidPkgName", packageName);
			parameters.put("AndroidUrl", pack.getDownloadUrl());
		}else if(terminalType.toLowerCase().equals("pad")){
			parameters.put("pad", "pkg");
			parameters.put("pkgFileUrlPad", pack.getDownloadUrl());
			parameters.put("pkgSizePad", String.valueOf(pack.getFileSize()));
			parameters.put("padPkgName", packageName);
			parameters.put("padUrl", pack.getDownloadUrl());
		}else if(terminalType.toLowerCase().equals("iphone")){
			parameters.put("iPhone", "pkg");
			parameters.put("pkgFileUrlIos", pack.getDownloadUrl());
			parameters.put("pkgSizeIos", String.valueOf(pack.getFileSize()));
			parameters.put("iPhoneBid", bundleIdentifier);
			parameters.put("iPhonePkgName", packageName);
			parameters.put("iPhoneUrl", pack.getDownloadUrl());
		}else if(terminalType.toLowerCase().equals("ipad")){
			parameters.put("iPad", "pkg");
			parameters.put("iPad_PkgFileUrl", pack.getDownloadUrl());
			parameters.put("pkgSizeIos", String.valueOf(pack.getFileSize()));
			parameters.put("iPadBid", bundleIdentifier);
			parameters.put("iPadPkgName", packageName);
			parameters.put("iPadUrl", pack.getDownloadUrl());
		}
		log.info("EMM3407service publisNativeApp parameters:"+parameters.toString());
		String resultStr="";
		String postUrl = "/mam/xieTongInter/addNativeApp";//默认调用发布native应用接口,如果应用已经发布过了,则下面需要改为editSaveNative方法
		
		if(pack.getBuildType().equals(AppPackageBuildType.PRODUCTION)){
			if(appCoop.getPublished().equals(IfStatus.YES)){
				postUrl = "/mam/xieTongInter/editSaveNative";
			}
			resultStr=HttpUtil.httpsPost(emm3MamUrl+postUrl, parameters,"UTF-8");
			pack.setPublised(IfStatus.YES);
			appPackageDao.save(pack);
			appCoop.setAppCategory(app_category.name());
			appCoop.setPublished(IfStatus.YES);
		}else{
			if(appCoop.getPublishedTest().equals(IfStatus.YES)){
				postUrl = "/mam/xieTongInter/editSaveNative";
			}
			resultStr=HttpUtil.httpsPost(emm3TestUrl+postUrl, parameters,"UTF-8");
			pack.setPublisedTest(IfStatus.YES);
			appCoop.setAppCategory(app_category.name());
			appCoop.setPublishedTest(IfStatus.YES);
			appPackageDao.save(pack);
		}
		log.info("EMM3407service publisNativeApp "+postUrl+"  result>> "+resultStr);
		JSONObject jsonObject=JSONObject.fromObject(resultStr);
		if(jsonObject.getString("status").equals("ok")){
			return this.getSuccessMap("ok");
		}else{
			throw new  RuntimeException(jsonObject.getString("info"));
		}
	}
}
