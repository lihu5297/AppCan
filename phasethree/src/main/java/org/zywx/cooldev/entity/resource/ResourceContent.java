package org.zywx.cooldev.entity.resource;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 资源管理
 * @author 东元
 *
 */
@Entity
@Table(name = "T_MAN_RESOURCE_CONTENT")
public class ResourceContent  extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String resName;		//资源名称
	private String resVersion;	//版本
	private String resDesc;		//描述
	private long resType;		//资源类别
	private String creator;		//创建者
	private String filialeIds;	//所属网省公司的ID,以逗号分隔
	
	@Transient
	private String typeName;
	@Transient
	private List<Long> fileIds;
	@Transient
	private List<ResourceFileInfo> fileList;
	@Transient
	private List<Long> chenckProvince;
	
	public String getResName() {
		return resName;
	}
	public void setResName(String resName) {
		this.resName = resName;
	}
	public String getResVersion() {
		return resVersion;
	}
	public void setResVersion(String resVersion) {
		this.resVersion = resVersion;
	}
	public String getResDesc() {
		return resDesc;
	}
	public void setResDesc(String resDesc) {
		this.resDesc = resDesc;
	}
	public long getResType() {
		return resType;
	}
	public void setResType(long resType) {
		this.resType = resType;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public List<ResourceFileInfo> getFileList() {
		return fileList;
	}
	public void setFileList(List<ResourceFileInfo> fileList) {
		this.fileList = fileList;
	}
	public List<Long> getFileIds() {
		return fileIds;
	}
	public void setFileIds(List<Long> fileIds) {
		this.fileIds = fileIds;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getFilialeIds() {
		return filialeIds;
	}
	public void setFilialeIds(String filialeIds) {
		this.filialeIds = filialeIds;
	}
	public List<Long> getChenckProvince() {
		return chenckProvince;
	}
	public void setChenckProvince(List<Long> chenckProvince) {
		this.chenckProvince = chenckProvince;
	}
	
	
}
