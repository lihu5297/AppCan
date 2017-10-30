	/**  
     * @author jingjian.wu
     * @date 2015年11月6日 下午4:43:29
     */
    
package org.zywx.cooldev.util.emm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.zywx.appdo.common.utils.JsonUtil;

import redis.clients.jedis.JedisCluster;


    /**
 * @author jingjian.wu
 * @date 2015年11月6日 下午4:43:29
 */

public class RedisUtilProduct {

private static Logger logger = Logger.getLogger(RedisUtilProduct.class);
	
    private static JedisCluster jedisCluster;  
    
    
    public static JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public static void setJedisCluster(JedisCluster jedisCluster) {
		RedisUtilProduct.jedisCluster = jedisCluster;
	}

	public static Boolean exists(String key) {
        return jedisCluster.exists(key);
	}

	public static boolean set(String key, String value, int second) {  
        try {  
            jedisCluster.setex(key, second, value);  
            return true;  
        } catch (Exception ex) {  
            logger.error("set error.", ex);  
            return false;
        } 
    }  
  
	public static boolean set(String key, String value) {
		try {
			jedisCluster.set(key, value);
			return true;
		} catch (Exception ex) {
			logger.error("set error.", ex);
		} 
		return false;
	}
  
    public static String get(String key) {  
    	try {  
    		return jedisCluster.get(key) == null?null:jedisCluster.get(key);  
    	} catch (Exception ex) {  
    		logger.error("get error.", ex);  
    	} 
    	return null;  
    }  
    
    public static <T> T get(String key,Class<T> clazz) throws Exception {  
    	String json = RedisUtilProduct.get(key);
		return JsonUtil.json2Obj(json, clazz);
    }
    
    public static String get(String key, String defaultValue) {  
        try {  
            return jedisCluster.get(key) == null?defaultValue:jedisCluster.get(key);  
        } catch (Exception ex) {  
            logger.error("get error.", ex);  
        } 
        return defaultValue;  
    }  
  
    public static boolean del(String key) {  
        try {  
        	jedisCluster.del(key);  
            return true;  
        } catch (Exception ex) {  
            logger.error("del error.", ex);  
        } 
        return false;  
    }  
  
    public long incr(String key) {  
        try {  
            return jedisCluster.incr(key);  
        } catch (Exception ex) {  
            logger.error("incr error.", ex);  
        } 
        return 0;  
    }  
  
    public long decr(String key) {  
        try {  
            return jedisCluster.decr(key);  
        } catch (Exception ex) {  
            logger.error("incr error.", ex);  
        } 
        return 0;  
    }  
  
    /** 
     * 设置一个key的过期时间（单位：秒） 
     * @param key key值 
     * @param seconds 多少秒后过期 
     * @return 1：设置了过期时间  0：没有设置过期时间/不能设置过期时间 
     */  
    public static long expire(String key, int seconds) {  
        if (key==null || key.equals("")) {  
            return 0;  
        }  
        try {  
            return jedisCluster.expire(key, seconds);  
        } catch (Exception ex) {  
            logger.error("EXPIRE error[key=" + key + " seconds=" + seconds + "]" + ex.getMessage(), ex);  
        } 
        return 0;  
    }  
  
    /** 
     * 设置一个key在某个时间点过期 
     * @param key key值 
     * @param unixTimestamp unix时间戳，从1970-01-01 00:00:00开始到现在的秒数 
     * @return 1：设置了过期时间  0：没有设置过期时间/不能设置过期时间 
     */  
    public long expireAt(String key, int unixTimestamp) {  
        if (key==null || key.equals("")) {  
            return 0;  
        }  
        try {  
            return jedisCluster.expireAt(key, unixTimestamp);  
        } catch (Exception ex) {  
            logger.error("EXPIRE error[key=" + key + " unixTimestamp=" + unixTimestamp + "]" + ex.getMessage(), ex);  
        } 
        return 0;  
    }  
  
