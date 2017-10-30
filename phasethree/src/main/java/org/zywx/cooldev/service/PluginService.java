package org.zywx.cooldev.service;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.PluginType;
import org.zywx.cooldev.commons.Enums.PluginVersionStatus;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.dao.builder.PluginDao;
import org.zywx.cooldev.dao.builder.PluginResourceDao;
import org.zywx.cooldev.dao.builder.PluginVersionDao;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.builder.Engine;
import org.zywx.cooldev.entity.builder.Plugin;
import org.zywx.cooldev.entity.builder.PluginCategory;
import org.zywx.cooldev.entity.builder.PluginResource;
import org.zywx.cooldev.entity.builder.PluginVersion;
import org.zywx.cooldev.thread.PushPluginEngineToQueueThread;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.vo.PushEntity;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import net.sf.json.JSONObject;

/**
 * 
 * @author yang.li
 * @date 2015-09-01
 *
 */
@Service
public class PluginService extends BaseService {
	
	@Value("${root.path}")
	private String baseDir;
	
	@Value("${picture.type}")
	private String pictureType;
	
	@Value("${file.type}")
	private String fileType;

	@Value("${office.supportTypes}")
	private String officeType;
	
	@Value("${root.path}")
	private String rootPath;
	
	@Value("${openoffice.host}")
	private String openofficeHost;
	
	@Value("${openoffice.port}")
	private String openofficePort;
	
	@Value("${office.destinateTypes}")
	private String officeDestinateTypes;
	
//	@Value("${file.destinateTypes}")
//	private String fileDestinateTypes;

	@Autowired
	private PluginDao pluginDao;
	@Autowired
	private PluginVersionDao pluginVersionDao;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private PluginResourceDao pluginResourceDao;
	
	@Value("${shellPath}")
	private String shellPath;
	
//	@Value("${appPackage.buildRepoPath}")
//	private String buildRepoPath;
	
	@Value("${gitShellServer}")
	private String gitShellServer;
	
	@Value("${gitShellEngineServer}")
	private String gitShellEngineServer;
	
	@Value("${xtGitHost}")
	private String xtGitHost;
	
	//***************************************************
	//    Plugin CRUD interfaces                        *
	//***************************************************
	/**
	 * 获取插件列表
	 * @param pageable
	 * @param loginUserId
	 * @param status 
	 * @return
	 */
	public Map<String, Object> getPlugList(Long engineId,Pageable pageable, Plugin match, long loginUserId, List<PluginVersionStatus> status,List<UploadStatus> uploadStatus) {
		if(match.getType() == null) {
			match.setType(PluginType.PROJECT);
		}
		Page<Plugin> pluginPage = null;
		long total = 0;
		if(match.getProjectId() != -1) {
			pluginPage = pluginDao.findByTypeAndProjectIdAndDel(pageable, match.getType(), match.getProjectId(), DELTYPE.NORMAL);
//			total = pluginDao.countByProjectIdAndDel(match.getProjectId() , DELTYPE.NORMAL);
			total = pluginPage.getTotalElements();
			List<Plugin> plugins = pluginPage.getContent();
			for(Plugin plugin : plugins){
				PluginCategory pluginCategory = this.pluginCategoryDao.findOne(plugin.getCategoryId());
				plugin.setCategoryName(pluginCategory.getName());
			}
		} else {
			pluginPage = pluginDao.findByTypeAndDel(pageable, match.getType(), DELTYPE.NORMAL);
//			total = pluginDao.countByDel(DELTYPE.NORMAL);
			total = pluginPage.getTotalElements();
			List<Plugin> plugins = pluginPage.getContent();
			for(Plugin plugin : plugins){
				PluginCategory pluginCategory = this.pluginCategoryDao.findOne(plugin.getCategoryId());
				plugin.setCategoryName(null!=pluginCategory && null!=pluginCategory.getName()?pluginCategory.getName():"");
			}
		}
		
		
//		message ：{
//		    list ：[
//		        {
//		            object : {},
//		            permission : {}
//		        },
//		        { }...
//		    ]，
//		    total : 100
//		}
		
		// 返回分页实体列表
		List<Map<String, Object>> list = new ArrayList<>();
		String required = (ENTITY_TYPE.PLUGIN + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		// 项目成员权限
		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,
				loginUserId,match.getProjectId());
		
		List<Plugin> pageContent = pluginPage.getContent();
		
		String engineVer="";
		if(null!=engineId){
			Engine engine = engineDao.findOne(engineId);
			engineVer = engine.getVersionNo().replace("sdksuit_", "").split("\\.")[0];//引擎大版本号
		}
		if(null!=pageContent){
			for (Plugin plugin : pageContent) {
				List<PluginVersion> versionList = pluginVersionDao.findByPluginIdAndStatusInAndUploadStatusInAndDel(plugin.getId(),status,uploadStatus, DELTYPE.NORMAL);
				if(versionList != null && versionList.size() >0 ) {
					if(null!=engineId){
						for(int i=0;i<versionList.size();i++){
							String currentVer = versionList.get(i).getVersionNo().split("\\.")[0];//插件大版本号
							if(Integer.parseInt(engineVer) <Integer.parseInt(currentVer)){
								System.out.println("============================================================");
								System.out.println(plugin.getEnName()+"============="+versionList.get(i).getVersionNo());
								versionList.remove(i);
								i--;
							}
						}
					}
					plugin.setPluginVersion(versionList);
				}
				if(null!=engineId && versionList.size()==0){//有engineId说明是打包时候选插件,打包时候如果没有版本就忽略此插件.
					total--;
					continue;
				}
				List<String> pListAsProjectMember = pMapAsProjectMember.get(plugin.getProjectId());
				Map<String, Integer> pMap = new HashMap<>();
				if (pListAsProjectMember != null) {
					for (String p : pListAsProjectMember) {
						pMap.put(p, 1);
					}
				}
				Map<String, Object> element = new HashMap<>();
				element.put("object", plugin);
				element.put("permissions", pMap);
				list.add(element);
			}
		}
		
		Map<String, Object> message = new HashMap<>();
		message.put("list", list);
		message.put("total", total);
		return message;
	}
	
