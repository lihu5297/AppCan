package org.zywx.cooldev.dao.app;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.AppVersionType;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.app.AppVersion;

public interface AppVersionDao extends PagingAndSortingRepository<AppVersion, Long>, JpaSpecificationExecutor<AppVersion> {

	public List<AppVersion> findByAppIdAndDel(long appId, DELTYPE delType);
	
	public List<AppVersion> findByAppIdAndVersionNoAndDel(long appId, String versionNo, DELTYPE delType);

	public long countByAppIdAndVersionNoAndTypeAndDel(long appId, String versionNo,AppVersionType type, DELTYPE delType);
	
	public long countByAppIdAndVersionNoAndTypeAndUserIdAndDel(long appId, String versionNo, AppVersionType type,Long userId,DELTYPE delType);

	/**
	 * @user jingjian.wu
	 * @date 2015年10月30日 下午12:01:42
	 */
	    
	public List<AppVersion> findByAppIdAndDelAndBranchNameAndTypeAndCreatedAtGreaterThan(
			long appId, DELTYPE normal, String branchName,
			AppVersionType project, Timestamp createdAt);

	/**
	 * @user jingjian.wu
	 * @date 2015年10月30日 下午12:03:42
	 */
	    
	public List<AppVersion> findByAppIdAndDelAndBranchNameAndTypeAndUserIdAndCreatedAtGreaterThan(
			long appId, DELTYPE normal, String branchName,
			AppVersionType personal, long userId, Timestamp createdAt);

	
	/**
	 * 获取某个人创建的个人版本,和所有的项目版本
	 * @param appId
	 * @param loginUserId
	 * @param normal
	 * @return
	 */
	@Query(value=" select distinct v.versionNo from AppVersion v where v.appId=?1  and v.del=?3 and ((v.userId=?2 and v.type=1) or v.type=0)")
	public List<String> findAppVersionByAppIdAndUserIdAndDel(long appId,Long loginUserId,DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_APP_VERSION where del=?1 and appId in (select id from T_APP where del=?1 and projectId=?2)")
	public List<AppVersion> getAppVersionByProjectId(int ordinal, long projectId);
}