    /** 
     * 截断一个List 
     * @param key 列表key 
     * @param start 开始位置 从0开始 
     * @param end 结束位置 
     * @return 状态码 
     */  
    public String trimList(String key, long start, long end) {  
        if (key == null || key.equals("")) {  
            return "-";  
        }  
        try {  
            return jedisCluster.ltrim(key, start, end);  
        } catch (Exception ex) {  
            logger.error("LTRIM 出错[key=" + key + " start=" + start + " end=" + end + "]" + ex.getMessage() , ex);  
        } 
        return "-";  
    }  
    /** 
     * 检查Set长度 
     * @param key 
     * @return 
     */  
    public long countSet(String key){  
        if(key == null ){  
            return 0;  
        }  
        try {  
            return jedisCluster.scard(key);  
        } catch (Exception ex) {  
            logger.error("countSet error.", ex);  
        } 
        return 0;  
    }  
    /** 
     * 添加到Set中（同时设置过期时间） 
     * @param key key值 
     * @param seconds 过期时间 单位s 
     * @param value 
     * @return 
     */  
    public boolean addSet(String key,int seconds, String... value) {  
        boolean result = addSet(key, value);  
        if(result){  
            long i = expire(key, seconds);  
            return i==1;  
        }  
        return false;  
    }  
    /** 
     * 添加到Set中 
     * @param key 
     * @param value 
     * @return 
     */  
    public boolean addSet(String key, String... value) {  
        if(key == null || value == null){  
            return false;  
        }  
        try {  
            jedisCluster.sadd(key, value);  
            return true;  
        } catch (Exception ex) {  
            logger.error("setList error.", ex);  
        } finally {  
            
        }  
        return false;  
    }  
  
      
    /** 
     * @param key 
     * @param value 
     * @return 判断值是否包含在set中 
     */  
    public boolean containsInSet(String key, String value) {  
        if(key == null || value == null){  
            return false;  
        }  
        try {  
            return jedisCluster.sismember(key, value);  
        } catch (Exception ex) {  
            logger.error("setList error.", ex);  
        } finally {  
            
        }  
        return false;  
    }  
    /** 
     * 获取Set 
     * @param key 
     * @return 
     */  
    public  Set<String> getSet(String key){  
        try {  
            return jedisCluster.smembers(key);  
        } catch (Exception ex) {  
            logger.error("getList error.", ex);  
        } finally {  
            
        }  
        return null;  
    }  
  
    /** 
     * 从set中删除value 
     * @param key 
     * @return 
     */  
    public  boolean removeSetValue(String key,String... value){  
        try {  
            jedisCluster.srem(key, value);  
            return true;  
        } catch (Exception ex) {  
            logger.error("getList error.", ex);  
        } finally {  
            
        }  
        return false;  
    }  
      
      
      
    /** 
     * 从list中删除value 默认count 1 
     * @param key 
     * @param values 值list 
     * @return 
     */  
    public  int removeListValue(String key,List<String> values){  
        return removeListValue(key, 1, values);  
    }  
    /** 
     * 从list中删除value 
     * @param key 
     * @param count  
     * @param values 值list 
     * @return 
     */  
    public  int removeListValue(String key,long count,List<String> values){  
        int result = 0;  
        if(values != null && values.size()>0){  
            for(String value : values){  
                if(removeListValue(key, count, value)){  
                    result++;  
                }  
            }  
        }  
        return result;  
    }  
    /** 
     *  从list中删除value 
     * @param key 
     * @param count 要删除个数 
     * @param value 
     * @return 
     */  
    public  boolean removeListValue(String key,long count,String value){  
        try {  
            jedisCluster.lrem(key, count, value);  
            return true;  
        } catch (Exception ex) {  
            logger.error("getList error.", ex);  
        } finally {  
            
        }  
        return false;  
    }  
      
    /** 
     * 截取List 
     * @param key  
     * @param start 起始位置 
     * @param end 结束位置 
     * @return 
     */  
    public List<String> rangeList(String key, long start, long end) {  
        if (key == null || key.equals("")) {  
            return null;  
        }  
        try {  
            return jedisCluster.lrange(key, start, end);  
        } catch (Exception ex) {  
            logger.error("rangeList 出错[key=" + key + " start=" + start + " end=" + end + "]" + ex.getMessage() , ex);  
        } finally {  
            
        }  
        return null;  
    }  
      
    /** 
     * 检查List长度 
     * @param key 
     * @return 
     */  
    public long countList(String key){  
        if(key == null ){  
            return 0;  
        }  
        try {  
            return jedisCluster.llen(key);  
        } catch (Exception ex) {  
            logger.error("countList error.", ex);  
        } finally {  
            
        }  
        return 0;  
    }  
      
    /** 
     * 添加到List中（同时设置过期时间） 
     * @param key key值 
     * @param seconds 过期时间 单位s 
     * @param value  
     * @return  
     */  
    public boolean addList(String key,int seconds, String... value){  
        boolean result = addList(key, value);  
        if(result){  
            long i = expire(key, seconds);  
            return i==1;  
        }  
        return false;  
    }  
    /** 
     * 添加到List 
     * @param key 
     * @param value 
     * @return 
     */  
    public boolean addList(String key, String... value) {  
        if(key == null || value == null){  
            return false;  
        }  
        try {  
            jedisCluster.lpush(key, value);  
            return true;  
        } catch (Exception ex) {  
            logger.error("setList error.", ex);  
        } finally {  
            
        }  
        return false;  
    }  
    /** 
     * 添加到List(只新增) 
     * @param key 
     * @param value 
     * @return 
     */  
    public boolean addList(String key, List<String> list) {  
        if(key == null || list == null || list.size() == 0){  
            return false;  
        }  
        for(String value : list){  
            addList(key, value);  
        }  
        return true;  
    }  
      
