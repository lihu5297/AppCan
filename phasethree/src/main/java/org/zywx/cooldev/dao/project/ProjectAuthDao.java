package org.zywx.cooldev.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.project.ProjectAuth;

public interface ProjectAuthDao extends PagingAndSortingRepository<ProjectAuth, Long> {

	List<ProjectAuth> findByMemberIdAndDel(long memberId, DELTYPE delType);
	@Query(nativeQuery=true,value="select * from T_PROJECT_AUTH where del=?1 and memberId in (select id from T_PROJECT_MEMBER where del=?1 and projectId=?2)")
	List<ProjectAuth> getProjectAuthByProjectId(int normal,long projectId);
}
