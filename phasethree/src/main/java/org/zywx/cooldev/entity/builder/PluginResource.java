package org.zywx.cooldev.entity.builder;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 自定义插件资源包
 * @author yang.li
 * @date 2015-10-17
 *
 */
@Entity
@Table(name="T_PLUGIN_RESOURCE")
public class PluginResource extends BaseEntity {

	private static final long serialVersionUID = 2056328002817207193L;

	//***************************************************
	//    PluginResource fieds                                  *
	//***************************************************
	private long pluginVersionId;	// 插件版本
	private long userId;			// 用户编号
	private String downloadUrl;		// 网络下载地址
	private String absFilePath;		// 服务器存储的绝对路径
	
	private String filePath;//IDC存储路径
	
	/**
	 * 上传处理状态（Success也包括git提交完毕）
	 */
	private UploadStatus uploadStatus;
	

	public UploadStatus getUploadStatus() {
		return uploadStatus;
	}
	public void setUploadStatus(UploadStatus uploadStatus) {
		this.uploadStatus = uploadStatus;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public long getPluginVersionId() {
		return pluginVersionId;
	}
	public void setPluginVersionId(long pluginVersionId) {
		this.pluginVersionId = pluginVersionId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getAbsFilePath() {
		return absFilePath;
	}
	public void setAbsFilePath(String absFilePath) {
		this.absFilePath = absFilePath;
	}
	@Override
	public String toString() {
		return "PluginResource [pluginVersionId=" + pluginVersionId
				+ ", userId=" + userId + ", downloadUrl=" + downloadUrl
				+ ", absFilePath=" + absFilePath + "]";
	}


}
