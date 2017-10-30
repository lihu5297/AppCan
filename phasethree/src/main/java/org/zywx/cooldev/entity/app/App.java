package org.zywx.cooldev.entity.app;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.RepositoryType;
import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.Resource;

/**
 * 应用实体
 * @author yang.li
 *
 */
@Entity
@Table(name="T_APP")
public class App extends BaseEntity {

	private static final long serialVersionUID = 1148934999815049435L;
	
	//***************************************************
	//    App fieds                                     *
	//***************************************************
	/**
	 * 应用名称
	 */
	private String name;
	
	/**
	 * 具体描述
	 */
	@Column(updatable=true,columnDefinition="longtext")
	private String detail="";
	
	/**
	 * 应用icon
	 */
	private String icon;
	
	/**
	 * 项目ID
	 */
	//@Column(updatable=false)
	private long projectId;
	
	/**
	 * appcan - APP_ID
	 */
	private String appcanAppId = "";

	/**
	 * appcan - APP_KEY
	 */
	private String appcanAppKey = "";

	/**
	 * 版本库相对路径
	 */
	private String relativeRepoPath = "";
	
	/**
	 * 应用类型(MOBILE,HTML,MAS)
	 */
	@Column(updatable=true)
	private Long appType = 0L;
	 
	
	/**
	 * 关联应用(选择网页应用时，关联移动应用)
	 */
	@Column(updatable=false)
	private Long AppSource = -1L;
	
	/**
	 * 版本库类型（GIT,SVN）
	 */
	@Column(updatable=false)
	private RepositoryType repoType = RepositoryType.GIT;
	
	/**
	 * 是否已发布至emm正式环境
	 */
	private IfStatus published;
	
	/**
	 * 是否已发布至emm测试环境
	 */
	private IfStatus publishedTest;
	
	/**
	 * 是否已发布至线上appcan3.0环境
	 */
	private IfStatus publishedAppCan;

	/**
	 * 应用发布人
	 */
	private long userId;
	
	/**
	 * 应用类别
	 * AppCanNative
	 * AppCanWgt
	 */
	private String appCategory;
	
	/**
	 * 由于git客户端提交了代码之后,GIT服务器调用协同的notifyGitRepoPushed接口,协同执行pull操作需要耗费一定时间,此时将应用的代码更新状态改为更新中,
	 * 当执行完之后,再将代码更新状态变为已完毕
	 */
	private String codePullStatus;
	
		//---------------------------------------云平台-----------begin-------------------------------
	/**
	 * 应用是否允许发布至管理平台.
	 * yes不允许
	 * no允许 空值也代表允许发布  
	 */
	private String forbidPub="no";
	
	/**
	 * 指定的appcanappId
	 */
	private String specialAppCanAppId;
	
	/**
	 * 指定的appcanappkey
	 */
	private String specialAppCanAppKey;
	
	/**
	 * 创建应用的时候默认代码来自于git仓库地址
	 */
	private String sourceGitRepo;
	
	private String pinYinHeadChar;
	
	private String pinYinName;
	
	//原生应用类型
	private String platForm;// iOS  ;  Android
	//zhouxx add 20170813 start
	/**
	 * 大项目id
	 */
	private long projectParentId;
	@Transient
	private String projectParentName;
	/**
	 * 状态 00有效，01无效
	 */
	private String appStatus;
	//应用编号
	private String appCode;
	
	//是否 为项目一起提交的应用
	private boolean isProApp =false;
	//zhouxx add 20170813 end
	
	public String getPlatForm() {
		return platForm;
	}

	public long getProjectParentId() {
		return projectParentId;
	}

	public void setProjectParentId(long projectParentId) {
		this.projectParentId = projectParentId;
	}

