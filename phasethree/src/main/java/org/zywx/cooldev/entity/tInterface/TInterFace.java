package org.zywx.cooldev.entity.tInterface;


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
@Table(name="T_INTERFACE")
public class TInterFace extends BaseEntity {

	private static final long serialVersionUID = 1148934999815049435L;

	/**
	 * 发布人
	 */
	private long userId;
	
	/**
	 * 描述
	 */
	
	private String infDesc="";
	
	/**
	 * 数模文件
	 */
	
	private String infFile;
	
	/**
	 * 大项目id
	 */
	private long projectParentId;
	/**
	 * 子项目id
	 */
	private long projectId=0l;

	/**
	 * 状态 00有效，01无效
	 */
	private String infStatus;
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


	public String getInfDesc() {
		return infDesc;
	}


	public void setInfDesc(String infDesc) {
		this.infDesc = infDesc;
	}


	public String getInfFile() {
		return infFile;
	}


	public void setInfFile(String infFile) {
		this.infFile = infFile;
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


	public String getInfStatus() {
		return infStatus;
	}


	public void setInfStatus(String infStatus) {
		this.infStatus = infStatus;
	}

	
	
}
