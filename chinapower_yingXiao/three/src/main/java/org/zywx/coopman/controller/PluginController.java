package org.zywx.coopman.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.commons.Enums.PluginType;
import org.zywx.coopman.commons.Enums.PluginVersionStatus;
import org.zywx.coopman.entity.QueryEntity;
import org.zywx.coopman.entity.builder.Plugin;
import org.zywx.coopman.entity.builder.PluginCategory;
import org.zywx.coopman.entity.builder.PluginVersion;
import org.zywx.coopman.entity.builder.PushPlugin;
import org.zywx.coopman.service.PluginService;


@Controller
@RequestMapping(value="/plugin")
public class PluginController extends BaseController {
	
	@Value("${rootpath}")
	private String rootPath;
	
	@Autowired
	private PluginService pluginService;
	
	@Value("${plugin.storePath}")
	private String storePath;
	
	@Value("${git.localRepoPath}")
	private String gitPath;
	
	private static int sequence = 0;
	
	private synchronized String getUniqHexStr() {
		long mills = System.currentTimeMillis();
		String millsHex = Long.toHexString(mills);
		
		sequence++;
		if(sequence >= 10000) {
			sequence = 0;
		}
		
		String seqHex = Integer.toHexString(sequence);
		
		return millsHex + "_" + seqHex;
	}
	
	
	@Autowired
	private CommonsMultipartResolver multipartResolver;
	
