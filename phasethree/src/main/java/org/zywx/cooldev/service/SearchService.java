package org.zywx.cooldev.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.system.Cache;

@Service
public class SearchService extends BaseService {
	
	@Autowired
	protected ProjectService projectService;
	@Autowired
	protected TeamService teamService;
	@Autowired
	protected ProcessService processService;
	@Autowired
	protected TaskService taskService;
	@Autowired
	protected BugService bugService;
	
	/**
	 * 
	 * @describe 根据类型进行搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月19日 下午8:24:54 <br>
	 * @param query
	 * @param type
	 * @param pageNo
	 * @param pageInfo
	 * @return <br>
	 * @returnType List<?>
	 *
	 */
	public Map<String, Object> getSearchByType(String query, Long loginUserId, int type, int pageNo, int pageInfo) {
		//前端说不分页 此字段预留 暂不用
		@SuppressWarnings("unused")
		PageRequest pageable = new PageRequest(pageNo, pageInfo, Direction.DESC, "createdAt");
		Map<String, Object> list = new HashMap<String, Object>();
		switch (type) {
		// 项目
		case 1:
			list = getProject(query, loginUserId, pageable);//不分页 最后的参数传null 分页就传pageable
			break;
		// 任务
		case 2:
			list = getTask(query, loginUserId, pageable);//不分页 最后的参数传null 分页就传pageable
			break;
		// 团队
		case 3:
			list = getTeam(query, loginUserId, pageable);//不分页 最后的参数传null 分页就传pageable
			break;
		// 文档
		case 4:
			list = getDocument(query, loginUserId, pageable);//不分页 最后的参数传null 分页就传pageable
			break;
		// 资源
		case 5:
			list = getResource(query, loginUserId, pageable);//不分页 最后的参数传null 分页就传pageable
			break;
		// bug
		default:
			list = getBug(query, loginUserId, pageable);//不分页 最后的参数传null 分页就传pageable
		}

		return list;

	}

	/**
	 * 
	 * @describe 搜索全部 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月19日 下午8:31:51 <br>
	 * @param query
	 * @param loginUserId
	 * @param type
	 * @return <br>
	 * @returnType List<?>
	 *
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSearch(String query, Long loginUserId) {
		Map<String, Object> res = new HashMap<String, Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int total = 0;
		int totalProject = 0;
		int totalTask = 0;
		int totalTeam = 0;
		int totalDocument = 0;
		int totalResource = 0;
	    int totalBug=0;
		for (int a = 1; a < 7; a++) {
			Map<String, Object> map = search(query, loginUserId, a);
			list.addAll((List<Map<String, Object>>) map.get("list"));
			total += Integer.parseInt(map.get("count").toString());
			switch (a) {
			// 项目结果总数
			case 1:
				totalProject = Integer.parseInt(map.get("count").toString());
				break;
			// 任务结果总数
			case 2:
				totalTask = Integer.parseInt(map.get("count").toString());
				break;
			// 团队结果总数
			case 3:
				totalTeam = Integer.parseInt(map.get("count").toString());
				break;
			// 文档结果总数
			case 4:
				totalDocument = Integer.parseInt(map.get("count").toString());
				break;
			// 资源结果总数
			case 5:
				totalResource = Integer.parseInt(map.get("count").toString());
				break;
			//bug
			default:
				totalBug = Integer.parseInt(map.get("count").toString());
			}
			
		}
		res.put("list", list);
		res.put("total", total);
		res.put("totalProject", totalProject);
		res.put("totalTask", totalTask);
		res.put("totalTeam", totalTeam);
		res.put("totalDocument", totalDocument);
		res.put("totalResource", totalResource);
		res.put("totalBug", totalBug);
		return res;

	}

	public Map<String, Object> search(String query, Long loginUserId, int type) {
		Map<String, Object> list = new HashMap<String, Object>();
		switch (type) {
		// 项目
		case 1:
			list = getProject(query, loginUserId, null);
			break;
		// 任务
		case 2:
			list = getTask(query, loginUserId, null);
			break;
		// 团队
		case 3:
			list = getTeam(query, loginUserId, null);
			break;
		// 文档
		case 4:
			list = getDocument(query, loginUserId, null);
			break;
		// 资源
		case 5:
			list = getResource(query, loginUserId, null);
			break;
	   //bug
		default:
			list=getBug(query,loginUserId,null);
		}

		return list;
	}

	/**
	 * 
	 * @describe 资源搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 下午6:11:48 <br>
	 * @param query
	 * @param loginUserId
	 * @param object
	 * @return <br>
	 * @returnType Map<String,Object>
	 *
	 */
	private Map<String, Object> getResource(String query, Long loginUserId, Pageable page) {
		String required = (ENTITY_TYPE.RESOURCE + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		// 项目成员权限
//		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required, loginUserId);
		Set<Long> projectIds = this.projectService.getProjectsByRequiredAndLoginUserId(required, loginUserId);
		String projectIdsIn = getConQuery(projectIds);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select r.id,r.name,r.fileSize,r.userName,p.id projectId,p.name projectName,u.icon,r.createdAt,'RESOURCES' as searchType from T_RESOURCES r left join T_USER u on r.userId = u.id left join T_PROJECT p on r.projectId = p.id "
				+ "where substring_index(r.name,'.',1) like ? and r.del = ? and r.projectId in ("+projectIdsIn+") order by r.createdAt desc ";
		String sql1 = "select count(*) count from T_RESOURCES r "
				+ "where substring_index(r.name,'.',1) like ? and r.del = ? and r.projectId in ("+projectIdsIn+") order by r.createdAt desc ";
		if (null != page) {
			sql += " limit " + (page.getPageNumber() - 1) * page.getPageSize() + "," + page.getPageSize() + ";";
		}

		list = this.jdbcTpl.queryForList(sql, new Object[] { query, DELTYPE.NORMAL.ordinal() });
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1, new Object[] { query, DELTYPE.NORMAL.ordinal() });
		log.info(sql);
		log.info(sql1);
		HashMap<String, Object> res = new HashMap<>();
		res.put("list", list);
		res.put("count", count.get("count"));
		return res;
	}

