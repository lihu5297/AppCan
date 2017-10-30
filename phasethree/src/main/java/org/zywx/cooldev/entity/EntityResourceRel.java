package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;

/**
 * 实体与资源的关联
 * @author yang.li
 * @date 2015-08-24
 * 
 */
@Entity
@Table(name="T_ENTITY_RESOURCE_REL")
public class EntityResourceRel extends BaseEntity implements Cloneable{

	private static final long serialVersionUID = 7507626265608668490L;

	private long entityId;
	
	private ENTITY_TYPE entityType;
	
	private long resourceId;

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public ENTITY_TYPE getEntityType() {
		return entityType;
	}

	public void setEntityType(ENTITY_TYPE entityType) {
		this.entityType = entityType;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
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
