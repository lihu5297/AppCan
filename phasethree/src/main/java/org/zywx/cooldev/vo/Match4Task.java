package org.zywx.cooldev.vo;

import java.util.List;

import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;

public class Match4Task {

	private long projectId = -1;
	private long appId = -1;
	private List<Long> processId;
	private List<TASK_MEMBER_TYPE> memberType;
	private long sortNum = -1;

	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public long getAppId() {
		return appId;
	}
	public void setAppId(long appId) {
		this.appId = appId;
	}
	public List<Long> getProcessId() {
		return processId;
	}
	public void setProcessId(List<Long> processId) {
		this.processId = processId;
	}
	public List<TASK_MEMBER_TYPE> getMemberType() {
		return memberType;
	}
	public void setMemberType(List<TASK_MEMBER_TYPE> memberType) {
		this.memberType = memberType;
	}
	public long getSortNum() {
		return sortNum;
	}
	public void setSortNum(long sortNum) {
		this.sortNum = sortNum;
	}
	@Override
	public String toString() {
		return "Match4Task [projectId=" + projectId + ", appId=" + appId
				+ ", processId=" + processId + ", memberType=" + memberType
				+ "]";
	}
}
