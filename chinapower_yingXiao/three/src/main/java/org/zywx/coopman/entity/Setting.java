package org.zywx.coopman.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.commons.Enums.AUTH_STATUS;
import org.zywx.coopman.commons.Enums.EMAIL_SERVER_TYPE;
import org.zywx.coopman.commons.Enums.EMAIL_STATUS;
import org.zywx.coopman.commons.Enums.INTEGRATE_STATUS;

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
	private int platExecuteTime_hour = 0;
	private int platExecuteTime_minutes = 0;
	private String platBackupPath;

	// 接入系统
	private String SYSdoMain;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp SYSIntegrateTime = new Timestamp(System.currentTimeMillis());
	private INTEGRATE_STATUS SYSStatus = INTEGRATE_STATUS.NORMAL;
	private String SYSKey;

	// EMM正式环境接入
	private String EMMAccessUrl;
	private String EMMDataReportUrl;
	private String EMMPushBindUrl;
	private String EMMDataStatisticUrl;
	private String EMMAndroidPushUrl;
	private String EMMDeviceManageUrl;
	private String EMMContentManageUrl;

	// EMM测试环境接入
	private String EMMTestAccessUrl;
	private String EMMTestDataReportUrl;
	private String EMMTestPushBindUrl;
	private String EMMTestDataStatisticUrl;
	private String EMMTestAndroidPushUrl;
	private String EMMTestDeviceManageUrl;
	private String EMMTestContentManageUrl;

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
	
	private String webAddr;//协同开发访问地址
	

	public String getWebAddr() {
		return webAddr;
	}

	public void setWebAddr(String webAddr) {
		this.webAddr = webAddr;
	}

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

	public int getPlatExecuteTime_hour() {
		return platExecuteTime_hour;
	}

	public void setPlatExecuteTime_hour(int platExecuteTime_hour) {
		this.platExecuteTime_hour = platExecuteTime_hour;
	}

	public int getPlatExecuteTime_minutes() {
		return platExecuteTime_minutes;
	}

	public void setPlatExecuteTime_minutes(int platExecuteTime_minutes) {
		this.platExecuteTime_minutes = platExecuteTime_minutes;
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

	public String getEMMAccessUrl() {
		return EMMAccessUrl;
	}

	public void setEMMAccessUrl(String eMMAccessUrl) {
		EMMAccessUrl = eMMAccessUrl;
	}

	public String getEMMDataReportUrl() {
		return EMMDataReportUrl;
	}

	public void setEMMDataReportUrl(String eMMDataReportUrl) {
		EMMDataReportUrl = eMMDataReportUrl;
	}

	public String getEMMPushBindUrl() {
		return EMMPushBindUrl;
	}

	public void setEMMPushBindUrl(String eMMPushBindUrl) {
		EMMPushBindUrl = eMMPushBindUrl;
	}

	public String getEMMDataStatisticUrl() {
		return EMMDataStatisticUrl;
	}

	public void setEMMDataStatisticUrl(String eMMDataStatisticUrl) {
		EMMDataStatisticUrl = eMMDataStatisticUrl;
	}

	public String getEMMAndroidPushUrl() {
		return EMMAndroidPushUrl;
	}

	public void setEMMAndroidPushUrl(String eMMAndroidPushUrl) {
		EMMAndroidPushUrl = eMMAndroidPushUrl;
	}

	public String getEMMTestAccessUrl() {
		return EMMTestAccessUrl;
	}

	public void setEMMTestAccessUrl(String eMMTestAccessUrl) {
		EMMTestAccessUrl = eMMTestAccessUrl;
	}

	public String getEMMTestDataReportUrl() {
		return EMMTestDataReportUrl;
	}

	public void setEMMTestDataReportUrl(String eMMTestDataReportUrl) {
		EMMTestDataReportUrl = eMMTestDataReportUrl;
	}

	public String getEMMTestPushBindUrl() {
		return EMMTestPushBindUrl;
	}

	public void setEMMTestPushBindUrl(String eMMTestPushBindUrl) {
		EMMTestPushBindUrl = eMMTestPushBindUrl;
	}

	public String getEMMTestDataStatisticUrl() {
		return EMMTestDataStatisticUrl;
	}

	public void setEMMTestDataStatisticUrl(String eMMTestDataStatisticUrl) {
		EMMTestDataStatisticUrl = eMMTestDataStatisticUrl;
	}

	public String getEMMTestAndroidPushUrl() {
		return EMMTestAndroidPushUrl;
	}

	public void setEMMTestAndroidPushUrl(String eMMTestAndroidPushUrl) {
		EMMTestAndroidPushUrl = eMMTestAndroidPushUrl;
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

	public String getEMMDeviceManageUrl() {
		return EMMDeviceManageUrl;
	}

	public void setEMMDeviceManageUrl(String eMMDeviceManageUrl) {
		EMMDeviceManageUrl = eMMDeviceManageUrl;
	}

	public String getEMMContentManageUrl() {
		return EMMContentManageUrl;
	}

	public void setEMMContentManageUrl(String eMMContentManageUrl) {
		EMMContentManageUrl = eMMContentManageUrl;
	}

	public String getEMMTestDeviceManageUrl() {
		return EMMTestDeviceManageUrl;
	}

	public void setEMMTestDeviceManageUrl(String eMMTestDeviceManageUrl) {
		EMMTestDeviceManageUrl = eMMTestDeviceManageUrl;
	}

	public String getEMMTestContentManageUrl() {
		return EMMTestContentManageUrl;
	}

	public void setEMMTestContentManageUrl(String eMMTestContentManageUrl) {
		EMMTestContentManageUrl = eMMTestContentManageUrl;
	}

}
