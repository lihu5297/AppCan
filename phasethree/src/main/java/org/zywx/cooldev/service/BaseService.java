package org.zywx.cooldev.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.dao.CheckInfoDao;
import org.zywx.cooldev.dao.DynamicDao;
import org.zywx.cooldev.dao.DynamicDependencyDao;
import org.zywx.cooldev.dao.DynamicModuleDao;
import org.zywx.cooldev.dao.EnterpriseDao;
import org.zywx.cooldev.dao.EntityResourceRelDao;
import org.zywx.cooldev.dao.GitOperationLogDao;
import org.zywx.cooldev.dao.IdentityCodeDao;
import org.zywx.cooldev.dao.PermissionInterceptorDao;
import org.zywx.cooldev.dao.ResourcesDao;
import org.zywx.cooldev.dao.SEQDao;
import org.zywx.cooldev.dao.TeamAnalyDao;
import org.zywx.cooldev.dao.TeamAuthDao;
import org.zywx.cooldev.dao.TeamDao;
import org.zywx.cooldev.dao.TeamGroupDao;
import org.zywx.cooldev.dao.TeamMemberDao;
import org.zywx.cooldev.dao.UserAuthDao;
import org.zywx.cooldev.dao.UserDao;
import org.zywx.cooldev.dao.VideoDao;
import org.zywx.cooldev.dao.app.AppChannelDao;
import org.zywx.cooldev.dao.app.AppDao;
import org.zywx.cooldev.dao.app.AppPackageDao;
import org.zywx.cooldev.dao.app.AppPatchDao;
import org.zywx.cooldev.dao.app.AppTypeDao;
import org.zywx.cooldev.dao.app.AppVersionDao;
import org.zywx.cooldev.dao.app.AppWidgetDao;
import org.zywx.cooldev.dao.auth.PermissionDao;
import org.zywx.cooldev.dao.auth.RoleDao;
import org.zywx.cooldev.dao.bug.BugAuthDao;
import org.zywx.cooldev.dao.bug.BugDao;
import org.zywx.cooldev.dao.bug.BugMarkDao;
import org.zywx.cooldev.dao.bug.BugMemberDao;
import org.zywx.cooldev.dao.bug.BugModuleDao;
import org.zywx.cooldev.dao.builder.EngineDao;
import org.zywx.cooldev.dao.builder.PluginCategoryDao;
import org.zywx.cooldev.dao.builder.PluginDao;
import org.zywx.cooldev.dao.builder.PluginVersionDao;
import org.zywx.cooldev.dao.datamodel.DataModelDao;
import org.zywx.cooldev.dao.document.DocumentChapterDao;
import org.zywx.cooldev.dao.document.DocumentDao;
import org.zywx.cooldev.dao.document.DocumentMarkerDao;
import org.zywx.cooldev.dao.filialeInfo.FilialeInfoDao;
import org.zywx.cooldev.dao.notice.NoticeDao;
import org.zywx.cooldev.dao.notice.NoticeDependencyDao;
import org.zywx.cooldev.dao.notice.NoticeModuleDao;
import org.zywx.cooldev.dao.process.ProcessAuthDao;
import org.zywx.cooldev.dao.process.ProcessDao;
import org.zywx.cooldev.dao.process.ProcessMemberDao;
import org.zywx.cooldev.dao.project.ProjectAuthDao;
import org.zywx.cooldev.dao.project.ProjectCategoryDao;
import org.zywx.cooldev.dao.project.ProjectDao;
import org.zywx.cooldev.dao.project.ProjectMemberDao;
import org.zywx.cooldev.dao.project.ProjectParentDao;
import org.zywx.cooldev.dao.project.ProjectSortDao;
import org.zywx.cooldev.dao.tInterface.TInterFaceDao;
import org.zywx.cooldev.dao.task.TaskAuthDao;
import org.zywx.cooldev.dao.task.TaskCommentDao;
import org.zywx.cooldev.dao.task.TaskDao;
import org.zywx.cooldev.dao.task.TaskGroupDao;
import org.zywx.cooldev.dao.task.TaskGroupSortDao;
import org.zywx.cooldev.dao.task.TaskLeafDao;
import org.zywx.cooldev.dao.task.TaskMemberDao;
import org.zywx.cooldev.dao.task.TaskTagDao;
import org.zywx.cooldev.dao.topic.TopicAuthDao;
import org.zywx.cooldev.dao.topic.TopicCommentDao;
import org.zywx.cooldev.dao.topic.TopicDao;
import org.zywx.cooldev.dao.topic.TopicMemberDao;
import org.zywx.cooldev.dao.topic.TopicResourceDao;
import org.zywx.cooldev.dao.trans.TransDao;
import org.zywx.cooldev.dao.trans.TransFlowHISDao;
import org.zywx.cooldev.dao.trans.TransHisDao;
import org.zywx.cooldev.entity.Dynamic;
import org.zywx.cooldev.entity.DynamicDependency;
import org.zywx.cooldev.entity.DynamicModule;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.notice.NoticeModule;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskLeaf;
import org.zywx.cooldev.util.ProcessClearStream;
import org.zywx.cooldev.util.mail.base.MailSenderInfo;
import org.zywx.cooldev.util.mail.base.SendMailTools;

