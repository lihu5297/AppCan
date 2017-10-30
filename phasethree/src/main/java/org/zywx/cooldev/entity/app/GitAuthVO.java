	/**  
     * @author jingjian.wu
     * @date 2015年12月11日 上午11:28:33
     */
    
package org.zywx.cooldev.entity.app;

import org.zywx.cooldev.util.MD5Util;


    /**
     * git权限授权相关接口
 * @author jingjian.wu
 * @date 2015年12月11日 上午11:28:33
 */

public class GitAuthVO {

		//git项目创建者
		private String username;
		//git项目分享权限的用户名
		private String partnername;
		//git项目的项目标识
		//String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
		private String project;
		//主干还是分支  取值为  all  branch
		private String authflag;
		//如果authflag为branch,ref为分支名
		private String ref;
		
		//appcanappid
		private String projectid;
		
		

		public String getProjectid() {
			return projectid;
		}

		public void setProjectid(String projectid) {
			this.projectid = projectid;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPartnername() {
			return partnername;
		}

		public void setPartnername(String partnername) {
			this.partnername = partnername;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public String getAuthflag() {
			return authflag;
		}

		public void setAuthflag(String authflag) {
			this.authflag = authflag;
		}

		public String getRef() {
			return ref;
		}

		public void setRef(String ref) {
			this.ref = ref;
		}
		
	
}
