package org.zywx.coopman.entity.DailyLog;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.coopman.entity.BaseEntity;

@Entity
@Table(name="T_MAN_PLATFORM_LOG")
public class PlatFormLog extends BaseEntity{

	/**
	 * @describe 平台日志	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月10日 下午10:09:07	<br>
	 * 
	 */
	private static final long serialVersionUID = 4008249412951439589L;
	
	private Timestamp logDate;
	
	private String filename;
	
	private String content;
	
	private String hostName;

	public Timestamp getLogDate() {
		return logDate;
	}

	public void setLogDate(Timestamp logDate) {
		this.logDate = logDate;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

}
