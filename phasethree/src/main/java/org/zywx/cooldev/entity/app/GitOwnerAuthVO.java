	/**  
     * @author jingjian.wu
     * @date 2015年12月11日 上午11:28:33
     */
    
package org.zywx.cooldev.entity.app;



    /**
     * git拥有者权限授权相关接口
 * @author jingjian.wu
 * @date 2015年12月11日 上午11:28:33
 */

public class GitOwnerAuthVO {

		//git项目创建者
		private String username;
		//git项目的项目标识
		//String encodeKey = "X" + MD5Util.MD5(app.getAppcanAppKey()).substring(0,5);
		private String project;
		
		private String projectid;
		
		public String getProjectid() {
			return projectid;
		}
		public void setProjectid(String projectid) {
			this.projectid = projectid;
		}
		//拥有者要转给谁
		private String other;
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getProject() {
			return project;
		}
		public void setProject(String project) {
			this.project = project;
		}
		public String getOther() {
			return other;
		}
		public void setOther(String other) {
			this.other = other;
		}

	
}
