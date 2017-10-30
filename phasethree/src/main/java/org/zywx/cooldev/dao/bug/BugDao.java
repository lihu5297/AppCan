package org.zywx.cooldev.dao.bug;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.bug.Bug;

public interface BugDao extends PagingAndSortingRepository<Bug, Long> {
	public Bug findByIdAndDel(long bugId,DELTYPE delType);
	
	public List<Bug> findByProcessIdAndDel(long processId,DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_BUG where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2)")
	public List<Bug> getBugByProjectId(int ordinal, long projectId);
    @Query(nativeQuery=true,value="select count(1) from T_BUG where del=?3 and id in (select bugId from T_BUG_MEMBER where del=?3 and userId=?2) and processId in (select id from T_PROCESS where del=?3 and projectId=?1)")
	public long countByProjectIdAndUserIdAndDel(long projectId, long userId,
			int ordinal);
    @Query(nativeQuery=true,value="select cast(id as char) from T_BUG where del=?3 and id in (select bugId from T_BUG_MEMBER where del=?3 and userId=?2) and processId in (select id from T_PROCESS where del=?3 and projectId=?1)")
	public List<String> getBugsByProjectIdAndUserIdAndDel(long projectId,
			long userId, int ordinal);
}
