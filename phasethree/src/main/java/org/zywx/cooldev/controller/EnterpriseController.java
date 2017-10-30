package org.zywx.cooldev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.entity.Enterprise;

@Controller
@RequestMapping(value="/info")
public class EnterpriseController extends BaseController{
	
	@ResponseBody
	@RequestMapping(value="",method=RequestMethod.POST)
	public ModelAndView addEnterpriseInfo(Enterprise enterprise){
		enterprise = this.enterpriseService.addEnterprise(enterprise);
		if(enterprise==null){
			return this.getFailedModel("信息不正确，添加失败！");
		}
		return this.getSuccessModel("添加成功");
	}

}
