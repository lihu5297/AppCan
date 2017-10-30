package org.zywx.cooldev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
/**
 * 检查信息(代码检测、安全检测等)
 * @author 东元
 *
 */
@Entity
@Table(name="T_CHECK_INFO")
public class CheckInfo extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Long userId;			//发起检查操作的用户ID
	short checkType;		//检测对象类型 0 代码；1安全
	String uniqueId;		//检测时的会话ID
	String checkResult;		//检查结果
	
	String checkInfo;		//检查结果描述
	String checkFilePath;	//检查结果文件存放路径
	String duration;		//检查时长
	long versionId;			//t_app_version  主键
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public short getCheckType() {
		return checkType;
	}
	public void setCheckType(short checkType) {
		this.checkType = checkType;
	}
	
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public String getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}
	public String getCheckInfo() {
		return checkInfo;
	}
	public void setCheckInfo(String checkInfo) {
		this.checkInfo = checkInfo;
	}
	public String getCheckFilePath() {
		return checkFilePath;
	}
	public void setCheckFilePath(String checkFilePath) {
		this.checkFilePath = checkFilePath;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public long getVersionId() {
		return versionId;
	}
	public void setVersionId(long versionId) {
		this.versionId = versionId;
	}
	
	
	
}
