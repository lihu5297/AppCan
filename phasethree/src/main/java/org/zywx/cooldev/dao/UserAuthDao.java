package org.zywx.cooldev.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.UserAuth;

public interface UserAuthDao extends PagingAndSortingRepository<UserAuth, Serializable>{

	List<UserAuth> findByUserId(Long userId);

	List<UserAuth> findByPermissionIdAndDel(Long permissionId, DELTYPE normal);

}
