package org.zywx.cooldev.dao.app;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.app.AppChannel;

public interface AppChannelDao extends PagingAndSortingRepository<AppChannel, Serializable>{

	public List<AppChannel> findByAppIdAndDel(long appId, DELTYPE delType);
	
	public  AppChannel findByAppIdAndCodeAndDel(long appId,String code,DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_APP_CHANNEL where del=?1 and appId in (select id from T_APP where del=?1 and projectId=?2)")
	public List<AppChannel> getAppChannelByProjectId(int ordinal, long projectId);

}
