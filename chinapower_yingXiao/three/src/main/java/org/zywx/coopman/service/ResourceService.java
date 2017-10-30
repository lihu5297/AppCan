package org.zywx.coopman.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.resource.ResourceContent;
import org.zywx.coopman.entity.resource.ResourceFileInfo;
import org.zywx.coopman.entity.resource.ResourceFileRelation;
import org.zywx.coopman.entity.resource.ResourceType;
import org.zywx.coopman.entity.resource.TempletInfo;

@Service
public class ResourceService extends BaseService{

	@Value("${rootpath}")
	private String fileRootPath;
	@Value("${accessUrl}")
	private String accessUrl;
	/**
	 * 查找资源类别
	 * @return
	 */
	public Page<ResourceType> findResourceTypeAll(PageRequest page){
		return resourceTypeDao.findByDel(Enums.DELTYPE.NORMAL,page);
	}
	/**
	 * 查找资源类别根据主键ID
	 * @param id
	 * @return
	 */
	public ResourceType findResourceTypeById(long id){
		return resourceTypeDao.findOne(id);
	}
	public List<ResourceType> findResourceTypeByIds(List<Long> ids){
		return resourceTypeDao.findByIdIn(ids);
	}
	/**
	 * 查找资源类别根据类别名称
	 * @param typeName
	 * @return
	 */
	public List<ResourceType> findResourceTypeByName(String typeName){
		return resourceTypeDao.findByTypeNameAndDel(typeName, Enums.DELTYPE.NORMAL);
	}
	/**
	 * 保存资源类别
	 * @param rt
	 * @return
	 */
	public ResourceType saveEditResourceType(ResourceType rt){
		return resourceTypeDao.save(rt);
	}
	/**
	 * 删除资源类别
	 * @param rt
	 */
	public void delResourceType(ResourceType rt){
		resourceTypeDao.delete(rt);
	}
	public void delResourceType(List<ResourceType> rtList){
		resourceTypeDao.delete(rtList);
	}
	/**
	 * 查询内容信息全部
	 * @return
	 */
	public Page<ResourceContent> findResourceContentAll(PageRequest page){
		return resourceContentfoDao.findByDel(Enums.DELTYPE.NORMAL,page);
	}
	/**
	 * 根据内容类别查询内容
	 * @param typeCode
	 * @return
	 */
	public List<ResourceContent> findResourceContentByType(long typeId){
		return resourceContentfoDao.findByResTypeAndDel(typeId, Enums.DELTYPE.NORMAL);
	}
	/**
	 * 查询内容信息根据主键ID
	 * @param id
	 * @return
	 */
	public ResourceContent findResourceContentById(long id){
		ResourceContent rc = resourceContentfoDao.findOne(id);
		if(rc != null){
			//查找资源内容附件
			List<Long> fileIdList = resourceFileRelationDao.findFileIdByContentId(rc.getId());
			List<ResourceFileInfo> rfList = null;
			if(fileIdList != null && !fileIdList.isEmpty()){
				rfList = resourceFileInfoDao.findByIdIn(fileIdList);
				if(rfList != null)
					rc.setFileList(rfList);
			}
			ResourceType rt = resourceTypeDao.findOne(rc.getResType());
			if(rt != null){
				rc.setTypeName(rt.getTypeName());
			}
		}
		return rc;
	}
	public List<ResourceContent> findResourceContentByIds(List<Long> ids){
		List<ResourceContent> rcList = resourceContentfoDao.findByIdIn(ids);
		return rcList;
	}
	/**
	 * 保存内容信息
	 * @param rf
	 * @return
	 */
	@Transactional
	public ResourceContent saveEditResourceContent(ResourceContent rc){
		ResourceContent rcNew = resourceContentfoDao.save(rc);
		//保存或更新内容附件
		if(rc.getFileIds() != null && !rc.getFileIds().isEmpty()){
			List<ResourceFileRelation> rfrList = findResourceFileRelationByContentId(rcNew.getId());
			resourceFileRelationDao.delete(rfrList);
			//建立内容与附件的关系
			ResourceFileRelation newRfr = null;
			List<ResourceFileRelation> relList = new ArrayList<ResourceFileRelation>();
			for(Long fileId : rc.getFileIds()){
				newRfr = new ResourceFileRelation();
				newRfr.setContentId(rcNew.getId());
				newRfr.setFileId(fileId);
				relList.add(newRfr);
			}
			resourceFileRelationDao.save(relList);
		}
		return rcNew;
	}
	/**
	 * 删除内容
	 * @param rf
	 */
	@Transactional
	public void delResourceContent(List<ResourceContent> rcList){
		if(rcList != null && !rcList.isEmpty()){
			List<Long> rcIds = new ArrayList<Long>();
			for(ResourceContent rc : rcList){
				rcIds.add(rc.getId());
			}
			//查找资源内容附件
			List<ResourceFileRelation> rfrList = resourceFileRelationDao.findByContentIdInAndDel(rcIds,Enums.DELTYPE.NORMAL);
			if(rfrList != null && !rfrList.isEmpty()){
				for(ResourceFileRelation rfr : rfrList){
					//删除文件
					//delFileInfo(rfr.getFileId());
					resourceFileInfoDao.delete(rfr.getFileId());
				}
			}
			//删除内容与附件的关系
			resourceFileRelationDao.delete(rfrList);
		}
		resourceContentfoDao.delete(rcList);
	}
	/**
	 * 根据内容查找内容相关联附件
	 * @param contentId
	 * @return
	 */
	public List<ResourceFileRelation> findResourceFileRelationByContentId(long contentId){
		return resourceFileRelationDao.findByContentIdAndDel(contentId, Enums.DELTYPE.NORMAL);
	}
	/**
	 * 删除内容与附件的关系
	 * @param rfr
	 */
	public void delResourceFileRelation(List<ResourceFileRelation> rfr){
		resourceFileRelationDao.delete(rfr);
	}
	/**
	 * 模板查询
	 * @return
	 */
	public List<TempletInfo> findTempletAll(){
		return templetInfoDao.findAll();
	}
	
	public TempletInfo findTempletById(long id){
		return templetInfoDao.findOne(id);
	}
	public TempletInfo saveEditTempletInfo(TempletInfo ti){
		return templetInfoDao.save(ti);
	}
	/**
	 * 查询附件
	 * @param id
	 * @return
	 */
	public ResourceFileInfo findFileById(long id){
		return resourceFileInfoDao.findOne(id);
	}
	
	/**
	 * 保存附件
	 * @param rf
	 * @return
	 */
	public ResourceFileInfo saveUpdateFileInfo(ResourceFileInfo rf){
		return resourceFileInfoDao.save(rf);
	}
	
	public void delFileInfo(long fileId){
		ResourceFileInfo rf = resourceFileInfoDao.findOne(fileId);
		if(rf != null){
			//删除表记录
			resourceFileInfoDao.delete(rf);
			//删除磁盘文件
			String command1 = "rm -rf " + fileRootPath + rf.getFilePath();
			Process process = null;
			try {
				process = Runtime.getRuntime().exec(command1);
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
 
}
