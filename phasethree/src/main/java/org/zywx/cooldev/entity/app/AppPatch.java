package org.zywx.cooldev.entity.app;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.commons.Enums.PATCH_TYPE;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 应用补丁包
 * @author yang.li
 * @date 2016-01-07
 */
@Entity
@Table(name="T_APP_PATCH")
public class AppPatch extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//***************************************************
	//    AppPatch fieds                                *
	//***************************************************
	/**
	 * Patch版本编号
	 */
	private String versionNo;
	
	/**
	 * Patch版本描述
	 */
	private String versionDescription;
	
	private String fileName;
	
	/**
	 * 发布人
	 */
	private long userId;
	
	/**
	 * 文件大小，单位KB
	 */
	private long fileSize;
	
	/**
	 * 基准代码版本编号（较低版本 - 原版本）
	 */
	private long baseAppVersionId;
	
	/**
	 * 补丁来源版本（较高版本 - 新版本）
	 */
	private long seniorAppVersionId;
	
	/**
	 * 补丁包类型（全量包，Widget包）
	 */
	private PATCH_TYPE type;
	
	/**
	 * Emm4.0正式环境发布标识
	 */
	private IfStatus published = IfStatus.NO;
	
	/**
	 * Emm4.0测试环境发布标识
	 */
	private IfStatus publishedTest = IfStatus.NO;
	
	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	/**
	 * 下载地址
	 */
	@Transient
	private String downloadUrl;
	
	@Transient
	private String baseAppVersionNo;
	
	@Transient
	private String baseAppVersionDescription;
	
	@Transient
	private String seniorAppVersionNo;
	
	@Transient
	private String seniorAppVersionDescription;
	
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public long getBaseAppVersionId() {
		return baseAppVersionId;
	}

	public void setBaseAppVersionId(long baseAppVersionId) {
		this.baseAppVersionId = baseAppVersionId;
	}

	public long getSeniorAppVersionId() {
		return seniorAppVersionId;
	}

	public void setSeniorAppVersionId(long seniorAppVersionId) {
		this.seniorAppVersionId = seniorAppVersionId;
	}

	public PATCH_TYPE getType() {
		return type;
	}

	public void setType(PATCH_TYPE type) {
		this.type = type;
	}

	public IfStatus getPublished() {
		return published;
	}

	public void setPublished(IfStatus published) {
		this.published = published;
	}

	public IfStatus getPublishedTest() {
		return publishedTest;
	}

	public void setPublishedTest(IfStatus publishedTest) {
		this.publishedTest = publishedTest;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getBaseAppVersionNo() {
		return baseAppVersionNo;
	}

	public void setBaseAppVersionNo(String baseAppVersionNo) {
		this.baseAppVersionNo = baseAppVersionNo;
	}

	public String getBaseAppVersionDescription() {
		return baseAppVersionDescription;
	}

	public void setBaseAppVersionDescription(String baseAppVersionDescription) {
		this.baseAppVersionDescription = baseAppVersionDescription;
	}

	public String getSeniorAppVersionNo() {
		return seniorAppVersionNo;
	}

	public void setSeniorAppVersionNo(String seniorAppVersionNo) {
		this.seniorAppVersionNo = seniorAppVersionNo;
	}

	public String getSeniorAppVersionDescription() {
		return seniorAppVersionDescription;
	}

	public void setSeniorAppVersionDescription(String seniorAppVersionDescription) {
		this.seniorAppVersionDescription = seniorAppVersionDescription;
	}
}
