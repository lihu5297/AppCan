package org.zywx.coopman.commons;

/**
 * 
    * @ClassName: Enums
    * @Description: 项目中使用到的枚举类型定义 
    * @author wjj
    * @date 2015年8月8日 下午2:10:31
    *
 */
public class Enums {

	/**
	 * 是否删除
	 * 0 -> NORMAL 正常
	 * 1 -> DELETED 已删除
	 * @author jiexiong.liu
	 * @date 2015-08-11
	 *
	 */
	public enum DELTYPE{
		/**
		 * NORMAL=0（正常）<br>DELETED=1（已删除）
		 */
		NORMAL,DELETED;
		
	}
	
	/**
	 * 用户状态
	 * NORMAL 正常用户 
	 * FORBIDDEN 禁用用户
	 * AUTHSTR 待审核用户
	 * UNPASS 审核未通过用户
	 * 从appcan导入的用户，初始状态为待审核状态，审核通过就变成正常状态，审核未通过就是未通过状态，禁用是禁用状态，启用是正常状态
	 * @author yang.li
	 * @date 2015-08-10
	 *
	 */
	public enum USER_STATUS {
		NORMAL, FORBIDDEN,AUTHSTR,UNPASS;
	}
	
    /**
    * @ClassName: USER_TYPE
    * @Description: 用户状态 0 未注册  1未认证开发者  2已认证开发者
    * @author wjj
    * @date 2015年8月13日 下午2:16:23
    *
    */
	    
	public enum USER_TYPE {
		
		NOREGISTER,NOAUTHENTICATION,AUTHENTICATION;
		
	}

	/**
	 * 		角色类型
	     * @Description: 
	     * CREATOR		      创建者
	     * ADMINISTRATOR  管理员
	     * MANAGER  	      负责人
	     * MEMBER		      普通成员
	     * @author jingjian.wu
	     * @date 2015年8月25日 上午10:03:45
	     *
	 */
	public enum ROLE_TYPE{
		CREATOR, ADMINISTRATOR, MANAGER, MEMBER;
	}
	
	/**
	 * 数据操作类型，用于标识数据操作许可
	 * @author yang.li
	 * @date 2015-08-25
	 *
	 */
	public enum CRUD_TYPE {
		//Create)、读取(Retrieve)（重新得到数据）、更新(Update)和删除(Delete)
		CREATE, RETRIEVE, EDIT, REMOVE
	}
	
	public enum RepositoryType {
		GIT, SVN
	}
	
	/**
	 * 支持的操作系统类型
	 * @author yang.li
	 * @date 2015-09-06
	 *
	 */
	public enum OSType {
		IOS, ANDROID
	}
	
	/**
	 * 实体类型
	 * @author yang.li
	 * @date 2015-08-14
	 *
	 */
	public enum ENTITY_TYPE {
		PROJECT, PROCESS, TASK, TEAM, TOPIC, COMMENT,DOCUMENT,DOCUMENTCHAPTER, TASK_COMMENT,RESOURCE
	}
	
	/**
	 * 
	 * @describe 	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月10日 下午10:44:58	<br>
	 * @param args  <br>
	 * @returnType void
	 *
	 */
	public enum OpertionType
	{
		POST(1, "执行了","POST"), DELETE(2, "删除了","DELETE"), PUT(3, "更新了","PUT"),GET(4,"访问了","GET");

	    private int value;
	    private String name;
	    private String alias;

	    private OpertionType(int value, String name,String alias)
	    {
	        this.value = value;
	        this.name = name;
	        this.alias = alias;
	    }

	    public int value()
	    {
	        return this.value;
	    }

	    public String getName()
	    {
	        return this.name;
	    }
	    
	    public String getAlias()
	    {
	        return this.alias;
	    }	    
	    
	}
	
	public enum URLTYPE{
		PLATFORMLOG,PROJECT, PROCESS, TASK, TEAM, TOPIC, COMMENT,DOCUMENT,DOCUMENTCHAPTER, TASK_COMMENT,RESOURCE
	}
	
	/**
	 * 用户接入平台
	 * INNER  公司内部
	 * APPCAN  appCan官网
	     * @author jingjian.wu
	     * @date 2015年9月15日 上午10:09:06
	 */
	public enum USER_JOINPLAT{
		INNER,APPCAN
	}
	
	/**
	 * 角色是否允许删除
	 * DENIED  不允许
	 * PERMIT 允许
	     * @author jingjian.wu
	     * @date 2015年9月15日 上午10:28:39
	 */
	public enum ROLE_ALLOW_DEL{
		DENIED,PERMIT
	}
	
	/**
	 * 用户是否同意接收邮件
	 * DENIED 拒绝
	 * PERMIT 允许
	     * @author jingjian.wu
	     * @date 2015年9月15日 上午10:30:34
	 */
	public enum USER_EMAIL_STATUS{
		DENIED,PERMIT
	}
	
	/**
	 * 用户等级
	 * ADVANCE 高级用户
	 * NORMAL 普通用户
	     * @author jingjian.wu
	     * @date 2015年9月17日 下午6:11:55
	 */
	public enum USER_LEVEL{
		NORMAL,ADVANCE
	}
	
	/**
	 * 用户性别<br>
	 * MALE   : 男性<br>
	 * FEMAIL : 女性<br>
	 *  
	 * @author yang.li
	 * @date 2015-09-18
	 */
	public enum UserGender {
		MALE, FEMALE, UNKNOWN
	}
	
