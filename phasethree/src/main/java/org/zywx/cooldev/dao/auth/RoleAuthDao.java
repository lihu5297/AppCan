package org.zywx.cooldev.dao.auth;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.auth.RoleAuth;

public interface RoleAuthDao extends PagingAndSortingRepository<RoleAuth, Long> {
	
	List<RoleAuth> findByDel(DELTYPE delType);
	
	List<RoleAuth> findByRoleIdAndDel(long roleId, DELTYPE delType);
	
	List<RoleAuth> findByRoleIdInAndPremissionIdAndDel(List<Long> roleIds,long premissionId,DELTYPE delType);
}
