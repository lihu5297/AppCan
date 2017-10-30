package org.zywx.cooldev.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.PermissionInterceptor;
import org.zywx.cooldev.service.PermissionInterceptorService;

public interface PermissionInterceptorDao extends PagingAndSortingRepository<PermissionInterceptor, Long>{
	@Query(nativeQuery=true,value="select * from T_PERMISSION_INTERCEPTOR where ?1 REGEXP  requestUrl and method=?2 and del=0")
	public PermissionInterceptor findByQuestUrlAndMethodAndDel(String requestUrl, String method);

}
