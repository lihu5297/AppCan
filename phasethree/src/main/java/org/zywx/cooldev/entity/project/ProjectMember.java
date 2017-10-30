package org.zywx.cooldev.entity.project;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.USER_STATUS;
import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.auth.Role;

/**
 * 项目成员
 * @author yang.li
 * @date 2015-08-06
 * 
 */
@Entity
@Table(name = "T_PROJECT_MEMBER")
public class ProjectMember extends BaseEntity {

	private static final long serialVersionUID = 9014232854364191639L;

	private long userId;
	
	private long projectId;
	
	private Enums.PROJECT_MEMBER_TYPE type;
	
	@Transient
	private String userName;
	
	@Transient
	private String userIcon = "";

	@Transient
	private String userAccount = "";
	
	@Transient
	private String userPhone = "";
	
	@Transient
	private String userQQ = "";
	
	@Transient
	private String userAddress = "";
	
	@Transient
	private USER_STATUS userStatus;
	
	@Transient
	private String projectName;
	
	@Transient
	private PROJECT_TYPE projectType;
	
	@Transient
	private List<Role> role;
	
	@Transient
	private Map<String, Integer> permissions;
	
	
	public USER_STATUS getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(USER_STATUS user_STATUS) {
		this.userStatus = user_STATUS;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public Enums.PROJECT_MEMBER_TYPE getType() {
		return type;
	}

	public void setType(Enums.PROJECT_MEMBER_TYPE type) {
		this.type = type;
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

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}

	public PROJECT_TYPE getProjectType() {
		return projectType;
	}

	public void setProjectType(PROJECT_TYPE projectType) {
		this.projectType = projectType;
	}

	public List<Role> getRole() {
		return role;
	}

	public void setRole(List<Role> role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "ProjectMember [userId=" + userId + ", projectId=" + projectId + ", type=" + type + "]" + super.toString();
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserQQ() {
		return userQQ;
	}

	public void setUserQQ(String userQQ) {
		this.userQQ = userQQ;
	}

	public String getUserAddress() {
		return userAddress;
	}

	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	
	public Map<String, Integer> getPermissions() {
		return permissions;
	}

	
	public void setPermissions(Map<String, Integer> permissions) {
		this.permissions = permissions;
	}

	
}
