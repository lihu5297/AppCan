package org.zywx.cooldev.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_PRIORITY;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.dao.UserDao;
import org.zywx.cooldev.dao.task.TaskLeafDao;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Tag;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskComment;
import org.zywx.cooldev.entity.task.TaskExcel;
import org.zywx.cooldev.entity.task.TaskGroup;
import org.zywx.cooldev.entity.task.TaskLeaf;
import org.zywx.cooldev.entity.task.TaskMember;
import org.zywx.cooldev.entity.task.TaskTag;
import org.zywx.cooldev.service.EntityService;
import org.zywx.cooldev.util.ExportExcel;
import org.zywx.cooldev.vo.UpdatableTask;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * 任务相关处理控制器
 * @author yang.li
 * @date 2015-08-12
 *
 */
@Controller
@RequestMapping(value = "/task")
public class TaskController extends BaseController {
	@Autowired
	private EntityService entityService;
	
	@Autowired
	private TaskLeafDao taskLeafDao;
	
	@Autowired
	private UserDao userDao;
	
	@Value("${downExcel.path}")
	private String taskExcelfile;
	
	@Value("${emailTaskBaseLink}")
	private String emailTaskBaseLink;
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

	
	/**
	 * 获取任务详情
	 * @param taskId
	 * @param loginUserId
	 * @param 新增一个字段isLeaf 默认值为否,当通知列表中点击子任务的详情时候,需要传isLeaf为true.
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{taskId}", method=RequestMethod.GET)
	public Map<String, Object> getTask(@PathVariable(value="taskId") long taskId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=false,defaultValue="false")boolean isLeaf) {

		try {
			if(!isLeaf){
				log.info("get task Detail-->taskId:"+taskId+",loginUserId:"+loginUserId);
			}else{
				TaskLeaf leaf = taskLeafDao.findOne(taskId);
				taskId = leaf.getTopTaskId();
				log.info("get task Detail-->taskId:"+taskId+",loginUserId:"+loginUserId);
			}
			Map<String, Object> map = taskService.getTask(taskId, loginUserId);
			
			return this.getSuccessMap(map);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 添加任务
	 * @param task
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> addTask(
			Task task,
			String taskLeafJson,
			@RequestParam(value="leaderUserId") long leaderUserId,
			@RequestParam(value="memberUserIdList",required=false) List<Long> memberUserIdList,
			@RequestParam(value="tagNameList", required=false) List<String> tagNameList,
			@RequestParam(value="resourceIdList", required=false) List<Long> resourceIdList,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		try {
			if(task.getDetail()==null || task.getDetail().length()>1000){
				return this.getFailedMap("任务描述不能超过1000个字符");
			}
			if(null!=tagNameList){
				for(String tagNameListSingle:tagNameList){
					if(tagNameListSingle.length()>30){
						return this.getFailedMap("任务标签不能超过30个字符");
					}
				}
			}
			/**
			 * 判断任务分组是否存在
			 */
			TaskGroup taskG = taskService.findGroupById(task.getGroupId());
			if(null==taskG || taskG.getDel().equals(DELTYPE.DELETED)){
				return this.getFailedMap("任务分组不存在");
			}
			log.info("taskLeafJson==>"+taskLeafJson);
			if(StringUtils.isNotBlank(taskLeafJson)){
				JSONUtils.getMorpherRegistry().registerMorpher(
				          new DateMorpher(new String[] { "yyyy-MM-dd" }));
				JSONObject jsonObj = JSONObject.fromObject(taskLeafJson);
				JSONArray jsonArray = jsonObj.getJSONArray("taskLeafList");
				List<TaskLeaf> list = (List<TaskLeaf>)JSONArray.toCollection(jsonArray, TaskLeaf.class);
				task.setTaskLeafList(list);
			}
			log.info("add task-->loginUserId:"+loginUserId+",leaderUserId:"+leaderUserId+",resourceIdList:"+resourceIdList+",memberuserIdList:"+memberUserIdList+",tagNameList:"+tagNameList);
			if(-1==task.getProcessId()){
				return this.getFailedMap("processId: "+task.getProcessId()+" is not available!");
			}
			List<TaskLeaf> leafList = this.taskService.addTask(task, tagNameList, resourceIdList, leaderUserId, memberUserIdList, loginUserId);

