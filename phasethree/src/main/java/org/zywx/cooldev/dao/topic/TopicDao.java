package org.zywx.cooldev.dao.topic;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.topic.Topic;

public interface TopicDao extends PagingAndSortingRepository<Topic, Long>{

	List<Topic> findByProjectIdAndDel(long projectId, DELTYPE normal);
    @Query(nativeQuery=true,value="select cast(id as char) from T_TOPIC where del=?3 and id in (select topicId from T_TOPIC_MEMBER where del=?3 and userId=?2) and projectId=?1")
	List<String> getTopicsByProjectIdAndUserIdAndDel(long projectId,
			long userId, int ordinal);
}
