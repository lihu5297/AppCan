package org.zywx.cooldev.service;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_TYPE;
import org.zywx.cooldev.entity.Dynamic;
import org.zywx.cooldev.entity.DynamicDependency;
import org.zywx.cooldev.entity.DynamicModule;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.vo.DynamicVO;

@Service
public class DynamicService extends BaseService{
	
	
	/**
     * @Description: 增加动态
     * @param @param userId	当前用户
     * @param @param moduleType  模板类型(枚举值)
     * @param @param teamId  相关团队的ID
     * @param @param objects 动态依赖的实体对象数组,并且里面的顺序不能错,会分别调用每个实体的toString()方法,来根据模板生成最终的动态信息
     * @return void    返回类型
	 * @user jingjian.wu
	 * @date 2015年8月19日 下午7:45:29
     * @throws
	 */
	public void addTeamDynamic(Long userId,DYNAMIC_MODULE_TYPE moduleType,long teamId,Object... objects){
		Dynamic dynamic = new Dynamic();
		dynamic.setUserId(userId);
		dynamic.setModuleType(moduleType);
		dynamic.setType(DYNAMIC_TYPE.TEAM);
		dynamic.setRelationId(teamId);
		this.addDynamic(dynamic,userId, moduleType, teamId, objects);
	}
	
