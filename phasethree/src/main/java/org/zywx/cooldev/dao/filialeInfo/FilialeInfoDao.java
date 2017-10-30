package org.zywx.cooldev.dao.filialeInfo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.filialeInfo.FilialeInfo;

public interface FilialeInfoDao extends PagingAndSortingRepository <FilialeInfo,Long>{

	public List<FilialeInfo> findByDel(DELTYPE del);
}
