package org.zywx.coopman.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.process.TaskConfig;

@Controller
@RequestMapping(value="/process")
public class TaskConfigController extends BaseController{
	
	@RequestMapping("/task")
	public ModelAndView getTaskList(HttpServletRequest request){
		List<TaskConfig> tasks = this.taskConfigService.findAll();
		ModelAndView mav = new ModelAndView();
		mav.addObject("tasks", tasks);
		mav.setViewName("process/taskProcess");
		return mav;
		
	}
	
	@RequestMapping(value="/taskConfig")
	public Map<String, Object> getAllTaskConfig(HttpServletRequest request,long taskConfigId){
		return this.getSuccessMap(this.taskConfigService.getTaskConfigExcept(taskConfigId));
	}
	
	@RequestMapping(value="/task/save")
	public Map<String, Object> saveTaskConfig(HttpServletRequest request,long taskId,
			@RequestParam(value="preTask",required=false)List<Long> preTask){
		this.taskConfigService.updatetaskConfigRelate(taskId,preTask);
		return this.getSuccessMap("success");
		
	}
	
}
