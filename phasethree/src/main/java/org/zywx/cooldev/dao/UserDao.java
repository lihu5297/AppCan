package org.zywx.cooldev.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.USER_STATUS;
import org.zywx.cooldev.entity.User;

public interface UserDao extends PagingAndSortingRepository<User, Long> {
	
	/**
	 * 根据团队id标识,和团队的关系(1创建,2参与,3受邀)查询该团队下面的用户列表
	    * @Title: findUserListByids
	    * @Description:
	    * @param @param typeIds  如果是团队下面的已有用户用(1,2);如果是团队下面的受邀还没同意的用户用3 
	    * @param @param teamId
	    * @param @return    参数
	    * @return List<User>    返回类型
		* @user wjj
		* @date 2015年8月11日 下午5:17:02
	    * @throws
	 */
	@Query(value="select u from User u where u.id in (select tm.userId from TeamMember tm where tm.type in ?1 and tm.del = ?3 and tm.teamId=?2 ) and u.del = ?3")
	public Page<User> findUserListByTeamId(List<TEAMREALTIONSHIP> typeIds,long teamId,DELTYPE del,Pageable pageable);
	@Query(value="select u from User u where u.id in (select tm.userId from TeamMember tm where tm.type in ?1 and tm.del = ?3 and tm.teamId=?2 ) and u.del = ?3 and (u.email like ?4 or u.userName like ?4 or u.pinYinName like ?4)")
	public Page<User> findUserListByTeamIdAndByKeywords(List<TEAMREALTIONSHIP> typeIds,long teamId,DELTYPE del,String keywords,Pageable pageable);
	
	/**
	 * 转让项目时候选取人
	 * @param typeIds
	 * @param teamId
	 * @param search
	 * @return List<User>
	 * @user jingjian.wu
	 * @date 2015年8月25日 下午3:12:08
	 * @throws
	 */
	@Query(nativeQuery=true,value="select * from T_USER where id in (select userId from T_TEAM_MEMBER where  type in ?1 and del=0 and teamId=?2 ) and (userName like (?3) or substring_index(account,'@',1) like (?3)) and del = 0")
	public List<User> findUserByTeamIdSearch(List<Integer> typeIds,long teamId,String search);
	
	/**
	 * 转让项目候选人
	 * @param typeIds
	 * @param teamId
	 * @param search
	 * @return List<User>
	 * @user jingjian.wu
	 * @date 2015年8月25日 下午3:12:08
	 * @throws
	 */
	@Query(value="select u from User u where u.id in (select userId from TeamMember where type in ?1 and del=0 and teamId=?2 ) and (userName like ?3 or substring_index(account,'@',1) like ?3) and u.del = ?4")
	public Page<User> findUserByTeamIdSearch(List<TEAMREALTIONSHIP> typeIds,long teamId,String search,DELTYPE del,Pageable pageable);
	
	/**
	 * 根据小组id标识,及人员和团队的关系(0创建,1参与,2受邀)查询该小组下面的用户列表
	    * @Title: findUserListByGroupId
	    * @Description: 
	    * @param @param typeIds  如果是团队小组下面的已有用户用(0,1);如果是团队小组下面的受邀还没同意的用户用3
	    * @param @param teamId
	    * @param @return    参数
	    * @return List<User>    返回类型
		* @user wjj
		* @date 2015年8月11日 下午5:22:43
	    * @throws
	 */
	@Query(value="select u from User u where u.id in (select tm.userId from TeamMember tm where tm.type in ?1 and tm.groupId = ?2 and tm.teamId = ?3 and tm.del=?4) and u.del =?4")
	public Page<User> findUserListByGroupIdAndTeamId(List<TEAMREALTIONSHIP> typeIds,long groupId,long teamId,DELTYPE del ,Pageable pagable);
	
	/**
	    * @Title: findByAccount
	    * @Description: 根据邮箱,删除状态账户来查询用户
	    * @param @param account
	    * @param @return    参数
	    * @return User    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午4:17:25
	    * @throws
	 */
	public User findByAccountAndDel(String account,DELTYPE del);

	/**
	 * 根据账户密码查询用户<br>
	 * 使用单点登录之前，临时采用的登录认证方案
	 * 
	 * @author yang.li
	 * @date 2015年8月20日
	 * 
	 * @param account
	 * @param password
	 * @param delType
	 * @return
	 */
	public List<User> findByAccountAndPasswordAndDel(String account, String password, DELTYPE delType);
	
