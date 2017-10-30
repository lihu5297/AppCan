package org.zywx.cooldev.dao.document;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DOC_CHAPTER_TYPE;
import org.zywx.cooldev.commons.Enums.DOC_PUB_TYPE;
import org.zywx.cooldev.entity.document.DocumentChapter;

public interface DocumentChapterDao extends PagingAndSortingRepository<DocumentChapter, Serializable>{

	List<DocumentChapter> findByParentIdAndDel(Long parentId, DELTYPE normal);
	
	DocumentChapter findByIdAndDel(Long docCId, DELTYPE deleted);

	List<DocumentChapter> findByDocumentIdAndParentIdAndDel(Long documentId , Long parentId , DELTYPE normal);
	
	@Query(value="from DocumentChapter tdc where (tdc.contentMD like ?2 or tdc.name like ?2) and tdc.del = ?3 and tdc.documentId = ?1 and tdc.type=?4 order by tdc.createdAt desc")
	Page<DocumentChapter> findByDocumentIdAndContentMDLikeAndDelOrderByCreatedAtDesc(Long docId, String query,DELTYPE normal, DOC_CHAPTER_TYPE type,Pageable page);

	DocumentChapter findByIdAndTypeAndDel(Long docCId, DOC_CHAPTER_TYPE part, DELTYPE normal);

	@Query(value="select count(*) from DocumentChapter tdc where (tdc.contentMD like ?1 or tdc.name like ?1) and tdc.del = ?3 and tdc.documentId = ?2 and tdc.type=?4")
	int findByContentMDLikeAndDocumentIdAndDel(String query, Long docId, DELTYPE normal,DOC_CHAPTER_TYPE type);

	List<DocumentChapter> findByDocumentIdAndContentMDLikeAndPubAndDelOrderByCreatedAtDesc(Long docId, String query,
			DOC_PUB_TYPE published, DELTYPE normal, Pageable page);

	@Query(value="select count(*) from DocumentChapter tdc where (tdc.contentMD like ?1 or tdc.name like ?1) and tdc.pub=?2 and tdc.type=?5 and tdc.del = ?4 and tdc.documentId = ?3")
	int findByContentMDLikeAndPubAndDocumentIdAndDel(String query, DOC_PUB_TYPE published,Long docId,  DELTYPE normal,DOC_CHAPTER_TYPE type);

	@Query(value="from DocumentChapter tdc where (tdc.contentMD like ?2 or tdc.name like ?2) and tdc.pub=?3 and tdc.type=?5 and tdc.del = ?4 and tdc.documentId = ?1 order by tdc.createdAt desc")
	Page<DocumentChapter> findByDocumentIdAndNameLikeOrContentMDLikeAndPubAndDelOrderByCreatedAtDesc(Long docId,
			String query, DOC_PUB_TYPE published, DELTYPE normal,DOC_CHAPTER_TYPE type, Pageable page);

	List<DocumentChapter> findByDocumentIdAndDel(Long documentId, DELTYPE normal);

	DocumentChapter findByNameAndParentIdAndDocumentIdAndDel(String name, Long parentId, Long documentId, DELTYPE normal);
    @Query(value="from DocumentChapter dc where dc.del=?1 and dc.documentId in (select d.id from Document d where d.del=?1 and d.projectId=?2)")
	List<DocumentChapter> getDocumentChapterByProjectId(DELTYPE normal,
			long projectId);

}
