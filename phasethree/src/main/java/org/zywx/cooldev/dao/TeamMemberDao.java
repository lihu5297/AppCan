package org.zywx.cooldev.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.TEAMTYPE;
import org.zywx.cooldev.entity.TeamMember;

public interface TeamMemberDao extends PagingAndSortingRepository<TeamMember, Long> {

	//获取某个团队下面,各个分组的人数   
	//select * from T_TEAM_MEMBER where teamId=? AND  del =0 and type in(1,2) GROUP BY groupId
	
	/**查看用户是否已经是该团队的成员
	 * 根据团队id,和用户id，获取在哪个分组
	    * @Title: findByTeamIdAndUserId
	    * @Description: 根据团队id,用户id查询记录， 
	    * @param @param teamId
	    * @param @param userId
	    * @param @return    参数
	    * @return TeamMember    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午7:15:06
	    * @throws
	 */
	public TeamMember findByTeamIdAndUserIdAndDel(long teamId,long userId,DELTYPE del);

	/**
	 * @param teamId
	 * @param normal
	 * @return List<TeamMember>
	 * @user jingjian.wu
	 * @date 2015年9月1日 下午8:41:40
	 * @throws
	 */
	    
	public List<TeamMember> findByTeamIdAndDel(long teamId, DELTYPE normal);
	
	@Query(value="select tm from TeamMember tm where userId in (select id from User u where u.userName like ?3 or u.pinYinName like ?3 or u.email like ?3) and teamid=?1 and del=?2")
	public List<TeamMember> findByTeamIdAndDelAndKeywords(long teamId, DELTYPE normal,String keywords);
	
	public List<TeamMember> findByUserIdAndDel(long userId, DELTYPE normal);
	
	public List<TeamMember> findByUserIdAndTeamIdAndDel(long userId,long teamId, DELTYPE normal);

	public List<TeamMember> findByTeamIdAndTypeAndDel(Long teamId, TEAMREALTIONSHIP create, DELTYPE normal);

	public List<TeamMember> findByUserIdAndTypeAndDel(long userId, TEAMREALTIONSHIP ask, DELTYPE normal);

	@Query(value="select teamId from TeamMember where id in (select memberId from TeamAuth where roleId in (?2) and del =?3) and userId=?1 and del=?3")
	public List<Long> findByUserIdAndRoleIdAndDel(Long loginUserId,List<Long> roleIds, DELTYPE normal);

	
	@Query(value="select tm from TeamMember tm where tm.id in (select ta.memberId from TeamAuth ta where ta.roleId =?1 and ta.del =?2)  and tm.del=?2")
	public List<TeamMember> findByRoleIdAndDel(long roleId, DELTYPE normal);

	@Query(value="select tm.teamId from TeamMember tm where tm.userId = ?1 and tm.id in(select memberId from TeamAuth where roleId in (select id from Role where enName in (?2) and del = ?3) and del = ?3)  and tm.del=?3")
	public List<Long> findByUserIdAndTypeInAndDel(Long loginUserId,List<String> typeList, DELTYPE normal);

}
