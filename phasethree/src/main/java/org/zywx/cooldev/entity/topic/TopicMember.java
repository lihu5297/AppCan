package org.zywx.cooldev.entity.topic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.TOPIC_MEMBER_TYPE;
import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_TOPIC_MEMBER")
public class TopicMember extends BaseEntity{
	
	private static final long serialVersionUID = -5908072235767938396L;

	//
	@Column(name="topicId")
	private Long topicId;
	
	//
	@Column(name="userId")
	private Long userId;

	//
	@Column(name="type")
	private TOPIC_MEMBER_TYPE type;

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public TOPIC_MEMBER_TYPE getType() {
		return type;
	}

	public void setType(TOPIC_MEMBER_TYPE type) {
		this.type = type;
	}
	
}
