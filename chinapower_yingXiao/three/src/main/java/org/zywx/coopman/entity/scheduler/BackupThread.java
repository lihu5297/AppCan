package org.zywx.coopman.entity.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.zywx.coopman.dao.BackupLogDao;
import org.zywx.coopman.entity.BackupLog;
import org.zywx.coopman.system.InitBean;
import org.zywx.coopman.util.FileUtil;
import org.zywx.coopman.util.ZipUtil;

public class BackupThread extends Thread implements InitializingBean,ApplicationContextAware,Runnable{
	
	private static ApplicationContext applicationContext;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	protected JdbcTemplate jdbcTpl;
	
	@Autowired
	protected BackupLogDao backupLogDao;

	private String shellBasePath;
	private String hostIp;
	private String port;
	private String dbUsr;
	private String dbPwd;
	private String databaseName;
	private String filePath;
	private String backupTmpPath;
	private String rootpath;
	private String backupDatePath;
	private String dateTime;
	private Long backupId;
	
	
	
	public BackupThread() {
		super();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.info("---load BackupThread applicationContext begin---");
		BackupThread.applicationContext = applicationContext;
		log.info("---load BackupThread applicationContext over---");
		
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		System.setProperty("sun.jnu.encoding","utf-8");
		System.setProperty("file.encoding","utf-8");
		log.info("=========BackupThread init success=========");
	}
	
	public String execShell(String command) {          
        Runtime run = Runtime.getRuntime();  
        String ret = "";
        try {  
            Process p = run.exec(command);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {
            	ret += (line + "\n\r");
            	System.out.println(line);
            }
            p.waitFor();
            in.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret;
 	}
	
	@Override
	public void run() {
		
		try{
			BackupThread backupThread = (BackupThread)applicationContext.getBean("backupThread");
			
			String shell_dataBase = shellBasePath+"/coopdev_bak/backupData.sh";
			String shell_database = "sh "+shell_dataBase +" "+ hostIp + " " + port + " " + dbUsr + " " + dbPwd + " " + databaseName +" " +filePath ;
			String result = backupThread.execShell(shell_database);
			log.info("shell_database:" + shell_database + "\n result:" + result);
			
			//备份资源
			String shell_resource = "zip -x "+rootpath+"/backup/**\\* -p -r "+ backupTmpPath +databaseName+"_resource.zip "+rootpath;
			String res = backupThread.execShell(shell_resource);
			log.info("shell_resource:" + shell_resource +"\n res：\n"+res);
	
			//git 暂不备份
			// TODO
			
			String show_shell = "ls " + backupTmpPath;
			log.info("show_shell:" + show_shell);
			String show = backupThread.execShell(show_shell);
			log.info("backup information:\n directory:\n"+ show);
	
			//压缩文件
			ZipUtil.zip(backupTmpPath, backupDatePath + dateTime + ".zip");
			
			// 删除临时数据
			File file = new File(backupTmpPath);
			if(file.exists()){
				FileUtil.deleteDir(file);
			}
			
			backupThread.jdbcTpl.update(" update T_MAN_BACKUP_LOG set status = 1 where id = "+backupId);
			
			//只保留4次备份数据  删除最早的数据;
			final BackupLog deleteOne = new BackupLog();
			backupThread.jdbcTpl.query("select b.* from T_MAN_BACKUP_LOG b order by createdAt DESC limit 4,1 ",
					new RowCallbackHandler() {

					@Override
					public void processRow(ResultSet rs) throws SQLException {
						deleteOne.setId(rs.getLong("id"));
						deleteOne.setBackupFileName(rs.getString("backupfileName"));
					}
				
			});
			String backUpFilename = deleteOne.getBackupFileName();
			File deleteFile = new File(backUpFilename);
			if(deleteFile.exists()){
				deleteFile.delete();
			}
			backupThread.jdbcTpl.update("delete from T_MAN_BACKUP_LOG where id="+deleteOne.getId());
			
		}catch(Exception e){
			
			e.printStackTrace();
			
		}
		
	}
	
	public BackupThread(String shellBasePath, String hostIp, String port, String dbUsr,
			String dbPwd, String databaseName, String filePath, String backupTmpPath, String rootpath,
			String backupDatePath, String dateTime, Long backupId) {
		super();
		this.shellBasePath = shellBasePath;
		this.hostIp = hostIp;
		this.port = port;
		this.dbUsr = dbUsr;
		this.dbPwd = dbPwd;
		this.databaseName = databaseName;
		this.filePath = filePath;
		this.backupTmpPath = backupTmpPath;
		this.rootpath = rootpath;
		this.backupDatePath = backupDatePath;
		this.dateTime = dateTime;
		this.backupId = backupId;
	}

	public String getShellBasePath() {
		return shellBasePath;
	}

	public void setShellBasePath(String shellBasePath) {
		this.shellBasePath = shellBasePath;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbUsr() {
		return dbUsr;
	}

	public void setDbUsr(String dbUsr) {
		this.dbUsr = dbUsr;
	}

	public String getDbPwd() {
		return dbPwd;
	}

	public void setDbPwd(String dbPwd) {
		this.dbPwd = dbPwd;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getBackupTmpPath() {
		return backupTmpPath;
	}

	public void setBackupTmpPath(String backupTmpPath) {
		this.backupTmpPath = backupTmpPath;
	}

	public String getRootpath() {
		return rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}

	public String getBackupDatePath() {
		return backupDatePath;
	}

	public void setBackupDatePath(String backupDatePath) {
		this.backupDatePath = backupDatePath;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public Long getBackupId() {
		return backupId;
	}

	public void setBackupId(Long backupId) {
		this.backupId = backupId;
	}
	
	
	
	
}
