package org.zywx.coopman.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.commons.Enums.PLUGIN_CATEGORY_STATUS;
import org.zywx.coopman.commons.Enums.PluginType;
import org.zywx.coopman.commons.Enums.PluginVersionStatus;
import org.zywx.coopman.commons.Enums.UploadStatus;
import org.zywx.coopman.dao.builder.PluginCategoryDao;
import org.zywx.coopman.dao.builder.PluginDao;
import org.zywx.coopman.dao.builder.PluginVersionDao;
import org.zywx.coopman.entity.builder.Plugin;
import org.zywx.coopman.entity.builder.PluginCategory;
import org.zywx.coopman.entity.builder.PluginVersion;
import org.zywx.coopman.entity.builder.PushPlugin;
import org.zywx.coopman.util.HttpUtil;
import org.zywx.coopman.util.PushPluginEngineToQueueThread;
import org.zywx.coopman.util.ReflectValues;

import net.sf.json.JSONObject;

/**
 * 
 * @author yang.li
 * @date 2015-09-01
 *
 */
@Service
public class PluginService extends BaseService {

	@Autowired
	private PluginDao pluginDao;
	@Autowired
	private PluginVersionDao pluginVersionDao;
	@Autowired
	private PluginCategoryDao pluginCategoryDao;
	
	@Value("${git.localRepoPath}")
	private String localRepoPath;
	
	@Value("${plugin.urlPrefix}")
	private String urlPrefix;
	
	@Value("${shellBasePath}")
	private String shellBasePath;
	
	@Value("${git.remoteRepoPath}")
	private String remoteRepoPath;
	
	@Value("${gitShellServer}")
	private String gitShellServer;
	
	@Value("${xtGitHost}")
	private String xtGitHost;
	/**
	 * 获取插件列表
	 * @param pageable
	 * @param search 
	 * @param loginUserId
	 * @return
	 */
	public ModelAndView getPlugList(Pageable pageable, PluginType type, String search) {
		Page<Plugin> pluginPage = pluginDao.findByTypeAndEnNameLikeAndDel(pageable, type,"%"+search+"%", DELTYPE.NORMAL);
		List<Plugin> plugins = pluginPage.getContent();
		for(Plugin plugin : plugins){
			PluginCategory pluginCategory = this.pluginCategoryDao.findOne(plugin.getCategoryId());
			plugin.setCategoryName(null!=pluginCategory && null!=pluginCategory.getName()?pluginCategory.getName():"");
		}
		
		String title = type.equals(PluginType.PUBLIC) ? "公共插件" : ( type.equals(PluginType.PRIVATE) ? "内部插件" : "" );

		ModelAndView mav = new ModelAndView();
		mav.setViewName("builder/plugin");
		mav.addObject("page", pluginPage);
		mav.addObject("title", title);
		mav.addObject("type", type);
		mav.addObject("search", search);
		mav.addObject("total", pluginPage.getTotalElements());
		mav.addObject("curPage", pageable.getPageNumber()+1);
		mav.addObject("pageSize", pageable.getPageSize());
		mav.addObject("totalPage", pluginPage.getTotalPages());
		return mav;
	}
	/**
	 * 获取插件列表
	 * @param pageable
	 * @param search 
	 * @param loginUserId
	 * @return
	 */
	public ModelAndView getPlugList(Pageable pageable, PluginType type) {
		Page<Plugin> pluginPage = pluginDao.findByTypeAndDel(pageable, type, DELTYPE.NORMAL);
		List<Plugin> plugins = pluginPage.getContent();
		for(Plugin plugin : plugins){
			PluginCategory pluginCategory = this.pluginCategoryDao.findOne(plugin.getCategoryId());
			plugin.setCategoryName(null!=pluginCategory && null!=pluginCategory.getName()?pluginCategory.getName():"");
		}
		
		String title = type.equals(PluginType.PUBLIC) ? "公共插件" : ( type.equals(PluginType.PRIVATE) ? "内部插件" : "" );
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("builder/plugin");
		mav.addObject("page", pluginPage);
		mav.addObject("title", title);
		mav.addObject("type", type);
		mav.addObject("total", pluginPage.getTotalElements());
		mav.addObject("curPage", pageable.getPageNumber()+1);
		mav.addObject("pageSize", pageable.getPageSize());
		mav.addObject("totalPage", pluginPage.getTotalPages());
		
		return mav;
	}
	
