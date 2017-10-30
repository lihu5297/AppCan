package org.zywx.cooldev.entity.process;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.ProcessTemplateStatus;
import org.zywx.cooldev.entity.BaseEntity;

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
