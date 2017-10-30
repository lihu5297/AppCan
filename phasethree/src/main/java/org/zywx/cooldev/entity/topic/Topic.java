package org.zywx.cooldev.entity.topic;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "T_TOPIC")
public class Topic extends BaseEntity {
	private static final long serialVersionUID = -5650470744814280415L;
	
	private static SimpleDateFormat FORMATOR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//标题
	@Column(updatable=true,columnDefinition="longtext")
	private String title;
	//内容
	@Column(updatable=true,columnDefinition="longtext")
	private String detail;
	//用户主键
	private Long userId;
	//项目主键
	private Long projectId;

	@Column(name="title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name="detail",columnDefinition="text")
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@Column(name="userId",updatable=false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	//发起人昵称
	@Transient
	private String topicNickName;
	//发起人昵称
	@Transient
	private String topicIcon;

	//回复总数
	@Transient
	private int replyCounts;
	
	//回复人昵称
	@Transient
	private String replyNickName;
	//回复人主键
	@Transient
	private Long replyId;
	
	//最后回复时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	@Transient
	private Timestamp lastReplyTime;

	public String getTopicNickName() {
		return topicNickName;
	}

	public void setTopicNickName(String topicNickName) {
		this.topicNickName = topicNickName;
	}

	public int getReplyCounts() {
		return replyCounts;
	}

	public void setReplyCounts(int replyCounts) {
		this.replyCounts = replyCounts;
	}

	public String getReplyNickName() {
		return replyNickName;
	}

	public void setReplyNickName(String replyNickName) {
		this.replyNickName = replyNickName;
	}

	public String getLastReplyTimeStr() {
		return lastReplyTime == null ? null : FORMATOR.format(lastReplyTime);
	}

	public void setLastReplyTime(Timestamp lastReplyTime) {
		this.lastReplyTime = lastReplyTime;
	}
	
	public Timestamp getLastReplyTime() {
		return lastReplyTime;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getTopicIcon() {
		return topicIcon;
	}

	public void setTopicIcon(String topicIcon) {
		this.topicIcon = topicIcon;
	}

	@Override
	public String toString() {
		return this.title;
	}
	
}
