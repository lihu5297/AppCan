package org.zywx.cooldev.entity.app;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.json.JSONObject;

import org.zywx.cooldev.commons.Enums.AppPackageBuildStatus;
import org.zywx.cooldev.commons.Enums.AppPackageBuildType;
import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.TerminalType;
import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.builder.PluginVersion;

/**
 * 应用构建包
 * @author derek
 *
 */
@Entity
@Table(name="T_APP_PACKAGE")
public class AppPackage extends BaseEntity {

	private static final long serialVersionUID = -9039797311244094418L;
	
	//***************************************************
	//    AppPackage fieds                              *
	//***************************************************
	/**
	 * 打包版本编号
	 */
	private String versionNo;
	
	private String versionDescription;

	/**
	 * 渠道号，字符串
	 */
	private String channelCode;

	/**
	 * 构建配置
	 */
	@Column(columnDefinition="text")
	private String buildJsonSettings;
	
	/**
	 * 构建者
	 */
	private long userId;
	
	/**
	 * 文件大小，单位KB
	 */
	private long fileSize;
	
	/**
	 * 支持的操作系统
	 */
	private OSType osType;
	
	/**
	 * 代码版本编号
	 */
	private long appVersionId;
	
	/**
	 * 下载地址
	 */
	private String downloadUrl;
	
	/**
	 * 二维码（苹果的二维码与下载地址不同，是plist文件下载地址）
	 */
	private String qrCode;
	
	/**
	 * 构建状态
	 */
	private AppPackageBuildStatus buildStatus;
	
	/**
	 * 构建类型
	 */
	private AppPackageBuildType buildType;
	
	/**
	 * 构建信息
	 */
	private String buildMessage;
	
	/**
	 * 构建日志下载地址
	 */
	private String buildLogUrl;
	
	private TerminalType terminalType;
	
	private int pushIF = -1;
	
	private int hardwareAccelerated = -1;
	
	private int increUpdateIF = -1;
	
	private int updateSwith = -1;	// 增量更新开关
	
	private IfStatus publised;
	
	private IfStatus publisedTest;
	
	private IfStatus publisedAppCan;
	
	//打包临时用appid和appkey
	private String newAppCanAppId;
	
	private String newAppCanAppKey;
	
	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private JSONObject settings;
	
	@Transient
	private String appVersionNo;
	
	@Transient
	private String appVersionDescription;

	@Transient
	private List<PluginVersion> pluginVersions;
	
	@Transient
	private String branchZipUrl;
	
	/**
	 * 代码版本库地址
	 */
	@Transient
	private String remoteRepoPath;
	
	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getBuildJsonSettings() {
		return buildJsonSettings;
	}

	public void setBuildJsonSettings(String buildJsonSettings) {
		this.buildJsonSettings = buildJsonSettings;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public OSType getOsType() {
		return osType;
	}

	public void setOsType(OSType osType) {
		this.osType = osType;
	}

	public long getAppVersionId() {
		return appVersionId;
	}

	public void setAppVersionId(long appVersionId) {
		this.appVersionId = appVersionId;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public JSONObject getSettings() {
		return settings;
	}

	public void setSettings(JSONObject settings) {
		this.settings = settings;
	}

	public String getAppVersionNo() {
		return appVersionNo;
	}

	public void setAppVersionNo(String appVersionNo) {
		this.appVersionNo = appVersionNo;
	}

	public String getAppVersionDescription() {
		return appVersionDescription;
	}

	public void setAppVersionDescription(String appVersionDescription) {
		this.appVersionDescription = appVersionDescription;
	}

	public AppPackageBuildStatus getBuildStatus() {
		return buildStatus;
	}

	public void setBuildStatus(AppPackageBuildStatus buildStatus) {
		this.buildStatus = buildStatus;
	}

	public String getBuildMessage() {
		return buildMessage;
	}

	public void setBuildMessage(String buildMessage) {
		this.buildMessage = buildMessage;
	}

	public TerminalType getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(TerminalType terminalType) {
		this.terminalType = terminalType;
	}

	public int getPushIF() {
		return pushIF;
	}

	public void setPushIF(int pushIF) {
		this.pushIF = pushIF;
	}

	public int getHardwareAccelerated() {
		return hardwareAccelerated;
	}

	public void setHardwareAccelerated(int hardwareAccelerated) {
		this.hardwareAccelerated = hardwareAccelerated;
	}

	public int getIncreUpdateIF() {
		return increUpdateIF;
	}

	public void setIncreUpdateIF(int increUpdateIF) {
		this.increUpdateIF = increUpdateIF;
	}

	public AppPackageBuildType getBuildType() {
		return buildType;
	}

	public void setBuildType(AppPackageBuildType buildType) {
		this.buildType = buildType;
	}

	
	public List<PluginVersion> getPluginVersions() {
		return pluginVersions;
	}

	public void setPluginVersions(List<PluginVersion> pluginVersions) {
		this.pluginVersions = pluginVersions;
	}


	public String getBuildLogUrl() {
		return buildLogUrl;
	}

	public void setBuildLogUrl(String buildLogUrl) {
		this.buildLogUrl = buildLogUrl;
	}
	
	public IfStatus getPublised() {
		return publised;
	}

	public void setPublised(IfStatus publised) {
		this.publised = publised;
	}
	
	public IfStatus getPublisedTest() {
		return publisedTest;
	}

	public void setPublisedTest(IfStatus publisedTest) {
		this.publisedTest = publisedTest;
	}

	public IfStatus getPublisedAppCan() {
		return publisedAppCan;
	}

	public void setPublisedAppCan(IfStatus publisedAppCan) {
		this.publisedAppCan = publisedAppCan;
	}

		//获取文件大小,大于1M按照MB返回,小于1M按照KB返回
	public String getSizeStr() {
//			double kb = fileSize;
//			double mb = kb/(double)1024;
		double kb = (double)Math.round((double)fileSize*10)/10;//KB 保留一位小数
		double mb = (double)Math.round(kb/1024*10)/10;//M 保留一位小数
		return mb>=1?mb+" MB":kb + " KB";
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

	public String getBranchZipUrl() {
		return branchZipUrl;
	}

	public void setBranchZipUrl(String branchZipUrl) {
		this.branchZipUrl = branchZipUrl;
	}

	
	public String getRemoteRepoPath() {
		return remoteRepoPath;
	}

	
	public void setRemoteRepoPath(String remoteRepoPath) {
		this.remoteRepoPath = remoteRepoPath;
	}


	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}


	public int getUpdateSwith() {
		return updateSwith;
	}

	public void setUpdateSwith(int updateSwith) {
		this.updateSwith = updateSwith;
	}

	public String getNewAppCanAppId() {
		return newAppCanAppId;
	}

	public void setNewAppCanAppId(String newAppCanAppId) {
		this.newAppCanAppId = newAppCanAppId;
	}

	public String getNewAppCanAppKey() {
		return newAppCanAppKey;
	}

	public void setNewAppCanAppKey(String newAppCanAppKey) {
		this.newAppCanAppKey = newAppCanAppKey;
	}
	

}
