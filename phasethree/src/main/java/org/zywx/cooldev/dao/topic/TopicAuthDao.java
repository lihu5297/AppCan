package org.zywx.cooldev.dao.topic;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.topic.TopicAuth;

public interface TopicAuthDao extends PagingAndSortingRepository<TopicAuth, Serializable>{

	List<TopicAuth> findByMemberIdAndDel(Long memberId, DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_TOPIC_AUTH where del=?1 and memberId in (select id from T_TOPIC_MEMBER where del=?1 and topicId in (select id from T_TOPIC where del=?1 and projectId=?2))")
	List<TopicAuth> getTopicAuthByProjectId(int ordinal, long projectId);

}
