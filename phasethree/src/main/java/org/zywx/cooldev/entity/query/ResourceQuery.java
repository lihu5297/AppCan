package org.zywx.cooldev.entity.query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class ResourceQuery {
	
	private String name = "";
	private String teamName = "";
	private Long teamId;
	private String projectName = "";
	private Long projectId;
	private String creator = "";
	private String actor = "";
	private String createdAtStart;
	private String createdAtEnd;
	
	public String getName() {
		return "%"+name+"%";
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getTeamName() {
		return "%"+teamName+"%";
	}
	
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	public Long getTeamId() {
		return teamId;
	}
	
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}
	
	public String getProjectName() {
		return "%"+projectName+"%";
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public Long getProjectId() {
		return projectId;
	}
	
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	
	public String getCreator() {
		return "%"+creator+"%";
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public Timestamp getCreatedAtStart() {
		if(StringUtils.isBlank(createdAtStart)){
			return null;
		}else{
			Date date = new Date();
			try{
				date = sdf.parse(createdAtStart);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new Timestamp(date.getTime());
		}
	}
	public Timestamp getCreatedAtEnd() {
		if(StringUtils.isBlank(createdAtEnd)){
			return null;
		}else{
			Date date = new Date();
			try{
				date = sdf.parse(createdAtEnd);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new Timestamp(date.getTime());
		}
	}

	public void setCreatedAtStart(String createdAtStart) {
		this.createdAtStart = createdAtStart;
	}

	public void setCreatedAtEnd(String createdAtEnd) {
		this.createdAtEnd = createdAtEnd;
	}

	public String getActor() {
		return "%"+actor+"%";
	}

	public void setActor(String actor) {
		this.actor = actor;
	}
	
	
}
