package org.zywx.cooldev.dao.topic;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.topic.TopicResource;

public interface TopicResourceDao extends PagingAndSortingRepository<TopicResource, Serializable>{

	@Query(nativeQuery=true,value="select t.id id,t.resourceId resourceId,t.topicCId topicCId,r.name name from T_TOPIC_RESOURCE t left join T_RESOURCES r on t.resourceId = r.id where t.topicCId = ?1 ")
	List<Object[]> findByTopicCId(Long topicCId);

}
