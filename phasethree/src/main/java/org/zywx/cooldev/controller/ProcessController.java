package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.PROCESS_MEMBER_TYPE;
import org.zywx.cooldev.dao.process.ProcessDao;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.process.ProcessMember;
import org.zywx.cooldev.entity.task.Task;

/**
 * 流程相关处理控制器
 * @author yang.li
 * @date 2015-08-10
 *
 */
@Controller
@RequestMapping(value = "/process")
public class ProcessController extends BaseController {

	@Autowired
	private ProcessDao processDao;
	/**
	 * 获取流程列表
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> getProcessList(
			Process match,
			@RequestHeader(value="loginUserId") long loginUserId) {
			
		try {
			Map<String, Object> map = this.processService.getProcessList(loginUserId, match);
			return this.getSuccessMap(map);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
			
	}
	/**
	 *2016-07-20
	 * 初始化流程的进度,首次上线时候需要.
	 * @return
	 */
	//TODO 企业版上线时候新增了progress字段,需要初始化.
	@ResponseBody
	@RequestMapping(value="/initProgress",method=RequestMethod.GET)
	public Map<String, Object> initProgress() {
			
		try {
			this.processService.updateProcessStatusAndProgress();
			return this.getSuccessMap("");

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
			
	}
	
	
	/**
	 * 根据项目ID,查询该项目下的流程列表(不做权限校验)
	 * search 流程名称
	 * @user jingjian.wu
	 * @date 2016年2月1日 下午5:25:31
	 */
	@ResponseBody
	@RequestMapping(value="/listProcess",method=RequestMethod.GET)
	public Map<String, Object> getProcessList(
			Long projectId,
			@RequestHeader(value="loginUserId") long loginUserId,
			String search) {
			
		try {
			List<Process> listProcess = new ArrayList<Process>();
			if(StringUtils.isNotBlank(search)){
				listProcess= this.processService.getProcessListByProjectIdAndName(projectId, search);
			}else{
				listProcess =this.processService.getProcessListByProjectId(projectId);
			}
			return this.getSuccessMap(listProcess);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
			
	}
	
	
	
	/**
	 * 获取流程详情
	 * @param processId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{processId}", method=RequestMethod.GET)
	public Map<String, Object> getProcess(@PathVariable(value="processId") Long processId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			log.info(" get process: processId:"+processId+",loginUserId:"+loginUserId);
			Process p = this.processService.getProcess(loginUserId, processId);
			if(p != null && p.getDel().equals(DELTYPE.NORMAL)) {
				return this.getSuccessMap(p);
			} else if(p!=null && p.getDel().equals(DELTYPE.DELETED)){
				return this.getFailedMap("此流程已删除,不可查看!");
			}else{
				return this.getFailedMap("not found process with id=" + processId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 创建新流程<br>
	 * 
	 * @param process
	 * @param leader
	 * @param member
	 * @param resource
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> addProcess(
			Process process,
			@RequestParam(value="leaderUserId") long leaderUserId,
			@RequestParam(value="memberUserIdList",required=false) List<Long> memberUserIdList,
			@RequestParam(value="resourceIdList",  required=false) List<Long> resourceIdList,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			if(process.getName()!=null&&process.getName().length()>1000){
				this.getFailedMap("流程名称不能超过1000个字符");
			}
			if(process.getDetail()!=null&&process.getDetail().length()>1000){
				this.getFailedMap("流程描述不能超过1000个字符");
			}
			log.info("add process -->leaderUserId:"+leaderUserId+",memberUserIdList:"+memberUserIdList+",resourceIdList:"+resourceIdList+",loginUserId:"+loginUserId);
			Process savedP = this.processService.createProcess(process, loginUserId, leaderUserId, memberUserIdList, resourceIdList);
			if(null==savedP){
				return this.getFailedMap("所选流程重复,请选择其他流程");
			}
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROCESS_CREATE, savedP.getProjectId(), new Object[]{savedP.getName()});
			User user = this.userService.findUserById(loginUserId);
			
			if(loginUserId!=leaderUserId){
				//添加通知
				this.noticeService.addNotice(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.PROCESS_ADD_LEADER, new Object[]{user,savedP});
			}
			memberUserIdList.remove(loginUserId);
			//添加通知
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROCESS_ADD_MEMBER, new Object[]{user,savedP});
			
			return this.getSuccessMap(savedP);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 修改流程基本信息
	 * @param processId
	 * @param process
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{processId}", method=RequestMethod.PUT)
	public Map<String, Object> editProcess(
			Process p,
			@PathVariable(value="processId") Long processId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			if(p.getName()!=null&&p.getName().length()>1000){
				this.getFailedMap("流程名称不能超过1000个字符");
			}
			if(p.getDetail()!=null&&p.getDetail().length()>1000){
				this.getFailedMap("流程描述不能超过1000个字符");
			}
			log.info(" update process-->processId:"+processId+",loginUserId:"+loginUserId+",process"+p.toStr());
			Process process = this.processService.getProcess(loginUserId, processId);
			p.setId(processId);
			//判断新改的流程名称是否与已经有的重复
			List<Process> listProcess = processDao.findByProjectIdAndNameAndDel(process.getProjectId(),p.getName(), DELTYPE.NORMAL);
			if(null!=listProcess && listProcess.size()>0){
				for(Process p1 :listProcess){
					if(p1.getId().longValue()!=processId.longValue() && p1.getName().equals(p.getName())){
						return this.getFailedMap("流程名称已存在");
					}
				}
			}
			
			
			int affected = this.processService.editProcess(p);
			Process savedP = this.processService.getProcess(loginUserId, processId);
			
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROCESS_UPDATE, savedP.getProjectId(), new Object[]{savedP.getName()});
			
			//添加通知
			if(!process.getName().equals(savedP.getName())){
				User user = this.userService.findUserById(loginUserId);
				List<ProcessMember> processMember= this.processService.getProcessMemberList(processId, null, null);
				Set<Long> ids = new java.util.HashSet<Long>();;
				for(ProcessMember pids : processMember){
					if(loginUserId==pids.getUserId()){
						continue;
					}
					ids.add(pids.getUserId());
				}
				this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROCESS_UPDATE, new Object[]{user,process,savedP});
			}
			
			
			return this.getSuccessMap(savedP);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	/**
	 * 删除流程
	 * @param processId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{processId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeProcess(@PathVariable(value="processId") long processId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			log.info("remove process-->processId:"+processId+",loginUserId:"+loginUserId);
			List<Task> listT =this.taskService.findByProcessId(processId);
			if(null!=listT && listT.size()>0){
				return this.getFailedMap("此流程下还有任务,不可以删除");
			}
			this.processService.removeProcess(processId);
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			//添加动态
			Process savedP = this.processService.getProcess(loginUserId, processId);
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROCESS_DELETE, savedP.getProjectId(), new Object[]{savedP.getName()});
			//添加通知
			Process process = this.processService.getProcess(loginUserId, processId);
			User user = this.userService.findUserById(loginUserId);
			List<ProcessMember> processMember= this.processService.getProcessMemberList(processId, null, null);
			Set<Long> ids = new java.util.HashSet<Long>();;
			for(ProcessMember pids : processMember){
				if(loginUserId==pids.getUserId()){
					continue;
				}
				ids.add(pids.getUserId());
			}
			this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROCESS_DELETE, new Object[]{user,process,savedP});
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("删除流程失败");
		}

	}
	
	/**
	 * 获取流程成员
	 * @param request
	 * @param processId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/member/{processId}", method=RequestMethod.GET)
	public Map<String, Object> getProcessMemberList(
			HttpServletRequest request,
			@PathVariable(value="processId") long processId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			log.info("process get member-->processId:"+processId+",loginUserId:"+loginUserId);
			String queryName = request.getParameter("name");
			String queryAccount = request.getParameter("account");
			return this.getSuccessMap( this.processService.getProcessMemberList(processId, queryName, queryAccount) );

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		
		}

	}
	
	/**
	 * 变更流程负责人
	 * @param processId
	 * @param userId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/member/leader", method=RequestMethod.PUT)
	public Map<String, Object> changeLeader(
			@RequestParam(value="processId") long processId,
			@RequestParam(value="userId") long userId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			ProcessMember leader = new ProcessMember();
			leader.setProcessId(processId);
			leader.setUserId(userId);
			leader.setType(PROCESS_MEMBER_TYPE.PARTICIPATOR);

			int affected = this.processService.updateLeader(leader);
			
			Process pro = this.processService.findOne(processId);
			User user = this.userService.findUserById(loginUserId);
			if(loginUserId!=userId){
				this.noticeService.addNotice(loginUserId, new Long[]{userId}, NOTICE_MODULE_TYPE.PROCESS_ADD_LEADER, new Object[]{user,pro});
			}
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("affected", affected);
			return this.getSuccessMap(map);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}	
	
	@ResponseBody
	@RequestMapping(value="/member/{memberId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeProcessMember(
			@PathVariable(value="memberId") long memberId,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try {
			this.processService.removeProcessMember(memberId);
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/member", method=RequestMethod.POST)
	public Map<String, Object> addProcessMember(
			ProcessMember member,
			@RequestHeader(value="loginUserId") long loginUserId) {

		try { 
			processService.saveProcessMember(member);
			
			User loginUser = this.userService.findUserById(loginUserId);
			Process process = this.processService.findOne(member.getProcessId());
			//添加通知
			this.noticeService.addNotice(loginUserId, new Long[]{member.getUserId()}, NOTICE_MODULE_TYPE.PROCESS_ADD_MEMBER, new Object[]{loginUser,process});
			
			
			return this.getSuccessMap(member);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 提供给一鹏,手机端插件提交任务时候,根据应用appcanId获取应用下的流程
	 * @user jingjian.wu
	 * @date 2015年10月23日 下午3:46:16
	 */
	@ResponseBody
	@RequestMapping(value="/mobileProcess/{appId}", method=RequestMethod.GET)
	public Map<String, Object> getProcessByApp(@PathVariable(value="appId")String appId,
			@RequestHeader(value="loginUserId") long loginUserId){
		try {
			log.info("mobile appId-->"+appId);
			App app = this.appService.findByAppcanAppId(appId);
			if(null==app){
				return this.getFailedMap("appId is not exist");
			}
			List<Process> listProcess = this.processService.getProcessListByProjectId(app.getProjectId());
			return this.getSuccessMap(listProcess);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("获取流程失败");
		}
	}
	
	/**
	 * 查询任务列表的时候,需要获取我能看到的流程的名字
	 * @user jingjian.wu
	 * @date 2016年3月7日 下午5:16:18
	 */
	@ResponseBody
	@RequestMapping(value="/name4tasklist",method=RequestMethod.GET)
	public Map<String, Object> getProcessNames(String name,
			@RequestHeader(value="loginUserId") long loginUserId,Long projectId) {
			
		try {
			if(null==name){
				name="";
			}
			Map<String, Object> map = this.processService.getProcessList(loginUserId, name,projectId);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
			
	}
	/**
	 * 增加拼音字段
	 */
	@ResponseBody//返回的不是html页面，返回数据
	@RequestMapping(value="/addPinyin",method=RequestMethod.GET)//地址映射
	public Map<String,Object> addPinYin(){//访问权限+返回值类型 方法
		try{
			Map<String,Object> map = this.processService.addPinyin();
			return map;
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
