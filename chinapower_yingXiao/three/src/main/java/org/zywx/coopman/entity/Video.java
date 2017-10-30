package org.zywx.coopman.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.commons.Enums.VIDEO_STATUS;
import org.zywx.coopman.commons.Enums.VIDEO_TUIJIAN;
import org.zywx.coopman.commons.Enums.VIDEO_TYPE;
/**
 * 
 * @author yongwen.wang
 * @date 2016-10-26
 */
@Entity
@Table(name="T_VIDEO")
public class Video extends BaseEntity{
	private String title;
	
	private String description;
	
	private String downloadUrl;
	
	private Enums.VIDEO_STATUS status=VIDEO_STATUS.NOPUBLISH;
	
	private Enums.VIDEO_TUIJIAN tuijian=VIDEO_TUIJIAN.NOTUIJIAN;
    
	private VIDEO_TYPE type;
	
	private long sort;
	
	
	public VIDEO_TYPE getType() {
		return type;
	}

	public void setType(VIDEO_TYPE type) {
		this.type = type;
	}

	public long getSort() {
		return sort;
	}

	public void setSort(long sort) {
		this.sort = sort;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public Enums.VIDEO_STATUS getStatus() {
		return status;
	}

	public void setStatus(Enums.VIDEO_STATUS status) {
		this.status = status;
	}

	public Enums.VIDEO_TUIJIAN getTuijian() {
		return tuijian;
	}

	public void setTuijian(Enums.VIDEO_TUIJIAN tuijian) {
		this.tuijian = tuijian;
	}
}
