package org.zywx.coopman.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.commons.Enums.BACKUP_STATUS;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月24日 下午6:19:26	<br>
 * 
 */
@Entity
@Table(name="T_MAN_BACKUP_LOG")
public class BackupLog extends BaseEntity{

	private static final long serialVersionUID = 3833176721260616739L;
	
	private String serverName;
	private String serverIp;
	private String backupPath;
	private String backupDetail;
	private String backupFileName;
	private BACKUP_STATUS status = BACKUP_STATUS.ONGOING;
	private Timestamp backupTime = new Timestamp(System.currentTimeMillis());
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerIp() {
		return serverIp;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public String getBackupPath() {
		return backupPath;
	}
	public void setBackupPath(String backupPath) {
		this.backupPath = backupPath;
	}
	public String getBackupDetail() {
		return backupDetail;
	}
	public void setBackupDetail(String backupDetail) {
		this.backupDetail = backupDetail;
	}
	public Timestamp getBackupTimeT() {
		return backupTime;
	}
	
	public String getBackupTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return sdf.format(backupTime);
	}
	
	public void setBackupTime(Timestamp backupTime) {
		this.backupTime = backupTime;
	}
	public String getBackupFileName() {
		return backupFileName;
	}
	public void setBackupFileName(String backupFileName) {
		this.backupFileName = backupFileName;
	}
	public BACKUP_STATUS getStatus() {
		return status;
	}
	public void setStatus(BACKUP_STATUS status) {
		this.status = status;
	}
	
	
	
}
