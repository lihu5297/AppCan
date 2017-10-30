package org.zywx.cooldev.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.Enterprise;

public interface EnterpriseDao extends PagingAndSortingRepository<Enterprise, Serializable>{

}
