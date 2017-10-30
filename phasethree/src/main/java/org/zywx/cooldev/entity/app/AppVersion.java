package org.zywx.cooldev.entity.app;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.AppVersionType;
import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.CheckInfo;

/**
 * 应用版本
 * @author yang.li
 *
 */
@Entity
@Table(name="T_APP_VERSION")
public class AppVersion extends BaseEntity {

	private static final long serialVersionUID = 3694126749469044051L;

	//***************************************************
	//    AppVersion fieds                              *
	//***************************************************
	/**
	 * 代码版本编号, 也用于git/svn标签名称
	 */
	private String versionNo;
	
	/**
	 * 代码版本描述
	 */
	@Column(name="versionDescription",columnDefinition="longtext")
	private String versionDescription;

	/**
	 * 版本基于的代码分支名称
	 */
	private String branchName;
	
	/**
	 * 发布人
	 */
	@Column(updatable=false)
	private long userId = -1;
	
	/**
	 * 应用编号
	 */
	@Column(updatable=false)
	private long appId = -1;
	
	/**
	 * 应用版本类型（项目版本或个人版本）
	 */
	@Column(updatable=false)
	private AppVersionType type;

	/**
	 * 分支代码zip文件名
	 */
	private String branchZipName;
	
	/**
	 * 分支代码zip文件大小（字节数）
	 */
	private long branchZipSize;
	
	// Widget相关已经迁移至AppWidget实体
	
	/**
	 * git标签名称
	 */
	private String tagName;
	
	/**
	 * 是否申请发布标志
	 */
	private boolean haveApplyPublish = false;
	/**
	 * 申请交易主键
	 */
	private Long transId = 0L;
	/**
	 * 申请发版的，安装包的属性：0 android;1 IOS; 2 android和IOS; 3widget；4补丁包
	 */
	private String applyPackageProperties;
	
	// 补丁包相关已经迁移至AppPatch实体

	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private List<AppPackage> androidPackages;
	
	@Transient
	private  CheckInfo  checkInfo;
	
	@Transient
	private List<AppPackage> iosPackages;

	@Transient
	private String userName;
	
	@Transient
	private String userIcon;
	
	@Transient
	private String appName;
	
	/**
	 * 分支代码zip包下载地址
	 */
	@Transient
	private String branchZipUrl;

	/**
	 * 如果是网页应用或者MAS应用,这里需要展示zip包大小
	 * 文件大小，
	 */
	@Transient
	private String zipFileSize;

	/**
	 * 代码版本生成的widget包（一个版本仅有一个widget包）
	 */
	@Transient
	private AppWidget appWidget;
	
	/**
	 * 已代码版本为基准（作为较低版本）生成的补丁包
	 */
	@Transient
	private List<AppPatch> appPatches;
	
	@Transient
	private String applyNum;		//申请发版编号
	@Transient
	private String applyStatus;		//申请发版状态
	@Transient
	private String applyNode;		//申请发版环节
	
	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	@Override
	public String toString() {
		return versionNo;
	}
	
	public String getBranchZipSizeStr() {

		double kb = (double)Math.round( (double)branchZipSize / 1024*10 ) / 10;	//KB 保留一位小数
		double mb = (double)Math.round( kb / 1024*10 ) / 10;					//M 保留一位小数
		return mb >= 1 ? mb + " MB" : kb + " KB";
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

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public AppVersionType getType() {
		return type;
	}

	public void setType(AppVersionType type) {
		this.type = type;
	}

	public String getBranchZipName() {
		return branchZipName;
	}

	public void setBranchZipName(String branchZipName) {
		this.branchZipName = branchZipName;
	}

	public long getBranchZipSize() {
		return branchZipSize;
	}

	public void setBranchZipSize(long branchZipSize) {
		this.branchZipSize = branchZipSize;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public List<AppPackage> getAndroidPackages() {
		return androidPackages;
	}

	public void setAndroidPackages(List<AppPackage> androidPackages) {
		this.androidPackages = androidPackages;
	}

	public List<AppPackage> getIosPackages() {
		return iosPackages;
	}

	public void setIosPackages(List<AppPackage> iosPackages) {
		this.iosPackages = iosPackages;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getBranchZipUrl() {
		return branchZipUrl;
	}

	 

	public CheckInfo getCheckInfo() {
		return checkInfo;
	}

	public void setCheckInfo(CheckInfo checkInfo) {
		this.checkInfo = checkInfo;
	}

	public void setBranchZipUrl(String branchZipUrl) {
		this.branchZipUrl = branchZipUrl;
	}

	public String getZipFileSize() {
		return zipFileSize;
	}

	public void setZipFileSize(String zipFileSize) {
		this.zipFileSize = zipFileSize;
	}

	public AppWidget getAppWidget() {
		return appWidget;
	}

	public void setAppWidget(AppWidget appWidget) {
		this.appWidget = appWidget;
	}

	public List<AppPatch> getAppPatches() {
		return appPatches;
	}

	public void setAppPatches(List<AppPatch> appPatches) {
		this.appPatches = appPatches;
	}

	public boolean isHaveApplyPublish() {
		return haveApplyPublish;
	}

	public void setHaveApplyPublish(boolean haveApplyPublish) {
		this.haveApplyPublish = haveApplyPublish;
	}

	public String getApplyPackageProperties() {
		return applyPackageProperties;
	}

	public void setApplyPackageProperties(String applyPackageProperties) {
		this.applyPackageProperties = applyPackageProperties;
	}

	public String getApplyNum() {
		return applyNum;
	}

	public void setApplyNum(String applyNum) {
		this.applyNum = applyNum;
	}

	public Long getTransId() {
		return transId;
	}

	public void setTransId(Long transId) {
		this.transId = transId;
	}

	public String getApplyStatus() {
		return applyStatus;
	}

	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}

	public String getApplyNode() {
		return applyNode;
	}

	public void setApplyNode(String applyNode) {
		this.applyNode = applyNode;
	}	
	
	
	
}