	/**
	 * 获取插件详情
	 * @param pluginId
	 * @param loginUserId
	 * @return
	 */
	public Plugin getPlugin(long pluginId) {
		
		Plugin plugin = pluginDao.findOne(pluginId);
		if(plugin != null) {
			List<PluginVersion> versions = pluginVersionDao.findByPluginIdAndDel(pluginId, DELTYPE.NORMAL);
			if(versions != null) {
				plugin.setPluginVersion(versions);
			}
		}

		return plugin;
	}

	/**
	 * 添加插件
	 * @param plugin
	 * @param loginUserId
	 * @return
	 */
	public List<PushPlugin> addPlugin (Plugin plugin, File iosFile, File androidFile, File helpFile) {
		List<PushPlugin> result = new ArrayList<PushPlugin>();
		if(null==plugin.getId() || 0==plugin.getId() || -1==plugin.getId()){
			// 创建插件记录
			pluginDao.save(plugin);
		}else{
			plugin = this.getPlugin(plugin.getId());
		}

		// 增加插件版本
		List<PluginVersion> pvList = new ArrayList<>();
		if(null!=iosFile){
			Map<String, String> iosInfo = this.getPluginZipInfo(iosFile);
			if(iosInfo != null) {
				PluginVersion pv = new PluginVersion();
				pv.setOsType(OSType.IOS);
				pv.setPluginId(plugin.getId());
				pv.setVersionNo(iosInfo.get("versionNo"));
				pv.setDownloadUrl("");
				pv.setVersionDescription(iosInfo.get("info"));
				pv.setUploadStatus(UploadStatus.ONGOING);
				pvList.add(pv);
				pluginVersionDao.save(pv);
				
//				this.addPluginVersionGit(pv,plugin,iosFile);
				PushPlugin pushPlugin = new PushPlugin();
				pushPlugin.setFile(iosFile);
				pushPlugin.setPlugin(plugin);
				pushPlugin.setPv(pv);
				result.add(pushPlugin);
			}
		}
		
		if(null!=androidFile){
			Map<String, String> androidInfo = this.getPluginZipInfo(androidFile);
			if(androidInfo != null) {
				PluginVersion pv = new PluginVersion();
				pv.setOsType(OSType.ANDROID);
				pv.setPluginId(plugin.getId());
				pv.setVersionNo(androidInfo.get("versionNo"));
				pv.setDownloadUrl("");
				pv.setVersionDescription(androidInfo.get("info"));
				pv.setUploadStatus(UploadStatus.ONGOING);
				pvList.add(pv);	
				pluginVersionDao.save(pv);
				
//				this.addPluginVersionGit(pv,plugin,androidFile);
				PushPlugin pushPlugin = new PushPlugin();
				pushPlugin.setFile(androidFile);
				pushPlugin.setPlugin(plugin);
				pushPlugin.setPv(pv);
				result.add(pushPlugin);
			}
		}
		
		
//		pluginVersionDao.save(pvList);
		
		return result;

	}
	
	public void addPluginToServer(List<PushPlugin> pushPlugin){
		for(PushPlugin push:pushPlugin){
			addPluginVersionGit(push.getPv(), push.getPlugin(), push.getFile());
		}
	}
	
