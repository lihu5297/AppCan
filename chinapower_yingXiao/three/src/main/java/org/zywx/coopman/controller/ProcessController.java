package org.zywx.coopman.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums.ProcessTemplateStatus;
import org.zywx.coopman.entity.auth.Role;
import org.zywx.coopman.entity.process.ProcessConfig;
import org.zywx.coopman.entity.process.ProcessTemplate;
import org.zywx.coopman.service.ProcessService;


@Controller
@RequestMapping(value="/process")
public class ProcessController extends BaseController {
	@Autowired
	private ProcessService processService;
	
	/**
	 * 流程阶段模板列表
	 * @author yang.li
	 * @date 2015-09-20
	 */
	@RequestMapping(value="/template/list", method=RequestMethod.GET)
	public ModelAndView getProcessTemplateList(HttpServletRequest request,
			@RequestParam(value = "processTemplateId", required = false) Integer processTemplateId,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize
			) {
		//返回参数
		ModelAndView mav = new ModelAndView();
		mav.setViewName("process/index");
		
		//分页逻辑
		/*int ipageNo = 0;
		int ipageSize = 10;
		try {
			if (pageNo != null && pageNo != 0) {
				ipageNo = pageNo - 1;
				ipageSize = pageSize;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("").addObject("pageNo or pageSize is illegal");
		}
		PageRequest page = new PageRequest(ipageNo, ipageSize, Direction.DESC, "createdAt");
		Page<ProcessTemplate> page1 = null;
		
		page1 = this.processService.getProcessTemplateListByPage(page);
		
		if (page1 != null && page1.getContent() != null) {
			mav.addObject("tplList", page1.getContent());
			mav.addObject("total", page1.getTotalElements());
			mav.addObject("totalPage", page1.getTotalPages());
			mav.addObject("curPage", ipageNo + 1);
			mav.addObject("pageSize", ipageSize);
		} else {
			mav.addObject("tplList", null);
			mav.addObject("total", 0);
			mav.addObject("totalPage", 0);
			mav.addObject("curPage", 1);
			mav.addObject("pageSize", ipageSize);
		}*/
		
//		List<ProcessTemplate> tplList = this.processService.getProcessTemplateList();
//		mav.addObject("tplList", tplList);
		
		int ipageNo = 0;
		int ipageSize = 10;
		try {
			if (pageNo != null && pageNo != 0) {
				ipageNo = pageNo - 1;
				ipageSize = pageSize;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("").addObject("pageNo or pageSize is illegal");
		}
		PageRequest page = new PageRequest(ipageNo, ipageSize, Direction.ASC, "sequence");
		
		if(processTemplateId==null){
			processTemplateId = 1;
		}
		List<ProcessTemplate> tplList = this.processService.getProcessTemplateList(page,processTemplateId);
		mav.addObject("tplList", tplList);
		mav.addObject("processTemplateId", processTemplateId);
		//Page<ProcessConfig> page1 = null;
		//List<ProcessConfig> configList =  (List<ProcessConfig>) tplList.get(0);
		
//		if(tplList != null && tplList.size() > 0) {
//			for(ProcessTemplate tpl : tplList) {
				// 扩展模板
				//page1 = tpl.getPage();
				
//				if (page1 != null && page1.getContent() != null) {
//					//mav.addObject("tplList", page1.getContent());
//					mav.addObject("total", page1.getTotalElements());
//					mav.addObject("totalPage", page1.getTotalPages());
//					mav.addObject("curPage", ipageNo + 1);
//					mav.addObject("pageSize", ipageSize);
//				} else {
//					//mav.addObject("tplList", null);
//					mav.addObject("total", 0);
//					mav.addObject("totalPage", 0);
//					mav.addObject("curPage", 1);
//					mav.addObject("pageSize", ipageSize);
//				}
				
//			}
//		}
		
		
		
		
		return mav;
	}
	
	/**
	 * 创建流程
	 * @author yang.li
	 * @date 2015-09-20
	 */
	@ResponseBody
	@RequestMapping(value="/template", method=RequestMethod.POST)
	public Map<String, Object> addProcessTemplate(
			HttpServletRequest request,
			@RequestParam(value="name") String name ) {
		if(StringUtils.isBlank("name")){
			return this.getFailedMap("流程模板名称不能为空");
		}
		List<ProcessTemplate> proList = processService.findProcessTemplateByName(name);
		if(proList != null && !proList.isEmpty()){
			return this.getFailedMap("流程模板名称已存在");
		}
		ProcessTemplate pc = this.processService.addProcessTemplateList(name);
		
		return this.getSuccessMap(pc.getId());
		//return this.getSuccessMap(pc);
	}
	
	/**
	 * 创建流程阶段
	 * @author yang.li
	 * @date 2015-09-20
	 */
	@ResponseBody
	@RequestMapping(value="/config", method=RequestMethod.POST)
	public Map<String, Object> addProcessConfig(
			HttpServletRequest request,
			@RequestParam(value="processTemplateId") Long processTemplateId,
			@RequestParam(value="name") String name ) {
		if(StringUtils.isBlank("name")){
			return this.getFailedMap("流程名称不能为空");
		}
		if(processTemplateId == null || processTemplateId < 1){
			return this.getFailedMap("流程模板不能为空");
		}
		List<ProcessConfig> proList = processService.findProcessConfigByNameAndTemplateId(name, processTemplateId);
		if(proList != null && !proList.isEmpty()){
			return this.getFailedMap("流程名称已存在");
		}
		ProcessConfig pc = this.processService.addProcessConfig(name, processTemplateId);
		return this.getSuccessMap(pc);
		
	}

	/**
	 * 上下移动流程阶段
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upDown", method=RequestMethod.POST)
	public Map<String, Object> editProcessSequence(
			HttpServletRequest request
			,@RequestParam(value="configId") Long configId
			,@RequestParam(value="configSequence") Long configSequence
			,@RequestParam(value="updown") Long updown
			,@RequestParam(value="configCount") Long configCount
			,@RequestParam(value="processId") Long processId
			) {
		Map<String, Object> map = new HashMap<>();
		
		if(1==configSequence&&0==updown){
			//如果是第一个元素，不允许上移
			map.put("affected", 0);
		}else if(configCount==configSequence&&1==updown){
			//如果是最后一个元素，不允许下移
			map.put("affected", 0);
		}else{
			//进行移动
			int affected = this.processService.upDown(processId,configId,configSequence,updown);
			map.put("affected", affected);
		}
		return this.getSuccessMap(map);
	}

	@ResponseBody
	@RequestMapping(value="/config/{id}", method=RequestMethod.GET)
	public Map<String, Object> getProcessConfig(HttpServletRequest request,@PathVariable(value="id") long id) {
		
		ProcessConfig pc = this.processService.getProcessConfig(id);
		List<Role> roleList = this.roleService.findRoleList();
		
		Map<String, Object> message = new HashMap<>();
		message.put("roleList", roleList);
		message.put("creatorRoleStr", pc.getCreatorRoleStr() == null ? "" : pc.getCreatorRoleStr());
		message.put("managerRoleStr", pc.getManagerRoleStr() == null ? "" : pc.getManagerRoleStr());
		message.put("memberRoleStr",  pc.getMemberRoleStr()  == null ? "" : pc.getMemberRoleStr());

		return this.getSuccessMap(message);
	}
	
	
	@ResponseBody
	@RequestMapping(value="/config/edit", method=RequestMethod.POST)
	public Map<String, Object> editProcessConfig(
			HttpServletRequest request,
			ProcessConfig pc ) {

		int affected = this.processService.updateProcessConfig(pc);
		Map<String, Object> map = new HashMap<>();
		map.put("affected", affected);
		return this.getSuccessMap(map);
		
	}
	
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
	@ResponseBody
	@RequestMapping(value="/changestatus/{processId}",method=RequestMethod.POST)
	public Map<String, Object> changeStaus(HttpServletRequest request,@PathVariable("processId")long processId,
			ProcessTemplateStatus status){
		
		int a = this.processService.updateStatus(processId,status);
		Map<String, Object> map = new HashMap<>();
		map.put("affected", a);
		return this.getSuccessMap(map);
		
	}

	/**
	 * 
	 * @describe 删除流程阶段	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月16日 下午4:45:47	<br>
	 * @param request
	 * @param processId
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/delete/{processId}",method=RequestMethod.POST)
	public Map<String, Object> deleteProcessConfig(HttpServletRequest request,@PathVariable("processId")long processId){
		int a = this.processService.deleteProcessTemplate(processId);
		Map<String, Object> map = new HashMap<>();
		map.put("affected", a);
		return this.getSuccessMap(map);
	}
}
