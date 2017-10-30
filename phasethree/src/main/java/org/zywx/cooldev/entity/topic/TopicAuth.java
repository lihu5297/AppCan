package org.zywx.cooldev.entity.topic;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月21日 上午9:30:57	<br>
 * 
 */
@Entity
@Table(name="T_TOPIC_AUTH")
public class TopicAuth extends BaseEntity{


	private static final long serialVersionUID = -8441088064702904389L;

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
	
}
