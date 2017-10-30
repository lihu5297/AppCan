package org.zywx.cooldev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="T_ADVICE")
public class Advice extends BaseEntity{

	private static final long serialVersionUID = -2242963794053013920L;

	@Column(columnDefinition="longtext")
	private String content;
	
	private long userId;
	
	@Transient
	private String account;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
