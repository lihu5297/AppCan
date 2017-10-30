package org.zywx.cooldev.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.entity.Team;


public interface TeamDao extends PagingAndSortingRepository<Team, Long> {

	/**
	 * 
	    * @Title: findByTypeAndIdInOrderByCreatedAtDesc
	    * @Description: 根据项目类型(个人项目,团队项目)及某人创建/某人参与. 查找对应的团队信息,并且按创建时间倒叙排列 
	    * @param @param type 0我创建 1我参与
	    * @param @param ids   0个人团队  1项目团队
	    * @param @return    参数
	    * @return List<Team>    返回类型
	    * @throws
	 */
//	@Query(nativeQuery=true,value="select * from T_TEAM where id in (select teamId from T_TEAM_MEMBER where type in (?1) and userId = ?2) and type in (?3) and del=?4 order by createdAt desc")
	@Query(value="select t from Team as t where t.id in (select teamId from TeamMember where type in (?1) and userId = ?2 and del=0) and t.type in (?3) and t.del=?4 order by t.createdAt desc")
	public List<Team> findByTypeAndIdInOrderByCreatedAtDesc(List<TEAMREALTIONSHIP> rel,long userId,List<TEAMTYPE> type,DELTYPE del);
	
//	@Query(value="select t from Team as t where t.id in (select teamId from TeamMember where type in (?1) and userId = ?2 and del=0) and t.type in (?3) and t.del=?4 order by t.createdAt desc")
//	public Page<Team> findByTypeAndIdInOrderByCreatedAtDesc(List<TEAMREALTIONSHIP> rel,long userId,List<TEAMTYPE> type,Pageable pageable,DELTYPE del);
	
	
	/**
	 * 根据团队名称,创建者,参与者,创建时间,我创建/我参与,普通团队/企业团队,查询团队列表
	 * @user jingjian.wu
	 * @date 2016年2月29日 下午8:25:08
	 */
	/*@Query(value="select t from Team as t where t.id in (select teamId from TeamMember where type in (?1) and userId = ?2 and del=0) and t.type in (?3) and t.del=?4 "
			+ " and t.name like ?5 "
			+ " and t.id in ( select teamId from TeamMember where type =0 and userId in( select id from User where userName like ?6) and del=0 ) "//创建者
			+ " and t.id in ( select teamId from TeamMember where type =1 and userId in( select id from User where userName like ?7) and del=0 ) "//参与者
			+ " and t.createdAt between ?8 and ?9 "
			+ "order by t.createdAt desc")
	public Page<Team> findTeamList(List<TEAMREALTIONSHIP> rel,long userId,List<TEAMTYPE> type,DELTYPE del,String teamName,String creator,String actor,Date begin,Date end,Pageable pageable);
	*/
	/**
	    * @Description:根据用户ID 和团队创建/管理员的角色ID 获取 用户创建和管理的团队列表 
	    * @param @param userId
	    * @param @param roleId
	    * @param @return 
	    * @return List<Team>    返回类型
		* @user jingjian.wu
		* @date 2015年8月19日 下午3:58:58
	    * @throws
	 */
	@Query(value="select t from Team t where t.id in ( "+" select teamId from TeamMember where userId = ?1 and id in( "+" select memberId from TeamAuth where roleId in(?2)  and del = 0 "+") and del = 0 "+" ) and del = 0")
	public List<Team> findMgrCrtTeamList(long userId,List<Long> roleId);

	public Page<Team> findByNameLikeAndDelOrderByCreatedAtDesc(String query, DELTYPE normal,  Pageable pageable);

	public List<Team> findByNameLikeAndDelOrderByCreatedAtDesc(String query, DELTYPE normal);

	/**
	 * @user jingjian.wu
	 * @date 2015年10月19日 下午5:05:24
	 */
	    
	public Page<Team> findByTypeOrEnterpriseIdIsNotNull(TEAMTYPE enterprise,Pageable pageable);
	
	public Team findByUuidAndDel(String uuid,DELTYPE delType);

	public List<Team> findByIdInAndDelAndNameLike(List<Long> set,DELTYPE deltype,String teamName);
	
	public List<Team> findByIdInAndDel(List<Long> set,DELTYPE deltype);
	
	public List<Team> findByIdInAndDelAndNameLikeAndCreatedAtBetween(List<Long> set,DELTYPE deltype,String teamName,Date begin,Date end);
	
	@Query(value="select id from Team where id in (?1) and name like ?2 and del=?3")
	public List<Long> findByIdInAndNameLikeAndDel(List<Long> set,String teamName,DELTYPE deltype);

	public List<Team> findByDel(DELTYPE normal);
	@Query(value="select t from Team t where id in (?1) and del=?2 and (name like ?3 or pinYinHeadChar like ?4 or pinYinName like ?5)")
	public List<Team> findByIdInAndDelAndNameLikeAndPinYinHeadCharLikeAndPinYinName(
			List<Long> teamIds, DELTYPE normal, String name, String headCharName,
			String pinyinName);
}
