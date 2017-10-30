package org.zywx.cooldev.entity.task;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 任务标签实体
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_TASK_TAG")
public class TaskTag extends BaseEntity implements Cloneable{

	private static final long serialVersionUID = -2474784133259315623L;

	long taskId;
	long tagId;

	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public long getTagId() {
		return tagId;
	}
	public void setTagId(long tagId) {
		this.tagId = tagId;
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
