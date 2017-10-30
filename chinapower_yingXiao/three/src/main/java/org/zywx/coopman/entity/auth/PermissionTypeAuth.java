package org.zywx.coopman.entity.auth;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2016年1月13日 上午9:23:55	<br>
 * 
 */
@Entity
@Table(name="T_PERMISSION_TYPE_AUTH")
public class PermissionTypeAuth extends BaseEntity{

	private static final long serialVersionUID = 663370630774825493L;

	private long roleId;
	
	private long permissionId;
	
	private String enName;
	
	private String cnName;
	
	private long permissionTypeId;

	public long getRoleId() {
		return roleId;
	}

	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	public long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}

	public long getPermissionTypeId() {
		return permissionTypeId;
	}

	public void setPermissionTypeId(long permissionTypeId) {
		this.permissionTypeId = permissionTypeId;
	}

	
	public String getEnName() {
		return enName;
	}

	
	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	
	
}
