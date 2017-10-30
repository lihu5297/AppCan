package org.zywx.cooldev.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ProcessTemplateStatus;
import org.zywx.cooldev.dao.auth.RoleDao;
import org.zywx.cooldev.dao.process.ProcessConfigDao;
import org.zywx.cooldev.dao.process.ProcessTemplateDao;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.process.ProcessConfig;
import org.zywx.cooldev.entity.process.ProcessTemplate;

/**
 * 
 * 流程阶段模板管理
 * @author yang.li
 * @date 2015-09-30
 *
 */
@Service
public class ProcessConfigService extends BaseService {
	
	@Autowired
	private ProcessTemplateDao processTemplateDao;

	@Autowired
	private ProcessConfigDao processConfigDao;
	
	@Autowired
	private RoleDao roleDao;
	
	/**
	 * 获取模板列表<br>
	 * 对模板的配置详情及角色分配进行扩展<br>
	 * @return
	 */
	public List<ProcessTemplate> getProcessTemplateList() {
		List<ProcessTemplate> tplList = processTemplateDao.findAll();
		
		if(tplList != null && tplList.size() > 0) {
			for(ProcessTemplate tpl : tplList) {
				// 扩展模板
				List<ProcessConfig> configList = processConfigDao.findByProcessTemplateId(tpl.getId());
				if(configList != null && configList.size() > 0) {
					for(ProcessConfig config : configList) {
						// 扩展配置项
						config.setCreatorRoleList( this.getRoleList( config.getCreatorRoleStr() ) );
						config.setManagerRoleList( this.getRoleList( config.getManagerRoleStr() ) );
						config.setMemberRoleList(  this.getRoleList( config.getMemberRoleStr()  ) );
					}
					tpl.setProcessConfigList(configList);
				}
			}
		}
		
		return tplList;
	}
	
	public ProcessTemplate addProcessTemplateList(String name) {
		ProcessTemplate tpl = new ProcessTemplate();
		tpl.setName(name);
		tpl.setStatus(ProcessTemplateStatus.ENABLE);
		
		return processTemplateDao.save(tpl);
	}
	
	public int updateProcessTemplate(ProcessTemplate tpl) {
		String settings = "";
		if(tpl.getName() != null) {
			settings += String.format(",name='%s'", tpl.getName());
		}
		if(tpl.getStatus() != null) {
			settings += String.format(",status=%d", tpl.getStatus().ordinal());
		}
		if(settings.length() > 0) {
			settings = settings.substring(1);
			String sql = "update T_MAN_PROCESS_TEMPLATE set " + settings + " where id=" + tpl.getId();
			return this.jdbcTpl.update(sql);

		} else {
			return 0;
		}
	}
	
	
	public synchronized ProcessConfig addProcessConfig(String name, long processTemplateId) {
		// 自动判定被创建流程的序列号，添加synchronized避免因并发生成相同的序列号
		long total = processConfigDao.countByProcessTemplateId(processTemplateId);
		int sequence = new Long(total).intValue() + 1;
		
		ProcessConfig pc = new ProcessConfig();
		pc.setName(name);
		pc.setProcessTemplateId(processTemplateId);
		pc.setSequence(sequence);

		return processConfigDao.save(pc);
	}
	
	public ProcessConfig getProcessConfig(long id) {
		ProcessConfig config = processConfigDao.findOne(id);
		if(config != null) {
			config.setCreatorRoleList( this.getRoleList( config.getCreatorRoleStr() ) );
			config.setManagerRoleList( this.getRoleList( config.getManagerRoleStr() ) );
			config.setMemberRoleList(  this.getRoleList( config.getMemberRoleStr()  ) );
		}
		return config;
	}
	
	public int updateProcessConfig(ProcessConfig pc) {
		String settings = "";
		if(pc.getCreatorRoleStr() != null) {
			settings += String.format(",creatorRoleStr='%s'", pc.getCreatorRoleStr());
		}
		if(pc.getManagerRoleStr() != null) {
			settings += String.format(",managerRoleStr='%s'", pc.getManagerRoleStr());
		}
		if(pc.getMemberRoleStr() != null) {
			settings += String.format(",memberRoleStr='%s'", pc.getMemberRoleStr());
		}
		if(settings.length() > 0) {
			settings = settings.substring(1);
			String sql = "update T_MAN_PROCESS_CONFIG set " + settings + " where id=" + pc.getId();
			return this.jdbcTpl.update(sql);

		} else {
			return 0;
		}		
	}
	
	private List<Role> getRoleList(String roleIdStr) {
		if(roleIdStr != null) {
			List<Long> idList = new ArrayList<>();
			
			String[] items = roleIdStr.split(",");
			if(items.length > 0) {
				for(String item : items) {
					idList.add(Long.parseLong(item));
				}
			}
			
			if(idList.size() > 0) {
				return roleDao.findByIdIn(idList);
			}
		}
		
		return new ArrayList<Role>();
	
	}

	
	public int updateStatus(long processId, ProcessTemplateStatus status) {
		ProcessTemplate pt = this.processTemplateDao.findByIdAndDel(processId,DELTYPE.NORMAL);
		if(pt!=null){
			pt.setStatus(status);
			this.processTemplateDao.save(pt);
			if(ProcessTemplateStatus.DISABLE.compareTo(status)==0){
				status = ProcessTemplateStatus.ENABLE;
			}else
				status = ProcessTemplateStatus.DISABLE;
			String sql = "update T_MAN_PROCESS_TEMPLATE set status = " + status.ordinal() + " where id <>" + processId;
			this.jdbcTpl.update(sql);
			
			return 1;
		}
		return 0;
		
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年10月15日 下午7:01:30
	 */
	    
	public List<ProcessConfig> getEnableProcessConfigList() {
		ProcessTemplate pt = this.processTemplateDao.findByStatusAndDel(ProcessTemplateStatus.ENABLE, DELTYPE.NORMAL);
		return this.processConfigDao.findByProcessTemplateIdOrderBySequenceAsc(pt.getId());
	}

}
