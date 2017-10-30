package org.zywx.coopman.entity;

import java.net.URLDecoder;
import java.sql.Timestamp;

public class QueryEntity {
	
	private String search;
	
	private int pageNo = 1;
	
	private int pageSize = 15;
	
	private Timestamp startTime;
	
	private Timestamp endTime;

	public String getSearch() {
		search = search==null?"":search;
		search = URLDecoder.decode(search);
		return search;
	}

	public void setSearch(String search) {
		this.search = search;
	}

	public int getPageNo() {
		return pageNo==0?1:pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize==0?15:pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	
	public int getStartNum(){
		this.pageNo = this.pageNo==0?1:this.pageNo;
		return (this.pageNo-1)*this.pageSize;
	}
	
	public int getEndNum(){
		return this.pageNo*this.pageSize;
	}

}
