package org.zywx.cooldev.entity.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.license.LicenseCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.zywx.cooldev.entity.builder.Setting;
import org.zywx.cooldev.service.SettingService;
import org.zywx.cooldev.system.InitBean;
import org.zywx.cooldev.util.PropertiesLoader;

@Component
public class licenseSchedule {
	
	@Autowired
	private SettingService settingService;
	
	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	@Scheduled(cron="0 0 6 * * ?")//每天早上6点
	public void sendEmailOrShutdownScheduled(){
		log.info("Six A.M. every morning to send warning");
		Setting setting = this.settingService.getSetting();
		String licenseStr = new InitBean().getLicenseStr(setting.getAuthorizePath());
		
		try {
//			String keyMD5 = LicenseCreator.getKeyMD5(PropertiesLoader.getText("product.ip"), PropertiesLoader.getText("product.mac"), PropertiesLoader.getText("product.name"));
//			licenseStr = LicenseCreator.decLicense(licenseStr, keyMD5);
//			net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(licenseStr);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			if(job==null || !job.containsKey("DATE")){
//				System.exit(0);
//			}
//			Date date = sdf.parse(job.getString("DATE"));
//			long diffTime = date.getTime()-new Date().getTime();
//			if(diffTime>0 && diffTime/(1000*60*60*24)>15){
//				//TODO
//				//授权提醒
//			}
//			
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
