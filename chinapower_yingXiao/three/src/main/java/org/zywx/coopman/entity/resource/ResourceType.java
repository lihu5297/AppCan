package org.zywx.coopman.entity.resource;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;
/**
 * 资源类别
 * @author 东元
 *
 */
@Entity
@Table(name = "T_MAN_RESOURCE_TYPE")
public class ResourceType  extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String typeName;	//类型名称

	private String typeCode;

	private String creator;		//创建者
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	
}
