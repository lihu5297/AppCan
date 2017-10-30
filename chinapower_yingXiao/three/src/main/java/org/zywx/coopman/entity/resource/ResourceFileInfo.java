package org.zywx.coopman.entity.resource;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.entity.BaseEntity;

/**
 * 资源内容附件表
 * @author 东元
 *
 */
@Entity
@Table(name = "T_MAN_RESOURCE_FILE")
public class ResourceFileInfo extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String fileName;	//文件名称
	
	private String originalName;	//文件原名称
	
	private String filePath;	//文件路径（相对路径）
	
	private long fileSize;		//文件大小
	@Transient
	private String fileUrl;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	
	
}
