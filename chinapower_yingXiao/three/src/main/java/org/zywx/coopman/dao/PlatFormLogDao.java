package org.zywx.coopman.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.DailyLog.PlatFormLog;

public interface PlatFormLogDao extends PagingAndSortingRepository<PlatFormLog, Serializable> {

}
