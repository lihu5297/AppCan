package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.service.ProjectExportService;

/**
 * 项目导出
 * @author yongwen.wang
 * @date  2016-06-07
 *
 */
@Controller
@RequestMapping(value="/projectExport")
public class ProjectExportController extends BaseController {
	@Autowired
	private ProjectExportService projectExportService;
	@Value("${root.path}")
	private String rootPath;
	
	@ResponseBody
	@RequestMapping(value="",method=RequestMethod.GET)
	//导出项目
	public Map<String,Object> projectExport(@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="projectId") long projectId){
		try{
			String message=this.projectExportService.projectExport(loginUserId,projectId);
			return this.getSuccessMap(message);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	//判断项目是否导出完毕
	@ResponseBody
	@RequestMapping(value="/judgeExportOver",method=RequestMethod.GET)
	public Map<String,Object> judgeExportOver(@RequestHeader(value="loginUserId") long loginUserId,
			@RequestParam(value="projectId") long projectId){
		try{
			boolean flag=this.projectExportService.judgeExportOver(loginUserId,projectId);
			if(flag){
				Project prjO=this.projectService.findOne(projectId);
				if(prjO==null){
					throw new RuntimeException("项目不存在");
				}
				String projectName=prjO.getName();
				return this.getSuccessMap(rootPath+"/projectExport/"+projectName+"_"+projectId+".zip");
			}else{
				return this.getFailedMap("项目还在导出中请稍等！");
			}
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	//导入项目第一步导入文件包
	@ResponseBody
	@RequestMapping(value="/fileZip",method=RequestMethod.POST)
	public Map<String,Object> projectImportFile(MultipartFile srcZip,@RequestHeader(value="loginUserId") long loginUserId){
		try{
			Map<String,Object> map=this.projectExportService.saveProjectImportFile(loginUserId,srcZip);
			return this.getSuccessMap(map);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	@ResponseBody
	@RequestMapping(value="/{teamId}",method=RequestMethod.POST)
	//导入项目
	public Map<String,Object> projectImport(@RequestParam(value="unzipFileName") String unzipFileName,@RequestParam(value="userList") List<String> userList,@RequestHeader(value="loginUserId") long loginUserId,
			@PathVariable(value="teamId") long teamId){
		try{
			List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
			for(int i=0;i<userList.size();i++){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("oldUserId", userList.get(i).split("_")[0]);
				map.put("newUserId", userList.get(i).split("_")[1]);
				list.add(map);
			}
			String message=this.projectExportService.saveProjectImport(loginUserId,teamId,unzipFileName,list);
			return this.getSuccessMap(message);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
