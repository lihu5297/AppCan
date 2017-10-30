package org.zywx.cooldev.dao.builder;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.builder.PluginResource;

public interface PluginResourceDao extends PagingAndSortingRepository<PluginResource, Serializable>{
	
	public PluginResource findOneByPluginVersionIdAndUserId(long pluginVersionId, long userId);

}
