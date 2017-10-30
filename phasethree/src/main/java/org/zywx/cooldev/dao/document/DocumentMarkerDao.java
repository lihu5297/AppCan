package org.zywx.cooldev.dao.document;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.document.DocumentMarker;

public interface DocumentMarkerDao extends PagingAndSortingRepository<DocumentMarker, Serializable>{

	List<DocumentMarker> findByDocCIdAndUserIdAndTargetAndDelOrderByCreatedAtDesc(Long docCId, Long loginUserId,
			String target, DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_DOCUMENT_MARKER where del=?1 and docCId in (select id from T_DOCUMENT_CHAPTER where del=?1 and documentId in (select id from T_DOCUMENT where del=?1 and projectId=?2))")
	List<DocumentMarker> getDocuementMarkerByProjectId(int ordinal,
			long projectId);


}
