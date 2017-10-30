package org.zywx.cooldev.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.SEQ;

public interface SEQDao extends PagingAndSortingRepository<SEQ, Serializable>{

	public List<SEQ> findByDel(DELTYPE delType);
	
	/**
	 * 利用大项目编号查询子项目申请编号
	 * @param apptype
	 * @param pjProjectCode
	 * @return
	 */
	public SEQ findByTypeAndPjProjectCode(String apptype,String pjProjectCode);
	/**
	 * 利用子项目编号和分类查询应用编号
	 * @param apptype
	 * @param projectCode
	 * @return
	 */
	public SEQ findByTypeAndProjectCode(String apptype,String projectCode);
	/**
	 * 利用类型查询申请编号（只针对申请审核编号）
	 * @param apptype
	 * @param pjProjectCode
	 * @return
	 */
	public SEQ findByType(String apptype);
	
}
