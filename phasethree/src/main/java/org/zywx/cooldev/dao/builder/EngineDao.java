package org.zywx.cooldev.dao.builder;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.EngineStatus;
import org.zywx.cooldev.commons.Enums.EngineType;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.builder.Engine;

public interface EngineDao extends PagingAndSortingRepository<Engine, Long> {
	public List<Engine> findByDel(DELTYPE delType);

	public List<Engine> findByOsTypeInAndDel(List<OSType> osType, DELTYPE normal);
	
	public List<Engine> findByProjectIdAndOsTypeInAndDel(long projectId, List<OSType> osType, DELTYPE normal);

	public List<Engine> findByProjectIdAndOsTypeInAndStatusInAndDel(Long projectId, List<OSType> osType,List<EngineStatus> status, DELTYPE normal);

	public List<Engine> findByOsTypeInAndStatusInAndDel(List<OSType> osType,List<EngineStatus> status, DELTYPE normal);

	public List<Engine> findByProjectIdAndDel(long projectId, DELTYPE normal);

	public List<Engine> findByProjectIdAndOsTypeInAndTypeInAndStatusInAndUploadStatusInAndVersionNoLikeAndDelOrderByCreatedAtDesc(Long projectId, List<OSType> osType,
			List<EngineType> type, List<EngineStatus> status,List<UploadStatus> uploadStatus,String keyWords, DELTYPE normal);

	
	public List<Engine> findByOsTypeInAndTypeInAndStatusInAndUploadStatusInAndVersionNoLikeAndDelOrderByCreatedAtDesc(List<OSType> osType, List<EngineType> type,
			List<EngineStatus> status,List<UploadStatus> uploadStatus,String keyWords, DELTYPE normal);

	
	public List<Engine> findByVersionNoAndOsTypeAndTypeAndDel(String versionNo, OSType osType, EngineType type,
			DELTYPE normal);

	
	public List<Engine> findByProjectIdAndVersionNoAndOsTypeAndTypeAndDelAndKernel(Long projectId, String versionNo,
			OSType osType, EngineType type, DELTYPE normal,String kernel);
}
