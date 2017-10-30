package org.zywx.cooldev.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.dao.auth.PermissionDao;
import org.zywx.cooldev.entity.auth.Permission;

/**
 * 鉴权服务
 * @author yang.li
 * @date 2015-08-06
 *
 */
public abstract class AuthService extends BaseService {
    @Autowired
    private PermissionDao permissionDao;
	public abstract List<Permission> getPermissionList(long loginUserId, long entityId);
}
