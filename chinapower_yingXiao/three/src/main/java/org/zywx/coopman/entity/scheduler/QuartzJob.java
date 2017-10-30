package org.zywx.coopman.entity.scheduler;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.service.SettingService;
import org.zywx.coopman.system.Cache;
import org.zywx.coopman.util.EditQuartzJob;

public class QuartzJob implements Job,InitializingBean,ApplicationContextAware {
	
	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	@Autowired
	private SettingService settingService;
	
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("---load QuartzJob begin---");
		QuartzJob.applicationContext = applicationContext;
		
	}

	/**
	 * 
	 * @describe 定时任务	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月14日 上午10:23:10	<br>
	 * @param arg0
	 * @throws JobExecutionException
	 *
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {

			QuartzJob quartzJob = (QuartzJob)applicationContext.getBean("quartzJob");
			Map<String,String> hostInfo = quartzJob.settingService.getHostInfo();
			quartzJob.settingService.updateBackup(hostInfo);
			
			//备份完成修改定时器
			Setting setting = Cache.getSetting("SETTING");
			long day = setting.getPlatInterval()==0?1:setting.getPlatInterval();
			int hour = setting.getPlatExecuteTime_hour();
			int minutes = setting.getPlatExecuteTime_minutes();
			
			EditQuartzJob.editQuartzJob(day,hour,minutes);
			
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.loadQuartzJob();
		System.out.println("=========sun.jnu.encoding:"+System.getProperty("sun.jnu.encoding")+"=========");
		System.out.println("=========file.encoding:"+System.getProperty("file.encoding")+"=========");
		System.out.println("---load QuartzJob over---");
	}
	
	/**
	 * 
	 * @describe 添加定时器	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月14日 上午10:15:10	<br>  <br>
	 * @returnType void
	 *
	 */
	private void loadQuartzJob() {
		Setting setting = Cache.getSetting("SETTING");
		long day = setting.getPlatInterval();
		int hour = setting.getPlatExecuteTime_hour();
		int minutes = setting.getPlatExecuteTime_minutes();
		
		EditQuartzJob.addQuartzJob(day,hour,minutes);
	}
	
}
