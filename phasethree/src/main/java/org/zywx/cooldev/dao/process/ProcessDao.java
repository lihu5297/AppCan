package org.zywx.cooldev.dao.process;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.process.Process;

public interface ProcessDao extends PagingAndSortingRepository<Process, Long> {
	
	public List<Process> findByDel(DELTYPE delType);

	public List<Process> findByProjectIdInAndDel(List<Long> projectIdList, DELTYPE delType);
	
	@Query(value="select id from Process where projectId in (?1) and del = ?2")
	public List<Long> findByProjectIdAndDel(Set<Long> projectIdList, DELTYPE delType);

	public List<Process> findByProjectIdAndDel(long projectId, DELTYPE delType);
	
	public List<Process> findByProjectIdAndNameAndDel(long projectId,String name, DELTYPE delType);
	
	public List<Process> findByProjectIdAndNameLikeAndDel(long projectId,String name, DELTYPE delType);
	
//	@Query(value="select distinct name from Process where projectId in (?1) and del = ?2 and name like ?3")
//	public List<String> findByProjectIdInAndDelAndNameLike(List<Long> projectIdList, DELTYPE delType,String name);
	
	@Query(value="select p from Process p where p.projectId in (?1) and p.del = ?2 and p.name like ?3")
	public List<Process> findByProjectIdInAndDelAndNameLike(List<Long> projectIdList, DELTYPE delType,String name);
    @Query(nativeQuery=true,value="select cast(id as char) from T_PROCESS where del=?3 and id in (select processId from T_PROCESS_MEMBER where del=?3 and userId=?2) and projectId=?1")
	public List<String> getProcessByProjectIdAndUserIdAndDel(long projectId,
			long userId, int ordinal);
    @Query(value="select p from Process p where p.projectId in (?1) and p.del = ?5 and (p.name like ?2  or pinYinHeadChar like ?3 or pinYinName like ?4)")
	public List<Process> findByProjectIdAndNameLikeOrPinYinHeadCharLikeOrPinYinNameAndDel(
			long projectId, String name, String pinyinHeader, String pinyin,
			DELTYPE normal);

}
