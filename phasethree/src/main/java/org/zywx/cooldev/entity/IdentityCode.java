package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2016年1月12日 下午3:50:21	<br>
 * 
 */
@Entity
@Table(name="T_IDENTITY_CODE")
public class IdentityCode extends BaseEntity{

	private static final long serialVersionUID = -944524324247660353L;

	private String email;
	
	private long userId;
	
	private String code;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
