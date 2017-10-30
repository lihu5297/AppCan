package org.zywx.cooldev.entity.bug;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;
/**
 * bug成员实体
 * @author yongwen.wang
 * @date 2016-04-20
 * 
 */
@Entity
@Table(name = "T_BUG_MEMBER")
public class BugMember extends BaseEntity implements Cloneable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long bugId = -1;
	
	private long userId = -1;
	private Enums.BUG_MEMBER_TYPE type = Enums.BUG_MEMBER_TYPE.PARTICIPATOR;
	@Transient
	private String userName;
	@Transient
	private String userIcon;
	@Transient
	private String userAccount;
	@Transient
	private List<String> role = null;
	
	
	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public List<String> getRole() {
		return role;
	}

	public void setRole(List<String> role) {
		this.role = role;
	}

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

	public Enums.BUG_MEMBER_TYPE getType() {
		return type;
	}

	public void setType(Enums.BUG_MEMBER_TYPE type) {
		this.type = type;
	}
}
