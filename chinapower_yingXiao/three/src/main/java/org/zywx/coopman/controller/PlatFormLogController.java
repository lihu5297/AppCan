package org.zywx.coopman.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.QueryEntity;
import org.zywx.coopman.entity.DailyLog.PlatFormLog;
import org.zywx.coopman.service.PlatFormLogService;

@Controller
@RequestMapping(value="/platformlog")
public class PlatFormLogController extends BaseController{

	@Autowired
	private PlatFormLogService platFormLogService;
	
	@RequestMapping(value="/list")
	public ModelAndView index(HttpServletRequest request,HttpServletResponse response,QueryEntity queryEntity){
		Page<PlatFormLog> page = this.platFormLogService.getPlatFormLog(queryEntity);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list",page.getContent());
		mv.addObject("total", page.getTotalElements());
		mv.addObject("totalPage", page.getTotalPages());
		mv.addObject("curPage", queryEntity.getPageNo());
		mv.addObject("pageSize", queryEntity.getPageSize());
		mv.addObject("startTime", queryEntity.getStartTime());
		mv.addObject("endTime", queryEntity.getEndTime());
		
		mv.setViewName("/platFormLog/list");
		return mv;
	}
}
