package org.zywx.coopman.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.entity.scheduler.PlatLogReadJob;
import org.zywx.coopman.entity.scheduler.QuartzJob;
import org.zywx.coopman.entity.scheduler.StatisticDiskJob;
import org.zywx.coopman.service.PlatFormLogService;

public class EditQuartzJob {
	
	static Log log = LogFactory.getLog(EditQuartzJob.class.getName());
	
	private final static String job_name = "PLATFORM_BACKUP_QUARTZ";
	
	/**
	 * 
	 * @describe 添加定时任务	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月14日 上午10:32:05	<br>
	 * @param day
	 * @param setting  <br>
	 * @returnType void
	 *
	 */
	public static void addQuartzJob(long day,int hour,int minutes){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, Integer.parseInt(day+""));
		Date date = cal.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("0 "+minutes+" "+hour+" dd MM ? *");
		
		String time = sdf.format(date);
		
		log.info("平台备份定时器："+job_name+" 启动:"+time);
		QuartzManager.addJob(job_name, QuartzJob.class, time);
		
	}
	
	/**
	 * 
	 * @describe 更新定时任务	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月14日 上午10:32:20	<br>
	 * @param day
	 * @param setting  <br>
	 * @returnType void
	 *
	 */
	public static void editQuartzJob(long day,int hour,int minutes){

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, Integer.parseInt(day + ""));
		
		java.util.Date date = cal.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("0 "+minutes+" "+hour+" dd MM ? yyyy");
		
		String time = sdf.format(date);

		log.info("平台备份定时器："+job_name+" 修改:"+time);
		QuartzManager.modifyJobTime(job_name, time);
		
	}

	@SuppressWarnings("deprecation")
	public static void addPlatLogReadJob(String hours, String minutes){
		
		Date date = new Date();
		date.setHours(Integer.parseInt(hours));
		date.setMinutes(Integer.parseInt(minutes));

		SimpleDateFormat sdf = new SimpleDateFormat("0 "+minutes+" "+hours+" * * ? *");
		
		String time = sdf.format(date);

		log.info("平台异常日志读取定时器："+"PLATLOG_READ_QUARTZ"+" 添加:"+time);
		QuartzManager.addJob("PLATLOG_READ_QUARTZ",PlatLogReadJob.class, time);
	}
	
	@SuppressWarnings("deprecation")
	public static void addStatisticDiskJob(long minutes){
		
		Date date = new Date();
		long hours = minutes/60;
		minutes = minutes%60;
		date.setHours(date.getHours()+Integer.parseInt(hours+""));
		date.setMinutes(date.getMinutes()+Integer.parseInt(minutes+""));
		
		SimpleDateFormat sdf = new SimpleDateFormat("0 mm HH * * ? *");
		
		String time = sdf.format(date);
		
		log.info("添加服务器空间统计定时器："+"STATISTIC_DISK_QUARTZ"+" 添加:"+time);
		QuartzManager.addJob("STATISTIC_DISK_QUARTZ",StatisticDiskJob.class, time);
	}
	
	@SuppressWarnings("deprecation")
	public static void editStatisticDiskJob(long minutes){
		
		Date date = new Date();
		long hours = minutes/60;
		minutes = minutes%60;
		date.setHours(date.getHours()+Integer.parseInt(hours+""));
		date.setMinutes(date.getMinutes()+Integer.parseInt(minutes+""));
		
		SimpleDateFormat sdf = new SimpleDateFormat("0 mm HH * * ? yyyy");
		
		String time = sdf.format(date);
		
		log.info("修改服务器空间统计定时器："+"STATISTIC_DISK_QUARTZ"+" 添加:"+time);
		QuartzManager.modifyJobTime("STATISTIC_DISK_QUARTZ", time);
	}
}
