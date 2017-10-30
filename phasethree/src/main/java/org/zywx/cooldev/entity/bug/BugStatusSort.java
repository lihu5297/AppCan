package org.zywx.cooldev.entity.bug;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.BUG_STATUS;
import org.zywx.cooldev.entity.BaseEntity;
/**
 * bug顺序移动
 * @author yongwen.wang
 * @date 2016-04-25
 */
@Entity
@Table(name= "T_BUG_STATUS_SORT")
public class BugStatusSort extends BaseEntity implements Cloneable {

	private static final long serialVersionUID = 1L;
	private long projectId;
	private long userId;
	private int sort;
	private BUG_STATUS status;
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public BUG_STATUS getStatus() {
		return status;
	}
	public void setStatus(BUG_STATUS status) {
		this.status = status;
	}
	
	
}
