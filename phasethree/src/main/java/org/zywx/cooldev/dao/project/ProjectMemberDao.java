package org.zywx.cooldev.dao.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.USER_STATUS;
import org.zywx.cooldev.entity.project.ProjectMember;

public interface ProjectMemberDao extends PagingAndSortingRepository<ProjectMember, Long> {

	List<ProjectMember> findByProjectIdAndUserIdAndDel(long projectId, long userId, DELTYPE delType);

	Page<ProjectMember> findByProjectIdAndDel(long projectId, DELTYPE delType,Pageable pageable);
	
	@Query(value="select pm from ProjectMember pm where pm.projectId = ?1 and pm.del =?2 and pm.userId in (select u.id from User u where u.userName like ?3 and u.account like ?4 and u.del =?2)")
	Page<ProjectMember> findByProjectIdAndDel(long projectId, DELTYPE delType,String queryName,String queryAccount,Pageable pageable);
	
	@Query(value="select pm from ProjectMember pm where pm.projectId = ?1 and pm.del =?2 and pm.userId in (select u.id from User u where (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) and u.del =?2 and u.status in (?4))")
	Page<ProjectMember> findByProjectIdAndDel1(long projectId, DELTYPE delType,String keyWords,List<USER_STATUS> status,Pageable pageable);
	
	@Query(value="select pm from ProjectMember pm where pm.userId not in (?4) and  pm.projectId = ?1 and pm.del =?2 and pm.userId in (select u.id from User u where (u.userName like ?3 or substring_index(u.account,'@',1) like ?3 or u.pinYinName like ?3 or u.pinYinHeadChar like ?3) and u.del =?2 and u.status in (?5))")
	Page<ProjectMember> findByProjectIdAndDelAndUserIdNotIn(long projectId, DELTYPE delType,String keyWords,List<Long> exceptUserIds,List<USER_STATUS> status,Pageable pageable);
	
	
	List<ProjectMember> findByProjectIdAndDel(long projectId, DELTYPE delType);
	
	List<ProjectMember> findByProjectIdAndTypeInAndDel(long projectId, List<PROJECT_MEMBER_TYPE> types, DELTYPE delType);
	
	Page<ProjectMember> findByProjectIdAndTypeInAndDel(long projectId, List<PROJECT_MEMBER_TYPE> types, DELTYPE delType,Pageable pageable);
	
	@Query(value="select pm from ProjectMember pm where pm.projectId = ?1 and type in (?2) and pm.del =?3 and pm.userId in (select u.id from User u where u.userName like ?4 and u.account like ?5 and u.del =?3)")
	Page<ProjectMember> findByProjectIdAndTypeInAndDel(long projectId, List<PROJECT_MEMBER_TYPE> types, DELTYPE delType,String queryName,String queryAccount,Pageable pageable);
	
	@Query(value="select pm from ProjectMember pm where pm.projectId = ?1 and type in (?2) and pm.del =?3 and pm.userId in (select u.id from User u where (u.userName like ?4 or substring_index(u.account,'@',1) like ?4 or u.pinYinName like ?4 or u.pinYinHeadChar like ?4) and u.del =?3 and u.status in (?5))")
	Page<ProjectMember> findByProjectIdAndTypeInAndDel(long projectId, List<PROJECT_MEMBER_TYPE> types, DELTYPE delType,String keyWords,List<USER_STATUS> status,Pageable pageable);

	@Query(value="select pm from ProjectMember pm where pm.userId not in (?5) and  pm.projectId = ?1 and type in (?2) and pm.del =?3 and pm.userId in (select u.id from User u where (u.userName like ?4 or substring_index(u.account,'@',1) like ?4 or u.pinYinName like ?4 or u.pinYinHeadChar like ?4) and u.del =?3 and u.status in (?6))")
	Page<ProjectMember> findByProjectIdAndTypeInAndDelAndUserIdNotIn(long projectId, List<PROJECT_MEMBER_TYPE> types, DELTYPE delType,String keyWords,List<Long> excepUserIds,List<USER_STATUS> status,Pageable pageable);
	
