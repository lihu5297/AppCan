package org.zywx.cooldev.dao.resoure;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.resource.ResourceFileInfo;

public interface ResourceFileInfoDao extends PagingAndSortingRepository<ResourceFileInfo, Long> {

	public List<ResourceFileInfo> findByDel(DELTYPE del);
	
	public List<ResourceFileInfo> findByIdIn(List<Long> ids);

	public List<Long> findIdByOriginalNameLikeAndDel(String value, DELTYPE normal);
	
}
