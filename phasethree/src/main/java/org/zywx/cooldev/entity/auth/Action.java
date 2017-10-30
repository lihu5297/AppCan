package org.zywx.cooldev.entity.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 服务接口
 * @author yang.li
 * @date 2015-08-06
 * 
 */
@Entity
@Table(name = "T_ACTION")
public class Action extends BaseEntity {

	private static final long serialVersionUID = -8564419645555044429L;

	/**
	 * 接口名称
	 */
	@Column(nullable=false, length=50)
	private String name;
	
	/**
	 * 调用使用的http方法 POST,PUT,DELETE
	 */
	@Column(nullable=false, length=6)
	private String method;

	/**
	 * 接口样式<br>
	 * 如：<br>
	 * /task
	 * /task/{id}
	 * /project/member/{id}
	 */
	@Column(nullable=false, length=255)
	private String pattern;
	
	/**
	 * 接口权限所关联的实体类型
	 */
	private ENTITY_TYPE authRelatedType;
	
	/**
	 * 接口操作对象的实体类型
	 */
	private ENTITY_TYPE targetType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public ENTITY_TYPE getAuthRelatedType() {
		return authRelatedType;
	}

	public void setAuthRelatedType(ENTITY_TYPE authRelatedType) {
		this.authRelatedType = authRelatedType;
	}

	public ENTITY_TYPE getTargetType() {
		return targetType;
	}

	public void setTargetType(ENTITY_TYPE targetType) {
		this.targetType = targetType;
	}

}
