package org.zywx.cooldev.entity.builder;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.PluginType;
import org.zywx.cooldev.commons.Enums.PluginVersionStatus;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_PLUGIN_VERSION")
public class PluginVersion extends BaseEntity {

	private static final long serialVersionUID = -7912729372456770287L;

	//***************************************************
	//    PluginVersion fieds                           *
	//***************************************************
	private String versionNo;
	
	private String versionDescription;
	
	private String downloadUrl;
	
	/**
	 * 给打包接口提供的GIT仓库地址
	 */
//	private String pkgGitRepoUrl;

	/**
	 * 自定义插件（包含自定义资源包）下载地址
	 */
	private String customDownloadUrl;
	
	/**
	 * 资源包下载地址
	 */
	private String resPackageUrl;

	/**
	 * 自定义资源包下载地址
	 */
	private String customResPackageUrl;
	
	private long pluginId;
	
	private OSType osType;
	
	private PluginVersionStatus status = PluginVersionStatus.ENABLE;

	/**
	 * 上传处理状态（Success也包括git提交完毕）
	 */
	private UploadStatus uploadStatus;	
	
	/**
	 * IDC存储路径
	 */
	private String filePath;
	
	
	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	//***************************************************
	//    Realted fieds                                 *
	//***************************************************
	@Transient
	private String pluginName;
	
	@Transient
	private String pluginEnName;
	
	@Transient
	private PluginType pluginType;

	@Transient
	private String pluginDescription;
	
	/**
	 * 插件保存在服务器的绝对地址
	 */
	@Transient
	private String absFilePath;
	
	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
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


	/*public String getPkgGitRepoUrl() {
		return pkgGitRepoUrl;
	}

	public void setPkgGitRepoUrl(String pkgGitRepoUrl) {
		this.pkgGitRepoUrl = pkgGitRepoUrl;
	}*/

	
	public String getAbsFilePath() {
		return absFilePath;
	}

	public void setAbsFilePath(String absFilePath) {
		this.absFilePath = absFilePath;
	}


	public PluginType getPluginType() {
		return pluginType;
	}

	public void setPluginType(PluginType pluginType) {
		this.pluginType = pluginType;
	}

	public UploadStatus getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(UploadStatus uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	
	
	public String getPluginEnName() {
		return pluginEnName;
	}

	public void setPluginEnName(String pluginEnName) {
		this.pluginEnName = pluginEnName;
	}

	public String getPluginDescription() {
		return pluginDescription;
	}

	public void setPluginDescription(String pluginDescription) {
		this.pluginDescription = pluginDescription;
	}	
	
	@Override
	public String toString(){
		return versionNo;
		
	}


}
