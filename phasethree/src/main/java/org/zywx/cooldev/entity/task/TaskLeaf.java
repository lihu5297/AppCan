package org.zywx.cooldev.entity.task;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 任务实体
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_TASK_LEAF")
public class TaskLeaf extends BaseEntity implements Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2710001149955107336L;

	private String detail;
	
	private long processId = -1;

	private long appId = -1;


	private Enums.TASK_STATUS status = Enums.TASK_STATUS.NOFINISHED;
	
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
	private Timestamp finishDate;
	
	//父任务ID
	private long topTaskId;
	
	//负责人
	private long managerUserId;
	
	
	//---------------------transient-----------------------
	@Transient
	private String managerName;//子任务负责人姓名
	@Transient
	private String managerIcon;//子任务负责人头像
	
	
	//---------------------transient-----------------------
	

	

	public String getManagerName() {
		return managerName;
	}

	public Timestamp getLastStatusUpdateTime() {
		return lastStatusUpdateTime;
	}

	public void setLastStatusUpdateTime(Timestamp lastStatusUpdateTime) {
		this.lastStatusUpdateTime = lastStatusUpdateTime;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerIcon() {
		return managerIcon;
	}

	public void setManagerIcon(String managerIcon) {
		this.managerIcon = managerIcon;
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

	public long getFinishUserId() {
		return finishUserId;
	}

	public void setFinishUserId(long finishUserId) {
		this.finishUserId = finishUserId;
	}

	public Timestamp getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Timestamp finishDate) {
		this.finishDate = finishDate;
	}

	public long getTopTaskId() {
		return topTaskId;
	}

	public void setTopTaskId(long topTaskId) {
		this.topTaskId = topTaskId;
	}


	public long getManagerUserId() {
		return managerUserId;
	}

	public void setManagerUserId(long managerUserId) {
		this.managerUserId = managerUserId;
	}

	@Override
	public String toString(){
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


}
