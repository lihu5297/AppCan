package org.zywx.cooldev.dao.app;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.app.AppWidget;

public interface AppWidgetDao extends PagingAndSortingRepository<AppWidget, Serializable>{

	public List<AppWidget> findByAppVersionIdAndDel(long appVersionId, DELTYPE delType);
	
	public AppWidget findTop1ByAppVersionIdAndDelOrderByVersionNoDesc(long appVersionId, DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_APP_WIDGET where del=?1 and appVersionId in (select id from T_APP_VERSION where del=?1 and appId in (select id from T_APP where del=?1 and projectId=?2))")
	public List<AppWidget> getAppWidgetByProjectId(int ordinal, long projectId);

}
