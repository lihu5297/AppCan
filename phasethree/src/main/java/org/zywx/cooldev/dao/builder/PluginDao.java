package org.zywx.cooldev.dao.builder;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.PluginType;
import org.zywx.cooldev.entity.builder.Plugin;

public interface PluginDao extends PagingAndSortingRepository<Plugin, Long> {
	
	public long countByDel(DELTYPE delType);

	public long countByProjectIdAndDel(long projectId, DELTYPE delType);
	
	public Page<Plugin> findByTypeAndDel(Pageable pageable, PluginType type, DELTYPE delType);
	
	public Page<Plugin> findByTypeAndProjectIdAndDel(Pageable pageable, PluginType type, long projectId, DELTYPE delType);

	public List<Plugin> findByProjectIdAndDel(long projectId, DELTYPE normal);

	@Query(value="select p from Plugin p where (p.enName like ?1 or p.cnName like ?1) and projectId=?2 and type=?3 and del = 0")
	public List<Plugin> searchPlugin(String keyword, long projectId, PluginType type);
	
	@Query(value="select p from Plugin p where (p.enName like ?1 or p.cnName like ?1) and projectId=?2 and type=?3 and del = 0")
	public Page<Plugin> searchPlugin(String keyword, long projectId, PluginType type,Pageable page);

	public List<Plugin> findByEnNameAndProjectIdAndTypeAndDel(String enName, long projectId, PluginType type,DELTYPE del);
}
