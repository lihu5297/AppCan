package org.zywx.cooldev.entity.trans;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 交易实体
 * @author zhouxx
 * 20170808
 *
 */
@Entity
@Table(name="T_TRANS")
public class Trans extends BaseEntity {

	private static final long serialVersionUID = 1148934999815049435L;

	/**
	 * 发布人
	 */
	private long userId;
	
	/**
	 * 审批类型 0其他， 1数模，2接口，3应用，4子项目，5子项目和应用申请，6移动应用发版申请，7后端应用发版申请
	 */
	private String tranType;
	
	/**
	 * 审批内容
	 */
	private String message;
	
	/**
	 * 审核管理员（签收）
	 */
	private long manageId;
	
	/**
	 * 审批时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp approvalTime;
	
	/**
	 * 审批签收时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp signTime;
	
	/**
	 * 状态 0申请， 1已签收，2未签收，3通过，4未通过,5废弃
	 */
	private String status;
	
	/**
	 * 节点 0申请，1审批，2创建，3结束
	 */
	private String node;
	/**
	 * 创建人Id
	 */
	private long markId;
	/**
	 * 业务主键
	 */
	private long transactionsId;
	
	/**
	 * 交易提交时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp subTime;
	
	/**
	 * 审批签收时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp signMarkTime;
	
	
	/**
	 * 创建应用时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp markTime;
	
	/**
	 * 申请编号
	 */
	private String applyNum;

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

	public long getManageId() {
		return manageId;
	}

	public void setManageId(long manageId) {
		this.manageId = manageId;
	}

	public Timestamp getApprovalTime() {
		return approvalTime;
	}

	public void setApprovalTime(Timestamp approvalTime) {
		this.approvalTime = approvalTime;
	}

	public Timestamp getSignTime() {
		return signTime;
	}

	public void setSignTime(Timestamp signTime) {
		this.signTime = signTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTransactionsId() {
		return transactionsId;
	}

	public void setTransactionsId(long transactionsId) {
		this.transactionsId = transactionsId;
	}

	public Timestamp getSubTime() {
		return subTime;
	}

	public void setSubTime(Timestamp subTime) {
		this.subTime = subTime;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Timestamp getSignMarkTime() {
		return signMarkTime;
	}

	public void setSignMarkTime(Timestamp signMarkTime) {
		this.signMarkTime = signMarkTime;
	}

	public long getMarkId() {
		return markId;
	}

	public void setMarkId(long markId) {
		this.markId = markId;
	}

	public Timestamp getMarkTime() {
		return markTime;
	}

	public void setMarkTime(Timestamp markTime) {
		this.markTime = markTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
 
	

}