	private void addPluginVersionGit(PluginVersion pv,Plugin plugin,File pluginVersionFile) {
		//创建插件新纪录
		String downloadUrl   = urlPrefix + "/" + plugin.getType().name().toLowerCase() + "/" + pv.getOsType().name().toLowerCase() + "/" + pluginVersionFile.getName();
		pv.setDownloadUrl(downloadUrl);
		this.pluginVersionDao.save(pv);
		String pushName = String.format("pluginVersion_%d_%s_%s_%s_%s",
				pv.getId(), plugin.getType(), pv.getOsType(), pv.getVersionNo(), plugin.getEnName());
		
		
		//后台异步提交到git仓库
		PushPluginEngineToQueueThread pushthread = new PushPluginEngineToQueueThread(downloadUrl, pushName,xtGitHost+"/plugin/status/"+pv.getId(), gitShellServer);
		Thread thread = new Thread(pushthread);
		thread.start();
		
		
		
		// 保存至Git仓库

		/*String name = String.format("pluginVersion_%d_%s_%s_%s_%s", pv.getId(), plugin.getType(), pv.getOsType(), pv.getVersionNo(),plugin.getEnName());
		String cmd = String.format("sh %s/coopdev_git/add_file.sh %s %s %s", shellBasePath, pluginVersionFile.getAbsolutePath(), name, localRepoPath);
		String ret = this.execShellForErrorInfo(cmd);
		if(ret.contains("fatal:")){
			this.execShellForErrorInfo("cd "+localRepoPath+" && echo y | rm ./.git/index.lock");
			ret = this.execShellForErrorInfo(cmd);
			if(ret.contains("fatal:")){
				pv.setUploadStatus(UploadStatus.FAILED);
				pluginVersionDao.save(pv);
			}else{
				pv.setUploadStatus(UploadStatus.SUCCESS);
				pluginVersionDao.save(pv);
			}
		}else{
			pv.setUploadStatus(UploadStatus.SUCCESS);
			pluginVersionDao.save(pv);
		}
		
		log.info(String.format("addpluginVersion -> cmd[%s] ret[%s]", cmd, ret));*/
				


		
	}
	/**
	 * 编辑插件
	 * @param plugin
	 * @return
	 */
	public int editPlugin(Plugin plugin) {
		String setting = "";
		if(plugin.getEnName() != null) {
			setting += String.format(",enName='%s'", plugin.getEnName());
		}
		if(plugin.getCnName() != null) {
			setting += String.format(",cnName='%s'", plugin.getCnName());
		}
		if(plugin.getDetail() != null) {
			setting += String.format(",detail='%s'", plugin.getDetail());
		}
		if(plugin.getTutorial() != null) {
			setting += String.format(",tutorial='%s'", plugin.getTutorial());
		}
		if(plugin.getCategoryId()!=0 && plugin.getCategoryId()!=-1){
			setting += String.format(",categoryId=%d", plugin.getCategoryId());
		}
		if(setting.length() > 0) {
			setting = setting.substring(1);
		} else {
			return 0;
		}
		String sql = "update T_PLUGIN set " + setting + " where id=" + plugin.getId();
		return this.jdbcTpl.update(sql);
	}
	
	public int removePlugin(List<Long> pluginId) {
		String pluginIds = "";
		for(Long id : pluginId){
			pluginIds += ","+id;
		}
		if(pluginId.isEmpty()){
			return 0;
		}
		String sql = "delete from T_PLUGIN_VERSION where pluginId in ("+pluginIds.substring(1)+")";
		this.jdbcTpl.update(sql);
		 sql = "delete from T_PLUGIN where id in("+pluginIds.substring(1)+")";
		return this.jdbcTpl.update(sql);
	}
	
	/**
	 * 获取插件版本列表
	 * @param pageable
	 * @param pluginId
	 * @param loginUserId
	 * @param osType
	 * @return
	 */
	public List<PluginVersion> getPluginVersionList(Long pluginId) {

		return pluginVersionDao.findByPluginIdAndDel(pluginId, DELTYPE.NORMAL);
		
	
	}
	
	public PluginVersion addPluginVersion(PluginVersion version, long loginUserId) {
		
		return pluginVersionDao.save(version);
	
	}
	
	public void removePluginVersion(long versionId, long loginUserID) {
	
		pluginVersionDao.delete(versionId);
	
	}

