package org.zywx.coopman.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.process.TaskConfigRelate;

public interface TaskConfigRelateDao extends PagingAndSortingRepository<TaskConfigRelate, Serializable>{

}
