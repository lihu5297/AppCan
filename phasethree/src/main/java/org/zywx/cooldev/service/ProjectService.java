package org.zywx.cooldev.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.appdo.facade.omm.entity.tenant.Enterprise;
import org.zywx.appdo.facade.omm.service.tenant.TenantFacade;
import org.zywx.appdo.facade.user.entity.organization.Personnel;
import org.zywx.appdo.facade.user.service.organization.PersonnelFacade;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.BUG_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.PROCESS_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.commons.Enums.TOPIC_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.USER_LEVEL;
import org.zywx.cooldev.commons.Enums.USER_STATUS;
import org.zywx.cooldev.commons.Enums.USER_TYPE;
import org.zywx.cooldev.dao.TeamDao;
import org.zywx.cooldev.dao.UserDao;
import org.zywx.cooldev.dao.task.TaskGroupDao;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.GitAuthVO;
import org.zywx.cooldev.entity.app.GitOwnerAuthVO;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.bug.BugAuth;
import org.zywx.cooldev.entity.bug.BugMember;
import org.zywx.cooldev.entity.builder.Setting;
import org.zywx.cooldev.entity.filialeInfo.FilialeInfo;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.process.ProcessAuth;
import org.zywx.cooldev.entity.process.ProcessMember;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectCategory;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.project.ProjectParent;
import org.zywx.cooldev.entity.project.ProjectSort;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskAuth;
import org.zywx.cooldev.entity.task.TaskGroup;
import org.zywx.cooldev.entity.task.TaskMember;
import org.zywx.cooldev.entity.topic.TopicAuth;
import org.zywx.cooldev.entity.topic.TopicMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.MD5Util;
import org.zywx.cooldev.util.Tools;
import org.zywx.cooldev.util.UserListWrapUtil;
import org.zywx.cooldev.util.emm.TokenUtilProduct;
import org.zywx.cooldev.util.mail.base.MailSenderInfo;
import org.zywx.cooldev.util.mail.base.SendMailTools;
import org.zywx.cooldev.vo.Match4Project;

import net.sf.json.JSONObject;

@Service
public class ProjectService extends AuthService {
	@Value("${emmUrl}")
	private String emmUrl;

	@Value("${emmInvokeTeamUrl}")
	private String emmInvokeTeamUrl;

	@Value("${tenantId}")
	private String tenantId;

	@Value("${key}")
	private String key;

	@Value("${xietongHost}")
	private String xietongHost;
	
	@Value("${emailSourceRootPath}")
	private String emailSourceRootPath;
	
	@Value("${xtHost}")
	private String xtHost;

	@Autowired
	protected UserService userService;

	@Autowired
	private NoticeService noticeService;
	@Autowired
	private DynamicService dynamicService;
	@Autowired
	private UserDao userDao;

	@Autowired
	protected TeamMemberService teamMemberService;

	@Autowired
	private ProjectMemberService projectMemberService;

	@Autowired
	protected ProcessService processService;

	@Autowired
	private TeamService teamService;

	@Autowired(required = false)
	private PersonnelFacade personnelFacade;

	@Autowired
	protected AppService appService;

	// 企业版还是大众版标识
	@Value("${serviceFlag}")
	private String serviceFlag;

	@Autowired(required = false)
	private TenantFacade tenantFacade;

	// sso用户名和密码校验
	@Value("${ssoValidHost}")
	private String ssoValidHost;

	@Autowired
	private TaskGroupDao taskGroupDao;
	
	@Autowired 
	private TeamDao teamDao;

	@Value("${ePortalGitRepoPath}")
	private String ePortalGitRepoPath;

	@Value("${oaGitRepoPath}")
	private String oaGitRepoPath;

	@Value("${user.icon}")
	private String userIcon;

	/**
	 * 获取项目列表，返回的Map为结果message
	 * 
	 * @param pageable
	 * @param matchObj
	 * @param loginUserId
	 * @return
	 */
	@Cacheable(value="ProjectService_getProjectList",key="#pageNo+'_'+#pageSize+'_'+#loginUserId+'_'+#projName+'_'+#creator+'_'+#actor+'_'+#begin+'_'+#end+'_'+#teamName+'_'+#pfstime+'_'+#pfetime+'_'"
			+ "+#matchObj.bizLicense+'_'+#matchObj.status+'_'+#matchObj.type+'_'+#matchObj.categoryId+'_'+#matchObj.memberType")
	public Map<String, Object> getProjectList(int pageNo, int pageSize, Match4Project matchObj, long loginUserId,
			String projName, String creator, String actor, String begin, String end, String teamName, String pfstime,
			String pfetime,Integer parentId) {
		log.info("request message:" + matchObj.toString() + ",teamName:" + teamName + ",projName:" + projName
				+ ",begin:" + begin + ",end:" + end + ",pfstime:" + pfstime + ",pfetime:" + pfetime);
		long beginSearchTime = System.currentTimeMillis();
		List<PROJECT_MEMBER_TYPE> memberType = matchObj.getMemberType();
		List<PROJECT_BIZ_LICENSE> bizLicense = matchObj.getBizLicense();
		List<PROJECT_STATUS> status = matchObj.getStatus();
		List<PROJECT_TYPE> type = matchObj.getType();
		List<Long> categoryId = matchObj.getCategoryId();
		StringBuffer categoryIdsStr = new StringBuffer("");
		if (null != matchObj.getCategoryId()) {
			for (Long cateId : categoryId)
				categoryIdsStr.append(cateId).append(",");
		}
		if (categoryIdsStr.length() > 0) {
			categoryIdsStr.deleteCharAt(categoryIdsStr.length() - 1);
		}
		if (memberType == null) {
			memberType = new ArrayList<PROJECT_MEMBER_TYPE>();
			memberType.add(PROJECT_MEMBER_TYPE.CREATOR);
			memberType.add(PROJECT_MEMBER_TYPE.PARTICIPATOR);
		}
		
		String bizLicenseStr = "";
		if (bizLicense == null) {
			bizLicense = new ArrayList<PROJECT_BIZ_LICENSE>();
			bizLicense.add(PROJECT_BIZ_LICENSE.AUTHORIZED);
			bizLicense.add(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
			bizLicense.add(PROJECT_BIZ_LICENSE.BINDING);
			bizLicense.add(PROJECT_BIZ_LICENSE.UNBINDING);
			bizLicenseStr = "0,1,2,3";
		}else if(bizLicense.size()==1){
			if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.AUTHORIZED){
				bizLicenseStr = "0";
			}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.NOT_AUTHORIZED){
				bizLicenseStr = "1";
			}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.BINDING){
				bizLicenseStr = "2";
			}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.UNBINDING){
				bizLicenseStr = "3";
			}
		}else{
			int i=0;
			for(PROJECT_BIZ_LICENSE biz:bizLicense){
				if(i==0){
					bizLicenseStr+=biz.ordinal();
				}else{
					bizLicenseStr+=","+biz.ordinal();
				}
				i++;
			}
		}
		
		String statusStr = "";
		if (status == null) {
			status = new ArrayList<PROJECT_STATUS>();
			status.add(PROJECT_STATUS.FINISHED);
			status.add(PROJECT_STATUS.ONGOING);
			statusStr = "0,1";
		} else if (status.size() == 1) {
			if (status.get(0) == PROJECT_STATUS.FINISHED) {
				statusStr = "0";
			} else if (status.get(0) == PROJECT_STATUS.ONGOING) {
				statusStr = "1";
			}
		} else {
			statusStr = "0,1";
		}
		String typeStr = "";
		if (type == null) {
			type = new ArrayList<PROJECT_TYPE>();
			type.add(PROJECT_TYPE.PERSONAL);
			type.add(PROJECT_TYPE.TEAM);
			typeStr = "0,1";
		} else if (type.size() == 1) {
			if (type.get(0) == PROJECT_TYPE.PERSONAL) {
				typeStr = "0";
			} else if (type.get(0) == PROJECT_TYPE.TEAM) {
				typeStr = "1";
			}
		} else {
			typeStr = "0,1";
		}

		final List<Project> pList = new ArrayList<Project>();
		List<Long> roleIds = new ArrayList<>();// 团队下的普通成员是不可以查看团队下项目的,所以下面的角色没有团队成员
		roleIds.add(Cache.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.CREATOR).getId());
		roleIds.add(Cache.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.ADMINISTRATOR).getId());
		List<Long> projectIds = new ArrayList<Long>();
		/**
		 * 查看是否总部成员
		 * 
		 */
		User loginUser = userDao.findOne(loginUserId);
		boolean isHQ=false;
		if(loginUser!=null){
			//FilialeInfo filialeInfo = filialeInfoDao.findOne(loginUser.getFilialeId());
			if(loginUser.getFilialeId()==1){
				//code为 11101 总部成员
				isHQ=true;
			}
		}
		if(!isHQ){ //如果不是总部成员
			// 我创建 我管理的团队
			List<Long> teamIds = this.teamMemberDao.findByUserIdAndRoleIdAndDel(loginUserId, roleIds, DELTYPE.NORMAL);
			log.info("search teamIds list:" + teamIds.toString());
			if (null != teamName && teamIds.size() > 0) {
				if (StringUtils.isNotBlank(teamName)) {
					teamIds = teamDao.findByIdInAndNameLikeAndDel(teamIds, "%" + teamName + "%", DELTYPE.NORMAL);
				}
			}
			// 团队下我创建或者参与的项目
			if (teamIds.size() > 0) {
				projectIds = projectDao.findByTeamIdInAndDel(teamIds, DELTYPE.NORMAL);
			}
			// 我创建,我参与的项目
			List<Long> projectIdsCrtAndAct = this.projectMemberDao.findByUserIdAndTypeIn(loginUserId, memberType);
			if (StringUtils.isNotBlank(teamName)) {
				projectIdsCrtAndAct = projectDao.findByIdInAndTeamNameLikeAndDel(projectIdsCrtAndAct, "%" + teamName + "%",
						DELTYPE.NORMAL);
			}
			projectIds.addAll(projectIdsCrtAndAct);
		}else{
			projectIds.clear();
		}
		

		// 项目主键去重
		Set<Long> set = new HashSet<>(projectIds);
		projectIds.clear();
		StringBuffer projectIdStr = new StringBuffer();
		Iterator<Long> it = set.iterator();
		while (it.hasNext()) {
			Long tmpPrjId = it.next();
			projectIds.add(tmpPrjId);
			projectIdStr.append(tmpPrjId).append(",");
		}

		// 项目id为空时 添加伪id
		if (null != projectIds && projectIds.isEmpty()) {
			projectIds.add(-99L);
			projectIdStr.append("p.id =-99");
		}else{
			projectIdStr.append("1=1");
			projectIdStr=new StringBuffer("p.id in("+projectIdStr+")");
			
		}
		if(isHQ){//如果总部成员
			projectIdStr.setLength(0);
			projectIdStr.append("1=1");
		}
		log.info("search project list:" + projectIdStr.toString());

		// 执行查询
		StringBuffer baseSql = new StringBuffer();
		baseSql.append(
				"select p.progress,p.id,p.name,p.detail,p.categoryId,p.createdAt,p.updatedAt,p.type,p.status,p.bizLicense"
						+ " ,p.teamId,p.bizCompanyId,p.bizCompanyName , t.endDate from T_PROJECT p  ")
				.append(" LEFT JOIN  ")
				.append(" (SELECT MAX(endDate) endDate,projectId FROM T_PROCESS WHERE del=0 GROUP BY projectId) t  ON p.id =t.projectId  ");
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" where ").append(projectIdStr).append(" and p.bizLicense in(")
				.append(bizLicenseStr).append(") ").append(" and p.status in(").append(statusStr).append(")")
				.append(" and p.type in ( ").append(typeStr).append(")").append(" and p.del=0 ");

		// 查询项目列表中第一个项目的projectId的sql语句
		StringBuffer firstSql = new StringBuffer();
		firstSql.append("select p.id from T_PROJECT p ").append(sbSql).append(" ORDER BY p.createdAt DESC limit 0,1");

		if (StringUtils.isNotBlank(projName)) {
			String formatProjName = Tools.sqlFormat(projName);
			sbSql.append(" and (p.name like '%").append(formatProjName).append("%' or p.pinYinHeadChar like '")
					.append(formatProjName).append("%' or p.pinYinName like '").append(formatProjName).append("%')");
		}
		if (StringUtils.isNotBlank(begin)) {
			sbSql.append(" and DATE_FORMAT(p.createdAt,'%Y-%m-%d')>= '").append(begin).append("'");
		}
		if (StringUtils.isNotBlank(end)) {
			sbSql.append(" and DATE_FORMAT(p.createdAt,'%Y-%m-%d')<='").append(end).append("'");
		}
		if (categoryId != null) {
			sbSql.append(" and p.categoryId in(").append(categoryIdsStr).append(")");
		}
		if (parentId != null) {
			sbSql.append(" and p.parentId =").append(parentId);
		}
		if (StringUtils.isNotBlank(creator)) {
			sbSql.append(" and p.id in( ")
					.append("select distinct pm.projectId from T_PROJECT_MEMBER pm where pm.type=")
					.append(PROJECT_MEMBER_TYPE.CREATOR.ordinal())
					.append(" and pm.del=0 and pm.userId in (select u.id from T_USER u where u.userName like '%")
					.append(creator).append("%')").append(")");
		}
		if (StringUtils.isNotBlank(actor)) {
			String actorRoleIds = "";
			actorRoleIds += Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR).getId();
			actorRoleIds += "," + Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.MEMBER).getId();
			actorRoleIds += "," + Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.OBSERVER).getId();

			sbSql.append(" and p.id in( ")
					.append("select distinct pm.projectId from T_PROJECT_MEMBER pm left join T_PROJECT_AUTH ta on ta.memberId=pm.id where ")
					.append("  pm.del=0 and ta.del=0 and pm.userId in (select u.id from T_USER u where u.userName like '%")
					.append(actor).append("%')").append(" and ta.roleId in ( ").append(actorRoleIds).append(")")
					.append(")");
		}
		if (StringUtils.isNotBlank(pfstime)) {
			sbSql.append(" and DATE_FORMAT(t.endDate,'%Y-%m-%d')>= '").append(pfstime).append("' ");
		}
		if (StringUtils.isNotBlank(pfetime)) {
			sbSql.append(" and DATE_FORMAT(t.endDate,'%Y-%m-%d')<='").append(pfetime).append("' ");
		}

		sbSql.append(" order by p.createdAt desc ");
		log.info("sbSql==>" + sbSql);
		log.info("baseSql==>" + baseSql);
		StringBuffer pageSql = new StringBuffer("");
		pageSql.append(" limit " + (pageNo - 1) * pageSize + ", " + pageSize);
		String totleSql = "select count(1) from T_PROJECT p LEFT JOIN  "
				+ " (SELECT MAX(endDate) endDate,projectId FROM T_PROCESS WHERE del=0 GROUP BY projectId) t  ON p.id =t.projectId "
				+ sbSql.toString();
		String lastSql = "SELECT * FROM (" + baseSql.toString() + sbSql.toString() + ") a"
				+ " LEFT JOIN ( select * from T_PROJECT_SORT where userId=" + loginUserId
				+ ") b ON a.id = b.projectId  " + "  ORDER BY b.sort DESC " + pageSql.toString();

		log.info("totleSql==>" + totleSql);
		log.info("lastSql==>" + lastSql);
		Long totle = jdbcTpl.queryForObject(totleSql, Long.class);
		Long totlePages = (totle - 1) / pageSize + 1;

		long projectListSearchTime = System.currentTimeMillis();
		log.info(String.format("search project time:[%s]ms", projectListSearchTime - beginSearchTime));

		this.jdbcTpl.query(lastSql, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Project vo = new Project();
				vo.setProgress(rs.getInt("progress"));
				vo.setId(rs.getLong("id"));
				vo.setName(rs.getString("name"));
				vo.setDetail(rs.getString("detail"));
				vo.setCategoryId(rs.getLong("categoryId"));
				int typeStr = rs.getInt("type");
				if (typeStr == PROJECT_TYPE.TEAM.ordinal()) {
					vo.setType(PROJECT_TYPE.TEAM);
				} else if (typeStr == PROJECT_TYPE.PERSONAL.ordinal()) {
					vo.setType(PROJECT_TYPE.PERSONAL);
				}
				int statusStr = rs.getInt("status");
				if (statusStr == PROJECT_STATUS.FINISHED.ordinal()) {
					vo.setStatus(PROJECT_STATUS.FINISHED);
				} else if (statusStr == PROJECT_STATUS.ONGOING.ordinal()) {
					vo.setStatus(PROJECT_STATUS.ONGOING);
				}
				int bizLicenseStr = rs.getInt("bizLicense");
				if (bizLicenseStr == PROJECT_BIZ_LICENSE.AUTHORIZED.ordinal()) {
					vo.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
				} else if (bizLicenseStr == PROJECT_BIZ_LICENSE.NOT_AUTHORIZED.ordinal()) {
					vo.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
				}else if(bizLicenseStr ==PROJECT_BIZ_LICENSE.BINDING.ordinal()){
					vo.setBizLicense(PROJECT_BIZ_LICENSE.BINDING);
				}else if(bizLicenseStr ==PROJECT_BIZ_LICENSE.UNBINDING.ordinal()){
					vo.setBizLicense(PROJECT_BIZ_LICENSE.UNBINDING);
				}

				vo.setTeamId(rs.getLong("teamId"));
				vo.setBizCompanyId(rs.getString("bizCompanyId"));
				vo.setBizCompanyName(rs.getString("bizCompanyName"));

				vo.setCreatedAt(rs.getTimestamp("createdAt"));
				vo.setUpdatedAt(rs.getTimestamp("updatedAt"));
				vo.setEndDate(rs.getString("endDate"));
				vo.setSort(rs.getLong("sort"));
				pList.add(vo);
			}
		});

		List<Map<String, Object>> projectMapList = new ArrayList<>();

		// 遍历记录进行扩展
		if (pList != null && pList.size() > 0) {
			for (Project p : pList) {
				// 增加分类数据
				ProjectCategory pc = Cache.getProjectCategory(p.getCategoryId());
				if (pc != null) {
					p.setCategoryName(pc.getName());
				}
				// 扩展团队信息
				Team team = teamDao.findOne(p.getTeamId());
				if (team != null) {
					p.setTeamName(team.getName());
				}
				ProjectParent parent = projectParentDao.findOne(p.getParentId());
				if (parent != null) {
					p.setParentName(parent.getProjectName());
				}
				User user = userDao.findOne(p.getCreatorId());
				if (user != null) {
					p.setCreator(user.getNickName());
					p.setUserName(user.getUserName());
				}
				// 项目列表要增加 项目进度，项目成员数量，创建者
				List<ProjectMember> listMember = projectMemberDao.findByProjectIdAndDel(p.getId(), DELTYPE.NORMAL);
				p.setMemberSum(listMember.size());
				List<Long> projId = new ArrayList<Long>();
				projId.add(p.getId());
				List<User> creatorUser = userDao.findCreatorForProjects(projId);
				if (null != creatorUser && creatorUser.size() > 0) {
					p.setCreator(creatorUser.get(0).getUserName());
				}
				// 增加bug总数，未关闭bug,task总数，未完成task
				StringBuffer taskBugTotalSql = new StringBuffer();
				taskBugTotalSql
						.append("select ifnull(t.taskTotal,0) as taskTotal,ifnull(t.taskNoFinishTotal,0) as taskNoFinishTotal,ifnull(b.bugTotal,0) as bugTotal,ifnull(b.bugNoCloseTotal,0) as bugNoCloseTotal from (select sum(taskTotal) as taskTotal,sum(taskNoFinishTotal) as taskNoFinishTotal from ")
						.append(" (SELECT count(1) AS taskTotal,sum(CASE WHEN STATUS = 0 THEN 1 ELSE 0 END) AS taskNoFinishTotal FROM T_TASK WHERE del = 0 AND processId ")
						.append("IN (select * from (SELECT id from T_PROCESS where del=0 and projectId=").append(p.getId())
						.append(") as xxx) and id not in (select t.id from T_TASK t  join T_TASK_LEAF tl on t.id=tl.topTaskId join T_PROCESS pc on pc.id=t.processId where t.del=0 and ")
						.append("tl.del=0 and pc.del=0 and pc.projectId=").append(p.getId())
						.append(") union all SELECT count(1) AS taskTotal,sum(CASE WHEN STATUS = 0 THEN 1 ELSE 0 END) AS taskNoFinishTotal FROM T_TASK_LEAF where del=0 and topTaskId in ")
						.append("(select t.id from T_TASK t  join T_TASK_LEAF tl on t.id=tl.topTaskId join T_PROCESS pc on pc.id=t.processId where t.del=0 and tl.del=0 and pc.del=0 and pc.projectId=")
						.append(p.getId()).append(" ")
						.append(")) as taskAndTaskLeaf) AS t JOIN (SELECT count(1) AS bugTotal,sum(case when status!=2 then 1 else 0 end) AS bugNoCloseTotal from T_BUG where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=")
						.append(p.getId()).append(")) as b");
				log.info("taskBugTotalSql=====>"+taskBugTotalSql.toString());
				Map<String, Object> taskAndBugMap = this.jdbcTpl.queryForMap(taskBugTotalSql.toString());
				p.setTaskTotal(taskAndBugMap.get("taskTotal").toString());
				p.setTaskNoFinishTotal(taskAndBugMap.get("taskNoFinishTotal").toString());
				p.setBugTotal(taskAndBugMap.get("bugTotal").toString());
				p.setBugNoCloseTotal(taskAndBugMap.get("bugNoCloseTotal").toString());
				Map<String, Object> pMap = new HashMap<>();
				pMap.put("object", p);
				pMap.put("permission", new ArrayList<>());
				projectMapList.add(pMap);

			}
		}

		// 获取所有项目中的第一个项目的projectId
		long firstProjectId = 0;
		List<Map<String, Object>> listMap = jdbcTpl.queryForList(firstSql.toString());
		if (null != listMap && listMap.size() > 0) {
			firstProjectId = Long.parseLong(listMap.get(0).get("id").toString());
		}
		long permissionsSearchTime = System.currentTimeMillis();
		log.info(String.format("search permissions time:[%s]	", permissionsSearchTime - projectListSearchTime));

		Map<String, Object> retMap = new HashMap<>();
		retMap.put("list", projectMapList);

		retMap.put("pageNo", pageNo);
		retMap.put("pageSize", pageSize);
		retMap.put("pageTotal", totlePages.intValue());
		retMap.put("total", totle.intValue());

		if (projectMapList != null && projectMapList.size() > 0) {
			Project project = (Project) projectMapList.get(0).get("object");
			long maxNum = this.getMaxProjectSort(loginUserId);
			if (maxNum == 0 && project.getSort() == 0 && project.getId() == firstProjectId) {
				// 刚开始进入初始化项目列表，如果项目列表中的项目没有进行过置顶操作，那么默认将项目列表中创建时间最大的那个项目默认显示置顶
				retMap.put("position", 1);
			} else {
				// 如果项目列表中的项目进行过置顶操作，那么如果项目sort是最大值，则显示置顶，否则显示不置顶
				if (project.getSort() > 0 && project.getSort() == maxNum) {
					retMap.put("position", 1);
				} else {
					retMap.put("position", 0);
				}
			}
		}
		return retMap;
	}

	public Map<String, Object> getProject(long projectId, long loginUserId) {
		Project p = projectDao.findOne(projectId);
		if (p == null) {
			return null;
		}
		/**
		 * 查看是否总部成员
		 * 
		 */
		User loginUser = userDao.findOne(loginUserId);
		boolean isHQ=false;
		if(loginUser!=null){
			//FilialeInfo filialeInfo = filialeInfoDao.findOne(loginUser.getFilialeId());
			if(loginUser.getFilialeId()==1){
				//code为 11101 总部成员
				isHQ=true;
			}
		}
		List<Permission> permissions = new ArrayList<Permission>();
		if(!isHQ){
			  permissions = this.getPermissionList(loginUserId, projectId);
			if(null==permissions || permissions.size()==0){
				return null;//判断如果没有权限,退回去
			}
		}
		
		ProjectCategory pc = Cache.getProjectCategory(p.getCategoryId());
		if (pc != null) {
			p.setCategoryName(pc.getName());
		}
		Team team = teamDao.findOne(p.getTeamId());
		if (team != null) {
			p.setTeamName(team.getName());
//			p.setBizCompanyId(team.getEnterpriseId());
//			p.setBizCompanyName(team.getEnterpriseName());
		}
		ProjectParent parent = projectParentDao.findOne(p.getParentId());
		if (parent != null) {
			p.setParentName(parent.getProjectName());
		}
		User user = userDao.findOne(p.getCreatorId());
		if (user != null) {
			p.setCreator(user.getNickName());
			p.setUserName(user.getUserName());
		}
		
		// 获取项目进度 人员数 待进行，进行中，已完成的任务数

		List<Task> tasks = this.findTaskByProjectId(projectId);
		int nofinished = 0, finished = 0;
		for (Task t : tasks) {
			if (t.getStatus().equals(Enums.TASK_STATUS.NOFINISHED)) {
				nofinished++;
			}
			if (t.getStatus().equals(Enums.TASK_STATUS.FINISHED)) {
				finished++;
			}
		}

		
		Map<String, Integer> permissionMap = new HashMap<>();
		if (permissions != null && permissions.size() > 0) {
			for (Permission permission : permissions) {
				permissionMap.put(permission.getEnName(), 1);
			}
		}

		if (p.getTeamId() != -1) {// 团队项目
			TeamMember teamMem = teamMemberService.findMemberByTeamIdAndMemberType(p.getTeamId(),
					TEAMREALTIONSHIP.CREATE);
			if (teamMem == null || teamMem.getUserId().longValue() != loginUserId) {// 不是团队的创建者
				permissionMap.remove("project_remove");// 去掉删除项目的权限
				permissionMap.remove("project_transfer");// 去掉转移项目的权限
				permissionMap.put("project_exit", 1);// 增加退出项目权限
			} else {// 如果是团队的创建者
				permissionMap.put("project_remove", 1);// 增加删除项目的权限
				permissionMap.put("project_transfer", 1);// 增加转移项目的权限
			}
			ProjectMember member = this.projectMemberDao.findOneByProjectIdAndUserIdAndDel(projectId, loginUserId,
					DELTYPE.NORMAL);
			if (null == member || null == member.getId()) {
				permissionMap.remove("project_exit");// 去掉退出项目权限
			}
		}

		Map<String, Object> pMap = new HashMap<>();
		pMap.put("object", p);
		pMap.put("permission", permissionMap);

		pMap.put("nofinished", nofinished);
		pMap.put("finished", finished);

		List<ProjectMember> listPm = this.findProjectMemberByProjectId(projectId);
		if (null == listPm || listPm.size() == 0) {
			pMap.put("totalUser", 0);
		} else {
			pMap.put("totalUser", listPm.size());
		}

		return pMap;
	}

	public Project getProject(long projectId) {
		Project p = projectDao.findOne(projectId);
		return p;
	}

	public Project addProject(Project p){
		if (p.getStatus() == null) {
			p.setStatus(PROJECT_STATUS.ONGOING);
		}
		if (p.getType() == null) {
			p.setType(PROJECT_TYPE.PERSONAL);
		}
		if (p.getBizLicense() == null) {
			p.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
		}
		Setting setting = (Setting) Cache.getObject("SETTING");

		if (p.getTeamId() != -1) {
			Team team = teamService.findOne(p.getTeamId());
			if (null != team && team.getType().equals(TEAMTYPE.ENTERPRISE)) {
				p.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
				p.setBizCompanyId(team.getEnterpriseId());
				p.setBizCompanyName(team.getEnterpriseName());
				p.setType(PROJECT_TYPE.TEAM);
			}
		}
		// 增加拼音字段
		p.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(p.getName() == null ? "" : p.getName()));
		p.setPinYinName(ChineseToEnglish.getPingYin(p.getName() == null ? "" : p.getName()));
		projectDao.save(p);

		for (int i = 0; i < 6; i++) {
			TaskGroup tg = new TaskGroup();
			tg.setCreatedAt(new Timestamp(System.currentTimeMillis()));
			tg.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			tg.setProjectId(p.getId());
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
			tg.setSort(i);
			tg.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(tg.getName() == null ? "" : tg.getName()));
			tg.setPinYinName(ChineseToEnglish.getPingYin(tg.getName() == null ? "" : tg.getName()));
			taskGroupDao.save(tg);
		}
		return p;
	}

	// 云平台创建项目,同时创建两个应用
	public Map<String, Object> addProjectForCloud(Project p, Long loginUserId) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		if (p.getName() != null && p.getName().length() > 1000) {
			throw new Exception("项目名称过长，不能超过1000个字符！");
		}
		p.setName("正益工作定制项目");
		p.setCategoryId(projectCategoryDao.findByNameAndDel("商务", DELTYPE.NORMAL).getId());
		p.setDetail(
				"正益工作由正益工场孵化的，专注于为企业提供内部移动化支撑的平台级产品，内含移动OA与移动CRM等企业管理模块。门户不仅为企业提供了如企业通讯录、IM等基线功能，还提供了如OA、CRM、工作管理、邮件等子应用，也包含运维所需的移动运行监控，应用接入管理等服务，为企业打造高效的移动办公体系。同时，平台还提供开放的接入服务，第三方应用可按照接入标准轻松接入门户，实现多系统的整合。");
		p.setStatus(PROJECT_STATUS.ONGOING);
		p.setType(PROJECT_TYPE.PERSONAL);
		p.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
		Setting setting = (Setting) Cache.getObject("SETTING");
		projectDao.save(p);
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_ADD, p.getId(), p.getName());
		ProjectMember member = new ProjectMember();
		member.setUserId(loginUserId);
		member.setProjectId(p.getId());
		member.setType(PROJECT_MEMBER_TYPE.CREATOR);
		User user = this.userService.findUserById(loginUserId);
		this.saveProjectMember(member, user);
		ProjectAuth auth = new ProjectAuth();
		auth.setMemberId(member.getId());
		auth.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR).getId());
		this.saveProjectAuth(auth);

		// 顺便创建两个应用
		// 第一个应用
		App app = new App();
		app.setAppType(0l);
		app.setName("EPortal应用");
		app.setDetail("集通讯录、IM、应用中心、消息中心为一体");
		app.setUserId(loginUserId);
		app.setForbidPub("yes");// 标识此应用禁止发布到应用管理平台
		app.setSpecialAppCanAppId("EPortal");
		app.setSpecialAppCanAppKey("0647513c-88f1-46c9-b764-b38e19f0e4e6");
		app.setSourceGitRepo(ePortalGitRepoPath);
		app.setProjectId(p.getId());
		this.appService.addApp(app, loginUserId);
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_ADD, p.getId(), new Object[] { app });

		// 第二个应用
		App app1 = new App();
		app1.setAppType(0l);
		app1.setName("移动OA应用");
		app1.setDetail("移动OA应用包含工作目标、任务; 工作报告、计划；线索、机会、客户、联系人；工单审批；考勤；企业公告；规章制度； 企业CIS；日程管理。");
		app.setSourceGitRepo(oaGitRepoPath);
		app1.setUserId(loginUserId);
		app1.setProjectId(p.getId());
		this.appService.addApp(app1, loginUserId);
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_ADD, p.getId(), new Object[] { app1 });
		List<Long> appIds = new ArrayList<Long>();
		appIds.add(app.getId());
		appIds.add(app1.getId());
		map.put("projectId", p.getId());
		map.put("appIds", appIds);
		return map;
	}

	// 云平台创建两个应用,为某个项目
	public List<Long> addTwoAppForCloud(Long projectId, Long loginUserId) throws Exception {

		// 第一个应用
		App app = new App();
		app.setAppType(0l);
		app.setName("EPortal应用");
		app.setDetail("集通讯录、IM、应用中心、消息中心为一体");
		app.setUserId(loginUserId);
		app.setForbidPub("yes");// 标识此应用禁止发布到应用管理平台
		app.setSpecialAppCanAppId("EPortal");
		app.setSpecialAppCanAppKey("0647513c-88f1-46c9-b764-b38e19f0e4e6");
		app.setSourceGitRepo(ePortalGitRepoPath);
		app.setProjectId(projectId);
		this.appService.addApp(app, loginUserId);
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_ADD, projectId, new Object[] { app });

		// 第二个应用
		App app1 = new App();
		app1.setAppType(0l);
		app1.setName("移动OA应用");
		app1.setDetail("移动OA应用包含工作目标、任务; 工作报告、计划；线索、机会、客户、联系人;工单审批;考勤；企业公告；规章制度; 企业CIS;日程管理。");
		app.setSourceGitRepo(oaGitRepoPath);
		app1.setUserId(loginUserId);
		app1.setProjectId(projectId);
		this.appService.addApp(app1, loginUserId);
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_ADD, projectId, new Object[] { app1 });

		List<Long> result = new ArrayList<Long>();
		result.add(app.getId());
		result.add(app1.getId());
		return result;
	}

	public int editProject(Project updatable, long loginUserId) throws Exception {
		// 查询变更前的项目 -> 用于存储动态
		Project originalProject = projectDao.findOne(updatable.getId());
		if (originalProject == null) {
			return 0;
		}

		if (updatable.getName() != null && updatable.getName().length() > 1000) {
			throw new Exception("项目名称过长，不能超过1000个字符！");
		}

		String setting = "";
		if (updatable.getName() != null) {
			setting += String.format(",name='%s',pinYinHeadChar='%s',pinYinName='%s'", updatable.getName(),
					ChineseToEnglish.getPinYinHeadChar(updatable.getName() == null ? "" : updatable.getName()),
					ChineseToEnglish.getPingYin(updatable.getName() == null ? ""
							: updatable.getName()));
		}
		if (updatable.getDetail() != null) {
			setting += String.format(",detail='%s'", updatable.getDetail());
		}
		if (updatable.getStatus() != null) {
			setting += String.format(",status=%d", updatable.getStatus().ordinal());
		}
		if (updatable.getType() != null) {
			setting += String.format(",type=%d", updatable.getType().ordinal());
		}
		if (updatable.getTeamId() != -1) {
			setting += String.format(",teamId=%d", updatable.getTeamId());
		}
		if (updatable.getCategoryId() != -1) {
			setting += String.format(",categoryId=%d", updatable.getCategoryId());
		}
		if (updatable.getBizCompanyId() != null) {
			setting += String.format(",bizCompanyId='%s'", updatable.getBizCompanyId());
		}
		if (updatable.getBizLicense() != null) {
			setting += String.format(",bizLicense=%d", updatable.getBizLicense().ordinal());
			if (updatable.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)) {
				setting += ",bizCompanyId=null,bizCompanyName=null";
			} else if (updatable.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)) {
				setting += String.format(",bizCompanyId='%s',bizCompanyName='%s'", updatable.getBizCompanyId(),
						updatable.getBizCompanyName());
			}
		}
		if (updatable.getProductionEMMUrl() != null) {
			setting += String.format(",productionEMMUrl='%s'", updatable.getProductionEMMUrl());
		}
		if (updatable.getTestingEMMUrl() != null) {
			setting += String.format(",testingEMMUrl='%s'", updatable.getTestingEMMUrl());
		}

		if (setting.length() > 0) {
			setting = setting.substring(1);
			String sql = "update T_PROJECT set " + setting + " where id=" + updatable.getId();
			int affected = this.jdbcTpl.update(sql);
			// 大众版、个人项目、已绑定企业编辑的时候调用EMM的方法
			if ("online".equals(serviceFlag)) {
				if (!this.isTeamBind(updatable.getTeamId())
					&& (updatable.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
					||updatable.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))) {
					
					ProjectMember projectmember = projectMemberService.findMemberByProjectIdAndMemberType(updatable.getId(), PROJECT_MEMBER_TYPE.CREATOR);
					User user = userDao.findOne(projectmember.getUserId());
					List<NameValuePair> parameters = new ArrayList<>();
					parameters.add(new BasicNameValuePair("creator", user.getAccount()));
					parameters.add(new BasicNameValuePair("teamName", updatable.getName()));
					parameters.add(new BasicNameValuePair("teamDesc", updatable.getDetail()));
					parameters.add(new BasicNameValuePair("tenantId", updatable.getBizCompanyId()));
					parameters.add(new BasicNameValuePair("entFullName", updatable.getBizCompanyName()));
					parameters.add(new BasicNameValuePair("teamId", updatable.getUuid()));
					parameters.add(new BasicNameValuePair("teamType",updatable.getType().name().equals("TEAM") ? "teamProject" : "personnelProject"));
					log.info("updateProjectInfo_params-->" + parameters.toString());
					String resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/updateTeamInfo", parameters);
					log.info("updateEMMProjectInfo-->" + resultStr);
					JSONObject jsonObject = JSONObject.fromObject(resultStr);
					if (jsonObject.get("returnCode").equals("200")) {// 如果修改团队的时候提示团队信息不存在,则再次去调用绑定的接口
						log.info("project update info to emm success!");
					} else if (jsonObject.get("returnCode").equals("500")
							&& jsonObject.getString("returnMessage").contains("select projectInfo is null")) {
						log.info("project bind again for project info .");
						parameters = new ArrayList<>();
						parameters.add(new BasicNameValuePair("tenantId", updatable.getBizCompanyId()));// 企业简称
						parameters.add(new BasicNameValuePair("entFullName", updatable.getBizCompanyName()));
						parameters.add(new BasicNameValuePair("teamId", updatable.getUuid()));
						parameters.add(new BasicNameValuePair("teamType",
								updatable.getType().name().equals("TEAM") ? "teamProject" : "personnelProject"));
						log.info("updateProjectInfo_params-->" + parameters.toString());
						resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/updateTeamInfo", parameters);
						log.info("updateEMMTeamInfo-->" + resultStr);
						jsonObject = JSONObject.fromObject(resultStr);
						if (jsonObject.get("returnCode").equals("200")) {// 如果修改团队的时候提示团队信息不存在,则再次去调用绑定的接口
							log.info("project update info to emm success!");
						} else if (jsonObject.get("returnCode").equals("500")
								&& jsonObject.getString("returnMessage").contains("select projectInfo is null")) {
							log.info("project bind again for project info .");
							parameters = new ArrayList<>();
							parameters.add(new BasicNameValuePair("tenantId", updatable.getBizCompanyId()));// 企业简称
							parameters.add(new BasicNameValuePair("entFullName", updatable.getBizCompanyName()));
							parameters.add(new BasicNameValuePair("teamId", updatable.getUuid()));
							parameters.add(new BasicNameValuePair("teamName", updatable.getName()));
							parameters.add(new BasicNameValuePair("teamDesc", updatable.getDetail()));
							parameters.add(new BasicNameValuePair("creator", user.getAccount()));
							parameters.add(new BasicNameValuePair("domainUrl", xietongHost));
							parameters.add(new BasicNameValuePair("name", user.getUserName()));// 管理员名字
							parameters.add(new BasicNameValuePair("telephone", user.getCellphone()));// 管理员电话
							parameters.add(new BasicNameValuePair("teamType",
									updatable.getType().name().equals("TEAM") ? "teamProject" : "personnelProject"));// 绑定类型（团队：null，团队项目teamProject，个人项目personnelProject）

							log.info("bindAuthGroup--> tenantId:" + updatable.getBizCompanyId() + ",entFullName:"
									+ updatable.getBizCompanyName() + ",teamId:" + updatable.getUuid() + ",teamName:"
									+ updatable.getName() + ",teamDesc:" + updatable.getDetail() + ",creator:"
									+ user.getAccount() + ",domainUrl:" + xietongHost);
							resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/bindAuthGroup", parameters);
							log.info("bindAuthGroup-->" + resultStr);
							jsonObject = JSONObject.fromObject(resultStr);
							if (!jsonObject.getString("returnCode").equals("200")) {
								throw new RuntimeException("调用EMM修改项目信息失败");
							}
						} else {
							throw new RuntimeException("调用EMM修改项目信息失败");
						}
					}
				}
			}

			// 添加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_EDIT, originalProject.getId(),
					originalProject.getName());

			// 添加通知
			if (updatable.getName() != null && !updatable.getName().equals(originalProject.getName())) {
				User user = userDao.findOne(loginUserId);
				List<Long> members = projectMemberDao.findByProjectIdAndUserIdNotAndDel(originalProject.getId(),
						loginUserId, DELTYPE.NORMAL);
				Set<Long> ids = new HashSet<Long>();
				ids.addAll(members);
				this.noticeService.addNotice(loginUserId, ids.toArray(new Long[] {}),
						NOTICE_MODULE_TYPE.PROJECT_UPDATE_NAME, new Object[] { user, originalProject, updatable });
			}

			return affected;

		} else {
			return 0;
		}
	}

	// 如果token为空的话,则不调用EMM接口删除对于的emm信息
	public void removeProject(long projectId, String token) throws ClientProtocolException, IOException {
		Project p = this.projectDao.findOne(projectId);
		p.setDel(DELTYPE.DELETED);
		projectDao.save(p);

		List<ProjectMember> listMem = projectMemberDao.findByProjectIdAndDel(p.getId(), DELTYPE.NORMAL);
		if (null != listMem && listMem.size() > 0) {
			for (ProjectMember pm : listMem) {
				pm.setDel(DELTYPE.DELETED);
				projectMemberDao.save(pm);
				List<ProjectAuth> listAuth = projectAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);
				if (null != listAuth && listAuth.size() > 0) {
					for (ProjectAuth pa : listAuth) {
						pa.setDel(DELTYPE.DELETED);
						projectAuthDao.save(pa);
					}
				}
			}
		}

		this.documentDao.delete(this.documentDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL));
		this.resourcesDao.delete(this.resourcesDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL));
		List<Process> processes = this.processDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
		for (Process process : processes) {
			// 删除子任务
			this.taskLeafDao.delete(this.taskLeafDao.findByProcessIdAndDel(process.getId(), DELTYPE.NORMAL));
			// 删除项目下的任务
			this.taskDao.delete(this.taskDao.findByProcessIdAndDel(process.getId(), DELTYPE.NORMAL));
			// 删除bug
			this.bugDao.delete(this.bugDao.findByProcessIdAndDel(process.getId(), DELTYPE.NORMAL));
		}
		this.processDao.delete(this.processDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL));
		this.topicDao.delete(this.topicDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL));
		this.pluginDao.delete(this.pluginDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL));
		this.engineDao.delete(this.engineDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL));

		// 如果是删除绑定下的项目
		if(serviceFlag.equals("online")||serviceFlag.equals("enterprise")){
			if (this.isTeamBind(p.getTeamId())&&(p.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED) || p.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))) {
				for (ProjectMember pm : listMem) {
					if (null != token) {
						this.teamMemberService.deleteEmmUser(p.getTeamId(), pm.getUserId(), token);
					}
				}
			}
			if (!this.isTeamBind(p.getTeamId())&&(p.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED) || p.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))) {
				for (ProjectMember pm : listMem) {
					if (null != token) {
						this.deleteEmmUser(p.getId(), pm.getUserId(), token);
					}
				}
			}
		}

		// 删除项目下的应用
		List<App> listApp = appDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
		if (null != listApp && listApp.size() > 0) {
			for (App app : listApp) {
				GitAuthVO vo = new GitAuthVO();
				vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
				String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
				vo.setProject(encodeKey);
				vo.setProjectid(app.getAppcanAppId());
				listAuth.add(vo);
//				app.setDel(DELTYPE.DELETED);
//				appDao.save(app);
			}
			Map<String, String> map = appService.delAllGitRepo(listAuth);
			log.info("delall app gitRepo-->" + (null == map ? null : map.toString()));
		}
		
		// ------------------ 调用EMM接口删除项目-----------------
