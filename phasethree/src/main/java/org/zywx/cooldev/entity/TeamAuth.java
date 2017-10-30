package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
    * @ClassName: TeamAuth
    * @Description: 团队权限
    * @author wjj
    * @date 2015年8月10日 上午10:31:51
    *
 */
@Entity
@Table(name = "T_TEAM_AUTH")
public class TeamAuth extends BaseEntity {

	private static final long serialVersionUID = -5931645092251902170L;

	private long memberId;		//团队ID
	
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
