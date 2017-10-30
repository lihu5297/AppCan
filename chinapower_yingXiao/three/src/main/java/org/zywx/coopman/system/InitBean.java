package org.zywx.coopman.system;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.dao.LogActionDao;
import org.zywx.coopman.dao.ModuleDao;
import org.zywx.coopman.dao.SettingDao;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.entity.DailyLog.LogAction;
import org.zywx.coopman.entity.module.Module;
import org.zywx.coopman.service.ModuleService;

/**
 * 项目初始化
 * @author yang.li
 * @date 2015-09-07
 *
 */
public class InitBean implements InitializingBean,ApplicationContextAware {
	
	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	@Autowired
	private LogActionDao logActionDao;
	@Autowired
	private SettingDao settingDao;
	@Autowired
	private ModuleService moduleService;
	
	private static ApplicationContext applicationContext;

	public void afterPropertiesSet() throws Exception {
		this.loadAction();
		this.loadSetting();
		this.loadModule();
		System.setProperty("sun.jnu.encoding","utf-8");
		System.setProperty("file.encoding","utf-8");
		log.info("=========InitBean init success=========");
	}
	
	/**
	 * @throws InterruptedException 
	 * 刷新缓存
	 *  void
	 * @user jingjian.wu
	 * @date 2015年8月31日 下午6:59:46
	 * @throws
	 */
	public static void refreshCache(){
		Cache.clearCache();
		InitBean init = (InitBean)applicationContext.getBean("initializingBean");
		init.loadAction();
		init.loadSetting();

	}
	
	private void loadSetting() {
		Setting setting = settingDao.findById(1L);
		Cache.addSetting("SETTING", setting);
	}

	private void loadAction() {
		Iterator<LogAction> it = logActionDao.findByDel(DELTYPE.NORMAL).iterator();
		while(it.hasNext()) {
			LogAction a = it.next();
			Cache.addAction(a);
		}
	}

	private void loadModule(){
		List<Module> it = moduleService.findAll();
		for(Module module : it){
			Cache.addModule(module);
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.info("---load applicationContext begin---");
		InitBean.applicationContext = applicationContext;
		log.info("---load applicationContext over---");
		
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	

}
