package org.zywx.coopman.entity.process;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.commons.Enums.TASK_STATUS;
import org.zywx.coopman.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年10月21日 下午4:21:05	<br>
 * 
 */
@Entity
@Table(name="T_MAN_TASK_CONFIG")
public class TaskConfig extends BaseEntity{

	private static final long serialVersionUID = 103108570224056521L;

	private String name;
	
	/**
	 * 该任务对应的操作结果状态
	 */
	private TASK_STATUS status;
		
	@Transient
	private List<TaskConfig> preTasks;
	
	@Transient
	private List<TASK_STATUS> curStatus;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TASK_STATUS getStatus() {
		return status;
	}

	public void setStatus(TASK_STATUS status) {
		this.status = status;
	}

	public List<TaskConfig> getPreTasks() {
		return preTasks;
	}

	public void setPreTasks(List<TaskConfig> preTasks) {
		this.preTasks = preTasks;
	}

	public List<TASK_STATUS> getCurStatus() {
		return curStatus;
	}

	public void setCurStatus(List<TASK_STATUS> curStatus) {
		this.curStatus = curStatus;
	}
	
}
