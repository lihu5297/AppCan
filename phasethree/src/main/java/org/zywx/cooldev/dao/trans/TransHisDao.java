package org.zywx.cooldev.dao.trans;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.trans.TransHis;

public interface TransHisDao extends PagingAndSortingRepository<TransHis, Serializable>{
	
}
