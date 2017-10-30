package org.zywx.cooldev.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.UserActiveTool;

public interface UserActiveToolDao extends PagingAndSortingRepository<UserActiveTool, Serializable>{

	UserActiveTool findByValue(String value);

}
