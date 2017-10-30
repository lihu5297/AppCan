package org.zywx.cooldev.dao.process;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.process.ProcessConfig;

public interface ProcessConfigDao extends PagingAndSortingRepository<ProcessConfig, Serializable> {

	List<ProcessConfig> findByProcessTemplateId(long processTemplateId);
	
	List<ProcessConfig> findByProcessTemplateIdOrderBySequenceAsc(long processTemplateId);

	long countByProcessTemplateId(long processTemplateId);
	
}
