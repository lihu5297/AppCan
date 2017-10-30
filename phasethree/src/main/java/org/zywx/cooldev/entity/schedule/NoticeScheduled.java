
    /**  
     * @Description: 
     * @author jingjian.wu
     * @date 2015年9月9日 下午6:59:03
     */
    
package org.zywx.cooldev.entity.schedule;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.TASK_MEMBER_TYPE;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskLeaf;
import org.zywx.cooldev.entity.task.TaskMember;
import org.zywx.cooldev.service.BaseService;
import org.zywx.cooldev.service.NoticeService;
import org.zywx.cooldev.service.ProcessService;
import org.zywx.cooldev.service.SettingService;
import org.zywx.cooldev.service.SettingsConfigService;
import org.zywx.cooldev.service.TaskService;
import org.zywx.cooldev.service.UserService;


    /**
 * @Description: 
 * @author jingjian.wu
 * @date 2015年9月9日 下午6:59:03
 *
 */
@Component
public class NoticeScheduled {
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private BaseService baseService;

	@Autowired
	private SettingsConfigService settingsConfigService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SettingService settingService;
	
	@Value("${machineHost}")
	private String machineHost;
	
	private int advance;
	private int back;
	
	protected Log log = LogFactory.getLog(this.getClass().getName());	
	
	/**
	 *
	 * 
	 * cron表达式：*(秒0-59) *(分钟0-59) *(小时0-23) *(日期1-31) *(月份1-12或是JAN-DEC) *(星期1-7或是SUN-SAT) 
	 */
	
//	@Scheduled(cron="0 0 6 * * ?")//每天早上6点
//	@Scheduled(fixedRate = 1000)//每秒
	public void appMonthCount(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		log.info("定时任务执行了:"+sdf.format(cal.getTime()));
	}
	