/**
 * 服务基类
 * @author yang.li
 * @date 2015-08-06
 *
 */
@Service
public class BaseService {

	protected static final Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
	@Autowired
	protected ProjectDao projectDao;
	@Autowired
	protected ProjectMemberDao projectMemberDao;
	@Autowired
	protected ProjectAuthDao projectAuthDao;
	@Autowired
	protected ProcessDao processDao;
	@Autowired
	protected PluginDao pluginDao;
	@Autowired
	protected PluginVersionDao pluginVersionDao;
	@Autowired
	protected EngineDao engineDao;
	@Autowired
	protected ProcessMemberDao processMemberDao;
	@Autowired
	protected ProcessAuthDao processAuthDao;
	@Autowired
	protected TaskDao taskDao;
	@Autowired
	protected BugDao bugDao;
	@Autowired
	protected BugMemberDao bugMemberDao;
	@Autowired
	protected BugAuthDao bugAuthDao;
	@Autowired
	protected BugModuleDao bugModuleDao;
	@Autowired
	protected BugMarkDao bugMarkDao;
	@Autowired
	protected TaskLeafDao taskLeafDao;
	@Autowired
	protected TaskAuthDao taskAuthDao;
	@Autowired
	protected TaskCommentDao taskCommentDao;
	@Autowired
	protected TaskMemberDao taskMemberDao;
	@Autowired
	protected TaskGroupDao taskGroupDao;
	@Autowired
	protected TaskTagDao taskTagDao;
	@Autowired
	protected TopicDao topicDao;
	@Autowired
	protected UserDao userDao;
	@Autowired
	protected UserAuthDao userAuthDao;
	@Autowired
	protected TopicMemberDao topicMemberDao;
	@Autowired
	protected TopicAuthDao topicAuthDao;
	@Autowired
	protected TopicCommentDao topicCommentDao;
	@Autowired
	protected TeamDao teamDao;
	@Autowired
	protected TeamGroupDao teamGroupDao;
	@Autowired
	protected TeamMemberDao teamMemberDao;
	@Autowired
	protected DocumentDao documentDao;
	@Autowired
	protected DocumentChapterDao documentChapterDao;
	@Autowired
	protected DocumentMarkerDao documentMarkerDao;
	@Autowired
	protected DynamicDao dynamicDao;
	@Autowired
	protected DynamicModuleDao dynamicModuleDao;
	@Autowired
	protected DynamicDependencyDao dynamicDependencyDao;
	@Autowired
	protected JdbcTemplate jdbcTpl;
	@Autowired
	protected ResourcesDao resourcesDao;
	@Autowired
	protected RoleDao roleDao;
	@Autowired
	protected TeamAuthDao teamAuthDao;
	@Autowired
	protected TopicResourceDao topicResourceDao;
	@Autowired
	protected NoticeDependencyDao noticeDependencyDao;
	@Autowired
	protected PluginCategoryDao pluginCategoryDao;
	@Autowired
	protected NoticeDao noticeDao;
	@Autowired
	protected NoticeModuleDao noticeModuleDao;
	@Autowired
	protected EntityResourceRelDao entityResourceRelDao;
	
	@Autowired
	protected ProjectCategoryDao projectCategoryDao;
	@Autowired
	protected PermissionDao permissionDao;

