package org.zywx.coopman.entity.resource;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;

/**
 * 内容与附件的关系
 * @author 东元
 *
 */
@Entity
@Table(name = "T_MAN_RESOURCE_FILE_REL")
public class ResourceFileRelation extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long contentId;			//资源内容ID
	private long fileId;				//内容附件ID
	
	public long getContentId() {
		return contentId;
	}
	public void setContentId(long contentId) {
		this.contentId = contentId;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
}
