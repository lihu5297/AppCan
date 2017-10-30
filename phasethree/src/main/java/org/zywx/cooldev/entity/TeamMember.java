package org.zywx.cooldev.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 
    * @ClassName: TeamMember
    * @Description:团队成员 
    * @author wjj
    * @date 2015年8月10日 上午10:32:31
    *
 */
@Entity
@Table(name = "T_TEAM_MEMBER")
public class TeamMember extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3396147456571094820L;

	
	private Long teamId;

	private Long userId;
	
	private Long groupId = -1L;//默认不分组
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp joinTime;
	
	
	@Transient
	private User userInfo;
	
	@Transient
	private List<TeamGroup> groupInfo;
	
	@Transient
	private String role;
	
	@Transient
	private Map<String, Integer> permissions;

	public User getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(User userInfo) {
		this.userInfo = userInfo;
	}

	public List<TeamGroup> getGroupInfo() {
		return groupInfo;
	}

	public void setGroupInfo(List<TeamGroup> groupInfo) {
		this.groupInfo = groupInfo;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Map<String, Integer> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, Integer> permissions) {
		this.permissions = permissions;
	}

	public String getJoinTime() {
		return joinTime == null ? null :TIME_FORMATOR.format(joinTime);
	}

	public void setJoinTime(Timestamp joinTime) {
		this.joinTime = joinTime;
	}

	/**
	 * 0我创建的团队；1我参与的团队;2受邀请成员(还未同意)
	 * 默认类型为我创建的团队
	 */
	private Enums.TEAMREALTIONSHIP type = Enums.TEAMREALTIONSHIP.CREATE;


	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Enums.TEAMREALTIONSHIP getType() {
		return type;
	}

	public void setType(Enums.TEAMREALTIONSHIP type) {
		this.type = type;
	}

	
}
