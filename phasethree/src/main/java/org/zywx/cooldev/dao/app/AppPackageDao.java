package org.zywx.cooldev.dao.app;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.AppPackageBuildType;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.TerminalType;
import org.zywx.cooldev.entity.app.AppPackage;

public interface AppPackageDao extends PagingAndSortingRepository<AppPackage, Serializable>{

	public List<AppPackage> findByAppVersionIdAndDel(long appVersionId, DELTYPE delType);
	
	public List<AppPackage> findByAppVersionIdAndDelOrderByCreatedAtDesc(long appVersionId, DELTYPE delType);
	
	public List<AppPackage> findByAppVersionIdAndOsTypeAndDel(long appVersionId, OSType osType, DELTYPE delType);

	public List<AppPackage> findByVersionNoAndOsTypeAndTerminalTypeAndChannelCodeAndBuildTypeAndDel(
			String versionNo, OSType osType, TerminalType terminalType, String channelCode, AppPackageBuildType buildType, DELTYPE delType
			);

	/**
	 * @user jingjian.wu
	 * @date 2015年11月10日 上午10:22:39
	 */
	    
	@Query(value="select t from AppPackage t  where t.appVersionId in (select id from AppVersion where  appId =?1 and del=0 ) and t.del=0")
	public List<AppPackage> findChannelCodeByAppId(long appId);

	public AppPackage findTop1ByAppVersionIdAndDelOrderByVersionNoDesc(long appVersionId, DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_APP_PACKAGE where del=?1 and appVersionId in (select id from T_APP_VERSION where del=?1 and appId in (select id from T_APP where del=?1 and projectId=?2))")
	public List<AppPackage> getAppPackageByProjectId(int ordinal, long projectId);
}
