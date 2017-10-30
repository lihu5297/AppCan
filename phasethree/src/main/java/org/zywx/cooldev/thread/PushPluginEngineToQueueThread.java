package org.zywx.cooldev.thread;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.zywx.cooldev.util.HttpUtil;

import net.sf.json.JSONObject;

public class PushPluginEngineToQueueThread implements Runnable{

	
	Logger log = Logger.getLogger(this.getClass());
	
	private String downUrl;
	
	private String pushName;
	
	private String callback;
	
	private String gitShellEngineServer;
	
	
	
	@Override
	public void run(){
		try {
			Map<String,String> params = new HashMap<String,String>();
			params.put("downUrl", downUrl);
			params.put("pushName", pushName);
			params.put("callback", callback);
			log.info("git pushPluginEngine params:->"+params.toString());
			String jsonStr="";
			try {
				jsonStr = HttpUtil.httpPost(gitShellEngineServer, params);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			log.info(String.format("GitAction -> pushPluginEngine --> shell for jsonStr[%s]", jsonStr));
			JSONObject obj = JSONObject.fromObject( jsonStr );
			
			if(!"success".equals(obj.getString("status"))){
				log.info("Git push failed, for :"+params);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("plugin or engine push to queue failed."+e.getMessage());
		}
	}
	
	public PushPluginEngineToQueueThread() {
		super();
	}
	
	public PushPluginEngineToQueueThread(String downUrl,String pushName,String callback,String gitShellEngineServer){
		this.downUrl = downUrl;
		this.pushName = pushName;
		this.callback = callback;
		this.gitShellEngineServer = gitShellEngineServer;
	}

	public String getDownUrl() {
		return downUrl;
	}

	public void setDownUrl(String downUrl) {
		this.downUrl = downUrl;
	}

	public String getPushName() {
		return pushName;
	}

	public void setPushName(String pushName) {
		this.pushName = pushName;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getGitShellEngineServer() {
		return gitShellEngineServer;
	}

	public void setGitShellEngineServer(String gitShellEngineServer) {
		this.gitShellEngineServer = gitShellEngineServer;
	}

	
}
