package org.zywx.coopman.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.DiskStatistic;

@Controller
@RequestMapping("/disk")
public class DiskStatisticController extends BaseController{
	
	@RequestMapping(value="/statistic")
	public ModelAndView getDiskStatistic(HttpServletRequest request){
		List<DiskStatistic> list = this.diskStatisticService.updateAndGetFromServer();
		ModelAndView mav = new ModelAndView();
		mav.addObject("statistic",list);
		mav.setViewName("statistic/diskSatistic");
		return mav;
	}
}
