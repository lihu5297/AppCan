package org.zywx.cooldev.service;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.PermissionInterceptor;

@Service
public class PermissionInterceptorService extends BaseService{
	public PermissionInterceptor isMatchInterceptor(String requestUrl,String method){
		PermissionInterceptor permissionInterceptor=this.permissionInterceptorDao.findByQuestUrlAndMethodAndDel(requestUrl,method);
		return permissionInterceptor;
	}
}
