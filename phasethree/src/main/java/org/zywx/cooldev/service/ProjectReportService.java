package org.zywx.cooldev.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.BUG_STATUS;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectCategory;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskLeaf;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.vo.Match4Project;

@Service
public class ProjectReportService extends BaseService{
	@Autowired
	protected ProcessService processService;
	@Autowired
	protected ProjectService projectService;
	
	/**
	 * 根据任务id查询正常进行中、已延期、已完成的任务数目
	 *  @param taskGroupId
	 *  @return
	 */
	public List<Map<String, String>> getTaskSituation(long projectId) {
		final List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String nowDate = sdf.format(cal.getTime());
		StringBuffer situationSql=new StringBuffer("select   '正常进行中'  status1,'已延期'  status2,'已完成'  status3,");
		situationSql.append(" sum(case when t.status='0' and (case when t.deadline>t1.endDate then t1.endDate else t.deadline end)>='").append(nowDate).append("' then 1 else 0 end) count1,");
		situationSql.append(" sum(case when t.status='0' and case when t.deadline>t1.endDate then t1.endDate else t.deadline end<'").append(nowDate).append("' then 1 else 0 end) count2,");
		situationSql.append(" sum(case when t.status='1' then 1 else 0 end )count3");
		situationSql.append(" from T_TASK t left join T_PROCESS t1 on t.processId=t1.id where  t1.projectId=").append(projectId);
		situationSql.append(" and t.del=0 and t1.del=0");
		log.info(nowDate);
		log.info(" getTaskSituation sql: "+situationSql.toString());
		this.jdbcTpl.query(situationSql.toString(), 
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String,String> map = new HashMap<>();
				map.put("正常进行中", rs.getString("count1")!=null?rs.getString("count1"):"0");
				map.put("已延期", rs.getString("count2")!=null?rs.getString("count2"):"0");
				map.put("已完成", rs.getString("count3")!=null?rs.getString("count3"):"0");
				list.add(map);
			}
		});
		return list;
	}
	/**
	 * 根据任务id查询任务优先级
	 * @param taskGroupId
	 * @return Map<String,Object>
	 */
	public List<Map<String,Object>> getTaskPriority(long projectId) {
		StringBuffer prioritySql= new StringBuffer("select 'NORMAL' priority0,'URGENT' priority1,'VERY_URGENT'  priority2,sum(case when t.priority=0 then 1 else 0 end) count0, sum(case when t.priority=1 then 1 else 0 end) count1,");
		prioritySql.append(" sum(case when t.priority=2 then 1 else 0 end) count2 from T_TASK t 	where t.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) ");
		prioritySql.append(" and t.del=0");
		log.info(" getTaskPriority sql: "+prioritySql.toString());
		final List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		this.jdbcTpl.query(prioritySql.toString(),
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				if(rs.getObject("count0")==null&&rs.getObject("count1")==null&&rs.getObject("count2")==null){
					//什么都不操作直接返回空list
				}else {
					Map<String,Object> map=new HashMap<String,Object>();
					Map<String,Object> map1=new HashMap<String,Object>();
					Map<String,Object> map2=new HashMap<String,Object>();
					map.put("priority",rs.getObject("priority0"));
					map.put("count", rs.getObject("count0")!=null?rs.getString("count0"):"0");
					map1.put("priority",rs.getObject("priority1"));
					map1.put("count", rs.getObject("count1")!=null?rs.getString("count1"):"0");
					map2.put("priority",rs.getObject("priority2"));
					map2.put("count", rs.getObject("count2")!=null?rs.getString("count2"):"0");
					list.add(map);
					list.add(map1);
					list.add(map2);
				}
				
			}
			
		});
		return list;
	}

	/**
	 * 根据项目id查询每天的任务情况
	 * @param taskProjectId
	 * @param startdate
	 * @param enddate
	 * @return Map<String,Object>
	 */
	public List<Map<String, Object>> getTaskCircumstances(Long projectId, String startdate, String enddate) {
		final List<Map<String, Object>> message = new ArrayList<>();
		StringBuffer circumstancesSql= new StringBuffer("select t.taskAt,sum(t.stockNum) stockNum,sum(t.addNum) addNum,sum(t.completeNum) completeNum  from(select taskAt,sum(stockNum) stockNum,sum(addNum) addNum,sum(completeNum) completeNum from T_TASK_SURVEY");
		circumstancesSql.append(" where projectId=").append(projectId).append(" and taskAt>='").append(startdate);
		circumstancesSql.append("' and taskAt<='").append(enddate);
		circumstancesSql.append("' group by taskAt UNION ALL select timeDate,0,0,0 from T_TIMEDATE  where timedate>='").append(startdate).append("' and timedate<='").append(enddate).append("' ) t group by taskAt");
		log.info(" getTaskCircumstances sql: "+circumstancesSql.toString());
		this.jdbcTpl.query(circumstancesSql.toString(),
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String,Object> map =new HashMap<>();
				map.put("taskAt", rs.getObject("taskAt"));
				map.put("stockNum", rs.getObject("stockNum"));
				map.put("addNum", rs.getObject("addNum"));
				map.put("completeNum", rs.getObject("completeNum"));
				message.add(map);
			}
		});
		return message;
	}
	/**
	 * 根据项目id成员完成工作情况
	 *  @param taskProjectId
	 */
	public List<Map<String, Object>> membersCompleteSituation(Long projectId) {
		final List<Map<String, Object>> message = new ArrayList<>();
//		StringBuffer countSql= new StringBuffer(" select count(1) from ( select t.userId from (select  distinct member.taskId id,member.userId from T_TASK task");
//		countSql.append(" left join T_TASK_MEMBER member on task.id=member.taskId left join T_TASK_AUTH auth on auth.memberId = member.id ");
//		countSql.append(" left join T_ROLE role on role.id=auth.roleId where task.del=0 and auth.del =0 and member.del =0 and  role.enName = 'TASK_MANAGER' and task.id not in (select topTaskId from T_TASK_LEAF where del=0)");
//		countSql.append(" and task.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and task.status=0");
//		countSql.append(" union all select t.id,t.managerUserId  userId from T_TASK_LEAF t left join T_TASK t1 on t.topTaskId=t1.id");
//		countSql.append(" where t1.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and t.del=0 and t1.del=0 and t.status=0");
//		countSql.append(" union all select  distinct member.bugId id,member.userId from T_BUG bug");
//		countSql.append(" left join T_BUG_MEMBER member on bug.id=member.bugId left join T_BUG_AUTH auth  on auth.memberId = member.id");
//		countSql.append(" where auth.del =0 and member.del =0 and auth.roleId = (select id from T_ROLE where enName = 'BUG_ASSIGNEDPERSON')");
//		countSql.append(" and bug.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and bug.status=0) t group by userId) t");
//		@SuppressWarnings("deprecation")
//		int count=this.jdbcTpl.queryForInt(countSql.toString());
//		if(count<=10){
			StringBuffer sql=new StringBuffer("select t1.userName,count(1) count from (select  distinct member.taskId id,member.userId from T_TASK task left join T_TASK_MEMBER member on task.id=member.taskId");
			sql.append(" left join T_TASK_AUTH auth on auth.memberId = member.id left join T_ROLE role on role.id=auth.roleId");
			sql.append(" where task.del=0 and auth.del =0 and member.del =0 and  role.enName = 'TASK_MANAGER' ");
			sql.append(" and task.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and task.status=0");
//			sql.append(" union all select t.id,t.managerUserId  userId from T_TASK_LEAF t left join T_TASK t1 on t.topTaskId=t1.id");
//			sql.append(" where t1.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and t.del=0 and t1.del=0 and t.status=0 ");
			sql.append(" union all select  distinct member.bugId id,member.userId from T_BUG bug left join T_BUG_MEMBER member on bug.id=member.bugId");
			sql.append(" left join T_BUG_AUTH auth  on auth.memberId = member.id where auth.del =0 and member.del =0 and auth.roleId = (select id from T_ROLE where enName = 'BUG_ASSIGNEDPERSON')");
			sql.append(" and bug.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and bug.status=0) t left join T_USER t1 on t.userId=t1.id");
			sql.append(" group by userName,t1.id order by count(1) desc");
			log.info(" membersCompleteSituation sql: "+sql.toString());
			this.jdbcTpl.query(sql.toString(),
			new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					Map<String,Object> map =new HashMap<>();
					map.put("userName", rs.getObject("userName"));
					map.put("count", rs.getObject("count"));
					message.add(map);
				}
			});
