package org.zywx.coopman.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.resource.ResourceContent;
import org.zywx.coopman.entity.resource.ResourceFileInfo;
import org.zywx.coopman.entity.resource.ResourceFileRelation;
import org.zywx.coopman.entity.resource.ResourceType;
import org.zywx.coopman.entity.resource.TempletInfo;
import org.zywx.coopman.service.ResourceService;

@Controller
@RequestMapping(value="/resource")
public class ResourceController extends BaseController {

	@Autowired
	private ResourceService resourceService;
	@Value("${accessUrl}")
	private String accessUrl;
	
	
	@RequestMapping(value="/findType/{id}")
	public Map<?,?> getResourceTypeOne(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id")Long id){
		if(id == null || id < 1){
			return this.getFailedMap("类别ID异常");
		}
		ResourceType type = resourceService.findResourceTypeById(id);
		return this.getSuccessMap(type);
	}
	/**
	 * 查找资源类别全部
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findType", method=RequestMethod.GET)
	public ModelAndView getResourceType(HttpServletRequest request,@RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize){
		if(pageNo != null)
			pageNo =  pageNo > 0 ? pageNo-1 : 0;
		else
			pageNo = 0;
		
		PageRequest page = new PageRequest(pageNo, pageSize != null && pageSize > 0 ? pageSize : 20, Sort.Direction.DESC, "id");
		Page<ResourceType> ryPage = resourceService.findResourceTypeAll(page);
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("flag", "1");
		map.put("msg", "成功");
		map.put("total", ryPage.getTotalElements());
		map.put("totalPage", ryPage.getTotalPages());
		map.put("pageSize", ryPage.getSize());
		map.put("curPage", pageNo+1);
		map.put("typeInfo", ryPage.getContent());
		return  new ModelAndView("resource/type",map);
	}
	
	/**
	 * 查找资源类别全部
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findTypeAll", method=RequestMethod.GET)
	public Map<String,Object> getResourceTypeAll(HttpServletRequest request){
		PageRequest page = new PageRequest(0, 1000000, Sort.Direction.DESC, "id");
		Page<ResourceType> ryPage = resourceService.findResourceTypeAll(page);
		return  getSuccessMap(ryPage.getContent());
	}
	/**
	 * 保存资源类别
	 * @param rt
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/saveType",method=RequestMethod.POST)
	public Map<String,Object> saveType(ResourceType rt){
		if(StringUtils.isBlank(rt.getTypeName())){
    		return this.getFailedMap("资源类别名称不能为空");
		}
		
		List<ResourceType> rtList = resourceService.findResourceTypeByName(rt.getTypeName());
		if(rtList != null && !rtList.isEmpty()){
    		return this.getFailedMap("类别名称已存在");
		}
		ResourceType rtNew = new ResourceType();
		rtNew.setTypeName(rt.getTypeName());
		rtNew = resourceService.saveEditResourceType(rtNew);
		if(rtNew.getId() > 0){
			return this.getSuccessMap("添加成功");
		}else{
			return this.getFailedMap("添加失败");
		}
	}
	/**
	 * 更新资源类别
	 * @param typeId
	 * @param typeName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateType",method=RequestMethod.POST)
	public Map<String,Object> updateType(long typeId,String typeName){
		if(StringUtils.isBlank(typeName)){
    		return this.getFailedMap("资源类别名称不能为空");
		}
		if(typeId < 1){
    		return this.getFailedMap("类别ID不能为空");
		}
		ResourceType rt = resourceService.findResourceTypeById(typeId);
		if(rt == null){
    		return this.getFailedMap("类别信息不存在");
		}
		rt.setTypeName(typeName);
		rt = resourceService.saveEditResourceType(rt);
		if(rt != null){
			return this.getSuccessMap("更新成功");
		}else{
			return this.getFailedMap("更新失败");
		}
		
	}
	/**
	 * 删除类别信息
	 * @param typeId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delType",method=RequestMethod.POST)
	public Map<String,Object> delType(HttpServletRequest request,HttpServletResponse response,
			@RequestParam(value="ids")List<Long> ids){
		if(ids == null || ids.isEmpty()){
			return this.getFailedMap("类别ID不能为空");
		}
		List<ResourceType> rtList = resourceService.findResourceTypeByIds(ids);
		if(rtList == null || rtList.isEmpty()){
    		return this.getFailedMap("类别信息不存在");
		}
		for (ResourceType resourceType : rtList) {
			List<ResourceContent> type = resourceService.findResourceContentByType(resourceType.getId());
			if(type!=null&&!type.isEmpty()){
				return this.getFailedMap("抱歉，当前类别下有内容，无法删除！");
			}
		}
		resourceService.delResourceType(rtList);
		return this.getSuccessMap("删除成功");
	}
	/**
	 * 
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findContent/{id}", method=RequestMethod.GET)
	public Map<String,Object> getResourceContentById(HttpServletRequest request,@PathVariable("id")Long id){
		if(id == null || id < 1){
			return getFailedMap("内容ID异常");
		}
		ResourceContent content = resourceService.findResourceContentById(id);
		if(StringUtils.isNoneBlank(content.getFilialeIds())){
			String[] filialeIdArray = content.getFilialeIds().split(",");
			List<Long> l = new ArrayList<Long>();
			for(int i=0; i<filialeIdArray.length; i++){
				l.add(Long.parseLong(filialeIdArray[i].trim()));
			}
			content.setChenckProvince(l);
		}
		return getSuccessMap(content);
	}
	/**
	 * 查询内容信息
	 * @param request
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findContent", method=RequestMethod.GET)
	public ModelAndView getResourceContent(
			HttpServletRequest request,
			@RequestParam(required = false) Integer pageNo,
			@RequestParam(required = false) String type,
			@RequestParam(required = false) Integer pageSize
			){
		pageNo =  pageNo > 0 ? pageNo-1 : 0;
		/*if("web".equalsIgnoreCase(type)&&20==pageSize){
			pageSize = 10;
		}*/
		if(null==type){
			pageSize = 10;
		}
		//PageRequest page = new PageRequest(pageNo, pageSize > 0 ? pageSize : 20, Sort.Direction.DESC, "id");
		PageRequest page = new PageRequest(pageNo, pageSize > 0 ? pageSize : 10, Sort.Direction.DESC, "id");
		Page<ResourceContent> resPage = resourceService.findResourceContentAll(page);
		List<ResourceContent> contentList = resPage.getContent();
		//查询内容类型名称
		if(contentList != null && !contentList.isEmpty()){
			ResourceType rt = null;
			for(ResourceContent rs : contentList){
				rt = resourceService.findResourceTypeById(rs.getResType());
				if(rt != null){
					rs.setTypeName(rt.getTypeName());
				}
			}
		}
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("flag", "1");
		map.put("msg", "成功");
		map.put("total", resPage.getTotalElements());
		map.put("totalPage", resPage.getTotalPages());
		map.put("pageSize", resPage.getSize());
		map.put("curPage", pageNo+1);
		map.put("contentInfo", contentList);
		return  new ModelAndView("resource/content",map);
	}
	/**
	 * 保存内容信息
	 * @param rc
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/saveContent",method=RequestMethod.POST)
	public Map<String,Object> saveContent(ResourceContent rc,HttpSession session){
		if(rc.getResType() < 0){
    		return getFailedMap("类别名称不能为空");
		}
		if(StringUtils.isBlank(rc.getResName())){
			return getFailedMap("内容名称不能为空");
		}
		String account = (String)session.getAttribute("account");
		if(StringUtils.isNotBlank(account))
			rc.setCreator(account);
		List<Long> resIdList = rc.getChenckProvince();
		if(resIdList != null && !resIdList.isEmpty()){
			if(!resIdList.contains("1")){//1为总部,如果不包含,填充进去
				resIdList.add(1l);
			}
			rc.setFilialeIds(StringUtils.join(resIdList.toArray(),","));
		}
		rc = resourceService.saveEditResourceContent(rc);
		
		return getSuccessMap("添加成功");
	}
	public static void main(String[] args) {
		String i=",2,3,4,5";
		boolean b = i.contains("1");
		int j = i.indexOf("1");
		System.out.println("*****");
		System.out.println(b);
		System.out.println(j);
	}
	@ResponseBody
	@RequestMapping(value="/updateContent",method=RequestMethod.POST)
	public Map<String,Object> updateContent(ResourceContent rc){
		if(rc.getId() < 0){
    		return getFailedMap("内容ID不能为空");
		}
		ResourceContent rcNew = resourceService.findResourceContentById(rc.getId());
		if(rcNew == null){
    		return getFailedMap("内容不存在");
		}
		if(rc.getResType() < 0){
    		return getFailedMap("类别不能为空");
		}
		if(StringUtils.isBlank(rc.getResName())){
    		return getFailedMap("内容名称不能为空");
		}
		rcNew.setResType(rc.getResType());
		rcNew.setResName(rc.getResName());
		rcNew.setResVersion(rc.getResVersion());
		rcNew.setResDesc(rc.getResDesc());
		rcNew.setFileList(rc.getFileList());
		
		rc = resourceService.saveEditResourceContent(rc);
		return getSuccessMap("更新成功");
	}
	/**
	 * 删除内容
	 * @param contentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delContent",method=RequestMethod.POST)
	public Map<String,Object> delContent(HttpServletRequest request,HttpServletResponse response,@RequestParam(value="ids")List<Long> ids){
		if(ids == null || ids.isEmpty()){
			return this.getFailedMap("内容ID不能为空");
		}
		List<ResourceContent> rcList = resourceService.findResourceContentByIds(ids);
		resourceService.delResourceContent(rcList);
		return getSuccessMap("删除成功");
	}
	/**
	 * 获取模板
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/findTemplet",method=RequestMethod.GET)
	public ModelAndView findTemplet(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String,Object>();
		List<TempletInfo> templetList = resourceService.findTempletAll();
		for(TempletInfo tmp : templetList){
			Manager m = managerService.getMnager(tmp.getCreator());
			tmp.setCreatorName(m.getAccount());
			if(StringUtils.isNotBlank(tmp.getFilePath()))
				tmp.setFileUrl(accessUrl + tmp.getFilePath());
		}
		map.put("flag", "1");
		map.put("msg", "成功");
		map.put("templetInfo", templetList);
		return  new ModelAndView("resource/templet",map);
	}
	/**
	 * 
	 * @param request
	 * @param templetId
	 * @param filePath
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateTemplet",method=RequestMethod.POST)
	public Map<String,Object> updateTemplet(HttpServletRequest request,long templetId,String filePath){
		if(templetId < 0){
			return getFailedMap("内容ID不能为空");
		}
		if(StringUtils.isBlank(filePath)){
    		return getFailedMap("附件内容不能为空");
		}
		TempletInfo templet = resourceService.findTempletById(templetId);
		if(templet == null){
    		return getFailedMap("模板不存在");
		}
		long adminId = (Long)request.getSession().getAttribute("userId");
		templet.setFilePath(filePath);
		templet.setCreator(adminId);
		templet.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		resourceService.saveEditTempletInfo(templet);
		return  getSuccessMap("成功");
	}
	/**
	 * 删除附件
	 * @param contentId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delFile",method=RequestMethod.POST)
	public Map<String,Object> delFile(long fileId){
		if(fileId < 0){
			return getFailedMap("文件ID不能为空");
		}
		ResourceFileInfo rf = resourceService.findFileById(fileId);
		if(rf == null){
    		return getSuccessMap("");
		}
		resourceService.delFileInfo(rf.getId());
		return getSuccessMap("删除成功");
	}
}
