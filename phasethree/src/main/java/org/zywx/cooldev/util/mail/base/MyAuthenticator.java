	/**  
     * @author jingjian.wu
     * @date 2015年11月5日 下午4:55:52
     */
    
package org.zywx.cooldev.util.mail.base;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


    /**
 * @author jingjian.wu
 * @date 2015年11月5日 下午4:55:52
 */

public class MyAuthenticator extends Authenticator{

	String userName=null;   
    String password=null;   
        
    public MyAuthenticator(){   
    }   
    public MyAuthenticator(String username, String password) {    
        this.userName = username;    
        this.password = password;    
    }    
    protected PasswordAuthentication getPasswordAuthentication(){   
        return new PasswordAuthentication(userName, password);   
    }   
}
