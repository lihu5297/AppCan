package org.zywx.cooldev.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.builder.PluginCategory;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月16日 上午10:20:56	<br>
 * 
 */
@Service
public class PluginCategoryService extends BaseService{

	public List<PluginCategory> getCategory() {
		return this.pluginCategoryDao.findByDel(DELTYPE.NORMAL);
	}

}
