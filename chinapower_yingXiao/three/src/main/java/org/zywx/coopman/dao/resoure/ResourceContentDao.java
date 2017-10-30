package org.zywx.coopman.dao.resoure;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.resource.ResourceContent;

public interface ResourceContentDao extends PagingAndSortingRepository<ResourceContent, Long> {

	public Page<ResourceContent> findByDel(DELTYPE del,Pageable page);
	
	public List<ResourceContent> findByResTypeAndDel(long typeId,DELTYPE del);
	
	public List<ResourceContent> findByIdIn(List<Long> ids);
}
