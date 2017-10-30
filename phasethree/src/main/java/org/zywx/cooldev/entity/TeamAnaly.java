package org.zywx.cooldev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;


/**
 * 
    * @ClassName: TeamAnaly
    * @Description: 团队统计
    * @author yongwen.wang
    * @date 2015年8月8日 下午2:55:53
    *
 */
@Entity
@Table(name = "T_TEAM_ANALY")
public class TeamAnaly extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5807385328304694926L;
	private long teamId;
	private long projectId;
	public long getTeamId() {
		return teamId;
	}
	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	
}