	/**
     * @Description: 增加动态
     * @param userId	当前用户
     * @param moduleType  模板类型(枚举值)
     * @param projectId  相关项目的ID
     * @param objects 动态依赖的实体对象数组,并且里面的顺序不能错,会分别调用每个实体的toString()方法,来根据模板生成最终的动态信息
     * @return void    返回类型
	 * @user jingjian.wu
	 * @date 2015年8月19日 下午7:45:29
     * @throws
	 */
	public void addPrjDynamic(Long userId,DYNAMIC_MODULE_TYPE moduleType,long projectId,Object...  objects){
		log.info("add dynamic --> moduleType:"+moduleType+",userId:"+userId+",projectId:"+projectId);
		log.info(objects);
		Dynamic dynamic = new Dynamic();
		dynamic.setUserId(userId);
		dynamic.setModuleType(moduleType);
		dynamic.setType(DYNAMIC_TYPE.PROJECT);
		dynamic.setRelationId(projectId);
		this.addDynamic(dynamic,userId, moduleType, projectId, objects);
	}
	/**
	 * 
	 * @describe  添加动态	<br>
	 * @author jingjian.wu	<br>
	 * @date 2015年8月19日 下午5:17:54	<br>
	 * @param userId	用户id
	 * @param moduleType	模板类型
	 * @param type	动态类型  PROJECT、TEAM
	 * @param relationId	如果动态属于某个项目,则相关ID为项目ID,否则项目应该是属于某个团队,则应该为团队ID
	 * @param objects	动态依赖对象,需要存储到DynamicDependency表中[参与此次动态的对象  object数组（该数组包括参与对象 并且包含对象id，可以序列化）]
	 * 					根据模板类型获取到对应的模板之后,objects中每个对象的toString()方法用于替换模板中的占位符变量参数 
	 * @returnType void
	 *
	 */
	private void addDynamic(Dynamic dynamic,Long userId,DYNAMIC_MODULE_TYPE moduleType,long relationId,Object... objects){
		
		try {
			DynamicModule module = this.dynamicModuleDao.findByModuleType(moduleType);

			User u =this.userDao.findOne(userId);
			String userName = null==u.getUserName()?u.getAccount():u.getUserName();
			
			Object formatParam[] =new Object[objects.length+1];//在objects对象的头一个加上当前用户,因为模板的第一个%s占位符都需要当前操作人
			formatParam[0] = userName;
			for(int i=0;i<objects.length;i++){
				formatParam[i+1] = objects[i];
				log.info("object :"+i+"->"+objects[i]);
			}
			
			log.info("record dynamic: module:"+module.getFormatStr());
			//根据传进来的参数,格式化日志
			String info = String.format(module.getFormatStr(), formatParam);
			dynamic.setInfo(info);
			dynamic = this.dynamicDao.save(dynamic);
			
			DynamicDependency dynamicDependency = new DynamicDependency();
			for(Object object : objects){
				if(null== object || object instanceof String || object instanceof Integer || object instanceof Long){
					continue;
				}
				dynamicDependency = new DynamicDependency();
				String className = object.getClass().getName();
				className = className.substring(className.lastIndexOf(".")+1);
				//entityType === object's class name 
				dynamicDependency.setEntityType(className);
				Method method = null;
				Method[] ms = object.getClass().getSuperclass().getDeclaredMethods();
				for (int i = 0; i < ms.length; i++) {
					if(ms[i].getName().equals("getId")){
						method = ms[i];break;
					}
				}
				Object entityIdObj = method.invoke(object);
				Long entityId = Long.parseLong(entityIdObj.toString());
				dynamicDependency.setEntityId(entityId);
				
				dynamicDependency.setDynamicId(dynamic.getId());
				this.dynamicDependencyDao.save(dynamicDependency);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @throws ParseException 
	    * @Description: 查询date前 interval天的
	    * @param @param userId  当前用户
	    * @param @param date	哪一天 2015-08-01  
	    * @param @param interval 查询多少天之内的
	    * @return void    返回类型
		* @user jingjian.wu
		* @date 2015年8月18日 下午6:33:41
	    * @throws
	 */
	public Map<String, Object> getDynamicList(long userId,String date,Long interval,Long projectId) throws ParseException{
		//因为要按照时间倒叙排列,而且必须用map结构的,所以此处选择linkhashMap
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		String projectIdSQL = "";
		if(null!=projectId){
			projectIdSQL = " and d.type = 0 and d.relationId = ?";
		}else{
			projectIdSQL =" and d.relationId in( " +
				" select projectId from T_PROJECT_MEMBER where  userId = ? " +
				" union " +
				" select teamId from T_TEAM_MEMBER where userId=? " +
				" ) ";
		}
		String sql = "select d.info,d.createdAt ,date(d.createdAt) as riqi,time(d.createdAt) as time ,u.icon,u.userName,u.account from T_DYNAMIC d ,T_USER u where  1=1 "+projectIdSQL+

				" and  d.userId = u.id   "  +
				" and date(d.createdAt) <=? " +

				" and  date(d.createdAt) >date_sub(?,interval ? day) " +

				" order by d.createdAt desc ";
		
		final List<DynamicVO> listvo = new ArrayList<DynamicVO>();
		Object []obj = null;
		if(null!=projectId){
			obj = new Object[]{projectId,date,date,interval};
		}else{
			obj = new Object[]{userId,userId,date,date,interval};
		}
		this.jdbcTpl.query(sql, obj,
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						DynamicVO vo  = new DynamicVO();
						vo.setCreatedAt(rs.getString("createdAt"));
						vo.setDate(rs.getString("riqi"));
						vo.setIcon(rs.getString("icon"));
						vo.setInfo(rs.getString("info"));
						vo.setTime(rs.getString("time"));
						vo.setUserName(rs.getString("userName"));
						vo.setAccount(rs.getString("account"));
						listvo.add(vo);
					}
				});
		List<DynamicVO> listtmp = new ArrayList<DynamicVO>();
		String dateStr = "";//倒叙排列的最大一天
		if(listvo.size()>0){
			dateStr = listvo.get(0).getDate();
		}
		for(DynamicVO v:listvo){//循环遍历每个对象,将同一天的对象,封装到一起,放入map当中
			if(!dateStr.equals(v.getDate())){
				map.put(dateStr, listtmp);
				listtmp = new ArrayList<DynamicVO>();
				dateStr = v.getDate();
				listtmp.add(v);
			}else{
				listtmp.add(v);
			}
		}
		if(listvo.size()>0){//上面for循环,最后一个日期的集合肯定放不进去,在这里补进去
			map.put(listvo.get(listvo.size()-1).getDate(), listtmp);
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			cal.setTime(sdf.parse(listvo.get(listvo.size()-1).getDate()));
			cal.add(Calendar.DATE, -1);
			map.put("lastDay",sdf.format(cal.getTime()) );//最后一天的时间,下次请求动态,再带回来(参数date)
		}
		return map;

	}
	
	/**
	   *@Description: 查询用户的动态记录,是否有日期小于等于date的
	   *@param @param userId
	   *@param @param date
	   *@param @return 
	   *@return boolean    返回类型
	   *@user jingjian.wu
	   *@date 2015年8月18日 下午8:39:28
	   *@throws
	 */
	public boolean existDataLessThan(long userId,String date,Long projectId){
		String projectIdSQL = "";
		if(null!=projectId){
			projectIdSQL = " and d.type = 0 and d.relationId = ?";
		}else{
			projectIdSQL =" and d.relationId in( " +
				" select projectId from T_PROJECT_MEMBER where  userId = ? " +
				" union " +
				" select teamId from T_TEAM_MEMBER where userId=? " +
				" ) ";
		}
		String sql = "select count(1) from T_DYNAMIC d ,T_USER u where  1=1 " +projectIdSQL+

				" and  d.userId = u.id   "  +
				" and date(d.createdAt) <=? " +

				" order by d.createdAt desc ";
		Object []obj = null;
		if(null!=projectId){
			obj = new Object[]{projectId,date};
		}else{
			obj = new Object[]{userId,userId,date};
		}
		int num = this.jdbcTpl.queryForInt(sql, obj);
		if(num>0){
			return true;
		}
		return false;

	}
	
	
	
	/**
	    * @Description:获取任务的相关动态 
	    * @param @param taskId
	    * @param @return 
	    * @return List<DynamicVO>    返回类型
		* @user jingjian.wu
		* @date 2015年8月19日 上午10:22:55
	    * @throws
	 */
	public Map<String,Object> getTaskDynamicList(Long taskId,Pageable pageable){
		int startNum = pageable.getPageNumber()*pageable.getPageSize();
		int number = pageable.getPageSize();
		
		String sql = "select d.info,DATE_FORMAT(d.createdAt,'%Y-%m-%d %H:%i:%s') createdAt ,DATE_FORMAT(d.createdAt,'%Y-%m-%d %H:%i:%s') as riqi,time(d.createdAt) as time ,u.icon,u.userName,u.account,m.moduleIcon from T_DYNAMIC d ,T_USER u,T_DYNAMIC_MODULE m where  d.id in( " +
				" select dynamicId from( "+
				" select dynamicId from T_DYNAMIC_DEPENDENCY where  entityType = 'Task' and entityId = ? " +
				" union " +
				" select dynamicId from T_DYNAMIC_DEPENDENCY where  entityType = 'TaskLeaf' and entityId in(  " +
				" select  id from T_TASK_LEAF where topTaskId = ?" +
				" 	) " +
				" ) as t"+
				" ) " +
				
				" and d.userId = u.id "  +
				
				" and d.moduleType = m.moduleType " +
				
				" order by d.createdAt desc limit " + startNum + "," + number;
				
		String sqlCount = "select count(1) from ( select d.info,DATE_FORMAT(d.createdAt,'%Y-%m-%d %H:%i:%s') createdAt ,DATE_FORMAT(d.createdAt,'%Y-%m-%d %H:%i:%s') as riqi,time(d.createdAt) as time ,u.icon,u.userName,u.account,m.moduleIcon from T_DYNAMIC d ,T_USER u,T_DYNAMIC_MODULE m where  d.id in( " +
				"  select dynamicId from( "+
				" select dynamicId from T_DYNAMIC_DEPENDENCY where  entityType = 'Task' and entityId =" +taskId+
				" union " +
				" select dynamicId from T_DYNAMIC_DEPENDENCY where  entityType = 'TaskLeaf' and entityId in(  " +
				" select  id from T_TASK_LEAF where topTaskId = " +taskId +
				" 	) " +
				" ) as t"+
				" ) " +
				
				" and  d.userId = u.id   "  +
				
				" and d.moduleType = m.moduleType " +
				
				" order by d.createdAt desc ) tt";
				
		final List<DynamicVO> listvo = new ArrayList<DynamicVO>();
		this.jdbcTpl.query(sql, new Object[]{taskId,taskId},
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						DynamicVO vo  = new DynamicVO();
						vo.setCreatedAt(rs.getString("createdAt"));
						vo.setDate(rs.getString("riqi"));
						vo.setIcon(rs.getString("icon"));
						vo.setInfo(rs.getString("info"));
						vo.setTime(rs.getString("time"));
						vo.setUserName(rs.getString("userName"));
						vo.setAccount(rs.getString("account"));
						vo.setModuleIcon(rs.getString("moduleIcon"));
						listvo.add(vo);
					}
				});
		