	/**
	 * 获取插件详情
	 * @param pluginId
	 * @param loginUserId
	 * @param status 
	 * @return
	 */
	public Map<String, Object> getPlugin(long pluginId, long loginUserId, List<PluginVersionStatus> status) {
		
		Plugin plugin = pluginDao.findOne(pluginId);
		if(plugin != null) {
			List<UploadStatus> uploadStatus = new ArrayList<UploadStatus>();
			uploadStatus.add(UploadStatus.FAILED);
			uploadStatus.add(UploadStatus.SUCCESS);
			uploadStatus.add(UploadStatus.ONGOING);
			List<PluginVersion> versions = pluginVersionDao.findByPluginIdAndStatusInAndUploadStatusInAndDel(pluginId,status,uploadStatus, DELTYPE.NORMAL);
			if(versions != null) {
				plugin.setPluginVersion(versions);
			}
		}
		
		
		Map<String, Object> message = new HashMap<>();
		message.put("object", plugin);

		return message;
	}

	/**
	 * 添加插件
	 * @param plugin
	 * @param loginUserId
	 * @return
	 */
	public Plugin addPlugin (Plugin plugin, long loginUserId) {
		
		log.info("PluginService -> addPlugin -> request -> " + plugin.toStr());

		plugin.setType(PluginType.PROJECT); // 默认为项目插件
		
		List<Plugin> plugins = this.pluginDao.findByEnNameAndProjectIdAndTypeAndDel(plugin.getEnName(), plugin.getProjectId(), plugin.getType(),DELTYPE.NORMAL);
		
		if(!plugins.isEmpty()){
			plugin.setId(plugins.get(0).getId());
		}
		pluginDao.save(plugin);

		/*if(plugin.getAndroidVersion() != null && 
			plugin.getAndroidDownloadUrl() != null &&
			!"".equals(plugin.getAndroidVersion()) &&
			!"".equals(plugin.getAndroidDownloadUrl()) ) {

			List<PluginVersion> pluginVersions = this.pluginVersionDao.findByOsTypeAndPluginIdAndVersionNoAndDelOrderByIdDesc(OSType.ANDROID,plugin.getId(),plugin.getAndroidVersion(),DELTYPE.NORMAL);
			PluginVersion pv = new PluginVersion();
			if(pluginVersions.size()>0){
				pv = pluginVersions.get(0);
			}else{
				pv.setOsType(OSType.ANDROID);
				pv.setVersionDescription("首次发布");
			}
			pv.setPluginId(plugin.getId());
			pv.setVersionNo(plugin.getAndroidVersion());
			pv.setDownloadUrl(plugin.getAndroidDownloadUrl());
			pv.setAbsFilePath(plugin.getAndroidAbsFilePath());
			pv.setResPackageUrl(plugin.getAndroidOriResUrl());
			pv.setCustomResPackageUrl(plugin.getAndroidCusResUrl());
			pv.setUploadStatus(UploadStatus.ONGOING);
			this.addPluginVersion(pv, loginUserId);
		}
		
		if(plugin.getIosVersion() != null && 
			plugin.getIosDownloadUrl() != null &&
			!"".equals(plugin.getIosVersion()) &&
			!"".equals(plugin.getIosDownloadUrl()) ) {

			PluginVersion pv1 = new PluginVersion();
			List<PluginVersion> pluginVersions = this.pluginVersionDao.findByOsTypeAndPluginIdAndVersionNoAndDelOrderByIdDesc(OSType.IOS,plugin.getId(),plugin.getIosVersion(),DELTYPE.NORMAL);
			if(pluginVersions.size()>0){
				pv1 = pluginVersions.get(0);
			}else{
				pv1.setOsType(OSType.IOS);
				pv1.setVersionDescription("首次发布");
			}
			
			pv1.setPluginId(plugin.getId());
			pv1.setVersionNo(plugin.getIosVersion());
			pv1.setDownloadUrl(plugin.getIosDownloadUrl());
			pv1.setAbsFilePath(plugin.getIosAbsFilePath());
			pv1.setResPackageUrl(plugin.getIosOriResUrl());
			pv1.setCustomResPackageUrl(plugin.getIosCusResUrl());
			pv1.setUploadStatus(UploadStatus.ONGOING);
			this.addPluginVersion(pv1, loginUserId);
		}
		long timeEnd = System.currentTimeMillis();
		log.info("PluginService -> addPlugin -> totalTime -> " + (timeEnd - timeStart) +"ms");*/
		return plugin;

	}
	
