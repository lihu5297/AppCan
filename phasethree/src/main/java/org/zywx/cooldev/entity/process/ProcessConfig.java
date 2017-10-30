package org.zywx.cooldev.entity.process;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.entity.BaseEntity;
import org.zywx.cooldev.entity.auth.Role;


/**
 * 流程阶段模板的具体配置项<br>
 * 
 * 每个配置项，都包含一个流程阶段相关的创建者、负责人及普通成员应具有的角色列表<br>
 * 
 * @author yang.li
 * @date 2015-09-30
 *
 */
@Entity
@Table(name="T_MAN_PROCESS_CONFIG")
public class ProcessConfig extends BaseEntity{

	private static final long serialVersionUID = 5527833559224096248L;

	// 阶段序列号，从1开始
	private int sequence = -1;
	
	// 阶段名称
	private String name;
	
	// 阶段创建人分配的角色列表
	private String creatorRoleStr;
	
	// 阶段负责人（经理）分配的角色列表
	private String managerRoleStr;
	
	// 阶段成员分配的角色列表
	private String memberRoleStr;
	
	private long processTemplateId;
	
	@Transient
	private List<Role> creatorRoleList;
	
	@Transient
	private List<Role> managerRoleList;
	
	@Transient
	private List<Role> memberRoleList;
	
	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatorRoleStr() {
		return creatorRoleStr;
	}

	public void setCreatorRoleStr(String creatorRoleStr) {
		this.creatorRoleStr = creatorRoleStr;
	}

	public String getManagerRoleStr() {
		return managerRoleStr;
	}

	public void setManagerRoleStr(String managerRoleStr) {
		this.managerRoleStr = managerRoleStr;
	}

	public String getMemberRoleStr() {
		return memberRoleStr;
	}

	public void setMemberRoleStr(String memberRoleStr) {
		this.memberRoleStr = memberRoleStr;
	}

	public List<Role> getCreatorRoleList() {
		return creatorRoleList;
	}

	public void setCreatorRoleList(List<Role> creatorRoleList) {
		this.creatorRoleList = creatorRoleList;
	}

	public List<Role> getManagerRoleList() {
		return managerRoleList;
	}

	public void setManagerRoleList(List<Role> managerRoleList) {
		this.managerRoleList = managerRoleList;
	}

	public List<Role> getMemberRoleList() {
		return memberRoleList;
	}

	public void setMemberRoleList(List<Role> memberRoleList) {
		this.memberRoleList = memberRoleList;
	}

	public long getProcessTemplateId() {
		return processTemplateId;
	}

	public void setProcessTemplateId(long processTemplateId) {
		this.processTemplateId = processTemplateId;
	}


	public String getCreatorRoleNameList() {
		String names = "";
		if(creatorRoleList != null && creatorRoleList.size() > 0) {
			for(Role r : creatorRoleList) {
				names += ( "," + r.getCnName() );
			}
			if(names.length() > 0) {
				names = names.substring(1);
			}
		}
		
		return names;
	}

	
	public String getManagerRoleNameList() {
		String names = "";
		if(managerRoleList != null && managerRoleList.size() > 0) {
			for(Role r : managerRoleList) {
				names += ( "," + r.getCnName() );
			}
			if(names.length() > 0) {
				names = names.substring(1);
			}
		}
		
		return names;
	}

	
	public String getMemberRoleNameList() {
		String names = "";
		if(memberRoleList != null && memberRoleList.size() > 0) {
			for(Role r : memberRoleList) {
				names += ( "," + r.getCnName() );
			}
			if(names.length() > 0) {
				names = names.substring(1);
			}
		}
		
		return names;
	}

}
