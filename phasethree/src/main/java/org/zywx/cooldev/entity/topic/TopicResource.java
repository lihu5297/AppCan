package org.zywx.cooldev.entity.topic;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月31日 下午4:49:03	<br>
 * 
 */
@Entity
@Table(name="T_TOPIC_RESOURCE")
public class TopicResource extends BaseEntity{

	private static final long serialVersionUID = -5951696567921120096L;
	
	private Long topicCId;
	
	private Long resourceId;
	
	@Transient
	private String name;

	public Long getTopicCId() {
		return topicCId;
	}

	public void setTopicCId(Long topicCId) {
		this.topicCId = topicCId;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
