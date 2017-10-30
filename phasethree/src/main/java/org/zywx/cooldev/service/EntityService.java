package org.zywx.cooldev.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.dao.EntityResourceRelDao;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskComment;
import org.zywx.cooldev.entity.topic.Topic;
import org.zywx.cooldev.entity.topic.TopicComment;

/**
 * 
 * @author yang.li
 * @date 2015-09-01
 *
 */
@Service
public class EntityService extends BaseService {
	@Autowired
	private EntityResourceRelDao entityResourceRelDao;
	@Autowired
	private DynamicService dynamicService;
	
	public List< Map<String, Object> > getEntityResourceRelList(long entityId, ENTITY_TYPE entityType) {
		List< Map<String, Object> > message = new ArrayList<>();
		
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(entityId, entityType, DELTYPE.NORMAL);
		
		for(EntityResourceRel rel : relList) {
			Map<String, Object> element = new HashMap<>();
			element.put("object", rel);
			message.add(element);
			
		}
		return message;
	}

	public Map<String, Object> getEntityResourceRel(long relId, long loginUserId) {

		EntityResourceRel rel = entityResourceRelDao.findOne(relId);

		Map<String, Object> message = new HashMap<>();
		message.put("object", rel);

		return message;
	}
	

	public EntityResourceRel addEntityResourceRel(EntityResourceRel rel, long loginUserId) {
		entityResourceRelDao.save(rel);
		return rel;
	}
	
	public void removeEntityResourceRel(long entityId, ENTITY_TYPE entityType, long resourceId) {
		entityResourceRelDao.removeByEntityIdAndEntityTypeAndResourceIdAndDel(entityId, entityType, resourceId, DELTYPE.NORMAL);
	}

	
	public void addEntityObjectDynamic(long loginUserId,ENTITY_TYPE entityType, long entityId, Resource resource) {
		log.info("addEntityObjectDynamic loginUserId:"+loginUserId+", entityType:"+entityType+", resource:"+resource);
		switch(entityType){
			case TASK:
				Task task = this.taskDao.findOne(entityId);
				org.zywx.cooldev.entity.process.Process process = this.processDao.findOne(task.getProcessId());
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_ADD_RESOURCE, process.getProjectId(), new Object[]{task,resource});
				break;
			case PROCESS:
				org.zywx.cooldev.entity.process.Process proces = this.processDao.findOne(entityId);
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROCESS_ADD_RESOURCE, proces.getProjectId(), new Object[]{proces,resource});
				break;
			case TASK_COMMENT:
				TaskComment taskComment = this.taskCommentDao.findOne(entityId);
				Task tas = this.taskDao.findOne(taskComment.getTaskId());
				org.zywx.cooldev.entity.process.Process proce = this.processDao.findOne(tas.getProcessId());
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_COMMENT_ADD_RESOURCE, proce.getProjectId(), new Object[]{tas,resource}); ;
				break;
			case COMMENT:
				TopicComment topicComment = this.topicCommentDao.findOne(entityId);
				Topic topic = this.topicDao.findOne(topicComment.getTopicId());
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.COMMENT_ADD_RESOURCE, topic.getProjectId(), new Object[]{topic,resource}); ;
				break;
			case BUG:
				Bug bug = this.bugDao.findOne(entityId);
				org.zywx.cooldev.entity.process.Process process1 = this.processDao.findOne(bug.getProcessId());
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.BUG_ADD_RESOURCE, process1.getProjectId(), new Object[]{bug,resource});
				break;
			default:
				break;
		}
	}

	public void addEntityObjectRemoveDynamic(long loginUserId, ENTITY_TYPE entityType, long entityId,
			Resource resource) {
		log.info("addEntityObjectRemoveDynamic loginUserId:"+loginUserId+", entityType:"+entityType+",entityId:"+entityId+", resource:"+resource);
		switch(entityType){
			case TASK:
				Task task = this.taskDao.findOne(entityId);
				org.zywx.cooldev.entity.process.Process process = this.processDao.findOne(task.getProcessId());
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_REMOVE_RESOURCE, process.getProjectId(), new Object[]{task,resource});
				break;
			case PROCESS:
				org.zywx.cooldev.entity.process.Process proces = this.processDao.findOne(entityId);
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROCESS_REMOVE_RESOURCE, proces.getProjectId(), new Object[]{proces,resource});
				break;
			case TASK_COMMENT:
				TaskComment taskComment = this.taskCommentDao.findOne(entityId);
				Task tas = this.taskDao.findOne(taskComment.getTaskId());
				org.zywx.cooldev.entity.process.Process proce = this.processDao.findOne(tas.getProcessId());
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_COMMENT_REMOVE_RESOURCE, proce.getProjectId(), new Object[]{tas,resource}); ;
				break;
			case COMMENT:
				TopicComment topicComment = this.topicCommentDao.findOne(entityId);
				Topic topic = this.topicDao.findOne(topicComment.getTopicId());
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.COMMENT_REMOVE_RESOURCE, topic.getProjectId(), new Object[]{topic,resource}); ;
				break;
			default:
				break;
		}
		
	}
	

}
