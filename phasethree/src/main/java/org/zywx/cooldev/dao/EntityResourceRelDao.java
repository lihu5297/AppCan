package org.zywx.cooldev.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.entity.EntityResourceRel;

public interface EntityResourceRelDao extends PagingAndSortingRepository<EntityResourceRel, Serializable>{
	
	List<EntityResourceRel> findByEntityIdAndEntityTypeAndDel(long entityId, ENTITY_TYPE entityType, DELTYPE delType);
	
	public void removeByEntityIdAndEntityTypeAndResourceIdAndDel(long entityId, ENTITY_TYPE entityType, long resourceId, DELTYPE delType);

	long countByEntityIdAndEntityTypeAndDel(Long id, ENTITY_TYPE process, DELTYPE normal);

	EntityResourceRel findByResourceIdAndDel(Long resourceId, DELTYPE normal);

}
