package org.zywx.coopman.entity.filialeInfo;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;

/**
 * 电网分公司信息
 * @author 东元
 *
 */
@Entity
@Table(name="T_MAN_FILIALE_INFO")
public class FilialeInfo extends BaseEntity {


	private static final long serialVersionUID = 1L;
	String filialeName;	//分公司全程
	String simpleName;	//分公司简称
	String filialeCode;	//分公司代码
	String filialeDesc;	//分公司描述
	public String getFilialeName() {
		return filialeName;
	}
	public void setFilialeName(String filialeName) {
		this.filialeName = filialeName;
	}
	public String getFilialeCode() {
		return filialeCode;
	}
	public void setFilialeCode(String filialeCode) {
		this.filialeCode = filialeCode;
	}
	public String getFilialeDesc() {
		return filialeDesc;
	}
	public void setFilialeDesc(String filialeDesc) {
		this.filialeDesc = filialeDesc;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	
	
}
