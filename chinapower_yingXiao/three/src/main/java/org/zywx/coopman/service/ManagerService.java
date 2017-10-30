package org.zywx.coopman.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.MANAGER_TYPE;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.filialeInfo.FilialeInfo;
import org.zywx.coopman.entity.module.ManagerAuth;
import org.zywx.coopman.entity.module.Module;

@Service
public class ManagerService extends BaseService{
	
	@Autowired
	private ModuleService moduleService;

	public Manager fingdByAccount(String account) {
		return this.managerDao.findByAccountAndDel(account,DELTYPE.NORMAL);
	}
	
	public Manager findById(Long id) {
		return managerDao.findOne(id);
	}

	public Page<Manager> getList(PageRequest page, String queryKey) {
		Page<Manager> page1 = this.managerDao.findByAccountLikeOrEmailLikeAndType(queryKey,queryKey,MANAGER_TYPE.ADMIN,page);
		List<Manager> managers = page1.getContent();
		for(Manager m : managers){
			List<Module> manageModule = this.moduleDao.findModuleByManagerId(m.getId(),DELTYPE.NORMAL);
			m.setManageModule(manageModule);
		}
		return page1;
	}

	public Page<Manager> getList(PageRequest page) {
		Page<Manager> page1 = this.managerDao.findByType(MANAGER_TYPE.ADMIN,page);
		List<Manager> managers = page1.getContent();
		for(Manager m : managers){
			List<Module> manageModule = this.moduleDao.findModuleByManagerId(m.getId(),DELTYPE.NORMAL);
			m.setManageModule(manageModule);
		}
		return page1;
	}

	public Manager getMnager(long id) {
		Manager manager = this.managerDao.findOne(id);
		List<Module> manageModule = this.moduleDao.findModuleByManagerId(manager.getId(),DELTYPE.NORMAL);
		manager.setManageModule(manageModule);
		//获取所属单位
		FilialeInfo filiale = filialeInfoDao.findOne(manager.getFilialeId());
		if(filiale != null)
			manager.setFilialeName(filiale.getFilialeName());
		else
			manager.setFilialeName("");
		return manager;
	}

	public Manager editManager(Manager manager, List<Long> modules) {
		Manager manager1 = this.managerDao.findOne(manager.getId());
		manager1.setAccount(null!=manager.getAccount()?manager.getAccount():manager1.getAccount());
		manager1.setPassword(null!=manager.getPassword()&&"".equals(manager.getPassword())?manager.getPassword():manager1.getPassword());
		manager1.setAddress(null!=manager.getAddress()?manager.getAddress():manager1.getAddress());
		manager1.setCellphone(null!=manager.getCellphone()?manager.getCellphone():manager1.getCellphone());
		manager1.setEmail(null!=manager.getEmail()?manager.getEmail():manager1.getEmail());
		manager1.setIcon(null!=manager.getIcon()?manager.getIcon():manager1.getIcon());
		manager1.setUserName(null!=manager.getUserName()?manager.getUserName():manager1.getUserName());
		manager1.setQq(null!=manager.getQq()?manager.getQq():manager1.getQq());
		manager1.setRemarks(null!=manager.getRemarks()?manager.getRemarks():manager1.getRemarks());
		manager1.setFilialeId(0!=manager.getFilialeId()?manager.getFilialeId():manager1.getFilialeId());
		manager = this.managerDao.save(manager1);
		
		List<Module> manageModule = this.moduleDao.findModuleByManagerId(manager.getId(),DELTYPE.NORMAL);
		List<Long> delIds = new ArrayList<>();
		List<Module> delModules = new ArrayList<>();
		for(Long id : modules){
			for(Module module : manageModule){
				if(id==module.getId()){
					delIds.add(id);
					delModules.add(module);
				}
			}
		}
		
		modules.removeAll(delIds);
		manageModule.removeAll(delModules);
		
		for(Long id : modules){
			ManagerAuth ma = new ManagerAuth();
			ma.setManagerId(manager.getId());
			ma.setModuleId(id);
			this.managerAuthDao.save(ma);
		}
		for(Module module : manageModule){
			List<ManagerAuth> ma = this.managerAuthDao.findByManagerIdAndModuleId(manager.getId(),module.getId());
			this.managerAuthDao.delete(ma.get(0));
		}
		
		return manager;
		
		
	}

