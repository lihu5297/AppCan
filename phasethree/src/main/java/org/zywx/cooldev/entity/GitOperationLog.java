package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.GIT_OPERATE_TYPE;

@Entity
@Table(name="T_GIT_OPERATION_LOG")
public class GitOperationLog extends BaseEntity{

	/**
	 * @describe 	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月23日 上午11:17:21	<br>
	 * 
	 */
	private static final long serialVersionUID = 6050147586483823494L;
	
	private Long userId;
	
	private Long appId;
	
	private String account;
	
	private GIT_OPERATE_TYPE type;
	
	private String gitRemoteUrl;

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getGitRemoteUrl() {
		return gitRemoteUrl;
	}

	public void setGitRemoteUrl(String gitRemoteUrl) {
		this.gitRemoteUrl = gitRemoteUrl;
	}

	
	public Long getUserId() {
		return userId;
	}
	

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	
	public GIT_OPERATE_TYPE getType() {
		return type;
	}
	

	public void setType(GIT_OPERATE_TYPE type) {
		this.type = type;
	}
	
	

}
