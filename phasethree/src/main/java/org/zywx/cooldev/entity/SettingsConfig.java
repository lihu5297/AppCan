package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="T_SETTINGS_CONFIG")
public class SettingsConfig extends BaseEntity{

	/**
	 * @describe 	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月9日 下午6:40:16	<br>
	 * 
	 */
	private static final long serialVersionUID = -979369500943208923L;
		
	private String type;
	
	private String code;
	
	private String value;
	
	private String describ;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
