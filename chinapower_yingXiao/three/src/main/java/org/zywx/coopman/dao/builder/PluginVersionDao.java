package org.zywx.coopman.dao.builder;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.entity.builder.PluginVersion;

public interface PluginVersionDao extends PagingAndSortingRepository<PluginVersion, Long> {
	
	public List<PluginVersion> findByOsTypeInAndDelOrderByVersionNoAsc(List<OSType> osType, DELTYPE delType);

	public List<PluginVersion> findByPluginIdAndOsTypeInAndDelOrderByVersionNoAsc(long pluginId, List<OSType> osType, DELTYPE delType);

	public List<PluginVersion> findByPluginIdAndDel(long pluginId, DELTYPE normal);

}
