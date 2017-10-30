package org.zywx.cooldev.service;

import java.io.Serializable;
import java.sql.Timestamp;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.entity.CheckInfo;

@Service
public class TopicMessageListener implements MessageListener {

	@Autowired
	protected CheckInfoService checkInfoService;
	private RedisTemplate<Serializable, Serializable> redisTemplate;
	
	public void setCheckInfoService(CheckInfoService checkInfoService){
		this.checkInfoService = checkInfoService;
	}
	public void setRedisTemplate(RedisTemplate<Serializable, Serializable> redisTemplate) {  
		 this.redisTemplate = redisTemplate;  
	}  

	@Override
	public void onMessage(Message message, byte[] arg1) {
		byte[] body = message.getBody();// 请使用valueSerializer
		byte[] channel = message.getChannel();
		// 请参考配置文件，本例中key，value的序列化方式均为string。
		// 其中key必须为stringSerializer。和redisTemplate.convertAndSend对应
		  String msgBody = (String) redisTemplate.getValueSerializer().deserialize(body);  
		  String channelName = (String) redisTemplate.getStringSerializer().deserialize(channel);  

		if(channelName.equals("meap_msg_xietong:checkOut") && StringUtils.isNotBlank(msgBody)){
			//{"type":"app", "from":"res", "resPath":"", "status":"success", "uniqueId":"1", "startTime":"1503472861325", "execTime":"30"}
			JSONObject json = JSONObject.fromObject(msgBody);
			//如果是请求响应，则继续
			if(json != null && json.get("from") != null && json.getString("from").equals("res")){
				String uniqueId = json.getString("uniqueId");
				CheckInfo ci = checkInfoService.findByUniqueId(uniqueId);
				if(ci != null){
					ci.setCheckFilePath(json.get("resPath") != null ? json.getString("resPath") : "");
					ci.setCheckInfo("");
					ci.setCheckResult(json.get("status") != null ? json.getString("status") : "");
					ci.setDuration(json.get("execTime") != null ? json.getString("execTime") : "");
					ci.setDel(DELTYPE.NORMAL);
					int i = checkInfoService.checkInfoSaveUpdate(ci);
					if(i == 0){
						System.out.println("更新失败:"+ci.getId());
					}else{
						System.out.println("更新成功"+ci.getId());
					}
					
				}
			}
		}
	}

}
