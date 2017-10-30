package org.zywx.cooldev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TopicMessagePublish {
	
	@Autowired
	private RedisTemplate<?, ?> redisTemplate;
	
	public synchronized void messagePublic(String channel,String msg){
		//其中channel必须为string，而且“序列化”策略也是StringSerializer  
		//消息内容，将会根据配置文件中指定的valueSerializer进行序列化  
		//本例中，默认全部采用StringSerializer  
		//那么在消息的subscribe端也要对“发序列化”保持一致。  
		redisTemplate.convertAndSend(channel, msg);  

	}
}
