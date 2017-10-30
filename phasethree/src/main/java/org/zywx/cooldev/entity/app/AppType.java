package org.zywx.cooldev.entity.app;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;
/**
 * 应用类型
 * @author 东元
 *
 */

@Entity
@Table(name="T_APP_TYPE")
public class AppType extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String typeName;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	
}
