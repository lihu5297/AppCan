package org.zywx.cooldev.entity.project;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 项目鉴权实体
 * @author yang.li
 * @date 2015-08-06
 * 
 */
@Entity
@Table(name = "T_PROJECT_AUTH")
public class ProjectAuth extends BaseEntity {

	private static final long serialVersionUID = -7065211611769916427L;
	
	private long memberId;	//项目ID
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
