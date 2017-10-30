	/**  
     * @author jingjian.wu
     * @date 2015年11月6日 下午4:42:05
     */
    
package org.zywx.cooldev.util.emm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zywx.appdo.common.constant.IConstInfo;
import org.zywx.appdo.common.utils.crypt.BaseCrypt;
import org.zywx.appdo.common.utils.crypt.CryptFactory;


    /**
 * @author jingjian.wu
 * @date 2015年11月6日 下午4:42:05
 */

public class TokenUtilProduct {
	private static Log log = LogFactory.getLog(TokenUtilProduct.class.getName());
	public static String getToken(String key,String[] params){
		BaseCrypt baseCrypt = CryptFactory.getCrypt(IConstInfo.CRYPT_TYPE_RC4);
		String tokenInfo = "";
		for (int i = 0; i < params.length; i++) {
			if(i<params.length-1){
				tokenInfo = tokenInfo + params[i] +"-";
			}else{
				tokenInfo = tokenInfo + params[i];
			}
		}
		//测试添加
		String result ="";
		if(null!=params){
			for(int i=0;i<params.length;i++){
				result+=params[i]+",";
			}
		}
		log.info("生成正式环境token:参数key->"+key+",params->"+result);
		//测试添加
		
		String token = baseCrypt.encrypt(key,tokenInfo+"-"+System.currentTimeMillis());
		log.info("生成正式环境token值为:toke-->"+token);
		//需要存到redis key = token ,value = 租户key;
		boolean recordFlag = RedisUtilProduct.set(token, key, IConstInfo.RPCCALL_TIMEOUT);
		log.info("存放redis正式环境token结果--->"+recordFlag);
		return token;
	}
	
	public static String[] getTokenInfo(String token){
		String key = RedisUtilProduct.get(token);//需要从redis 中根据token 取出key
		BaseCrypt baseCrypt = CryptFactory.getCrypt(IConstInfo.CRYPT_TYPE_RC4);
		String tokenInfo = baseCrypt.decrypt(key, token);
		return tokenInfo.split("-");
	}
	
	public static String getDecryptToken(String token){
		log.info("token---"+token);
		String key = RedisUtilProduct.get(token);//需要从redis 中根据token 取出key
		log.info("key---"+key);
		BaseCrypt baseCrypt = CryptFactory.getCrypt(IConstInfo.CRYPT_TYPE_RC4);
		String tokenInfo = baseCrypt.decrypt(key, token);
		log.info("tokenInfo---"+tokenInfo);
		return tokenInfo;
	}
	
	
	public static String getDefaultToken(String[] params){
		String key = IConstInfo.DEFAULT_KEY;
		BaseCrypt baseCrypt = CryptFactory.getCrypt(IConstInfo.CRYPT_TYPE_RC4);
		String tokenInfo = "";
		for (int i = 0; i < params.length; i++) {
			if(i<params.length-1){
				tokenInfo = tokenInfo + params[i] +"-";
			}else{
				tokenInfo = tokenInfo + params[i];
			}
		}
		String token = baseCrypt.encrypt(key,tokenInfo+"-"+System.currentTimeMillis());
		//需要存到redis key = token ,value = 租户key;
//		RedisUtilProduct.set(token, key, IConstInfo.RPCCALL_TIMEOUT);
		return token;
	}
	public static String getDefaultDecryptToken(String token){
		log.info("token---"+token);
		String key = IConstInfo.DEFAULT_KEY;//RedisUtilProduct.get(token);//需要从redis 中根据token 取出key
		log.info("key---"+key);
		BaseCrypt baseCrypt = CryptFactory.getCrypt(IConstInfo.CRYPT_TYPE_RC4);
		String tokenInfo = baseCrypt.decrypt(key, token);
		log.info("tokenInfo---"+tokenInfo);
		return tokenInfo;
	}

	public static Long getTenantId(String decryptToken){
		log.info("token-----------------------------"+decryptToken);
		return new Long(decryptToken.split("-")[0]);
	}
	
	public static void main(String[] args) {
		String tenantId = "1";
		String key = tenantId;//
		String[] params = new String[2];
		params[0] = tenantId;
		params[1] = IConstInfo.WEB_APP_EMM;//调用方标识，比如OA,EMM,OMM等
		log.info(TokenUtilProduct.getToken(key, params));
	}
}