	public List<PushEntity> addPluginStep2(Plugin plugin, long loginUserId){
		List<PushEntity> result = new ArrayList<PushEntity>();
		long timeStart = System.currentTimeMillis();
		if(plugin.getAndroidVersion() != null && 
				plugin.getAndroidDownloadUrl() != null &&
				!"".equals(plugin.getAndroidVersion()) &&
				!"".equals(plugin.getAndroidDownloadUrl()) ) {

				List<PluginVersion> pluginVersions = this.pluginVersionDao.findByOsTypeAndPluginIdAndVersionNoAndDelOrderByIdDesc(OSType.ANDROID,plugin.getId(),plugin.getAndroidVersion(),DELTYPE.NORMAL);
				PluginVersion pv = new PluginVersion();
				if(pluginVersions.size()>0){
					pv = pluginVersions.get(0);
				}else{
					pv.setOsType(OSType.ANDROID);
					pv.setVersionDescription("首次发布");
				}
				pv.setPluginId(plugin.getId());
				pv.setVersionNo(plugin.getAndroidVersion());
				pv.setDownloadUrl(plugin.getAndroidDownloadUrl());
				pv.setAbsFilePath(plugin.getAndroidAbsFilePath());
				pv.setResPackageUrl(plugin.getAndroidOriResUrl());
				pv.setCustomResPackageUrl(plugin.getAndroidCusResUrl());
				pv.setUploadStatus(UploadStatus.ONGOING);
//				this.addPluginVersion(pv, loginUserId);
				PushEntity pushEntity = this.addPluginVersion(pv, loginUserId);
				result.add(pushEntity);
			}
			
			if(plugin.getIosVersion() != null && 
				plugin.getIosDownloadUrl() != null &&
				!"".equals(plugin.getIosVersion()) &&
				!"".equals(plugin.getIosDownloadUrl()) ) {

				PluginVersion pv1 = new PluginVersion();
				List<PluginVersion> pluginVersions = this.pluginVersionDao.findByOsTypeAndPluginIdAndVersionNoAndDelOrderByIdDesc(OSType.IOS,plugin.getId(),plugin.getIosVersion(),DELTYPE.NORMAL);
				if(pluginVersions.size()>0){
					pv1 = pluginVersions.get(0);
				}else{
					pv1.setOsType(OSType.IOS);
					pv1.setVersionDescription("首次发布");
				}
				
				pv1.setPluginId(plugin.getId());
				pv1.setVersionNo(plugin.getIosVersion());
				pv1.setDownloadUrl(plugin.getIosDownloadUrl());
				pv1.setAbsFilePath(plugin.getIosAbsFilePath());
				pv1.setResPackageUrl(plugin.getIosOriResUrl());
				pv1.setCustomResPackageUrl(plugin.getIosCusResUrl());
				pv1.setUploadStatus(UploadStatus.ONGOING);
//				this.addPluginVersion(pv1, loginUserId);
				PushEntity pushEntity = this.addPluginVersion(pv1, loginUserId);
				result.add(pushEntity);
			}
			long timeEnd = System.currentTimeMillis();
			log.info("PluginService -> addPlugin -> totalTime -> " + (timeEnd - timeStart) +"ms");
			return result;
	}
	
