package org.zywx.cooldev.dao.auth;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.auth.Role;

public interface RoleDao extends PagingAndSortingRepository<Role, Long> {
	
	List<Role> findByDel(Enums.DELTYPE delType);
	
	List<Role> findByIdIn(List<Long> roleIdList);
	
	Role findByEnNameAndDel(String enName,DELTYPE delType);

}
