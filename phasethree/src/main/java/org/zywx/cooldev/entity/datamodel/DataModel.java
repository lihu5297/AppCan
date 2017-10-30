package org.zywx.cooldev.entity.datamodel;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.Resource;


/**
 * 数模表
 * @author zhouxx
 * 20170808
 *
 */
@Entity
@Table(name="T_DATAMODEL")
public class DataModel extends BaseEntity {

	private static final long serialVersionUID = 1148934999815049435L;

	/**
	 * 发布人
	 */
	private long userId;
	
	/**
	 * 数模描述
	 */
	
	private String dmDesc="";
	
	/**
	 * 数模文件
	 */
	
	private String file;
	
	/**
	 * 大项目id
	 */
	private long projectParentId;
	/**
	 * 子项目id
	 */
	private long projectId;

	/**
	 * 状态 00有效，01无效
	 */
	private String dmStatus;
	/**
	 * 资源
	 */
	@Transient
	private List<Resource> resource;
	
	public List<Resource> getResource() {
		return resource;
	}


	public void setResource(List<Resource> resource) {
		this.resource = resource;
	}


	public long getUserId() {
		return userId;
	}


	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getFile() {
		return file;
	}


	public void setFile(String file) {
		this.file = file;
	}


	public long getProjectParentId() {
		return projectParentId;
	}


	public void setProjectParentId(long projectParentId) {
		this.projectParentId = projectParentId;
	}


	public long getProjectId() {
		return projectId;
	}


	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}


	public String getDmDesc() {
		return dmDesc;
	}


	public void setDmDesc(String dmDesc) {
		this.dmDesc = dmDesc;
	}


	public String getDmStatus() {
		return dmStatus;
	}


	public void setDmStatus(String dmStatus) {
		this.dmStatus = dmStatus;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	
	
}
