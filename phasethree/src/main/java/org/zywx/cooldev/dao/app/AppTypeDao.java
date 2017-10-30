package org.zywx.cooldev.dao.app;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.app.AppType;

public interface AppTypeDao extends PagingAndSortingRepository<AppType, Long>{

	public List<AppType> findByDel(DELTYPE delType);
	
	public List<AppType> findByTypeName(String typeName);
}
