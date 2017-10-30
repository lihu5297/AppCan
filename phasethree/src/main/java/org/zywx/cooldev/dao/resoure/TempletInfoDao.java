package org.zywx.cooldev.dao.resoure;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.resource.TempletInfo;

public interface TempletInfoDao extends PagingAndSortingRepository<TempletInfo,Long>{

	public List<TempletInfo> findAll();
}
