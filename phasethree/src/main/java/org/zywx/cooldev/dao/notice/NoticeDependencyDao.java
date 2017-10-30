package org.zywx.cooldev.dao.notice;

import java.io.Serializable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.notice.NoticeDependency;

public interface NoticeDependencyDao extends PagingAndSortingRepository<NoticeDependency, Serializable>{

}
