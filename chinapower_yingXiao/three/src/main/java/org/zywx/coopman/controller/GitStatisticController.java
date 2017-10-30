package org.zywx.coopman.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.GitOperationLog;
import org.zywx.coopman.entity.QueryEntity;

@Controller
@RequestMapping("/git")
public class GitStatisticController extends BaseController{

	@RequestMapping("/statistic")
	public ModelAndView getGitStatistic(HttpServletRequest request,QueryEntity queryEntity){
		Page<GitOperationLog> list = this.gitStatisticService.findAllBySearch(queryEntity);
		ModelAndView mav = new ModelAndView();
		mav.addObject("gits",list.getContent());
		mav.addObject("total", list.getTotalElements());
		mav.addObject("totalPage", list.getTotalPages());
		mav.addObject("curPage", queryEntity.getPageNo());
		mav.addObject("pageSize", queryEntity.getPageSize());
		mav.addObject("queryKey", queryEntity.getSearch());
		mav.addObject("startTime", queryEntity.getStartTime());
		mav.addObject("endTime", queryEntity.getEndTime());
		mav.setViewName("statistic/gitSatistic");
		return mav;
	}
	
	@RequestMapping(value="/export")
	public void expotExcel(HttpServletRequest request,QueryEntity queryEntity,HttpServletResponse response){
		this.gitStatisticService.exportExcel(response,queryEntity);
	}
	
}
