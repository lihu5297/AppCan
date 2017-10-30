package org.zywx.cooldev.dao.process;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.process.ProcessAuth;

public interface ProcessAuthDao extends PagingAndSortingRepository<ProcessAuth, Long> {

	List<ProcessAuth> findByMemberIdAndDel(long memberId, DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_PROCESS_AUTH where del=?1 and memberId in (select id from T_PROCESS_MEMBER where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2))")
	List<ProcessAuth> getProcessAuthByProjectId(int normal,long projectId);

}
