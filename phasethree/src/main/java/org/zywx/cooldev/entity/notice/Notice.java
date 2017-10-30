package org.zywx.cooldev.entity.notice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_READ_TYPE;
import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_NOTICE")
public class Notice extends BaseEntity{

	private static final long serialVersionUID = -5480769725603178129L;
	
	//通知启动人
	@Column(name="userId")
	private Long userId;
	
	//通知接受者
	@Column(name="recievedId")
	private Long recievedId;
	
	//模板类型
	@Enumerated(EnumType.STRING)
	@Column(name="noModuleType")
	private NOTICE_MODULE_TYPE noModuleType;
	
	//通知内容
	@Column(name="noInfo")
	private String noInfo;
	
	//通知是否已读
	@Column(name="noRead")
	private NOTICE_READ_TYPE noRead = NOTICE_READ_TYPE.UNREAD;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRecievedId() {
		return recievedId;
	}

	public void setRecievedId(Long recievedId) {
		this.recievedId = recievedId;
	}

	public NOTICE_MODULE_TYPE getNoModuleType() {
		return noModuleType;
	}

	public void setNoModuleType(NOTICE_MODULE_TYPE noModuleType) {
		this.noModuleType = noModuleType;
	}

	public String getNoInfo() {
		return noInfo;
	}

	public void setNoInfo(String noInfo) {
		this.noInfo = noInfo;
	}

	public NOTICE_READ_TYPE getNoRead() {
		return noRead;
	}

	public void setNoRead(NOTICE_READ_TYPE noRead) {
		this.noRead = noRead;
	}
	
}