	/**
	 * 插件列表
	 * @author yang.li
	 * @date 2015-09-20
	 */
	@RequestMapping(value="/list")
	public ModelAndView getPluginList(HttpServletRequest request,@RequestParam(value="type") PluginType type,String search,QueryEntity queryEntity) {

		int pageNo       = queryEntity.getPageNo();
		int pageSize     = queryEntity.getPageSize();
		//插件列表页默认10条
		if(15==pageSize){
			pageSize =10;
		}
		Pageable pageable = new PageRequest(pageNo-1, pageSize, Direction.DESC, "id");

		if(null!=search && !"".equals(search)){
			try {
				search = URLDecoder.decode(search,"utf-8");
				search = URLDecoder.decode(search,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return this.pluginService.getPlugList(pageable, type,search);
		}else{
			return this.pluginService.getPlugList(pageable, type);
		}
	}

	/**
	 * 上传插件
	 * @param engineZipFile 引擎zip文件
	 * @param type 引擎类型（PUBLIC, PRIVATE）
	 * @param osType 系统类型（ANDROID, IOS）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public HashMap<String, Object> uploadPlugin(
			HttpServletRequest request,
			Plugin plugin,
			MultipartFile iosFile,
			MultipartFile androidFile,
			MultipartFile helpFile,
			@RequestParam(value="type") PluginType type) {
		log.info(String.format("Plugin [%s] iosFile [%s] androidFile [%s] helpFile [%s]", plugin,null==iosFile?"":iosFile.getOriginalFilename(),null==androidFile?"":androidFile.getOriginalFilename(),null==helpFile?"":helpFile.getOriginalFilename()));
		HashMap<String, Object> map = new HashMap<>();
		int pageNo       = 0;
		int pageSize     = 15;
//		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

		if(iosFile == null) {
//			ModelAndView mav = this.pluginService.getPlugList(pageable, type);
//			mav.addObject("actionInfo", "未提交ios插件");
			
			//map.put("actionInfo", "未提交ios插件");
//			return map;
		}
		if(androidFile == null) {
//			ModelAndView mav = this.pluginService.getPlugList(pageable, type);
//			mav.addObject("actionInfo", "未提交安卓插件");

			//map.put("actionInfo", "未提交安卓插件");
//			return map;
		}
		if(helpFile == null) {
//			ModelAndView mav = this.pluginService.getPlugList(pageable, type);
//			mav.addObject("actionInfo", "未提交帮助手册");

			//map.put("actionInfo", "未提交帮助手册");
//			return map;
		}

		try {
			// 处理插件文件，存储到服务器
			String basePath = type.equals(PluginType.PUBLIC) ? storePath+"/public" : ( type.equals(PluginType.PRIVATE) ? storePath+"/private" : null );
			if(basePath == null) {
//				ModelAndView mav = this.pluginService.getPlugList(pageable, type);
//				mav.addObject("actionInfo", "插件类型错误");

				map.put("actionInfo", "插件类型错误");
				return map;
			}
			// 存储iOS插件
			File iosTransferFile = null;
			if(iosFile != null) {
				String storePath = basePath + "/ios" + "/plugin_" + getUniqHexStr() + ".zip";
				iosTransferFile = new File(storePath);
				iosFile.transferTo(iosTransferFile);		
			}
			// 存储Android插件
			File androidTransferFile = null;
			if(androidFile != null) {
				String storePath = basePath + "/android" + "/plugin_" + getUniqHexStr() + ".zip";
				androidTransferFile = new File(storePath);
				androidFile.transferTo(androidTransferFile);
			}
			// 存储帮助手册
			File helpTransferFile = null;
			if(helpFile != null) {
				String storePath = basePath + "/plugin_help_" + getUniqHexStr() + ".zip";
				helpTransferFile = new File(storePath);
				helpFile.transferTo(helpTransferFile);
			}
			
			List<PushPlugin> saved = this.pluginService.addPlugin(plugin, iosTransferFile, androidTransferFile, helpTransferFile);
			this.pluginService.addPluginToServer(saved);
//			ModelAndView mav = this.pluginService.getPlugList(pageable, type);
			if(saved.size()==0) {
//				mav.addObject("actionInfo", "插件添加失败");
				
				//map.put("actionInfo", "插件添加失败");
			} else {
//				mav.addObject("actionInfo", "插件添加成功");
				
				//map.put("actionInfo", "插件添加成功");
			}
			return map;

		} catch (Exception e) {
			e.printStackTrace();
//			ModelAndView mav = this.pluginService.getPlugList(pageable, type);
//			mav.addObject("actionInfo", "添加插件异常：" + e.getMessage());

			map.put("actionInfo", "添加插件异常：" + e.getMessage());
			return map;
		}
	}
	
	/**
	 * 编辑插件
	 * @param type 引擎类型（PUBLIC, PRIVATE）
	 * @param osType 系统类型（ANDROID, IOS）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/edit", method=RequestMethod.POST)
	public HashMap<String, Object> editPlugin(
			HttpServletRequest request,
			Plugin plugin,
			@RequestParam(value="type") PluginType type) {
		HashMap<String, Object> map = new HashMap<>();
		int pageNo       = 0;
		int pageSize     = 15;
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
		
		String basePath = type.equals(PluginType.PUBLIC) ? storePath+"/public" : ( type.equals(PluginType.PRIVATE) ? storePath+"/private" : null );
		if(basePath == null) {
			ModelAndView mav = this.pluginService.getPlugList(pageable, type);
			mav.addObject("actionInfo", "插件类型错误");
			
			map.put("actionInfo", "插件类型错误");
			return map;
		}
		
		
		int saved = this.pluginService.editPlugin(plugin);
		
		if(saved == 0) {
			map.put("actionInfo", "编辑失败");
		} else
			map.put("actionInfo", "编辑成功");
		return map;
	}
	
	/**
	 * 
	 * @describe 删除插件	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年12月3日 上午10:24:20	<br>
	 * @param request
	 * @param plugin
	 * @param type
	 * @return  <br>
	 * @returnType HashMap<String,Object>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/delete", method=RequestMethod.POST)
	public HashMap<String, Object> deletePlugin(
			HttpServletRequest request,
			@RequestParam(value="pluginIds")List<Long> pluginIds) {
		HashMap<String, Object> map = new HashMap<>();
		
		int a = this.pluginService.removePlugin(pluginIds);
		
		map.put("status", "success");
		map.put("affected", a);
		return map;
	}

	/**
	 * 插件版本列表
	 * @author yang.li
	 * @date 2015-09-20
	 */
	@RequestMapping(value="/version/list")
	public ModelAndView getPluginVersionList(HttpServletRequest request,@RequestParam(value="pluginId") long pluginId) {
		Plugin plugin = this.pluginService.getPlugin(pluginId);
		List<PluginVersion> pvList = plugin.getPluginVersion();
		List<PluginVersion> iosList = new ArrayList<>();
		List<PluginVersion> androidList = new ArrayList<>();
		for(PluginVersion pv : pvList) {
			if( pv.getOsType().equals(OSType.ANDROID) ) {
				androidList.add(pv);
			} else if( pv.getOsType().equals(OSType.IOS) ) {
				iosList.add(pv);
			}
		}
		
		PluginType type = plugin.getType();
		String title = type.equals(PluginType.PUBLIC) ? "公共插件" : ( type.equals(PluginType.PRIVATE) ? "内部插件" : "" );
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("builder/pluginVersion");
		mav.addObject("iosList", iosList);
		mav.addObject("androidList", androidList);
		mav.addObject("title", title);
		mav.addObject("type", type);
		mav.addObject("pluginId", pluginId);
		
		return mav;
	}

	/**
	 * 
	 * @describe 获取插件分类	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月16日 上午9:24:26	<br>
	 * @param request
	 * @return  <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value="/category/list")
	public ModelAndView getPluginCategoryList(HttpServletRequest request,String search){
		List<PluginCategory> list = this.pluginService.getPluginCategory(search,DELTYPE.NORMAL);
		ModelAndView mv = new ModelAndView();
		mv.addObject("categorys",list);
		mv.addObject("total",list.size());
		if(null!=search && !"".equals(search)){
			mv.addObject("search",search);
		}
		mv.setViewName("builder/pluginCategory");
		return mv;
		
	}
	
	@RequestMapping(value="/category/enable")
	public ModelAndView getPluginEnableCategoryList(HttpServletRequest request ){
		List<PluginCategory> list = this.pluginService.getPluginEnableCategory( DELTYPE.NORMAL);
		ModelAndView mv = new ModelAndView();
		mv.addObject("categorys",list);
		mv.addObject("total",list.size());
		return mv;
		
	}
	
	@RequestMapping(value="/category",method=RequestMethod.POST)
	public Map<String, Object> addPluginCategory(HttpServletRequest request,
			PluginCategory category){
		Map<String, Object> map = new HashMap<String,Object>();
		if(category.getName()==null||StringUtils.isBlank(category.getName())){
			map.put("flag", "0");
			map.put("msg", "分类名称不可为空");
			return getFailedMap("分类名称不可为空");
		}
		List<PluginCategory> list = this.pluginService.getPluginCategory(category.getName(), DELTYPE.NORMAL);
		if(list!=null&&!list.isEmpty()){
			map.put("flag", "-1");
			map.put("msg", "分类名称已存在");
			return getFailedMap("分类名称已存在");
		}
		this.pluginService.addPluginCategory(category);
		map.put("flag", "1");
		map.put("msg", "添加成功");
		return getSuccessMap("添加成功");
	}
	
	@RequestMapping(value="/category/{id}",method=RequestMethod.POST)
	public Map<?,?> editPluginCategory(HttpServletRequest request,
			@PathVariable("id")long id,PluginCategory category){
		this.pluginService.editPluginCategory(id,category);
		return this.getSuccessMap("{'affected':1}");
	}
	
	@RequestMapping(value="/category/del",method=RequestMethod.POST)
	public ModelAndView deletePluginCategory(HttpServletRequest request,
			@RequestParam(value="ids") List<Long> ids,PluginCategory category){
		this.pluginService.deletePluginCategory(ids);
		return this.getSuccessModel("{'affected':1}");
	}

	@ResponseBody
	@RequestMapping(value="/autoload", method=RequestMethod.GET)
	public Map<String, Object> autoLoadPlugin(String tempPath) {
		Map<String, Object> ret = pluginService.addautoLoadPlugins(tempPath);
		return ret;
	}
	
	@ResponseBody
	@RequestMapping(value="/status/pluginV")
	public Map<String, Object> ablePluginV(long pluginVId,HttpServletRequest request,PluginVersionStatus status){
		Map<String, Object> ret = pluginService.updatePluginV(pluginVId,status);
		return ret;
	}
	
	/**
	 * 修改插件状态为更新完成接口
	 * @param versionId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/status/{versionId}",method=RequestMethod.GET)
	public Map<String, Object> notifyGitRepoPushed(@PathVariable(value="versionId") long  versionId,String result) {
		try {
			log.info("gitAction update version status :for Id->"+versionId);
			this.pluginService.updateVersionUploadStatus(versionId,result);
			return this.getSuccessMap("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
