package org.zywx.cooldev.entity.bug;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.domain.Page;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.Dynamic;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * bug实体
 * 
 * @author yongwen.wang
 * @date 2016-04-20
 * 
 */
@Entity
@Table(name = "T_BUG")
public class Bug extends BaseEntity implements Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 标题
	private String title;
	// 描述
	private String detail;
	// 流程id
	private long processId = -1;
	// 应用id
	private long appId = -1;
	// bug状态
	private Enums.BUG_STATUS status;
	// bug解决方案
	private Enums.BUG_SOLUTION solution;
	// 模块id
	private long moduleId = -1;//默认
	// bug优先级
	private Enums.BUG_PRIORITY priority;
	// 影响版本
	private String affectVersion;
	// 解决版本
	private String resolveVersion;
	// 解决人
	private long resolveUserId;
	private long lastModifyUserId;
	private long closeUserId;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp resolveAt;
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp closeAt;
	@Transient
	private String markInfo;
	// 模块
	@Transient
	private String module;
	// 被指派的人
	@Transient
	private long assignedUserId;
	// 参与人
	@Transient
	private List<Long> memberUserIdList;
	// 资源
	@Transient
	private List<Long> resourceIdList;
	// 项目id
	@Transient
	private long projectId;
	// 应用名称
	@Transient
	private String appName;
	// 项目名称
	@Transient
	private String projectName;
	// 流程名称
	@Transient
	private String processName;
	//查询bug详情输出 指派人相关信息，如果要输出姓名，icon在bugMember里加入参数
	@Transient
	private BugMember assignedPerson;
	//查询bug详情输出 参与人相关信息，如果要输出姓名，icon在bugMember里加入参数
	@Transient
	private List<BugMember> member;
	//查询bug详情输出 创建者相关信息
	@Transient
	private User creator;
	@Transient
	private List<Resource> resource;
	@Transient
	private List<BugMark> marks;
	@Transient
	private Page<Dynamic> dynamic;
	@Transient
	private String statusName;
	@Transient
	private String search;
	@Transient
	private String teamName;
	@Transient
	private long creatorUserId;
	@Transient
	private long assignedPersonUserId;
	@Transient
	private long memberUserId;
	@Transient
	private String sortType;
	@Transient
	private String createAtStart;
	@Transient
	private String createAtEnd;
	@Transient
	private String resolveAtStart;
	@Transient
	private String resolveAtEnd;
	@Transient
	private String closeAtStart;
	@Transient
	private String closeAtEnd;
	@Transient
	private String updatedAtStart;
	@Transient
	private String updatedAtEnd;
    @Transient
    private int sortNum=-1;
    @Transient 
    private String teamIdList;
    @Transient
    private String projectIdList;
    @Transient
    private String processIdList;
    @Transient
    private String appIdList;
    @Transient
    private String moduleName;
	@Transient
	private List<Enums.BUG_STATUS> bugStatusList;
	@Transient
	private List<Enums.BUG_PRIORITY> bugPriorityList;
	@Transient
	private List<Enums.BUG_SOLUTION> bugSolutionList;
	@Transient
	private String creatorName;
	@Transient
	private String assignedPersonName;
	@Transient
	private String memberName;
	@Transient
	private String resolveName;
	@Transient
	private String lastModifyName;
	@Transient
	private String closeName;
	@Transient
	private String orderBy;
	@Transient
	private String sortBy;
	
	
	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getAssignedPersonName() {
		return assignedPersonName;
	}

	public void setAssignedPersonName(String assignedPersonName) {
		this.assignedPersonName = assignedPersonName;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getResolveName() {
		return resolveName;
	}

	public void setResolveName(String resolveName) {
		this.resolveName = resolveName;
	}

	public String getLastModifyName() {
		return lastModifyName;
	}

	public void setLastModifyName(String lastModifyName) {
		this.lastModifyName = lastModifyName;
	}

	public String getCloseName() {
		return closeName;
	}

	public void setCloseName(String closeName) {
		this.closeName = closeName;
	}

	public List<Enums.BUG_STATUS> getBugStatusList() {
		return bugStatusList;
	}

	public void setBugStatusList(List<Enums.BUG_STATUS> bugStatusList) {
		this.bugStatusList = bugStatusList;
	}

	public List<Enums.BUG_PRIORITY> getBugPriorityList() {
		return bugPriorityList;
	}

	public void setBugPriorityList(List<Enums.BUG_PRIORITY> bugPriorityList) {
		this.bugPriorityList = bugPriorityList;
	}

	public List<Enums.BUG_SOLUTION> getBugSolutionList() {
		return bugSolutionList;
	}

	public void setBugSolutionList(List<Enums.BUG_SOLUTION> bugSolutionList) {
		this.bugSolutionList = bugSolutionList;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Page<Dynamic> getDynamic() {
		return dynamic;
	}

	public void setDynamic(Page<Dynamic> dynamic) {
		this.dynamic = dynamic;
	}

	public String getAppIdList() {
		return appIdList;
	}

	public void setAppIdList(String appIdList) {
		this.appIdList = appIdList;
	}

	public String getTeamIdList() {
		return teamIdList;
	}

	public void setTeamIdList(String teamIdList) {
		this.teamIdList = teamIdList;
	}

	public String getProjectIdList() {
		return projectIdList;
	}

	public void setProjectIdList(String projectIdList) {
		this.projectIdList = projectIdList;
	}

	public String getProcessIdList() {
		return processIdList;
	}

	public void setProcessIdList(String processIdList) {
		this.processIdList = processIdList;
	}

	public int getSortNum() {
		return sortNum;
	}

	public void setSortNum(int sortNum) {
		this.sortNum = sortNum;
	}

	
	public String getCreateAtStart() {
		return createAtStart;
	}

	public void setCreateAtStart(String createAtStart) {
		this.createAtStart = createAtStart;
	}

	public String getCreateAtEnd() {
		return createAtEnd;
	}

	public void setCreateAtEnd(String createAtEnd) {
		this.createAtEnd = createAtEnd;
	}

	public String getResolveAtStart() {
		return resolveAtStart;
	}

	public void setResolveAtStart(String resolveAtStart) {
		this.resolveAtStart = resolveAtStart;
	}

	public String getResolveAtEnd() {
		return resolveAtEnd;
	}

	public void setResolveAtEnd(String resolveAtEnd) {
		this.resolveAtEnd = resolveAtEnd;
	}

	public String getCloseAtStart() {
		return closeAtStart;
	}

	public void setCloseAtStart(String closeAtStart) {
		this.closeAtStart = closeAtStart;
	}

	public String getCloseAtEnd() {
		return closeAtEnd;
	}

	public void setCloseAtEnd(String closeAtEnd) {
		this.closeAtEnd = closeAtEnd;
	}

	public String getUpdatedAtStart() {
		return updatedAtStart;
	}

	public void setUpdatedAtStart(String updatedAtStart) {
		this.updatedAtStart = updatedAtStart;
	}

	public String getUpdatedAtEnd() {
		return updatedAtEnd;
	}

	public void setUpdatedAtEnd(String updatedAtEnd) {
		this.updatedAtEnd = updatedAtEnd;
	}

	public long getLastModifyUserId() {
		return lastModifyUserId;
	}

	public void setLastModifyUserId(long lastModifyUserId) {
		this.lastModifyUserId = lastModifyUserId;
	}

	public long getCloseUserId() {
		return closeUserId;
	}

	public void setCloseUserId(long closeUserId) {
		this.closeUserId = closeUserId;
	}

	public Timestamp getResolveAt() {
		return resolveAt;
	}

	public void setResolveAt(Timestamp resolveAt) {
		this.resolveAt = resolveAt;
	}

	public Timestamp getCloseAt() {
		return closeAt;
	}

	public void setCloseAt(Timestamp closeAt) {
		this.closeAt = closeAt;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public long getCreatorUserId() {
		return creatorUserId;
	}

	public void setCreatorUserId(long creatorUserId) {
		this.creatorUserId = creatorUserId;
	}

	public long getAssignedPersonUserId() {
		return assignedPersonUserId;
	}

	public void setAssignedPersonUserId(long assignedPersonUserId) {
		this.assignedPersonUserId = assignedPersonUserId;
	}

	public long getMemberUserId() {
		return memberUserId;
	}

	public void setMemberUserId(long memberUserId) {
		this.memberUserId = memberUserId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getSearch() {
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public List<BugMark> getMarks() {
		return marks;
	}

	public void setMarks(List<BugMark> marks) {
		this.marks = marks;
	}

	public List<Resource> getResource() {
		return resource;
	}

	public void setResource(List<Resource> resource) {
		this.resource = resource;
	}

	public BugMember getAssignedPerson() {
		return assignedPerson;
	}

	public void setAssignedPerson(BugMember assignedPerson) {
		this.assignedPerson = assignedPerson;
	}

	public List<BugMember> getMember() {
		return member;
	}

	public void setMember(List<BugMember> member) {
		this.member = member;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public long getProjectId() {
		return projectId;
	}

	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}

	public String getMarkInfo() {
		return markInfo;
	}

	public void setMarkInfo(String markInfo) {
		this.markInfo = markInfo;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public long getAssignedUserId() {
		return assignedUserId;
	}

	public void setAssignedUserId(long assignedUserId) {
		this.assignedUserId = assignedUserId;
	}

	public List<Long> getMemberUserIdList() {
		return memberUserIdList;
	}

	public void setMemberUserIdList(List<Long> memberUserIdList) {
		this.memberUserIdList = memberUserIdList;
	}

	public List<Long> getResourceIdList() {
		return resourceIdList;
	}

	public void setResourceIdList(List<Long> resourceIdList) {
		this.resourceIdList = resourceIdList;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
	}

	public long getAppId() {
		return appId;
	}

	public void setAppId(long appId) {
		this.appId = appId;
	}

	public Enums.BUG_STATUS getStatus() {
		return status;
	}

	public void setStatus(Enums.BUG_STATUS status) {
		this.status = status;
	}

	public Enums.BUG_SOLUTION getSolution() {
		return solution;
	}

	public void setSolution(Enums.BUG_SOLUTION solution) {
		this.solution = solution;
	}

	public long getModuleId() {
		return moduleId;
	}

	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}

	public Enums.BUG_PRIORITY getPriority() {
		return priority;
	}

	public void setPriority(Enums.BUG_PRIORITY priority) {
		this.priority = priority;
	}

	public String getAffectVersion() {
		return affectVersion;
	}

	public void setAffectVersion(String affectVersion) {
		this.affectVersion = affectVersion;
	}

	public String getResolveVersion() {
		return resolveVersion;
	}

	public void setResolveVersion(String resolveVersion) {
		this.resolveVersion = resolveVersion;
	}

	public long getResolveUserId() {
		return resolveUserId;
	}

	public void setResolveUserId(long resolveUserId) {
		this.resolveUserId = resolveUserId;
	}

	@Override
	public String toString() {
		return this.title;
	}
}
