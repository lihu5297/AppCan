package org.zywx.coopman.entity.builder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.commons.Enums.PluginVersionStatus;
import org.zywx.coopman.commons.Enums.UploadStatus;
import org.zywx.coopman.entity.BaseEntity;

@Entity
@Table(name="T_PLUGIN_VERSION")
public class PluginVersion extends BaseEntity {

	private static final long serialVersionUID = -7912729372456770287L;

	private String versionNo;
	
	private String versionDescription;
	
	private String downloadUrl;
	
	private String resPackageUrl;
	
	private String customDownloadUrl;
	
	private String customResPackageUrl;
	
	private long pluginId;
	
	private OSType osType;
	/**
	 * IDC存放路径
	 */
	private String filePath;
	
	/**
	 * 上传处理状态（Success也包括git提交完毕）
	 */
	private UploadStatus uploadStatus;
	
	@Transient
	private String pluginName;
	
	private PluginVersionStatus status = PluginVersionStatus.ENABLE;
	
	

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPluginName() {
		return pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
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

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public long getPluginId() {
		return pluginId;
	}

	public void setPluginId(long pluginId) {
		this.pluginId = pluginId;
	}


	public OSType getOsType() {
		return osType;
	}

	public void setOsType(OSType osType) {
		this.osType = osType;
	}

	public PluginVersionStatus getStatus() {
		return status;
	}

	public void setStatus(PluginVersionStatus status) {
		this.status = status;
	}

	
	public String getResPackageUrl() {
		return resPackageUrl;
	}

	public void setResPackageUrl(String resPackageUrl) {
		this.resPackageUrl = resPackageUrl;
	}

	public String getCustomDownloadUrl() {
		return customDownloadUrl;
	}

	public void setCustomDownloadUrl(String customDownloadUrl) {
		this.customDownloadUrl = customDownloadUrl;
	}

	public String getCustomResPackageUrl() {
		return customResPackageUrl;
	}

	public void setCustomResPackageUrl(String customResPackageUrl) {
		this.customResPackageUrl = customResPackageUrl;
	}

	public UploadStatus getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(UploadStatus uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	
}
