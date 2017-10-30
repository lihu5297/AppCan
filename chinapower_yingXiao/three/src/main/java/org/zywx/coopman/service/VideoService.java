package org.zywx.coopman.service;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.VIDEO_TYPE;
import org.zywx.coopman.entity.Video;

/**
 * 
 * @author yongwen.wang
 * @date 2016-10-26
 */
@Service
public class VideoService extends BaseService{
	@Value("${rootpath}")
    private String rootpath;
	@Value("${resource.rootUrl}")
	private String resourceRootUrl;
	public void addVideo(Video video) {
		this.videoDao.save(video);
		video.setSort(video.getId());
		this.videoDao.save(video);
	}

	public ModelAndView videoList(Pageable pageable) {
		List<Video> videoJuniorList=this.videoDao.findByTypeAndDelOrderBySortDesc(VIDEO_TYPE.JUNIOR,DELTYPE.NORMAL);
		List<Video> videoMiddleList=this.videoDao.findByTypeAndDelOrderBySortDesc(VIDEO_TYPE.MIDDLE,DELTYPE.NORMAL);
		List<Video> videoSeniorList=this.videoDao.findByTypeAndDelOrderBySortDesc(VIDEO_TYPE.SENIOR,DELTYPE.NORMAL);
		ModelAndView mv = new ModelAndView();
		mv.setViewName("video/videoList");
		mv.addObject("title", "视频维护");
		mv.addObject("videoJuniorList", videoJuniorList);
		mv.addObject("juniorTotal",videoJuniorList.size());
		mv.addObject("videoMiddleList", videoMiddleList);
		mv.addObject("middleTotal",videoMiddleList.size());
		mv.addObject("videoSeniorList", videoSeniorList);
		mv.addObject("seniorTotal",videoSeniorList.size());
		return mv;
	}

	public Video videoDetail(long id) {
		Video video=this.videoDao.findByIdAndDel(id,DELTYPE.NORMAL);
		return video;
	}

	public Map<String, Object> uploadVideo(MultipartFile videoZip) throws IllegalStateException, IOException {
		String os = System.getProperty("os.name");
		String videoDir="";
		//获取文件名称
		String fileName =System.currentTimeMillis()+videoZip.getOriginalFilename();
		if (os.toLowerCase().startsWith("win")) {
			videoDir="C:\\mas_upload\\coopDevelopment_online\\video\\";
		}else{
			videoDir=rootpath+File.separator+"video";
		}
		File videoDirFile = new File(videoDir);  
        if(!videoDirFile.exists()){  
        	videoDirFile.mkdirs();  
        }
        //获取绝对路径
        File destDir =new File(videoDirFile.getAbsolutePath()+File.separator+fileName);
    	//复制文件
        videoZip.transferTo(destDir);
        Map<String, Object> map=new HashMap<String,Object>();
        map.put("url",resourceRootUrl+"/video/"+fileName);
		return map;
	}

	public int updateVideo(Video video) {
		this.videoDao.save(video);
		return 1;
	}

	public int deleteVideos(String ids) {
		String sql="update T_VIDEO set del=1 where id in ("+ids+")";
		int num=this.jdbcTpl.update(sql);
		return num;
	}
	public int updateSort(ArrayList<Long> ids, ArrayList<Long> sorts) {
		final List<Map<String,Long>> videoList=new ArrayList<Map<String,Long>>();
		int m=0;
		for(Long id:ids){
			Map<String,Long> map=new HashMap<String,Long>();
			map.put("id", id);
			map.put("sort", sorts.get(m));
			m++;
			videoList.add(map);
		}
		String sql="update T_VIDEO set sort=? where id=?";
		this.jdbcTpl.batchUpdate(sql, new BatchPreparedStatementSetter() {  
            public int getBatchSize() {  
                return videoList.size();  
                //这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size();  
            }  
            public void setValues(PreparedStatement ps, int i)throws SQLException {  
            	Map<String,Long> videoSg=videoList.get(i);
                ps.setLong(1, videoSg.get("sort"));  
                ps.setLong(2, videoSg.get("id"));  
            }  
        });  
		return ids.size();
	}
	public static void main(String[] args) {
		List<Integer> ids=new ArrayList<Integer>(Arrays.asList(1,2,3));
		List<Map<String,Integer>> list=new ArrayList<Map<String,Integer>>();
		for(Integer id:ids){
			Map<String,Integer> map=new HashMap<String,Integer>();
			map.put("id",id);
			list.add(map);
		}
		System.out.println(list.toString());
	}
}
