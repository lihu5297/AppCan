package org.zywx.cooldev.dao.task;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.task.TaskGroup;

public interface TaskGroupDao extends PagingAndSortingRepository<TaskGroup, Long> {

	public TaskGroup findByProjectIdAndName(Long projectId,String name);
	
	public List<TaskGroup> findByProjectIdAndNameLikeAndDel(Long projectId,String name,DELTYPE delType);
	
	@Query(nativeQuery=false,value="select t.id from TaskGroup t where t.projectId=?1 and t.name like ?2 and t.del=?3")
	public List<Long> findIdsByProjectIdAndNameLikeAndDel(Long projectId,String name,DELTYPE delType);
	
	public List<TaskGroup> findByProjectIdAndDel(Long projectId,DELTYPE delType);
	
	public TaskGroup findByProjectIdAndSortAndDel(Long projectId,int sort,DELTYPE delType);
	
	@Query(nativeQuery=true,value="select g.* from (select * from T_TASK_GROUP where projectId=?1 and del=?3) as g "
			+" left join  "
			+" (select * from T_TASK_GROUP_SORT where userId=?2 and projectId=?1 and del=?3) as s on g.id = s.groupId "
			+" order by s.sort,g.sort ")
	public List<TaskGroup> getOrderedTaskGroupByProjectIdAndUserId(Long projectId,Long userId,int delType);
	
	
	@Query(nativeQuery=true,value="select IFNULL(MAX(sort),0) from T_TASK_GROUP where projectId=?1 and del=?2")
	public int findMaxSortByProjectAndDel(Long projectId,int delType);
    @Query(nativeQuery=true,value="select * from T_TASK_GROUP where del=?1 and projectId=?2")
	public List<TaskGroup> getTaskGroupByProjectId(int ordinal, long projectId);

	public TaskGroup findByProjectIdAndNameAndDel(long projectId, String name,
			DELTYPE normal);

	public List<TaskGroup> findByDel(DELTYPE normal);
	@Query(value="select tr from TaskGroup tr where tr.projectId =?1 and tr.del = ?4 and (tr.name like ?2 or tr.pinYinHeadChar like ?3 or tr.pinYinName like ?3)")
	public List<TaskGroup> findByProjectIdAndNameLikeOrPinYinAndDel(
			long projectId, String name, String pinyin, DELTYPE normal);
	
	
}