//		if ("online".equals(serviceFlag)) {
//			if (!this.isTeamBind(p.getTeamId()) && p.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)) {
//				// 调用EMM接口删除对于的信息
//				List<NameValuePair> parameters = new ArrayList<>();
//				parameters.add(new BasicNameValuePair("teamId", p.getUuid()));
//				parameters.add(new BasicNameValuePair("tenantId", p.getBizCompanyId()));
//				parameters.add(new BasicNameValuePair("entFullName", p.getBizCompanyName()));
//
//				log.info("delete bindTeam parameters-->" + parameters.toString());
//				String resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/deleteEntBindTeam", parameters);
//				log.info("delete bindTeam-->" + resultStr);
//				JSONObject jsonObject = JSONObject.fromObject(resultStr);
//				if (!jsonObject.get("returnCode").equals("200")) {
//					throw new RuntimeException(jsonObject.getString("returnMessage"));
//				}
//			}
//		}
	}

	/**
	 * 项目转让
	 * 
	 * @param projectId
	 * @param loginUserId
	 * @param targetUserId
	 * @return
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public int transferProject(long projectId, long loginUserId, long targetUserId)
			throws ClientProtocolException, IOException {

		// 1. find target member
		ProjectMember targetMember = projectMemberDao.findOneByProjectIdAndUserIdAndDel(projectId, targetUserId,
				DELTYPE.NORMAL);
		List<ProjectAuth> targetAuthArr = projectAuthDao.findByMemberIdAndDel(targetMember.getId(), DELTYPE.NORMAL);

		if (targetMember != null && targetMember.getProjectId() == projectId) {
			// 2. find current owner
			// List<ProjectMember> owners =
			// projectMemberDao.findByProjectIdAndUserIdAndDel(projectId,
			// loginUserId, DELTYPE.NORMAL);
			// 如果是团队项目,当前loginUserId为团队创建者,但是他未必参与到项目当中,所以查找项目拥有者不能用上面的方法,需要修改为以下
			ProjectMember ownerMember = projectMemberDao.findByProjectIdAndTypeAndDel(projectId,
					PROJECT_MEMBER_TYPE.CREATOR, DELTYPE.NORMAL);
			if (ownerMember != null) {
				List<ProjectAuth> ownerAuthArr = projectAuthDao.findByMemberIdAndDel(ownerMember.getId(),
						DELTYPE.NORMAL);

				// 3. proceed transfer
				ownerMember.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
				targetMember.setType(PROJECT_MEMBER_TYPE.CREATOR);
				boolean hasMemberRoleId = false; // 标识项目创建者是否拥有管理员的权限
				for (ProjectAuth auth : ownerAuthArr) {
					if (auth.getRoleId() == Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR).getId()) {// 如果权限是创建者权限,将改权限转给接收人
						auth.setMemberId(targetMember.getId());
					}
					if (auth.getRoleId() == Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR)
							.getId()) {
						hasMemberRoleId = true;
					}
				}
				if (!hasMemberRoleId) {// 项目创建者没有项目管理员的权限,转让之后应该让他变成项目管理员
					ProjectAuth pa = new ProjectAuth();
					pa.setMemberId(ownerMember.getId());
					pa.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR).getId());
					this.projectAuthDao.save(pa);
				}

				// 4. save changes
				List<ProjectMember> members = new ArrayList<ProjectMember>();
				members.add(ownerMember);
				members.add(targetMember);

				projectMemberDao.save(members);

				ownerAuthArr.addAll(targetAuthArr);
				projectAuthDao.save(ownerAuthArr);

				// 由于原先targetUser有退出此项目的权限(原来是普通成员),所以需要将targetUser的普通成员权限给去掉
				ProjectMember updateMember = projectMemberDao.findOneByProjectIdAndUserIdAndDel(projectId, targetUserId,
						DELTYPE.NORMAL);
				List<ProjectAuth> updateAuthArr = projectAuthDao.findByMemberIdAndDel(updateMember.getId(),
						DELTYPE.NORMAL);
				for (ProjectAuth auth : updateAuthArr) {
					if (auth.getRoleId() == Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.MEMBER).getId()
							.longValue()) {
						auth.setDel(DELTYPE.DELETED);
						projectAuthDao.save(auth);
					}
					if (auth.getRoleId() == Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR).getId()
							.longValue()) {
						auth.setDel(DELTYPE.DELETED);
						projectAuthDao.save(auth);
					}
				}
				Project project = projectDao.findOne(projectId);
				User receiveTeamUser = userDao.findOne(targetUserId);

				// 调用EMM接口删除对于的信息
				
				if ("online".equals(serviceFlag)) {
					// 判断项目是不是团队项目
					if (!this.isTeamBind(project.getTeamId())&&(project.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)||project.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))) {
						List<NameValuePair> parameters = new ArrayList<>();
						parameters.add(new BasicNameValuePair("teamId", project.getUuid()));
						parameters.add(new BasicNameValuePair("tenantId", project.getBizCompanyId()));
						parameters.add(new BasicNameValuePair("entFullName", project.getBizCompanyName()));
						parameters.add(new BasicNameValuePair("handOverEmail", receiveTeamUser.getAccount()));
						log.info("handOverTeam  parameters-->" + parameters.toString());
						String resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/handOverTeam", parameters);
						log.info("handOverTeam-->" + resultStr);
						JSONObject jsonObject = JSONObject.fromObject(resultStr);
						if (!jsonObject.get("returnCode").equals("200")) {
							throw new RuntimeException(jsonObject.getString("returnMessage"));
						}
					}
					return 1;

				}

			}

		}
		return 0;
	}

	/**
	 * 如果团队管理员退出了自己创建的团队项目,则将此项目转给团队创建者 如果团队创建者退出了自己创建的团队项目()
	 * 
	 * @user jingjian.wu
	 * @date 2015年12月17日 下午7:53:24
	 */

	public int updateToQuitProject(long projectId, long loginUserId) {
		log.info(String.format("related projectId[%d] loginUserId[%d] taskMemberType[%d]", projectId, loginUserId,
				Cache.getRole(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MANAGER).getId()));

		ProjectMember member = projectMemberDao.findOneByProjectIdAndUserIdAndDel(projectId, loginUserId,
				DELTYPE.NORMAL);
		long countTask = this.taskDao.countByProjectIdAndUserIdAndDel(loginUserId, DELTYPE.NORMAL.ordinal(),
				DELTYPE.NORMAL.ordinal(), projectId, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal());
		long countProcess = this.processMemberDao.countByProjectIdAndUserIdAndDel(projectId,
				Cache.getRole(ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MANAGER).getId(), loginUserId, DELTYPE.NORMAL,
				DELTYPE.NORMAL, DELTYPE.NORMAL);

		log.info(String.format("related projectId[%d] countTask[%d] countProcess[%d]", projectId, countTask,
				countProcess));

		if (member != null && countTask == 0 && countProcess == 0) {

			Project project = projectDao.findOne(member.getProjectId());
			// 如果是个人项目,项目创建者是不可以退出的,但是如果是团队项目,项目的创建者是可以退出的.项目创建者退出之后,该项目的创建者要改为团队创建者
			if (member.getType().equals(PROJECT_MEMBER_TYPE.CREATOR)
					&& project.getType().equals(PROJECT_TYPE.PERSONAL)) {// 如果退出的人是个人项目的创建者
				return 0;// 个人项目的创建者不可以退出项目
			}
			member.setDel(DELTYPE.DELETED);
			projectMemberDao.save(member);

			if (member.getType().equals(PROJECT_MEMBER_TYPE.CREATOR) && project.getType().equals(PROJECT_TYPE.TEAM)) {// 如果退出的人是团队项目的创建者
				Team team = teamDao.findOne(project.getTeamId());
				List<TeamMember> listMemb = teamMemberDao.findByTeamIdAndTypeAndDel(team.getId(),
						TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
				TeamMember memberCreator = listMemb.get(0);// 团队创建者
				if (member.getUserId() == memberCreator.getUserId().longValue()) {// 退出项目的人,正好是所在团队的创建者
					member.setDel(DELTYPE.NORMAL);// 因为上面删除了,所以这里需要再改回来
					projectMemberDao.save(member);
					return 0;// 不需要更改git权限,团队创建者不可以退出自己创建的团队项目,不然此项目的创建者不知道给谁了.
				}
				// 接下来需要将项目创建者的身份转给团队创建者,并且如果团队创建者以前就参与此项目,则需要将以前在此项目成员的信息删除掉
				// 1.找到团队创建者在项目中参与的成员
				List<ProjectMember> teamCreatorInProjectMember = projectMemberDao
						.findByProjectIdAndUserIdAndDel(projectId, memberCreator.getUserId(), DELTYPE.NORMAL);
				if (null != teamCreatorInProjectMember && teamCreatorInProjectMember.size() > 0) {
					for (ProjectMember pm : teamCreatorInProjectMember) {
						pm.setDel(DELTYPE.DELETED);
						projectMemberDao.save(pm);

						List<ProjectAuth> listAuths1 = projectAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);
						if (null != listAuths1 && listAuths1.size() > 0) {
							for (ProjectAuth pa : listAuths1) {
								pa.setDel(DELTYPE.DELETED);
								projectAuthDao.save(pa);
							}
						}
					}
				}

				member.setDel(DELTYPE.NORMAL);// 将上面删除的团队成员,改回来
				member.setUserId(memberCreator.getUserId());// 将此项目创建者换为团队创建者

			}

			// 不是团队项目，退出得删除权限；不是团队项目创建者退出项目，得删除权限
			if (!project.getType().equals(PROJECT_TYPE.TEAM) || !member.getType().equals(PROJECT_MEMBER_TYPE.CREATOR)) {
				List<ProjectAuth> listAuths = projectAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
				if (null != listAuths && listAuths.size() > 0) {
					for (ProjectAuth pa : listAuths) {
						pa.setDel(DELTYPE.DELETED);
						projectAuthDao.save(pa);
					}
				}
			}
			// 如果退出项目的这个人
			// ---------------------------------------git权限---------------------------------------
			boolean deleteGitAuth = true;

			if (project.getType().equals(PROJECT_TYPE.TEAM)) {
				TeamMember tm = teamMemberDao.findByTeamIdAndUserIdAndDel(project.getTeamId(), loginUserId,
						DELTYPE.NORMAL);
				if (null != tm) {
					TeamAuth tah = teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
					Role role = Cache.getRole(tah.getRoleId());
					List<Permission> listPer = role.getPermissions();
					if (null != listPer && listPer.size() > 0) {
						for (Permission p : listPer) {
							if (p.getEnName().equals("code_upload_master_code")
									|| p.getEnName().equals("code_update_branch")) {
								deleteGitAuth = false;
								break;
							}
						}
					}
					/*
					 * Role creatorRole =
					 * Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
					 * Role mgrRole =
					 * Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE
					 * .ADMINISTRATOR);
					 * if(tah.getRoleId()==creatorRole.getId().longValue() ||
					 * tah.getRoleId()==mgrRole.getId().longValue()){
					 * //是团队创建者或者团队管理员 //不需要删除git权限 deleteGitAuth = false; }
					 */
				}
				if (deleteGitAuth) {
					// 删除对应的git权限
					List<App> listApp = appDao.findByProjectIdAndDel(project.getId(), DELTYPE.NORMAL);
					User user = userDao.findOne(loginUserId);
					List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
					List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
					for (App app : listApp) {
						if (app.getUserId() != loginUserId) {
							GitAuthVO vo = new GitAuthVO();
							// vo.setAuthflag("all");
							vo.setPartnername(user.getAccount());
							vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
							vo.setProject(encodeKey.toLowerCase());
							// vo.setRef("master");
							vo.setProjectid(app.getAppcanAppId());
							listAuth.add(vo);
						} else {
							GitOwnerAuthVO vo = new GitOwnerAuthVO();
							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
							vo.setProject(encodeKey.toLowerCase());
							vo.setUsername(user.getAccount());
							User other = new User();// 应用要转给谁
							if (project.getType().equals(PROJECT_TYPE.TEAM)) {
								List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(),
										TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
								if (null != listTm && listTm.size() > 0) {
									other = userDao.findOne(listTm.get(0).getUserId());
								}
							} else {
								ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(),
										PROJECT_MEMBER_TYPE.CREATOR, DELTYPE.NORMAL);
								if (null != pmCrt) {
									other = userDao.findOne(pmCrt.getUserId());
								}
							}
							vo.setOther(other.getAccount());
							vo.setProjectid(app.getAppcanAppId());
							app.setUserId(other.getId());
							appDao.save(app);
							changeOwnerAuth.add(vo);
						}
					}
					// 删除git权限(项目下的应用不是被删除人创建的应用)
					Map<String, String> map = appService.delGitAuth(listAuth);
					log.info(user.getAccount() + " quit project->projectId:" + projectId + " ,and delGitAuth->"
							+ (null == map ? null : map.toString()));
					// 转让git权限(项目下的应用是被删除人创建的应用)
					map = appService.updateGitAuth(changeOwnerAuth);
					log.info(user.getAccount() + " quit project->projectId:" + projectId + " ,and updateGitAuth->"
							+ (null == map ? null : map.toString()));
				}
			}
			// ---------------------------------------git权限---------------------------------------
			return 1;
		}
		if (countTask != 0) {
			// "你有负责的任务，请更换任务负责人后，再退出项目！";
			return -1;
		} else if (countProcess != 0) {
			// "你有负责的流程，请更换流程负责人后，再退出项目！";
			return -2;
		}
		// "退出项目失败！";
		return 0;

	}

	public HashMap<String, Object> getProjectMemberList(long projectId, List<PROJECT_MEMBER_TYPE> typeList,
			String queryName, String queryAccount, Pageable pageable) {

		Page<ProjectMember> pagelist = null;
		if (typeList != null && typeList.size() > 0) {
			pagelist = projectMemberDao.findByProjectIdAndTypeInAndDel(projectId, typeList, DELTYPE.NORMAL,
					"%" + queryName + "%", "%" + queryAccount + "%", pageable);
		} else {
			pagelist = projectMemberDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL, "%" + queryName + "%",
					"%" + queryAccount + "%", pageable);
		}

		List<ProjectMember> retList = new ArrayList<>();

		// Project project = projectDao.findOne(projectId);

		for (ProjectMember m : pagelist.getContent()) {
			User u = userDao.findOne(m.getUserId());
			if (u != null) {
				if (queryName != null) {
					if (!u.getUserName().contains(queryName))
						continue;
				}
				if (queryAccount != null) {
					if (!u.getAccount().contains(queryAccount))
						continue;
				}

				String name = u.getUserName() != null ? u.getUserName() : u.getAccount();

				m.setUserName(name);
				m.setUserIcon(u.getIcon() == null ? userIcon : u.getIcon());
				m.setUserAccount(u.getAccount());
				retList.add(m);
			}
			// if(project != null) {
			// m.setProjectType(project.getType());
			// }
		}

		HashMap<String, Object> map = new HashMap<>();
		map.put("list", retList);
		map.put("total", pagelist.getTotalElements());

		return map;

	}

	@SuppressWarnings("deprecation")
	public HashMap<String, Object> getProjectMemberList(long projectId, List<PROJECT_MEMBER_TYPE> typeList,
			String keyWords, List<Long> exceptUserIds, Pageable pageable, long loginUserId, String type) {
		List<USER_STATUS> status = new ArrayList<USER_STATUS>();
		status.add(USER_STATUS.NORMAL);
		if (type != null && type.equals("memberList")) {
			status.add(USER_STATUS.FORBIDDEN);
		}
		//ios机器自动生成单引号，过滤单引号
		keyWords=keyWords.replaceAll("'", "");
		Page<ProjectMember> pagelist = null;
		if (typeList != null && typeList.size() > 0) {
			if (null != exceptUserIds && exceptUserIds.size() > 0) {
				pagelist = projectMemberDao.findByProjectIdAndTypeInAndDelAndUserIdNotIn(projectId, typeList,
						DELTYPE.NORMAL, "%" + keyWords + "%", exceptUserIds, status, pageable);
			} else {
				pagelist = projectMemberDao.findByProjectIdAndTypeInAndDel(projectId, typeList, DELTYPE.NORMAL,
						"%" + keyWords + "%", status, pageable);
			}
		} else {
			if (null != exceptUserIds && exceptUserIds.size() > 0) {
				pagelist = projectMemberDao.findByProjectIdAndDelAndUserIdNotIn(projectId, DELTYPE.NORMAL,
						"%" + keyWords + "%", exceptUserIds, status, pageable);
			} else {
				pagelist = projectMemberDao.findByProjectIdAndDel1(projectId, DELTYPE.NORMAL, "%" + keyWords + "%",
						status, pageable);
			}
		}
        
		List<ProjectMember> retList = new ArrayList<>();

		// Project project = projectDao.findOne(projectId);
		//邀请的总人数
		StringBuffer inviteSql=new StringBuffer();
		inviteSql.append("select sum(case when type!=").append(PROJECT_MEMBER_TYPE.INVITEE.ordinal()).append(" then 1 else 0 end) as invitedTotal,sum(case when type=").append(PROJECT_MEMBER_TYPE.INVITEE.ordinal()).append(" then 1 else 0 end) as invitingTotal  from T_PROJECT_MEMBER where projectId=").append(projectId).append(" and del=0");
		Map<String,Object> invitemMap=this.jdbcTpl.queryForMap(inviteSql.toString());
		int invitedTotal=Integer.parseInt(invitemMap.get("invitedTotal").toString());
		int invitingTotal=Integer.parseInt(invitemMap.get("invitingTotal").toString());
		for (ProjectMember m : pagelist.getContent()) {
			User u = userDao.findOne(m.getUserId());
			if (u != null) {
				String name = u.getUserName() != null ? u.getUserName() : u.getAccount();

				m.setUserName(name);
				m.setUserIcon(u.getIcon() == null ? userIcon : u.getIcon());
				m.setUserAccount(u.getAccount());
				m.setUserStatus(u.getStatus());
				List<Role> roleList = new ArrayList<>();
				List<ProjectAuth> authes = projectAuthDao.findByMemberIdAndDel(m.getId(), DELTYPE.NORMAL);
				if (authes != null && authes.size() > 0) {
					for (ProjectAuth auth : authes) {
						Role r = Cache.getRole(auth.getRoleId());
						if (r != null) {
							roleList.add(r);
						}
					}
				}

				m.setRole(roleList);

				retList.add(m);
			}
			// if(project != null) {
			// m.setProjectType(project.getType());
			// }
		}
		boolean creatorOrAdministrator = false;// 标识是否有权限对某个成员进行交接工作(删除某个人)
		ProjectMember pm = projectMemberDao.findOneByProjectIdAndUserIdAndDel(projectId, loginUserId, DELTYPE.NORMAL);
		if (pm != null) {
			List<ProjectAuth> pa = projectAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);
			for (ProjectAuth paSingle : pa) {
				Role r = Cache.getRole(paSingle.getRoleId());
				if (r.getEnName().equals(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR)
						|| r.getEnName().equals(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR)) {
					creatorOrAdministrator = true;
					break;
				}
			}
		}
		if (!creatorOrAdministrator) {
			Project p = projectDao.findOne(projectId);
			TeamMember tm = teamMemberDao.findByTeamIdAndUserIdAndDel(p.getTeamId(), loginUserId, DELTYPE.NORMAL);
			if (tm != null) {
				TeamAuth ta = teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
				Role r = Cache.getRole(ta.getRoleId());
				if (r.getEnName().equals(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.ADMINISTRATOR)
						|| r.getEnName().equals(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.CREATOR)) {
					creatorOrAdministrator = true;
				}
			}
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("list", retList);
		map.put("total", pagelist.getTotalElements());
		map.put("invitedTotal", invitedTotal);
		map.put("invitingTotal", invitingTotal);
		// 用于前台标示:是否显示交接工作的按钮,只有创建者和管理员才会显示交接工作的按钮.
		map.put("creatorOrAdministrator", creatorOrAdministrator);
		return map;

	}

	/**
	 * 获取具有指定许可的项目成员<br>
	 * 如果是团队项目,增加了团队中有对应权限的人,有一部分人只有userAccount,没有ID，使用此方法时候要注意<br>
	 * 
	 * Used by :<br>
	 * 创建
	 * 
	 * @param projectId
	 * @param required
	 * @return
	 */
	public List<ProjectMember> getMemberListWithPermissionRequired(long projectId, String required) {
		List<ProjectMember> list = projectMemberDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
		List<ProjectMember> retList = new ArrayList<>();

		if (list != null && list.size() > 0) {
			// 遍历成员
			for (ProjectMember member : list) {
				List<ProjectAuth> authes = projectAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
				if (authes != null && authes.size() > 0) {
					// 遍历授权（角色）
					boolean found = false;
					for (ProjectAuth auth : authes) {
						if (found) {
							break;
						}
						Role role = Cache.getRole(auth.getRoleId());
						if (role != null) {
							List<Permission> pList = role.getPermissions();
							if (pList != null && pList.size() > 0) {

								// 遍历许可
								for (Permission p : pList) {
									if (found) {
										break;
									}
									if (p.getEnName().equals(required)) {
										found = true;
										retList.add(member);
									}
								}
							}
						}
					}
				}
			}
		}

		if (retList.size() > 0) {
			for (ProjectMember member : retList) {
				User u = userDao.findOne(member.getUserId());
				member.setUserAccount(u.getAccount());
				member.setUserAddress(u.getAddress());
				member.setUserIcon(u.getIcon());
				member.setUserName(u.getUserName());
				member.setUserQQ(u.getQq());
				member.setUserPhone(u.getCellphone());
			}
		}

		// 如果项目是团队项目,则需要看此团队下有权限的用户 added by wjj
		Project p = projectDao.findOne(projectId);
		if (p.getType().equals(PROJECT_TYPE.TEAM)) {
			List<TeamMember> listTeamMem = teamService.membersPermissionWithGit(required, p.getTeamId());
			for (TeamMember mem : listTeamMem) {
				ProjectMember pm = new ProjectMember();
				User u = userDao.findOne(mem.getUserId());
				pm.setUserAccount(u.getAccount());
				pm.setUserId(u.getId());
				boolean teamMemberExist = false;
				for (ProjectMember prjMem : retList) {
					if (prjMem.getUserAccount().equals(pm.getUserAccount())) {
						teamMemberExist = true;
						break;
					}
				}
				if (!teamMemberExist) {
					retList.add(pm);
				}
			}
		}

		return retList;
	}

	public ProjectMember getProjectMember(long memberId, long loginUserId) {
		ProjectMember member = projectMemberDao.findOne(memberId);

		if (member != null) {
			// 扩展member信息
			User user = userDao.findOne(member.getUserId());
			if (user != null) {
				member.setUserIcon(user.getIcon());
				member.setUserName(user.getUserName());
				member.setUserAccount(user.getAccount());
				member.setUserPhone(user.getCellphone());
				member.setUserQQ(user.getQq());
				member.setUserAddress(user.getAddress());

			}

			Project project = projectDao.findOne(member.getProjectId());
			if (project != null) {
				member.setProjectName(project.getName());
				member.setProjectType(project.getType());
			}

			List<Role> roleList = new ArrayList<>();
			List<ProjectAuth> authes = projectAuthDao.findByMemberIdAndDel(memberId, DELTYPE.NORMAL);
			if (authes != null && authes.size() > 0) {
				for (ProjectAuth auth : authes) {
					Role r = Cache.getRole(auth.getRoleId());
					if (r != null) {
						roleList.add(r);
					}
				}
			}

			member.setRole(roleList);

			Map<String, Integer> permissions = new HashMap<>();
			List<Permission> permissionList = this.getPermissionList(loginUserId, member.getProjectId());
			for (Permission permission : permissionList) {
				permissions.put(permission.getEnName(), 1);
			}

			List<ProjectMember> currentMembers = projectMemberDao.findByProjectIdAndUserIdAndDel(member.getProjectId(),
					loginUserId, DELTYPE.NORMAL);
			for (ProjectMember currentMember : currentMembers) {
				List<ProjectAuth> authes1 = projectAuthDao.findByMemberIdAndDel(currentMember.getId(), DELTYPE.NORMAL);
				List<String> roleList1 = new ArrayList<>();
				if (authes1 != null && authes1.size() > 0) {
					for (ProjectAuth auth : authes1) {
						Role r = Cache.getRole(auth.getRoleId());
						if (r != null) {
							roleList1.add(r.getEnName());
						}
					}
				}
				log.info("member ：" + memberId + "roleList:" + roleList);
				log.info("loginUser ：" + loginUserId + "roleList:" + roleList1);

				// 增加---------------------------------------
				// 如果是团队创建者/管理员,会对团队下的项目也有相应的权限
				Project proj = projectDao.findOne(member.getProjectId());
				boolean owner = false;// 是否在团队中有相应的权限
				if (proj.getType().equals(PROJECT_TYPE.TEAM)) {
					TeamMember memberTmp = teamMemberService.findMemberByTeamIdAndUserId(proj.getTeamId(), loginUserId);
					if (null != memberTmp) {
						if (memberTmp.getType().equals(TEAMREALTIONSHIP.CREATE)) {
							owner = true;
						}
						TeamAuth teamAuth = teamAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
						if (null != teamAuth && teamAuth.getRoleId() == Cache
								.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.ADMINISTRATOR).getId().longValue()) {
							owner = true;
						}
					}

				}
				// 增加----------------------------------------

				if (null != permissions && permissions.size() > 0) {
					if (member.getType().equals(PROJECT_MEMBER_TYPE.CREATOR)) {// 如果被操作人是项目创建者,则不允许修改
						permissions.remove("project_remove_member");// 删除成员
						permissions.remove("project_change_member_role");// 改变角色
					} else if (member.getType().equals(currentMember.getType())) {// 被操作人和操作人都不是创建者,需要比较权限
						if (member.getUserId() != currentMember.getUserId()) {// 不是同一个人(不是自己查看自己的信息)
							// 下面判断,如果被操作人是管理员,但是操作人不是创建者也不是团队创建者和管理员,管理员操作的也不是他自己的话，则没有删除成员,改变角色的权限.
							if (roleList.contains(Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR))
									&& (!roleList1.contains(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR)
											&& owner == false)) {
								permissions.remove("project_remove_member");// 删除成员
								permissions.remove("project_change_member_role");// 改变角色
							}
							/*
							 * if(roleList1.contains(ENTITY_TYPE.PROJECT+"_"+
							 * ROLE_TYPE.MEMBER)){
							 * permissions.remove("project_remove_member"
							 * );//删除成员
							 * permissions.remove("project_change_member_role"
							 * );//改变角色 }
							 */
						}
					}
				}
			}
			member.setPermissions(permissions);
		}
		return member;
	}

	public int saveProjectMember(ProjectMember p, User user) {
		projectMemberDao.save(p);
		/*Project pt = projectDao.findOne(p.getProjectId());
		
		if (!this.isTeamBind(pt.getTeamId()) && (pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)||pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))) {// 判断该项项目是否已授权；授权的话往EMM4.0加人，没有授权不往EMM4.0加人
			//添加项目成员到emm4.0
			if(serviceFlag.equals("online")||serviceFlag.equals("enterprise")){
				ProjectMember projectCreator = this.projectMemberService.findMemberByProjectIdAndMemberType(p.getProjectId(), PROJECT_MEMBER_TYPE.CREATOR);
				User teamCrt = userDao.findOne(projectCreator.getUserId());
				User memberUser=userDao.findOne(p.getUserId());
				Personnel personnel = new Personnel();
				personnel.setCreatorId(teamCrt.getAccount());
				personnel.setName(memberUser.getAccount());
				personnel.setTeamGroupId(projectDao.findOne(p.getProjectId()).getUuid());
				personnel.setMobileNo(memberUser.getCellphone());
				personnel.setEmail(memberUser.getAccount());
				personnel.setGroupName(projectDao.findOne(p.getProjectId()).getName());
				personnel.setTeamDevAddress(xietongHost);
				personnel.setTeamType(p.getType().name().equals("TEAM")?"teamProject":"personnelProject");//个人项目还是团队项目
				String token = "";
				String[] params = new String[2];
				if(serviceFlag.equals("online")){
					Enterprise enterprise = tenantFacade.getEnterpriseByShortName(pt.getBizCompanyId());
					params[0] = enterprise.getId().toString();
				}else{
					params[0] = tenantId;
				}
				params[1] = "dev";
				token= TokenUtilProduct.getToken(key, params);
				
				log.info("projectMember sync to EMM-->"+personnel.getName());
				String flag = "";
				if(serviceFlag.equals("online")){//线上版本
					flag = personnelFacade.createAdminUser(token, personnel);
				}else if(serviceFlag.equals("enterprise")){//企业版
					flag = personnelFacade.createTeamUser(token, personnel);
				}
				log.info("projectMember sync to EMM-->:"+flag);
				if(StringUtils.isNotBlank(flag)){
					throw new RuntimeException("添加项目成员失败,"+flag);
				}
			}
		}*/
		return 1;
	}

	private ProjectMember findMemberByProjectIdAndMemberType(long projectId, PROJECT_MEMBER_TYPE creator) {
		ProjectMember members = this.projectMemberDao.findByProjectIdAndTypeAndDel(projectId, creator, DELTYPE.NORMAL);
		return members;
	}

	public int removeProjectMember(long memberId, String token, Long transferUserId) {
		log.info("------------------>removeProjectMember begin");
		// 如果该项目是团队项目,并且已经被授权,则删除项目成员时候,需要判断此团队下面是否还有此用户,如果没有,则需要删除EMM用户
		ProjectMember pm = projectMemberDao.findOne(memberId);
		long userId = pm.getUserId();
		if(transferUserId!=null){
			if (userId == transferUserId) {
				throw new RuntimeException("此人是您要移除的成员无法接收工作");
			}
		}
		
		Project project = projectDao.findOne(pm.getProjectId());

		// 这里才是删除成员
		ProjectMember member = projectMemberDao.findOneByProjectIdAndUserIdAndDel(pm.getProjectId(), userId,
				DELTYPE.NORMAL);
		// long countTask = this.taskDao.countByProjectIdAndUserIdAndDel(userId,
		// DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),
		// pm.getProjectId(), DELTYPE.NORMAL.ordinal(),
		// DELTYPE.NORMAL.ordinal());
		// long countTaskLeaf =
		// this.taskLeafDao.countByProjectIdAndUserIdAndDel(
		// pm.getProjectId(), userId, DELTYPE.NORMAL.ordinal());
		// long countBug = this.bugDao.countByProjectIdAndUserIdAndDel(
		// pm.getProjectId(), userId, DELTYPE.NORMAL.ordinal());
		// long countProcess = this.processMemberDao
		// .countByProjectIdAndUserIdAndDel(pm.getProjectId(), Cache
		// .getRole(ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MANAGER)
		// .getId(), pm.getUserId(), DELTYPE.NORMAL,
		// DELTYPE.NORMAL, DELTYPE.NORMAL);
		if (member != null) {
			//邀请中的成员不需要转移
			if(transferUserId!=null){
				// 修改所有和人相关的
				changeAllUser(pm.getProjectId(), userId, transferUserId);
			}
			pm.setDel(DELTYPE.DELETED);
			projectMemberDao.save(pm);
			List<ProjectAuth> listPa = projectAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);
			if (null != listPa && listPa.size() > 0) {
				ProjectAuth pa = listPa.get(0);
				pa.setDel(DELTYPE.DELETED);
				projectAuthDao.save(pa);
			}
			//如果团队绑定调用teamMemberService中的删人，
			if (this.isTeamBind(project.getTeamId())) {
				teamMemberService.deleteEmmUser(project.getTeamId(), userId, token);
			}
			//如果团队未绑定，项目绑定调用ProjectService中的删人
			if ("online".endsWith(serviceFlag)) {
				if (!isTeamBind(project.getTeamId())
				&& (project.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED) 
				||project.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))) {
					this.deleteEmmUser(pm.getProjectId(), userId, token);
				}
			}

			// ----------------------------------------git权限----------------------------------------------------------
			boolean deleteGitAuth = true;
			if (project.getType().equals(PROJECT_TYPE.TEAM)) {
				TeamMember tm = teamMemberDao.findByTeamIdAndUserIdAndDel(project.getTeamId(), userId, DELTYPE.NORMAL);
				if (null != tm) {
					TeamAuth tah = teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
					Role role = Cache.getRole(tah.getRoleId());
					List<Permission> listPer = role.getPermissions();
					if (null != listPer && listPer.size() > 0) {
						for (Permission p : listPer) {
							if (p.getEnName().equals("code_upload_master_code")
									|| p.getEnName().equals("code_update_branch")) {
								deleteGitAuth = false;
								break;
							}
						}
					}
					/*
					 * Role creatorRole =
					 * Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
					 * Role mgrRole =
					 * Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE
					 * .ADMINISTRATOR);
					 * if(tah.getRoleId()==creatorRole.getId().longValue() ||
					 * tah.getRoleId()==mgrRole.getId().longValue()){
					 * //是团队创建者或者团队管理员 //不需要删除git权限 deleteGitAuth = false; }
					 */
				}
			}
			if (deleteGitAuth) {
				// 删除对应的git权限
				List<App> listApp = appDao.findByProjectIdAndDel(project.getId(), DELTYPE.NORMAL);
				User user = userDao.findOne(userId);
				List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
				List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
				for (App app : listApp) {
					if (app.getUserId() != userId) {
						GitAuthVO vo = new GitAuthVO();
						// vo.setAuthflag("all");不传代表删除所有权限,所以注释掉,不用传了
						vo.setPartnername(user.getAccount());
						vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
						String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
						vo.setProject(encodeKey.toLowerCase());
						vo.setProjectid(app.getAppcanAppId());
						// vo.setRef("master");
						listAuth.add(vo);
					} else {
						GitOwnerAuthVO vo = new GitOwnerAuthVO();
						String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
						vo.setProject(encodeKey.toLowerCase());
						vo.setUsername(user.getAccount());
						User other = new User();// 应用要转给谁
						if (project.getType().equals(PROJECT_TYPE.TEAM)) {
							List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(),
									TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
							if (null != listTm && listTm.size() > 0) {
								other = userDao.findOne(listTm.get(0).getUserId());
							}
						} else {
							ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(),
									PROJECT_MEMBER_TYPE.CREATOR, DELTYPE.NORMAL);
							if (null != pmCrt) {
								other = userDao.findOne(pmCrt.getUserId());
							}
						}
						vo.setOther(other.getAccount());
						vo.setProjectid(app.getAppcanAppId());
						app.setUserId(other.getId());
						appDao.save(app);
						changeOwnerAuth.add(vo);
					}
				}
				// 删除git权限(项目下的应用不是被删除人创建的应用)
				Long delGitAuthStartTime=System.currentTimeMillis();
				Map<String, String> map = appService.delGitAuth(listAuth);
				Long delGitAuthEndtTime=System.currentTimeMillis();
				log.info(String.format("delGitAuth need time  [%s]ms", delGitAuthEndtTime-delGitAuthStartTime));
				log.info(user.getAccount() + " was by deleted from project->projectId:" + pm.getProjectId()
						+ " ,and delGitAuth->" + (null == map ? null : map.toString()));
				// 转让git权限(项目下的应用是被删除人创建的应用)
				map = appService.updateGitAuth(changeOwnerAuth);
				log.info(user.getAccount() + " was by deleted from project->projectId:" + pm.getProjectId()
						+ " ,and updateGitAuth->" + (null == map ? null : map.toString()));
			}
			// ----------------------------------------git权限----------------------------------------------------------
			return 1;
		}
		return 0;

	}

	/**
	 * 程海军 项目绑定EMM,团队未绑定,时候调用删除人员,同步EMM接口
	 * @param projectId
	 * @param userId
	 * @param token
	 */
	public void deleteEmmUser(long projectId, long userId, String token) {
		/*log.info("prepare delete EMM User:userId->" + userId + ",projectId->" + projectId + ",token->" + token);
		Project project = projectDao.findOne(projectId);
		// 如果是授权通过的项目,还需要依情况看是否需要调用EMM接口,删除其对应的用户信息
		List<User> list = this.findAllUserBelongProject(projectId);
		log.info("project member size: -- >" + list.size());
		if (null != list) {
			for (User u : list) {
				log.info("already in project user:--->" + u.getId());
			}
		}
		boolean flag = false;
		boolean delFlag = false;
		if (null != list && list.size() > 0) {
			for (User u : list) {
				if (u.getId().longValue() == userId) {
					flag = true;
					log.info("don't to delete emm user");
					break;
				}
			}
			if (!flag) {
				// 调用EMM删除
				delFlag = true;
			}
		} else {
			// 调用EMM删除
			delFlag = true;
		}
		log.info("judge deleteEmm user -->" + delFlag);
		if (delFlag) {
			log.info("=====begin delete emm user=======:");
			ProjectMember ptCreator = this.findMemberByProjectIdAndMemberType(projectId, PROJECT_MEMBER_TYPE.CREATOR);
			User userProjectCreator = userDao.findOne(ptCreator.getUserId());
			User delUser = userDao.findOne(userId);
			log.info(" deleteProejctUser params:token:" + token + ",proejctId:" + project.getUuid() + ",actorUser:"
					+ userProjectCreator.getAccount() + ",delUser:" + delUser.getAccount());
			String resultFlag = "";
			if (serviceFlag.equals("online")) {// 大众版
				resultFlag = personnelFacade.deleteAdminUser(token, project.getUuid(), delUser.getAccount());
				log.info("返回:" + resultFlag);
			}else if(serviceFlag.equals("enterprise")){//企业版
				resultFlag = personnelFacade.deleteTeamUser(token, project.getUuid(), delUser.getAccount());
				log.info("返回:"+resultFlag);
			}
			if (serviceFlag.equals("online") || serviceFlag.equals("enterprise")) {// 非EMM3
				if (StringUtils.isNotBlank(resultFlag)) {
					throw new RuntimeException("EMM del projectUser failed," + resultFlag);
				}
			}
		}*/
	}

	public int editProjectMemberRole(long memberId, ROLE_TYPE roleType, long loginUserId) throws Exception {
		log.info("udpate projectMember role method--->memberId:" + memberId + ",roleType:" + roleType + ",loginUserId:"
				+ loginUserId);
		ProjectMember member = projectMemberDao.findOne(memberId);
		if (member == null) {
			return 0;
		}
		User user = userDao.findOne(member.getUserId());
		Project project = projectDao.findOne(member.getProjectId());
		boolean masterInTeam = false;// 标识此人在项目对应的团队中是否有主干权限
		boolean branchInTeam = false;// 标识此人在项目对应的团队中是否有分支权限
		List<GitAuthVO> addAuth = new ArrayList<GitAuthVO>();
		List<GitAuthVO> delAuth = new ArrayList<GitAuthVO>();
		List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
		if (-1 != project.getTeamId()) {
			List<TeamMember> listTeamMem = teamMemberDao.findByTeamIdAndDel(project.getTeamId(), DELTYPE.NORMAL);
			for (TeamMember tm : listTeamMem) {
				if (tm.getUserId().longValue() == member.getUserId()) {
					TeamAuth ta = teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
					List<Permission> teamPermission = Cache.getRole(ta.getRoleId()).getPermissions();
					if (null != teamPermission && teamPermission.size() > 0) {
						for (Permission p : teamPermission) {
							if (p.getEnName().equals("code_upload_master_code")) {
								masterInTeam = true;
								break;
							}
							if (p.getEnName().equals("code_update_branch")) {
								branchInTeam = true;
							}
						}
					}
				}
			}
		}
		log.info("团队中master权限-->" + masterInTeam);
		log.info("团队中branch权限-->" + branchInTeam);
		Set<String> availableRoleNameSet = new HashSet<>();
		availableRoleNameSet.add(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR); // 管理员
																						// ->
																						// 成员
		availableRoleNameSet.add(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.MEMBER); // 成员
																				// ->
																				// 管理员
		availableRoleNameSet.add(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.OBSERVER); // 成员
																					// ->
																					// 观察员

		Role targetRole = Cache.getRole(ENTITY_TYPE.PROJECT + "_" + roleType.name());
		if (targetRole == null || !availableRoleNameSet.contains(targetRole.getEnName())) {
			log.info("修改的目标角色不存在");
			return 0;
		}
		availableRoleNameSet.add(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR);

		List<ProjectAuth> authes = projectAuthDao.findByMemberIdAndDel(memberId, DELTYPE.NORMAL);
		ProjectMember projectCreator = this.projectMemberDao.findByProjectIdAndTypeAndDel(member.getProjectId(),
				PROJECT_MEMBER_TYPE.CREATOR, DELTYPE.NORMAL);
		if (authes != null && authes.size() > 0) {
			for (ProjectAuth auth : authes) {
				Role currentRole = Cache.getRole(auth.getRoleId());
				if (currentRole == null) {
					log.info("缓存中不存在此角色-->" + auth.getRoleId());
					continue;
				}

				if (availableRoleNameSet.contains(currentRole.getEnName())) {
					// 进行变更
					if (currentRole.getId() == targetRole.getId()) {
						log.info("当前和目标角色相同");
						return 0;
					} else {
						// 被修改的的member是管理员
						if (currentRole.getEnName().equals(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR)) {
							// 当前人不是项目创建者,并且不是自己修改自己
							if (loginUserId != projectCreator.getUserId() && loginUserId != user.getId().longValue()) {
								//
								return 0;
							}
							// 被修改的 member为创建者
						} else if (currentRole.getEnName().equals(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR)) {
							return 0;
						}
						// member 要被修改为创建者
						if (targetRole.getEnName().equals(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR)) {
							return 0;
						}
						long oldRoleId = auth.getRoleId();
						List<Permission> listOldPermission = Cache.getRole(oldRoleId).getPermissions();
						auth.setRoleId(targetRole.getId());
						projectAuthDao.save(auth);

						// ---------------------------------git权限-----------------------------------
						List<App> listApp = appDao.findByProjectIdAndDel(member.getProjectId(), DELTYPE.NORMAL);
						// a.判断修改之前的git权限
						boolean oldMaster = masterInTeam;
						boolean oldBranch = branchInTeam;
						if (null != listOldPermission && listOldPermission.size() > 0) {
							for (Permission p : listOldPermission) {
								if ("code_upload_master_code".equals(p.getEnName())) {
									oldMaster = true;
									break;
								}
								if ("code_update_branch".equals(p.getEnName())) {
									oldBranch = true;
								}
							}
						}
						boolean nowMaster = masterInTeam;
						boolean nowBranch = branchInTeam;
						if (null != targetRole.getPermissions() && targetRole.getPermissions().size() > 0) {
							for (Permission p : targetRole.getPermissions()) {
								if ("code_upload_master_code".equals(p.getEnName())) {
									nowMaster = true;
									break;
								}
								if ("code_update_branch".equals(p.getEnName())) {
									nowBranch = true;
								}
							}
						}
						log.info("update projectMember Role for userId-->" + user.getId() + ",oldMaster-->" + oldMaster
								+ ",oldBranch->" + oldBranch + ",nowMaster->" + nowMaster + ",nowBranch->" + nowBranch);
						if ((oldMaster == nowMaster) && (oldBranch == nowBranch)) {
							return 1;
						}
						log.info("----------------begin judge git auth----------------");
						boolean addMaster = false;
						boolean delGitFlag = false;// 是否删除git权限(不区分删除主干和分支)
						if (oldMaster != nowMaster) {
							if (nowMaster) {
								// 增加主干权限
								addMaster = true;
								for (App app : listApp) {
									if (app.getUserId() != user.getId()) {

										GitAuthVO vo = new GitAuthVO();
										vo.setAuthflag("all");
										vo.setPartnername(user.getAccount());
										vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
										String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
										vo.setProject(encodeKey.toLowerCase());
										vo.setRef("master");
										vo.setProjectid(app.getAppcanAppId());
										addAuth.add(vo);
									}
								}

							} else {
								// 删除主干权限
								delGitFlag = true;
							}
						}
						if (oldBranch != nowBranch) {
							if (nowBranch && !addMaster) {
								// 增加分支
								for (App app : listApp) {
									if (app.getUserId() != user.getId()) {

										GitAuthVO vo = new GitAuthVO();
										vo.setAuthflag("all");
										vo.setPartnername(user.getAccount());
										vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
										String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
										vo.setProject(encodeKey.toLowerCase());
										vo.setRef("master");
										vo.setProjectid(app.getAppcanAppId());
										addAuth.add(vo);
									}
								}

							} else if (!nowBranch) {
								// 减去分支
								delGitFlag = true;
							}
						}

						if (delGitFlag) {
							for (App app : listApp) {
								if (app.getUserId() != user.getId()) {
									GitAuthVO vo = new GitAuthVO();
									vo.setPartnername(user.getAccount());
									vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
									String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
									vo.setProject(encodeKey.toLowerCase());
									vo.setProjectid(app.getAppcanAppId());
									delAuth.add(vo);
								} else {
									GitOwnerAuthVO vo = new GitOwnerAuthVO();
									String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
									vo.setProject(encodeKey.toLowerCase());
									vo.setUsername(user.getAccount());
									User other = new User();// 应用要转给谁
									if (project.getType().equals(PROJECT_TYPE.TEAM)) {
										List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(
												project.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
										if (null != listTm && listTm.size() > 0) {
											other = userDao.findOne(listTm.get(0).getUserId());
										}
									} else {
										ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(
												project.getId(), PROJECT_MEMBER_TYPE.CREATOR, DELTYPE.NORMAL);
										if (null != pmCrt) {
											other = userDao.findOne(pmCrt.getUserId());
										}
									}
									vo.setOther(other.getAccount());
									app.setUserId(other.getId());
									vo.setProjectid(app.getAppcanAppId());
									appDao.save(app);
									changeOwnerAuth.add(vo);
								}
							}
						}
						Map<String, String> map = appService.addGitAuth(addAuth);
						log.info("need add ----->addAuth.size()" + addAuth.size());
						if (addAuth.size() > 0) {
							map = appService.addGitAuth(addAuth);
							log.info("change projectMember roles for user (id-->" + user.getId() + "), addGitAuth -->"
									+ map.toString());
						}
						log.info("need del ----->delAuth.size()" + delAuth.size());
						if (delAuth.size() > 0) {

							// 删除git权限(项目下的应用不是被删除人创建的应用)
							map = appService.delGitAuth(delAuth);
							log.info("change projectMember roles for user (id-->" + user.getId() + "), delGitAuth -->"
									+ map.toString());
						}
						log.info("need changeOwner ----->changeOwnerAuth.size()" + changeOwnerAuth.size());
						if (changeOwnerAuth.size() > 0) {

							// 转让git权限(项目下的应用是被删除人创建的应用)
							map = appService.updateGitAuth(changeOwnerAuth);
							log.info("change projectMember roles for user (id-->" + user.getId()
									+ "), updateGitAuth -->" + map.toString());

						}

						// ---------------------------------git权限-----------------------------------

						return 1;
					}

				}

			}

		}

		return 0;
	}

	/**
	 * @Title: findProjList @Description: 查询团队下面的项目列表 @param @param
	 * teamId @param @return 参数 @return List<Project> 返回类型 @user
	 * jingjian.wu @date 2015年8月15日 下午8:03:30 @throws
	 */
	public List<Project> findProjList(long teamId) {
		List<Project> listProj = this.projectDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
		for (Project p : listProj) {
			ProjectCategory pc = Cache.getProjectCategory(p.getCategoryId());
			if (pc != null) {
				p.setCategoryName(pc.getName());
			}
			Team tm = teamDao.findOne(p.getTeamId());
			if (tm != null) {
				p.setTeamName(tm.getName());
			}
		}
		return listProj;
	}

	// 分页查询团队项目列表
	public Page<Project> findProjList(long teamId, Pageable pageable) {
		Page<Project> listProj = this.projectDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL, pageable);
		for (Project p : listProj.getContent()) {
			ProjectCategory pc = Cache.getProjectCategory(p.getCategoryId());
			if (pc != null) {
				p.setCategoryName(pc.getName());
			}
			Team tm = teamDao.findOne(p.getTeamId());
			if (tm != null) {
				p.setTeamName(tm.getName());
			}
		}
		return listProj;
	}

	// 分页查询团队项目列表
	public Page<Project> findProjList(long teamId, long loginUserId, List<PROJECT_MEMBER_TYPE> types,
			Pageable pageable) {
		Page<Project> listProj = this.projectDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL, types, loginUserId,
				pageable);
		for (Project p : listProj.getContent()) {
			ProjectCategory pc = Cache.getProjectCategory(p.getCategoryId());
			if (pc != null) {
				p.setCategoryName(pc.getName());
			}
			Team tm = teamDao.findOne(p.getTeamId());
			if (tm != null) {
				p.setTeamName(tm.getName());
			}
		}
		return listProj;
	}

	public void saveProjectAuth(ProjectAuth auth) {
		projectAuthDao.save(auth);
	}

	public int addProjectAuth(List<ProjectAuth> authList) {
		projectAuthDao.save(authList);
		return authList.size();
	}

	@Override
	public List<Permission> getPermissionList(long loginUserId, long projectId) {
		HashMap<Long, Permission> permissionMap = new HashMap<>();

		List<ProjectMember> memberList = this.projectMemberDao.findByProjectIdAndUserIdAndDel(projectId, loginUserId,
				DELTYPE.NORMAL);
		if (memberList != null && memberList.size() > 0) {
			for (ProjectMember member : memberList) {
				List<ProjectAuth> authList = this.projectAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
				if (authList != null && authList.size() > 0) {
					for (ProjectAuth auth : authList) {
						long roleId = auth.getRoleId();
						Role role = Cache.getRole(roleId);
						if (role != null) {
							List<Permission> permissions = role.getPermissions();
							if (permissions != null && permissions.size() > 0) {
								for (Permission p : permissions) {
									permissionMap.put(p.getId(), p);
								}
							}
						}
					}
				}
			}
		}
		List<Permission> listPermission = new ArrayList<>(permissionMap.values());

		// 如果是团队创建者/管理员,会对团队下的项目也有相应的权限
		Project proj = projectDao.findOne(projectId);
		if (proj.getType().equals(PROJECT_TYPE.TEAM)) {
			boolean owner = false;
			TeamMember member = teamMemberService.findMemberByTeamIdAndUserId(proj.getTeamId(), loginUserId);
			if (null != member) {
				if (member.getType().equals(TEAMREALTIONSHIP.CREATE)) {
					owner = true;
				}
				TeamAuth teamAuth = teamAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
				if (null != teamAuth && teamAuth.getRoleId() == Cache
						.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.ADMINISTRATOR).getId().longValue()) {
					owner = true;
				}
				if (owner) {
					TeamMember tmember = this.teamMemberDao.findByTeamIdAndUserIdAndDel(proj.getTeamId(), loginUserId,
							DELTYPE.NORMAL);
					if (null != tmember) {
						TeamAuth teamAuthInner = this.teamAuthDao.findByMemberIdAndDel(tmember.getId(), DELTYPE.NORMAL);
						if (null != teamAuthInner) {
							List<Permission> listPermissionTeam = Cache.getRole(teamAuthInner.getRoleId())
									.getPermissions();
							if (listPermissionTeam != null && listPermissionTeam.size() > 0) {
								listPermission.addAll(listPermissionTeam);
							}
						}
					}

				}
			}

		}
		return listPermission;
	}

	/**
	 * 获取项目许可集合<br>
	 * 
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * 
	 * @param permissionEnName
	 * @return
	 */
	/*
	 * public Map<Long, List<String>> permissionMapAsMemberWith(String
	 * permissionEnName, long loginUserId) { Map<Long, List<String>>
	 * permissionsMapAsMember = new HashMap<>();
	 * 
	 * List<ProjectMember> projectMembers =
	 * projectMemberDao.findByUserIdAndDel(loginUserId, DELTYPE.NORMAL);
	 * if(projectMembers != null && projectMembers.size() > 0) { // 遍历项目成员
	 * for(ProjectMember pm : projectMembers) { List<ProjectAuth> authList =
	 * projectAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);
	 * 
	 * // 遍历角色 Map<Long, String> permissionUnionMap = new HashMap<>();
	 * if(authList != null && authList.size() > 0) { for(ProjectAuth auth :
	 * authList) { Role role = Cache.getRole(auth.getRoleId()); List<Permission>
	 * permissions = (role == null) ? new ArrayList<Permission>() :
	 * role.getPermissions(); for(Permission p : permissions) {
	 * permissionUnionMap.put(p.getId(), p.getEnName()); } } } // 生成权限并集
	 * List<String> permissionUnionArr = new
	 * ArrayList<>(permissionUnionMap.values());
	 * 
	 * // 判定是否存在读取权限 boolean hasRequiredPermission = false; for(String p :
	 * permissionUnionArr) { if( permissionEnName.equals( p ) ) {
	 * hasRequiredPermission = true; break; } }
	 * 
	 * if(hasRequiredPermission) { permissionsMapAsMember.put(pm.getProjectId(),
	 * permissionUnionArr); } } }
	 * 
	 * return permissionsMapAsMember; }
	 */
	/**
	 * 获取项目许可集合<br>
	 * 
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * 
	 * @param permissionEnName
	 * @return
	 */
	public Map<Long, List<String>> permissionMap(String permissionEnName, long loginUserId) {
		long startTime = System.currentTimeMillis();
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();

		StringBuffer sql = new StringBuffer("select projectId,GROUP_CONCAT(roleId) from (");
		sql.append(
				" select distinct member.projectId,auth.roleId from T_PROJECT_MEMBER member left join T_PROJECT_AUTH  auth on member.id = auth.memberId   ")
				.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
				.append(" and auth.roleId  in( ")
				.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
				.append(permissionEnName).append("' and del=0) and del=0  ").append(")").append(" union all  ")
				.append("  select distinct prj.Id projectId, auth.roleId from T_TEAM_MEMBER member left join T_TEAM_AUTH  auth on member.id = auth.memberId  ")
				.append(" left join T_PROJECT prj on member.teamId =prj.teamId ")
				.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
				.append(" and auth.roleId  in( ")
				.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
				.append(permissionEnName).append("' and del=0) and del=0  ").append(" ) ").append(" and prj.del=0  ")
				.append(" ) t GROUP BY t.projectId ");
		log.info("permissionMapAsMemberWithSql===>"+sql.toString());
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				roleIdList.clear();
				roleIdList.addAll(Arrays.asList(rs.getString(2).split(",")));
				permissionSet.clear();
				for (String str : roleIdList) {
					List<Permission> listP = null != Cache.getRole(Integer.parseInt(str))
							? Cache.getRole(Integer.parseInt(str)).getPermissions() : new ArrayList<Permission>();
					for (Permission p : listP) {
						permissionSet.add(p.getEnName());
					}
				}

				permissionsMapAsMember.put(rs.getLong(1), new ArrayList<String>(permissionSet));
			}
		});
		long endTime = System.currentTimeMillis();
		log.info("projectService permissionMapAsMemberWith total time--> " + (endTime - startTime) + "  ms");
		return permissionsMapAsMember;
	}
	/**
	 * 获取项目许可集合<br>
	 * 
	 * 返回带有指定许可(permissionEnName)的情况下<br>
	 * 每个项目（key = taskId）具有的许可标记(enName)列表（value = List）
	 * 
	 * @param permissionEnName
	 * @return
	 */
	public Map<Long, List<String>> permissionMapAsMemberWith(String permissionEnName, long loginUserId) {
		long startTime = System.currentTimeMillis();
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();

		StringBuffer sql = new StringBuffer("select projectId,GROUP_CONCAT(roleId) from (");
		sql.append(
				" select distinct member.projectId,auth.roleId from T_PROJECT_MEMBER member left join T_PROJECT_AUTH  auth on member.id = auth.memberId   ")
				.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
				.append(" and auth.roleId  in( ")
				.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
				.append(permissionEnName).append("' and del=0) and del=0  ").append(")").append(" union all  ")
				.append("  select distinct prj.Id projectId, auth.roleId from T_TEAM_MEMBER member left join T_TEAM_AUTH  auth on member.id = auth.memberId  ")
				.append(" left join T_PROJECT prj on member.teamId =prj.teamId ")
				.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
				.append(" and auth.roleId  in( ")
				.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
				.append(permissionEnName).append("' and del=0) and del=0  ").append(" ) ").append(" and prj.del=0  ")
				.append(" ) t GROUP BY t.projectId ");
		log.info("permissionMapAsMemberWithSql===>"+sql.toString());
//		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				/*roleIdList.clear();
				roleIdList.addAll(Arrays.asList(rs.getString(2).split(",")));
				permissionSet.clear();
				for (String str : roleIdList) {
					List<Permission> listP = null != Cache.getRole(Integer.parseInt(str))
							? Cache.getRole(Integer.parseInt(str)).getPermissions() : new ArrayList<Permission>();
					for (Permission p : listP) {
						permissionSet.add(p.getEnName());
					}
				}*/

				permissionsMapAsMember.put(rs.getLong(1), new ArrayList<String>(permissionSet));
			}
		});
		long endTime = System.currentTimeMillis();
		log.info("projectService permissionMapAsMemberWith total time--> " + (endTime - startTime) + "  ms");
		return permissionsMapAsMember;
	}
	public Map<Long, List<String>> permissionMapAsMemberWithAndOnlyByProjectId(String permissionEnName, long loginUserId,long projectId) {
		long startTime = System.currentTimeMillis();
		final Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();

		StringBuffer sql = new StringBuffer("select projectId,GROUP_CONCAT(roleId) from (");
		sql.append(
				" select distinct member.projectId,auth.roleId from T_PROJECT_MEMBER member left join T_PROJECT_AUTH  auth on member.id = auth.memberId   ")
				.append(" where member.del=0 and auth.del=0  and member.projectId=").append(projectId).append(" and member.userId= ").append(loginUserId)
				.append(" and auth.roleId  in( ")
				.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
				.append(permissionEnName).append("' and del=0) and del=0  ").append(")").append(" union all  ")
				.append("  select distinct prj.Id projectId, auth.roleId from T_TEAM_MEMBER member left join T_TEAM_AUTH  auth on member.id = auth.memberId  ")
				.append(" left join T_PROJECT prj on member.teamId =prj.teamId ")
				.append(" where member.del=0 and auth.del=0  and prj.id=").append(projectId).append(" and member.userId= ").append(loginUserId)
				.append(" and auth.roleId  in( ")
				.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
				.append(permissionEnName).append("' and del=0) and del=0  ").append(" ) ").append(" and prj.del=0  ")
				.append(" ) t GROUP BY t.projectId ");
		log.info("permissionMapAsMemberWithSql===>"+sql.toString());
		final List<String> roleIdList = new ArrayList<String>();
		final Set<String> permissionSet = new HashSet<String>();
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				roleIdList.clear();
				roleIdList.addAll(Arrays.asList(rs.getString(2).split(",")));
				permissionSet.clear();
				for (String str : roleIdList) {
					List<Permission> listP = null != Cache.getRole(Integer.parseInt(str))
							? Cache.getRole(Integer.parseInt(str)).getPermissions() : new ArrayList<Permission>();
					for (Permission p : listP) {
						permissionSet.add(p.getEnName());
					}
				}

				permissionsMapAsMember.put(rs.getLong(1), new ArrayList<String>(permissionSet));
			}
		});
		long endTime = System.currentTimeMillis();
		log.info("projectService permissionMapAsMemberWith total time--> " + (endTime - startTime) + "  ms");
		return permissionsMapAsMember;
	}
	// 查询某个项目下的所有任务
	public List<Task> findTaskByProjectId(Long projectId) {
		List<Task> tasks = this.taskDao.countByProjectIdAndDel(projectId, DELTYPE.NORMAL, DELTYPE.NORMAL);
		return tasks;
	}

	// 查询某个项目下一共的人员
	public List<ProjectMember> findProjectMemberByProjectId(Long projectId) {
		List<ProjectMember> listMember = this.projectMemberDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
		return listMember;
	}

	public int getProjectProgressForInt(long projectId) {
		int result=0;
		//完成流程个数
		int countCompleteProcess = 0;
		//所有流程列表
		List<Process> listProcess = this.processDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
		if (null == listProcess || listProcess.size() == 0) {
			return 0;
		}
		BigDecimal allProcessWeight = new BigDecimal(0);
		for (Process pro : listProcess) {
			allProcessWeight = allProcessWeight.add(new BigDecimal(pro.getWeight()));
		}
		if (allProcessWeight.intValue() == 0) {// 如果所有流程的权重之和为0,返回0,防止下面除以0报错
			return 0;
		}
		BigDecimal progress = new BigDecimal(0);
		final List<Float> oneElementList = new ArrayList<Float>();
		for (Process pro : listProcess) {
			StringBuffer sql = new StringBuffer("select ifnull(( ");
			sql.append(" (").append(" (")
				.append(" select count(1) from T_TASK where del=0 and processId= " + pro.getId() + " and status=1 ")
				.append(" ) ").append(" + ")
				.append(" (select count(1) from T_BUG where processId = " + pro.getId()+ " and del=0 and status=2 ) ")
				.append(")").append(" / ")
				.append("(").append("(")
				.append("select count(1) from T_TASK where del=0 and processId= " + pro.getId()).append(") ")
				.append(" + ")
				.append(" (select count(1) from T_BUG where processId = " + pro.getId() + " and del=0  ) ")
				.append(")").append("),0) perc");
			log.info("calculate projectProgress sql -->" + sql.toString());
			this.jdbcTpl.query(sql.toString(), new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					float tmp = rs.getFloat("perc");
					//tmp=Math.round(tmp*100);
//					if (tmp < 1 && tmp > 0) {
//						String toStringStr = new BigDecimal(tmp).toPlainString();
//						if (toStringStr.length() > 1) {
//							tmp = Float.parseFloat(toStringStr.substring(0, 2));
//						}
//					}
					oneElementList.add(tmp);
				}
			});
			float tmp = oneElementList.get(0);
			//如果当前流程完成进度是0，则进行下一个流程
			if (new BigDecimal(tmp).equals(BigDecimal.ZERO)) {
				continue;
			}
			//如果当前流程完成进度是100%，则统计总数
			if(new BigDecimal(tmp).compareTo(BigDecimal.ONE)==0){
				countCompleteProcess=countCompleteProcess+1;
			}
			//完成度进行四舍五入
			BigDecimal tmpPercent = new BigDecimal(Math.round(tmp*100));
			oneElementList.remove(0);
			//BigDecimal proPercent = new BigDecimal(pro.getWeight()).divide(allProcessWeight, 2, RoundingMode.FLOOR);
			//解决bug：【协同开发】如果创建了两个权重比例是1:2的流程，且流程都完成的话报表中流程阶段情况会显示已完成99%实际上应该是100%
			BigDecimal proPercent = new BigDecimal(pro.getWeight()).divide(allProcessWeight, 2, RoundingMode.HALF_UP);
			progress = progress.add(tmpPercent.multiply(proPercent));
			log.info("calculate progress -->" + progress);
		}
		//progress = progress.multiply(new BigDecimal(100));
		//如果所有流程完成度都是100%，则项目的完成度就是100%
		if(countCompleteProcess==listProcess.size()){
			result=100;
		}else{
			//当项目流程不是全部100%完成时是，根据各流程完成度及权重占比进行计算。
			if(progress.compareTo(BigDecimal.ZERO)>0&&progress.compareTo(BigDecimal.ONE)<=0){
				result=1;	
			}else if(progress.compareTo(new BigDecimal(100))>=0){
				result=100;
			}else{
				result = progress.setScale(0, RoundingMode.FLOOR).intValue();
			}
		}
		return result;
	}

	public List<Long> getProjectMemberIdList(Long projectId, Long userId) {
		return this.projectMemberDao.findByProjectIdAndUserIdNotAndDel(projectId, userId, DELTYPE.NORMAL);
	}

	public ProjectMember getProjectCreator(Long projectId) {
		return this.projectMemberDao.findByProjectIdAndTypeAndDel(projectId, PROJECT_MEMBER_TYPE.CREATOR,
				DELTYPE.NORMAL);
	}

	public List<ProjectMember> getProjectManager(Long projectId) {
		return this.projectMemberDao.findByProjectIdAndType(projectId, PROJECT_MEMBER_TYPE.PARTICIPATOR.ordinal(),
				ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.MANAGER);
	}

	public List<ProjectCategory> findCategoryList() {
		return this.projectCategoryDao.findByDel(DELTYPE.NORMAL);
	}

	public ProjectMember findProjectMemberByMemberId(long memberId) {
		return projectMemberDao.findOne(memberId);
	}

	public List<Project> findProjectListByIds(Set<Long> proIds) {
		return this.projectDao.findByIdInAndDelOrderByCreatedAtDesc(proIds, DELTYPE.NORMAL);
	}

	public List<Project> findProjectListByIdsAndName(Set<Long> proIds, String name) {
		return this.projectDao.findByIdInAndDelAndNameLikeOrderByCreatedAtDesc(proIds, DELTYPE.NORMAL, "%" + name + "%",
				name + "%");
	}

	/**
	 * 判断项目下是否有已经存在某个人
	 * 
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午4:17:53
	 */
	public boolean userExistInProject(Long projectId, Long userId) {
		ProjectMember pm = projectMemberDao.findOneByProjectIdAndUserIdAndDel(projectId, userId, DELTYPE.NORMAL);
		if (null == pm) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @describe 项目下未完成的任务数量 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年12月4日 上午9:31:25 <br>
	 * @param projectId
	 * @return <br>
	 * @returnType int
	 * 
	 */
	// public int getProjectTaskNum(long projectId) {
	// log.info("start calculate project total unclosed task number, projectId:
	// "+projectId);
	// List<TASK_STATUS> status = new ArrayList<>();
	// status.add(TASK_STATUS.FINISHED);
	// status.add(TASK_STATUS.ONGOING);
	// status.add(TASK_STATUS.REJECTED);
	// status.add(TASK_STATUS.WAITING);
	// long num =
	// this.taskDao.countByProjectIdAndStatusInAndDel(projectId,DELTYPE.NORMAL,status,DELTYPE.NORMAL);
	// log.info("end calculate project total unclosed task number: "+num);
	// return (int)num;
	// }

	/**
	 * 获取项目下正式成员的用户id集合
	 * 
	 * @user jingjian.wu
	 * @date 2015年10月23日 下午5:30:03
	 */
	public List<Long> getProjectOfficalMemberUserIdList(Long projectId) {
		List<PROJECT_MEMBER_TYPE> membertypes = new ArrayList<PROJECT_MEMBER_TYPE>();
		membertypes.add(PROJECT_MEMBER_TYPE.CREATOR);
		membertypes.add(PROJECT_MEMBER_TYPE.PARTICIPATOR);
		return this.projectMemberDao.findProjectOfficalUserId(projectId, membertypes);
	}

	/**
	 * 根据用户id,将此用户在项目中从被邀请状态激活为正常用户
	 * @throws Exception 
	 * 
	 * @user jingjian.wu
	 * @date 2015年10月27日 下午5:48:02
	 */
	public void updateMemberType(long userId) throws Exception {
		log.info("user login --> agree to join project ,userId:" + userId);
		List<ProjectMember> listMember = projectMemberDao.findByUserIdAndDel(userId, DELTYPE.NORMAL);
		log.info("user login -- > projectMember Size:" + (null == listMember ? 0 : listMember.size()));
		if (null != listMember) {
			for (ProjectMember member : listMember) {
				if (member.getType().equals(PROJECT_MEMBER_TYPE.INVITEE)) {
					long teamId = projectDao.findOne(member.getProjectId()).getTeamId();
					log.info(
							"user login -->  joined project ,userId:" + userId + ",projectId:" + member.getProjectId());
					boolean existInEmm = teamService.isAlreadyExistInEMM(teamId, userId);
					log.info("user login --> judge in Emm :" + existInEmm);
					member.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
					projectMemberDao.save(member);
					// --------------------------增加对应的git权限-----------begin--------------------------
					List<ProjectAuth> listAuth = projectAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
					if (null != listAuth && listAuth.size() > 0) {
						for (ProjectAuth pa : listAuth) {
							Role ro = Cache.getRole(pa.getRoleId());
							List<Permission> listP = ro.getPermissions();
							boolean master = false;
							boolean branch = false;
							if (null != listP && listP.size() > 0) {
								for (Permission p : listP) {
									if (p.getEnName().equals("code_upload_master_code")) {
										master = true;
										break;
									}
									if (p.getEnName().equals("code_update_branch")) {
										branch = true;
									}
								}
								List<App> projectAppList = appDao.findByProjectIdAndDel(member.getProjectId(),
										DELTYPE.NORMAL);// 此项目下的应用
								User currUser = userDao.findOne(userId);// 当前人
								List<GitAuthVO> listAuthApp = new ArrayList<GitAuthVO>();
								if (master) {// 此人有主干权限的话
									if (null != projectAppList && projectAppList.size() > 0) {
										for (App app : projectAppList) {
											if (app.getUserId() != currUser.getId().longValue()) {
												GitAuthVO vo = new GitAuthVO();
												vo.setAuthflag("all");
												vo.setPartnername(currUser.getAccount());
												vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
												String encodeKey = "X"
														+ MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
												vo.setProject(encodeKey.toLowerCase());
												vo.setRef("master");
												vo.setProjectid(app.getAppcanAppId());
												listAuthApp.add(vo);
											}

										}
										Map<String, String> map = appService.addGitAuth(listAuthApp);
										log.info(currUser.getAccount() + " join project id->" + member.getProjectId()
												+ ",and shareallgit->" + (null == map ? null : map.toString()));
									}
								} else if (branch) {// 此人有分支权限的话
									for (App app : projectAppList) {
										if (app.getUserId() != currUser.getId().longValue()) {
											GitAuthVO vo = new GitAuthVO();
											vo.setAuthflag("allbranch");
											vo.setPartnername(currUser.getAccount());
											vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
											String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
											vo.setProject(encodeKey.toLowerCase());
											vo.setProjectid(app.getAppcanAppId());
											listAuthApp.add(vo);
										}

									}
									Map<String, String> map = appService.addGitAuth(listAuthApp);
									log.info(currUser.getAccount() + " join project id->" + member.getProjectId()
											+ ",and shareallgit->" + (null == map ? null : map.toString()));
								}
							}
						}
					}
					// --------------------------增加对应的git权限-----------end--------------------------
					this.dynamicService.addPrjDynamic(userId, DYNAMIC_MODULE_TYPE.PROJECT_JOIN_MEMBER,
							member.getProjectId(), new Object[] {});

					// 如果此项目为团队项目,而且已经被授权,那么需要判断此用户是否需要添加到EMM
					Project prj = projectDao.findOne(member.getProjectId());
					if (prj.getType().equals(PROJECT_TYPE.TEAM)
							&& (prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
									|| prj.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))) {
						if (!existInEmm) {
							Personnel personnel = new Personnel();
							User user = userDao.findOne(userId);
							personnel.setName(user.getAccount());
							TeamMember teamCreator = teamMemberService.findMemberByTeamIdAndMemberType(teamId,
									TEAMREALTIONSHIP.CREATE);
							User teamCrt = userDao.findOne(teamCreator.getUserId());
							personnel.setCreatorId(teamCrt.getAccount());
							personnel.setTeamGroupId(teamDao.findOne(teamId).getUuid());
							personnel.setMobileNo(user.getCellphone());
							personnel.setEmail(user.getAccount());
							personnel.setGroupName(teamDao.findOne(teamId).getName());
							String token = "";
							String[] params = new String[2];
							Enterprise enterprise = tenantFacade.getEnterpriseByShortName(prj.getBizCompanyId());
							params[0] = enterprise.getId().toString();
							params[1] = "dev";
							token = TokenUtilProduct.getToken(enterprise.getEntkey(), params);

							log.info("user login --> project :" + prj.getId() + "," + prj.getName()
									+ "joined new perple  to sync EMM-->userId:" + userId + ",名字:"
									+ personnel.getName());
							String flag = "";
							if (serviceFlag.equals("online")) {// 线上版本
								flag = personnelFacade.createAdminUser(token, personnel);
							} else if (serviceFlag.equals("enterprise")) {// 企业版本
								flag = personnelFacade.createTeamUser(token, personnel);
							}

							log.info("projectMember sync to emm return :" + flag);
							if (StringUtils.isNotBlank(flag)) {
								throw new RuntimeException("用户登录协同,同意加入被邀请的团队,此时添加EMM成员时候失败," + flag);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 * @describe 获取工作台集合 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年11月9日 上午10:28:39 <br>
	 * @param pageable
	 * @param match
	 * @param loginUserId
	 * @return <br>
	 * @throws ParseException
	 * @returnType Map<String,Object>
	 * 
	 */
	@Cacheable(value="ProjectService_getWorkPlatList",key="#loginUserId+'_'+#pageable.pageNumber+'_'+"
			+ "#matchObj.status+'_'+#matchObj.type +'_'+ #matchObj.categoryId  +'_'+#matchObj.bizLicense  +'_'+ #matchObj.teamId +'_'+#matchObj.teamName   +'_'+ #matchObj.creator +'_'+ #matchObj.actor +'_'+#matchObj.projectName  +'_'+ #matchObj.teamName +'_'+#matchObj.createdAtStart  +'_'+#matchObj.createdAtEnd  +'_'+ #matchObj.palnAtStart +'_'+#matchObj.palnAtEnd")
	public Map<String, Object> getWorkPlatList(Pageable pageable, Match4Project matchObj, long loginUserId)
			throws ParseException {

		long beginWorkPlatTime = System.currentTimeMillis();

		log.info(this.getClass().getSimpleName() + " Method getWorkPlatList is called. loginUserId = " + loginUserId);

		final List<Project> pList = new ArrayList<Project>();

		// 状态筛选
		String statusStr = "";
		List<PROJECT_STATUS> status = matchObj.getStatus();
		if (null == status) {
			status = new ArrayList<>();
			status.add(PROJECT_STATUS.ONGOING);
		}
		for (PROJECT_STATUS sta : status) {
			statusStr += sta.ordinal() + ",";
		}
		statusStr += "-1";
		// 类型筛选
		String typeStr = "";
		List<PROJECT_TYPE> type = matchObj.getType();
		if (null == type) {
			typeStr = null;
		} else {
			for (PROJECT_TYPE sta : type) {
				typeStr += sta.ordinal() + ",";
			}
			typeStr += "-1";
		}
		// 分类筛选
		String categoryStr = "";
		List<Long> categoryId = matchObj.getCategoryId();
		if (null == categoryId) {
			categoryStr = null;
		} else {
			for (Long sta : categoryId) {
				categoryStr += sta + ",";
			}
			categoryStr += "-1";
		}
		//授权情况
		String bizLicenseStr = "";
		if(matchObj.getBizLicense() == null) {
			bizLicenseStr = "0,1,2,3";
		}else if(matchObj.getBizLicense().size()==1){
			if(matchObj.getBizLicense().get(0)==PROJECT_BIZ_LICENSE.AUTHORIZED){
				bizLicenseStr = "0";
			}else if(matchObj.getBizLicense().get(0)==PROJECT_BIZ_LICENSE.NOT_AUTHORIZED){
				bizLicenseStr = "1";
			}else if(matchObj.getBizLicense().get(0)==PROJECT_BIZ_LICENSE.BINDING){
				bizLicenseStr = "2";
			}else if(matchObj.getBizLicense().get(0)==PROJECT_BIZ_LICENSE.UNBINDING){
				bizLicenseStr = "3";
			}
		}else{
			int i=0;
			for(PROJECT_BIZ_LICENSE biz:matchObj.getBizLicense()){
				if(i==0){
					bizLicenseStr+=biz.ordinal();
				}else{
					bizLicenseStr+=","+biz.ordinal();
				}
				i++;
			}
		}
		// 团队筛选
		List<Long> teamIds = new ArrayList<>();
		teamIds.add(-99L);
		if (null != matchObj.getTeamId()) {
			teamIds.add(matchObj.getTeamId());
		}
		List<Long> pIds = this.getProjectIdsByTeam(teamIds, matchObj.getTeamName());

		StringBuffer roleIds = new StringBuffer();
		roleIds.append(Cache.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.CREATOR).getId());
		roleIds.append("," + Cache.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.ADMINISTRATOR).getId());

		StringBuffer querySql = new StringBuffer();
		querySql.append("( p.id IN ( " + "SELECT DISTINCT projectId FROM T_PROJECT_MEMBER pm "
				+ "WHERE pm.userId = %d AND pm.type != %d AND pm.del = %d " + ") ");// 用户相关的项目，具体可以为用户创建的，或用户参与的
		querySql.append(" OR p.id in( "
				+ "SELECT tp.id FROM T_PROJECT tp LEFT JOIN T_TEAM_MEMBER ttm ON tp.teamId = ttm.teamId "
				+ "LEFT JOIN T_TEAM_AUTH tta ON ttm.id = tta.memberId "
				+ "WHERE tta.roleId IN (%s) AND tta.del = %d AND ttm.del = %d " + "AND ttm.userId = %d AND tp.del = %d "
				+ ")" + ")");

		// 查询项目列表中第一个项目的projectId的sql语句
		StringBuffer firstSql = new StringBuffer();
		firstSql.append("SELECT p.id FROM T_PROJECT p  WHERE ").append(querySql.toString())
				.append(" AND p.del =%d AND p.status in (%s) ORDER BY p.createdAt DESC LIMIT 0,1 ");

		// 类型标记
		if (null != typeStr) {
			querySql.append(" AND p.type in (" + typeStr + ") ");
		}
		// 分类标记
		if (null != categoryStr) {
			querySql.append(" AND p.categoryId in (" + categoryStr + ") ");
		}
		//授权情况
		querySql.append(" and p.bizLicense in(").append(bizLicenseStr).append(") ");
		// 创建者标记
		if (null != matchObj.getCreator()) {// 下面格式化SQL使用string.format所以此处应该是两个%
											// (u.userName like '%%张琪%%')
			querySql.append(
					" AND p.id in (select DISTINCT pm.projectId FROM T_PROJECT_MEMBER pm left join T_USER u on u.id = pm.userId where u.userName like '%"
							+ matchObj.getCreator() + "%' and pm.del=0 and pm.type=0 ) ");
		}
		// 参与者标记
		if (null != matchObj.getActor()) {
			String actorRoleIds = "";
			actorRoleIds += Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR).getId();
			actorRoleIds += "," + Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.MEMBER).getId();
			actorRoleIds += "," + Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.OBSERVER).getId();
			querySql.append(
					" AND p.id in (select DISTINCT pm.projectId FROM T_PROJECT_MEMBER pm left join T_USER u on u.id = pm.userId left join T_PROJECT_AUTH ta on ta.memberId=pm.id where u.userName like '%"
							+ matchObj.getActor() + "%' and pm.del=0 AND ta.del=0 and ta.roleId in (" + actorRoleIds + ") ) ");
		}
		// 项目标记
		String projectName = matchObj.getProjectName();
		if (null != projectName) {
			// Tools.sqlFormatPerCent(sql)的作用是将projectName中包含的%替换为\\%%，因为在String.format进行格式化，会有格式化异常出现，所以在此处进行了转换
			projectName = projectName.substring(projectName.indexOf("%") + 1, projectName.lastIndexOf("%"));
			projectName = "%" + Tools.sqlFormatPerCent(projectName) + "%";
			querySql.append(" AND p.name like '%" + projectName + "%' ");
		}
		// 团队标记
		if (null != matchObj.getTeamName()) {
			querySql.append(" AND p.teamId in (").append("select id from T_TEAM where del=0 and name like '%")
					.append(matchObj.getTeamName()).append("%')");
		}
		// 团队标记
		if (!pIds.isEmpty()) {
			String pIdsStr = "";
			for (Long sta : pIds) {
				pIdsStr += sta + ",";
			}
			pIdsStr += "-1";
			querySql.append(" AND p.id in (" + pIdsStr + ") ");
		}

		querySql.append(" AND p.del =%d AND p.status in (%s) ");// 删除标记 和状态标记

		String querysql = "SELECT p.* FROM T_PROJECT p left join (SELECT MAX(endDate) endDate,projectId FROM T_PROCESS WHERE del=0 GROUP BY projectId) t on p.id=t.projectId WHERE "
				+ querySql.toString();

		if (StringUtils.isNotBlank(matchObj.getCreatedAtStart())) {
			querysql += " AND DATE_FORMAT(p.createdAt,'%%Y-%%m-%%d') >= '" + matchObj.getCreatedAtStart() + "' ";
		}
		if (StringUtils.isNotBlank(matchObj.getCreatedAtEnd())) {
			querysql += " AND DATE_FORMAT(p.createdAt,'%%Y-%%m-%%d') <= '" + matchObj.getCreatedAtEnd() + "' ";
		}
		if (matchObj.getParentId() != null && matchObj.getParentId() > 1) {
			querysql += " AND p.parentId = " + matchObj.getParentId();
		}
		
		if (StringUtils.isNotBlank(matchObj.getPalnAtStart())) {
			querysql += " AND DATE_FORMAT(t.endDate,'%%Y-%%m-%%d') >= '" + matchObj.getPalnAtStart() + "' ";
		}
		if (StringUtils.isNotBlank(matchObj.getPalnAtEnd())) {
			querysql += " AND DATE_FORMAT(t.endDate,'%%Y-%%m-%%d') <= '" + matchObj.getPalnAtEnd() + "' ";
		}
		querysql += " ORDER BY p.createdAt DESC ";
		String parent =",parent.projectName parentName,parent.createdAt parentCreatime,parent.userId,usr.userName";
//		String parentLeft="LEFT JOIN T_PROJECT_PARENT parent ON a.parentId=parent.id LEFT JOIN T_USER usr ON usr.id=parent.userId";
		String parentLeft="LEFT JOIN T_PROJECT_PARENT parent ON a.parentId=parent.id LEFT JOIN T_USER usr ON usr.id=a.creatorId";
		querysql = "SELECT a.*,b.sort "+parent+" FROM (" + querysql
				+ ") a LEFT JOIN (select * from T_PROJECT_SORT WHERE userId=%d ) b ON a.id=b.projectId "+parentLeft+"  ORDER BY b.sort DESC limit %d,%d";

		String querycount = "SELECT count(1) FROM T_PROJECT p left join (SELECT MAX(endDate) endDate,projectId FROM T_PROCESS WHERE del=0 GROUP BY projectId) t on p.id=t.projectId WHERE "
				+ querySql.toString();
		if (StringUtils.isNotBlank(matchObj.getCreatedAtStart())) {
			querycount += " AND DATE_FORMAT(p.createdAt,'%%Y-%%m-%%d') >= '" + matchObj.getCreatedAtStart()+ "' ";
		}
		if (StringUtils.isNotBlank(matchObj.getCreatedAtEnd())) {
			querycount += " AND DATE_FORMAT(p.createdAt,'%%Y-%%m-%%d') <= '" + matchObj.getCreatedAtEnd() + "' ";
		}
		if (StringUtils.isNotBlank(matchObj.getPalnAtStart())) {
			querycount += " AND DATE_FORMAT(t.endDate,'%%Y-%%m-%%d') >= '" + matchObj.getPalnAtStart()+"' ";
		}
		if (StringUtils.isNotBlank(matchObj.getPalnAtEnd())) {
			querycount += " AND DATE_FORMAT(t.endDate,'%%Y-%%m-%%d') <= '" + matchObj.getPalnAtEnd() + "' ";
		}
		if (matchObj.getParentId() != null && matchObj.getParentId() > 1) {
			querycount += " AND p.parentId ="+matchObj.getParentId()+"  ";
		}
		String sql = String.format(querysql, loginUserId, PROJECT_MEMBER_TYPE.INVITEE.ordinal(),
				DELTYPE.NORMAL.ordinal(), roleIds.toString(), DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),
				loginUserId, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(), statusStr, loginUserId,
				pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize());

		this.jdbcTpl.query(sql, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Project p = new Project();
				p.setCreatedAt(rs.getTimestamp("createdAt"));
				p.setCategoryId(rs.getLong("categoryId"));
				ProjectCategory pc = projectCategoryDao.findOne(p.getCategoryId());
				if (null != pc) {
					p.setCategoryName(pc.getName());
				}
				p.setTeamId(rs.getLong("teamId"));
				p.setProgress(rs.getInt("progress"));
				p.setType(PROJECT_TYPE.values()[(int) rs.getLong("type")]);
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setStatus(PROJECT_STATUS.values()[(int) rs.getLong("status")]);
				p.setBizLicense(PROJECT_BIZ_LICENSE.values()[(int) rs.getLong("bizLicense")]);
				p.setBizCompanyId(rs.getString("bizCompanyId"));
				p.setUpdatedAt(rs.getTimestamp("updatedAt"));
				p.setBizCompanyName(rs.getString("bizCompanyName"));
				p.setSort(rs.getLong("sort"));
				p.setUserName(rs.getString("userName"));
				p.setParentName(rs.getString("parentName"));
				p.setParentCreatime(rs.getString("parentCreatime"));
				pList.add(p);
			}
		});
		log.info(String.format("search project querysql:[%s]", sql));

		String sql1 = String.format(querycount, loginUserId, PROJECT_MEMBER_TYPE.INVITEE.ordinal(),
				DELTYPE.NORMAL.ordinal(), roleIds.toString(), DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),
				loginUserId, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(), statusStr);

		long total = this.jdbcTpl.queryForLong(sql1);

		String firstSql1 = String.format(firstSql.toString(), loginUserId, PROJECT_MEMBER_TYPE.INVITEE.ordinal(),
				DELTYPE.NORMAL.ordinal(), roleIds.toString(), DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),
				loginUserId, DELTYPE.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(), statusStr);
		long firstProjectId = 0;
		List<Map<String, Object>> listMap = jdbcTpl.queryForList(firstSql1.toString());
		if (null != listMap && listMap.size() > 0) {
			firstProjectId = Long.parseLong(listMap.get(0).get("id").toString());
		}
		long projectListSearchTime = System.currentTimeMillis();
		log.info(String.format("search project querycount:[%s]", sql1));
		log.info(String.format("search project time:[%s]ms", projectListSearchTime - beginWorkPlatTime));

		List<Map<String, Object>> projectMapList = new ArrayList<>();

		// 遍历记录进行扩展
		if (pList != null && pList.size() > 0) {
			for (Project p : pList) {

				// 扩展团队信息
				Team team = teamDao.findOne(p.getTeamId());
				if (team != null) {
					p.setTeamName(team.getName());
				}
				ProjectMember pm = projectMemberDao.findByProjectIdAndTypeAndDel(p.getId(), PROJECT_MEMBER_TYPE.CREATOR,
						DELTYPE.NORMAL);
				if (null != pm) {
					User userCreator = userDao.findOne(pm.getUserId());
					if (null != userCreator && StringUtils.isNotBlank(userCreator.getUserName())) {
						p.setCreator(userCreator.getUserName());
					}
				}
				// 扩展流程信息
				List<Process> plist = processDao.findByProjectIdAndDel(p.getId(), DELTYPE.NORMAL);

				List<Map<String, Object>> processMapList = new ArrayList<>();
				for (Process process : plist) {
					long memberTotal = processMemberDao.countByProcessIdAndDel(process.getId(), DELTYPE.NORMAL);
					process.setMemberTotal(memberTotal);

					Map<String, Object> pMap = new HashMap<>();
					pMap.put("object", process);
					processMapList.add(pMap);
				}

				Map<String, Object> processMap = new HashMap<>();
				processMap.put("list", processMapList);

				Map<String, Object> pMap = new HashMap<>();
				pMap.put("object", p);
				pMap.put("processList", processMap);
				projectMapList.add(pMap);

			}
		}
		long sSearchTime = System.currentTimeMillis();
		log.info(String.format("search permissions time:[%s]ms", sSearchTime - projectListSearchTime));

		Map<String, Object> retMap = new HashMap<>();
		retMap.put("message", projectMapList);
		retMap.put("total", total);
		retMap.put("status", "success");
		if (projectMapList != null && projectMapList.size() > 0) {
			Project p = (Project) projectMapList.get(0).get("object");
			long maxNum = this.getMaxProjectSort(loginUserId);
			if (maxNum == 0 && p.getSort() == 0 && p.getId() == firstProjectId) {
				// 刚开始进入初始化项目列表，如果项目列表中的项目没有进行过置顶操作，那么默认将项目列表中创建时间最大的那个项目默认显示置顶
				retMap.put("position", 1);
			} else {
				// 如果项目列表中的项目进行过置顶操作，那么如果项目sort是最大值，则显示置顶，否则显示不置顶
				if (p.getSort() > 0 && p.getSort() == maxNum) {
					retMap.put("position", 1);
				} else {
					retMap.put("position", 0);
				}
			}

		}
		return retMap;
	}

	public Project saveProject(Project p) {
		return this.projectDao.save(p);

	}

	public Map<String, Object> addProjectMemberFromTeam(long projectId, long loginUserId, List<String> userStrList) throws Exception {

		Map<String, Object> map = new HashMap<>();
		StringBuffer userNameStrs = new StringBuffer("");
		List<GitAuthVO> listAuthApp = new ArrayList<GitAuthVO>();
		List<ProjectMember> members = new ArrayList<>();
		List<Long> memberUserIdArr = new ArrayList<>();
		final List<App> projectAppList = new ArrayList<App>();// 此项目下的应用
		String appInfoStr = "select a.appcanAppId,a.appcanAppKey,u.account from T_APP a left join T_USER u on a.userId = u.id where a.projectId="+projectId;
		jdbcTpl.query(appInfoStr, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				App app = new App();
				app.setUserName(rs.getString("account"));
				app.setAppcanAppId(rs.getString("appcanAppId"));
				app.setAppcanAppKey(rs.getString("appcanAppKey"));
				projectAppList.add(app);
			}
		});
		Project project = this.getProject(projectId);
		TeamMember teamCreator = this.teamMemberService
				.findMemberByTeamIdAndMemberType(project.getTeamId(),
						TEAMREALTIONSHIP.CREATE);
		User teamCrt = userDao.findOne(teamCreator.getUserId());
		Team team = teamDao.findOne(project.getTeamId());
		
		Enterprise enterprise = null;
		if(null!=project.getBizCompanyId() && !serviceFlag.equals("enterpriseEmm3")){
			enterprise = tenantFacade
			.getEnterpriseByShortName(project.getBizCompanyId());
		}
		String regex = "#[A-Z]*";
		StringBuffer every = new StringBuffer("-999");
		for(int i=0;i<userStrList.size();i++){
			//TODO 先过滤一遍,将不存在用户,还要已经是成员的用户过滤掉
			 every.append(",").append(userStrList.get(i).replaceAll(regex, ""));
		}
		final List<User> userList = new ArrayList<User>();//想要添加的人的信息列表
		this.jdbcTpl.query("select id,userName,account,cellphone from T_USER where del=0 and id in ("+every.toString()+")", new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				User user = new User();
				user.setId(rs.getLong("id"));
				user.setUserName(rs.getString("userName"));
				user.setAccount(rs.getString("account"));
				user.setCellphone(rs.getString("cellphone"));
				userList.add(user);
			}
		});
		if(userList.size()!=userStrList.size()){
			throw new RuntimeException("参数不合法");
		}
		List<ProjectMember> listMe = projectMemberService.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);//项目下的成员
		boolean skip = false;
		for (String userStr : userStrList) {
			skip = false;
			String[] items = userStr.split("#");
			if (items.length != 2) {
				continue;
			}
			String userId = items[0];
			String roleTypeStr = items[1].toUpperCase();
			if (!roleTypeStr.equals(ROLE_TYPE.ADMINISTRATOR.name()) && !roleTypeStr.equals(ROLE_TYPE.MEMBER.name())
					&& !roleTypeStr.equals(ROLE_TYPE.OBSERVER.name())) {
				continue;
			}
			
			for(ProjectMember pm:listMe){
				if(userId.equals(pm.getUserId()+"")){
					//如果要添加的人已经在项目下了,则跳过此人
					skip = true;
					break;
				}
			}
			if(skip){
				continue;
			}
			User user = null;
			for(User u:userList){
				if(u.getId().toString().equals(userId)){
					user = u;
					break;
				}
			}
			if(null==user){
				log.info("params userStrList:"+userStrList.toArray());
				throw new RuntimeException("参数不合法");
			}
				ProjectMember member = new ProjectMember();
				member.setProjectId(projectId);
				member.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
				member.setUserId(user.getId());
				memberUserIdArr.add(user.getId());
				this.saveProjectMember(member, user);
				userNameStrs.append((null == user.getUserName()) ? user.getAccount() : user.getUserName() + ",");
				ProjectAuth auth = new ProjectAuth();
				auth.setMemberId(member.getId());
				Role role = Cache.getRole(ENTITY_TYPE.PROJECT + "_" + roleTypeStr);
				if (role == null) {
					continue;
				}
				auth.setRoleId(role.getId());
				this.saveProjectAuth(auth);

				// ------------------增加对应的git权限-----------------------------begin----
				boolean master = false;
				boolean branch = false;// 是否需要分享对应的git权限
				List<Permission> listPer = role.getPermissions();
				if (null != listPer && listPer.size() > 0) {
					for (Permission p : listPer) {
						if (p.getEnName().equals("code_upload_master_code")) {
							master = true;
							break;
						} else if (p.getEnName().equals("code_update_branch")) {
							branch = true;break;
						}
					}
				}
				
				if (master) {// 此人有主干权限的话
					if (null != projectAppList && projectAppList.size() > 0) {
						for (App app : projectAppList) {
							if (app.getUserId() != user.getId().longValue()) {
								GitAuthVO vo = new GitAuthVO();
								vo.setAuthflag("all");
								vo.setPartnername(user.getAccount());
								vo.setUsername(app.getUserName());
								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
								vo.setProject(encodeKey.toLowerCase());
								vo.setRef("master");
								vo.setProjectid(app.getAppcanAppId());
								listAuthApp.add(vo);
							}

						}
					}
				} else if (branch) {// 此人有分支权限的话
					for (App app : projectAppList) {
						if (app.getUserId() != user.getId().longValue()) {
							GitAuthVO vo = new GitAuthVO();
							vo.setAuthflag("allbranch");
							vo.setPartnername(user.getAccount());
							vo.setUsername(app.getUserName());
							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
							vo.setProject(encodeKey.toLowerCase());
							vo.setProjectid(app.getAppcanAppId());
							listAuthApp.add(vo);
						}

					}
				}

				members.add(member);

				// ------------------增加对应的git权限-----------------------------end----

				
				if (!project.getType().equals(PROJECT_TYPE.TEAM)) {
					continue;
				}
				if (!project.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
						&& 
						!project.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING)) {
					continue;
				}
				
			   if(this.isTeamBind(project.getTeamId()) && (project.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED) || project.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING))){//如果是EMM3.3则不走下面的方法
			    // 添加项目成员到emm4.0 
					Personnel personnel = new Personnel();
					personnel.setName(user.getAccount());
					
					personnel.setCreatorId(teamCrt.getAccount());
					personnel.setTeamGroupId(team.getUuid());
					personnel.setMobileNo(user.getCellphone());
					personnel.setEmail(user.getAccount());
					personnel.setGroupName(team.getName());
					personnel.setTeamDevAddress(xietongHost);
					personnel.setTeamType(project.getType().name().equals("TEAM")?"teamProject":"personnelProject");//个人项目还是团队项目
					String token = "";
					String[] params = new String[2];

					params[0] = enterprise.getId().toString();
					params[1] = "dev";
					token = TokenUtilProduct.getToken(enterprise.getEntkey(),
							params);
					log.info("teamMember sync to EMM-->" + personnel.getName());
					String flag = "";
					if(serviceFlag.equals("online")){//线上版本
						flag = personnelFacade.createAdminUser(token, personnel);
					}else if(serviceFlag.equals("enterprise")){//企业版
						flag = personnelFacade.createTeamUser(token, personnel);
					}
					log.info("teamMember sync to EMM-->:" + flag);
					if (StringUtils.isNotBlank(flag)) {
						throw new RuntimeException("从团队添加成员进入项目,此时添加EMM成员时候失败,"+ flag);
					}
			}
		}
		Long startAuthTime=System.currentTimeMillis();
		Map<String, String> mapResult = appService.addGitAuth(listAuthApp);
		Long endAuthTime=System.currentTimeMillis();
		log.info(String.format("time [%s]", endAuthTime-startAuthTime));
		log.info(userStrList + " join project from team: projectId->" + projectId + ",and shareallgit->"
				+ (null == mapResult ? null : mapResult.toString()));

		if (userNameStrs.length() > 1) {
			userNameStrs.deleteCharAt(userNameStrs.length() - 1);
		}
		map.put("dynamicUserNames", userNameStrs);
		map.put("memberUserIdArr", memberUserIdArr);
		map.put("size", members.size());

		return map;
	}

	
	/**
	 * 方法从addProjectMemberFromTeam拷贝过来,由于线上EMM4.0服务停掉,调用dubbo服务时候报错,所以改为不调用dubbo服务去同步人员
	 * 2017.06.20
	 * @param projectId
	 * @param loginUserId
	 * @param userStrList
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> addProjectMember(long projectId, long loginUserId, List<String> userStrList) throws Exception {

		Map<String, Object> map = new HashMap<>();
		StringBuffer userNameStrs = new StringBuffer("");
		List<GitAuthVO> listAuthApp = new ArrayList<GitAuthVO>();
		List<ProjectMember> members = new ArrayList<>();
		List<Long> memberUserIdArr = new ArrayList<>();
		final List<App> projectAppList = new ArrayList<App>();// 此项目下的应用
		String appInfoStr = "select a.appcanAppId,a.appcanAppKey,u.account from T_APP a left join T_USER u on a.userId = u.id where a.projectId="+projectId;
		jdbcTpl.query(appInfoStr, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				App app = new App();
				app.setUserName(rs.getString("account"));
				app.setAppcanAppId(rs.getString("appcanAppId"));
				app.setAppcanAppKey(rs.getString("appcanAppKey"));
				projectAppList.add(app);
			}
		});
		Project project = this.getProject(projectId);
		TeamMember teamCreator = this.teamMemberService
				.findMemberByTeamIdAndMemberType(project.getTeamId(),
						TEAMREALTIONSHIP.CREATE);
		User teamCrt = userDao.findOne(teamCreator.getUserId());
		Team team = teamDao.findOne(project.getTeamId());
		
		String regex = "#[A-Z]*";
		StringBuffer every = new StringBuffer("-999");
		for(int i=0;i<userStrList.size();i++){
			//TODO 先过滤一遍,将不存在用户,还要已经是成员的用户过滤掉
			 every.append(",").append(userStrList.get(i).replaceAll(regex, ""));
		}
		final List<User> userList = new ArrayList<User>();//想要添加的人的信息列表
		this.jdbcTpl.query("select id,userName,account,cellphone from T_USER where del=0 and id in ("+every.toString()+")", new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				User user = new User();
				user.setId(rs.getLong("id"));
				user.setUserName(rs.getString("userName"));
				user.setAccount(rs.getString("account"));
				user.setCellphone(rs.getString("cellphone"));
				userList.add(user);
			}
		});
		if(userList.size()!=userStrList.size()){
			throw new RuntimeException("参数不合法");
		}
		List<ProjectMember> listMe = projectMemberService.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);//项目下的成员
		boolean skip = false;
		for (String userStr : userStrList) {
			skip = false;
			String[] items = userStr.split("#");
			if (items.length != 2) {
				continue;
			}
			String userId = items[0];
			String roleTypeStr = items[1].toUpperCase();
			if (!roleTypeStr.equals(ROLE_TYPE.ADMINISTRATOR.name()) && !roleTypeStr.equals(ROLE_TYPE.MEMBER.name())
					&& !roleTypeStr.equals(ROLE_TYPE.OBSERVER.name())) {
				continue;
			}
			
			for(ProjectMember pm:listMe){
				if(userId.equals(pm.getUserId()+"")){
					//如果要添加的人已经在项目下了,则跳过此人
					skip = true;
					break;
				}
			}
			if(skip){
				continue;
			}
			User user = null;
			for(User u:userList){
				if(u.getId().toString().equals(userId)){
					user = u;
					break;
				}
			}
			if(null==user){
				log.info("params userStrList:"+userStrList.toArray());
				throw new RuntimeException("参数不合法");
			}
				ProjectMember member = new ProjectMember();
				member.setProjectId(projectId);
				member.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
				member.setUserId(user.getId());
				memberUserIdArr.add(user.getId());
				this.saveProjectMember(member, user);
				userNameStrs.append((null == user.getUserName()) ? user.getAccount() : user.getUserName() + ",");
				ProjectAuth auth = new ProjectAuth();
				auth.setMemberId(member.getId());
				Role role = Cache.getRole(ENTITY_TYPE.PROJECT + "_" + roleTypeStr);
				if (role == null) {
					continue;
				}
				auth.setRoleId(role.getId());
				this.saveProjectAuth(auth);

				// ------------------增加对应的git权限-----------------------------begin----
				boolean master = false;
				boolean branch = false;// 是否需要分享对应的git权限
				List<Permission> listPer = role.getPermissions();
				if (null != listPer && listPer.size() > 0) {
					for (Permission p : listPer) {
						if (p.getEnName().equals("code_upload_master_code")) {
							master = true;
							break;
						} else if (p.getEnName().equals("code_update_branch")) {
							branch = true;break;
						}
					}
				}
				
				if (master) {// 此人有主干权限的话
					if (null != projectAppList && projectAppList.size() > 0) {
						for (App app : projectAppList) {
							if (app.getUserId() != user.getId().longValue()) {
								GitAuthVO vo = new GitAuthVO();
								vo.setAuthflag("all");
								vo.setPartnername(user.getAccount());
								vo.setUsername(app.getUserName());
								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
								vo.setProject(encodeKey.toLowerCase());
								vo.setRef("master");
								vo.setProjectid(app.getAppcanAppId());
								listAuthApp.add(vo);
							}

						}
					}
				} else if (branch) {// 此人有分支权限的话
					for (App app : projectAppList) {
						if (app.getUserId() != user.getId().longValue()) {
							GitAuthVO vo = new GitAuthVO();
							vo.setAuthflag("allbranch");
							vo.setPartnername(user.getAccount());
							vo.setUsername(app.getUserName());
							String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
							vo.setProject(encodeKey.toLowerCase());
							vo.setProjectid(app.getAppcanAppId());
							listAuthApp.add(vo);
						}

					}
				}

				members.add(member);

				// ------------------增加对应的git权限-----------------------------end----

				
				if (!project.getType().equals(PROJECT_TYPE.TEAM)) {
					continue;
				}
				if (!project.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
						&& 
						!project.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING)) {
					continue;
				}
				//此处去掉了同步人员至EMM4
		}
		Long startAuthTime=System.currentTimeMillis();
		Map<String, String> mapResult = appService.addGitAuth(listAuthApp);
		Long endAuthTime=System.currentTimeMillis();
		log.info(String.format("time [%s]", endAuthTime-startAuthTime));
		log.info(userStrList + " join project from team: projectId->" + projectId + ",and shareallgit->"
				+ (null == mapResult ? null : mapResult.toString()));

		if (userNameStrs.length() > 1) {
			userNameStrs.deleteCharAt(userNameStrs.length() - 1);
		}
		map.put("dynamicUserNames", userNameStrs);
		map.put("memberUserIdArr", memberUserIdArr);
		map.put("size", members.size());

		return map;
	}
	/**
	 * 项目查询列表中需要查询出和我相关的项目的创建者
	 * 
	 * @user jingjian.wu
	 * @date 2016年2月27日 下午5:28:11
	 */
	public Map<String, Object> getAllCreatorInProjectList(Match4Project matchObj, long loginUserId, String projName,
			String creator, String begin, String end) {
		List<PROJECT_MEMBER_TYPE> memberType = matchObj.getMemberType();
		List<PROJECT_BIZ_LICENSE> bizLicense = matchObj.getBizLicense();
		List<PROJECT_STATUS> status = matchObj.getStatus();
		List<PROJECT_TYPE> type = matchObj.getType();
		List<Long> categoryId = matchObj.getCategoryId();
		Long teamId = matchObj.getTeamId();
		StringBuffer categoryIdsStr = new StringBuffer("");
		if (null != matchObj.getCategoryId()) {
			for (Long cateId : categoryId)
				categoryIdsStr.append(cateId).append(",");
		}
		if (categoryIdsStr.length() > 0) {
			categoryIdsStr.deleteCharAt(categoryIdsStr.length() - 1);
		}
		if (memberType == null) {
			memberType = new ArrayList<PROJECT_MEMBER_TYPE>();
			memberType.add(PROJECT_MEMBER_TYPE.CREATOR);
			memberType.add(PROJECT_MEMBER_TYPE.PARTICIPATOR);
		}

		String bizLicenseStr = "";
		if (bizLicense == null) {
			bizLicense = new ArrayList<PROJECT_BIZ_LICENSE>();
			bizLicense.add(PROJECT_BIZ_LICENSE.AUTHORIZED);
			bizLicense.add(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
			bizLicense.add(PROJECT_BIZ_LICENSE.BINDING);
			bizLicense.add(PROJECT_BIZ_LICENSE.UNBINDING);
			bizLicenseStr = "0,1,2,3";
		}else if(bizLicense.size()==1){
			if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.AUTHORIZED){
				bizLicenseStr = "0";
			}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.NOT_AUTHORIZED){
				bizLicenseStr = "1";
			}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.BINDING){
				bizLicenseStr = "2";
			}else if(bizLicense.get(0)==PROJECT_BIZ_LICENSE.UNBINDING){
				bizLicenseStr = "3";
			}
		}else{
			int i=0;
			for(PROJECT_BIZ_LICENSE biz:bizLicense){
				if(i==0){
					bizLicenseStr+=biz.ordinal();
				}else{
					bizLicenseStr+=","+biz.ordinal();
				}
				i++;
			}
		}
		String statusStr = "";
		if (status == null) {
			status = new ArrayList<PROJECT_STATUS>();
			status.add(PROJECT_STATUS.FINISHED);
			status.add(PROJECT_STATUS.ONGOING);
			statusStr = "0,1";
		} else if (status.size() == 1) {
			if (status.get(0) == PROJECT_STATUS.FINISHED) {
				statusStr = "0";
			} else if (status.get(0) == PROJECT_STATUS.ONGOING) {
				statusStr = "1";
			}
		} else {
			statusStr = "0,1";
		}
		String typeStr = "";
		if (type == null) {
			type = new ArrayList<PROJECT_TYPE>();
			type.add(PROJECT_TYPE.PERSONAL);
			type.add(PROJECT_TYPE.TEAM);
			typeStr = "0,1";
		} else if (type.size() == 1) {
			if (type.get(0) == PROJECT_TYPE.PERSONAL) {
				typeStr = "0";
			} else if (type.get(0) == PROJECT_TYPE.TEAM) {
				typeStr = "1";
			}
		} else {
			typeStr = "0,1";
		}

		final List<Long> prjIdList = new ArrayList<Long>();// 我能查看到的项目的id列表
		List<Long> roleIds = new ArrayList<>();
		roleIds.add(Cache.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.CREATOR).getId());
		roleIds.add(Cache.getRole(ENTITY_TYPE.TEAM + "_" + ROLE_TYPE.ADMINISTRATOR).getId());
		List<Long> projectIds = null;

		if (null != teamId && teamId != -1) {
			// 团队下的项目
			List<Long> projectIdFromTeam = this.projectDao.findLongByTeamIdAndRoleIdAndDel(loginUserId, teamId, roleIds,
					DELTYPE.NORMAL);
			projectIds = this.projectDao.findByTeamAndUserIdAndTypeAndDel(teamId, loginUserId, memberType,
					DELTYPE.NORMAL);
			if (memberType.size() == 2) {
				projectIds.addAll(projectIdFromTeam);
			}

			// 只选择我创建的或者我参与的 不包括团队项目
		} else {
			// 我参与 我创建的项目主键
			projectIds = this.projectMemberDao.findByUserIdAndTypeIn(loginUserId, memberType);
			// 我创建 我管理的团队
			List<Long> teamIds = this.teamMemberDao.findByUserIdAndRoleIdAndDel(loginUserId, roleIds, DELTYPE.NORMAL);
			if (null != teamIds && teamIds.size() > 0) {
				// 我的查看的项目
				List<Long> projectIdss = this.projectDao.findByTeamIdInAndDel(teamIds, DELTYPE.NORMAL);
				// 合并我的项目和我能查看的项目
				if (memberType.size() == 2) {
					projectIds.addAll(projectIdss);
				} else {
					if (memberType.get(0).equals(PROJECT_MEMBER_TYPE.CREATOR)) {// 只选择我创建的
						List<Long> creatPrjIds = projectMemberDao.findByUserIdAndTypeAndProjectIdIn(
								PROJECT_MEMBER_TYPE.CREATOR, projectIdss, loginUserId);
						projectIds.addAll(creatPrjIds);
					} else {// 只选择我参与的
						List<Long> actorPrjIds = projectMemberDao.findByUserIdAndTypeAndProjectIdIn(
								PROJECT_MEMBER_TYPE.PARTICIPATOR, projectIdss, loginUserId);
						projectIds.addAll(actorPrjIds);
					}
				}
			}
		}

		// 项目主键去重
		Set<Long> set = new HashSet<>(projectIds);
		projectIds.clear();
		StringBuffer projectIdStr = new StringBuffer();
		Iterator<Long> it = set.iterator();
		while (it.hasNext()) {
			Long tmpPrjId = it.next();
			projectIds.add(tmpPrjId);
			projectIdStr.append(tmpPrjId).append(",");
		}

		// 项目id为空时 添加伪id
		if (null != projectIds && projectIds.isEmpty()) {
			projectIds.add(-99L);
		}
		projectIdStr.append("-99");

		// 执行查询
		StringBuffer baseSql = new StringBuffer();
		baseSql.append("select p.id from T_PROJECT p  ");
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" where p.id in(").append(projectIdStr).append(") ").append(" and p.bizLicense in(")
				.append(bizLicenseStr).append(") ").append(" and p.status in(").append(statusStr).append(")")
				.append(" and p.type in ( ").append(typeStr).append(")").append(" and p.del=0 ");
		if (StringUtils.isNotBlank(projName)) {
			sbSql.append(" and p.name like '%").append(projName).append("%'");
		}
		if (StringUtils.isNotBlank(begin)) {
			sbSql.append(" and p.createdAt>= '").append(begin).append("' ");
		}
		if (StringUtils.isNotBlank(end)) {
			sbSql.append(" and p.createdAt<='").append(end).append("' ");
		}
		if (categoryId != null) {
			sbSql.append(" and p.categoryId in(").append(categoryIdsStr).append(")");
		}
		if (StringUtils.isNotBlank(creator)) {
			sbSql.append(" and p.id in( ")
					.append("select distinct pm.projectId from T_PROJECT_MEMBER pm where pm.type=")
					.append(PROJECT_MEMBER_TYPE.CREATOR.ordinal())
					.append(" and pm.del=0 and pm.userId in (select u.id from T_USER u where u.account like '%")
					.append(creator).append("%' ").append(" or u.userName like '%").append(creator).append("%'")
					.append(" or u.email like '%").append(creator).append("%'").append(" or u.pinYinName like '%")
					.append(creator).append("%'").append(" or u.pinYinHeadChar like '%").append(creator).append("%'")
					.append(")").append(")");
		}
		sbSql.append(" order by p.createdAt desc ");

		String lastSql = baseSql.toString() + sbSql.toString();
		this.jdbcTpl.query(lastSql, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				prjIdList.add(rs.getLong("id"));
			}
		});

		List<User> creatorList = new ArrayList<User>();
		if (null != prjIdList && prjIdList.size() > 0) {
			creatorList = userDao.findCreatorForProjects(prjIdList);
		}
		return this.getSuccessMap(creatorList);
	}

	/**
	 * 重新计算项目进度和完成状态,完成时间
	 * 
	 * @user jingjian.wu
	 * @date 2016年2月26日 下午8:07:37
	 */
	public void updateProjProgressAndStatus(Long projectId) {
		if (null == projectId || projectId == -1) {
			log.info("update project progress but projectId is not found :" + projectId);
			return;
		}
		Project project = this.projectDao.findOne(projectId);
		if (null == project) {
			log.info("update project progress but projectId is not found :" + projectId);
			return;
		}
		int projProgress = this.getProjectProgressForInt(projectId);
		project.setProgress(projProgress);
		if (projProgress == 100) {
			if (null != project.getFinishDate()) {
				project.setStatus(PROJECT_STATUS.FINISHED);
				project.setFinishDate(new Timestamp(System.currentTimeMillis()));
			}
			this.projectDao.save(project);
		} else {
			project.setStatus(PROJECT_STATUS.ONGOING);
			project.setFinishDate(null);
			this.projectDao.save(project);
		}
	}

	/**
	 * teamIds首选需要默认加一个-99之类的,方法判断teamIds长度大于1才根据teamIds查询
	 * 补充说明:teamId存在,则根据teamId查,否则根据teamName查询
	 * 
	 * @param teamIds
	 * @param teamName
	 *            注意参数的teamName是需要传进来百分号的
	 * @return
	 */
	public List<Long> getProjectIdsByTeam(List<Long> teamIds, String teamName) {
		if (teamIds.size() > 1) {
			return this.projectDao.findByTeamIdInAndDel(teamIds, DELTYPE.NORMAL);
		} else if (StringUtils.isNotBlank(teamName)) {
			return this.projectDao.findByTeamNameAndDel(teamName, DELTYPE.NORMAL);
		} else {
			return new ArrayList<>();
		}
	}

	public List<Long> findByIdInAndNameLikeAndDel(List<Long> prjIds, String projName, DELTYPE delType) {
		return projectDao.findByIdInAndNameLikeAndDel(prjIds, projName, delType);
	}

	/**
	 * 新增或者编辑项目排序
	 * 
	 * @param userId
	 * @param projectId
	 * @user tingwei.yuan
	 * @date 2016/3/31
	 * @return
	 */
	@CacheEvict(value="ProjectService_getWorkPlatList",allEntries=true)//
	public long editProjectSort(long userId, long projectId) {
		// 1、获取当前登录用户是否已经对当前项目进行排序操作
		List<ProjectSort> list = this.projectSortDao.findByUserIdAndProjectIdAndDel(userId, projectId, DELTYPE.NORMAL);
		// 2、获取当前登录用户对项目排序的最大值
		String querysql = "SELECT IFNULL(MAX(sort),0) AS num FROM T_PROJECT_SORT WHERE userId = " + userId
				+ " AND del=0";
		long num = this.jdbcTpl.queryForLong(querysql);
		if (list != null && list.size() > 0) {
			ProjectSort projectSort = list.get(0);
			Long sort = projectSort.getSort();
			// 3、将比当前项目的的排序值大的排序项目减一，然后在将当前项目的sort值设置为最大值
			String sql = "UPDATE T_PROJECT_SORT a SET a.sort=a.sort-1 WHERE a.sort > " + sort + " AND userId=" + userId;
			this.jdbcTpl.update(sql);
			projectSort.setSort(num);
			projectSort.setUpdatedAt(new Timestamp(new Date().getTime()));
			this.projectSortDao.save(projectSort);
		} else {
			ProjectSort projectSort = new ProjectSort();
			projectSort.setUserId(userId);
			projectSort.setProjectId(projectId);
			if (num == 0) {
				// 4、如果num为0说明当前用户没有进行过置顶排序操作,默认初始化设置为1
				projectSort.setSort(1l);
			} else {
				projectSort.setSort(num + 1);
			}

			this.projectSortDao.save(projectSort);
		}
		return num;
	}

	/**
	 * 项目转移、删除校验用户名和密码
	 * 
	 * @param account
	 * @param password
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String ssoValidaton(String account, String password) throws ClientProtocolException, IOException {
		String resultStr = "";
		if (serviceFlag.equals("enterpriseEmm3")) {

			String sql = "select count(1) from T_USER where account='" + account + "' and password ='"
					+ MD5Util.MD5(password) + "'";
			int count = this.jdbcTpl.queryForInt(sql);
			if (count > 0) {
				resultStr = "{retCode: 'ok',retMsg: '校验成功'}";
			} else {
				resultStr = "{retCode: 'fail',retMsg: '校验失败'}";
			}
		} else {
			String url = ssoValidHost + "/pwdVerify?name=" + account + "&password=" + password;
			resultStr = HttpUtil.httpGet(url);
			log.info("sso-valid:result-->"+resultStr);
		}
//		resultStr = resultStr.substring(resultStr.indexOf("(") + 1, resultStr.lastIndexOf(")"));
		return resultStr;
	}

	/**
	 * 获取t_project_sort最大排序值
	 * 
	 * @param userId
	 * @return
	 */
	public long getMaxProjectSort(long userId) {
		String querysql = "SELECT IFNULL(MAX(sort),0) AS num FROM T_PROJECT_SORT WHERE userId = " + userId
				+ " AND del=0";
		long num = this.jdbcTpl.queryForLong(querysql);
		return num;
	}

	public List<Project> findByDel(DELTYPE delType) {
		return projectDao.findByDel(delType);
	}

	public Project findOne(long projectId) {
		Project pjO = this.projectDao.findOne(projectId);
		return pjO;
	}

	public int updateMemberTransfer(long memberId, long transferUserId) {
		ProjectMember pm = projectMemberDao.findOne(memberId);
		long userId = pm.getUserId();
		ProjectMember member = projectMemberDao.findOneByProjectIdAndUserIdAndDel(pm.getProjectId(), userId,
				DELTYPE.NORMAL);
		if (member != null) {
			// 修改所有的userId
			changeAllUser(pm.getProjectId(), userId, transferUserId);
			return 1;
		} else {
			return 0;
		}
	}

	public Project getByUuid(String uuid) {
		return this.projectDao.findByUuidAndDel(uuid, DELTYPE.NORMAL);
	}

	/**
	 * EMM调用企业授权通过
	 * 
	 * @user haijun.cheng
	 * @date 2016年07月06日 下午4:53:51
	 */
	public Project bizLicense(Long projectId) {
		Project pt = this.projectDao.findOne(projectId);
		if (pt.getBizCompanyId() == null || pt.getBizCompanyName() == null) {
			return null;
		}
		pt.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
		jdbcTpl.update("update T_PROJECT set bizCompanyId=? ,bizCompanyName=? , bizLicense=? where Id=?", new Object[] {
				pt.getBizCompanyId(), pt.getBizCompanyName(), PROJECT_BIZ_LICENSE.AUTHORIZED.ordinal(), pt.getId() });
		return pt;
	}

	/**
	 * 获取项目中的成员
	 * 
	 * @user haijun.cheng
	 * @date 2016年07月06日
	 */
	public List<User> findAllUserBelongProject(Long projectId) {
		List<User> list = this.userDao.findUserBelongProject(projectId);
		UserListWrapUtil.setNullForPwdFromUserList(list);// 将密码置为空
		return list;
	}

	/**
	 * 解绑企业 @param projectId @return Project @user haijun.cheng @date
	 * 2016年7月6日 @throws
	 */
	public List updateUnEnterprise(Long projectId) {
		List<Object> list = new ArrayList<Object>();
		Project pt = this.projectDao.findOne(projectId);

		list.add(pt);
		list.add(pt.getBizCompanyName());

		jdbcTpl.update("update T_PROJECT set bizCompanyId=null ,bizCompanyName=null , bizLicense=? where Id=?",
				new Object[] { PROJECT_BIZ_LICENSE.NOT_AUTHORIZED.ordinal(), pt.getId() });
		return list;
	}

	/**
	 * 根据项目Id获取项目信息
	 * 
	 * @param projectId
	 * @date 2016年7月7日
	 * @return
	 */
	public Project getOne(Long projectId) {
		return this.projectDao.findOne(projectId);
	}

	/**
	 * @throws Exception 
	 */
	public void updateTransferProject(Long projectId, long srcUserId, Long targetUserId)
			throws Exception {
		// ---git--
		// 标识是否需要给接收者增加git权限-------
		boolean addGitAuth = true;
		// ---git--
		ProjectMember tMemberTarget = (ProjectMember) this.projectMemberDao.findByProjectIdAndUserIdAndDel(projectId,
				targetUserId, DELTYPE.NORMAL);
		ProjectMember tMemberSrc = (ProjectMember) this.projectMemberDao.findByProjectIdAndUserIdAndDel(projectId,
				srcUserId, DELTYPE.NORMAL);
		tMemberSrc.setType(PROJECT_MEMBER_TYPE.PARTICIPATOR);
		tMemberTarget.setType(PROJECT_MEMBER_TYPE.CREATOR);
		this.projectMemberDao.save(tMemberSrc);
		this.projectMemberDao.save(tMemberTarget);

		ProjectAuth projectAuthTarget = (ProjectAuth) this.projectAuthDao.findByMemberIdAndDel(tMemberTarget.getId(),
				DELTYPE.NORMAL);
		ProjectAuth projectAuthSrc = (ProjectAuth) this.projectAuthDao.findByMemberIdAndDel(tMemberSrc.getId(),
				DELTYPE.NORMAL);
		// ---git--
		if (projectAuthTarget.getRoleId() == Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR)
				.getId()) {
			// 接收者以前是团队的管理员
			addGitAuth = false;
		}
		// --git--
		projectAuthSrc.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.ADMINISTRATOR).getId());
		Role ro = Cache.getRole(ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR);
		projectAuthTarget.setRoleId(ro.getId());

		this.projectAuthDao.save(projectAuthSrc);
		this.projectAuthDao.save(projectAuthTarget);
		 
		// ------------------git权限----------------
		if (addGitAuth) {
			List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
			User currUser = userDao.findOne(tMemberTarget.getUserId());
			// 如果是团队管理员
			List<Permission> listP = ro.getPermissions();
			boolean master = false;
			boolean branch = false;
			if (null != listP && listP.size() > 0) {
				for (Permission p : listP) {
					if (p.getEnName().equals("code_upload_master_code")) {
						master = true;
						break;
					}
					if (p.getEnName().equals("code_update_branch")) {
						branch = true;
					}
				}
				log.info("是否有主干权限---->" + master);
				log.info("是否有分支权限---->" + branch);
				List<App> proejectAppList = appDao.findByProjectId(projectId);// 项目下的应用
				if (null != proejectAppList) {
					if (master) {// 团队管理员有主干权限的话
						for (App app : proejectAppList) {
							if (app.getUserId() != currUser.getId().longValue()) {

								GitAuthVO vo = new GitAuthVO();
								vo.setAuthflag("all");
								vo.setPartnername(currUser.getAccount());
								vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
								vo.setProject(encodeKey.toLowerCase());
								vo.setRef("master");
								vo.setProjectid(app.getAppcanAppId());
								listAuth.add(vo);
							}
						}
					} else if (branch) {// 团队管理员有分支权限的话
						for (App app : proejectAppList) {
							if (app.getUserId() != currUser.getId().longValue()) {
								GitAuthVO vo = new GitAuthVO();
								vo.setAuthflag("allbranch");
								vo.setPartnername(currUser.getAccount());
								vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
								String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0, 5);
								vo.setProject(encodeKey.toLowerCase());
								vo.setProjectid(app.getAppcanAppId());
								listAuth.add(vo);
							}
						}
					}
				}
			}

			Map<String, String> map = appService.addGitAuth(listAuth);
			log.info(currUser.getAccount() + " 被转让成项目 id为->" + projectId + "的管理员,and shareallgit->"
					+ (null == map ? null : map.toString()));
		}
	}

	/**
	 * @throws IOException 
	 * @throws ClientProtocolException
	 * @Title: updateEnterprise 
	 * @Description: 为项目绑定企业ID 
	 * @param enterpriseId 
	 * @param teamId 
	 * @return 参数 @return Project 返回类型
	 * @throws
	 */
	public Project updateEnterprise(String bizCompanyId, String bizCompanyName, Long projectId, String loginUserId)
			throws ClientProtocolException, IOException {
		Project pt = this.projectDao.findOne(projectId);
		if (pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
				|| pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING)) {
			return null;
		}
		pt.setBizCompanyId(bizCompanyId);
		pt.setBizCompanyName(bizCompanyName);
		pt.setBizLicense(PROJECT_BIZ_LICENSE.BINDING);
		this.projectDao.save(pt);
		ProjectMember projectmember = projectMemberService.findMemberByProjectIdAndMemberType(projectId,
				PROJECT_MEMBER_TYPE.CREATOR);
		User user = userDao.findOne(projectmember.getUserId());

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("creator", user.getAccount()));
		parameters.add(new BasicNameValuePair("teamName", pt.getName()));
		parameters.add(new BasicNameValuePair("teamDesc", pt.getDetail()));
		parameters.add(new BasicNameValuePair("tenantId", bizCompanyId));
		parameters.add(new BasicNameValuePair("entFullName", bizCompanyName));
		parameters.add(new BasicNameValuePair("teamId", pt.getUuid()));
		parameters.add(new BasicNameValuePair("teamType",
				pt.getType().name().equals("TEAM") ? "teamProject" : "personnelProject"));// 绑定类型（团队：null，团队项目teamProject，个人项目personnelProject）

		// 添加创建者个人信息（姓名，手机号码）
		parameters.add(new BasicNameValuePair("domainUrl", emmInvokeTeamUrl));

		log.info("parameters-->creator:" + user.getAccount() + ",projectName:" + pt.getName() + ",projectDesc:"
				+ pt.getDetail() + ",tenantId:" + bizCompanyId + ",entFulName:" + bizCompanyName + ",projectId:"
				+ pt.getUuid());
		String resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/createAuthGroup", parameters);
		log.info("createAuthGroup-->" + resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if (!jsonObject.get("returnCode").equals("200")) {
			throw new RuntimeException("调用EMM申请授权失败");
		}

		return pt;
	}

	/**
	 * 页面申请取消绑定
	 * 
	 * @param projectId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List updateCancelEnterprise(Long projectId) throws ClientProtocolException, IOException {
		List<Object> list = new ArrayList<Object>();
		Project pt = this.projectDao.findOne(projectId);
		if (pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)
				|| pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.UNBINDING)) {
			return null;
		}
		String enterpriseName = pt.getBizCompanyName();
		String enterpriseId = pt.getBizCompanyId();
		pt.setBizCompanyId(null);
		pt.setBizCompanyName(null);
		pt.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
		this.projectDao.save(pt);
		list.add(pt);
		list.add(enterpriseName);

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("tenantId", enterpriseId));
		parameters.add(new BasicNameValuePair("entFullName", enterpriseName));
		parameters.add(new BasicNameValuePair("teamId", pt.getUuid()));

		String resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/deleteAuthGroup", parameters);
		log.info("updateCancelEnterprise-->" + resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if (!jsonObject.get("returnCode").equals("200")) {
			throw new RuntimeException(jsonObject.getString("returnMessage"));
		}
		return list;
	}

	/**
	 * 项目绑定成功后申请解绑
	 * 
	 * @param projectId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public List updateDeleteEnterprise(Long projectId) throws ClientProtocolException, IOException {
		List<Object> list = new ArrayList<Object>();
		Project pt = this.projectDao.findOne(projectId);
		if (pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED)) {
			return null;
		}
		String enterpriseName = pt.getBizCompanyName();
		String enterpriseId = pt.getBizCompanyId();
		pt.setBizLicense(PROJECT_BIZ_LICENSE.UNBINDING);// 项目状态改成解绑申请中
		this.projectDao.save(pt);
		list.add(pt);
		list.add(enterpriseName);

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("tenantId", enterpriseId));
		parameters.add(new BasicNameValuePair("entFullName", enterpriseName));
		parameters.add(new BasicNameValuePair("teamId", pt.getUuid()));
		parameters.add(new BasicNameValuePair("isUnbunding", "true"));

		String resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/unbundingTeam", parameters);
		log.info("updateDeleteEnterprise-->" + resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if (!jsonObject.get("returnCode").equals("200")) {
			throw new RuntimeException(jsonObject.getString("returnMessage"));
		}
		return list;
	}

	public void changeAllUser(long projectId, long userId, long transferUserId) {
		if (userId == transferUserId) {
			return;
		}
		// process
		List<String> processList = this.processDao.getProcessByProjectIdAndUserIdAndDel(projectId, userId,
				DELTYPE.NORMAL.ordinal());
		for (String processId : processList) {
			// 查看该流程里是否已有该转移的成员
			List<ProcessMember> memberNew = this.processMemberDao
					.findByProcessIdAndUserIdAndDel(Long.parseLong(processId), transferUserId, DELTYPE.NORMAL);
			List<ProcessMember> memberOld = this.processMemberDao
					.findByProcessIdAndUserIdAndDel(Long.parseLong(processId), userId, DELTYPE.NORMAL);
			if (memberNew.size() > 0) {
				long newMemberId = memberNew.get(0).getId();
				long oldMemberId = memberOld.get(0).getId();
				// 删除旧成员
				if (memberNew.get(0).getType().equals(PROCESS_MEMBER_TYPE.CREATOR)) {
					memberNew.get(0).setType(PROCESS_MEMBER_TYPE.CREATOR);
					this.processMemberDao.save(memberNew.get(0));
				}
				memberOld.get(0).setDel(DELTYPE.DELETED);
				this.processMemberDao.save(memberOld.get(0));
				List<ProcessAuth> paOldList = this.processAuthDao.findByMemberIdAndDel(oldMemberId, DELTYPE.NORMAL);
				for (ProcessAuth paListSingle : paOldList) {
					boolean twoPersonIsMember = false;
					if (paListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.PROCESS + "_" + ROLE_TYPE.MEMBER)
							.getId()) {
						List<ProcessAuth> paNewList = this.processAuthDao.findByMemberIdAndDel(newMemberId,
								DELTYPE.NORMAL);
						for (ProcessAuth paNewListSingle : paNewList) {
							if (paNewListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER)
									.getId()) {
								twoPersonIsMember = true;
							}
						}
						if (twoPersonIsMember == false) {
							paListSingle.setMemberId(newMemberId);
							this.processAuthDao.save(paListSingle);
							// 已经是成员删掉就行
						} else {
							paListSingle.setDel(DELTYPE.DELETED);
							this.processAuthDao.save(paListSingle);
						}
						// 创建者或者指派者，必须要直接替换新的memberId
					} else {
						paListSingle.setMemberId(newMemberId);
						this.processAuthDao.save(paListSingle);
					}

				}
			} else {
				if (memberOld.size() > 0) {
					memberOld.get(0).setUserId(transferUserId);
					this.processMemberDao.save(memberOld.get(0));
				}
			}
		}

		// bug
		List<String> bugsList = this.bugDao.getBugsByProjectIdAndUserIdAndDel(projectId, userId,
				DELTYPE.NORMAL.ordinal());
		for (String bugId : bugsList) {
			// 查看该bug里是否已有该转移的成员
			BugMember memberNew = this.bugMemberDao.findByBugIdAndUserIdAndDel(Long.parseLong(bugId), transferUserId,
					DELTYPE.NORMAL);
			BugMember memberOld = this.bugMemberDao.findByBugIdAndUserIdAndDel(Long.parseLong(bugId), userId,
					DELTYPE.NORMAL);
			if (memberNew != null) {
				long newMemberId = memberNew.getId();
				long oldMemberId = memberOld.getId();
				// 删除旧成员
				if (memberOld.getType().equals(BUG_MEMBER_TYPE.CREATOR)) {
					memberNew.setType(BUG_MEMBER_TYPE.CREATOR);
					this.bugMemberDao.save(memberNew);
				}
				memberOld.setDel(DELTYPE.DELETED);
				this.bugMemberDao.save(memberOld);
				List<BugAuth> baOldList = this.bugAuthDao.findByMemberIdAndDel(oldMemberId, DELTYPE.NORMAL);
				for (BugAuth baListSingle : baOldList) {
					// 删除的人是bug成员，被转移的已是成员
					boolean twoPersonIsMember = false;
					if (baListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER).getId()) {
						List<BugAuth> baNewList = this.bugAuthDao.findByMemberIdAndDel(newMemberId, DELTYPE.NORMAL);
						for (BugAuth baNewListSingle : baNewList) {
							if (baNewListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER)
									.getId()) {
								twoPersonIsMember = true;
							}
						}
						if (twoPersonIsMember == false) {
							baListSingle.setMemberId(newMemberId);
							this.bugAuthDao.save(baListSingle);
						} else {
							baListSingle.setDel(DELTYPE.DELETED);
							this.bugAuthDao.save(baListSingle);
						}
						// 创建者或者指派者
					} else {
						baListSingle.setMemberId(newMemberId);
						this.bugAuthDao.save(baListSingle);
					}

				}
			} else {
				memberOld.setUserId(transferUserId);
				this.bugMemberDao.save(memberOld);
			}
		}

		// 任务
		List<String> tasksList = this.taskDao.getTasksByProjectIdAndUserIdAndDel(projectId, userId,
				DELTYPE.NORMAL.ordinal());
		for (String taskId : tasksList) {
			// 查看该bug里是否已有该转移的成员
			TaskMember memberNew = this.taskMemberDao.findByTaskIdAndUserIdAndDel(Long.parseLong(taskId),
					transferUserId, DELTYPE.NORMAL);
			TaskMember memberOld = this.taskMemberDao.findByTaskIdAndUserIdAndDel(Long.parseLong(taskId), userId,
					DELTYPE.NORMAL);
			if (memberNew != null) {
				// 删除旧成员(如果删除的是创建者)
				if (memberOld.getType().equals(TASK_MEMBER_TYPE.CREATOR)) {
					memberNew.setType(TASK_MEMBER_TYPE.CREATOR);
					this.taskMemberDao.save(memberNew);
				}
				memberOld.setDel(DELTYPE.DELETED);
				this.taskMemberDao.save(memberOld);
				long newMemberId = memberNew.getId();
				long oldMemberId = memberOld.getId();
				List<TaskAuth> taOldList = this.taskAuthDao.findByMemberIdAndDel(oldMemberId, DELTYPE.NORMAL);
				for (TaskAuth taListSingle : taOldList) {
					// 删除的人是bug成员，被转移的已是成员
					boolean twoPersonIsMember = false;
					if (taListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER).getId()) {
						List<TaskAuth> taNewList = this.taskAuthDao.findByMemberIdAndDel(newMemberId, DELTYPE.NORMAL);
						for (TaskAuth taNewListSingle : taNewList) {
							if (taNewListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.TASK + "_" + ROLE_TYPE.MEMBER)
									.getId()) {
								twoPersonIsMember = true;
							}
						}
						if (twoPersonIsMember == false) {
							taListSingle.setMemberId(newMemberId);
							this.taskAuthDao.save(taListSingle);
						} else {
							taListSingle.setDel(DELTYPE.DELETED);
							this.taskAuthDao.save(taListSingle);
						}
						// 创建者或者负责人
					} else {
						taListSingle.setMemberId(newMemberId);
						this.taskAuthDao.save(taListSingle);
					}
				}
			} else {
				memberOld.setUserId(transferUserId);
				this.taskMemberDao.save(memberOld);
			}
		}
		// topic
		List<String> topicList = this.topicDao.getTopicsByProjectIdAndUserIdAndDel(projectId, userId,
				DELTYPE.NORMAL.ordinal());
		for (String topicId : topicList) {
			TopicMember memberNew = this.topicMemberDao.findByTopicIdAndUserIdAndDel(Long.parseLong(topicId),
					transferUserId, DELTYPE.NORMAL);
			TopicMember memberOld = this.topicMemberDao.findByTopicIdAndUserIdAndDel(Long.parseLong(topicId), userId,
					DELTYPE.NORMAL);
			if (memberNew != null) {
				long newMemberId = memberNew.getId();
				long oldMemberId = memberOld.getId();
				// 删除旧成员
				if (memberOld.getType().equals(TOPIC_MEMBER_TYPE.SPONSOR)) {
					memberNew.setType(TOPIC_MEMBER_TYPE.SPONSOR);
					this.topicMemberDao.save(memberNew);
				}
				memberOld.setDel(DELTYPE.DELETED);
				this.topicMemberDao.save(memberOld);
				List<TopicAuth> baOldList = this.topicAuthDao.findByMemberIdAndDel(oldMemberId, DELTYPE.NORMAL);
				for (TopicAuth baListSingle : baOldList) {
					// 删除的人是bug成员，被转移的已是成员
					boolean twoPersonIsMember = false;
					if (baListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER).getId()) {
						List<TopicAuth> baNewList = this.topicAuthDao.findByMemberIdAndDel(newMemberId, DELTYPE.NORMAL);
						for (TopicAuth baNewListSingle : baNewList) {
							if (baNewListSingle.getRoleId() == Cache.getRole(ENTITY_TYPE.BUG + "_" + ROLE_TYPE.MEMBER)
									.getId()) {
								twoPersonIsMember = true;
							}
						}
						if (twoPersonIsMember == false) {
							baListSingle.setMemberId(newMemberId);
							this.topicAuthDao.save(baListSingle);
						} else {
							baListSingle.setDel(DELTYPE.DELETED);
							this.topicAuthDao.save(baListSingle);
						}
						// 创建者或者指派者
					} else {
						baListSingle.setMemberId(newMemberId);
						this.topicAuthDao.save(baListSingle);
					}

				}
			} else {
				memberOld.setUserId(transferUserId);
				this.topicMemberDao.save(memberOld);
			}
		}
		// 其他的
		User newUser = userDao.findByIdAndDel(transferUserId, DELTYPE.NORMAL);
		if (newUser == null) {
			throw new RuntimeException("userId 为：" + transferUserId + "的人在该项目不存在");
		}
		String newUserName = newUser.getUserName();
		List<String> sqlList = new ArrayList<String>();
		// 更新task完成人
		StringBuffer taskSql = new StringBuffer();
		taskSql.append("update T_TASK set finishUserId=").append(transferUserId)
				.append(" where del=0 and finishUserId=").append(userId)
				.append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId)
				.append(")");
		sqlList.add(taskSql.toString());
		// 更新子任务完成人
		StringBuffer taskLeafSql1 = new StringBuffer();
		taskLeafSql1.append("update T_TASK_LEAF set finishUserId=").append(transferUserId)
				.append(" where del=0 and finishUserId=").append(userId)
				.append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId)
				.append(")");
		sqlList.add(taskLeafSql1.toString());
		// 更新子任务负责人
		StringBuffer taskLeafSql2 = new StringBuffer();
		taskLeafSql2.append("update T_TASK_LEAF set managerUserId=").append(transferUserId)
				.append(" where del=0 and managerUserId=").append(userId)
				.append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId)
				.append(")");
		sqlList.add(taskLeafSql2.toString());
		// 更新任务分组排序成员
		StringBuffer taskGroupSortSql = new StringBuffer();
		taskGroupSortSql.append("update T_TASK_GROUP_SORT set userId=").append(transferUserId)
				.append(" where del=0 and userId=").append(userId).append(" and projectId=").append(projectId);
		sqlList.add(taskGroupSortSql.toString());
		// 更新任务评论成员
		StringBuffer taskCommentSql = new StringBuffer();
		taskCommentSql.append("update T_TASK_COMMENT set userId=").append(transferUserId)
				.append(" where del=0 and userId=").append(userId)
				.append(" and taskId in (select id from T_TASK where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=")
				.append(projectId).append("))");
		sqlList.add(taskCommentSql.toString());
		// 更新bug解决人
		StringBuffer bugSql1 = new StringBuffer();
		bugSql1.append("update T_BUG set resolveUserId=").append(transferUserId)
				.append(" where del=0 and resolveUserId=").append(userId)
				.append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId)
				.append(")");
		sqlList.add(bugSql1.toString());
		// 更新bug关闭人
		StringBuffer bugSql2 = new StringBuffer();
		bugSql2.append("update T_BUG set closeUserId=").append(transferUserId).append(" where del=0 and closeUserId=")
				.append(userId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=")
				.append(projectId).append(")");
		sqlList.add(bugSql2.toString());
		// 更新bug最后修改人
		StringBuffer bugSql3 = new StringBuffer();
		bugSql3.append("update T_BUG set lastModifyUserId=").append(transferUserId)
				.append(" where del=0 and lastModifyUserId=").append(userId)
				.append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId)
				.append(")");
		sqlList.add(bugSql3.toString());
		// 更新bug备注成员
		StringBuffer bugMarkSql = new StringBuffer();
		bugMarkSql.append("update T_BUG_MARK set userId=").append(transferUserId).append(" where del=0 and userId=")
				.append(userId)
				.append(" and bugId in (select id from T_BUG where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=")
				.append(projectId).append("))");
		sqlList.add(bugMarkSql.toString());
		// 更新bug模块创建者
		StringBuffer bugModuleSql1 = new StringBuffer();
		bugModuleSql1.append("update T_BUG_MODULE set creatorId=").append(transferUserId)
				.append(" where del=0 and creatorId=").append(userId).append(" and projectId=").append(projectId);
		sqlList.add(bugModuleSql1.toString());
		// 更新bug模块负责人
		StringBuffer bugModuleSql2 = new StringBuffer();
		bugModuleSql2.append("update T_BUG_MODULE set managerId=").append(transferUserId)
				.append(" where del=0 and managerId=").append(userId).append(" and projectId=").append(projectId);
		sqlList.add(bugModuleSql2.toString());
		// 修改动态操作人
		StringBuffer dynamicSql = new StringBuffer();
		dynamicSql.append("update T_DYNAMIC set userId=").append(transferUserId).append(" where del=0 and userId=")
				.append(userId).append(" and relationId=").append(projectId);
		sqlList.add(dynamicSql.toString());
		// 修改应用创建者
		StringBuffer appSql = new StringBuffer();
		appSql.append("update T_APP set userId=").append(transferUserId).append(" where del=0 and userId=")
				.append(userId).append(" and projectId=").append(projectId);
		sqlList.add(appSql.toString());
		// 修改应用版本创建者
		StringBuffer appVersionSql = new StringBuffer();
		appVersionSql.append("update T_APP_VERSION set userId=").append(transferUserId)
				.append(" where del=0 and userId=").append(userId)
				.append(" and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append(")");
		sqlList.add(appVersionSql.toString());
		// 修改应用包创建者
		StringBuffer appPackageSql = new StringBuffer();
		appPackageSql.append("update T_APP_PACKAGE set userId=").append(transferUserId)
				.append(" where del=0 and userId=").append(userId)
				.append(" and appVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=")
				.append(projectId).append("))");
		sqlList.add(appPackageSql.toString());
		// 修改widget创建者
		StringBuffer appWidgetSql = new StringBuffer();
		appWidgetSql.append("update T_APP_WIDGET set userId=").append(transferUserId).append(" where del=0 and userId=")
				.append(userId)
				.append(" and appVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=")
				.append(projectId).append("))");
		sqlList.add(appWidgetSql.toString());
		// 修改补丁包创建者
		StringBuffer appPatchSql = new StringBuffer();
		appPatchSql.append("update T_APP_PATCH set userId=").append(transferUserId).append(" where del=0 and userId=")
				.append(userId)
				.append(" and baseAppVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=")
				.append(projectId).append("))");
		sqlList.add(appPatchSql.toString());
		// 修改讨论创建者
		StringBuffer topicSql = new StringBuffer();
		topicSql.append("update T_TOPIC set userId=").append(transferUserId).append(" where del=0 and userId=")
				.append(userId).append(" and projectId=").append(projectId);
		sqlList.add(topicSql.toString());
		// 修改讨论评论
		StringBuffer topicCommentSql = new StringBuffer();
		topicCommentSql.append("update T_TOPIC_COMMENT set userId=").append(transferUserId)
				.append(" where del=0 and userId=").append(userId)
				.append(" and topicId in (select id from T_TOPIC where del=0 and projectId=").append(projectId)
				.append(")");
		sqlList.add(topicCommentSql.toString());
		// 修改文档创建者
		StringBuffer documentSql = new StringBuffer();
		documentSql.append("update T_DOCUMENT set userId=").append(transferUserId).append(" where del=0 and userId=")
				.append(userId).append(" and projectId=").append(projectId);
		sqlList.add(documentSql.toString());
		// 修改文档章节创建者
		StringBuffer documentChapterSql = new StringBuffer();
		documentChapterSql.append("update T_DOCUMENT_CHAPTER set userId=").append(transferUserId)
				.append(" where userId=").append(userId)
				.append(" and documentId in (select id from T_DOCUMENT where del=0 and projectId=").append(projectId)
				.append(")");
		sqlList.add(documentChapterSql.toString());
		// 修改文档标记创建者
		StringBuffer documentMarkSql = new StringBuffer();
		documentMarkSql.append("update T_DOCUMENT_MARKER set userName='").append(newUserName).append("',userId=")
				.append(transferUserId).append(" where userId=").append(userId)
				.append(" and docCId in (select id from T_DOCUMENT_CHAPTER where del=0 and documentId in (select id from T_DOCUMENT where del=0 and projectId=")
				.append(projectId).append("))");
		sqlList.add(documentMarkSql.toString());
		// 修改资源创建
		StringBuffer resourceSql = new StringBuffer();
		resourceSql.append("update T_RESOURCES set userName='").append(newUserName).append("',userId=")
				.append(transferUserId).append(" where del=0 and userId=").append(userId).append(" and projectId=")
				.append(projectId);
		sqlList.add(resourceSql.toString());
		for (String sql : sqlList) {
			this.jdbcTpl.execute(sql);
		}
	}

	public List deleteEnterpriseCancel(Long projectId) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		List<Object> list = new ArrayList<Object>();
		Project pt = this.projectDao.findOne(projectId);
		if (pt.getBizLicense().equals(PROJECT_BIZ_LICENSE.AUTHORIZED)) {
			return null;
		}
		String enterpriseName = pt.getBizCompanyName();
		String enterpriseId = pt.getBizCompanyId();
		pt.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
		this.projectDao.save(pt);
		list.add(pt);
		list.add(enterpriseName);

		List<NameValuePair> parameters = new ArrayList<>();
		parameters.add(new BasicNameValuePair("tenantId", enterpriseId));
		parameters.add(new BasicNameValuePair("entFullName", enterpriseName));
		parameters.add(new BasicNameValuePair("teamId", pt.getUuid()));
		parameters.add(new BasicNameValuePair("isUnbunding", "false"));

		String resultStr = HttpUtil.httpPost(emmUrl + "/emm/teamAuth/unbundingTeam", parameters);
		log.info("cancelDeleteEnterprise-->" + resultStr);
		JSONObject jsonObject = JSONObject.fromObject(resultStr);
		if (!jsonObject.get("returnCode").equals("200")) {
			throw new RuntimeException(jsonObject.getString("returnMessage"));
		}
		return list;
	}

	public Map<String, Object> addPinyin() {
		List<Project> projects = this.projectDao.findByDel(DELTYPE.NORMAL);
		for (Project project : projects) {
			project.setPinYinHeadChar(
					ChineseToEnglish.getPinYinHeadChar(project.getName() == null ? "" : project.getName()));
			project.setPinYinName(ChineseToEnglish.getPingYin(project.getName() == null ? "" : project.getName()));
		}
		this.projectDao.save(projects);
		return this.getSuccessMap("affected " + projects.size());
	}

	/**
	 * haijun.cheng 2016-07-19 判断团队是否绑定企业
	 * 
	 * @param teamId
	 * @return
	 */
	public boolean isTeamBind(Long teamId) {
		String sql = "select count(1) from T_TEAM where type in (1,3) and id=" + teamId;
		int count = this.jdbcTpl.queryForInt(sql);
		if (count > 0)
			return true;
		else
			return false;
	}

	public Team findIsTeamDeleteBind(Long projectId) {
		Project pt=this.projectDao.findOne(projectId);
		Team team=teamDao.findOne(pt.getTeamId());
		return team;
	}

	public boolean findIsTeamUnenterprise(Long projectId) {
		Project pt=this.projectDao.findOne(projectId);
		String sql="select count(1) from T_TEAM where type!=0 and id="+pt.getTeamId();
		@SuppressWarnings("deprecation")
		int count=this.jdbcTpl.queryForInt(sql);
		if(count>0){
			return true;
		}else{
			return false;
		}
	}

	public PROJECT_BIZ_LICENSE findBizLicense(Long projectId) {
		Project pt=this.projectDao.findOne(projectId);
		return pt.getBizLicense();
	}
