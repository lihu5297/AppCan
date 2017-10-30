package org.zywx.coopman.dao.builder;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.EngineType;
import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.entity.builder.Engine;
import org.zywx.coopman.entity.builder.Plugin;

/**
 * 引擎DAO
 * @author yang.li
 * @date 2015-09-12
 *
 */
public interface EngineDao extends PagingAndSortingRepository<Engine, Long> {

	/**
	 * 获取指定类型及操作系统的引擎列表（引擎不考虑删除标记 -> 物理删除）
	 * @param osType
	 * @return
	 */
	public List<Engine> findByTypeAndOsType(EngineType engineType, OSType osType);

	public List<Engine> findByType(EngineType engineType);

	public List<Engine> findByVersionNoAndOsType(String versionNo, OSType osType);
	
	public List<Engine> findByVersionNoAndOsTypeAndTypeAndDelAndKernel(String versionNo, OSType osType, EngineType type,
			DELTYPE normal,String kernel);

	public Page<Engine> findByType(EngineType type, Pageable pageable);
	
}
