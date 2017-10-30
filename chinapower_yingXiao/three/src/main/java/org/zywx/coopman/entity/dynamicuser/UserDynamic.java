package org.zywx.coopman.entity.dynamicuser;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name="C_USER_DYNAMIC")
public class UserDynamic {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name="userid")
	private long userid;
	
	@Column(name="account")
	private String account;
	
	
	@Column(name="username")
	private String username;
	
	@Column(name="totaldynamic")
	private int totaldynamic;
	
	@Column(name="taskdynamic")
	private int taskdynamic;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	@Column(updatable=false)
	private Timestamp v_time = new Timestamp(System.currentTimeMillis());
	

	
	
	//---------------------------------@Transient---------------------------------------
	//v_time属于第几周(00,01,02....53)
	@Transient
	private String week;
	
	//时间按月统计用(2016-01,2016-02)
	@Transient
	private String v_timeStr;
	
	

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}
	

	public String getV_timeStr() {
		return v_timeStr;
	}

	public void setV_timeStr(String v_timeStr) {
		this.v_timeStr = v_timeStr;
	}

	//---------------------------------@Transient---------------------------------------
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	

	public int getTotaldynamic() {
		return totaldynamic;
	}

	public void setTotaldynamic(int totaldynamic) {
		this.totaldynamic = totaldynamic;
	}

	public int getTaskdynamic() {
		return taskdynamic;
	}

	public void setTaskdynamic(int taskdynamic) {
		this.taskdynamic = taskdynamic;
	}

	public Timestamp getV_time() {
		return v_time;
	}

	public void setV_time(Timestamp v_time) {
		this.v_time = v_time;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
}