	public String getAppStatus() {
		return appStatus;
	}

	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}

	public void setPlatForm(String platForm) {
		this.platForm = platForm;
	}

	public String getPinYinHeadChar() {
		return pinYinHeadChar;
	}

	public void setPinYinHeadChar(String pinYinHeadChar) {
		this.pinYinHeadChar = pinYinHeadChar;
	}

	public String getPinYinName() {
		return pinYinName;
	}

	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
	}

	public String getSpecialAppCanAppId() {
		return specialAppCanAppId;
	}

	public void setSpecialAppCanAppId(String specialAppCanAppId) {
		this.specialAppCanAppId = specialAppCanAppId;
	}

	public String getSpecialAppCanAppKey() {
		return specialAppCanAppKey;
	}

	public void setSpecialAppCanAppKey(String specialAppCanAppKey) {
		this.specialAppCanAppKey = specialAppCanAppKey;
	}
	
	public String getSourceGitRepo() {
		return sourceGitRepo;
	}

	public void setSourceGitRepo(String sourceGitRepo) {
		this.sourceGitRepo = sourceGitRepo;
	}

	public String getForbidPub() {
		return forbidPub;
	}

	public void setForbidPub(String forbidPub) {
		this.forbidPub = forbidPub;
	}

	//---------------------------------------云平台----------end--------------------------------
	
	
	public String getCodePullStatus() {
		return codePullStatus;
	}

	public void setCodePullStatus(String codePullStatus) {
		this.codePullStatus = codePullStatus;
	}


	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private String appTypeName="";
	
	@Transient
	private String appSourceName;
	
	@Transient
	private String projectName;
	
	@Transient
	private PROJECT_BIZ_LICENSE projectBizLicense;
	
	@Transient
	private PROJECT_TYPE projectType;

	@Transient
	private String remoteRepoUrl;
	
	@Transient
	private String userName;//创建者姓名

	@Transient
	private   List<Resource> resources;
	@Transient
	private   Integer resourcesTotal;
	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	
	public String getName() {
		return name;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public Integer getResourcesTotal() {
		return resourcesTotal;
	}

	public void setResourcesTotal(Integer resourcesTotal) {
		this.resourcesTotal = resourcesTotal;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getAppcanAppId() {
		return appcanAppId;
	}

	public void setAppcanAppId(String appcanAppId) {
		this.appcanAppId = appcanAppId;
	}

	public String getAppcanAppKey() {
		return appcanAppKey;
	}

	public void setAppcanAppKey(String appcanAppKey) {
		this.appcanAppKey = appcanAppKey;
	}

	public String getRelativeRepoPath() {
		return relativeRepoPath;
	}

	public void setRelativeRepoPath(String relativeRepoPath) {
		this.relativeRepoPath = relativeRepoPath;
	}

	public Long getAppType() {
		return appType;
	}

	public void setAppType(Long appType) {
		this.appType = appType;
	}

	public Long getAppSource() {
		return AppSource;
	}

	public void setAppSource(Long appSource) {
		AppSource = appSource;
	}

	public RepositoryType getRepoType() {
		return repoType;
	}

	public void setRepoType(RepositoryType repoType) {
		this.repoType = repoType;
	}

	public String getAppTypeName() {
		return appTypeName;
	}

	public void setAppTypeName(String appTypeName) {
		this.appTypeName = appTypeName;
	}

	public String getAppSourceName() {
		return appSourceName;
	}

	public void setAppSourceName(String appSourceName) {
		this.appSourceName = appSourceName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public PROJECT_BIZ_LICENSE getProjectBizLicense() {
		return projectBizLicense;
	}

	public void setProjectBizLicense(PROJECT_BIZ_LICENSE projectBizLicense) {
		this.projectBizLicense = projectBizLicense;
	}

	public PROJECT_TYPE getProjectType() {
		return projectType;
	}

	public void setProjectType(PROJECT_TYPE projectType) {
		this.projectType = projectType;
	}

	public String getRemoteRepoUrl() {
		return remoteRepoUrl;
	}

	public void setRemoteRepoUrl(String remoteRepoUrl) {
		this.remoteRepoUrl = remoteRepoUrl;
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

	public IfStatus getPublishedAppCan() {
		return publishedAppCan;
	}

	public void setPublishedAppCan(IfStatus publishedAppCan) {
		this.publishedAppCan = publishedAppCan;
	}

	@Override
	public String toString() {
		return this.name;
	}

	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	
	public String getAppCategory() {
		/*if(appCategory==null || appCategory.equals("")){
			return null;
		}else{
			return appCategory.equals(AppCategory.AppCanNative.toString())?AppCategory.AppCanNative.toString():AppCategory.AppCanWgt.toString();
		}*/
		return appCategory;
	}

	
	public void setAppCategory(String appCategory) {
		this.appCategory = appCategory;
	}

	
	public String getIcon() {
		return icon;
	}

	
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public boolean isProApp() {
		return isProApp;
	}

	public void setProApp(boolean isProApp) {
		this.isProApp = isProApp;
	}

	public String getProjectParentName() {
		return projectParentName;
	}

	public void setProjectParentName(String projectParentName) {
		this.projectParentName = projectParentName;
	}
	

}
