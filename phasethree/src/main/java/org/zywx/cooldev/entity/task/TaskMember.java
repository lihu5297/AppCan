package org.zywx.cooldev.entity.task;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 任务成员实体
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_TASK_MEMBER")
public class TaskMember extends BaseEntity implements Cloneable{

	private static final long serialVersionUID = -3024156769861697018L;

	private long taskId = -1;
	
	private long userId = -1;
	
	private Enums.TASK_MEMBER_TYPE type = Enums.TASK_MEMBER_TYPE.PARTICIPATOR;
	
	@Transient
	private String userName;
	
	@Transient
	private String userIcon = "";
	
	@Transient
	private String userAccount = "";
	
	@Transient
	private List<String> role = null;
	
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

	public Enums.TASK_MEMBER_TYPE getType() {
		return type;
	}

	public void setType(Enums.TASK_MEMBER_TYPE type) {
		this.type = type;
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

	public List<String> getRole() {
		return role;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}


	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	@Override
	public String toString() {
		return "TaskMember [taskId=" + taskId + ", userId=" + userId
				+ ", type=" + type + ", userName=" + userName + ", userIcon="
				+ userIcon + ", userAccount=" + userAccount + ", role=" + role
				+ "]";
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