	/**
	 * 编辑插件版本
	 * @param version
	 * @param loginUserId
	 * @return
	 */
	public int editPluginVersion(PluginVersion version, long loginUserId) {
		String setting = "";
		if(version.getVersionDescription() != null) {
			setting += String.format(",versionDescription='%s'", version.getVersionDescription());
		}
		if(version.getDownloadUrl() != null) {
			setting += String.format(",downloadUrl='%s'", version.getDownloadUrl());
		}
		if(version.getResPackageUrl() != null) {
			setting += String.format(",resPackageUrl='%s'", version.getResPackageUrl());
		}
		if(version.getCustomDownloadUrl() != null) {
			setting += String.format(",customDownloadUrl='%s'", version.getCustomDownloadUrl());
		}
		if(version.getCustomResPackageUrl() != null) {
			setting += String.format(",customResPackageUrl='%s'", version.getCustomResPackageUrl());
		}
		
		
		if(version.getStatus() != null) {
			setting += String.format(",status=%d", version.getStatus().ordinal());
		}
		if(setting.length() > 0) {
			setting = setting.substring(1);
		} else {
			return 0;
		}
		String sql = "update T_PLUGIN_VERSION set " + setting + " where id=" + version.getId();
		return this.jdbcTpl.update(sql);
	}

	
	private Map<String, String> getPluginZipInfo(File pluginZipFile) {

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(pluginZipFile);
			Enumeration<? extends ZipEntry> enu = zipFile.entries();
			while(enu.hasMoreElements()) {
				ZipEntry entry = enu.nextElement();
				if(entry.getName().endsWith("info.xml")) {
					InputStream in = zipFile.getInputStream(entry);
					
					SAXReader saxReader = new SAXReader();
					Document doc = saxReader.read(in);
					Element root = doc.getRootElement();
					Element pluginElement = root.element("plugin");
					if(pluginElement != null) {
						String versionNo = null;
						String info = null;
						String enName = null;
						String cnName = null;

						Attribute attrVersion = pluginElement.attribute("version");
						if(attrVersion != null) {
							versionNo = attrVersion.getText().trim();
						}
						
						Attribute attrUexName = pluginElement.attribute("uexName");
						if(attrUexName != null) {
							enName = attrUexName.getText().trim();
						}
						
						Element infoElement = pluginElement.element("info");
						if(infoElement != null) {
							info = infoElement.getTextTrim();
						}
						
						List<Element> buildElementList = pluginElement.elements("build");
						if(buildElementList.size() > 0) {
						
							for(Element e : buildElementList) {
								String buildText = e.getTextTrim();
								if( buildText.startsWith("0:") ) {
									cnName = e.getTextTrim().substring(2);
									break;
								}
							}
						} else {
							if(info != null && info.startsWith("0:")) {
								cnName = info.substring(2);
							}
						}
						
						if(cnName == null) {
							cnName = "";
						}
						
						
						if(versionNo != null && info != null && enName != null) {
							Map<String, String> map = new HashMap<>();
							map.put("versionNo", versionNo);
							map.put("info", info);
							map.put("enName", enName);
							map.put("cnName", cnName);
							return map;
						}
					}
					
					in.close(); 
				}
			}
			
		} catch (ZipException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
			
	}

	
	/**
	 * 
	 * @describe 获取插件分类	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月16日 上午9:23:04	<br>
	 * @param normal
	 * @return  <br>
	 * @returnType List<PluginCategory>
	 *
	 */
	public List<PluginCategory> getPluginCategory(String search ,DELTYPE normal) {
		if(null==search || "".equals(search)){
			return this.pluginCategoryDao.findByDel(normal);
		}else
			return this.pluginCategoryDao.findByNameLikeAndDel("%"+search+"%",normal);
		
	}

	
	public int editPluginCategory(long id, PluginCategory category) {
		PluginCategory categoryOld = this.pluginCategoryDao.findOne(category.getId());
		//ReflectValues.SetValueFromTo(category, categoryOld);
		categoryOld.setStatus(category.getStatus());
		if(StringUtils.isNotBlank(category.getName())){
			categoryOld.setName(category.getName());
		}
		categoryOld.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		this.pluginCategoryDao.save(categoryOld);
		return 1;
	}

	public void addPluginCategory(PluginCategory category) {
		this.pluginCategoryDao.save(category);
		
	}

	public void deletePluginCategory(List<Long> ids) {
		for(Long id : ids){
			if(null==id){
				continue;
			}
			PluginCategory pluginCategory = this.pluginCategoryDao.findOne(id);
			pluginCategory.setDel(DELTYPE.DELETED);
			this.pluginCategoryDao.save(pluginCategory);
		}
		
	}	


	/**
	 * 20170619 此接口暂时不用了,如果需要使用也不需要上传到git目录
	 * 自动加载插件（添加到数据库及GIT仓库）
	 * @author yang.li
	 * @date 2015-11-21
	 * @param storePath
	 * @return
	 */
	public  Map<String, Object> addautoLoadPlugins(String tempBasePath) {		
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "failed");
		File baseDir = new File(tempBasePath);
		if(baseDir == null || !baseDir.isDirectory()) {
			ret.put("message", tempBasePath + " not found");
			return ret;
		}

		// 存储规范：必须存在android及ios两个子文件夹，针对子文件夹分别处理两类插件
		File[] osTypeDirList = baseDir.listFiles();
		if(osTypeDirList == null || osTypeDirList.length == 0) {
			ret.put("message", "plugin not found");
			return ret;
		}
		
