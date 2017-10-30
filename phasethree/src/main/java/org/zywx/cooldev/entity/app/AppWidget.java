package org.zywx.cooldev.entity.app;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 应用Widget包（纯代码，非编译）
 * @author yang.li
 * @date 2016-01-07
 */
@Entity
@Table(name="T_APP_WIDGET")
public class AppWidget extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//***************************************************
	//    AppWidget fieds                               *
	//***************************************************
	/**
	 * Widget版本编号
	 */
	private String versionNo;
	
	/**
	 * Widget版本描述
	 */
	private String versionDescription;

	/**
	 * 发布人
	 */
	private long userId;
	
	/**
	 * 代码版本编号
	 */
	private long appVersionId;
	
	/**
	 * 压缩文件名
	 */
	private String fileName;
	
	/**
	 * 压缩文件大小
	 */
	private long fileSize;
	
	/**
	 * Emm4.0正式环境发布标识
	 */
	private IfStatus publised = IfStatus.NO;
	
	/**
	 * Emm4.0测试环境发布标识
	 */
	private IfStatus publisedTest = IfStatus.NO;
	
	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private String appVersionNo;
	
	@Transient
	private String appVersionDescription;
	
	/**
	 * 下载地址
	 */
	@Transient
	private String downloadUrl;

	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	/**
	 * 获取文件大小,大于1M按照MB返回,小于1M按照KB返回
	 * @return
	 */
	public String getFileSizeStr() {
		double kb = (double)Math.round((double)fileSize/1024*10)/10;//KB 保留一位小数
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getAppVersionId() {
		return appVersionId;
	}

	public void setAppVersionId(long appVersionId) {
		this.appVersionId = appVersionId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}


	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

}