	List<ProjectMember> findByUserIdAndDel(long userId, DELTYPE delType);

	ProjectMember findOneByIdAndDel(long id, DELTYPE delType);
	
	ProjectMember findOneByProjectIdAndUserIdAndDel(long projectId, long userId, DELTYPE delType);

	@Query("select pm.userId from ProjectMember pm where pm.projectId = ?1 and pm.userId <> ?2 and pm.del = ?3")
	List<Long> findByProjectIdAndUserIdNotAndDel(Long projectId, Long userId, DELTYPE normal);

	ProjectMember findByProjectIdAndTypeAndDel(Long projectId, PROJECT_MEMBER_TYPE creator,DELTYPE normal);

	@Query(nativeQuery=true,value="select pm.* from T_PROJECT_MEMBER pm left join T_PROJECT_AUTH pa on pm.id = pa.memberId left join T_ROLE r on r.id = pa.roleId where pm.projectId = ? and pm.type = ? and r.enName = ? ")
	List<ProjectMember> findByProjectIdAndType(Long projectId, int participator, String manager);

	List<ProjectMember> findByUserIdAndProjectIdAndDel(Long loginUserId, long projectId, DELTYPE normal);
	
	/**
	 * 根据类型(创建者,参与者,邀请)获取项目下的成员的用户id集合
	 * @user jingjian.wu
	 * @date 2015年10月23日 下午5:27:50
	 */
	@Query("select pm.userId from ProjectMember pm where pm.projectId = ?1 and pm.type in(?2)  and pm.del = 0")
	List<Long> findProjectOfficalUserId(Long projectId, List<PROJECT_MEMBER_TYPE> memberTypes);

	List<ProjectMember> findByUserIdAndTypeAndDel(long userId, PROJECT_MEMBER_TYPE invitee, DELTYPE normal);

	/**
	 * @user jingjian.wu
	 * @date 2015年11月3日 下午3:56:41
	 */
	    
	List<ProjectMember> findByUserIdAndType(Long loginUserId,
			PROJECT_MEMBER_TYPE creator);

	@Query("select distinct projectId from ProjectMember where userId=?1 and type in (?2) and del=0 ")
	List<Long> findByUserIdAndTypeIn(long loginUserId, List<PROJECT_MEMBER_TYPE> type);

	/**
	 * @user jingjian.wu
	 * @date 2015年12月23日 下午4:16:22
	 */
	    
	@Query("select pm  from ProjectMember pm where pm.id in ( select pa.memberId from ProjectAuth pa where pa.roleId=?1 and pa.del=?2 )  and pm.del=?2 ")
	List<ProjectMember> findByRoleIdAndDel(long roleId, DELTYPE normal);

	@Query("select p.teamId from Project p where p.id in (select pm.projectId from ProjectMember pm where pm.userId=?1 and pm.type in (?2) and pm.del = ?3 ) and p.del = ?3  ")
	List<Long> findByUserIdAndTypeInAndDel(Long loginUserId, List<PROJECT_MEMBER_TYPE> typeList1, DELTYPE normal);

	/**
	 * @user jingjian.wu
	 * @date 2015年12月23日 上午11:31:41
	 */
//	@Query(value="select * from ProjectMember where id in (select memberId from ProjectAuth where roleId =?2 and del =?3) and projectId=?1 and del=?3")
//	List<ProjectMember> findByProjectIdAndRoleIdAndDel(long projectId,
//			int roleId, DELTYPE normal);
	
	@Query("select distinct projectId from ProjectMember where type=?1 and projectId in (?2) and userid = ?3 and del=0 ")
	List<Long> findByUserIdAndTypeAndProjectIdIn(PROJECT_MEMBER_TYPE type,List<Long> projectIds,Long userId);
	/**查看用户是否已经是该项目的成员
	    * @Title: findByTeamIdAndUserIdAndDel
	    * @Description: 根据项目id,用户id查询记录， 
	    * @param @param projectId
	    * @param @param userId
	    * @param @return 参数
		* @user haijun.cheng
		* @date 2016年7月05日 
	 */
}
