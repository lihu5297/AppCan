package org.zywx.cooldev.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.auth.Role;

@Service
public class RoleService extends BaseService{
	

	
	/**
	 * 查询角色列表
	 * @user jingjian.wu
	 * @date 2015年9月17日 下午12:02:14
	 */
	public List<Role> findRoleList(){
		List<Role> listRole = roleDao.findByDel(DELTYPE.NORMAL);
		for(Role r:listRole){
			r.setPermissions(permissionDao.findByRoleIdAndDelType(r.getId(), DELTYPE.NORMAL));
		}
		return listRole;
	}
	
	
	
}
