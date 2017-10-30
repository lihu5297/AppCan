package org.zywx.cooldev.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_READ_TYPE;
import org.zywx.cooldev.dao.notice.NoticeDao;
import org.zywx.cooldev.dao.notice.NoticeModuleDao;
import org.zywx.cooldev.entity.notice.Notice;
import org.zywx.cooldev.entity.notice.NoticeDependency;
import org.zywx.cooldev.entity.notice.NoticeModule;

@Service
public class NoticeService extends BaseService {

	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private NoticeModuleDao noticeModuleDao;

	/**
	 * 
	 * @describe 添加通知 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月19日 上午9:04:41 <br>
	 * @param userId
	 *            通知启动者 Long
	 * @param recievedIds
	 *            通知接收者 该参数是数组结构 Long[]{}
	 * @param noModuleType
	 *            通知模板类型 根据模板获取通知的 详细信息 String
	 * @param placeHolder
	 *            <br>
	 *            通知的参数设置 具体到某人、项目、团队、讨论等的对象或者实体 Object[]{}
	 * @returnType void
	 *
	 */
	public void addNotice(Long userId, Long[] recievedIds, NOTICE_MODULE_TYPE noModuleType, Object[] placeHolder) {
		try{
			NoticeModule module = this.noticeModuleDao.findByNoModuleType(noModuleType);
			String noInfo = String.format(module.getNoFormatStr(), placeHolder);
			for (Long recievedId : recievedIds) {
				if(recievedId==null){
					continue;
				}
				Notice notice = new Notice();
				notice.setUserId(userId);
				notice.setNoModuleType(noModuleType);
				notice.setNoInfo(noInfo);
				notice.setRecievedId(recievedId);
				this.noticeDao.save(notice);
			
			
				NoticeDependency noticeDependency = new NoticeDependency();
				
				Object entity[] =new Object[placeHolder.length-1];//在placeHolder对象中减去头一个,因为模板的第一个%s占位符都需要当前操作人
				
				for(int i=0;i<entity.length;i++){
					entity[i] = placeHolder[i+1];
				}
				for(Object object : entity){
					if(object instanceof String || object instanceof Integer || object instanceof Long || object==null){
						continue;
					}
					String className = object.getClass().getName();
					className = className.substring(className.lastIndexOf(".")+1);
					//entityType === object's class name 
					noticeDependency.setEntityType(className);
					Method method = null;
					Method[] ms = object.getClass().getSuperclass().getDeclaredMethods();
					for (int i = 0; i < ms.length; i++) {
						if(ms[i].getName().equals("getId")){
							method = ms[i];break;
						}
					}
					Object entityIdObj = method.invoke(object);
					Long entityId = Long.parseLong(entityIdObj.toString());
					noticeDependency.setEntityId(entityId);
					
					noticeDependency.setNoticeId(notice.getId());
					this.noticeDependencyDao.save(noticeDependency);
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("发送通知失败，错误信息:"+e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 获取通知列表 <br>
	 * @author jiexiong.liu <br>
	 * @param loginUserId
	 * @date 2015年8月19日 上午9:06:35 <br>
	 * @param typeQ
	 * @param pageSize
	 * @param pageNo
	 * @return <br>
	 * @returnType HashMap<Object,Object>
	 *
	 */
	public HashMap<String, Object> getNoticeList(Long loginUserId, StringBuffer typeQ, int pageNo, int pageSize) {
		String sql = "SELECT TN.id,DATE_FORMAT(TN.createdAt,'%Y-%m-%d %H:%i:%S') createdAt,DATE_FORMAT(TN.updatedAt,'%Y-%m-%d %H:%i:%S') updatedAt,TN.noInfo noInfo,"
				+ " CASE WHEN TN.noRead = 0 THEN 'UNREAD' ELSE 'READ' END noRead,TN.recievedId recievedId,TN.userId userId,TND.entityId entityId,TND.entityType entityType"
				+ " FROM T_NOTICE TN LEFT JOIN T_NOTICE_DEPENDENCY TND ON TN.id = TND.noticeId WHERE TN.noRead IN (" + typeQ
				+ ") AND TN.recievedId = ? and TN.del = ? ORDER BY TN.createdAt DESC LIMIT ?,?";
		String sql1 = "SELECT count(*) count FROM T_NOTICE TN WHERE TN.noRead IN (" + typeQ
				+ ") AND TN.recievedId = ? and TN.del = ? ";
		String sql2 = "SELECT count(*) count FROM T_NOTICE TN WHERE TN.noRead =" + NOTICE_READ_TYPE.UNREAD.ordinal()
				+ " AND TN.recievedId = ? and TN.del = ? ";
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql,
				new Object[] { loginUserId,DELTYPE.NORMAL.ordinal(), (pageNo - 1) * pageSize, pageSize });
		Map<String, Object> count = this.jdbcTpl.queryForMap(sql1, new Object[] { loginUserId,DELTYPE.NORMAL.ordinal() });
		Map<String, Object> unread = this.jdbcTpl.queryForMap(sql2, new Object[] { loginUserId,DELTYPE.NORMAL.ordinal() });
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("total", count.get("count"));
		map.put("unreadCount", unread.get("count"));
		map.put("list", list);
		return map;
	}

	/**
	 * 
	 * @describe 设置已读通知 <br>
	 * @author jiexiong.liu <br>
	 * @param loginUserId
	 * @date 2015年8月19日 上午10:06:55 <br>
	 * @param noId
	 * @return <br>
	 * @returnType int
	 *
	 */
	public int updateReadNotice(Long loginUserId, String noId) {
		log.info("noId-->"+noId+",loginUserId-->"+loginUserId);
		//注释掉的这种方法,比如传进了noId为111,222,333只会修改111的不知道为啥
		String sql = "UPDATE T_NOTICE TN SET TN.noRead = "+Enums.NOTICE_READ_TYPE.READ.ordinal()+" WHERE TN.id in ("+noId+") and TN.recievedId = "+loginUserId;
		int a = this.jdbcTpl.update(sql);
		if (a == 1) {
			return 1;
		}
		return noId.split(",").length;
	}

	/**
	 * 
	 * @describe 删除通知 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 上午9:57:01 <br>
	 * @param loginUserId
	 * @param noId
	 * @return <br>
	 * @returnType int
	 *
	 */
	public int deleteNotice(Long loginUserId, List<Long> noId) {
		String noIds = noId.toString();
		noIds = noIds.substring(1,noIds.length()-1);
		String sql = "UPDATE T_NOTICE TN SET TN.del = ? WHERE TN.id in ("+noIds+") and TN.recievedId = ?";
		int a = this.jdbcTpl.update(sql, new Object[] { Enums.DELTYPE.DELETED.ordinal(),loginUserId });
		return a;
	}
	
}
