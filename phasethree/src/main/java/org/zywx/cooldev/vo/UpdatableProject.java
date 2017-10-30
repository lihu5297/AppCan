package org.zywx.cooldev.vo;

import org.zywx.cooldev.commons.Enums;

public class UpdatableProject {

	private long id = -1;

	private String name;
	
	private String detail;

	private long categoryId = -1;

	private Enums.PROJECT_TYPE type;
	
	private Enums.PROJECT_STATUS status;

	private Enums.PROJECT_BIZ_LICENSE bizLicense;
	
	private long teamId = -1;

	private String bizCompanyId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public Enums.PROJECT_TYPE getType() {
		return type;
	}

	public void setType(Enums.PROJECT_TYPE type) {
		this.type = type;
	}

	public Enums.PROJECT_STATUS getStatus() {
		return status;
	}

	public void setStatus(Enums.PROJECT_STATUS status) {
		this.status = status;
	}

	public Enums.PROJECT_BIZ_LICENSE getBizLicense() {
		return bizLicense;
	}

	public void setBizLicense(Enums.PROJECT_BIZ_LICENSE bizLicense) {
		this.bizLicense = bizLicense;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public String getBizCompanyId() {
		return bizCompanyId;
	}

	public void setBizCompanyId(String bizCompanyId) {
		this.bizCompanyId = bizCompanyId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
