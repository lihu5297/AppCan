package org.zywx.cooldev.controller;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.VIDEO_TYPE;
import org.zywx.cooldev.entity.Video;



/**
 * 
 * @author yongwen.wang
 * @date 2016-10-26
 *
 */
@Controller
@RequestMapping(value="/video")
public class VideoController extends BaseController{
	@ResponseBody
	@RequestMapping(value="",method=RequestMethod.GET)
	public Map<String,Object> videoList(VIDEO_TYPE type,@RequestParam(value="pageNo",required=false) String pageNo,@RequestParam(value="pageSize",required=false) String pageSize){
		try{
			int dyPageNo = 0;
			int dyPageSize = 15;
			if (pageNo != null) {
				dyPageNo = Integer.parseInt(pageNo) - 1;
			}
			if (pageSize != null) {
				dyPageSize = Integer.parseInt(pageSize);
			}
			Direction direction=Direction.DESC;
			Pageable pageable = new PageRequest(dyPageNo, dyPageSize,
					direction, "sort");
			Page<Video> videoList=this.videoService.videoList(type,pageable);
			return this.getSuccessMap(videoList);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	@ResponseBody
	@RequestMapping(value="/{id}",method=RequestMethod.GET)
	public Map<String,Object> videoDetail(@PathVariable(value="id") long id ){
		try{
			Video videoDetail= this.videoService.videoDetail(id);
			return this.getSuccessMap(videoDetail);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
