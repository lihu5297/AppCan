package org.zywx.cooldev.entity.app;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 应用渠道
 * @author yang.li
 *
 */
@Entity
@Table(name="T_APP_CHANNEL")
public class AppChannel extends BaseEntity {

	private static final long serialVersionUID = 7258384847858801530L;

	/**
	 * 渠道号，字符串标记
	 */
	private String code;
	
	/**
	 * 渠道名称
	 */
	private String name;
	
	/**
	 * 具体描述
	 */
	private String detail;

	/**
	 * 应用编号
	 */
	private long appId;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
