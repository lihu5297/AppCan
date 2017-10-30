package org.zywx.cooldev.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_PRIORITY;
import org.zywx.cooldev.commons.Enums.TASK_REPEATABLE;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.dao.EntityResourceRelDao;
import org.zywx.cooldev.dao.TagDao;
import org.zywx.cooldev.dao.app.AppDao;
import org.zywx.cooldev.dao.task.TaskCommentDao;
import org.zywx.cooldev.dao.task.TaskDao;
import org.zywx.cooldev.dao.task.TaskGroupDao;
import org.zywx.cooldev.dao.task.TaskLeafDao;
import org.zywx.cooldev.dao.task.TaskMemberDao;
import org.zywx.cooldev.dao.task.TaskTagDao;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.Tag;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskAuth;
import org.zywx.cooldev.entity.task.TaskComment;
import org.zywx.cooldev.entity.task.TaskExcel;
import org.zywx.cooldev.entity.task.TaskGroup;
import org.zywx.cooldev.entity.task.TaskGroupSort;
import org.zywx.cooldev.entity.task.TaskLeaf;
import org.zywx.cooldev.entity.task.TaskMember;
import org.zywx.cooldev.entity.task.TaskTag;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.util.TimestampFormat;
import org.zywx.cooldev.util.Tools;
import org.zywx.cooldev.vo.UpdatableTask;

