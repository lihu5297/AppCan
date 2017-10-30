package org.zywx.cooldev.dao;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.Dynamic;
import org.zywx.cooldev.entity.DynamicDependency;

public interface DynamicDependencyDao extends PagingAndSortingRepository<DynamicDependency, Serializable>{
	@Query("select d from Dynamic d where id in (select dynamicId from DynamicDependency where entityType=?1 and entityId=?2 and del=?3)")
	Page<Dynamic> findDynamic(String string,Long bugId, DELTYPE normal,Pageable pageable);

}
