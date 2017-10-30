package org.zywx.cooldev.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.EngineStatus;
import org.zywx.cooldev.commons.Enums.EngineType;
import org.zywx.cooldev.commons.Enums.OSType;
import org.zywx.cooldev.commons.Enums.UploadStatus;
import org.zywx.cooldev.dao.builder.EngineDao;
import org.zywx.cooldev.entity.builder.Engine;
import org.zywx.cooldev.thread.PushPluginEngineToQueueThread;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.vo.PushEntity;

import net.sf.json.JSONObject;

/**
 * 
 * @author yang.li
 * @date 2015-09-01
 *
 */
@Service
public class EngineService extends BaseService {

	@Autowired
	private EngineDao engineDao;
	
	@Value("${shellPath}")
	private String shellPath;
	
//	@Value("${appPackage.buildRepoPath}")
//	private String buildRepoPath;
	
	@Value("${gitShellServer}")
	private String gitShellServer;
	
	@Value("${gitShellEngineServer}")
	private String gitShellEngineServer;
	
	@Value("${xtGitHost}")
	private String xtGitHost;
	
	public List<Engine> getEngineList(Pageable pageable, long loginUserId, Long projectId, List<OSType> osType, List<EngineType> type, List<EngineStatus> status,List<UploadStatus> uploadStatus,String keyWords) {
		
		if(projectId != null) {
			return engineDao.findByProjectIdAndOsTypeInAndTypeInAndStatusInAndUploadStatusInAndVersionNoLikeAndDelOrderByCreatedAtDesc(projectId, osType,type,status,uploadStatus,keyWords, DELTYPE.NORMAL);
		} else {
			return engineDao.findByOsTypeInAndTypeInAndStatusInAndUploadStatusInAndVersionNoLikeAndDelOrderByCreatedAtDesc(osType,type,status,uploadStatus,keyWords, DELTYPE.NORMAL);
		}
	}
	
	public PushEntity addEngine(Engine engine, long loginUserId) {
		if(engine.getStatus() == null) {
			engine.setStatus(EngineStatus.ENABLE);
		}
		Engine existEngine = this.getEngineExist(engine.getProjectId(),engine.getOsType(),engine.getVersionNo(),engine.getType(),engine.getKernel());
		if(existEngine!=null){
			engine.setId(existEngine.getId());
		}
		log.info("====>kernel:"+engine.getKernel());
		engine.setUploadStatus(UploadStatus.ONGOING);
		
		engineDao.save(engine);

		//后台异步提交到git仓库
//		Thread thread = new EnginePushToGitRepo(engine, shellPath, buildRepoPath);
//		thread.start();
		//同步提交
//		this.pushToGitRepo(engine);
		
		
		
		String pushName = String.format("engine_%d_%s_%s_%s",
				engine.getId(), engine.getType(), engine.getOsType(), engine.getVersionNo());
		
		//后台异步提交到git仓库
//		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(engine.getDownloadUrl(), pushName, xtGitHost+"/engine/status/"+engine.getId(), gitShellEngineServer);
//		Thread thread = new Thread(pushthread);
//		thread.start();
		PushEntity pushEngine = new PushEntity();
		pushEngine.setDownLoadUrl(engine.getDownloadUrl());
		pushEngine.setEnginePluginId(engine.getId().toString());
		pushEngine.setPushName(pushName);
		
		return pushEngine;
	}
	
	public void saveEngineToServer(PushEntity pushEngine){
		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(pushEngine.getDownLoadUrl(), pushEngine.getPushName(), xtGitHost+"/engine/status/"+pushEngine.getEnginePluginId(), gitShellEngineServer);
		Thread thread = new Thread(pushthread);
		thread.start();
	}
	
	/**
	 * 提供给IDC机房git钩子回调结束后修改引擎的上传状态
	 * @param relativeRepoPath
	 */
	public void updateUploadStatus(Long engineId,String result) {
		Engine engine = engineDao.findOne(engineId);
		if(null!=engine){
			if("FAILED".equals(result)){
				engine.setUploadStatus(UploadStatus.FAILED);
			}else{
				engine.setFilePath(result.replace("(", "/"));
				engine.setUploadStatus(UploadStatus.SUCCESS);
			}
			engineDao.save(engine);
		}else{
			log.info("GitAction push engine failed for engineId->"+engineId);
		}

	}
	public int editEngine(Engine engine, long loginUserId) {
		
		String setting = "";
		if(engine.getDownloadUrl() != null) {
			setting += String.format(",downloadUrl='%s'", engine.getDownloadUrl());
		}
		if(engine.getVersionNo() != null) {
			setting += String.format(",versionNo='%s'", engine.getVersionNo());
		}
		if(engine.getVersionDescription() != null) {
			setting += String.format(",versionDescription='%s'", engine.getVersionDescription());
		}
		if(engine.getOsType() != null) {
			setting += String.format(",osType=%d", engine.getOsType().ordinal());
		}
		if(engine.getStatus() != null) {
			setting += String.format(",status=%d", engine.getStatus().ordinal());
		}
		if(setting.length() > 0) {
			setting = setting.substring(1);
			String sql = "update T_ENGINE set " + setting + " where id=" + engine.getId();
			return jdbcTpl.update(sql);

		} else {
			return 0;
		}
		

	}
	
	public void removeEngine(long engineId, long loginUserId) {
	
		engineDao.delete(engineId);
	
	}

	//***************************************************
	//    private Methods                               *
	//***************************************************
	/*private void pushToGitRepo(Engine e) {

		if(e == null || e.getAbsFilePath() == null) {
			return;
		}
		
		String gitRepoName = String.format("engine_%d_%s_%s_%s",
				e.getId(), e.getType(), e.getOsType(), e.getVersionNo());
		String cmd = String.format("sh "+shellPath+"coopdev_git/add_file.sh %s %s %s", e.getAbsFilePath(), gitRepoName, buildRepoPath);
		this.execShell(cmd);
		
		e.setUploadStatus(UploadStatus.SUCCESS);
		engineDao.save(e);

	}*/
	
	/**
	 * @describe 获取已存在的开启的引擎	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月17日 上午11:51:30	<br>
	 * @param osType
	 * @param versionNo
	 * @return  <br>
	 * @returnType Engine
	 *
	 */
	public Engine getEngineExist(Long projectId,OSType osType, String versionNo,EngineType type,String kernel) {
		List<Engine> engines = this.engineDao.findByProjectIdAndVersionNoAndOsTypeAndTypeAndDelAndKernel(projectId,versionNo,osType,type,DELTYPE.NORMAL,kernel);
		for(Engine engine : engines){
			if(engine.getStatus().compareTo(EngineStatus.ENABLE)==0){
				return engine;
			}
		}
		return null;
	}

	
	public Engine getEngineExist(long engineId) {
		return this.engineDao.findOne(engineId);
	}
}