	/**
	 * 文档列表增加项目id
	 * @describe 文档搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 下午6:11:33 <br>
	 * @param query
	 * @param loginUserId
	 * @param page
	 * @return <br>
	 * @returnType Map<String,Object>
	 *
	 */
	private Map<String, Object> getDocument(String query, Long loginUserId, Pageable page) {
		String required = (ENTITY_TYPE.DOCUMENT + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		// 项目成员权限
//		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required, loginUserId);
		Set<Long> projectIds = this.projectService.getProjectsByRequiredAndLoginUserId(required, loginUserId);
		
		String projectIdsIn = getConQuery(projectIds);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select d.id,d.name,d.describ,p.id projectId,p.name projectName,u.userName,u.icon,d.createdAt,'DOCUMENT' as searchType from T_DOCUMENT d left join T_PROJECT p on d.projectId = p.id left join T_USER u on u.id = d.userId "
				+ "where (d.name like ? or d.describ like ?) and d.projectId in ("+projectIdsIn+") and p.del = ? and d.del = ? and u.del = ? order by d.createdAt desc ";
		String sql1 = "select count(*) count from T_DOCUMENT d left join T_PROJECT p on d.projectId = p.id left join T_USER u on u.id = d.userId "
				+ "where (d.name like ? or d.describ like ?) and d.projectId in ("+projectIdsIn+") and p.del = ? and d.del = ? and u.del = ? ";
		if (null != page) {
			sql += " limit " + (page.getPageNumber() - 1) * page.getPageSize() + "," + page.getPageSize() + ";";
		}
		log.info(sql);
		log.info(sql1);
		list = this.jdbcTpl.queryForList(sql, new Object[] { query, query, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal() });
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1, new Object[] { query, query, DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal() });

		HashMap<String, Object> res = new HashMap<>();
		res.put("list", list);
		res.put("count", count.get("count"));
		return res;
	}

