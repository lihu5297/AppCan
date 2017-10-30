package org.zywx.coopman.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.zywx.appdo.common.utils.JsonUtil;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.ENTITY_TYPE;
import org.zywx.coopman.commons.Enums.ROLE_ALLOW_DEL;
import org.zywx.coopman.commons.Enums.ROLE_TYPE;
import org.zywx.coopman.entity.auth.Permission;
import org.zywx.coopman.entity.auth.PermissionType;
import org.zywx.coopman.entity.auth.PermissionTypeAuth;
import org.zywx.coopman.entity.auth.Role;
import org.zywx.coopman.entity.auth.RoleAuth;

import com.alibaba.dubbo.cache.Cache;

@Service
public class RoleService extends BaseService{
	
	@Value("${xietongreloadRoleURL}")
	private String xietongreloadRoleURL;
	@Value("${xietongupdateGitAuthURL}")
	private String xietongupdateGitAuthURL;
	@Autowired
	private StringRedisTemplate  redisTemplate;
	public List<Role> findRoleDetail(){
		List<Role> listRole = roleDao.findByDel(DELTYPE.NORMAL);
		
		//获取所有的角色类型
		List<PermissionType> listPermissionType  = permissionTypeDao.findByDel(DELTYPE.NORMAL);
		
		if(null!=listRole && listRole.size()>0){
			for(Role r:listRole){
				//角色拥有的权限
				List<Permission> listPermission =  this.permissionDao.findByRoleIdAndDelType(r.getId(), DELTYPE.NORMAL);
				
				//遍历角色类型
				if(null!=listPermissionType && listPermissionType.size()>0){
					for(PermissionType pt:listPermissionType){
						//获取每个角色类型下的所有权限
						List<Permission> listPermitAll = permissionDao.findByTypeIdAndDel(pt.getId(), DELTYPE.NORMAL);
						for(Permission p:listPermitAll){
							//如果本角色拥有这个权限,则让其选中状态
							if(listPermission.contains(p)){
								p.setSelected(1);
							}else{
								p.setSelected(0);
							}
						}
						//为每个角色类型设置角色列表
						pt.setPermission(listPermitAll);
					}
				}
				//为每个角色设置角色类型
				r.setPermissionTypes(listPermissionType);
			}
		}
		return listRole;
	}
	
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
	
	/**
	 * 查询角色详情
	 * @user jingjian.wu
	 * @date 2015年9月17日 下午12:02:37
	 */
	public Role findRoleDetailOld(long roleId){
		Role role = roleDao.findOne(roleId);
		
		//获取所有的角色类型
		List<PermissionType> listPermissionType  = permissionTypeDao.findByDel(DELTYPE.NORMAL);
		
		if(null!=role){
			//角色拥有的权限
			List<Permission> listPermission =  this.permissionDao.findByRoleIdAndDelType(role.getId(), DELTYPE.NORMAL);
			
			//遍历角色类型
			if(null!=listPermissionType && listPermissionType.size()>0){
				for(PermissionType pt:listPermissionType){
					//获取每个角色类型下的所有权限
					List<Permission> listPermitAll = permissionDao.findByTypeIdAndDel(pt.getId(), DELTYPE.NORMAL);
					for(Permission p:listPermitAll){
						//如果本角色拥有这个权限,则让其选中状态
						if(listPermission.contains(p)){
							p.setSelected(1);
						}else{
							p.setSelected(0);
						}
					}
					//为每个角色类型设置角色列表
					pt.setPermission(listPermitAll);
				}
			}
			//为每个角色设置角色类型
			role.setPermissionTypes(listPermissionType);
		}
		return role;
	}
	
