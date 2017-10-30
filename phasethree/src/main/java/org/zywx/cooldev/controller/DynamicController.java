package org.zywx.cooldev.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.vo.DynamicVO;

/**
 * 动态控制层
    * @Description: 
    * @author jingjian.wu
    * @date 2015年8月18日 下午7:57:54
    *
 */
@Controller
@RequestMapping(value = "/dynamic")
public class DynamicController extends BaseController{
	
	/**
	    * @Description:查询动态 
	    * @param @param loginUserId
	    * @param @param date
	    * @param @param interval
	    * @param @return 
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月18日 下午8:56:21
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/bydate",method=RequestMethod.GET)
	public Map<String, Object> findDynamicByDate(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			String date,Long interval,Long projectId){
		//不传日期,默认当天
		if(null == date || "".equals(date.trim())){
			date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
		}
		//不传查询到哪天,默认往前倒数3天
		if(null == interval){
			interval = 3L;
		}
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
//		boolean existData = this.dynamicService.existDataLessThan(loginUserId, date,projectId);
		boolean existData = this.dynamicService.existDataLessThan(loginUserId,projectId);
		if(existData){
			while(true){//直到查询到有动态为止,跳出
				try {
					tmpMap = this.dynamicService.getDynamicList(loginUserId, date, interval,projectId);
					List<Permission> permissions = this.projectService.getPermissionList(loginUserId, projectId);
					Map<String, Integer> permissionMap = new HashMap<>();
					if(permissions != null && permissions.size() > 0) {
						for(Permission permission : permissions) {
							permissionMap.put(permission.getEnName(), 1);
						}
					}
					map.put("permissions", permissionMap);
				} catch (ParseException e1) {
					e1.printStackTrace();
					return this.getFailedMap(e1.getMessage());
				}
				if(tmpMap.keySet().size()>=0){
					break;
				}
				Calendar cal = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				try {
					cal.setTime(sdf.parse(date));
				} catch (ParseException e) {
					e.printStackTrace();
					return this.getFailedMap("parseDate error!");
				}
				cal.add(Calendar.DATE, interval.intValue());
				date = sdf.format(cal.getTime());
			}
			map.put("list", tmpMap);
			return this.getSuccessMap(map); 
		}else{
			List<Permission> permissions = this.projectService.getPermissionList(loginUserId, projectId);
			Map<String, Integer> permissionMap = new HashMap<>();
			if(permissions != null && permissions.size() > 0) {
				for(Permission permission : permissions) {
					permissionMap.put(permission.getEnName(), 1);
				}
			}
			map.put("permissions", permissionMap);
			map.put("list", tmpMap);
			return this.getSuccessMap(map);
		}
		
	}
	
	/**
	 * @Description:查询动态 
	 * @param @param loginUserId
	 * @param @param date
	 * @param @param interval
	 * @param @return 
	 * @return Map<String,Object>    返回类型
	 * @user jingjian.wu
	 * @date 2015年8月18日 下午8:56:21
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> findDynamic(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			HttpServletRequest request,Long projectId){
		
		String sPageNo      = request.getParameter("pageNo");
		String sPageSize    = request.getParameter("pageSize");
		int pageNo       = 0;
		int pageSize     = 20;
		try {
			if(sPageNo != null) {
				pageNo		= Integer.parseInt(sPageNo)-1;
			}
			if(sPageSize != null) {
				pageSize	= Integer.parseInt(sPageSize);
			}
		} catch (NumberFormatException nfe) {				
			return this.getFailedMap( nfe.getMessage() );
		}
		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
		
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Integer> permissionMap = new HashMap<>();
		try{
			tmpMap = this.dynamicService.getDynamicList(loginUserId,projectId,pageable);
			List<Permission> permissions = this.projectService.getPermissionList(loginUserId, projectId);
			if(permissions != null && permissions.size() > 0) {
				for(Permission permission : permissions) {
					permissionMap.put(permission.getEnName(), 1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		map.put("permissions", permissionMap);
		map.put("list", tmpMap);
		return this.getSuccessMap(map);
		
	}
	
	
	/**
	    * @Description:获取任务的动态列表 
	    * @param @param loginUserId
	    * @param @param taskId
	    * @param @return 
	    * @return Map<String,Object>    返回类型
		* @user jingjian.wu
		* @date 2015年8月19日 上午10:26:02
	    * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/task/{taskId}",method=RequestMethod.GET)
	public Map<String, Object> findTaskDynamic(@RequestHeader(value="loginUserId",required=true) long loginUserId,
			HttpServletRequest request,@PathVariable("taskId") Long taskId){
		
		String sPageNo      = request.getParameter("pageNo");
		String sPageSize    = request.getParameter("pageSize");

		int pageNo       = 0;
		int pageSize     = 20;
		
		try {
			if(sPageNo != null) {
				pageNo		= Integer.parseInt(sPageNo)-1;
			}
			if(sPageSize != null) {
				pageSize	= Integer.parseInt(sPageSize);
			}
			
		} catch (NumberFormatException nfe) {				
			return this.getFailedMap( nfe.getMessage() );
		}

		Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
		
		if(null == taskId){
			return this.getFailedMap("taskId is required!");
		}
		Map<String, Object> result = this.dynamicService.getTaskDynamicList(taskId,pageable);
		return this.getSuccessMap(result); 
		
	}
	
}
 