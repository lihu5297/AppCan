package org.zywx.coopman.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.license.LicenseCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.zywx.appdo.common.utils.email.MyAuthenticator;
import org.zywx.coopman.commons.Enums.AUTH_STATUS;
import org.zywx.coopman.commons.Enums.BACKUP_STATUS;
import org.zywx.coopman.commons.Enums.EMAIL_SERVER_TYPE;
import org.zywx.coopman.commons.Enums.INTEGRATE_STATUS;
import org.zywx.coopman.entity.BackupLog;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.entity.scheduler.BackupThread;
import org.zywx.coopman.system.InitBean;
import org.zywx.coopman.util.EditQuartzJob;

@Service
public class SettingService extends BaseService {
	
	@Autowired
	private ManagerService managerService;


	@Value("${license}")
	private String license;
	@Value("${license.ip}")
	private String ip;
	@Value("${license.mac}")
	private String mac;
	@Value("${telnet.mail}")
	private String telnetStr;
	
	@Value("${license.productName}")
	private String productName;

	@Value("${hibernate.databaseName}")
	private String databaseName;

	@Value("${resource.baseDir}")
	private String resourceBaseDir;
	
	@Value("${hibernate.dbUsr}")
	private String dbUsr;

	@Value("${hibernate.host}")
	private String host;
	
	@Value("${hibernate.host.ip}")
	private String hostIp;

	@Value("${hibernate.port}")
	private String port;

	@Value("${hibernate.dbPwd}")
	private String dbPwd;
	
	/**
	 * 机器的IP 运行定时任务时候,只在集群中的一台执行
	 */
//	@Value("${machineHost}")
//	private String machineHost;
	
	@Value("${shellBasePath}")
	private String shellBasePath;
	
	@Value("${rootpath}")
	private String rootpath;
	
	public Setting getSetting() {
		Iterator<Setting> set = this.settingDao.findAll().iterator();
		return set.hasNext() ? set.next() : new Setting();
	}

	public Setting updateSetting(Setting set, String info) {
		if (null == set.getId() || -1 == set.getId()) {
			return this.settingDao.save(set);
		}
		Setting setting = this.settingDao.findOne(set.getId());
		if (info.equals("INFO")) {
			
			Map<String, String> map = this.managerService.getProperties();
	        String realPath = map.get("logo");
	        File file = new File(realPath+"logo.png");
	        File file1 = new File(realPath+"logo1.png");
	        if(file.exists()){
	        	file.delete();
	        }
	        if(file1.exists()){
	        	file1.renameTo(file);
	        }
	        
			setting.setPlatLogo(map.get("logoUri")+"logo.png");
			setting.setPlatName(set.getPlatName());
			setting.setWebAddr(set.getWebAddr());

		} else if (info.equals("BACKUP")) {
			setting.setPlatExecuteTime_hour(set.getPlatExecuteTime_hour());
			setting.setPlatExecuteTime_minutes(set.getPlatExecuteTime_minutes());
			setting.setPlatInterval(set.getPlatInterval());
			setting.setPlatBackupPath(set.getPlatBackupPath());

			long day = setting.getPlatInterval();
			int hour = setting.getPlatExecuteTime_hour();
			int minutes = setting.getPlatExecuteTime_minutes();
			
			EditQuartzJob.editQuartzJob(day,hour,minutes);

		} else if (info.equals("EMAIL")) {
			setting.setEmailAccount(set.getEmailAccount());
			setting.setEmailPassword(set.getEmailPassword());
			setting.setEmailServerPort(set.getEmailServerPort());
			setting.setEmailServerType(set.getEmailServerType());
			setting.setEmailServerUrl(set.getEmailServerUrl());
			setting.setEmailServerStatus(set.getEmailServerStatus());

		} else if (info.equals("SYS")) {
			setting.setSYSdoMain(set.getSYSdoMain());
			setting.setSYSIntegrateTime(set.getSYSIntegrateTime());
			setting.setSYSKey(set.getSYSKey());
			setting.setSYSStatus(set.getSYSStatus());

		} else if (info.equals("EMM")) {
			setting.setEMMAccessUrl(set.getEMMAccessUrl());
			setting.setEMMAndroidPushUrl(set.getEMMAndroidPushUrl());
			setting.setEMMDataReportUrl(set.getEMMDataReportUrl());
			setting.setEMMDataStatisticUrl(set.getEMMDataStatisticUrl());
			setting.setEMMPushBindUrl(set.getEMMPushBindUrl());
			setting.setEMMDeviceManageUrl(set.getEMMDeviceManageUrl());
			setting.setEMMContentManageUrl(set.getEMMContentManageUrl());

		} else if (info.equals("EMMTEST")) {
			setting.setEMMTestAccessUrl(set.getEMMTestAccessUrl());
			setting.setEMMTestAndroidPushUrl(set.getEMMTestAndroidPushUrl());
			setting.setEMMTestDataReportUrl(set.getEMMTestDataReportUrl());
			setting.setEMMTestDataStatisticUrl(set.getEMMTestDataStatisticUrl());
			setting.setEMMTestPushBindUrl(set.getEMMTestPushBindUrl());
			setting.setEMMTestDeviceManageUrl(set.getEMMTestDeviceManageUrl());
			setting.setEMMTestContentManageUrl(set.getEMMTestContentManageUrl());

		} else if (info.equals("AUTH")) {
			setting.setAuthorizePath(set.getAuthorizePath());

			String licenseStr = this.getLicenseStr(set.getAuthorizePath());
			String keyMD5 = LicenseCreator.getKeyMD5(ip, mac, productName);
			licenseStr = LicenseCreator.decLicense(licenseStr, keyMD5);
			net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(licenseStr);

			setting.setAuthDeadTime(job.get("DATE").toString());
			setting.setAuthStatus(AUTH_STATUS.EFFECTIVE);
		}
		InitBean.refreshCache();
		return this.settingDao.save(setting);
	}

