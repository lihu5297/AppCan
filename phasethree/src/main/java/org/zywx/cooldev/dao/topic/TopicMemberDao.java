package org.zywx.cooldev.dao.topic;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TOPIC_MEMBER_TYPE;
import org.zywx.cooldev.entity.topic.TopicMember;

public interface TopicMemberDao extends PagingAndSortingRepository<TopicMember, Long>{

	TopicMember findByTopicIdAndUserIdAndDel(Long topicId, Long loginUserId, int val);

	TopicMember findByTopicIdAndUserIdAndDel(Long topicId, Long userId, DELTYPE normal);

	TopicMember findByTopicIdAndUserIdAndTypeAndDel(Long topicId, Long userId, TOPIC_MEMBER_TYPE actor, DELTYPE normal);

	List<TopicMember> findByUserIdAndDel(Long loginUserId, DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_TOPIC_MEMBER where del=?1 and topicId in (select id from T_TOPIC where del=?1 and projectId=?2)")
	List<TopicMember> getTopicCommentByProjectId(int ordinal, long projectId);
}
