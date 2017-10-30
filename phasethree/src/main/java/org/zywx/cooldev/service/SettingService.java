package org.zywx.cooldev.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.license.LicenseCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.dao.builder.SettingDao;
import org.zywx.cooldev.entity.builder.Setting;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.PropertiesLoader;


@Service
public class SettingService extends BaseService{

	@Autowired
	private SettingDao settingDao;
	
	public Setting getSetting() {
		log.info("get setting entity ,");
		Iterable<Setting> settings = this.settingDao.findAll();
		log.info("get setting entitys:"+settings);
		Iterator<Setting> setting = settings.iterator();
		return setting.next();
	}
	public void loadLicense(){
		Setting setting = getSetting();
		Cache.addObject(setting);
		String licenseStr = getLicenseStr(setting.getAuthorizePath());
		
		try {
//			String keyMD5 = LicenseCreator.getKeyMD5(prop.getProperty("product.ip"), prop.getProperty("product.mac"), prop.getProperty("product.name"));
			String keyMD5 = LicenseCreator.getKeyMD5(PropertiesLoader.getText("product.ip"), PropertiesLoader.getText("product.mac"), PropertiesLoader.getText("product.name"));
			licenseStr = LicenseCreator.decLicense(licenseStr, keyMD5);
			net.sf.json.JSONObject job = net.sf.json.JSONObject.fromObject(licenseStr);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(job==null || !job.containsKey("DATE")){
				log.info("license過期,強制停止tomcat服務");
				//System.exit(0);
				return;
			}
			Date date = sdf.parse(job.getString("DATE"));
			if(date.before(new Date())){
				//停止tomcat启动
				try {
					log.info("license過期,強制停止tomcat服務");
					//System.exit(0);
				} catch (Exception e) {			
					e.printStackTrace();
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
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
}
