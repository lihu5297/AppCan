package org.zywx.cooldev.entity.auth;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_ROLE_AUTH")
public class RoleAuth extends BaseEntity{

	private static final long serialVersionUID = 2974877211166962740L;

	private long roleId;
	
	private long premissionId;

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public long getPremissionId() {
		return premissionId;
	}

	public void setPremissionId(long premissionId) {
		this.premissionId = premissionId;
	}

}
