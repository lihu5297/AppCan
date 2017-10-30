package org.zywx.cooldev.dao;



import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.CheckInfo;

public interface CheckInfoDao extends PagingAndSortingRepository<CheckInfo, Long>{

	public CheckInfo findByUniqueId(String uniqueId);
	
	@Modifying
	@Query(value="UPDATE CheckInfo c SET c.checkFilePath = ?1, c.checkResult=?2, c.duration=?3 WHERE c.id=?4")
	int checkInfoUpdate(String checkFilePath, String checkResult ,String duration,Long id);

	public List<CheckInfo> findCheckResultAndCheckFilePathByVersionIdOrderByCreatedAtDesc(
			Long id);
}
