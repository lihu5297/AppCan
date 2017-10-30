package org.zywx.cooldev.entity.task;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.Resource;

/**
 * 任务评论实体
 * @author yang.li
 * @date 2015-08-24
 * 
 */
@Entity
@Table(name="T_TASK_COMMENT")
public class TaskComment extends BaseEntity {

	private static final long serialVersionUID = -7646957540044432215L;

	@Lob
	@Column(columnDefinition="text",length = 65535)
	private String content;

	private long taskId;

	private long userId;
	
	/**
	 * 被回复的TaskComment的ID
	 */
	private long replyTo = -1;
	
	@Transient
	private String userName;
	
	@Transient
	private String userIcon;

	@Transient
	private List<Resource> resource;
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(long replyTo) {
		this.replyTo = replyTo;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}


	public List<Resource> getResource() {
		return resource;
	}

	public void setResource(List<Resource> resource) {
		this.resource = resource;
	}

}
