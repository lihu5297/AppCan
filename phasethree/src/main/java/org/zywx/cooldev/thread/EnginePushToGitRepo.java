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
import org.zywx.cooldev.dao.builder.EngineDao;
import org.zywx.cooldev.entity.builder.Engine;
import org.zywx.cooldev.util.ProcessClearStream;

public class EnginePushToGitRepo extends Thread implements InitializingBean,ApplicationContextAware,Runnable{

	private static ApplicationContext applicationContext;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@Autowired
	private EngineDao engineDao;
	
	private Engine engine;
	
	private String shellPath;
	
	private String buildRepoPath;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.info("---load BuilderPushToGitRepo applicationContext begin---");
		EnginePushToGitRepo.applicationContext = applicationContext;
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
            new ProcessClearStream(p.getInputStream(),"EnginePushToGitRepo-INFO",ret).start();
//            new ProcessClearStream(p.getErrorStream(),"EnginePushToGitRepo-ERROR").start();
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
		
		long timeStart = System.currentTimeMillis();
		log.info("shellPath:"+shellPath+",buildRepoPath:"+buildRepoPath);
		EnginePushToGitRepo enginePushToGitRepo = (EnginePushToGitRepo)applicationContext.getBean("enginePushToGitRepo");
		
		if(engine == null || engine.getAbsFilePath() == null) {
			log.info("engine is null or absfilePath is null");
			return;
		}
		
		String gitRepoName = String.format("engine_%d_%s_%s_%s",
				engine.getId(), engine.getType(), engine.getOsType(), engine.getVersionNo());
		String cmd = String.format("sh "+shellPath+"coopdev_git/add_file.sh %s %s %s", engine.getAbsFilePath(), gitRepoName, buildRepoPath);
		String errorInfo = enginePushToGitRepo.execShell(cmd);
		log.info("engine_add_file_result-->"+errorInfo);
		if(errorInfo.contains("fatal:")){
			enginePushToGitRepo.execShell("cd "+buildRepoPath+" && echo y | rm ./.git/index.lock");
			errorInfo = enginePushToGitRepo.execShell(cmd);
			if(errorInfo.contains("fatal:")){
				engine.setUploadStatus(UploadStatus.FAILED);
				enginePushToGitRepo.engineDao.save(engine);
				log.error("git_add_engine_failed!"+engine.getAbsFilePath());
			}else{
				engine.setUploadStatus(UploadStatus.SUCCESS);
				enginePushToGitRepo.engineDao.save(engine);
			}
			
		}else{
			engine.setUploadStatus(UploadStatus.SUCCESS);
			enginePushToGitRepo.engineDao.save(engine);
			
			long timeEnd = System.currentTimeMillis();
			log.info("EngineService -> addEngine ["+engine.toStr()+"] to remoteRepository -> totalTime -> [" + ( timeEnd -  timeStart) +"ms]");
			
			log.info(" add engine "+gitRepoName+" to git remote repository success ");
		}
		
		return;
	}
	
	public EnginePushToGitRepo() {
		super();
	}

	public EnginePushToGitRepo(Engine engine, String shellPath, String buildRepoPath) {
		super();
		this.engine = engine;
		this.shellPath = shellPath;
		this.buildRepoPath = buildRepoPath;
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

	public EngineDao getEngineDao() {
		return engineDao;
	}

	public void setEngineDao(EngineDao engineDao) {
		this.engineDao = engineDao;
	}

	public Engine getEngine() {
		return engine;
	}

	public void setEngine(Engine engine) {
		this.engine = engine;
	}
	
	
}