	/**
	 * 
	 * @describe 团队搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 下午5:26:15 <br>
	 * @param query
	 * @param loginUserId
	 * @param page
	 * @return <br>
	 * @returnType Map<String,Object>
	 *
	 */
	private Map<String, Object> getTeam(String query, Long loginUserId, Pageable page) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select t.id,t.name,t.type,u.userName,u.icon,t.createdAt,'TEAM' as searchType,"
				//参与总人数
				+ "(select count(*) count from T_TEAM_MEMBER ttm1 where ttm1.teamId=t.id and ttm1.del =?) member,"
				//项目数量
				+ "(select count(*) count from T_PROJECT p1 where p1.teamId=t.id and p1.del =? ) project"
				+ " from T_TEAM t left join T_TEAM_MEMBER tm on t.id = tm.teamId and tm.type = ? left join T_USER u on u.id = tm.userId  "
				+ "where t.name like ? and t.id in (select ttm.teamId from T_TEAM_MEMBER ttm where ttm.userId = ?) and t.del =? and tm.del = ? order by t.createdAt desc ";
		String sql1 = "select count(*) count from T_TEAM t left join T_TEAM_MEMBER tm on t.id = tm.teamId AND tm.type = ? left join T_USER u on u.id = tm.userId "
				+ "where t.name like ? and t.id in (select ttm.teamId from T_TEAM_MEMBER ttm where ttm.userId = ?) and t.del =? and tm.del = ? order by t.createdAt desc ";
		if (null != page) {
			sql += " limit " + (page.getPageNumber() - 1) * page.getPageSize() + "," + page.getPageSize() + ";";
		}
		log.info(sql);
		log.info(sql1);
		list = this.jdbcTpl.queryForList(sql,
				new Object[] {DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal(),TEAMREALTIONSHIP.CREATE.ordinal(), query, loginUserId, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal() });
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1,
				new Object[] { TEAMREALTIONSHIP.CREATE.ordinal(),query, loginUserId, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal() });
		HashMap<String, Object> res = new HashMap<>();
		res.put("list", list);
		res.put("count", count.get("count"));
		return res;
	}

	/**
	 * 
	 * @describe 任务搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 下午4:36:32 <br>
	 * @param query
	 * @param loginUserId
	 * @param page
	 * @return <br>
	 * @returnType Map<String,Object>
	 *
	 */
	private Map<String, Object> getTask(String query, Long loginUserId, Pageable page) {
		// 根据用户权限构建查询条件
		String required = (ENTITY_TYPE.TASK + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		// 项目成员权限
//		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required, loginUserId);
		// 流程成员权限
//		Map<Long, List<String>> pMapAsProcessMember = processService.permissionMapAsMemberWith(required, loginUserId);
		// 任务成员权限
//		Map<Long, List<String>> pMapAsTaskMember = taskService.permissionMapAsMemberWith(required, loginUserId);
		// 根据团队、项目、流程及任务许可确定查询范围
		
		Set<Long> projectIdList = this.projectService.getProjectsByRequiredAndLoginUserId(required, loginUserId);
		
		Set<Long> processIdSet = this.processService.getPermissionMapAsMemberWith(required, loginUserId);
		
		Set<Long> taskIdSet = this.taskService.getPermissionMapAsMemberWith(required, loginUserId);
		if( projectIdList.size() > 0 ) {
			List<Long> pList = processDao.findByProjectIdAndDel(projectIdList, DELTYPE.NORMAL);
			processIdSet.addAll(pList);
		}
		
		String processIds = getConQuery(processIdSet);
		String taskIds = getConQuery(taskIdSet);
		
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select t.id,t.detail,t.status,u.userName,u.icon,t.createdAt,'TASK' as searchType,"
				//流程名称
				+ " (select p1.name processName from T_TASK t1 left join T_PROCESS p1 on p1.id = t1.processId where t1.id=t.id and p1.del =? ) processName, "
				//项目名称
				+ " (select pro1.name projectName from T_TASK t2 left join T_PROCESS p2 on p2.id = t2.processId left join T_PROJECT pro1 on pro1.id = p2.projectId where t2.id=t.id and p2.del =? and pro1.del=?) projectName, "
				//评论数量
				+ " (select count(1) from T_TASK_COMMENT ttc where ttc.taskId = t.id and ttc.del = ? ) comment, "
				//资源数量
				+ " (select count(1) from T_ENTITY_RESOURCE_REL ter where ter.entityId = t.id and ter.del = ? ) resourceCount ,"
				+" ( select count(1) from T_TASK_LEAF leaf where leaf.topTaskId=t.id and leaf.del=0 ) totalTaskLeaf,"
				+" ( select count(1) from T_TASK_LEAF leaf where leaf.topTaskId=t.id and leaf.del=0 and leaf.status=0 ) nofinishedTaskLeaf"
				+ " from T_TASK t left join T_TASK_MEMBER tm on t.id = tm.taskId and tm.type=? left join T_USER u on u.id = tm.userId "
				+ " where t.detail like ? and (t.id in ("+taskIds+") or t.processId in ("+processIds+")) and t.del =? and tm.del = ? order by t.createdAt desc ";
		String sql1 = "select count(1) count from T_TASK t left join T_TASK_MEMBER tm on t.id = tm.taskId and tm.type=? left join T_USER u on u.id = tm.userId "
				+ " where t.detail like ? and (t.id in ("+taskIds+") or t.processId in ("+processIds+") and t.del =?) and tm.del = ? and t.del=?";
		if (null != page) {
			sql += " limit " + (page.getPageNumber() - 1) * page.getPageSize() + "," + page.getPageSize() + ";";
		}
		log.info(sql);
		log.info(sql1);
		list = this.jdbcTpl.queryForList(sql,
				new Object[] { DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal(),TASK_MEMBER_TYPE.CREATOR.ordinal(),query, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal() });
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1,
				new Object[] { TASK_MEMBER_TYPE.CREATOR.ordinal(),query, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal() });
		HashMap<String, Object> res = new HashMap<>();
		res.put("list", list);
		res.put("count", count.get("count"));
		return res;
	}

	/**
	 * 
	 * @describe 项目搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 下午4:08:32 <br>
	 * @param query
	 * @param loginUserId
	 * @return <br>
	 * @returnType List<Map<String,Object>>
	 *
	 */
	public Map<String, Object> getProject(String query, Long loginUserId, Pageable page) {
		String required = (ENTITY_TYPE.PROJECT + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		// 项目成员权限
//		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required, loginUserId);
		Set<Long> projectIds = this.projectService.getProjectsByRequiredAndLoginUserId(required, loginUserId);
		
		String projectIdsIn = getConQuery(projectIds);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select p.progress,p.id,p.name projectName,p.bizLicense,t.name teamName,u.userName,u.icon,p.createdAt,'PROJECT' as searchType from T_PROJECT p left join T_TEAM t on p.teamId = t.id left join T_PROJECT_MEMBER tpm on p.id = tpm.projectId and tpm.type=? left join T_USER u on tpm.userId = u.id "
				+ "where p.id in ("+projectIdsIn+") "
				+ "and p.name like ? and p.del = ? order by p.createdAt desc ";
		String sql1 = "select count(*) count from T_PROJECT p left join T_TEAM t on p.teamId = t.id left join T_PROJECT_MEMBER tpm on p.id = tpm.projectId and tpm.type=? left join T_USER u on tpm.userId = u.id "
				+ "where p.id in ("+projectIdsIn+") "
				+ "and p.name like ? and p.del = ? ";
		if (null != page) {
			sql += " limit " + (page.getPageNumber() - 1) * page.getPageSize() + "," + page.getPageSize() + ";";
		}
		log.info(sql);
		log.info(sql1);
		list = this.jdbcTpl.queryForList(sql,
				new Object[] {PROJECT_MEMBER_TYPE.CREATOR.ordinal(),query, DELTYPE.NORMAL.ordinal() });
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1,
				new Object[] {PROJECT_MEMBER_TYPE.CREATOR.ordinal(),query, DELTYPE.NORMAL.ordinal() });

		for (Map<String, Object> map : list) {
//			int progress = projectService.getProjectProgressForInt((Long)map.get("id"));由于任务,bug时候已经计算了项目进度存到数据库,这直接读取
			map.put("progress", map.get("progress") + "%");
			map.put("bizLicense", null!=map.get("bizLicense") && map.get("bizLicense").equals(PROJECT_BIZ_LICENSE.AUTHORIZED.ordinal())
					? PROJECT_BIZ_LICENSE.AUTHORIZED.name() : PROJECT_BIZ_LICENSE.NOT_AUTHORIZED.name());
		}
		HashMap<String, Object> res = new HashMap<>();
		res.put("list", list);
		res.put("count", count.get("count"));
		return res;
	}

	private String getConQuery(Set<Long> projectIds) {
		String str = "-1,";
		for(Long id : projectIds){
			str += id+",";
		}
		str = str.substring(0,str.length()-1);
		return str;
	}
	private Map<String, Object> getBug(String query, Long loginUserId,
			PageRequest page) {
		// 根据用户权限构建查询条件
		String required = (ENTITY_TYPE.BUG + "_" + CRUD_TYPE.RETRIEVE)
				.toLowerCase();
		// 项目成员权限
//		Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required, loginUserId);
		// 流程成员权限
//		Map<Long, List<String>> pMapAsProcessMember = processService
//				.permissionMapAsMemberWith(required, loginUserId);
		// bug成员权限
//		Map<Long, List<String>> pMapAsBugMember = bugService
//				.permissionMapAsMemberWith(required, loginUserId);
		// 根据团队、项目、流程及任务许可确定查询范围

		Set<Long> projectIdList = this.projectService.getProjectsByRequiredAndLoginUserId(required, loginUserId);

		Set<Long> processIdSet = this.processService.getPermissionMapAsMemberWith(required, loginUserId);
		
		Set<Long> bugIdSet = this.processService.getPermissionMapAsMemberWith(required, loginUserId);
		if (projectIdList.size() > 0) {
			List<Long> pList = processDao.findByProjectIdAndDel(projectIdList,
					DELTYPE.NORMAL);
			processIdSet.addAll(pList);
		}

		String processIds = getConQuery(processIdSet);
		String bugIds = getConQuery(bugIdSet);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String sql = "select b.id,b.title,b.status,b.solution,u.userName as assignedPersonName,u.icon,"
				+ "b.createdAt,'BUG' as searchType,pc.name as processName,pj.id as projectId,pj.name as projectName,"
				// 资源数量
				+ " (select count(*) from T_ENTITY_RESOURCE_REL ter where ter.entityId = b.id and ter.del = ? ) resourceCount "
				+ " from T_BUG b  left join T_BUG_MEMBER bm on b.id = bm.bugId left join T_BUG_AUTH ba on ba.memberId=bm.id left join T_USER u on u.id = bm.userId left join T_PROCESS pc on pc.id=b.processId left join T_PROJECT pj on pj.id=pc.projectId"
				+ " where  pc.del=? and pj.del=? and ba.roleId=? and ba.del=? and b.title like ? and (b.id in ("
				+  bugIds
				+ ") or b.processId in ("
				+ processIds
				+ ")) and b.del =? and bm.del = ? order by b.createdAt desc ";
		String sql1 = "select count(*) count from T_BUG b left join T_BUG_MEMBER bm on b.id = bm.bugId left join T_BUG_AUTH ba on ba.memberId=bm.id  left join T_USER u on u.id = bm.userId "
				+ " where  ba.roleId=? and ba.del=? and b.title like ? and (b.id in ("
				+ bugIds
				+ ") or b.processId in ("
				+ processIds
				+ ") and b.del =?) and bm.del = ? and b.del=?";
		if (null != page) {
			sql += " limit " + (page.getPageNumber() - 1) * page.getPageSize()
					+ "," + page.getPageSize() + ";";
		}
		log.info(sql);
		log.info(sql1);
		list = this.jdbcTpl.queryForList(
				sql,
				
				new Object[] {
						DELTYPE.NORMAL.ordinal(),
						DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal(),Cache.getRole(ENTITY_TYPE.BUG+"_"+ROLE_TYPE.ASSIGNEDPERSON).getId(),DELTYPE.NORMAL.ordinal(),query,
						DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal() });
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1,
				new Object[] {Cache.getRole(ENTITY_TYPE.BUG+"_"+ROLE_TYPE.ASSIGNEDPERSON).getId(),DELTYPE.NORMAL.ordinal(),query,
						DELTYPE.NORMAL.ordinal(), 
						DELTYPE.NORMAL.ordinal(),DELTYPE.NORMAL.ordinal()});
		HashMap<String, Object> res = new HashMap<>();
		res.put("list", list);
		res.put("count", count.get("count"));
		return res;
	}


}
