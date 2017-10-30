package org.zywx.coopman.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.filialeInfo.FilialeInfo;
import org.zywx.coopman.service.FilialeInfoService;
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
	public Map<String, Object> findAllFilialeInfo(HttpServletRequest request, HttpSession session){
		Long adminId = (Long)session.getAttribute("userId");
		if(adminId == null || adminId < 1){
			return this.getFailedMap("登录失效，请重新登录");
		}
		Manager man = managerService.findById(adminId);
		if(man == null){
			return this.getFailedMap("登录管理员不存在");
		}
		if(man.getFilialeId() < 1){
			return this.getFailedMap("登录管理员不属于任何网省公司");
		}
		FilialeInfo fi = filialeService.findById(man.getFilialeId());
		if(fi == null){
			return this.getFailedMap("登录管理员所属网省公司不存在");
		}
		List<FilialeInfo> filialeList = null;
		if(fi.getId()==1){
			filialeList = filialeService.findFilialeInfoAll();
		}else{
			filialeList = new ArrayList<FilialeInfo>();
			filialeList.add(fi);
		}
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("filialeInfo", filialeList);
		return this.getSuccessMap(map);
	}
}