	/**
	 * 
	 * @describe 模块类型	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月16日 下午3:01:20	<br>
	 * 0-->mormal-->最底层模块（有url）
	 * 1-->specail-->有孩子节点模块（没有url）
	 *
	 */
	public enum MODULE_TYPE{
		NORMAL,SPECAIL
	}
	
	/**
	 * 
	 * @describe 管理员类型	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月18日 上午10:23:25	<br>
	 *	ADMIN --> 总部管理员 0
	 *	SUPERADMIN --> 超级管理员 1
	 *	FILIALEADMIN --分公司管理员2
	 */
	public enum MANAGER_TYPE{
		ADMIN,SUPERADMIN,FILIALEADMIN
	}
	
	/**
	 * 
	 * @describe 接入系统状态	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月23日 下午3:35:59	<br>
	 *
	 */
	public enum INTEGRATE_STATUS{
		NORMAL,FORBIDDEN
	}
	
	public enum AUTH_STATUS{
		EFFECTIVE,OFNOAVAIL
	}
	
	/**
	 * 
	 * @describe 邮件服务器类型	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月23日 下午3:47:37	<br>
	 *
	 */
	public enum EMAIL_SERVER_TYPE{
		POP3,IMAP,EXCHANGE, SMTP
	}
	public enum EMAIL_STATUS{
		OPEN,CLOSE
	}
	/**
	 * 插件类型
	 * PUBLIC  公共插件（官方）
	 * PRIVATE 内部插件（公司私有）
	 * PROJECT 项目插件
	 * 
	 * @author yang.li
	 *
	 */
	public enum PluginType {
		PUBLIC, PRIVATE, PROJECT
	}

	/**
	 * 引擎类型
	 * PUBLIC  公共引擎（官方）
	 * PRIVATE 内部引擎（公司私有）
	 * PROJECT 项目引擎
	 * 
	 * @author yang.li
	 *
	 */
	public enum EngineType {
		PUBLIC, PRIVATE, PROJECT
	}
	
	/**
	 * @describe 插件状态	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月7日 下午6:43:31	<br>
	 *	DISABLE --> 1   禁用
	 *	ENABLE --> 0  启用
	 */
	public enum PluginVersionStatus {
		ENABLE, DISABLE
	}

	/**
	 * @describe 引擎状态	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月7日 下午6:43:31	<br>
	 *	DISABLE --> 1   禁用
	 *	ENABLE --> 0  启用
	 */
	public enum  EngineStatus{
		ENABLE, DISABLE
	}
	
	/**
	 * 
	 * @describe 插件分类状态	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月16日 上午10:40:16	<br>
	 * 	DISABLE --> 1   禁用
	 *	ENABLE --> 0  启用
	 *
	 */
	public enum PLUGIN_CATEGORY_STATUS{
		ENABLE, DISABLE
	}
	
	/**
	 * 流程模板状态
	 * @author yang.li
	 *
	 */
	public enum ProcessTemplateStatus {
		ENABLE, DISABLE
	}

	/**
	 * 任务状态
	 * 
	 * WAITING 待执行
	 * ONGOING 进行中
	 * REJECTED 已驳回
	 * FINISHED 已完成
	 * SUSPENDED 已搁置
	 * CLOSED 已关闭
	 * @author yang.li
	 * @date 2015-08-10
	 *
	 */
	public enum TASK_STATUS {
		WAITING, ONGOING, REJECTED, FINISHED, SUSPENDED, CLOSED
	}
	
	/**
	 * 
	 * @describe git操作类型	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月23日 上午11:52:31	<br>
	 *
	 */
	public enum GIT_OPERATE_TYPE{
		ADD,REMOVE,MERGE
	}
	
	/**
	 * App构建状态<br>
	 * ONGOING 构建进行中<br>
	 * SUCCESS 构建成功<br>
	 * FAILED  构建失败<br>
	 * @author yang.li
	 * @date 2015-09-12
	 *
	 */
	public enum AppPackageBuildStatus {
		ONGOING, SUCCESS, FAILED
	}
	
	/**
	 * 
	 * App构建类型<br>
	 * TESTING : 测试包<br>
	 * PRODUCTION : 正式包<br>
	 * @author yang.li
	 * @date 2015-09-24
	 *
	 */
	public enum AppPackageBuildType {
		TESTING, PRODUCTION
	}
	
	/**
	 * @describe 备份状态	<br>
	 * @author jiexiong.liu	<br>
	 * ONGOING：进行中<br>
	 * SUCCEED：已完成<br>
	 * @date 2015年12月24日 上午9:48:33	<br>
	 *
	 */
	public enum BACKUP_STATUS{
		ONGOING,SUCCEED
	}
	
	/**
	 * 引擎插件上传状态
	 */
	public enum UploadStatus {
		ONGOING, SUCCESS, FAILED 
	}
	public  enum VIDEO_STATUS{
		NOPUBLISH,PUBLISH
	}
	public  enum VIDEO_TUIJIAN{
		NOTUIJIAN,TUIJIAN
	}
	public enum VIDEO_TYPE{
		JUNIOR,MIDDLE,SENIOR
	}
	public static void main(String[] args) {
		ENTITY_TYPE pro = ENTITY_TYPE.PROCESS;
		ENTITY_TYPE task = ENTITY_TYPE.PROCESS;
		System.out.println(ENTITY_TYPE.valueOf("TEAM"));
		System.out.println(pro.equals(task));
		System.out.println(pro==task);
		
		System.out.println(java.util.Calendar.getInstance().get(java.util.Calendar.YEAR));
		
		System.out.println(ENTITY_TYPE.PROCESS + "_"+ENTITY_TYPE.PROJECT);
	}

}
