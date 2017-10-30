package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.task.TaskTag;

public interface TaskTagDao extends PagingAndSortingRepository<TaskTag, Long> {
	
	public List<TaskTag> findByTaskIdAndDel(long taskId, Enums.DELTYPE delType);

	public TaskTag findOneByTaskIdAndTagId(long taskId, long tagId);

	public void removeByTaskIdAndTagId(long taskId, long tagId);
    @Query(nativeQuery=true,value="select * from T_TASK_TAG where del=?1 and taskId in (select id from T_TASK where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2))")
	public List<TaskTag> getTaskTagByProjectId(int ordinal, long projectId);
	
	/**
	 * 根据任务ID列表查询对应的标签名称
	 * @user jingjian.wu
	 * @date 2016年3月14日 上午11:21:50
	 */
//	@Query(nativeQuery=true,value="SELECT * FROM T_TAG  WHERE del=0 AND id IN (  SELECT tagId FROM T_TASK_TAG WHERE del=0 AND taskId IN (?1)) and name like ?2")
//	public List<TaskTag> findNameForTaskList(List<Long> taskIds,String name);
	
//	@Query(nativeQuery=true,value="SELECT distinct name FROM T_TAG  WHERE del=0 AND id IN (  SELECT tagId FROM T_TASK_TAG WHERE del=0 AND taskId IN (?1)) and name like ?2")
//	public List<String> findNameForTaskList(List<Long> taskIds,String name);
	
}
