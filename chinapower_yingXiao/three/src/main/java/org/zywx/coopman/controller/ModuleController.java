package org.zywx.coopman.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zywx.coopman.entity.module.Module;

@Controller
@RequestMapping(value="/module")
public class ModuleController extends BaseController{ 

	@RequestMapping
	public Map<?,?> getAllModule(HttpServletRequest request, HttpServletResponse response){
		List<Module> modules = this.moduleService.findAll();
		return this.getSuccessMap(modules);
		
	}
}
