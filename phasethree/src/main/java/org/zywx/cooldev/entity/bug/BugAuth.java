package org.zywx.cooldev.entity.bug;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * bug成员鉴权
 * @author yongwen.wang
 * @date 2016-04-20
 * 
 */
@Entity
@Table(name = "T_BUG_AUTH")
public class BugAuth extends BaseEntity  implements Cloneable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long memberId = -1;
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
	private long roleId = -1;

}
