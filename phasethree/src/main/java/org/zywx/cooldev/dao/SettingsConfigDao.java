package org.zywx.cooldev.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.SettingsConfig;

public interface SettingsConfigDao extends PagingAndSortingRepository<SettingsConfig, Serializable>{

	SettingsConfig findByCodeAndType(String code, String type);

}
