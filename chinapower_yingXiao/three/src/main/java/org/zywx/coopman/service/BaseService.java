package org.zywx.coopman.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.zywx.coopman.dao.BackupLogDao;
import org.zywx.coopman.dao.DiskStatisticDao;
import org.zywx.coopman.dao.GitOperationLogDao;
import org.zywx.coopman.dao.ManagerAuthDao;
import org.zywx.coopman.dao.ManagerDao;
import org.zywx.coopman.dao.ModuleDao;
import org.zywx.coopman.dao.OperationLogDao;
import org.zywx.coopman.dao.PermissionDao;
import org.zywx.coopman.dao.PermissionTypeAuthDao;
import org.zywx.coopman.dao.PermissionTypeDao;
import org.zywx.coopman.dao.PlatFormLogDao;
import org.zywx.coopman.dao.RoleAuthDao;
import org.zywx.coopman.dao.RoleDao;
import org.zywx.coopman.dao.SettingDao;
import org.zywx.coopman.dao.TaskConfigRelateDao;
import org.zywx.coopman.dao.UserAuthDao;
import org.zywx.coopman.dao.UserDao;
import org.zywx.coopman.dao.VideoDao;
import org.zywx.coopman.dao.filialeInfo.FilialeInfoDao;
import org.zywx.coopman.dao.process.TaskConfigDao;
import org.zywx.coopman.dao.resoure.ResourceContentDao;
import org.zywx.coopman.dao.resoure.ResourceFileInfoDao;
import org.zywx.coopman.dao.resoure.ResourceFileRelationDao;
import org.zywx.coopman.dao.resoure.ResourceTypeDao;
import org.zywx.coopman.dao.resoure.TempletInfoDao;
import org.zywx.coopman.util.ProcessClearStream;

/**
 * 服务基类
 * @author yang.li
 * @date 2015-08-06
 *
 */
@Service
public class BaseService {

	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	@Autowired
	protected UserDao userDao;
	@Autowired
	protected UserAuthDao userAuthDao;
	@Autowired
	protected RoleDao roleDao;
	@Autowired
	protected RoleAuthDao roleAuthDao;
	
	@Autowired
	protected PermissionDao permissionDao;
	
	@Autowired
	protected PermissionTypeDao permissionTypeDao;
	
	@Autowired
	protected ManagerDao managerDao;
	@Autowired
	protected OperationLogDao operationLogDao;
	@Autowired
	protected SettingDao settingDao;
	@Autowired
	protected BackupLogDao backupLogDao;
	
	@Autowired
	protected JdbcTemplate jdbcTpl;
	@Autowired
	protected ModuleDao moduleDao;
	@Autowired
	protected ManagerAuthDao managerAuthDao;

	@Autowired
	protected GitOperationLogDao gitOperationLogDao;
	
	@Autowired
	protected DiskStatisticDao diskStatisticDao;
	
	@Autowired
	protected TaskConfigDao taskConfigDao;
	
	@Autowired
	protected PlatFormLogDao platFormLogDao;
	
	@Autowired
	protected TaskConfigRelateDao taskConfigRelateDao;
	
	@Autowired
	protected PermissionTypeAuthDao permissionTypeAuthDao;
	
	@Autowired
	protected VideoDao videoDao;
	@Autowired
	protected FilialeInfoDao filialeInfoDao;
	@Autowired
	protected ResourceContentDao resourceContentfoDao;
	@Autowired
	protected ResourceFileInfoDao resourceFileInfoDao;
	@Autowired
	protected ResourceTypeDao resourceTypeDao;
	@Autowired
	protected ResourceFileRelationDao resourceFileRelationDao;
	@Autowired
	protected TempletInfoDao templetInfoDao;
	
	
	
	
 	public String execShell(String command) {          
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        try {  
            Process p = run.exec(command);
            new ProcessClearStream(p.getErrorStream(),"BaseService-ERROR").start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// 打印信息
			String line = null;
			while ((line = br.readLine()) != null) {
				ret.append(line+"\n\r");
			}
			p.getErrorStream().close();
			br.close();
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret.toString();
 	}
 	
 	public String execShellForErrorInfo(String command) {          
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        StringBuffer errorStream = new StringBuffer();
        try {  
            Process p = run.exec(command);
            new ProcessClearStream(p.getInputStream(),"BaseService-INFO",ret).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			// 打印信息
			String line = null;
			while ((line = br.readLine()) != null) {
				errorStream.append(line+"\n\r");
			}
			p.getErrorStream().close();
			br.close();
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return errorStream.toString();
 	}
}
