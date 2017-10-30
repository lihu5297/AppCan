package org.zywx.coopman.entity.DailyLog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.commons.Enums.ENTITY_TYPE;
import org.zywx.coopman.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月11日 上午10:11:14	<br>
 * 
 */
@Entity
@Table(name="T_MAN_LOG_ACTION")
public class LogAction extends BaseEntity{


	private static final long serialVersionUID = -8113412008160971425L;

	/**
	 * 接口名称
	 */
	@Column(nullable=false, length=50)
	private String name;
	
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
	 * 接口操作对象的实体类型
	 */
	private ENTITY_TYPE targetType;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public ENTITY_TYPE getTargetType() {
		return targetType;
	}

	public void setTargetType(ENTITY_TYPE targetType) {
		this.targetType = targetType;
	}
	
}
