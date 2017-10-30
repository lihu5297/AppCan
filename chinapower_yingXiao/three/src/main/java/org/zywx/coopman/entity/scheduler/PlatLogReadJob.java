package org.zywx.coopman.entity.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zywx.coopman.dao.PlatFormLogDao;
import org.zywx.coopman.entity.DailyLog.PlatFormLog;
import org.zywx.coopman.service.PlatFormLogService;
import org.zywx.coopman.service.SettingService;
import org.zywx.coopman.util.EditQuartzJob;

public class PlatLogReadJob implements Job,InitializingBean,ApplicationContextAware {
	
	private static Pattern pattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2})\\s([0-9]{2}):([0-9]{2}):([0-9]{2})");
	
	private static String regex = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
	
	@Value("${logDir}")
	private String logDir;

	@Value("${logFirstName}")
	private String logFirstName;

	@Value("${grepString}")
	private String grepString;

	@Value("${grepException}")
	private String grepException;

	@Value("${grepError}")
	private String grepError;
	
	@Value("${shellBasePath}")
	private String shellBasePath;
	
	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private PlatFormLogService platFormLogService;
	
	@Autowired
	private PlatFormLogDao platFormLogDao;
	
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("---load PlatLogReadJob begin---");
		PlatLogReadJob.applicationContext = applicationContext;
		
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
			log.info("start statistic platform logs!");
			int exceptionnum = 0;
			int errornum = 0;
			List<PlatFormLog> platformlogs = new ArrayList<>();
			
			PlatLogReadJob platLogReadJob = (PlatLogReadJob)applicationContext.getBean("platLogReadJob");
			Map<String,String> hostInfo = platLogReadJob.settingService.getHostInfo();
			Map<String,String> grepInfo = platLogReadJob.platFormLogService.getGrepAttributes();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			String logFiles = platLogReadJob.execShell("sh " + grepInfo.get("shellBasePath") + "/coopdev_disk/findErrorFile.sh " + grepInfo.get("logDir") + " " + grepInfo.get("logFirstName"));

			log.info("cmd: "+"sh " + grepInfo.get("shellBasePath") + "/coopdev_disk/findErrorFile.sh " + grepInfo.get("logDir") + " " + grepInfo.get("logFirstName")+",log files :" + logFiles);
			
			String[] fileNames = logFiles.split("\n");
			for (String filename : fileNames) {
				
				log.info("log filename :" + filename + ";yesterday:" + yesterday);
				if (filename.contains(yesterday)) {
					File file = new File(grepInfo.get("logDir")+filename);
					log.info("log one filename :" + grepInfo.get("logDir")+filename+";isExists :"+file.exists());
					if (file.exists()) {
						String exceptions = platLogReadJob.execShell(String.format(grepInfo.get("grepString"), grepInfo.get("grepException"), grepInfo.get("logDir") + filename));
						log.info("log one filename :" + file.getName() + ";exceptions:" + exceptions);
						String[] exceps = exceptions.split("--");
						for (String exception : exceps) {
							String oneException = "";
							String dateTime = null;
							//首个\t开始的行的标识
							boolean flag = false;
							boolean firstline = true;
							boolean available = false;
							for (String line : exception.split("\n")) {
								line = line.replace("\r", "");
								log.info("log exceptions one line :" + line);
								if (line.toLowerCase().contains("exception") && firstline) {
									oneException += line + "\n";
									firstline = false;
									flag = true;
								} else if(flag){
									oneException += line + "\n";
									if(line.startsWith("\t")) {
										flag = false;
										available = true;
									}
									
								}else if (line.startsWith("\t")) {
									if(!flag){
										oneException += line + "\n";
									}
								}else{
									break;
								}
								
								if(matchDateString(line)!=null){
									dateTime = matchDateString(line);
									log.info("log exceptions' date :" + dateTime);
								}
							}
							Date date = new Date();
							if (dateTime != null) {
								try {
									date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
									log.info("log exceptions one line date:" + date);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
							if(oneException.equals("") || !available){
								continue;
							}
							
							oneException = this.getHtmlException(oneException);
							
							PlatFormLog platFormLog2 = new PlatFormLog();
							platFormLog2.setContent(oneException);
							platFormLog2.setFilename(grepInfo.get("logDir") + filename);
							platFormLog2.setHostName(hostInfo.get("hostname"));
							platFormLog2.setLogDate(new Timestamp(date.getTime()));
							platformlogs.add(platFormLog2);
							
							exceptionnum++;
						}

						String errors = platLogReadJob.execShell(String.format(grepInfo.get("grepString"), grepInfo.get("grepError"), grepInfo.get("logDir") + filename));
						log.info("log one filename :" + file.getName() + ";errors:" + errors);
						String[] erros = errors.split("--");
						for (String error : erros) {
							String oneError = "";
							String dateTime = null;
							//首个\t开始的行的标识
							boolean flag = false;
							boolean firstline = true;
							boolean available = false;
							for (String line : error.split("\n")) {
								line = line.replace("\r", "");
								log.info("log errors one line :" + line);
								if (line.toLowerCase().contains("error") && firstline) {
									oneError += line + "\n";
									firstline = false;
									flag = true;
								} else if(flag){
									oneError += line + "\n";
									if(line.startsWith("\t")) {
										flag = false;
										available = true;
									}
									
								}else if (line.startsWith("\t")) {
									if(!flag){
										oneError += line + "\n";
									}
								}else{
									break;
								}
								
								if(matchDateString(line)!=null){
									dateTime = matchDateString(line);
									log.info("log error' date :" + dateTime);
								}
							}
							Date date = new Date();
							if (dateTime != null) {
								try {
									date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
									log.info("log error one line date:" + date);
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
							if(oneError.equals("") || !available){
								continue;
							}
							
							oneError = this.getHtmlException(oneError);
							
							PlatFormLog platFormLog2 = new PlatFormLog();
							platFormLog2.setContent(oneError);
							platFormLog2.setFilename(grepInfo.get("logDir") + filename);
							platFormLog2.setHostName(hostInfo.get("hostname"));
							platFormLog2.setLogDate(new Timestamp(date.getTime()));
							platformlogs.add(platFormLog2);
							
							errornum++;
						}

					}

				}
			}
			if(!platformlogs.isEmpty()){
				platLogReadJob.platFormLogService.addPlatFormLogs(platformlogs);
			}
			
			log.info(String.format("end statistic platform logs,exception[%d] error[%d]!",exceptionnum,errornum));
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
	}

	private String getHtmlException(String oneException) {
		StringBuffer result = new StringBuffer();
		result.append("<div><p>");
		result.append(oneException.replace("\n", "</p><p>"));
		result.append("</p></div>");
		return result.toString();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.loadQuartzJob();
		System.out.println("---load PlatLogReadJob over---");
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
		PlatLogReadJob platLogReadJob = (PlatLogReadJob)applicationContext.getBean("platLogReadJob");
		Map<String,String> grepInfo = platLogReadJob.platFormLogService.getGrepAttributes();
		EditQuartzJob.addPlatLogReadJob(grepInfo.get("readTimeH"),grepInfo.get("readTimeM"));
		
	}
	
	public String execShell(String command) {          
        Runtime run = Runtime.getRuntime();  
        String ret = "";
        try {  
            Process p = run.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {
            	ret += (line + "\n");
//            	System.out.println(line);
            }
            p.waitFor();
            in.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret;
 	}
	
	private static String matchDateString(String dateStr) {  
		try {  
	           List matches = null;  
	           Pattern p = Pattern.compile("(\\d{1,4}[-|\\/|年|\\.]\\d{1,2}[-|\\/|月|\\.]\\d{1,2}([日|号])?(\\s)*(\\d{1,2}([点|时])?((:)?\\d{1,2}(分)?((:)?\\d{1,2}(秒)?)?)?)?(\\s)*(PM|AM)?)", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);  
	           Matcher matcher = p.matcher(dateStr);  
	           if (matcher.find() && matcher.groupCount() >= 1) {  
	               matches = new ArrayList();  
	               for (int i = 1; i <= matcher.groupCount(); i++) {  
	                   String temp = matcher.group(i);  
	                   matches.add(temp);  
	               }  
	           } else {  
	               matches = Collections.EMPTY_LIST;  
	           }             
	           if (matches.size() > 0) {  
	               return ((String) matches.get(0)).trim();  
	           } else {  
	           }  
	       } catch (Exception e) {  
	           return null;  
	       }  
	    return null;
	   }  
	  
	/** 
	 *  
	 * @param args 
	 */  
	public static void main(String[] args) {  
	      
	    String iSaid = "我们是2014-4-25 19:00:00deshih";  
	      
	    // 匹配时间串  
	    String answer = matchDateString(iSaid);  
	    
	    System.out.println("问：请问我们是什么时候？");  
	    System.out.println("答：" + answer);  
	      
	}
	
}
