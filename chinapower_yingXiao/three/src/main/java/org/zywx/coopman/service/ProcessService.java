package org.zywx.coopman.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.ProcessTemplateStatus;
import org.zywx.coopman.dao.RoleDao;
import org.zywx.coopman.dao.process.ProcessConfigDao;
import org.zywx.coopman.dao.process.ProcessTemplateDao;
import org.zywx.coopman.entity.auth.Role;
import org.zywx.coopman.entity.process.ProcessConfig;
import org.zywx.coopman.entity.process.ProcessTemplate;

/**
 * 
 * 流程阶段模板管理
 * @author yang.li
 * @date 2015-09-30
 *
 */
@Service
public class ProcessService extends BaseService {
	
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
				List<ProcessConfig> configList = processConfigDao.findByProcessTemplateIdOrderBySequenceAsc(tpl.getId());
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
	
	//增加流程阶段分页
	public List<ProcessTemplate> getProcessTemplateList(PageRequest page,int processTemplateId) {
		List<ProcessTemplate> tplList = processTemplateDao.findAll();
		Page<ProcessConfig> page1 = null;
		if(tplList != null && tplList.size() > 0) {
			for(ProcessTemplate tpl : tplList) {
				// 扩展模板
				page1 = processConfigDao.findByProcessTemplateIdOrderBySequenceAsc(tpl.getId(),page);
				List<ProcessConfig> configList = page1.getContent();
				if(configList != null && configList.size() > 0) {
					for(ProcessConfig config : configList) {
						// 扩展配置项
						config.setCreatorRoleList( this.getRoleList( config.getCreatorRoleStr() ) );
						config.setManagerRoleList( this.getRoleList( config.getManagerRoleStr() ) );
						config.setMemberRoleList(  this.getRoleList( config.getMemberRoleStr()  ) );
					}
					tpl.setProcessConfigList(configList);
				}
				if (page1 != null && page1.getContent() != null&&tpl.getId()==processTemplateId) {
					//mav.addObject("tplList", page1.getContent());
					tpl.setTotal((int) page1.getTotalElements());
					tpl.setTotalPage(page1.getTotalPages());
					tpl.setCurPage(page.getPageNumber() + 1);
					tpl.setPageSize(page.getPageSize());
				} else if (page1 != null && page1.getContent() != null) {
					//mav.addObject("tplList", page1.getContent());
					tpl.setTotal((int) page1.getTotalElements());
					tpl.setTotalPage(page1.getTotalPages());
					tpl.setCurPage(page.getPageNumber() + 1);
					tpl.setPageSize(page.getPageSize());
				}else {
					//mav.addObject("tplList", null);
					tpl.setTotal(0);
					tpl.setTotalPage(1);
					tpl.setCurPage(1);
					tpl.setPageSize(page.getPageSize());
				}
				tpl.setPage(page1);
			}
		}
		return tplList;
	}
	
	//增加分页的流程模版列表
	public Page<ProcessTemplate> getProcessTemplateListByPage(PageRequest page) {
		Page<ProcessTemplate> page1 = this.processTemplateDao.findAll(page);
		if(null==page1){
			return page1;
		}
		List<ProcessTemplate> tplList = page1.getContent();
		if(tplList != null && tplList.size() > 0) {
			for(ProcessTemplate tpl : tplList) {
				// 扩展模板
				List<ProcessConfig> configList = processConfigDao.findByProcessTemplateIdOrderBySequenceAsc(tpl.getId());
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
		return page1;
	}

	public List<ProcessTemplate> findProcessTemplateByName(String name) {
		return processTemplateDao.findByNameAndDel(name, DELTYPE.NORMAL);
	}
	public ProcessTemplate addProcessTemplateList(String name) {
		ProcessTemplate tpl = new ProcessTemplate();
		tpl.setName(name);
		tpl.setStatus(ProcessTemplateStatus.DISABLE);
		
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
	public List<ProcessConfig> findProcessConfigByNameAndTemplateId(String name, long processTemplateId){
		return processConfigDao.findByNameAndProcessTemplateId(name, processTemplateId);
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
	
	public int deleteProcessConfig(ProcessConfig pc){
		int result = 0;
		
		
		return result; 
	}
	
	/**
	 * 上移下移
	 * 
	 * @param pc
	 * @return
	 */
	public int upDown(Long processId, Long configId, Long configSequence,Long updown) {
		Long tempSequence = configSequence;
		//上移时，和上一个流程阶段交换位置序号；下移时，和下一个流程阶段交换位置序号
		if(0==updown){//上移
			configSequence = tempSequence-1;
		}else if(1==updown){//下移
			configSequence = tempSequence + 1;
		}else{
			return 0;
		}
		
		//更新原有流程阶段
		String updateSql = "update T_MAN_PROCESS_CONFIG pc set pc.sequence = " + configSequence + " where id=" +configId;
		
		//确定受影响的流程阶段--根据流程模版id和变更后的序号查找，
		String affectedSql = "select config.id as id from T_MAN_PROCESS_CONFIG config where config.processTemplateId = " + processId+" and config.sequence = "+configSequence;
		Long affectedConfigId = this.jdbcTpl.queryForObject(affectedSql,Long.class);
		
		//更新受影响的流程阶段，将原序号赋值给该流程阶段
		String updateAffectedSql = "update T_MAN_PROCESS_CONFIG pc set pc.sequence = " + tempSequence + " where id=" +affectedConfigId;
		int k = this.jdbcTpl.update(updateSql);
		int t = this.jdbcTpl.update(updateAffectedSql);
		
		return k+t==2?2:0;		
	}
	
	
	/**
	 * 更新流程阶段
	 * @param pc
	 * @return
	 */
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
		if(ProcessTemplateStatus.DISABLE.compareTo(status)==0){
			return 0;//不让点击禁用,只让点击启用,启用一个的时候，自动会把别的变为禁用
		}
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

	
	public int deleteProcessTemplate(long processId) {
		ProcessTemplate processTemplate = new ProcessTemplate();
		if(processId==1){
			return 0;
		}
		processTemplate.setId(processId);
		this.processTemplateDao.delete(processTemplate);
		return 1;
	}

}
