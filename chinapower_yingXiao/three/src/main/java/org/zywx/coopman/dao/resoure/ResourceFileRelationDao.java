package org.zywx.coopman.dao.resoure;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.resource.ResourceFileRelation;

public interface ResourceFileRelationDao extends PagingAndSortingRepository<ResourceFileRelation,Long>{

	public List<ResourceFileRelation> findByContentIdAndDel(long contentId,DELTYPE del);
	
	public List<ResourceFileRelation> findByContentIdInAndDel(List<Long> contentIds,DELTYPE del);
	
	@Query("select rf.fileId from ResourceFileRelation rf where contentId = ?1")
	public List<Long> findFileIdByContentId(long contentId);
}
