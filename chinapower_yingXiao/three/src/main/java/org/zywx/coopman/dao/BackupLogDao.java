package org.zywx.coopman.dao;

import java.io.Serializable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.BackupLog;

public interface BackupLogDao extends PagingAndSortingRepository<BackupLog, Serializable>{

	@Query(nativeQuery=true,value="select b.* from T_MAN_BACKUP_LOG b order by createdAt asc limit 1")
	BackupLog findOneLast();

}
