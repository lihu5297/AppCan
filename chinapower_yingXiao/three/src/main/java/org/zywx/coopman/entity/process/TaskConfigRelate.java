package org.zywx.coopman.entity.process;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年10月21日 下午5:16:30	<br>
 * 
 */
@Entity
@Table(name="T_MAN_TASK_CONFIG_RELATE")
public class TaskConfigRelate extends BaseEntity{
	

	private static final long serialVersionUID = 8744702585047950769L;

	private Long taskConfigId;

	private Long nextTaskId;

	public Long getTaskConfigId() {
		return taskConfigId;
	}

	public void setTaskConfigId(Long taskConfigId) {
		this.taskConfigId = taskConfigId;
	}

	public Long getNextTaskId() {
		return nextTaskId;
	}

	public void setNextTaskId(Long nextTaskId) {
		this.nextTaskId = nextTaskId;
	}

}
