package org.zywx.cooldev.service;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.auth.Permission;

@Service
public class PermissionService extends BaseService{
	public Permission getPermissionId(String enName){
		Permission p=this.permissionDao.findByEnNameAndDel(enName,DELTYPE.NORMAL);
		return p;
	}
}
