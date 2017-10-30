package org.zywx.cooldev.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.GitAuthVO;
import org.zywx.cooldev.entity.app.GitOwnerAuthVO;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.MD5Util;
@Service
public class ProjectMemberService extends BaseService{
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private ProjectAuthService projectAuthService;
	
	@Autowired
	private AppService appService;
	public ProjectMember findMemberByProjectIdAndUserId(Long projectId, Long userId) {
		ProjectMember pm = null;
		List<ProjectMember> list= this.projectMemberDao.findByProjectIdAndUserIdAndDel(projectId, userId,DELTYPE.NORMAL);
		if(list.size()>0){
			pm=list.get(0);
		}
		return pm;
	}

	public void save(ProjectMember projectMember) {
		this.projectMemberDao.save(projectMember);
		ProjectAuth projectAuth = new ProjectAuth();
		projectAuth.setMemberId(projectMember.getId());
		projectAuth.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.MEMBER).getId());
		projectAuthService.save(projectAuth);
	}

	public boolean updateEmmInvokeDelMember(String loginName, String mobilePhone, String userName, String uuid) {
		User user = userService.findUserByAccountAndDel(loginName, DELTYPE.NORMAL);
		if(null==user){
			return true;
		}
		Project project = projectService.getByUuid(uuid);
		ProjectMember pm = this.findMemberByProjectIdAndUserId(project.getId(),user.getId());
		//-------------------------------------git权限-------------------------------------
		List<GitAuthVO> listAuth = new ArrayList<GitAuthVO>();
		List<GitOwnerAuthVO> changeOwnerAuth = new ArrayList<GitOwnerAuthVO>();
		if(null!=pm){
			pm.setDel(DELTYPE.DELETED);
			this.save(pm);//在项目成员表将该成员设为失效
			List<ProjectAuth> projectAuthList = projectAuthService.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);//根据成员Id查询项目角色表中
			if(null!=projectAuthList && projectAuthList.size()>0){
				for(ProjectAuth projectAuth:projectAuthList){
					projectAuth.setDel(DELTYPE.DELETED);
					projectAuthService.save(projectAuth);
					
					boolean isAdministrator=false;//标识被删除人是否为项目的管理员
					if(projectAuth.getRoleId()==Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.ADMINISTRATOR).getId()){
						isAdministrator=true;
					}
					if(isAdministrator){//退出人是项目管理员,需要判断此人是否参与了该项目,如果没有参与,则应该设定此人无权访问对应项目下的应用git权限
						Role role = Cache.getRole(projectAuth.getRoleId());
						List<Permission> listPermission = role.getPermissions();
						boolean uploadMasterOrBranch = false;//标识此人在项目中是否有上传主干或者分支的权限
						if(null!=listPermission && listPermission.size()>0){
							for(Permission p :listPermission){
								if(p.getEnName().equals("code_upload_master_code") || p.getEnName().equals("code_update_branch")){
									uploadMasterOrBranch = true;
									break;
								}
							}
						}
						if(uploadMasterOrBranch){
							List<ProjectMember> projectMembers = this.projectMemberDao.findByProjectIdAndUserIdAndDel(project.getId(), pm.getUserId(), DELTYPE.NORMAL);
							boolean minusGitAuth = true;//是否减去git访问权限(此人是否实质参与项目)
							for(ProjectMember projectMember :projectMembers){
								if(projectMember.getUserId()==pm.getUserId()){
									minusGitAuth = false;
									break;
								}
							}
							if(minusGitAuth){//如果此人没有实质参与到对应的项目中,则除去对应的权限
								List<App> listApp = appDao.findByProjectIdAndDel(project.getId(),DELTYPE.NORMAL);
								for(App app:listApp){
									if(app.getUserId()!=pm.getUserId()){
										GitAuthVO vo = new GitAuthVO();
//									vo.setAuthflag("all");
										vo.setPartnername(user.getAccount());
										vo.setUsername(userDao.findOne(app.getUserId()).getAccount());
										String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
										vo.setProject(encodeKey.toLowerCase());
										vo.setProjectid(app.getAppcanAppId());
//									vo.setRef("master");
										listAuth.add(vo);
									}else{
										GitOwnerAuthVO vo = new GitOwnerAuthVO();
										String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
										vo.setProject(encodeKey.toLowerCase());
										vo.setUsername(user.getAccount());
										User other = new User();//应用要转给谁
										if(project.getType().equals(PROJECT_TYPE.TEAM)){
											List<TeamMember> listTm = teamMemberDao.findByTeamIdAndTypeAndDel(project.getTeamId(), TEAMREALTIONSHIP.CREATE, DELTYPE.NORMAL);
											if(null!=listTm && listTm.size()>0){
												other = userDao.findOne(listTm.get(0).getUserId());
											}
										}else{
											ProjectMember pmCrt = projectMemberDao.findByProjectIdAndTypeAndDel(project.getId(), PROJECT_MEMBER_TYPE.CREATOR,DELTYPE.NORMAL);
											if(null!=pmCrt){
												other = userDao.findOne(pmCrt.getUserId());
											}
										}
										vo.setOther(other.getAccount());
										app.setUserId(other.getId());
										vo.setProjectid(app.getAppcanAppId());
										appDao.save(app);
										changeOwnerAuth.add(vo);
									}
								}
							}
						}
					}
				}
			}
			//删除git权限(项目下的应用不是被删除人创建的应用)
			Map<String,String> map = appService.delGitAuth(listAuth);
			log.info(user.getAccount()+" was by deleted(EMM) from project id:->"+pm.getProjectId()+" ,and delGitAuth->"+(null==map?null:map.toString()));
			//转让git权限(项目下的应用是被删除人创建的应用)
			map = appService.updateGitAuth(changeOwnerAuth);
			log.info(user.getAccount()+"was by deleted(EMM) from project id:"+pm.getProjectId()+" ,and updateGitAuth->"+(null==map?null:map.toString()));
			
			//---------------------删除对应的git权限---------------------end---------
		}
		return true;
	
	}
	public ProjectMember findMemberByProjectIdAndUserId(long projectId,long userId){
		ProjectMember pm = (ProjectMember) this.projectMemberDao.findByProjectIdAndUserIdAndDel(projectId, userId,DELTYPE.NORMAL);
		return pm;
	}
	/**
	 * 根据项目id查找项目成员
	 * @param projectId
	 * @param del
	 * @return
	 */
	public List<ProjectMember> findByProjectIdAndDel(Long projectId, DELTYPE del) {
		List<ProjectMember> listTm = this.projectMemberDao.findByProjectIdAndDel(projectId, del);
		return listTm;
	}

	public ProjectMember findMemberByProjectIdAndMemberType(Long id, PROJECT_MEMBER_TYPE creator) {
		ProjectMember projectms = this.projectMemberDao.findByProjectIdAndTypeAndDel(id,creator,DELTYPE.NORMAL);
		return projectms;
	}
}