//		}else if(count>10){
//			StringBuffer sql1=new StringBuffer("select t1.userName,count(1) count from (select  distinct member.taskId id,member.userId from T_TASK task left join T_TASK_MEMBER member on task.id=member.taskId");
//			sql1.append(" left join T_TASK_AUTH auth on auth.memberId = member.id left join T_ROLE role on role.id=auth.roleId");
//			sql1.append(" where task.del=0 and auth.del =0 and member.del =0 and  role.enName = 'TASK_MANAGER' and task.id not in (select topTaskId from T_TASK_LEAF where del=0)");
//			sql1.append(" and task.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and task.status=0");
//			sql1.append(" union all select t.id,t.managerUserId  userId from T_TASK_LEAF t left join T_TASK t1 on t.topTaskId=t1.id");
//			sql1.append(" where t1.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and t.del=0 and t1.del=0 and t.status=0 ");
//			sql1.append(" union all select  distinct member.bugId id,member.userId from T_BUG bug left join T_BUG_MEMBER member on bug.id=member.bugId");
//			sql1.append(" left join T_BUG_AUTH auth  on auth.memberId = member.id where auth.del =0 and member.del =0 and auth.roleId = (select id from T_ROLE where enName = 'BUG_ASSIGNEDPERSON')");
//			sql1.append(" and bug.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and bug.status=0) t left join T_USER t1 on t.userId=t1.id");
//			sql1.append(" group by userName,t1.id order by count(1) desc limit 9");
//			this.jdbcTpl.query(sql1.toString(),
//					new RowCallbackHandler() {
//						@Override
//						public void processRow(ResultSet rs) throws SQLException {
//							Map<String,Object> map =new HashMap<>();
//							map.put("userName", rs.getObject("userName"));
//							map.put("count", rs.getObject("count"));
//							message.add(map);
//						}
//			});
//			StringBuffer sql2=new StringBuffer("select '其他' userName,sum(1) count from (select t1.userName,count(1) count from (select  distinct member.taskId id,member.userId from T_TASK task left join T_TASK_MEMBER member on task.id=member.taskId");
//			sql2.append(" left join T_TASK_AUTH auth on auth.memberId = member.id left join T_ROLE role on role.id=auth.roleId");
//			sql2.append(" where task.del=0 and auth.del =0 and member.del =0 and  role.enName = 'TASK_MANAGER' and task.id not in (select topTaskId from T_TASK_LEAF where del=0)");
//			sql2.append(" and task.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and task.status=0");
//			sql2.append(" union all select t.id,t.managerUserId  userId from T_TASK_LEAF t left join T_TASK t1 on t.topTaskId=t1.id");
//			sql2.append(" where t1.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and t.del=0 and t1.del=0 and t.status=0 ");
//			sql2.append(" union all select  distinct member.bugId id,member.userId from T_BUG bug left join T_BUG_MEMBER member on bug.id=member.bugId");
//			sql2.append(" left join T_BUG_AUTH auth  on auth.memberId = member.id where auth.del =0 and member.del =0 and auth.roleId = (select id from T_ROLE where enName = 'BUG_ASSIGNEDPERSON')");
//			sql2.append(" and bug.processId in (select distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0 ) and bug.status=0) t left join T_USER t1 on t.userId=t1.id");
//			sql2.append(" group by t1.userName,t1.id order by count(1) desc limit 9,").append(count).append("  )t");
//			this.jdbcTpl.query(sql2.toString(),
//					new RowCallbackHandler() {
//						@Override
//						public void processRow(ResultSet rs) throws SQLException {
//							Map<String,Object> map =new HashMap<>();
//							map.put("userName", rs.getObject("userName"));
//							map.put("count", rs.getObject("count"));
//							message.add(map);
//						}
//					});
//		}
		return message;
	}
	/**
	 * 成员未完成任务量和总任务量对比图
	 *  @param taskProjectId
	 *  @param userId
	 */
	public List<Map<String, Object>> taskCompleteSituation(Long loginUserId,Long projectId, String userId) {
		final List<Map<String, Object>> message = new ArrayList<>();
		//如果userId不为空则更新该项目下的已选则成员,如果userId为空则在数据库查询该项目已选则的成员
		if(userId!=null&&userId!=""){
			this.updateProjectUserId(loginUserId,projectId, userId);
		}else{
			userId=this.memberChoiced(loginUserId,projectId).get("userId");
		}
		StringBuffer sql= new StringBuffer("select t.userName,sum(allcount) allcount,sum(uncount) uncount from ( select t5.userName,t5.id,count(1) as allcount,sum(case when t.status=0 then 1 else 0 end ) as uncount");
		sql.append(" from T_TASK t left join T_PROCESS t1 on t.processId=t1.id left join T_TASK_MEMBER t2 on t.id=t2.taskId left join T_TASK_AUTH t3 on t3.memberId = t2.id");
		sql.append(" left join T_ROLE t4 on t4.id=t3.roleId left join T_USER t5 on t2.userId=t5.id where t.del=0 and t1.del=0 and t3.del =0 and t2.del =0 and t5.del=0");
		sql.append(" and t1.projectId=").append(projectId).append(" and t2.userId in (").append(userId).append(") and  t4.enName = 'TASK_MANAGER'  group by t5.userName,t5.id");
//		sql.append(" UNION ALL select t8.userName,t8.id,count(1) as allcount,sum(case when t6.status=0 then 1 else 0 end ) as uncount from T_TASK_LEAF t6");
//		sql.append(" inner join T_TASK t7 on t6.topTaskId=t7.id left join T_USER t8 on t6.managerUserId=t8.id left join T_PROCESS t9 on t9.id=t7.processId");
//		sql.append(" where t6.del=0 and t7.del=0 and t8.del=0 and t9.del=0 and t9.projectId=").append(projectId).append(" and t6.managerUserId in (").append(userId).append(") group by t8.userName,t8.id");
		sql.append(" union all select t.userName,id,0,0 from T_USER t where t.id in (").append(userId).append(") ) t group by t.userName,t.id");
		log.info(" taskCompleteSituation sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(),
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String,Object> map =new HashMap<>();
				map.put("userName", rs.getObject("userName"));
				map.put("allcount", rs.getObject("allcount"));
				map.put("uncount", rs.getObject("uncount"));
				message.add(map);
			}
		});

		return message;
	}
	/**
	 * 成员详情
	 * @param projectId
	 */
	public Map<String, Object> memberDetails(int pageNo, int pageSize, long projectId , String detail, String sequence) {
		final List<Map<String, Object>> message = new ArrayList<>();
		Map<String,Object> remap =new HashMap<>();
		StringBuffer sql= new StringBuffer("select t.userName,sum(taskcount) taskCount,sum(taskficount) taskFinishCount,sum(taskuncount) taskUnCount, sum(bugcount) bugCount,sum(bugficount) bugFinishcount,sum(buguncount) bugUnCount");
//		sql.append(" from (select tmp.userName,tmp.id,sum(allcount) taskcount,sum(count) taskficount,sum(uncount) taskuncount,0 bugcount,0 bugficount,0 buguncount ");
		sql.append(" from (select t5.userName,t5.id,count(1) taskcount,sum(case when t1.status=1 then 1 else 0 end) taskficount,sum(case when t1.status=0 then 1 else 0 end)  taskuncount,0 bugcount,0 bugficount,0 buguncount");
		sql.append(" from T_TASK t1 left join T_TASK_MEMBER t2 on t1.id=t2.taskId left join T_TASK_AUTH t3 on t2.id=t3.memberId  left join T_ROLE t4 on t3.roleid=t4.id");
		sql.append(" left join T_USER t5 on t2.userId=t5.id where t1.del=0 and t2.del=0 and t3.del=0 and t4.del=0 ");
		sql.append(" and t4.enName='TASK_MANAGER' and t1.processId in (select id from T_PROCESS tt where tt.projectId=").append(projectId).append(" and del=0) group by t5.userName,t5.id");
//		sql.append(" UNION ALL select t3.userName,t3.id,count(1) allcount,sum(case when t2.status=1 then 1 else 0 end) count,sum(case when t2.status=0 then 1 else 0 end)  uncount");
//		sql.append(" from T_TASK t1 inner join T_TASK_LEAF t2 on t1.id=t2.topTaskId left join T_USER t3 on t2.managerUserId=t3.id where  t1.del=0 and t2.del=0  and  t1.processId in (select id from T_PROCESS tt where tt.projectId=").append(projectId).append(" and del=0)");
		sql.append(" UNION ALL select t5.userName,t5.id,0 taskcount,0 taskficount,0 taskuncount,count(1) bugcount,");
		sql.append(" sum(case when t1.status=1 then 1 else 0 end) bugficount, sum(case when t1.status=0 then 1 else 0 end)  buguncount");
		sql.append(" from T_BUG t1 left join T_BUG_MEMBER t2 on t1.id=t2.bugId left join T_BUG_AUTH t3 on t2.id=t3.memberId left join T_ROLE t4 on t3.roleId=t4.id");
		sql.append(" left join T_USER t5 on t5.id=t2.userId where t1.del=0 and t2.del=0 and t3.del=0 and t4.del=0 and t4.enName='BUG_ASSIGNEDPERSON'");
		sql.append(" and t1.processId in (select id from T_PROCESS tt where tt.projectId=").append(projectId).append(" and del=0) and t1.status =0 group by t5.userName,t5.id");
		sql.append(" UNION ALL select t5.userName,t5.id,0 taskcount,0 taskficount,0 taskuncount,count(1) bugcount,sum(case when  t1.status in (1,2) then 1 else 0 end) bugficount,");
		sql.append(" sum(case when t1.status=0 then 1 else 0 end)  buguncount from T_BUG t1 left join T_USER t5 on t5.id=t1.resolveUserId where t1.del=0 and t1.resolveUserId!=0");
		sql.append(" and t1.processId in (select id from T_PROCESS tt where tt.projectId=").append(projectId).append(" and del=0) and t1.status in (1,2) group by t5.userName,t5.id");
		sql.append(" UNION ALL select t2.userName,t2.id,0 taskcount,0 taskficount,0 taskuncount,0 bugcount,0 bugficount,0 buguncount from T_PROJECT t left join T_PROJECT_MEMBER t1 on t.id=t1.projectId");
		sql.append(" left join T_USER t2 on t1.userId=t2.id where t.id=").append(projectId).append(" and t.del=0 and t1.del=0 ) t   group by t.userName,t.id");
		if(StringUtils.isNotBlank(detail)&&StringUtils.isNotBlank(sequence)){
			sql.append(" order by ").append(detail).append(" ").append(sequence);
		}
		sql.append(" limit ").append(pageNo*pageSize).append(",").append(pageSize);
		log.info(" memberDetails sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(),
				new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String,Object> map =new HashMap<>();
				map.put("userName", rs.getObject("userName"));
				map.put("taskCount", rs.getObject("taskCount"));
				map.put("taskFinishCount", rs.getObject("taskFinishCount"));
				map.put("taskUnCount", rs.getObject("taskUnCount"));
				map.put("bugCount", rs.getObject("bugCount"));
				map.put("bugFinishcount", rs.getObject("bugFinishcount"));
				map.put("bugUnCount", rs.getObject("bugUnCount"));
				message.add(map);
			}
		});
		remap.put("data",message);
		StringBuffer countSql=new StringBuffer("select count(1) from ( select t2.userName from T_PROJECT t left join T_PROJECT_MEMBER t1 on t.id=t1.projectId");
		countSql.append(" left join T_USER t2 on t1.userId=t2.id where t.id=").append(projectId).append(" and t.del=0 and t1.del=0 ) t");
		@SuppressWarnings("deprecation")
		int pageTotal=this.jdbcTpl.queryForInt(countSql.toString());
		remap.put("pageTotal", pageTotal);
		return remap;
	}
	/**
	 * BUG概况
	 * @param projectId
	 */
	public List<Map<String, String>> getBugSituation(Long projectId) {
		final List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		StringBuffer sql=new StringBuffer("select sum(case when t.status=0 then 1 else 0 end)  count1,sum(case when t.status=1 then 1 else 0 end)  count2,");
		sql.append(" sum(case when t.status=2 then 1 else 0 end)  count3 from T_BUG t ");
		sql.append(" where t.processId in (SELECT distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0) and t.del=0");
		log.info(" getBugSituation sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(),new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String,String> map =new HashMap<>();
				map.put("NOTFIX", rs.getString("count1")!=null?rs.getString("count1"):"0");
				map.put("FIXED", rs.getString("count2")!=null?rs.getString("count2"):"0");
				map.put("CLOSED", rs.getString("count3")!=null?rs.getString("count3"):"0");
				list.add(map);
			}
		});
		return list;
	}
	/**
	 * BUG优先级分布
	 * @param projectId
	 */
	public List<Map<String, String>> getBugPriority(Long projectId) {
		final List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer("select  'NORMAL' priority1,'URGENT' priority2,'VERY_URGENT'  priority3,");
		sql.append(" sum(case when t.priority=0 then 1 else 0 end) count1,");
		sql.append(" sum(case when t.priority=1 then 1 else 0 end) count2,");
		sql.append(" sum(case when t.priority=2 then 1 else 0 end) count3");
		sql.append(" from T_BUG t where t.processId in (select id from T_PROCESS where projectId=").append(projectId).append(" and del=0) and del=0");
		log.info(" getBugPriority sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(),new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				if(rs.getObject("count1")==null&&rs.getObject("count2")==null&&rs.getObject("count3")==null){
					//什么都不操作直接返回空list
				}else {
					Map<String,String> map =new HashMap<>();
					Map<String,String> map1 =new HashMap<>();
					Map<String,String> map2 =new HashMap<>();
					map.put("priority", rs.getString("priority1"));
					map.put("count", rs.getString("count1")!=null?rs.getString("count1"):"0");
					map1.put("priority", rs.getString("priority2"));
					map1.put("count", rs.getString("count2")!=null?rs.getString("count2"):"0");
					map2.put("priority", rs.getString("priority3"));
					map2.put("count", rs.getString("count3")!=null?rs.getString("count3"):"0");
					list.add(map);
					list.add(map1);
					list.add(map2);
				}
				
			}
		});
  		return list;
	}
	/**
	 * BUG驻留情况
	 * @param projectId
	 */
	public List<Map<String, String>> getBugRside(Long projectId) {
		final List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer("select case when TO_DAYS(now())-TO_DAYS(t.createdAt)>=60 then '两个月以上'  when TO_DAYS(now())-TO_DAYS(t.createdAt)>=30 and TO_DAYS(now())-TO_DAYS(t.createdAt)<60 then '一个月以上不足两个月'");
		sql.append(" when TO_DAYS(now())-TO_DAYS(t.createdAt)>=14 and TO_DAYS(now())-TO_DAYS(t.createdAt)<30 then '两个周以上不足一个月'  when TO_DAYS(now())-TO_DAYS(t.createdAt)>=7 and TO_DAYS(now())-TO_DAYS(t.createdAt)<14 then '一周以上不足两周'");
		sql.append(" when TO_DAYS(now())-TO_DAYS(t.createdAt)<7 then '一周以内' end as state,");
		sql.append(" sum(case when TO_DAYS(now())-TO_DAYS(t.createdAt)>=60 then 1  when TO_DAYS(now())-TO_DAYS(t.createdAt)>=30 and TO_DAYS(now())-TO_DAYS(t.createdAt)<60 then 1");
		sql.append(" when TO_DAYS(now())-TO_DAYS(t.createdAt)>=14 and TO_DAYS(now())-TO_DAYS(t.createdAt)<30 then 1  when TO_DAYS(now())-TO_DAYS(t.createdAt)>=7 and TO_DAYS(now())-TO_DAYS(t.createdAt)<14 then 1 ");
		sql.append(" when TO_DAYS(now())-TO_DAYS(t.createdAt)<7 then 1  end)  as count");
		sql.append(" from T_BUG  t where t.processId in (SELECT distinct id from T_PROCESS where projectId=").append(projectId).append(" and del=0) and t.del=0 and t.status in (0,1) group by 1");
		log.info(" getBugRside sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				Map<String,String> map =new HashMap<>();
				map.put("state", rs.getString("state"));
				map.put("count", rs.getString("count"));
				list.add(map);
			}
		});
		return list;
	}
	/**
	 * 每天的BUG情况
	 * @param projectId
	 */
	public List<Map<String, String>> getBugCircumstances(Long projectId,String startdate, String enddate) {
		final List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer("select t.bugAt,sum(t.stockNum) stockNum,sum(t.addNum) addNum,sum(t.completeNum) completeNum  from(select bugAt,sum(stockNum) stockNum,sum(addNum) addNum,sum(completeNum) completeNum from T_BUG_SURVEY t left join T_USER t1 on t.managerUserId=t1.id");
		sql.append(" where projectId=").append(projectId).append(" and bugAt>='").append(startdate).append("' and bugAt<='").append(enddate);
		sql.append("' group by bugAt UNION ALL select timeDate,0,0,0 from T_TIMEDATE  where timedate>='").append(startdate).append("' and timedate<='").append(enddate).append("' ) t group by bugAt");
		log.info(" getBugCircumstances sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				Map<String,String> map =new HashMap<>();
				map.put("bugAt", rs.getString("bugAt"));
				map.put("stockNum", rs.getString("stockNum"));
				map.put("addNum", rs.getString("addNum"));
				map.put("completeNum", rs.getString("completeNum"));
				list.add(map);
			}
		});
		return list;
	}
	/**
	 * 成员未完成BUG量和总BUG量
	 * @param projectId
	 * @param userId
	 */
	public List<Map<String, String>> bugCompleteSituation(Long loginUserId,Long projectId, String userId) {
		final List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if(userId==null||userId==""){
			userId=this.memberChoiced(loginUserId,projectId).get("userId");
		}
		StringBuffer sql = new StringBuffer("select t.userName,sum(allcount) allcount,sum(uncount) uncount from (");
		sql.append(" select t5.userName,t5.id,count(1) as allcount,sum(case when t.status=0 then 1 else 0 end ) as uncount");
		sql.append(" from T_BUG t left join T_PROCESS t1 on t.processId=t1.id left join T_BUG_MEMBER t2 on t.id=t2.bugId");
		sql.append(" left join T_BUG_AUTH t3 on t3.memberId = t2.id left join T_ROLE t4 on t4.id=t3.roleId");
		sql.append(" left join T_USER t5 on t2.userId=t5.id where t.del=0 and t1.del=0 and t3.del =0 and t2.del =0 and t5.del=0");
		sql.append(" and t4.enName = 'BUG_ASSIGNEDPERSON'  and t.status=0 and t1.projectId=").append(projectId).append(" and t5.id in (").append(userId).append(") group by t5.userName,t5.id");
		sql.append(" union all select t5.userName,t5.id,count(1) allcount,sum(case when t1.status=1 then 1 else 0 end)  uncount");
		sql.append(" from T_BUG t1 left join T_USER t5 on t5.id=t1.resolveUserId where t1.del=0 and t1.resolveUserId!=0");
		sql.append(" and t1.processId in (select id from T_PROCESS tt where tt.projectId=").append(projectId).append(" and tt.del=0) and t1.status in (1,2) and t5.id in (").append(userId).append(")");
		sql.append(" group by t5.userName,t5.id union all select t.userName,t.id,0,0 from T_USER t where t.id in (").append(userId).append(") ) t group by t.userName,t.id");
		log.info(" bugCompleteSituation sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				Map<String,String> map =new HashMap<>();
				map.put("userName", rs.getString("userName"));
				map.put("allcount", rs.getString("allcount"));
				map.put("uncount", rs.getString("uncount"));
				list.add(map);
			}
		});
		return list;
	}
	/**
	 * 成员每天完成任务情况
	 * @param projectId
	 * @param userId
	 * @param startdate
	 * @param enddate 
	 */
	public List<Object> memberTaskCompleteSituation(Long projectId, String userId, String startdate,
			String enddate,long loginUserId) {
		final List<Object> list = new ArrayList<>();
		final List<Map<String,String>> list1 = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer("select taskAt,userName,id userId,sum(stockNum) stockNum,sum(addNum) addNum,sum(completeNum) completeNum  from (");
		sql.append(" select taskAt,t1.userName,t1.id,sum(stockNum) stockNum,sum(addNum) addNum,sum(completeNum) completeNum");
		sql.append(" from T_TASK_SURVEY t left join T_USER t1 on t.managerUserId=t1.id");
		sql.append(" where projectId=").append(projectId).append(" and taskAt>='").append(startdate).append("' and taskAt<='").append(enddate).append("' and managerUserId in(").append(userId).append(")");
		sql.append(" group by t.taskAt,t1.userName union ALL select timeDate as taskAt,userName,id,0,0,0 from (");
		sql.append(" select t.timeDate,t1.userName,t1.id from T_TIMEDATE t,T_USER t1");
		sql.append(" where t.timedate>='").append(startdate).append("' and t.timedate<='").append(enddate).append("' and t1.id in  (").append(userId).append(")");
		sql.append(" ) t ) t group by taskAt,userName,id ORDER BY userName,taskAt");
		log.info(" memberTaskCompleteSituation sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				Map<String,String> map =new HashMap<>();
				map.put("userName", rs.getString("userName"));
				map.put("taskAt", rs.getString("taskAt"));
				map.put("stockNum", rs.getString("stockNum"));
				map.put("addNum", rs.getString("addNum"));
				map.put("completeNum", rs.getString("completeNum"));
				map.put("userId", rs.getString("userId"));
				list1.add(map);
			}
		});
		String id[]=userId.split(",");
		for (int i=0;i<id.length;i++){
			List<Map<String,String>> list2=new ArrayList<>();
			for (int j=0;j<list1.size();j++){
				if (id[i].equals(list1.get(j).get("userId"))){
					Map<String,String> map1 =new HashMap<>();
					map1=list1.get(j);
					//map1.remove("userId");
					list2.add(map1);
				}
			}
//			Map<String,Object> map2 =new HashMap<>();
//			map2.put(list2.get(0).get("userName"),list2);
			list.add(list2);
		}
		return list;
	}
	/**
	 * 成员每天完成BUG情况
	 * @param projectId
	 * @param userId
	 * @param startdate
	 * @param enddate 
	 */
	public List<Object> memberBugCompleteSituation(Long projectId, String userId, String startdate,
			String enddate) {
		final List<Object> list = new ArrayList<>();
		final List<Map<String,String>> list1 = new ArrayList<Map<String,String>>();
		StringBuffer sql = new StringBuffer("select bugAt,userName,id userId,sum(stockNum) stockNum,sum(addNum) addNum,sum(completeNum) completeNum  from (");
		sql.append(" select bugAt,t1.userName,t1.id,sum(stockNum) stockNum,sum(addNum) addNum,sum(completeNum) completeNum");
		sql.append(" from T_BUG_SURVEY t left join T_USER t1 on t.managerUserId=t1.id");
		sql.append(" where projectId=").append(projectId).append(" and bugAt>='").append(startdate).append("' and bugAt<='").append(enddate).append("' and managerUserId in(").append(userId).append(")");
		sql.append(" group by t.bugAt,t1.userName union ALL select timeDate as bugAt,userName,id,0,0,0 from (");
		sql.append(" select t.timeDate,t1.userName,t1.id from T_TIMEDATE t,T_USER t1");
		sql.append(" where t.timedate>='").append(startdate).append("' and t.timedate<='").append(enddate).append("' and t1.id in  (").append(userId).append(")");
		sql.append(" ) t ) t group by bugAt,userName,id ORDER BY userName,bugAt");
		log.info(" memberBugCompleteSituation sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				Map<String,String> map =new HashMap<>();
				map.put("userName", rs.getString("userName"));
				map.put("bugAt", rs.getString("bugAt"));
				map.put("stockNum", rs.getString("stockNum"));
				map.put("addNum", rs.getString("addNum"));
				map.put("completeNum", rs.getString("completeNum"));
				map.put("userId", rs.getString("userId"));
				list1.add(map);
			}
		});
		String id[]=userId.split(",");
		for (int i=0;i<id.length;i++){
			List<Map<String,String>> list2=new ArrayList<>();
			for (int j=0;j<list1.size();j++){
				if (id[i].equals(list1.get(j).get("userId"))){
					Map<String,String> map1 =new HashMap<>();
					map1=list1.get(j);
					list2.add(map1);
				}
			}
			list.add(list2);
		}
		return list;
	}
	public Map<String, String> projectCreatedAt(Long projectId) {
		final Map<String,String> map = new HashMap<String,String>();
		StringBuffer sql = new StringBuffer("select DATE_FORMAT(t.createdAt,'%Y-%m-%d') createdAt from T_PROJECT t where t.id=").append(projectId);
		log.info(" projectCreatedAt sql: "+sql.toString());
		this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				map.put("createdAt", rs.getString("createdAt"));
			}
		});
		return map;
	}
	/**
	 * 查询该项目下已选则的成员
	 * @param projectId
	 */
	public List<Map<String, String>> selectMemberChoiced(Long loginUserId,Long projectId) {
		final List<Map<String,String>> list = new ArrayList<>();
		final Map<String,String> map=new HashMap<>();
		StringBuffer userIdSql=new StringBuffer("select userId from T_USERCHOICED where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId);
		log.info(" selectMemberChoiced sql: "+userIdSql.toString());
		this.jdbcTpl.query(userIdSql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				map.put("userId", rs.getString("userId"));
			}
		});
		if(map.size()==0){
			return list;
		}else{
//			String userId[]=map.get("userId").split(",");
			StringBuffer sql = new StringBuffer("select id userId,userName,icon from T_USER where  FIND_IN_SET (id,(select userId  from  T_USERCHOICED where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId).append(") )");
