package org.zywx.coopman.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.zywx.coopman.entity.BackupLog;

@Service
public class BackupLogService extends BaseService{

	public Page<BackupLog> getLogList(PageRequest page) {
		return this.backupLogDao.findAll(page);
	}

	public void deleteByIds(List<Long> ids) {
		for(Long id : ids){
			if(null==id || id==-1){
				continue;
			}
			this.backupLogDao.delete(id);
		}
	}

}
