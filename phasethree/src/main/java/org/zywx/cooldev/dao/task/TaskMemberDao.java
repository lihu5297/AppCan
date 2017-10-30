package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;
import org.zywx.cooldev.entity.task.TaskMember;

public interface TaskMemberDao extends PagingAndSortingRepository<TaskMember, Long> {
	
	List<TaskMember> findByTaskIdAndDel(long taskId, DELTYPE delType);
	
	List<TaskMember> findByUserIdAndDel(long userId, DELTYPE delType);

	List<TaskMember> findByUserIdAndTypeInAndDel(long userId, List<TASK_MEMBER_TYPE> typeList, DELTYPE delType);

	@Query(value="select p from TaskMember p where p.id in(select ta.memberId from TaskAuth ta where ta.roleId=?2 and ta.del=?3) and p.taskId=?1 and p.del=?3")
	TaskMember findByTaskIdAndRoleIdAndDel(long taskId, Long roleId, DELTYPE normal);
	
	TaskMember findByTaskIdAndUserIdAndDel(long taskId,long userId,DELTYPE delType);
    @Query(nativeQuery=true,value="select * from T_TASK_MEMBER where del=?1 and taskId in (select id from T_TASK where del=?1 and processId in (select id from T_PROCESS where del=?1 and projectId=?2))")
	List<TaskMember> getTaskMemberByProjectId(int ordinal, long projectId);
    
    @Query(value="select p from TaskMember p where   p.taskId=?1 and p.userId = ?2 and  p.del=?3")
    List<TaskMember> findRepeatTaskIdAndUserIdAndDel(long taskId,long userId,DELTYPE delType);
	
}
