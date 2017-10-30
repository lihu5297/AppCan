package org.zywx.cooldev.entity.document;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 
 * @describe 文档标记	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月26日 下午5:43:09	<br>
 *
 */
@Entity
@Table(name="T_DOCUMENT_MARKER")
public class DocumentMarker extends BaseEntity{

	private static final long serialVersionUID = 3764312131452876274L;
	
	private Long userId;
	
	private String userName;
	
	private Long docCId;
	
	private String target;
	
	private String content;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getDocCId() {
		return docCId;
	}

	public void setDocCId(Long docCId) {
		this.docCId = docCId;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return this.content;
	}

}
