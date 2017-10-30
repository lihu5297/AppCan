package org.zywx.cooldev.dao.topic;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.topic.TopicComment;

public interface TopicCommentDao extends PagingAndSortingRepository<TopicComment, Serializable>{
    @Query(nativeQuery=true,value="select * from T_TOPIC_COMMENT where del=?1 and topicId in (select id from T_TOPIC where del=?1 and projectId=?2)")
	List<TopicComment> getTopicCommentByProjectId(int ordinal, long projectId);

}
