package org.zywx.coopman.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.DiskStatistic;

public interface DiskStatisticDao extends PagingAndSortingRepository<DiskStatistic, Serializable>{

	List<DiskStatistic> findByHostName(String string);

	List<DiskStatistic> findByHostNameAndHost(String string, String string2);

}
