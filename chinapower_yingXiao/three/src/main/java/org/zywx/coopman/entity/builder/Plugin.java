package org.zywx.coopman.entity.builder;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.commons.Enums.PluginType;
import org.zywx.coopman.entity.BaseEntity;

@Entity
@Table(name="T_PLUGIN")
public class Plugin extends BaseEntity {

	private static final long serialVersionUID = -753000798476095540L;

	private String enName;
	private String cnName;
	private String detail;
	private long categoryId = -1;
	private String tutorial;
	private long projectId = -1;
	private PluginType type;
	
	@Transient
	private String iosVersion;
	@Transient
	private String iosDownloadUrl;
	@Transient
	private String androidVersion;
	@Transient
	private String androidDownloadUrl;
	
	@Transient
	private List<PluginVersion> pluginVersion;
	
	@Transient
	private String categoryName;

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

}
