package org.zywx.coopman.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.zywx.coopman.entity.QueryEntity;
import org.zywx.coopman.entity.DailyLog.PlatFormLog;

@Service
public class PlatFormLogService extends BaseService{

	@Autowired
	private SettingService settingService;

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
	
	@Value("${grep.readTimeH}")
	private String readTimeH;
	
	@Value("${grep.readTimeM}")
	private String readTimeM;

	@Value("${shellBasePath}")
	private String shellBasePath;
	
	public Map<String,String> getGrepAttributes() {
		Map<String,String> grepInfo = new HashMap<>();
		grepInfo.put("logDir", logDir);
		grepInfo.put("logFirstName", logFirstName);
		grepInfo.put("grepString", grepString);
		grepInfo.put("grepException", grepException);
		grepInfo.put("grepError", grepError);
		grepInfo.put("readTimeH", readTimeH);
		grepInfo.put("readTimeM", readTimeM);
		grepInfo.put("shellBasePath", shellBasePath);
		return grepInfo;
	}

	public void updatePlatFormLogFromOneFile() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String dateTomorrow = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
		String logFiles = this.execShell("sh " + shellBasePath + "/coopdev_disk/findErrorFile.sh " + logDir + " statistic");
		String[] fileNames = logFiles.split("\n");
		for (String filename : fileNames) {
			if (filename.contains(dateTomorrow)) {
				File file = new File(logDir, filename);
				if (file.exists()) {
					String exception = "";
					FileInputStream in;
					byte[] b = new byte[] {};

					try {
						in = new FileInputStream(file);
						b = new byte[in.available()]; // 新建一个字节数组
						in.read(b); // 将文件中的内容读取到字节数组中
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					exception = new String(b); // 再将字节数组中的内容转化成字符串形式输出
					
					if(exception.equals("")){
						continue;
					}
					
					Map<String, String> hostInfo = this.settingService.getHostInfo();
					
					PlatFormLog platFormLog = new PlatFormLog();
					platFormLog.setContent(exception);
					platFormLog.setFilename(logDir + filename);
					platFormLog.setHostName(hostInfo.get("hostname"));
					platFormLog.setLogDate(new Timestamp(System.currentTimeMillis()));
					this.platFormLogDao.save(platFormLog);

				}

			}
		}

	}

	public Page<PlatFormLog> getPlatFormLog(QueryEntity queryEntity) {
		Pageable page = new PageRequest(queryEntity.getPageNo() - 1, queryEntity.getPageSize(),
				new Sort(Direction.DESC, "logDate"));
		Page<PlatFormLog> pageList = this.platFormLogDao.findAll(page);
		return pageList;
	}

	public static void updatePlatFormLogfile() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		String exceptions = null;
		File file = new File("D:\\usr\\bin\\coopdev_disk\\log4j.log");
		if (file.exists()) {
			String license = null;
			FileInputStream in;
			byte[] b = new byte[] {};

			try {
				in = new FileInputStream(file);
				b = new byte[in.available()]; // 新建一个字节数组
				in.read(b); // 将文件中的内容读取到字节数组中
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			license = new String(b); // 再将字节数组中的内容转化成字符串形式输出
			exceptions = license;
		}

		String[] exceps = exceptions.split("--");
		for (String exception : exceps) {
			String oneException = "";
			String dateTime = null;
			boolean flag = false;
			for (String line : exception.split("\n")) {
				if (line.toLowerCase().contains("exception") && !line.startsWith("\t")  && line.startsWith("2")) {
					oneException = line + "\n" + oneException;
					dateTime = line.substring(0, 19);
					flag = true;
				} else if(flag){
					oneException += line + "\n";
					flag = false;
				}else if (line.startsWith("\t")) {
					oneException += line + "\n";
					
				}
				
			}
			Date date = new Date();
			if (dateTime != null) {
				try {
					date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.print(date);
			System.out.println(oneException);
		}

	}
	
	public  static void main(String args[]){
		PlatFormLogService.updatePlatFormLogfile();	
	}

	
	public void addPlatFormLogs(List<PlatFormLog> platFormLogs) {
		this.platFormLogDao.save(platFormLogs);
		
	}
	

}
