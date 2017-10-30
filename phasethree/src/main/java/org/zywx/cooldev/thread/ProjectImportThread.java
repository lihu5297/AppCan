package org.zywx.cooldev.thread;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zywx.cooldev.service.ProjectExportService;
public class ProjectImportThread  implements InitializingBean,ApplicationContextAware,Runnable  {

	private long loginUserId;
	private long teamId;
	private String unzipPath;
	private List<Map<String,Object>> userList;
	public static ApplicationContext applicationContext;
	@Override
	public void run(){
		try {
		    ProjectExportService projectExportService = (ProjectExportService)applicationContext.getBean("projectExportService");
		    projectExportService.addTablesAndResource(teamId,unzipPath,loginUserId,userList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ProjectImportThread() {
		super();
	}
	
	public ProjectImportThread(long loginUserId,long teamId,String unzipPath,List<Map<String,Object>> userList){
		this.loginUserId = loginUserId;
		this.teamId = teamId;
		this.unzipPath=unzipPath;
		this.userList=userList;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
}
