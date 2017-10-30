package org.zywx.cooldev.entity.task;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.TASK_GROUP;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年12月28日 下午4:06:34	<br>
 * 
 */
@Entity
@Table(name="T_TASK_GROUP_SORT")
public class TaskGroupSort extends BaseEntity{

	private static final long serialVersionUID = 2808556031315550908L;
	
	private Long projectId;//项目
	
	private Long userId;//用户
	
	private Long groupId;//任务分组
	
	private Long sort;//排序

	
	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}
}