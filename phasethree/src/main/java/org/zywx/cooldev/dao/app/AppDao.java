package org.zywx.cooldev.dao.app;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.IfStatus;
import org.zywx.cooldev.entity.app.App;

public interface AppDao extends PagingAndSortingRepository<App, Serializable>{

	public List<App> findByDel(DELTYPE delType);
	
	public List<App> findByProjectIdAndDel(long projectId, DELTYPE delType);
	
	public List<App> findByProjectIdAndDelAndNameLike(long projectId, DELTYPE delType,String appName);

	public List<App> findByProjectIdAndAppTypeAndDel(long projectId, Long appType, DELTYPE delType);
	
	public List<App> findByProjectIdAndAppTypeAndDelAndNameLike(long projectId, Long appType, DELTYPE delType,String appName);

	public List<App> findByAppcanAppIdAndAppcanAppKeyAndPublishedAndDel(String appcanAppId, String appcanAppKey, IfStatus published, DELTYPE delType);
	
	public List<App> findByAppcanAppIdAndAppcanAppKeyAndPublishedTestAndDel(String appcanAppId, String appcanAppKey, IfStatus published, DELTYPE delType);
	
	public List<App> findByAppcanAppIdAndAppcanAppKeyAndPublishedAppCanAndDel(String appcanAppId, String appcanAppKey, IfStatus published, DELTYPE delType);

	/**
	 * @user jingjian.wu
	 * @date 2015年11月3日 下午3:03:51
	 */
	    
	public App findByAppcanAppIdAndDel(String appcanAppId, DELTYPE normal);
	
	public List<App> findByNameAndDel(String name, DELTYPE normal);
	
	public List<App> findByUserIdAndNameLike(long userId,String appName);
	
	public List<App> findByUserId(long userId);
	
	public List<App> findByUserIdAndDel(long userId,DELTYPE normal);
	
	/**
	 * 查询某个团队下面的应用
	 * @user jingjian.wu
	 * @date 2015年12月8日 上午11:34:16
	 */
	@Query(value="select t from App as t where t.projectId in (select p.id from Project p where p.teamId=?1 and p.del=0) and t.del=0 order by t.createdAt desc")
	public List<App> findByTeamId(long teamId);
	
	public List<App> findByAppcanAppIdAndAppcanAppKeyAndDel(String appcanAppId, String appcanAppKey,  DELTYPE delType);
	
//	@Query(value="select distinct name from App where projectId in (?1) and del = ?2 and name like ?3")
//	public List<String> findByProjectIdInAndDelAndNameLike(List<Long> projectIdList, DELTYPE delType,String name);
	
	@Query(value="select p from App p where p.projectId in (?1) and p.del = ?2 and p.name like ?3")
	public List<App> findByProjectIdInAndDelAndNameLike(List<Long> projectIdList, DELTYPE delType,String name);
	
	public App findByRelativeRepoPathAndDel(String relativeRepoPath, DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_APP where del=?1 and projectId=?2")
	public List<App> getAppByProjectId(int ordinal, long projectId);
    
    public List<App> findByProjectIdAndAndNameLikeAndDel(long projectId,String name,DELTYPE delType);
	/**
	 * 查询某个项目下面的应用
	 * @user haijun.cheng
	 * @date 2016年07月7日 上午
	 */
	@Query(value="select t from App as t where t.projectId =?1 and t.del=0 order by t.createdAt desc")
	public List<App> findByProjectId(Long projectId);
	
	@Query(value="select p from App p where p.projectId =?1 and p.del = ?5 and (p.name like ?2 or p.pinYinHeadChar like ?3 or p.pinYinName like ?4)")
	public List<App> findByProjectIdAndAndNameLikeOrPinYinHeadCharOrPinYinNameAndDel(
			long projectId, String name, String pinYinHead, String pinYinName,
			DELTYPE  normal);

	public App findByAppcanAppKeyAndDel(String appcanAppKey, DELTYPE delType);

	/**
	 * 通过id查询应用数据
	 * zhouxx add 20170813
	 * @param transactionsId
	 * @param normal
	 * @return
	 */
	public App findByIdAndDel(long transactionsId, DELTYPE normal);
	
	public List<App> findByProjectIdAndIsProApp(long dmid, boolean b);
	@Modifying  
	@Transactional 
	@Query(value=" DELETE FROM APP t WHERE t.id =?1",nativeQuery=true)
	public void delByid(long appId);
}
