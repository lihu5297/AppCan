package org.zywx.cooldev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.service.EntityService;

/**
 * 实体相关功能处理<br>
 * 1. 为实体增加资源关联
 * @author yang.li
 * @date 2015-08-12
 *
 */
@Controller
@RequestMapping(value = "/entity")
public class EntityController extends BaseController {
	@Autowired
	private EntityService entityService;

	@ResponseBody
	@RequestMapping(value="/resource", method=RequestMethod.POST)
	public Map<String, Object> addResourceToTask(
			@RequestParam(value="entityId") long entityId,
			@RequestParam(value="entityType") ENTITY_TYPE entityType,
			@RequestParam(value="resourceIdList") List<Long> resourceIdList,
			@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="createBugOrTask",required=false) String createBugOrTask) {
		log.info("-----------------come into EntityResource POST:entityType="+entityType+",entityId="+entityId+",resourceIdList="+resourceIdList);
		for(long resourceId : resourceIdList) {
			EntityResourceRel rel = new EntityResourceRel();
			rel.setEntityId(entityId);
			rel.setEntityType(entityType);
			rel.setResourceId(resourceId);
			entityService.addEntityResourceRel(rel, loginUserId);
			
			try {
				if(createBugOrTask!=null&&createBugOrTask.equals("YES")){
					
				}else{
					Resource resource = this.resourcesService.findOne(resourceId);
					//申请子项目时，上传关联资源时还没有创建项目,所以当子项目关联资源上传成功、项目创建成功后关联资源时，更新资源表中的项目ID。
					if(entityType.ordinal() == ENTITY_TYPE.PROJECT.ordinal()){
						resource.setProjectId(entityId);
						resourcesService.addResources(resource);
					}
					this.entityService.addEntityObjectDynamic(loginUserId,entityType,entityId,resource);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Map<String, Integer> affected = new HashMap<>();
		affected.put("affected", resourceIdList.size());
		
		return this.getSuccessMap(affected);
		
	}
	
	@ResponseBody
	@RequestMapping(value="/resource", method=RequestMethod.DELETE)
	public Map<String, Object> removeResourceFromTask(
			@RequestParam(value="entityId") long entityId,
			@RequestParam(value="entityType") ENTITY_TYPE entityType,
			@RequestParam(value="resourceId") long resourceId,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		Resource resource = this.resourcesService.findOne(resourceId);
		
		entityService.removeEntityResourceRel(entityId, entityType, resourceId);
		
		this.entityService.addEntityObjectRemoveDynamic(loginUserId,entityType,entityId,resource);
		
		Map<String, Integer> affected = new HashMap<>();
		affected.put("affected", 1);
		
		return this.getSuccessMap(affected);
	}

}
