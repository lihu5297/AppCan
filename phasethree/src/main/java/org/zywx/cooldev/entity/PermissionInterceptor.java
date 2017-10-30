package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="T_PERMISSION_INTERCEPTOR")
public class PermissionInterceptor extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	private String requestUrl;
	private String method;
	private String tableName;
	private String keyName;
	private String enName;
	private String keyIdType;
	private Integer keyIdIndex;
	
	public String getKeyIdType() {
		return keyIdType;
	}
	public void setKeyIdType(String keyIdType) {
		this.keyIdType = keyIdType;
	}
	public Integer getKeyIdIndex() {
		return keyIdIndex;
	}
	public void setKeyIdIndex(Integer keyIdIndex) {
		this.keyIdIndex = keyIdIndex;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getKeyName() {
		return keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	
}
