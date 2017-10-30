package org.zywx.cooldev.vo;

import java.util.List;

import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.util.Tools;

public class Match4Project {

	private List<PROJECT_BIZ_LICENSE> bizLicense = null;
	private List<PROJECT_STATUS> status = null;
	private List<PROJECT_TYPE> type = null;
	private List<Long> categoryId = null;
	private List<PROJECT_MEMBER_TYPE> memberType = null;
	private Long teamId = null;
	private String teamName = null;
	private String projectName = null;
	private Long projectId = null;
	private String creator = null;
	private String actor = null;
	private String createdAtStart;
	private String createdAtEnd;
	private String palnAtStart;
	private String palnAtEnd;
	private Long parentId;		//大项目ID

	public List<PROJECT_BIZ_LICENSE> getBizLicense() {
		return bizLicense;
	}
	public void setBizLicense(List<PROJECT_BIZ_LICENSE> bizLicense) {
		this.bizLicense = bizLicense;
	}
	public List<PROJECT_STATUS> getStatus() {
		return status;
	}
	public void setStatus(List<PROJECT_STATUS> status) {
		this.status = status;
	}
	public List<PROJECT_TYPE> getType() {
		return type;
	}
	public void setType(List<PROJECT_TYPE> type) {
		this.type = type;
	}
	public List<Long> getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(List<Long> categoryId) {
		this.categoryId = categoryId;
	}
	public List<PROJECT_MEMBER_TYPE> getMemberType() {
		return memberType;
	}
	public void setMemberType(List<PROJECT_MEMBER_TYPE> memberType) {
		this.memberType = memberType;
	}
	@Override
	public String toString() {
		return "Match4Project [bizLicense=" + bizLicense + ", status=" + status
				+ ", type=" + type + ", categoryId=" + categoryId
				+ ", memberType=" + memberType + "]";
	}
	
	public Long getTeamId() {
		return teamId;
	}
	
	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		if(null!=teamName){
			return "%"+teamName+"%";
		}else
			return teamName;
	}
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	public String getCreator() {
		if(null!=creator){
			return "%"+creator+"%";
		}else
			return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	public String getActor() {
		if(null!=actor){
			return "%"+actor+"%";
		}else
			return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	public Timestamp getCreatedAtStart() {
//		if(null==createdAtStart){
//			return null;
//		}else{
//			Date date = new Date();
//			try{
//				date = sdf.parse(createdAtStart);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			return new Timestamp(date.getTime());
//		}
//	}
	public void setCreatedAtStart(String createdAtStart) {
		this.createdAtStart = createdAtStart;
	}
	
//	public Timestamp getCreatedAtEnd() {
//		if(null==createdAtEnd){
//			return null;
//		}else{
//			Date date = new Date();
//			try{
//				date = sdf.parse(createdAtEnd);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			return new Timestamp(date.getTime());
//		}
//	}
	public void setCreatedAtEnd(String createdAtEnd) {
		this.createdAtEnd = createdAtEnd;
	}
	
	
	
	public String getCreatedAtStart() {
		return createdAtStart;
	}
	public String getCreatedAtEnd() {
		return createdAtEnd;
	}
	public String getProjectName() {
		if(null!=projectName){
			return "%"+projectName+"%";
		}else
			return projectName;
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
//	public Timestamp getPalnAtStart() {
//		if(null==palnAtStart){
//			return null;
//		}else{
//			Date date = new Date();
//			try{
//				date = sdf.parse(palnAtStart);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			return new Timestamp(date.getTime());
//		}
//	}
	public void setPalnAtStart(String palnAtStart) {
		this.palnAtStart = palnAtStart;
	}
//	public Timestamp getPalnAtEnd() {
//		if(null==palnAtEnd){
//			return null;
//		}else{
//			Date date = new Date();
//			try{
//				date = sdf.parse(palnAtEnd);
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			return new Timestamp(date.getTime());
//		}
//	}
	public void setPalnAtEnd(String palnAtEnd) {
		this.palnAtEnd = palnAtEnd;
	}
	public String getPalnAtStart() {
		return palnAtStart;
	}
	public String getPalnAtEnd() {
		return palnAtEnd;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
}
