package org.zywx.cooldev.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.filialeInfo.FilialeInfo;

@Service
public class FilialeInfoService extends BaseService{

	 
	
	public List<FilialeInfo> findFilialeInfoAll(){
		return filialeInfoDao.findByDel(Enums.DELTYPE.NORMAL);
	}
	
	public Map<Long,String> findFilialeInfoAllMap(){
		List<FilialeInfo>  list = filialeInfoDao.findByDel(Enums.DELTYPE.NORMAL);
		Map<Long,String> map = new HashMap<Long, String>();
		for(FilialeInfo fi : list){
			map.put(fi.getId(), fi.getFilialeName());
		}
		return map;
	}
	
	public FilialeInfo findById(Long id){
		return filialeInfoDao.findOne(id);
	}
}
