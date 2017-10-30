package org.zywx.coopman.system;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.entity.DailyLog.LogAction;
import org.zywx.coopman.entity.module.Module;

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
	private static final HashMap<String, LogAction> ACTION_MAP = new HashMap<>();
	
	/**
	 * 模块缓存
	 */
	private static final HashMap<String, Module> MODULE_MAP = new HashMap<>();
	
	/**
	 * 平台设置缓存<br>
	 */
	private static final HashMap<String, Setting> SETTING_MAP = new HashMap<>();

	public static void addAction(LogAction a) {
		ACTION_MAP.put(a.getPattern(), a);
	}
	
	public static LogAction getAction(String urlPattern) {
		return ACTION_MAP.get(urlPattern);
	}
	
	public static void addSetting(String key, Setting value){
		SETTING_MAP.put(key, value);
	}
	
	public static Setting getSetting(String key) {
		return SETTING_MAP.get(key);
	}

	public static void clearCache(){
		ACTION_MAP.clear();
	}

	
	
	/**
	 * @describe 智能获取LogAction	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月17日 下午2:35:22	<br>
	 * @param urlPattern
	 * @return  <br>
	 * @returnType LogAction
	 *
	 */
	public static LogAction getAllMostAction(String urlPattern) {
		Set<String> keys = ACTION_MAP.keySet();
		Map<String,Object> map = new HashMap<>();
		String keyStr = "";
		for(String key : keys){
			
			double score = 0;
			//相等 40
			if(key.equals(urlPattern)){
				keyStr = key;
				score+=40;
			}else if(urlPattern.contains(key)){
				//被包含 30
				keyStr = key;
				score+=30;
			}else if(key.contains(urlPattern)){
				//包含 20
				keyStr = key;
				score+=20;
			}else if(key.contains("?") && urlPattern.contains("?")){
				//部分匹配 10
				String keystr[] = key.split("\\?");
				if(keystr.length<2){
					continue;
				}
				String urlstr[] = urlPattern.split("\\?");
				if(urlstr.length<2){
					continue;
				}
				
				if(keystr[0].equals(urlstr[0])){
					if(keystr[1].equals(urlstr[1])){
						//相等 10
						keyStr = key;
						score+=10;
					}else if(urlstr[1].contains(keystr[1])){
						//被包含 5
						keyStr = key;
						score+=5;
					}else if(keystr[1].contains(urlstr[1])){
						//包含 2
						keyStr = key;
						score+=2;
					}else if(keystr[1].contains("&") && urlstr[1].contains("&")){
						//query部分匹配
						String keyQStr[] = keystr[1].split("\\&");
						if(keyQStr.length<2){
							continue;
						}
						String queryStr[] = urlstr[1].split("\\&");
						if(keyQStr.length<2){
							continue;
						}
						for(String str : keyQStr){
							for(String string : queryStr){
								if(str.equals(string)){
									score += 0.1;
								}
							}
						}
					}
				}
			}
			
			map.put(key, score);
		}
		
		Set<String> scores = map.keySet();
		double maxScore = 0;
		//获取最大分数
		for(String abc : scores){
			if((double)map.get(abc) > maxScore){
				maxScore = (double)map.get(abc);
				keyStr = abc;
			}
		}
		
		LogAction logAction= ACTION_MAP.get(keyStr);
		
		return logAction;
	}
	
	
	public static void addModule(Module module){
		MODULE_MAP.put(module.getUrl(), module);
	}
	
	public static Module getModule(String urlPattern){
		return MODULE_MAP.get(urlPattern);
		
	}
}
