package org.zywx.cooldev.entity.topic;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.Resource;

/**
 * 创建人:刘杰雄 <br>
 * 时间:2015年8月10日 上午10:09:36 <br>
 */
@Entity
@Table(name = "T_TOPIC_COMMENT")
public class TopicComment extends BaseEntity {

	private static final long serialVersionUID = 2248932602149043102L;

	@Column(name = "topicId")
	private Long topicId;

	@Column(name = "detail", columnDefinition = "text")
	private String detail;

	@Column(name = "userId")
	private Long userId;
	
	@Transient
	private List<Resource> topicResource;

	@Column(name = "replyTo")
	private Long replyTo = -1L;

	public Long getTopicId() {
		return topicId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Long getUserId() {
		return userId;
	}

	public Long getReplyTo() {
		return replyTo;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setReplyTo(Long replyTo) {
		this.replyTo = replyTo;
	}

	public List<Resource> getTopicResource() {
		return topicResource;
	}

	public void setTopicResource(List<Resource> topicResource) {
		this.topicResource = topicResource;
	}
	
	@Override
	public String toString() {
		return this.detail;
	}
	
}
