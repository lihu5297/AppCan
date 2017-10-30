package org.zywx.cooldev.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.cooldev.commons.Enums;


/**
 * 
    * @ClassName: Team
    * @Description: 团队实体
    * @author wjj
    * @date 2015年8月8日 下午2:55:53
    *
 */
@Entity
@Table(name = "T_TEAM")
public class Team extends BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5807385328304694926L;

	private String name;
	
	private String detail;
	
	@Column(updatable=false)
	private String uuid = UUID.randomUUID().toString();
	
	/**
	 * 0普通团队 1企业团队  
	 */
	@Enumerated(EnumType.ORDINAL)
	private Enums.TEAMTYPE type=Enums.TEAMTYPE.NORMAL;
	
	/**
	 * 绑定企业ID
	 */
	private String enterpriseId;
	
	/**
	 * 绑定企业名称
	 */
	private String enterpriseName;
	
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

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(String enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Enums.TEAMTYPE getType() {
		return type;
	}

	public void setType(Enums.TEAMTYPE type) {
		this.type = type;
	}
	

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String toString() {
		return name;
	}

	
	
	@Transient 
	private int projectSum;//团队下有几个团队项目
	
	@Transient
	private int memberSum;//团队下有多少个成员
	
	@Transient
	private String creator;//团队创建者



	public int getProjectSum() {
		return projectSum;
	}

	public void setProjectSum(int projectSum) {
		this.projectSum = projectSum;
	}

	public int getMemberSum() {
		return memberSum;
	}

	public void setMemberSum(int memberSum) {
		this.memberSum = memberSum;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	

}
