package org.zywx.coopman.dao.filialeInfo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.filialeInfo.FilialeInfo;

public interface FilialeInfoDao extends PagingAndSortingRepository <FilialeInfo,Long>{

	public List<FilialeInfo> findByDel(DELTYPE del);

	public FilialeInfo findByFilialeName(String value);
	
	/***
	 * 根据所属单位(分公司名字)模糊查询分公司id列表
	 * @param FilialeName
	 * @return
	 */
	@Query(value="select fi.id from FilialeInfo fi where fi.filialeName like ?1 ")
	public List<Long> findLikeFilialeName(String FilialeName);
}
