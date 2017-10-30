package org.zywx.cooldev.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.SOURCE_TYPE;
import org.zywx.cooldev.entity.Resource;

public interface ResourcesDao extends PagingAndSortingRepository<Resource, Long> {

	Page<Resource> findByNameLikeAndDelOrderByCreatedAtDesc(String query, DELTYPE normal, Pageable pageable);

	List<Resource> findByNameLikeAndDelOrderByCreatedAtDesc(String query, DELTYPE normal);
	

	public Resource findByFilePathAndName(String filePath,String name);
	
	public Resource findByUuidAndIsPublicAndDel(String uuid,int ispublic,DELTYPE normal);
	
	public List<Resource> findByParentIdAndType(Long parentId,String type);
	
	public List<Resource> findByFilePathLike(String filePath);

	public List<Resource> findByIdIn(List<Long> idList);

	List<Resource> findByProjectIdAndDel(long projectId, DELTYPE normal);
	
	public List<Resource> findByUserId(Long userIds);

	List<Resource> findBySourceType(SOURCE_TYPE sourceType);
	
//	List<Resource> findBySourceTypeAndUserId(SOURCE_TYPE sourceType,long userId);
//	
//	List<Resource> findBySourceTypeAndUserIdAndProjectId(SOURCE_TYPE sourceType,long userId,long projectId);
	
	List<Resource> findBySourceTypeAndProjectIdIn(SOURCE_TYPE sourceType,List<Long> projectId);
}
