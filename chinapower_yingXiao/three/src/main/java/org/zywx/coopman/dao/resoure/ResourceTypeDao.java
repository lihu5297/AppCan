package org.zywx.coopman.dao.resoure;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.resource.ResourceType;

public interface ResourceTypeDao extends PagingAndSortingRepository<ResourceType, Long> {

	public Page<ResourceType> findByDel(DELTYPE del, Pageable page);
	
	public List<ResourceType> findByTypeNameAndDel(String typeName,DELTYPE del);
	
	public List<ResourceType> findByIdIn(List<Long> ids);
	public List<ResourceType> findByDel(DELTYPE del);
}
