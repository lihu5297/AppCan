package org.zywx.cooldev.entity.bug;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * bug模块
 * @author yongwen.wang
 * @date 2016-04-20
 * 
 */
@Entity
@Table(name = "T_BUG_MODULE")
public class BugModule extends BaseEntity implements Cloneable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private long projectId;
	private long managerId;
	private long creatorId;
	@Transient
	private String managerName;
	@Transient
	private String managerIcon;
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
	public String getManagerName() {
		return managerName;
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
	public long getManagerId() {
		return managerId;
	}
	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}
	public long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}
	@Override
	public String toString(){
		return this.name;
	}
	

}
