package org.zywx.cooldev.dao.project;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.entity.Team;
import org.zywx.cooldev.entity.project.Project;

public interface ProjectDao extends PagingAndSortingRepository<Project, Long>{
	
	List<Project> findByDel(Enums.DELTYPE delType);

	@Query(value="select p from Project p where "
			+ "p.id in (?1) "	// 用户相关的项目，具体可以为用户创建的，或用户参与的
			+ "and p.bizLicense in (?2) " // 项目授权
			+ "and p.categoryId in (?3) " // 项目分类
			+ "and p.status in (?4) "     // 项目状态
			+ "and p.type in (?5) "       // 项目类型
			+ "and p.del=?6 "	          // 删除标记
			+ "order by p.createdAt desc")
	Page<Project> findByProperties(List<Long> projectIds,
			List<PROJECT_BIZ_LICENSE> bizLicense,
			List<Long> categoryId,
			List<PROJECT_STATUS> status,
			List<PROJECT_TYPE> type,
			DELTYPE delType,Pageable pageable);
	
	@Query(value="select p from Project p where "
			+ "p.id in (?1) "	// 用户相关的项目，具体可以为用户创建的，或用户参与的
			+ "and p.bizLicense in (?2) " // 项目授权
			+ "and p.status in (?3) "     // 项目状态
			+ "and p.type in (?4) "       // 项目类型
			+ "and p.del=?5 "	          // 删除标记
			+ "order by p.createdAt desc")
	Page<Project> findByPropertiesWithoutCategoryId(List<Long> projectIds,
			List<PROJECT_BIZ_LICENSE> bizLicense,
			List<PROJECT_STATUS> status,
			List<PROJECT_TYPE> type,
			DELTYPE delType, Pageable pageable);
	
	/**
	    * @Title: findByTeamIdAndDel
	    * @Description: 查询团队下面的项目列表
	    * @param @param teamId
	    * @param @param delType
		* @user jingjian.wu
		* @date 2015年8月15日 下午8:02:32
	    * @throws
	 */
	List<Project> findByTeamIdAndDel(long teamId,Enums.DELTYPE delType);

	Page<Project> findByTeamIdAndDel(long teamId,Enums.DELTYPE delType,Pageable pageable);

	@Query(value="select p from Project p where p.id in (select pm.projectId from ProjectMember pm where type in (?3) and pm.del =?2 and pm.userId = ?4) and p.teamId =?1 and p.del=?2")
	Page<Project> findByTeamIdAndDel(long teamId,Enums.DELTYPE delType,List<PROJECT_MEMBER_TYPE> types,long loginUserId,Pageable pageable);

	Page<Project> findByNameLikeAndDelOrderByCreatedAtDesc(String query,Enums.DELTYPE delType, Pageable pageable);
	
	List<Project> findByNameLikeAndDelOrderByCreatedAtDesc(String query, DELTYPE normal);

	@Query(value="select p from Project p where p.id in (?1) and p.del = ?2 order by p.createdAt desc")
	List<Project> findByIdInAndDelOrderByCreatedAtDesc(Set<Long> proIds, DELTYPE normal);
	
	@Query(value="select p from Project p where p.id in (?1) and p.del = ?2 and (name like ?3 or pinYinHeadChar like ?4 or pinYinName like ?4) order by p.createdAt desc")
	List<Project> findByIdInAndDelAndNameLikeOrderByCreatedAtDesc(Set<Long> proIds, DELTYPE normal,String name,String pinYin );

	@Query(nativeQuery=true,value="SELECT p.* FROM T_PROJECT p WHERE "
			+ "p.id IN (SELECT projectId FROM T_PROJECT_MEMBER WHERE type IN (?) AND userId = ?) "	// 用户相关的项目，具体可以为用户创建的，或用户参与的			
			+ "OR p.id IN (SELECT id FROM T_PROJECT	WHERE teamId IN (SELECT teamId FROM T_TEAM_MEMBER WHERE id IN (	SELECT memberId FROM T_TEAM_AUTH WHERE roleId IN (?)) AND type IN (?))) " // 团队项目
			+ "AND p.bizLicense IN (?)  " // 项目授权
			+ "AND p.status IN (?) "     // 项目状态
			+ "AND p.type IN (?) "       // 项目类型
			+ "AND p.del =? "	          // 删除标记
			+ "ORDER BY p.createdAt DESC limit ?,?")
	List<Project> findByPropertiesWithoutCategoryIdAndPage(String memberType,long loginUserId,String roleIds,String types,
			String bizLicense, String status, String type, int normal,int pageStart,int pageSize);

	@Query(value="select id from Project where teamId in (?1) and del = ?2")
	List<Long> findByTeamIdInAndDel(List<Long> teamIds, DELTYPE normal);
	
