package org.zywx.cooldev.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.project.ProjectAuth;


@Service
public class ProjectAuthService extends BaseService{

	public void save(ProjectAuth projectAuth) {
		this.projectAuthDao.save(projectAuth);
	}

	public List<ProjectAuth> findByMemberIdAndDel(Long memberid, DELTYPE del) {
		List<ProjectAuth> list=this.projectAuthDao.findByMemberIdAndDel(memberid, del);
		return list;
	}

}
