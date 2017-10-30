package org.zywx.cooldev.entity.project;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 大项目实体
 * @author zhouxx
 * @date 20170810
 * 
 */
@Entity
@Table(name = "T_PROJECT_PARENT")
public class ProjectParent extends BaseEntity {

	private static final long serialVersionUID = 6568796021562624212L;

	//大项目编号
	private String projectCode="";
	
	//大项目名名称
	private String projectName;
	//大项目描述
	@Column(columnDefinition="longtext")
	private String projectDesc="";
	//创建人id
	private long userId;
	@Transient
	private int projectCount = 0;
	@Transient
	private int appCount = 0;
	@Transient
	private int memberCount = 0;
	@Transient
	private String partFiliale ;// 参与网省字段
	@Transient
	private boolean flag;		//是否可删除标志
	
	public String getProjectCode() {
		return projectCode;
	}
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectDesc() {
		return projectDesc;
	}
	public void setProjectDesc(String projectDesc) {
		this.projectDesc = projectDesc;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getProjectCount() {
		return projectCount;
	}
	public void setProjectCount(int projectCount) {
		this.projectCount = projectCount;
	}
	public int getAppCount() {
		return appCount;
	}
	public void setAppCount(int appCount) {
		this.appCount = appCount;
	}
	public int getMemberCount() {
		return memberCount;
	}
	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String getPartFiliale() {
		return partFiliale;
	}
	public void setPartFiliale(String partFiliale) {
		this.partFiliale = partFiliale;
	}
	
	
}
