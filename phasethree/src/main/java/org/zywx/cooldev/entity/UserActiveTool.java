package org.zywx.cooldev.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name="T_MAN_USER_ACTIVE")
public class UserActiveTool{

	@Id
	 @GenericGenerator(name="systemUUID",strategy="uuid")
	 @GeneratedValue(generator="systemUUID")
	 @Column(name = "id", insertable = true, updatable = false, nullable = false)
	private String id;
	
	private String value;
	
	/**
	 * 团队id或者项目ID
	 */
	private Long entityId;
	
	/**
	 * project 或者team
	 */
	private String entityType; 
	
	private String describ;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	@Column(updatable=false)
	private Timestamp createdAt = new Timestamp(System.currentTimeMillis()); 

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescrib() {
		return describ;
	}

	public void setDescrib(String describ) {
		this.describ = describ;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}


	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	
	
}
