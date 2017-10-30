package org.zywx.cooldev.entity.trans;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 工单历史
 * 
 * @author lihu
 *
 */
@Entity
@Table(name = "T_TRANS_HIS")
public class TransHis extends BaseEntity {

	private static final long serialVersionUID = 1148934999815049435L;
	/**
	 * 申请编号
	 */
	private String applyNum;
	/**
	 * 申请人
	 */
	private long userId;
	/**
	 * 操作人Id
	 */
	private long operationId;
	/**
	 * 工单类型 0其他， 1数模，2接口，3应用，4子项目，5子项目和应用申请，6移动应用发版申请，7后端应用发版申请
	 */
	private String tranType;
	/**
	 * 历史记录类型 0 重新提交 1 创建 2 作废  3审批同意 4审批不同意
	 */
	private int hisType;
	/**
	 * 审批内容
	 */
	private String message;
	/**
	 * 状态 0申请， 1已签收，2未签收，3通过，4未通过,5废弃
	 */
	private String status;
	/**
	 * 节点 0申请，1审批，2创建，3结束
	 */
	private String node;

	/**
	 * 业务主键
	 */
	private long transactionsId;
	/**
	 * 主键
	 */
	private long transId;
	/**
	 * 环节开始时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp startTime;
	/**
	 * 环节结束时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Timestamp endTime;
	
	public String getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(String applyNum) {
		this.applyNum = applyNum;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getOperationId() {
		return operationId;
	}
	public void setOperationId(long operationId) {
		this.operationId = operationId;
	}
	public String getTranType() {
		return tranType;
	}
	public void setTranType(String tranType) {
		this.tranType = tranType;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public long getTransactionsId() {
		return transactionsId;
	}
	public void setTransactionsId(long transactionsId) {
		this.transactionsId = transactionsId;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public long getTransId() {
		return transId;
	}
	public void setTransId(long transId) {
		this.transId = transId;
	}
	public int getHisType() {
		return hisType;
	}
	public void setHisType(int hisType) {
		this.hisType = hisType;
	}
	 

}
