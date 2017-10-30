package org.zywx.coopman.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.EngineStatus;
import org.zywx.coopman.commons.Enums.EngineType;
import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.commons.Enums.UploadStatus;
import org.zywx.coopman.dao.builder.EngineDao;
import org.zywx.coopman.entity.builder.Engine;
import org.zywx.coopman.util.PushPluginEngineToQueueThread;

/**
 * 引擎操作相关服务
 * @author yang.li
 * @date 2015-09-01
 *
 */
@Service
public class EngineService extends BaseService {

	@Autowired
	private EngineDao engineDao;
	
	@Value("${engine.storePath}")
	private String storePath;
	
	@Value("${git.localRepoPath}")
	private String localRepoPath;
	
	@Value("${git.remoteRepoPath}")
	private String remoteRepoPath;
	
	@Value("${shellBasePath}")
	private String shellBasePath;
	
	@Value("${engine.urlPrefix}")
	private String urlPrefix;

	@Value("${gitShellServer}")
	private String gitShellServer;
	
	@Value("${xtGitHost}")
	private String xtGitHost;
	/**
	 * 获取指定类型的引擎列表
	 * @param pageable
	 * @param loginUserId
	 * @param projectId
	 * @param osType
	 * @return
	 */
	public ModelAndView getEngineList(Pageable pageable, EngineType type) {
		List<Engine> engineList = null;
		if(type == null) {
			engineList = new ArrayList<Engine>();
		} else {
			engineList = this.engineDao.findByType(type);
		}
		
		List<Engine> iosEngineList = new ArrayList<>();
		List<Engine> androidEngineList = new ArrayList<>();
		
		for(Engine e : engineList) {
			if( e.getOsType().equals(OSType.ANDROID) ) {
				androidEngineList.add(e);
			} else if( e.getOsType().equals(OSType.IOS) ) {
				iosEngineList.add(e);
			} else {
				continue;
			}
		}
		
		String title = type.equals(EngineType.PUBLIC) ? "公共引擎" : ( type.equals(EngineType.PRIVATE) ? "内部引擎" : "" );

		ModelAndView mav = new ModelAndView();
		mav.setViewName("builder/engine");
		mav.addObject("iosEngineList", iosEngineList);
		mav.addObject("androidEngineList", androidEngineList);
		mav.addObject("title", title);
		mav.addObject("type", type);
		mav.addObject("iosTotal",iosEngineList.size());
		mav.addObject("androidTotal",androidEngineList.size());
		
		return mav;
	}
	
	public Map<String,String> addEngine(File engineFile, EngineType type, OSType osType) {
		// 读取zip文件中的说明xml文件
		String versionNo = null;
		String versionDescription = null;
		String packageDescription = null;
		String kernel = "system";
		try {
			ZipFile zipFile = new ZipFile(engineFile);
			ZipEntry entry = zipFile.getEntry(osType.name().toLowerCase() + "Engine.xml");
			if(entry == null) {
				zipFile.close();
				return null;
			}
			InputStream in = zipFile.getInputStream(entry);
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(in);
			Element root = doc.getRootElement();
			Element versionElement = root.element("version");
			Element descElement    = root.element("description");
			Element packElement    = root.element("package");
			Element kernelElement   = root.element("kernel");
			
			versionNo = versionElement.getTextTrim();
			versionDescription = descElement.getTextTrim();
			packageDescription = packElement.getTextTrim();
			if(kernelElement!=null){
				kernel=kernelElement.getTextTrim();
			}
			
			in.close();
			zipFile.close();
		} catch (IOException e) {
			log.error("addEngine -> IOException : " + ExceptionUtils.getStackTrace(e));
			return null;
		} catch (DocumentException e) {
			log.error("addEngine -> DocumentException : " + ExceptionUtils.getStackTrace(e));
			return null;
		}

		
		// 创建引擎新纪录
		String downloadUrl   = urlPrefix + "/" + type.name().toLowerCase() + "/" + osType.name().toLowerCase() + "/" + engineFile.getName();
		
		Engine existEngine = this.getEngineExist(osType,versionNo,type,kernel);
		
		Engine engine = new Engine();
		if(existEngine != null){
			engine.setId(existEngine.getId());
		}
		
		engine.setType(type);
		engine.setOsType(osType);
		engine.setDownloadUrl(downloadUrl);
		engine.setStatus(EngineStatus.ENABLE);
		engine.setVersionNo(versionNo);
		engine.setVersionDescription(versionDescription);
		engine.setPackageDescription(packageDescription);
		engine.setUploadStatus(UploadStatus.ONGOING);
		engine.setKernel(kernel);
		engineDao.save(engine);	// 创建后生成了engineId
		
		// 保存至Git仓库
		/*String name = String.format("engine_%d_%s_%s_%s", engine.getId(), type, osType, engine.getVersionNo());
		String cmd = String.format("sh %s/coopdev_git/add_file.sh %s %s %s", shellBasePath, engineFile.getAbsolutePath(), name,localRepoPath);
		String ret = this.execShellForErrorInfo(cmd);
		if(ret.contains("fatal:")){
			this.execShellForErrorInfo("cd "+localRepoPath+" && echo y | rm ./.git/index.lock");
			ret = this.execShellForErrorInfo(cmd);
			if(ret.contains("fatal:")){
				engine.setUploadStatus(UploadStatus.FAILED);
				engineDao.save(engine);	//更新上传状态
			}else{
				engine.setUploadStatus(UploadStatus.SUCCESS);
				engineDao.save(engine);	//更新上传状态
			}
		}else{
			engine.setUploadStatus(UploadStatus.SUCCESS);
			engineDao.save(engine);	//更新上传状态
		}
		log.info(String.format("addEngine -> cmd[%s] ret[%s]", cmd, ret));*/
		
		String pushName = String.format("engine_%d_%s_%s_%s", engine.getId(), type, osType, engine.getVersionNo());
		Map<String,String> map = new HashMap<String,String>();
		map.put("downloadUrl", downloadUrl);
		map.put("pushName", pushName);
		map.put("engineId", engine.getId().toString());
		
//		String pkgGitRepoUrl = remoteRepoPath + "/d_" + pushName;
//		engine.setPkgGitRepoUrl(pkgGitRepoUrl);
		
		engineDao.save(engine);	// 更新记录
		
		return map;
	}
	
	public void saveEngineToServer(String downloadUrl,String pushName,String engineId){
		//后台异步提交到git仓库
		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(downloadUrl, pushName,xtGitHost+"/engine/status/"+engineId, gitShellServer);
		Thread thread = new Thread(pushthread);
		thread.start();
	}
	
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
	public Engine getEngineExist(OSType osType, String versionNo,EngineType type,String kernel) {
		List<Engine> engines = this.engineDao.findByVersionNoAndOsTypeAndTypeAndDelAndKernel(versionNo,osType,type,DELTYPE.NORMAL,kernel);
		for(Engine engine : engines){
			if(engine.getStatus().compareTo(EngineStatus.ENABLE)==0){
				return engine;
			}
		}
		return null;
	}

	public int editEngine(Engine engine) {
		
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
	
	public void removeEngine(List<Long> engineIds) {
		for(Long engineId : engineIds){
			if(engineId==null || engineId==0 || engineId==-1){
				continue;
			}
			engineDao.delete(engineId);
		}
	}

	
	public void updateStatusEngine(long engineId, EngineStatus status) {
		Engine engine = this.engineDao.findOne(engineId);
		engine.setStatus(status);
		this.engineDao.save(engine);
		
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


}
