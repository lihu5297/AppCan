package org.zywx.cooldev.dao.auth;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.auth.Permission;

public interface PermissionDao extends PagingAndSortingRepository<Permission, Long> {
	
	List<Permission> findByDel(DELTYPE delType);
	
	//根据角色id查找对应的权限集合
	@Query(value="select p from Permission p where id in (select premissionId from RoleAuth where roleId =?1 and del=?2) and del=?2")
	List<Permission> findByRoleIdAndDelType(Long roleId,DELTYPE del);

	Permission findByEnNameAndDel(String enName, DELTYPE normal);

	List<Permission> findByIdInAndDel(List<Long> ids, DELTYPE normal);
	
	List<Permission> findByIdInAndTypeIdAndDel(List<Long> ids, Long typeId,  DELTYPE normal);
}