	/**
	 * 编辑插件
	 * @param plugin
	 * @return
	 */
	public int editPlugin(Plugin plugin) {
		String setting = "";
		if(plugin.getEnName() != null) {
			setting += String.format(",enName='%s'", plugin.getEnName());
		}
		if(plugin.getCnName() != null) {
			setting += String.format(",cnName='%s'", plugin.getCnName());
		}
		if(plugin.getDetail() != null) {
			setting += String.format(",detail='%s'", plugin.getDetail());
		}
		if(plugin.getTutorial() != null) {
			setting += String.format(",tutorial='%s'", plugin.getTutorial());
		}
		if(plugin.getCategoryId() != 0 && plugin.getCategoryId() != -1) {
			setting += String.format(",categoryId=%d", plugin.getCategoryId());
		}
		if(setting.length() > 0) {
			setting = setting.substring(1);
		} else {
			return 0;
		}
		String sql = "update T_PLUGIN set " + setting + " where id=" + plugin.getId();
		return this.jdbcTpl.update(sql);
	}
	
	public void removePlugin(List<Long> pluginId) {
		String pluginIds = "";
		for(Long id : pluginId){
			pluginIds += ","+id;
		}
		String sql = "delete from T_PLUGIN_VERSION where pluginId in ("+pluginIds.substring(1)+")";
		this.jdbcTpl.update(sql);
		 sql = "delete from T_PLUGIN where id in("+pluginIds.substring(1)+")";
		this.jdbcTpl.update(sql);
	}
	
	public Map<String, Object> searchPlugin(Long engineId,String key, long projectId, PluginType type,List<UploadStatus> uploadStatus, long loginUserId) {
		String engineVer="";
		log.info("engineId===>"+engineId);
		if(null!=engineId){
			Engine engine = engineDao.findOne(engineId);
			engineVer = engine.getVersionNo().replace("sdksuit_", "").split("\\.")[0];//引擎大版本号
			log.info("engineVerNo====>"+engineVer);
		}
		List<Plugin> pList = pluginDao.searchPlugin("%" + key + "%", projectId, type);
		List<Map<String, Object>> list = new ArrayList<>();
		if(pList != null && pList.size() > 0) {
			for(Plugin p : pList) {
				List<PluginVersion> versionList = pluginVersionDao.findByPluginIdAndStatusAndUploadStatusInAndDel(p.getId(), PluginVersionStatus.ENABLE,uploadStatus, DELTYPE.NORMAL);
				if(versionList != null && versionList.size() >0 ) {
					if(null!=engineId){
						for(int i=0;i<versionList.size();i++){
							String currentVer = versionList.get(i).getVersionNo().split("\\.")[0];//插件大版本号
							log.info("================================================>"+currentVer);
							if(Integer.parseInt(engineVer) <Integer.parseInt(currentVer)){
								System.out.println("============================================================");
								System.out.println(p.getEnName()+"============="+versionList.get(i).getVersionNo());
								versionList.remove(i);
								i--;
							}
						}
					}
					p.setPluginVersion(versionList);
				}
				if(null!=engineId && versionList.size()==0){//有engineId说明是打包时候选插件,打包时候如果没有版本就忽略此插件.
					continue;
				}
				
				Map<String, Object> element = new HashMap<>();
				element.put("object", p);
				list.add(element);
			}
		}
		
		
//		message ：{
//		    list ：[
//		        {
//		            object : {},
//		            permission : {}
//		        },
//		        { }...
//		    ]，
//		    total : 100
//		}
		
		Map<String, Object> message = new HashMap<>();
		message.put("list", list);
		return message;
	}
	
