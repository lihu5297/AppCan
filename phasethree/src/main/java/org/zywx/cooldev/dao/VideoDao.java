package org.zywx.cooldev.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.VIDEO_STATUS;
import org.zywx.cooldev.commons.Enums.VIDEO_TYPE;
import org.zywx.cooldev.entity.Video;

public interface VideoDao extends PagingAndSortingRepository<Video, Long>{

	Video findByIdAndDel(long id, DELTYPE normal);

	Page<Video> findByTypeAndStatusAndDel(VIDEO_TYPE type,
			VIDEO_STATUS publish, DELTYPE normal,Pageable pageable);

}
