package org.zywx.coopman.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.appdo.common.utils.token.TokenUtil;
import org.zywx.coopman.service.BackupLogService;
import org.zywx.coopman.service.DiskStatisticService;
import org.zywx.coopman.service.GitStatisticService;
import org.zywx.coopman.service.ManagerService;
import org.zywx.coopman.service.ModuleAuthService;
import org.zywx.coopman.service.ModuleService;
import org.zywx.coopman.service.PackageStatisticService;
import org.zywx.coopman.service.PermissionTypeService;
import org.zywx.coopman.service.RoleService;
import org.zywx.coopman.service.SettingService;
import org.zywx.coopman.service.TaskConfigService;
import org.zywx.coopman.service.UserAuthService;
import org.zywx.coopman.service.UserService;
import org.zywx.coopman.service.VideoService;

/**
 * 控制器基类
 * @author yang.li
 * @date 2015-08-06
 *
 */
public class BaseController {
	
	protected Log log = LogFactory.getLog(this.getClass().getName());	
	
	@Autowired
	protected UserService userService;
	@Autowired
	protected UserAuthService userAuthService;
	
	@Autowired
	protected RoleService roleService;
	@Autowired
	protected ManagerService managerService;
	@Autowired
	protected PermissionTypeService permissionTypeService;
	@Autowired
	protected ModuleService moduleService;
	@Autowired
	protected SettingService settingService;
	@Autowired
	protected BackupLogService backupLogService;
	
	@Autowired
	protected ModuleAuthService moduleAuthService;
	
	@Autowired
	protected TaskConfigService taskConfigService;
	
	@Autowired
	protected DiskStatisticService diskStatisticService;
	
	@Autowired
	protected GitStatisticService gitStatisticService;
	
	@Autowired
	protected PackageStatisticService packageStatisticService;
	
	@Autowired
	protected VideoService videoService;
	
	@Value("${tenantId}")
	private String tenantId;
	
	@Value("${key}")
	private String key;
	
	@Value("${serviceFlag}")
	private String serviceFlag;
	
	public String getToken() {
		if(serviceFlag.equals("enterpriseEmm3")){
			return "";
		}
		log.info("生成token: tenantId-->"+tenantId+",key-->"+key);
		String[] params = new String[2];
		params[0] = tenantId;
		params[1] = "dev";
		String token = TokenUtil.getToken(key, params);
		log.info("生成token为-->"+token);
		return token;
	}
	
	
	protected ModelAndView getFailedModel(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "failed");
		ret.put("message", message);
		return new ModelAndView("",ret);
	}
	
	protected ModelAndView getSuccessModel(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", message);
		return new ModelAndView("",ret);
	}

	protected Map<String, Object> getSuccessMap(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", message);
		return ret;
	}
	
	protected Map<String, Object> getFailedMap(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "failed");
		ret.put("message", message);
		return ret;
	}

}
