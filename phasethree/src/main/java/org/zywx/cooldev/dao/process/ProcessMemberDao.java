package org.zywx.cooldev.dao.process;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.process.ProcessMember;
import org.zywx.cooldev.entity.project.ProjectAuth;

public interface ProcessMemberDao extends PagingAndSortingRepository<ProcessMember, Long> {

	long countByProcessIdAndDel(long processId, DELTYPE delType);
	
	List<ProcessMember> findByProcessIdAndDel(long processId, DELTYPE delType);
	
	List<ProcessMember> findByUserIdAndDel(long loginUserId, DELTYPE delType);

	List<ProcessMember> findByProcessIdAndUserIdAndDel(long processId, long userId, DELTYPE delType);

	@Query("select count(*) from Process p where p.projectId = ?1 and p.id in (select processId from ProcessMember pm where pm.userId = ?2 and pm.id in (select memberId from ProcessAuth pa where pa.roleId = ?3 and pa.del=?4) and pm.del=?5) and p.del=?6")
	long countByProjectIdAndUserIdAndDel(long projectId, long roleId,long loginUserId, DELTYPE normal,DELTYPE normal1,DELTYPE normal2);
	@Query(nativeQuery=true,value="select * from T_PROCESS_MEMBER where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2)")
	List<ProcessMember> getProcessMemberByProjectId(int normal,long projectId);
}
