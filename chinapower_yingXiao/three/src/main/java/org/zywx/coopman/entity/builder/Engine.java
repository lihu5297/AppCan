package org.zywx.coopman.entity.builder;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.commons.Enums.EngineStatus;
import org.zywx.coopman.commons.Enums.EngineType;
import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.commons.Enums.UploadStatus;
import org.zywx.coopman.entity.BaseEntity;

/**
 * 
 * @author yang.li
 * @date 2015-09-12
 *
 */
@Entity
@Table(name="T_ENGINE")
public class Engine extends BaseEntity {

	private static final long serialVersionUID = -4598641119648691453L;

	/**
	 * 引擎版本 -> 说明文件version
	 */
	private String versionNo;

	/**
	 * 引擎描述 -> 说明文件description
	 */
	private String versionDescription;
	
	/**
	 * 引擎包描述 -> 说明文件package
	 */
	private String packageDescription;

	/**
	 * 操作系统类型
	 */
	private OSType osType;

	/**
	 * HTTP下载地址
	 */
	private String downloadUrl;

	/**
	 * 给打包接口提供的GIT仓库地址
	 */
	private String pkgGitRepoUrl;

	/**
	 * 引擎状态（启用/禁用）
	 */
	private EngineStatus status;

	/**
	 * 引擎类型（官方/内部/项目 ->PUBLIC/PRIVATE/PROJECT）
	 */
	private EngineType type;
	
	/**
	 * 上传处理状态（Success也包括git提交完毕）
	 */
	private UploadStatus uploadStatus;
	
	/**
	 * IDC存放路径
	 */
	private String filePath;
	/**
	 * 项目引擎所属的项目编号（默认值-1）
	 */
	private long projectId = -1;
	
	/**
	 * 项目引擎kenel(默认值system)
	 */
	private String kernel = "system";

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
	

}
