package org.zywx.cooldev.controller;

import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.CheckInfo;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.service.TopicMessagePublish;

@Controller
@RequestMapping(value = "/codeCheck")
public class CodeCheckController extends BaseController {

	@Autowired
	TopicMessagePublish messagePublish;
	
	@Value("${root.path}")
	String rootPath;
	@Value("${accessUrl}")
	String accessUrl;
	/**
	 * 代码检测请求			//   coolDev/codeCheck/test?message=haorentest&channel=xietong:codeCheck
	 * @param appId
	 * @param version
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/check", method=RequestMethod.GET)
	public Map<String, Object> check(HttpServletRequest request,
			@RequestParam(value="versionId") Long versionId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		if(versionId == null || versionId < 1){
			return this.getFailedMap("versionId不能为空");
		}
		AppVersion appVer = appService.getAppVersion(versionId);
		if(appVer == null){
			return this.getFailedMap("应用版本 不存在");
		}
		String randomUUID = UUID.randomUUID().toString();
		String uniqueId = randomUUID + "-" + request.getSession().getId();
		JSONObject json = new JSONObject();
		json.put("type", "app");	//app  前端包;   rear 后端包
		json.put("from", "req");	//req 请求
		json.put("proPath", rootPath + "/zipfile/appCode/" + appVer.getBranchZipName());	//源码路径
		json.put("status", "send");		//状态  send:发送;  success:成功;   fail:失败
		json.put("scope", "all");		//范围 :all|html|js|css
		json.put("uniqueId", uniqueId);
		CheckInfo ci = new CheckInfo();
		ci.setUserId(loginUserId);
		ci.setCheckType((short)0);
		ci.setUniqueId(uniqueId);
		ci.setDel(DELTYPE.NORMAL);
		ci.setCheckResult("checking");
		ci.setVersionId(versionId);
		checkInfoService.saveUpdate(ci);
		messagePublish.messagePublic("meap_msg_xietong:codeCheck",json.toString());
		
		return this.getSuccessMap(ci);
	}
	@ResponseBody
	@RequestMapping(value="/getResult", method=RequestMethod.GET)
	public Map<String, Object> getResult(HttpServletRequest request,
			@RequestParam(value="uniqueIdStr") String uniqueIdStr,
			@RequestHeader(value="loginUserId",required=true) long loginUserId) {
		if(StringUtils.isBlank(uniqueIdStr)){
			return this.getFailedMap("代码检测流水号不能为空");
		}
		
		CheckInfo ci = checkInfoService.findByUniqueId(uniqueIdStr);
		if(ci == null){
			return this.getFailedMap("代码检测对象不存在");
		}
		if(StringUtils.isNoneBlank(ci.getCheckFilePath()))
			ci.setCheckFilePath(accessUrl + ci.getCheckFilePath());
		
		return this.getSuccessMap(ci);
	}
	@ResponseBody
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public Map<String, Object> test(@RequestParam(value="channel") String channel,@RequestParam(value="message") String message){
		messagePublish.messagePublic(channel,message);
		return this.getSuccessMap("ok");
	}
		
}
