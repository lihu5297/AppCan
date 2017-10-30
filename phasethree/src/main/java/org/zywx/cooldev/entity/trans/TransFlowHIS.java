package org.zywx.cooldev.entity.trans;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 工单流程记录历史表
 * @author lihu
 * 20170808
 *
 */
@Entity
@Table(name="T_TRANS_FLOW_HIS")
public class TransFlowHIS extends BaseEntity {

	private static final long serialVersionUID = 1148934999815049435L;

	/**
	 * 申请人
	 */
	private long userId;
	
	/**
	 * 工单流程内容
	 */
	private String message;
	
	/**
	 * 工单流程类型 0 重新提交 1 创建 2 作废  3审批同意 4审批不同意
	 */
	private Integer hisType;
	
	/**
	 * 操作人id
	 */
	private long operationId;
	
	/**
	 * 原创交易Id
	 */
	private long transId;
	/**
	 * 原创交易Id
	 */
	private String applyNum;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getHisType() {
		return hisType;
	}

	public void setHisType(Integer hisType) {
		this.hisType = hisType;
	}

	public long getOperationId() {
		return operationId;
	}

	public void setOperationId(long operationId) {
		this.operationId = operationId;
	}
	 
	public long getTransId() {
		return transId;
	}

	public void setTransId(long transId) {
		this.transId = transId;
	}

	public String getApplyNum() {
		return applyNum;
	}

	public void setApplyNum(String applyNum) {
		this.applyNum = applyNum;
	}

}
