package org.zywx.coopman.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.auth.Permission;

public interface PermissionDao extends PagingAndSortingRepository<Permission, Long> {
	
	List<Permission> findByDel(DELTYPE delType);
	
	//根据角色id查找对应的权限集合
	@Query(value="select p from Permission p where id in (select premissionId from RoleAuth where roleId =?1 and del=?2) and del=?2")
	List<Permission> findByRoleIdAndDelType(Long roleId,DELTYPE del);
	
	List<Permission> findByTypeIdAndDel(long typeId,DELTYPE del);
	
	Permission findByEnNameAndDel(String enName,DELTYPE delType);

	@Query(value="from Permission where id in (select permissionId from PermissionTypeAuth where roleId =?1 and permissionTypeId = ?2 and del =?3 ) and del =?3")
	List<Permission> findByRoleIdAndTypeIdAndDel(long roleId, Long id, DELTYPE normal);
	

}
