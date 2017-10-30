package org.zywx.cooldev.entity;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;
import org.zywx.cooldev.commons.Enums.SOURCE_TYPE;
import org.zywx.cooldev.util.PropertiesLoader;


/**
 * 资源实体类
 * @Description: 
 * @author jingjian.wu
 * @date 2015年8月20日 上午9:11:31
 *
 */
@Component
@Entity
@Table(name = "T_RESOURCES")
public class Resource extends BaseEntity implements Cloneable{
	
	/**
	 * @Fields serialVersionUID :
	 */
	private static final long serialVersionUID = -3107437865153640942L;

	//文件名称
	@Column(name="name",nullable= false)
	private String name;
	//文件类型(目录,png等)
	@Column(name="type",nullable= false)
	private String type;
	//父亲节点
	@Column(name="parentId",nullable= false)
	private long parentId=-1L;
	//用户id
	@Column(name="userId",nullable= false)
	private long userId;
	
	@Column(name="userName",nullable= false)
	private String userName;
	//项目ID
	@Column(name="projectId",nullable= false)
	private long projectId;
	
	// @Transient
	//private String projectId;
	//文件大小
	@Column(name="fileSize")
	private long fileSize = 0L;
	//文件父亲路径
	@Column(name="filePath",nullable= false)
	private String filePath;
	
	@Column(name="sourceType",nullable= false)
	private SOURCE_TYPE sourceType;
	
	@Column(name="uuid")
	private String uuid=UUID.randomUUID().toString();;
	
	@Column(name="isPublic")
	private int isPublic;
	
	@Transient
	private String projectName;
	
	@Transient
	private String downLoadPath;   
	
	@Transient
	private List<Resource> child;
	
	//在资料列表中,上传到目标文件夹,需要以下拉框的方式展现   -- -- b
	@Transient
	private String showNameInSelect;
	
	@Transient
	private String src;
	
	public String getSrc() {
		String [] picture = new String[]{".JPEG",".JPG",".PNG",".SWF",".SVG",".PCX",".DXF",".WMF",".EMF",".TIFF",".PSD",".GIF",".BMP"};
		for(String str : picture){
			if(str.toLowerCase().equals(this.getType().toLowerCase())){
				return PropertiesLoader.getText("BASEURI")+this.getFilePath()+this.getName();
			}
		}
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getShowNameInSelect() {
		return showNameInSelect;
	}
	public void setShowNameInSelect(String showNameInSelect) {
		this.showNameInSelect = showNameInSelect;
	}
	public List<Resource> getChild() {
		return child;
	}
	public void setChild(List<Resource> child) {
		this.child = child;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}
	public String getDownLoadPath() {
		return downLoadPath;
	}
	public void setDownLoadPath(String downLoadPath) {
		this.downLoadPath = downLoadPath;
	}
	//获取文件大小,大于1M按照MB返回,小于1M按照KB返回
	public String getSizeStr() {
		String unit;
		DecimalFormat df = new DecimalFormat("0.00");
		if((double)fileSize/1024/1024>=1){
			unit=df.format((double)fileSize/1024/1024)+"MB";
		}else if((double)fileSize/1024>=1){
			unit=df.format((double)fileSize/1024)+"KB";
		}else{
			unit=fileSize+"B";
		}
		return unit;
	}
	@Override
	public String toString() {
		return this.getName();
	}
	
	public SOURCE_TYPE getSourceType() {
		return sourceType;
	}
	public void setSourceType(SOURCE_TYPE sourceType) {
		this.sourceType = sourceType;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {   
            return super.clone();   
        } catch (CloneNotSupportedException e) {   
            return null;   
        }  
	}
}
