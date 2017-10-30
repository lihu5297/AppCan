package org.zywx.cooldev.daemon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.zywx.cooldev.util.HttpUtil;

public class NotifyPullThread extends Thread implements InitializingBean,ApplicationContextAware,Runnable{
	
	
	private static ApplicationContext applicationContext;
	
	
	@Autowired
	private JdbcTemplate jdbcTpl;
	
	
	public JdbcTemplate getJdbcTpl() {
		return jdbcTpl;
	}

	public void setJdbcTpl(JdbcTemplate jdbcTpl) {
		this.jdbcTpl = jdbcTpl;
	}

	private String cmd;
	
	private Long appId;
	
	private String gitShellServer;//调用idc机房服务
	
	private String callBackService;//回调地址
	

	public String getCallBackService() {
		return callBackService;
	}

	public void setCallBackService(String callBackService) {
		this.callBackService = callBackService;
	}

	public String getGitShellServer() {
		return gitShellServer;
	}

	public void setGitShellServer(String gitShellServer) {
		this.gitShellServer = gitShellServer;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	public NotifyPullThread(){
		
	}
	
	public NotifyPullThread(String cmd) {
		this.cmd = cmd;
	}
	
	public NotifyPullThread(String cmd,Long appId) {
		this.cmd = cmd;
		this.appId = appId;
	}
	
	public NotifyPullThread(String cmd,Long appId,String gitShellServer) {
		this.cmd = cmd;
		this.appId = appId;
		this.gitShellServer = gitShellServer;
	}
	
	public NotifyPullThread(String cmd,Long appId,String gitShellServer,String callBaseService) {
		this.cmd = cmd;
		this.appId = appId;
		this.gitShellServer = gitShellServer;
		this.callBackService = callBaseService;
	}
	
	public void run() {
//		log.info(String.format("notifyRepoPushed start waiting sleep cmd[%s]", cmd));
       /* try {
			Thread.sleep(20*1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}*/
		/*Runtime run = Runtime.getRuntime();  
        String ret = "";
        StringBuffer errorInfo = new StringBuffer();
        log.info(String.format("notifyRepoPushed start process cmd[%s]", cmd));
        try {  
        	NotifyPullThread notifyPullThread=(NotifyPullThread) applicationContext.getBean("notifyPullBean");
            Process p = run.exec(cmd);
            
            log.info(String.format("notifyRepoPushed end process cmd[%s]", cmd));
            
            new ProcessClearStream(p.getInputStream(),"NotifyPullThread-INFO").start();
//            new ProcessClearStream(p.getErrorStream(),"NotifyPullThread-ERROR",errorInfo).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			// 打印信息
			String line = null;
			while ((line = br.readLine()) != null) {
				errorInfo.append(line+"\n\r");
			}
			p.getErrorStream().close();
			br.close();
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
            if(errorInfo.toString().contains("fatal")){
            	String params[] = cmd.split(" ");
            	p=run.exec("cd "+params[2]+" && echo y | rm ./.git/index.lock");
            	new ProcessClearStream(p.getInputStream(),"NotifyPullThread-INFO").start();
            	new ProcessClearStream(p.getErrorStream(),"NotifyPullThread-ERROR",errorInfo).start();
            	p=run.exec(cmd);
            	errorInfo.setLength(0);
            	new ProcessClearStream(p.getInputStream(),"NotifyPullThread-INFO").start();
            	new ProcessClearStream(p.getErrorStream(),"NotifyPullThread-ERROR",errorInfo).start();
            	status =p.waitFor();
            	if(!errorInfo.toString().contains("fatal:")){
            		int effect = notifyPullThread.jdbcTpl.update("update  T_APP set codePullStatus='finish' where id = "+appId);
        			log.info("git_pull affect again->"+effect+",for script->"+cmd);
            	}else{
            		int effect = notifyPullThread.jdbcTpl.update("update  T_APP set codePullStatus='failed' where id = "+appId);
            		log.info("git_pull affect failed->"+effect+",for script->"+cmd);
            	}
    		}else{
    			int effect = notifyPullThread.jdbcTpl.update("update  T_APP set codePullStatus='finish' where id = "+appId);
    			log.info("git_pull affect->"+effect+",for script->"+cmd);
    		}
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
    

        log.info(String.format("notifyRepoPushed cmd[%s] ret[%s]", cmd, ret));*/
		Map<String,String> params = new HashMap<String,String>();
		params.put("relativeRepoPath", cmd);
		params.put("callback", callBackService+"/app/status/"+appId.toString());
		log.info("git notifyPull params:->"+params.toString());
		String jsonStr="";
		try {
			jsonStr = HttpUtil.httpPost(gitShellServer+"/git/notify/pullRepo", params);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.info(String.format("GitAction -> notifyPull --> shell for jsonStr[%s]", jsonStr));
		JSONObject obj = JSONObject.fromObject( jsonStr );
		
		if(!"success".equals(obj.getString("status"))){
			log.info("Git pull failed, for AppId:"+appId.toString()+",relativeRepoPath:"+cmd);
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		NotifyPullThread.applicationContext = arg0;
	}
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

}