	/**查询不在项目列表中,而且不在existUserIds中,但是在teamId下的用户列表
     * @Description: 
     * @param @param teamId
     * @param @param projectId
     * @param @param existUserIds
     * @param @param groupId  团队分组ID
     * @param @return 
     * @return List<User>    返回类型
	 * @user jingjian.wu
	 * @date 2015年8月20日 下午7:40:30
     * @throws
	 */
	@Query(value="select u from User u where u.id in (select userId from TeamMember where del=0 and teamId=?1 and type !=2) and u.id not in(select userId from ProjectMember where del=0 and projectId= ?2) and u.del = 0 and u.id not in(?3) and u.id in (select userId from TeamMember where del=0 and groupId=?4 and teamId=?1) and (u.userName like ?5 or substring_index(u.account,'@',1) like ?5 or substring_index(u.email,'@',1) like ?5  or u.pinYinName like ?5 or u.pinYinHeadChar like ?5)" )
	public List<User> findUserForAskUser(long teamId,long projectId,List<Long> existUserIds,Long groupId,String search);
	
	
	@Query(value = "select u from User u where u.del = ?1 and u.status = ?2 and (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or substring_index(u.email,'@',1) like ?3)")
	public List<User> findByDelAndStatusAndKeyWords(DELTYPE del,USER_STATUS status,String keyWords);
	
	@Query(value = "select u from User u where u.del = ?1 and u.status = ?2 and (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or substring_index(u.email,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) ")
	public Page<User> findByDelAndStatusAndKeyWords(DELTYPE del,USER_STATUS status,String keyWords,Pageable pageable);
	
	@Query(value = "select u from User u where u.del = ?1 and u.status = ?2 and (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or substring_index(u.email,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) and u.id not in (select pm.userId from ProjectMember pm where projectId = ?4 and pm.del=?1)")
	public Page<User> findByDelAndStatusAndKeyWordsAndProject(DELTYPE del,USER_STATUS status,String keyWords,Long queryId,Pageable pageable);
	@Query(value = "select u from User u where u.del = ?1 and u.filialeId =?5 and u.status = ?2 and (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or substring_index(u.email,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) and u.id not in (select pm.userId from ProjectMember pm where projectId = ?4 and pm.del=?1)")
	public Page<User> findByDelAndStatusAndKeyWordsAndProjectAndFilialeId(DELTYPE del,USER_STATUS status,String keyWords,Long queryId,Pageable pageable,Long filialeId);
	
	@Query(value = "select u from User u where u.del = ?1 and u.status = ?2 and (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or substring_index(u.email,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) and u.id not in (select tm.userId from TeamMember tm where teamId = ?4 and tm.del=?1)")
	public Page<User> findByDelAndStatusAndKeyWordsAndTeam(DELTYPE del,USER_STATUS status,String keyWords,Long queryId,Pageable pageable);
	
	/**
	 * 获取某个团队下的正式成员和团队下的项目中的正式人员
	 * @user jingjian.wu
	 * @date 2015年10月23日 上午10:25:46
	 */
	@Query(nativeQuery = true,value= "select * from T_USER where id in ( "
			+ " select * from ( "
			+ " select userid from T_TEAM_MEMBER where type in (0,1) and teamId=?1 and del=0 "
			+ " union "
			+ " select userId from T_PROJECT_MEMBER where projectId in (select id from T_PROJECT where teamId =?1 and del=0 ) and type in (0,1) and del=0 "
			+ ") as t"
			+ ") ")
	public List<User> findUserBelongTeam(long teamId);
	
	public List<User> findByIdIn(List<Long> userId);

	public List<User> findByStatusAndDel(USER_STATUS normal, DELTYPE normal2);
	
	

	/**
	 * 根据项目ID找到项目的创建者(原先最后又and u.status=0   因为创建者即使被删掉也应该显示,所以去掉了这个条件)
	 * @user jingjian.wu
	 * @date 2016年2月27日 下午5:24:23
	 */
	@Query("select u from User u where u.id in "
			+ " ( "
			+ " select distinct userId from ProjectMember where type=0 and projectId in (?1)  and del=0 "
			+ " ) "
			+ " and u.del = 0  ")
	public List<User> findCreatorForProjects(List<Long> projectIds);
	
	/**
	 * 根据团队id列表找到团队的创建者/或者是参与者
	 * @user jingjian.wu
	 * @date 2016年2月29日 下午2:26:41
	 */
	@Query(value="select u from User u where u.id in (select tm.userId from TeamMember tm where tm.type = ?1 and tm.teamId in ?2  and tm.del = ?3 ) and u.del = ?3")
	public List<User> findUserListByTeamIdsAndTeamRelation(TEAMREALTIONSHIP relationShiptype,List<Long> teamIds,DELTYPE del);

