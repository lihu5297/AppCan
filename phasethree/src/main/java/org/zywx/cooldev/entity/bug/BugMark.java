package org.zywx.cooldev.entity.bug;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * bug备注实体
 * @author yongwen.wang
 * @date 2016-04-20
 * 
 */
@Entity
@Table(name = "T_BUG_MARK")
public class BugMark extends BaseEntity implements Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long bugId;
	private long userId;
	private String info;
	@Transient 
	private String userName;
	@Transient
	private String userIcon;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}
	public long getBugId() {
		return bugId;
	}
	public void setBugId(long bugId) {
		this.bugId = bugId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	

}
