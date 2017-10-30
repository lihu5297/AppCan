package org.zywx.coopman.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.VIDEO_TYPE;
import org.zywx.coopman.entity.Video;

public interface VideoDao extends PagingAndSortingRepository<Video, Long>{

	List<Video> findByTypeAndDelOrderBySortDesc(VIDEO_TYPE junior, DELTYPE normal);

	Video findByIdAndDel(long id, DELTYPE normal);

}
