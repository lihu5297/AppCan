package org.zywx.cooldev.service;

import org.springframework.stereotype.Service;
import org.zywx.cooldev.entity.Enterprise;

@Service
public class EnterpriseService extends BaseService{

	public Enterprise addEnterprise(Enterprise enterprise) {
		if(enterprise.getLinkMan()!=null && enterprise.getTelephone()!=null && enterprise.getEmailOrQQ()!=null){
			this.enterpriseDao.save(enterprise);
			return enterprise;
		}
		return null;
	}

}