	private String getLicenseStr(String authorizePath) {
		File file = new File(authorizePath);
		if (file.exists()) {
			String license = null;
			FileInputStream in;
			byte[] b = new byte[] {};

			try {
				in = new FileInputStream(authorizePath);
				b = new byte[in.available()]; // 新建一个字节数组
				in.read(b); // 将文件中的内容读取到字节数组中
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			license = new String(b); // 再将字节数组中的内容转化成字符串形式输出
			return license;
		}
		return null;
	}

	public Setting updateStatus(INTEGRATE_STATUS sYSStatus, Long id) {
		Setting set = this.settingDao.findOne(id);
		set.setSYSStatus(sYSStatus);
		set = this.settingDao.save(set);
		return set;
	}

	public void updateKey(String password, Long id) {
		// TODO Auto-generated method stub

	}

	public boolean testEmail(Setting setting) {
		String telnet = setting.getEmailServerUrl();
		log.info("telnet " + telnet);
		String result = this.execShell("sh " + telnetStr + "/coopdev_disk/checkMailServer.sh " + telnet + " " + setting.getEmailServerPort());
		log.info("result:" + result);
		if (result.contains("OK") || result.contains("Connected")) {
			return true;
		}
		return false;
	}

	public boolean testPersonalEmail(Setting setting) {
		String smtpServer = setting.getEmailServerUrl();
		String protocol = "smtps";
		String user = setting.getEmailAccount();
		String pwd = setting.getEmailPassword();

		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", protocol);
		props.setProperty("mail.smtps.host", smtpServer);

		Session session = Session.getInstance(props);
		session.setDebug(true);

		Transport transport;
		try {
			transport = session.getTransport();
			transport.connect(smtpServer, user, pwd);

			boolean connected = transport.isConnected();
			System.out.println("connected:" + connected);
			transport.close();
			if (connected) {
				return true;
			}

		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return false;

	}

	public boolean testPersonalEmail1(Setting setting) {
		String telnet = this.getserverType(setting.getEmailServerType()) + "." + setting.getEmailServerUrl();
		String res = this.execShell("sh /home/test.sh " + setting.getEmailAccount() + " " + setting.getEmailPassword()
				+ " " + telnet + " " + setting.getEmailServerPort());

		if (res.split("OK").length >= 3) {
			return true;
		}
		return false;
	}

	private String getserverType(EMAIL_SERVER_TYPE emailServerType) {
		String type = "";
		switch (emailServerType) {
		case POP3:
			type = "pop";
			break;
		case SMTP:
			type = "smtp";
			break;
		case IMAP:
			type = "imap";
			break;
		default:
			type = "pop";
			break;

		}
		return type;
	}

	public static void sendTxtMail(Setting setting) {
		Properties props = new Properties();

		Session session = Session.getInstance(props, null);
		session.setDebug(true);// 打开debug模式，会打印发送细节到console
		Message message = new MimeMessage(session); // 实例化一个MimeMessage集成自abstract
													// Message 。参数为session
		try {
			message.setFrom(new InternetAddress(setting.getEmailAccount())); // 设置发出方,使用setXXX设置单用户，使用addXXX添加InternetAddress[]

			message.setText("邮箱测试邮件，来自自己（" + setting.getEmailAccount() + "）的邮件"); // 设置文本内容
			// 单一文本使用setText,Multipart复杂对象使用setContent

			message.setSubject("邮箱测试邮件"); // 设置标题

			message.setRecipient(Message.RecipientType.TO, new InternetAddress(setting.getEmailAccount())); // 设置接收方

			/**
			 * 使用静态方法每次发送需要建立一个到smtp服务器的链接，你可以手动控制连接状态
			 * ，通过session获得tansport，连接到mailserver，而session就可以使用Session.
			 * getDefaultInstance(props,null);获得
			 */
			Transport transport = session.getTransport(setting.getEmailServerType().toString().toLowerCase());
			transport.connect(setting.getEmailServerType().toString().toLowerCase() + setting.getEmailServerUrl(),
					setting.getEmailAccount(), setting.getEmailPassword());
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void sendTxtMailPOP3(Setting setting) {
		Properties props = new Properties();
		props.put("mail." + setting.getEmailServerType().toString().toLowerCase() + ".host",
				setting.getEmailServerType().toString().toLowerCase() + "." + setting.getEmailServerUrl()); // smtp服务器地址

		props.put("mail." + setting.getEmailServerType().toString().toLowerCase() + ".auth", true); // 是否需要认证

		/**
		 * 实例化一个验证里，继承abstract Authenticator 实现 protected PasswordAuthentication
		 * getPasswordAuthentication(){ return new
		 * PasswordAuthentication(userName,password); }
		 */
		MyAuthenticator myauth = new MyAuthenticator(setting.getEmailAccount(), setting.getEmailPassword());
		// 获得一个带有authenticator的session实例
		Session session = Session.getInstance(props, myauth);
		session.setDebug(true);// 打开debug模式，会打印发送细节到console
		Message message = new MimeMessage(session); // 实例化一个MimeMessage集成自abstract
													// Message 。参数为session
		try {
			message.setFrom(new InternetAddress(setting.getEmailAccount())); // 设置发出方,使用setXXX设置单用户，使用addXXX添加InternetAddress[]

			message.setText("只是一个简简单单的文本内容哟！"); // 设置文本内容
												// 单一文本使用setText,Multipart复杂对象使用setContent

			message.setSubject("只是简简单单的文本标题哟！"); // 设置标题

			message.setRecipient(Message.RecipientType.TO, new InternetAddress("liujiexiong10@sina.com")); // 设置接收方

			Transport.send(message); // 使用Transport静态方法发送邮件

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("static-access")
	public Map<String, String> getHostInfo() {
		HashMap<String, String> map = new HashMap<>();

		InetAddress inetaddress = null;
		try {
			inetaddress = inetaddress.getLocalHost();

			String localname = inetaddress.getHostName();
			String localip = inetaddress.getHostAddress();
			log.info("本机名称是：" + localname);
			log.info("本机的ip是 ：" + localip);
			log.info("this server`s inetaddress:" + inetaddress);

			map.put("hostname", localname);
			map.put("ip", localip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	public static void main(String args[]) {
		SettingService ss = new SettingService();
		ss.getHostInfo();
	}

	/**
	 * 
	 * @describe 备份：数据库数据、git数据、资源（协同资源和打包资源）<br>
	 * @author jiexiong.liu <br>
	 * @date 2015年10月9日 下午7:30:47 <br>
	 * @param hostInfo
	 *            <br>
	 * @throws Exception 
	 * @returnType void
	 *
	 */
	public BackupLog updateBackup(Map<String, String> hostInfo) throws Exception {

		try{
			
			log.info("backup is starting ,hostInfo:" + hostInfo);
			Setting setting = this.getSetting();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy_MM_dd_HHmmssS");
			String date = sdf.format(Calendar.getInstance().getTime());
			String dateTime = sdf1.format(Calendar.getInstance().getTime());
			
			String backupDatePath = setting.getPlatBackupPath() + "/" + date + "/";
			String backupTmpPath = setting.getPlatBackupPath() + "/" + date + "/tmp/";
			
			String cmd = "sh " + shellBasePath + "/coopdev_disk/statistic_disk.sh";
			String diskstatistic = this.execShell(cmd);
//			String diskstatistic = "文件系统	      容量  已用  可用 已用%% 挂载点"
//					+ "\n/dev/mapper/VolGroup-lv_root   50G   30G   17G  64% /"
//					+ "\ntmpfs                 3.9G   12K  3.9G   1% /dev/shm"
//					+ "\n/dev/sda1             485M   37M  423M   8% /boot"
//					+ "\n/dev/mapper/VolGroup-lv_home   42G  4.6G   35G  12% /home";	
			Map<String, String> result = this.analysisResult(diskstatistic);
			log.info("result:"+result);
			String pathTmp = "";
			String sizeTmp = "";
			
			for(String str : result.keySet()){
				if(backupDatePath.startsWith(str)){
					if(pathTmp.equals("") || pathTmp.length()<str.length()){
						pathTmp = str;
						sizeTmp = result.get(str);
					}
				}
			}
			
			String duCMD = " du "+rootpath+" -sh ";
			String sizeToBackup = this.execShell(duCMD);
//			String sizeToBackup = "14G	/usr/local";			
			String size = sizeToBackup.split("\t")[0];
			if(null!=size && !"".equals(size) && null!=sizeTmp && !"".equals(sizeTmp)){
				log.info(String.format("备份需要空间大小：[%s],可用空间大小：[%s]",size,sizeTmp));
				//换作相同单位进行比较
				double SIZETMP = 0;
				double SIZE = 0;
				String unit = "";
				if(sizeTmp.toLowerCase().contains("g")){
					SIZETMP = Double.parseDouble(sizeTmp.toLowerCase().replace("g", ""));
					if(size.toLowerCase().contains("g")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("g", ""));
						unit = "g";
					}else if(size.toLowerCase().contains("m")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("m", ""));
						SIZETMP *= 1024;
						unit = "m";
					}else if(size.toLowerCase().contains("t")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("t", ""));
						SIZE *= 1024;
						unit = "g";
					}else{
						SIZE = Double.parseDouble(size.toLowerCase().replace("k", ""));
						unit = "";
					}
						
				}else if(sizeTmp.toLowerCase().contains("m")){
					SIZETMP = Double.parseDouble(sizeTmp.toLowerCase().replace("m", ""));
					if(size.toLowerCase().contains("g")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("g", ""));
						SIZE = SIZE * 1024;
						unit = "m";
					}else if(size.toLowerCase().contains("m")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("m", ""));
						unit = "m";
					}else if(size.toLowerCase().contains("t")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("t", ""));
						SIZE = SIZE * 1024 * 1024;
						unit = "m";
					}else{
						SIZE = Double.parseDouble(size.toLowerCase().replace("k", ""));
						unit = "";
					}
				}else if(sizeTmp.toLowerCase().contains("t")){
					SIZETMP = Double.parseDouble(sizeTmp.toLowerCase().replace("t", ""));
					if(size.toLowerCase().contains("g")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("g", ""));
						SIZETMP = SIZETMP * 1024;
						unit = "g";
					}else if(size.toLowerCase().contains("m")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("m", ""));
						SIZETMP = SIZETMP * 1024 * 1024;
						unit = "m";
					}else if(size.toLowerCase().contains("t")){
						SIZE = Double.parseDouble(size.toLowerCase().replace("t", ""));
						unit = "t";
					}else{
						SIZE = Double.parseDouble(size.toLowerCase().replace("k", ""));
						unit = "";
					}
						
				}
				
				if(SIZETMP < SIZE){
					throw new Exception("空间不够,备份路径至少还需要"+(SIZE-SIZETMP)+unit);
				}
			}
			
			File fileA = new File(backupDatePath);
			File fileB = new File(backupTmpPath);
			if (!fileA.exists()) {
				fileA.mkdirs();
			}
			if (!fileB.exists()) {
				fileB.mkdirs();
			}
			
			// 备份数据库
			String filePath = backupTmpPath + databaseName + "_backup.sql";
			File backup = new File(filePath);
			if(!backup.exists()){
				backup.createNewFile();
			}
				
			BackupLog backupLog = new BackupLog();
			backupLog.setBackupPath(backupDatePath);
			backupLog.setServerIp(hostInfo.get("ip"));
			backupLog.setServerName(hostInfo.get("hostname"));
			backupLog.setBackupFileName(backupDatePath + dateTime + ".zip");
			backupLog.setBackupDetail(date+"之前的数据备份，包括：数据库数据和表结构，上传的资源，插件、引擎、文档等");
			backupLog.setStatus(BACKUP_STATUS.ONGOING);
			this.backupLogDao.save(backupLog);
	
			Thread thread = new BackupThread(shellBasePath, hostIp, port, dbUsr,
			dbPwd, databaseName, filePath, backupTmpPath, rootpath,
			backupDatePath, dateTime, backupLog.getId());
			thread.start();
			
			return backupLog;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, String> getProperties() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("license", license);
		return map;
	}
	
	private Map<String, String> analysisResult(String diskStatistic) {
		
		Map<Integer, String> head = new HashMap<>();
		List<Map<Integer, String>> list = new ArrayList<>();
		log.info("diskStatistic:\n"+diskStatistic);
		String tmp[] = diskStatistic.split("\n");
		int flag = 0;
		for(String str : tmp){
			if(str.trim()!=""){
				str = str.trim();
				int count = 0;
				Map<Integer, String> map = new HashMap<>();
				String line[] = str.split(" ");
				for(String node : line){
					if(node.trim().length()==0)
						continue;
					++count;
					if(flag==0){
						head.put(count, node);
					}else
						map.put(count, node);
				}
				if(flag!=0 && !map.isEmpty()){
					list.add(map);
				}
				++flag;
			}
		}
		
		return this.dealStatistic(list,head);
	}

	private Map<String, String> dealStatistic(List<Map<Integer, String>> list, Map<Integer, String> head) {
		Map<String, String> statistic = new HashMap<>();
		List<Map<Integer, String>> result = new ArrayList<>();
		
		for(Map<Integer, String> map : list){
			Map<Integer, String> res = new HashMap<>();
			for(int node : head.keySet()){
				res.put(node, map.get(node));
			}
			if(!res.isEmpty()){
				result.add(res);
			}
		}
		
		for(Map<Integer, String> map : result){
			//获取可使用的空间大小
			statistic.put(map.get(6),map.get(4));
		}
		return statistic;
	}

	public Setting updateSetting(Setting set) {
		Setting save = settingDao.save(set);
		return save;
	}

}
