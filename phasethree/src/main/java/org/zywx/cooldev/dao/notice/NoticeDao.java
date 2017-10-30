package org.zywx.cooldev.dao.notice;

import java.io.Serializable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.notice.Notice;

public interface NoticeDao extends PagingAndSortingRepository<Notice, Serializable>{

	Notice findByRecievedIdAndIdAndDel(Long loginUserId, Long noId, DELTYPE normal);


}
