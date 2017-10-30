package org.zywx.cooldev.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.license.LicenseCreator;
import org.license.LicenseUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.controller.BaseController;
import org.zywx.cooldev.dao.auth.ActionDao;
import org.zywx.cooldev.dao.auth.PermissionDao;
import org.zywx.cooldev.dao.auth.RoleAuthDao;
import org.zywx.cooldev.dao.auth.RoleDao;
import org.zywx.cooldev.dao.project.ProjectCategoryDao;
import org.zywx.cooldev.entity.auth.Action;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.builder.Setting;
import org.zywx.cooldev.entity.project.ProjectCategory;
import org.zywx.cooldev.service.SettingService;
import org.zywx.cooldev.util.PropertiesLoader;

/**
 * 项目初始化
 * @author yang.li
 * @date 2015-08-06
 *
 */
public class InitBean extends BaseController implements InitializingBean,ApplicationContextAware {
	
	@Autowired
	private ActionDao actionDao;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private PermissionDao permissionDao;
	
	@Autowired
	private RoleDao roleDao;
	
	@Autowired
	private RoleAuthDao roleAuthDao;
	
	@Autowired
	private ProjectCategoryDao projectCategoryDao;
	
	public static ApplicationContext applicationContext;

	public void afterPropertiesSet() throws Exception {
		this.loadAction();
		this.loadRole();
		this.loadProjectCategory();
		this.loadLicense();
		//settingService.loadLicense();
		System.setProperty("sun.jnu.encoding","utf-8");
		System.setProperty("file.encoding","utf-8");
	}
	private void loadLicense() {
		 Setting set = settingService.getSetting();
		 //解析License文件
		 LicenseUtil licenseUtil=new LicenseUtil();
		 boolean b = licenseUtil.hasLicenseFile(set.getAuthorizePath());
		 log.info("Setting表License文件路径  --> "+set.getAuthorizePath());
		 if(b){
			 //读取结束日期
			 JSONObject jsonObject = licenseUtil.initLicenseParser(set.getAuthorizePath());
			 log.info("解析文件  --> "+jsonObject);
			 if(jsonObject!=null){
				 //保存
				 String endTime=(String)jsonObject.get("DATE");
				//刷新缓存
				Cache.addEndTime(endTime);
				 log.info("Cache保存License结果:--> "+Cache.getEndTimeMap("endTime"));
			 }
		 }
	}
	/**
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
		init.loadRole();
		init.loadProjectCategory();
	}
	
	private void loadAction() {
		Iterator<Action> it = actionDao.findByDel(DELTYPE.NORMAL).iterator();
		while(it.hasNext()) {
			Action a = it.next();
			Cache.addAction(a);
		}
	}
	
	private void loadRole()  {
		Iterator<Role> it = roleDao.findByDel(DELTYPE.NORMAL).iterator();
		while(it.hasNext()) {
			Role r = it.next();
			List<Permission> listPermission = this.permissionDao.findByRoleIdAndDelType(r.getId(), DELTYPE.NORMAL);
			r.setPermissions(listPermission);
			// 测试用初始化角色
			/*List<Permission> list = new ArrayList<Permission>();
			Permission p = new Permission();
			p.setEnName("task_retrieve");
			list.add(p);
			r.setPermissions(list);*/
			try {
				Cache.addRole(r);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void loadProjectCategory() {
		Iterator<ProjectCategory> it = projectCategoryDao.findByDel(DELTYPE.NORMAL).iterator();
		while(it.hasNext()) {
			ProjectCategory pc = it.next();
			Cache.addProjectCategory(pc);
		}
	}
	
//	private void loadLicense(){
//		Setting setting = this.settingService.getSetting();
//		Cache.addObject(setting);
//		String licenseStr = getLicenseStr(setting.getAuthorizePath());
//		
//		try {
//			
////			String keyMD5 = LicenseCreator.getKeyMD5(prop.getProperty("product.ip"), prop.getProperty("product.mac"), prop.getProperty("product.name"));
//			String keyMD5 = LicenseCreator.getKeyMD5(PropertiesLoader.getText("product.ip"), PropertiesLoader.getText("product.mac"), PropertiesLoader.getText("product.name"));
//			licenseStr = LicenseCreator.decLicense(licenseStr, keyMD5);
//			net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(licenseStr);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			if(job==null || !job.containsKey("DATE")){
//				System.exit(0);
//				return;
//			}
//			Date date = sdf.parse(job.getString("DATE"));
//			if(date.before(new Date())){
//				//停止tomcat启动
//				try {
//					
//					System.exit(0);
//					
//				} catch (Exception e) {			
//					e.printStackTrace();
//				}
//			}
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		
//	}
	
	public String getLicenseStr(String authorizePath) {
		File file = new File(authorizePath);
		if(file.exists()){
			String license = null;
			FileInputStream in;
			byte[] b = new byte[]{};

			try {
				in = new FileInputStream(authorizePath);
				b = new byte[in.available()]; // 新建一个字节数组
				in.read(b); // 将文件中的内容读取到字节数组中
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			license = new String(b); // 再将字节数组中的内容转化成字符串形式输出
			return license;
		}
		return null;
	}
	
	    /* (非 Javadoc)
	     * 
	     * 
	     * @param applicationContext
	     * @throws BeansException
	     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	     */
	    
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}
