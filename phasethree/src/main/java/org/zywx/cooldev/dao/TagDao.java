package org.zywx.cooldev.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.entity.Tag;

public interface TagDao extends PagingAndSortingRepository<Tag, Long> {

	public Tag findOneByName(String name);

}
