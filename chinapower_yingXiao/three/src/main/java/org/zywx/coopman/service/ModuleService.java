package org.zywx.coopman.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.module.Module;

@Service
public class ModuleService extends BaseService{

	public List<Module> findAll() {
		return this.moduleDao.findByParentId(-1L,DELTYPE.NORMAL);
	}
	
	public List<Module> findAllOrParentId() {
		return (List<Module>) this.moduleDao.findAll();
	}
	
	public List<Module> getModule(Long managerId) {
		List<Module> manageModule = this.moduleDao.findModuleByManagerId(managerId, DELTYPE.NORMAL);
		List<Module> manageModuleAll = this.findAllOrParentId();
		for(Module module : manageModule){
			getAllChildren(manageModuleAll,module);
		}
		
		List<Module> manageModuleCopy = new ArrayList<>();
		for(Module module : manageModuleAll){
			if(module.getChildrenModule()==null){
				manageModuleCopy.add(module);
			}
		}
		
		manageModuleAll.removeAll(manageModuleCopy);
		
		return manageModuleAll;
	}


	private void getAllChildren(List<Module> manageModuleAll, Module manageModule) {
		if(manageModule.getParentId()!=-1){
			for(Module module : manageModuleAll){
				if(module.getId()==manageModule.getParentId()){
					List<Module> childrenModule = module.getChildrenModule();
					if(childrenModule==null){
						childrenModule = new ArrayList<>();
					}
					childrenModule.add(manageModule);
					
					module.setChildrenModule(childrenModule);
				}
			}
		}
		
	}

}
