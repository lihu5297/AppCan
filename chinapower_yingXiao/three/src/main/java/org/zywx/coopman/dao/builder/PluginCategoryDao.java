package org.zywx.coopman.dao.builder;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.PLUGIN_CATEGORY_STATUS;
import org.zywx.coopman.entity.builder.PluginCategory;

public interface PluginCategoryDao extends PagingAndSortingRepository<PluginCategory, Serializable>{

	List<PluginCategory> findByDel(DELTYPE normal);

	@Query("select pc from PluginCategory pc where pc.name like ?1 and pc.del=?2 ")
	List<PluginCategory> findByNameLikeAndDel(String search, DELTYPE normal);

	List<PluginCategory> findByDelAndStatus(DELTYPE normal,
			PLUGIN_CATEGORY_STATUS enable);

}
