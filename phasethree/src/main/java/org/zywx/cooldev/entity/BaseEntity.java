package org.zywx.cooldev.entity;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.zywx.cooldev.commons.Enums;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 实体基类
 * @author yang.li
 * @date 2015-08-06
 * 
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -3850567204198694011L;

	protected static SimpleDateFormat TIME_FORMATOR = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	protected static SimpleDateFormat DAY_FORMATOR = new SimpleDateFormat("yyyy-MM-dd");

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	@Column(updatable=false)
	private Timestamp createdAt = new Timestamp(System.currentTimeMillis());
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")  
	private Timestamp updatedAt = new Timestamp(System.currentTimeMillis());
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "del",columnDefinition="tinyint")
	private Enums.DELTYPE del = Enums.DELTYPE.NORMAL;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCreatedAtStr() {
		return createdAt == null ? null : TIME_FORMATOR.format(createdAt);
	}
	
	public String getUpdatedAtStr() {
		return updatedAt == null ? null : TIME_FORMATOR.format(updatedAt);
	}


	public Enums.DELTYPE getDel() {
		return del;
	}

	public void setDel(Enums.DELTYPE del) {
		this.del = del;
	}

	@Override
	public String toString() {
		return "BaseEntity [id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", del=" + del + "]";
	}
	
	/**
	 * 输出实体类中的各个属性
	 * @return String
	 * @user jingjian.wu
	 * @date 2015年8月28日 下午4:16:21
	 * @throws
	 */
	public String toStr() {
	       StringBuffer sb = new StringBuffer();
	       try {
	    	   //返回父类中的各个属性值
	           Class t = this.getClass().getSuperclass();
	           Field[] fields = t.getDeclaredFields();
	           for (int i = 0; i < fields.length; i++) {
	              Field field = fields[i];
	              field.setAccessible(true);
	              sb.append("{");
	              sb.append(field.getName());
	              sb.append(":");
	              /*if (field.getType() == Integer.class) {
	                  sb.append(field.getInt(this));
	              } else if (field.getType() == Long.class) {
	                  sb.append(field.getLong(this));
	              } else if (field.getType() == Boolean.class) {
	                  sb.append(field.getBoolean(this));
	              } else if (field.getType() == char.class) {
	                  sb.append(field.getChar(this));
	              } else if (field.getType() == Double.class) {
	                  sb.append(field.getDouble(this));
	              } else if (field.getType() == Float.class) {
	                  sb.append(field.getFloat(this));
	              } else
	                  sb.append(field.get(this));*/
	              sb.append(field.get(this));
	              sb.append("}");
	           }
	           
	         //返回本类中的各个属性值
	           t = this.getClass();
	           fields = t.getDeclaredFields();
	           for (int i = 0; i < fields.length; i++) {
	              Field field = fields[i];
	              field.setAccessible(true);
	              sb.append("{");
	              sb.append(field.getName());
	              sb.append(":");
	              if (field.getType() == Integer.class) {
	                  sb.append(field.getInt(this));
	              } else if (field.getType() == Long.class) {
	                  sb.append(field.getLong(this));
	              } else if (field.getType() == Boolean.class) {
	                  sb.append(field.getBoolean(this));
	              } else if (field.getType() == char.class) {
	                  sb.append(field.getChar(this));
	              } else if (field.getType() == Double.class) {
	                  sb.append(field.getDouble(this));
	              } else if (field.getType() == Float.class) {
	                  sb.append(field.getFloat(this));
	              } else
	                  sb.append(field.get(this));
	              sb.append("}");
	           }
	           
	       } catch (Exception e) {
	           e.printStackTrace();
	       }
	       return sb.toString();
	    }

	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis()/1000);
		new Date().getTime();
		
	}
}
