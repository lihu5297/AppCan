package org.zywx.cooldev.entity.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DOC_CHAPTER_TYPE;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * @describe 文档章节实体类	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月12日 下午5:38:24	<br>
 * 
 */
@Entity
@Table(name="T_DOCUMENT_CHAPTER")
public class DocumentChapter extends BaseEntity{

	
	@Override
	public String toString() {
		return this.name;
	}

	public String toStr(){
		return "DocumentChapter [userId=" + userId + ", documentId=" + documentId + ", name=" + name + ", contentMD="
				+ contentMD + ", contentHTML=" + contentHTML + ", parentId=" + parentId + ", pub=" + pub + ", sort="
				+ sort + ", type=" + type + "]";
	}
	
	private static final long serialVersionUID = -932304324820596508L;

	@Column(name="userId")
	private Long userId;
	
	@Column(name="documentId")
	private Long documentId;
	
	@Column(name="name")
	private String name;
	
	@Column(name="contentMD",columnDefinition="longtext")
	private String contentMD;
	
	@Column(name="contentHTML",columnDefinition="longtext")
	private String contentHTML;
	
	@Column(name="parentId")
	private Long parentId;
	
	//章节是否发布 默认回收状态=未发布状态
	@Column(name="pub",columnDefinition="tinyint")
	private Enums.DOC_PUB_TYPE pub = Enums.DOC_PUB_TYPE.RETRIEVED;
	
	//排序
	@Column(name="sort",columnDefinition="tinyint")
	private int sort;
	
	//章节类型  默认节
	@Column(name="type",columnDefinition="tinyint")
	private Enums.DOC_CHAPTER_TYPE type = Enums.DOC_CHAPTER_TYPE.PART;

	//文档所有父级id
	@Transient
	private String idPath;
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentMD() {
		return contentMD;
	}

	public void setContentMD(String contentMD) {
		this.contentMD = contentMD;
	}

	public String getContentHTML() {
		return contentHTML;
	}

	public void setContentHTML(String contentHTML) {
		this.contentHTML = contentHTML;
	}

	public DOC_CHAPTER_TYPE getType() {
		return type;
	}

	public void setType(DOC_CHAPTER_TYPE type) {
		this.type = type;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public Enums.DOC_PUB_TYPE getPub() {
		return pub;
	}

	public void setPub(Enums.DOC_PUB_TYPE pub) {
		this.pub = pub;
	}

	public String getIdPath() {
		return idPath;
	}

	public void setIdPath(String idPath) {
		this.idPath = idPath;
	}
	
	
}
