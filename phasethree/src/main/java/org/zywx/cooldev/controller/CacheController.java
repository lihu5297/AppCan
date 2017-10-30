package org.zywx.cooldev.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.service.AppService;
import org.zywx.cooldev.system.InitBean;


/**
 *  刷新缓存
     * @Description: 
     * @author jingjian.wu
     * @date 2015年8月31日 下午7:04:19
     *
 */
@Controller
@RequestMapping(value = "/cache")
public class CacheController extends BaseController {
	
	@Autowired
	private AppService appService;
	
	@ResponseBody
	@RequestMapping(value="/reload/{key}", method=RequestMethod.GET)
	public Map<String, Object> refresh(
			@PathVariable("key")String key,HttpServletRequest request) {

		try {
			if("3g2win".equals(key)){
				log.info(" refresh cache at "+new Date());
				InitBean.refreshCache();
				Map<String, Integer> affected = new HashMap<>();
				affected.put("affected", 1);
				return this.getSuccessMap(affected);	
			}else{
				return this.getFailedMap("sorry!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	
	@ResponseBody
	@RequestMapping(value="/updateGit/{key}", method=RequestMethod.GET)
	public Map<String, Object> updateGitAuth(
			@PathVariable("key")String key,HttpServletRequest request) {

		try {
			if("3g2win".equals(key)){
				log.info(" Daemon update GitAuth  at "+new Date());
				String roleIdStr =request.getParameter("roleId");
				String masterStr =request.getParameter("master");
				String branchStr =request.getParameter("branch");
				int roleId = Integer.parseInt(roleIdStr);
				int master = Integer.parseInt(masterStr);
				int branch = Integer.parseInt(branchStr);
				if(master<-1 || master>1){
					return this.getFailedMap("参数非法");
				}
				if(branch<-1 || branch>1){
					return this.getFailedMap("参数非法");
				}
				log.info("roleId-->"+roleId+",master-->"+master+",branch-->"+branch);
				Map<String,String> map = appService.invokeGitAuthForDaemon(roleId, master, branch);
				log.info("invoke method updateGit for Daemon . result--->"+((null==map)?"":map.toString()));
				return this.getAffectMap();	
			}else{
				log.info("非法访问");
				return this.getFailedMap("sorry!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	
	
	

}
