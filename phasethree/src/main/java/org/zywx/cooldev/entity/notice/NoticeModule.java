package org.zywx.cooldev.entity.notice;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.entity.BaseEntity;

/**
 * 
 * @describe 通知模板实体	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年8月18日 下午3:28:16	<br>
 *
 */
@Entity
@Table(name="T_NOTICE_MODULE")
public class NoticeModule extends BaseEntity{
	
	private static final long serialVersionUID = -4523990722780315148L;

	//格式化字符串
	@Column(name="noFormatStr")
	private String noFormatStr;
	
	//模板类型
	@Enumerated(EnumType.STRING)
	@Column(name="noModuleType")
	private NOTICE_MODULE_TYPE noModuleType;

	public String getNoFormatStr() {
		return noFormatStr;
	}

	public void setNoFormatStr(String noFormatStr) {
		this.noFormatStr = noFormatStr;
	}

	public NOTICE_MODULE_TYPE getNoModuleType() {
		return noModuleType;
	}

	public void setNoModuleType(NOTICE_MODULE_TYPE noModuleType) {
		this.noModuleType = noModuleType;
	}


	
	
}
