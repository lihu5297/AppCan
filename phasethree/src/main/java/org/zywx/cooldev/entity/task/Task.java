package org.zywx.cooldev.entity.task;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 任务实体
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_TASK")
public class Task extends BaseEntity implements Cloneable{

	private static final long serialVersionUID = -616224357183141330L;

	
	@Column(columnDefinition="longtext")
	private String detail;
	
	private long processId = -1;

	private long appId = -1;

	private Enums.TASK_REPEATABLE repeatable = Enums.TASK_REPEATABLE.NONE;
	
	private Enums.TASK_PRIORITY priority = Enums.TASK_PRIORITY.NORMAL;

	private Enums.TASK_STATUS status = Enums.TASK_STATUS.NOFINISHED;
	
	private float workHour =0;//工时
	
	
	
	public float getWorkHour() {
		return workHour;
	}

	public void setWorkHour(float workHour) {
		this.workHour = workHour;
	}

	/**
	 * 修改任务状态的时候,需要修改此值
	 * 前台需要根据此时间,判断任务是在延期之前完成的,还是延期了才完成
	 */
	private Timestamp lastStatusUpdateTime = new Timestamp(System.currentTimeMillis());
	

	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	@Column(columnDefinition="date")
	private Date deadline;
	
	
	//任务完成人
	private long finishUserId = -1;
	
	//完成时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	@Column(columnDefinition="date")
	private Date finishDate;
	
	
	//任务分组
	private long groupId;
	
	@Transient
	private String groupName;
	
	
	
	@Transient
	private int resourceTotal;
	
	@Transient
	private int commentTotal;
	
	@Transient
	private String processName = "";
	
	@Transient
	private String projectName = "";
	
	@Transient
	private String processEndTime;
	
	@Transient
	private String appName = "";
	
	@Transient
	private long projectId;
	
	@Transient
	private List<String> tag;
	
	@Transient
	private List<TaskComment> comment;
	
	@Transient
	private List<Resource> resource;
	
	@Transient
	private TaskMember leader;
	
	//获取任务详情的时候,需要任务的创建者,以及创建者头像信息等
	@Transient
	private User creator;
	
	@Transient
	private List<TaskMember> member;
	
	
	@Transient
	private List<TaskLeaf> taskLeafList;
	
	@Transient
	private int noFinishedTaskLeafSum;//此任务下未完成子任务数量
	
	@Transient
	private int allTaskLeafSum;//此任务下所有子任务数量
	@Transient
	private long count;
	//统计任务优先级
	@Transient
	private String priorityString;
	
	
	public String getPriorityString() {
		return priorityString;
	}

	public void setPriorityString(String priorityString) {
		this.priorityString = priorityString;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getNoFinishedTaskLeafSum() {
		return noFinishedTaskLeafSum;
	}

	public void setNoFinishedTaskLeafSum(int noFinishedTaskLeafSum) {
		this.noFinishedTaskLeafSum = noFinishedTaskLeafSum;
	}

	public int getAllTaskLeafSum() {
		return allTaskLeafSum;
	}

	public void setAllTaskLeafSum(int allTaskLeafSum) {
		this.allTaskLeafSum = allTaskLeafSum;
	}

	public List<TaskLeaf> getTaskLeafList() {
		return taskLeafList;
	}

	public void setTaskLeafList(List<TaskLeaf> taskLeafList) {
		this.taskLeafList = taskLeafList;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
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


	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public int getResourceTotal() {
		return resourceTotal;
	}

	public void setResourceTotal(int resourceTotal) {
		this.resourceTotal = resourceTotal;
	}

	public int getCommentTotal() {
		return commentTotal;
	}

	public void setCommentTotal(int commentTotal) {
		this.commentTotal = commentTotal;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public List<String> getTag() {
		return tag;
	}

	public void setTag(List<String> tag) {
		this.tag = tag;
	}

	public List<TaskComment> getComment() {
		return comment;
	}

	public void setComment(List<TaskComment> comment) {
		this.comment = comment;
	}

	public List<Resource> getResource() {
		return resource;
	}

	public void setResource(List<Resource> resource) {
		this.resource = resource;
	}

	public TaskMember getLeader() {
		return leader;
	}

	public void setLeader(TaskMember leader) {
		this.leader = leader;
	}

	public List<TaskMember> getMember() {
		return member;
	}

	public void setMember(List<TaskMember> member) {
		this.member = member;
	}
		
	public Timestamp getLastStatusUpdateTime() {
		return lastStatusUpdateTime;
	}

	public void setLastStatusUpdateTime(Timestamp lastStatusUpdateTime) {
		this.lastStatusUpdateTime = lastStatusUpdateTime;
	}

	@Override
	public String toString(){
		/*if(StringUtils.isNotBlank(this.detail)){
			if(this.detail.length()>25){
				return this.detail.substring(0, 20)+"...";
			}
		}*/
		return this.detail;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		try {   
            return super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }  
	}

	public String getProcessEndTime() {
		return processEndTime;
	}

	public void setProcessEndTime(String processEndTime) {
		this.processEndTime = processEndTime;
	}



	public long getFinishUserId() {
		return finishUserId;
	}

	public void setFinishUserId(long finishUserId) {
		this.finishUserId = finishUserId;
	}



	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


}
