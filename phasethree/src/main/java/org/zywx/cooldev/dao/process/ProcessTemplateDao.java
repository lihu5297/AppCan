package org.zywx.cooldev.dao.process;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ProcessTemplateStatus;
import org.zywx.cooldev.entity.process.ProcessTemplate;

public interface ProcessTemplateDao extends PagingAndSortingRepository<ProcessTemplate, Serializable> {
	public List<ProcessTemplate> findAll();

	public ProcessTemplate findByIdAndDel(long processId, DELTYPE normal);
	
	public ProcessTemplate findByStatusAndDel(ProcessTemplateStatus status, DELTYPE normal);
}