    /** 
     * 获取List 
     * @param key 
     * @return 
     */  
    public  List<String> getList(String key){  
        try {  
            return jedisCluster.lrange(key, 0, -1);  
        } catch (Exception ex) {  
            logger.error("getList error.", ex);  
        } finally {  
            
        }  
        return null;  
    }  
    /** 
     * 设置HashSet对象 
     * 
     * @param domain 域名 
     * @param key    键值 
     * @param value  Json String or String value 
     * @return 
     */  
    public static boolean hmset(String key, Map<String, String> value) {  
    	if (value == null) return false;  
    	try {  
    		jedisCluster.hmset(key, value);
    		return true;  
    	} catch (Exception ex) {  
    		logger.error("setHSet error.", ex);  
    	} finally {  
    	}  
    	return false;  
    }  
    /** 
     * 设置HashSet对象 
     * 
     * @param domain 域名 
     * @param key    键值 
     * @param value  Json String or String value 
     * @return 
     */  
    public static boolean hset(String domain, String key, String value) {  
        if (value == null) return false;  
        try {  
            jedisCluster.hset(domain, key, value);  
            return true;  
        } catch (Exception ex) {  
            logger.error("setHSet error.", ex);  
        } finally {  
            
        }  
        return false;  
    }  
  
    /** 
     * 获得HashSet对象 
     * 
     * @param domain 域名 
     * @param key    键值 
     * @return Json String or String value 
     */  
    public static String hget(String domain, String key) {  
        try {  
            return jedisCluster.hget(domain, key);  
        } catch (Exception ex) {  
            logger.error("getHSet error.", ex);  
        } finally {  
            
        }  
        return null;  
    }  
    public static <T> T hget(String domain, String key,Class<T> clazz) throws Exception {  
    	String json = hget(domain,key);
		return JsonUtil.json2Obj(json, clazz);
    }
    /** 
     * 删除HashSet对象 
     * 
     * @param domain 域名 
     * @param key    键值 
     * @return 删除的记录数 
     */  
    public static long hdel(String domain, String key) {  
        long count = 0;  
        try {  
            count = jedisCluster.hdel(domain, key);  
        } catch (Exception ex) {  
            logger.error("delHSet error.", ex);  
        } finally {  
            
        }  
        return count;  
    }  
  
    /** 
     * 删除HashSet对象 
     * 
     * @param domain 域名 
     * @param key    键值 
     * @return 删除的记录数 
     */  
    public static long hdel(String domain, String... key) {  
        long count = 0;  
        try {  
            count = jedisCluster.hdel(domain, key);  
        } catch (Exception ex) {  
            logger.error("delHSet error.", ex);  
        } finally {  
            
        }  
        return count;  
    }  
  
    /** 
     * 判断key是否存在 
     * 
     * @param domain 域名 
     * @param key    键值 
     * @return 
     */  
    public static boolean hexists(String domain, String key) {  
        boolean isExist = false;  
        try {  
            isExist = jedisCluster.hexists(domain, key);  
        } catch (Exception ex) {  
            logger.error("existsHSet error.", ex);  
        } finally {  
            
        }  
        return isExist;  
    }  
    
  
    /** 
     * 全局扫描hset 
     * 
     * @param match field匹配模式 
     * @return 
     */  
//    public List<Map.Entry<String, String>> scanHSet(String domain, String match) {  
//        try {  
//            int cursor = 0;  
//            ScanParams scanParams = new ScanParams();  
//            scanParams.match(match);  
//            Jedis jedis = jedisCluster.getShard(domain);  
//            ScanResult<Map.Entry<String, String>> scanResult;  
//            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();  
//            do {  
//                scanResult = jedis.hscan(domain, String.valueOf(cursor), scanParams);  
//                list.addAll(scanResult.getResult());  
//                cursor = Integer.parseInt(scanResult.getStringCursor());  
//            } while (cursor > 0);  
//            return list;  
//        } catch (Exception ex) {  
//            logger.error("scanHSet error.", ex);  
//             
//        } finally {  
//            
//        }  
//        return null;  
//    }  
  
  
    /** 
     * 返回 domain 指定的哈希集中所有字段的value值 
     * 
     * @param domain 
     * @return 
     */  
  
