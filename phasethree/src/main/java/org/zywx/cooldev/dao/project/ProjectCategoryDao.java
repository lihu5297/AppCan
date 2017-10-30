package org.zywx.cooldev.dao.project;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.project.ProjectCategory;

/**
 * 
 * @author yang.li
 * @date 2015-08-10
 *
 */
public interface ProjectCategoryDao extends PagingAndSortingRepository<ProjectCategory, Long> {

	List<ProjectCategory> findByDel(DELTYPE delType);

	@Query(value="select pg from ProjectCategory pg where pg.id in (select p.categoryId from Project p where p.id in (?1) and p.del =?2 ) and pg.del= ?2")
	List<ProjectCategory> findByProjectIdsAndDel(List<Long> projectIds, DELTYPE normal);
	
	ProjectCategory findByNameAndDel(String name,DELTYPE del);

}
