package org.zywx.cooldev.entity.builder;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.PluginType;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_PLUGIN")
public class Plugin extends BaseEntity {

	private static final long serialVersionUID = -753000798476095540L;

	//***************************************************
	//    Plugin fieds                                  *
	//***************************************************
	private String enName;
	private String cnName;
	
	@Column(name="detail",columnDefinition="longtext")
	private String detail;
	private long categoryId;
	private String tutorial;
	private long projectId = -1;
	/**
	 * 插件类型（前端服务生成仅为项目插件）
	 */
	@Column(updatable=false)
	private PluginType type;
	
	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private String iosVersion;
	@Transient
	private String iosDownloadUrl;
	@Transient
	private String iosAbsFilePath;
	@Transient
	private String iosOriResUrl;		// 原始资源包下载地址
	@Transient
	private String iosCusResUrl;		// 自定义资源包下载地址
	
	@Transient
	private String androidVersion;
	@Transient
	private String androidDownloadUrl;
	@Transient
	private String androidAbsFilePath;
	@Transient
	private String androidOriResUrl;
	@Transient
	private String androidCusResUrl;
	
	
	@Transient
	private List<PluginVersion> pluginVersion;
	
	@Transient
	private String categoryName;

	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public String getTutorial() {
		return tutorial;
	}

	public void setTutorial(String tutorial) {
		this.tutorial = tutorial;
	}

	public List<PluginVersion> getPluginVersion() {
		return pluginVersion;
	}

	public void setPluginVersion(List<PluginVersion> pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	
	public String getIosVersion() {
		return iosVersion;
	}

	public void setIosVersion(String iosVersion) {
		this.iosVersion = iosVersion;
	}

	public String getIosDownloadUrl() {
		return iosDownloadUrl;
	}

	public void setIosDownloadUrl(String iosDownloadUrl) {
		this.iosDownloadUrl = iosDownloadUrl;
	}

	public String getAndroidVersion() {
		return androidVersion;
	}

	public void setAndroidVersion(String androidVersion) {
		this.androidVersion = androidVersion;
	}

	public String getAndroidDownloadUrl() {
		return androidDownloadUrl;
	}

	public void setAndroidDownloadUrl(String androidDownloadUrl) {
		this.androidDownloadUrl = androidDownloadUrl;
	}


	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	
	public PluginType getType() {
		return type;
	}

	public void setType(PluginType type) {
		this.type = type;
	}

	public String getIosAbsFilePath() {
		return iosAbsFilePath;
	}

	public void setIosAbsFilePath(String iosAbsFilePath) {
		this.iosAbsFilePath = iosAbsFilePath;
	}

	public String getAndroidAbsFilePath() {
		return androidAbsFilePath;
	}

	public void setAndroidAbsFilePath(String androidAbsFilePath) {
		this.androidAbsFilePath = androidAbsFilePath;
	}

	
	public String getIosOriResUrl() {
		return iosOriResUrl;
	}

	public void setIosOriResUrl(String iosOriResUrl) {
		this.iosOriResUrl = iosOriResUrl;
	}

	public String getIosCusResUrl() {
		return iosCusResUrl;
	}

	public void setIosCusResUrl(String iosCusResUrl) {
		this.iosCusResUrl = iosCusResUrl;
	}

	public String getAndroidOriResUrl() {
		return androidOriResUrl;
	}

	public void setAndroidOriResUrl(String androidOriResUrl) {
		this.androidOriResUrl = androidOriResUrl;
	}

	public String getAndroidCusResUrl() {
		return androidCusResUrl;
	}

	public void setAndroidCusResUrl(String androidCusResUrl) {
		this.androidCusResUrl = androidCusResUrl;
	}

	
	@Override
	public String toString() {
		return enName;
	}


}
