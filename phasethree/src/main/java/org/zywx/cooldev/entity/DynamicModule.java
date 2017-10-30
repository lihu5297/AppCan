package org.zywx.cooldev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;

/**
 * 
 * @describe 动态生成模板	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月14日 下午1:40:57	<br>
 *
 */
@Entity
@Table(name="T_DYNAMIC_MODULE")
public class DynamicModule extends BaseEntity{
	
	private static final long serialVersionUID = -4523990722780315148L;

	//格式化字符串
	@Column(name="formatStr")
	private String formatStr;
	
	//模板类型
	@Enumerated(EnumType.STRING)
	@Column(name="moduleType",unique=true)
	private DYNAMIC_MODULE_TYPE moduleType;
	
	//模板图标
	@Column(name="moduleIcon")
	private String moduleIcon;
	

	public String getModuleIcon() {
		return moduleIcon;
	}

	public void setModuleIcon(String moduleIcon) {
		this.moduleIcon = moduleIcon;
	}

	public String getFormatStr() {
		return formatStr;
	}

	public void setFormatStr(String formatStr) {
		this.formatStr = formatStr;
	}

	public DYNAMIC_MODULE_TYPE getModuleType() {
		return moduleType;
	}

	public void setModuleType(DYNAMIC_MODULE_TYPE moduleType) {
		this.moduleType = moduleType;
	}

	
}
