package org.zywx.cooldev.dao.app;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.app.AppPatch;

public interface AppPatchDao extends PagingAndSortingRepository<AppPatch, Serializable>{
	public List<AppPatch> findByBaseAppVersionIdAndDel(long baseAppVersionId, DELTYPE delType);
	
	public AppPatch findTop1ByBaseAppVersionIdAndDelOrderByVersionNoDesc(long baseAppVersionId, DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_APP_PATCH where del=?1 and baseAppVersionId in (select id from T_APP_VERSION where del=?1 and appId in (select id from T_APP where del=?1 and projectId=?2))")
	public List<AppPatch> getAppPatchByProjectId(int ordinal, long projectId);
}
