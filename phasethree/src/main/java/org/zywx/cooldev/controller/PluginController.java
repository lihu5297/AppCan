package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.PluginType;
import org.zywx.cooldev.commons.Enums.PluginVersionStatus;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.builder.Plugin;
import org.zywx.cooldev.entity.builder.PluginCategory;
import org.zywx.cooldev.entity.builder.PluginResource;
import org.zywx.cooldev.entity.builder.PluginVersion;
import org.zywx.cooldev.service.PluginCategoryService;
import org.zywx.cooldev.service.PluginService;
import org.zywx.cooldev.thread.BuilderPushToGitRepo;
import org.zywx.cooldev.vo.PushEntity;


/**
 * 插件相关处理控制器
 * @author yang.li
 * @date 2015-08-12
 *
 */
@Controller
@RequestMapping(value = "/plugin")
public class PluginController extends BaseController {
	@Autowired
	private PluginService pluginService;
	@Autowired
	private PluginCategoryService pluginCategoryService;

	/**
	 * 插件列表
	 * @param plugin
	 * @param pageNo
	 * @param pageSize
	 * @param status
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> getPluginList(Plugin plugin,
			@RequestParam(value="pageNo", required=false) Integer pageNo,
			@RequestParam(value="pageSize", required=false) Integer pageSize,
			@RequestParam(value="status", required=false) List<PluginVersionStatus> status,
			@RequestParam(value="uploadStatus",required=false) List<UploadStatus> uploadStatus,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="engineId", required=false)Long engineId) {
		log.info("engineId-->"+engineId);
		pageNo = ( pageNo == null ? 1 : pageNo );
		pageSize = ( pageSize == null ? 15 : pageSize );
		
		if(null==status || status.size()<1){
			status = new ArrayList<PluginVersionStatus>();
			status.add(PluginVersionStatus.DISABLE);
			status.add(PluginVersionStatus.ENABLE);
			
		}
		if(null==uploadStatus || uploadStatus.size()<1){
			uploadStatus = new ArrayList<UploadStatus>();
			uploadStatus.add(UploadStatus.ONGOING);
			uploadStatus.add(UploadStatus.SUCCESS);
			uploadStatus.add(UploadStatus.FAILED);
		}
		Pageable pageable = new PageRequest(pageNo-1, pageSize, Direction.DESC, "id");
		Map<String, Object> message = pluginService.getPlugList(engineId,pageable, plugin, loginUserId,status,uploadStatus);
		
		return this.getSuccessMap(message);
	}
	
	@ResponseBody
	@RequestMapping(value="/{pluginId}", method=RequestMethod.GET)
	public Map<String, Object> getPlugin(@PathVariable(value="pluginId") long pluginId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(value="status", required=false) List<PluginVersionStatus> status) {
		
		try {
			
			if(null==status || status.size()<1){
				status = new ArrayList<PluginVersionStatus>();
				status.add(PluginVersionStatus.DISABLE);
				status.add(PluginVersionStatus.ENABLE);
				
			}
			
			Map<String, Object> map = pluginService.getPlugin(pluginId, loginUserId,status);
			if(map.get("object") != null) {
				Plugin plugin = (Plugin) map.get("object");
				String required = (ENTITY_TYPE.PLUGIN + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
				// 项目成员权限
				Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,
						loginUserId,plugin.getProjectId());
				if (null != pMapAsProjectMember && pMapAsProjectMember.containsKey(plugin.getProjectId())) {
					List<String> pListAsProjectMember = pMapAsProjectMember.get(plugin.getProjectId());
					Map<String, Integer> pMap = new HashMap<>();
					if (pListAsProjectMember != null) {
						for (String p : pListAsProjectMember) {
							pMap.put(p, 1);
						}
					}
					Map<String, Object> element = new HashMap<>();
					element.put("object",plugin);
					element.put("permissions", pMap);				
					return this.getSuccessMap(element);
				} else {
					return this.getFailedMap("not found Plugin with id=" + pluginId);
				}
			} else {
				return this.getFailedMap("not found Plugin with id=" + pluginId);
			}

		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}		
	}
	
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> addPlugin(
			Plugin plugin,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		try {

			pluginService.addPlugin(plugin, loginUserId);
			List<PushEntity> listPushEntity = pluginService.addPluginStep2(plugin, loginUserId);
			if(listPushEntity.size()>0){
				for(PushEntity pushEntity:listPushEntity){
					this.pluginService.saveToServer(pushEntity);
				}
			}
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_UPLOAD, plugin.getProjectId(), new Object[]{plugin});
			
			return this.getSuccessMap(plugin);

		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value="/{pluginId}", method=RequestMethod.PUT)
	public Map<String, Object> editPlugin(
			Plugin plugin,
			@PathVariable(value="pluginId") long pluginId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		try {
			Plugin pluginOld = this.pluginService.findPlugin(pluginId);
			plugin.setId(pluginId);
			int affected = pluginService.editPlugin(plugin);
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);
			
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_EDIT, pluginOld.getProjectId(), new Object[]{pluginOld});
			
			return this.getSuccessMap(affectedMap);

		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value="", method=RequestMethod.DELETE)
	public Map<String, Object> removePlugin(
			@RequestParam(value="pluginId") List<Long> pluginId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			if(null==pluginId || pluginId.size()==0){
				return this.getFailedMap("pluginId is empty!");
			}
			
			List<Plugin> pluginOld = this.pluginService.findIdIn(pluginId);
			
			pluginService.removePlugin(pluginId);
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			for(Plugin plugin : pluginOld){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_DELETE, plugin.getProjectId(), new Object[]{plugin});
			}
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}

	@ResponseBody
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public Map<String, Object> searchPlugin(
			@RequestParam(value="keyword") String keyword,
			@RequestParam(value="projectId") long projectId,
			@RequestParam(value="type") PluginType type,
			@RequestParam(value="uploadStatus",required=false) List<UploadStatus> uploadStatus,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="engineId", required=false) Long engineId) {
		
		if(null==uploadStatus || uploadStatus.size()<1){
			uploadStatus = new ArrayList<UploadStatus>();
			uploadStatus.add(UploadStatus.FAILED);
			uploadStatus.add(UploadStatus.SUCCESS);
			uploadStatus.add(UploadStatus.ONGOING);
		}
		Map<String, Object> message = this.pluginService.searchPlugin(engineId,keyword, projectId, type,uploadStatus, loginUserId);

		return this.getSuccessMap(message);
		
	}
	
	@ResponseBody
	@RequestMapping(value="/search/list", method=RequestMethod.GET)
	public Map<String, Object> searchPlugin(
			@RequestParam(value="keyword") String keyword,
			@RequestParam(value="projectId") long projectId,
			@RequestParam(value="type") PluginType type,
			@RequestParam(value="uploadStatus",required=false) List<UploadStatus> uploadStatus,
			HttpServletRequest request,
			@RequestHeader(value="loginUserId") long loginUserId) {
		Integer pageNo = 0;
		Integer pageSize = 20;
		try{
			if(null!=request.getAttribute("pageNo")){
				pageNo = Integer.parseInt(request.getAttribute("pageNo").toString())-1;
			}
			if(null!=request.getAttribute("pageSize")){
				pageSize = Integer.parseInt(request.getAttribute("pageSize").toString())-1;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		Pageable page = new PageRequest(pageNo,pageSize,new Sort(Direction.DESC,"id"));
		
		if(null==uploadStatus || uploadStatus.size()<1){
			uploadStatus = new ArrayList<UploadStatus>();
			uploadStatus.add(UploadStatus.FAILED);
			uploadStatus.add(UploadStatus.SUCCESS);
			uploadStatus.add(UploadStatus.ONGOING);
		}
		Map<String, Object> message = this.pluginService.searchPlugin(keyword, projectId, type,uploadStatus, loginUserId,page);
		
		return this.getSuccessMap(message);
		
	}
	

	@ResponseBody
	@RequestMapping(value="/version", method=RequestMethod.GET)
	public Map<String, Object> getPluginVersionList(
			HttpServletRequest request,
			@RequestParam(value="pluginId", required=false) Long pluginId,
			@RequestParam(value="osType", required=false) List<OSType> osType,
			@RequestParam(value="status", required=false) List<PluginVersionStatus> status,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			if(null == osType || osType.size()<1){
				osType = new ArrayList<>();
				osType.add(OSType.IOS);
				osType.add(OSType.ANDROID);
			}
			int pageNo       = 0;
			int pageSize     = 15;
			
			if(null==status || status.size()<1){
				status = new ArrayList<PluginVersionStatus>();
				status.add(PluginVersionStatus.DISABLE);
				status.add(PluginVersionStatus.ENABLE);
				
			}
			
			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
			List<PluginVersion> arr = pluginService.getPluginVersionList(pageable, pluginId, loginUserId,osType,status);
			List<Map<String, Object>> arrMap = new ArrayList<Map<String, Object>>();
			
			if(arr != null) {
				
				String required = (ENTITY_TYPE.PLUGIN + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
				for(PluginVersion pluginVersion : arr){
					Plugin plugin = (Plugin) this.pluginService.getPlugin(pluginVersion.getPluginId(), loginUserId,status).get("object");
					pluginVersion.setPluginName(plugin.getCnName());
					Map<String, Integer> pMap = new HashMap<>();
					// 项目成员权限
					Map<Long, List<String>> pMapAsProjectMember=null;
					if(plugin.getProjectId()!=-1){
						pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,
								loginUserId,plugin.getProjectId());
					}
					if (null != pMapAsProjectMember && pMapAsProjectMember.containsKey(plugin.getProjectId())) {
						List<String> pListAsProjectMember = pMapAsProjectMember.get(plugin.getProjectId());
						
						if (pListAsProjectMember != null) {
							for (String p : pListAsProjectMember) {
								pMap.put(p, 1);
							}
						}
						Map<String, Object> element = new HashMap<>();
						element.put("object",pluginVersion);
						element.put("permissions", pMap);	
						element.put("name", plugin.getEnName());
						arrMap.add(element);
					}
				}
			} else {
				return this.getFailedMap("not found Plugin with id=" + pluginId);
			}
			if(arrMap.size()==0){
				Map<String, Object> element = new HashMap<>();
				Plugin plugin1 = this.pluginService.findPlugin(pluginId);
				element.put("name",plugin1.getEnName());
				arrMap.add(element);
			}
			return this.getSuccessMap( arrMap );
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/version/{pluginVersionId}", method=RequestMethod.GET)
	public Map<String, Object> getPluginVersion(@PathVariable(value="pluginVersionId") long pluginVersionId) {
		PluginVersion pv = this.pluginService.getPluginVersion(pluginVersionId);
		Map<String, Object> map = new HashMap<>();
		map.put("object", pv);
		return this.getSuccessMap(map);
	}

	@ResponseBody
	@RequestMapping(value="/version", method=RequestMethod.POST)
	public Map<String, Object> addPluginVersion(
			PluginVersion version,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		
		try {
			PushEntity pushEntity = this.pluginService.addPluginVersion(version, loginUserId);
			if(null!=pushEntity){
				this.pluginService.saveToServer(pushEntity);
			}
			
			if(null==version.getId()){//这一步需要获取version的id,如果是更新版本的话,version的id为Null,需要重新获取
				version = this.pluginService.getPluginVersion(Long.parseLong(pushEntity.getEnginePluginId()));
			}
			Plugin plugin = this.pluginService.getPlugin(version.getPluginId());
			if(version.getOsType().compareTo(OSType.IOS)==0){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_EDIT_IOS_VERSION, plugin.getProjectId(), new Object[]{plugin,version});
			}else
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_EDIT_ANDROID_VERSION, plugin.getProjectId(), new Object[]{plugin,version});
			return this.getSuccessMap(version);
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}


	@ResponseBody
	@RequestMapping(value="/version/{pluginVersionId}", method=RequestMethod.DELETE)
	public Map<String, Object> removePluginVersion(
			@PathVariable(value="pluginVersionId") long pluginVersionId,
			@RequestHeader(value="loginUserId", required=true) long loginUserId) {
		
		try {
			
			PluginVersion pluginVersion = this.pluginService.getPluginVersion(pluginVersionId);
			
			pluginService.removePluginVersion(pluginVersionId, loginUserId);
			
			Plugin plugin = this.pluginService.getPlugin(pluginVersion.getPluginId());
			if(pluginVersion.getOsType().compareTo(OSType.IOS)==0){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_DELETE_IOS_VERSION, plugin.getProjectId(), new Object[]{plugin,pluginVersion});
			}else
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_DELETE_ANDROID_VERSION, plugin.getProjectId(), new Object[]{plugin,pluginVersion});
			
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/version", method=RequestMethod.PUT)
	public Map<String, Object> editPluginVersion(
			PluginVersion version,
			@RequestHeader(value="loginUserId", required=true) long loginUserId) {
		
		try {
			
			int a = pluginService.editPluginVersion(version, loginUserId);
		
			PluginVersion pluginVersion = this.pluginService.getPluginVersion(version.getId());
			Plugin plugin = this.pluginService.getPlugin(pluginVersion.getPluginId());
			if(version.getStatus().compareTo(PluginVersionStatus.ENABLE)==0){
				if(pluginVersion.getOsType().compareTo(OSType.IOS)==0){
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_ENABLE_IOS_VERSION, plugin.getProjectId(), new Object[]{plugin,pluginVersion});
				}else
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_ENABLE_ANDROID_VERSION, plugin.getProjectId(), new Object[]{plugin,pluginVersion});
				
			}else{
				if(pluginVersion.getOsType().compareTo(OSType.IOS)==0){
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_DISABLE_IOS_VERSION, plugin.getProjectId(), new Object[]{plugin,pluginVersion});
				}else
					this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PLUGIN_DISABLE_ANDROID_VERSION, plugin.getProjectId(), new Object[]{plugin,pluginVersion});
				
			}
				
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", a);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/resource", method=RequestMethod.POST)
	public Map<String, Object> addPluginResource(
			PluginResource resource,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		
		try {
			log.info("PluginResource ->" + resource);
			
//			PluginResource saved = this.pluginService.addPluginResource(resource, loginUserId);
//			return this.getSuccessMap(saved);
			Map<String,String> map = this.pluginService.addPluginResource(resource, loginUserId);
			if(null!=map){
				this.pluginService.savePluginResourceToServer(map.get("downLoadUrl").toString(), map.get("gitRepoName").toString(), map.get("id").toString());
			}
			return this.getSuccessMap("");
		} catch (Exception e) {
			
			log.error(ExceptionUtils.getFullStackTrace(e));;
			
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/category", method=RequestMethod.GET)
	public ModelAndView category(
			@RequestHeader(value="loginUserId", required=true) long loginUserId) {
		
		try {
			List<PluginCategory> list =this.pluginCategoryService.getCategory();
			
			return this.getSuccessModel(list);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}

	}
	
	/**
	 * 
	 * @describe 插件助手手册预览	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年12月28日 上午11:33:06	<br>
	 * @param loginUserId
	 * @param sessionId
	 * @param resourceId
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@RequestMapping(value="/preview/{pluginId}",method=RequestMethod.GET)
	public Map<String,Object> preViewResource(@RequestHeader(value="loginUserId")long loginUserId,
			@RequestParam(value="sessionId")String sessionId,
			@PathVariable(value="pluginId")long pluginId){
		try{
			Map<String,Object> map = this.pluginService.preViewResource(loginUserId,sessionId,pluginId);			
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("预览失败："+e.getMessage());
		}
	}
	
	/**
	 * 修改插件状态为更新完成接口
	 * @param versionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/status/{versionId}",method=RequestMethod.GET)
	public Map<String, Object> notifyGitRepoPushed(@PathVariable(value="versionId") long  versionId,String result) {
		try {
			log.info("gitAction update version status :for Id->"+versionId);
			this.pluginService.updateVersionUploadStatus(versionId,result);
			return this.getSuccessMap("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	
	/**
	 * 修改插件状态为更新完成接口
	 * @param versionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/pluginResource/status/{pluginResourceId}",method=RequestMethod.GET)
	public Map<String, Object> updatePluginResourcePushed(@PathVariable(value="pluginResourceId") long  pluginResourceId,String result) {
		try {
			log.info("gitAction update pluginResource status :for Id->"+pluginResourceId);
			this.pluginService.updatePluginResourceUploadStatus(pluginResourceId,result);
			return this.getSuccessMap("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
