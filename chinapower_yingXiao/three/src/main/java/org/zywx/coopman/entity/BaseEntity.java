package org.zywx.coopman.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.zywx.coopman.commons.Enums;

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

	public Timestamp getCreatedAtT() {
		return createdAt;
	}
	
	public String getCreatedAt() {
		if(null==createdAt){
			createdAt = new Timestamp(System.currentTimeMillis());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return sdf.format(createdAt);
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAtT() {
		return updatedAt;
	}
	
	public String getUpdatedAt() {
		if(null==updatedAt){
			updatedAt = new Timestamp(System.currentTimeMillis());
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		return sdf.format(updatedAt);
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}



	public Enums.DELTYPE getDel() {
		return del;
	}

	public void setDel(Enums.DELTYPE del) {
		this.del = del;
	}

	/*@Override
	public String toString() {
	       StringBuffer sb = new StringBuffer();
	       try {
	    	   //返回父类中的各个属性值
	           Class<?> t = this.getClass().getSuperclass();
	           Field[] fields = t.getDeclaredFields();
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
	}*/
	
	@Override
	public String toString() {
		return "BaseEntity [id=" + id + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", del=" + del + "]";
	}
	
	//串行化深度复制
	 public Object deepClone() throws IOException,
	 	OptionalDataException,ClassNotFoundException{//将对象写到流里
		 ByteArrayOutputStream bo=new ByteArrayOutputStream();
		  ObjectOutputStream oo=new ObjectOutputStream(bo);
		  oo.writeObject(this);//从流里读出来
		  ByteArrayInputStream bi=new ByteArrayInputStream(bo.toByteArray());
		  ObjectInputStream oi=new ObjectInputStream(bi);
		  return(oi.readObject());
	 }

}
