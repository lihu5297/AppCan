package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.task.TaskGroupSort;

public interface TaskGroupSortDao extends PagingAndSortingRepository<TaskGroupSort, Long> {

	List<TaskGroupSort> findByProjectIdAndUserIdOrderBySortAsc(Long projectId,Long userId);
	@Query(nativeQuery=true,value="select * from T_TASK_GROUP_SORT where del=?1 and projectId=?2")
	List<TaskGroupSort> getTaskGroupSortByProjectId(int ordinal, long projectId);

}
