package org.zywx.cooldev.entity.document;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DOC_PUB_TYPE;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * @describe 文档实体类	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月12日 下午4:17:41	<br>
 * 
 */
@Entity
@Table(name="T_DOCUMENT")
public class Document extends BaseEntity{


	private static final long serialVersionUID = 6603404473878697391L;

	@Column(name="name")
	private String name;
	
	@Column(name="describ",columnDefinition="text")
	private String describ;
	
	@Column(name="projectId")
	private Long projectId;
	
	@Column(name="userId",updatable=false)
	private Long userId;
	
	@Column(name="pub",columnDefinition="tinyint")
	private Enums.DOC_PUB_TYPE pub = Enums.DOC_PUB_TYPE.RETRIEVED;
	
	private String pubUrl;
	
	private String pinYinHeadChar;
	
	private String pinYinName;
	
	public String getPinYinHeadChar() {
		return pinYinHeadChar;
	}
	public void setPinYinHeadChar(String pinYinHeadChar) {
		this.pinYinHeadChar = pinYinHeadChar;
	}
	public String getPinYinName() {
		return pinYinName;
	}
	public void setPinYinName(String pinYinName) {
		this.pinYinName = pinYinName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescrib() {
		return describ;
	}
	public void setDescrib(String describ) {
		this.describ = describ;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public DOC_PUB_TYPE getPub() {
		return pub;
	}
	public void setPub(DOC_PUB_TYPE pub) {
		this.pub = pub;
	}
	public String getPubUrl() {
		return pubUrl;
	}
	public void setPubUrl(String pubUrl) {
		this.pubUrl = pubUrl;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	
}
