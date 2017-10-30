package org.zywx.cooldev.dao.resoure;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.resource.ResourceContent;

public interface ResourceContentDao extends PagingAndSortingRepository<ResourceContent, Long> {

public Page<ResourceContent> findByDel(DELTYPE del,Pageable page);
	
	public List<ResourceContent> findByResTypeAndDel(long typeId,DELTYPE del);
	
	public List<ResourceContent> findByIdInAndDel(List<Long> ids,DELTYPE del);

	public List<ResourceContent> findByResNameLikeOrResDescLikeAndDel(String value1,String value2, DELTYPE normal);
}