	public Map<String, Object> searchPlugin(String key, long projectId, PluginType type,List<UploadStatus> uploadStatus, long loginUserId,Pageable page) {
		
		Page<Plugin> pages = pluginDao.searchPlugin("%" + key + "%", projectId, type,page);
		List<Plugin> pList = pages.getContent();
		List<Map<String, Object>> list = new ArrayList<>();
		
		if(pList != null && pList.size() > 0) {
			for(Plugin p : pList) {
				List<PluginVersion> versionList = pluginVersionDao.findByPluginIdAndStatusAndUploadStatusInAndDel(p.getId(), PluginVersionStatus.ENABLE,uploadStatus, DELTYPE.NORMAL);
				if(versionList != null && versionList.size() >0 ) {
					p.setPluginVersion(versionList);
				}
				PluginCategory pluginCategory = this.pluginCategoryDao.findOne(p.getCategoryId());
				p.setCategoryName(pluginCategory.getName());
			
				Map<String, Object> element = new HashMap<>();
				element.put("object", p);
				list.add(element);
			}
		}
		
		
//		message ：{
//		    list ：[
//		        {
//		            object : {},
//		            permission : {}
//		        },
//		        { }...
//		    ]，
//		    total : 100
//		}
		
		//Map<Long, List<String>>  permissions = this.projectService.permissionMapAsMemberWith(ENTITY_TYPE.PLUGIN+"_"+CRUD_TYPE.RETRIEVE, loginUserId);
		List<Permission>  permissions = this.projectService.getPermissionList(loginUserId, projectId);
		Map<String,Integer> permissionsMap = new HashMap<>();
		for(Permission per : permissions){
			permissionsMap.put(per.getEnName(), 1);
		}
		Map<String, Object> message = new HashMap<>();
		message.put("list", list);
		message.put("permissions", permissionsMap);
		message.put("total", pages.getTotalElements());
		return message;
	}
	
	//***************************************************
	//    PluginVersion CRUD interfaces                 *
	//***************************************************
	/**
	 * 插件版本列表
	 * @param pageable
	 * @param pluginId
	 * @param loginUserId
	 * @param osType
	 * @param status
	 * @return
	 */
	public List<PluginVersion> getPluginVersionList(Pageable pageable, Long pluginId, long loginUserId, List<OSType> osType, List<PluginVersionStatus> status) {
		List<PluginVersion> listPluginVersion = new ArrayList<PluginVersion>();
		if(pluginId == null) {
			listPluginVersion= pluginVersionDao.findByOsTypeInAndStatusInAndDelOrderByIdDesc(osType,status, DELTYPE.NORMAL);
		} else {
			listPluginVersion= pluginVersionDao.findByPluginIdAndOsTypeInAndStatusInAndDelOrderByIdDesc(pluginId, osType,status, DELTYPE.NORMAL);
		}
		
		return listPluginVersion;
		
	
	}
	
	public PluginVersion getPluginVersion(long id) {
		PluginVersion pv = pluginVersionDao.findOne(id);
		Plugin p = pluginDao.findOne(pv.getPluginId());
		pv.setPluginName(p.getCnName());
		pv.setPluginEnName(p.getEnName());
		pv.setPluginType(p.getType());
		pv.setPluginDescription(p.getDetail());
		return pv;
	}
	
	/**
	 * 添加插件版本
	 * @param version
	 * @param loginUserId
	 * @return
	 */
	public PushEntity addPluginVersion(PluginVersion version, long loginUserId) {
		List<PluginVersion> pluginVersions = this.pluginVersionDao.findByOsTypeAndPluginIdAndVersionNoAndDelOrderByIdDesc(version.getOsType(),version.getPluginId(),version.getVersionNo(),DELTYPE.NORMAL);
		if(pluginVersions.size() > 0) {
			// 覆盖已有插件版本
			PluginVersion pluginVersion = pluginVersions.get(0);
			pluginVersion.setDownloadUrl(version.getDownloadUrl());
			pluginVersion.setVersionDescription(version.getVersionDescription());
			pluginVersion.setAbsFilePath(version.getAbsFilePath());
			pluginVersion.setResPackageUrl(version.getResPackageUrl());
			version = pluginVersion;
		}
		
		// 新创建或者覆盖，都要重新上传仓库
		version.setUploadStatus(UploadStatus.ONGOING);
		
		pluginVersionDao.save(version);
		// 插件提交至GIT版本库
		
		
		Plugin plugin = pluginDao.findOne(version.getPluginId());
		if(plugin == null) {
			log.info("can not find plugin");
			return null;
		}
		
		String pushName = String.format("pluginVersion_%d_%s_%s_%s_%s",
				version.getId(), plugin.getType(), version.getOsType(), version.getVersionNo(), plugin.getEnName());
		
		//后台异步提交到git仓库
//		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(version.getDownloadUrl(), pushName, xtGitHost+"/plugin/status/"+version.getId(), gitShellEngineServer);
//		Thread thread = new Thread(pushthread);
//		thread.start();
		
		PushEntity pushPlugin = new PushEntity();
		pushPlugin.setDownLoadUrl(version.getDownloadUrl());
		pushPlugin.setPushName(pushName);
		pushPlugin.setEnginePluginId(version.getId().toString());
		return pushPlugin;
	
	}
	public void savePluginResourceToServer(String downLoadUrl,String gitRepoName,String id){
		//后台异步提交到git仓库
		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(downLoadUrl, gitRepoName, xtGitHost+"/plugin/pluginResource/status/"+id, gitShellEngineServer);
		Thread thread = new Thread(pushthread);
		thread.start();
	}
	public void saveToServer(PushEntity pushEntity){
		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(pushEntity.getDownLoadUrl(), pushEntity.getPushName(), xtGitHost+"/plugin/status/"+pushEntity.getEnginePluginId(), gitShellEngineServer);
		Thread thread = new Thread(pushthread);
		thread.start();
	}
	/**
	 * 提供给IDC机房git钩子回调结束后修改插件的上传状态
	 * @param relativeRepoPath
	 */
	public void updateVersionUploadStatus(Long versionId,String result) {
		PluginVersion pv = pluginVersionDao.findOne(versionId);
		if(null!=pv){
			if("FAILED".equals(result)){
				pv.setUploadStatus(UploadStatus.FAILED);
			}else{
				pv.setFilePath(result.replace("(", "/"));
				pv.setUploadStatus(UploadStatus.SUCCESS);
			}
			pluginVersionDao.save(pv);
		}else{
			log.info("GitAction push plugin failed for versionId->"+versionId);
		}

	}
	
