package org.zywx.coopman.dao.process;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.process.TaskConfig;

public interface TaskConfigDao extends PagingAndSortingRepository<TaskConfig, Serializable>{

	@Query("from TaskConfig tc where tc.id <> ?1")
	List<TaskConfig> findById(long taskConfigId);

	@Query("from TaskConfig tc where tc.id in (select tcr.taskConfigId from TaskConfigRelate tcr where tcr.nextTaskId = ?1)")
	List<TaskConfig> findBytaskId(long taskConfigId);

}
