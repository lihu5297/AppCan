package org.zywx.cooldev.dao.project;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.project.ProjectParent;

public interface ProjectParentDao extends PagingAndSortingRepository<ProjectParent, Long>{
	
	List<ProjectParent> findByDelOrderByIdDesc(Enums.DELTYPE delType);
	
	ProjectParent findByIdAndDel(long projectId, DELTYPE normal);
	//通过用户id获取记录
	List<ProjectParent> findByUserIdAndDel(long userId,Enums.DELTYPE delType);
	
	List<ProjectParent> findByProjectCodeAndDel(String projectCode,Enums.DELTYPE delType);
	
	List<ProjectParent> findByIdNotAndProjectCodeAndDel(Long id, String projectCode,Enums.DELTYPE delType);
	
	//通过code和用户id获取记录
	List<ProjectParent> findByProjectCodeAndUserIdAndDel(String projectCode,long userId,Enums.DELTYPE delType);
	
}
