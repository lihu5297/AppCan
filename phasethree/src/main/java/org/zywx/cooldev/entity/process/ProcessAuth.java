package org.zywx.cooldev.entity.process;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 流程鉴权实体
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_PROCESS_AUTH")
public class ProcessAuth extends BaseEntity {

	private static final long serialVersionUID = -1258649020102917290L;

	private long memberId;
	
	private long roleId;
	
	public long getMemberId() {
		return memberId;
	}
	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}
	public long getRoleId() {
		return roleId;
	}
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

}