	public Role findRoleDetailBak(long roleId){
		Role role = roleDao.findOne(roleId);
		
		//获取所有的角色类型
		List<PermissionType> listPermissionType  = permissionTypeDao.findByDel(DELTYPE.NORMAL);
		
		if(null!=role){
			//角色拥有的权限
			List<Permission> listPermission =  this.permissionDao.findByRoleIdAndDelType(role.getId(), DELTYPE.NORMAL);
			
			//遍历角色类型
			if(null!=listPermissionType && listPermissionType.size()>0){
				for(PermissionType pt:listPermissionType){
					//获取每个角色类型下的所有权限
					List<Permission> listPermitAll = permissionDao.findByTypeIdAndDel(pt.getId(), DELTYPE.NORMAL);
					for(Permission p:listPermitAll){
						//如果本角色拥有这个权限,则让其选中状态
						if(listPermission.contains(p)){
							p.setSelected(1);
						}else{
							p.setSelected(0);
						}
					}
					//为每个角色类型设置角色列表
					pt.setPermission(listPermitAll);
				}
			}
			//为每个角色设置角色类型
			role.setPermissionTypes(listPermissionType);
		}
		return role;
	}
	
	
	public Role findRoleDetail(long roleId){
		Role role = roleDao.findOne(roleId);
		
		//获取所有的角色类型
		List<PermissionType> listPermissionType  = permissionTypeDao.findByRoleIdAndDel(roleId,DELTYPE.NORMAL);
		
		if(null!=role){
			//角色拥有的权限
			List<Permission> listPermission =  this.permissionDao.findByRoleIdAndDelType(role.getId(), DELTYPE.NORMAL);
			
			//遍历角色类型
			if(null!=listPermissionType && listPermissionType.size()>0){
				for(PermissionType pt:listPermissionType){
					//获取每个角色类型下的所有权限
					List<Permission> listPermitAll = permissionDao.findByRoleIdAndTypeIdAndDel(roleId,pt.getId(), DELTYPE.NORMAL);
					for(Permission p:listPermitAll){
						//如果本角色拥有这个权限,则让其选中状态
						if(listPermission.contains(p)){
							p.setSelected(1);
						}else{
							p.setSelected(0);
						}
					}
					//为每个角色类型设置角色列表
					pt.setPermission(listPermitAll);
				}
			}
			//为每个角色设置角色类型
			role.setPermissionTypes(listPermissionType);
		}
		return role;
	}
	
