package org.zywx.coopman.dao;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.GitOperationLog;

public interface GitOperationLogDao extends PagingAndSortingRepository<GitOperationLog, Serializable>{

	Page<GitOperationLog> findByAccountLike(String string, Pageable page);

	@Query("from GitOperationLog gol where gol.account like ?1 and gol.createdAt > ?2 and gol.createdAt < ?3")
	Page<GitOperationLog> findByAccountLike(String string, Timestamp startTime, Timestamp endTime, Pageable page);
	
	List<GitOperationLog> findByAccountLike(String string);
	
	@Query("from GitOperationLog gol where gol.account like ?1 and gol.createdAt > ?2 and gol.createdAt < ?3")
	List<GitOperationLog> findByAccountLike(String string, Timestamp startTime, Timestamp endTime);

}
