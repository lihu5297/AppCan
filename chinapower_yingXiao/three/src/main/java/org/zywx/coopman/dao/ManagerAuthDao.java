package org.zywx.coopman.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.module.ManagerAuth;

public interface ManagerAuthDao extends PagingAndSortingRepository<ManagerAuth, Serializable>{

	List<ManagerAuth> findByManagerIdAndModuleId(Long ManagerId, Long ModuleId);

	List<ManagerAuth> findByManagerId(Long managerId);

}
