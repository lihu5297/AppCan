package org.zywx.cooldev.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.IdentityCode;

public interface IdentityCodeDao extends PagingAndSortingRepository<IdentityCode, Serializable>{

	IdentityCode findByUserIdAndEmailAndCodeAndDel(long loginUserId, String email, String code, DELTYPE normal);

	
}