	@Autowired
	protected GitOperationLogDao gitOperationLogDao;

	@Autowired
	protected EnterpriseDao enterpriseDao;
	
	@Autowired
	protected AppDao appDao;
	@Autowired
	protected AppVersionDao appVersionDao;
	@Autowired
	protected AppChannelDao appChannelDao;
	@Autowired
	protected AppPackageDao appPackageDao;
	@Autowired
	protected AppPatchDao appPatchDao;
	@Autowired
	protected AppWidgetDao appWidgetDao;
	@Autowired
	protected TaskGroupSortDao taskGroupSortDao;
	
	@Autowired
	protected IdentityCodeDao identityCodeDao;
	
	@Autowired
	protected SendMailTools sendMailTool;
	
	@Autowired
	protected ProjectSortDao projectSortDao;
	@Autowired
	protected TeamAnalyDao teamAnalyDao;
	
	@Autowired 
	protected PermissionInterceptorDao permissionInterceptorDao;
	@Autowired
	protected FilialeInfoDao filialeInfoDao;
	@Autowired
	protected VideoDao videoDao;
	@Autowired
	protected AppTypeDao appTypeDao;
	
	@Value("${emailTaskBaseLink}")
	private String emailTaskBaseLink;
	
	@Value("${emailSourceRootPath}")
	private String emailSourceRootPath;
	
	@Value("${xtHost}")
	private String xtHost;
	
	protected Log log = LogFactory.getLog(this.getClass().getName());
	
	//zhouxx add start ----------->
	//交易表
	@Autowired 
	protected TransDao transDao;
	//数模表
	@Autowired 
	protected DataModelDao dataModelDao;
	
	//大项目表
	@Autowired 
	protected ProjectParentDao projectParentDao;
	//交易历史表
	@Autowired 
	protected TransHisDao transHisDao;
	//流水号表
	@Autowired 
	protected SEQDao seqDao;
	//接口表
	@Autowired 
	protected TInterFaceDao tInterFaceDao;
	
	//代码检测表
	@Autowired 
	protected CheckInfoDao checkInfoDao;
	
	@Autowired 
	protected TransFlowHISDao transFlowHISDao;
	
