package org.zywx.cooldev.commons;

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
	 * 
	    * @ClassName: TEAMTYPE
	    * @Description: 团队类型 0普通团队  1企业团队 2绑定审核中 3解绑审核中 
	    * @author wjj
	    * @date 2015年8月8日 下午2:10:14
	    *
	 */
	public enum TEAMTYPE{
		NORMAL,ENTERPRISE,BINDING,UNBINDING;
	}
	
	/**
	 * 
	    * @ClassName: TEAMREALTIONSHIP
	    * @Description: 团队关系  0我创建  1我参与  2被邀请
	    * @author wjj
	    * @date 2015年8月10日 下午5:36:35
	    *
	 */
	public enum TEAMREALTIONSHIP{
		CREATE,ACTOR ,ASK;
	}
	
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
	 * 
	    * @ClassName: USER_ASKED_TYPE
	    * @Description: 团队邀请成员时候,指定的类型0.普通成员.1管理员
	    * @author jingjian.wu
	    * @date 2015年8月13日 下午2:26:28
	    *
	 */
	public enum USER_ASKED_TYPE{
		
		ACTOR,MANAGER;
		
	}

	/**
	 * 项目类型
	 * PERSONAL 个人项目0
	 * TEAM 团队项目1
	 * @author yang.li
	 * @date 2015-08-10
	 *
	 */
	public enum PROJECT_TYPE {
		PERSONAL, TEAM
	}
	
	/**
	 * 项目状态
	 * FINISHED 已完成
	 * ONGOING 未完成（进行中）
	 * @author yang.li
	 * @date 2015-08-10
	 *
	 */
	public enum PROJECT_STATUS {
		FINISHED, ONGOING	
	}

	/**
	 * 项目授权状态（企业授权）
	 * AUTHORIZED 已授权
	 * NOT_AUTHORIZED 未授权
	 * BINDING 绑定审核中
	 * UNBINDING 解绑审核中
	 * @author yang.li
	 * @date 2015-08-10
	 * @Modify haijun.cheng
	 * @modifyData 2016-07-15
	 *
	 */	
	public enum PROJECT_BIZ_LICENSE {
		AUTHORIZED, NOT_AUTHORIZED,BINDING,UNBINDING	
	}
	
	/**
	 * 项目成员类型
	 * CREATOR 创建者
	 * PARTICIPATOR 参与者
	 * INVITEE 受邀者
	 * @author yang.li
	 * @date 2015-08-10
	 *
	 */	
	public enum PROJECT_MEMBER_TYPE {
		CREATOR, PARTICIPATOR, INVITEE
	}	
	
	public enum PROCESS_MEMBER_TYPE {
		CREATOR, PARTICIPATOR
	}
	
	/**
	 * 任务优先级
	 * 
	 * NORMAL 普通
	 * URGENT 紧急
	 * VERY_URGENT 非常紧急
	 * @author yang.li
	 * @date 2015-08-10
	 *
	 */
	public enum TASK_PRIORITY {
		NORMAL, URGENT, VERY_URGENT
	}
	
	/**
	 * 任务重复执行
	 * 
	 * NONE 不重复执行
	 * DAY 每日
	 * WEEK 每周
	 * MONTH 每月
	 * SEASON 每季度
	 * YEAR 每年
	 * @author yang.li
	 * @date 2015-08-10
	 *
	 */
