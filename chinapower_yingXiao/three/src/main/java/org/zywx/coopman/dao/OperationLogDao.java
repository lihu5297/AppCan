package org.zywx.coopman.dao;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.DailyLog.OperationLog;

public interface OperationLogDao extends PagingAndSortingRepository<OperationLog, Serializable>{

	@Query("from OperationLog ol where ol.createdAt >= ?1 and ol.createdAt <= ?2 and operationLog like ?3 ")
	Page<OperationLog> findByCreatedAtAndOperationLog(Timestamp startTime, Timestamp endTime, String queryKey,Pageable page);

	Page<OperationLog> findByOperationLogLike(String queryKey, Pageable page);

	@Query("from OperationLog ol where ol.createdAt >= ?1 and ol.createdAt <= ?2 and operationLog like ?3 ")
	List<OperationLog> findByCreatedAtAndOperationLog(Timestamp startTime, Timestamp endTime, String queryKey);

	List<OperationLog> findByOperationLogLike(String queryKey);

}
