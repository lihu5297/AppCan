package org.zywx.cooldev.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.UserAuth;
import org.zywx.cooldev.entity.auth.Permission;

@Service
public class UserAuthService extends BaseService{

	
	public UserAuth addUserAuth(UserAuth ua) {
		ua = this.userAuthDao.save(ua);
		return ua;
	}
	/**
	 * 查询用户权限
	 * @param userId
	 * @return
	 */
	public Map<String, Integer> findUserAuth(Long userId) {
		List<UserAuth> auth = this.userAuthDao.findByUserId(userId);
		HashMap<String, Integer> map = new HashMap<>();
		if(null!=auth){
			List<Long> perIds = new ArrayList<Long>();
			for(UserAuth ua : auth){
				perIds.add(ua.getPermissionId());
			}
			List<Permission> pms = this.permissionDao.findByIdInAndDel(perIds, DELTYPE.NORMAL);
			for(Permission pm : pms){
				map.put(pm.getEnName(),1);
			}
		}
		return map;
	}

	/**
	 * 查询用户创建项目和审批的权限
	 * @param userId
	 * @return
	 */
	public Map<String, Integer> findUserCreateAndProAuth(Long userId) {
		List<UserAuth> auth = this.userAuthDao.findByUserId(userId);
		HashMap<String, Integer> map = new HashMap<>();
		if(null!=auth){
			List<Long> perIds = new ArrayList<Long>();
			for(UserAuth ua : auth){
				perIds.add(ua.getPermissionId());
			}
			List<Permission> pms = this.permissionDao.findByIdInAndTypeIdAndDel(perIds, 31L, DELTYPE.NORMAL);
			for(Permission pm : pms){
				map.put(pm.getEnName(),1);
			}
		}
		return map;
	}
	public List<Long> findUserIdByPermissionId(Long permissionId) {
		List<Long> lList=new ArrayList<Long>(); 
		List<UserAuth> list=userAuthDao.findByPermissionIdAndDel(permissionId,DELTYPE.NORMAL);
		if(list!=null){
			for (UserAuth userAuth : list) {
				lList.add(userAuth.getUserId());
			}
		}else{
			lList.add(-99l);
		}
		return lList;
	}
}
