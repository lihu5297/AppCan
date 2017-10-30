package org.zywx.cooldev.dao.document;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.document.Document;

public interface DocumentDao extends PagingAndSortingRepository<Document, Serializable>{

	Document findByIdAndDel(Long docId, DELTYPE normal);

	Page<Document> findByNameLikeOrDescribLikeAndDelOrderByCreatedAtDesc(String query,String query1, DELTYPE normal,  Pageable pageable);

	List<Document> findByNameLikeOrDescribLikeAndDelOrderByCreatedAtDesc(String query, String query2, DELTYPE normal);

	List<Document> findByProjectIdAndDel(long projectId, DELTYPE normal);

	List<Document> findByDel(DELTYPE normal);

//	@Modifying
//	@Query(value="UPDATE DOCUMENT d SET d.pub = ?1 WHERE d.del = ?2 and d.userId = ?3 AND d.docId = ?4")
//	int findByDelAndUserIdAndId(int opertion,int del ,Long loginUserId,Long docId);

}
