package org.zywx.cooldev.dao.resoure;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.resource.ResourceFileRelation;

public interface ResourceFileRelationDao extends PagingAndSortingRepository<ResourceFileRelation,Long>{

	public List<ResourceFileRelation> findByContentIdAndDel(long contentId,DELTYPE del);
	
	public List<ResourceFileRelation> findByContentIdInAndDel(List<Long> contentIds,DELTYPE del);
	
	@Query("select rf.fileId from ResourceFileRelation rf where contentId = ?1")
	public List<Long> findFileIdByContentId(long contentId);
	
	public static String hql = "SELECT rf.contentId FROM T_MAN_RESOURCE_FILE_REL rf "
	+"LEFT JOIN T_MAN_RESOURCE_FILE f ON f.id = rf.fileId WHERE f.originalName LIKE ?1 AND f.del=0";
	@Query(value=hql,nativeQuery=true)
	public List<BigInteger> findContentIdByValueLike(String value);
}
