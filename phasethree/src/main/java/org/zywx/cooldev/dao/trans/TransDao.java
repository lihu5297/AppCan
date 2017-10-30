package org.zywx.cooldev.dao.trans;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TRANS_TYPE;
import org.zywx.cooldev.entity.trans.Trans;

public interface TransDao extends PagingAndSortingRepository<Trans, Serializable>{

	public List<Trans> findByDel(DELTYPE delType);
	
	public List<Trans> findByTransactionsIdAndDel(long transactionsId, DELTYPE delType);
	
	public List<Trans> findByUserIdAndDel(long userId,DELTYPE normal);
	/**
	 * 通过申请编号和创建人获取申请信息
	 * @param userId
	 * @param applyNum
	 * @return
	 */
	public List<Trans> findByUserIdAndApplyNum(long userId,String applyNum);
	/**
	 * 通过申请编号获取申请信息
	 * @param applyNum
	 * @return
	 */
	public Trans findByApplyNum(String applyNum);

	public Trans findByTransactionsIdAndTranTypeIn(Long id, List<String> tranType);

	public List<Trans> findByTransactionsIdAndTranType(Long id, String tranType);
	
}
