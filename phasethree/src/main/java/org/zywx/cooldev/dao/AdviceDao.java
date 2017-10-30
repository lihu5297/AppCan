package org.zywx.cooldev.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.Advice;

public interface AdviceDao extends PagingAndSortingRepository<Advice, Serializable>{

}
