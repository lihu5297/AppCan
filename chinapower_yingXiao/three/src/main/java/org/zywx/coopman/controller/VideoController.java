package org.zywx.coopman.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.QueryEntity;
import org.zywx.coopman.entity.Video;

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
	@RequestMapping(value="",method=RequestMethod.POST)
	public Map<String,Object> addVideo(Video video){
		try{
			this.videoService.addVideo(video);
			return this.getSuccessMap(video);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	@RequestMapping(value="list",method=RequestMethod.GET)
	public ModelAndView videoList(QueryEntity queryEntity){
		try{
			int pageNo       = queryEntity.getPageNo();
			int pageSize     = queryEntity.getPageSize();
			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "sort");
			return this.videoService.videoList(pageable);
		}catch(Exception e){
			e.getStackTrace();
			return null;
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
	//上传视频
	@ResponseBody
	@RequestMapping(value="/upload",method=RequestMethod.POST)
	public Map<String,Object> uploadVideo(MultipartFile videoZip){
		try{
			Map<String,Object> map=this.videoService.uploadVideo(videoZip);
			return this.getSuccessMap(map);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	@ResponseBody
	@RequestMapping(value="/edit",method=RequestMethod.POST)
	public Map<String,Object> updateVideo(Video video){
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			int affect=this.videoService.updateVideo(video);
			map.put("affect",affect);
			return this.getSuccessMap(map);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	@ResponseBody
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public Map<String,Object> deleteVideos(String ids){
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			int affect=this.videoService.deleteVideos(ids);
			map.put("affect",affect);
			return this.getSuccessMap(map);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	@ResponseBody
	@RequestMapping(value="/sort",method=RequestMethod.POST)
	public Map<String,Object> updateSort(@RequestParam(value="ids") ArrayList<Long> ids,@RequestParam(value="sorts") ArrayList<Long> sorts){
		try{
			Map<String,Object> map=new HashMap<String,Object>();
			int affect=this.videoService.updateSort(ids,sorts);
			map.put("affect",affect);
			return this.getSuccessMap(map);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
