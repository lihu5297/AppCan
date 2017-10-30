package org.zywx.cooldev.entity.trans;


import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;


/**
 * 交易：项目应用申请实体
 * @author zhouxx
 * @date 20170811
 * 
 */

public class TransApp {

	
	private static final long serialVersionUID = 2710001149955107336L;
	/**
	 * 用户id
	 */
	private long userId ;
	/**
	 * 应用id
	 */
	private long appId ;
	/**
	 * 交易id
	 */
	private long transId;
	/**
	 * 状态id，3通过，4未通过
	 */
	private String status;
	/**
	 * 申请编号
	 */
	private String applyNum;
	
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
	public long getTransId() {
		return transId;
	}
	public void setTransId(long transId) {
		this.transId = transId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(String applyNum) {
		this.applyNum = applyNum;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
