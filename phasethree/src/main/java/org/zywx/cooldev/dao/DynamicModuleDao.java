package org.zywx.cooldev.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.entity.DynamicModule;

public interface DynamicModuleDao extends PagingAndSortingRepository<DynamicModule, Serializable>{

	/**
	 * 
	    * @Description: 
	    * @param @param moduleType
	    * @param @return 
	    * @return DynamicModule    返回类型
		* @user jingjian.wu
		* @date 2015年8月17日 上午11:19:07
	    * @throws
	 */
	public DynamicModule findByModuleType(DYNAMIC_MODULE_TYPE moduleType);
}
