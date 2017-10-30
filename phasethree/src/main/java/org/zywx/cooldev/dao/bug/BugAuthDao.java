package org.zywx.cooldev.dao.bug;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.bug.BugAuth;

public interface BugAuthDao extends PagingAndSortingRepository<BugAuth, Long> {

	List<BugAuth> findByMemberIdAndDel(Long id, DELTYPE normal);
	@Query(nativeQuery=true,value="select * from T_BUG_AUTH where del=?1 and memberId in (select id from T_BUG_MEMBER where del=?1 and bugId in (select id from T_BUG where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2)))")
	List<BugAuth> getBugAuthByProjectId(int ordinal, long projectId);

}
