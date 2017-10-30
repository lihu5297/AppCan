package org.zywx.cooldev.dao;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.GitOperationLog;

public interface GitOperationLogDao extends PagingAndSortingRepository<GitOperationLog, Serializable>{

}
