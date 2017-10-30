package org.zywx.coopman.dao.process;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.entity.process.ProcessConfig;

public interface ProcessConfigDao extends PagingAndSortingRepository<ProcessConfig, Serializable> {

	List<ProcessConfig> findByProcessTemplateIdOrderBySequenceAsc(long processTemplateId);
	
	Page<ProcessConfig> findByProcessTemplateIdOrderBySequenceAsc(long processTemplateId,Pageable page);

	long countByProcessTemplateId(long processTemplateId);
	
	List<ProcessConfig> findByNameAndProcessTemplateId(String name,long processTemplateId);
}
