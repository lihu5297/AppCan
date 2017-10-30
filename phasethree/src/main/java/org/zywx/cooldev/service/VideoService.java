package org.zywx.cooldev.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.VIDEO_STATUS;
import org.zywx.cooldev.commons.Enums.VIDEO_TYPE;
import org.zywx.cooldev.entity.Video;



/**
 * 
 * @author yongwen.wang
 * @date 2016-10-26
 */
@Service
public class VideoService extends BaseService{
	public Page<Video> videoList(VIDEO_TYPE type,Pageable pageable) {
		Page<Video> videoList=this.videoDao.findByTypeAndStatusAndDel(type,VIDEO_STATUS.PUBLISH,DELTYPE.NORMAL,pageable);
		return videoList;
	}
	public Video videoDetail(long id) {
		Video video=this.videoDao.findByIdAndDel(id,DELTYPE.NORMAL);
		return video;
	}
}
