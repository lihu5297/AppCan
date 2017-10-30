package org.zywx.cooldev.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.Dynamic;

public interface DynamicDao extends PagingAndSortingRepository<Dynamic, Serializable>{
    @Query(nativeQuery=true,value="select * from T_DYNAMIC where del=?1 and type=0 and relationId=?2")
	List<Dynamic> getDynamicByProjectId(int ordinal, long projectId);

}
