package org.zywx.coopman.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年10月21日 上午10:21:08	<br>
 * 
 */
@Entity
@Table(name="T_MAN_DISK_STATISTIC")
public class DiskStatistic extends BaseEntity{

	private static final long serialVersionUID = -6081052820958309531L;

	/**
	 * 服务器名称
	 */
	private String hostName;
	
	/**
	 * 服务器地址
	 */
	private String host;
	
	/**
	 * 已用空间
	 */
	@Column(columnDefinition="longtext")
	private String usedInfo;
	
	/**
	 * 剩余空间
	 */
	@Column(columnDefinition="longtext")
	private String unUsedInfo;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsedInfo() {
		return usedInfo;
	}

	public void setUsedInfo(String usedInfo) {
		this.usedInfo = usedInfo;
	}

	public String getUnUsedInfo() {
		return unUsedInfo;
	}

	public void setUnUsedInfo(String unUsedInfo) {
		this.unUsedInfo = unUsedInfo;
	}

	
	@Override
	public String toString() {
		return "DiskStatistic [hostName=" + hostName + ", host=" + host + ", usedInfo=" + usedInfo + ", unUsedInfo="
				+ unUsedInfo + "]";
	}
	
	
	
}
