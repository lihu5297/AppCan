package org.zywx.cooldev.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.BUG_STATUS;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROCESS_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROCESS_STATUS;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.dao.EntityResourceRelDao;
import org.zywx.cooldev.dao.UserDao;
import org.zywx.cooldev.dao.process.ProcessDao;
import org.zywx.cooldev.dao.process.ProcessMemberDao;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.process.ProcessAuth;
import org.zywx.cooldev.entity.process.ProcessMember;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.vo.Match4Project;

@Service
public class ProcessService extends AuthService {
	@Autowired
	private ProcessDao processDao;
	@Autowired
	private ProcessMemberDao processMemberDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private EntityResourceRelDao entityResourceRelDao;
	@Autowired
	private ProjectService projectService;
	
	
	@Value("${serviceFlag}")
	private String serviceFlag;
	

	public Map<String, Object> getProcessList(long loginUserId, Process match) throws ParseException {
		List<Process> plist = null;
		if(match.getProjectId() != -1) {
			plist = processDao.findByProjectIdAndDel(match.getProjectId(), DELTYPE.NORMAL);
		} else {
			plist = processDao.findByDel(DELTYPE.NORMAL);
		}

		List<Map<String, Object>> processMapList = new ArrayList<>();
		for(Process p : plist) {
			long memberTotal = processMemberDao.countByProcessIdAndDel(p.getId(), DELTYPE.NORMAL);
			p.setMemberTotal(memberTotal);
			long taskTotal = taskDao.countByProcessIdAndDel(p.getId(), DELTYPE.NORMAL);
			p.setTaskTotal(taskTotal);
			List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(p.getId(), ENTITY_TYPE.PROCESS, DELTYPE.NORMAL);
			long resourceTotal = relList.size();
			p.setResourceTotal(resourceTotal);
			//添加项目名称
			Project pro = this.projectDao.findOne(p.getProjectId());
			if(null!=pro){
				p.setProjectName(pro.getName());
			}
			Map<String, Integer> permissions = this.getPermissionMap(loginUserId, p.getId());
			
			Map<String, Object> pMap = new HashMap<>();
			pMap.put("object", p);
			pMap.put("permission", permissions);
			processMapList.add(pMap);
		}
		String required = (ENTITY_TYPE.PROCESS + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		Map<Long, List<String>> pmaps = this.projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,loginUserId,match.getProjectId());
		
		HashMap<String, Integer> maps = new HashMap<>();
		List<String> list = pmaps.get(match.getProjectId());
		if(null!=list){
			for(String str : list){
				maps.put(str, 1);
			}
		}
		
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("list", processMapList);
		retMap.put("permissions", maps);
		
		return retMap;
	}
	
	
	public PROCESS_STATUS getStatus(Process process) throws ParseException {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		cal.add(Calendar.DATE, 1);
		//当前时间+1天
		Date currentTimeAddOneDay=sdf.parse(sdf.format(cal.getTime()));
		//当前时间
		Timestamp currentTime=new Timestamp(System.currentTimeMillis());
	    Date endTime=process.getEndDate();
	    Date endTimeAddOneDay=getDate(endTime);
	    Date finishDate=process.getFinishDate();
	    List<Task> t=taskDao.findByProcessIdAndDel(process.getId(),DELTYPE.NORMAL);
	    List<Bug> b=bugDao.findByProcessIdAndDel(process.getId(),DELTYPE.NORMAL);
	    if(t.size()==0&&b.size()==0){
	    	return PROCESS_STATUS.NORMAL;
	    }else{
	    	if(currentTimeAddOneDay.before(endTime)){
				return PROCESS_STATUS.NORMAL;
			}else if(currentTime.after(endTimeAddOneDay)){
				if(finishDate==null){
					return PROCESS_STATUS.OVERDUE;
				}else if(finishDate.before(endTime)){
					return PROCESS_STATUS.NORMAL;
				}else{
					return PROCESS_STATUS.OVERDUE;
				}
			}else{
				if(finishDate==null){
					return PROCESS_STATUS.DELAY;
				}else{
					return PROCESS_STATUS.NORMAL;
				}
			}
	    }
	}

