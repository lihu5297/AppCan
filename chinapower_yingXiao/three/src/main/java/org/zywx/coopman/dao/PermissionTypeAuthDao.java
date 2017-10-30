package org.zywx.coopman.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.auth.PermissionTypeAuth;

public interface PermissionTypeAuthDao extends PagingAndSortingRepository<PermissionTypeAuth, Serializable>{

	
}