	/**
	 * 移除插件版本
	 * @param versionId
	 * @param loginUserID
	 */
	public void removePluginVersion(long versionId, long loginUserID) {
	
		pluginVersionDao.delete(versionId);
	
	}

	/**
	 * 编辑插件版本
	 * @param version
	 * @param loginUserId
	 * @return
	 */
	public int editPluginVersion(PluginVersion version, long loginUserId) {
		String setting = "";
		if(version.getVersionDescription() != null) {
			setting += String.format(",versionDescription='%s'", version.getVersionDescription());
		}
		if(version.getDownloadUrl() != null) {
			setting += String.format(",downloadUrl='%s'", version.getDownloadUrl());
		}
		if(version.getResPackageUrl() != null) {
			setting += String.format(",resPackageUrl='%s'", version.getResPackageUrl());
		}
		if(version.getCustomDownloadUrl() != null) {
			setting += String.format(",customDownloadUrl='%s'", version.getCustomDownloadUrl());
		}
		if(version.getCustomResPackageUrl() != null) {
			setting += String.format(",customResPackageUrl='%s'", version.getCustomResPackageUrl());
		}
		
		
		if(version.getStatus() != null) {
			setting += String.format(",status=%d", version.getStatus().ordinal());
		}
		if(setting.length() > 0) {
			setting = setting.substring(1);
		} else {
			return 0;
		}
		String sql = "update T_PLUGIN_VERSION set " + setting + " where id=" + version.getId();
		return this.jdbcTpl.update(sql);
	}

	/**
	 * 为插件添加自定义资源包
	 * @param pr
	 * @param loginUserId
	 * @return
	 */
	public Map<String,String> addPluginResource(PluginResource pr, long loginUserId) {
		
		PluginVersion version = pluginVersionDao.findOne(pr.getPluginVersionId());
		
		if(version == null) {
			return null;
		}
		
		Plugin plugin = pluginDao.findOne(version.getPluginId());
		if(plugin == null) {
			return null;
		}
		
		// 判断是否已经存在自定义资源包
		PluginResource existed = pluginResourceDao.findOneByPluginVersionIdAndUserId(pr.getPluginVersionId(), loginUserId);

		if(existed == null) {
			existed = new PluginResource();
		}
		existed.setPluginVersionId(pr.getPluginVersionId());
		existed.setDownloadUrl( pr.getDownloadUrl() );
		existed.setAbsFilePath( pr.getAbsFilePath() );
		existed.setUserId(loginUserId);
		pluginResourceDao.save(existed);
		
		// 添加到git仓库
		/*String gitRepoName = String.format("pluginResource_%d_%d_%s_%s_%s_%s",
				existed.getId(), version.getId(), plugin.getType(), version.getOsType(), version.getVersionNo(), plugin.getEnName());
		
		String cmd = String.format("sh "+shellPath+"coopdev_git/add_file.sh %s %s", existed.getAbsFilePath(), gitRepoName);
		String ret = this.execShell(cmd);
		
		log.info(String.format("addPluginResource ->cmd[%s] ret[%s]", cmd, ret));*/
		String gitRepoName = String.format("pluginResource_%d_%d_%s_%s_%s_%s",
				existed.getId(), version.getId(), plugin.getType(), version.getOsType(), version.getVersionNo(), plugin.getEnName());
		
		
		//后台异步提交到git仓库
//		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(pr.getDownloadUrl(), gitRepoName, xtGitHost+"/plugin/pluginResource/status/"+existed.getId(), gitShellEngineServer);
//		Thread thread = new Thread(pushthread);
//		thread.start();
		Map<String,String> map = new HashMap<String,String>();
		map.put("downLoadUrl", pr.getDownloadUrl());
		map.put("gitRepoName", gitRepoName);
		map.put("id", existed.getId().toString());
		return map;
	}
	
