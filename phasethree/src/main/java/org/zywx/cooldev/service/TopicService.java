package org.zywx.cooldev.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TOPIC_MEMBER_TYPE;
import org.zywx.cooldev.dao.EntityResourceRelDao;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.query.TopicQuery;
import org.zywx.cooldev.entity.topic.Topic;
import org.zywx.cooldev.entity.topic.TopicAuth;
import org.zywx.cooldev.entity.topic.TopicComment;
import org.zywx.cooldev.entity.topic.TopicMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.RelativeDateFormat;
import org.zywx.cooldev.util.TimestampFormat;

@Service
public class TopicService extends AuthService {
	
	@Value("${user.icon}")
	private String icon;

	@Autowired
	private EntityResourceRelDao entityResourceRelDao;
	@Autowired
	private ProjectService projectService;
	
	private static String DELETE_TOPICAUTH_BY_TOPICID = "update T_TOPIC_AUTH set del=1 where memberId in (select id from T_TOPIC_MEMBER where topicId = ?)";
	private static String DELETE_TOPICMEMBER_BY_TOPICID = "update T_TOPIC_MEMBER set del=1 where topicId = ?";

	/**
	 * 分页条件查询topic列表 创建人:刘杰雄 <br>
	 * 时间:2015年8月10日 下午5:00:17 <br>
	 * 
	 * @param pageable
	 * @param project
	 * @param loginUser
	 * @param sponsor
	 * @param actor
	 * @return
	 *
	 */
	public HashMap<String, Object> getTopicList(Pageable pageable, Long project, Long loginUser, List<TOPIC_MEMBER_TYPE> type,TopicQuery query) {
		
		Integer startNum = pageable.getPageNumber()*pageable.getPageSize();
		Integer number = pageable.getPageSize();
		
		String types = "";
		
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		
		String required = (ENTITY_TYPE.TOPIC + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
		if(this.projectService.permissionMapAsMemberWith(required, loginUser).containsKey(project) && type.contains(TOPIC_MEMBER_TYPE.OTHER)){
			List<TOPIC_MEMBER_TYPE> list = new ArrayList<>();
			list.add(TOPIC_MEMBER_TYPE.ACTOR);
			list.add(TOPIC_MEMBER_TYPE.SPONSOR);
			list.add(TOPIC_MEMBER_TYPE.OTHER);
			for(int i=0;i<type.size();i++){
				if(!type.get(i).getDeclaringClass().equals(TOPIC_MEMBER_TYPE.class)){
					continue;
				}
				if(type.get(i).equals(TOPIC_MEMBER_TYPE.ACTOR) || type.get(i).equals(TOPIC_MEMBER_TYPE.SPONSOR)){
					list.remove(type.get(i));
					continue;
				}
			}
			for(int i=0;i<list.size();i++){
				if(i != list.size()-1){
					types += list.get(i).ordinal()+",";
				}
				if(i == list.size()-1){
					types += list.get(i).ordinal();
				}
			}
			sql.append(" SELECT tt.id,tt.projectId,tt.createdAt,tt.updatedAt,tt.title,tt.detail,tt.userId,tu.userName,tu.account,tu.icon FROM T_TOPIC tt LEFT JOIN T_USER tu ON tt.userId = tu.id ");
			sql1.append("SELECT count(1) AS count FROM T_TOPIC tt");
			sql.append(" WHERE tt.projectId =" + project );
			sql1.append(" WHERE tt.projectId =" + project );
			sql.append(" AND tt.id not in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where ttm.userId = "+loginUser+" and ttm.type in (" + types + ") and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
			sql1.append(" AND tt.id not in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where ttm.userId = "+loginUser+" and ttm.type in (" + types + ") and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
			if (!StringUtils.equals(query.getCreator(),"%%") || !StringUtils.equals(query.getActor(),"%%")) {
				if (!StringUtils.equals(query.getCreator(),"%%")) {
					sql.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getCreator()+"' and ttm.type=0  and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
					sql1.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getCreator()+"' and ttm.type=0 and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");

				} 
				if (!StringUtils.equals(query.getActor(),"%%")) {
					sql.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getActor()+"' and ttm.type = 1 and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
					sql1.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getActor()+"' and ttm.type = 1 and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
				}
			}
			
			sql.append(" AND tt.del =" + DELTYPE.NORMAL.ordinal() + " AND tt.title like '"+query.getTopicName()+"' ");
			if(null!=query.getCreatedAtStart()){
				sql.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') >= '"+query.getCreatedAtStart()+"' ");
			} 
			if(null!=query.getCreatedAtEnd()){
				sql.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ");
			} 
			sql.append(" ORDER BY tt.createdAt DESC LIMIT "+startNum+","+number);
			
			sql1.append(" AND tt.del =" + DELTYPE.NORMAL.ordinal() + " AND tt.title like '"+query.getTopicName()+"' ");
			if(null!=query.getCreatedAtStart()){
				sql1.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') >= '"+query.getCreatedAtStart()+"' ");
			}
			if(null!=query.getCreatedAtEnd()){
				sql1.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ");
			}
		}else{
			
			for(int i=0;i<type.size();i++){
				if(!type.get(i).getDeclaringClass().equals(TOPIC_MEMBER_TYPE.class)){
					continue;
				}
				if(i != type.size()-1){
					types += type.get(i).ordinal()+",";
				}
				if(i == type.size()-1){
					types += type.get(i).ordinal();
				}
			}
			sql.append(" SELECT tt.id,tt.projectId,tt.createdAt,tt.updatedAt,tt.title,tt.detail,tt.userId,tu.userName,tu.account,tu.icon FROM T_TOPIC tt LEFT JOIN T_USER tu ON tt.userId = tu.id ");
			sql1.append("SELECT count(1) AS count FROM T_TOPIC tt");
			sql.append(" WHERE tt.projectId =" + project );
			sql1.append(" WHERE tt.projectId =" + project );
			sql.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm where ttm.userId = "+loginUser+" and ttm.type in (" + types + ") and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
			sql1.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm where ttm.userId = "+loginUser+" and ttm.type in (" + types + ") and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
			if (!StringUtils.equals(query.getCreator(),"%%") || !StringUtils.equals(query.getActor(),"%%")) {
				if (!StringUtils.equals(query.getCreator(),"%%")) {
					sql.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getCreator()+"' and ttm.type=0  and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
					sql1.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getCreator()+"' and ttm.type=0 and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
				} 
				if (!StringUtils.equals(query.getActor(),"%%")) {
					sql.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getActor()+"' and ttm.type = 1 and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
					sql1.append(" AND tt.id in (select ttm.topicId from T_TOPIC_MEMBER ttm LEFT JOIN T_USER u on ttm.userId = u.id where u.userName like '"+query.getActor()+"' and ttm.type = 1 and ttm.del = "+ DELTYPE.NORMAL.ordinal() +")");
				}
			}
			sql.append(" AND tt.del =" + DELTYPE.NORMAL.ordinal() + " AND tt.title like '"+query.getTopicName()+"' ");
			if(null!=query.getCreatedAtStart()){
				sql.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') >= '"+query.getCreatedAtStart()+"' ");
			} 
			if(null!=query.getCreatedAtEnd()){
				sql.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ");
			} 
			sql.append("ORDER BY tt.createdAt DESC LIMIT "+startNum+","+number);
			
			sql1.append(" AND tt.del =" + DELTYPE.NORMAL.ordinal() + " AND tt.title like '"+query.getTopicName()+"' ");
			if(null!=query.getCreatedAtStart()){
				sql1.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') >= '"+query.getCreatedAtStart()+"' ");
			}
			if(null!=query.getCreatedAtEnd()){
				sql1.append(" AND DATE_FORMAT(tt.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ");
			}
		}
		
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		log.info("JdbcTemplate:" + sql);
		log.info("JdbcTemplate:" + sql1);
		list = jdbcTpl.queryForList(sql.toString());

		Map<String, Object> count = jdbcTpl.queryForMap(sql1.toString());

		List<Topic> list2 = new ArrayList<Topic>();
		if (list.size() > 0) {
			for (Map<?, ?> map : list) {
				Topic topic = new Topic();
				List<Map<String, Object>> list1 = jdbcTpl.queryForList(
						"SELECT tu.id,tu.account,tu.userName,ttc.updatedAt FROM T_TOPIC_COMMENT ttc LEFT JOIN T_USER tu on ttc.userId = tu.id WHERE ttc.topicId = ? AND ttc.del = ? ORDER BY updatedAt desc LIMIT 0,1",
						new Object[] { map.get("id"), DELTYPE.NORMAL.ordinal() });
				Map<String, Object> count1 = jdbcTpl.queryForMap(
						"SELECT count(1) as count FROM T_TOPIC_COMMENT ttc LEFT JOIN T_USER tu on ttc.userId = tu.id WHERE ttc.topicId = ? AND ttc.del = ? ",
						new Object[] { map.get("id"), DELTYPE.NORMAL.ordinal() });
				if (list1.size() < 1) {
					topic.setReplyNickName("");
					topic.setReplyId(Long.parseLong("0"));
					topic.setLastReplyTime(TimestampFormat.getTimestamp(map.get("updatedAt").toString()));
				} else {
					topic.setReplyNickName(getuserName(list1.get(0)));
					topic.setReplyId(Long.parseLong(list1.get(0).get("id").toString()));
					topic.setLastReplyTime(TimestampFormat.getTimestamp(list1.get(0).get("updatedAt").toString()));
				}
				topic.setId(Long.parseLong(map.get("id").toString()));
				topic.setUserId(loginUser);
				topic.setProjectId(project);
				topic.setTitle(map.get("title").toString());
				topic.setDetail(map.get("detail").toString());
				topic.setTopicIcon(null!=map.get("icon")?map.get("icon").toString():icon);
				topic.setCreatedAt(TimestampFormat.getTimestamp(map.get("createdAt").toString()));
				topic.setUpdatedAt(TimestampFormat.getTimestamp(map.get("updatedAt").toString()));
				topic.setTopicNickName(getuserName(map));
				topic.setReplyCounts(Integer.parseInt(count1.get("count").toString()));
				list2.add(topic);
			}
		}

		HashMap<String, Object> job = new HashMap<String, Object>();
		job.put("total", count.get("count"));
		job.put("list", list2);
		Project pro = this.projectDao.findOne(project);
		job.put("project", pro);
		return job;
	}

	/**
	 * 获取显示昵称或者账号 创建人:刘杰雄 <br>
	 * 时间:2015年8月10日 下午4:59:56 <br>
	 * 
	 * @param map
	 * @return
	 *
	 */
	public String getuserName(Map<?, ?> map) {
		if (null != map.get("userName") && !"".equals(map.get("userName").toString())) {
			return map.get("userName").toString();
		} else
			return map.get("account").toString();

	}

	/**
	 * 创建topic 并添加附属数据<br>
	 * 创建人:刘杰雄 <br>
	 * 时间:2015年8月10日 下午6:57:52 <br>
	 * 
	 * @param topic
	 * @param actors
	 * @return
	 *
	 */
	public Topic addTopic(Topic topic, List<Long> actors) {
		topic = this.topicDao.save(topic);

		TopicMember topicM = new TopicMember();
		topicM.setTopicId(topic.getId());
		topicM.setUserId(topic.getUserId());
		topicM.setType(TOPIC_MEMBER_TYPE.SPONSOR);
		topicM = this.topicMemberDao.save(topicM);
		
		TopicAuth ta = new TopicAuth();
		ta.setMemberId(topicM.getId());
		Role role = Cache.getRole(ENTITY_TYPE.TOPIC+"_"+ROLE_TYPE.CREATOR);
		ta.setRoleId(role.getId());
		ta = this.topicAuthDao.save(ta);
		
		for (Long actor : actors) {
			TopicMember tmO=this.topicMemberDao.findByTopicIdAndUserIdAndDel(topic.getId(), actor, DELTYPE.NORMAL);
			long memberId=0;
			if(tmO!=null){
				memberId=tmO.getId();
			}else{
				TopicMember topicMa = new TopicMember();
				topicMa.setTopicId(topic.getId());
				topicMa.setUserId(actor);
				topicMa.setType(TOPIC_MEMBER_TYPE.ACTOR);
				topicM = this.topicMemberDao.save(topicMa);
				memberId=topicM.getId();
			}
			TopicAuth ta1 = new TopicAuth();
			ta1.setMemberId(memberId);
			Role role1 = Cache.getRole(ENTITY_TYPE.TOPIC+"_"+ROLE_TYPE.MEMBER);
			ta1.setRoleId(role1.getId());
			ta1 = this.topicAuthDao.save(ta1);
		}

		return topic;
	}

	/**
	 * 提交topic评论 <br>
	 * 创建人:刘杰雄 <br>
	 * 时间:2015年8月10日 下午8:38:28 <br>
	 * 
	 * @param topicC
	 * @param resourceId2
	 * @return
	 *
	 */
	public TopicComment addComment(TopicComment topicC, List<Long> resourceIdList) {
		topicC = this.topicCommentDao.save(topicC);
		List<Resource> topicResource = new ArrayList<Resource>();
		if (resourceIdList != null) {
			for (Long resourceId : resourceIdList) {

				EntityResourceRel rel = new EntityResourceRel();
				rel.setResourceId(resourceId);
				rel.setEntityId(topicC.getId());
				rel.setEntityType(ENTITY_TYPE.COMMENT);
				rel = entityResourceRelDao.save(rel);
			}
			
			topicResource = this.resourcesDao.findByIdIn(resourceIdList);
			topicC.setTopicResource(topicResource);

		}
		return topicC;
	}

	/**
	 * 
	 * 功能描述：获取讨论的基本信息 <br>
	 * 创建人:刘杰雄 <br>
	 * 时间:2015年8月11日 下午3:02:22 <br>
	 * 
	 * @param topicId
	 * @return
	 *
	 */
	public Map<String, Object> getTopic(Long topicId) {

		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();

		sql.append(
				" SELECT tt.id,tt.projectId,tt.createdAt,tt.updatedAt,tt.title,tt.detail,tt.userId,tu.userName,tu.account,tu.icon FROM T_TOPIC tt LEFT JOIN T_TOPIC_MEMBER ttm on tt.id = ttm.topicId LEFT JOIN T_USER tu ON ttm.userId = tu.id ");
		sql1.append("SELECT count(1) AS count FROM T_TOPIC_COMMENT ttc ");

		sql.append(" WHERE tt.id =" + topicId + " AND ttm.type=" + TOPIC_MEMBER_TYPE.SPONSOR.ordinal());
		sql1.append(" WHERE ttc.topicId =" + topicId);

		sql.append(" AND tt.del =" + DELTYPE.NORMAL.ordinal() + "  and ttm.del=0");
		sql1.append(" AND ttc.del =" + DELTYPE.NORMAL.ordinal() + " ");

		log.info("JdbcTemplate:" + sql);
		log.info("JdbcTemplate:" + sql1);

		Map<String, Object> objMap = this.jdbcTpl.queryForMap(sql.toString());
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1.toString());

		String userName = (String) (null != objMap.get("userName") && !"".equals(objMap.get("userName"))
				? objMap.get("userName") : objMap.get("account"));
		objMap.remove("account");
		objMap.put("userName", userName);
		objMap.put("replyCounts", count.get("count"));
		objMap.put("createdAt", RelativeDateFormat.format((Date) objMap.get("createdAt")));
		objMap.put("updatedAt", RelativeDateFormat.format((Date) objMap.get("updatedAt")));


		return objMap;
	}

	/**
	 * 
	 * 功能描述：获取单一讨论详情 <br>
	 * 创建人:刘杰雄 <br>
	 * 时间:2015年8月11日 下午4:33:21 <br>
	 * 
	 * @param topicId
	 * @return
	 *
	 */
	public Topic getTopicSingle(Long topicId) {
		Topic topic = this.topicDao.findOne(topicId);
		return topic;
	}

	/**
	 * 
	 * 功能描述：更新讨论标题和内容 <br>
	 * 创建人:刘杰雄 <br>
	 * 时间:2015年8月11日 下午4:39:14 <br>
	 * 
	 * @param topic
	 * @return
	 *
	 */
	public Topic updateTopic(Topic topic) {
		topic = this.topicDao.save(topic);
		return topic;
	}

	/**
	 * 
	 * 功能描述：删除讨论 <br>
	 * 创建人:刘杰雄 <br>
	 * 时间:2015年8月11日 下午4:50:33 <br>
	 * 
	 * @param topicId
	 *
	 */
	public int deleteTopic(Long topicId) {
		String sql = "UPDATE T_TOPIC SET del = " + DELTYPE.DELETED.ordinal() + " WHERE id = " + topicId;

		int a = this.jdbcTpl.update(sql);
		
		this.jdbcTpl.update(DELETE_TOPICAUTH_BY_TOPICID,topicId);//删除讨论授权信息
		this.jdbcTpl.update(DELETE_TOPICMEMBER_BY_TOPICID,topicId);//删除讨论成员信息
		
		if (a > 0 && a == 1) {
			return a;
		} else
			return 0;
	}

	/**
	 * 
	 * @describe 获取单个实体 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:47:34 <br>
	 * @param topicCId
	 * @return
	 *
	 */
	public TopicComment getTopicCommentSingle(Long topicCId) {
		TopicComment topicComment = this.topicCommentDao.findOne(topicCId);
		return topicComment;
	}

	/**
	 * 
	 * @describe 删除评论 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午5:52:46 <br>
	 * @param topicId
	 * @return
	 *
	 */
	public int deleteTopicComment(Long topicCId) {

		String sql = "UPDATE T_TOPIC_COMMENT SET del = " + DELTYPE.DELETED.ordinal() + " WHERE id = " + topicCId;

		int a = this.jdbcTpl.update(sql);
		if (a > 0 && a == 1) {
			return a;
		} else
			return 0;
	}

	/**
	 * 
	 * @describe 获取评论列表 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月17日 下午2:10:14 <br>
	 * @param topicId
	 * @param topicCId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 *
	 */
	public HashMap<String, Object> getTopicComment(Long topicId, Long topicCId, int pageNo, int pageSize) {
		String sql = "SELECT ttc.id,ttc.updatedAt,ttc.detail,ttc.userId,tu.userName,tu.account,tu.icon,ttc.replyTo,tur.userName replyToNick,tur.account replyToAccount FROM T_TOPIC_COMMENT ttc LEFT JOIN T_USER tu ON ttc.userId = tu.id LEFT JOIN T_USER tur ON ttc.replyTo = tur.id "
				+ " WHERE ttc.topicId = " + topicId + " AND ttc.id > " + topicCId + " AND ttc.del = "
				+ DELTYPE.NORMAL.ordinal() + " ORDER BY ttc.updatedAt DESC LIMIT 0," + pageSize;
		String sql1 = "SELECT COUNT(1) AS count FROM T_TOPIC_COMMENT ttc " + " WHERE ttc.topicId = " + topicId
				+ " AND ttc.del = " + DELTYPE.NORMAL.ordinal();
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);

		// 转换昵称 和时间
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				String userName = (String) (null != map.get("userName") && !"".equals(map.get("userName"))
						? map.get("userName") : map.get("account"));
				map.remove("account");
				map.put("userName", userName);

				String replyToNick = (String) (null != map.get("replyToNick") && !"".equals(map.get("replyToNick"))
						? map.get("replyToNick") : map.get("replyToAccount"));
				map.remove("replyToAccount");
				map.put("replyToNick", replyToNick);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				map.put("updatedAt", sdf.format(map.get("updatedAt")));

				// 添加任务资源列表
				List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel((Long)map.get("id"), ENTITY_TYPE.COMMENT, DELTYPE.NORMAL);
				List<Long> resourceIdList = new ArrayList<>();
				for(EntityResourceRel rel : relList) {
					resourceIdList.add(rel.getResourceId());
				}
				if(resourceIdList.size() > 0) {
					List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
					map.put("topicResource", resources);
				}
			}
		}

		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1);

		HashMap<String, Object> job = new HashMap<String, Object>();
		job.put("total", count.get("count"));
		job.put("list", list);
		return job;
	}

	/**
	 * 
	 * @describe 验证是否有权限获取评论列表 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月11日 下午6:39:29 <br>
	 * @param topicId
	 * @param loginUserId
	 * @return
	 *
	 */
	public boolean isExistUserInMember(Long topicId, Long loginUserId) {

		// TopicMember topicMember =
		// this.topicMemberDao.findByTopicIdAndUserIdAndDel(topicId,loginUserId,DELTYPE.NORMAL.ordinal());
		String sql = "SELECT * FROM T_TOPIC_MEMBER ttm WHERE ttm.topicId = " + topicId + " AND ttm.userId = "
				+ loginUserId + " AND ttm.del = " + DELTYPE.NORMAL.ordinal();
		List<Map<String, Object>> topicMember = this.jdbcTpl.queryForList(sql);
		if (topicMember.size() > 0 && null != topicMember.get(0)) {
			return true;
		} else
			return false;

	}

	/**
	 * 
	 * @describe 获取讨论所有参与者 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月12日 上午8:58:41 <br>
	 * @param topicId
	 * @return
	 *
	 */
	public HashMap<String, Object> getTopicMember(Long topicId) {
		StringBuffer sql1 = new StringBuffer();
		StringBuffer sql2 = new StringBuffer();

		sql1.append("SELECT count(1) AS count FROM T_TOPIC_MEMBER ttc ");
		sql2.append(
				"SELECT ttm.userId,tu.userName,tu.account,tu.icon FROM T_TOPIC_MEMBER ttm LEFT JOIN T_USER tu on ttm.userId = tu.id");

		sql1.append(" WHERE ttc.topicId =" + topicId + " AND ttc.type=" + TOPIC_MEMBER_TYPE.ACTOR.ordinal());
		sql2.append(" WHERE ttm.topicId =" + topicId + " AND ttm.type=" + TOPIC_MEMBER_TYPE.ACTOR.ordinal());

		sql1.append(" AND ttc.del =" + DELTYPE.NORMAL.ordinal() + " ");
		sql2.append(" AND ttm.del =" + DELTYPE.NORMAL.ordinal() + " ");

		log.info("JdbcTemplate:" + sql1);
		log.info("JdbcTemplate:" + sql2);

		Map<String, Object> count = jdbcTpl.queryForMap(sql1.toString());

		List<Map<String, Object>> user = jdbcTpl.queryForList(sql2.toString());
		if (user.size() > 0) {
			for (Map<String, Object> map : user) {
				String userName = (String) (null != map.get("userName") && !"".equals(map.get("userName"))
						? map.get("userName") : map.get("account"));
				map.put("userName", userName);
				map.remove("account");
			}
		}

		HashMap<String, Object> job = new HashMap<String, Object>();
		job.put("total", count.get("count"));
		job.put("list", user);
		return job;
	}

	/**
	 * 
	 * @describe 删除讨论者 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午2:49:58 <br>
	 * @param topicId
	 * @param userId
	 * @return <br>
	 * @returnType int
	 *
	 */
	public int deleteMember(Long topicId, Long userId) {
		TopicMember member = this.topicMemberDao.findByTopicIdAndUserIdAndTypeAndDel(topicId, userId,
				TOPIC_MEMBER_TYPE.ACTOR, DELTYPE.NORMAL);
		if (null != member) {
			TopicAuth ta = this.topicAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL).get(0);
			ta.setDel(DELTYPE.DELETED);
			this.topicAuthDao.delete(ta);
			member.setDel(DELTYPE.DELETED);
			this.topicMemberDao.delete(member);
			return 1;
		}
		return 0;

	}

	/**
	 * 
	 * @describe 添加讨论者 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午2:50:14 <br>
	 * @param topicId
	 * @param userId
	 * @return <br>
	 * @throws Exception 
	 * @returnType int
	 *
	 */
	public TopicMember addMember(Long topicId, Long userId) throws Exception {
		TopicMember member = null;
		member = this.topicMemberDao.findByTopicIdAndUserIdAndDel(topicId, userId, DELTYPE.NORMAL);
		if(null==member){
			member = new TopicMember();
		}else{
			throw new Exception("该参与人已经存在");
		}
		
		member.setTopicId(topicId);
		member.setUserId(userId);
		member.setType(TOPIC_MEMBER_TYPE.ACTOR);
		member = this.topicMemberDao.save(member);
		
		TopicAuth ta = new TopicAuth();
		ta.setMemberId(member.getId());
		ta.setRoleId(Cache.getRole(ENTITY_TYPE.TOPIC+"_"+ROLE_TYPE.MEMBER).getId());
		ta = this.topicAuthDao.save(ta);
		return member;
	}

	/**
	 * 
	 * @describe permission 集合	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月21日 上午9:55:22	<br>
	 * @param loginUserId
	 * @param topicId
	 * @return
	 *
	 */
	@Override
	public List<Permission> getPermissionList(long loginUserId, long topicId) {
		List<Permission> listPermission = new ArrayList<Permission>();
		TopicMember tm = this.topicMemberDao.findByTopicIdAndUserIdAndDel(topicId, loginUserId, DELTYPE.NORMAL);
		if(null==tm){
			return listPermission;
		}
		List<TopicAuth> ta = this.topicAuthDao.findByMemberIdAndDel(tm.getId(),DELTYPE.NORMAL);
		if(null == ta || ta.size()==0){
			return listPermission;
		}
		Role role = Cache.getRole(ta.get(0).getRoleId());
		return role.getPermissions();
	}

	public Map<Long, List<String>> permissionMapAsMemberWith(String permissionEnName, Long loginUserId) {
		Map<Long, List<String>> permissionsMapAsMember = new HashMap<>();
		
		//获取讨论级别权限
		List<TopicMember> topicMembers = this.topicMemberDao.findByUserIdAndDel(loginUserId, DELTYPE.NORMAL);
		if(topicMembers != null && topicMembers.size() > 0) {
			// 遍历项目成员
			for(TopicMember pm : topicMembers) {
				List<TopicAuth> authList = this.topicAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);

				// 遍历角色
				HashMap<Long, String> permissionUnionMap = new HashMap<>();
				if(authList != null && authList.size() > 0) {
					for(TopicAuth auth : authList) {
						Role role = Cache.getRole(auth.getRoleId());
						List<Permission> permissions = (role == null) ? new ArrayList<Permission>() : role.getPermissions();
						for(Permission p : permissions) {
							permissionUnionMap.put(p.getId(), p.getEnName());
						}
					}
				}				
				
				// 生成权限并集
				List<String> permissionUnionArr = new ArrayList<>(permissionUnionMap.values());
				// 判定是否存在读取权限
				boolean hasRequiredPermission = false;
				for(String p : permissionUnionArr) {
					if( permissionEnName.equals( p ) ) {
						hasRequiredPermission = true;
						break;
					}
				}

				if(hasRequiredPermission) {
					permissionsMapAsMember.put(pm.getTopicId(), permissionUnionArr);
				}
				
				
			}
		}
		
		return permissionsMapAsMember;
	}
}
