package org.zywx.cooldev.entity.query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DOCQuery {

	private String docName = "";
	private String teamName = "";
	private Long teamId;
	private String projectName = "";
	private Long projectId;
	private String creator = "";
	private String actor = "";
	private String createdAtStart;
	private String createdAtEnd;
	
	public String getDocName() {
		return "%"+docName+"%";
	}
	public void setDocName(String docName) {
		this.docName = docName;
	}
	public String getPinYinName() {
		return docName+"%";
	}
	public String getTeamName() {
		if(null==teamName){
			return "";
		}
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
		if(null==projectName){
			return "";
		}
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
		if(null==creator){
			return "";
		}
		return "%"+creator+"%";
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getActor() {
		if(null==actor){
			return "";
		}
		return "%"+actor+"%";
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public void setCreatedAtStart(String createdAtStart) {
		this.createdAtStart = createdAtStart;
	}
	public void setCreatedAtEnd(String createdAtEnd) {
		this.createdAtEnd = createdAtEnd;
	}
	public String getCreatedAtStart() {
		return createdAtStart;
	}
	public String getCreatedAtEnd() {
		return createdAtEnd;
	}
	
	/*public Timestamp getCreatedAtStart() {
		if(null==createdAtStart){
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
		if(null==createdAtEnd){
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
	}*/
	
	
	
}