	public int getProgress(long processId){
		StringBuffer sql = new StringBuffer("select ifnull(( ");
		sql.append(" (")
		.append(" (")
		.append(" select count(1) from T_TASK where del=0 and processId= "+processId+" and status=1")
		.append(" ) ")
		.append(" + ")
		.append(" (select count(1) from T_BUG where processId = "+processId+" and del=0 and status=2 ) ")
		.append(")")
		.append(" / ")
		
		.append("(")
		.append("(")
		.append("select count(1) from T_TASK where del=0 and processId= "+processId)
		.append(") ")
		.append(" + ")
		.append(" (select count(1) from T_BUG where processId = "+processId+" and del=0  ) ")
		.append(")")
		.append("),0) perc");
		log.info("calculate processProgress sql -->"+sql.toString());
		final List<Float> oneElementList = new ArrayList<Float>();
		this.jdbcTpl.query(sql.toString(),
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						float tmp = rs.getFloat("perc");
						tmp=Math.round(tmp*100);
						if(tmp<1 && tmp >0){
							String toStringStr = new BigDecimal(tmp).toPlainString();
							if(toStringStr.length()>1){
								tmp = Float.parseFloat(toStringStr.substring(0, 2));
							}
						}
						oneElementList.add(tmp);
					}
				});
		BigDecimal result = new BigDecimal(oneElementList.get(0));
		return result.setScale(0, RoundingMode.FLOOR).intValue();
	}
	
	public Process getProcess(long loginUserId, long processId) throws ParseException {

		Process p = processDao.findOne(processId);
		List<ProcessMember> allMembers = processMemberDao.findByProcessIdAndDel(p.getId(), DELTYPE.NORMAL);
		long taskTotal = taskDao.countByProcessIdAndDel(p.getId(), DELTYPE.NORMAL);
		
		p.setMemberTotal(allMembers.size());
		p.setTaskTotal(taskTotal);
		
		List<ProcessMember> otherMembers = new ArrayList<>();	// leader 之外的其他成员
		for(ProcessMember member : allMembers) {
			User user = userDao.findOne(member.getUserId());
			member.setUserName(user.getUserName());
			member.setUserIcon(user.getIcon());
			
			List<ProcessAuth> authes = processAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
			Set<String> roleSet = new HashSet<>();
			for(ProcessAuth auth : authes) {
				Role role = Cache.getRole(auth.getRoleId());
				if(role != null) {
					roleSet.add( role.getEnName() );
				}
			}
			member.setRole(new ArrayList<String>(roleSet));
			if(roleSet.contains(ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MANAGER)) {
				p.setLeader(member);
			}
			if(roleSet.contains(ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MEMBER)){
				if(member.getType().equals(PROCESS_MEMBER_TYPE.CREATOR)){
					otherMembers.add(0, member);
				}else{
					otherMembers.add(member);
				}
			}
		}
		p.setMember(otherMembers);
		
		//资源
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(p.getId(), ENTITY_TYPE.PROCESS, DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for(EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		if(resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			p.setResource(resources);
			p.setResourceTotal(resources.size());
		}
		
		Map<String, Integer> permissions = this.getPermissionMap( loginUserId,p.getId());
		p.setPermissions(permissions);
		
		return p;
	}
	
	public Process findOne(long processId){
		Process p = processDao.findOne(processId);
		return p;
	}
	/**
	 * 添加流程
	 * 
	 * @param process
	 * @param loginUserId
	 * @param leader
	 * @param member
	 * @param resource
	 * @return
	 */
	public Process createProcess(Process process, long loginUserId, long leader, List<Long> member, List<Long> resource) {
		//如果是企业版,则同一个项目下不允许有相同名字的流程
		//大众版流程名称也不能相同
		//if("enterprise".equals(serviceFlag)){
			long projectId = process.getProjectId();
			List<Process> listProcess = processDao.findByProjectIdAndNameAndDel(projectId,process.getName(), DELTYPE.NORMAL);
			if(null!=listProcess && listProcess.size()>0){
				throw new RuntimeException("流程阶段已存在");
			}
		//}
		//增加拼音
		process.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(process.getName()==null?"":process.getName()));
		process.setPinYinName(ChineseToEnglish.getPingYin(process.getName()==null?"":process.getName()));
		// 1. create new process
		processDao.save(process);
		// 2. add creator, leader, memeber
		Set<Long> memberSet = new HashSet<>(member);
		memberSet.add(loginUserId);
		memberSet.add(leader);

		// 添加流程成员
		List<ProcessMember> processMembers = new ArrayList<>();
		for(long userId : memberSet) {
			ProcessMember pm = new ProcessMember();
			pm.setProcessId(process.getId());
			pm.setUserId(userId);
			if(userId == loginUserId) {
				pm.setType(PROCESS_MEMBER_TYPE.CREATOR);
			} else {
				pm.setType(PROCESS_MEMBER_TYPE.PARTICIPATOR);
			}
			processMembers.add(pm);
		}
		if(processMembers.size() >0) {
			processMemberDao.save(processMembers);
		}
		
		// 成员授权
		List<ProcessAuth> processAuthes = new ArrayList<>();
		for(ProcessMember pm : processMembers) {
			if(pm.getUserId() == loginUserId) {
				String roleEnName = ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.CREATOR;
				Role role = Cache.getRole(roleEnName);
				if(role != null) {
					ProcessAuth auth = new ProcessAuth();
					auth.setMemberId(pm.getId());
					auth.setRoleId(role.getId());
					processAuthes.add(auth);
				}

			}
			
			if(pm.getUserId() == leader) {
				String roleEnName = ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MANAGER;
				Role role = Cache.getRole(roleEnName);
				if(role != null) {
					ProcessAuth auth = new ProcessAuth();
					auth.setMemberId(pm.getId());
					auth.setRoleId(role.getId());
					processAuthes.add(auth);
				}
			}
			
			if(member.contains(pm.getUserId())){//只有选了是成员,才会给成员权限
				String roleEnName = ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MEMBER;
				Role role = Cache.getRole(roleEnName);
				if(role != null) {
					ProcessAuth auth = new ProcessAuth();
					auth.setMemberId(pm.getId());
					auth.setRoleId(role.getId());
					processAuthes.add(auth);
				}
			}
		}
		if(processAuthes.size() > 0) {
			processAuthDao.save(processAuthes);
		}
		
		// 关联资源
		if(resource != null) {
			for(long resourceId : resource) {
				EntityResourceRel rel = new EntityResourceRel();
				rel.setResourceId(resourceId);
				rel.setEntityId(process.getId());
				rel.setEntityType(ENTITY_TYPE.PROCESS);
				this.entityResourceRelDao.save(rel);
			}
		}
		projectService.updateProjProgressAndStatus(process.getProjectId());
		return process;
	}
	
	/**
	 * 变更流程基本信息
	 * @param process
	 * @return
	 */
	public int editProcess(Process process) {

		SimpleDateFormat formator = new SimpleDateFormat("yyyy-MM-dd");
		String settings = "";
		if(process.getName() != null) {
			settings += String.format(",name='%s',pinYinHeadChar='%s',pinYinName='%s'", process.getName(),ChineseToEnglish.getPinYinHeadChar(process.getName()==null?"":process.getName()),ChineseToEnglish.getPingYin(process.getName()==null?"":process.getName()));
		}
		if(process.getDetail() != null) {
			settings += String.format(",detail='%s'", process.getDetail());
		}
		if(process.getWeight() != -1) {
			settings += String.format(",weight=%d", process.getWeight());
		}
		if(process.getStartDate() != null) {
			settings += String.format(",startDate='%s'", formator.format(process.getStartDate()));
		}
		if(process.getEndDate() != null) {
			settings += String.format(",endDate='%s'", formator.format(process.getEndDate()));
		}

		if(settings.length() > 0) {
			settings = settings.substring(1);
			String sql = "update T_PROCESS set " + settings + " where id=" + process.getId();
			int t = jdbcTpl.update(sql);
			Process pro = processDao.findOne(process.getId());
			projectService.updateProjProgressAndStatus(pro.getProjectId());
			return t;
		} else {
			return 0;
		}
	}
	
	public void removeProcess(long processId) {
		Process process = this.processDao.findOne(processId);
		process.setDel(DELTYPE.DELETED);
		processDao.save(process);
		
		List<Task> listTask = taskDao.findByProcessIdAndDel(processId, DELTYPE.NORMAL);
		if(null!=listTask && listTask.size()>0 ){
			for(Task task:listTask){
				task.setDel(DELTYPE.DELETED);
				taskDao.save(task);
			}
		}
		projectService.updateProjProgressAndStatus(process.getProjectId());
	}
	
	public List<ProcessMember> getProcessMemberList(long processId, String queryName, String queryAccount) {
		
		List<ProcessMember> list = processMemberDao.findByProcessIdAndDel(processId, DELTYPE.NORMAL);
		
		
		List<ProcessMember> retList = new ArrayList<>();
		
		for(ProcessMember m : list) {
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
	
	public ProcessMember saveProcessMember(ProcessMember p) {
		p.setType(PROCESS_MEMBER_TYPE.PARTICIPATOR);
		processMemberDao.save(p);
		
		ProcessAuth auth = new ProcessAuth();
		auth.setMemberId(p.getId());
		String roleEnName = ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MEMBER;
		auth.setRoleId(Cache.getRole(roleEnName).getId());
		this.processAuthDao.save(auth);
		
		return p;
	}
	
	public void removeProcessMember(long memberId) {
		processMemberDao.delete(memberId);
	}
	
	/**
	 * 变更负责人
	 * @param leader
	 * @return
	 */
	public int updateLeader(ProcessMember leader) {
		Role managerRole = Cache.getRole(ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MANAGER);
		Role memberRole  = Cache.getRole(ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MEMBER);
		if(managerRole == null || memberRole == null) {
			return 0;
		}

		// 删除目前的负责人
		boolean isFound = false;
		ProcessMember deleteMember = null;//原有的流程负责人
		ProcessMember participatorMember = null;//原有流程负责人是否也是参与人
		ProcessAuth  deleteAuth = null;//原有流程负责人对一个的权限
		List<ProcessMember> list = processMemberDao.findByProcessIdAndDel(leader.getProcessId(), DELTYPE.NORMAL);
		if(list != null && list.size() > 0) {
			for(ProcessMember member : list) {
				List<ProcessAuth> authes = processAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
				if(authes != null && authes.size() > 0) {
					for(ProcessAuth auth : authes) {
						if(auth.getRoleId() == managerRole.getId().longValue()) {
							deleteAuth = auth;
							deleteMember = member;
							isFound = true;
							break;
						}
					}
					for(ProcessAuth auth : authes) {
						if(auth.getRoleId()==memberRole.getId().longValue() && null!=deleteMember && member.getUserId()==deleteMember.getUserId()){
							participatorMember = member;
							break;
						}
					}
				}
				if(isFound) break;
			}
			
			
		}
		if(!isFound) {
			log.info("processManager is not found!");
			return 0;
		}
		//删除原有流程负责人的权限
		if(null!=deleteAuth){
			this.processAuthDao.delete(deleteAuth);
		}
		//负责人不是参与人 则删除member 否则不能删除member
		if(participatorMember==null){
			this.processMemberDao.delete(deleteMember);
		}
		// 变更或保存新的负责人
		List<ProcessMember> target = processMemberDao.findByProcessIdAndUserIdAndDel(leader.getProcessId(), leader.getUserId(), DELTYPE.NORMAL);
		if(target != null && target.size() > 0) {
			// 成员内部变更负责人
			ProcessMember member = target.get(0);
			ProcessAuth auth = new ProcessAuth();
			auth.setMemberId(member.getId());
			auth.setRoleId(managerRole.getId());
			processAuthDao.save(auth);
		} else {
			// 非成员，变更负责人
			leader.setType(PROCESS_MEMBER_TYPE.PARTICIPATOR);
			processMemberDao.save(leader);
			ProcessAuth auth = new ProcessAuth();
			auth.setMemberId(leader.getId());
			auth.setRoleId(managerRole.getId());
			processAuthDao.save(auth);
		}
		return 1;
		
	}

	/**
	 * 获取流程许可集合<br>
	 * 
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * @param permissionEnName
	 * @return
	 */
	public Map<Long, List<String>> permissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();
		long startTime = System.currentTimeMillis();
		
		StringBuffer sql= new StringBuffer("select t.processId,group_concat(t.roleId) from (");
		sql.append(" select distinct member.processId,auth.roleId from T_PROCESS_MEMBER member left join T_PROCESS_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(")")
		.append(") t group by t.processId");
		log.info("permissionMapAsMemberWith sql====>"+sql);
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
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
		log.info("processService permissionMapAsMemberWith total time--> "+(startTime-endTime)+"  ms");
		return permissionsMapAsMember;
	}
	
	/**
	 * 获取流程Id集合<br>
	 * @author haijun.cheng
	 * @date 2016-09-18
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * @param permissionEnName
	 * @return
	 */
	public Set<Long> getPermissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		long startTime = System.currentTimeMillis();
		final Set<Long> processIdSet = new HashSet<>();
		StringBuffer sql= new StringBuffer("select distinct member.processId from T_PROCESS_MEMBER member left join T_PROCESS_AUTH  auth on member.id = auth.memberId ");
		sql.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(")");
		log.info("permissionMapAsMemberWith sql====>"+sql);
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						processIdSet.add(rs.getLong(1));
					}
				});
		long endTime = System.currentTimeMillis();
		log.info("processService permissionMapAsMemberWith total time--> "+(startTime-endTime)+"  ms");
		return processIdSet;
	}
	/**
	 * 获取流程许可集合<br>
	 * 
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * @param permissionEnName
	 * @return
	 */
	public Map<Long, List<String>> permissionMapAsMemberWithAndOnlyByProcessId(String permissionEnName, long loginUserId,long processId) {
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();
		long startTime = System.currentTimeMillis();
		
		StringBuffer sql= new StringBuffer("select t.processId,group_concat(t.roleId) from (");
		sql.append(" select distinct member.processId,auth.roleId from T_PROCESS_MEMBER member left join T_PROCESS_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.del=0 and auth.del=0 and member.processId=").append(processId).append(" and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(")")
		.append(") t group by t.processId");
		log.info("processPermission====>"+sql.toString());
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
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
		log.info("processService permissionMapAsMemberWith total time--> "+(startTime-endTime)+"  ms");
		return permissionsMapAsMember;
	}
	@Override
	public List<Permission> getPermissionList(long loginUserId, long processId) {
		HashMap<Long, Permission> permissionMap = new HashMap<>();
		
		List<ProcessMember> memberList = this.processMemberDao.findByProcessIdAndUserIdAndDel(processId, loginUserId, DELTYPE.NORMAL);
		if(memberList != null && memberList.size() > 0) {
			for(ProcessMember member : memberList) {
				List<ProcessAuth> authList = this.processAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
				if(authList != null && authList.size() > 0) {
					for(ProcessAuth auth : authList) {
						long roleId = auth.getRoleId();
						Role role = Cache.getRole(roleId);
						if(role != null) {
							List<Permission> permissions = role.getPermissions();
							if(permissions != null && permissions.size() > 0) {
								for(Permission p : permissions) {
									permissionMap.put(p.getId(), p);
								}															
							}
						}
					}
				}
			}
		}
		
		return new ArrayList<>(permissionMap.values());
	}
	
	
	public Map<String, Integer> getPermissionMap(long loginUserId, long processId) {
		
		// 根据用户权限构建查询条件
		String required = (ENTITY_TYPE.PROCESS + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		Process process = this.processDao.findOne(processId);
		// 项目成员权限
		Map<Long, List<String>> pMapAsProjectMember = this.projectService.permissionMapAsMemberWithAndOnlyByProjectId(required, loginUserId,process.getProjectId());
						
		// 流程成员权限
		Map<Long, List<String>> pMapAsProcessMember = this.permissionMapAsMemberWithAndOnlyByProcessId(required, loginUserId,processId);
		// 获取前端许可
				List<String> pListAsProjectMember = pMapAsProjectMember.get(process.getProjectId());
				List<String> pListAsProcessMember = pMapAsProcessMember.get(processId);
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

		return pMap;
	}
	
	public List<Process> getProcessListByProjectId(long projectId){
		return  processDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
	}
	
	public List<Process> getProcessListByProjectIdAndName(long projectId,String processName){
		return  processDao.findByProjectIdAndNameLikeOrPinYinHeadCharLikeOrPinYinNameAndDel(projectId, "%"+processName+"%",processName+"%",processName+"%", DELTYPE.NORMAL);
	}


	/**
	 * @user jingjian.wu
	 * 查询任务的时候   获取我能看到的流程的名字,需要根据流程名字模糊匹配
	 * @date 2016年3月7日 下午3:14:40
	 */
	    
	public Map<String, Object> getProcessList(long loginUserId, String name,Long projectId) {
		List<Long> projectIds = new ArrayList<Long>();
		Match4Project matchObj = new Match4Project();
		Map<String,Object> map = projectService.getProjectList(1, 10000, matchObj, loginUserId, null,null, null, null, null, null, null, null,null);
		List<Map<String, Object>> projectMapList = (List<Map<String, Object>>) map.get("list");
		for(Map<String,Object> ma:projectMapList){
			Project pp = (Project) ma.get("object");
			projectIds.add(pp.getId());
		}
		
		if(null!=projectId && projectIds.contains(projectId)){
			projectIds.clear();
			projectIds.add(projectId);
		}else if(null!=projectId && !projectIds.contains(projectId)){
			log.info("user with ID:"+loginUserId+",has no permission for projectId :"+projectId);
			return this.getSuccessMap(new ArrayList<String>());
		}
		
		List<Process> listProcess = processDao.findByProjectIdInAndDelAndNameLike(projectIds, DELTYPE.NORMAL,"%"+name+"%");
		return this.getSuccessMap(listProcess);
	}
	/**
	 * 更新流程完成时间,进度,状态
	 */
	public void changeProcessFinishDateAndStatusAndProgress(long processId) throws ParseException{
		StringBuffer processSql=new StringBuffer();
		processSql.append("select sum(count) from (")
		.append("select count(1) as count from T_TASK where del=0 and processId=").append(processId).append(" and status=").append(TASK_STATUS.NOFINISHED.ordinal()).append(" union ")
		.append("select count(1) as count from T_BUG where del=0 and processId=").append(processId).append(" and status!=").append(BUG_STATUS.CLOSED.ordinal()).append(") as xxx");
		@SuppressWarnings("deprecation")
		int count=this.jdbcTpl.queryForInt(processSql.toString());
		if(count==0){
			Process processO=this.processDao.findOne(processId);
			if(processO.getFinishDate()==null){
				processO.setFinishDate(new Timestamp(System.currentTimeMillis()));
			}
			processO.setProgress(this.getProgress(processId));
			processO.setStatus(this.getStatus(processO));
			processDao.save(processO);
		}else{
			Process processObj=this.processDao.findOne(processId);
			processObj.setFinishDate(null);
			processObj.setProgress(this.getProgress(processId));
			processObj.setStatus(this.getStatus(processObj));
			processDao.save(processObj);
		}
		
	}
	public static Date getDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date date1 = new Date(calendar.getTimeInMillis());
		return date1;
	}

	/**
	 * 定时任务,每天更新流程的进度和状态,
	 * 同时在添加,关闭任务,bug的时候,也会调用
	 * @throws ParseException
	 */
	public void updateProcessStatusAndProgress() throws ParseException{
		List<Process> listProcess = processDao.findByDel(DELTYPE.NORMAL);
		for(Process p:listProcess){
			changeProcessFinishDateAndStatusAndProgress(p.getId());
		}
	}

	public Map<String, Object> addPinyin() {
		List<Process> processs = this.processDao.findByDel(DELTYPE.NORMAL);
		for(Process process : processs){
			process.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(process.getName()==null?"":process.getName()));
			process.setPinYinName(ChineseToEnglish.getPingYin(process.getName()==null?"":process.getName()));
		}
		this.processDao.save(processs);
		return this.getSuccessMap("affected "+processs.size());
	}
}
