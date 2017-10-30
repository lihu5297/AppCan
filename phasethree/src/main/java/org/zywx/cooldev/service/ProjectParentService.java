package org.zywx.cooldev.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.dao.project.ProjectParentDao;
import org.zywx.cooldev.entity.project.ProjectParent;
import org.zywx.cooldev.entity.project.ProjectSort;


@Service
public class ProjectParentService {

	@Autowired
	protected ProjectParentDao projectParentDao;
	/**
	 * 查询全部大项目
	 * @return
	 */
	public List<ProjectParent> findAll(){
		return projectParentDao.findByDelOrderByIdDesc(Enums.DELTYPE.NORMAL);
	}
	
	public List<ProjectParent> findByProjectCode(String proCode){
		return projectParentDao.findByProjectCodeAndDel(proCode,Enums.DELTYPE.NORMAL);
	}
	
	public List<ProjectParent> findByIdNotAndProjectCode(Long id, String proCode){
		return projectParentDao.findByIdNotAndProjectCodeAndDel(id,proCode,Enums.DELTYPE.NORMAL);
	}
	
	public ProjectParent findById(Long proId){
		if(proId != null && proId > 0){
			return projectParentDao.findOne(proId);
		}
		return null;
	}
	
	public ProjectParent saveUpdateProject(ProjectParent pro){
		if(pro != null){
			return projectParentDao.save(pro);
		}else{
			return null;
		}
	}
	
	public void delProject(ProjectParent pro){
		projectParentDao.delete(pro);
	}
	
	public void delProject(long proId){
		projectParentDao.delete(proId);
	}
	
}
