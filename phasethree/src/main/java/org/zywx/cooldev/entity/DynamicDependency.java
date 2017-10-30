package org.zywx.cooldev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 
 * @describe 动态关系表 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月14日 下午1:44:30	<br>
 *
 */
@Entity
@Table(name="T_DYNAMIC_DEPENDENCY")
public class DynamicDependency extends BaseEntity{
	
	private static final long serialVersionUID = -1234417625180040446L;

	@Column(name="entityType")
	private String entityType;
	
	@Column(name="entityId")
	private Long entityId;
	
	@Column(name="dynamicId")
	private Long dynamicId;

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getDynamicId() {
		return dynamicId;
	}

	public void setDynamicId(Long dynamicId) {
		this.dynamicId = dynamicId;
	}
	
}
