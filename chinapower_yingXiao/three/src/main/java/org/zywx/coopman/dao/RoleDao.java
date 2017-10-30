package org.zywx.coopman.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.auth.Role;

public interface RoleDao extends PagingAndSortingRepository<Role, Long> {
	
	List<Role> findByDel(Enums.DELTYPE delType);
	
	List<Role> findByIdIn(List<Long> roleIdList);
	
	Role findByEnNameAndDel(String enName,DELTYPE delType);

	Role findByIdAndDel(Long roleId, DELTYPE normal);

}