    public List<String> hvals(String domain) {  
        List<String> retList = null;  
        try {  
            retList = jedisCluster.hvals(domain);  
        } catch (Exception ex) {  
            logger.error("hvals error.", ex);  
        } finally {  
            
        }  
        return retList;  
    }  
  
    /** 
     * 返回 domain 指定的哈希集中所有字段的key值 
     * 
     * @param domain 
     * @return 
     */  
  
    public static Set<String> hkeys(String domain) {  
        Set<String> retList = null;  
        try {  
            retList = jedisCluster.hkeys(domain);  
        } catch (Exception ex) {  
            logger.error("hkeys error.", ex);  
        } finally {  
            
        }  
        return retList;  
    }  
    
    /** 
     * 删除HashSet对象 
     * 
     * @param domain 域名 
     * @param key    键值 
     * @return 删除的记录数 
     */  
    public static long hdelAll(String domain) {  
        long count = 0;  
        try {  
            Set<String> set = hkeys(domain);
            count = jedisCluster.hdel(domain, set.toArray(new String[0]));  
        } catch (Exception ex) {  
            logger.error("delHSet error.", ex);  
        } finally {  
            
        }  
        return count;  
    }  
  
    /** 
     * 返回 domain 指定的哈希key值总数 
     * 
     * @param domain 
     * @return 
     */  
    public long lenHset(String domain) {  
        long retList = 0;  
        try {  
            retList = jedisCluster.hlen(domain);  
        } catch (Exception ex) {  
            logger.error("hkeys error.", ex);  
             
        } finally {  
            
        }  
        return retList;  
    }  
  
    /** 
     * 设置排序集合 
     * 
     * @param key 
     * @param score 
     * @param value 
     * @return 
     */  
    public boolean setSortedSet(String key, long score, String value) {  
        try {  
            jedisCluster.zadd(key, score, value);  
            return true;  
        } catch (Exception ex) {  
            logger.error("setSortedSet error.", ex);  
        } finally {  
            
        }  
        return false;  
    }  
  
    /** 
     * 获得排序集合 
     * 
     * @param key 
     * @param startScore 
     * @param endScore 
     * @param orderByDesc 
     * @return 
     */  
    public Set<String> getSoredSet(String key, long startScore, long endScore, boolean orderByDesc) {  
        try {  
            if (orderByDesc) {  
                return jedisCluster.zrevrangeByScore(key, endScore, startScore);  
            } else {  
                return jedisCluster.zrangeByScore(key, startScore, endScore);  
            }  
        } catch (Exception ex) {  
            logger.error("getSoredSet error.", ex);  
        } finally {  
            
        }  
        return null;  
    }  
  
    /** 
     * 计算排序长度 
     * 
     * @param key 
     * @param startScore 
     * @param endScore 
     * @return 
     */  
    public long countSoredSet(String key, long startScore, long endScore) {  
        try {  
            Long count = jedisCluster.zcount(key, startScore, endScore);  
            return count == null ? 0L : count;  
        } catch (Exception ex) {  
            logger.error("countSoredSet error.", ex);  
        } finally {  
            
        }  
        return 0L;  
    }  
  
    /** 
     * 删除排序集合 
     * 
     * @param key 
     * @param value 
     * @return 
     */  
    public boolean delSortedSet(String key, String value) {  
        try {  
            long count = jedisCluster.zrem(key, value);  
            return count > 0;  
        } catch (Exception ex) {  
            logger.error("delSortedSet error.", ex);  
        }  
        return false;  
    }  
  
    /** 
     * 获得排序集合 
     * 
     * @param key 
     * @param startRange 
     * @param endRange 
     * @param orderByDesc 
     * @return 
     */  
    public Set<String> getSoredSetByRange(String key, int startRange, int endRange, boolean orderByDesc) {  
        try {  
            if (orderByDesc) {  
                return jedisCluster.zrevrange(key, startRange, endRange);  
            } else {  
                return jedisCluster.zrange(key, startRange, endRange);  
            }  
        } catch (Exception ex) {  
            logger.error("getSoredSetByRange error.", ex);  
             
        }   
        return null;  
    }  
  
    /** 
     * 获得排序打分 
     * 
     * @param key 
     * @return 
     */  
    public Double getScore(String key, String member) {  
        try {  
            return jedisCluster.zscore(key, member);  
        } catch (Exception ex) {  
            logger.error("getSoredSet error.", ex);  
        } 
        return null;  
    } 
}
