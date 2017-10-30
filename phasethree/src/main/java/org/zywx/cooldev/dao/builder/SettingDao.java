package org.zywx.cooldev.dao.builder;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.builder.Setting;

public interface SettingDao extends PagingAndSortingRepository<Setting, Serializable>{

}
