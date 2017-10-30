package org.zywx.cooldev.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.dao.project.ProjectDao;
import org.zywx.cooldev.entity.TeamAnaly;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.system.Cache;

@Service
public class TeamAnalyService extends BaseService {
	@Autowired
	protected ProjectService projectService;
    //项目完成、延期、进行总数量
	public Map<String,Object> projectAnaly(long teamId, long loginUserId) {
		StringBuffer projTotalsSql=new StringBuffer();
		projTotalsSql.append("select count(1) as total,status from T_PROJECT where del=0 and teamId=")
		.append(teamId).append(" group by status");
		List<Map<String, Object>> proLists = jdbcTpl.queryForList(projTotalsSql.toString());
		Map<String,Object> map =new HashMap<String,Object>();
		for(Map<String, Object> proList:proLists){
			if(proList.get("status").equals(PROJECT_STATUS.FINISHED.ordinal())){
				map.put("projectFinishedTotal",proList.get("total"));
			}else if(proList.get("status").equals(PROJECT_STATUS.ONGOING.ordinal())){
				Integer ongoingAndDelayTotal=Integer.parseInt(proList.get("total").toString());
				if(ongoingAndDelayTotal>0){
					StringBuffer delaySql=new StringBuffer();
					delaySql.append("select pj.id from T_PROJECT pj left join T_PROCESS pc on pj.id=pc.projectId where pj.del=0 and pj.status=")
					.append(PROJECT_STATUS.ONGOING.ordinal()).append(" and pc.del=0  and pj.teamId=").append(teamId).append(" group by pj.id having max(date_format(pc.endDate,'%Y-%m-%d'))<date_format(now(),'%Y-%m-%d')");
					List<Map<String, Object>> listDelay=this.jdbcTpl.queryForList(delaySql.toString());
					int delayTotal=listDelay.size();
					map.put("projectOngoingTotal",ongoingAndDelayTotal-delayTotal);
					map.put("projectDelayTotal",delayTotal);
				}else{
					map.put("projectOngoingTotal",0);
					map.put("projectDelayTotal",0);
				}
			}else{
				continue;
			}
		}
		if(map.get("projectFinishedTotal")==null){
			map.put("projectFinishedTotal",0);
		}
		if(map.get("projectOngoingTotal")==null){
			map.put("projectOngoingTotal",0);
		}
		if(map.get("projectDelayTotal")==null){
			map.put("projectDelayTotal",0);
		}
		return map;
		
	}
    //所有项目的详情
	public Map<String,Object> getProjectsDetail(long loginUserId, long teamId){
	    Map<String,Object> map=new HashMap<String,Object>();
		List<Object> finishList=new ArrayList<Object>();
		List<Object> onGoingList=new ArrayList<Object>();
		//List<Object> delayList=new ArrayList<Object>();
		List<TeamAnaly> teamAnalys=this.teamAnalyDao.findByTeamIdAndDel(teamId,DELTYPE.NORMAL);
		for(TeamAnaly teamAnalyO:teamAnalys){
			long projectId=teamAnalyO.getProjectId();
			Project project=this.projectDao.findByTeamIdAndIdAndDel(teamId,projectId,DELTYPE.NORMAL);
			if(project!=null){
				if(project.getStatus().equals(PROJECT_STATUS.FINISHED)){
					finishList.add(projectDetail(projectId,teamId));
				}
				else{
					onGoingList.add(projectDetail(projectId,teamId));
				}
			}
		}
		map.put("finishList", finishList);
		map.put("onGoingList", onGoingList);
		//map.put("delayList", delayList);
		return map;
	}

