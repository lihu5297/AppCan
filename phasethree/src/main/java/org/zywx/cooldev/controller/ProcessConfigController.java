package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.entity.process.ProcessConfig;
import org.zywx.cooldev.service.ProcessConfigService;
import org.zywx.cooldev.service.RoleService;


@Controller
@RequestMapping(value="/processConfig")
public class ProcessConfigController extends BaseController {
	@Autowired
	private ProcessConfigService processService;
	
	@Autowired
	private RoleService roleService;
	
	
	/**
	 * 获取目前启用的流程阶段模板下的流程阶段列表
	 * 创建流程时候,需要读取后台配置的流程
	 * @user jingjian.wu
	 * @date 2015年10月15日 下午7:00:34
	 */
	@ResponseBody
	@RequestMapping(value="/enableProcessConfigList", method=RequestMethod.GET)
	public Map<String, Object> enableProcessConfigList() {
		List<ProcessConfig> tplList = new ArrayList<ProcessConfig>();
		try {
			tplList = this.processService.getEnableProcessConfigList();
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
			
		}
		return this.getSuccessMap(tplList);
	}
	
	
	/**
	 * 流程阶段模板列表
	 * @author yang.li
	 * @date 2015-09-20
	 */
	/*@RequestMapping(value="/template", method=RequestMethod.GET)
	public ModelAndView getProcessTemplateList() {
		List<ProcessTemplate> tplList = this.processService.getProcessTemplateList();
		ModelAndView mav = new ModelAndView();
		mav.setViewName("process/index");
		mav.addObject("tplList", tplList);

		return mav;
	}*/
	
	/**
	 * 创建流程
	 * @author yang.li
	 * @date 2015-09-20
	 */
	/*@ResponseBody
	@RequestMapping(value="/template", method=RequestMethod.POST)
	public Map<String, Object> addProcessTemplate(
			@RequestParam(value="name") String name ) {

		ProcessTemplate pc = this.processService.addProcessTemplateList(name);
		
		return this.getSuccessMap(pc);
		
	}*/
	
	/**
	 * 创建流程阶段
	 * @author yang.li
	 * @date 2015-09-20
	 */
	/*@ResponseBody
	@RequestMapping(value="/config", method=RequestMethod.POST)
	public Map<String, Object> addProcessConfig(
			@RequestParam(value="processTemplateId") long processTemplateId,
			@RequestParam(value="name") String name ) {

		ProcessConfig pc = this.processService.addProcessConfig(name, processTemplateId);
		return this.getSuccessMap(pc);
		
	}*/

	/*@ResponseBody
	@RequestMapping(value="/config/{id}", method=RequestMethod.GET)
	public Map<String, Object> getProcessConfig(@PathVariable(value="id") long id) {
		
		ProcessConfig pc = this.processService.getProcessConfig(id);
		List<Role> roleList = this.roleService.findRoleList();
		
		Map<String, Object> message = new HashMap<>();
		message.put("roleList", roleList);
		message.put("creatorRoleStr", pc.getCreatorRoleStr() == null ? "" : pc.getCreatorRoleStr());
		message.put("managerRoleStr", pc.getManagerRoleStr() == null ? "" : pc.getManagerRoleStr());
		message.put("memberRoleStr",  pc.getMemberRoleStr()  == null ? "" : pc.getMemberRoleStr());

		return this.getSuccessMap(message);
	}*/

	/*@ResponseBody
	@RequestMapping(value="/config/edit", method=RequestMethod.POST)
	public Map<String, Object> editProcessConfig(
			ProcessConfig pc ) {

		int affected = this.processService.updateProcessConfig(pc);
		Map<String, Object> map = new HashMap<>();
		map.put("affected", affected);
		return this.getSuccessMap(map);
		
	}*/
	
	/**
	 * 
	 * @describe 启用停用流程	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月14日 下午6:47:23	<br>
	 * @param request
	 * @param processId
	 * @param status
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	/*@ResponseBody
	@RequestMapping(value="/changestatus/{processId}",method=RequestMethod.POST)
	public Map<String, Object> changeStaus(HttpServletRequest request,@PathVariable("processId")long processId,
			ProcessTemplateStatus status){
		
		int a = this.processService.updateStatus(processId,status);
		Map<String, Object> map = new HashMap<>();
		map.put("affected", a);
		return this.getSuccessMap(map);
		
	}*/
}
