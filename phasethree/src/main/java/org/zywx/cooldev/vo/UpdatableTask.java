package org.zywx.cooldev.vo;

import org.zywx.cooldev.commons.Enums;

public class UpdatableTask {
	
	private long id;

	private String detail;	

	private long processId = -1;
	private long appId = -1;
	private int progress = -1;

	private Enums.TASK_REPEATABLE repeatable;
	private Enums.TASK_PRIORITY priority;
	private Enums.TASK_STATUS status;
	
	private String deadline;	// 来自表单
	
	private Long groupId;
	
	private float workHour;//工时
	
	

	public float getWorkHour() {
		return workHour;
	}

	public void setWorkHour(float workHour) {
		this.workHour = workHour;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public Enums.TASK_REPEATABLE getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(Enums.TASK_REPEATABLE repeatable) {
		this.repeatable = repeatable;
	}

	public Enums.TASK_PRIORITY getPriority() {
		return priority;
	}

	public void setPriority(Enums.TASK_PRIORITY priority) {
		this.priority = priority;
	}

	public Enums.TASK_STATUS getStatus() {
		return status;
	}

	public void setStatus(Enums.TASK_STATUS status) {
		this.status = status;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "UpdatableTask [id=" + id +  ", detail="
				+ detail + ", processId=" + processId + ", appId=" + appId
				+ ", progress=" + progress + ", repeatable=" + repeatable
				+ ", priority=" + priority + ", status=" + status
				+ ", deadline=" + deadline + "]";
//		return this.name;
	}
	
}
