package org.zywx.cooldev.entity.builder;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.AUTH_STATUS;
import org.zywx.cooldev.commons.Enums.EMAIL_SERVER_TYPE;
import org.zywx.cooldev.commons.Enums.EMAIL_STATUS;
import org.zywx.cooldev.commons.Enums.INTEGRATE_STATUS;
import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @describe 平台设置 <br>
 * @author jiexiong.liu <br>
 * @date 2015年9月23日 下午3:08:53 <br>
 * 
 */
@Entity
@Table(name = "T_MAN_SETTING")
public class Setting extends BaseEntity {

	private static final long serialVersionUID = 7891763938948517140L;

	// 平台形象
	private String platLogo;
	private String platName;

	// 平台备份
	private long platInterval = 7L;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp platExecuteTime = new Timestamp(System.currentTimeMillis());;
	private String platBackupPath;

	// 接入系统
	private String SYSdoMain;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp SYSIntegrateTime = new Timestamp(System.currentTimeMillis());
	private INTEGRATE_STATUS SYSStatus = INTEGRATE_STATUS.NORMAL;
	private String SYSKey;

	// 邮件设置
	private EMAIL_SERVER_TYPE emailServerType = EMAIL_SERVER_TYPE.SMTP;
	private String emailServerUrl;
	private String emailServerPort;
	private String emailAccount;
	private EMAIL_STATUS emailServerStatus = EMAIL_STATUS.OPEN;
	private String emailPassword;

	// 授权信息
	private Enums.AUTH_STATUS authStatus = AUTH_STATUS.EFFECTIVE;
	private String authDeadTime;
	private String authorizePath;

	public EMAIL_STATUS getEmailServerStatus() {
		return emailServerStatus;
	}

	public void setEmailServerStatus(EMAIL_STATUS emailServerStatus) {
		this.emailServerStatus = emailServerStatus;
	}

	@Override
	public String toString() {
		return this.platName;
	}

	public String getPlatLogo() {
		return platLogo;
	}

	public void setPlatLogo(String platLogo) {
		this.platLogo = platLogo;
	}

	public String getPlatName() {
		return platName;
	}

	public void setPlatName(String platName) {
		this.platName = platName;
	}

	public long getPlatInterval() {
		return platInterval;
	}

	public void setPlatInterval(long platInterval) {
		this.platInterval = platInterval;
	}

	public Timestamp getPlatExecuteTime() {
		return platExecuteTime;
	}

	public void setPlatExecuteTime(Timestamp platExecuteTime) {
		this.platExecuteTime = platExecuteTime;
	}

	public String getPlatBackupPath() {
		return platBackupPath;
	}

	public void setPlatBackupPath(String platBackupPath) {
		this.platBackupPath = platBackupPath;
	}

	public String getSYSdoMain() {
		return SYSdoMain;
	}

	public void setSYSdoMain(String sYSdoMain) {
		SYSdoMain = sYSdoMain;
	}

	public Timestamp getSYSIntegrateTime() {
		return SYSIntegrateTime;
	}

	public void setSYSIntegrateTime(Timestamp sYSIntegrateTime) {
		SYSIntegrateTime = sYSIntegrateTime;
	}

	public INTEGRATE_STATUS getSYSStatus() {
		return SYSStatus;
	}

	public void setSYSStatus(INTEGRATE_STATUS sYSStatus) {
		SYSStatus = sYSStatus;
	}

	public String getSYSKey() {
		return SYSKey;
	}

	public void setSYSKey(String sYSKey) {
		SYSKey = sYSKey;
	}

	

	public EMAIL_SERVER_TYPE getEmailServerType() {
		return emailServerType;
	}

	public void setEmailServerType(EMAIL_SERVER_TYPE emailServerType) {
		this.emailServerType = emailServerType;
	}

	public String getEmailServerUrl() {
		return emailServerUrl;
	}

	public void setEmailServerUrl(String emailServerUrl) {
		this.emailServerUrl = emailServerUrl;
	}

	public String getEmailServerPort() {
		return emailServerPort;
	}

	public void setEmailServerPort(String emailServerPort) {
		this.emailServerPort = emailServerPort;
	}

	public String getEmailAccount() {
		return emailAccount;
	}

	public void setEmailAccount(String emailAccount) {
		this.emailAccount = emailAccount;
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword;
	}

	public AUTH_STATUS getAuthStatus() {
		return authStatus;
	}

	public void setAuthStatus(AUTH_STATUS authStatus) {
		this.authStatus = authStatus;
	}

	public String getAuthDeadTime() {
		return authDeadTime;
	}

	public void setAuthDeadTime(String authDeadTime) {
		this.authDeadTime = authDeadTime;
	}

	public String getAuthorizePath() {
		return authorizePath;
	}

	public void setAuthorizePath(String authorizePath) {
		this.authorizePath = authorizePath;
	}


	
}
