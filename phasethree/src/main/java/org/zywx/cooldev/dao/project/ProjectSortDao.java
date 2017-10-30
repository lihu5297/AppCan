package org.zywx.cooldev.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.project.ProjectSort;

public interface ProjectSortDao extends PagingAndSortingRepository<ProjectSort,Long>{
	/**
	 * 根据用户id获取当前用户对项目进行排序信息列表
	 * @param loginUserId
	 * @param normal
	 * @return
	 */
	List<ProjectSort> findByUserIdAndDel(Long loginUserId, DELTYPE normal);
	
	/**
	 * 根据用户id、项目id获取当前用户对项目进行排序信息列表
	 * @param loginUserId
	 * @param projectId
	 * @param normal
	 * @return
	 */
	List<ProjectSort> findByUserIdAndProjectIdAndDel(Long loginUserId, Long projectId, DELTYPE normal);
	
}
