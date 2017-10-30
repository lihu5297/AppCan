package org.zywx.cooldev.entity.process;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.zywx.cooldev.commons.Enums.PROCESS_STATUS;
import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.Resource;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 流程阶段实体
 * @author yang.li
 * @date 2015-08-12
 * 
 */
@Entity
@Table(name = "T_PROCESS")
public class Process extends BaseEntity {

	private static final long serialVersionUID = -2587984317414001937L;

	//***************************************************
	//    Process fieds                                 *
	//***************************************************
	@Column(nullable=false, length=50)
	private String name;
	
	@Column(length=500)
	private String detail;
	
	private int weight = -1;		//权重
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	private Date startDate;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	private Date endDate;
	
	@Column(nullable=false, updatable=false)
	private long projectId = -1;

	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	/**
	 * 进度
	 */
	private int progress;
	
	/**
	 * 相关资源数量
	 */
	@Transient
	private long resourceTotal;

	/**
	 * 相关成员数量
	 */
	@Transient
	private long memberTotal;
	
	/**
	 * 相关任务数量
	 */
	@Transient
	private long taskTotal;
	/**
	 * 相关任务未完成数量
	 */
	@Transient
	private long taskUnfinishTotal;
	/**
	 * 相关BUG数量
	 */
	@Transient
	private long bugTotal;
	/**
	 * 相关BUG未完成数量
	 */
	@Transient
	private long bugUnfinishTotal;

	@Transient
	private ProcessMember leader;
	
	@Transient
	private List<ProcessMember> member;
	
	@Transient
	private List<Resource> resource;
	
	@Transient
	private String projectName;

	@Transient
	private Map<String, Integer> permissions;
	
	@Column(name="status")
	private PROCESS_STATUS status = PROCESS_STATUS.NORMAL;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	private Date finishDate;
	
	private String pinYinHeadChar;
	
	private String pinYinName;
	
	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	
	
	public Date getFinishDate() {
		return finishDate;
	}

	public String getPinYinHeadChar() {
		return pinYinHeadChar;
	}

	public void setPinYinHeadChar(String pinYinHeadChar) {
		this.pinYinHeadChar = pinYinHeadChar;
	}

	public String getPinYinName() {
		return pinYinName;
	}

	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public long getResourceTotal() {
		return resourceTotal;
	}

	public void setResourceTotal(long resourceTotal) {
		this.resourceTotal = resourceTotal;
	}

	public long getMemberTotal() {
		return memberTotal;
	}

	public void setMemberTotal(long memberTotal) {
		this.memberTotal = memberTotal;
	}

	public long getTaskTotal() {
		return taskTotal;
	}

	public void setTaskTotal(long taskTotal) {
		this.taskTotal = taskTotal;
	}

	public ProcessMember getLeader() {
		return leader;
	}

	public void setLeader(ProcessMember leader) {
		this.leader = leader;
	}

	public List<ProcessMember> getMember() {
		return member;
	}

	public void setMember(List<ProcessMember> member) {
		this.member = member;
	}

	public List<Resource> getResource() {
		return resource;
	}

	public void setResource(List<Resource> resource) {
		this.resource = resource;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	public Map<String, Integer> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, Integer> permissions) {
		this.permissions = permissions;
	}

	
	public PROCESS_STATUS getStatus() {
		return status;
	}

	public void setStatus(PROCESS_STATUS status) {
		this.status = status;
	}

	public long getTaskUnfinishTotal() {
		return taskUnfinishTotal;
	}

	public void setTaskUnfinishTotal(long taskUnfinishTotal) {
		this.taskUnfinishTotal = taskUnfinishTotal;
	}

	public long getBugTotal() {
		return bugTotal;
	}

	public void setBugTotal(long bugTotal) {
		this.bugTotal = bugTotal;
	}

	public long getBugUnfinishTotal() {
		return bugUnfinishTotal;
	}

	public void setBugUnfinishTotal(long bugUnfinishTotal) {
		this.bugUnfinishTotal = bugUnfinishTotal;
	}
	
	
}