		long count = this.jdbcTpl.queryForLong(sqlCount);
		
		HashMap<String,Object> map = new HashMap<String,Object>();
		map.put("list", listvo);
		map.put("total", count);
		return map;
				
	}

	
	public boolean existDataLessThan(long userId, Long projectId) {
		String projectIdSQL = "";
		if(null!=projectId){
			projectIdSQL = " and d.type = 0 and d.relationId = ?";
		}else{
			projectIdSQL =" and d.relationId in( " +
				" select projectId from T_PROJECT_MEMBER where  userId = ? " +
				" union " +
				" select teamId from T_TEAM_MEMBER where userId=? " +
				" ) ";
		}
		String sql = "select count(1) from T_DYNAMIC d ,T_USER u where  1=1 " +projectIdSQL+
				" and  d.userId = u.id   "  +
				" order by d.createdAt desc ";
		Object []obj = null;
		if(null!=projectId){
			obj = new Object[]{projectId};
		}else{
			obj = new Object[]{userId,userId};
		}
		int num = this.jdbcTpl.queryForInt(sql, obj);
		if(num>0){
			return true;
		}
		return false;
	}

	public Map<String, Object> getDynamicList(long userId, Long projectId, Pageable pageable) throws ParseException {
		
		int startNum = pageable.getPageNumber() * pageable.getPageSize();
		int number = pageable.getPageSize();
		
		//因为要按照时间倒叙排列,而且必须用map结构的,所以此处选择linkhashMap
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		String projectIdSQL = "";
		if(null!=projectId){
			projectIdSQL = " and d.type = 0 and d.relationId = ?";
		}else{
			projectIdSQL =" and d.relationId in( " +
				" select projectId from T_PROJECT_MEMBER where  userId = ? " +
				" union " +
				" select teamId from T_TEAM_MEMBER where userId=? " +
				" ) ";
		}
		String sql = "select d.info,d.createdAt ,date(d.createdAt) as riqi,time(d.createdAt) as time ,u.icon,u.userName,u.account from T_DYNAMIC d ,T_USER u where  1=1 "+projectIdSQL+
				" and  d.userId = u.id   "  +
				" order by d.createdAt desc limit ?,?";
		String sqlCount = "select count(*) from (select d.info,d.createdAt ,date(d.createdAt) as riqi,time(d.createdAt) as time ,u.icon,u.userName,u.account from T_DYNAMIC d ,T_USER u where  1=1 "+projectIdSQL+
				" and  d.userId = u.id   "  +
				" order by d.createdAt desc ) tt ";
		
		final List<DynamicVO> listvo = new ArrayList<DynamicVO>();
		Object []obj = null;
		Object []objCount = null;
		if(null!=projectId){
			obj = new Object[]{projectId,startNum,number};
			objCount = new Object[]{projectId};
		}else{
			obj = new Object[]{userId,userId,startNum,number};
			objCount = new Object[]{userId,userId};
		}
		
		long total = this.jdbcTpl.queryForLong(sqlCount, objCount);
		
		this.jdbcTpl.query(sql, obj,
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						DynamicVO vo  = new DynamicVO();
						vo.setCreatedAt(rs.getString("createdAt"));
						vo.setDate(rs.getString("riqi"));
						vo.setIcon(rs.getString("icon"));
						vo.setInfo(rs.getString("info"));
						vo.setTime(rs.getString("time"));
						vo.setUserName(rs.getString("userName"));
						vo.setAccount(rs.getString("account"));
						listvo.add(vo);
					}
				});
		List<DynamicVO> listtmp = new ArrayList<DynamicVO>();
		String dateStr = "";//倒叙排列的最大一天
		if(listvo.size()>0){
			dateStr = listvo.get(0).getDate();
		}
		for(DynamicVO v:listvo){//循环遍历每个对象,将同一天的对象,封装到一起,放入map当中
			if(!dateStr.equals(v.getDate())){
				map.put(dateStr, listtmp);
				listtmp = new ArrayList<DynamicVO>();
				dateStr = v.getDate();
				listtmp.add(v);
			}else{
				listtmp.add(v);
			}
		}
		if(listvo.size()>0){//上面for循环,最后一个日期的集合肯定放不进去,在这里补进去
			map.put(listvo.get(listvo.size()-1).getDate(), listtmp);
		}
		map.put("total", total);
		return map;
	}
}
