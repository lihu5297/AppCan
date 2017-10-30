package org.zywx.coopman.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.DailyLog.LogAction;

public interface LogActionDao extends PagingAndSortingRepository<LogAction, Serializable>{

	List<LogAction> findByDel(DELTYPE normal);

}
