package org.zywx.cooldev.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.dao.builder.PluginDao;
import org.zywx.cooldev.dao.builder.PluginVersionDao;
import org.zywx.cooldev.entity.builder.Plugin;
import org.zywx.cooldev.entity.builder.PluginVersion;
import org.zywx.cooldev.util.ProcessClearStream;

public class BuilderPushToGitRepo extends Thread implements InitializingBean,ApplicationContextAware,Runnable{

	private static ApplicationContext applicationContext;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private PluginDao pluginDao;
	@Autowired
	private PluginVersionDao pluginVersionDao;
	
	private PluginVersion version;
	
	private String shellPath;
	
	private String buildRepoPath;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.info("---load BuilderPushToGitRepo applicationContext begin---");
		BuilderPushToGitRepo.applicationContext = applicationContext;
		log.info("---load BuilderPushToGitRepo applicationContext over---");
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		System.setProperty("sun.jnu.encoding","utf-8");
		System.setProperty("file.encoding","utf-8");
		log.info("=========BuilderPushToGitRepo init success=========");
	}
	
	public String execShell(String command) {          
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        StringBuffer errorStream = new StringBuffer();
        try {  
            Process p = run.exec(command);
            new ProcessClearStream(p.getInputStream(),"BuilderPushToGitRepo-INFO",ret).start();
//            new ProcessClearStream(p.getErrorStream(),"BuilderPushToGitRepo-ERROR",errorStream).start();
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			// 打印信息
			String line = null;
			while ((line = br.readLine()) != null) {
				errorStream.append(line+"\n\r");
			}
			p.getErrorStream().close();
			br.close();
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return errorStream.toString();
 	}
	
	@Override
	public void run(){
		log.info(" git_push plugins to server.");
		long timeStart = System.currentTimeMillis();
		log.info("shellPath:"+shellPath+",buildRepoPath:"+buildRepoPath);
		BuilderPushToGitRepo builderPushToGitRepo = (BuilderPushToGitRepo)applicationContext.getBean("builderPushToGitRepo");
		if(null==version){
			log.info("version is null");
		}
		log.info("version object--version.getPluginId()=>"+version.getPluginId()+",version.getAbsFilePath()="+version.getAbsFilePath());
		if(version == null || version.getAbsFilePath() == null) {
			log.info("can not find version or version.getAbsFilePath()");
			return;
		}
		
		Plugin plugin = builderPushToGitRepo.pluginDao.findOne(version.getPluginId());
		log.info("version.getPluginId()--->"+version.getPluginId());
		if(plugin == null) {
			log.info("can not find plugin");
			return;
		}
		
		String gitRepoName = String.format("pluginVersion_%d_%s_%s_%s_%s",
				version.getId(), plugin.getType(), version.getOsType(), version.getVersionNo(), plugin.getEnName());
		
		String cmd = String.format("sh "+shellPath+"coopdev_git/add_file.sh %s %s %s", version.getAbsFilePath(), gitRepoName, buildRepoPath);
		log.info("execute cmd--> "+ cmd);
		String errorInfo = builderPushToGitRepo.execShell(cmd);
		log.info("plugin_up to git--->"+errorInfo);
		if(errorInfo.contains("fatal:")){
			builderPushToGitRepo.execShell("cd "+buildRepoPath+" && echo y | rm ./.git/index.lock");
			errorInfo = builderPushToGitRepo.execShell(cmd);
			if(errorInfo.contains("fatal:")){
				version.setUploadStatus(UploadStatus.FAILED);
				builderPushToGitRepo.pluginVersionDao.save(version);
				log.error("git_add_plugin_failed!"+version.getAbsFilePath());
			}else{
				version.setUploadStatus(UploadStatus.SUCCESS);
				builderPushToGitRepo.pluginVersionDao.save(version);
			}
		}else{
			version.setUploadStatus(UploadStatus.SUCCESS);
			builderPushToGitRepo.pluginVersionDao.save(version);
			long timeEnd = System.currentTimeMillis();
			log.info("PluginService -> addPluginVersion ["+version.toStr()+"] to remoteRepository -> totalTime -> [" + ( timeEnd -  timeStart) +"ms]");
			log.info(" add plugin "+gitRepoName+" to git remote repository success .and plugin["+version.getAbsFilePath()+"]"+" upload successed!");
		}
		return;
	}
	
	public BuilderPushToGitRepo() {
		super();
	}

	public BuilderPushToGitRepo(PluginVersion version, String shellPath, String buildRepoPath) {
		super();
		this.version = version;
		this.shellPath = shellPath;
		this.buildRepoPath = buildRepoPath;
	}

	public PluginVersion getVersion() {
		return version;
	}

	public void setVersion(PluginVersion version) {
		this.version = version;
	}

	public String getShellPath() {
		return shellPath;
	}

	public void setShellPath(String shellPath) {
		this.shellPath = shellPath;
	}

	public String getBuildRepoPath() {
		return buildRepoPath;
	}

	public void setBuildRepoPath(String buildRepoPath) {
		this.buildRepoPath = buildRepoPath;
	}

	public PluginDao getPluginDao() {
		return pluginDao;
	}

	public void setPluginDao(PluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	public PluginVersionDao getPluginVersionDao() {
		return pluginVersionDao;
	}

	public void setPluginVersionDao(PluginVersionDao pluginVersionDao) {
		this.pluginVersionDao = pluginVersionDao;
	}

	
}
