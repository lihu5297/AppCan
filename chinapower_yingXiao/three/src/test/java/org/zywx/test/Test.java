	/**  
     * @author jingjian.wu
     * @date 2015年9月24日 上午10:05:02
     */
    
package org.zywx.test;

import org.zywx.appdo.common.utils.HttpTools;
import org.zywx.appdo.common.utils.HttpUtils;
import org.zywx.appdo.common.utils.license.Md5Encrypt;
import org.zywx.coopman.util.MD5Util;




    /**
 * @author jingjian.wu
 * @date 2015年9月24日 上午10:05:02
 */

public class Test {

	/**
	 * @user jingjian.wu
	 * @date 2015年9月24日 上午10:05:02
	 */

	public static void main(String[] args) {
//		String str = HttpTools.sendGet("http://newsso.appcan.cn/captcha", null);
//		System.out.println(str);
		
		/*String md5Pass = MD5Util.MD5("123456").toLowerCase();
		String md5Pass11 =Md5Encrypt.md5("123456");
		System.out.println(md5Pass.equals(md5Pass11));
//		String md5Pass = "aaaaaa";
		StringBuffer sb = new StringBuffer();
		sb.append("loginName=");
		sb.append("yongwen1");
		sb.append("&tenant=");
		sb.append("205");
		sb.append("&tenantName=");
		sb.append("xtkf");
		sb.append("&nickName=");
		sb.append("yongwen1");
		sb.append("&captcha=");
		sb.append("");
		sb.append("&loginPassword=");
		sb.append( md5Pass);
		sb.append("&regSource=''");
		sb.append("&regRefer=''");
		sb.append("&regFrom=emm");
		sb.append("&isAdmin=0&isUse=1&creator=");
		sb.append("xtkf");
		sb.append("&userEmail=");
		sb.append("289306290@qq.com");
		String postResult = HttpTools.sendPost("http://192.168.4.29:8087/cas/"+"signup", sb.toString());
		System.out.println(postResult);*/

		MailThread m1 = new MailThread();
		Thread thread = new Thread(m1);
		Thread thread1 = new Thread(m1);
		thread.start();
		thread1.start();
	}

}
