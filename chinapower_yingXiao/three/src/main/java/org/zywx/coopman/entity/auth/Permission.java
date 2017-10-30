package org.zywx.coopman.entity.auth;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.entity.BaseEntity;

@Entity
@Table(name="T_PERMISSION")
public class Permission extends BaseEntity {

	private static final long serialVersionUID = -3589647123599268839L;

	/**
	 * 英文名称，用于前端后端匹配
	 */
	private String enName;
	
	/**
	 * 中文名称，用于管理后台显示
	 */
	private String cnName;
	
	/**
	 * 许可关联的服务接口ID
	 */
	private long actionId;
	/**
	 * 权限列别id
	 */
	private long typeId;
	
	/**
	 * 后台管理修改角色权限的时候,返回是否选中
	 * 0 不选中
	 * 1选中
	 */
	@Transient
	private int selected = 0;
	

	public int getSelected() {
		return selected;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}

	public long getTypeId() {
		return typeId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
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

	public long getActionId() {
		return actionId;
	}

	public void setActionId(long actionId) {
		this.actionId = actionId;
	}

}
