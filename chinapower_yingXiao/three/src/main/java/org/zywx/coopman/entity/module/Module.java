package org.zywx.coopman.entity.module;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.commons.Enums.MODULE_TYPE;
import org.zywx.coopman.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月14日 下午7:18:39	<br>
 * 
 */
@Entity
@Table(name="T_MAN_MODULE")
public class Module extends BaseEntity{

	private static final long serialVersionUID = 7403867289992567105L;

	private String cnName;
	private String enName;
	private String url;
	private long parentId;
	
	@Transient
	private List<Module> childrenModule;
	
	private MODULE_TYPE type = MODULE_TYPE.NORMAL;
	
	public String getCnName() {
		return cnName;
	}
	public void setCnName(String cnName) {
		this.cnName = cnName;
	}
	public String getEnName() {
		return enName;
	}
	public void setEnName(String enName) {
		this.enName = enName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public MODULE_TYPE getType() {
		return type;
	}
	public void setType(MODULE_TYPE type) {
		this.type = type;
	}
	public List<Module> getChildrenModule() {
		return childrenModule;
	}
	public void setChildrenModule(List<Module> childrenModule) {
		this.childrenModule = childrenModule;
	}
	
	@Override
	public String toString() {
		return "Module [cnName=" + cnName + ", enName=" + enName + ", url=" + url + ", parentId=" + parentId
				+ ", childrenModule=" + childrenModule + ", type=" + type + "]";
	}
	
	
}
