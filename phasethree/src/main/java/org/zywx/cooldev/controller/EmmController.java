package org.zywx.cooldev.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.appdo.facade.mam.entity.application.MdmAplctiongrp;
import org.zywx.appdo.facade.mam.enums.AppCategory;
import org.zywx.appdo.facade.mam.service.app.AppBaseInfoFacade;
import org.zywx.appdo.facade.mam.service.appGroup.MdmAplctiongrpFacade;
import org.zywx.appdo.facade.omm.entity.app.AppType;
import org.zywx.appdo.facade.omm.entity.tenant.Enterprise;
import org.zywx.appdo.facade.omm.service.app.AppTypeFacade;
import org.zywx.appdo.facade.omm.service.tenant.TenantFacade;
import org.zywx.cooldev.commons.Enums.AppPackageBuildType;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.EMMAppSource;
import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.PATCH_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.service.EmmService;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.emm.TokenUtilProduct;
import org.zywx.cooldev.util.emm.TokenUtilTest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * EMM关联接口
 * @author yang.li
 * @date 2015-09-18
 *
 */
@Controller
@RequestMapping(value = "/emm")
public class EmmController extends BaseController {

	@Autowired
	private EmmService emmService;
	@Autowired(required=false)
	@Qualifier(value="mdmAplctiongrpFacade")
	private MdmAplctiongrpFacade mdmAplctiongrpFacade;
	
	
	@Autowired(required=false)
	@Qualifier(value="mdmAplctiongrpFacadeTest")
	private MdmAplctiongrpFacade mdmAplctiongrpFacadeTest;
	
	
	@Autowired(required=false)
	@Qualifier(value="appTypeFacade")
	private AppTypeFacade appTypeFacade;
	@Autowired(required=false)
	@Qualifier(value="appBaseInfoFacade")
	private AppBaseInfoFacade appBaseInfoFacade;
	
	@Autowired(required=false)
	private TenantFacade tenantFacade;
	
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	@Value("${emm3Url}")
	private String emm3MamUrl;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	
	@ResponseBody
	@RequestMapping(value = "/type", method=RequestMethod.GET)

