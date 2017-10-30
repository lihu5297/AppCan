package org.zywx.coopman.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.zywx.coopman.commons.Enums.TASK_STATUS;
import org.zywx.coopman.entity.process.TaskConfig;
import org.zywx.coopman.entity.process.TaskConfigRelate;

@Service
public class TaskConfigService extends BaseService{

	public List<TaskConfig> findAll() {
		List<TaskConfigRelate> taskRelates = (List<TaskConfigRelate>) this.taskConfigRelateDao.findAll();
		
		List<TaskConfig> tasks  = (List<TaskConfig>) this.taskConfigDao.findAll();
		
		return this.dealTasks(taskRelates,tasks);
	}

	private List<TaskConfig> dealTasks(List<TaskConfigRelate> taskRelates, List<TaskConfig> tasks) {
		for(TaskConfigRelate taskConfigRelate : taskRelates){
			if(taskConfigRelate.getNextTaskId()!=null && taskConfigRelate.getNextTaskId()!=-1){
				for(TaskConfig task : tasks){
					if(task.getId() == taskConfigRelate.getNextTaskId()){
						List<TaskConfig> preTasks = task.getPreTasks();
						if(preTasks == null){
							preTasks = new ArrayList<>();
						}
						TaskConfig TaskConfig = this.taskConfigDao.findOne(taskConfigRelate.getTaskConfigId());
						preTasks.add(TaskConfig);
						
						List<TASK_STATUS> statuses = task.getCurStatus();
						if(statuses == null){
							statuses = new ArrayList<>();
						}
						statuses.add(TaskConfig.getStatus());
						
						task.setPreTasks(preTasks);
						task.setCurStatus(statuses);
					}
				}
			}
		}
		return tasks;
	}

	public Map<String, Object> getTaskConfigExcept(long taskConfigId) {
		List<TaskConfig> except = this.taskConfigDao.findById(taskConfigId);
		
		List<TaskConfig> preTask = this.taskConfigDao.findBytaskId(taskConfigId);
		
		Map<String , Object> map = new HashMap<String, Object>();
		map.put("allTaskconfigExcept", except);
		map.put("preTaskConfig", preTask);
		
		return map;
	}

	public void updatetaskConfigRelate(long taskId, List<Long> preTask) {
		String sql="delete from T_MAN_TASK_CONFIG_RELATE where nextTaskId = "+taskId;
		this.jdbcTpl.execute(sql);
		if(preTask==null){
			return ;
		}
		for(Long id : preTask){
			TaskConfigRelate taskConfigRelate = new TaskConfigRelate();
			taskConfigRelate.setNextTaskId(taskId);
			taskConfigRelate.setTaskConfigId(id);
			this.taskConfigRelateDao.save(taskConfigRelate);
		}
		
	}
	
}