	public Manager saveManager(Manager manager) {
		manager.setPassword(manager.getPassword()==null || manager.getPassword().equals("")?"123456":manager.getPassword());
		if(null==manager.getIcon() || "".equals(manager.getIcon())){
			manager.setIcon(defaultUri);
		}
		manager = this.managerDao.save(manager);
		List<Long> modules = manager.getModules();
		if(null!=modules){
			for(Long id : modules){
				ManagerAuth ma = new ManagerAuth();
				ma.setManagerId(manager.getId());
				ma.setModuleId(id);
				this.managerAuthDao.save(ma);
			}
		}
		return manager;
	}

	public Manager getSuperMnager() {
		List<Manager> list = this.managerDao.findByType(MANAGER_TYPE.SUPERADMIN);
		if(list.size()>0){
			return list.get(0);
		}else
			return null;
	}

	public Manager editSuperEditManager(Manager manager) {
		Manager manager1 = this.managerDao.findOne(manager.getId());
		manager1.setAccount(null!=manager.getAccount()?manager.getAccount():manager1.getAccount());
		manager1.setAddress(null!=manager.getAddress()?manager.getAddress():manager1.getAddress());
		manager1.setCellphone(null!=manager.getCellphone()?manager.getCellphone():manager1.getCellphone());
		manager1.setEmail(null!=manager.getEmail()?manager.getEmail():manager1.getEmail());
		manager1.setIcon(null!=manager.getIcon()?manager.getIcon():manager1.getIcon());
		manager1.setUserName(null!=manager.getUserName()?manager.getUserName():manager1.getUserName());
		manager1.setQq(null!=manager.getQq()?manager.getQq():manager1.getQq());
		manager1.setRemarks(null!=manager.getRemarks()?manager.getRemarks():manager1.getRemarks());
		
		manager = this.managerDao.save(manager1);
		return manager;
	}

	@Value("${SSO.loginUrl}")
	private String SSOloginUrl;
	@Value("${SSO.service}")
	private String service;
	@Value("${SSO.serviceValidate}")
	private String serviceValidate;
	@Value("${EMM.intergration}")
	private String intergration;
	
	public HashMap<String,Object> getValue() {
		HashMap<String,Object> map = new HashMap<>();
		map.put("SSOloginUrl", SSOloginUrl);
		map.put("service", service);
		map.put("serviceValidate",serviceValidate);
		map.put("intergration",intergration);
		return map;
	}

	//修复删除bug
	public void deleteByIds(List<Long> ids) {
		Manager manager = new Manager();
		for(Long id : ids){
			if(null==id || id==-1){
				continue;
			}
			manager.setId(id);
			this.managerDao.delete(manager);
		}
	}

	@Value("${manager.upload}")
	private String iconUpload;
	@Value("${manager.uri}")
	private String iconUri;
	@Value("${manager.defaultUri}")
	private String defaultUri;
	@Value("${logo}")
	private String logo;
	@Value("${logoUri}")
	private String logoUri;
	
	public Map<String, String> getProperties() {
		HashMap<String, String> map = new HashMap<>();
		map.put("iconUpload", iconUpload);
		map.put("iconUri", iconUri);
		map.put("logo", logo);
		map.put("logoUri", logoUri);
		return map;
	}

	
	/**
	 * 
	 * @describe 重新加载module	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月19日 下午4:05:33	<br>
	 * @param session
	 * @param id  <br>
	 * @returnType void
	 *
	 */
	public void reloadManagerModule(HttpSession session) {
		Manager manager = (Manager) session.getAttribute("manager");
		List<Module> modules =  this.moduleService.getModule(manager.getId());
		manager.setManageModule(modules);
		session.setAttribute("manager", manager);
	}

	
}