			User user = this.userService.findUserById(loginUserId);
			Process p =this.processService.findOne(task.getProcessId());
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_CREATE, p.getProjectId(), new Object[]{task});
			//添加通知
			Long[] recievedIds = new Long[1];
			if(leaderUserId!=loginUserId){
				recievedIds[0]=leaderUserId;
			}
			if(null==memberUserIdList){
				memberUserIdList = new ArrayList<Long>();
			}
			memberUserIdList.remove(loginUserId);
			this.noticeService.addNotice(loginUserId, recievedIds, NOTICE_MODULE_TYPE.TASK_ADD_TO_LEADER, new Object[]{user,task,sdf.format(task.getDeadline())});
			//发送邮件
			this.baseService.sendEmail(loginUserId, recievedIds, NOTICE_MODULE_TYPE.TASK_ADD_TO_LEADER, new Object[]{user,task,sdf.format(task.getDeadline())});
			
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_TO_MEMBER, new Object[]{user,task,sdf.format(task.getDeadline())});
			//发送邮件
			this.baseService.sendEmail(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_TO_MEMBER, new Object[]{user,task,sdf.format(task.getDeadline())});
			
			if(null!=leafList && leafList.size()>0){
				for(TaskLeaf leaf:leafList){
					if(memberUserIdList.contains(leaf.getManagerUserId()) || leaf.getManagerUserId()==leaderUserId || loginUserId==leaf.getManagerUserId()){
						//如果子任务的负责人是主任务的创建者,负责人或者参与人,只发主任务相关的邮件
						continue;
					}
					//子任务发通知和邮件
					recievedIds[0]=leaf.getManagerUserId();
					this.noticeService.addNotice(loginUserId, recievedIds, NOTICE_MODULE_TYPE.TASK_LEAF_ADD_TO_LEADER, new Object[]{user,leaf,sdf.format(leaf.getDeadline())});
					//发送邮件
					this.baseService.sendEmail(loginUserId, recievedIds, NOTICE_MODULE_TYPE.TASK_LEAF_ADD_TO_LEADER, new Object[]{user,leaf,sdf.format(leaf.getDeadline())});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(task);
	}

	/**
	 * 编辑主任务
	 * @param taskId
	 * @param task
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{taskId}", method=RequestMethod.PUT)
	public Map<String, Object> editTask(UpdatableTask task,
			@PathVariable(value="taskId") long taskId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		HashMap<String, Integer> ret =null;
		Task taskOld =null;
		String oldContent ="";
		try {
			if(task.getDetail()!=null&&task.getDetail().length()>1000){
				return this.getFailedMap("任务描述不能超过1000个字符");
			}
			log.info("edit task-->taskId:"+taskId+",task:"+task);
			taskOld = this.taskService.getSingleTask(taskId);
			oldContent =taskOld.getDetail();
			task.setId(taskId);
			int affected = this.taskService.editTask(task,loginUserId);
			ret = new HashMap<>();
			ret.put("affected", affected);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		Task recordTask = this.taskService.getSingleTask(taskId);
		
		
		//添加通知
		User user = this.userService.findUserById(loginUserId);
		List<TaskMember> members= this.taskService.getTaskMemberList(taskId, null, null);
		TaskMember leader= this.taskService.getTaskManager(taskId);
		Set<Long> ids = new java.util.HashSet<Long>();
		Set<Long> leaderIds = new java.util.HashSet<Long>();
		Set<Long> creatorIds = new java.util.HashSet<Long>();
		for(TaskMember pids : members){
			if(pids.getUserId()==loginUserId){
				continue;
			}
			if(null!=leader && leader.getUserId()==loginUserId){
				leader=null;
			}
			if(pids.getType().equals(TASK_MEMBER_TYPE.CREATOR)){
				creatorIds.add(pids.getUserId());
				continue;
			}
			ids.add(pids.getUserId());
		}
		if(leader!=null){
			leaderIds.add(leader.getUserId());
		}
		boolean dynamicFlag = false;//标记是否已经记录了动态(完成,关闭,驳回,搁置和修改其他的记录的动态不一样)
		Process p =this.processService.findOne(recordTask.getProcessId());
		Calendar cal = Calendar.getInstance();
		long dateDiff = (cal.getTimeInMillis()-taskOld.getDeadline().getTime())/(24L * 60 * 60 * 1000);
		if(!taskOld.getDetail().equals(recordTask.getDetail())){
			this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_UPDATE, new Object[]{user,taskOld,recordTask});
		}else if(!taskOld.getStatus().equals(TASK_STATUS.FINISHED) && recordTask.getStatus().equals(TASK_STATUS.FINISHED)){
			//添加动态
			dynamicFlag = true;
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_FINISH, p.getProjectId(), new Object[]{recordTask});
			if(dateDiff>2){
				this.noticeService.addNotice(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_CREATOR, new Object[]{user,taskOld});
				//发送邮件
				this.baseService.sendEmail(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_CREATOR, new Object[]{user,taskOld});
				
				this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_MEMBER, new Object[]{user,taskOld});
				//发送邮件
				this.baseService.sendEmail(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_MEMBER, new Object[]{user,taskOld});
				
			}else if(0<dateDiff && dateDiff<=2){
				this.noticeService.addNotice(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_WARNNING, new Object[]{user,taskOld});
				//发送邮件
				this.baseService.sendEmail(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_WARNNING, new Object[]{user,taskOld});
				
				this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_MEMBER, new Object[]{user,taskOld});
				//发送邮件
				this.baseService.sendEmail(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_MEMBER, new Object[]{user,taskOld});
			}else{

				this.noticeService.addNotice(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_OVERDUE, new Object[]{user,taskOld});
				//发送邮件
				this.baseService.sendEmail(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_OVERDUE, new Object[]{user,taskOld});
				
				this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_MEMBER, new Object[]{user,taskOld});
				//发送邮件
				this.baseService.sendEmail(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_FINISHED_TO_MEMBER, new Object[]{user,taskOld});
			}
		}else if(!taskOld.getStatus().equals(TASK_STATUS.NOFINISHED) && recordTask.getStatus().equals(TASK_STATUS.NOFINISHED)){
			//添加动态
			dynamicFlag = true;
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_REJECT, p.getProjectId(), new Object[]{recordTask});
			if(null!=leader && !ids.isEmpty()){
				ids.remove(leader.getUserId());
			}
			if(leaderIds.size()>0){
				this.noticeService.addNotice(loginUserId, leaderIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_UNFINISHED, new Object[]{user,taskOld});
				//发送邮件
				this.baseService.sendEmail(loginUserId, leaderIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_UNFINISHED, new Object[]{user,taskOld});
			}
			this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_UNFINISHED, new Object[]{user,taskOld});
			//发送邮件
			this.baseService.sendEmail(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_UNFINISHED, new Object[]{user,taskOld});
		}
		if(!dynamicFlag){
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_UPDATE, p.getProjectId(), new Object[]{oldContent,recordTask});
		}
		return this.getSuccessMap(ret);

	}
	
	/**
	 * 单独创建子任务
	 * @param taskId
	 * @param loginUserId
	 * @param taskLeafList
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/taskLeaf", method=RequestMethod.POST)
	public Map<String, Object> addTaskLeaf(long taskId,@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String taskLeafJson) {
		try {
			log.info("taskLeafJson==>"+taskLeafJson);
			if(StringUtils.isNotBlank(taskLeafJson)){
				JSONUtils.getMorpherRegistry().registerMorpher(
				          new DateMorpher(new String[] { "yyyy-MM-dd" }));
				JSONObject jsonObj = JSONObject.fromObject(taskLeafJson);
				JSONArray jsonArray = jsonObj.getJSONArray("taskLeafList");
				List<TaskLeaf> list = (List<TaskLeaf>)JSONArray.toCollection(jsonArray, TaskLeaf.class);
				taskService.addTaskLeaf(loginUserId,taskId, list);
			}else{
				return this.getFailedMap("参数不合法");
			}
			
			return this.getSuccessMap("添加子任务成功");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		
	}
	/**
	 * 编辑子任务(必须将子任务的所有属性全部传过来,否则参数为空的话,也将子任务对应的字段改为空值)
	 * @param taskLeafId
	 * @param loginUserId
	 * @param detail
	 * @param deadLine
	 * @param managerUserId
	 * @param status
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/taskLeaf/{taskLeafId}", method=RequestMethod.PUT)
	public Map<String, Object> editTaskLeaf(@PathVariable("taskLeafId") Long taskLeafId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String detail,String deadLine,Long managerUserId,@RequestParam(required=false)TASK_STATUS status) {
		try {
			if(detail!=null&&detail.length()>1000){
				return this.getFailedMap("子任务描述不能超过1000个字符");
			}
			TaskLeaf oldLeaf = taskLeafDao.findOne(taskLeafId);
			TaskLeaf newTaskLeaf = taskService.editTaskLeaf(taskLeafId, detail, deadLine, managerUserId, status,loginUserId);
			User user = userService.findUserById(loginUserId);
			
			Task mainTask = taskService.getSingleTask(oldLeaf.getTopTaskId());
			Process p = processService.findOne(mainTask.getProcessId());
			boolean dynamicFlag = false;
			if(oldLeaf.getManagerUserId()!=managerUserId){
				//添加动态
				User manager = userService.findUserById(managerUserId);
				dynamicFlag = true;
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_LEAF_CHANGE_MANAGER, p.getProjectId(), new Object[]{oldLeaf,manager});
				Long[] receivedIds = new Long[1];
				receivedIds[0]=managerUserId;
				//给新添加的人.
				this.noticeService.addNotice(loginUserId, receivedIds, NOTICE_MODULE_TYPE.TASK_LEAF_ADD_TO_LEADER, new Object[]{user,newTaskLeaf,sdf.format(newTaskLeaf.getDeadline())});
				this.baseService.sendEmail(loginUserId, receivedIds, NOTICE_MODULE_TYPE.TASK_LEAF_ADD_TO_LEADER, new Object[]{user,newTaskLeaf,sdf.format(newTaskLeaf.getDeadline())});
				receivedIds[0]=oldLeaf.getManagerUserId();//原先人
				this.noticeService.addNotice(loginUserId, receivedIds, NOTICE_MODULE_TYPE.TASK_REMOVE_TO_MEMBER, new Object[]{user,newTaskLeaf,sdf.format(newTaskLeaf.getDeadline())});
				this.baseService.sendEmail(loginUserId, receivedIds, NOTICE_MODULE_TYPE.TASK_REMOVE_TO_MEMBER, new Object[]{user,newTaskLeaf,sdf.format(newTaskLeaf.getDeadline())});
			}
			if(!oldLeaf.getStatus().equals(newTaskLeaf.getStatus())){
				dynamicFlag = true;
				DYNAMIC_MODULE_TYPE moduleType;
				Calendar cal = Calendar.getInstance();
				long dateDiff = (cal.getTimeInMillis()-newTaskLeaf.getDeadline().getTime())/(24L * 60 * 60 * 1000);
				TaskMember tmCreator = taskService.getTaskCreator(oldLeaf.getTopTaskId());
				if(newTaskLeaf.getStatus().equals(TASK_STATUS.FINISHED)){
					moduleType=DYNAMIC_MODULE_TYPE.TASK_LEAF_FINISHED;
					if(tmCreator.getUserId()!=loginUserId){//主任务创建者,不是当前操作人
						//完成了任务
						if(dateDiff>2){
							this.noticeService.addNotice(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_LEAF_FINISHED_TO_CREATOR, new Object[]{user,newTaskLeaf});
							this.baseService.sendEmail(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_LEAF_FINISHED_TO_CREATOR, new Object[]{user,newTaskLeaf});
						}else if(dateDiff>0){
							this.noticeService.addNotice(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_LEAF_FINISHED_TO_CREATOR_WARNING, new Object[]{user,newTaskLeaf});
							this.baseService.sendEmail(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_LEAF_FINISHED_TO_CREATOR_WARNING, new Object[]{user,newTaskLeaf});
						}else{
							this.noticeService.addNotice(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_LEAF_FINISHED_TO_CREATOR_OVERDUE, new Object[]{user,newTaskLeaf});
							this.baseService.sendEmail(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_LEAF_FINISHED_TO_CREATOR_OVERDUE, new Object[]{user,newTaskLeaf});
						}
						
					}
				}else{
					moduleType=DYNAMIC_MODULE_TYPE.TASK_LEAF_UNFINISHED;
					//修改为未完成
					if(tmCreator.getUserId()!=loginUserId){//主任务创建者,不是当前操作人
						this.noticeService.addNotice(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_UNFINISHED, new Object[]{user,newTaskLeaf});
						this.baseService.sendEmail(loginUserId, new Long[]{tmCreator.getUserId()}, NOTICE_MODULE_TYPE.TASK_UNFINISHED, new Object[]{user,newTaskLeaf});
					}
				}
				this.dynamicService.addPrjDynamic(loginUserId, moduleType, p.getProjectId(), new Object[]{newTaskLeaf});
			}
			if(!dynamicFlag){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_LEAF_UPDATE, p.getProjectId(), new Object[]{newTaskLeaf});
			}
			return this.getSuccessMap("编辑子任务成功");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		
	}
	
	/**
	 * 移除任务
	 * @param taskId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/{taskId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeTask(
			@PathVariable(value="taskId") long taskId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {

		try {
			this.taskService.removeTask(taskId);
			this.taskService.updateProgress(taskId);
			
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			//添加动态
			Task task = this.taskService.getSingleTask( taskId);
			Process p =this.processService.findOne(task.getProcessId());
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_DELETE, p.getProjectId(), new Object[]{task});
			//添加通知
			User user = this.userService.findUserById(loginUserId);
			List<TaskMember> members= this.taskService.getTaskMemberList(taskId, null, null);
			Set<Long> ids = new java.util.HashSet<Long>();;
			Set<Long> creatorIds = new java.util.HashSet<Long>();
			for(TaskMember pids : members){
				if(pids.getUserId()==loginUserId){
					continue;
				}
				if(pids.getType().equals(TASK_MEMBER_TYPE.CREATOR)){
					creatorIds.add(pids.getUserId());
					continue;
				}
				ids.add(pids.getUserId());
			}
			this.noticeService.addNotice(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_DELETE_TO_CREATOR, new Object[]{user,task});
			//发送邮件
			this.baseService.sendEmail(loginUserId, creatorIds.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_DELETE_TO_CREATOR, new Object[]{user,task});
			
			this.noticeService.addNotice(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_DELETE, new Object[]{user,task});
			//发送邮件
			this.baseService.sendEmail(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_DELETE, new Object[]{user,task});
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	
	/**
	 * 根据子任务ID删除子任务
	 * @param taskLeafId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/taskLeaf/{taskLeafId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeTaskLeaf(
			@PathVariable(value="taskLeafId") long taskLeafId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		try {
			TaskLeaf oldLeaf = taskLeafDao.findOne(taskLeafId);
			taskService.removeTaskLeaf(taskLeafId);
			Task mainTask = taskService.getSingleTask(oldLeaf.getTopTaskId());
			Process p = processService.findOne(mainTask.getProcessId());
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_LEAF_REMOVE, p.getProjectId(), new Object[]{oldLeaf});
			
			TaskMember creator = taskService.getTaskCreator(oldLeaf.getTopTaskId());
			User user = userService.findUserById(loginUserId);
			if(creator.getUserId()!=loginUserId){
				this.noticeService.addNotice(loginUserId, new Long[]{creator.getUserId()}, NOTICE_MODULE_TYPE.TASK_DELETE_TO_CREATOR, new Object[]{user,oldLeaf});
				//发送邮件
				this.baseService.sendEmail(loginUserId, new Long[]{creator.getUserId()}, NOTICE_MODULE_TYPE.TASK_DELETE_TO_CREATOR, new Object[]{user,oldLeaf});
			}
			if(oldLeaf.getManagerUserId()!=loginUserId){
				this.noticeService.addNotice(loginUserId, new Long[]{oldLeaf.getManagerUserId()}, NOTICE_MODULE_TYPE.TASK_DELETE, new Object[]{user,oldLeaf});
				//发送邮件
				this.baseService.sendEmail(loginUserId, new Long[]{oldLeaf.getManagerUserId()}, NOTICE_MODULE_TYPE.TASK_DELETE, new Object[]{user,oldLeaf});
				
			}
			return this.getSuccessMap("删除子任务成功");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("删除子任务失败");
		}
	}

	/**
	 * 子任务转换为主任务
	 * @param loginUserId
	 * @param taskLeafId
	 * @return
	 */
	@RequestMapping(value="/taskLeaf/{taskLeafId}/upgrade",method=RequestMethod.POST)
	public Map<String,Object> upgradeTaskLeaf(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@PathVariable(value="taskLeafId") long taskLeafId){
		try{
			Task newTask =this.taskService.updateToMainTask(taskLeafId, loginUserId);
			TaskLeaf oldLeaf = taskLeafDao.findOne(taskLeafId);
			Task mainTask = taskService.getSingleTask(oldLeaf.getTopTaskId());
			Process p = processService.findOne(mainTask.getProcessId());
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_LEAF_UPGRADE, p.getProjectId(), new Object[]{oldLeaf});
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_CREATE, p.getProjectId(), new Object[]{newTask});
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("转换失败！");
		}
		return this.getSuccessMap("转换成功");
	}
	
	
	/**
	 * 添加参与成员
	 * @param member
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/member", method=RequestMethod.POST)
	public Map<String, Object> addMember(
			TaskMember member,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		
		try {
			log.info("task add member-->loginUserId:"+loginUserId+",member:"+member.toStr());
			this.taskService.addTaskMember(member,loginUserId);
			
			Task task = this.taskService.getSingleTask(member.getTaskId());
			User user = this.userService.findUserById(loginUserId);
			User targetUser = this.userService.findUserById(member.getUserId());
			
			Process p =this.processService.findOne(task.getProcessId());
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_ADD_MEMBER, p.getProjectId(), new Object[]{task,targetUser.getUserName()});
			//添加通知
			this.noticeService.addNotice(loginUserId, new Long[]{member.getUserId()}, NOTICE_MODULE_TYPE.TASK_ADD_TO_MEMBER, new Object[]{user,task,sdf.format(task.getDeadline())});
			//发送邮件
			this.baseService.sendEmail(loginUserId, new Long[]{member.getUserId()}, NOTICE_MODULE_TYPE.TASK_ADD_TO_MEMBER, new Object[]{user,task,sdf.format(task.getDeadline())});
			return this.getSuccessMap(member);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	@ResponseBody
	@RequestMapping(value="/member/{memberId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeMember(
			@PathVariable(value="memberId") long memberId,
			@RequestHeader(value="loginUserId") long loginUserId) {
		TaskMember tm = this.taskService.getTaskMember(memberId);
		Task task = this.taskService.getSingleTask(tm.getTaskId());
		Process process = this.processService.findOne(task.getProcessId());
		User user = this.userService.findUserById(tm.getUserId());
		User loginUser = this.userService.findUserById(loginUserId);
		
		this.taskService.removeTaskMember(memberId,loginUserId);
		
		Map<String, Integer> affected = new HashMap<>();
		affected.put("affected", 1);
		
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_REMOVE_MEMBER, process.getProjectId(), new Object[]{task,user});
		this.noticeService.addNotice(loginUserId, new Long[]{user.getId()}, NOTICE_MODULE_TYPE.TASK_REMOVE_TO_MEMBER, new Object[]{loginUser,task});
		//发送邮件
		this.baseService.sendEmail(loginUserId, new Long[]{user.getId()}, NOTICE_MODULE_TYPE.TASK_REMOVE_TO_MEMBER, new Object[]{loginUser,task});
		return this.getSuccessMap(affected);
	}
	
	@ResponseBody
	@RequestMapping(value="/changeManager/{taskId}", method=RequestMethod.PUT)
	public Map<String, Object> changeTaskManager(
			@PathVariable(value="taskId") long taskId,
			@RequestParam(value="manager") long manager,
			@RequestHeader(value="loginUserId") long loginUserId) {

		Map<String, Object> ret;
		try {
			log.info("update task member-->loginUserId:"+loginUserId+",taskId:"+taskId+",mamager:"+manager);
			TaskMember oldManager = this.taskService.getTaskManager(taskId);
			int affected = taskService.editTaskManager(taskId, manager,loginUserId);
			Task task = this.taskService.getSingleTask(taskId);
			Process p =this.processService.findOne(task.getProcessId());
			User user = this.userService.findUserById(manager);
			String userName = user.getUserName()==null?user.getAccount():user.getUserName();
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_CHANGE_MANAGER, p.getProjectId(), new Object[]{task,userName});
			user = this.userService.findUserById(loginUserId);
			if(oldManager.getUserId()!=loginUserId){
				this.noticeService.addNotice(loginUserId, new Long[]{oldManager.getUserId()}, NOTICE_MODULE_TYPE.TASK_REMOVE_TO_MEMBER, new Object[]{user,task});
				//发送邮件
				this.baseService.sendEmail(loginUserId, new Long[]{oldManager.getUserId()}, NOTICE_MODULE_TYPE.TASK_REMOVE_TO_MEMBER, new Object[]{user,task});
			}
			if(manager!=loginUserId){
				this.noticeService.addNotice(loginUserId, new Long[]{manager}, NOTICE_MODULE_TYPE.TASK_ADD_TO_LEADER, new Object[]{user,task,sdf.format(task.getDeadline())});
				//发送邮件
				this.baseService.sendEmail(loginUserId, new Long[]{manager}, NOTICE_MODULE_TYPE.TASK_ADD_TO_LEADER, new Object[]{user,task,sdf.format(task.getDeadline())});
			}
			ret = new HashMap<>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.getFailedMap("");

	}	
	
	/**
	 * 添加标签
	 * @param taskId
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/tag/{taskId}", method=RequestMethod.POST)
	public Map<String, Object> addTagToTask(Tag tag,
			@PathVariable(value="taskId") long taskId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		
		try {
			if(tag.getName()!=null&&tag.getName().length()>30){
				return this.getFailedMap("任务标签不能超过30个字符");
			}
			log.info("add task tag-->loginUserId:"+loginUserId+",taskId:"+taskId+",tag:"+tag.toStr());
			Tag savedTag = tagService.addTag(tag);
	
			TaskTag returnFlag = this.taskService.addTaskTag(taskId, savedTag.getId(),loginUserId);
			if(returnFlag == null){
				return this.getFailedMap("标签已存在");
			}
			
			Task task = this.taskService.getSingleTask(taskId);
			Process p =this.processService.findOne(task.getProcessId());
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_ADD_TAG, p.getProjectId(), new Object[]{task,tag.getName()});
			
			
		
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 移除标签
	 * @param taskId
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/tag/{taskId}", method=RequestMethod.DELETE)
	public Map<String, Object> removeTagFromTask(
			@PathVariable(value="taskId") long taskId,
			@RequestParam(value="name", required=true) String name,
			@RequestHeader(value="loginUserId", required=true) long loginUserId) {
		
		try {
			log.info("delete task tag--->loginUserId:"+loginUserId+",taskId:"+taskId+",name:"+name);
			Tag tag = tagService.getTag(name);
			if(tag == null) {
				return this.getFailedMap("tag not exist");
			}
			this.taskService.removeTaskTag(taskId, tag.getId(),loginUserId);
			
			Task task = this.taskService.getSingleTask(taskId);
			Process p =this.processService.findOne(task.getProcessId());
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_REMOVE_TAG, p.getProjectId(), new Object[]{task,tag.getName()});
			
		
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);
			
			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	/**
	 * 为任务添加评论
	 * @param taskCommnet
	 * @param taskId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/comment", method=RequestMethod.POST)
	public Map<String, Object> addTaskComment(
			TaskComment taskComment,
			@RequestParam(value="resourceIdList", required=false) List<Long> resourceIdList,
			@RequestHeader(value="loginUserId") long loginUserId) {
		
		try {
			if(taskComment.getContent()!=null&&taskComment.getContent().length()>1000){
				return this.getFailedMap("任务评论内容不能超过1000个字符");
			}
			log.info("add task comment-->loginUserId:"+loginUserId+",resourceIdList:"+resourceIdList+",taskComment:"+taskComment.toStr());
			taskComment.setUserId(loginUserId);
			this.taskService.addTaskComment(taskComment,loginUserId);
			
			if(resourceIdList != null && resourceIdList.size() > 0) {
				for(long resourceId : resourceIdList) {
					EntityResourceRel rel = new EntityResourceRel();
					rel.setEntityId(taskComment.getId());
					rel.setEntityType(ENTITY_TYPE.TASK_COMMENT);
					rel.setResourceId(resourceId);
					entityService.addEntityResourceRel(rel, loginUserId);
				}
			}
			
			Task task = this.taskService.getSingleTask(taskComment.getTaskId());
			Process p =this.processService.findOne(task.getProcessId());
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_ADD_COMMENT, p.getProjectId(), new Object[]{task});
			//发送邮件
			List<TaskMember> members= this.taskService.getTaskMemberList(taskComment.getTaskId(), null, null);
			Set<Long> ids = new java.util.HashSet<Long>();
			for(TaskMember pids : members){
				if(pids.getUserId()==loginUserId){
					continue;
				}
				ids.add(pids.getUserId());
			}
			User user = this.userService.findUserById(loginUserId);
//			TaskMember createmember=this.taskService.getTaskCreator(taskComment.getTaskId());
//			User createruser = this.userService.findUserById(createmember.getUserId());
//			String con = "<a href='"+emailTaskBaseLink+"myTask?taskId="+taskComment.getTaskId()+"'>"+taskComment.getContent()+"</a>";
			this.baseService.sendEmail(loginUserId, ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_COMMENT, new Object[]{user,task});

			return this.getSuccessMap(taskComment);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	
	/**
	 * 添加任务(手机插件)
	 * 和上面的创建任务除了appid传的值不一样之外,其他的都一样
	 * @user jingjian.wu
	 * @date 2015年11月3日 下午3:09:34
	 */
	@ResponseBody
	@RequestMapping(value="mobileTask",method=RequestMethod.POST)
	public Map<String, Object> addMobileTask(
			Task task,
			@RequestParam(value="leaderUserId") long leaderUserId,
			@RequestParam(value="memberUserIdList",required=false) List<Long> memberUserIdList,
			@RequestParam(value="tagNameList", required=false) List<String> tagNameList,
			@RequestParam(value="resourceIdList", required=false) List<Long> resourceIdList,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		try {
			log.info("add mobile task-->loginUserId:"+loginUserId+",leaderUserId:"+leaderUserId+",resourceIdList:"+resourceIdList+",memberUserIdList:"+memberUserIdList+",tagNameList:"+tagNameList);
			if(-1==task.getProcessId()){
				return this.getFailedMap("processId: "+task.getProcessId()+" is not available!");
			}
			if(null==task || 0==task.getAppId()){
				return this.getFailedMap("应用ID不合法");
			}
			App app = this.appService.findByAppcanAppId(task.getAppId()+"");
			task.setAppId(app.getId());
			task.setProjectId(app.getProjectId());
			this.taskService.addTask(task, tagNameList, resourceIdList, leaderUserId, memberUserIdList, loginUserId);

			User user = this.userService.findUserById(loginUserId);
			Process p =this.processService.findOne(task.getProcessId());
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_CREATE, p.getProjectId(), new Object[]{task});
			//添加通知
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, 2);
			if((cal.getTimeInMillis()-task.getDeadline().getTime())/(24L * 60 * 60 * 1000)<=2){
				this.noticeService.addNotice(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.TASK_ADD_LEADER_WARNNING, new Object[]{user,task});
				this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_MEMBER_WARNNING, new Object[]{user,task});
			}else{
				this.noticeService.addNotice(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.TASK_ADD_LEADER, new Object[]{user,task});
				this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_MEMBER, new Object[]{user,task});
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(task);
	}

	
	
	/**
	 * 查询任务列表时候筛选条件(标签)
	 * @user jingjian.wu
	 * @date 2016年3月14日 上午11:26:15
	 */
	@RequestMapping(value="/tagNames",method=RequestMethod.GET)
	public Map<String,Object> tagNameForTaskList(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			 String keyWords,Long projectId){
		try{
			List<String> result = taskService.getTagNameForTasklist(loginUserId, keyWords,projectId);
			return this.getSuccessMap(result);
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("获取任务标签失败");
		}
	}
	
	/**
	 * 查询任务列表
	 * @param detail 任务描述
	 * @param timeStatus 全部   正常(normal)  即将延迟(jjyq)  已延迟(yyq)     
	 * @param taskStatus  未完成 (NOFINISHED),已完成(FINISHED)
	 * @param creatorId 创建者ID
	 * @param creator 创建者名称
	 * @param leaderId 负责人ID
	 * @param leader 负责人
	 * @param partnerId 参与人ID
	 * @param partner 参与人
	 * @param priority  正常(NORMAL)   紧急 (URGENT) 非常紧急(VERY_URGENT)
	 * @param teamId 团队ID
	 * @param teamName 团队名称
	 * @param projectId 项目ID
	 * @param projName 项目名称
	 * @param processId 流程ID
	 * @param processName 流程名称
	 * @param appId 应用ID
	 * @param appName  应用名称
	 * @param tagName 标签名称
	 * @param createBegin  createEnd  创建时间
	 * @param endTimeBegin,endTimeEnd 截止时间
	 * @param completeTimeBegin,completeTimeEnd 完成时间
	 * @param completeUserId 任务完成人ID
	 * @param completeUser 任务完成人
	 * @param groupId 分组ID
	 * @param orderBy 排序字段
	 * @param sortBy 排序方式  DESC ASC
	 * @user jingjian.wu
	 * @date 2016年3月14日 上午11:49:17
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> taskListPhaseTwo(HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String detail,
			@RequestParam(required=false)List<String> timeStatus,//延迟状态
			@RequestParam(required=false)List<TASK_STATUS> taskStatus, //任务完成状态
			Long creatorId,String creator,Long leaderId,String leader,Long partnerId,String partner,//创建人 负责人 参与人
			@RequestParam(required=false)List<TASK_PRIORITY> priority,//优先级 正常,紧急,非常紧急
			Long teamId,String teamName,Long projectId,String projName,Long processId,String processName,Long appId,String appName,//团队,项目,流程,应用
			String tagName,//标签
			String createBegin,String createEnd,//创建时间
			String endTimeBegin,String endTimeEnd,//截止时间
			String completeTimeBegin,String completeTimeEnd,//完成时间
			Long completeUserId,String completeUser,//完成人
			Long  groupId,String groupName,//任务分组ID
			@RequestParam(required= false,defaultValue="createdAt")String orderBy,//排序字段
			@RequestParam(required=false,defaultValue="DESC")String sortBy//升序,降序
			) {
		
			try {
				String sPageNo      = request.getParameter("pageNo");
				String sPageSize    = request.getParameter("pageSize");

				int pageNo       = 0;
				int pageSize     = 15;
				
				try {
					if(sPageNo != null) {
						if(Integer.parseInt(sPageNo)>0){
							pageNo		= Integer.parseInt(sPageNo)-1;
						}
					}
					if(sPageSize != null) {
						pageSize	= Integer.parseInt(sPageSize);
					}
					
				} catch (NumberFormatException nfe) {	
					nfe.printStackTrace();
					return this.getFailedMap( nfe.getMessage() );
				}

				Map<String,Object> arr = this.taskService.getTaskLists(pageNo,pageSize,
						loginUserId,
						 detail,
						timeStatus,//延迟状态
						 taskStatus, //任务完成状态
						creatorId, creator, leaderId, leader, partnerId, partner,//创建人 负责人 参与人
						 priority,//优先级 正常,紧急,非常紧急
						 teamId, teamName, projectId, projName, processId, processName, appId, appName,//团队,项目,流程,应用
						 tagName,//标签
						 createBegin, createEnd,//创建时间
						 endTimeBegin, endTimeEnd,//截止时间
						 completeTimeBegin, completeTimeEnd,//完成时间
						 completeUserId, completeUser,//完成人
						  groupId,groupName,//任务分组ID
						   orderBy,
						   sortBy
						);
				return this.getSuccessMap(arr);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
			
	}
	
	/**
	 * 导出任务列表
	 * @param request
	 * @param loginUserId
	 * @param detail
	 * @param timeStatus
	 * @param taskStatus
	 * @param creatorId
	 * @param creator
	 * @param leaderId
	 * @param leader
	 * @param partnerId
	 * @param partner
	 * @param priority
	 * @param teamId
	 * @param teamName
	 * @param projectId
	 * @param projName
	 * @param processId
	 * @param processName
	 * @param appId
	 * @param appName
	 * @param tagName
	 * @param createBegin
	 * @param createEnd
	 * @param endTimeBegin
	 * @param endTimeEnd
	 * @param completeTimeBegin
	 * @param completeTimeEnd
	 * @param completeUserId
	 * @param completeUser
	 * @param groupId
	 * @param orderBy
	 * @param sortBy
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/downExcel",method=RequestMethod.GET)
	public Map<String,Object>  downloadTask(HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true)long loginUserId,
			String detail,
			@RequestParam(required=false)List<String> timeStatus,//延迟状态
			@RequestParam(required=false)List<TASK_STATUS> taskStatus, //任务完成状态
			Long creatorId,String creator,Long leaderId,String leader,Long partnerId,String partner,//创建人 负责人 参与人
			@RequestParam(required=false)List<TASK_PRIORITY> priority,//优先级 正常,紧急,非常紧急
			Long teamId,String teamName,Long projectId,String projName,Long processId,String processName,Long appId,String appName,//团队,项目,流程,应用
			String tagName,//标签
			String createBegin,String createEnd,//创建时间
			String endTimeBegin,String endTimeEnd,//截止时间
			String completeTimeBegin,String completeTimeEnd,//完成时间
			Long completeUserId,String completeUser,//完成人
			Long  groupId,String groupName,//任务分组ID
			@RequestParam(required= false,defaultValue="createdAt")String orderBy,//排序字段
			@RequestParam(required=false,defaultValue="DESC")String sortBy//升序,降序
			,HttpServletResponse response
			) {
		
			try {

				List<TaskExcel> listTask = this.taskService.downloadTask(loginUserId, detail, timeStatus, taskStatus, creatorId, creator, leaderId, leader, partnerId, partner, priority, teamId, teamName, projectId, projName, processId, processName, appId, appName, tagName, createBegin, createEnd, endTimeBegin, endTimeEnd, completeTimeBegin, completeTimeEnd, completeUserId, completeUser, groupId,groupName, orderBy, sortBy);
				String[] title = new String[] { "状态|status","描述|detail","优先级|priority",
						"所属分组|groupName","所属应用|appName","负责人|leader","创建者|creator",
						"创建时间|createdAt","计划截止时间|deadline","实际完成时间|finishDate","工时|workHour"};
				String fileName="任务列表";
				HSSFWorkbook wb = ExportExcel.exportExcel(title,fileName,listTask);
//				response.setContentType("application/x-msdownload;charset=utf-8");
//				response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(),"ISO-8859-1") + ".xls");
//				ServletOutputStream out = response.getOutputStream();
//				wb.write(out);
//				out.flush();
//				out.close();
				String fileNameF = taskExcelfile+"/"+System.currentTimeMillis()+".xls";
				File f = new File(taskExcelfile);
				if(!f.exists()){
					f.createNewFile();
				}
				FileOutputStream fo = new FileOutputStream(new File(fileNameF));
				wb.write(fo);
				fo.flush();
				fo.close();
				return this.getSuccessMap(fileNameF);
			} catch (Exception e) {
				e.printStackTrace();
				return this.getFailedMap(e.getMessage());
			}
			
	}
	
	/**
	 * 为已有项目初始化任务分组
	 * @return
	 */
	@RequestMapping(value="/taskGroup/init",method=RequestMethod.GET)
	public Map<String,Object> taskGroupInit(@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try{
			taskService.saveInitTaskGroupForProject();
			return this.getSuccessMap("");
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 将以前任务的状态 待进行 等等6个状态 兼容现在的任务分组
	 * @param loginUserId
	 * @return
	 */
	@RequestMapping(value="/taskGroup/oldDate",method=RequestMethod.GET)
	public Map<String,Object> taskGroupBefore(@RequestHeader(value="loginUserId",required=true) long loginUserId){
		try{
			taskService.updateTaskGroupInit();
			return this.getSuccessMap("");
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 获取任务分组列表,包括任务分组查询
	 * @param loginUserId
	 * @param projectId
	 * @param name
	 * @return
	 */
	@RequestMapping(value="/taskGroup",method=RequestMethod.GET)
	public Map<String,Object> TaskGroupList(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			Long projectId,String name){
		try{
			if(null==projectId){
				return this.getFailedMap("请选择项目");
			}
			return this.getSuccessMap(this.taskService.getTaskGroupList(projectId,name,loginUserId));
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("获取任务分组失败！");
		}
	}
	
	
	/**
	 * 在项目中查看任务列表之前,需要获取某人在此项目下的任务分组排序顺序,然后前台根据分组的顺序,进行调用任务列表接口
	 * @param loginUserId
	 * @param projectId
	 * @return
	 */
	@RequestMapping(value="/taskGroup/sort",method=RequestMethod.GET)
	public Map<String,Object> TaskGroupList(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			Long projectId){
		try{
			if(null==projectId){
				return this.getFailedMap("请选择项目");
			}
			return this.getSuccessMap(this.taskService.getOrderedTaskGroupByProjectIdAndUserId(projectId,loginUserId));
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("获取任务分组失败！");
		}
	}
	
	
	/**
	 * 创建任务分组
	 * @param taskGroup
	 * @return
	 */
	@RequestMapping(value="/taskGroup",method=RequestMethod.POST)
	public Map<String,Object> taskGroupAdd(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			TaskGroup taskGroup){
		try{
			if(taskGroup.getName()!=null&&taskGroup.getName().length()>10){
				return this.getFailedMap("任务分组名称不能超过10个字符");
			}
			taskService.addTaskGroup(taskGroup);
			//添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_GROUP_ADD, taskGroup.getProjectId(), new Object[]{taskGroup});
			return this.getAffectMap();
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 修改任务分组
	 * @param taskGroup
	 * @return
	 */
	@RequestMapping(value="/taskGroup",method=RequestMethod.PUT)
	public Map<String,Object> taskGroupEdit(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			TaskGroup taskGroup){
		try{
			if(taskGroup.getName()!=null&&taskGroup.getName().length()>10){
				return this.getFailedMap("任务分组名称不能超过10个字符");
			}
			taskService.editTaskGroup(taskGroup);
			return this.getAffectMap();
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 删除任务分组
	 * @param taskGroupId
	 * @param transferToGroupId 将原有任务分组下的任务,转移到此参数指定分组下
	 * @return
	 */
	@RequestMapping(value="/taskGroup/{taskGroupId}",method=RequestMethod.DELETE)
	public Map<String,Object> taskGroupDel(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@PathVariable("taskGroupId") long taskGroupId,Long transferToGroupId){
		try{
			if(null !=transferToGroupId && taskGroupId==transferToGroupId){
				return this.getFailedMap("删除的分组不能和转移的分组相同");
			}
			boolean haveTaskInGroup = taskService.delTaskGroup(taskGroupId,transferToGroupId);
			//添加动态
			TaskGroup originGroup = taskService.findGroupById(taskGroupId);
			if(haveTaskInGroup){
				TaskGroup newGroup = taskService.findGroupById(transferToGroupId);
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_GROUP_REMOVE_AND_TRANSFER, originGroup.getProjectId(), new Object[]{originGroup,newGroup});
			}else{
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_GROUP_REMOVE, originGroup.getProjectId(), new Object[]{originGroup});
			}
			return this.getAffectMap();
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 任务分组排序
	 * @param loginUserId
	 * @param projectId
	 * @param groupList
	 * @return
	 */
	@RequestMapping(value="/group/sort",method=RequestMethod.POST)
	public Map<String,Object> updateTaskGroupSort(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=true)Long projectId,@RequestParam(value="groupList",required=true)List<Long> groupList){
		if(groupList.isEmpty()){
			return this.getFailedMap("参数groupList不能为空");
		}
		try{
			this.taskService.updateTaskGroupSort(projectId,loginUserId,groupList);
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("更新失败！");
		}
		return this.getSuccessMap("更新成功");
	}
	
	/**
	 * 根据任务角色,和任务状态,以及分组查询任务 
	 * 获取我创建/负责/参与的;完成/未完成;分组下的任务
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@RequestMapping(value="/joinedTask")
	public Map<String, Object> getTaskListByMemberTypeAndStatusAndGroup(HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) long loginUserId,
			@RequestParam(required=false)List<ROLE_TYPE> roleType,@RequestParam(required=false)List<TASK_STATUS> status,Long groupId,
			String orderBy,String sortBy){
		try {
			String sPageNo      = request.getParameter("pageNo");
			String sPageSize    = request.getParameter("pageSize");

			int pageNo       = 0;
			int pageSize     = 20;
			
			try {
				if(sPageNo != null) {
					int paramsPageNo =Integer.parseInt(sPageNo); 
					if(paramsPageNo>0){
						pageNo		= paramsPageNo-1;
					}
				}
				if(sPageSize != null) {
					pageSize	= Integer.parseInt(sPageSize);
				}
				
			} catch (NumberFormatException nfe) {				
				return this.getFailedMap( nfe.getMessage() );
			}
			if(StringUtils.isBlank(orderBy)){
				orderBy = "id";
			}else if(!orderBy.equals("id") && !orderBy.equals("createdAt") && !orderBy.equals("priority")
					&& !orderBy.equals("deadline")
					&& !orderBy.equals("finishDate")){
				return this.getFailedMap("查询错误");
			}
			
			Direction direction = Direction.DESC;
			if(StringUtils.isNotBlank(sortBy)){
				if("asc".equalsIgnoreCase(sortBy)){
					direction = Direction.ASC;
				}
			}
			Pageable pageable = new PageRequest(pageNo, pageSize,direction , orderBy);
			
			Page<Task> pageTask = taskService.findByRoleTypeAndStatusAndGroup(pageable, loginUserId, roleType, status, groupId);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("total", pageTask.getTotalElements());
			map.put("list", pageTask.getContent());
			
			if(null==status||status.size()==0){
				List<TASK_STATUS> taskStatus = new ArrayList<TASK_STATUS>();
				taskStatus.add(TASK_STATUS.FINISHED);
				Page<Task> pageTaskFinished = taskService.findByRoleTypeAndStatusAndGroup(pageable, loginUserId, roleType, taskStatus, groupId);
				map.put("totalFinished", pageTaskFinished.getTotalElements());
			}
			return this.getSuccessMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		JSONUtils.getMorpherRegistry().registerMorpher(
		          new DateMorpher(new String[] { "yyyy-MM-dd" }));
		JSONObject jsonObj = JSONObject.fromObject("{\"taskLeafList\":[{\"detail\":\"chgkhgjkghjkhgjkghjk\",\"managerUserId\":\"181\",\"deadline\":\"2016-06-10\"}]}");
		JSONArray jsonArray = jsonObj.getJSONArray("taskLeafList");
		List<TaskLeaf> list = (List<TaskLeaf>)JSONArray.toCollection(jsonArray, TaskLeaf.class);
		System.out.println(list);
	}
	
	
	/*@RequestMapping(value="/json",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> testJson(@RequestBody TaskLeaf leafs){
		try{
			log.info(leafs);
			return this.getSuccessMap("");
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}*/
	/**
	 * 增加任务分组拼音字段
	 */
	@ResponseBody
	@RequestMapping(value="/addTaskGroupPinyin",method=RequestMethod.GET)
	public Map<String,Object> addTaskGroupPinyin(){
		try{
			Map<String,Object> map=this.taskService.addTaskGroupPinyin();
			return map;
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * wjj 20161028
	 * 删除数据库中重复的任务成员信息以及修复成员权限信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/deleteRepeatTaskMember",method=RequestMethod.DELETE)
	public Map<String,Object> deleteRepeatTaskMember(){
		try{
			this.taskService.deleteRepeatTaskMember();
			return this.getSuccessMap("success");
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
}
