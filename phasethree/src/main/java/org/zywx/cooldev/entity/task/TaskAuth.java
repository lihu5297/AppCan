package org.zywx.cooldev.entity.task;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 任务成员鉴权
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_TASK_AUTH")
public class TaskAuth extends BaseEntity  implements Cloneable{

	private static final long serialVersionUID = -2228721117546960261L;

	private long memberId = -1;
	
	private long roleId = -1;

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
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {   
            return super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }  
	}
}