	//***************************************************
	//    private Methods                               *
	//***************************************************
	/*private void pushToGitRepo(PluginVersion v) {
		//		# Created by yang.li 2015-09-12
		//		# Engines & Plugins stores to defualt git repository which named /000/000/000/builder.git
		//		# Specification for engine's name:
		//		#       Engine format: engine_{engineId}_{engineType}_{osType}_{version}_{enName}
		//		#       Plugin format: pluginVersion_{pluginVersionId}_{pluginType}_{osType}_{version}_{enName}
		//		#       English words & digits ONLY
		//		# Variables:
		//		#       $1 : engine file path on disk
		//		#       #2 : file's name
		
		long timeStart = System.currentTimeMillis();
		
		if(v == null || v.getAbsFilePath() == null) {
			return;
		}
		
		Plugin plugin = pluginDao.findOne(v.getPluginId());
		if(plugin == null) {
			return;
		}
		
		String gitRepoName = String.format("pluginVersion_%d_%s_%s_%s_%s",
				v.getId(), plugin.getType(), v.getOsType(), v.getVersionNo(), plugin.getEnName());
		
		String cmd = String.format("sh "+shellPath+"coopdev_git/add_file.sh %s %s %s", v.getAbsFilePath(), gitRepoName, buildRepoPath);
		this.execShell(cmd);
		v.setUploadStatus(UploadStatus.SUCCESS);
		pluginVersionDao.save(v);
		long timeEnd = System.currentTimeMillis();
		log.info("PluginService -> addPluginVersion ["+v.toStr()+"] to remoteRepository -> totalTime -> [" + ( timeEnd -  timeStart) +"ms]");
		
		log.info(" add plugin "+gitRepoName+" to git remote repository success ");
		return;
	}*/

	
	public Plugin findPlugin(long pluginId) {
		return this.pluginDao.findOne(pluginId);
	}

	public List<Plugin> findIdIn(List<Long> pluginId) {
		List<Plugin> list = (List<Plugin>) this.pluginDao.findAll(pluginId);
		return list;
	}

