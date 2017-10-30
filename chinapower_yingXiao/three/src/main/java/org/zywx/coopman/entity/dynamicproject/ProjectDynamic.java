package org.zywx.coopman.entity.dynamicproject;

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
@Table(name="C_PROJECT_DYNAMIC")
public class ProjectDynamic {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@Column(name="prjid")
	private long prjid;
	
	@Column(name="prjname")
	private String prjname;
	
	@Column(name="totaldynamic")
	private int totaldynamic;
	
	@Column(name="taskdynamic")
	private int taskdynamic;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	@Column(updatable=false)
	private Timestamp v_time = new Timestamp(System.currentTimeMillis());
	
	@Column(name="totalmember")
	private int totalmember;

	
	
	//---------------------------------@Transient---------------------------------------
	//平均总人数
	@Transient
	private double avgTotalMember;
	//v_time属于第几周(00,01,02....53)
	@Transient
	private String week;
	
	//时间按月统计用(2016-01,2016-02)
	@Transient
	private String v_timeStr;
	
	
	public double getAvgTotalMember() {
		return avgTotalMember;
	}

	public void setAvgTotalMember(double avgTotalMember) {
		this.avgTotalMember = avgTotalMember;
	}

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


	public long getPrjid() {
		return prjid;
	}

	public void setPrjid(long prjid) {
		this.prjid = prjid;
	}

	public String getPrjname() {
		return prjname;
	}

	public void setPrjname(String prjname) {
		this.prjname = prjname;
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

	public int getTotalmember() {
		return totalmember;
	}

	public void setTotalmember(int totalmember) {
		this.totalmember = totalmember;
	}
	
	
	
}
