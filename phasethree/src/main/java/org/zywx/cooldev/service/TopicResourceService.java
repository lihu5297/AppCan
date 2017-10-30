package org.zywx.cooldev.service;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.topic.TopicResource;

@Service
public class TopicResourceService extends BaseService{

	public TopicResource addResource(TopicResource topicResource) {
		topicResource = this.topicResourceDao.save(topicResource);
		Resource res = this.resourcesDao.findOne(topicResource.getResourceId());
		topicResource.setName(res.getName());
		return topicResource;
	}

	public void deleteTopicResource(Long id) {
		TopicResource tr = this.topicResourceDao.findOne(id);
		this.topicResourceDao.delete(tr.getId());
//		this.resourcesDao.delete(tr.getResourceId());
	}

}
