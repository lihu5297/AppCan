package org.zywx.coopman.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 用户与初始化权限的关系表
 * @author 东元
 *
 */
@Entity
@Table(name="T_USER_AUTH")
public class UserAuth extends BaseEntity{

	
	
    /**
     * @Fields serialVersionUID :
     */
	    
	private static final long serialVersionUID = 7736055398588120017L;

	//用户id
	private long userId;
	
	//权限ID
	private long permissionId;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(long permissionId) {
		this.permissionId = permissionId;
	}



	
}
