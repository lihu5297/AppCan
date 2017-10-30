package org.zywx.cooldev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.entity.Advice;
import org.zywx.cooldev.entity.filialeInfo.FilialeInfo;
import org.zywx.cooldev.service.FilialeInfoService;
/**
 * 网省下属分公司
 * @author 东元
 *
 */
@Controller
@RequestMapping(value="/filiale")
public class FilialeInfoController extends BaseController {

	@Autowired
	private FilialeInfoService filialeService;
	
	@ResponseBody
	@RequestMapping(value = "/findAll", method=RequestMethod.GET)
	public Map<String, Object> findAllFilialeInfo(HttpServletRequest request){
		List<FilialeInfo> filialeList = filialeService.findFilialeInfoAll();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("filialeInfo", filialeList);
		return this.getSuccessMap(map);
	}
	
	@ResponseBody
	@RequestMapping(value = "/test", method=RequestMethod.POST)
	public Map<String, Object> test(HttpServletRequest request,@RequestBody List<Advice> adList){
		return this.getSuccessMap(adList);
	}
}
