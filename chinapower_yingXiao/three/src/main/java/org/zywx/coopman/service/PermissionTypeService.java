package org.zywx.coopman.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.auth.Permission;
import org.zywx.coopman.entity.auth.PermissionType;

@Service
public class PermissionTypeService extends BaseService{
	
	/**
	 * 查询角色类别
	 * @user jingjian.wu
	 * @date 2015年9月17日 下午12:02:37
	 */
	public List<PermissionType> findPermissionTypes(){
		
		//获取所有的角色类型
		List<PermissionType> listPermissionType  = permissionTypeDao.findByDel(DELTYPE.NORMAL);
			
		//遍历角色类型
		if(null!=listPermissionType && listPermissionType.size()>0){
			for(PermissionType pt:listPermissionType){
				//获取每个角色类型下的所有权限
				List<Permission> listPermitAll = permissionDao.findByTypeIdAndDel(pt.getId(), DELTYPE.NORMAL);
				//为每个角色类型设置角色列表
				pt.setPermission(listPermitAll);
			}
		}
		return listPermissionType;
	}
	
}
