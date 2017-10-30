package org.zywx.cooldev.dao.builder;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.PluginVersionStatus;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.entity.builder.PluginVersion;

public interface PluginVersionDao extends PagingAndSortingRepository<PluginVersion, Long> {
	
	public List<PluginVersion> findByOsTypeInAndDelOrderByVersionNoAsc(List<OSType> osType, DELTYPE delType);

	public List<PluginVersion> findByPluginIdAndOsTypeInAndDelOrderByVersionNoAsc(long pluginId, List<OSType> osType, DELTYPE delType);

	public List<PluginVersion> findByPluginIdAndDel(long pluginId, DELTYPE normal);
	
	public List<PluginVersion> findByPluginIdIn(List<Long> pluginIdList);

	public List<PluginVersion> findByOsTypeInAndStatusInAndDelOrderByVersionNoDesc(List<OSType> osType,
			List<PluginVersionStatus> status, DELTYPE normal);

	public List<PluginVersion> findByPluginIdAndOsTypeInAndStatusInAndDelOrderByVersionNoDesc(Long pluginId,
			List<OSType> osType, List<PluginVersionStatus> status, DELTYPE normal);
	
	public List<PluginVersion> findByPluginIdAndStatusInAndUploadStatusInAndDel(long pluginId, List<PluginVersionStatus> status,List<UploadStatus> uploadStatus,
			DELTYPE normal);
	
	public List<PluginVersion> findByPluginIdAndStatusAndUploadStatusInAndDel(long pluginId, PluginVersionStatus status,List<UploadStatus> uploadStatus, DELTYPE normal);

	public List<PluginVersion> findByIdIn(List<Long> idList);

	public List<PluginVersion> findByOsTypeInAndStatusInAndDelOrderByIdDesc(List<OSType> osType,
			List<PluginVersionStatus> status, DELTYPE normal);

	public List<PluginVersion> findByPluginIdAndOsTypeInAndStatusInAndDelOrderByIdDesc(Long pluginId,
			List<OSType> osType, List<PluginVersionStatus> status, DELTYPE normal);

	
	public List<PluginVersion> findByOsTypeAndPluginIdAndVersionNoAndDelOrderByIdDesc(OSType osType, long pluginId,
			String versionNo, DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_PLUGIN_VERSION where del=?1 and pluginId in (select id from T_PLUGIN where del=?1 and projectId=?2)")
	public List<PluginVersion> getPluginVersionByProjectId(int ordinal,
			long projectId);

}
