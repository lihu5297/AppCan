package org.zywx.coopman.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.UserAuth;

public interface UserAuthDao extends PagingAndSortingRepository<UserAuth, Serializable>{

	public List<UserAuth> findByUserId(long userId);
}