@Service
public class TaskService extends BaseService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private TaskMemberDao taskMemberDao;
	@Autowired
	private TaskCommentDao taskCommentDao;
	@Autowired
	private TagDao tagDao;
	@Autowired
	private TaskTagDao taskTagDao;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private ResourcesService resourceService;
	@Autowired
	private TagService tagService;
	@Autowired
	private EntityResourceRelDao entityResourceRelDao;
	@Autowired
	private AppDao appDao;
	@Autowired
	private TaskGroupDao taskGroupDao;
	@Autowired
	private TaskLeafDao taskLeafDao;
	
	@Autowired
	protected UserService userService;
	@Autowired
	protected DynamicService dynamicService;
	
	@Autowired
	protected NoticeService noticeService;
	
	
	/**
	 * 获取任务详情<br>
	 * 添加标签、任务评论及任务资源
	 * 
	 * @param taskId
	 * @return
	 */
	@Cacheable(value="TaskService_getTask",key="#taskId+'_'+#loginUserId")
	public Map<String, Object> getTask(long taskId, long loginUserId) {
		Task task = taskDao.findByIdAndDel(taskId,DELTYPE.NORMAL);
		if(null==task || task.getDel()==DELTYPE.DELETED){
			throw new RuntimeException("任务不存在");
		}
		
		Map<String, Object> message = new HashMap<>();
		// 根据用户权限构建查询条件
		String required = (ENTITY_TYPE.TASK + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
        Process pc=processDao.findOne(task.getProcessId());
		// 项目成员权限
		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required, loginUserId,pc.getProjectId());
		// 流程成员权限
		Map<Long, List<String>> pMapAsProcessMember = processService.permissionMapAsMemberWithAndOnlyByProcessId(required, loginUserId,pc.getId());
		// 任务成员权限
//		Map<Long, List<String>> pMapAsTaskMember = this.permissionMapAsMemberWith(required, loginUserId);
		Map<Long, List<String>> pMapAsTaskMember = this.permissionMapAsMemberWithOnlyTaskId(required, loginUserId,taskId);
		
		
		if(-1!=task.getAppId()){//增加appName
			App app = appDao.findOne(task.getAppId());
			task.setAppName(null==app||null==app.getName()?null:app.getName());
		}
		Process process = processDao.findOne(task.getProcessId());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(process != null) {
			task.setProcessName(process.getName());
			task.setProcessEndTime(sdf.format(process.getEndDate()));
			Project project = projectDao.findOne(process.getProjectId());
			if(project != null) {
				task.setProjectName(project.getName());
				task.setProjectId(project.getId());
			}
		}
		
		// 获取前端许可
		List<String> pListAsProjectMember = pMapAsProjectMember.get(task.getProjectId());
		List<String> pListAsProcessMember = pMapAsProcessMember.get(task.getProcessId());
		List<String> pListAsTaskMember = pMapAsTaskMember.get(task.getId());
		Map<String, Integer> pMap = new HashMap<>();
		if(pListAsProjectMember != null) {
			for(String p : pListAsProjectMember) {
				pMap.put(p, 1);
			}
		}
		if(pListAsProcessMember != null) {
			for(String p : pListAsProcessMember) {
				pMap.put(p, 1);
			}
		}
		if(pListAsTaskMember != null) {
			for(String p : pListAsTaskMember) {
				pMap.put(p, 1);
			}
		}
		if(pMap.size() == 0) {
			throw new RuntimeException("您没有查看该任务的权限");
		}
		
		// 添加成员，负责人
		List<TaskMember> allMembers = taskMemberDao.findByTaskIdAndDel(task.getId(), DELTYPE.NORMAL);
		List<TaskMember> otherMembers = new ArrayList<>();	// leader 之外的其他成员
		for(TaskMember member : allMembers) {
			User user = userDao.findOne(member.getUserId());
			member.setUserName(user.getUserName());
			member.setUserIcon(user.getIcon());
			
			List<TaskAuth> authes = taskAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
			Set<String> roleSet = new HashSet<>();
			for(TaskAuth auth : authes) {
				Role role = Cache.getRole(auth.getRoleId());
				if(role != null) {
					roleSet.add( role.getEnName() );
				}
			}
			member.setRole(new ArrayList<String>(roleSet));
			if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER)) {
				
				task.setLeader(member);
			} 
			if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER)){
				otherMembers.add(member);
			}
			//添加一个
			if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.CREATOR)){
//				otherMembers.add(member);
				User creator = userDao.findOne(member.getUserId());
				task.setCreator(creator);
			}
		}
		task.setMember(otherMembers);
		
		
		// 添加任务资源列表
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(taskId, ENTITY_TYPE.TASK, DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for(EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		if(resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			task.setResource(resources);
			task.setResourceTotal(resources.size());
		}
		
		// 添加任务评论列表
		List<TaskComment> comments = taskCommentDao.findByTaskIdAndDelOrderByCreatedAtDesc(taskId, DELTYPE.NORMAL);
		for(TaskComment comment : comments) {
			User user = userDao.findOne(comment.getUserId());
			if(user != null) {
				comment.setUserIcon(user.getIcon());
				comment.setUserName(user.getUserName());
			}
			
			List<EntityResourceRel> commentRelList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(comment.getId(), ENTITY_TYPE.TASK_COMMENT, DELTYPE.NORMAL);
			if(commentRelList != null && commentRelList.size() > 0) {
				List<Long> rIdList = new ArrayList<>();
				for(EntityResourceRel rel : commentRelList) {
					rIdList.add(rel.getResourceId());
				}
				List<Resource> resources = resourcesDao.findByIdIn(rIdList);
				comment.setResource(resources);
			}
			
			
		}
		task.setComment(comments);
		task.setCommentTotal(comments.size());
		
		// 添加任务标签
		List<TaskTag> taskTagList = getTaskTagList(taskId);
		List<String> tagNameList = new ArrayList<>();
		for(TaskTag tt : taskTagList) {
			Tag tag = tagService.getTag(tt.getTagId());
			if(tag != null) {
				tagNameList.add(tag.getName());
			}
		}
		task.setTag(tagNameList);
		
//		ArrayList<String> array = this.findByTaskStatus(task.getStatus());
		
		//增加子任务
		List<TaskLeaf> listLeaf = taskLeafDao.findByTopTaskIdAndDelOrderByCreatedAt(taskId, DELTYPE.NORMAL);
		for(TaskLeaf leaf:listLeaf){
			User u = userDao.findOne(leaf.getManagerUserId());
			leaf.setManagerName(u.getUserName());
			leaf.setManagerIcon(u.getIcon());
		}
		//增加任务分组
		TaskGroup tg = taskGroupDao.findOne(task.getGroupId());
		if(tg!=null){
			task.setGroupName(tg.getName());
		}
		
		Map<String,Object> innerMap = new HashMap<String, Object>();
		innerMap.put("taskLeafList", listLeaf);
		innerMap.put("task", task);
		message.put("object", innerMap);
		message.put("permissions", pMap);

		return message;
	}
	
	/*private ArrayList<String> findByTaskStatus(TASK_STATUS status) {
		String sql = "select tc.name,tc.status from T_MAN_TASK_CONFIG tc where tc.id in(select tcr.nextTaskId from T_MAN_TASK_CONFIG_RELATE tcr where tcr.taskConfigId in (select t.id from T_MAN_TASK_CONFIG t where t.status = %d ) )";
		sql = String.format(sql, status.ordinal());
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		ArrayList<String> arryList = new ArrayList<>();
		TASK_STATUS task_status[] = TASK_STATUS.values();
		for(Map<String,Object> map : list){
			int statusInt = Integer.parseInt(map.get("status").toString());
			arryList.add(task_status[statusInt].name());
		}
		return arryList;
	}*/

	/**
	 * 添加新任务
	 * @param task
	 * @param tagNames
	 * @param resourceIdList
	 * @param loginUserId
	 * @return
	 * @throws ParseException 
	 */
	public List<TaskLeaf> addTask(
			Task task,
			List<String> tagNames,
			List<Long> resourceIdList,
			long leader,
			List<Long> member,
			long loginUserId) throws ParseException {
		/**
		 * 判断任务分组是否存在
		 */
		TaskGroup taskG = this.findGroupById(task.getGroupId());
		if(null==taskG || taskG.getDel().equals(DELTYPE.DELETED)){
			throw new RuntimeException("任务分组不存在==>"+task.getGroupId());
		}
		taskDao.save(task);
		
		List<TaskLeaf> listLeaf = task.getTaskLeafList();
		List<TaskLeaf> result = new ArrayList<TaskLeaf>();
		if(null!=listLeaf && listLeaf.size()>0){
			for(TaskLeaf tl:listLeaf){
				tl.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				tl.setTopTaskId(task.getId());
				tl.setDel(DELTYPE.NORMAL);
				tl.setProcessId(task.getProcessId());
				tl.setStatus(TASK_STATUS.NOFINISHED);
				tl.setAppId(task.getAppId());
				User u = userDao.findOne(tl.getManagerUserId());
				if(StringUtils.isBlank(tl.getDetail()) || 0==tl.getManagerUserId() || u ==null ||null==tl.getDeadline()){
					continue;
				}
				if(tl.getDetail().length()>1000){
					throw new RuntimeException("子任务描述不能超过1000");
				}
				taskLeafDao.save(tl);
				result.add(tl);
			}
		}
		
		
		if(member==null){
			member = new ArrayList<Long>();
		}
		// 2. add creator, leader, memeber
		Set<Long> memberSet = new HashSet<>(member);
		memberSet.add(loginUserId);//当前人不需要后台添加,完全按照前台传过来的的人员来存储
		memberSet.add(leader);

		// 添加流程成员
		List<TaskMember> taskMembers = new ArrayList<>();
		for(long userId : memberSet) {
			TaskMember tm = new TaskMember();
			tm.setTaskId(task.getId());
			tm.setUserId(userId);
			if(userId == loginUserId) {
				tm.setType(TASK_MEMBER_TYPE.CREATOR);
			} else {
				tm.setType(TASK_MEMBER_TYPE.PARTICIPATOR);
			}
			taskMembers.add(tm);
		}
		if(taskMembers.size() > 0) {
			taskMemberDao.save(taskMembers);
		}
				
		// 成员授权
		List<TaskAuth> taskAuthes = new ArrayList<>();
		for(TaskMember tm : taskMembers) {
			if(tm.getUserId() == loginUserId) {
				String roleEnName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.CREATOR;
				Role role = Cache.getRole(roleEnName);
				if(role != null) {
					TaskAuth auth = new TaskAuth();
					auth.setMemberId(tm.getId());
					auth.setRoleId(role.getId());
					taskAuthes.add(auth);
				}

			}
					
			if(tm.getUserId() == leader) {
				String roleEnName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER;
				Role role = Cache.getRole(roleEnName);
				if(role != null) {
					TaskAuth auth = new TaskAuth();
					auth.setMemberId(tm.getId());
					auth.setRoleId(role.getId());
					taskAuthes.add(auth);
				}
			}
					
			if(member.contains(tm.getUserId())){//只有选了是成员,才会给成员权限
				String roleEnName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER;
				Role role = Cache.getRole(roleEnName);
				if(role != null) {
					TaskAuth auth = new TaskAuth();
					auth.setMemberId(tm.getId());
					auth.setRoleId(role.getId());
					taskAuthes.add(auth);
				}
			}
			
		}
		if(taskAuthes.size() > 0) {
			taskAuthDao.save(taskAuthes);
		}
		
		if(tagNames != null) {
			for(String tagName : tagNames) {
				Tag tag = new Tag();
				tag.setName(tagName);
				tagService.addTag(tag);
				this.addTaskTag(task.getId(), tag.getId(),loginUserId);
			}
		}
		
		if(resourceIdList != null) {
			for(long resourceId : resourceIdList) {
				EntityResourceRel rel = new EntityResourceRel();
				rel.setResourceId(resourceId);
				rel.setEntityId(task.getId());
				rel.setEntityType(ENTITY_TYPE.TASK);
				entityResourceRelDao.save(rel);
			}
		}
		//更新流程完成时间
		processService.changeProcessFinishDateAndStatusAndProgress(task.getProcessId());
		//更新项目状态,完成时间
		projectService.updateProjProgressAndStatus(task.getProjectId());
		return result;
	}
	
	/**
	 * 编辑任务
	 * @param task
	 * @return
	 * @throws ParseException 
	 */
	public int editTask(UpdatableTask task,Long userId) throws ParseException {

		String settings = "";
		if(task.getDetail() != null) {
			if(task.getDetail().length()>1000){
				throw new RuntimeException("任务描述不能超过1000");
			}
			settings += String.format(",detail='%s'", task.getDetail().replace("\\", "\\\\\\\\").replace("'", "\\'"));
		}
		if(task.getPriority() != null) {
			settings += String.format(",priority=%d", task.getPriority().ordinal());
		}
		
		if(task.getDeadline() != null) {
			settings += String.format(",deadline='%s'", task.getDeadline());
		}
		if(task.getRepeatable() != null) {
			settings += String.format(",repeatable=%d", task.getRepeatable().ordinal());
		}
		if(task.getAppId() != -1) {
			settings += String.format(",appId=%d", task.getAppId());
		}
		if(task.getProcessId() != -1) {
			settings += String.format(",processId=%d", task.getProcessId());
		}
//		if(task.getWorkHour() != 0) {
			settings += String.format(",workHour=%.1f", task.getWorkHour());
//		}
		if(null!=task.getGroupId() && task.getGroupId() != -1) {
			/**
			 * 判断任务分组是否存在
			 */
			TaskGroup taskG = this.findGroupById(task.getGroupId());
			if(null==taskG || taskG.getDel().equals(DELTYPE.DELETED)){
				throw new RuntimeException("任务分组不存在");
			}
			settings += String.format(",groupId=%d", task.getGroupId());
		}
		
		if(null != task.getStatus()) {
			settings += String.format(",status=%d", task.getStatus().ordinal());
			settings += String.format(",lastStatusUpdateTime='%s'", new Timestamp(System.currentTimeMillis()));
			
			if(task.getStatus().equals(TASK_STATUS.FINISHED)){
				settings += String.format(",finishUserId=%d", userId);
				settings += String.format(",finishDate='%s'", new Timestamp(System.currentTimeMillis()));
			}else{
				settings += String.format(",finishUserId = -1");
				settings += String.format(",finishDate= null ");
			}
			
		}
		
		if(settings.length() > 0) {
			settings = settings.substring(1);
			String sql = String.format("update T_TASK set %s where id=%d", settings, task.getId());
			log.info("execute Sql:"+sql);
			int a = this.jdbcTemplate.update(sql);
			
			//设置项目状态
			Task taskOld = this.taskDao.findOne(task.getId());
			log.info("get process for projectId,processId:"+taskOld.getProcessId());
			Process process = this.processDao.findOne(taskOld.getProcessId());
			
			//更新流程完成时间
			processService.changeProcessFinishDateAndStatusAndProgress(taskOld.getProcessId());
			//更新项目状态,完成时间
			projectService.updateProjProgressAndStatus(process.getProjectId());
			return a;
		} else {
			return 0;
		}

	}
	
	
	/**
	 * 添加子任务
	 * @param taskId
	 * @param taskLeafList
	 */
	public void addTaskLeaf(Long loginUserId,long taskId,List<TaskLeaf> taskLeafList){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for(TaskLeaf taskLeaf:taskLeafList){
			User u = userDao.findOne(taskLeaf.getManagerUserId());
			if(StringUtils.isBlank(taskLeaf.getDetail()) || 0==taskLeaf.getManagerUserId() || u ==null ||null==taskLeaf.getDeadline()){
				continue;
			}
			if(taskLeaf.getDetail().length()>1000){
				throw new RuntimeException("子任务描述不能超过1000");
			}
			Task task = taskDao.findOne(taskId);
			taskLeaf.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			taskLeaf.setTopTaskId(task.getId());
			taskLeaf.setDel(DELTYPE.NORMAL);
			taskLeaf.setProcessId(task.getProcessId());
			taskLeaf.setStatus(TASK_STATUS.NOFINISHED);
			taskLeaf.setAppId(task.getAppId());
			taskLeafDao.save(taskLeaf);
			Process p = processService.findOne(task.getProcessId());
			//添加动态
			dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_LEAF_ADD, p.getProjectId(), new Object[]{task});
			Long[] recievedIds = new Long[1];
			User user = userService.findUserById(loginUserId);
			recievedIds[0] = taskLeaf.getManagerUserId();
			this.noticeService.addNotice(loginUserId, recievedIds, NOTICE_MODULE_TYPE.TASK_LEAF_ADD_TO_LEADER, new Object[]{user,taskLeaf,sdf.format(taskLeaf.getDeadline())});
			//发送邮件
			this.sendEmail(loginUserId, recievedIds, NOTICE_MODULE_TYPE.TASK_LEAF_ADD_TO_LEADER, new Object[]{user,taskLeaf,sdf.format(taskLeaf.getDeadline())});
		}
	}
	/**
	 * 编辑子任务
	 * 返回是否修改了任务负责人
	 * @param taskLeafId
	 * @param detail
	 * @param deadLine
	 * @param managerUserId
	 * @param status
	 * @throws ParseException
	 */
	public TaskLeaf editTaskLeaf(Long taskLeafId,String detail,String deadLine,Long managerUserId,TASK_STATUS status,Long loginUserId) throws ParseException{
		TaskLeaf tl = taskLeafDao.findOne(taskLeafId);
		if(null==tl){
			throw new RuntimeException("子任务不存在");
		}
		tl.setDeadline(new SimpleDateFormat("yyyy-MM-dd").parse(deadLine));
		if(tl.getDetail().length()>1000){
			throw new RuntimeException("子任务描述不能超过1000");
		}
		tl.setDetail(detail);
		tl.setManagerUserId(managerUserId);
		if(null!=status && !tl.getStatus().equals(status)){
			tl.setStatus(status);
			tl.setLastStatusUpdateTime(new Timestamp(System.currentTimeMillis()));
			if(status.equals(TASK_STATUS.FINISHED)){
				tl.setFinishDate(new Timestamp(System.currentTimeMillis()));
				tl.setFinishUserId(loginUserId);
			}else{
				tl.setFinishDate(null);
				tl.setFinishUserId(-1l);
			}
		}
		taskLeafDao.save(tl);
		return tl;
	}
	
	
	/**
	 * 删除任务
	 * @param taskId
	 * @throws ParseException 
	 */
	public void removeTask(long taskId) throws ParseException {
		Task t= this.taskDao.findOne(taskId);
		Process p = processDao.findOne(t.getProcessId());
		t.setDel(DELTYPE.DELETED);
		this.taskDao.save(t);
		List<TaskLeaf> listTaskLeaf = taskLeafDao.findByTopTaskIdAndDelOrderByCreatedAt(taskId, DELTYPE.NORMAL);
		for(TaskLeaf leaf:listTaskLeaf){
			leaf.setDel(DELTYPE.DELETED);
			taskLeafDao.save(leaf);
		}
	}
	
	/**
	 * 更新流程进度和项目进度
	 * @param p
	 * @throws ParseException
	 */
	public void updateProgress(long taskId) throws ParseException{
		Task t= this.taskDao.findOne(taskId);
		Process p = processDao.findOne(t.getProcessId());
		processService.changeProcessFinishDateAndStatusAndProgress(p.getId());
		projectService.updateProjProgressAndStatus(p.getProjectId());
	}
	
	/**
	 * 删除子任务
	 * @param taskLeafId
	 */
	public void removeTaskLeaf(long taskLeafId){
		TaskLeaf taskLeaf = taskLeafDao.findOne(taskLeafId);
		taskLeaf.setDel(DELTYPE.DELETED);
		taskLeafDao.save(taskLeaf);
	}
	
	/**
	 * 子任务转化为主任务
	 * @param taskLeafId
	 * @throws ParseException 
	 */
	public Task updateToMainTask(long taskLeafId,Long loginUserId) throws ParseException{
		TaskLeaf taskLeaf = taskLeafDao.findOne(taskLeafId);
		Task task = taskDao.findOne(taskLeaf.getTopTaskId());
		//创建一条主任务
		Task newTask = new Task();
		newTask.setAppId(task.getAppId());
		newTask.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		newTask.setDeadline(taskLeaf.getDeadline());
		newTask.setDel(DELTYPE.NORMAL);
		newTask.setDetail(taskLeaf.getDetail());
		newTask.setGroupId(task.getGroupId());
		newTask.setLastStatusUpdateTime(new Timestamp(System.currentTimeMillis()));
		newTask.setPriority(task.getPriority());
		newTask.setProcessId(task.getProcessId());
		newTask.setRepeatable(task.getRepeatable());
		newTask.setStatus(TASK_STATUS.NOFINISHED);
		newTask.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		taskDao.save(newTask);
		
		//任务创建者
		TaskMember taskMember = new TaskMember();
		taskMember.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		taskMember.setDel(DELTYPE.NORMAL);
		taskMember.setTaskId(newTask.getId());
		taskMember.setType(TASK_MEMBER_TYPE.CREATOR);
		taskMember.setUserId(loginUserId);
		taskMemberDao.save(taskMember);
		//创建者权限
		TaskAuth  taCretor = new TaskAuth();
		taCretor.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		taCretor.setDel(DELTYPE.NORMAL);
		taCretor.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		taCretor.setMemberId(taskMember.getId());
		taCretor.setRoleId(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.CREATOR).getId());
		taskAuthDao.save(taCretor);
		

		if(taskLeaf.getManagerUserId()!=loginUserId){
			//任务负责人
			TaskMember taskMemberLeader = new TaskMember();
			taskMemberLeader.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			taskMemberLeader.setDel(DELTYPE.NORMAL);
			taskMemberLeader.setTaskId(newTask.getId());
			taskMemberLeader.setType(TASK_MEMBER_TYPE.PARTICIPATOR);
			taskMemberLeader.setUserId(taskLeaf.getManagerUserId());
			taskMemberDao.save(taskMemberLeader);
			
			//负责人权限
			TaskAuth  taLeader = new TaskAuth();
			taLeader.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			taLeader.setDel(DELTYPE.NORMAL);
			taLeader.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			taLeader.setMemberId(taskMemberLeader.getId());
			taLeader.setRoleId(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId());
			taskAuthDao.save(taLeader);
		}else{
			//负责人权限
			TaskAuth  taLeader = new TaskAuth();
			taLeader.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			taLeader.setDel(DELTYPE.NORMAL);
			taLeader.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			taLeader.setMemberId(taskMember.getId());
			taLeader.setRoleId(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId());
			taskAuthDao.save(taLeader);
		}
		

		
		
		//删除要转换的子任务
		taskLeaf.setDel(DELTYPE.DELETED);
		taskLeafDao.save(taskLeaf);
		//更新流程完成时间
		processService.changeProcessFinishDateAndStatusAndProgress(task.getProcessId());
		//更新项目状态,完成时间
		projectService.updateProjProgressAndStatus(processDao.findOne(task.getProcessId()).getId());
		return newTask;
	}
	
	/**
	 * 获取成员列表
	 * @param taskId
	 * @return
	 */
	public List<TaskMember> getTaskMemberList(long taskId, String queryName, String queryAccount) {

		List<TaskMember> memberList = taskMemberDao.findByTaskIdAndDel(taskId, DELTYPE.NORMAL);

		List<TaskMember> retList = new ArrayList<>();
		
		for(TaskMember m : memberList) {
			User u = userDao.findOne(m.getUserId());
			if(u != null) {
				if(queryName != null) {
					if(!queryName.equals(u.getUserName())) continue;
				}
				if(queryAccount != null) {
					if(!queryAccount.equals(u.getAccount())) continue;
				}

				String name = u.getUserName() != null ? u.getUserName() : u.getAccount();
				
				m.setUserName(name);
				m.setUserIcon(u.getIcon() == null ? "" : u.getIcon());
				m.setUserAccount(u.getAccount());
				retList.add(m);
			}
		}
		
		return retList;
	}
	
	/**
	 * 添加新参与人
	 * @param member
	 * @return
	 */
	public TaskMember addTaskMember(TaskMember member,Long loginUserId) {
		
		User user = userDao.findOne(member.getUserId());
		if(user != null) {
			member.setUserIcon(user.getIcon());
			member.setUserName(user.getUserName());
		}
		
		List<TaskMember> list = taskMemberDao.findByTaskIdAndDel(member.getTaskId(), DELTYPE.NORMAL);
		int i=0;
		if(null!=list && list.size()>0){
			for(TaskMember tm:list){
				if(tm.getUserId()==member.getUserId()){
					//这个人是创建者,或者管理员
					TaskAuth ta = new TaskAuth();
					ta.setMemberId(tm.getId());
					String roleName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER;
					Role role = Cache.getRole(roleName);
					ta.setRoleId(role.getId());
					taskAuthDao.save(ta);
					break;
				}
				i++;
			}
		}
		
		if(null==list || i==list.size()){
			//新添加的人,以前不是该任务的相关人员
			taskMemberDao.save(member);
			TaskAuth ta = new TaskAuth();
			ta.setMemberId(member.getId());
			String roleName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER;
			Role role = Cache.getRole(roleName);
			ta.setRoleId(role.getId());
			taskAuthDao.save(ta);
		}
		
		return member;	
	}
	
	/**
	 * 移除成员
	 * @param memberId
	 */
	public void removeTaskMember(long memberId,Long loginUserId) {
		
		
		TaskMember taskMember = this.taskMemberDao.findOne(memberId);
		
		List<TaskAuth> listTaskAuth = this.taskAuthDao.findByMemberIdAndDel(taskMember.getId(), DELTYPE.NORMAL);
		if(null!=listTaskAuth && listTaskAuth.size()>0){
			String roleName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER;
			Role role = Cache.getRole(roleName);
			for(TaskAuth ta:listTaskAuth){
				if(ta.getRoleId()==role.getId()){
					ta.setDel(DELTYPE.DELETED);
					this.taskAuthDao.save(ta);
				}
			}
			
			if(listTaskAuth.size()==1){
				//只有一个角色的情况下:如果此角色还是普通成员,则需要连成员taskMember记录也删除
				if(listTaskAuth.get(0).getRoleId()==role.getId()){
					taskMember.setDel(DELTYPE.DELETED);
					taskMemberDao.save(taskMember);
				}
			}
			
		}
	}

	/**
	 * 更换任务负责人
	 */
	public synchronized int editTaskManager(long taskId, long newManagerUserId,long loginUserId) {
		String memberRoleName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER;
		Role memberRole = Cache.getRole(memberRoleName);
		
		String roleName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER;
		Role role = Cache.getRole(roleName);
		if(role == null) {
			return 0;
		}
		
		List<TaskMember> members = taskMemberDao.findByTaskIdAndDel(taskId, DELTYPE.NORMAL);
		for(TaskMember member : members) {
			List<TaskAuth> authes = taskAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
			for(TaskAuth auth : authes) {
				if(auth.getRoleId() == role.getId()) {
					// 变更负责人
					if(authes.size()>1){
						//除了项目负责人还有别的身份
						auth.setDel(DELTYPE.DELETED);
						taskAuthDao.save(auth);
					}else{
						//只是项目负责人(更改为普通参与人)
						auth.setRoleId(memberRole.getId());
						taskAuthDao.save(auth);
					}
					
					//新的负责人以前是否在此任务下的成员中
					TaskMember targetMember = taskMemberDao.findByTaskIdAndUserIdAndDel(taskId,newManagerUserId, DELTYPE.NORMAL);
					if(null==targetMember){
						TaskMember taskManager = new TaskMember();
						taskManager.setTaskId(member.getTaskId());
						taskManager.setType(TASK_MEMBER_TYPE.PARTICIPATOR);
						taskManager.setUserId(newManagerUserId);
						taskMemberDao.save(taskManager);

						TaskAuth taManager = new TaskAuth();
						taManager.setMemberId(taskManager.getId());
						taManager.setRoleId(role.getId());
						taskAuthDao.save(taManager);
					}else{
						TaskAuth taManager = new TaskAuth();
						taManager.setMemberId(targetMember.getId());
						taManager.setRoleId(role.getId());
						taskAuthDao.save(taManager);
					}
					
					
					return 1;
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * 添加任务标签
	 * @param taskId
	 * @param tagId
	 * @return
	 */
	public TaskTag addTaskTag(long taskId, long tagId,long loginUserId) {
		TaskTag tt = taskTagDao.findOneByTaskIdAndTagId(taskId, tagId);
		
		if(tt == null) {
			tt = new TaskTag();
			tt.setTagId(tagId);
			tt.setTaskId(taskId);
			taskTagDao.save(tt);
			return tt;
		}
		return null;
	}
	
	/**
	 * 移除任务标签
	 * @param taskId
	 * @param tagId
	 */
	public void removeTaskTag(long taskId, long tagId,long loginUserId) {
		taskTagDao.removeByTaskIdAndTagId(taskId, tagId);
		
	}
	
	/**
	 * 获取任务标签列表
	 * @param taskId
	 * @return
	 */
	public List<TaskTag> getTaskTagList(long taskId) {
		return taskTagDao.findByTaskIdAndDel(taskId, DELTYPE.NORMAL);
	}

	/**
	 * 添加任务评论
	 * @param taskComment
	 * @return
	 */
	public TaskComment addTaskComment(TaskComment taskComment,long loginUserId) {
		
		return taskCommentDao.save(taskComment);
		
	}

	/**
	 * 具有permissionEname权限的任务列表
	 * @user jingjian.wu
	 * @date 2015年11月24日 下午3:13:14
	 */
	public Map<Long, List<String>> permissionMapAsMemberWithOnlyTaskId(String permissionEnName, long loginUserId,long taskId) {
			
		long startTime = System.currentTimeMillis();
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();
		StringBuffer sql= new StringBuffer("select t.taskId,group_concat(t.roleId) from (");
		sql.append(" select distinct member.taskId,auth.roleId from T_TASK_MEMBER member left join T_TASK_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.del=0 and auth.del=0  and member.taskId= "+taskId+" and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( select * from ( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(" ) ttt")
		.append(")")
		.append(") t group by t.taskId");
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
		log.info("taskPermissionSql-->"+sql.toString());
		long searchEndTime = System.currentTimeMillis();
		log.info("taskService permissionsMapAsMember search db time -->"+(searchEndTime-startTime) +" ms");
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						roleIdList.clear();
						roleIdList.addAll(Arrays.asList(rs.getString(2).split(",")));
						permissionSet.clear();
						for(String str:roleIdList){
							List<Permission> listP = null!=Cache.getRole(Integer.parseInt(str))?Cache.getRole(Integer.parseInt(str)).getPermissions():new ArrayList<Permission>();
							for(Permission p:listP){
								permissionSet.add(p.getEnName());
							}
						}
						
						permissionsMapAsMember.put(rs.getLong(1), new ArrayList<String>(permissionSet));
					}
				});
		
		long endTime = System.currentTimeMillis();
		log.info("taskService permissionsMapAsMember total time -->"+(endTime-startTime) +" ms");
		return permissionsMapAsMember;
	}
	/**
	 * 获取任务许可集合<br>
	 * 
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * @param permissionEnName
	 * @return
	 */
	public Map<Long, List<String>> permissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		long startTime = System.currentTimeMillis();
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();
		StringBuffer sql= new StringBuffer("select t.taskId,group_concat(t.roleId) from (");
		sql.append(" select distinct member.taskId,auth.roleId from T_TASK_MEMBER member left join T_TASK_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( select * from ( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(" ) ttt")
		.append(")")
		.append(") t group by t.taskId");
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
		log.info("taskPermissionSql-->"+sql.toString());
		long searchEndTime = System.currentTimeMillis();
		log.info("taskService permissionsMapAsMember search db time -->"+(searchEndTime-startTime) +" ms");
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						roleIdList.clear();
						roleIdList.addAll(Arrays.asList(rs.getString(2).split(",")));
						permissionSet.clear();
						for(String str:roleIdList){
							List<Permission> listP = null!=Cache.getRole(Integer.parseInt(str))?Cache.getRole(Integer.parseInt(str)).getPermissions():new ArrayList<Permission>();
							for(Permission p:listP){
								permissionSet.add(p.getEnName());
							}
						}
						
						permissionsMapAsMember.put(rs.getLong(1), new ArrayList<String>(permissionSet));
					}
				});
		
		long endTime = System.currentTimeMillis();
		log.info("taskService permissionsMapAsMember total time -->"+(endTime-startTime) +" ms");
		return permissionsMapAsMember;
	}
	
	/**
	 * 获取任务ID集合<br>
	 * @author haijun.cheng
	 * @date 2016-09-18
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * @param permissionEnName
	 * @return
	 */
	public Set<Long> getPermissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		long startTime = System.currentTimeMillis();
		final Set<Long> idSet = new HashSet<Long>();
		StringBuffer sql= new StringBuffer(" select distinct member.taskId,auth.roleId from T_TASK_MEMBER member left join T_TASK_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( select * from ( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(" ) ttt")
		.append(")");
		log.info("taskPermissionSql-->"+sql.toString());
		long searchEndTime = System.currentTimeMillis();
		log.info("taskService permissionsMapAsMember search db time -->"+(searchEndTime-startTime) +" ms");
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						idSet.add(rs.getLong(1));
					}
				});
		
		long endTime = System.currentTimeMillis();
		log.info("taskService permissionsMapAsMember total time -->"+(endTime-startTime) +" ms");
		return idSet;
	}

	private void removeExcept(Map<Long, List<String>> map, long remainKey) {
		Set<Long> remainKeySet = new HashSet<>();
		remainKeySet.add(remainKey);
		this.removeExcept(map, remainKeySet);
	}
	
	private void removeExcept(Map<Long, List<String>> map, Set<Long> remainKeySet) {
		List<Long> removeList = new ArrayList<>();
		Iterator<Long> it = map.keySet().iterator();
		while(it.hasNext()) {
			long curKey = it.next();
			if( ! remainKeySet.contains(curKey) ) {
				removeList.add(curKey);
			}
		}
		for(long removeKey : removeList) {
			map.remove(removeKey);
		}
	}

	public Task getSingleTask(long taskId) {
		Task task = this.taskDao.findOne(taskId);
		return task;
	}

	
	/**
	 * 查询任务截止时间离当前时间还有someDay天的子任务
	 * @param someDay
	 * @return
	 */
	public List<Task> findTaskByAdvance(int advance) {
		List<Task> tasks = this.taskDao.findBySomeDayToDeadLineAndStatusAndDel(advance,TASK_STATUS.NOFINISHED.ordinal(),DELTYPE.NORMAL.ordinal());
		return tasks;
		
	}
	
	/**
	 * 查询子任务截止时间离当前时间还有someDay天的子任务
	 * @param someDay
	 * @return
	 */
	public List<TaskLeaf> findTaskLeafByWarning(int someDay){
		List<TaskLeaf> leafList = taskLeafDao.findBySomedaytdeadLineAndStatusAndDelTaskLeaf(someDay, TASK_STATUS.NOFINISHED.ordinal(), DELTYPE.NORMAL.ordinal());
		return leafList;
	}
	

	public void _quickSort(Map<String, Object>[] list, int low, int high) {
		if (low < high) {
			int middle = getMiddle(list, low, high); // 将list数组进行一分为二
			_quickSort(list, low, middle - 1); // 对低字表进行递归排序
			_quickSort(list, middle + 1, high); // 对高字表进行递归排序
		}
	}

	public int getMiddle(Map<String, Object>[] list, int low, int high) {

		Map<String, Object> obj = list[low]; // 数组的第一个作为中轴
		Timestamp tmp = getTimestamp(obj);

		while (low < high) {

			while (low < high && getTimestamp(list[high]).getTime() < tmp.getTime()) {
				high--;
			}
			list[low] = list[high]; // 比中轴小的记录移到低端
			while (low < high && getTimestamp(list[low]).getTime() > tmp.getTime()) {
				low++;
			}
			while (low < high && getTimestamp(list[low]).getTime() == tmp.getTime()) {
				low++;
			}
			list[high] = list[low]; // 比中轴大的记录移到高端

		}
		list[low] = obj; // 中轴记录到尾
		return low; // 返回中轴的位置
	}

	private Timestamp getTimestamp(Map<String, Object> obj) {
		Timestamp times = new Timestamp(0L);
		
		Task task  = (Task) obj.get("object");
		times = task.getCreatedAt();
		return times;
	}

	
	public TaskMember getTaskMember(long memberId) {
		return this.taskMemberDao.findOne(memberId);
	}
	
	public List<Task> findByProcessId(Long processId){
		return this.taskDao.findByProcessIdAndDel(processId, DELTYPE.NORMAL);
	}
	
	public List<Task> findByAppId(Long appId){
		return this.taskDao.findByAppIdAndDel(appId, DELTYPE.NORMAL);
	}
	
	/**
	 * 查找重复性任务
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午5:48:54
	 */
	public List<Task> findByRepeatableNotAndDel(TASK_REPEATABLE repeatable,DELTYPE del){
		return taskDao.findByRepeatableNotAndDel(repeatable, del);
	}
	public static void main(String[] args) {
		List<Long> a = new ArrayList<Long>();
		a.add(1L);
		a.add(22L);
		a.add(33L);
		a.add(55L);
		System.out.println(a.toString().replace("[", "(").replace("]", ")"));
		System.out.println(String.format(",workHour=%.1f", 0f));;
		
	}

	/**
	 * 每天定时运行,插入重复性任务
	 * @throws CloneNotSupportedException 
	 * @throws ParseException 
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午6:14:51
	 */
	public void saveRepeatTask() throws CloneNotSupportedException, ParseException{
		//重复性任务
		List<Task> repeatTasks = findByRepeatableNotAndDel(TASK_REPEATABLE.NONE, DELTYPE.NORMAL);
		Calendar cal = Calendar.getInstance();
		if(null!=repeatTasks){
			for(Task task:repeatTasks){
				int t = 0;
				if(TASK_REPEATABLE.DAY.equals(task.getRepeatable())){
					t=1;
				}else if(TASK_REPEATABLE.WEEK.equals(task.getRepeatable())){
					t=7;
				}else if(TASK_REPEATABLE.TWOWEEK.equals(task.getRepeatable())){
					t=14;
				}else if(TASK_REPEATABLE.MONTH.equals(task.getRepeatable())){
					t=30;
				}
				
				if(TimestampFormat.daysBetween(task.getCreatedAt(), cal.getTime())==t){
					TASK_REPEATABLE originRepeat = task.getRepeatable();
					//将原任务改为不重复
					task.setRepeatable(TASK_REPEATABLE.NONE);
					taskDao.save(task);
					
					//新创建任务
					Task newTask = new Task();
					newTask.setAppId(task.getAppId());
					newTask.setAppName(task.getAppName());
					newTask.setDetail(task.getDetail());
					newTask.setLeader(task.getLeader());
					newTask.setPriority(task.getPriority());
					newTask.setProcessId(task.getProcessId());
					Date deadLine = task.getDeadline();
					Calendar calTmp = Calendar.getInstance();
					calTmp.setTime(deadLine);
					if(TASK_REPEATABLE.DAY.equals(originRepeat)){
						calTmp.add(Calendar.DATE, 1);
					}else if(TASK_REPEATABLE.WEEK.equals(originRepeat)){
						calTmp.add(Calendar.DATE, 7);
					}else if(TASK_REPEATABLE.TWOWEEK.equals(originRepeat)){
						calTmp.add(Calendar.DATE, 14);
					}else if(TASK_REPEATABLE.MONTH.equals(originRepeat)){
						calTmp.add(Calendar.DATE, 30);
					}
					newTask.setDeadline(calTmp.getTime());
					newTask.setRepeatable(originRepeat);
					newTask.setGroupId(task.getGroupId());
					//添加任务
					taskDao.save(newTask);
					
					//添加子任务
					List<TaskLeaf> listTaskLeaf = taskLeafDao.findByTopTaskIdAndDelOrderByCreatedAt(task.getId(), DELTYPE.NORMAL);
					for(TaskLeaf taskLeaf:listTaskLeaf){
						TaskLeaf leaf = (TaskLeaf) taskLeaf.clone();
						leaf.setId(null);
						leaf.setTopTaskId(newTask.getId());
						taskLeafDao.save(leaf);
					}
					
					// 添加成员及权限
					List<TaskMember> allMembers = taskMemberDao.findByTaskIdAndDel(task.getId(), DELTYPE.NORMAL);
					for(TaskMember taskMember:allMembers){
						TaskMember newTaskMember = (TaskMember) taskMember.clone();
						newTaskMember.setId(null);
						newTaskMember.setTaskId(newTask.getId());
						taskMemberDao.save(newTaskMember);
						List<TaskAuth> authes = taskAuthDao.findByMemberIdAndDel(taskMember.getId(), DELTYPE.NORMAL);
						for(TaskAuth taskAuth:authes){
							TaskAuth newTaskAuth = (TaskAuth) taskAuth.clone();
							newTaskAuth.setId(null);
							newTaskAuth.setMemberId(newTaskMember.getId());
							taskAuthDao.save(newTaskAuth);
						}
					}
					
					// 添加任务资源列表
					List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(task.getId(), ENTITY_TYPE.TASK, DELTYPE.NORMAL);
					for(EntityResourceRel resourceRel:relList){
						EntityResourceRel newResourceRel = (EntityResourceRel)resourceRel.clone();
						newResourceRel.setId(null);
						newResourceRel.setEntityId(newTask.getId());
						entityResourceRelDao.save(newResourceRel);
					}
					
					// 添加任务标签
					List<TaskTag> taskTagList = getTaskTagList(task.getId());
					for(TaskTag taskTag:taskTagList){
						TaskTag newTaskTag = (TaskTag)taskTag.clone();
						newTaskTag.setId(null);
						newTaskTag.setTaskId(newTask.getId());
						taskTagDao.save(newTaskTag);
					}
				}
				
			}
		}
	}

	

	
	public TaskMember getTaskManager(long taskId) {
		String roleName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER;
		Role role = Cache.getRole(roleName);
		Long roleId = role.getId();
		TaskMember member = this.taskMemberDao.findByTaskIdAndRoleIdAndDel(taskId,roleId,DELTYPE.NORMAL);
		return member;
	}
	
	public TaskMember getTaskCreator(long taskId) {
		String roleName = ENTITY_TYPE.TASK + "_" + ROLE_TYPE.CREATOR;
		Role role = Cache.getRole(roleName);
		Long roleId = role.getId();
		TaskMember member = this.taskMemberDao.findByTaskIdAndRoleIdAndDel(taskId,roleId,DELTYPE.NORMAL);
		return member;
	}
	
   
	/**
	 * 查询某个人能看到的所有标签名称(查询任务列表的时候筛选条件)
	 * @user jingjian.wu
	 * @date 2016年3月14日 上午11:22:51
	 */
	@Cacheable(value="TaskService_getTagNameForTasklist",key="#projectId+'_'+#loginUserId+'_'+#keyWords")
	public List<String> getTagNameForTasklist(Long loginUserId,String keyWords,Long projectId){
		String taskIds = this.getTaskIdsForSomeOne(loginUserId,projectId);
		String result ="";
		if(null==keyWords){
			result = "SELECT distinct name FROM T_TAG  WHERE del=0 AND id IN (  SELECT tagId FROM T_TASK_TAG WHERE del=0 AND taskId IN ("+taskIds+"))";
		}else{
			keyWords="%"+keyWords.trim()+"%";
			result = "SELECT distinct name FROM T_TAG  WHERE del=0 AND id IN (  SELECT tagId FROM T_TASK_TAG WHERE del=0 AND taskId IN ("+taskIds+")) and name like '"+keyWords+"'";
		}
		final List<String> taskNameList = new ArrayList<String>();
		this.jdbcTpl.query(result, 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						taskNameList.add(rs.getString("name"));
					}
				});
		return taskNameList;
		
	}
	/**
	 * 获取某个人能查看的任务的Id集合
	 * @user jingjian.wu
	 * @date 2016年3月14日 上午11:02:27
	 */
	@Cacheable(value="TaskService_getTaskIdsForSomeOne")
	public String /*List<Long>*/ getTaskIdsForSomeOne(long loginUserId,Long projectId){
		String required = (ENTITY_TYPE.TASK + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		//第一步  哪些角色可以查看任务
		StringBuffer roleIdsSql = new StringBuffer();
		roleIdsSql.append(" select distinct roleId from T_ROLE_AUTH where premissionId = (select id from T_PERMISSION where enName= ")
		.append("'").append(required).append("'")
		.append(" and del=0 )");
		
		//第二步  哪些团队下可以查看任务	
		StringBuffer teamIdsSql  = new StringBuffer();
				teamIdsSql.append(" select  teamId from T_TEAM_MEMBER where id in (")
//				.append(" select distinct memberId from T_TEAM_AUTH  where ")
//				.append(" roleId in(")
//				.append(roleIdsSql)
//				.append(") and del=0")
				.append(" select distinct auth.memberId from T_TEAM_AUTH auth right join ( ")
				.append(roleIdsSql)
				.append(") tmp1 on auth.roleId= tmp1.roleId and auth.del=0")
				
				
				
				.append(" ) and userId =").append(loginUserId)
				.append(" and del=0");
				
				
		//第三步  哪些项目下可以查看任务
				StringBuffer projectIdsSql = new StringBuffer();
	   	if(null!=projectId){//即使选了项目,也需要看你在此项目下是否有权限查看任务
		 	   projectIdsSql.append("select id from(")
		 	   
		 	   .append(" select id from T_PROJECT where teamId in(").append(teamIdsSql).append(" )  and del=0 ").append(" and id in (").append(projectId).append(")")
		 	   .append(" union ")
		 	   .append(" select projectId as id from T_PROJECT_MEMBER where id in(")
//		 	   .append(" select memberId from T_PROJECT_AUTH  where roleId in(")
//		 	   .append(roleIdsSql).append(" ) and del=0 ")
		 	  .append(" select authprj.memberId from T_PROJECT_AUTH  authprj right join (")
		 	   .append(roleIdsSql).append(" ) tmp2 on authprj.roleId= tmp2.roleId and authprj.del=0 ")
		 	   
		 	   .append("  ) and userId=").append(loginUserId)
		 	   .append("  and del=0 ")
		 	   .append(" and projectId in(").append(projectId).append(")")
		 	   .append(") t");
		 		
		 	   			
	   	}else{
	 	   projectIdsSql.append("select id from(")
	 	   
	 	   .append(" select id from T_PROJECT where teamId in(").append(teamIdsSql).append(" )  and del=0")
	 	   .append(" union ")
	 	   .append(" select projectId as id from T_PROJECT_MEMBER where id in(")
//	 	   .append(" select memberId from T_PROJECT_AUTH  where roleId in(")
//	 	   .append(roleIdsSql).append(") and del=0 ")
	 	  .append(" select authprj.memberId from T_PROJECT_AUTH  authprj right join (")
	 	   .append(roleIdsSql).append(" ) tmp2 on authprj.roleId= tmp2.roleId and authprj.del=0 ")
	 	   .append(" ) and userId=").append(loginUserId)
	 	   .append("  and del=0")
	 	   .append(") t");
	 		
	 	   			
	   	}	
	   	//第三步 哪些流程下可以查看任务	
	   			
	   	StringBuffer processIdsSql = new StringBuffer();
	   	if(null==projectId){
	   		processIdsSql.append("select distinct * from (")
			.append(" select distinct id from T_PROCESS where projectId in (")
			.append(projectIdsSql)
			.append(") and del=0 ");
	   		processIdsSql.append(" union ")
			.append(" select processId as id from T_PROCESS_MEMBER where id in(")
			 .append(" select authprc.memberId from T_PROCESS_AUTH  authprc right join (")
		 	   .append(roleIdsSql).append(" ) tmp3 on authprc.roleId= tmp3.roleId and authprc.del=0 ")
			.append(" ) and del = 0 and userId=").append(loginUserId)
			.append(") tprocess");
	   	}else{
	   		processIdsSql.append(" select distinct id from T_PROCESS where projectId in (")
			.append(projectIdsSql)
			.append(") and del=0 ");
	   	}
			
	   		//第四步  哪些任务下可以查看任务	
	   	StringBuffer taskIdsSql = new StringBuffer();
	   	
	   			taskIdsSql.append("select id from T_TASK where id in( select * from (")
	   			.append("select distinct taskId from T_TASK_MEMBER where id in (")
	   			.append(" select authtask.memberId from T_TASK_AUTH  authtask right join (")
			 	   .append(roleIdsSql).append(" ) tmp4 on authtask.roleId= tmp4.roleId and authtask.del=0 ")
	   			.append(") and userId=").append(loginUserId).append(" and del=0 ) t) and del=0 ");
	   			if(null!=projectId){
	   				taskIdsSql.append(" and processId in(").append("select id from T_PROCESS where projectId =").append(projectId).append(" and del=0 )");
	   			}
	   			//增加子任务
	   			taskIdsSql.append(" union  ")
	   			.append(" select DISTINCT topTaskId from T_TASK_LEAF where del=0 and managerUserId= ").append(loginUserId);
	   			if(null!=projectId){
	   				taskIdsSql.append(" and processId in(").append("select id from T_PROCESS where projectId =").append(projectId).append(" and del=0 )");
	   			}
				
				//其他
				StringBuffer otherTaskIdsSql = new StringBuffer();
			   	otherTaskIdsSql.append("select id from T_TASK where processId in( ").append(processIdsSql).append(") and del=0 ");
			   	
				
				StringBuffer sql = new StringBuffer();
				sql.append("select t.id from T_TASK t right join (")
				.append(otherTaskIdsSql).append(" union ").append(taskIdsSql)
				.append(") t_ids on t.id = t_ids.id and t.del=0 ");
				return sql.toString();
	}
	
	
	//@Cacheable(value="TaskService_getTaskLists")
	public Map<String,Object> getTaskLists(int pageNo ,int pageSize,
			long loginUserId,
			String detail,
			List<String> timeStatus,//延迟状态
			List<TASK_STATUS> taskStatus, //任务完成状态
			Long creatorId,String creator,Long leaderId,String leader,Long partnerId,String partner,//创建人 负责人 参与人
			List<TASK_PRIORITY> priority,//优先级 正常,紧急,非常紧急
			Long teamId,String teamName,Long projectId,String projName,Long processId,String processName,Long appId,String appName,//团队,项目,流程,应用
			String tagName,//标签
			String createBegin,String createEnd,//创建时间
			String endTimeBegin,String endTimeEnd,//截止时间
			String completeTimeBegin,String completeTimeEnd,//完成时间
			Long completeUserId,String completeUser,//完成人
			Long  groupId,String groupName,//任务分组ID
			String orderBy,
			String sortBy
			) {
		
		final List< Map<String, Object> > message = new ArrayList<>();
		String taskIdStr = this.getTaskIdsForSomeOne(loginUserId,projectId);//获取我能查看到的任务ID
		StringBuffer whereSql = new StringBuffer();//主任务条件
		StringBuffer commonWhereSql = new StringBuffer();//主任务,子任务公共条件
		StringBuffer leafTaskSql = new StringBuffer(" where del=0 ");//子任务条件
		//主任务描述
		if(StringUtils.isNotBlank(detail)){
			whereSql.append(" and  detail like '%").append(detail.trim()).append("%'");
			leafTaskSql.append(" and  detail like '%").append(detail.trim()).append("%'");
		}
		
		//延迟状态
		
		StringBuffer statusSql = new StringBuffer();
		if(null!=timeStatus && timeStatus.size()>0 && timeStatus.size()!=3){
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			statusSql.append("(1=2 ");
			String nowDate = sdf.format(cal.getTime());
			cal.add(Calendar.DATE, 2);
			String twoDateLater = sdf.format(cal.getTime());
			if(timeStatus.contains("normal")){
				statusSql.append(" or LEAST(a.deadline,b.endDate) >'").append(twoDateLater).append("'");
			}
			if(timeStatus.contains("jjyq")){//即将延期
				statusSql.append(" or (LEAST(a.deadline,b.endDate) >='").append(nowDate).append("'");
				statusSql.append(" and LEAST(a.deadline,b.endDate) <='").append(twoDateLater).append("'");
				statusSql.append(" and a.status=0)");
			}
			if(timeStatus.contains("yyq")){//已延期
				statusSql.append(" or LEAST(a.deadline,b.endDate) <'").append(nowDate).append("'");
			}
			statusSql.append(")");
		}
		if(!statusSql.toString().contains("LEAST")){//如果延迟状态上面的statusSql并未拼接(),这时候去掉延迟状态查询,防止查询1=2永远查不到结果
			statusSql.delete(0, statusSql.length());
		}
		//完成状态
		if(null!=taskStatus && taskStatus.size()>0 && taskStatus.size()!=2){
			commonWhereSql.append(" and  tk.status =").append(taskStatus.get(0).ordinal());
		}
		
		//创建者
		if(StringUtils.isNotBlank(creator) || null!=creatorId){
			String creatorSql ="";
			if(null!=creatorId){
				creatorSql = "select taskId from T_TASK_MEMBER where del=0 and type=0 and userId ="+creatorId;
			}else{
				creatorSql = "select taskId from T_TASK_MEMBER where del=0 and type=0 and userId in ("+
						"select id from T_USER where del=0 and userName like '%"+creator+"%'"
						+")";
			}
			commonWhereSql.append(" and tk.id in (").append(creatorSql).append(")");
		}
		//负责人
		if(StringUtils.isNotBlank(leader)|| null!=leaderId){
			String leaderSql = "";
			if(null!=leaderId){
				leaderSql = "select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0   and tm.userId ="+
						leaderId
						+" and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId() ;
				leafTaskSql.append(" and managerUserId=").append(leaderId);
			}else{
				leaderSql = "select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0   and tm.userId in ("+
						"select id from T_USER where del=0 and userName like '%"+leader+"%'"
						+") and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId() ;
				
				leafTaskSql.append(" and managerUserId in (")
				.append("select id from T_USER where del=0 and userName like '%").append(leader).append("%'")
				.append(")");
			}
			whereSql.append(" and t.id in (").append(leaderSql).append(")");
		}
		//参与人
		if(StringUtils.isNotBlank(partner)|| null!=partnerId){
			String partnerSql ="";
			if(null!=partnerId){
				partnerSql ="select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0  and tm.userId ="+
						partnerId
						+" and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MEMBER).getId() ;
			}else{
				partnerSql ="select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0  and tm.userId in ("+
						"select id from T_USER where del=0 and userName like '%"+partner+"%'"
						+") and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MEMBER).getId() ;
			}
			commonWhereSql.append(" and tk.id in (").append(partnerSql).append(")");
		}
		//主任务优先级
		if(null!=priority && priority.size()>0 &&  priority.size()!=3){
			StringBuffer priorityStr = new StringBuffer();
			for(TASK_PRIORITY pri:priority){
				priorityStr.append(pri.ordinal()).append(",");
			}
			if(priorityStr.toString().length()>1){
				priorityStr.deleteCharAt(priorityStr.length()-1);
			}
			commonWhereSql.append(" and tk.priority in ( ").append(priorityStr).append(") ");
		}
		//团队
		String teamNameSql="";
		if(StringUtils.isNotBlank(teamName) || null!=teamId){
			if(null!=teamId){
				teamNameSql = "select id from T_PROJECT where  teamId ="+teamId+" and del=0 ";
			}else{
				String teamSql = "select id from T_TEAM where name like '%"+teamName.trim()+"%' and del=0 ";
				teamNameSql = "select id from T_PROJECT where  teamId in ("+teamSql+") and del=0 ";
			}
		}
		//项目
		String projNameSql = "";
		if(StringUtils.isNotBlank(projName) || null!=projectId){
			if(null!=projectId){
				projNameSql = "select id from T_PROJECT where del=0 and id =  "+projectId ;
				if(StringUtils.isNotBlank(teamName)){
					projNameSql+=" and id in ("+teamNameSql+")";
				}
			}else{
				projNameSql = "select id from T_PROJECT where del=0 and name like '%"+Tools.sqlFormat(projName.trim())+"%' ";
				if(StringUtils.isNotBlank(teamName)){
					projNameSql+=" and id in ("+teamNameSql+")";
				}
			}
		}else{
			projNameSql = teamNameSql;
		}
		//流程
		String processNameSql = "";
		if(StringUtils.isNotBlank(processName) || null!=processId){
			if(null!=processId){
				processNameSql ="select id from T_PROCESS where del=0 and id="+processId;
				if(StringUtils.isNotBlank(projNameSql)){
					processNameSql +=" and projectId in ("+projNameSql+")";
				}
			}else{
				processNameSql ="select id from T_PROCESS where del=0 and name like '%"+processName.trim()+"%'";
				if(StringUtils.isNotBlank(projNameSql)){
					processNameSql +=" and projectId in ("+projNameSql+")";
				}
			}
		}else{
			if(StringUtils.isNotBlank(projNameSql)){
				processNameSql = "select id from T_PROCESS where del=0 and projectId in ("+projNameSql+")";
			}
		}
		if(StringUtils.isNotBlank(processNameSql)){
			commonWhereSql.append(" and tk.processId in (").append(processNameSql).append(")");
		}
		//应用
		if(StringUtils.isNotBlank(appName) || null!=appId){
			if(null!=appId){
				commonWhereSql.append(" and tk.appId ="+appId);
			}else{
				commonWhereSql.append(" and tk.appId in (").append("select id from T_APP where del=0 and name like '%").append(appName).append("%'").append(")");
			}
		}
		
		//标签
		if(StringUtils.isNotBlank(tagName)){
			String tagSql = "SELECT taskId FROM T_TASK_TAG WHERE del=0 AND tagId IN ("  +
					"SELECT id FROM T_TAG  where del=0 AND name like '%"+tagName+"%'"
					+")";
			commonWhereSql.append(" and tk.id in (").append(tagSql).append(")");
		}
		
		//创建时间
		if(StringUtils.isNotBlank(createBegin)){
			commonWhereSql.append(" and DATE_FORMAT(tk.createdAt,'%Y-%m-%d') >='").append(createBegin).append("'");
		}
		if(StringUtils.isNotBlank(createEnd)){
			commonWhereSql.append(" and DATE_FORMAT(tk.createdAt,'%Y-%m-%d') <='").append(createEnd).append("'");
		}
		
		//截止时间
		if(StringUtils.isNotBlank(endTimeBegin)){
			whereSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')>='").append(endTimeBegin).append("'");
			
			leafTaskSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')>='").append(endTimeBegin).append("'");
		}
		if(StringUtils.isNotBlank(endTimeEnd)){
			whereSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')<='").append(endTimeEnd).append("'");
			
			leafTaskSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')<='").append(endTimeEnd).append("'");
		}
		
		//完成时间
		if(StringUtils.isNotBlank(completeTimeBegin)){
			commonWhereSql.append(" and DATE_FORMAT(tk.finishDate ,'%Y-%m-%d')>='").append(completeTimeBegin).append("'");
		}
		if(StringUtils.isNotBlank(completeTimeEnd)){
			commonWhereSql.append(" and DATE_FORMAT(tk.finishDate ,'%Y-%m-%d')<='").append(completeTimeEnd).append("'");
		}
		//完成人
		if(StringUtils.isNotBlank(completeUser) || null!=completeUserId){
			if(null!=completeUserId){
				commonWhereSql.append(" and tk.finishUserId= ").append(completeUserId);
			}else{
				commonWhereSql.append(" and tk.finishUserId in (")
				.append("select id from T_USER where userName like '%").append(completeUser).append("%'")
				.append(")");
			}
		}

		//分组
		if(null!=groupId || StringUtils.isNotBlank(groupName)){
			if(null!=groupId){
				commonWhereSql.append(" and tk.groupId = ").append(groupId);
			}else{
				List<Long> groupIds = taskGroupDao.findIdsByProjectIdAndNameLikeAndDel(projectId, "%"+groupName.trim()+"%", DELTYPE.NORMAL);
				groupIds.add(-99L);
				commonWhereSql.append(" and tk.groupId in ").append(groupIds.toString().replace("[", "(").replace("]", ")"));
			}
		}
		
		//由于描述,负责人,和截止时间,如果子任务满足条件,也需要将此子任务对应的主任务查询出来,所以增加子任务满足条件的主任务ID
		StringBuilder leafTaskSqlForMainTask = new StringBuilder("select topTaskId from T_TASK_LEAF").append(leafTaskSql)
				.append(" and topTaskId in(").append(taskIdStr.toString()).append(")");
		
		//添加排序
		String	execSql = " select * from T_TASK tk where (tk.id in(select * from ("+taskIdStr.toString()+whereSql+") as xxx) or tk.id in (  "+leafTaskSqlForMainTask+" ) )"+" and tk.del=0 "+commonWhereSql;
		String	execSqlFinished = " select * from T_TASK tk  where (tk.id in("+taskIdStr.toString()+whereSql+" ) or tk.id in (  "+leafTaskSqlForMainTask+" ) )"+" and tk.del=0 "+commonWhereSql;
		//增加延期状态
	   	execSql = "SELECT a.*,app.name as appName,b.name as processName,b.endDate as processEndTime,"
	   			+"pj.id as projectId,pj.name as projectName,tg.name as groupName,count(tl.id) as allTaskLeafSum,sum(case when tl.status=0  then 1 else 0 end) as noFinishedTaskLeafSum"
	   			+" FROM (" + execSql + ") a left join T_PROCESS b on a.processId=b.id"
	   			+" left join T_APP app on(app.id=a.appId and app.del=0) left join T_PROJECT pj on(b.projectId=pj.id) left join T_TASK_GROUP tg on(tg.id=a.groupId and tg.del=0) left join T_TASK_LEAF tl on(tl.topTaskId=a.id and tl.del=0)"
	   			+" WHERE b.del=0  " + ("".equals(statusSql.toString())?"":(" and "+statusSql.toString()))
	   			;
	   	execSqlFinished = "SELECT a.* FROM (" + execSqlFinished + ") a left join T_PROCESS b on a.processId=b.id WHERE b.del=0  " + ("".equals(statusSql.toString())?"":(" and "+statusSql.toString()))
	   			+" order by a."+orderBy+" " + sortBy;
	   	//查询总条数
	   	String	totleSql = "SELECT count(1) as total,ifnull(sum(case when t.status=1 then 1 else 0 end),0) as finishTotal FROM (" + execSqlFinished + " ) t";
	   //	String	totleFinishedSql = "SELECT count(1) FROM (" + execSqlFinished + " ) t";
	   	log.info("task_totle_sql -->"+totleSql);
	   	Map<String,Object> totalMap=this.jdbcTemplate.queryForMap(totleSql);
	   	Long totle = Long.parseLong(totalMap.get("total").toString());
	   	Long totleFinished= Long.parseLong(totalMap.get("finishTotal").toString());
	   //	Long totleFinished = jdbcTpl.queryForObject(totleFinishedSql, Long.class);
		execSql+=" group by a.id ";
	   	execSql+=" order by a."+orderBy+" " + sortBy;
	   	execSql += " limit " +pageNo*pageSize + ", "+pageSize;
	   	log.info("execute-tasklist-sql-->"+execSql);
	   	Long totlePages = (totle-1)/pageSize+1;
	   	final List<Task> volist = new ArrayList<Task>();
	   	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   	long  ListForStartTime=System.currentTimeMillis();
		this.jdbcTpl.query(execSql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						Task vo  = new Task();
						vo.setDetail(rs.getString("detail"));
						vo.setProcessId(rs.getLong("processId"));
						vo.setGroupId(rs.getLong("groupId"));
						vo.setAppId(rs.getLong("appId"));
						int repeat = rs.getInt("repeatable");
						if(repeat ==TASK_REPEATABLE.NONE.ordinal()){
							vo.setRepeatable(TASK_REPEATABLE.NONE);
						}else if(repeat ==TASK_REPEATABLE.DAY.ordinal()){
							vo.setRepeatable(TASK_REPEATABLE.DAY);
						}else if(repeat ==TASK_REPEATABLE.WEEK.ordinal()){
							vo.setRepeatable(TASK_REPEATABLE.WEEK);
						}else if(repeat ==TASK_REPEATABLE.TWOWEEK.ordinal()){
							vo.setRepeatable(TASK_REPEATABLE.TWOWEEK);
						}else if(repeat ==TASK_REPEATABLE.MONTH.ordinal()){
							vo.setRepeatable(TASK_REPEATABLE.MONTH);
						}
						int priority = rs.getInt("priority");
						
						if(priority==TASK_PRIORITY.NORMAL.ordinal()){
							vo.setPriority(TASK_PRIORITY.NORMAL);
						}else if(priority==TASK_PRIORITY.URGENT.ordinal()){
							vo.setPriority(TASK_PRIORITY.URGENT);
						}else if(priority==TASK_PRIORITY.VERY_URGENT.ordinal()){
							vo.setPriority(TASK_PRIORITY.VERY_URGENT);
						}
						
						int status = rs.getInt("status");
						
						if(status==TASK_STATUS.NOFINISHED.ordinal()){
							vo.setStatus(TASK_STATUS.NOFINISHED);
						}else if(status==TASK_STATUS.FINISHED.ordinal()){
							vo.setStatus(TASK_STATUS.FINISHED);
						}
						
						vo.setLastStatusUpdateTime(rs.getTimestamp("lastStatusUpdateTime"));
						
						
						try {
							if(null!=rs.getDate("deadline")){
								vo.setDeadline(sdf.parse(sdf.format(rs.getDate("deadline"))));
							}
							if(null!=rs.getDate("finishDate")){
								vo.setFinishDate(sdf.parse(sdf.format(rs.getDate("finishDate"))));
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
						vo.setId(rs.getLong("id"));
						vo.setCreatedAt(rs.getTimestamp("createdAt"));
						
						vo.setUpdatedAt(rs.getTimestamp("updatedAt"));
						vo.setProcessName(rs.getString("processName"));
						vo.setProcessEndTime(sdf.format(rs.getTimestamp("processEndTime")));
						vo.setProjectName(rs.getString("projectName"));
						vo.setProjectId(rs.getLong("projectId"));
//						Process process = processDao.findOne(vo.getProcessId());
//						if(process != null) {
//							vo.setProcessName(process.getName());
//							vo.setProcessEndTime(sdf.format(process.getEndDate()));
//							Project project = projectDao.findOne(process.getProjectId());
//							if(project != null) {
//								vo.setProjectName(project.getName());
//								vo.setProjectId(project.getId());
//							}
//						}
						if(vo.getAppId()!=-1){
//							App app = appDao.findOne(vo.getAppId());
//							if(null!=app){
//								vo.setAppName(app.getName());
//							}
							if(rs.getString("appName")==null){
								vo.setAppName("");
							}else{
								vo.setAppName(rs.getString("appName"));
							}
						}
						
						List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(vo.getId(), ENTITY_TYPE.TASK, DELTYPE.NORMAL);
						List<Long> resourceIdList = new ArrayList<>();
						for(EntityResourceRel rel : relList) {
							resourceIdList.add(rel.getResourceId());
						}
						if(resourceIdList.size() > 0) {
							List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
							vo.setResource(resources);
							vo.setResourceTotal(resources.size());
						}
						long taskCommentTotal = taskCommentDao.countByTaskIdAndDel(vo.getId(), DELTYPE.NORMAL);
						vo.setCommentTotal(new Long(taskCommentTotal).intValue());
						// 添加成员，负责人
						List<TaskMember> allMembers = taskMemberDao.findByTaskIdAndDel(vo.getId(), DELTYPE.NORMAL);
						for(TaskMember member : allMembers) {
							User user = userDao.findOne(member.getUserId());
							member.setUserName(user.getUserName());
							List<TaskAuth> authes = taskAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
							Set<String> roleSet = new HashSet<>();
							for(TaskAuth auth : authes) {
								Role role = Cache.getRole(auth.getRoleId());
								if(role != null) {
									roleSet.add( role.getEnName() );
								}
							}
							if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER)) {
								vo.setLeader(member);
							} 
							if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.CREATOR)){
								vo.setCreator(user);
							}
							//子任务未完成数,子任务总数
							vo.setNoFinishedTaskLeafSum(rs.getInt("noFinishedTaskLeafSum"));
							vo.setAllTaskLeafSum(rs.getInt("allTaskLeafSum"));
//							List<TASK_STATUS> statusTmp = new ArrayList<TASK_STATUS>();
//							statusTmp.add(TASK_STATUS.NOFINISHED);
//							vo.setNoFinishedTaskLeafSum(taskLeafDao.countByTopTaskIdAndStatusInAndDel(vo.getId(),statusTmp, DELTYPE.NORMAL));;
//							statusTmp.add(TASK_STATUS.FINISHED);
//							vo.setAllTaskLeafSum(taskLeafDao.countByTopTaskIdAndStatusInAndDel(vo.getId(),statusTmp, DELTYPE.NORMAL));
						}
						//增加任务分组
//						TaskGroup tg = taskGroupDao.findOne(vo.getGroupId());
//						if(tg!=null){
//							vo.setGroupName(tg.getName());
//						}
						vo.setGroupName(rs.getString("groupName"));
						Map<String, Object> element = new HashMap<>();
						
						element.put("object", vo);
						message.add(element);
						volist.add(vo);
					}
				});
		long  ListForEndTime=System.currentTimeMillis();
		log.info(String.format("list for time [%s]ms",ListForEndTime-ListForStartTime));
