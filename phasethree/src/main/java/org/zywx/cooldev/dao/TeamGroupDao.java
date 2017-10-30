package org.zywx.cooldev.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.TeamGroup;

public interface TeamGroupDao extends PagingAndSortingRepository<TeamGroup, Long> {

	/**
	 * 根据teamId,删除标识获取该小组下面所有的分组信息
	    * @Title: findByTeamIdAndDel
	    * @Description: 
	    * @param @param teamId
	    * @param @param del
	    * @param @return    参数
	    * @return List<TeamGroup>    返回类型
		* @user wjj
		* @date 2015年8月11日 下午9:02:29
	    * @throws
	 */
	public List<TeamGroup> findByTeamIdAndDel(long teamId,DELTYPE del);
	
	public List<TeamGroup> findByNameAndDel(String anme,DELTYPE del);

	public List<TeamGroup> findByNameAndTeamIdAndDel(String name, long teamId, DELTYPE delType);
	
}
