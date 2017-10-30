package org.zywx.coopman.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zywx.coopman.entity.DailyLog.OperationLog;

@Service
public class OperationLogService extends BaseService{

	public Page<OperationLog> getList(PageRequest page, Timestamp startTime, Timestamp endTime, String queryKey) {
		Page<OperationLog> page1 = this.operationLogDao.findByCreatedAtAndOperationLog(startTime,endTime,queryKey,page);
		
		return page1;
	}

	public Page<OperationLog> getList(PageRequest page, String queryKey) {
		Page<OperationLog> page1 = this.operationLogDao.findByOperationLogLike(queryKey,page);
		return page1;
	}

	public Page<OperationLog> getList(PageRequest page) {
		Page<OperationLog> page1 = this.operationLogDao.findAll(page);
		return page1;
	}

	public List<OperationLog> getList(Timestamp startTime, Timestamp endTime, String queryKey) {
		return this.operationLogDao.findByCreatedAtAndOperationLog(startTime,endTime,queryKey);
	}

	public List<OperationLog> getList(String queryKey) {
		return this.operationLogDao.findByOperationLogLike(queryKey);
	}

	public List<OperationLog> getList() {
		return (List<OperationLog>) this.operationLogDao.findAll();
	}
}
