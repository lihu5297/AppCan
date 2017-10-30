package org.zywx.cooldev.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.resource.ResourceContent;
import org.zywx.cooldev.entity.resource.ResourceType;
import org.zywx.cooldev.entity.resource.TempletInfo;
import org.zywx.cooldev.service.ContentService;

@Controller
@RequestMapping(value="/content")
public class ContentController extends BaseController {

	@Autowired
	private ContentService resourceService;
	 
	/**
	 * 查找资源类别全部
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getAllType", method=RequestMethod.GET)
	public Map<String,Object> getAllResourceType(HttpServletRequest request){
		List<ResourceType> list = resourceService.findAllResourceType(Enums.DELTYPE.NORMAL);
		if(list==null){
			getSuccessMap("");
		}
		return  getSuccessMap(list);
	}
	/**
	 * 查找内容根据资源id
	 * @param request
	 * @param resourceId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getContent/{resourceType}", method=RequestMethod.GET)
	public Map<String,Object> findResourceContentById(HttpServletRequest request,@PathVariable("resourceType")Long resourceType,@RequestHeader(value="loginUserId",required=true) long loginUserId){
		if(resourceType == null || resourceType < 1){
			return getFailedMap("资源类别异常");
		}
		User user = this.userService.findUserById(loginUserId);
		if(user == null){
			return getFailedMap("登录用户不存在");
		}
		if(user.getFilialeId() < 1){
			return getFailedMap("登录用户不属于任何网省");
		}
		List<ResourceContent> content = resourceService.findResourceContentByContentType(resourceType,String.valueOf(user.getFilialeId()));
		return getSuccessMap(content);
	}
	
	/**
	 * 查找内容根据内容id
	 * @param request
	 * @param resourceId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findContent/{id}", method=RequestMethod.GET)
	public Map<String,Object> findContentById(HttpServletRequest request,@PathVariable("id")Long id){
		if(id == null || id < 1){
			return getFailedMap("内容ID异常");
		}
		ResourceContent rc = resourceService.findResourceContentById(id);
		return getSuccessMap(rc);
	}
	
	
	 /**
	  * 查询内容,附件名接口
	  * @param request
	  * @param value
	  * @return
	  */
	@ResponseBody
	@RequestMapping(value = "/searchContent", method=RequestMethod.GET)
	public Map<String,Object> searchContent(HttpServletRequest request,@RequestParam String value,@RequestHeader(value="loginUserId",required=true) long loginUserId){
		User user = this.userService.findUserById(loginUserId);
		if(user == null){
			return getFailedMap("登录用户不存在");
		}
		if(user.getFilialeId() < 1){
			return getFailedMap("登录用户不属于任何网省");
		}
		if(value == null || "".equals(value)){
			return getFailedMap("搜索内容不可为空");
		}
		value="%"+value+"%";
		List<ResourceContent> rcList = resourceService.searchContent(value, String.valueOf(user.getFilialeId()));
		return getSuccessMap(rcList);
	}
	
	@ResponseBody
	@RequestMapping(value="/findTempletUrl",method=RequestMethod.GET)
	public Map<String,Object> findTempletUrl(HttpServletRequest request){
		List<TempletInfo> templetList = resourceService.findTempletAll();
		return getSuccessMap(templetList);
	}
}
