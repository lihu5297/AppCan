package org.zywx.cooldev.entity.notice;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

@Entity
@Table(name="T_NOTICE_DEPENDENCY")
public class NoticeDependency extends BaseEntity{

	/**
	 * @describe 	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月2日 下午5:25:22	<br>
	 * 
	 */
	private static final long serialVersionUID = 4048700491920248987L;

	private Long noticeId;
	
	private Long entityId;
	
	private String entityType;

	public Long getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Long noticeId) {
		this.noticeId = noticeId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	
}
