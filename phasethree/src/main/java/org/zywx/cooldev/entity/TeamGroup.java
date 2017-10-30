package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
    * @ClassName: TeamGroup
    * @Description: 团队小组
    * @author wjj
    * @date 2015年8月10日 上午10:32:15
    *
 */
@Entity
@Table(name = "T_TEAM_GROUP")
public class TeamGroup extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1487786542856360830L;

	private String name;
	
	private Long teamId;
	
	@Transient
	private boolean selected = false;
	

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	@Override
	public String toString() {
		return this.getName();
	}
	
	

}