	@Query(value="select id from Project where id in (?1) and del = ?3 and teamId in( select id from Team where del=0 and name like ?2)")
	List<Long> findByIdInAndTeamNameLikeAndDel(List<Long> prjIds,String teamName, DELTYPE normal);

	@Query(value="select id from Project where teamId = ?1 and del = ?2 ")
	List<Long> findLongByTeamIdAndDel(long teamId,Enums.DELTYPE delType);

	@Query(value="select id from Project where teamId in(select id from Team where id = ?2 and id in (select teamId from TeamMember where id in (select memberId from TeamAuth where roleId in (?3) and del =?4) and userId=?1 and del=?4))")
	List<Long> findLongByTeamIdAndRoleIdAndDel(Long loginUserId, Long teamId, List<Long> roleIds, DELTYPE normal);

	@Query(value="select p.id from Project p where p.teamId = ?1 and p.id in (select projectId from ProjectMember where type in (?3) and userId = ?2 and del = ?4 ) and p.del = ?4")
	List<Long> findByTeamAndUserIdAndTypeAndDel(Long teamId, long loginUserId, List<PROJECT_MEMBER_TYPE> memberType,DELTYPE normal);
	
	/*
	//2016
	//项目查询  增加了项目名称模糊查询
	@Query(value="select p from Project p where "
			+ "p.id in (?1) "	// 用户相关的项目，具体可以为用户创建的，或用户参与的
			+ "and p.bizLicense in (?2) " // 项目授权
			+ "and p.status in (?3) "     // 项目状态
			+ "and p.type in (?4) "       // 项目类型
			+ "and p.del=?5 "	          // 删除标记
			+ " and p.name like '%?6%' "
			+ "order by p.createdAt desc")
	Page<Project> findByPropertiesWithoutCategoryIdAndProjName(List<Long> projectIds,
			List<PROJECT_BIZ_LICENSE> bizLicense,
			List<PROJECT_STATUS> status,
			List<PROJECT_TYPE> type,
			DELTYPE delType,String projName, Pageable pageable);
	
	//项目列表查询  增加了项目创建时间查询
	@Query(value="select p from Project p where "
			+ "p.id in (?1) "	// 用户相关的项目，具体可以为用户创建的，或用户参与的
			+ "and p.bizLicense in (?2) " // 项目授权
			+ "and p.status in (?3) "     // 项目状态
			+ "and p.type in (?4) "       // 项目类型
			+ "and p.del=?5 "	          // 删除标记
			+ " and p.createdAt >= ?6 and p.createdAt <=?7 "
			+ "order by p.createdAt desc")
	Page<Project> findByPropertiesWithoutCategoryIdAndCreateAt(List<Long> projectIds,
			List<PROJECT_BIZ_LICENSE> bizLicense,
			List<PROJECT_STATUS> status,
			List<PROJECT_TYPE> type,
			DELTYPE delType,String begin,String end, Pageable pageable);
	
	@Query(value="select p from Project p where "
			+ "p.id in (?1) "	// 用户相关的项目，具体可以为用户创建的，或用户参与的
			+ "and p.bizLicense in (?2) " // 项目授权
			+ "and p.status in (?3) "     // 项目状态
			+ "and p.type in (?4) "       // 项目类型
			+ "and p.del=?5 "	          // 删除标记
			+ " and p.name like '%?6%' "
			+ " and p.createdAt >= ?7 and p.createdAt <=?8 "
			+ "order by p.createdAt desc")
	Page<Project> findByPropertiesWithoutCategoryIdAndProjNameAndCreateAt(List<Long> projectIds,
			List<PROJECT_BIZ_LICENSE> bizLicense,
			List<PROJECT_STATUS> status,
			List<PROJECT_TYPE> type,
			DELTYPE delType,String projName,String begin,String end, Pageable pageable);
	*/
	
	//找出某个人创建的所有项目
	@Query(value="select distinct pm.projectId from ProjectMember pm where pm.type=?1 and pm.del=?2 and pm.userId = (select u.id from User u where u.account=?3)")
	List<Long> findLongByCreatorAndDel(PROJECT_MEMBER_TYPE prjMemberType, DELTYPE normal,String account);

	@Query(value="select distinct p.id from Project p where teamId in (select t.id from Team t where t.name like ?1 and del = ?2) and del = ?2")
	List<Long> findByTeamNameAndDel(String teamName, DELTYPE normal);
	
	@Query(value="select p.id from  Project p where p.id in (?1) and p.name like ?2 and del=?3")
	List<Long> findByIdInAndNameLikeAndDel(List<Long> projIds,String projName,DELTYPE delTpe);

	Project findByTeamIdAndIdAndDel(long teamId, long projectId, DELTYPE normal);

	Project findByIdAndDel(long projectId, DELTYPE normal);
	
	public Project findByUuidAndDel(String uuid,DELTYPE delType);
	
	public List<Project> findByParentIdAndDel(Long parentId, DELTYPE delType);
}
