package org.zywx.cooldev.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.zywx.appdo.common.utils.JsonUtil;
import org.zywx.cooldev.entity.auth.Action;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.builder.Setting;
import org.zywx.cooldev.entity.project.ProjectCategory;
//import org.zywx.cooldev.util.emm.RedisUtilInitSetting;

/**
 * 
 * 系统资源缓存<br>
 * 1. Action 缓存
 * 2. Permission 缓存
 * 3. Role 缓存
 * 4. ProjectCategory 缓存
 * 
 * @author yang.li
 * @date 2015-08-20
 */
public class Cache {

	/**
	 * 服务接口缓存<br>
	 * 只提供加载函数
	 */
	private static final HashMap<String, Action> ACTION_MAP = new HashMap<>();
	
	/**
	 * 角色缓存(id,entity)
	 */
	private static final HashMap<Long, Role> ROLE_MAP = new HashMap<>();
	
	/**
	 * 角色缓存(enName,entity)
	 */
	private static final HashMap<String, Role> ROLE_NAME_MAP = new HashMap<>();
	
	/**
	 * 项目分类缓存
	 */
	private static final HashMap<Long, ProjectCategory> PROJECT_CATEGORY_MAP = new HashMap<>();
	
	private static final HashMap<String, Object> OBJECT_MAP = new HashMap<>(); 
	private static final HashMap<String, String> END_TIME_MAP = new HashMap<>(); 

	public static void addAction(Action a) {
		ACTION_MAP.put(a.getMethod() + a.getPattern(), a);
	}
	public static Action getAction(String urlPattern) {
		return ACTION_MAP.get(urlPattern);
	}
	
	public static void addEndTime(String a) {
		END_TIME_MAP.put("endTime", a);
	}
	public static String getEndTimeMap(String key) {
		return END_TIME_MAP.get(key);
	}


	private static StringRedisTemplate  redisTemplate = (StringRedisTemplate) InitBean.applicationContext.getBean("redisTemplate");
	
	public static void addRole(Role r)  {
		try {
			redisTemplate.opsForValue().set("roleId"+r.getId(), JsonUtil.obj2Json(r));
			redisTemplate.opsForValue().set("roleEnName"+r.getEnName(), JsonUtil.obj2Json(r));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//ROLE_MAP.put(r.getId(), r);
		//ROLE_NAME_MAP.put(r.getEnName(), r);
	}
	public static Role getRole(long roleId)  {
		//return ROLE_MAP.get(roleId);
		try {
			return  JsonUtil.json2Obj(redisTemplate.opsForValue().get("roleId"+roleId),Role.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static Role getRole(String roleEnName) {
		try {
			return  JsonUtil.json2Obj(redisTemplate.opsForValue().get("roleEnName"+roleEnName),Role.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void removeRole(long roleId) {
		Role r = ROLE_MAP.get(roleId);
		String roleName = r.getEnName();
		ROLE_NAME_MAP.remove(roleName);
		ROLE_MAP.remove(roleId);
	}
	
	public static void addObject(Object object){
		OBJECT_MAP.put("SETTING", object);
	}
	
	public static Object getObject(Object object){
		return OBJECT_MAP.get("SETTING");
	}

	public static void addProjectCategory(ProjectCategory pc) {
		PROJECT_CATEGORY_MAP.put(pc.getId(), pc);
	}
	public static void removeProjectCategory(long categoryId) {
		PROJECT_CATEGORY_MAP.remove(categoryId);
	}
	public static ProjectCategory getProjectCategory(long categoryId) {
		return PROJECT_CATEGORY_MAP.get(categoryId);
	}
	public static List<ProjectCategory> getProjectCategoryList() {
		Iterator<Entry<Long, ProjectCategory>>   it = PROJECT_CATEGORY_MAP.entrySet().iterator();
		ArrayList<ProjectCategory> list = new ArrayList<>();
		while(it.hasNext()) {
			Entry<Long, ProjectCategory> e = it.next();
			list.add(e.getValue());
		}
		return list;
	}
	
	//--Clear Cache
	public static void clearCache(){
		ACTION_MAP.clear();
		ROLE_MAP.clear();
		ROLE_NAME_MAP.clear();
		PROJECT_CATEGORY_MAP.clear();
	}

}
