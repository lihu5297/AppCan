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
import org.zywx.coopman.service.DiskStatisticService;
import org.zywx.coopman.system.Cache;
import org.zywx.coopman.util.EditQuartzJob;

public class StatisticDiskJob implements Job,InitializingBean,ApplicationContextAware {
	
	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	@Autowired
	private DiskStatisticService diskStatisticService;
	
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("---load StatisticDiskJob begin---");
		StatisticDiskJob.applicationContext = applicationContext;
		
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

			StatisticDiskJob statisticDiskJob = (StatisticDiskJob)applicationContext.getBean("statisticDiskJob");
			 
			statisticDiskJob.diskStatisticService.updateAndGetFromServer();
			
			//统计完成修改空间统计定时器
			long frequency = statisticDiskJob.diskStatisticService.getDiskStatisticFrequency();
			
			EditQuartzJob.editStatisticDiskJob(frequency);
			
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.loadQuartzJob();	
		System.out.println("---load StatisticDiskJob over---");
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
		StatisticDiskJob statisticDiskJob = (StatisticDiskJob)applicationContext.getBean("statisticDiskJob");
		//添加空间统计定时器
		long frequency = statisticDiskJob.diskStatisticService.getDiskStatisticFrequency();
		
		EditQuartzJob.addStatisticDiskJob(frequency);
	}
	
}
