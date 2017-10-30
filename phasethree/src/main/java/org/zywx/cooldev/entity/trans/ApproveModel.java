package org.zywx.cooldev.entity.trans;

import java.util.List;

public class ApproveModel {
	private String type;
	private String status;
	private long id;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "ApproveModel [type=" + type + ", status=" + status + ", id="
				+ id + "]";
	}
	
}
 
