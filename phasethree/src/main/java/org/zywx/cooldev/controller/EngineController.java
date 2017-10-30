package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.EngineStatus;
import org.zywx.cooldev.commons.Enums.EngineType;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.builder.Engine;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.service.EngineService;
import org.zywx.cooldev.vo.PushEntity;


/**
 * 引擎相关处理控制器
 * @author yang.li
 * @date 2015-08-12
 *
 */
@Controller
@RequestMapping(value = "/engine")
public class EngineController extends BaseController {
	@Autowired
	private EngineService engineService;


	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> getEngineList(
			@RequestParam(value="projectId",required=false) Long projectId,
			@RequestParam(value="osType",required=false) List<OSType> osType,
			@RequestParam(value="pageNo",required=false) Integer ipageNo,
			@RequestParam(value="pageSize",required=false) Integer ipageSize,
			@RequestParam(value="status",required=false) List<EngineStatus> status,
			@RequestParam(value="uploadStatus",required=false) List<UploadStatus> uploadStatus,
			@RequestParam(value="type",required=false) List<EngineType> type,
			@RequestParam(value="keyWords",required=false,defaultValue="") String keyWords,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			if(null == osType || osType.size()<1){
				osType = new ArrayList<>();
				osType.add(OSType.IOS);
				osType.add(OSType.ANDROID);
			}
			if(null == type || type.size()<1){
				type = new ArrayList<>();
				type.add(EngineType.PRIVATE);
				type.add(EngineType.PROJECT);
				type.add(EngineType.PUBLIC);
			}
			if(null==uploadStatus || uploadStatus.size()<1){
				uploadStatus = new ArrayList<UploadStatus>();
				uploadStatus.add(UploadStatus.ONGOING);
				uploadStatus.add(UploadStatus.SUCCESS);
				uploadStatus.add(UploadStatus.FAILED);
			}
			int pageNo       = 0;
			int pageSize     = 15;
			if(null!=ipageNo){
				pageNo = ipageNo;
				pageSize = ipageSize;
			}
			if(null==status || status.size()<1){
				status = new ArrayList<EngineStatus>();
				status.add(EngineStatus.DISABLE);
				status.add(EngineStatus.ENABLE);
				
			}
			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
			List<Engine> arr = engineService.getEngineList(pageable, loginUserId, projectId, osType,type,status,uploadStatus,"%"+keyWords+"%");
			
			// 返回分页实体列表
			List<Map<String, Object>> list = new ArrayList<>();
			String required = (ENTITY_TYPE.ENGINE + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
			// 项目成员权限
			Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,
					loginUserId,projectId);
			if(null!=arr){
				for (Engine engine : arr) {					
					List<String> pListAsProjectMember = pMapAsProjectMember.get(engine.getProjectId());
					Map<String, Integer> pMap = new HashMap<>();
					if (pListAsProjectMember != null) {
						for (String p : pListAsProjectMember) {
							pMap.put(p, 1);
						}
					}
					Map<String, Object> element = new HashMap<>();
					element.put("object", engine);
					element.put("permissions", pMap);
					list.add(element);
				}
			}
			
			Map<String, Object> element = new HashMap<>();
			if(null != projectId && projectId !=-1L){
				Project pro = this.projectService.getProject(projectId);
				element.put("project", pro);
			}
			List<List<String>> pListAsProjectMember = new ArrayList<List<String>>();
			if(null!=projectId){
				pListAsProjectMember.add(pMapAsProjectMember.get(projectId));
			}else{
				Iterator<List<String>> it = pMapAsProjectMember.values().iterator();
				while(it.hasNext()){
					pListAsProjectMember.add(it.next());
				}
			}
			
			HashMap<String,Long> map = new HashMap<>();
			if(null!=pListAsProjectMember){
				for(List<String> lis : pListAsProjectMember){
					if(null!=lis){
						for(String str : lis){
							map.put(str, 1L);
						}
					}
				}
			}
			element.put("permission", map);
			element.put("object", list);
			return this.getSuccessMap( element );
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}
	

	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> addEngine(
			Engine engine,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			PushEntity pushEngine = this.engineService.addEngine(engine, loginUserId);
			if(null!=pushEngine){
				this.engineService.saveEngineToServer(pushEngine);
			}
			if(engine.getOsType().compareTo(OSType.IOS)==0){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.ENGINE_UPLOAD_IOS, engine.getProjectId(), new Object[]{engine});
			}else
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.ENGINE_UPLOAD_ANDROID, engine.getProjectId(), new Object[]{engine});
			
			return this.getSuccessMap(engine);
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}
	

	@ResponseBody
	@RequestMapping(value="/{engineId}", method=RequestMethod.PUT)
	public Map<String, Object> editEngine(
			Engine engine,
			@PathVariable(value="engineId") long engineId,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			engine.setId(engineId);
			int affected = engineService.editEngine(engine, loginUserId);
		
			Map<String, Integer> affectedMap = new HashMap<>();
			affectedMap.put("affected", affected);
			Engine engineNew = this.engineService.getEngineExist(engineId);
			
			String dynamicType = "ENGINE_";
			if(engine.getStatus()!=null){
				if(engine.getStatus().compareTo(EngineStatus.ENABLE)==0){
					dynamicType += "ENABLE_";
				}else
					dynamicType += "DISABLE_";
				
				if(engineNew.getOsType().compareTo(OSType.IOS)==0){
					dynamicType += "IOS";
				}else
					dynamicType += "ANDROID";
				
				DYNAMIC_MODULE_TYPE DYNAMICTYPE = DYNAMIC_MODULE_TYPE.valueOf(dynamicType);
				
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMICTYPE, engineNew.getProjectId(), new Object[]{engineNew});
			}
			
			
			return this.getSuccessMap(affectedMap);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	@ResponseBody
	@RequestMapping(value="/{engineId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeEngine(
			@PathVariable(value="engineId") long engineId,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			Engine engineNew = this.engineService.getEngineExist(engineId);
			
			engineService.removeEngine(engineId, loginUserId);
		
			if(engineNew.getOsType().compareTo(OSType.IOS)==0){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.ENGINE_REMOVE_IOS, engineNew.getProjectId(), new Object[]{engineNew});
			}else
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.ENGINE_REMOVE_ANDROID, engineNew.getProjectId(), new Object[]{engineNew});
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}

	}
	
		/**
	 * 修改引擎状态为更新完成接口
	 * @param engineId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/status/{engineId}",method=RequestMethod.GET)
	public Map<String, Object> notifyGitRepoPushed(@PathVariable(value="engineId") long  engineId,String result) {
		try {
			log.info("gitAction update engine status :for Id->"+engineId);
			this.engineService.updateUploadStatus(engineId,result);
			return this.getSuccessMap("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	

}
