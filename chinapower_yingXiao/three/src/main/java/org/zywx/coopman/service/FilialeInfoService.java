package org.zywx.coopman.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.entity.filialeInfo.FilialeInfo;

@Service
public class FilialeInfoService extends BaseService{

	public List<FilialeInfo> findFilialeInfoAll(){
		return filialeInfoDao.findByDel(Enums.DELTYPE.NORMAL);
	}
	
	public FilialeInfo findById(Long id){
		return filialeInfoDao.findOne(id);
	}
	
	public Map<Long,String> findFilialeInfoAllMap(){
		List<FilialeInfo>  list = filialeInfoDao.findByDel(Enums.DELTYPE.NORMAL);
		Map<Long,String> map = new HashMap<Long, String>();
		for(FilialeInfo fi : list){
			map.put(fi.getId(), fi.getFilialeName());
		}
		return map;
	}
	
	/***
	 * 根据所属单位(分公司名字)模糊查询分公司id列表
	 * @param FilialeName
	 * @return
	 */
	public List<Long> findLikeFilialeName(String FilialeName){
		return filialeInfoDao.findLikeFilialeName("%"+FilialeName.trim()+"%");
	}
}