/**
 * 查询该用户创建的项目
 * @param loginUserId
 * @return
 */
	public List<Long> getProjectByCreater(Long loginUserId) {
		StringBuffer sql=new StringBuffer();
		sql.append("select t.id from T_PROJECT t left join T_PROJECT_MEMBER t1 on t.id=t1.projectId left join T_PROJECT_AUTH t2 on t1.id=t2.memberId");
		sql.append(" left join T_ROLE t3 on t2.roleId=t3.id where t3.enName='PROJECT_CREATOR' and t1.userId=").append(loginUserId).append(" and t.del=0 and t1.del=0 and t2.del=0");
		final List<Long> list=new ArrayList<Long>();
		this.jdbcTpl.query(sql.toString(),
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				list.add(rs.getLong("id"));
			}
		});
		return list;
	}

public Map<String, Object> addProjectMember(long projectId, List<String> userStrList, long loginUserId,
		String content) {
		Project project = this.getProject(projectId);
		List<ProjectMember> members = new ArrayList<>();
		List<Long> memberUserIdArr = new ArrayList<>();
		String sendEmailMessage = "";
		Map<String, Object> affectedMap = new HashMap<>();
		for(String userStr : userStrList) {
			String[] items = userStr.split("#");
			if(items.length != 2) {
				continue;
			}
			String account = items[0];
			String roleTypeStr = items[1].toUpperCase();
			if( !roleTypeStr.equals(ROLE_TYPE.ADMINISTRATOR.name())  &&
				!roleTypeStr.equals(ROLE_TYPE.MEMBER.name()) &&
				!roleTypeStr.equals(ROLE_TYPE.OBSERVER.name())
					) {
				continue;
			}
			
			User user = userService.findUserByAccountAndDel(account, DELTYPE.NORMAL);
			if(user != null) {
				boolean exist = this.userExistInProject(projectId, user.getId());
				if(exist){
					sendEmailMessage=sendEmailMessage+account+"已存在;";
					continue;
				}
				
				User loginUser = this.userService.findUserById(loginUserId);
				
				MailSenderInfo mailInfo = new MailSenderInfo();
				mailInfo.setContent(content+"</br>点击以下链接登录协同开发 :<a href=\""+xietongHost+"\">"+xietongHost+"</a>");
				mailInfo.setToAddress(user.getAccount());
				SendMailTools.setXtHost(xtHost);
				SendMailTools.setEmailSourceRootPath(emailSourceRootPath);
				sendMailTool.sendMailByAsynchronousMode(mailInfo);
				
				//邮件发送成功则添加到project中
				ProjectMember member = new ProjectMember();
				member.setProjectId(projectId);
				member.setType(PROJECT_MEMBER_TYPE.INVITEE);
				member.setUserId(user.getId());
				members.add(member);
				memberUserIdArr.add(user.getId());
				this.saveProjectMember(member,loginUser);
				
				ProjectAuth auth = new ProjectAuth();
				auth.setMemberId(member.getId());
				Role role = Cache.getRole(ENTITY_TYPE.PROJECT + "_" + roleTypeStr);
				if(role == null) {
					continue;
				}
				auth.setRoleId(role.getId());
				this.saveProjectAuth(auth);
				//添加通知
				this.noticeService.addNotice(loginUserId, (Long[])memberUserIdArr.toArray(new Long[memberUserIdArr.size()]), NOTICE_MODULE_TYPE.PROJECT_ADD_MEMBER, new Object[]{loginUser,project});
				
			}else{
			    Pattern emailpattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
				Matcher matcher = emailpattern.matcher(account);
				log.info("email is right:"+matcher.matches());
				if(!matcher.matches()){
					sendEmailMessage=sendEmailMessage+account+"邮箱格式不对;";
					continue;
				}
				
				user = new User();
				user.setAccount(account);
				user.setEmail(account);
				user.setUserName(account);
				user.setUserlevel(USER_LEVEL.ADVANCE);
				/**
				 * 应该调用AppCan接口,查询这个用户是否是注册用户，
				 * 1.如果是非注册用户，那么类型为Enums.USER_STATUS.NOREGISTER.getVal()
				 * 2.入股是注册用户,但是未认证开发者,那么类型为Enums.USER_STATUS.NOAUTHENTICATION.getVal()
				 * 3.如果是注册用户,并且已经是认证开发者,那么类型为Enums.USER_STATUS.AUTHENTICATION.getVal()
				 */
				user.setType(USER_TYPE.NOREGISTER);
				this.userService.addUser(user);
				
				MailSenderInfo mailInfo = new MailSenderInfo();
				mailInfo.setContent(content+"</br>点击以下链接登录协同开发 :<a href=\""+xietongHost+"\">"+xietongHost+"</a>");
				mailInfo.setToAddress(user.getAccount());
				SendMailTools.setXtHost(xtHost);
				SendMailTools.setEmailSourceRootPath(emailSourceRootPath);
				sendMailTool.sendMailByAsynchronousMode(mailInfo);
				
				//邮件发送成功则添加到project中
				ProjectMember member = new ProjectMember();
				member.setProjectId(projectId);
				member.setType(PROJECT_MEMBER_TYPE.INVITEE);
				member.setUserId(user.getId());
				members.add(member);
				this.saveProjectMember(member,user);
				
				ProjectAuth auth = new ProjectAuth();
				auth.setMemberId(member.getId());
				Role role = Cache.getRole(ENTITY_TYPE.PROJECT + "_" + roleTypeStr);
				if(role == null) {
					continue;
				}
				auth.setRoleId(role.getId());
				this.saveProjectAuth(auth);
			}
			
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_INVITE_MEMBER, projectId, new Object[]{user.getAccount()});
			sendEmailMessage=sendEmailMessage+account+"添加成功;";
		}
		if(members.size() == 0) {
			return this.getFailedMap(sendEmailMessage);
		}
		return this.getSuccessMap(sendEmailMessage.substring(0,sendEmailMessage.length()-1));
	}