	/**
	 * 修改角色权限
	 * @user jingjian.wu
	 * @date 2015年9月17日 下午3:43:31
	 */
	public Map<String,String> updateRoleAuth(long roleId,String permissionIds){
		boolean code_upload_master_code = false;//原先是否有主干权限
		boolean code_update_branch = false;//原先是否有分支权限
		
		boolean nowMasterFlag = false;//修改后是否有主干权限
		boolean nowBranchFlag = false;//修改后是否有分支权限
		List<Permission> listPermissionOriginal= permissionDao.findByRoleIdAndDelType(roleId, DELTYPE.NORMAL);
		if(null!=listPermissionOriginal){
			for(Permission p:listPermissionOriginal){
				if("code_upload_master_code".equals(p.getEnName())){
					code_upload_master_code = true;
				}
				if("code_update_branch".equals(p.getEnName())){
					code_update_branch = true;
				}
			}
		}
		Permission permissionMaster = permissionDao.findByEnNameAndDel("code_upload_master_code",DELTYPE.NORMAL);
		Permission permissionBranch = permissionDao.findByEnNameAndDel("code_update_branch",DELTYPE.NORMAL);
		Role role = roleDao.findOne(roleId);
		
		if(role.getEnName().equals(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR)){
			Role roleTeamCreator=this.roleDao.findByEnNameAndDel(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR,DELTYPE.NORMAL);
			List<RoleAuth> roleAuthList=this.roleAuthDao.findByRoleId(role.getId());
			//List<Long> permissionIdList=new ArrayList<Long>();
			String pjCreatorPermissionIds="";
			for(RoleAuth roleAuth:roleAuthList){
				pjCreatorPermissionIds+=","+roleAuth.getPremissionId();
			}
			String deleteProjectCreatorSql="delete from T_ROLE_AUTH where roleId="+roleTeamCreator.getId()+" and premissionId in ("+pjCreatorPermissionIds.substring(1)+")";
			jdbcTpl.execute(deleteProjectCreatorSql);
		}
		if(role.getEnName().equals(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR)){
			Role roleTeamAdm=this.roleDao.findByEnNameAndDel(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR,DELTYPE.NORMAL);
			List<RoleAuth> roleAuthList=this.roleAuthDao.findByRoleId(role.getId());
			//List<Long> permissionIdList=new ArrayList<Long>();
			String pjCreatorPermissionIds="";
			for(RoleAuth roleAuth:roleAuthList){
				pjCreatorPermissionIds+=","+roleAuth.getPremissionId();
			}
			String deleteProjectCreatorSql="delete from T_ROLE_AUTH where roleId="+roleTeamAdm.getId()+" and premissionId in ("+pjCreatorPermissionIds.substring(1)+")";
			jdbcTpl.execute(deleteProjectCreatorSql);
		}
		String deleteSql="delete from T_ROLE_AUTH where roleId="+roleId;
		jdbcTpl.execute(deleteSql);
		String permissionId[] = permissionIds.split(",");
		
		//=============【团队创建者】拥有【项目创建者】的所有权限,【团队管理员】 拥有【项目管理员】的所有权限===========//
		//【团队创建者】拥有【项目创建者】的所有权限
		if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR)){
			Role projectCreatorRole = this.findByName(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR);
			List<Permission> listPermission =  this.permissionDao.findByRoleIdAndDelType(projectCreatorRole.getId(), DELTYPE.NORMAL);
			String permissionIdPRJ[] = new String[listPermission.size()];
			int a = 0;
			for(Permission per : listPermission){
				permissionIdPRJ[a] = per.getId()+"";
				a++;
			}
			String permisssions[] = new String[permissionId.length+permissionIdPRJ.length];
			int i=0;
			for(;i<permissionId.length;i++){
				permisssions[i] = permissionId[i];
			}
			for(int j=0;j<permissionIdPRJ.length;j++){
				permisssions[i] = permissionIdPRJ[j];
				i++;
			}
			permissionId = permisssions;
		}
		
		//【团队管理员】 拥有【项目管理员】
		if(role.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR)){
			Role projectCreatorRole = this.findByName(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR);
			List<Permission> listPermission =  this.permissionDao.findByRoleIdAndDelType(projectCreatorRole.getId(), DELTYPE.NORMAL);
			String permissionIdPRJ[] = new String[listPermission.size()];
			int a = 0;
			for(Permission per : listPermission){
				permissionIdPRJ[a] = per.getId()+"";
				a++;
			}
			String permisssions[] = new String[permissionId.length+permissionIdPRJ.length];
			int i = 0;
			for(;i<permissionId.length;i++){
				permisssions[i] = permissionId[i];
			}
			for(int j=0;j<permissionIdPRJ.length;j++){
				permisssions[i] = permissionIdPRJ[j];
				i++;
			}
			
			permissionId = permisssions;
		}
		//=============【团队创建者】拥有【项目创建者】的所有权限,【团队管理员】 拥有【项目管理员】的所有权限===========//
		
		int i = 0;
		for(String permissId:permissionId){
			if(null!=permissionMaster && permissionMaster.getId().longValue()==Long.parseLong(permissId)){
				nowMasterFlag = true;
			}
			if(null!=permissionBranch && permissionBranch.getId().longValue()==Long.parseLong(permissId)){
				nowBranchFlag = true;
			}
			RoleAuth ra = new RoleAuth();
			ra.setRoleId(roleId);
			ra.setPremissionId(Long.parseLong(permissId));
			roleAuthDao.save(ra);
			i++;
		}
		HashMap<String, String> map = new HashMap<>();
		map.put("count", i+"");
		map.put("xietongreloadRoleURL",xietongreloadRoleURL);
