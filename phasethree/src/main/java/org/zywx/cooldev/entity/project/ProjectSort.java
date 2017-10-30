package org.zywx.cooldev.entity.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_PROJECT_SORT")
public class ProjectSort extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	@Column(name="sort")
	private Long sort;         //排序字段
	
	@Column(name="projectId")
	private Long projectId;   //项目id
	
	@Column(name="userId")
	private Long userId;     //用户id
	
	public Long getSort() {
		return sort;
	}
	public void setSort(Long sort) {
		this.sort = sort;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
