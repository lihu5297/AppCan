package org.zywx.coopman.entity.DailyLog;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;


/**
 * @describe 操作日志	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月10日 下午10:11:03	<br>
 * 
 */
@Entity
@Table(name="T_MAN_OPERATION_LOG")
public class OperationLog extends BaseEntity{


	private static final long serialVersionUID = -5820430196252847621L;
	
	private String account;
	private String ip;
	private String operationLog;
	private long logActionId;
	private String method;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOperationLog() {
		return operationLog;
	}
	public void setOperationLog(String operationLog) {
		this.operationLog = operationLog;
	}	
	public Long getLogActionId() {
		return logActionId;
	}
	public void setLogActionId(Long logActionId) {
		this.logActionId = logActionId;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
}
