package org.zywx.cooldev.dao.notice;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.entity.notice.NoticeModule;

public interface NoticeModuleDao extends PagingAndSortingRepository<NoticeModule, Serializable>{

	NoticeModule findByNoModuleType(NOTICE_MODULE_TYPE noModuleType);

}
