package org.zywx.cooldev.dao.datamodel;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.datamodel.DataModel;

public interface DataModelDao extends PagingAndSortingRepository<DataModel, Serializable>{

	public List<DataModel> findByDel(DELTYPE delType);
	
	public List<DataModel> findByUserIdAndDel(long userId,DELTYPE normal);
	
	public DataModel findByIdAndDel(long id,DELTYPE normal);
	
}
