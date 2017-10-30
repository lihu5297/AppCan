package org.zywx.coopman.dao.process;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.MANAGER_TYPE;
import org.zywx.coopman.entity.Manager;
import org.zywx.coopman.entity.process.ProcessTemplate;

public interface ProcessTemplateDao extends PagingAndSortingRepository<ProcessTemplate, Serializable> {
	public List<ProcessTemplate> findAll();

	public ProcessTemplate findByIdAndDel(long processId, DELTYPE normal);
	
	public List<ProcessTemplate> findByNameAndDel(String name, DELTYPE normal);
	
	public Page<ProcessTemplate> findAll(Pageable page);
	
}
