package org.zywx.cooldev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zywx.cooldev.dao.CheckInfoDao;
import org.zywx.cooldev.entity.CheckInfo;

@Service
public class CheckInfoService extends BaseService {

	@Autowired
	private CheckInfoDao checkInfoDao;
	
	public int checkInfoSaveUpdate(CheckInfo ci){
		if(checkInfoDao != null){
			int i = 0;
			try{
				i = checkInfoDao.checkInfoUpdate(ci.getCheckFilePath(), ci.getCheckResult(), ci.getDuration(), ci.getId());
			}catch(Exception e){
				e.printStackTrace();
			}
			return i;
		}else{
			return 0;
		}
	}
	public CheckInfo saveUpdate(CheckInfo ci){
		return checkInfoDao.save(ci);
	}
	
	public CheckInfo findByUniqueId(String sessionId){
		return checkInfoDao.findByUniqueId(sessionId);
	}
}
