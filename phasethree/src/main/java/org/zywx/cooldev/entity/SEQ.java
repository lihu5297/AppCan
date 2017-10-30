package org.zywx.cooldev.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 流水号表 1申请编号,2项目编号，3子项目编号，4应用编号
 * @author zhouxx
 * 20170808
 *
 */
@Entity
@Table(name="T_SEQ")
public class SEQ extends BaseEntity {

	private static final long serialVersionUID = 1148934999815049435L;

	/**
	 * 类型，1申请编号,2项目编号，3子项目编号，4应用编号
	 */
	private String type;
	
	/**
	 * 当前流水编号
	 */
	private long nowCode;
	
	/**
	 * 大项目编号
	 */
	private String pjProjectCode ;
	/**
	 * 子项目编号
	 */
	private String projectCode ;
	
	public String getPjProjectCode() {
		return pjProjectCode;
	}

	public void setPjProjectCode(String pjProjectCode) {
		this.pjProjectCode = pjProjectCode;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	/**
	 * 当前时间
	 */
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp nowDate;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getNowCode() {
		return nowCode;
	}

	public void setNowCode(long nowCode) {
		this.nowCode = nowCode;
	}

	public Timestamp getNowDate() {
		return nowDate;
	}

	public void setNowDate(Timestamp nowDate) {
		this.nowDate = nowDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	

}
