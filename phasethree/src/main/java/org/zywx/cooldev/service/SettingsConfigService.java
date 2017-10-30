package org.zywx.cooldev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.dao.SettingsConfigDao;
import org.zywx.cooldev.entity.SettingsConfig;

@Service
public class SettingsConfigService extends BaseService{
	
	@Autowired
	private SettingsConfigDao settingsConfigDao;

	public String findValueById(String code, String type) {
		SettingsConfig a = this.settingsConfigDao.findByCodeAndType(code,type);
		return a.getValue();
	}

	
}
