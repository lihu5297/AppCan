package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TASK_STATUS;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.task.TaskLeaf;

public interface TaskLeafDao extends PagingAndSortingRepository<TaskLeaf, Long> {
	
	public List<TaskLeaf> findByTopTaskIdAndDelOrderByCreatedAt(Long topTaskId,DELTYPE delType);
	
	public int countByTopTaskIdAndStatusInAndDel(Long topTaskId,List<TASK_STATUS> status,DELTYPE delType);
	
	/**
	 * 查询离截止时间还有一天的子任务,和已延期一天的子任务
	 * @param advance
	 * @param deltype
	 * @return
	 */
	@Query(nativeQuery=true,value="select LEAF.* from T_TASK_LEAF LEAF LEFT JOIN T_TASK T ON LEAF.TOPTASKID=T.ID LEFT JOIN T_PROCESS P ON T.PROCESSID=P.ID "
			+ " WHERE datediff(LEAST(LEAF.deadline,P.endDate), sysdate()) = ?1 "
			+ " AND LEAF.STATUS=0 AND LEAF.DEL=?3  ")
	public List<TaskLeaf> findBySomedaytdeadLineAndStatusAndDelTaskLeaf(int someDay,int status,int deltype);
    @Query(nativeQuery=true,value="select * from T_TASK_LEAF where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2)")
	public List<TaskLeaf> getTaskLeafByProjectId(int ordinal, long projectId);
    @Query(nativeQuery=true,value="select count(1) from T_TASK_LEAF where del=?3 and managerUserId=?2 and processId in (select id from T_PROCESS where projectId=?1 and del=?3)")
	public long countByProjectIdAndUserIdAndDel(long projectId, long userId,
			int ordinal);
    @Query(nativeQuery=true,value="select * from T_TASK_LEAF where del=?3 and managerUserId=?2 and processId in (select id from T_PROCESS where projectId=?1 and del=?3)")
	public List<TaskLeaf> findByProjectIdAndManagerIdAndDel(long projectId,
			long userId, int ordinal);

    public List<TaskLeaf> findByProcessIdAndDel(long processId,DELTYPE delType);
}
