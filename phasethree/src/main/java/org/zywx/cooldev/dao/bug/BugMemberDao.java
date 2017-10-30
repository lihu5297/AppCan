package org.zywx.cooldev.dao.bug;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.BUG_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.bug.BugMember;
import org.zywx.cooldev.entity.task.TaskMember;

public interface BugMemberDao extends PagingAndSortingRepository<BugMember, Long> {

	List<BugMember> findByBugIdAndDel(Long id, DELTYPE normal);
	
	@Query(value="select p from BugMember p where p.id in(select ta.memberId from BugAuth ta where ta.roleId=?2 and ta.del=?3) and p.bugId=?1 and p.del=?3")
	BugMember findByBugIdAndRoleIdAndDel(long bugId, Long roleId,
			DELTYPE normal);

	BugMember findByBugIdAndUserIdAndDel(long bugId, long assignedPersonUserId,
			DELTYPE normal);
	BugMember findByBugIdAndTypeAndDel(long bugId, BUG_MEMBER_TYPE creator,
			DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_BUG_MEMBER where del=?1 and bugId in (select id from T_BUG where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2))")
	List<BugMember> getBugMemberByProjectId(int ordinal, long projectId);


}
