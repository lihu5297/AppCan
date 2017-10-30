package org.zywx.cooldev.entity.auth;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.ROLE_ALLOW_DEL;
import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_ROLE")
public class Role extends BaseEntity{

	private static final long serialVersionUID = 9027873412254884411L;

	private String enName;	// 英文名称
	
	private String cnName;	// 中文名称
	
	private long parentId = -1;		// 父角色编号
	
	private ROLE_ALLOW_DEL  allowdel;	//允许删除
	
	
	public ROLE_ALLOW_DEL getAllowdel() {
		return allowdel;
	}

	public void setAllowdel(ROLE_ALLOW_DEL allowdel) {
		this.allowdel = allowdel;
	}

	@Transient
	private List<Permission> permissions;	// 角色具有的许可集合

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

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public List<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	
}