/**
 * @author haijun.cheng
 * @date 2016-09-18 
 * @param required
 * @param loginUserId
 * @return
 */
public Set<Long> getProjectsByRequiredAndLoginUserId(String permissionEnName, Long loginUserId) {
	long startTime = System.currentTimeMillis();
	final Set<Long> set=new HashSet<Long>();
	StringBuffer sql = new StringBuffer("select distinct projectId from (");
	sql.append(
			" select member.projectId from T_PROJECT_MEMBER member left join T_PROJECT_AUTH  auth on member.id = auth.memberId   ")
			.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
			.append(" and auth.roleId  in( ")
			.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
			.append(permissionEnName).append("' and del=0) and del=0  ").append(")").append(" union all  ")
			.append("  select prj.Id projectId from T_TEAM_MEMBER member left join T_TEAM_AUTH  auth on member.id = auth.memberId  ")
			.append(" left join T_PROJECT prj on member.teamId =prj.teamId ")
			.append(" where member.del=0 and auth.del=0  and member.userId= ").append(loginUserId)
			.append(" and auth.roleId  in( ")
			.append(" select roleId from T_ROLE_AUTH where premissionId  in (select id from T_PERMISSION where enName='")
			.append(permissionEnName).append("' and del=0) and del=0  ").append(" ) ").append(" and prj.del=0  ")
			.append(" ) t ");
	log.info("getProjectsByRequiredAndLoginUserId===>"+sql.toString());
	this.jdbcTpl.query(sql.toString(), new RowCallbackHandler() {
		@Override
		public void processRow(ResultSet rs) throws SQLException {
			set.add(rs.getLong(1));
		}
	});
	long endTime = System.currentTimeMillis();
	log.info("projectService permissionMapAsMemberWith total time--> " + (endTime - startTime) + "  ms");
	return set;
}

public List<User> findAllUserBelongTeam(Long id) {
	// TODO Auto-generated method stub
	return null;
}

	public List<Project> findByParentId(Long parentId){
		return projectDao.findByParentIdAndDel(parentId, DELTYPE.NORMAL);
	}
	
	public List<ProjectMember> findMemberByProjectId(Long projectId){
		return projectMemberService.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);//项目下的成员
	}

	public List<Project> findByParentIdAndDel(Long parentId) {
		// TODO Auto-generated method stub
		return projectDao.findByParentIdAndDel(parentId, DELTYPE.DELETED);
	}

}