package org.zywx.cooldev.dao.builder;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.builder.PluginCategory;

public interface PluginCategoryDao extends PagingAndSortingRepository<PluginCategory, Serializable>{

	List<PluginCategory> findByDel(DELTYPE normal);

}
