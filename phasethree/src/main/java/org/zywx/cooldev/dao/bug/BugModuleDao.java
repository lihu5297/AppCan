package org.zywx.cooldev.dao.bug;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.bug.BugModule;

public interface BugModuleDao extends PagingAndSortingRepository<BugModule, Long> {

	BugModule findExitByProjectIdAndNameAndDel(long projectId, String name,
			DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_BUG_MODULE where del=?1 and projectId=?2")
	List<BugModule> getBugModuleByProjectId(
			int ordinal, long projectId);
	BugModule findByIdAndDel(long moduleId, DELTYPE normal);
	List<BugModule> findByDel(DELTYPE normal);
}
