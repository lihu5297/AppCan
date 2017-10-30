package org.zywx.cooldev.util;

import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

public class JedisUtil {
	 public static void main(String[] args) {
		 Set sentinels = new HashSet();
	     sentinels.add(new HostAndPort("192.168.1.202", 26380).toString());
	     sentinels.add(new HostAndPort("192.168.1.202", 26381).toString());
	     sentinels.add(new HostAndPort("192.168.1.202", 26382).toString());
	     JedisSentinelPool sentinelPool = new JedisSentinelPool("mymaster", sentinels);
	     System.out.println("Current master: " + sentinelPool.getCurrentHostMaster().toString());
	     Jedis master = sentinelPool.getResource();
	     master.set("username","liangzhichao1");
	     sentinelPool.returnResource(master);
	     Jedis master2 = sentinelPool.getResource();
	     String value = master2.get("username");
	     System.out.println("username: " + value);
	     master2.close();
	     sentinelPool.destroy();
	}
}
