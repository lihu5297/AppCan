package org.zywx.coopman.entity.resource;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.entity.BaseEntity;
/**
 * 模板管理
 * @author 东元
 *
 */
@Entity
@Table(name = "T_MAN_TEMPLET_INFO")
public class TempletInfo  extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String temName;		//模板名称
	long creator;		//创建者
	String filePath;	//模板附件路径
	@Transient
	String creatorName;	//
	@Transient
	String fileUrl;
	public String getTemName() {
		return temName;
	}
	public void setTemName(String temName) {
		this.temName = temName;
	}
	public long getCreator() {
		return creator;
	}
	public void setCreator(long creator) {
		this.creator = creator;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getCreatorName() {
		return creatorName;
	}
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}
	public String getFileUrl() {
		return fileUrl;
	}
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	
	
}
