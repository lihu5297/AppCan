package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TASK_REPEATABLE;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.entity.task.Task;

public interface TaskDao extends PagingAndSortingRepository<Task, Long> {
	
	public List<Task> findByDel(DELTYPE delType);
	
	public Task findByIdAndDel(long taskId,DELTYPE delType);
	
	public long countByProcessIdAndDel(long processId, DELTYPE delType);
	
//	public Page<Task> findByNameLikeAndDelOrderByCreatedAtDesc(String query, DELTYPE normal,  Pageable pageable);
	
//	public List<Task> findByNameLikeAndDelOrderByCreatedAtDesc(String query, DELTYPE normal);

	@Query("from Task t where t.processId in (select id from Process p where p.projectId = ? and p.del =?) and t.del =?")
	public List<Task> countByProjectIdAndDel(long parseLong, DELTYPE normal, DELTYPE normal1);
	
	@Query("from Task t where (t.processId in (?1) and t.id in (?2) ) and t.appId in (?3) and t.del=?4")
	public List<Task> findTaskList(List<Long> processIdList, List<Long> idList, List<Long> appIdList, DELTYPE delType);
	
	@Query("from Task t where (t.processId in (?1) and t.id in (?2) ) and t.del=?3")
	public List<Task> findTaskList(List<Long> processIdList, List<Long> idList,  DELTYPE delType);
	
	public List<Task> findByProcessIdAndDel(long processId,DELTYPE delType);

	/**
	 * 根据任务状态和 离截止时间还有advance天   查询任务
	 * @return
	 */
	@Query(nativeQuery=true,value="SELECT t.* FROM	T_TASK t LEFT JOIN T_PROCESS p on t.processId = p.id WHERE "
			+" datediff(LEAST(t.deadline,p.endDate), sysdate()) = ?1  "
			+"  and t.status=?2  AND t.del = ?3")
	public List<Task> findBySomeDayToDeadLineAndStatusAndDel(int advance,int status,int del);
	
	
	
	//
	public List<Task> findByAppIdAndDel(long appId,DELTYPE delType);
	
	public List<Task> findByAppIdAndProcessIdInAndDel(long appId,List<Long> processId,DELTYPE delType);
	
	public List<Task> findByProcessIdInAndDel(List<Long> processId,DELTYPE delType);
	
	public List<Task> findByIdInAndDel(List<Long> taskIds,DELTYPE delType);
	
	@Query("from Task t where (t.processId in (?1) or t.id in (?2) ) and t.del=?3 order by t.createdAt desc")
	public List<Task> findUnionTaskListByProcessIdAndTaskId(List<Long> processIdList, List<Long> idList,  DELTYPE delType);

	
	/**
	 * 查询所有重复性任务  
	 * TASK_REPEATABLE.NONE
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午5:46:22
	 */
	public List<Task> findByRepeatableNotAndDel(TASK_REPEATABLE repeatable,DELTYPE del);

	@Query("select count(*) from Task t where t.processId in (select id from Process p where p.projectId = ? and p.del =?) and t.status !=? and t.del =?")
	public long countByProjectIdAndStatusAndDel(long projectId, DELTYPE normal1,TASK_STATUS closed, DELTYPE normal2);

	@Query(nativeQuery=true,value="SELECT count(*) FROM	T_TASK t WHERE t.id IN (SELECT taskId FROM T_TASK_MEMBER tm LEFT JOIN T_TASK_AUTH ta on tm.id = ta.memberId WHERE tm.userId =?  AND ta.del =? AND tm.del =?) AND t.processId IN (SELECT id FROM T_PROCESS p WHERE p.projectId =? AND p.del =?) AND t.del =? ")
	public long countByProjectIdAndUserIdAndDel(long userId,int normal,int normal1,long projectId,int normal2,int normal3);

	@Query("select count(*) from Task t where t.processId in (select id from Process p where p.projectId = ?1 and p.del =?2) and t.status in(?3) and t.del =?4")
	public long countByProjectIdAndStatusInAndDel(long projectId, DELTYPE normal, List<TASK_STATUS> status,
			DELTYPE normal2);
	
	public List<Task> findByGroupIdAndDel(Long groupId,DELTYPE delType);
	
	public int countByGroupIdAndStatusInAndDel(Long groupId,List<TASK_STATUS> status,DELTYPE delType);
	
	
	/**
	 * 获取我创建的任务
	 * @param loginUserId
	 * @param roleId
	 * @return
	 */
	@Query(nativeQuery=true, 
			value="select member.taskId from T_TASK_MEMBER member  "
			+ " where  member.type=0 and member.del=0 "
			+" and member.userId=?1  ")
	public List<Object> findCreatedTasks(long loginUserId);
	/**
	 * 获取我负责的任务,roleID为任务负责人
	 * @param loginUserId
	 * @param roleId
	 * @return
	 */
	@Query(nativeQuery=true, 
			value="select member.taskId from T_TASK_MEMBER member left join T_TASK_AUTH auth on member.id=auth.memberId "
			+ " where  member.type=1 and member.del=0 and auth.del=0 "
			+" and member.userId=?1 and auth.roleId=?2  "
			+" UNION ALL "
			+" select topTaskId from T_TASK_LEAF where managerUserId=?1 and del=0 ")
	public List<Object> findManagedTasks(long loginUserId,long roleId);
	
	/**
	 * 获取我参与的任务,roleID为任务参与人
	 * @param loginUserId
	 * @param roleId
	 * @return
	 */
	@Query(nativeQuery=true, 
			value="select member.taskId from T_TASK_MEMBER member left join T_TASK_AUTH auth on member.id=auth.memberId "
			+ " where  member.type=1 and member.del=0 and auth.del=0 "
			+" and member.userId=?1 and auth.roleId=?2  ")
	public List<Object> findJoinedTasks(long loginUserId,long roleId);

	
	public Page<Task> findByIdInAndStatusInAndDel(List<Long> ids,List<TASK_STATUS> status,DELTYPE delType,Pageable pageable);
	
	public Page<Task> findByIdInAndStatusInAndGroupIdAndDel(List<Long> ids,List<TASK_STATUS> status,Long groupId,DELTYPE delType,Pageable pageable);
    @Query(nativeQuery=true,value="select * from T_TASK where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2)")
	public List<Task> getTaskByProjectId(int normal,long projectId);
    @Query(nativeQuery=true,value="select cast(id as char) from T_TASK where del=?3 and id in (select taskId from T_TASK_MEMBER where del=?3 and userId=?2) and processId in (select id from T_PROCESS where del=?3 and projectId=?1)")
	public List<String> getTasksByProjectIdAndUserIdAndDel(long projectId,
			long userId, int ordinal);

}