		int loadTotal = 0;
		for(File dir : osTypeDirList) {
			if(dir.isDirectory()) {
				OSType osType = "android".equalsIgnoreCase( dir.getName() ) ? OSType.ANDROID :
					( "ios".equalsIgnoreCase(dir.getName()) ? OSType.IOS : null );
				
				File[] zipList = dir.listFiles();
				if(zipList != null && zipList.length > 0) {
					for(File zip : zipList) {
						// 解析zip文件
						Map<String, String> zipInfo = this.getPluginZipInfo(zip);
						String enName = zipInfo.get("enName");
						String cnName = zipInfo.get("cnName");
						String versionNo = zipInfo.get("versionNo");
						String info = zipInfo.get("info");
						
						System.out.println(String.format("autoLoadPlugins osType[%s] zipFileName[%s] enName[%s] cnName[%s] versionNo[%s] info[%s]",
								osType, zip.getName(), enName, cnName, versionNo, info));
						
						// 保存插件信息
						Plugin plugin = pluginDao.findOneByTypeAndEnNameAndProjectIdAndDel(PluginType.PUBLIC, enName, -1, DELTYPE.NORMAL);
						if(plugin == null) {
							plugin = new Plugin();
							plugin.setEnName(enName);
							plugin.setCnName(cnName);
							plugin.setDetail("");
							plugin.setCategoryId(1);
							plugin.setProjectId(-1);
							plugin.setType(PluginType.PUBLIC);
							
						} else {
							if("".equals( plugin.getCnName() )) {
								plugin.setCnName(cnName);
							}
						}
						pluginDao.save(plugin);
						
						// 保存插件版本信息至数据库
						PluginVersion pv = new PluginVersion();
						pv.setOsType(osType);
						pv.setPluginId(plugin.getId());
						pv.setVersionNo(versionNo);
						pv.setDownloadUrl("");
						pv.setResPackageUrl("");
						pv.setCustomResPackageUrl("");
						pv.setVersionDescription(info);
						pluginVersionDao.save(pv);
						
						// 提交至GIT版本库
						
						
						String gitRepoName = String.format("pluginVersion_%d_%s_%s_%s_%s",
								pv.getId(), plugin.getType(), pv.getOsType(), pv.getVersionNo(), plugin.getEnName());
						
						String cmd = String.format("sh "+shellBasePath+"/coopdev_git/add_file.sh %s %s %s", zip.getAbsolutePath(), gitRepoName, localRepoPath);
						String execRet = this.execShell(cmd);
						
						log.info(String.format("autoLoadPlugins -> cmd[%s] ret[%s]", cmd, execRet));
						
						loadTotal++;
						
					} // End of single Zip Process
				}
			}
		} // End load
		
		ret.put("status", "success");
		ret.put("message", "total" + loadTotal);
		return ret;
	}
	
	public static void main(String[] args) {
		String tempPath = "D:\\plugin";
		new PluginService().addautoLoadPlugins(tempPath);
	}
	
	public Map<String, Object> updatePluginV(long pluginVId, PluginVersionStatus status) {
		PluginVersion pv = this.pluginVersionDao.findOne(pluginVId);
		pv.setStatus(status);
		this.pluginVersionDao.save(pv);
		
		Map<String, Object> map = new HashMap<String, Object>(); 
		map.put("status", "success");
		map.put("message", "1");
		return map;
	}
	
	/**
	 * 提供给IDC机房git钩子回调结束后修改插件的上传状态
	 * @param relativeRepoPath
	 */
	public void updateVersionUploadStatus(Long versionId,String result) {
		PluginVersion pv = pluginVersionDao.findOne(versionId);
		if(null!=pv){
			if("FAILED".equals(result)){
				pv.setUploadStatus(UploadStatus.FAILED);
			}else{
				pv.setFilePath(result.replace("(", "/"));
				pv.setUploadStatus(UploadStatus.SUCCESS);
			}
			pluginVersionDao.save(pv);
		}else{
			log.info("GitAction push plugin failed for versionId->"+versionId);
		}

	}
	public List<PluginCategory> getPluginEnableCategory(DELTYPE normal) {
		return this.pluginCategoryDao.findByDelAndStatus(normal, PLUGIN_CATEGORY_STATUS.ENABLE) ;
	}
}
