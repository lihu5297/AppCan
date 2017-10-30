package org.zywx.cooldev.service;

import redis.clients.jedis.JedisPubSub;
public class TopicMessageRedis extends JedisPubSub{

	 public void onMessage(String s, String s1) {  
		   // TODO Auto-generated method stub  
		      System.out.println("Message received,Channel:"+s+",Msg:"+s1);  
	 }  

}
