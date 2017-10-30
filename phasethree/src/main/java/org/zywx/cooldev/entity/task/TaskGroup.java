package org.zywx.cooldev.entity.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 任务实体
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_TASK_GROUP")
public class TaskGroup extends BaseEntity implements Cloneable{


	private static final long serialVersionUID = -1158305696338516511L;

	@Column(name="name")
	private String name;
	
	@Column(name="projectId")
	private long projectId;
	
	@Column(name="sort")
	private int sort;

	@Transient
	private int noFinishedTaskSum;
	@Transient
	private int finishTaskTotal;
	
	public int getFinishTaskTotal() {
		return finishTaskTotal;
	}


	public void setFinishTaskTotal(int finishTaskTotal) {
		this.finishTaskTotal = finishTaskTotal;
	}

	@Transient
	private int allTaskSum;
	
	private String pinYinHeadChar;
    
	private String pinYinName;

	
	
	public String getPinYinHeadChar() {
		return pinYinHeadChar;
	}


	public void setPinYinHeadChar(String pinYinHeadChar) {
		this.pinYinHeadChar = pinYinHeadChar;
	}


	public String getPinYinName() {
		return pinYinName;
	}


	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
	}


	public int getNoFinishedTaskSum() {
		return noFinishedTaskSum;
	}


	public void setNoFinishedTaskSum(int noFinishedTaskSum) {
		this.noFinishedTaskSum = noFinishedTaskSum;
	}


	public int getAllTaskSum() {
		return allTaskSum;
	}


	public void setAllTaskSum(int allTaskSum) {
		this.allTaskSum = allTaskSum;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public long getProjectId() {
		return projectId;
	}


	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}


	public int getSort() {
		return sort;
	}


	public void setSort(int sort) {
		this.sort = sort;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
