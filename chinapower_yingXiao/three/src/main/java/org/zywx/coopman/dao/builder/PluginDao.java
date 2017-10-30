package org.zywx.coopman.dao.builder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.PluginType;
import org.zywx.coopman.entity.builder.Plugin;

public interface PluginDao extends PagingAndSortingRepository<Plugin, Long> {

	public long countByTypeAndDel(PluginType type, DELTYPE delType);
	
	public Page<Plugin> findByTypeAndCnNameLikeAndDel(Pageable pageable, PluginType type, String search, DELTYPE delType);

	public Page<Plugin> findByTypeAndDel(Pageable pageable, PluginType type, DELTYPE normal);

	public Page<Plugin> findByTypeAndEnNameLikeAndDel(Pageable pageable, PluginType type, String search,DELTYPE normal);
	
	/**
	 * 唯一确定一个插件
	 * @param type
	 * @param enName
	 * @param projectId
	 * @param delType
	 * @return
	 */
	public Plugin findOneByTypeAndEnNameAndProjectIdAndDel(PluginType type, String enName, long projectId, DELTYPE delType);
}
