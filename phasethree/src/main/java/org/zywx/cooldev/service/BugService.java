package org.zywx.cooldev.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.BUG_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.BUG_STATUS;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.dao.bug.BugAuthDao;
import org.zywx.cooldev.dao.bug.BugDao;
import org.zywx.cooldev.dao.bug.BugMarkDao;
import org.zywx.cooldev.dao.bug.BugMemberDao;
import org.zywx.cooldev.dao.bug.BugModuleDao;
import org.zywx.cooldev.dao.bug.BugStatusSortDao;
import org.zywx.cooldev.dao.project.ProjectMemberDao;
import org.zywx.cooldev.entity.Dynamic;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.bug.BugAuth;
import org.zywx.cooldev.entity.bug.BugMark;
import org.zywx.cooldev.entity.bug.BugMember;
import org.zywx.cooldev.entity.bug.BugModule;
import org.zywx.cooldev.entity.bug.BugStatusSort;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.Tools;


@Service
public class BugService extends BaseService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private BugDao bugDao;
	@Autowired
	private BugMemberDao bugMemberDao;
	@Autowired
	private BugAuthDao bugAuthDao;
	@Autowired
	private BugMarkDao bugMarkDao;
	@Autowired
	private ProjectMemberDao projectMemberDao;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProcessService processService;
	@Autowired
	private BugModuleDao bugModuleDao;
	@Autowired
	private BugStatusSortDao bugStatusSortDao;
	@Autowired
	private BugMemberService bugMemberService;
	/**
	 * 新建bug
	 * 
	 * @param bug
	 * @param loginUserId
	 * @return
	 * @throws ParseException 
	 */
	public Bug addBug(Bug bug, long loginUserId) throws ParseException {
		// 1.add bug,set projectStatus
		if(bug.getModuleId()!=0&&bug.getModuleId()!=-1){
		  BugModule bmO= this.bugModuleDao.findByIdAndDel(bug.getModuleId(),DELTYPE.NORMAL);
		  if(bmO==null){
			  throw new RuntimeException("bug模块不存在或已删除");
		  }
		}
		bugDao.save(bug);
		//更新项目状态、完成时间
		Project project = this.projectDao.findOne(bug.getProjectId());
		project.setStatus(PROJECT_STATUS.ONGOING);
		project.setFinishDate(null);
		projectDao.save(project);
		
		// 2. add creator, assigned, member
		Set<Long> memberSet = new HashSet<>();//所有成员的id集合
		memberSet.add(loginUserId);
		memberSet.addAll(bug.getMemberUserIdList());
		memberSet.add(bug.getAssignedUserId());

		// 添加bug成员
		List<BugMember> bugMembers = new ArrayList<>();//创建一个BugMember的list，一起保存list
		for (long userId : memberSet) {
			BugMember bugmember = new BugMember();
			bugmember.setBugId(bug.getId());
			bugmember.setUserId(userId);
			if (userId == loginUserId) {
				bugmember.setType(BUG_MEMBER_TYPE.CREATOR);
			} else {
				bugmember.setType(BUG_MEMBER_TYPE.PARTICIPATOR);
			}
			bugMembers.add(bugmember);
		}
		if (bugMembers.size() > 0) {
			bugMemberDao.save(bugMembers);
		}

		// 给bug成员授权
		List<BugAuth> bugAuthes = new ArrayList<>();
		for (BugMember bugMember : bugMembers) {
			if (bugMember.getUserId() == loginUserId) {
				String roleEnName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.CREATOR;
				Role role = Cache.getRole(roleEnName);
				if (role != null) {
					BugAuth auth = new BugAuth();
					auth.setMemberId(bugMember.getId());
					auth.setRoleId(role.getId());
					bugAuthes.add(auth);
				}

			}

			if (bugMember.getUserId() == bug.getAssignedUserId()) {
				String roleEnName = ENTITY_TYPE.BUG + "_"
						+ ROLE_TYPE.ASSIGNEDPERSON;
				Role role = Cache.getRole(roleEnName);
				if (role != null) {
					BugAuth auth = new BugAuth();
					auth.setMemberId(bugMember.getId());
					auth.setRoleId(role.getId());
					bugAuthes.add(auth);
				}
			}

			if (bug.getMemberUserIdList().contains(bugMember.getUserId())) {// 只有选了是成员,才会给成员权限
				String roleEnName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER;
				Role role = Cache.getRole(roleEnName);
				if (role != null) {
					BugAuth auth = new BugAuth();
					auth.setMemberId(bugMember.getId());
					auth.setRoleId(role.getId());
					bugAuthes.add(auth);
				}
			}

		}
		if (bugAuthes.size() > 0) {
			bugAuthDao.save(bugAuthes);
		}
		// 添加资源关联
		if (bug.getResourceIdList() != null) {
			for (long resourceId : bug.getResourceIdList()) {
				EntityResourceRel rel = new EntityResourceRel();
				rel.setResourceId(resourceId);
				rel.setEntityId(bug.getId());
				rel.setEntityType(ENTITY_TYPE.BUG);
				entityResourceRelDao.save(rel);
			}
		}
		//更新流程完成时间
		processService.changeProcessFinishDateAndStatusAndProgress(bug.getProcessId());
		//更新项目完成时间
		this.projectService.updateProjProgressAndStatus(bug.getProjectId());
		return bug;
	}

	/**
	 * 查看bug详情
	 * 
	 * @param bugId
	 * @param loginUserId
	 * @param pageable 
	 * @return
	 */
	//@Cacheable(value="BugService_getBugDetail",key="#bugId+'_'+#loginUserId+'_'+#pageable.pageNumber+'_'+#pageable.pageSize")
	public Map<String, Object> getBugDetail(long bugId, long loginUserId, Pageable pageable) {
		Map<String, Object> message = new HashMap<>();
		// 根据用户权限构建查询条件
		final Bug bug = bugDao.findByIdAndDel(bugId, DELTYPE.NORMAL);
		if (null == bug || bug.getDel() == DELTYPE.DELETED) {
			message.put("status", "failed");
			message.put("message", "bug已经删除");
			return message;
		}
		Process process = processDao.findOne(bug.getProcessId());
		String required = (ENTITY_TYPE.BUG + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();

		// 项目成员权限
		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required, loginUserId,process.getProjectId());
		// 流程成员权限
		Map<Long, List<String>> pMapAsProcessMember = processService.permissionMapAsMemberWithAndOnlyByProcessId(required, loginUserId,process.getId());
		// bug成员权限
		Map<Long, List<String>> pMapAsBugMember = this.permissionMapAsMemberWithAndOnlyByBugId(required, loginUserId,bugId);
		// 查看bug详情权限
		//		List<ProjectMember> projectMembers = projectMemberDao
		//				.findByProjectIdAndDel(process.getProjectId(), DELTYPE.NORMAL);
		//		long flag = 0;
		//		for (ProjectMember pm : projectMembers) {
		//			if (pm.getUserId() == loginUserId) {
		//				flag = 1;
		//			}
		//		}
		//		if (flag == 0) {
		//			message.put("status", "failed");
		//			message.put("message", "您不是该项目下的成员，您没有查看该bug的权限");
		//			return message;
		//		}
		// 获取appName
		if (-1 != bug.getAppId()) {
			App app = appDao.findOne(bug.getAppId());
			bug.setAppName(null == app || null == app.getName() ? null : app
					.getName());
		}
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取项目名称、流程名称
		if (process != null) {
			bug.setProcessName(process.getName());
			Project project = projectDao.findOne(process.getProjectId());
			if (project != null) {
				bug.setProjectName(project.getName());
				bug.setProjectId(project.getId());
			}
		}
		//----------------替换原先的查询bugMember列表------------------------
		final List<BugMember> otherMembers = new ArrayList<>(); // leader 之外的其他成员
		StringBuffer sb = new StringBuffer();
		sb.append("select t.id,t.userId,GROUP_CONCAT(t.roleId) roleIds,t.userName,t.icon,t.moduleName from ( ")
		.append(" select m.id,m.type,m.userId,auth.roleId,u.userName,u.icon ,bm.name,ifnull(bm.name,'无') moduleName from T_BUG_MEMBER m left join T_BUG_AUTH auth on m.id =auth.memberId ")
		.append(" left join T_USER u on u.id =m.userId ")
		.append(" left join T_BUG bg on bg.id = m.bugId ")
		.append(" left join T_BUG_MODULE bm on bm.id=bg.moduleId ")
		.append("  where m.bugId="+bug.getId()+" and m.del=0  and auth.del=0 ")
		.append(" ) t group by t.id ");
		jdbcTemplate.query(sb.toString(), new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				BugMember bm = new BugMember();
				bm.setId(rs.getLong("id"));
				bm.setUserName(rs.getString("userName"));
				bm.setUserIcon(rs.getString("icon"));
				bm.setUserId(rs.getLong("userId"));
				Set<String> roleSet = new HashSet<>();
				String roleIds = rs.getString("roleIds");
				if(roleIds.indexOf(",")>0){
					String[] roleId = roleIds.split(",");
					for(String rid:roleId){
						roleSet.add(Cache.getRole(Long.parseLong(rid)).getEnName());
					}
				}else{
					roleSet.add(Cache.getRole(Long.parseLong(roleIds)).getEnName());
				}
				bm.setRole(new ArrayList<String>(roleSet));
				if (roleSet.contains(ENTITY_TYPE.BUG + "_"
						+ ROLE_TYPE.ASSIGNEDPERSON)) {

					bug.setAssignedPerson(bm);
				}
				if (roleSet.contains(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER)) {
					otherMembers.add(bm);
				}
				if (roleSet.contains(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.CREATOR)) {
					User creator = userDao.findOne(bm.getUserId());
					bug.setCreator(creator);
				}
				bug.setModuleName(rs.getString("moduleName"));
			}
		});
		bug.setMember(otherMembers);
		//----------------替换原先的查询bugMember列表------------------------
		// 获取bug创建者,指派者，参与人
		/*List<BugMember> allMembers = bugMemberDao.findByBugIdAndDel(
				bug.getId(), DELTYPE.NORMAL);
		List<BugMember> otherMembers = new ArrayList<>(); // leader 之外的其他成员
		for (BugMember member : allMembers) {
			User user = userDao.findOne(member.getUserId());
			member.setUserName(user.getUserName());
			member.setUserIcon(user.getIcon());

			List<BugAuth> authes = bugAuthDao.findByMemberIdAndDel(
					member.getId(), DELTYPE.NORMAL);
			Set<String> roleSet = new HashSet<>();
			for (BugAuth auth : authes) {
				Role role = Cache.getRole(auth.getRoleId());
				if (role != null) {
					roleSet.add(role.getEnName());
				}
			}
			member.setRole(new ArrayList<String>(roleSet));
			if (roleSet.contains(ENTITY_TYPE.BUG + "_"
					+ ROLE_TYPE.ASSIGNEDPERSON)) {

				bug.setAssignedPerson(member);
			}
			if (roleSet.contains(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER)) {
				otherMembers.add(member);
			}
			if (roleSet.contains(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.CREATOR)) {
				User creator = userDao.findOne(member.getUserId());
				bug.setCreator(creator);
			}
		}
		bug.setMember(otherMembers);
        //添加moduleName
		BugModule  bugModule=this.bugModuleDao.findOne(bug.getModuleId());
		if(bugModule!=null){
			bug.setModuleName(bugModule.getName());
		}else{
			bug.setModuleName("无");
		}*/
		// 添加任务资源列表
		List<EntityResourceRel> relList = entityResourceRelDao
				.findByEntityIdAndEntityTypeAndDel(bugId, ENTITY_TYPE.BUG,
						DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for (EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		if (resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			bug.setResource(resources);
		}

		// 获取bug备注
		List<BugMark> marks = bugMarkDao.findByBugIdAndDel(bugId,
				DELTYPE.NORMAL);
		for (BugMark mark : marks) {
			User user = userDao.findOne(mark.getUserId());
			if (user != null) {
				mark.setUserIcon(user.getIcon());
				mark.setUserName(user.getUserName());
			}
		}
		bug.setMarks(marks);
		//动态
		Page<Dynamic> dynamics=dynamicDependencyDao.findDynamic("Bug",bugId,DELTYPE.NORMAL,pageable);
	    for(Dynamic dynamic :dynamics){
	    	User user=userDao.findOne(dynamic.getUserId());
	    	if(user!=null){
	    		dynamic.setUserIcon(user.getIcon());
	    	}
	    }
	 // 获取前端许可
 		List<String> pListAsProjectMember = pMapAsProjectMember.get(bug.getProjectId());
 		List<String> pListAsProcessMember = pMapAsProcessMember.get(bug.getProcessId());
 		List<String> pListAsBugMember = pMapAsBugMember.get(bug.getId());
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
 		if(pListAsBugMember != null) {
 			for(String p : pListAsBugMember) {
 				pMap.put(p, 1);
 			}
 		}
 		if(pMap.size() == 0) {
 			throw new RuntimeException("您没有查看该bug的权限");
 		}
		bug.setDynamic(dynamics);
		message.put("object", bug);
		message.put("permission",pMap);
		return message;
	}

	/**
	 * 编辑bug
	 * 
	 * @param bug
	 * @param loginUserId
	 * @return
	 * @throws ParseException 
	 */
	public int editBug(Bug bug,Bug bugOld,long loginUserId) throws ParseException {
		if(bug.getModuleId()!=0&&bug.getModuleId()!=-1){
			  BugModule bmO= this.bugModuleDao.findByIdAndDel(bug.getModuleId(),DELTYPE.NORMAL);
			  if(bmO==null){
				  throw new RuntimeException("bug模块不存在或已删除");
			  }
		}
		String settings = "";
		settings += String.format(",lastModifyUserId='%d'", loginUserId);
		if (bug.getTitle() != null) {
			settings += String.format(",title='%s'", bug.getTitle().replace("\\", "\\\\\\\\").replace("'", "\\'"));
		}
		if (bug.getDetail() != null) {
			settings += String.format(",detail='%s'", bug.getDetail().replace("\\", "\\\\\\\\").replace("'", "\\'"));
		}
		if (bug.getPriority() != null) {
			settings += String.format(",priority=%d", bug.getPriority()
					.ordinal());
		}
		if (bug.getAppId() != -1) {
			settings += String.format(",appId=%d", bug.getAppId());
		}
		if (bug.getProcessId() != -1) {
			settings += String.format(",processId=%d", bug.getProcessId());
		}
		if(bug.getAssignedUserId()!=0){
			this.bugMemberService.editBugAssignedPerson(bug.getId(),bug.getAssignedUserId());
		}
		if (bug.getStatus() != null) {
			settings += String.format(",status=%d", bug.getStatus().ordinal());
			if (bug.getStatus().equals(BUG_STATUS.FIXED)) {
				bug.setResolveAt(new Timestamp(System.currentTimeMillis()));
				settings += String.format(",resolveUserId=%d", loginUserId);
				settings += ",resolveAt='" + bug.getResolveAt() + "'";
				System.out.print(bug.getId()+"================");
				BugMember creatorObj = getBugCreator(bug.getId());
				bugMemberService.editBugAssignedPerson(bug.getId(),creatorObj.getUserId());
				
			}
			if (bug.getStatus().equals(BUG_STATUS.CLOSED)) {
				bug.setCloseAt(new Timestamp(System.currentTimeMillis()));
				settings += String.format(",closeUserId=%d", loginUserId);
				settings += ",closeAt='" + bug.getCloseAt() + "'";
			}
			//激活bug
			if(bug.getStatus().equals(BUG_STATUS.NOTFIX)){
				settings += ",solution=null,resolveVersion=null,resolveUserId=0";
				bugMemberService.editBugAssignedPerson(bug.getId(),bugOld.getResolveUserId());
			}
		}
		if (bug.getModuleId() != -1) {
			settings += String.format(",moduleId=%d", bug.getModuleId());
		}
		if (bug.getAffectVersion() != null) {
			settings += String.format(",affectVersion='%s'",
					bug.getAffectVersion());
		}
		if (bug.getResolveVersion() != null) {
			settings += String.format(",resolveVersion='%s'",
					bug.getResolveVersion());
		}
		if (bug.getSolution() != null &&  !BUG_STATUS.NOTFIX.equals(bug.getStatus())) {
			settings += String.format(",solution=%d", bug.getSolution()
					.ordinal());
		}
		if (settings.length() > 0) {
			settings = settings.substring(1);
			String sql = String.format("update T_BUG set %s where id=%d",
					settings, bug.getId());
			log.info("execute Sql:" + sql);
			int a = this.jdbcTemplate.update(sql);
			// 设置项目状态
			Bug newBug = this.bugDao.findOne(bug.getId());
			Process process = this.processDao.findOne(newBug.getProcessId());
			//更新流程完成时间
			processService.changeProcessFinishDateAndStatusAndProgress(newBug.getProcessId());
			//更新项目完成时间
			this.projectService.updateProjProgressAndStatus(process.getProjectId());
			
			return a;
		} else {
			return 0;
		}
	}

	/**
	 * 获取成员列表
	 * 
	 * @param bugId
	 * @param queryName
	 * @param queryAccount
	 * @return
	 */
	public List<BugMember> getBugMemberList(long bugId, String queryName,
			String queryAccount) {

		List<BugMember> memberList = bugMemberDao.findByBugIdAndDel(bugId,
				DELTYPE.NORMAL);

		List<BugMember> retList = new ArrayList<>();

		for (BugMember m : memberList) {
			User u = userDao.findOne(m.getUserId());
			if (u != null) {
				if (queryName != null) {
					if (!queryName.equals(u.getUserName()))
						continue;
				}
				if (queryAccount != null) {
					if (!queryAccount.equals(u.getAccount()))
						continue;
				}

				String name = u.getUserName() != null ? u.getUserName() : u
						.getAccount();

				m.setUserName(name);
				m.setUserIcon(u.getIcon() == null ? "" : u.getIcon());
				m.setUserAccount(u.getAccount());
				retList.add(m);
			}
		}

		return retList;
	}

	public BugMember getBugAssignPerson(long bugId) {
		String roleName = ENTITY_TYPE.BUG + "_" + ROLE_TYPE.ASSIGNEDPERSON;
		Role role = Cache.getRole(roleName);
		Long roleId = role.getId();
		BugMember member = this.bugMemberDao.findByBugIdAndRoleIdAndDel(bugId,
				roleId, DELTYPE.NORMAL);
		return member;
	}
    public BugMember getBugCreator(long bugId){
    	BugMember member=this.bugMemberDao.findByBugIdAndTypeAndDel(bugId,BUG_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
        return member;
    }
	// 添加bug备注
	public BugMark addMark(BugMark bugMark, long loginUserId) {
		bugMark.setUserId(loginUserId);
		bugMarkDao.save(bugMark);
		return bugMark;
	}
	//@Cacheable(value="BugService_getBugList",key="#loginUserId+'_'+#pageNo+'_'+#pageSize+'_'"
	//+"+#bug.resource")
	public List<Object> getBugList(Bug bug, int pageNo, int pageSize,
			long loginUserId) {
		if(bug.getSortNum()!=-1){
		List<BugStatusSort> bugStatusSort = this.bugStatusSortDao.findByUserIdAndProjectIdAndSortAndDel(loginUserId,bug.getProjectId(),bug.getSortNum(), DELTYPE.NORMAL);
		if(null!=bugStatusSort && bugStatusSort.size()>0){
			bug.setStatus(bugStatusSort.get(0).getStatus());
		}else{
			//创建默认排序
			JSONObject obj = new JSONObject();
			obj.put(0, BUG_STATUS.NOTFIX);
			obj.put(1, BUG_STATUS.FIXED);
			obj.put(2, BUG_STATUS.CLOSED);
			this.updateBugStatusSort(loginUserId,obj,bug.getProjectId());
		}
		}
		StringBuffer whereSql = new StringBuffer();
		// 导航查询 标题、内容、bugId
		if (bug.getSearch() != null) {
			whereSql.append(" and (title like '%")
					.append(bug.getSearch().trim()).append("%' ")
					.append(" or detail like '%")
					.append(bug.getSearch().trim()).append("%' ")
					.append(" or id like '%").append(bug.getSearch().trim())
					.append("%')");
		}
		// 模块id
		if (bug.getModuleId() != -1) {
			whereSql.append(" and moduleId = ").append(bug.getModuleId());
		}
		//模块名称,不传id
		if(bug.getModuleName()!=null&&bug.getModuleId()==-1){
			whereSql.append(" and moduleId in (select id from T_BUG_MODULE where name like '%").append(bug.getModuleName()).append("%')");
		}
		// 优先级
		if (bug.getPriority() != null) {
			whereSql.append(" and priority = ").append(
					bug.getPriority().ordinal());
		}
		if(bug.getBugPriorityList()!=null){
			List<Integer> bpList=new ArrayList<Integer>();
			for(Enums.BUG_PRIORITY bp:bug.getBugPriorityList()){
				bpList.add(bp.ordinal());
			}
			whereSql.append(" and priority in (").append(StringUtils.join(bpList,',')).append(")");
		}
		// 状态
		if (bug.getStatus() != null) {
			whereSql.append(" and status = ").append(bug.getStatus().ordinal());
		}
		if(bug.getBugStatusList()!=null){
			List<Integer> bsList=new ArrayList<Integer>();
			for(Enums.BUG_STATUS bs:bug.getBugStatusList()){
				bsList.add(bs.ordinal());
			}
			whereSql.append(" and status in (").append(StringUtils.join(bsList,',')).append(")");
		}
		// 解决方案
		if (bug.getSolution() != null) {
			whereSql.append(" and solution = ").append(bug.getSolution().ordinal());
		}
		if(bug.getBugSolutionList()!=null){
			List<Integer> bsoList=new ArrayList<Integer>();
			for(Enums.BUG_SOLUTION bso:bug.getBugSolutionList()){
				bsoList.add(bso.ordinal());
			}
			if(bug.getBugStatusList()!=null&&bug.getBugStatusList().contains(BUG_STATUS.NOTFIX)){
				whereSql.append(" and (status=0 or (solution in (").append(StringUtils.join(bsoList,',')).append(")))");
			}else{
				whereSql.append(" and solution in (").append(StringUtils.join(bsoList,',')).append(")");
			}
			
		}
		// 团队名称
		String teamNameSql = "";
		if (StringUtils.isNotBlank(bug.getTeamName())&&bug.getTeamIdList()==null) {
			String teamSql = "select id from T_TEAM where name like '%"
					+ bug.getTeamName().trim() + "%' and del=0 ";
			teamNameSql = "select id from T_PROJECT where  teamId in ("
					+ teamSql + ") and del=0 ";
		}
		// 项目名称
		String projNameSql = "";
		if (StringUtils.isNotBlank(bug.getProjectName())&&bug.getProjectIdList()==null) {
			projNameSql = "select id from T_PROJECT where del=0 and name like '%"
					+ Tools.sqlFormat(bug.getProjectName().trim()) + "%' ";
//			if (StringUtils.isNotBlank(bug.getTeamName())) {//这么用teamNameSql可能为空字符串
			if (StringUtils.isNotBlank(teamNameSql)) {
				projNameSql += " and id in (" + teamNameSql + ")";
			}
		} else {
			projNameSql = teamNameSql;
		}
		// 流程名称
		String processNameSql = "";
		if (StringUtils.isNotBlank(bug.getProcessName())&&bug.getProcessIdList()==null) {
			processNameSql = "select id from T_PROCESS where del=0 and name like '%"
					+ bug.getProcessName().trim() + "%'";
			if (StringUtils.isNotBlank(projNameSql)) {
				processNameSql += " and projectId in (" + projNameSql + ")";
			}
		} else {
			if (StringUtils.isNotBlank(projNameSql)) {
				processNameSql = "select id from T_PROCESS where del=0 and projectId in ("
						+ projNameSql + ")";
			}
		}
		// 团队idList
				String teamIdListSql = "";
				if (bug.getTeamIdList()!=null) {
					String teamIdList1Sql = "select id from T_TEAM where  id in ("
							+ bug.getTeamIdList() + ") and del=0 ";
					teamIdListSql = "select id from T_PROJECT where  teamId in ("
							+ teamIdList1Sql + ") and del=0 ";
				}
				// 项目名称idList
				String projectIdListSql = "";
				if (bug.getProjectIdList()!=null) {
					projectIdListSql = "select id from T_PROJECT where del=0 and id in ("+bug.getProjectIdList()+")";
					if (bug.getTeamIdList()!=null) {
						projectIdListSql += " and teamId in (" + teamIdListSql + ")";
					}
				} else {
					projectIdListSql = teamIdListSql;
				}
				// 流程名称idList
				String processIdListSql = "";
				if (bug.getProcessIdList()!=null) {
					processIdListSql = "select id from T_PROCESS where del=0 and id in ("+bug.getProcessIdList()+")";
					if (StringUtils.isNotBlank(projectIdListSql)) {
						processIdListSql += " and projectId in (" + projectIdListSql + ")";
					}
				} else {
					if (StringUtils.isNotBlank(projectIdListSql)) {
						processIdListSql = "select id from T_PROCESS where del=0 and projectId in ("
								+ projectIdListSql + ")";
					}
				}
		// 团队，项目，流程合并
		if (StringUtils.isNotBlank(processNameSql)) {
			whereSql.append(" and processId in (").append(processNameSql)
					.append(")");
		}
		if(StringUtils.isNotBlank(processIdListSql)){
			whereSql.append(" and processId in (").append(processIdListSql)
			.append(")");
		}
		// 应用名称
		if (bug.getAppIdList()==null&&StringUtils.isNotBlank(bug.getAppName())) {
			whereSql.append(" and appId in (")
					.append("select id from T_APP where del=0 and name like '%")
					.append(bug.getAppName()).append("%'").append(")");
		}
		if(bug.getAppIdList()!=null){
			whereSql.append(" and appId in (")
			.append("select id from T_APP where del=0 and id in (").append(bug.getAppIdList())
			.append("))");
		}
		// 创建者名称、拼音、邮箱
		if (bug.getCreatorUserId() != 0) {

			String creatorSql = "select bugId from T_BUG_MEMBER where del=0 and type=0 and userId="
					+ bug.getCreatorUserId();
			whereSql.append(" and id in (").append(creatorSql).append(")");
		}
		//传创建者名称不传id
		if (bug.getCreatorUserId() == 0&&bug.getCreatorName()!=null) {

			String creatorSql = "select bugId from T_BUG_MEMBER where del=0 and type=0 and userId in "
					+"(select id from T_USER where userName like '%"+bug.getCreatorName()+"%')";
			whereSql.append(" and id in (").append(creatorSql).append(")");
		}
		// 指派者名称、拼音、邮箱
		if (bug.getAssignedPersonUserId() != 0) {
			String assignedPersonSql = "select bm.bugId from T_BUG_MEMBER bm left join T_BUG_AUTH ba on bm.id = ba.memberId where bm.del=0 and ba.del=0  and bm.userId="
					+ bug.getAssignedPersonUserId()
					+ " and ba.roleId = "
					+ Cache.getRole(
							ENTITY_TYPE.BUG + "_" + ROLE_TYPE.ASSIGNEDPERSON)
							.getId();
			whereSql.append(" and id in (").append(assignedPersonSql)
					.append(")");
		}
		//传指派者名称不传id
		if(bug.getAssignedUserId()==0&&bug.getAssignedPersonName()!=null){
			String assignedPersonSql = "select bm.bugId from T_BUG_MEMBER bm left join T_BUG_AUTH ba on bm.id = ba.memberId where bm.del=0 and ba.del=0  and bm.userId in"
					+ "(select id from T_USER where userName like '%"+bug.getAssignedPersonName()+"%')"
					+ " and ba.roleId = "
					+ Cache.getRole(
							ENTITY_TYPE.BUG + "_" + ROLE_TYPE.ASSIGNEDPERSON)
							.getId();
			whereSql.append(" and id in (").append(assignedPersonSql)
					.append(")");
		}
		// 参与者名称、拼音、邮箱
		if (bug.getMemberUserId() != 0) {
			String memberSql = "select bm.bugId from T_BUG_MEMBER bm left join T_BUG_AUTH ba on bm.id = ba.memberId where bm.del=0 and ba.del=0  and bm.userId="
					+ bug.getMemberUserId()
					+ " and ba.roleId = "
					+ Cache.getRole(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER)
							.getId();
			whereSql.append(" and id in (").append(memberSql).append(")");
		}
		//传参与者名称不传id
		if(bug.getMemberUserId()==0&&bug.getMemberName()!=null){
			String memberSql = "select bm.bugId from T_BUG_MEMBER bm left join T_BUG_AUTH ba on bm.id = ba.memberId where bm.del=0 and ba.del=0  and bm.userId in "
					+ "(select id from T_USER where userName like '%"+bug.getMemberName()+"%')"
					+ " and ba.roleId = "
					+ Cache.getRole(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER)
							.getId();
			whereSql.append(" and id in (").append(memberSql).append(")");
		}
		// 解决者名称、拼音、邮箱
		if (bug.getResolveUserId() != 0) {
			whereSql.append(" and status!=").append(BUG_STATUS.NOTFIX.ordinal()).append(" and resolveUserId in (")
					.append("select id from T_USER where id=")
					.append(bug.getResolveUserId()).append(")");
		}
		//传解决者名称不传id
		if(bug.getResolveUserId()==0&&bug.getResolveName()!=null){
			whereSql.append(" and status!=").append(BUG_STATUS.NOTFIX.ordinal()).append(" and resolveUserId in (")
			.append("select id from T_USER where userName like '%")
			.append(bug.getResolveName()).append("%')");
		}
		// 最后操作人名称、邮箱、拼音
		if (bug.getLastModifyUserId() != 0) {
			whereSql.append(" and lastModifyUserId in (")
					.append("select id from T_USER where id=")
					.append(bug.getLastModifyUserId()).append(")");
		}
		//传操作人名称不传id
		if (bug.getLastModifyUserId() == 0&&bug.getLastModifyName()!=null) {
			whereSql.append(" and lastModifyUserId in (")
					.append("select id from T_USER where userName like '%")
					.append(bug.getLastModifyName()).append("%')");
		}
		// 关闭人名称、邮箱、拼音
		if (bug.getCloseUserId() != 0) {
			whereSql.append(" and status=").append(BUG_STATUS.CLOSED.ordinal()).append(" and closeUserId in (")
					.append("select id from T_USER where id=")
					.append(bug.getCloseUserId()).append(")");
		}
		//传关闭人名称不传id
		if(bug.getCloseUserId()==0&&bug.getCloseName()!=null){
			whereSql.append(" and status=").append(BUG_STATUS.CLOSED.ordinal()).append(" and closeUserId in (")
			.append("select id from T_USER where userName like '%")
			.append(bug.getCloseName()).append("%')");
		}
		// 影响版本
		if (bug.getAffectVersion() != null) {
			whereSql.append(" and affectVersion like '%").append(
					bug.getAffectVersion()).append("%'");
		}
		// 解决版本
		if (bug.getResolveVersion() != null) {
			whereSql.append(" and resolveVersion like '%").append(
					bug.getResolveVersion()).append("%'");
		}
		// 创建时间
		if (bug.getCreateAtStart() != null) {
			whereSql.append(" and date_format(createdAt,'%Y-%m-%d')>='").append(bug.getCreateAtStart()).append("'");
		}
		if (bug.getCreateAtEnd() != null) {
			whereSql.append(" and date_format(createdAt,'%Y-%m-%d')<='").append(bug.getCreateAtEnd()).append("'");
		}
		// 解决时间
		if (bug.getResolveAtStart() != null) {
			whereSql.append(" and date_format(resolveAt,'%Y-%m-%d')>='").append(bug.getResolveAtStart()).append("'");
		}
		if (bug.getResolveAtEnd() != null) {
//			whereSql.append(" and date_format(resolveAt,'%Y-%m-%d')<='").append(bug.getResolveAtEnd().split("-")[0]+'-'+bug.getResolveAtEnd().split("-")[0]+'-'+bug.getResolveAtEnd().split("-")[2]+1).append("'");
			whereSql.append(" and date_format(resolveAt,'%Y-%m-%d')<='").append(bug.getResolveAtEnd()).append("'");
		}
		//
		// 关闭时间
		if (bug.getCloseAtStart() != null) {
			whereSql.append(" and date_format(closeAt,'%Y-%m-%d')>='").append(bug.getCloseAtStart()).append("'");
		}
		if (bug.getCloseAtEnd() != null) {
			whereSql.append(" and date_format(closeAt,'%Y-%m-%d')<='").append(bug.getCloseAtEnd()).append("'");
		}
		// 最后时间
		if (bug.getUpdatedAtStart() != null) {
			whereSql.append(" and date_format(updatedAt,'%Y-%m-%d')>='").append(bug.getUpdatedAtStart()).append("'");
		}
		if (bug.getUpdatedAtEnd() != null) {
			whereSql.append(" and date_format(updatedAt,'%Y-%m-%d')<='").append(bug.getUpdatedAtEnd()).append("'");
		}
	    log.info("bugList whereSql ===>"+whereSql);
		//后台权限配置
	    String permissionEnName=(ENTITY_TYPE.BUG + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
        //项目角色和团队角色权限
	    String permissionSql ="(select * from (select b.id from T_BUG  b left join T_PROCESS pc on b.processId=pc.id"
	    		+" left join T_PROJECT pj on pc.projectId =pj.id left join T_PROJECT_MEMBER pm on pj.id=pm.projectId "
	    		+" left join T_PROJECT_AUTH  pa on pa.memberId=pm.id where b.del=0 and pc.del=0 and pj.del=0"
	    		+" and pm.del=0 and pa.del=0 and pm.userId="+loginUserId+" and pa.roleId in"
	    		+" (select roleId from T_ROLE_AUTH where del=0 and premissionid ="
	    		+" (select id from T_PERMISSION where del=0 and enName='"+permissionEnName+"') )"
	    		+" union  "
				+"select b.id from T_BUG  b left join T_PROCESS pc on b.processId=pc.id"
				+"	left join T_PROJECT pj on pc.projectId =pj.id left join T_TEAM t on pj.teamId=t.id left join T_TEAM_MEMBER tm on t.id=tm.teamId" 
				+"	left join T_TEAM_AUTH  ta on ta.memberId=tm.id where b.del=0 and pc.del=0 and pj.del=0 and t.del=0"
				+"	and tm.del=0 and ta.del=0 and tm.userId="+loginUserId+"  and ta.roleId in"
				+"	(select roleId from T_ROLE_AUTH where del=0 and premissionid ="
				+"	(select id from T_PERMISSION where del=0 and enName='"+permissionEnName+"')))as xxx)";
		
	    //用户参与的所有的项目下的bug可查看
//		String permissionSql = "(select id from t_bug where "
//				+ "processId in (select id from t_process where projectId in (select projectId from "
//				+ "t_project_member where userId=" + loginUserId
//				+ " and del=0) and del=0))";
		String execSql = "select * from T_BUG  where id in " + permissionSql
				+ (whereSql!=null?whereSql:"") + " and del=0";
		// 排序方式
		if(bug.getOrderBy()!=null&&bug.getSortBy()!=null){
			execSql += "  order by " + bug.getOrderBy()+"  "+bug.getSortBy();
		}else if (bug.getSortType() != null) {
			execSql += " " + bug.getSortType();
		} else {
			execSql += " order by createdAt desc";
		}
		String totleSql = "select count(1) from T_BUG  where id in "
				+ permissionSql + (whereSql!=null?whereSql:"") + "  and del=0";
		Long total = jdbcTpl.queryForObject(totleSql, Long.class);

		execSql += " limit " + (pageNo - 1) * pageSize + ", " + pageSize;
		List<Map<String, Object>> bugList = jdbcTpl.queryForList(execSql);
		if (bugList.size() > 0) {
			for (Map<String, Object> singleBug : bugList) {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				// 格式化时间
				singleBug.put(
						"createdAt",
						formatter.format(singleBug.get("createdAt"))
						);
				singleBug.put(
						"updatedAt",
						formatter.format(singleBug.get("updatedAt")));
				// 枚举格式化
				if (singleBug.get("status") != null) {
					switch (Integer
							.parseInt(singleBug.get("status").toString())) {
					case 0:
						singleBug.put("status", Enums.BUG_STATUS.NOTFIX);
						break;
					case 1:
						singleBug.put("status", Enums.BUG_STATUS.FIXED);
						break;
					case 2:
						singleBug.put("status", Enums.BUG_STATUS.CLOSED);
						break;
					default:
						singleBug.put("status", Enums.BUG_STATUS.NOTFIX);
						break;
					}
				}
				if (singleBug.get("solution") != null) {
					switch (Integer.parseInt(singleBug.get("solution")
							.toString())) {
					case 0:
						singleBug.put("solution", Enums.BUG_SOLUTION.BYDESIGN);
						break;
					case 1:
						singleBug.put("solution", Enums.BUG_SOLUTION.DUPLICATE);
						break;
					case 2:
						singleBug.put("solution", Enums.BUG_SOLUTION.NOTREPRO);
						break;
					case 3:
						singleBug.put("solution", Enums.BUG_SOLUTION.FIXED);
						break;
					case 4:
						singleBug.put("solution", Enums.BUG_SOLUTION.EXTERNAL);
						break;
					case 5:
						singleBug.put("solution", Enums.BUG_SOLUTION.POSTPONED);
						break;
					case 6:
						singleBug.put("solution", Enums.BUG_SOLUTION.NOTFIX);
						break;
					default:
						singleBug.put("solution", Enums.BUG_SOLUTION.FIXED);
						break;
					}
				}else{
					singleBug.put("solution","无");
				}
				if (singleBug.get("priority") != null) {
					switch (Integer.parseInt(singleBug.get("priority")
							.toString())) {
					case 0:
						singleBug.put("priority", Enums.BUG_PRIORITY.NORMAL);
						break;
					case 1:
						singleBug.put("priority", Enums.BUG_PRIORITY.URGENT);
						break;
					case 2:
						singleBug.put("priority",
								Enums.BUG_PRIORITY.VERY_URGENT);
						break;
					default:
						singleBug.put("priority", Enums.BUG_PRIORITY.NORMAL);
						break;
					}
				}
				Process process = processDao.findOne(Long.parseLong(singleBug
						.get("processId").toString()));
				if (process != null) {
					singleBug.put("processName", process.getName());
					Project project = projectDao
							.findOne(process.getProjectId());
					if (project != null) {
						singleBug.put("projectName", project.getName());
						singleBug.put("projectId", project.getId());
					}
				}
				if (Long.parseLong(singleBug.get("appId").toString()) != -1) {
					App app = appDao.findOne(Long.parseLong(singleBug.get(
							"appId").toString()));
					if (null != app) {
						singleBug.put("appName", app.getName());
					}else{
						singleBug.put("appName","无");
					}
				}else{
					singleBug.put("appName","无");
				}

				List<EntityResourceRel> relList = entityResourceRelDao
						.findByEntityIdAndEntityTypeAndDel(
								Long.parseLong(singleBug.get("id").toString()),
								ENTITY_TYPE.BUG, DELTYPE.NORMAL);
				List<Long> resourceIdList = new ArrayList<>();
				for (EntityResourceRel rel : relList) {
					resourceIdList.add(rel.getResourceId());
				}
				if (resourceIdList.size() > 0) {
					//List<Resource> resources = resourcesDao
					//		.findByIdIn(resourceIdList);
					//singleBug.put("resources", resources);
					singleBug.put("resourceTotal", resourceIdList.size());
				} else {
					singleBug.put("resourceTotal", 0);
				}
				// 模块名称
				if (Integer.parseInt(singleBug.get("moduleId").toString()) != -1) {
					BugModule bugModule = bugModuleDao.findOne(Long
							.parseLong(singleBug.get("moduleId").toString()));
					singleBug.put("moduleName", bugModule.getName());
				} else {
					singleBug.put("moduleName", "无");
				}

				// 添加创建者，负责人
				List<BugMember> allMembers = bugMemberDao.findByBugIdAndDel(
						Long.parseLong(singleBug.get("id").toString()),
						DELTYPE.NORMAL);
				//List<User> memberUsers = new ArrayList<User>();
				for (BugMember member : allMembers) {
					User user = userDao.findOne(member.getUserId());
					member.setUserName(user.getUserName());
					List<BugAuth> authes = bugAuthDao.findByMemberIdAndDel(
							member.getId(), DELTYPE.NORMAL);
					Set<String> roleSet = new HashSet<>();
					for (BugAuth auth : authes) {
						Role role = Cache.getRole(auth.getRoleId());
						if (role != null) {
							roleSet.add(role.getEnName());
						}
					}
					if (roleSet.contains(ENTITY_TYPE.BUG + "_"
							+ ROLE_TYPE.ASSIGNEDPERSON)) {
						singleBug.put("assignedPersonName", user.getUserName());
					} 
					if(roleSet.contains(ENTITY_TYPE.BUG + "_"
							+ ROLE_TYPE.CREATOR)) {
						singleBug.put("creatorName", user.getUserName());
					} 
				}
				//singleBug.put("member", memberUsers);
				// 获取指派者
				User user = userDao.findOne(Long.parseLong(singleBug.get(
						"resolveUserId").toString()));
				if (user != null) {
					singleBug.put("resolveName", user.getUserName());
				}else{
					singleBug.put("resolveName","无");
				}
			}
		}

		log.info("execute-buglist-sql-->" + execSql);
		Long totalPage;
		if(total % pageSize==0){
			totalPage= total / pageSize;
		}else{
			totalPage= total / pageSize+1;
		}
		log.info("bug list sql -->" + execSql);
		List<Object> result = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", bugList);
		map.put("total", total.intValue());
		map.put("pageNo", pageNo);
		map.put("pageSize", pageSize);
		map.put("totalPage", totalPage.intValue());
		result.add(map);
		return result;
	}

	public int updateBugStatusSort(Long loginUserId,JSONObject obj,Long projectId) {
		List<BugStatusSort> bugStatusSorts = this.bugStatusSortDao.findByUserIdAndProjectIdAndDel(loginUserId,projectId,DELTYPE.NORMAL);
		for(int a = 0;a<obj.size();a++){
			BugStatusSort bugStatusSort = null;
			if(null!=bugStatusSorts && !bugStatusSorts.isEmpty()){
				bugStatusSort = bugStatusSorts.get(a);
				bugStatusSort.setSort(a);
				String bugStatus = (String)obj.get(a+"");
				bugStatusSort.setStatus(BUG_STATUS.valueOf(bugStatus));
			}else{
				bugStatusSort = new BugStatusSort();
				bugStatusSort.setSort(a);
				bugStatusSort.setProjectId(projectId);
				
				String bugStatus = (String)obj.get(a+"");
				
				bugStatusSort.setStatus(BUG_STATUS.valueOf(bugStatus) );
				bugStatusSort.setUserId(loginUserId);
			}
			this.bugStatusSortDao.save(bugStatusSort);
			
		}
		return 1;
	}

	public Bug getSingleBug(long bugId) {
		Bug bug = this.bugDao.findOne(bugId);
		return bug;
	}

	public BugMember getBugMember(long memberId) {
		return this.bugMemberDao.findOne(memberId);
	}

	public Bug findOne(long bugId) {
		Bug b=bugDao.findOne(bugId);
		return b;
	}
	public Map<Long, List<String>> permissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		long startTime = System.currentTimeMillis();
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();
		StringBuffer sql= new StringBuffer("select t.bugId,group_concat(t.roleId) from (");
		sql.append(" select distinct member.bugId,auth.roleId from T_BUG_MEMBER member left join T_BUG_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(")")
		.append(") t group by t.bugId");
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
//						rs.getLong(1)
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
		log.info("bugService permissionsMapAsMember total time -->"+(endTime-startTime) +" ms");
		return permissionsMapAsMember;
	}
	/**
	 * @author haijun.cheng
	 * @date 2016-09-18
	 * @param permissionEnName
	 * @param loginUserId
	 * @return
	 */
	public Set<Long> getPermissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		long startTime = System.currentTimeMillis();
		final Set<Long> idSet = new HashSet<Long>();
		StringBuffer sql= new StringBuffer("select distinct member.bugId,auth.roleId from T_BUG_MEMBER member left join T_BUG_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(")");
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						idSet.add(rs.getLong(1));
					}
				});
		
		long endTime = System.currentTimeMillis();
		log.info("bugService permissionsMapAsMember total time -->"+(endTime-startTime) +" ms");
		return idSet;
	}
	public Map<Long, List<String>> permissionMapAsMemberWithAndOnlyByBugId(String permissionEnName, long loginUserId,long bugId) {
		long startTime = System.currentTimeMillis();
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();
		StringBuffer sql= new StringBuffer("select t.bugId,group_concat(t.roleId) from (");
		sql.append(" select distinct member.bugId,auth.roleId from T_BUG_MEMBER member left join T_BUG_AUTH  auth on member.id = auth.memberId  ")
		.append(" where member.bugId=").append(bugId).append(" and member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
		.append(" and auth.roleId  in( ")
		.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='").append(permissionEnName).append("' and del=0) and del=0  ")
		.append(")")
		.append(") t");
		log.info("permissionMapAsMemberWithOnlyByBugId====sql=====>"+sql.toString());
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						if(rs.next()){
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
					}
				});
		
		long endTime = System.currentTimeMillis();
		log.info("bugService permissionsMapAsMember total time -->"+(endTime-startTime) +" ms");
		return permissionsMapAsMember;
	}
}