	public Map<String, Object> addProject(long loginUserId, TeamAnaly teamAnaly) {
		Map<String,Object> map=new HashMap<String,Object>();
		TeamAnaly teamAnalyO=this.teamAnalyDao.findByTeamIdAndProjectIdAndDel(teamAnaly.getTeamId(),teamAnaly.getProjectId(),DELTYPE.NORMAL);
		if(teamAnalyO!=null){
			map.put("failed", "该项目已在统计列表");
			return map;
		}
		//创建者、管理员才能增加项目
		String yesOrNo=judgeTeamManager(loginUserId,teamAnaly.getTeamId());
		if(yesOrNo.equals("YES")){
			this.teamAnalyDao.save(teamAnaly);
			List<TeamAnaly> teamAnalys=this.teamAnalyDao.findByTeamIdAndDel(teamAnaly.getTeamId(),DELTYPE.NORMAL);
			if(teamAnalys.size()>5){
				removeProject(teamAnalys.get(0).getProjectId(),teamAnaly.getTeamId());
			}
		    return getProjectsDetail(loginUserId,teamAnaly.getTeamId());
		}else{
			map.put("failed", "无权限,您不是该团队的创建者或者管理员");
			return map;
		}
	}
	//一个项目详情的统计
	public Map<String,Object> projectDetail(long projectId,long teamId){
		Map<String,Object> map=new HashMap<String,Object>();
		StringBuffer sql=new StringBuffer();
		sql.append("select pj.progress,pj.id as projectId,pj.name as projectName,ifnull(m.memberTotal,0) as memberTotal,ifnull(t.taskTotal,0) as taskTotal,ifnull(t.taskNoFinishTotal,0) as taskNoFinishTotal,ifnull(b.bugTotal,0) as bugTotal,ifnull(b.bugNoCloseTotal,0) as bugNoCloseTotal,")
		   .append("date_format(pj.finishDate,'%Y-%m-%d') as finishDate, date_format(f.processMaxEndDate,'%Y-%m-%d') as processMaxEndDate from T_PROJECT pj JOIN (SELECT count(1) AS memberTotal")
		   .append(" FROM T_PROJECT_MEMBER WHERE del = 0 AND projectId =")
		   .append(projectId).append(") AS m")
		   .append(" JOIN (SELECT count(1) AS taskTotal,sum(CASE WHEN STATUS = 0 THEN 1 ELSE 0 END) AS taskNoFinishTotal FROM T_TASK WHERE del = 0 AND processId")
		   .append(" IN (SELECT id from T_PROCESS where del=0 and projectId=").append(projectId).append(")) AS t")
		   .append(" JOIN (SELECT count(1) AS bugTotal,sum(case when status!=2 then 1 else 0 end) AS bugNoCloseTotal")
		   .append(" from T_BUG where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=")
		   .append(projectId).append(")) as b")
		   .append(" join (select max(endDate) as  processMaxEndDate from T_PROCESS where projectId=")
		   .append(projectId).append(" and T_PROCESS.del=0) as f where pj.id=").append(projectId);
			map=this.jdbcTpl.queryForMap(sql.toString());
//			map.put("progress",this.projectService.getProjectProgressForInt(projectId));任务,bug时候已经计算好存到数据库了,上面SQL直接取就行
			return map;
	}
	public String judgeTeamManager(long loginUserId, long teamId) {
		TeamMember teamMember=this.teamMemberDao.findByTeamIdAndUserIdAndDel(teamId, loginUserId, DELTYPE.NORMAL);
		if(teamMember==null){
			return "NO";
		}
		TeamAuth teamAuth1=this.teamAuthDao.findByMemberIdAndRoleIdAndDel(teamMember.getId(), Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR).getId(),DELTYPE.NORMAL);
		TeamAuth teamAuth2=this.teamAuthDao.findByMemberIdAndRoleIdAndDel(teamMember.getId(), Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId(),DELTYPE.NORMAL);
		if(teamAuth1==null&&teamAuth2==null){
			return "NO";
		}else{
			return "YES";
		}
	}
	public int removeProject(long projectId, long teamId) {
		StringBuffer sql=new StringBuffer();
		sql.append("update T_TEAM_ANALY set del=").append(DELTYPE.DELETED.ordinal())
		   .append(" where del!=").append(DELTYPE.DELETED.ordinal()).append(" and teamId=").append(teamId).append(" and projectId=").append(projectId);
		int a=this.jdbcTpl.update(sql.toString());
		return a;
	}
	public Map<String,Object> teamMember(long teamId, Integer pageNoS, Integer pageSizeS) {
		Map<String,Object> map=new LinkedHashMap<String,Object>();
		StringBuffer listSql=new StringBuffer();
		StringBuffer taskSql=new StringBuffer();
		StringBuffer bugSql=new StringBuffer();
		StringBuffer listTotalSql=new StringBuffer();
		Integer pageStart=(pageNoS-1)*pageSizeS;
		listSql.append("select u1.id as userId,u1.userName,ifnull(ptj.projectTotal,0) as projectTotal,ifnull(btj.bugTotal,0) as bugTotal,ifnull(ttj.taskTotal,0) as taskTotal,ifnull(ttj.taskNoFinishTotal,0) as taskNoFinishTotal,ifnull(btj.bugNoFixTotal,0) as bugNoFixTotal,(ifnull(ttj.taskNoFinishTotal,0)+ifnull(btj.bugNoFixTotal,0)) as taskAndBugNoFixTotal")
           .append("  from T_TEAM_MEMBER teamm1 left join T_TEAM team1 on teamm1.teamId=team1.id left join T_USER u1 on u1.id=teamm1.userId")
           .append(" left join (select  count(1) as taskTotal,sum(case when t.status=0  then 1 else 0 end) as taskNoFinishTotal,tm.userId from T_TASK t left join T_TASK_MEMBER tm on t.id=tm.taskId left join T_TEAM_MEMBER teamm on teamm.userId=tm.userId left join T_TASK_AUTH")
           .append(" ta on tm.id=ta.memberId left join T_PROCESS pc on pc.id=t.processId left join T_PROJECT pj on pj.id=pc.projectId left join T_TEAM team on team.id=pj.teamId where ta.roleId=").append(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId())
           .append(" and t.del=0 and tm.del=0 and ta.del=0 and pc.del=0 and pj.del=0 and team.del=0 and teamm.del=0 and teamm.teamId=").append(teamId).append(" and team.id=").append(teamId)
           .append(" group by tm.userId) as ttj  on ttj.userId=u1.id left join (")
           .append("select sum(xxxx.bugFixAndCloseTotal) as bugTotal,sum(xxxx.bugNoFixTotal) as bugNoFixTotal,xxxx.userId ")
           .append("from (select sum(case when b.status=0  then 1 else 0 end) as bugFixAndCloseTotal,")
           .append("sum(case when b.status=0  then 1 else 0 end) as bugNoFixTotal,bm.userId ")
           .append("from T_BUG b left join T_BUG_MEMBER bm on b.id=bm.bugId left join T_BUG_AUTH ba on bm.id=ba.memberId  left join T_PROCESS pc on pc.id=b.processId ")
           .append("left join T_PROJECT pj on pj.id=pc.projectId left join T_TEAM team on team.id=pj.teamId ")
           .append("where b.del=0 and bm.del=0 and ba.del=0 and pc.del=0 and pj.del=0 and team.del=0 and team.id=").append(teamId).append("  and ba.roleId=").append(Cache.getRole(ENTITY_TYPE.BUG+"_"+ROLE_TYPE.ASSIGNEDPERSON).getId()).append(" group by userId")
           .append(" union all select count(1) as bugFixAndCloseTotal,0 as bugNoFixTotal,resolveUserId as userId from T_BUG b left join T_PROCESS pc on b.processId=pc.id ")
           .append("left join T_PROJECT pj on pc.projectId=pj.id left join T_TEAM t on pj.teamId=t.id where b.del=0 and pc.del=0 and pj.del=0 and t.del=0 and b.status!=0 and t.id=").append(teamId).append(" group by resolveUserId)  xxxx group by userId")
           .append(") as btj on btj.userId=teamm1.userId")
           .append(" left join (select count(1) as projectTotal,pjm2.userId from T_PROJECT_MEMBER pjm2 left join T_PROJECT pj2 on pjm2.projectId=pj2.id where pj2.teamId=").append(teamId).append(" and pj2.del=0 and pjm2.del=0 and pjm2.userId in (select teamm2.userId from T_TEAM_MEMBER teamm2 where teamm2.teamId=").append(teamId)
           .append(" and teamm2.del=0) group by pjm2.userId) as ptj on ptj.userId=teamm1.userId where team1.id=").append(teamId).append(" and teamm1.del=0 and teamm1.type!=").append(TEAMREALTIONSHIP.ASK.ordinal()).append(" and team1.del=0 and u1.del=0 ")
           .append(" group by teamm1.userId order by taskAndBugNoFixTotal desc limit ").append(pageStart).append(",").append(pageSizeS);
        listTotalSql.append("select count(1) from T_TEAM_MEMBER teamm1 left join T_TEAM team1 on teamm1.teamId=team1.id left join T_USER u1 on u1.id=teamm1.userId")
                    .append(" where team1.id=").append(teamId).append(" and teamm1.del=0 and teamm1.type!=").append(TEAMREALTIONSHIP.ASK.ordinal()).append(" and team1.del=0 and u1.del=0");
		List<Map<String, Object>> list=this.jdbcTpl.queryForList(listSql.toString());
		int pageTotal=this.jdbcTpl.queryForInt(listTotalSql.toString());
        taskSql.append("select  sum(case when t.status=0 then 1 else 0 end) as taskNoFinishTotal from T_TASK t  left join T_TASK_MEMBER taskm on taskm.taskId=t.id left join T_TEAM_MEMBER teamm on teamm.userId=taskm.userId left join T_TASK_AUTH ta on ta.memberId= taskm.id left join T_PROCESS pc on pc.id=t.processId left join T_PROJECT pj on pj.id=pc.projectId left join T_TEAM team on team.id=pj.teamId where t.del=0  and pc.del=0 and pj.del=0 and team.del=0 and teamm.del=0 and ta.del=0 and teamm.teamId=").append(teamId)
        .append(" and team.id=").append(teamId).append(" and ta.roleId=").append(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId());
        bugSql.append("select  sum(case when b.status=0 then 1 else 0 end) as bugNotFixTotal from T_BUG b left join T_BUG_MEMBER bm on bm.bugId=b.id left join T_TEAM_MEMBER tm on tm.userId=bm.userId left join T_BUG_AUTH ba on ba.memberId=bm.id left join T_PROCESS pc on pc.id=b.processId left join T_PROJECT pj on pj.id=pc.projectId left join T_TEAM team on team.id=pj.teamId where b.del=0 and pc.del=0 and pj.del=0 and team.del=0 and team.id=").append(teamId).append(" and tm.teamId=").append(teamId).append(" and ba.roleId=").append(Cache.getRole(ENTITY_TYPE.BUG+"_"+ROLE_TYPE.ASSIGNEDPERSON).getId());
        int taskNoFinishTotal=this.jdbcTpl.queryForInt(taskSql.toString());
        int bugNotFixTotal=this.jdbcTpl.queryForInt(bugSql.toString());
        //long taskTotal=this.jdbcTpl.queryForObject(taskSql.toString(), Long.class);
        //long bugTotal=this.jdbcTpl.queryForObject(bugSql.toString(),Long.class);
        map.put("pageTotal",pageTotal);
        map.put("taskNoFinishTotal", taskNoFinishTotal);
        map.put("bugNotFixTotal", bugNotFixTotal);
        map.put("data", list);
        return map;
	}
	//查看单个成员的统计
	public Map<String,Object> singleTeamMember(long userId,long teamId){
		StringBuffer sql=new StringBuffer();
		 sql.append("select u1.userName,ifnull(ptj.projectTotal,0) as projectTotal,ifnull(btj.bugTotal,0) as bugTotal,ifnull(ttj.taskTotal,0) as taskTotal,ifnull(ttj.taskNoFinishTotal,0) as taskNoFinishTotal,ifnull(btj.bugNoFixTotal,0) as bugNoFixTotal,(ifnull(ttj.taskNoFinishTotal,0)+ifnull(btj.bugNoFixTotal,0)) as taskAndBugNoFixTotal")
        .append("  from T_TEAM_MEMBER teamm1 left join T_TEAM team1 on teamm1.teamId=team1.id left join T_USER u1 on u1.id=teamm1.userId")
        .append(" left join (select  count(1) as taskTotal,sum(case when t.status=0 then 1 else 0 end) as taskNoFinishTotal,tm.userId from T_TASK t left join T_TASK_MEMBER tm on t.id=tm.taskId  left join T_TASK_AUTH ta on tm.id=ta.memberId left join T_PROCESS pc on pc.id=t.processId left join T_PROJECT pj on pj.id=pc.projectId left join T_TEAM team on team.id=pj.teamId where ta.roleId=").append(Cache.getRole(ENTITY_TYPE.TASK+"_"+ROLE_TYPE.MANAGER).getId())
        .append(" and t.del=0 and tm.del=0 and ta.del=0 and pc.del=0 and pj.del=0 and team.del=0 and team.id=").append(teamId)
        .append(" and tm.userId=").append(userId).append(") as ttj  on ttj.userId=u1.id left join (select sum(xxxx.bugFixAndCloseTotal) as bugTotal,sum(xxxx.bugNoFixTotal) as bugNoFixTotal,xxxx.userId from (")
        .append("select sum(case when b.status=0  then 1 else 0 end) as bugFixAndCloseTotal,sum(case when b.status=0  then 1 else 0 end) as bugNoFixTotal,bm.userId")
        .append(" from T_BUG b left join T_BUG_MEMBER bm on b.id=bm.bugId left join T_BUG_AUTH ba on bm.id=ba.memberId ")
        .append("left join T_PROCESS pc on pc.id=b.processId left join T_PROJECT pj on pj.id=pc.projectId left join T_TEAM team on team.id=pj.teamId where b.del=0 and bm.del=0 and ba.del=0 and pc.del=0 and pj.del=0 and team.del=0 and team.id=").append(teamId).append(" and bm.userId=").append(userId).append(" and ba.roleId=").append(Cache.getRole(ENTITY_TYPE.BUG+"_"+ROLE_TYPE.ASSIGNEDPERSON).getId()).append(" ")
        .append("union all select count(1) as bugFixAndCloseTotal,0 as bugNoFixTotal,resolveUserId as userId from T_BUG b ")
        .append("left join T_PROCESS pc on b.processId=pc.id left join T_PROJECT pj on pc.projectId=pj.id ")
        .append("left join T_TEAM t on pj.teamId=t.id where b.del=0 and pc.del=0 and pj.del=0 and t.del=0 and b.status!=0 and b.resolveUserId=").append(userId).append(" and t.id=").append(teamId).append(")  xxxx) as btj on btj.userId=teamm1.userId")
        .append(" left join (select count(1) as projectTotal,pjm2.userId from T_PROJECT_MEMBER pjm2 left join T_PROJECT pj2 on pjm2.projectId=pj2.id where pj2.teamId=").append(teamId).append(" and pj2.del=0 and pjm2.del=0 and pjm2.userId in (select teamm2.userId from T_TEAM_MEMBER teamm2 where teamm2.teamId=").append(teamId)
        .append(" and teamm2.del=0) and  pjm2.userId=").append(userId).append(") as ptj on ptj.userId=teamm1.userId where team1.id=").append(teamId)
        .append(" and teamm1.userId=").append(userId).append(" and teamm1.del=0");
		Map<String,Object> map=this.jdbcTpl.queryForMap(sql.toString());
		return map;
	}
	public ArrayList<Object> selectTeamMemberTj(long teamId,
			ArrayList<Long> userIdList) {
	    ArrayList<Object> list=new ArrayList<Object>();
	    for(Long singleUserId:userIdList){
	    	list.add(singleTeamMember(singleUserId,teamId));
	    }
		return list;
	}
	public ArrayList<Object> selectTeamMemberList(long teamId,
			String userNameSearch, long loginUserId,String existUserIdList) {
		 ArrayList<Object> list=new ArrayList<Object>();
		//后台权限配置
	    String permissionEnName=(ENTITY_TYPE.TEAM + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
        //项目角色权限
	    StringBuffer permissionSql=new StringBuffer();
	    permissionSql.append("select count(1) from T_TEAM_MEMBER tm left join T_TEAM_AUTH ta on tm.id=ta.memberId where tm.del=0 and ta.del=0 and tm.userId=")
	    			 .append(loginUserId).append(" and tm.teamId=").append(teamId).append(" and ta.roleId in (select roleId from T_ROLE_AUTH where del=0 and premissionid=")
	    			 .append("(select id from T_PERMISSION where del=0 and enName='").append(permissionEnName).append("'))");
	    int permission=this.jdbcTpl.queryForInt(permissionSql.toString());
	    if(permission==0){
	    	return list;
	    }else{
	    	StringBuffer teamMemberSql=new StringBuffer();
	    	teamMemberSql.append("select * from (select tm.userId,u.userName,u.icon from T_TEAM_MEMBER tm left join T_USER u on tm.userId=u.id where tm.teamId=")
	    	.append(teamId).append(" and tm.del=0 and u.del=0 and u.status=0 and (u.pinYinName like '")
	    	.append(userNameSearch).append("%' or u.pinYinHeadChar like '").append(userNameSearch)
	    	.append("%') union select tm.userId,u.userName,u.icon from T_TEAM_MEMBER tm left join T_USER u on tm.userId=u.id where tm.teamId=")
	    	.append(teamId).append(" and tm.del=0 and u.del=0 and u.status=0 and (u.pinYinName like '%")
	    	.append(userNameSearch).append("%' or u.pinYinHeadChar like '%").append(userNameSearch)
	    	.append("%') union select tm.userId,u.userName,u.icon from T_TEAM_MEMBER tm left join T_USER u on tm.userId=u.id where tm.teamId=")
	    	.append(teamId).append(" and tm.del=0 and u.del=0 and u.status=0 and (u.userName like '%")
	    	.append(userNameSearch).append("%' or u.email like '%").append(userNameSearch)
	    	.append("%' or u.account like '%").append(userNameSearch).append("%')) as User");
	    	if(existUserIdList!=null&&!existUserIdList.equals("")){
	    		teamMemberSql.append(" where userId not in (").append(existUserIdList).append(")");
	    	}
	    	teamMemberSql.append(" limit 0,10");
	    	List<Map<String,Object>> listM=this.jdbcTpl.queryForList(teamMemberSql.toString());
	    	list.add(listM);
	    	return list;
	    }
	    
	    		
	}
}
