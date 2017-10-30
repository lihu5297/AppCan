package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.project.ProjectParent;
import org.zywx.cooldev.service.TransService;

/**
 * 大项目管理控制类
 * 
 * @author 东元
 *
 */
@Controller
@RequestMapping(value = "/bigProject")
public class ProjectParentController extends BaseController {

	@Autowired
	private TransService transService;
	
	@ResponseBody
	@RequestMapping(value = "/findBigProjectInfo", method = RequestMethod.GET)
	public Map<String, Object> addBigProject(
			@RequestHeader(value = "loginUserId", required = true) long loginUserId
			) {
		List<ProjectParent> ppList = projectParentService.findAll();
		if(ppList != null && !ppList.isEmpty()){
			List<String> typeList = new ArrayList<String>();
			typeList.add("4");
			typeList.add("5");
			List<Project> pList = null;
			int appCount = 0;
			int memberCount = 0;
			//查询子项目
			for(ProjectParent pp : ppList){
				List<String> artFilialelist = new ArrayList<String>();
				pList = projectService.findByParentId(pp.getId());
				if(pList != null && !pList.isEmpty()){
					pp.setFlag(false);		//不可以删除
					List<App> appList = null;
					List<ProjectMember> memberList = null;
					for(Project p : pList){
						User user = userService.findUserById(p.getCreatorId());
						if(user!=null&&user.getFilialeName()!=null){
							if(!artFilialelist.contains(user.getFilialeName())){
								artFilialelist.add(user.getFilialeName());
							}
						}
						
						//查询应用信息
						appList = appService.findAppByProjectId(p.getId());
						if(appList != null){
							appCount += appList.size();
						}
						
						//查询项目参与人员
						memberList = projectService.findMemberByProjectId(p.getId());
						if(memberList != null){
							memberCount += memberList.size();
						}
					}
					pp.setProjectCount(pList.size());
					pp.setAppCount(appCount);
					pp.setMemberCount(memberCount);
					pp.setPartFiliale(StringUtils.join(artFilialelist.toArray(), ","));
				}else{
					pp.setPartFiliale("");
					pp.setFlag(true);		//可以删除
					pp.setProjectCount(0);
					pp.setAppCount(0);
					pp.setMemberCount(0);
				}
				List<Project> xlist = projectService.findByParentIdAndDel(pp.getId());
				if(xlist!=null&&!xlist.isEmpty()){
					pp.setFlag(false);	
				}
			}
		}
		return this.getSuccessMap(ppList);
	}
	/**
	 * 查询大项目下的子项目
	 * @param request
	 * @param bigId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findProjectByBigId", method = RequestMethod.GET)
	public Map<String, Object> findProjectByBigId(HttpServletRequest request,@RequestParam(value="id")Long bigId,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId
			) {
		if(bigId == null || bigId < 1){
			return this.getFailedMap("项目ID不能为空");
		}
		List<Project> pList = projectService.findByParentId(bigId);
		return this.getSuccessMap(pList);
	}
	
	/**
	 * 添加大工程
	 * @param project
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addBigProject", method = RequestMethod.POST)
	public Map<String, Object> addBigProject( HttpServletRequest request, ProjectParent project,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId
			) {
		if( loginUserId==0){
			return this.getFailedMap("登录状态为空");
		}
		Map<String,Integer> permissions = this.userAuthService.findUserAuth(loginUserId);
		if(permissions != null && permissions.get("project_create") != null && permissions.get("project_create").equals("1")){
			return this.getFailedMap("无权创建大项目");
		}
		
		if(StringUtils.isBlank(project.getProjectCode())){
			return this.getFailedMap("项目代码不能为空");
		}
		if (project.getProjectName() != null && project.getProjectName().length() > 50) {
			return this.getFailedMap("项目名称不能超过50个字符");
		}
		if (project.getProjectDesc() != null && project.getProjectDesc().length() > 500) {
			return this.getFailedMap("项目描述不能超过500个字符");
		}
		List<ProjectParent> ppList = projectParentService.findByProjectCode(project.getProjectCode());
		if(ppList != null && !ppList.isEmpty()){
			return this.getFailedMap("项目代码已被占用");
		}
		project.setUserId(loginUserId);
		ProjectParent pp = projectParentService.saveUpdateProject(project);
		if(pp != null){
			return this.getSuccessMap("添加成功");
		}else{
			return this.getFailedMap("添加失败");
		}
	}
	/**
	 * 修改大项目信息
	 * @param project
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/editBigProject", method = RequestMethod.POST)
	public Map<String, Object> editBigProject(ProjectParent project,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId
			) {
		Map<String,Integer> permissions = this.userAuthService.findUserAuth(loginUserId);
		if(permissions != null && permissions.get("project_create") != null && permissions.get("project_create").equals("1")){
			return this.getFailedMap("无权修改大项目");
		}
		if(project.getId() == null || project.getId() < 1){
			return this.getFailedMap("项目ID不能为空");
		}
		if(StringUtils.isBlank(project.getProjectCode())){
			return this.getFailedMap("项目代码不能为空");
		}
		if (project.getProjectName() != null && project.getProjectName().length() > 100) {
			return this.getFailedMap("项目名称不能超过100个字符");
		}
		if (project.getProjectDesc() != null && project.getProjectDesc().length() > 1000) {
			return this.getFailedMap("项目描述不能超过1000个字符");
		}
		List<ProjectParent> ppList = projectParentService.findByIdNotAndProjectCode(project.getId(),project.getProjectCode());
		if(ppList != null && !ppList.isEmpty()){
			return this.getFailedMap("项目代码已被占用");
		}
		ProjectParent pp = projectParentService.saveUpdateProject(project);
		if(pp != null){
			return this.getSuccessMap("修改成功");
		}else{
			return this.getFailedMap("修改失败");
		}
	}
	/**
	 * 删除大项目
	 * @param id
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delBigProject", method = RequestMethod.POST)
	public Map<String, Object> delBigProject(@RequestParam(value="id") Long id,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId
			) {
		/*Map<String,Integer> permissions = this.userAuthService.findUserAuth(loginUserId);
		if(permissions != null && permissions.get("project_create") != null && permissions.get("project_create").equals("1")){
			return this.getFailedMap("无权删除大项目");
		}*/
		if(id == null || id < 1){
			return this.getFailedMap("项目ID不能为空");
		}
		ProjectParent pp = projectParentService.findById(id);
		if(pp == null){
			return this.getFailedMap("项目不存在");
		}else{
			List<Project> proList = projectService.findByParentId(pp.getId());
			if(proList != null && !proList.isEmpty()){
				return this.getFailedMap("包含子项目信息，删除失败");
			}
			
			//pp.setDel(Enums.DELTYPE.DELETED);
			 projectParentService.delProject(id);
			return this.getSuccessMap("删除成功");
			/*if(ppDel == null){
				return this.getFailedMap("删除失败");
			}else{
			}*/
		}
	}
}
