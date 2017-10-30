package org.zywx.cooldev.vo;

import java.io.Serializable;
/**
 * 
    * @Description:查询动态时候,返回的封装类 
    * @author jingjian.wu
    * @date 2015年8月18日 下午6:38:35
    *
 */
public class DynamicVO implements Serializable {

	
	    /**
	    * @Fields serialVersionUID :
	    */
	    
	private static final long serialVersionUID = 2127955826974548907L;

	private String info;
	
	private String  createdAt;
	
	private String date;
	
	private String time;
	
	private String icon;
	
	private String account;
	
	private String userName;
	
	private String moduleIcon;//任务动态 图标标识
	

	public String getModuleIcon() {
		return moduleIcon;
	}

	public void setModuleIcon(String moduleIcon) {
		this.moduleIcon = moduleIcon;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