	@SuppressWarnings({ "deprecation" })
	@Scheduled(cron="0 0 8 * * ?")//每天早上8点
//	@Scheduled(fixedRate = 60000)//每秒
	public void sendNoticeScheduled(){
		try {
			InetAddress ia=null;
			ia=ia.getLocalHost();
			String localname=ia.getHostName();
			if(!localname.equals(machineHost)){//只在其中一個執行
				return;
			}
			log.info("eight clock every morning to send notice");
			advance = Integer.parseInt(this.settingsConfigService.findValueById("ADVANCE","SCHEDULED"));//2
			back = Integer.parseInt(this.settingsConfigService.findValueById("BACK","SCHEDULED"));//-1
			List<Task> taskWarning = this.taskService.findTaskByAdvance(advance);//即将延期的任务,截止日期比当前日期大一天
			List<Task> taskOverDue = this.taskService.findTaskByAdvance(back);//已延期一天的任务
			List<TaskLeaf> taskLeafWarnging = taskService.findTaskLeafByWarning(advance);//即将延期的子任务,截止日期比当前日期大一天
			List<TaskLeaf> taskLeafOverDue = taskService.findTaskLeafByWarning(back);//已延期一天的子任务
			
			sendWarningNoticeAndMail(taskWarning);
			sendOverDueNoticeAndMail(taskOverDue);
			
			sendWarningNoticeAndMailForLeaf(taskLeafWarnging);
			sendOverDueNoticeAndMailForLeaf(taskLeafOverDue);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
	}
	
	private void sendWarningNoticeAndMail(List<Task> tasksWarning){
		for(Task task : tasksWarning){
			User user = null;
			List<TaskMember> members= this.taskService.getTaskMemberList(task.getId(), null, null);
			Set<Long> ids = new java.util.HashSet<Long>();
			for(TaskMember pids : members){
				if(pids.getType().compareTo(TASK_MEMBER_TYPE.CREATOR)==0){
					user = this.userService.findUserById(pids.getUserId());
				}else{
					ids.add(pids.getUserId());
				}
			}
			//给创建者发送即将过期提醒
			this.noticeService.addNotice(user.getId(), new Long[]{user.getId()}, NOTICE_MODULE_TYPE.TASK_WARNING_TO_CREATOR, new Object[]{task});
			this.baseService.sendEmail(user.getId(), new Long[]{user.getId()}, NOTICE_MODULE_TYPE.TASK_WARNING_TO_CREATOR, new Object[]{task});
			//给负责人和参与人发送提醒
			if(null!=ids && ids.size()>0 && null!=user){
				this.noticeService.addNotice(user.getId(), ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_WARNING_TO_PARTICIPATOR, new Object[]{task});
				this.baseService.sendEmail(user.getId(), ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_WARNING_TO_PARTICIPATOR, new Object[]{task});
			}
		}
	}
	
	private void sendOverDueNoticeAndMail(List<Task> tasksOverDue){
		for(Task task : tasksOverDue){
			User user = null;
			List<TaskMember> members= this.taskService.getTaskMemberList(task.getId(), null, null);
			Set<Long> ids = new java.util.HashSet<Long>();
			for(TaskMember pids : members){
				if(pids.getType().compareTo(TASK_MEMBER_TYPE.CREATOR)==0){
					user = this.userService.findUserById(pids.getUserId());
				}else{
					ids.add(pids.getUserId());
				}
			}
			//给创建者发送即将过期提醒
			this.noticeService.addNotice(user.getId(), new Long[]{user.getId()}, NOTICE_MODULE_TYPE.TASK_OVERDUE_TO_CREATOR, new Object[]{task});
			this.baseService.sendEmail(user.getId(), new Long[]{user.getId()}, NOTICE_MODULE_TYPE.TASK_OVERDUE_TO_CREATOR, new Object[]{task});
			//给负责人和参与人发送提醒
			if(null!=ids && ids.size()>0 && null!=user){
				this.noticeService.addNotice(user.getId(), ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_OVERDUE_TO_PARTICIPATOR, new Object[]{task});
				this.baseService.sendEmail(user.getId(), ids.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_OVERDUE_TO_PARTICIPATOR, new Object[]{task});
			}
		}
	}
	
	private void sendWarningNoticeAndMailForLeaf(List<TaskLeaf> tasksWarning){
		for(TaskLeaf taskleaf : tasksWarning){
			this.noticeService.addNotice(taskleaf.getManagerUserId(), new Long[]{taskleaf.getManagerUserId()}, NOTICE_MODULE_TYPE.TASK_WARNING_TO_PARTICIPATOR, new Object[]{taskleaf});
			//发送邮件
			this.baseService.sendEmail(taskleaf.getManagerUserId(), new Long[]{taskleaf.getManagerUserId()}, NOTICE_MODULE_TYPE.TASK_WARNING_TO_PARTICIPATOR, new Object[]{taskleaf});
		}
	}
	
	private void sendOverDueNoticeAndMailForLeaf(List<TaskLeaf> tasksOverDue){
		for(TaskLeaf taskleaf : tasksOverDue){
			this.noticeService.addNotice(taskleaf.getManagerUserId(), new Long[]{taskleaf.getManagerUserId()}, NOTICE_MODULE_TYPE.TASK_OVERDUE_TO_PARTICIPATOR, new Object[]{taskleaf});
			//发送邮件
			this.baseService.sendEmail(taskleaf.getManagerUserId(), new Long[]{taskleaf.getManagerUserId()}, NOTICE_MODULE_TYPE.TASK_OVERDUE_TO_PARTICIPATOR, new Object[]{taskleaf});
		}
	}
	
	@SuppressWarnings({ "deprecation" })
	@Scheduled(cron="0 0 1 * * ?")//每天早上1点
	public void repeatTask(){
		//重复性任务
		InetAddress ia=null;
		try {
			ia=ia.getLocalHost();
			String localname=ia.getHostName();
			if(!localname.equals(machineHost)){//只在其中一個執行
				return ;
			}
			log.info("=====================================重复性任务=====================================");
			taskService.saveRepeatTask();
			processService.updateProcessStatusAndProgress();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	//判斷license是否過期
	@SuppressWarnings({ "deprecation" })
	@Scheduled(cron="0 0 0 * * ?")//每天晚上24点
	public void judgeLicense(){
		try {
		    settingService.loadLicense();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@SuppressWarnings({ "deprecation" })
////	@Scheduled(cron="0 0 8 * * ?")//每天早上8点
//	@Scheduled(fixedRate = 600)//每秒
//	public void test(){
//		System.out.println("111");
//	}
	
}
