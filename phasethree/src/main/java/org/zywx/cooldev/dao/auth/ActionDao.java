package org.zywx.cooldev.dao.auth;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.auth.Action;

public interface ActionDao extends PagingAndSortingRepository<Action, Long> {
	
	List<Action> findByDel(Enums.DELTYPE delType);

}
