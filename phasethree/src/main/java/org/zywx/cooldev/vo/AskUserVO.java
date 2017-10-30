package org.zywx.cooldev.vo;

import java.io.Serializable;

import org.zywx.cooldev.commons.Enums;

/**
 * 
    * @ClassName: AskUserVO
    * @Description:团队邀请成员加入时候,前台传入参数转为此对象使用 
    * @author jingjian.wu
    * @date 2015年8月13日 下午2:27:17
    *
 */
public class AskUserVO implements Serializable {

	
	    /**
	    * @Fields serialVersionUID :
	    */
	    
	private static final long serialVersionUID = -5749018872736080482L;

	private String email;
	/**
	 * 0.普通成员   
	 * 1.管理员
	 * 对应Enums.USER_ASKED_TYPE
	 */
	private Enums.USER_ASKED_TYPE userAuth;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Enums.USER_ASKED_TYPE getUserAuth() {
		return userAuth;
	}

	public void setUserAuth(Enums.USER_ASKED_TYPE userAuth) {
		this.userAuth = userAuth;
	}

}
