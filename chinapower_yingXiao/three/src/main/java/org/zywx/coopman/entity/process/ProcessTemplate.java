package org.zywx.coopman.entity.process;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.domain.Page;
import org.zywx.coopman.commons.Enums.ProcessTemplateStatus;
import org.zywx.coopman.entity.BaseEntity;

/**
 * 
 * 流程配置模板<br>
 * 每个模板包含一组流程配置（ProcessConfig）<br>
 * 
 * @author yang.li
 * @date 2015-09-30
 *
 */
@Entity
@Table(name="T_MAN_PROCESS_TEMPLATE")
public class ProcessTemplate extends BaseEntity {

	private static final long serialVersionUID = 1919775448489782242L;

	// 模板名称
	private String name;
	
	// 模板启用状态
	private ProcessTemplateStatus status;
	
	// 属于模板的阶段配置列表
	@Transient
	private List<ProcessConfig> processConfigList;
	
	// 流程模板的流程阶段配置分页
	@Transient
	private Page<ProcessConfig> page;
	@Transient
	private int total;//总条数
	@Transient
	private int totalPage;//总页数
	@Transient
	private int curPage;//当前页
	@Transient
	private int pageSize;//页大小
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	
	public Page<ProcessConfig> getPage() {
		return page;
	}

	public void setPage(Page<ProcessConfig> page) {
		this.page = page;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProcessTemplateStatus getStatus() {
		return status;
	}

	public void setStatus(ProcessTemplateStatus status) {
		this.status = status;
	}

	public List<ProcessConfig> getProcessConfigList() {
		return processConfigList;
	}

	public void setProcessConfigList(List<ProcessConfig> processConfigList) {
		this.processConfigList = processConfigList;
	}

}
