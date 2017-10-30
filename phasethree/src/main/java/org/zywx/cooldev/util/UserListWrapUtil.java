package org.zywx.cooldev.util;

import java.util.List;

import org.zywx.cooldev.entity.User;

public class UserListWrapUtil {

	/**
	 * 
	    * @Title: setNullForPwdFromUserList
	    * @Description:为了返回json串中不包含密码,设置用户列表中每个用户的密码为null 
	    * @param @param listUser    参数
	    * @return void    返回类型
		* @user wjj
		* @date 2015年8月12日 下午9:09:10
	    * @throws
	 */
	public static void setNullForPwdFromUserList(List<User> listUser){
		if(null !=listUser && listUser.size()>0){
			for(User u:listUser){
				u.setPassword(null);
			}
		}
	}
	
	public static String getUserNick(User user){
		if(null != user.getUserName() && !user.getUserName().equals("")){
			return user.getUserName();
		}else
			return user.getAccount();
	}
}