	/**
	 * 根据团队id列表找到团队的创建者/或者是参与者(和上面方法一样,只是增加了模糊查询)
	 * @user jingjian.wu
	 * @date 2016年2月29日 下午2:26:41
	 */
	@Query(value="select u from User u where   (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or substring_index(u.email,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) and u.id in (select tm.userId from TeamMember tm where tm.type = ?1 and tm.teamId in ?2 and tm.del = ?4  ) and u.del = ?4 ")

	public List<User> findUserListByTeamIdsAndTeamRelationAndUserLike(TEAMREALTIONSHIP relationShiptype,List<Long> teamIds,String keyWord,DELTYPE del);
	
	/**
	 * 获取某个人创建的/管理的所有团队下的正式成员
	 * 此接口提供给正益工场使用
	 * @user jingjian.wu
	 * @date 2015年10月23日 上午10:25:46
	 */
	@Query(nativeQuery = true,value= "select * from T_USER where id in ( "
			+ " select userId from T_TEAM_MEMBER where teamId in ( "
			+ " select teamId from T_TEAM_MEMBER where userId= ?1 and del=0 and id in ("
			+ " select memberId from T_TEAM_AUTH where roleId in ?2 and del=0 ) "
			+ ") and del=0 and type in (0,1) ) and del=0 and ( userName like ?3 or substring_index(account,'@',1) like ?3)")
	public List<User> findUsersBelongSomeOneMgrTeam(Long userId,List<Long> roleIds,String keyWords);
	
	
	
	/**
	 * 查询协同中的所有人,根据关键字模糊查询(只返回前10个)
	 * @user jingjian.wu
	 * @date 2016年3月7日 下午5:49:58
	 */
	@Query(nativeQuery = true ,value="select t.* from T_USER t where t.status = ?1 and t.del=?2 and (t.pinYinName like ?3  or t.pinYinHeadChar like ?3 or t.userName like ?3 or substring_index(t.account,'@',1) like ?3 or t.email = substring(substring_index(?3,'%',2),2)) limit 10")
	public List<User> findByStatusAndDelAndKeyWordLike(long status, long del,String keyWords);
	
	/**
	 * 查询某个项目下的人(只返回前10个)
	 * @user jingjian.wu
	 * @date 2016年3月7日 下午5:49:41
	 */
	@Query(nativeQuery=true, value="select u.* from T_USER u where u.id in "
			+ " ( "
			+ " select distinct userId from T_PROJECT_MEMBER where type in (0,1) and projectId =?1  and del=0 "
			+ " ) "
			+ " and u.del = 0 and u.status=0  and (u.pinYinName like ?2  or u.pinYinHeadChar like ?2 or u.userName like ?2 or substring_index(u.account,'@',1) like ?2 or u.email=substring(substring_index(?2,'%',2),2)) limit 10")
	public List<User> findUserForProject(Long projectId,String keyWords);
	
	/**
	 * 根据团队id、项目id、分组id及已经存在的用户id判断获取用户数量
	 * @param teamId
	 * @param projectId
	 * @param existUserIds
	 * @param groupId
	 * @user  tingwei.yuan
	 * @date 2016年3月25日 下午5:49:41
	 * @return
	 */
	@Query(value="	SELECT count(u.id) FROM User u WHERE u.id IN (SELECT userId FROM TeamMember WHERE del =0 AND teamId = ?1 AND TYPE !=2 AND groupId=?2) AND u.id NOT IN (SELECT userId FROM ProjectMember  WHERE del = 0  AND projectId = ?3)  AND u.del = 0 AND u.id NOT IN (?4) and u.status=0 ")
	public int findUserCount(long teamId,long groupId,long projectId,List<Long> existUserIds);
	
	/**
	 * 根据团队id、项目id、分组id及已经存在的用户id判断获取分组下的用户信息列表
	 * @param teamId
	 * @param groupId
	 * @param projectId
	 * @param existsUserIds
	 * @user  tingwei.yuan
	 * @date 2016年3月28日 上午10:20:36
	 * @return
	 */
	@Query(value="	SELECT u FROM User u WHERE u.id IN (SELECT userId FROM TeamMember WHERE del =0 AND teamId = ?1 AND TYPE !=2 AND groupId=?2) AND u.id NOT IN (SELECT userId FROM ProjectMember  WHERE del = 0  AND projectId = ?3)  AND u.del = 0 AND u.id NOT IN (?4)  and u.status=0")
	public List<User> findUserGroupList(long teamId,long groupId,long projectId,List<Long> existsUserIds);

