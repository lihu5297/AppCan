package org.zywx.cooldev.dao.tInterface;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.tInterface.TInterFace;

public interface TInterFaceDao extends PagingAndSortingRepository<TInterFace, Serializable>{

	public List<TInterFace> findByDel(DELTYPE delType);
	
	public List<TInterFace> findByUserIdAndDel(long userId,DELTYPE normal);
	
	public TInterFace findByIdAndDel(long id,DELTYPE normal);
	
}