//	public enum TASK_REPEATABLE {
//		NONE, DAY, WEEK, MONTH, SEASON, YEAR
//	}
	/**
	 * 任务重复执行
	 * NONE	不执行
	 * DAY 每天
	 * WEEK	每周
	 * TWOWEEK	每两周
	 * MONTH	每月
	     * @author jingjian.wu
	     * @date 2015年9月15日 下午7:26:49
	 */
	public enum TASK_REPEATABLE {
		NONE, DAY, WEEK, TWOWEEK, MONTH
	}
	
	/**
	 * 任务默认分组
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
	public enum TASK_GROUP {
		WAITING, ONGOING, REJECTED, FINISHED, SUSPENDED, CLOSED
	}
	
	/**
	 * 任务状态
	 * NOFINISHED 未完成
	 * FINISHED 已完成
	 *
	 */
	public enum TASK_STATUS{
		NOFINISHED,FINISHED;
	}
	
	public enum TASK_MEMBER_TYPE {
		CREATOR, PARTICIPATOR
	}

	/**
	 * 讨论成员类型
	 * 0 -> SPONSOR 创建者
	 * 1 -> ACTOR 参与者
	 * @author jiexiong.liu
	 * @date 2015-08-11
	 *
	 */
	public enum TOPIC_MEMBER_TYPE{
		/**
		 * SPONSOR=0(创建者)
		 * ACTOR=1(参与者)
		 */
		SPONSOR,ACTOR,OTHER;
	}
	
	/**
	 * 文档发布类型
	 * 0 -> PUBLISHED 已发布
	 * 1 -> RETRIEVED 已回收（未发布），默认
	 * @author jiexiong.liu
	 * @date 2015-08-13
	 *
	 */
	public enum DOC_PUB_TYPE{
		PUBLISHED,RETRIEVED;
	}
	
	/**
	 * 文档成员类型
	 * 0 -> SPONSOR 创建者
	 * 1 -> ACTOR 参与者
	 * @author jiexiong.liu
	 * @date 2015-08-13
	 *
	 */
	public enum DOC_MEMBER_TYPE{
		/**
		 * SPONSOR=0(创建者)
		 * ACTOR=1(参与者)
		 */
		SPONSOR,ACTOR;
	}
	
	/**
	 * 章、节类型
	 * 0 -> CHAPTER 章
	 * 1 -> PART 节
	 * @author jiexiong.liu
	 * @date 2015-08-13
	 *
	 */
	public enum DOC_CHAPTER_TYPE{
		/**
		 * CHAPTER=0(章)
		 * PART=1(节)
		 */
		CHAPTER,PART;
	}

	/**
	 * 实体类型
	 * @author yang.li
	 * @date 2015-08-14
	 *
	 */
	public enum ENTITY_TYPE {
		PROJECT, PROCESS, TASK, TEAM, TOPIC, COMMENT,DOCUMENT,DOCUMENTCHAPTER, TASK_COMMENT,RESOURCE, PLUGIN, ENGINE,BUG,BUG_MODULE,DATAMODEL,INTERFACE,APP
	}
	
	/**
	    * @ClassName: DYNAMIC_TYPE
	    * @Description:动态类型,0项目相关,1团队相关 
	    * @author jingjian.wu
	    * @date 2015年8月17日 上午9:21:43
	    *
	 */
	public enum DYNAMIC_TYPE{
		PROJECT,TEAM;
	}
	
	/**
	 * 
	 * @describe 通知阅读状态	<br>
	 * @Description:阅读类型,0未读,1已读 <br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月18日 下午3:41:41	<br>
	 *
	 */
	public enum NOTICE_READ_TYPE{
		UNREAD,READ;
	}
	
	/**
	 * 动态模板类型
	 * TEAM_ADD   创建团队
	    * @Description: 
	    * @author jingjian.wu
	    * @date 2015年8月19日 上午11:36:52
	    *
	 */
	public enum DYNAMIC_MODULE_TYPE{
		/**
		 * 创建项目
		 */
		PROJECT_ADD,
		/**
		 * 编辑项目
		 */
		PROJECT_EDIT,
		/**
		 * 删除项目
		 */
		PROJECT_REMOVE,
		/**
		 * 绑定项目
		 */
		PROJECT_BIZ_BIND,
		/**
		 * 项目申请绑定
		 */
		PROJECT_ASK_BIND,
		/**
		 * 项目申请取消绑定
		 */
		PROJECT_CANCEL_BIND,
		/**
		 * 项目申请解绑
		 */
		PROJECT_ASK_UNBIND,
		/**
		 * 项目取消解绑
		 */
		PROJECT_CANCEL_UNBIND,
		/**
		 * 解绑项目
		 */
		PROJECT_BIZ_UNBIND,
		/**
		 * 转让项目
		 */
		PROJECT_TRANSFER,
		/**
		 * 邀请成员
		 */
		PROJECT_INVITE_MEMBER,
		/**
		 * 添加成员
		 */
		PROJECT_ADD_MEMBER,
		/**
		 * 更新管理员
		 */
		PROJECT_UPDATE_LEADER,
		/**
		 * 更新成员
		 */
		PROJECT_UPDATE_MEMBER,
		/**
		 * 移除成员
		 */
		PROJECT_REMOVE_MEMBER,
		/**
		 * 成员加入项目
		 */
		PROJECT_JOIN_MEMBER,
		
		
		/**
		 * 创建应用
		 */
		APP_ADD,
		/**
		 * 修改应用
		 */
		APP_EDIT,
		/**
		 * 删除应用
		 */
		APP_DELETE,
		/**
		 * 创建分支
		 */
		APP_ADD_BRANCH,
		/**
		 * 删除分支
		 */
		APP_DELETE_BRANCH,
		/**
		 * 合并分支
		 */
		APP_MERGE_BRANCH,
		/**
		 * 发布主干版本
		 */
		APP_PUBLISH_MASTER_VERSION,
		/**
		 * 发布主干测试包
		 */
		APP_PUBLISH_MASTER_TEST_PACKAGE,
		/**
		 * 发布分支版本
		 */
		APP_PUBLISH_BRANCH_VERSION,
		/**
		 * 发布分支测试包
		 */
		APP_PUBLISH_BRANCH_TEST_PACKAGE,
		/**
		 * 生成补丁包
		 */
		APP_ADD_PATCH_PACKAGE,
		/**
		 * 编辑版本描述
		 */
		APP_EDIT_VERSION_DESCRIBE, 
		/**
		 * 删除版本
		 */
		APP_DELTE_VERSION, 
		/**
		 * 生成个人——IOS——正式包
		 */
		APP_ADD_PERSONAL_IOS_PACKAGE, 
		/**
		 * 生成项目——IOS——正式包
		 */
		APP_ADD_PROJECT_IOS_PACKAGE,
		/**
		 * 生成个人——IOS——测试包
		 */
		APP_ADD_PERSONAL_IOS_TEST_PACKAGE, 
		/**
		 * 生成项目——IOS——测试包
		 */
		APP_ADD_PROJECT_IOS_TEST_PACKAGE, 
		/**
		 * 生成个人——ANDROID——正式包
		 */
		APP_ADD_PERSONAL_ANDROID_PACKAGE, 
		/**
		 * 生成项目——ANDROID——正式包
		 */
		APP_ADD_PROJECT_ANDROID_PACKAGE,
		/**
		 * 生成个人——ANDROID——测试包
		 */
		APP_ADD_PERSONAL_ANDROID_TEST_PACKAGE, 
		/**
		 * 生成项目——ANDROID——测试包
		 */
		APP_ADD_PROJECT_ANDROID_TEST_PACKAGE,
		/**
		 * 删除ios正式包
		 */
		APP_DELETE__IOS_PACKAGE,
		/**
		 * 删除ios测试包
		 */
		APP_DELETE__IOS_TEST_PACKAGE,
		/**
		 * 删除android正式包
		 */
		APP_DELETE__ANDROID_PACKAGE,
		/**
		 * 删除android测试包
		 */
		APP_DELETE__ANDROID_TEST_PACKAGE,
		/**
		 * 发布IOS正式包
		 */
		APP_PUBLISH_IOS_PACKAGE,
		/**
		 * 发布android正式包
		 */
		APP_PUBLISH_ANDROID_PACKAGE,
		/**
		 * 发布IOS测试包
		 */
		APP_PUBLISH_IOS_TEST_PACKAGE,
		/**
		 * 发布ANDROID测试包
		 */
		APP_PUBLISH_ANDROID_TEST_PACKAGE,
		/**
		 * 发布widget测试包
		 */
		APP_PUBLISH_TEST_WIDGET_PACKAGE,
		/**
		 * 发布widget正式包
		 */
		APP_PUBLISH_WIDGET_PACKAGE,
		
		
		/**
		 * 创建团队
		 */
		TEAM_ADD,
		/**
		 * 修改团队
		 */
		TEAM_EDIT,
		/**
		 * 解散团队
		 */
		TEAM_EXPIRE,
		/**
		 * 申请团队企业授权
		 */
		TEAM_ASK_BIND,
		/**
		 * 取消团队企业绑定申请
		 */
		TEAM_CANCEL_BIND,
		/**
		 * 团队绑定企业
		 */
		TEAM_BIND_ENTERPRISE,
		/**
		 * 团队解绑企业
		 */
		TEAM_UNBIND_ENTERPRISE,
		/**
		 * 团队申请解绑企业
		 */
		TEAM_ASK_UNBIND,
		/**
		 * 团队取消解绑企业
		 */
		TEAM_CANCEL_UNBIND,
		/**
		 * 创建团队项目
		 */
		TEAM_CREATE_PRJ,
		/**
		 * 团队邀请成员
		 */
		TEAM_ASKUSER,
		/**
		 * 团队移除成员
		 */
		TEAM_REMOVE_MEMBER,
		/**
		 * 团队移除成员
		 */
		TEAM_EXIT,
		/**
		 * 改变团队成员所属小组
		 */
		TEAM_CHANGE_GROUP,
		/**
		 * 创建团队小组
		 */
		TEAMGROUP_ADD,
		/**
		 * 删除团队小组
		 */
		TEAMGROUP_DEL,
		/**
		 * 更改成员团队权限
		 */
		TEAMAUTH_EDIT,
		
		
		/**
		 * 创建讨论
		 */
		TOPIC_CREATE,
		/**
		 * 创建评论
		 */
		TOPIC_COMMENT_CREATE,
		/**
		 * 添加讨论成员
		 */
		TOPIC_COMMENT_ADD,
		/**
		 * 回复某人的评论
		 */
		TOPIC_COMMENT_REPLY,
		/**
		 * 删除评论
		 */
		TOPIC_COMMENT_DELETE,
		/**
		 * 更新讨论
		 */
		TOPIC_UPDATE,
		/**
		 * 删除讨论
		 */
		TOPIC_DELETE,
		/**
		 * 移除讨论参与者
		 */
		TOPIC_MEMBER_DELETE, 
		/**
		 * 添加讨论参与者
		 */
		TOPIC_MEMBER_ADD, 
		/**
		 * 添加讨论评论资源
		 */
		COMMENT_ADD_RESOURCE, 
		/**
		 * 删除讨论资源
		 */
		COMMENT_REMOVE_RESOURCE,
		
		
		/**
		 * 创建文档
		 */
		DOCUMENT_CREATE,
		/**
		 * 更新文档
		 */
		DOCUMENT_UPDATE,
		/**
		 * 删除文档
		 */
		DOCUMENT_DELETE, 
		/**
		 * 发布文档
		 */
		DOCUMENT_PUBLISH,
		/**
		 * 回收文档
		 */
		DOCUMENT_RETRIEVED,
		/**
		 * 导入文档
		 */
		DOCUMENT_IMPORT_CREATE, 
		/**
		 * 创建文档章节
		 */
		DOCUMENTCHAPTER_CREATE, 
		/**
		 * 更新文档章节
		 */
		DOCUMENTCHAPTER_UPDATE, 
		/**
		 * 删除文档章节
		 */
		DOCUMENTCHAPTER_DELETE, 
		/**
		 * 发布文档章节
		 */
		DOCUMENTCHAPTER_PUBLISH, 
		/**
		 * 回收文档章节
		 */
		DOCUMENTCHAPTER_RETRIEVED, 
		/**
		 * 添加文档批注
		 */
		DOCUMENT_MARKER, 
		/**
		 * 删除文档批注
		 */
		DOCUMENT_MARKER_DELETE,
		/**
		 * 文档排序
		 */
		DOCUMENT_SORT,
		/**
		 * 文档导出
		 */
		DOCUMENT_EXPORT,
		
		
		/**
		 * 创建资源
		 */
		RESOURCE_ADD,
		/**
		 * 转移资源
		 */
		RESOURCE_TRANSFER,
		/**
		 * 删除资源
		 */
		RESOURCE_DEL,
		/**
		 * 公开资源
		 */
		RESOURCE_OPEN,
		/**
		 * 关闭资源
		 */
		RESOURCE_CLOSE,
		/**
		 * 创建资源文件夹
		 */
		RESOURCE_ADD_DIR,
		/**
		 * 删除资源文件夹
		 */
		RESOURCE_DELETE_DIR,
		
		
		/**
		 * 创建流程
		 */
		PROCESS_CREATE, 
		/**
		 * 更新流程
		 */
		PROCESS_UPDATE,
		/**
		 * 删除流程
		 */
		PROCESS_DELETE, 
		/**
		 * 流程添加资源
		 */
		PROCESS_ADD_RESOURCE, 
		/**
		 * 删除流程资源
		 */
		PROCESS_REMOVE_RESOURCE, 
		/**
		 * 修改资源名称
		 */
		PROCESS_RENAME_RESOURCE, 
		
		/**
		 * 更新任务
		 */
		TASK_UPDATE, 
		/**
		 * 创建任务
		 */
		TASK_CREATE, 
		/**
		 * 删除任务
		 */
		TASK_DELETE,
		/**
		 * 完成任务
		 */
		TASK_FINISH, 
		/**
		 * 驳回任务
		 */
		TASK_REJECT, 
		/**
		 * 搁置任务
		 */
		TASK_SUSPENDED, 
		/**
		 * 添加任务成员
		 */
		TASK_ADD_MEMBER, 
		/**
		 *	移除任务成员
		 */
		TASK_REMOVE_MEMBER,
		/**
		 * 任务变更负责人
		 */
		TASK_CHANGE_MANAGER,
		/**
		 * 添加任务标签
		 */
		TASK_ADD_TAG,
		/**
		 * 任务删除标签
		 */
		TASK_REMOVE_TAG,
		/**
		 * 任务添加评论
		 */
		TASK_ADD_COMMENT,
		/**
		 * 添加任务资源
		 */
		TASK_ADD_RESOURCE, 
		/**
		 * 添加任务评论资源
		 */
		TASK_COMMENT_ADD_RESOURCE, 
		/**
		 * 删除任务资源
		 */
		TASK_REMOVE_RESOURCE, 
		/**
		 * 删除任务评论资源
		 */
		TASK_COMMENT_REMOVE_RESOURCE, 
		
		/**
		 * 添加子任务
		 * 姓名 + 在 + 任务描述 + 中添加了子任务
		 */
		TASK_LEAF_ADD,
		
		/**
		 * 修改子任务描述
		 * 姓名 + 修改了子任务
		 */
		TASK_LEAF_UPDATE,
		
		/**
		 * 完成了子任务
		 * 姓名  完成了子任务
		 */
		TASK_LEAF_FINISHED,
		
		/**
		 * 驳回了子任务
		 * 姓名  驳回了子任务
		 */
		TASK_LEAF_UNFINISHED,
		
		/**
		 * TASK_LEAF
		 * 姓名 + 将子任务 + 子任务描述 + 的负责人修改为 + 姓名
		 */
		TASK_LEAF_CHANGE_MANAGER,
		/**
		 * 删除子任务
		 * 姓名 + 删除了子任务  + 子任务描述
		 */
		TASK_LEAF_REMOVE,
		/**
		 * 子任务转换为任务
		 * 姓名 + 将子任务  + 子任务描述 +转化为任务
		 */
		TASK_LEAF_UPGRADE,
		/**
		 * 创建任务分组
		 * 姓名 + 添加了任务分组 + 任务分组名称（只在项目动态中显示）
		 */
		TASK_GROUP_ADD,
		/**
		 * 删除任务分组
		 * 姓名 + 删除了任务分组 + 任务分组名称（该分组下无任务）（只在项目动态中显示）
		 */
		TASK_GROUP_REMOVE,
		/**
		 * 删除任务分组,并将原先分组下的任务转移到另一个任务分组里
		 * 姓名 + 删除了任务分组 + 任务分组名称，且将该组下的任务转移至 + 任务分组 + 中（只在项目动态中显示）
		 */
		TASK_GROUP_REMOVE_AND_TRANSFER,
		
		/**
		 * 上传ios新引擎
		 */
		ENGINE_UPLOAD_IOS,
		/**
		 * 上传android新引擎
		 */
		ENGINE_UPLOAD_ANDROID,
		/**
		 * 启用ios引擎
		 */
		ENGINE_ENABLE_IOS,
		/**
		 * 启用android引擎
		 */
		ENGINE_ENABLE_ANDROID,
		/**
		 * 禁用android引擎
		 */
		ENGINE_DISABLE_ANDROID,
		/**
		 * 禁用IOS引擎
		 */
		ENGINE_DISABLE_IOS,
		/**
		 * 修改引擎
		 */
		ENGINE_EDIT, 
		/**
		 * 删除IOS引擎
		 */
		ENGINE_REMOVE_IOS, 
		/**
		 * 删除android引擎
		 */
		ENGINE_REMOVE_ANDROID, 
		/**
		 * 上传新插件
		 */
		PLUGIN_UPLOAD,
		/**
		 * 编辑插件
		 */
		PLUGIN_EDIT,
		/**
		 * 删除插件
		 */
		PLUGIN_DELETE,
		/**
		 * 添加ios插件版本
		 */
		PLUGIN_ADD_IOS_VERSION, 
		/**
		 * 添加ANDROID插件版本
		 * 
		 */
		PLUGIN_ADD_ANDROID_VERSION, 
		/**
		 * 更新插件版本
		 */
		PLUGIN_VERSION_UPDATE,
		/**
		 * 启用插件版本
		 */
		PLUGIN_VERSION_ENABLE,
		/**
		 * 禁用插件版本
		 */
		PLUGIN_VERSION_DISABLE, 
		/**
		 * 删除ios插件版本
		 */
		PLUGIN_DELETE_IOS_VERSION, 
		/**
		 * 删除android插件版本
		 */
		PLUGIN_DELETE_ANDROID_VERSION, 
		/**
		 * 更新IOS插件版本
		 */
		PLUGIN_EDIT_IOS_VERSION, 
		/**
		 * 更新android插件版本
		 */
		PLUGIN_EDIT_ANDROID_VERSION, 
		/**
		 * 启用IOS插件版本
		 */
		PLUGIN_ENABLE_IOS_VERSION, 
		/**
		 * 启用ANDROID插件版本
		 */
		PLUGIN_ENABLE_ANDROID_VERSION, 
		/**
		 * 禁用IOS插件版本
		 */
		PLUGIN_DISABLE_IOS_VERSION, 
		/**
		 * 禁用IOS插件版本
		 */
		PLUGIN_DISABLE_ANDROID_VERSION, 
		/**
		 * 创建bug
		 */
		BUG_CREATE,
		/**
		 * 解决bug
		 */
		BUG_SOLVE,
		/**
		 * 关闭bug
		 */
		BUG_CLOSE,
		/**
		 * 激活bug
		 */
		BUG_ACTIVE,
		/**
		 * 创建bug模块
		 */
		BUG_MODULE_CREATE,
		/**
		 * 创建删除了模块
		 */
		BUG_MODULE_DELETE,
		/**
		 * 更新bug
		 */
		BUG_UPDATE,
		/**
		 * 更新bug模块
		 */
		BUG_UPDATE_TITLE,
		/**
		 * 更新bug模块标题
		 */
		BUG_UPDATE_DETAIL,
		/**
		 * 更新bug模块描述
		 */
		BUG_MODULE_UPDATE,
		/**
		 * 修改bug指派人
		 */
		BUG_CHANGE_ASSIGNEDPERSON, 
		/**
		 * 移除bug成员
		 */
		BUG_REMOVE_MEMBER,
		/**
		 * 新增bug成员
		 */
		BUG_ADD_MEMBER,
		/**
		 * 复制bug
		 */
		BUG_COPY, 
		/**
		 * bug添加资源
		 */
		BUG_ADD_RESOURCE, 
		/**
		 * bug添加备注
		 */
		BUG_ADD_MARK,
		/**
		 * 创建bug模块增加负责人
		 */
		BUG_MODULE_ADD_MANAGER,
		/**
		 * bug模块修改复制人
		 */
		BUG_MODULE_UPDATE_MANAGER,
		/**
		 * trans数模申请
		 */
		TRANS_DATAMODEL_ADD,
		/**
		 * trans数模审批
		 */
		TRANS_DATAMODEL_APPROVAL,
		/**
		 * trans数模修改
		 */
		TRANS_DATAMODEL_UPDATE,
		/**
		 * trans接口申请
		 */
		TRANS_INTEGERFACE_ADD,
		/**
		 * trans接口修改
		 */
		TRANS_INTEGERFACE_UPDATE,
		/**
		 * trans接口审批
		 */
		TRANS_INTEGERFACE_APPROVAL,
		/**
		 * trans项目申请
		 */
		TRANS_PROJECT_ADD,
		/**
		 * trans项目修改
		 */
		TRANS_PROJECT_UPDATE,
		/**
		 * trans项目审批
		 */
		TRANS_PROJECT_APPROVAL,
		/**
		 * trans应用申请
		 */
		TRANS_APP_ADD,
		/**
		 * trans应用修改
		 */
		TRANS_APP_UPDATE,
		/**
		 * trans应用审批
		 */
		TRANS_APP_APPROVAL,
		/**
		 * trans发版申请
		 */
		TRANS_PUBLISH_ADD,
		/**
		 * 生成IOS包
		 */
		TRANS_APP_IOS_PACKAGE_ADD,
		/**
		 * 生成ANDROID包
		 */
		TRANS_APP_ANDROID_PACKAGE_ADD,
		/**
		 * 删除ANDROID包
		 */
		TRANS_APP_ANDROID_PACKAGE_DEL,
		/**
		 * 删除IOS包
		 */
		TRANS_APP_IOS_PACKAGE_DEL,
		/**
		 * 生成 版本补丁包
		 */
		TRANS_APP_PACKAGE_PATCH_ADD
	}
	/**
	 * 
	 * @describe 通知模板类型	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月21日 上午11:21:06	<br>
	 *
	 */
	public enum NOTICE_MODULE_TYPE{
		
		PROJECT_ADD_MEMBER,//xxx（创建者或管理员） 把你加入了xx（项目名称）项目；
		PROJECT_REMOVE_MEMBER,//xxx（创建者或管理员） 把你移除了xx（项目名称）项目；
		PROJECT_UPDATE_LEADER,//xxx（创建者或管理员） 把你设为了 xx（项目名称）项目的管理员；
		PROJECT_UPDATE_MEMBER,//xxx（创建者或管理员） 把你设为了 xx（项目名称）项目的成员；
		PROJECT_TRANSFER,//xxx（创建者）把 xx（项目名称）项目转移给了你；
		PROJECT_DELETE,//xxx（创建者）删除了 xx（项目名称）项目，你已经不是该项目的成员；
		PROJECT_UPDATE_NAME,//xxx（创建者或管理者）将xx（项目名称）的项目名称修改为xx（项目名称）；
		PROJECT_ALLOW_AUTHORIZED,//你（创建者或管理员）的xx（项目名称）项目已成功被xx（企业名称）企业授权；
		PROJECT_REFUSE_AUTHORIZED,//你（创建者或管理员）的xx（项目名称）项目被xx（企业名称）企业拒绝授权；
		TOPIC_REPLY_COMMENT,//xxx（讨论的参与者）在xxxxxxxxxxxxxxxxxxxxxx（讨论标题）中@（回复）了你；
		TOPIC_ADD_MEMBER,//xxx（所有项目成员）邀你加入xxxxxxxxxxxxxxxxxxxxxx（讨论标题）话题讨论；
		TOPIC_REMOVE_MEMBER,//xxx（讨论创建者）把你移除了xxxxxxxxxxxxxxxxxxxxxx（讨论标题）的讨论；

		TEAM_ASKUSER,//xxx	（创建者或管理员） 把你加入了xx（团队名称）团队；
		TEAM_REMOVE_MEMBER,//xxx（创建者或管理员） 把你移除了xx（团队名称）团队；
		TEAM_AUTH,//xxx（创建者或管理员） 把你设为了 xx（团队名称）团队的管理员/成员；
		TEAM_TRANSFER,//xxx（创建者）把 xx（团队名称）团队转移给了你；
		TEAM_REMOVE,//xxx（创建者）解散了 xx（团队名称）团队，你已经不是该团队的成员；
		TEAM_ADD_PROJECT,//xxx（创建者或管理员） 创建了一个团队项目：xx（项目名称），你有权管理该项目；
		TEAM_UPDATE,//xxx（创建者或管理者）将xx（团队名称）的团队名称修改为xx（团队名称）；
		TEAM_BIND_ENTERPRISE,//团队授权通过
		TEAM_UNBIND_ENTERPRISE, //拒绝绑定
		TEAM_UNAGREE_UNBIND_ENTERPRISE,//拒绝解绑
		TEAM_AGREE_UNBIND_ENTERPRISE,//同意解绑
		/*TASK_ADD_LEADER,			//xxx（所有项目成员）给你（任务负责人）分配了任务：xxxxxxxxxxxxxxxxxxxxxx（任务标题）;
		TASK_UPDATE_LEADER,			//xxx（任务的创建者或任务的负责人）把你设为了xxxxxxxxxxxxxxxxxxxx（任务标题）的负责人；
		TASK_ADD_MEMBER,			//xxx（任务的创建者或任务的负责人）邀你（任务参与者）参与xxxxxxxxxxxxxxxxxxxxxx（任务标题）；
		TASK_REMOVE_MEMBER,  		//xxx（任务的创建者）已把你（任务负责人或参与人）从xxxxxxxxxxxxxxxxxxxxxx（任务标题）中删除；
		TASK_DATE_WARNING,			//xxx（所有项目成员）给你（任务负责人）分配的任务xxxxxxxxxxxxxxxxxxxxxx（任务标题）即将过期，请尽快处理；
		TASK_OVERDUE,				//xxx（所有项目成员）给你（任务负责人）分配的任务xxxxxxxxxxxxxxxxxxxxxx（任务标题）已过期；  
		TASK_DELETE,				//xxx（任务的创建者）删除了xxxxxxxxxxxxxxxxxxxxxx（任务标题），你已不用完成该任务；
		TASK_CLOSE,					//xxx（任务的创建者）关闭了xxxxxxxxxxxxxxxxxxxxxx（任务标题），你的任务已完成；
		TASK_SUSPENDED,				//xxx（任务的创建者）搁置了xxxxxxxxxxxxxxxxxxxxxx（任务标题），你已不用完成该任务；
		TASK_FINISHED,				//xxx（任务负责人或参与者）完成了xxxxxxxxxxxxxxxxxxxxxx（任务标题），你（任务创建者）需要确认任务完成情况；
		TASK_UPDATE, 				//xxx（任务的创建者）修改了xxxxxxxxxxxxxxxxxxxxxx（任务标题）；
		TASK_REJECT,				//xxx（任务负责人或参与者）驳回了xxxxxxxxxxxxxxxxxxxxxx（任务标题）；
		TASK_UPDATE_NAME, 			//xxx（任务的创建者）将任务的标题xxxxxxxxxxxxxxxxxxxxxx（旧任务标题）修改为了xxxxxxxxxxxx（新任务标题）；
		TASK_ADD_LEADER_WARNNING,	//xxx（所有项目成员）给你（任务负责人）分配了任务：xxxxxxxxxxxxxxxxxxxxxx（任务标题）,并且该任务即将过期，请你及时处理
*/
//		<span>%s</span>给你分配了任务：<span>%s</span>，请知晓。	
		TASK_ADD_LEADER,
//		<span>%s</span>给你分配了任务<span>%s</span>,并且该任务即将过期，请你及时处理。
		TASK_ADD_LEADER_WARNNING,
//		<span>%s</span>把你设为了<span>%s</span>的负责人，请知晓。
		TASK_UPDATE_LEADER,
//		<span>%s</span>邀你参与任务：<span>%s</span>，请知晓。
		TASK_ADD_MEMBER,
//		<span>%s</span>邀你参与任务：<span>%s</span>，并且该任务即将过期，请你及时处理。	
		TASK_ADD_MEMBER_WARNNING,
//		<span>%s</span>给你分配的任务<span>%s</span>即将过期，请尽快处理。	
		TASK_DATE_WARNING,
//		<span>%s</span>给你分配的任务<span>%s</span>已过期，请知晓。
		TASK_OVERDUE,
//		<span>%s</span>删除了<span>%s</span>，你已不用完成该任务。	
		TASK_DELETE,
//		<span>%s</span>删除了您创建的任务：<span>%s</span>，请知晓。
		TASK_DELETE_TO_CREATOR,
//		<span>%s</span>把任务<span>%s</span>的描述改为了<span>%s</span>，请知晓。
		TASK_UPDATE,
//		<span>%s</span>将任务的标题<span>%s</span>修改为<span>%s</span>，请知晓。
		TASK_UPDATE_NAME,
//		<span>%s</span>关闭了<span>%s</span>，你的任务已完成。
		TASK_CLOSE,
//		<span>%s</span>搁置了<span>%s</span>，你已不用完成该任务。	
		TASK_SUSPENDED,
//		<span>%s</span>完成了<span>%s</span>，您需要确认任务完成情况。	
		TASK_FINISHED_TO_CREATOR,
//		<span>%s</span>已把你从<span>%s</span>中移除。	
		TASK_REMOVE_MEMBER,
//		<span>%s</span>完成了<span>%s</span>，您的任务已完成。	
		TASK_FINISHED_TO_MEMBER,
//		<span>%s</span>完成了<span>%s</span>，现任务已延期，您需要尽快确认任务完成情况。	
		TASK_FINISHED_OVERDUE,
//		<span>%s</span>完成了<span>%s</span>，现任务即将延期，您需要尽快确认任务完成情况。	
		TASK_FINISHED_WARNNING,
//		<span>%s</span>驳回了您创建的任务：<span>%s</span>，请知晓。
		TASK_REJECT,
//		<span>%s</span>驳回了您创建的任务：<span>%s</span>，现任务已延期，请知晓。	
		TASK_REJECT_OVERDUE,
//		<span>%s</span>驳回了您创建的任务：<span>%s</span>，现任务即将延期，请知晓。	
		TASK_REJECT_WARNNING,
//		<span>%s</span>驳回了<span>%s</span>，您需要重新完成此任务。	
		TASK_CREATOR_REJECT_LEADER,
//		<span>%s</span>驳回了<span>%s</span>，您需要重新配合完成此任务。	
		TASK_CREATOR_REJECT_MEMBER,
//		<span>%s</span>驳回了<span>%s</span>，您需要尽快重新完成此任务。	
		TASK_CREATOR_REJECT_EMG_LEADER,
//		<span>%s</span>驳回了<span>%s</span>，您需要尽快重新配合完成此任务。	
		TASK_CREATOR_REJECT_EMG_MEMBER,
//		<span>%s</span>搁置了您创建的任务：<span>%s</span>，请知晓。	
		TASK_SUSPENDED_TO_CREATOR,
//		<span>%s</span>关闭了您创建的任务：<span>%s</span>，请知晓。	
		TASK_CLOSE_TO_CREATOR,

		PROCESS_ADD_LEADER,//xxx（创建者或管理员）把你设为了 xx（流程名称）流程的管理员/成员；
		PROCESS_ADD_MEMBER,// xxx（创建者或管理员）把你加入了xx（流程阶段名称）流程阶段；
		PROCESS_UPDATE,//xxx（创建者或管理者）将xx（流程阶段名称）流程阶段修改为xx（项目名称）流程阶段；
		PROCESS_DELETE, //xxx（创建者或管理员）删除了xx（流程阶段名称）流程阶段，你已经不是该流程阶段的参与者（但你仍是该流程所属项目成员）； 
		DOCUMENT_MARKER,  //xxx在你的文档xxx>xxx中添加了批注xxx;
		TEAM_QUIT, //xxx退出了你的团队xxx>xxx;
		PROJECT_QUIT,     //xxx退出了你的项目xxx>xxx;
	    BUG_ADD_MANAGER,//		<span>%s</span>给你分配了BUG：<span>%s</span>，请知晓。	
		BUG_ADD_MEMBER,	//		<span>%s</span>邀你参与BUG：<span>%s</span>，请知晓。
		BUG_MODULE_ADD_MANAGER,//<span>%s</span>把你设置为BUG模块：<span>%s</span>的负责人，请知晓。
		BUG_MODULE_DELETE_TO_MANAGER,//<span>%s</span>删除了BUG模块：<span>%s</span>，你已不用完成该模块BUG。
		BUG_MODULE_DELETE_TO_CREATOR,//<span>%s</span>删除了您创建的bug模块：<span>%s</span>，请知晓。
		BUG_CLOSE_TO_CREATOR,//<span>%s</span>关闭了您创建的bug：<span>%s</span>，请知晓。
		BUG_CLOSE_TO_MEMBER,//<span>%s</span>关闭了您参与的bug：<span>%s</span>，请知晓。
		BUG_CLOSE_TO_ASSIGNEDPERSON,//<span>%s</span>关闭了您负责的bug：<span>%s</span>，请知晓。
		BUG_ACTIVE_TO_CREATOR,//<span>%s</span>激活了您创建的bug：<span>%s</span>，请知晓。
		BUG_ACTIVE_TO_MEMBER,//<span>%s</span>激活了您参与的bug：<span>%s</span>，请知晓。
		BUG_ACTIVE_TO_ASSIGNEDPERSON,//<span>%s</span>激活了您负责的bug：<span>%s</span>，请知晓。
		BUG_SOLVE_TO_CREATOR,//<span>%s</span>解决了您创建的bug：<span>%s</span>，解决方式：<span>%s</span>,请知晓。
		BUG_SOLVE_TO_MEMBER,//<span>%s</span>解决了您参与的bug：<span>%s</span>，解决方式：<span>%s</span>,请知晓。
		BUG_SOLVE_TO_ASSIGNEDPERSON,//<span>%s</span>解决了您负责的bug：<span>%s</span>，解决方式：<span>%s</span>,请知晓。
		BUG_MODULE_UPDATE_TO_CREATOR,//<span>%s</span>将您创建bug模块：<span>%s</span>的负责人：<span>%s</span>,修改为：<span>%s</span>,请知晓。
		BUG_MODULE_UPDATE_TO_MANAGER,//<span>%s</span>将bug模块:<span>%s</span>的负责人：<span>%s</span>改成您,请知晓。
		BUG_MODULE_UPDATE_TO_OLDMANAGER,//<span>%s</span>将您负责的bug模块:<span>%s</span>的负责人改成:<span>%s</span>,请知晓。
	    BUG_REMOVE_MEMBER, BUG_UPDATE_ASSIGNEDPERSON,
	    
	    //三期新加
	    /**
	     * TASK_ADD_TO_LEADER
	     * 张影给您分配了任务：【张影创建的任务】，任务截止时间为：2016年3月25号，请知晓
	     */
	    TASK_ADD_TO_LEADER,
	    /**
	     * hi,盼盼
	     * 莉红评论了：【武晶建创建的任务】，请知晓
	     * 祝使用愉快！
	     */
	    TASK_COMMENT,
	    /**
	     * TASK_ADD_TO_MEMBER,
	     * 张影邀您参与任务：【张影创建的任务】，任务截止时间为：2016年3月25号，请知晓。
	     */
	    TASK_ADD_TO_MEMBER,
	    /**
	     * TASK_LEAF_ADD_TO_LEADER
	     * 张影给您分配了子任务：【张影创建的子任务】，任务截止时间为：2016年3月25号，请知晓。
	     */
	    TASK_LEAF_ADD_TO_LEADER,
	    /**
	     * TASK_REMOVE_TO_MEMBER
	     * 张影已经将您从【张影创建的子任务】中移除，您已不需要关注此任务
	     */
	    TASK_REMOVE_TO_MEMBER,
	    /**
	     * 盼盼（或婷婷或武晶建）完成了【张影创建的任务/子任务】，您需要确认任务完成情况
	     */
	    TASK_LEAF_FINISHED_TO_CREATOR,
	    /**
	     * 盼盼（或婷婷或武晶建）完成了【张影创建的任务/子任务】，现任务即将延期,您需要确认任务完成情况
	     */
	    TASK_LEAF_FINISHED_TO_CREATOR_WARNING,
	    /**
	     * 盼盼（或婷婷或武晶建）完成了【张影创建的任务/子任务】，现任务已延期,您需要确认任务完成情况
	     */
	    TASK_LEAF_FINISHED_TO_CREATOR_OVERDUE,
	    /**
	     * 任务完成后又被改为未完成状态：
			xxx（任务的创建者）修改了【xxxxxxxxxxxxxxxxxxxxxx（任务描述.....）】任务状态，您需要重新完成此任务。
	     */
	    TASK_UNFINISHED,
	    /**
	     * 您创建的【张影创建的任务/子任务】即将延期（或已延期），请知晓。
	     */
	    TASK_WARNING_TO_CREATOR,
	    
	    /**
	     * 您创建的【张影创建的任务/子任务】已延期，请知晓。
	     */
	    TASK_OVERDUE_TO_CREATOR,
	    
	    /**
	     * 您的任务【张影创建的任务】即将延期,您需要尽快完成此任务。
	     */
	    TASK_WARNING_TO_PARTICIPATOR,
	    /**
	     * 您的任务【张影创建的任务】已延期，您需要尽快完成此任务。
	     */
	    TASK_OVERDUE_TO_PARTICIPATOR,
	    /**
	     * 项目授权通过
	     */ 
		PROJECT_BIND_ENTERPRISE,
	    /**
	     * 项目拒绝绑定
	     */ 
		PROJECT_UNBIND_ENTERPRISE,
	    /**
	     * 项目同意解绑
	     */ 
		PROJECT_AGREE_UNBIND_ENTERPRISE,
	    /**
	     * 项目拒绝解绑
	     */ 
		PROJECT_UNAGREE_UNBIND_ENTERPRISE,
		/**项目导出**/
		PROJECT_EXPORT,
		/**项目导入**/
		PROJECT_IMPORT,
		
		/**数模审核通过**/
		TRANS_DATAMODEL_APPROVALFINSH,
		/**数模审核失败
		TRANS_DATAMODEL_REFUSE,**/
		
		/**接口审核通过**/
		TRANS_INTEGERFACE_APPROVALFINSH,
		/**接口审核失败
		TRANS_INTEGERFACE_REFUSE,**/
		/**应用审核通过**/
		TRANS_APP_APPROVALFINSH,
		/**应用审核失败
		TRANS_APP_REFUSE,**/
		/**项目审核通过**/
		TRANS_PROJECT_APPROVALFINSH,
		/**发版审核通过**/
		TRANS_PUBLISH_APPROVALFINSH,
		/**项目审核失败
		TRANS_PROJECT_REFUSE,**/
		/**申请接口**/
		TRANS_INTEGERFACE_ADD,
		/**申请数模**/
		TRANS_DATAMODEL_ADD,
		/**申请子项目**/
		TRANS_PROJECT_ADD,
		/**申请应用**/
		TRANS_APP_ADD,
		/**申请发版**/
		TRANS_PUBLISH_ADD,
		/**子项目创建**/
		TRANS_PROJECT_GREATE,
		/**应用创建**/
		TRANS_APP_GREATE,
		;
	}
	/**
	 * 查询资源列表时候,我创建的CREATE,我参与的ACTOR
     * @Description: 
     * @author jingjian.wu
     * @date 2015年8月20日 下午2:22:50
     *
	 */
	public enum RESOURCE_TYPE{
		CREATE,ACTOR;
	}
	
	public enum SOURCE_TYPE{
		NORMAL,PROCESS,TASK,TOPIC,BUG,PROJECT,DATAMODEL,INTERFACE,APP;
	}
	
	/**
	 * 		角色类型
	     * @Description: 
	     * CREATOR		      创建者
	     * ADMINISTRATOR  管理员
	     * MANAGER  	      负责人
	     * MEMBER		      普通成员
	     * OBSERVER		      观察者
	     * @author jingjian.wu
	     * @date 2015年8月25日 上午10:03:45
	     *
	 */
	public enum ROLE_TYPE{
		CREATOR, ADMINISTRATOR, MANAGER, MEMBER,OBSERVER,ASSIGNEDPERSON;
	}
	
	/**
	 * 数据操作类型，用于标识数据操作许可
	 * @author yang.li
	 * @date 2015-08-25
	 *
	 */
	public enum CRUD_TYPE {
		//Create)、读取(Retrieve)（重新得到数据）、更新(Update)和删除(Delete)
		CREATE, RETRIEVE, EDIT, REMOVE, REMOVE_FILE, REMOVE_DIR
	}

	
	/**
	 * 应用版本类型<br>
	 * PROJECT: 项目版本<br>
	 * PERSONAL: 个人版本<br>
	 * PERSONAL: 申请发布过的版本<br>
	 * @author yang.li
	 * @date 2015-09-11
	 *
	 */
	public enum AppVersionType {
		PROJECT, PERSONAL,PUBLISH
	}
	
	/**
	 * 全量版本 FULL
	 * 补丁版本PATCH
	     * @author jingjian.wu
	     * @date 2015年10月30日 下午12:28:55
	 */
	public enum AppVersionPatchOrFull {
		FULL,PATCH
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
	 * App在EMM平台的类型<br>
	 * PERSONAL: 个人应用<br>
	 * ENTERPRISE: 企业应用<br>
	 * @author yang.li
	 *
	 */
	public enum EMMAppSource {
		PERSONAL, COMPANY
	}
	
	/**
	 * 终端类型
	 * @author yang.li
	 * @date 2015-09-11
	 */
	public enum TerminalType {
		IPHONE, IPAD, IPHONE_IPAD, ANDROID
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
	 * @describe 插件状态	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月7日 下午6:43:31	<br>
	 *	DISABLE --> 1   禁用
	 *	ENABLE --> 0  启用
	 */
	public enum  PluginVersionStatus{
		ENABLE, DISABLE
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
	 * @describe 引擎状态	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月7日 下午6:43:31	<br>
	 *	DISABLE --> 1   禁用
	 *	ENABLE --> 0  启用
	 */
	public enum  EngineStatus{
		ENABLE, DISABLE
	}
	
	public enum UploadStatus {
		ONGOING, SUCCESS, FAILED 
	}
	
	/**
	 * 是与否状态
	 * @author liyang
	 * @date 2015-09-19
	 *
	 */
	public enum IfStatus {
		NO, YES
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
	
	/**
	 * 
	 * @describe 邮件服务器类型	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月23日 下午3:47:37	<br>
	 *
	 */
	public enum EMAIL_SERVER_TYPE{
		POP3,SMTP,IMAP,EXCHANGE
	}
	
	/**
	 * 
	 * @describe 邮箱开启状态	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月9日 上午10:58:25	<br>
	 *
	 */
	public enum EMAIL_STATUS{
		OPEN,CLOSE
	}
	
	public enum AUTH_STATUS{
		EFFECTIVE,OFNOAVAIL
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
	 * 流程模板状态
	 * @author yang.li
	 *
	 */
	public enum ProcessTemplateStatus {
		ENABLE, DISABLE
	}
	
	/**
	 * @describe 流程状态	<br>
	 * NORMAL --> 正常
	 * DELAY --> 即将延期
	 * OVERDUE --> 已经延期
	 * @author jiexiong.liu	<br>
	 * @date 2015年10月27日 上午11:03:41	<br>
	 */
	public enum PROCESS_STATUS{
		NORMAL,DELAY,OVERDUE
	}
	
	/**
	 * 补丁包类型
	 * @describe 	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年11月13日 上午11:58:34	<br>
	 *
	 */
	public enum PATCH_TYPE{
		AppCanNative,AppCanWgt
	}
	
	/**
	 * @describe 资源预览转换状态	<br>
	 * ONGOING：转换中<br>
	 * SUCCEED：完成<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年12月24日 下午3:26:44	<br>
	 *
	 */
	public enum CONVERT_STATUS{
		ONGOING,SUCCEED
	}
	
	/**
	 * @describe 初始化实例项目、应用等的状态	<br>
	 * ONGOING：初始化中<br>
	 * SUCCEED：完成<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年12月24日 下午3:26:44	<br>
	 *
	 */
	public enum INIT_DEMO_STATUS{
		ONGOING,SUCCEED
	}
	/**
	 * bug解决方案
	 * 
	 * BYDESIGN 设计如此
	 * DUPLICATE 重复bug
	 * NOTREPRO 无法复现
	 * FIXED 已修复
	 * EXTERNAL 外部原因
	 * POSTPONED 发现的太晚了，下一个版本讨论是否解决
	 * NOTFIX 是个问题，但是不值得修复
	 * @author yongwen.wang
	 * @date 2016-04-20
	 *
	 */
	public enum BUG_SOLUTION {
		BYDESIGN,DUPLICATE,NOTREPRO,FIXED,EXTERNAL,POSTPONED,NOTFIX
	}
	/**
	 * bug状态
	 * 
	 * NOTFIX 未解决
	 * FIXED 已解决
	 * CLOSED 已关闭
	 * @author yongwen.wang
	 * @date 2016-04-20
	 *
	 */
	public enum BUG_STATUS {
		NOTFIX,FIXED,CLOSED
	}
	/**
	 * bug优先级
	 * 
	 * NORMAL 普通
	 * URGENT 紧急
	 * VERY_URGENT 非常紧急
	 * @author yongwen.wang
	 * @date 2016-04-20
	 *
	 */
	public enum BUG_PRIORITY {
		NORMAL, URGENT, VERY_URGENT
	}
	/**
	 * bug成员类型
	 * 
	 * CREATOR 创建者
	 * PARTICIPATOR 参与人包括指派者和参与人
	 * @author yongwen.wang
	 * @date 2016-04-20
	 *
	 */
	public enum BUG_MEMBER_TYPE {
		CREATOR, PARTICIPATOR
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
	/**
	 * 交易类型  0其他， 1数模，2接口，3应用，4子项目，5子项目和应用申请，6移动应用发版申请，7后端应用发版申请
	 * @author zhouxx
	 *	20170808
	 */
	public enum TRANS_TYPE{
		OTHER,DM,INTERFACE,APP,PJ,PJANDAPP,MOVEAPP,BACKEND
	}
	
	/**
	 * 数据状态 0其他，1有效，2无效
	 * @author zhouxx
	 *	20170808
	 */
	public enum DATA_STATUS{
		OTHER,VALID,INVALID
	}
	
	/**
	 * 交易状态  0申请， 1已签收，2未签收，3同意，4不同意,5废弃,
	 * @author zhouxx
	 *	20170808
	 */
	public enum TRANS_STATUS{
		APPLY,SIGNED,NOTSIGN,FINSH,UNFINSH,CUTOUT
	}
	
	/**
	 * 交易节点  0申请，1审批，2创建，3结束
	 * @author zhouxx
	 *	20170808
	 */
	public enum TRANS_NODE{
		APPLY,APPROVAL,CREATE,FINSH
	}
	
	/**
	 * 审核动作  0通过，1失败
	 * @author zhouxx
	 *	20170808
	 */
	public enum TRANS_MOVE{
		PASS,NOTPASS
	}
	
	public static void main(String[] args) {
	boolean a=true;
	boolean b=true;
	if(a&&b){
		System.out.println("a,b");
	}else if(a){
		System.out.println("a ");
	}else if(b){
		System.out.println(" b");
	}
	
	}

}