//		if(null!=volist){
//			for(Task task:volist){
//				Process process = processDao.findOne(task.getProcessId());
//				if(process != null) {
//					task.setProcessName(process.getName());
//					task.setProcessEndTime(sdf.format(process.getEndDate()));
//					Project project = projectDao.findOne(process.getProjectId());
//					if(project != null) {
//						task.setProjectName(project.getName());
//						task.setProjectId(project.getId());
//					}
//				}
//				if(task.getAppId()!=-1){
//					App app = appDao.findOne(task.getAppId());
//					if(null!=app){
//						task.setAppName(app.getName());
//					}
//				}
//				
//				List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(task.getId(), ENTITY_TYPE.TASK, DELTYPE.NORMAL);
//				List<Long> resourceIdList = new ArrayList<>();
//				for(EntityResourceRel rel : relList) {
//					resourceIdList.add(rel.getResourceId());
//				}
//				if(resourceIdList.size() > 0) {
//					List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
//					task.setResource(resources);
//					task.setResourceTotal(resources.size());
//				}
//				long taskCommentTotal = taskCommentDao.countByTaskIdAndDel(task.getId(), DELTYPE.NORMAL);
//				task.setCommentTotal(new Long(taskCommentTotal).intValue());
//				
//				
//				// 添加成员，负责人
//				List<TaskMember> allMembers = taskMemberDao.findByTaskIdAndDel(task.getId(), DELTYPE.NORMAL);
//				for(TaskMember member : allMembers) {
//					User user = userDao.findOne(member.getUserId());
//					member.setUserName(user.getUserName());
//					List<TaskAuth> authes = taskAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
//					Set<String> roleSet = new HashSet<>();
//					for(TaskAuth auth : authes) {
//						Role role = Cache.getRole(auth.getRoleId());
//						if(role != null) {
//							roleSet.add( role.getEnName() );
//						}
//					}
//					if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER)) {
//						task.setLeader(member);
//					} 
//					if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.CREATOR)){
//						task.setCreator(user);
//					}
//					//子任务未完成数,子任务总数
//					List<TASK_STATUS> statusTmp = new ArrayList<TASK_STATUS>();
//					statusTmp.add(TASK_STATUS.NOFINISHED);
//					task.setNoFinishedTaskLeafSum(taskLeafDao.countByTopTaskIdAndStatusInAndDel(task.getId(),statusTmp, DELTYPE.NORMAL));;
//					statusTmp.add(TASK_STATUS.FINISHED);
//					task.setAllTaskLeafSum(taskLeafDao.countByTopTaskIdAndStatusInAndDel(task.getId(),statusTmp, DELTYPE.NORMAL));
//					
//				}
//				//增加任务分组
//				TaskGroup tg = taskGroupDao.findOne(task.getGroupId());
//				if(tg!=null){
//					task.setGroupName(tg.getName());
//				}
//				Map<String, Object> element = new HashMap<>();
//				
//				element.put("object", task);
//				message.add(element);
//			}
//		}
		
		
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("list",message);
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("total",totle.intValue());
		map.put("totalFinished",totleFinished.intValue());
		map.put("group",null!=groupId?groupId:"");
		map.put("pageNo", pageNo);
		map.put("pageSize", pageSize);
		map.put("totlePages", totlePages.intValue());
		result.put("pageInfo",map);
		return result;
	}
	
	public List<TaskExcel> downloadTask(
			long loginUserId,
			String detail,
			List<String> timeStatus,//延迟状态
			List<TASK_STATUS> taskStatus, //任务完成状态
			Long creatorId,String creator,Long leaderId,String leader,Long partnerId,String partner,//创建人 负责人 参与人
			List<TASK_PRIORITY> priority,//优先级 正常,紧急,非常紧急
			Long teamId,String teamName,Long projectId,String projName,Long processId,String processName,Long appId,String appName,//团队,项目,流程,应用
			String tagName,//标签
			String createBegin,String createEnd,//创建时间
			String endTimeBegin,String endTimeEnd,//截止时间
			String completeTimeBegin,String completeTimeEnd,//完成时间
			Long completeUserId,String completeUser,//完成人
			Long  groupId,String groupName,//任务分组ID
			String orderBy,
			String sortBy
			) {
		
		String taskIdStr = this.getTaskIdsForSomeOne(loginUserId,projectId);//获取我能查看到的任务ID
		StringBuffer whereSql = new StringBuffer();//主任务条件
		StringBuffer commonWhereSql = new StringBuffer();//主任务,子任务公共条件
		StringBuffer leafTaskSql = new StringBuffer(" where del=0 ");//子任务条件
		//主任务描述
		if(StringUtils.isNotBlank(detail)){
			whereSql.append(" and  detail like '%").append(detail.trim()).append("%'");
			leafTaskSql.append(" and  detail like '%").append(detail.trim()).append("%'");
		}
		
		//延迟状态
		
		StringBuffer statusSql = new StringBuffer();
		if(null!=timeStatus && timeStatus.size()>0 && timeStatus.size()!=3){
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			statusSql.append("(1=2 ");
			String nowDate = sdf.format(cal.getTime());
			cal.add(Calendar.DATE, 2);
			String twoDateLater = sdf.format(cal.getTime());
			if(timeStatus.contains("normal")){
				statusSql.append(" or LEAST(a.deadline,b.endDate) >'").append(twoDateLater).append("'");
			}
			if(timeStatus.contains("jjyq")){//即将延期
				statusSql.append(" or (LEAST(a.deadline,b.endDate) >='").append(nowDate).append("'");
				statusSql.append(" and LEAST(a.deadline,b.endDate) <='").append(twoDateLater).append("'");
				statusSql.append(")");
			}
			if(timeStatus.contains("yyq")){//已延期
				statusSql.append(" or LEAST(a.deadline,b.endDate) <'").append(nowDate).append("'");
			}
			statusSql.append(")");
		}
		if(!statusSql.toString().contains("LEAST")){//如果延迟状态上面的statusSql并未拼接(),这时候去掉延迟状态查询,防止查询1=2永远查不到结果
			statusSql.delete(0, statusSql.length());
		}
		//完成状态
		if(null!=taskStatus && taskStatus.size()>0 && taskStatus.size()!=2){
			commonWhereSql.append(" and  tk.status =").append(taskStatus.get(0).ordinal());
		}
		
		//创建者
		if(StringUtils.isNotBlank(creator) || null!=creatorId){
			String creatorSql ="";
			if(null!=creatorId){
				creatorSql = "select taskId from T_TASK_MEMBER where del=0 and type=0 and userId ="+creatorId;
			}else{
				creatorSql = "select taskId from T_TASK_MEMBER where del=0 and type=0 and userId in ("+
						"select id from T_USER where del=0 and userName like '%"+creator+"%'"
						+")";
			}
			commonWhereSql.append(" and tk.id in (").append(creatorSql).append(")");
		}
		//负责人
		if(StringUtils.isNotBlank(leader)|| null!=leaderId){
			String leaderSql = "";
			if(null!=leaderId){
				leaderSql = "select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0   and tm.userId ="+
						leaderId
						+" and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId() ;
				leafTaskSql.append(" and managerUserId=").append(leaderId);
			}else{
				leaderSql = "select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0   and tm.userId in ("+
						"select id from T_USER where del=0 and userName like '%"+leader+"%'"
						+") and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId() ;
				
				leafTaskSql.append(" and managerUserId in (")
				.append("select id from T_USER where del=0 and userName like '%").append(leader).append("%'")
				.append(")");
			}
			whereSql.append(" and t.id in (").append(leaderSql).append(")");
		}
		//参与人
		if(StringUtils.isNotBlank(partner)|| null!=partnerId){
			String partnerSql ="";
			if(null!=partnerId){
				partnerSql ="select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0  and tm.userId ="+
						partnerId
						+" and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MEMBER).getId() ;
			}else{
				partnerSql ="select tm.taskId from T_TASK_MEMBER tm left join T_TASK_AUTH ta on tm.id = ta.memberId where tm.del=0 and ta.del=0  and tm.userId in ("+
						"select id from T_USER where del=0 and userName like '%"+partner+"%'"
						+") and ta.roleId = "+Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MEMBER).getId() ;
			}
			commonWhereSql.append(" and tk.id in (").append(partnerSql).append(")");
		}
		//主任务优先级
		if(null!=priority && priority.size()>0 &&  priority.size()!=3){
			StringBuffer priorityStr = new StringBuffer();
			for(TASK_PRIORITY pri:priority){
				priorityStr.append(pri.ordinal()).append(",");
			}
			if(priorityStr.toString().length()>1){
				priorityStr.deleteCharAt(priorityStr.length()-1);
			}
			commonWhereSql.append(" and tk.priority in ( ").append(priorityStr).append(") ");
		}
		//团队
		String teamNameSql="";
		if(StringUtils.isNotBlank(teamName) || null!=teamId){
			if(null!=teamId){
				teamNameSql = "select id from T_PROJECT where  teamId ="+teamId+" and del=0 ";
			}else{
				String teamSql = "select id from T_TEAM where name like '%"+teamName.trim()+"%' and del=0 ";
				teamNameSql = "select id from T_PROJECT where  teamId in ("+teamSql+") and del=0 ";
			}
		}
		//项目
		String projNameSql = "";
		if(StringUtils.isNotBlank(projName) || null!=projectId){
			if(null!=projectId){
				projNameSql = "select id from T_PROJECT where del=0 and id =  "+projectId ;
				if(StringUtils.isNotBlank(teamName)){
					projNameSql+=" and id in ("+teamNameSql+")";
				}
			}else{
				projNameSql = "select id from T_PROJECT where del=0 and name like '%"+Tools.sqlFormat(projName.trim())+"%' ";
				if(StringUtils.isNotBlank(teamName)){
					projNameSql+=" and id in ("+teamNameSql+")";
				}
			}
		}else{
			projNameSql = teamNameSql;
		}
		//流程
		String processNameSql = "";
		if(StringUtils.isNotBlank(processName) || null!=processId){
			if(null!=processId){
				processNameSql ="select id from T_PROCESS where del=0 and id="+processId;
				if(StringUtils.isNotBlank(projNameSql)){
					processNameSql +=" and projectId in ("+projNameSql+")";
				}
			}else{
				processNameSql ="select id from T_PROCESS where del=0 and name like '%"+processName.trim()+"%'";
				if(StringUtils.isNotBlank(projNameSql)){
					processNameSql +=" and projectId in ("+projNameSql+")";
				}
			}
		}else{
			if(StringUtils.isNotBlank(projNameSql)){
				processNameSql = "select id from T_PROCESS where del=0 and projectId in ("+projNameSql+")";
			}
		}
		if(StringUtils.isNotBlank(processNameSql)){
			commonWhereSql.append(" and tk.processId in (").append(processNameSql).append(")");
		}
		//应用
		if(StringUtils.isNotBlank(appName) || null!=appId){
			if(null!=appId){
				commonWhereSql.append(" and tk.appId ="+appId);
			}else{
				commonWhereSql.append(" and tk.appId in (").append("select id from T_APP where del=0 and name like '%").append(appName).append("%'").append(")");
			}
		}
		
		//标签
		if(StringUtils.isNotBlank(tagName)){
			String tagSql = "SELECT taskId FROM T_TASK_TAG WHERE del=0 AND tagId IN ("  +
					"SELECT id FROM T_TAG  where del=0 AND name like '%"+tagName+"%'"
					+")";
			commonWhereSql.append(" and tk.id in (").append(tagSql).append(")");
		}
		
		//创建时间
		if(StringUtils.isNotBlank(createBegin)){
			commonWhereSql.append(" and DATE_FORMAT(tk.createdAt,'%Y-%m-%d') >='").append(createBegin).append("'");
		}
		if(StringUtils.isNotBlank(createEnd)){
			commonWhereSql.append(" and DATE_FORMAT(tk.createdAt,'%Y-%m-%d') <='").append(createEnd).append("'");
		}
		
		
		//截止时间
		if(StringUtils.isNotBlank(endTimeBegin)){
			whereSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')>='").append(endTimeBegin).append("'");
			
			leafTaskSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')>='").append(endTimeBegin).append("'");
		}
		if(StringUtils.isNotBlank(endTimeEnd)){
			whereSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')<='").append(endTimeEnd).append("'");
			
			leafTaskSql.append(" and DATE_FORMAT(deadline ,'%Y-%m-%d')<='").append(endTimeEnd).append("'");
		}
		
		//完成时间
		if(StringUtils.isNotBlank(completeTimeBegin)){
			commonWhereSql.append(" and DATE_FORMAT(tk.finishDate ,'%Y-%m-%d')>='").append(completeTimeBegin).append("'");
		}
		if(StringUtils.isNotBlank(completeTimeEnd)){
			commonWhereSql.append(" and DATE_FORMAT(tk.finishDate ,'%Y-%m-%d')<='").append(completeTimeEnd).append("'");
		}
		//完成人
		if(StringUtils.isNotBlank(completeUser) || null!=completeUserId){
			if(null!=completeUserId){
				commonWhereSql.append(" and tk.finishUserId= ").append(completeUserId);
			}else{
				commonWhereSql.append(" and tk.finishUserId in (")
				.append("select id from T_USER where userName like '%").append(completeUser).append("%'")
				.append(")");
			}
		}

		//分组
		if(null!=groupId || StringUtils.isNotBlank(groupName)){
			if(null!=groupId){
				commonWhereSql.append(" and tk.groupId = ").append(groupId);
			}else{
				List<Long> groupIds = taskGroupDao.findIdsByProjectIdAndNameLikeAndDel(projectId, "%"+groupName.trim()+"%", DELTYPE.NORMAL);
				groupIds.add(-99L);
				commonWhereSql.append(" and tk.groupId in ").append(groupIds.toString().replace("[", "(").replace("]", ")"));
			}
		}
		
		//由于描述,负责人,和截止时间,如果子任务满足条件,也需要将此子任务对应的主任务查询出来,所以增加子任务满足条件的主任务ID
		StringBuilder leafTaskSqlForMainTask = new StringBuilder("select topTaskId from T_TASK_LEAF").append(leafTaskSql)
				.append(" and topTaskId in(").append(taskIdStr.toString()).append(")");
		
		//添加排序
		String	execSql = " select * from T_TASK tk where (tk.id in("+taskIdStr.toString()+whereSql+") or tk.id in (  "+leafTaskSqlForMainTask+" ) )"+" and tk.del=0 "+commonWhereSql;
		//增加延期状态
	   	execSql = "SELECT a.* FROM (" + execSql + ") a left join T_PROCESS b on a.processId=b.id WHERE b.del=0  " + ("".equals(statusSql.toString())?"":(" and "+statusSql.toString()))
	   			+" order by a."+orderBy+" " + sortBy;
	   	log.info("execute-tasklist-sql-->"+execSql);
	   	final List<TaskExcel> volist = new ArrayList<TaskExcel>();
	   	final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   	
		this.jdbcTpl.query(execSql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						TaskExcel vo  = new TaskExcel();
						int status = rs.getInt("status");//状态
						
						if(status==TASK_STATUS.NOFINISHED.ordinal()){
							vo.setStatus("未完成");
						}else if(status==TASK_STATUS.FINISHED.ordinal()){
							vo.setStatus("已完成");
						}
						vo.setDetail(rs.getString("detail"));//描述
						
						int priority = rs.getInt("priority");//优先级
						
						if(priority==TASK_PRIORITY.NORMAL.ordinal()){
							vo.setPriority("正常");
						}else if(priority==TASK_PRIORITY.URGENT.ordinal()){
							vo.setPriority("紧急");
						}else if(priority==TASK_PRIORITY.VERY_URGENT.ordinal()){
							vo.setPriority("非常紧急");
						}
						//所属项目
						Long processId =rs.getLong("processId");
						Process process = processDao.findOne(processId);
						if(process != null) {
							Project project = projectDao.findOne(process.getProjectId());
							if(project != null) {
								vo.setProjectName(project.getName());
							}
						}
						//所属分组
						TaskGroup taskGroup=taskGroupDao.findOne(rs.getLong("groupId"));       
						vo.setGroupName(taskGroup.getName());
						//所属应用
						if(rs.getLong("appId")!=-1){
							App app = appDao.findOne(rs.getLong("appId"));
							if(null!=app){
								vo.setAppName(app.getName());
							}
						}
						//负责人
						TaskMember mLeader = taskMemberDao.findByTaskIdAndRoleIdAndDel(rs.getLong("id"), Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId(), DELTYPE.NORMAL);
						if(null!=mLeader){
							vo.setLeader(userDao.findOne(mLeader.getUserId()).getUserName());
						}
						
						//创建人
						TaskMember mCreator= taskMemberDao.findByTaskIdAndRoleIdAndDel(rs.getLong("id"), Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.CREATOR).getId(), DELTYPE.NORMAL);
						if(null!=mCreator){
							vo.setCreator(userDao.findOne(mCreator.getUserId()).getUserName());
						}
						
						//创建时间
						vo.setCreatedAt(sdf.format(rs.getTimestamp("createdAt")));
						//计划截至时间
						vo.setDeadline(sdf.format(rs.getTimestamp("deadline")));
						//实际完成时间
						if(null!=rs.getDate("finishDate")){
							vo.setFinishDate(sdf.format(rs.getTimestamp("finishDate")));
						}else{
							vo.setFinishDate(" -- ");
						}
						//工时
						vo.setWorkHour(rs.getString("workHour"));
						volist.add(vo);
					}
				});
		
			
		return volist;
	}
	
	/**
	 * 为已有项目初始化6个任务分组
	 * WAITING, ONGOING, REJECTED, FINISHED, SUSPENDED, CLOSED
	 */
	public void saveInitTaskGroupForProject(){
		List<Project>  listPrj = projectDao.findByDel(DELTYPE.NORMAL);
		for(Project prj:listPrj){
			for(int i =0;i<6;i++){
				TaskGroup tg = new TaskGroup();
				tg.setSort(i);
				tg.setCreatedAt(new Timestamp(System.currentTimeMillis()));
				tg.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				tg.setProjectId(prj.getId());
				switch (i) {
				case 0:
					tg.setName("待进行");
					break;
				case 1:
					tg.setName("进行中");
					break;
				case 2:
					tg.setName("被驳回");
					break;
				case 3:
					tg.setName("已完成");
					break;
				case 4:
					tg.setName("已搁置");
					break;
				case 5:
					tg.setName("已关闭");
					break;
				default:
					break;
				}
				taskGroupDao.save(tg);
			}
		}
	}
	
	/**
	 * 将以前任务的六个状态对应到新的数据结构的任务分组
	 * WAITING, ONGOING, REJECTED, FINISHED, SUSPENDED, CLOSED
	 * eg:  某个任务的(原先status=1)group=1 代表进行中,则需要找到此任务所在的项目ID,然后根据projectID,sort(任务的group),找到对应的TASK_GROUP对象,然后将
	 * 任务的group改为TASK_GROUP对象的ID
	 */
	public void updateTaskGroupInit(){
		List<Task> listTask = taskDao.findByDel(DELTYPE.NORMAL);
		for(Task task:listTask){
			Process process = processDao.findOne(task.getProcessId());
			TaskGroup tg = taskGroupDao.findByProjectIdAndSortAndDel(process.getProjectId(), new Long(task.getGroupId()).intValue(),DELTYPE.NORMAL);
			task.setGroupId(tg.getId());
			taskDao.save(task);
		}
	}
	
	/**
	 * 创建任务分组
	 * @param taskGroup
	 */
	public void addTaskGroup(TaskGroup taskGroup){
		if(StringUtils.isBlank(taskGroup.getName())){
			throw new RuntimeException("分组名称不能为空");
		}
		//增加拼音
		taskGroup.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(taskGroup.getName()==null?"":taskGroup.getName()));
		taskGroup.setPinYinName(ChineseToEnglish.getPingYin(taskGroup.getName()==null?"":taskGroup.getName()));
		if(taskGroup.getProjectId()==0){
			throw new RuntimeException("请选择分组所在项目");
		}
		TaskGroup group = taskGroupDao.findByProjectIdAndName(taskGroup.getProjectId(), taskGroup.getName());
		int maxSort = taskGroupDao.findMaxSortByProjectAndDel(taskGroup.getProjectId(), DELTYPE.NORMAL.ordinal());
		if(group==null){
			taskGroup.setSort(maxSort+1);
			taskGroupDao.save(taskGroup);
		}else if(group.getDel().equals(DELTYPE.DELETED)){
			group.setDel(DELTYPE.NORMAL);
			group.setSort(maxSort+1);
			taskGroupDao.save(group);
		}else if(group.getName().equals(taskGroup.getName())){
			throw new RuntimeException("分组名称已存在");
		}
		
	}
	/**
	 * 修改任务分组
	 * @param taskGroup
	 */
	public void editTaskGroup(TaskGroup taskGroup){
		TaskGroup groupByName = taskGroupDao.findByProjectIdAndNameAndDel(taskGroup.getProjectId(), taskGroup.getName(),DELTYPE.NORMAL);
		if(groupByName!=null){
			throw new RuntimeException("任务分组名称已存在");
		}
		if(null==taskGroup.getId() || taskGroup.getId()==0){
			throw new RuntimeException("任务分组参数ID不合法");
		}
		if(StringUtils.isBlank(taskGroup.getName())){
			throw new RuntimeException("分组名称不能为空");
		}
		if(taskGroup.getProjectId()==0){
			throw new RuntimeException("请选择分组所在项目");
		}
		TaskGroup group = taskGroupDao.findOne(taskGroup.getId());
		if(group==null || group.getDel().equals(DELTYPE.DELETED)){
			throw new RuntimeException("分组不存在");
		}else if(!group.getName().equals(taskGroup.getName())){
			group.setName(taskGroup.getName());
			//修改拼音
			group.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(taskGroup.getName()==null?"":taskGroup.getName()));
			group.setPinYinName(ChineseToEnglish.getPingYin(taskGroup.getName()==null?"":taskGroup.getName()));
			taskGroupDao.save(group);
		}
		
	}
	
	/**
	 * @describe 更新任务分组的排序	
	 */
	public void updateTaskGroupSort(Long projectId,Long loginUserId,List<Long> groupList) {
		List<TaskGroupSort> taskGroupSort = this.taskGroupSortDao.findByProjectIdAndUserIdOrderBySortAsc(projectId,loginUserId);
		taskGroupSortDao.delete(taskGroupSort);
		Long sort = 0l;
		for(Long groupId:groupList){
			TaskGroupSort taskSort = new TaskGroupSort();
			taskSort.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			taskSort.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			taskSort.setDel(DELTYPE.NORMAL);
			taskSort.setGroupId(groupId);
			taskSort.setProjectId(projectId);
			taskSort.setUserId(loginUserId);
			taskSort.setSort(sort);
			sort++;
			taskGroupSortDao.save(taskSort);
		}
	}
	
	
	/**
	 * 删除任务分组
	 * @param taskGroupId
	 */
	public boolean delTaskGroup(long taskGroupId,Long transferToGroupId){
		boolean haveTaskInGroup = false;
		TaskGroup targetGroup  = taskGroupDao.findOne(taskGroupId);
		if(null==targetGroup){
			throw new RuntimeException("任务分组不存在");
		}
		List<Task> tasksInGroup = taskDao.findByGroupIdAndDel(taskGroupId, DELTYPE.NORMAL);
		if(null!=tasksInGroup && tasksInGroup.size()>0 && transferToGroupId!=null){//此分组下还有未删除的任务
			TaskGroup transferToGroup  = taskGroupDao.findOne(transferToGroupId);
			if(null==transferToGroup){
				throw new RuntimeException("任务转移的目标分组不存在");
			}else if(targetGroup.getProjectId()!=transferToGroup.getProjectId()){
				throw new RuntimeException("目标分组不合法,只能转移到此项目下的分组中");
			}
			for(Task task:tasksInGroup){
				task.setGroupId(transferToGroupId);
				taskDao.save(task);
			}
			haveTaskInGroup = true;
		}else if(null!=tasksInGroup && tasksInGroup.size()>0 && null ==transferToGroupId ){
			throw new RuntimeException("此分组下还有为转移的任务,请选择目标分组进行转移");
		}
		targetGroup.setDel(DELTYPE.DELETED);
		taskGroupDao.save(targetGroup);
		return haveTaskInGroup;
	}
	
	/**
	 * 获取任务分组
	 * @param projectId
	 * @param groupName 模糊匹配
	 */
	public Map<String,Object> getTaskGroupList(long projectId,String groupName,Long loginUserId){
		Map<String,Object> map = new HashMap<String,Object>();
		List<TaskGroup> list  = new ArrayList<TaskGroup>();
		if(StringUtils.isNotBlank(groupName)){
			list =  taskGroupDao.findByProjectIdAndNameLikeOrPinYinAndDel(projectId, "%"+groupName.trim()+"%",groupName.trim()+"%", DELTYPE.NORMAL);
		}else{
			list = taskGroupDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
		}
		for(TaskGroup g:list){
			List<TASK_STATUS> status = new ArrayList<TASK_STATUS>();
			status.add(TASK_STATUS.FINISHED);
			g.setFinishTaskTotal(taskDao.countByGroupIdAndStatusInAndDel(g.getId(), status, DELTYPE.NORMAL));
			status.add(TASK_STATUS.NOFINISHED);
			g.setAllTaskSum(taskDao.countByGroupIdAndStatusInAndDel(g.getId(), status, DELTYPE.NORMAL));
			
		}
		map.put("object", list);
		List<Permission> permission = projectService.getPermissionList(loginUserId, projectId);
		Map<String,Object> mapPermission = new HashMap<String,Object>();
		for(Permission p :permission){
			mapPermission.put(p.getEnName(), 1);
		}
		map.put("permission", mapPermission);
		return map;
	}
	
	/**
	 * 根据项目,人员查询排好序的任务分组
	 * @param projectId
	 * @param userId
	 * @return
	 */
	public Map<String,Object> getOrderedTaskGroupByProjectIdAndUserId(Long projectId,Long userId){
		Map<String,Object> map = new HashMap<String,Object>();
		List<TaskGroup> result =  taskGroupDao.getOrderedTaskGroupByProjectIdAndUserId(projectId, userId, DELTYPE.NORMAL.ordinal());
		map.put("object", result);
		List<Permission> permission = projectService.getPermissionList(userId, projectId);
		Map<String,Object> mapPermission = new HashMap<String,Object>();
		for(Permission p :permission){
			mapPermission.put(p.getEnName(), 1);
		}
		map.put("permission", mapPermission);
		return map;
	}
	
	/**
	 * 获取我创建/负责/参与的;完成/未完成;分组下的任务;
	 * @param pageable
	 * @param loginUserId
	 * @return
	 */
	public Page<Task> findByRoleTypeAndStatusAndGroup(Pageable pageable,long loginUserId,List<ROLE_TYPE> roleType,List<TASK_STATUS> status,Long groupId){
		if(null==status){
			status = new ArrayList<TASK_STATUS>();
			status.add(TASK_STATUS.NOFINISHED);
			status.add(TASK_STATUS.FINISHED);
		}
		Page<Task> taskList =null;
		List<Object> taskIdsObject=new ArrayList<Object>();
		List<Long> taskIds=new ArrayList<Long>();
		if(null!=roleType && roleType.size()>0){
			if(roleType.contains(ROLE_TYPE.CREATOR)){
				taskIdsObject= taskDao.findCreatedTasks(loginUserId);
				for(Object tId:taskIdsObject){
					taskIds.add(Long.parseLong(tId.toString()));
				}
			}
			
			if(roleType.contains(ROLE_TYPE.MANAGER)){
				Role taskLeaderRole = Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER);
				taskIdsObject = taskDao.findManagedTasks(loginUserId, taskLeaderRole.getId());
				for(Object tId:taskIdsObject){
					taskIds.add(Long.parseLong(tId.toString()));
				}
			}
			if(roleType.contains(ROLE_TYPE.MEMBER)){
				Role taskMemberRole = Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MEMBER);
				taskIdsObject = taskDao.findJoinedTasks(loginUserId, taskMemberRole.getId());
				for(Object tId:taskIdsObject){
					taskIds.add(Long.parseLong(tId.toString()));
				}
			}
			
		}
		if(null==groupId){
			taskList = taskDao.findByIdInAndStatusInAndDel(taskIds, status, DELTYPE.NORMAL, pageable);
		}else{
			taskList = taskDao.findByIdInAndStatusInAndGroupIdAndDel(taskIds, status,groupId, DELTYPE.NORMAL, pageable);
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(Task task:taskList){
			Process process = processDao.findOne(task.getProcessId());
			if(process != null) {
				task.setProcessName(process.getName());
				task.setProcessEndTime(sdf.format(process.getEndDate()));
				Project project = projectDao.findOne(process.getProjectId());
				if(project != null) {
					task.setProjectName(project.getName());
					task.setProjectId(project.getId());
				}
			}
			if(task.getAppId()!=-1){
				App app = appDao.findOne(task.getAppId());
				if(null!=app){
					task.setAppName(app.getName());
				}
			}
			
			List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(task.getId(), ENTITY_TYPE.TASK, DELTYPE.NORMAL);
			List<Long> resourceIdList = new ArrayList<>();
			for(EntityResourceRel rel : relList) {
				resourceIdList.add(rel.getResourceId());
			}
			if(resourceIdList.size() > 0) {
				List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
				task.setResource(resources);
				task.setResourceTotal(resources.size());
			}
			long taskCommentTotal = taskCommentDao.countByTaskIdAndDel(task.getId(), DELTYPE.NORMAL);
			task.setCommentTotal(new Long(taskCommentTotal).intValue());
			
			
			// 添加成员，负责人
			List<TaskMember> allMembers = taskMemberDao.findByTaskIdAndDel(task.getId(), DELTYPE.NORMAL);
			for(TaskMember member : allMembers) {
				User user = userDao.findOne(member.getUserId());
				if(null!=user){
					member.setUserName(user.getUserName());
					List<TaskAuth> authes = taskAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					Set<String> roleSet = new HashSet<>();
					for(TaskAuth auth : authes) {
						Role role = Cache.getRole(auth.getRoleId());
						if(role != null) {
							roleSet.add( role.getEnName() );
						}
					}
					if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER)) {
						task.setLeader(member);
					} 
					if(roleSet.contains(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.CREATOR)){
						task.setCreator(user);
					}
				}
			}
			//增加任务分组
			TaskGroup tg = taskGroupDao.findOne(task.getGroupId());
			if(tg!=null){
				task.setGroupName(tg.getName());
			}
			
			//未完成子任务数量,及全部子任务数量
			List<TASK_STATUS> statusTmp = new ArrayList<TASK_STATUS>();
			statusTmp.add(TASK_STATUS.NOFINISHED);
			task.setNoFinishedTaskLeafSum(taskLeafDao.countByTopTaskIdAndStatusInAndDel(task.getId(),statusTmp, DELTYPE.NORMAL));;
			statusTmp.add(TASK_STATUS.FINISHED);
			task.setAllTaskLeafSum(taskLeafDao.countByTopTaskIdAndStatusInAndDel(task.getId(),statusTmp, DELTYPE.NORMAL));
			
		}
		return taskList;
	}
	
	public TaskGroup findGroupById(Long groupId){
		return taskGroupDao.findOne(groupId);
	}

	public Map<String, Object> addTaskGroupPinyin() {
		List<TaskGroup> taskGroups = this.taskGroupDao.findByDel(DELTYPE.NORMAL);
		for(TaskGroup taskGroup : taskGroups){
			taskGroup.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(taskGroup.getName()==null?"":taskGroup.getName()));
			taskGroup.setPinYinName(ChineseToEnglish.getPingYin(taskGroup.getName()==null?"":taskGroup.getName()));
		}
		this.taskGroupDao.save(taskGroups);
		return this.getSuccessMap("affected "+taskGroups.size());
	}

	/**
	 * 删除任务成员表中的重复记录
	 * 手工调用,不提供接口,处理重复数据的时候用一下
	 */
	public void deleteRepeatTaskMember() {
		String execSql = "select taskId,userId, max(updatedAt) from T_TASK_MEMBER where  del =0 group by taskId ,userId HAVING count(1) >1";
		final List<TaskMember> list = new ArrayList<TaskMember>();
		this.jdbcTpl.query(execSql, 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						long taskId = rs.getLong("taskId");
						long userId = rs.getLong("userId");
						TaskMember tm = new TaskMember();
						tm.setTaskId(taskId);
						tm.setUserId(userId);
						list.add(tm);
					}
				});
		//先将这个人在这个任务下面的成员和权限信息需要删除,先记录到下面两个list中.
		List<TaskMember> readyDelMem = new ArrayList<TaskMember>();
		List<TaskAuth> readyDelAuth = new ArrayList<TaskAuth>();
		for(TaskMember tm:list){
			List<TaskMember> delMem = taskMemberDao.findRepeatTaskIdAndUserIdAndDel(tm.getTaskId(),tm.getUserId(), DELTYPE.NORMAL);
			for(TaskMember t:delMem){
				List<TaskAuth> talist = taskAuthDao.findByMemberIdAndDel(t.getId(), DELTYPE.NORMAL);
				for(TaskAuth ta:talist){
					readyDelAuth.add(ta);
				}
				readyDelMem.add(t);
			}
		}
		//下面开始增加
		for(TaskMember tmTmp:list){
			String sql = "select distinct a.roleId from T_TASK_MEMBER m left join T_TASK_AUTH a on m.id = a.memberId where m.taskId="+tmTmp.getTaskId()+" and m.userId = "+tmTmp.getUserId()+" and m.del=0 and a.del=0";
			final List<Long> roleIds = new ArrayList<Long>();
			this.jdbcTpl.query(sql, 
					new RowCallbackHandler() {
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							roleIds.add(rs.getLong("roleId"));
						}
					});
			
			TaskMember tm = new TaskMember();
			tm.setTaskId(tmTmp.getTaskId());
			tm.setUserId(tmTmp.getUserId());
			
			
			if(roleIds.contains(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.CREATOR).getId().longValue()) ){
				tm.setType(TASK_MEMBER_TYPE.CREATOR);
			}else if(roleIds.contains(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId().longValue())){
				tm.setType(TASK_MEMBER_TYPE.PARTICIPATOR);
			}else if(roleIds.contains(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MEMBER).getId().longValue())){
				tm.setType(TASK_MEMBER_TYPE.PARTICIPATOR);
			}else{
				continue;
			}
			taskMemberDao.save(tm);
			
			for(Long roleId:roleIds){
				TaskAuth ta = new TaskAuth();
				ta.setMemberId(tm.getId());
				ta.setRoleId(roleId);
				taskAuthDao.save(ta);
			}
		}
		//下面开始真正删除原先的taskMember,taskAuth
		for(TaskMember tm:readyDelMem){
			tm.setDel(DELTYPE.DELETED);
		}
		taskMemberDao.save(readyDelMem);
		for(TaskAuth ta:readyDelAuth){
			ta.setDel(DELTYPE.DELETED);
		}
		taskAuthDao.save(readyDelAuth);
	}

}
