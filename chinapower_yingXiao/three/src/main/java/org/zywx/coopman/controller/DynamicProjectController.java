package org.zywx.coopman.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.dynamicproject.ProjectDay;
import org.zywx.coopman.entity.dynamicproject.ProjectHour;
import org.zywx.coopman.entity.dynamicproject.ProjectMonth;
import org.zywx.coopman.entity.dynamicproject.ProjectWeek;
import org.zywx.coopman.service.DynamicProjectService;


    /**
	 * @author jingjian.wu
	 * @date 2015年9月10日 下午5:58:44
	 */

@Controller
@RequestMapping(value="/dynamic/project")
public class DynamicProjectController extends BaseController {
	
	@Autowired
	private DynamicProjectService dynamicProjectService;

	@RequestMapping(value = "toList")
	public ModelAndView toList(){
		return new ModelAndView("dynamicProject/project");
	}
	
	/**
	 * 按小时统计团队使用频率(动态信息)
	 * @param date 要查看哪一天的团队动态
	 * @param dynamicType   all所有团队动态,task 团队下的任务动态
	 * @param viewType    sum 动态总量     avg 动态平均值
	 * @user jingjian.wu
	 * @date 2016年1月20日 下午4:05:03
	 */
	@ResponseBody
	@RequestMapping(value="hour")
	public ModelAndView  listhour(@RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize,HttpServletRequest request,String date,String dynamicType,String viewType,String keyWords){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if (pageNo == null || pageNo<1){
				pageNo = 1;
			}
			if (pageSize == null){
				pageSize = 10;
			}
			if(StringUtils.isBlank(dynamicType)){
				dynamicType="all";
			}
			if(StringUtils.isBlank(viewType)){
				viewType = "sum";
			}
			if(StringUtils.isBlank(date)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				date = sdf.format(cal.getTime());
			}
			List<ProjectHour> list = dynamicProjectService.findHourList(date,dynamicType,viewType,keyWords);
			if(null!=list){
				map.put("total", list.size());
				map.put("pageSize", pageSize);
				int totalPages = (list.size()-1)/pageSize+1;
				if(pageNo>totalPages){
					pageNo = totalPages;
				}
				map.put("pageNo", pageNo);
				map.put("totalPages", totalPages);
				
				list = list.subList((pageNo-1)*pageSize, (pageNo*pageSize> list.size())?( list.size()):pageNo*pageSize);
				map.put("list", list);
			}else{
				map.put("list", list);
			}
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "hour");
			map.put("keyWords", keyWords);
			map.put("date", date);
		} catch (Exception e) {
			map.put("error", e.getMessage());
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "hour");
			map.put("keyWords", keyWords);
			map.put("date", date);
			e.printStackTrace();
		}
		return  new ModelAndView("dynamicProject/projecthour",map);
	}
	
	@ResponseBody
	@RequestMapping(value="day")
	public ModelAndView  listday(@RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize,HttpServletRequest request,String begin,String end,String dynamicType,String viewType,String keyWords){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if (pageNo == null || pageNo<1){
				pageNo = 1;
			}
			if (pageSize == null){
				pageSize = 10;
			}
			if(StringUtils.isBlank(dynamicType)){
				dynamicType="all";
			}
			if(StringUtils.isBlank(viewType)){
				viewType = "sum";
			}
			Calendar cal = Calendar.getInstance();
			if(StringUtils.isBlank(begin)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-");
				begin = sdf.format(cal.getTime())+"01";
			}
			if(!begin.substring(0, 7).equals(end.substring(0, 7))){
				map.put("error", "起止时间必须在同一个月份内"); 
				map.put("dynamicType", dynamicType);
				map.put("viewType", viewType);
				map.put("classType", "day");
				map.put("keyWords", keyWords);
				map.put("begin", begin);
				map.put("end", end);
				return  new ModelAndView("dynamicProject/projectday",map);
			}
			List<ProjectDay> list = dynamicProjectService.findDayList(begin,end,dynamicType,viewType,keyWords);
			if(null!=list){
				map.put("total", list.size());
				map.put("pageSize", pageSize);
				int totalPages = (list.size()-1)/pageSize+1;
				if(pageNo>totalPages){
					pageNo = totalPages;
				}
				map.put("pageNo", pageNo);
				map.put("totalPages", totalPages);
				
				list = list.subList((pageNo-1)*pageSize, (pageNo*pageSize> list.size())?( list.size()):pageNo*pageSize);
				map.put("list", list);
			}else{
				map.put("list", list);
			}
			
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "day");
			map.put("keyWords", keyWords);
			map.put("begin", begin);
			map.put("end", end);
		} catch (Exception e) {
			map.put("error", e.getMessage());
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "day");
			map.put("keyWords", keyWords);
			map.put("begin", begin);
			map.put("end", end);
			e.printStackTrace();
		}
		return  new ModelAndView("dynamicProject/projectday",map);
	}
	
	@ResponseBody
	@RequestMapping(value="week")
	public ModelAndView  listweek(@RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize,HttpServletRequest request,String begin,String end,String dynamicType,String viewType,String keyWords){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if (pageNo == null || pageNo<1){
				pageNo = 1;
			}
			if (pageSize == null){
				pageSize = 10;
			}
			if(StringUtils.isBlank(dynamicType)){
				dynamicType="all";
			}
			if(StringUtils.isBlank(viewType)){
				viewType = "sum";
			}
			Calendar cal = Calendar.getInstance();
			if(StringUtils.isBlank(end)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				end = sdf.format(cal.getTime());
			}
			if(StringUtils.isBlank(begin)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-");
				begin = sdf.format(cal.getTime())+"01-01";
			}
			if(!begin.substring(0, 4).equals(end.substring(0, 4))){
				map.put("error", "起止时间必须在同一年内"); 
				map.put("dynamicType", dynamicType);
				map.put("viewType", viewType);
				map.put("classType", "week");
				map.put("keyWords", keyWords);
				map.put("begin", begin);
				map.put("end", end);
				return  new ModelAndView("dynamicProject/projectweek",map);
			}
			List<ProjectWeek> list = dynamicProjectService.findWeekList(begin,end,dynamicType,viewType,keyWords);
			if(null!=list){
				map.put("total", list.size());
				map.put("pageSize", pageSize);
				int totalPages = (list.size()-1)/pageSize+1;
				if(pageNo>totalPages){
					pageNo = totalPages;
				}
				map.put("pageNo", pageNo);
				map.put("totalPages", totalPages);
				
				list = list.subList((pageNo-1)*pageSize, (pageNo*pageSize> list.size())?( list.size()):pageNo*pageSize);
				map.put("list", list);
			}else{
				map.put("list", list);
			}
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "week");
			map.put("keyWords", keyWords);
			map.put("begin", begin);
			map.put("end", end);
			
		} catch (Exception e) {
			map.put("error", e.getMessage());
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "week");
			map.put("keyWords", keyWords);
			map.put("begin", begin);
			map.put("end", end);
			e.printStackTrace();
		}
		return  new ModelAndView("dynamicProject/projectweek",map);
	}
	
	@ResponseBody
	@RequestMapping(value="month")
	public ModelAndView  listmonth(@RequestParam(required = false) Integer pageNo,@RequestParam(required = false) Integer pageSize,HttpServletRequest request,String begin,String end,String dynamicType,String viewType,String keyWords){
		Map<String, Object> map = new HashMap<String,Object>(); 
		try {
			if (pageNo == null || pageNo<1){
				pageNo = 1;
			}
			if (pageSize == null){
				pageSize = 10;
			}
			if(StringUtils.isBlank(dynamicType)){
				dynamicType="all";
			}
			if(StringUtils.isBlank(viewType)){
				viewType = "sum";
			}
			Calendar cal = Calendar.getInstance();
			if(StringUtils.isBlank(end)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				end = sdf.format(cal.getTime());
			}
			if(StringUtils.isBlank(begin)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-");
				begin = sdf.format(cal.getTime())+"01-01";
			}
			if(!begin.substring(0, 4).equals(end.substring(0, 4))){
				map.put("error", "起止时间必须在同一年内"); 
				map.put("dynamicType", dynamicType);
				map.put("viewType", viewType);
				map.put("classType", "month");
				map.put("keyWords", keyWords);
				map.put("begin", begin);
				map.put("end", end);
				return  new ModelAndView("dynamicProject/projectmonth",map);
			}
			List<ProjectMonth> list = dynamicProjectService.findMonthList(begin,end,dynamicType,viewType,keyWords);
			if(null!=list){
				map.put("total", list.size());
				map.put("pageSize", pageSize);
				int totalPages = (list.size()-1)/pageSize+1;
				if(pageNo>totalPages){
					pageNo = totalPages;
				}
				map.put("pageNo", pageNo);
				map.put("totalPages", totalPages);
				
				list = list.subList((pageNo-1)*pageSize, (pageNo*pageSize> list.size())?( list.size()):pageNo*pageSize);
				map.put("list", list);
			}else{
				map.put("list", list);
			}
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "month");
			map.put("keyWords", keyWords);
			map.put("begin", begin);
			map.put("end", end);
		} catch (Exception e) {
			map.put("error", e.getMessage());
			map.put("dynamicType", dynamicType);
			map.put("viewType", viewType);
			map.put("classType", "month");
			map.put("keyWords", keyWords);
			map.put("begin", begin);
			map.put("end", end);
			e.printStackTrace();
		}
		return  new ModelAndView("dynamicProject/projectmonth",map);
	}
	
	
}
