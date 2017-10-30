package org.zywx.coopman.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.QueryEntity;

@Controller
@RequestMapping(value="/package")
public class PackageStatisticController extends BaseController{

	@RequestMapping(value="/statistic")
	public ModelAndView getPackageStatistic(HttpServletRequest request,QueryEntity queryEntity){
		List<Map<String, Object>> list = this.packageStatisticService.getpackageBuildInfo(queryEntity);
		Long total = this.packageStatisticService.getCountPackageBuildInfo(queryEntity);
		ModelAndView mav = new ModelAndView();
		mav.addObject("packages",list);
		mav.addObject("total", total);
		mav.addObject("totalPage", (total%queryEntity.getPageSize())==0?total/queryEntity.getPageSize():total/queryEntity.getPageSize()+1);
		mav.addObject("curPage", queryEntity.getPageNo());
		mav.addObject("pageSize", queryEntity.getPageSize());
		mav.addObject("queryKey", queryEntity.getSearch());
		mav.addObject("startTime", queryEntity.getStartTime());
		mav.addObject("endTime", queryEntity.getEndTime());
		mav.setViewName("statistic/packageStatistic");
		return mav;
		
	}
	
	@RequestMapping(value="/statistic/info")
	public Map<String, Object> getPackageBuildInfo(HttpServletRequest request,String date){
		List<Map<String, Object>> list = this.packageStatisticService.getpackageBuildInfo(date);
		
		return this.getSuccessMap(list);
		
	}
}
