package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.task.TaskAuth;

public interface TaskAuthDao extends PagingAndSortingRepository<TaskAuth, Long> {
	
	List<TaskAuth> findByDel(DELTYPE delType);
	
	List<TaskAuth> findByMemberIdAndDel(long memberId, DELTYPE delType);

	List<Long> findRoleIdByMemberIdAndDel(long memberId, DELTYPE delType);

	/**
	 * @user jingjian.wu
	 * @date 2015年11月3日 上午11:00:38
	 */
	    
	List<TaskAuth>  findByRoleIdInAndDel(List<Long> roleIds, DELTYPE normal);
    @Query(nativeQuery=true,value="select * from T_TASK_AUTH where del=?1 and memberId in (select id from T_TASK_MEMBER where del=?1 and taskId in (select id from T_TASK where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2)))")
	List<TaskAuth> getTaskAuthByProjectId(int ordinal, long projectId);

}
