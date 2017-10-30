package org.zywx.coopman.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.zywx.coopman.commons.Enums.EngineStatus;
import org.zywx.coopman.commons.Enums.EngineType;
import org.zywx.coopman.commons.Enums.OSType;
import org.zywx.coopman.entity.QueryEntity;
import org.zywx.coopman.entity.builder.Engine;
import org.zywx.coopman.service.EngineService;


@Controller
@RequestMapping(value="/engine")
public class EngineController extends BaseController {
	
	@Value("${rootpath}")
	private String rootPath;
	
	@Autowired
	private EngineService engineService;
	
	@Value("${engine.storePath}")
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
	 * 引擎列表(正益官方)
	 * @author yang.li
	 * @date 2015-09-20
	 */
	@RequestMapping(value="/list")
	public ModelAndView getPublicEngineList(HttpServletRequest request,@RequestParam(value="type") EngineType type,QueryEntity queryEntity) {

		int pageNo       = queryEntity.getPageNo();
		int pageSize     = queryEntity.getPageSize();
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

		return this.engineService.getEngineList(pageable, type);
	}

	/**
	 * 上传引擎
	 * @param engineZipFile 引擎zip文件
	 * @param type 引擎类型（PUBLIC, PRIVATE）
	 * @param osType 系统类型（ANDROID, IOS）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public Map<String, Object> uploadEngine(
			HttpServletRequest request,
			MultipartFile engineZipFile,
			@RequestParam(value="type") EngineType type,
			@RequestParam(value="osType") OSType osType) {
		HashMap<String, Object> map = new HashMap<>();
		int pageNo       = 0;
		int pageSize     = 15;
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");

		if(engineZipFile == null) {
			ModelAndView mav = this.engineService.getEngineList(pageable, type);
			mav.addObject("actionInfo", "未提交引擎文件");
			map.put("actionInfo", "未提交引擎文件");
			return map;
		}

		try {
			// 处理zip文件，存储到服务器
			String basePath = type.equals(EngineType.PUBLIC) ? storePath+"/public" : ( type.equals(EngineType.PRIVATE) ? storePath+"/private" : null );
			if(basePath == null) {
				ModelAndView mav = this.engineService.getEngineList(pageable, type);
				mav.addObject("actionInfo", "引擎类型错误");
				map.put("actionInfo", "引擎类型错误");
				return map;
			}
			basePath += "/"+ osType.name().toLowerCase()+ "/" + getUniqHexStr() + ".zip";
			File transferFile = new File(basePath);
			engineZipFile.transferTo(transferFile);
			
			Map<String,String> engineMap = this.engineService.addEngine(transferFile, type, osType);
			if(null!=engineMap){
				this.engineService.saveEngineToServer(engineMap.get("downloadUrl").toString(), engineMap.get("pushName").toString(), engineMap.get("engineId").toString());
			}
			ModelAndView mav = this.engineService.getEngineList(pageable, type);
			if(engineMap == null) {
				mav.addObject("actionInfo", "引擎上传失败");
				map.put("actionInfo", "引擎上传失败");
			} else {
				mav.addObject("actionInfo", "引擎上传成功");
				map.put("actionInfo", "引擎上传成功");
			}
			return map;

		} catch (Exception e) {
			e.printStackTrace();
			ModelAndView mav = this.engineService.getEngineList(pageable, type);
			mav.addObject("actionInfo", "添加引擎异常：" + e.getMessage());
			map.put("actionInfo", "添加引擎异常：" + e.getMessage());
			return map;
		}
	}

	/**
	 * 
	 * @describe 删除引擎	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月16日 下午5:23:27	<br>
	 * @param request
	 * @param engineId
	 * @return  <br>
	 * @returnType Map<?,?>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public Map<?,?> deleteEngine(HttpServletRequest request,@RequestParam("engineIds") List<Long> engineId){
		this.engineService.removeEngine(engineId);
		HashMap<String, Integer> map = new HashMap<>();
		map.put("affected", 1);
		return this.getSuccessMap(map);
		
	}
	
	/**
	 * 
	 * @describe 启用禁用引擎	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月16日 下午5:23:27	<br>
	 * @param request
	 * @param engineId
	 * @return  <br>
	 * @returnType Map<?,?>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/status/{engineId}",method=RequestMethod.POST)
	public Map<?,?> statusEngine(HttpServletRequest request,@PathVariable("engineId") long engineId,EngineStatus status){
		this.engineService.updateStatusEngine(engineId,status);
		HashMap<String, Integer> map = new HashMap<>();
		map.put("affected", 1);
		return this.getSuccessMap(map);
	}
	
	/**
	 * 修改引擎状态为更新完成接口
	 * @param engineId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/status/{engineId}",method=RequestMethod.GET)
	public Map<String, Object> notifyGitRepoPushed(@PathVariable(value="engineId") long  engineId,String result) {
		try {
			log.info("gitAction update engine status :for Id->"+engineId);
			this.engineService.updateUploadStatus(engineId,result);
			return this.getSuccessMap("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