	/**
	 * 根据团队id、项目id、分组id及已经存在的用户id判断获取用户数量
	 * @param teamId
	 * @param projectId
	 * @param existUserIds
	 * @param groupId
	 * @user  tingwei.yuan
	 * @date 2016年3月25日 下午5:49:41
	 * @return
	 */
	@Query(value="	SELECT count(u.id) FROM User u WHERE u.id IN (SELECT userId FROM TeamMember WHERE del =0 AND teamId = ?1 AND TYPE !=2 AND groupId=?2) AND u.id NOT IN (SELECT userId FROM ProjectMember  WHERE del = 0  AND projectId = ?3)  AND u.del = 0 AND u.id NOT IN (?4) and (u.userName like ?5 or substring_index(u.account,'@',1) like ?5 or substring_index(u.email,'@',1) like ?5  or u.pinYinName like ?5 or u.pinYinHeadChar like ?5) and u.status=0")
	public int findUserCount(long teamId,long groupId,long projectId,List<Long> existUserIds,String search);
	
	/**
	 * 根据团队id、项目id、分组id及已经存在的用户id判断获取分组下的用户信息列表
	 * @param teamId
	 * @param groupId
	 * @param projectId
	 * @param existsUserIds
	 * @user  tingwei.yuan
	 * @date 2016年3月28日 上午10:20:36
	 * @return
	 */
	@Query(value="	SELECT u FROM User u WHERE u.id IN (SELECT userId FROM TeamMember WHERE del =0 AND teamId = ?1 AND TYPE !=2 AND groupId=?2) AND u.id NOT IN (SELECT userId FROM ProjectMember  WHERE del = 0  AND projectId = ?3)  AND u.del = 0 AND u.id NOT IN (?4) and (u.userName like ?5 or substring_index(u.account,'@',1) like ?5 or substring_index(u.email,'@',1) like ?5  or u.pinYinName like ?5 or u.pinYinHeadChar like ?5) and u.status=0")
	public List<User> findUserGroupList(long teamId,long groupId,long projectId,List<Long> existsUserIds,String search);
	
	/**
	 * 从项目添加成员根据团队id、项目id、分组id及已经存在的用户id判断获取分组下的用户信息列表，做返回结果限制，最多显示10行
	 * @param teamId
	 * @param projectId
	 * @param existUserIds
	 * @param groupId
	 * @param search
	 * @user  tingwei.yuan
	 * @date 2016年3月28日 上午10:20:36
	 * @return
	 */
	@Query(nativeQuery=true,value="SELECT u.* FROM T_USER u WHERE u.id IN (SELECT userId FROM T_TEAM_MEMBER WHERE del=0 AND teamId=?1 AND TYPE !=2) AND u.id NOT IN(SELECT userId FROM T_PROJECT_MEMBER WHERE del=0 AND projectId= ?2) AND u.del = 0 AND u.id NOT IN(?3) AND u.id IN (SELECT userId FROM T_TEAM_MEMBER WHERE del=0 AND groupId=?4 AND teamId=?1) AND (u.userName LIKE ?5 OR substring_index(u.account,'@',1) LIKE ?5 OR substring_index(u.email,'@',1) LIKE ?5  OR u.pinYinName LIKE ?5 OR u.pinYinHeadChar LIKE ?5) LIMIT 0,10")
	public List<User> findUserForAskUserLimit(long teamId,long projectId,List<Long> existUserIds,Long groupId,String search);

	public User findByIdAndDel(long managerId, DELTYPE normal);
	
	@Query(nativeQuery = true,value= "select * from T_USER where id in (  select userId from T_PROJECT_MEMBER where projectId=?1 and type in (0,1) and del=0 ) ")
	public List<User> findUserBelongProject(Long projectId);

	public User findByEmailAndDel(String email, DELTYPE normal);
	@Query(value = "select u from User u where u.del = ?1 and u.filialeId =?5 and u.status = ?2 and (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or substring_index(u.email,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) and u.id not in (select tm.userId from TeamMember tm where teamId = ?4 and tm.del=?1)")
	public Page<User> findByDelAndStatusAndKeyWordsAndTeamAndfilialeId(
			DELTYPE del, USER_STATUS status, String string, Long queryId,
			Pageable pageable,Long filialeId);

}
