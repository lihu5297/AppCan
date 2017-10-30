package org.zywx.coopman.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.auth.RoleAuth;

public interface RoleAuthDao extends PagingAndSortingRepository<RoleAuth, Long> {
	
	public List<RoleAuth> findByRoleId(long roleId);
}
