package org.zywx.cooldev.entity.query;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TopicQuery {

	private String topicName = "";
	private String creator = "";
	private String actor = "";
	private String createdAtStart;
	private String createdAtEnd;
	
	public String getTopicName() {
		return "%"+topicName+"%";
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getCreator() {
		return "%"+creator+"%";
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getActor() {
		return "%"+actor+"%";
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getCreatedAtStart() {
		return createdAtStart;
	}
	public void setCreatedAtStart(String createdAtStart) {
		this.createdAtStart = createdAtStart;
	}
	public String getCreatedAtEnd() {
		return createdAtEnd;
	}
	public void setCreatedAtEnd(String createdAtEnd) {
		this.createdAtEnd = createdAtEnd;
	}
	
//	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public Timestamp getCreatedAtStart() {
		if(null==createdAtStart){
			return null;
		}else{
			Date date = new Date();
			try{
				date = sdf.parse(createdAtStart);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new Timestamp(date.getTime());
		}
	}
	public void setCreatedAtStart(Timestamp createdAtStart) {
		String date = null;
		try{
			date = sdf.format(createdAtStart);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.createdAtStart = date;
	}
	public Timestamp getCreatedAtEnd() {
		if(null==createdAtEnd){
			return null;
		}else{
			Date date = new Date();
			try{
				date = sdf.parse(createdAtEnd);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new Timestamp(date.getTime());
		}
	}
	public void setCreatedAtEnd(Timestamp createdAtEnd) {
		String date = null;
		try{
			date = sdf.format(createdAtEnd);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.createdAtEnd = date;
	}*/
	
	
	
}
