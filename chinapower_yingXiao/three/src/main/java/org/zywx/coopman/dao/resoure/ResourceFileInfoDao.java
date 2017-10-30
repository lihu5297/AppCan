package org.zywx.coopman.dao.resoure;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.resource.ResourceFileInfo;

public interface ResourceFileInfoDao extends PagingAndSortingRepository<ResourceFileInfo, Long> {

	public List<ResourceFileInfo> findByDel(DELTYPE del);
	
	public List<ResourceFileInfo> findByIdIn(List<Long> ids);
	
}