//		String getReturn = HttpTools.sendGet(xietongreloadRoleURL, "");
//		log.info(xietongreloadRoleURL+" reload cache :"+getReturn);
		
		if(role.getEnName().equals("TEAM_CREATOR") 
				|| role.getEnName().equals("TEAM_ADMINISTRATOR")
				|| role.getEnName().equals("PROJECT_CREATOR")
				|| role.getEnName().equals("PROJECT_ADMINISTRATOR")
				|| role.getEnName().equals("PROJECT_MEMBER")
				|| role.getEnName().equals("PROJECT_OBSERVER")
				){//只有团队创建者,团队管理员,项目创建者,项目管理员,项目参与人,项目观察员会修改git权限
			int masterResult = 0;//标识是否修改主干权限  1:增加主干权限 0:不修改  -1：删除主干权限
			int branchResult = 0;//标识是否修改分支权限  1:增加分支权限 0:不修改  -1：删除分支权限
			if(code_upload_master_code == nowMasterFlag){
				if(code_update_branch == nowBranchFlag){
					log.info("没有修改git上传主干及更新分支的权限");
					//=============【团队创建者】拥有【项目创建者】的所有权限,【团队管理员】 拥有【项目管理员】的所有权限===========//
					if(role.getEnName().equals(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR)){
						Role teamCreatorRole = this.findByName(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR);
						List<Permission> listPermissions =  this.permissionDao.findByRoleIdAndDelType(teamCreatorRole.getId(), DELTYPE.NORMAL);
						String listPermissionStr = "";
						for(Permission per : listPermissions){
							listPermissionStr += ","+per.getId();
						}
						listPermissionStr = listPermissionStr.substring(1);
						this.updateRoleAuth(teamCreatorRole.getId(),listPermissionStr);
					}
					
					if(role.getEnName().equals(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR)){
						Role teamCreatorRole = this.findByName(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
						List<Permission> listPermissions =  this.permissionDao.findByRoleIdAndDelType(teamCreatorRole.getId(), DELTYPE.NORMAL);
						String listPermissionStr = "";
						for(Permission per : listPermissions){
							listPermissionStr += ","+per.getId();
						}
						listPermissionStr = listPermissionStr.substring(1);
						this.updateRoleAuth(teamCreatorRole.getId(),listPermissionStr);
					}
					//=============【团队创建者】拥有【项目创建者】的所有权限,【团队管理员】 拥有【项目管理员】的所有权限===========//
					return map;
				}else{
					log.info("修改了更新分支的权限");
					if(nowBranchFlag){//现在此角色没有更新分支的权限了
						//增加分支的权限
						branchResult = 1;
					}else{
						//删除分支的权限
						branchResult = -1;
					}
				}
			}else if(code_update_branch == nowBranchFlag){//只修改了主干权限
				if(nowMasterFlag){
					//增加主干权限
					masterResult = 1;
				}else{
					//删除主干权限
					masterResult = -1;
				}
			}else{//主干分支全变了
				if(nowMasterFlag){
					//增加主干权限
					masterResult = 1;
				}else{
					//删除主干权限
					masterResult = -1;
				}
				
				if(nowBranchFlag){
					//增加分支权限
					branchResult = 1;
				}else{
					//删除分支权限
					branchResult = 1;
				}
			}
			//调用前端接口修改git权限
			map.put("xietongupdateGitAuthURL", xietongupdateGitAuthURL);
			map.put("roleId", roleId+"");
			map.put("master", masterResult+"");
			map.put("branch", branchResult+"");
			//(roleId,masterResult,branchResult)
//			String updateRoleReturn = HttpTools.sendGet(xietongreloadRoleURL, "");
//			log.info(xietongreloadRoleURL+" reload git Auth :roleId-->"+roleId+",result:"+updateRoleReturn);
		}
		
		//=============【团队创建者】拥有【项目创建者】的所有权限,【团队管理员】 拥有【项目管理员】的所有权限===========//
		if(role.getEnName().equals(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR)){
			Role teamCreatorRole = this.findByName(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR);
			List<Permission> listPermissions =  this.permissionDao.findByRoleIdAndDelType(teamCreatorRole.getId(), DELTYPE.NORMAL);
			String listPermissionStr = "";
			for(Permission per : listPermissions){
				listPermissionStr += ","+per.getId();
			}
			listPermissionStr = listPermissionStr.substring(1);
			this.updateRoleAuth(teamCreatorRole.getId(),listPermissionStr);
		}
		
		if(role.getEnName().equals(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR)){
			Role teamCreatorRole = this.findByName(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
			List<Permission> listPermissions =  this.permissionDao.findByRoleIdAndDelType(teamCreatorRole.getId(), DELTYPE.NORMAL);
			String listPermissionStr = "";
			for(Permission per : listPermissions){
				listPermissionStr += ","+per.getId();
			}
			listPermissionStr = listPermissionStr.substring(1);
			this.updateRoleAuth(teamCreatorRole.getId(),listPermissionStr);
		}
		//=============【团队创建者】拥有【项目创建者】的所有权限,【团队管理员】 拥有【项目管理员】的所有权限===========//
		
		return map;
		
		
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年9月18日 上午9:46:19
	 */
	    
	public Map<String,Object>  addCustomRole(Role role, String permissionIds) {
		role.setAllowdel(ROLE_ALLOW_DEL.PERMIT);
		roleDao.save(role);
		String permissionId[] = permissionIds.split(",");
		int i=0;
		for(String permissId:permissionId){
			RoleAuth ra = new RoleAuth();
			ra.setRoleId(role.getId());
			ra.setPremissionId(Long.parseLong(permissId));
			roleAuthDao.save(ra);
			i++;
		}
		List<Permission> lists = this.permissionDao.findByDel(DELTYPE.NORMAL);
		for(Permission per:lists){
			PermissionTypeAuth pTypeAuth = new PermissionTypeAuth();
			pTypeAuth.setPermissionId(per.getId());
			pTypeAuth.setRoleId(role.getId());
			pTypeAuth.setPermissionTypeId(per.getTypeId());
			pTypeAuth.setEnName(per.getEnName());
			pTypeAuth.setCnName(per.getCnName());
			this.permissionTypeAuthDao.save(pTypeAuth);
		}
		HashMap<String, Object> map = new HashMap<>();
		map.put("count", i);
		map.put("xietongreloadRoleURL",xietongreloadRoleURL);
		return map;
	}
	
	public String deleteCustomRole(long roleId){
		//删除角色
		String sql = "delete from T_ROLE_AUTH where roleId=?";
		jdbcTpl.update(sql, roleId);
		//删除可配权限
		String sql2 = "delete from T_PERMISSION_TYPE_AUTH where roleId=?";
		jdbcTpl.update(sql2, roleId);
		roleDao.delete(roleId);
		return xietongreloadRoleURL;
	}

	public Role findByName(String string) {
		Role role= this.roleDao.findByEnNameAndDel(string, DELTYPE.NORMAL);
		return role;
	}

	public void updateRoleSetting(Long roleId) {
		Role r =this.roleDao.findByIdAndDel(roleId,DELTYPE.NORMAL);
		List<Permission> listPermission = this.permissionDao.findByRoleIdAndDelType(r.getId(), DELTYPE.NORMAL);
		r.setPermissions(listPermission);
		try {
			redisTemplate.opsForValue().set("roleId"+r.getId(), JsonUtil.obj2Json(r));
			redisTemplate.opsForValue().set("roleEnName"+r.getEnName(), JsonUtil.obj2Json(r));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateRedisRole(Long roleId) {
		Role prjectA=roleDao.findByEnNameAndDel(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR,DELTYPE.NORMAL);
		Role prjectC=roleDao.findByEnNameAndDel(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR,DELTYPE.NORMAL);
		if(roleId.equals(prjectA.getId())){
			updateRoleSetting(roleId);
			Role teamA=roleDao.findByEnNameAndDel(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR,DELTYPE.NORMAL);
			updateRoleSetting(teamA.getId());
		}else if(roleId.equals(prjectC.getId())){
			updateRoleSetting(roleId);
			Role teamC=roleDao.findByEnNameAndDel(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR,DELTYPE.NORMAL);
			updateRoleSetting(teamC.getId());
		}else{
			updateRoleSetting(roleId);
		}
		
	}
	/**
	 * 查询用户初始化权限列表
	 * @return
	 */
	public List<Permission> findUserInitPermission(){
		return permissionDao.findByTypeIdAndDel(31, DELTYPE.NORMAL);
	}
}