	public Plugin getPlugin(long pluginId) {
		return this.findPlugin(pluginId);
	}

	
	public Map<String, Object> preViewResource(long loginUserId, String sessionId, long pluginId) {
		HashMap<String,Object> map = new HashMap<>();
		Plugin plugin = this.findPlugin(pluginId);
		if(null==plugin || plugin.getDel().equals(DELTYPE.DELETED)){
			return this.getFailedMap("资源不存在");
		}
		//预览前 做资源权限校验
//		boolean isOwnerPermissions = this.getResoucePermissions(ENTITY_TYPE.RESOURCE+"_"+CRUD_TYPE.RETRIEVE,pluginId,loginUserId);
//		if(!isOwnerPermissions){
//			return this.getFailedMap("没有查看该资源的权限");
//		}
		
		if(null== plugin.getTutorial() || plugin.getTutorial().equals("")){
			return this.getFailedMap("该插件没有助手手册");
		}
		
		String pluginTutorialType = plugin.getTutorial().substring(plugin.getTutorial().lastIndexOf("."));
		
		String path = plugin.getTutorial().substring(plugin.getTutorial().lastIndexOf("normalFile"));
		
		String filenameOrg = plugin.getTutorial().substring(plugin.getTutorial().lastIndexOf("/")+1);
		
		String filename = filenameOrg.replace(pluginTutorialType, "");
		//图片原始路径
		String filePath = baseDir +"/"+ path;
		
//   	/mnt/glfs/coopDevelopment_private/normalFile/152/1451502321705.zip
		File tutorialFile = new File(filePath);
		long fileSize = tutorialFile.length();
		if(fileSize/(1024*1024)>4 || (fileSize%(1024*1024)>0 && fileSize/(1024*1024)==4)){
			map.put("status", "failed");
			map.put("message", "资源文件超过4M暂不支持预览");
			map.put("action", "download");
			map.put("downloadURI", plugin.getTutorial());
			map.put("filename", filenameOrg);
			return this.getFailedMap(map);
		}
		
		try{
			//图片预览
			String[] picture = pictureType.split(";");
			for(String pic : picture){
				if(pluginTutorialType.toUpperCase().equals(pic)){
					
					//预览路径
					long time = System.currentTimeMillis();
					String preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+pic.toLowerCase();
					
					File resourceViewPath = new File(preViewPath);
					if(!resourceViewPath.exists()){
						resourceViewPath.mkdirs();
					}
					
					String cmd = "sh " + shellPath + "/coopdev_res/copyRes.sh " + filePath + " " + preViewPath;
					String result = this.execShell(cmd);
					log.info("copy file " + filePath +" to " + preViewPath + "result:"+result);
					
					map.put("time", time+"");
					map.put("type", pic.toLowerCase());
					map.put("oldType", pluginTutorialType);
					map.put("filename", filename);
					return this.getSuccessMap(map);
				}
			}
			
			//文本预览
			String[] txtFile = fileType.split(";");
			for(String txt : txtFile){
				if(pluginTutorialType.toUpperCase().equals(txt)){
					//预览路径
					long time = System.currentTimeMillis();
					String preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+txt.toLowerCase();
					
					File resourceViewPath = new File(preViewPath);
					if(!resourceViewPath.exists()){
						resourceViewPath.mkdirs();
					}
					
					String cmd = "sh " + shellPath + "/coopdev_res/copyRes.sh " + filePath + " " + preViewPath;
					String result = this.execShell(cmd);
					log.info("copy file " + filePath +" to " + preViewPath + "result:"+result);
					
					map.put("time", time+"");
					map.put("type", txt.toLowerCase());
					map.put("oldType", pluginTutorialType);
					map.put("filename", filename);
					return this.getSuccessMap(map);
				}
			}
			
			//office预览
			String[] officeFile = officeType.split(";");
			for(String office : officeFile){
				if(pluginTutorialType.toUpperCase().equals(office)){
					OpenOfficeConnection connection = null;
					try{
						connection = new SocketOpenOfficeConnection(openofficeHost, Integer.parseInt(openofficePort));
						connection.connect();
					}catch(ConnectException e){
						log.info("文件转换出错，请检查OpenOffice服务是否启动。");
					    e.printStackTrace();
					    
					    log.info("尝试重新启动服务");
					    String cmd = "sh " + shellPath + "/coopdev_res/rebootOpenOfficeServices.sh "+ openofficeHost +" " + openofficePort;
						String result = this.execShell(cmd);
						log.info("尝试重新启动服务结果："+result);
					    
					}
					
					DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
					
					//预览路径
					long time = System.currentTimeMillis();
					String preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+officeDestinateTypes.toLowerCase();
					File resourceViewPath = new File(preViewPath);
					if(!resourceViewPath.exists()){
						resourceViewPath.mkdirs();
					}
					File oldFile = new File(filePath);
					File newFile = new File(preViewPath+"/"+filename+officeDestinateTypes.toLowerCase());
					log.info("file:"+filePath+" convert to:"+newFile);
					converter.convert(oldFile, newFile);
					
					log.info("succeed converter file " + filePath +" to " + preViewPath+"/"+filename+officeDestinateTypes.toLowerCase());
					
					connection.disconnect();
					
					map.put("time", time+"");
					map.put("type", officeDestinateTypes.toLowerCase());
					map.put("oldType", pluginTutorialType);
					map.put("filename", filename);
					return this.getSuccessMap(map);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("文档资源预览失败");
		}
		return this.getFailedMap("该类型文件不支持预览");
	}

	/**
	 * 提供给IDC机房git钩子回调结束后修改插件资源的上传状态
	 * @param relativeRepoPath
	 */
	public void updatePluginResourceUploadStatus(Long pluginResourceId,String result) {
		PluginResource pr = pluginResourceDao.findOne(pluginResourceId);
		if(null!=pr){
			if("FAILED".equals(result)){
				pr.setUploadStatus(UploadStatus.FAILED);
			}else{
				pr.setFilePath(result.replace("(", "/"));
				pr.setUploadStatus(UploadStatus.SUCCESS);
			}
			pluginResourceDao.save(pr);
		}else{
			log.info("GitAction push pluginResource failed for pluginResourceId:->"+pluginResourceId);
		}

	}
}
