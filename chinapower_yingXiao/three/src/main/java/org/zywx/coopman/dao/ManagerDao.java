package org.zywx.coopman.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.MANAGER_TYPE;
import org.zywx.coopman.entity.Manager;

public interface ManagerDao extends PagingAndSortingRepository<Manager, Serializable>{

	Manager findByAccountAndDel(String account, DELTYPE normal);

	Page<Manager> findByUserNameLike(String queryKey, Pageable page);

	Page<Manager> findByAccountLike(String queryKey, Pageable page);

	Page<Manager> findByAccountLikeOrEmailLike(String queryKey, String queryKey2, Pageable page);

	List<Manager> findByType(MANAGER_TYPE superadmin);

	Page<Manager> findByAccountLikeOrEmailLikeAndType(String queryKey, String queryKey2, MANAGER_TYPE admin,
			Pageable page);

	Page<Manager> findByType(MANAGER_TYPE admin, Pageable page);

}
