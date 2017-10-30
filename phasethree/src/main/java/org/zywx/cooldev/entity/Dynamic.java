package org.zywx.cooldev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_TYPE;

/**
 * @describe 动态实体类 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月14日 下午1:35:19	<br>
 * 
 */
@Entity
@Table(name="T_DYNAMIC")
public class Dynamic extends BaseEntity{

	private static final long serialVersionUID = -5480769725603178129L;
	
	@Column(name="userId")
	private Long userId;
	
	//模板类型
	@Enumerated(EnumType.STRING)
	@Column(name="moduleType")
	private DYNAMIC_MODULE_TYPE moduleType;
	
	@Lob
	@Column(name="info",columnDefinition="TEXT",length = 65535)
	private String info;
	
	/**动态类型 分为项目相关动态,团队相关动态*/
	@Column(name="type")
	private DYNAMIC_TYPE type = DYNAMIC_TYPE.PROJECT;
	
	/**
	 * 项目ID/团队ID
	 * 如果是项目相关动态:项目ID
	 * 如果是团队相关动态:团队ID
	 */
	@Column(name="relationId")
	private long relationId;
    @Transient
    private String userIcon;
    
	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	

	public DYNAMIC_MODULE_TYPE getModuleType() {
		return moduleType;
	}

	public void setModuleType(DYNAMIC_MODULE_TYPE moduleType) {
		this.moduleType = moduleType;
	}

	public DYNAMIC_TYPE getType() {
		return type;
	}

	public void setType(DYNAMIC_TYPE type) {
		this.type = type;
	}

	public long getRelationId() {
		return relationId;
	}

	public void setRelationId(long relationId) {
		this.relationId = relationId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
}
