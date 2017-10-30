package org.zywx.coopman.service;

import org.springframework.stereotype.Service;
import org.zywx.coopman.entity.UserAuth;

@Service
public class UserAuthService extends BaseService{

	
	public UserAuth addUserAuth(UserAuth ua) {
		ua = this.userAuthDao.save(ua);
		return ua;
	}

}