	//zhouxx add end ----------->
 	protected String execShell(String command) {          
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        try {  
            Process p = run.exec(command);
//            
//            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
//            String line;  
//            while ((line = in.readLine()) != null) {
//            	ret += (line + "\n\r");
//            	log.info(line);
//            }
//            in.close();
            new ProcessClearStream(p.getInputStream(),"BaseService-INFO",ret).start();
            new ProcessClearStream(p.getErrorStream(),"BaseService-ERROR", ret).start();
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret.toString();
 	}
 	
 	/**
 	 * 由于空格等为题,执行shell报错
 	 * @param command
 	 * @param dir
 	 * @return
 	 */
 	protected String syncExecShell(String[] command,File dir) {  
 		StringBuilder sb = new StringBuilder();
 		for(String str:command){
 			sb.append(",").append(str);
 		}
 		sb.deleteCharAt(0);
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        try {  
            Process p = run.exec(command,null,dir);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {
            	ret.append(line + "\n\r");
            }
            in.close();
            
            
            BufferedReader inErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = inErr.readLine()) != null) {
            	ret.append(line + "\n\r");
            }
            inErr.close();
            
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
            log.info("exec cmd["+sb+" in "+dir.getAbsolutePath()+"],result:["+ret.toString()+"]");
            log.info("Process exitValue:"+status);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret.toString();
 	}
 	protected String syncExecShell(String command) {          
        Runtime run = Runtime.getRuntime();  
        StringBuffer ret = new StringBuffer();
        try {  
            Process p = run.exec(command);
            int status = p.waitFor();
            log.info("Process exitValue:"+status);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));  
            String line;  
            while ((line = in.readLine()) != null) {
            	ret.append(line + "\n\r");
            }
            in.close();
            
            
            BufferedReader inErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = inErr.readLine()) != null) {
            	ret.append(line + "\n\r");
            }
            inErr.close();
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return ret.toString();
 	}
 	
 	
 	protected Map<String, Object> getFailedMap(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "failed");
		ret.put("message", message);
		return ret;
	}
	
	protected Map<String, Object> getSuccessMap(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", message);
		return ret;
	}

	
	/**
	 * @Description: 增加动态
	 * @param @param userId 当前用户
	 * @param @param moduleType 模板类型(枚举值)
	 * @param @param teamId 相关团队的ID
	 * @param @param objects
	 *        动态依赖的实体对象数组,并且里面的顺序不能错,会分别调用每个实体的toString()方法,来根据模板生成最终的动态信息
	 * @return void 返回类型
	 * @user jingjian.wu
	 * @date 2015年8月19日 下午7:45:29
	 * @throws
	 */
	public void addTeamDynamic(Long userId, DYNAMIC_MODULE_TYPE moduleType, long teamId, Object... objects) {
		Dynamic dynamic = new Dynamic();
		dynamic.setUserId(userId);
		dynamic.setModuleType(moduleType);
		dynamic.setType(DYNAMIC_TYPE.TEAM);
		dynamic.setRelationId(teamId);
		this.addDynamic(dynamic, objects);
	}

	/**
	 * @Description: 增加动态
	 * @param userId
	 *            当前用户
	 * @param moduleType
	 *            模板类型(枚举值)
	 * @param projectId
	 *            相关项目的ID
	 * @param objects
	 *            动态依赖的实体对象数组,并且里面的顺序不能错,会分别调用每个实体的toString()方法,来根据模板生成最终的动态信息
	 * @return void 返回类型
	 * @user jingjian.wu
	 * @date 2015年8月19日 下午7:45:29
	 * @throws
	 */
	public void addPrjDynamic(Long userId, DYNAMIC_MODULE_TYPE moduleType, long projectId, Object... objects) {
		log.info("add dynamic --> moduleType:" + moduleType + ",userId:" + userId + ",projectId:" + projectId);
		log.info(objects);
		Dynamic dynamic = new Dynamic();
		dynamic.setUserId(userId);
		dynamic.setModuleType(moduleType);
		dynamic.setType(DYNAMIC_TYPE.PROJECT);
		dynamic.setRelationId(projectId);
		this.addDynamic(dynamic, objects);
	}

	/**
	 * 
	 * @describe 添加动态 <br>
	 * @author jingjian.wu <br>
	 * @date 2015年8月19日 下午5:17:54 <br>
	 * @param userId
	 *            用户id
	 * @param moduleType
	 *            模板类型
	 * @param type
	 *            动态类型 PROJECT、TEAM
	 * @param relationId
	 *            如果动态属于某个项目,则相关ID为项目ID,否则项目应该是属于某个团队,则应该为团队ID
	 * @param objects
	 *            动态依赖对象,需要存储到DynamicDependency表中[参与此次动态的对象 object数组（该数组包括参与对象
	 *            并且包含对象id，可以序列化）]
	 *            根据模板类型获取到对应的模板之后,objects中每个对象的toString()方法用于替换模板中的占位符变量参数
	 * @returnType void
	 *
	 */
	private void addDynamic(Dynamic dynamic, Object... objects) {

		try {
			DynamicModule module = dynamicModuleDao.findByModuleType(dynamic.getModuleType());

			User u = this.userDao.findOne(dynamic.getUserId());
			String userName = (null == u.getUserName()) ? u.getAccount() : u.getUserName();
			Object formatParam[] = new Object[objects.length + 1]; // 在objects对象的头一个加上当前用户,因为模板的第一个%s占位符都需要当前操作人
			formatParam[0] = userName;
			for (int i = 0; i < objects.length; i++) {
				formatParam[i + 1] = objects[i];
				log.info("object :" + i + "->" + objects[i]);
			}

			log.info("record dynamic: module:" + module.getFormatStr());
			// 根据传进来的参数,格式化日志
			String info = String.format(module.getFormatStr(), formatParam);
			dynamic.setInfo(info);
			dynamic = this.dynamicDao.save(dynamic);

			DynamicDependency dynamicDependency = new DynamicDependency();
			for (Object object : objects) {
				if (null == object || object instanceof String || object instanceof Integer || object instanceof Long) {
					continue;
				}
				dynamicDependency = new DynamicDependency();
				String className = object.getClass().getName();
				className = className.substring(className.lastIndexOf(".") + 1);
				// entityType === object's class name
				dynamicDependency.setEntityType(className);
				Method method = null;
				Method[] ms = object.getClass().getSuperclass()
						.getDeclaredMethods();
				for (int i = 0; i < ms.length; i++) {
					if (ms[i].getName().equals("getId")) {
						method = ms[i];
						break;
					}
				}
				Object entityIdObj = method.invoke(object);
				Long entityId = Long.parseLong(entityIdObj.toString());
				dynamicDependency.setEntityId(entityId);
				dynamicDependency.setDynamicId(dynamic.getId());
				this.dynamicDependencyDao.save(dynamicDependency);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendEmail(Long userId, Long[] recievedIds, NOTICE_MODULE_TYPE noModuleType, Object[] placeHolder){
		try{
			NoticeModule module = this.noticeModuleDao.findByNoModuleType(noModuleType);
			if(module.getNoModuleType().name().startsWith("TASK_")){
				Object [] taskEmail = new Object[placeHolder.length];
				Task tmp = null;
				for(int i=0;i<placeHolder.length;i++){
					if(placeHolder[i] instanceof Task){
						tmp = (Task)placeHolder[i];
						placeHolder[i]="<a href='"+emailTaskBaseLink+"myTask?taskId="+tmp.getId()+"'>"+tmp.getDetail()+"</a>";
					}
					taskEmail[i]=placeHolder[i];
				}
				placeHolder = taskEmail;
			}
			if(module.getNoModuleType().name().startsWith("BUG_")){
				Object [] bugEmail = new Object[placeHolder.length];
				Bug tmp = null;
				for(int i=0;i<placeHolder.length;i++){
					if(placeHolder[i] instanceof Bug){
						tmp = (Bug)placeHolder[i];
						placeHolder[i]="<a href='"+emailTaskBaseLink+"myBug?bugId="+tmp.getId()+"'>"+tmp.getTitle()+"</a>";
					}
					bugEmail[i]=placeHolder[i];
				}
				placeHolder = bugEmail;
			}
			if(module.getNoModuleType().name().startsWith("TASK_LEAF_")){
				Object [] taskEmail = new Object[placeHolder.length];
				TaskLeaf tmp = null;
				for(int i=0;i<placeHolder.length;i++){
					if(placeHolder[i] instanceof TaskLeaf){
						tmp = (TaskLeaf)placeHolder[i];
						placeHolder[i]="<a href='"+emailTaskBaseLink+"myTask?taskId="+tmp.getTopTaskId()+"'>"+tmp.getDetail()+"</a>";
					}
					taskEmail[i]=placeHolder[i];
				}
				placeHolder = taskEmail;
			}
			String noInfo = String.format(module.getNoFormatStr(), placeHolder);
			for (Long recievedId : recievedIds) {
				if(recievedId==null){
					continue;
				}
				User user = this.userDao.findOne(recievedId);
				MailSenderInfo mailInfo = new MailSenderInfo();
				
				
				StringBuffer center = new StringBuffer();
				center.append("<p style=\"line-height:48px;margin-top:25px;margin-bottom:0\">Hi，"+user.getUserName()+"</p> <p style=\"line-")
				.append("height:30px;margin:0\"> "+noInfo+" </p>");
				
				
				mailInfo.setContent(center.toString());
//				mailInfo.setSubject("【AppCan-协同开发】邮箱通知");
				mailInfo.setSubject("【协同开发】邮箱通知");
				
				//if(null!=user.getBindEmail()){
				if(null!=user.getEmail()){
					mailInfo.setToAddress(user.getEmail());
					SendMailTools.setXtHost(xtHost);
					SendMailTools.setEmailSourceRootPath(emailSourceRootPath);
					sendMailTool.sendMailByAsynchronousMode(mailInfo);
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error("发送通知邮件失败，错误信息:"+e.getMessage());
		}
	}
	
	protected ModelAndView getFailedModel(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "failed");
		ret.put("message", message);
		return new ModelAndView("",ret);
	}
	
	protected ModelAndView getSuccessModel(Object message) {
		Map<String, Object> ret = new HashMap<>();
		ret.put("status", "success");
		ret.put("message", message);
		return new ModelAndView("",ret);
	}

}
