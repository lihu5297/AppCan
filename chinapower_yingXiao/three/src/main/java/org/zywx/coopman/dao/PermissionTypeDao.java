package org.zywx.coopman.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.auth.Permission;
import org.zywx.coopman.entity.auth.PermissionType;

public interface PermissionTypeDao extends PagingAndSortingRepository<PermissionType, Long> {
	
	public List<PermissionType> findByDel(DELTYPE del);

	@Query(value="from PermissionType where id in (select permissionTypeId from PermissionTypeAuth where roleId =?1 and del = ?2) and del =?2")
	public List<PermissionType> findByRoleIdAndDel(long roleId, DELTYPE normal);

	@Query(value="from Permission where id in (select permissionId from PermissionTypeAuth where roleId =?1 and permissionTypeId=?2 and  del = ?3) and del =?3")
	public List<Permission> findByPermissionTypeIdAndRoleIdAndDel(long roleId, Long id, DELTYPE normal);
}