	public Map<String, Object> getCategory(
			@RequestHeader(value="loginUserId") long loginUserId) {
		try {
			if(serviceFlag.equals("enterpriseEmm3")){
				Map<String,String> parameters = new HashMap<String,String>();
				String resultStr = HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/getAppType", parameters,"UTF-8");
				log.info("emm3 get appType-->"+resultStr);
				JSONArray jsonArray = JSONArray.fromObject(resultStr);
				List< Map<String, Object> > retList = new ArrayList<>();
				for(int i =0;i<jsonArray.size();i++){
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					Map<String, Object> map = new HashMap<>();
					map.put("id", jsonObj.getString("id"));
					map.put("categoryName", jsonObj.getString("name"));
					retList.add(map);
				}
				return this.getSuccessMap(retList);
			}else{
				List<AppType> typeList = appTypeFacade.getAll();
				List< Map<String, Object> > retList = new ArrayList<>();
				if(typeList != null && typeList.size() > 0) {
					for(AppType type : typeList) {
						Map<String, Object> map = new HashMap<>();
						map.put("id", type.getId());
						map.put("categoryName", type.getName());
						retList.add(map);
					}
				}
				return this.getSuccessMap(retList);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	
	/**
	 * 创建EMM分组
	 * @user jingjian.wu
	 * @date 2015年11月10日 下午6:37:55
	 */
	@ResponseBody
	@RequestMapping(value = "/group/create", method=RequestMethod.POST)
	public Map<String, Object> creatGroup(
			@RequestHeader(value="loginUserId") long loginUserId,
			@PathVariable(value="appId") long appId,
			@RequestParam(value="productEnv") boolean productEnv,
			@RequestParam(value="groupName") String  groupName,
			@RequestParam(value="desc") String desc) {
		try {
			log.info("loginUserId:"+loginUserId+",appId:"+appId+",productEnv:"+productEnv+",groupName:"+groupName+",desc:"+desc);
			Project project =  appService.findProject(appId);
			if(project.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
					|| project.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING)){
				String[] params = new String[2];
				Enterprise enterprise = tenantFacade.getEnterpriseByShortName(project.getBizCompanyId());
				params[0] = enterprise.getId().toString();
				params[1] = "dev";
//				TeamMember teamMember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
				String token = "";
				User user  = userService.findUserById(this.emmService.findCreateUserId(project));
				List<MdmAplctiongrp> groupList = new ArrayList<MdmAplctiongrp>();
				if(productEnv){
					token =  TokenUtilProduct.getToken(enterprise.getEntkey(), params);
					MdmAplctiongrp entity=new MdmAplctiongrp();
					entity.setName(groupName);
					entity.setDescription(desc); 
					entity.setCreateUser(user.getAccount());
					mdmAplctiongrpFacade.create(token,entity);
					groupList = mdmAplctiongrpFacade.getByCreateUser(token, null);
				}else{
					token =  TokenUtilTest.getToken(enterprise.getEntkey(), params);
					MdmAplctiongrp entity=new MdmAplctiongrp();
					entity.setName(groupName);
					entity.setDescription(desc); 
					entity.setCreateUser(user.getAccount());
					mdmAplctiongrpFacadeTest.create(token,entity);
					groupList = mdmAplctiongrpFacadeTest.getByCreateUser(token, null);//获取所有分组
				}
				List< Map<String, Object> > retList = new ArrayList<>();
				if(groupList != null && groupList.size() > 0) {
					for(MdmAplctiongrp group : groupList) {
						Map<String, Object> map = new HashMap<>();
						map.put("id", group.getId());
						map.put("groupName", group.getName());
						retList.add(map);
					}
				}

				return this.getSuccessMap(retList);
			}else{
				return this.getFailedMap("应用所在项目未绑定企业");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/group/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getGroup(
			@RequestHeader(value="loginUserId") long loginUserId,
			@PathVariable(value="appId") long appId,
			@RequestParam(value="productEnv") boolean productEnv) {
		try {
			log.info("loginUserId-->"+loginUserId+",appId-->"+appId+",productEnv-->"+productEnv);
			Project project =  appService.findProject(appId);
//			TeamMember teamMember = teamMemberService.findMemberByTeamIdAndMemberType(project.getTeamId(), TEAMREALTIONSHIP.CREATE);
			User user  = userService.findUserById(this.emmService.findCreateUserId(project));
			if(project.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
					|| project.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING)){
				if(serviceFlag.equals("enterpriseEmm3")){
					Map<String,String> parameters = new HashMap<String,String>();
					parameters.put("adminLoginName", user.getAccount());
					String resultStr="";
					if(productEnv){
						resultStr=HttpUtil.httpsPost(emm3MamUrl+"/mam/xieTongInter/getAppGroupInfo", parameters,"UTF-8");
					}else{
						resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mam/xieTongInter/getAppGroupInfo", parameters,"UTF-8");
					}
					log.info("emm3 get appType-->"+resultStr);
					JSONObject jsonObject=JSONObject.fromObject(resultStr);
					if(jsonObject.getString("status").equals("ok")){
						JSONArray jsonArray = jsonObject.getJSONArray("rows");
						List< Map<String, Object> > retList = new ArrayList<>();
						for(int i =0;i<jsonArray.size();i++){
							JSONObject jsonObj = jsonArray.getJSONObject(i);
							Map<String, Object> map = new HashMap<>();
							map.put("id",jsonObj.getString("id"));
							map.put("groupName",jsonObj.getString("name"));
							retList.add(map);
						}
						return  this.getSuccessMap(retList);
					}else{
						return  this.getSuccessMap("获取应用组失败");
					}
					
				}else{				
					String[] params = new String[2];
					Enterprise enterprise = tenantFacade.getEnterpriseByShortName(project.getBizCompanyId());
					
					params[0] = enterprise.getId().toString();
					params[1] = "dev";
					String token = "";
					List<MdmAplctiongrp> groupList = new ArrayList<MdmAplctiongrp>();
					if(productEnv){
						token =  TokenUtilProduct.getToken(enterprise.getEntkey(), params);
						groupList = mdmAplctiongrpFacade.getByCreateUser(token, user.getAccount());
//						groupList = mdmAplctiongrpFacade.getByCreateUser(token, null);
					}else{
						token =  TokenUtilTest.getToken(enterprise.getEntkey(), params);
						groupList = mdmAplctiongrpFacadeTest.getByCreateUser(token, user.getAccount());//只获取user.getAccount()创建的分组
//						groupList = mdmAplctiongrpFacadeTest.getByCreateUser(token, null);//获取所有分组
					}
					List< Map<String, Object> > retList = new ArrayList<>();
					if(groupList != null && groupList.size() > 0) {
						for(MdmAplctiongrp group : groupList) {
							Map<String, Object> map = new HashMap<>();
							map.put("id", group.getId());
							map.put("groupName", group.getName());
							retList.add(map);
						}
					}
					log.info("loginUserId-->"+loginUserId+",appId-->"+appId+",productEnv-->"+productEnv+",groupList-->"+groupList.toString());
					return this.getSuccessMap(retList);
				}
				
			}else{
				return this.getFailedMap("应用所在项目未绑定企业");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 判断EMM中的发布状态
	 * @user jingjian.wu
	 * @date 2015年11月9日 下午10:15:35
	 */
	@ResponseBody
	@RequestMapping(value = "/app/exist", method=RequestMethod.GET)
	public Map<String, Object> isAppExist(
			@RequestParam(value="appcanAppId") String appcanAppId,
			@RequestParam(value="appcanAppKey") String appcanAppKey,
			@RequestParam(value="productEnv", required=false,defaultValue="true") boolean productEnv,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		Map<String, Object> ret = emmService.updateOrIsAppExist(appcanAppId, appcanAppKey,productEnv,serviceFlag,emm3MamUrl,emm3TestUrl);
		return ret;

	}
	
	
	/**
	 * native包发布至emm3407
	 * @param packageName
	 * @param bundleIdentifier
	 * @param appPackageId
	 * @param emmAppTypeId
	 * @param emmAppGroupId
	 * @param detail
	 * @param emmAppSource
	 * @param images
	 * @param terminalType   android,pad,iphone,ipad
	 * @param loginUserId
	 * @return
	 */
	
	@ResponseBody
	@RequestMapping(value = "/app/publishNative", method=RequestMethod.POST)
	public Map<String, Object> publishNativeApp(
			@RequestParam(value="packageName") String packageName,
			@RequestParam(value="bundleIdentifier", required=false) String bundleIdentifier,
			@RequestParam(value="appPackageId",required=false,defaultValue="0l") long appPackageId,
			@RequestParam(value="emmAppTypeId",required=false,defaultValue="0") int emmAppTypeId,
			@RequestParam(value="emmAppGroupId",required=false,defaultValue="0") int emmAppGroupId,
			@RequestParam(value="detail", required=false) String description,
			@RequestParam(value="emmAppSource",required=false,defaultValue="PERSONAL") EMMAppSource emmAppSource,
			@RequestParam(value="images",required=false) List<String> images,
			@RequestParam(value="appType",defaultValue="Native") AppCategory app_category,
			@RequestParam(value="terminalType",defaultValue="android") String terminalType,//发包时候的终端类型android,还是pad,还是iphone还是ipad
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			Map<String,Object> map = this.emmService.publishNativePkg(packageName, bundleIdentifier, appPackageId, emmAppTypeId, emmAppGroupId, description, emmAppSource, images, loginUserId, app_category, terminalType, emm3MamUrl, emm3TestUrl);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/app/publish", method=RequestMethod.POST)
	public Map<String, Object> publishApp(
			@RequestParam(value="appType",defaultValue="AppCanNative") AppCategory app_category,
			@RequestParam(value="appcanAppId") String appcanAppId,
			@RequestParam(value="appcanAppKey") String appcanAppKey,
			@RequestParam(value="emmAppTypeId",required=false,defaultValue="0") int emmAppTypeId,
			@RequestParam(value="emmAppGroupId",required=false,defaultValue="0") int emmAppGroupId,
			@RequestParam(value="emmAppSource",required=false,defaultValue="PERSONAL") EMMAppSource emmAppSource,
			@RequestParam(value="images",required=false) List<String> images,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(required=false,defaultValue="true") boolean productEnv) {
		
		try {
			log.info("appcanAppId->"+appcanAppId+",appcanAppKey->"+appcanAppKey+",emmAppTypeId->"+emmAppTypeId+",emmAppGroupId->"+emmAppGroupId+",loginUserId->"+loginUserId+",productEnv->"+productEnv+",appCategory->"+app_category);
			Map<String,Object> map = this.emmService.publishApp(serviceFlag,appcanAppId, appcanAppKey, emmAppTypeId, emmAppGroupId, emmAppSource, images, loginUserId,productEnv,app_category,emm3MamUrl,emm3TestUrl);

//		HashMap<String, Object> ret = new HashMap<>();
//		
//		ret.put("affected", affected);
//		return this.getSuccessMap(ret);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 发布全量包（EMM）
	 * @param appPackageId
	 * @param detail
	 * @param forceUpgrade
	 * @param confirmUpgrade
	 * @param packageName
	 * @param bundleIdentifier
	 * @param upgradeTip
	 * @param shutdownTip
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/package/publish", method=RequestMethod.POST)
	public Map<String, Object> publishPackage(
			@RequestParam(value="appPackageId",required=false,defaultValue="0l") long appPackageId,
			@RequestParam(value="detail", required=false) String detail,
			@RequestParam(value="forceUpgrade", required=false) IfStatus forceUpgrade,
			@RequestParam(value="confirmUpgrade", required=false) IfStatus confirmUpgrade,
			@RequestParam(value="packageName", required=false) String packageName,
			@RequestParam(value="bundleIdentifier", required=false) String bundleIdentifier,
			@RequestParam(value="upgradeTip", required=false) String upgradeTip,
			@RequestParam(value="shutdownTip", required=false) String shutdownTip,
			@RequestHeader(value="loginUserId", required=false) long loginUserId) {
		
		try {
			AppPackage pack = this.appService.getSingleAppPackage(appPackageId);
			if (null == pack) {
				return this.getFailedMap("所选包不存在");
			}
	
			Map<String,Object> map = this.emmService.publishPackage(serviceFlag,appPackageId, detail, forceUpgrade, confirmUpgrade, packageName, bundleIdentifier, upgradeTip, shutdownTip,emm3MamUrl,emm3TestUrl);

			// 添加动态
			App app = this.appService.findByAppVersion(pack.getAppVersionId());
			String dynamicType = "APP_PUBLISH_";

			if (pack.getOsType().compareTo(OSType.IOS) == 0) {
				dynamicType += "IOS_";
			} else
				dynamicType += "ANDROID_";

			if (pack.getBuildType().compareTo(AppPackageBuildType.TESTING) == 0) {
				dynamicType += "TEST_";
			} else {
				dynamicType += "";
			}
			dynamicType += "PACKAGE";
			DYNAMIC_MODULE_TYPE APPPACKAGETYPE = DYNAMIC_MODULE_TYPE.valueOf(dynamicType);
			this.dynamicService.addPrjDynamic(loginUserId, APPPACKAGETYPE, app.getProjectId(), new Object[] { app });

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 发布Widget包（EMM）
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
	@ResponseBody
	@RequestMapping(value = "/widget/publish", method=RequestMethod.POST)
	public Map<String, Object> publishWidget(
			@RequestParam(value="appWidgetId",required=false,defaultValue="0l") long appWidgetId,
			@RequestParam(value="detail", required=false) String detail,
			@RequestParam(value="productEnv", required=false,defaultValue="true") boolean productEnv,
			@RequestParam(value="terminalType", required=false) String terminalType,
			@RequestParam(value="forceUpgrade", required=false) IfStatus forceUpgrade,
			@RequestParam(value="confirmUpgrade", required=false) IfStatus confirmUpgrade,
			@RequestParam(value="packageName", required=false) String packageName,
			@RequestParam(value="bundleIdentifier", required=false) String bundleIdentifier,
			@RequestParam(value="upgradeTip", required=false) String upgradeTip,
			@RequestParam(value="shutdownTip", required=false) String shutdownTip,
			@RequestHeader(value="loginUserId", required=false) long loginUserId) {
		
		try {
			Map<String,Object> map = this.emmService.publishWidget(
					serviceFlag,appWidgetId, detail,productEnv,terminalType, forceUpgrade, confirmUpgrade,packageName, bundleIdentifier, upgradeTip, shutdownTip, loginUserId,emm3MamUrl,emm3TestUrl);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 补丁包发布时候,通过应用id获取该应用曾经打包使用过的渠道号列表
	 * @user jingjian.wu
	 * @date 2015年11月10日 上午10:36:18
	 */
	@ResponseBody
	@RequestMapping(value = "/channel/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getChannelByappId(@PathVariable(value="appId") long appId,
			@RequestHeader(value="loginUserId", required=false) long loginUserId){
		try {
			try {
				log.info("get patch channel-->appId:"+appId+",loginUserId:"+loginUserId);
				List<String> result = appService.getChannelListByAppId(appId);
				return this.getSuccessMap(result);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 发布补丁包
	 * @user jingjian.wu
	 * @date 2015年11月9日 下午10:38:24
	 */
	@ResponseBody
	@RequestMapping(value = "/patch/publish", method=RequestMethod.POST)
	public Map<String, Object> patchPackagePublish(
			@RequestParam(value="appPatchId") long appPatchId,
			@RequestParam(value="patchType") PATCH_TYPE patchType,
			@RequestParam(value="detail", required=false) String detail,
			@RequestParam(value="forceUpgrade", required=false) IfStatus forceUpgrade,
			@RequestParam(value="confirmUpgrade", required=false) IfStatus confirmUpgrade,
			@RequestParam(value="bundleIdentifier", required=false) String bundleIdentifier,
			@RequestParam(value="upgradeTip", required=false) String upgradeTip,
			@RequestParam(value="shutdownTip", required=false) String shutdownTip,
			@RequestParam(value="channelCode",required=false) String channelCode,
			@RequestParam(value="terminalType") String terminalType,
			@RequestParam(value="productEnv", required=false,defaultValue="true") boolean productEnv,
			@RequestHeader(value="loginUserId", required=false) long loginUserId) {
		
		try {
			Map<String,Object> map = null;
			if(patchType.equals(PATCH_TYPE.AppCanNative)){
				map = this.emmService.publishPackagePatch(appPatchId, detail, forceUpgrade, confirmUpgrade, bundleIdentifier, upgradeTip, shutdownTip,channelCode,terminalType,productEnv);
			}else{
				//不传渠道
				map = this.emmService.publishWidgetPatch(appPatchId, detail, forceUpgrade, confirmUpgrade, bundleIdentifier, upgradeTip, shutdownTip,terminalType,productEnv);
			}
			/*App app = this.appService.findByAppVersion(appVersionId);
			String dynamicType = "APP_PUBLISH_";
			
			if(terminalType.compareTo(TerminalType.ANDROID)==0){
				dynamicType += "IOS_";
			}else
				dynamicType += "ANDROID_";
			
			if(pack.getBuildType().compareTo(AppPackageBuildType.TESTING)==0){
				dynamicType += "TEST_";
			}else{
				dynamicType += "";
			}
			dynamicType += "PACKAGE";
			
			DYNAMIC_MODULE_TYPE APPPACKAGETYPE = DYNAMIC_MODULE_TYPE.valueOf(dynamicType);
			
			this.dynamicService.addPrjDynamic(loginUserId, APPPACKAGETYPE, app.getProjectId(), new Object[]{app});
			*/
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * EMM判断是否有测试环境
	 * @param args
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "isHaveTestEnv", method=RequestMethod.GET) 
	public Map<String, Object> isHaveTestEnv(@RequestHeader(value="loginUserId", required=false) long loginUserId){
		Map<String, Object> map=new HashMap<String,Object>();
		if(StringUtils.isNotBlank(emm3TestUrl)&&!emm3TestUrl.equals("${emm3TestUrl}")){
			return this.getSuccessMap(true);
		}else{
			 return this.getSuccessMap(false);
		}
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		Map<String,String> parameters = new HashMap<String,String>();
		String resultStr = HttpUtil.httpPost("http://192.168.1.224:8080/mam/app/getAppType",parameters);
//		String resultStr = HttpUtil.httpPost("http://192.168.1.198:8080/mam/app/getAppType",parameters);
		System.out.println(resultStr+"  =");
		
		/* resultStr = HttpUtil.httpPost("http://192.168.1.224:8080/mam/app/getAppGroupInfo",parameters);
		log.info(resultStr);
		
		parameters.put("appId", "123456");
		resultStr = HttpUtil.httpPost("http://192.168.1.224:8080/mam/app/validApp",parameters);
//		String resultStr = HttpUtil.httpPost("http://192.168.1.198:8080/mam/app/getAppType",parameters);
		log.info(resultStr);*/
	}
}
