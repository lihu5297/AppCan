package org.zywx.cooldev.entity.process;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 流程成员
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_PROCESS_MEMBER")
public class ProcessMember extends BaseEntity {

	private static final long serialVersionUID = -2809968907249692720L;

	//***************************************************
	//    ProcessMember fieds                           *
	//***************************************************
	private long userId;
	
	private long processId;
	
	private Enums.PROCESS_MEMBER_TYPE type;
	
	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private String userName;
	
	@Transient
	private String userIcon = "";
	
	@Transient
	private String userAccount = "";
	
	@Transient
	private List<String> role = null;
	
	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
	}

	public Enums.PROCESS_MEMBER_TYPE getType() {
		return type;
	}

	public void setType(Enums.PROCESS_MEMBER_TYPE type) {
		this.type = type;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public List<String> getRole() {
		return role;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}

	
	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	
}
