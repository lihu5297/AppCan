package org.zywx.cooldev.entity.project;

import java.sql.Timestamp;
import java.util.Date;
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
 * 项目实体
 * @author yang.li
 * @date 2015-08-06
 * 
 */
@Entity
@Table(name = "T_PROJECT")
public class Project extends BaseEntity {

	private static final long serialVersionUID = 6568796021562624212L;

	//***************************************************
	//    Project fieds                                 *
	//***************************************************
	@Column(nullable=false, length=50)
	private String name;
	
//	@Column(length=500)
	private String detail="";				//项目详情
	
	private long categoryId = -1;		//项目类别Id

	private Enums.PROJECT_TYPE type;	//0个人项目  1团队项目
	
	private Enums.PROJECT_STATUS status;	//项目状态,0已完成；1进行中

	private Enums.PROJECT_BIZ_LICENSE bizLicense;	//授权状态
	
	private String projectCode="";		//项目编号
	
	/**
	 * 生产环境EMM接入地址
	 */
	private String productionEMMUrl;
	
	/**
	 * 测试环境EMM接入地址
	 */
	private String testingEMMUrl;
	
	/**如果个人项目:默认值-1;   如果团队项目:团队ID*/
	private long teamId = -1;

	/**
	 * 授权企业ID
	 */
	private String bizCompanyId;
	
	/**
	 * 授权企业名称
	 */
	private String bizCompanyName;
	
	//完成时间
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
	@Column(columnDefinition="date")
	private Date finishDate;

	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private String categoryName = "";

	@Transient
	private String teamName = "";
	
	private int progress=0;
	
	@Transient
	private String endDate;//计划截至时间(取项目下属所有流程的最晚截至日期)
	
	@Transient
	private int memberSum;//成员总数
	
	private long creatorId = 0;	//
	@Transient
	private String creator;//项目创建者
	@Transient
	private String userName;//项目创建者
	@Transient
	private long sort;//排序置顶
	@Transient
	private String taskTotal;
	@Transient
	private String taskNoFinishTotal;
	@Transient
	private String bugTotal;
	@Transient
	private String bugNoCloseTotal;
	@Column(updatable=false)
	private String uuid = UUID.randomUUID().toString();		//项目编号，唯一标识
	
	private String pinYinHeadChar;
	
	private String pinYinName;
	
	private Long parentId = 0l;		//所属大项目ID	
	@Transient
	private String parentName;
	@Transient
	private String parentCreatime;
	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	
	public long getTeamId() {
		return teamId;
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

	public String getTaskTotal() {
		return taskTotal;
	}

	public void setTaskTotal(String taskTotal) {
		this.taskTotal = taskTotal;
	}

	public String getTaskNoFinishTotal() {
		return taskNoFinishTotal;
	}

	public void setTaskNoFinishTotal(String taskNoFinishTotal) {
		this.taskNoFinishTotal = taskNoFinishTotal;
	}

	public String getBugTotal() {
		return bugTotal;
	}

	public void setBugTotal(String bugTotal) {
		this.bugTotal = bugTotal;
	}

	public String getBugNoCloseTotal() {
		return bugNoCloseTotal;
	}

	public void setBugNoCloseTotal(String bugNoCloseTotal) {
		this.bugNoCloseTotal = bugNoCloseTotal;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
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

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public Enums.PROJECT_TYPE getType() {
		return type;
	}

	public void setType(Enums.PROJECT_TYPE type) {
		this.type = type;
	}

	public Enums.PROJECT_STATUS getStatus() {
		return status;
	}

	public void setStatus(Enums.PROJECT_STATUS status) {
		this.status = status;
	}

	public Enums.PROJECT_BIZ_LICENSE getBizLicense() {
		return bizLicense;
	}

	public void setBizLicense(Enums.PROJECT_BIZ_LICENSE bizLicense) {
		this.bizLicense = bizLicense;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	
	public int getMemberSum() {
		return memberSum;
	}

	public void setMemberSum(int memberSum) {
		this.memberSum = memberSum;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String toString() {
		return this.getName();
//		return "Project [name=" + name + ", detail=" + detail + ", categoryId=" + categoryId + ", type=" + type
//				+ ", status=" + status + ", bizLicense=" + bizLicense + "]" + super.toString();
	}

	public String getBizCompanyId() {
		return bizCompanyId;
	}

	public void setBizCompanyId(String bizCompanyId) {
		this.bizCompanyId = bizCompanyId;
	}

	public String getProductionEMMUrl() {
		return productionEMMUrl;
	}

	public void setProductionEMMUrl(String productionEMMUrl) {
		this.productionEMMUrl = productionEMMUrl;
	}

	public String getTestingEMMUrl() {
		return testingEMMUrl;
	}

	public void setTestingEMMUrl(String testingEMMUrl) {
		this.testingEMMUrl = testingEMMUrl;
	}


	
	public String getBizCompanyName() {
		return bizCompanyName;
	}


	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setBizCompanyName(String bizCompanyName) {
		this.bizCompanyName = bizCompanyName;
	}

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getParentCreatime() {
		return parentCreatime;
	}

	public void setParentCreatime(String parentCreatime) {
		this.parentCreatime = parentCreatime;
	}
	
}
