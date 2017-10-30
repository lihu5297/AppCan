	/**  
     * @author jingjian.wu
     * @date 2015年11月3日 下午5:12:12
     */
    
package org.zywx.test;

import org.zywx.appdo.common.utils.HttpUtils;


    /**
 * @author jingjian.wu
 * @date 2015年11月3日 下午5:12:12
 */

public class MailThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			System.out.println("==============================");
			HttpUtils.sendGet("http://localhost:8080/cooldev/email", null);
		}

	
}
