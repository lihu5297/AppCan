package org.zywx.cooldev.entity.builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.EngineStatus;
import org.zywx.cooldev.commons.Enums.EngineType;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_ENGINE")
public class Engine extends BaseEntity {

	private static final long serialVersionUID = -4598641119648691453L;

	private String versionNo;
	private String versionDescription;
	private String packageDescription;
	private OSType osType;
	private String downloadUrl;
	/**
	 * 引擎类型（前端服务生成仅为项目引擎）
	 */
	@Column(updatable=false)
	private EngineType type = EngineType.PROJECT;
	private EngineStatus status;
	/**
	 * 项目引擎所属的项目编号（不可变更）
	 */
	@Column(updatable=false)
	private long projectId = -1;
	/**
	 * 项目引擎内核
	 */
	@Column
	private String kernel="system";
	/**
	 * 给打包接口提供的GIT仓库地址
	 */
	private String pkgGitRepoUrl;
	
	/**
	 * IDC存储路径
	 */
	private String filePath;

	@Transient
	private String absFilePath;
	
	/**
	 * 上传处理状态（Success也包括git提交完毕）
	 */
	private UploadStatus uploadStatus;
	
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public String getVersionDescription() {
		return versionDescription;
	}
	public void setVersionDescription(String versionDescription) {
		this.versionDescription = versionDescription;
	}
	public OSType getOsType() {
		return osType;
	}
	public void setOsType(OSType osType) {
		this.osType = osType;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public EngineStatus getStatus() {
		return status;
	}
	public void setStatus(EngineStatus status) {
		this.status = status;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public EngineType getType() {
		return type;
	}
	public void setType(EngineType type) {
		this.type = type;
	}
	public String getPkgGitRepoUrl() {
		return pkgGitRepoUrl;
	}
	public void setPkgGitRepoUrl(String pkgGitRepoUrl) {
		this.pkgGitRepoUrl = pkgGitRepoUrl;
	}
	public String getAbsFilePath() {
		return absFilePath;
	}
	public void setAbsFilePath(String absFilePath) {
		this.absFilePath = absFilePath;
	}

	public String getPackageDescription() {
		return packageDescription;
	}
	public void setPackageDescription(String packageDescription) {
		this.packageDescription = packageDescription;
	}
	public UploadStatus getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(UploadStatus uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public String getKernel() {
		return kernel;
	}
	public void setKernel(String kernel) {
		this.kernel = kernel;
	}
	@Override
	public String toString(){
		return versionNo;
		
	}
}
