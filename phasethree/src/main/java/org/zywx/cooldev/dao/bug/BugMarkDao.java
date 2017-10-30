package org.zywx.cooldev.dao.bug;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.bug.BugMark;

public interface BugMarkDao extends PagingAndSortingRepository<BugMark, Long> {

	List<BugMark> findByBugIdAndDel(long bugId, DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_BUG_MARK where del=?1 and bugId in (select id from T_BUG where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2))")
	List<BugMark> getBugMarkByProjectId(int ordinal, long projectId);

}