//			for (int i=0;i<userId.length;i++){
//				sql.append(Integer.parseInt(userId[i])).append(",");
//			}
//			sql.deleteCharAt(sql.length()-1); 
			log.info(sql.toString());
			this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
				@Override
				public void processRow(ResultSet rs) throws SQLException{
					Map<String,String> map1=new HashMap<>();
					map1.put("userId", rs.getString("userId"));
					map1.put("userName", rs.getString("userName"));
					map1.put("userIcon", rs.getString("icon"));
					list.add(map1);
				}
			});
			return list;
		}
		
	}
	public Map<String, String> memberChoiced(Long loginUserId,Long projectId) {
		final Map<String,String> map=new HashMap<>();
		StringBuffer userIdSql=new StringBuffer("select userId from T_USERCHOICED where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId);
		this.jdbcTpl.query(userIdSql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				map.put("userId", rs.getString("userId"));
			}
		});
		return map;
	}
	/**
	 * 更新已选则的成员
	 * @param projectId
	 */
	public void updateProjectUserId(Long loginUserId,Long projectId,String userId){
		StringBuffer deleteSql=new StringBuffer("delete from T_USERCHOICED where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId);
		StringBuffer insertSql=new StringBuffer("insert into T_USERCHOICED(projectId,userId,loginUserId) VALUES(").append(projectId).append(",'").append(userId).append("',").append(loginUserId).append(")");
		this.jdbcTpl.execute(deleteSql.toString());
		this.jdbcTpl.execute(insertSql.toString());
	}
	
	
	public Map<String, Object> getWorkPlatList(Match4Project matchObj, long loginUserId) throws ParseException{

		log.info(this.getClass().getSimpleName()+" Method getWorkPlatList is called. loginUserId = "+loginUserId);
		
		final List<Project> pList = new ArrayList<Project>();
		
		StringBuffer roleIds = new StringBuffer();
		roleIds.append(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR).getId());
		roleIds.append(","+Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId());
		
		StringBuffer querySql= new StringBuffer(" SELECT a.*,b.sort FROM (SELECT p.* FROM T_PROJECT p left join (SELECT MAX(endDate) endDate,projectId");
		querySql.append(" FROM T_PROCESS WHERE del=0 and projectId=").append(matchObj.getProjectId()).append(") t on p.id=t.projectId WHERE  p.id=").append(matchObj.getProjectId());
		querySql.append(" AND p.del =0 AND p.status in (1,-1)) a ");
		querySql.append(" LEFT JOIN (select * from T_PROJECT_SORT WHERE userId=").append(loginUserId).append(" ) b ON a.id=b.projectId  ORDER BY b.sort DESC ");
		log.info(" getWorkPlatList sql: "+querySql.toString());
		this.jdbcTpl.query(querySql.toString(), new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Project p  = new Project();
				p.setCreatedAt(rs.getTimestamp("createdAt"));
				p.setCategoryId(rs.getLong("categoryId"));
				ProjectCategory pc = projectCategoryDao.findOne(p.getCategoryId());
				if(null!=pc){
					p.setCategoryName(pc.getName());
				}
				p.setTeamId(rs.getLong("teamId"));
				p.setType(PROJECT_TYPE.values()[(int) rs.getLong("type")]);
				p.setId(rs.getLong("id"));
				p.setName(rs.getString("name"));
				p.setStatus(PROJECT_STATUS.values()[(int)rs.getLong("status")]);
				p.setBizLicense(PROJECT_BIZ_LICENSE.values()[(int)rs.getLong("bizLicense")]);
				p.setBizCompanyId(rs.getString("bizCompanyId"));
				p.setUpdatedAt(rs.getTimestamp("updatedAt"));
				p.setBizCompanyName(rs.getString("bizCompanyName"));
				p.setSort(rs.getLong("sort"));
				p.setProgress(rs.getInt("progress"));
				pList.add(p);
			}
		});
		log.info(String.format("search project querysql:[%s]", querySql));
		
		List<Map<String, Object>> projectMapList = new ArrayList<>();
		
		//遍历记录进行扩展
		if(pList != null && pList.size() > 0) {
			for(Project p : pList) {
				
				// 扩展团队信息
				Team team = teamDao.findOne(p.getTeamId());
				if(team != null) {
					p.setTeamName(team.getName());
				}
				ProjectMember pm = projectMemberDao.findByProjectIdAndTypeAndDel(p.getId(), PROJECT_MEMBER_TYPE.CREATOR, DELTYPE.NORMAL);
				if(null!=pm){
					User userCreator = userDao.findOne(pm.getUserId());
					if(null!=userCreator && StringUtils.isNotBlank(userCreator.getUserName())){
						p.setCreator(userCreator.getUserName());
					}
				}
				//扩展流程信息
				List<Process> plist = processDao.findByProjectIdAndDel(p.getId(), DELTYPE.NORMAL);
				
				List<Map<String, Object>> processMapList = new ArrayList<>();
				//项目进度
				for(Process process : plist) {
					
					long memberTotal = processMemberDao.countByProcessIdAndDel(process.getId(), DELTYPE.NORMAL);
					process.setMemberTotal(memberTotal);
					
					StringBuffer taskTotalCount=new StringBuffer("select count(1) count from T_TASK t where t.processId=").append(process.getId()).append(" and t.del=0");
					@SuppressWarnings("deprecation")
					long taskTotal=(long)jdbcTpl.queryForInt(taskTotalCount.toString());
					process.setTaskTotal(taskTotal);
					
					StringBuffer taskUnfinishCount=new StringBuffer("select count(1) count from T_TASK t where t.processId=").append(process.getId()).append(" and t.del=0 and  t.status=0");
					@SuppressWarnings("deprecation")
					long taskUnfinish=(long)jdbcTpl.queryForInt(taskUnfinishCount.toString());
					process.setTaskUnfinishTotal(taskUnfinish);
					
					StringBuffer bugTotalCount=new StringBuffer("select count(1) count from T_BUG t where t.processId=").append(process.getId()).append(" and t.del=0 ");
					@SuppressWarnings("deprecation")
					long bugTotal=(long)jdbcTpl.queryForInt(bugTotalCount.toString());
					process.setBugTotal(bugTotal);
					
					
					StringBuffer bugUnfinishCount=new StringBuffer("select count(1) count from T_BUG t where t.processId=").append(process.getId()).append(" and t.del=0 and t.status=0");
					@SuppressWarnings("deprecation")
					long bugUnfinish=(long)jdbcTpl.queryForInt(bugUnfinishCount.toString());
					process.setBugUnfinishTotal(bugUnfinish);
					
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
		
		//=============================查询项目分类==================================//
		List<PROJECT_MEMBER_TYPE> memberType = matchObj.getMemberType();
		//我参与 我创建的项目主键
		List<Long> projectIds = this.projectMemberDao.findByUserIdAndTypeIn(loginUserId, memberType);
		//我创建 我管理的团队
		List<Long> roleIds1 = new ArrayList<>();//团队下的普通成员是不可以查看团队下项目的,所以下面的角色没有团队成员
		roleIds1.add(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR).getId());
		roleIds1.add(Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR).getId());
		List<Long> teamIds1 = this.teamMemberDao.findByUserIdAndRoleIdAndDel(loginUserId,roleIds1,DELTYPE.NORMAL);
		if(null!=teamIds1 && teamIds1.size()>0){
			//我的查看的项目
			List<Long> projectIdss = this.projectDao.findByTeamIdInAndDel(teamIds1, DELTYPE.NORMAL);
			//合并我的项目和我能查看的项目
			projectIds.addAll(projectIdss);
			}
		//项目主键去重
		Set<Long> setCategoryProIds = new HashSet<>(projectIds);
		projectIds.clear();
		Iterator<Long> itCategoryProIds = setCategoryProIds.iterator();
		while (itCategoryProIds.hasNext())
		{
			projectIds.add(itCategoryProIds.next());
		}
		//项目id为空时 添加伪id
		if(null!=projectIds && projectIds.isEmpty()){
			projectIds.add(-99L);
		}
//		List<ProjectCategory> categoryMap1 = this.projectCategoryDao.findByProjectIdsAndDel(projectIds,DELTYPE.NORMAL);
		//=============================查询项目分类==================================//		
		

		
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("message", projectMapList);
		retMap.put("status", "success");
//		retMap.put("category", categoryMap1);
		
		return retMap;
	}
	/**
	 * teamIds首选需要默认加一个-99之类的,方法判断teamIds长度大于1才根据teamIds查询
	 * 补充说明:teamId存在,则根据teamId查,否则根据teamName查询
	 * @param teamIds
	 * @param teamName
	 * 注意参数的teamName是需要传进来百分号的
	 * @return
	 */
	public List<Long> getProjectIdsByTeam(List<Long> teamIds, String teamName) {
		if(teamIds.size()>1){
			return this.projectDao.findByTeamIdInAndDel(teamIds,DELTYPE.NORMAL);
		}else if(StringUtils.isNotBlank(teamName)){
			return this.projectDao.findByTeamNameAndDel(teamName,DELTYPE.NORMAL);
		}else{
			return new ArrayList<>();
		}
	}
	
	public Map<String, Object> memberCompleteChoiced(long loginUserId, Long projectId) {
		final Map<String,Object> remap = new HashMap<>();
		final Map<String,String> map=new HashMap<>();
		final List<Map<String,String>> list=new ArrayList<>();
		StringBuffer userIdSql=new StringBuffer("select userId from T_MEMBER_COMPLETE where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId);
		this.jdbcTpl.query(userIdSql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException{
				map.put("userId", rs.getString("userId"));
			}
		});
		if(map.size()==0){
			return remap;
		}else{
			StringBuffer sql = new StringBuffer("select id userId,userName,icon from T_USER where  FIND_IN_SET (id,(select userId  from  T_MEMBER_COMPLETE where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId).append(") )");
//			log.info(sql.toString());
			this.jdbcTpl.query(sql.toString(), new RowCallbackHandler(){
				@Override
				public void processRow(ResultSet rs) throws SQLException{
					Map<String,String> map1=new HashMap<>();
					map1.put("userId", rs.getString("userId"));
					map1.put("userName", rs.getString("userName"));
					map1.put("userIcon", rs.getString("icon"));
					list.add(map1);
					}
			});
			StringBuffer chartSql=new StringBuffer("select distinct chartContent from T_MEMBER_COMPLETE where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId);
			this.jdbcTpl.query(chartSql.toString(), new RowCallbackHandler(){
				@Override
				public void processRow(ResultSet rs) throws SQLException{
					Map<String,String> map2=new HashMap<>();
					map2.put("chartContent", rs.getString("chartContent"));
					remap.put("charInfo",map2);
				}
			});
			remap.put("userInfo", list);	
			return remap;
		}
	}
	/**
	 * 成员每天完成工作情况保存已选则的成员和图表内容
	 * @param projectId
	 * @param chartContent
	 */
	public int putChoiced(long loginUserId, Long projectId, String chartContent, String userId) {
		StringBuffer deleteSql=new StringBuffer("delete from T_MEMBER_COMPLETE where projectId=").append(projectId).append(" and loginUserId=").append(loginUserId);
		StringBuffer updateSql=new StringBuffer(" insert into T_MEMBER_COMPLETE (projectId,loginUserId,userId,chartContent) VALUES(").append(projectId).append(",").append(loginUserId).append(",'").append(userId).append("',").append(chartContent).append(")");
		jdbcTpl.update(deleteSql.toString());
		return jdbcTpl.update(updateSql.toString());
	}
}
