package org.zywx.coopman.entity.auth;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.entity.BaseEntity;

@Entity
@Table(name="T_PERMISSION_TYPE")
public class PermissionType extends BaseEntity {

	
	/**
	 * 中文名称，用于管理后台显示
	 */
	private String cnName;
	
	@Transient
	private List<Permission> permission;
	

	public List<Permission> getPermission() {
		return permission;
	}

	public void setPermission(List<Permission> permission) {
		this.permission = permission;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	

}
