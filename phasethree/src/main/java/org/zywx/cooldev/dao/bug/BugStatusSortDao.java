package org.zywx.cooldev.dao.bug;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.bug.BugStatusSort;

public interface  BugStatusSortDao  extends PagingAndSortingRepository<BugStatusSort, Long> {
	//@Query(value="select BSS from BugStatusSort BSS where userId=?1 and projectId=?2 and sort=?3 and del=?4")
	List<BugStatusSort> findByUserIdAndProjectIdAndSortAndDel(long loginUserId,
			long projectId, int sort, DELTYPE normal);

	List<BugStatusSort> findByUserIdAndProjectIdAndDel(Long loginUserId, long projectId,
			DELTYPE normal);

}
