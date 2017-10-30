package org.zywx.coopman.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.module.Module;

public interface ModuleDao extends PagingAndSortingRepository<Module, Serializable>{

	@Query("from Module m where m.id in (select ma.moduleId from ManagerAuth ma where ma.managerId = ?1 ) and m.del=?3 and m.parentId <>?2")
	List<Module> findModuleByManagerId(Long id,long parentId, DELTYPE deltype);
	
	@Query("from Module m where m.id in (select ma.moduleId from ManagerAuth ma where ma.managerId = ?1 ) and m.del=?2 ")
	List<Module> findModuleByManagerId(Long id, DELTYPE deltype);
	
	@Query("from Module m where m.parentId <>?1 and m.del=?2 ")
	List<Module> findByParentId(long parentId, DELTYPE deltype);

}
