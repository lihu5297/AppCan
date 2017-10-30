package org.zywx.coopman.entity.module;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月16日 下午2:11:47	<br>
 * 
 */
@Entity
@Table(name="T_MAN_MANAGER_AUTH")
public class ManagerAuth extends BaseEntity{

	private static final long serialVersionUID = -5094005262356257986L;

	private long managerId;
	private long moduleId;
	public long getManagerId() {
		return managerId;
	}
	public void setManagerId(long managerId) {
		this.managerId = managerId;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	
}
