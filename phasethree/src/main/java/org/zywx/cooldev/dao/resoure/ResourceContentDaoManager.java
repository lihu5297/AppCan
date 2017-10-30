package org.zywx.cooldev.dao.resoure;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.zywx.cooldev.entity.resource.ResourceContent;

@Repository
public class ResourceContentDaoManager {

	@PersistenceContext(unitName = "coolDevEntity")
	private EntityManager em;
	
	public List<ResourceContent> findByResTypeAndDel(long typeId,int del, String filialeId){
		StringBuffer sqlRs = new StringBuffer();
		sqlRs.append("select * from T_MAN_RESOURCE_CONTENT  where resType = ?1 and del = ?2 and find_in_set(?3,filialeIds)");
		Query sqlQuery = em.createNativeQuery(sqlRs.toString());
		sqlQuery.setParameter(1, typeId);
		sqlQuery.setParameter(2, del);
		sqlQuery.setParameter(3, filialeId);
		List<ResourceContent> rcList = new ArrayList<ResourceContent>();
		try {
			List<Object[]> l = sqlQuery.getResultList();
			ResourceContent rc = null;
			if (l != null && !l.isEmpty()) {
				for (Object[] obj : l) {
					rc = new ResourceContent();
					rc.setId(Long.parseLong(obj[0].toString()));
					rc.setCreatedAt(Timestamp.valueOf(obj[1].toString()));
					rc.setUpdatedAt(Timestamp.valueOf(obj[3].toString()));
					rc.setCreator(obj[4] == null ? "" : obj[4].toString());
					rc.setResDesc(obj[5] == null ? "" : obj[5].toString());
					rc.setResName(obj[6] == null ? "" : obj[6].toString());
					rc.setResType(obj[7] == null ? 1 : Long.parseLong(obj[7].toString()));
					rc.setResVersion(obj[8] == null ? "" : obj[8].toString());
					rcList.add(rc);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rcList;
	}
	
	public List<ResourceContent> findByResNameLikeOrResDescLikeAndDel(String value1, int del, String filialeId){
		StringBuffer sqlRs = new StringBuffer();
		sqlRs.append("select * from T_MAN_RESOURCE_CONTENT  where (resName like ?1 or resDesc like ?1) and del = ?2 and find_in_set(?3,filialeIds)");
		Query sqlQuery = em.createNativeQuery(sqlRs.toString());
		sqlQuery.setParameter(1, value1);
		sqlQuery.setParameter(2, del);
		sqlQuery.setParameter(3, filialeId);
		List<ResourceContent> rcList = new ArrayList<ResourceContent>();
		try {
			List<Object[]> l = sqlQuery.getResultList();
			ResourceContent rc = null;
			if (l != null && !l.isEmpty()) {
				for (Object[] obj : l) {
					rc = new ResourceContent();
					rc.setId(Long.parseLong(obj[0].toString()));
					rc.setCreatedAt(Timestamp.valueOf(obj[1].toString()));
					rc.setUpdatedAt(Timestamp.valueOf(obj[3].toString()));
					rc.setCreator(obj[4] == null ? "" : obj[4].toString());
					rc.setResDesc(obj[5] == null ? "" : obj[5].toString());
					rc.setResName(obj[6] == null ? "" : obj[6].toString());
					rc.setResType(obj[7] == null ? 1 : Long.parseLong(obj[7].toString()));
					rc.setResVersion(obj[8] == null ? "" : obj[8].toString());
					rcList.add(rc);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rcList;
	}
}
