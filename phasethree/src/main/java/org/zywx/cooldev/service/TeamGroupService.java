package org.zywx.cooldev.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.TeamGroup;
import org.zywx.cooldev.vo.TeamGroupVO;

@Service
public class TeamGroupService extends BaseService {
	
	private static String UPDATE_TEAM_MEMBER_SET_TEAMID_NULL = "update T_TEAM_MEMBER SET groupId=-1 where groupId=?";
	private static String FIND_GROUP_MEMBERSUM_BY_TEAMID = "select tg.id,tg.name,ifnull(ttt.total,0) total from  T_TEAM_GROUP tg left join (select count(1)  total,groupId from T_TEAM_MEMBER where del=0 and groupId!=-1 and teamId=? and type!=2  group by groupId) ttt on tg.id = ttt.groupId where tg.teamId=? and tg.del = 0";
	private static String FIND_GROUP_MEMBERSUM_BY_TEAMID_KEYWORDS="select tg.id,tg.name,ifnull(ttt.total,0) total from  T_TEAM_GROUP tg left join (select count(1)  total,t.groupId from T_TEAM_MEMBER t left join T_USER t1 on t.userId=t1.id where t.del=0 and t1.del=0 and t.groupId!=-1 and t.teamId=? and t.type!=2 AND (t1.email like ? and t1.userName like ? and t1.pinYinName like ?) group by groupId) ttt on tg.id = ttt.groupId where tg.teamId=? and tg.del = 0";
	/**
	 * 
	    * @Title: addTeamGroup
	    * @Description: 创建团队小组,实际参数只需要小组名称,和 teamId,其他参数都是默认值就ok
	    * @param @param tg
	    * @param @return    参数
	    * @return TeamGroup    返回类型
		* @user wjj
		* @date 2015年8月12日 上午9:50:00
	    * @throws
	 */
	public TeamGroup addTeamGroup(TeamGroup tg){
		return this.teamGroupDao.save(tg);
	}
	
	
	/**
	 * 
	    * @Title: delTeamGroupByid
	    * @Description:根据分组ID删除分组,并且将原分组下面的成员设置为无分组 
	    * @param @param id
	    * @param @return    参数
	    * @return TeamGroup    返回类型
		* @user wjj
		* @date 2015年8月12日 上午10:10:34
	    * @throws
	 */
	public TeamGroup delTeamGroupById(long id){
		TeamGroup tg = this.teamGroupDao.findOne(id);
		tg.setDel(DELTYPE.DELETED);
		this.teamGroupDao.save(tg);
		//将原先小组下面的成员,设置为无分组
		this.jdbcTpl.update(UPDATE_TEAM_MEMBER_SET_TEAMID_NULL,id);
		return tg;
	}
	
	/**
	    * @Title: findAllByTeamId
	    * @Description:根据团队标识,获取某个团队下面的未被删除的所有小组信息 
	    * @param @param teamId
	    * @param @return    参数
	    * @return List<TeamGroup>    返回类型
		* @user wjj
		* @date 2015年8月12日 上午10:18:25
	    * @throws
	 */
	public List<TeamGroup> findAllByTeamId(long teamId){
		return this.teamGroupDao.findByTeamIdAndDel(teamId, DELTYPE.NORMAL);
	}
	
	/**
	 * 
	    * @Title: findGroupInfoByTeamId
	   * @Description:获取某个team团队下面的 所有分组,及每个分组有多少人 
	    * @param @param teamId
	    * @param @return    参数
	    * @return List<TeamGroupVO>    返回类型
		* @user wjj
		* @date 2015年8月12日 下午1:51:42
	    * @throws
	 */
	public List<TeamGroupVO> findGroupInfoByTeamId(long teamId,String keywords){
		final List<TeamGroupVO> volist = new ArrayList<TeamGroupVO>();
		if(StringUtils.isNotBlank(keywords)){
			this.jdbcTpl.query(FIND_GROUP_MEMBERSUM_BY_TEAMID_KEYWORDS, new Object[]{teamId,keywords,keywords,keywords,teamId},
					new RowCallbackHandler() {
						
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							TeamGroupVO vo  = new TeamGroupVO();
							vo.setGroupId(rs.getLong("id"));
							vo.setName(rs.getString("name"));
							vo.setTotal(rs.getInt("total"));
							volist.add(vo);
						}
					});
		}else{
			this.jdbcTpl.query(FIND_GROUP_MEMBERSUM_BY_TEAMID, new Object[]{teamId,teamId},
					new RowCallbackHandler() {
						
						@Override
						public void processRow(ResultSet rs) throws SQLException {
							TeamGroupVO vo  = new TeamGroupVO();
							vo.setGroupId(rs.getLong("id"));
							vo.setName(rs.getString("name"));
							vo.setTotal(rs.getInt("total"));
							volist.add(vo);
						}
					});
		}
		return volist;
	}
	
	public TeamGroup findOne(Long id){
		return this.teamGroupDao.findOne(id);
	}
	
	public List<TeamGroup> findByNameAndDel(String name,long teamId, DELTYPE delType){
		return this.teamGroupDao.findByNameAndTeamIdAndDel(name,teamId, delType);
	}
	
}
