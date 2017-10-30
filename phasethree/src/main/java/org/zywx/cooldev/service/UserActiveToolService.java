package org.zywx.cooldev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.dao.UserActiveToolDao;
import org.zywx.cooldev.entity.UserActiveTool;

@Service
public class UserActiveToolService{
	
	@Autowired
	private UserActiveToolDao userActiveToolDao;

	public UserActiveTool findValueById(String id) {
		UserActiveTool a = this.userActiveToolDao.findOne(id);
		return a;
	}
	
	public UserActiveTool save(UserActiveTool userActiveTool){
		this.userActiveToolDao.save(userActiveTool);
		return userActiveTool;
	}

	
}
