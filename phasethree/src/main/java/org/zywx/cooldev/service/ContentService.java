
package org.zywx.cooldev.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.dao.resoure.ResourceContentDao;
import org.zywx.cooldev.dao.resoure.ResourceContentDaoManager;
import org.zywx.cooldev.dao.resoure.ResourceFileInfoDao;
import org.zywx.cooldev.dao.resoure.ResourceFileRelationDao;
import org.zywx.cooldev.dao.resoure.ResourceTypeDao;
import org.zywx.cooldev.dao.resoure.TempletInfoDao;
import org.zywx.cooldev.entity.resource.ResourceContent;
import org.zywx.cooldev.entity.resource.ResourceFileInfo;
import org.zywx.cooldev.entity.resource.ResourceType;
import org.zywx.cooldev.entity.resource.TempletInfo;

@Service
public class ContentService extends BaseService {

	@Value("${accessUrl}")
	private String accessUrl;
	@Autowired
	protected ResourceTypeDao  resourceTypeDao;
	
	@Autowired
	protected ResourceContentDao  resourceContentfoDao;
	
	@Autowired
	protected ResourceFileRelationDao  resourceFileRelationDao;
	
	@Autowired
	protected ResourceFileInfoDao  resourceFileInfoDao;
	@Autowired
	protected TempletInfoDao  templetInfoDao;
	@Autowired
	protected ResourceContentDaoManager rcDaoManager;
	
	 
	public List<ResourceType> findAllResourceType(DELTYPE normal) {
		return resourceTypeDao.findByDel(normal);
	}

	public ResourceContent findResourceContentById(Long id) {

		ResourceContent rc = resourceContentfoDao.findOne(id);
		if (rc != null) {
			// 查找资源内容附件
			List<Long> fileIdList = resourceFileRelationDao.findFileIdByContentId(rc.getId());
			rc.setFileIds(fileIdList);
			List<ResourceFileInfo> rfList = null;
			if (fileIdList != null && !fileIdList.isEmpty()) {
				rfList = resourceFileInfoDao.findByIdIn(fileIdList);
				if (rfList != null)
					for (ResourceFileInfo rfInfo : rfList) {
						rfInfo.setFilePath(accessUrl + rfInfo.getFilePath());
					}
				rc.setFileList(rfList);
			}
			ResourceType rt = resourceTypeDao.findOne(rc.getResType());
			if (rt != null) {
				rc.setTypeName(rt.getTypeName());
			}
		}

		return rc;
	}

	public List<ResourceContent> findResourceContentByContentType(Long resourceId,String filialeId) {
		return rcDaoManager.findByResTypeAndDel(resourceId,  Enums.DELTYPE.NORMAL.ordinal(), filialeId);
		//return resourceContentfoDao.findByResTypeAndDel(resourceId, Enums.DELTYPE.NORMAL );
	}

	public List<ResourceContent> searchContent(String value,String filialeId) {
		List<ResourceContent> rclist = rcDaoManager.findByResNameLikeOrResDescLikeAndDel(value, Enums.DELTYPE.NORMAL.ordinal(), filialeId);
		//List<ResourceContent> rclist = resourceContentfoDao.findByResNameLikeOrResDescLikeAndDel(value, value,Enums.DELTYPE.NORMAL );

		if(rclist !=null){
			//获取内容Ids
			List<BigInteger> clist=resourceFileRelationDao.findContentIdByValueLike(value);
			 
			List<Long> idList = new ArrayList<Long>();
			for(BigInteger id : clist){
				idList.add(id.longValue());
			}
			List<ResourceContent> rclist2 =  resourceContentfoDao.findByIdInAndDel(idList, Enums.DELTYPE.NORMAL);
			rclist.addAll(rclist2);
		} 
		List<ResourceContent> listWithoutDup = new ArrayList<>(new HashSet<>(rclist));
		return listWithoutDup;
	}

	/**
	 * 模板查询
	 * @return
	 */
	public List<TempletInfo> findTempletAll(){
		List<TempletInfo> all = templetInfoDao.findAll();
		for (TempletInfo templetInfo : all) {
			templetInfo.setFilePath(accessUrl+templetInfo.getFilePath());
		}
		return all;
	}
}
