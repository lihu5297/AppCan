package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.task.TaskComment;

public interface TaskCommentDao extends PagingAndSortingRepository<TaskComment, Long> {
	
	public List<TaskComment> findByDel(DELTYPE delType);
	
	public long countByTaskIdAndDel(long taskId, DELTYPE delType);
	
	public List<TaskComment> findByTaskIdAndDelOrderByCreatedAtDesc(long taskId, DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_TASK_COMMENT where del=?1 and taskId in (select id from T_TASK where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2))")
	public List<TaskComment> getTaskCommentByProjectId(int ordinal,
			long projectId);
}
