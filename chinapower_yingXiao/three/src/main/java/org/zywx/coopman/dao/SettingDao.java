package org.zywx.coopman.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.Setting;

public interface SettingDao extends PagingAndSortingRepository<Setting, Serializable>{

	Setting findById(long l);

}
