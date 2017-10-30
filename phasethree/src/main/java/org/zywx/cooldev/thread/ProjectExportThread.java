package org.zywx.cooldev.thread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.FileSystemUtils;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.dao.DynamicDao;
import org.zywx.cooldev.dao.ResourcesDao;
import org.zywx.cooldev.dao.UserDao;
import org.zywx.cooldev.dao.app.AppChannelDao;
import org.zywx.cooldev.dao.app.AppDao;
import org.zywx.cooldev.dao.app.AppPackageDao;
import org.zywx.cooldev.dao.app.AppPatchDao;
import org.zywx.cooldev.dao.app.AppVersionDao;
import org.zywx.cooldev.dao.app.AppWidgetDao;
import org.zywx.cooldev.dao.bug.BugAuthDao;
import org.zywx.cooldev.dao.bug.BugDao;
import org.zywx.cooldev.dao.bug.BugMarkDao;
import org.zywx.cooldev.dao.bug.BugMemberDao;
import org.zywx.cooldev.dao.bug.BugModuleDao;
import org.zywx.cooldev.dao.builder.EngineDao;
import org.zywx.cooldev.dao.builder.PluginDao;
import org.zywx.cooldev.dao.builder.PluginVersionDao;
import org.zywx.cooldev.dao.document.DocumentChapterDao;
import org.zywx.cooldev.dao.document.DocumentDao;
import org.zywx.cooldev.dao.document.DocumentMarkerDao;
import org.zywx.cooldev.dao.process.ProcessAuthDao;
import org.zywx.cooldev.dao.process.ProcessDao;
import org.zywx.cooldev.dao.process.ProcessMemberDao;
import org.zywx.cooldev.dao.project.ProjectAuthDao;
import org.zywx.cooldev.dao.project.ProjectDao;
import org.zywx.cooldev.dao.project.ProjectMemberDao;
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
import org.zywx.cooldev.entity.Dynamic;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppChannel;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.app.AppPatch;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.app.AppWidget;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.bug.BugAuth;
import org.zywx.cooldev.entity.bug.BugMark;
import org.zywx.cooldev.entity.bug.BugMember;
import org.zywx.cooldev.entity.bug.BugModule;
import org.zywx.cooldev.entity.builder.Engine;
import org.zywx.cooldev.entity.builder.Plugin;
import org.zywx.cooldev.entity.builder.PluginVersion;
import org.zywx.cooldev.entity.document.Document;
import org.zywx.cooldev.entity.document.DocumentChapter;
import org.zywx.cooldev.entity.document.DocumentMarker;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.process.ProcessAuth;
import org.zywx.cooldev.entity.process.ProcessMember;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskAuth;
import org.zywx.cooldev.entity.task.TaskComment;
import org.zywx.cooldev.entity.task.TaskGroup;
import org.zywx.cooldev.entity.task.TaskGroupSort;
import org.zywx.cooldev.entity.task.TaskLeaf;
import org.zywx.cooldev.entity.task.TaskMember;
import org.zywx.cooldev.entity.task.TaskTag;
import org.zywx.cooldev.entity.topic.Topic;
import org.zywx.cooldev.entity.topic.TopicAuth;
import org.zywx.cooldev.entity.topic.TopicComment;
import org.zywx.cooldev.entity.topic.TopicMember;
import org.zywx.cooldev.service.NoticeService;
import org.zywx.cooldev.system.InitBean;
import org.zywx.cooldev.util.ExportExcel;
import org.zywx.cooldev.util.ZipUtil;
import org.zywx.cooldev.util.mail.base.MailSenderInfo;
import org.zywx.cooldev.util.mail.base.SendMailTools;
public class ProjectExportThread  implements InitializingBean,ApplicationContextAware,Runnable  {

	private long loginUserId;
	private long projectId;
	public static Map<String,Object> projectExportStatus=new HashMap<String,Object>();
	private String rootPath;
	private JdbcTemplate jdbcTpl;
	public static ApplicationContext applicationContext;
	private String emailTaskBaseLink;
	private String emailSourceRootPath;
	private String xtHost;
	public void run(){
		try {
			NoticeService noticeService=(NoticeService)applicationContext.getBean("noticeService");
			SendMailTools sendMailTool = (SendMailTools)applicationContext.getBean("mailTool");
			//ProjectExportThread projectExportThread = (ProjectExportThread)applicationContext.getBean("builderPushToGitRepo");
			ProjectDao projectDao = (ProjectDao)applicationContext.getBean("projectDao");
			AppDao appDao = (AppDao)applicationContext.getBean("appDao");
			ProjectMemberDao projectMemberDao = (ProjectMemberDao)applicationContext.getBean("projectMemberDao");
			ProjectAuthDao projectAuthDao = (ProjectAuthDao)applicationContext.getBean("projectAuthDao");
			ProcessDao processDao = (ProcessDao)applicationContext.getBean("processDao");
			ProcessAuthDao processAuthDao = (ProcessAuthDao)applicationContext.getBean("processAuthDao");
			ProcessMemberDao processMemberDao = (ProcessMemberDao)applicationContext.getBean("processMemberDao");
			TaskDao taskDao = (TaskDao)applicationContext.getBean("taskDao");
			TaskLeafDao taskLeafDao = (TaskLeafDao)applicationContext.getBean("taskLeafDao");
			TaskGroupDao taskGroupDao = (TaskGroupDao)applicationContext.getBean("taskGroupDao");
			TaskMemberDao taskMemberDao = (TaskMemberDao)applicationContext.getBean("taskMemberDao");
			TaskGroupSortDao taskGroupSortDao = (TaskGroupSortDao)applicationContext.getBean("taskGroupSortDao");
			TaskAuthDao taskAuthDao = (TaskAuthDao)applicationContext.getBean("taskAuthDao");
			TaskCommentDao taskCommentDao = (TaskCommentDao)applicationContext.getBean("taskCommentDao");
			TaskTagDao taskTagDao = (TaskTagDao)applicationContext.getBean("taskTagDao");
			BugDao bugDao = (BugDao)applicationContext.getBean("bugDao");
			BugMemberDao bugMemberDao = (BugMemberDao)applicationContext.getBean("bugMemberDao");
			BugAuthDao bugAuthDao = (BugAuthDao)applicationContext.getBean("bugAuthDao");
			BugMarkDao bugMarkDao = (BugMarkDao)applicationContext.getBean("bugMarkDao");
			BugModuleDao bugModuleDao = (BugModuleDao)applicationContext.getBean("bugModuleDao");
			DynamicDao dynamicDao = (DynamicDao)applicationContext.getBean("dynamicDao");
			AppVersionDao appVersionDao = (AppVersionDao)applicationContext.getBean("appVersionDao");
			AppChannelDao appChannelDao = (AppChannelDao)applicationContext.getBean("appChannelDao");
			AppPackageDao appPackageDao = (AppPackageDao)applicationContext.getBean("appPackageDao");
			AppPatchDao appPatchDao = (AppPatchDao)applicationContext.getBean("appPatchDao");
			AppWidgetDao appWidgetDao = (AppWidgetDao)applicationContext.getBean("appWidgetDao");
			TopicDao topicDao = (TopicDao)applicationContext.getBean("topicDao");
			TopicCommentDao topicCommentDao = (TopicCommentDao)applicationContext.getBean("topicCommentDao");
			TopicMemberDao topicMemberDao = (TopicMemberDao)applicationContext.getBean("topicMemberDao");
			TopicAuthDao topicAuthDao = (TopicAuthDao)applicationContext.getBean("topicAuthDao");
			PluginDao pluginDao = (PluginDao)applicationContext.getBean("pluginDao");
			PluginVersionDao pluginVersionDao = (PluginVersionDao)applicationContext.getBean("pluginVersionDao");
			EngineDao engineDao = (EngineDao)applicationContext.getBean("engineDao");
			DocumentDao documentDao = (DocumentDao)applicationContext.getBean("documentDao");
			DocumentChapterDao documentChapterDao = (DocumentChapterDao)applicationContext.getBean("documentChapterDao");
			DocumentMarkerDao documentMarkerDao = (DocumentMarkerDao)applicationContext.getBean("documentMarkerDao");
			ResourcesDao resourcesDao = (ResourcesDao)applicationContext.getBean("resourcesDao");
			UserDao userDao = (UserDao)applicationContext.getBean("userDao");
			
			
			Project prjectObj = projectDao.findOne(projectId);
			if (prjectObj == null) {
				throw new RuntimeException("项目不存在");
			}
			final String projectName = prjectObj.getName()+"_"+loginUserId+"_"+projectId;
			String projectExportDirctory = "";
			String resourceSrcFileDir="";
			String resourceTargetFileDir="";
			String pluginAndroidSrcFileDir="";
			String pluginIosSrcFileDir="";
			String pluginAndroidTargetFileDir="";
			String pluginIosTargetFileDir="";
			String engineAndroidSrcFileDir="";
			String engineIosSrcFileDir="";
			String engineAndroidTargetFileDir="";
			String engineIosTargetFileDir="";
			String os = System.getProperty("os.name");
			String filePath ="";
			String[] projectFolderPath=new String[11];
			String[] projectFilePath=new String[4];
			System.out.print(rootPath+"========rootPath");
			
			if (os.toLowerCase().startsWith("win")) {
				projectExportDirctory = "C:\\mas_upload\\coopDevelopment_private\\projectExport\\";
				resourceSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\upload\\"+projectId;
				resourceTargetFileDir="C:\\mas_upload\\coopDevelopment_private\\projectExport\\"+projectName+"\\file\\resource\\";
				pluginAndroidSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\plugin\\project\\android\\"+projectId;
				pluginIosSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\plugin\\project\\ios\\"+projectId;
				pluginAndroidTargetFileDir="C:\\mas_upload\\coopDevelopment_private\\projectExport\\"+projectName+"\\file\\plugin\\project\\android\\";
				pluginIosTargetFileDir="C:\\mas_upload\\coopDevelopment_private\\projectExport\\"+projectName+"\\file\\plugin\\project\\ios\\";
				engineAndroidSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\engine\\project\\android\\"+projectId;
				engineIosSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\engine\\project\\ios\\"+projectId;
				engineAndroidTargetFileDir="C:\\mas_upload\\coopDevelopment_private\\projectExport\\"+projectName+"\\file\\engine\\project\\android\\";
				engineIosTargetFileDir="C:\\mas_upload\\coopDevelopment_private\\projectExport\\"+projectName+"\\file\\engine\\project\\ios\\";
				filePath = projectExportDirctory + projectName;
				projectFolderPath[0]=filePath +   "\\tables\\project";
				projectFolderPath[1]=filePath +   "\\tables\\process";
				projectFolderPath[2]=filePath +   "\\tables\\task";
				projectFolderPath[3]=filePath +   "\\tables\\bug";
				projectFolderPath[4]=filePath +   "\\tables\\dynamic";
				projectFolderPath[5]=filePath +   "\\tables\\app";
				projectFolderPath[6]=filePath +   "\\tables\\topic";
				projectFolderPath[7]=filePath +   "\\tables\\plugin";
				projectFolderPath[8]=filePath +   "\\tables\\engine";
				projectFolderPath[9]=filePath +   "\\tables\\document";
				projectFolderPath[10]=filePath +   "\\tables\\resource";
				projectFilePath[0]=filePath+"\\file\\plugin";
				projectFilePath[1]=filePath+"\\file\\engine";
				projectFilePath[2]=filePath+"\\file\\resource";
				projectFilePath[3]=filePath+"\\file\\app";
			} else {
				projectExportDirctory = rootPath+"/projectExport/";
				resourceSrcFileDir=rootPath+"/upload/"+projectId;
				resourceTargetFileDir=rootPath+"/projectExport/"+projectName+"/file/resource/";
				pluginAndroidSrcFileDir=rootPath+"/plugin/project/android/"+projectId;
				pluginIosSrcFileDir=rootPath+"/plugin/project/ios/"+projectId;
				pluginAndroidTargetFileDir=rootPath+"/projectExport/"+projectName+"/file/plugin/project/android/";
				pluginIosTargetFileDir=rootPath+"/projectExport/"+projectName+"/file/plugin/project/ios/";
				engineAndroidSrcFileDir=rootPath+"/engine/project/android/"+projectId;
				engineIosSrcFileDir=rootPath+"engine/project/ios/"+projectId;
				engineAndroidTargetFileDir=rootPath+"/projectExport/"+projectName+"/file/engine/project/android/";
				engineIosTargetFileDir=rootPath+"/projectExport/"+projectName+"/file/engine/project/ios/";
				filePath = projectExportDirctory + projectName;
				projectFolderPath[0]=filePath +   "/tables/project";
				projectFolderPath[1]=filePath +   "/tables/process";
				projectFolderPath[2]=filePath +   "/tables/task";
				projectFolderPath[3]=filePath +   "/tables/bug";
				projectFolderPath[4]=filePath +   "/tables/dynamic";
				projectFolderPath[5]=filePath +   "/tables/app";
				projectFolderPath[6]=filePath +   "/tables/topic";
				projectFolderPath[7]=filePath +   "/tables/plugin";
				projectFolderPath[8]=filePath +   "/tables/engine";
				projectFolderPath[9]=filePath +   "/tables/document";
				projectFolderPath[10]=filePath +   "/tables/resource";
				projectFilePath[0]=filePath+"/file/plugin";
				projectFilePath[1]=filePath+"/file/engine";
				projectFilePath[2]=filePath+"/file/resource";
				projectFilePath[3]=filePath+"/file/app";
			}
			File file = new File(filePath);// 要被压缩的文件夹
			if (!file.exists()) {
				file.mkdirs();
			}
			if(!new File(resourceSrcFileDir).exists()){
				new File(resourceSrcFileDir).mkdirs();
			}
			if(!new File(resourceTargetFileDir).exists()){
				new File(resourceTargetFileDir).mkdirs();
			}
			if(!new File(pluginAndroidSrcFileDir).exists()){
				new File(pluginAndroidSrcFileDir).mkdirs();
			}
			if(!new File(pluginIosSrcFileDir).exists()){
				new File(pluginIosSrcFileDir).mkdirs();
			}
			if(!new File(pluginAndroidTargetFileDir).exists()){
				new File(pluginAndroidTargetFileDir).mkdirs();
			}
			if(!new File(pluginIosTargetFileDir).exists()){
				new File(pluginIosTargetFileDir).mkdirs();
			}
			if(!new File(engineAndroidSrcFileDir).exists()){
				new File(engineAndroidSrcFileDir).mkdirs();
			}
			if(!new File(engineIosSrcFileDir).exists()){
				new File(engineIosSrcFileDir).mkdirs();
			}
			if(!new File(engineAndroidTargetFileDir).exists()){
				new File(engineAndroidTargetFileDir).mkdirs();
			}
			if(!new File(engineIosTargetFileDir).exists()){
				new File(engineIosTargetFileDir).mkdirs();
			}
			// 创建项目、流程、任务、bug。。。等文件夹
			for(int m=0;m<projectFolderPath.length;m++){
				File folderF = new File(projectFolderPath[m]);// 要被压缩的文件夹
				if (!folderF.exists()) {
					folderF.mkdirs();
				}
			}
			for(int i=0;i<projectFilePath.length;i++){
				File fileF = new File(projectFilePath[i]);// 要被压缩的文件夹
				if (!fileF.exists()) {
					fileF.mkdirs();
				}
			}
			//拷贝资源、插件、引擎、应用代码
		    final File resourceSrcFile=new File(resourceSrcFileDir);
			final File resourceTargetFile=new File(resourceTargetFileDir);
			final File pluginAndroidSrcFile=new File(pluginAndroidSrcFileDir);
			final File pluginAndroidTargetFile=new File(pluginAndroidTargetFileDir);
			final File pluginIosSrcFile=new File(pluginIosSrcFileDir);
			final File pluginIosTargetFile=new File(pluginIosTargetFileDir);
			final File engineAndroidSrcFile=new File(engineAndroidSrcFileDir);
			final File engineAndroidTargetFile=new File(engineAndroidTargetFileDir);
			final File engineIosSrcFile=new File(engineIosSrcFileDir);
			final File engineIosTargetFile=new File(engineIosTargetFileDir);
		    final List<App> appListF=appDao.getAppByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			String os1 = System.getProperty("os.name");
			String appSrcFileDir="";
			String appTargetFileDir="";
			if (os1.toLowerCase().startsWith("win")) {
				appSrcFileDir="C:\\mas_upload\\share\\gitroot\\";
				appTargetFileDir="C:\\mas_upload\\coopDevelopment_private\\"+projectName+"\\file\\app\\";
			}else{
				appSrcFileDir="/mnt/glfs/share/gitroot/";
				appTargetFileDir=rootPath+"/projectExport/"+projectName+"/file/app/";
			}
			//将目录下所有文件复制
			if(resourceSrcFile.exists()){
				FileSystemUtils.copyRecursively(resourceSrcFile, resourceTargetFile);
			}
			if(pluginAndroidSrcFile.exists()){
				FileSystemUtils.copyRecursively(pluginAndroidSrcFile, pluginAndroidTargetFile);
			}
			if(pluginIosSrcFile.exists()){
				FileSystemUtils.copyRecursively(pluginIosSrcFile, pluginIosTargetFile);
			}
			if(engineAndroidSrcFile.exists()){
				FileSystemUtils.copyRecursively(engineAndroidSrcFile, engineAndroidTargetFile);
			}
			if(engineIosSrcFile.exists()){
				FileSystemUtils.copyRecursively(engineIosSrcFile, engineIosTargetFile);
			}
			//拷贝app代码
			for(App appO:appListF){
				File appSrcFile=new  File(appSrcFileDir+appO.getRelativeRepoPath());
			    if(appSrcFile.exists()){
			    	FileSystemUtils.copyRecursively(appSrcFile,new File(appTargetFileDir));
			    }
			}
			//T_PROJECT
			Project project = projectDao.findByIdAndDel(projectId,
					DELTYPE.NORMAL);
			List<Object> projectList=new ArrayList<Object>();
			projectList.add(project);
			//T_PROJECT_MEMBER
			List<ProjectMember> projectMemberList = projectMemberDao.findByProjectIdAndDel(
					projectId, DELTYPE.NORMAL);
			//T_PROJECT_AUTH
			List<ProjectAuth> projectAuthList = projectAuthDao.getProjectAuthByProjectId(
					 DELTYPE.NORMAL.ordinal(),projectId);
			//T_PROCESS
			List<Process> processList=processDao.findByProjectIdAndDel(projectId,DELTYPE.NORMAL);
			//T_PROCESS_MEMBER
			List<ProcessMember> processMemberList=processMemberDao.getProcessMemberByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_PROCESS_AUTH 
			List<ProcessAuth> processAuthList=processAuthDao.getProcessAuthByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK
			List<Task> taskList=taskDao.getTaskByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK_Leaf
			List<TaskLeaf> taskLeafList=taskLeafDao.getTaskLeafByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK_MEMBER
			List<TaskMember> taskMemberList=taskMemberDao.getTaskMemberByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK_GROUP
			List<TaskGroup> taskGroupList=taskGroupDao.getTaskGroupByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK_GROUP_SORT
			List<TaskGroupSort> taskGroupSortList=taskGroupSortDao.getTaskGroupSortByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK_AUTH
			List<TaskAuth> taskAuthList=taskAuthDao.getTaskAuthByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK_COMMENT
			List<TaskComment> taskCommentList=taskCommentDao.getTaskCommentByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TASK_TAG
			List<TaskTag> taskTagList=taskTagDao.getTaskTagByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_BUG
			List<Bug> bugList=bugDao.getBugByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_BUG_MEMBER
			List<BugMember> bugMemberList=bugMemberDao.getBugMemberByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_BUG_AUTH
			List<BugAuth> bugAuthList=bugAuthDao.getBugAuthByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_BUG_MARK
			List<BugMark> bugMarkList=bugMarkDao.getBugMarkByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_BUG_MODULE
			List<BugModule> bugModuleList=bugModuleDao.getBugModuleByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_DYNAMIC
			List<Dynamic> dynamicList=dynamicDao.getDynamicByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_APP
			List<App>  appList=appDao.getAppByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_APP_VERSION
			List<AppVersion> appVersionList=appVersionDao.getAppVersionByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_APP_CHANNEL
			List<AppChannel> appChannelList=appChannelDao.getAppChannelByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_APP_PACKAGE
			List<AppPackage> appPackageList=appPackageDao.getAppPackageByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_APP_PATCH
			List<AppPatch> appPatchList=appPatchDao.getAppPatchByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_APP_WIDGET
			List<AppWidget> appWidgetList=appWidgetDao.getAppWidgetByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TOPIC
			List<Topic> topicList=topicDao.findByProjectIdAndDel(projectId,DELTYPE.NORMAL);
			//T_TOPIC_COMMENT
			List<TopicComment> topicCommentList=topicCommentDao.getTopicCommentByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TOPIC_MEMBER
			List<TopicMember> topicMemberList=topicMemberDao.getTopicCommentByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_TOPIC_AUTH
			List<TopicAuth> topicAuthList=topicAuthDao.getTopicAuthByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_PLUGIN
			List<Plugin> pluginList=pluginDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
			//T_PLUGIN_VERSION
			List<PluginVersion> pluginVersionList=pluginVersionDao.getPluginVersionByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_ENGINE
			List<Engine> engineList=engineDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
			//T_DOCUMENT
			List<Document> documentList=documentDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
			//T_DOCUMENT_CHAPTER
			List<DocumentChapter> documentChapterList=documentChapterDao.getDocumentChapterByProjectId(DELTYPE.NORMAL,projectId);
			//T_DOCUMENT_MARKER
			List<DocumentMarker> documentMarkerList=documentMarkerDao.getDocuementMarkerByProjectId(DELTYPE.NORMAL.ordinal(),projectId);
			//T_RESOURCES
			List<Resource> resourceList=resourcesDao.findByProjectIdAndDel(projectId, DELTYPE.NORMAL);
			//T_PROJECT_ALL_MEMBER
			StringBuffer allMemberSql=new StringBuffer();
			allMemberSql.append("select id as UserId,account from T_USER where id in (select distinct(userId) from( ")
						.append("select distinct(userId) from T_PROJECT_MEMBER where del=0 and projectId=").append(projectId)
						.append(" union ")
						.append(" select distinct(finishUserId) as userId from T_TASK where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")")
						.append(" union ")
						.append("select distinct(finishUserId) as userId from T_TASK_LEAF where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")")
						.append(" union ")
						.append("select distinct(managerUserId) as userId from T_TASK_LEAF where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")")
						.append(" union ")
						.append("select distinct(userId) from T_TASK_MEMBER where del=0 and taskId in (select id from T_TASK where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))")
						.append(" union ")
						.append("select distinct(userId) from T_TASK_GROUP_SORT where del=0 and projectId=").append(projectId).append("")
						.append(" union ")
						.append("select distinct(userId) from T_TASK_COMMENT where del=0 and taskId in (select id from T_TASK where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))")
						.append(" union ")
						.append("select distinct(resolveUserId) as userId from T_BUG where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")")
						.append(" union ")
						.append("select distinct(closeUserId) as userId from T_BUG where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")")
						.append(" union ")
						.append("select distinct(lastModifyUserId) as userId from T_BUG where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")")
						.append(" UNION ")
						.append("select distinct(userId) from T_BUG_MEMBER where del=0 and bugId in (select id from T_TASK where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))")
						.append(" UNION ")
						.append("select distinct(userId) from T_BUG_MARK where del=0 and bugId in (select id from T_TASK where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))")
						.append(" UNION ")
						.append("select distinct(creatorId) as userId from T_BUG_MODULE where del=0 and projectId=").append(projectId).append("")
						.append(" UNION ")
						.append("select distinct(managerId) as userId from T_BUG_MODULE where del=0 and projectId=").append(projectId).append("")
						.append(" UNION ")
						.append("select distinct(userId) from T_DYNAMIC where del=0 and relationId=500")
						.append(" UNION ")
						.append("select DISTINCT(userId) from T_APP where del=0 and projectId=").append(projectId).append("")
						.append(" UNION ")
						.append("select distinct(userId) from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append(")")
						.append(" UNION ")
						.append("select distinct(userId) from T_APP_PACKAGE where del=0 and appVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append("))")
						.append(" UNION ")
						.append("select distinct(userId) from T_APP_PATCH where del=0 and baseAppVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append("))")
						.append(" UNION ")
						.append("select distinct(userId) from T_APP_WIDGET where del=0 and appVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append("))")
						.append(" union ")
						.append("select distinct(userId) from T_TOPIC where del=0 and projectId=").append(projectId).append("")
						.append(" union ")
						.append("select distinct(userId) from T_TOPIC_COMMENT where del=0 and topicId in (select id from T_TOPIC where del=0 and projectId=").append(projectId).append(")")
						.append(" UNION ")
						.append("select distinct(userId) from T_TOPIC_MEMBER where del=0 and topicId in (select id from T_TOPIC where del=0 and projectId=").append(projectId).append(")")
						.append(" UNION ")
						.append("select distinct(userId) from T_DOCUMENT where del=0 and projectId=").append(projectId).append("")
						.append(" union ")
						.append("select DISTINCT(userId) from T_DOCUMENT_CHAPTER where del=0 and documentId in (select id from T_DOCUMENT where del=0 and projectId=").append(projectId).append(")")
						.append(" union ")
						.append("select DISTINCT(userId) from T_DOCUMENT_MARKER where del=0 and docCId in (select id from T_DOCUMENT_CHAPTER where del=0 and documentId in (select id from T_DOCUMENT where del=0 and projectId=").append(projectId).append("))")
						.append(" UNION ")
						.append("select DISTINCT(userId) from T_RESOURCES where del=0 and projectId=").append(projectId).append("")
						.append(") as xxx where userId>0) and del=0");
					//JdbcTemplate jdbcTpl=new JdbcTemplate();
					List<Map<String, Object>> projectAllMemberList=jdbcTpl.queryForList(allMemberSql.toString());
					System.out.print(projectAllMemberList.toString()+"============");
					System.out.print(projectList.toString()+"=============");
					
					List lists[][]={{projectList,projectMemberList,projectAuthList,projectAllMemberList},{processList,processMemberList,processAuthList},
					{taskList,taskLeafList,taskMemberList,taskGroupList,taskGroupSortList,taskAuthList,taskCommentList,taskTagList},
					{bugList,bugMemberList,bugAuthList,bugMarkList,bugModuleList},{dynamicList},
					{appList,appVersionList,appChannelList,appPackageList,appPatchList,appWidgetList},
					{topicList,topicCommentList,topicMemberList,topicAuthList},
					{pluginList,pluginVersionList},{engineList},
					{documentList,documentChapterList,documentMarkerList},{resourceList}};
			//T_PROJECT table
			String[] projectTitle = new String[] {"id|id",
					"bizCompanyId|bizCompanyId",
					"bizCompanyName|bizCompanyName", "bizLicense|bizLicense",
					"categoryId|categoryId", "detail|detail", "name|name",
					"productionEMMUrl|productionEMMUrl", "status|status",
					"teamId|teamId", "testingEMMUrl|testingEMMUrl", "type|type",
					"finishDate|finishDate" };
			//T_PROJECT_MEMBER table
			String[] projectMemberTitle = new String[] {"id|id", 
					 "projectId|projectId",
					"type|type", "userId|userId"};
			//T_PROJECT_ALL_MEMBER table
			String[] projectAllMemberTitle = new String[] {"oldAccount|account","oldUserId|userId", 
					 "newUserId|userId"};
			//T_PROJECT_AUTH table
			String[] projectAuthTitle = new String[] {
					"memberId|memberId","roleId|roleId" };
			//T_PROCESS table
			String[] processTitle = new String[] {"id|id", 
					"detail|detail","endDate|endDate",
					"name|name","projectId|projectId","startDate|startDate","status|status","weight|weight" };
			//T_PROCESS_MEMBER table
			String[] processMemberTitle = new String[] {"id|id",  
					"processId|processId","type|type","userId|userId"};
			
			//T_PROCESS_MEMBER table
		    String[] processAuthTitle = new String[] { 
				    "memberId|memberId","roleId|roleId"};
		    //T_TASK table
		    String[] taskTitle = new String[] {"id|id","processId|processId",  
				    "appId|appId","deadline|deadline",
				    "detail|detail", "lastStatusUpdateTime|lastStatusUpdateTime","priority|priority",
				    "progress|progress", "repeatable|repeatable","groupId|groupId","finishDate|finishDate","finishUserId|finishUserId","status|status"};
		    //T_TASK_LEAF table
		    String[] taskLeafTitle =new String[] {"topTaskId|topTaskId","detail|detail","processId|processId","appId|appId",
		    		"deadline|deadline","status|status","finishDate|finishDate","finishUserId|finishUserId","managerUserId|managerUserId",
				     "lastStatusUpdateTime|lastStatusUpdateTime"};
			//T_TASK_MEMBER table
		    String[] taskMemberTitle = new String[] {"id|id", 
					"taskId|taskId","type|type","userId|userId"};
		   //T_TASK_GROUP table
		    String[] taskGroupTitle = new String[] {"id|id", 
					"projectId|projectId","name|name","sort|sort"};
		   //T_TASK_GROUP_SORT table
		    String[] taskGroupSortTitle = new String[] { 
					"projectId|projectId","sort|sort","groupId|groupId","userId|userId"};
		   //T_TASK_AUTH table
		    String[] taskAuthTitle = new String[] { 
					"memberId|memberId","roleId|roleId"};
		    //T_TASK_COMMENT
		    String[] taskCommentTitle = new String[] {"taskId|taskId", 
					"content|content","replyTo|replyTo",
					"userId|userId"};
		   //T_TASK_COMMENT
		    String[] taskTagTitle = new String[] {"taskId|taskId",
					"tagId|tagId"};
		   //T_BUG
		    String[] bugTitle = new String[] {"id|id","processId|processId", 
					"title|title","detail|detail",
					 "appId|appId","status|status","moduleId|moduleId","priority|priority",
					"affectVersion|affectVersion", "resolveVersion|resolveVersion","resolveUserId|resolveUserId","solution|solution","resolveAt|resolveAt",
					"closeAt|closeAt", "closeUserId|closeUserId","lastModifyUserId|lastModifyUserId"};
		   //T_BUG_MEMBER
		    String[] bugMemberTitle = new String[] {"id|id", 
					"bugId|bugId","type|type","userId|userId"}; 
		   //T_BUG_AUTH
		    String[] bugAuthTitle = new String[] { 
					"memberId|memberId","roleId|roleId"}; 
		    //T_BUG_MARK
		    String[] bugMarkTitle = new String[] { 
					"bugId|bugId","info|info","userId|userId"}; 
		    //T_BUG_MODULE
		    String[] bugModuleTitle = new String[] {"id|id", 
		    		"projectId|projectId","name|name","managerId|managerId","creatorId|creatorId"}; 
		    //T_DYNAMIC
		    String[] dynamicTitle = new String[] { 
					"info|info","moduleType|moduleType","relationId|relationId","type|type","userId|userId"}; 
		    //T_APP
		    String[] appTitle = new String[] {"id|id", 
		    		"appType|appType","appcanAppId|appcanAppId","appcanAppKey|appcanAppKey","detail|detail",
		    		"name|name","projectId|projectId","published|published","publishedAppCan|publishedAppCan",
		    		"publishedTest|publishedTest","relativeRepoPath|relativeRepoPath","repoType|repoType","userId|userId",
		    		"appCategory|appCategory","icon|icon","codePullStatus|codePullStatus",
		    		"specialAppCanAppId|specialAppCanAppId","specialAppCanAppKey|specialAppCanAppKey","forbidPub|forbidPub","sourceGitRepo|sourceGitRepo"}; 
		    //T_APP_VERSION
		    String[] appVersionTitle = new String[] {"id|id", 
					"appId|appId","branchName|branchName","branchZipName|branchZipName",
					"tagName|tagName","type|type","userId|userId","versionDescription|versionDescription","versionNo|versionNo","branchZipSize|branchZipSize"}; 
		    //T_APP_CHANNEL
		    String[] appChannelTitle = new String[] { 
					"appId|appId","code|code","detail|detail","name|name"}; 
		    //T_APP_PACKAGE
		    String[] appPackageTitle = new String[] { 
					"appVersionId|appVersionId","buildJsonSettings|buildJsonSettings","buildLogUrl|buildLogUrl","buildMessage|buildMessage",
					"buildStatus|buildStatus","buildType|buildType","channelCode|channelCode","downloadUrl|downloadUrl","fileSize|fileSize","hardwareAccelerated|hardwareAccelerated",
					"increUpdateIF|increUpdateIF","osType|osType","publised|publised","publisedAppCan|publisedAppCan","publisedTest|publisedTest","pushIF|pushIF",
					"qrCode|qrCode","terminalType|terminalType","updateSwith|updateSwith","userId|userId","versionDescription|versionDescription","versionNo|versionNo",
					"newAppCanAppId|newAppCanAppId","newAppCanAppKey|newAppCanAppKey"}; 
		    //T_APP_PATCH
		    String[] appPatchTitle = new String[] { 
					"baseAppVersionId|baseAppVersionId","fileName|fileName","fileSize|fileSize","published|published",
					"publishedTest|publishedTest","seniorAppVersionId|seniorAppVersionId","type|type","userId|userId","versionDescription|versionDescription","versionNo|versionNo"};
		    //T_APP_WIDGET
		    String[] appWidgetTitle = new String[] { 
					"appVersionId|appVersionId","fileName|fileName","fileSize|fileSize","published|published",
					"publishedTest|publishedTest","userId|userId","versionDescription|versionDescription","versionNo|versionNo"}; 
		    //T_TOPIC
		    String[] topicTitle = new String[] {"id|id",
					"detail|detail","projectId|projectId","title|title","userId|userId"};
		    //T_TOPIC_COMMENT
		    String[] topicCommentTitle = new String[] {"topicId|topicId", 
					"detail|detail","replyTo|replyTo","userId|userId"};
		    //T_TOPIC_MEMBER
		    String[] topicMemberTitle = new String[] {"id|id","topicId|topicId", 
					"type|type","userId|userId"};
		    //T_TOPIC_AUTH
		    String[] topicAuthTitle = new String[] { 
					"memberId|memberId","roleId|roleId"}; 
		    //T_PLUGIN
		    String[] pluginTitle = new String[] {"id|id",
					"categoryId|categoryId","cnName|cnName",
					"detail|detail","enName|enName","projectId|projectId","tutorial|tutorial","type|type"};
		    //T_PLUGIN_VERSION
		    String[] pluginVersionTitle = new String[] {"pluginId|pluginId", 
					"customDownloadUrl|customDownloadUrl","customResPackageUrl|customResPackageUrl",
					"downloadUrl|downloadUrl","osType|osType","pkgGitRepoUrl|pkgGitRepoUrl","resPackageUrl|resPackageUrl",
					"status|status","versionDescription|versionDescription","versionNo|versionNo","uploadStatus|uploadStatus","filePath|filePath"}; 
		    //T_ENGINE
		    String[] engineTitle = new String[] { 
					"downloadUrl|downloadUrl","osType|osType",
					"pkgGitRepoUrl|pkgGitRepoUrl","projectId|projectId","status|status","type|type","versionDescription|versionDescription",
					"versionNo|versionNo","uploadStatus|uploadStatus","packageDescription|packageDescription","filePath|filePath"};
		    //T_DOCUMENT
		    String[] documentTitle = new String[] {"id|id", 
					"describ|describ","name|name",
					"projectId|projectId","pub|pub","pubUrl|pubUrl","userId|userId"};
		    //T_DOCUMENT_CHAPTER
		    String[] documentChapterTitle = new String[] {"id|id","documentId|documentId",
					"contentHTML|contentHTML","contentMD|contentMD",
					"name|name","parentId|parentId","pub|pub",
					"sort|sort","type|type","userId|userId"};
		   //T_DOCUMENT_MARKER
		    String[] documentMarkerTitle = new String[] {"docCId|docCId", 
					"content|content","target|target","userId|userId","userName|userName"};
		    //T_RESOURCES
		    String[] resourceTitle = new String[] { 
					"filePath|filePath","fileSize|fileSize",
					"name|name","parentId|parentId","projectId|projectId",
					"userId|userId","type|type","userName|userName","sourceType|sourceType"};
		    String allFileNames[][]={{"T_PROJECT","T_PROJECT_MEMBER","T_PROJECT_AUTH","T_PROJECT_ALL_MEMBER"},{"T_PROCESS","T_PROCESS_MEMBER","T_PROCESS_AUTH"},
					{"T_TASK","T_TASK_LEAF","T_TASK_MEMBER","T_TASK_GROUP","T_TASK_GROUP_SORT","T_TASK_AUTH","T_TASK_COMMENT","T_TASK_TAG"},
					{"T_BUG","T_BUG_MEMBER","T_BUG_AUTH","T_BUG_MARK","T_BUG_MODULE"},
					{"T_DYNAMIC"},{"T_APP","T_APP_VERSION","T_APP_CHANNEL","T_APP_PACKAGE","T_APP_PATCH","T_APP_WIDGET"},
					{"T_TOPIC","T_TOPIC_COMMENT","T_TOPIC_MEMBER","T_TOPIC_AUTH"},
					{"T_PLUGIN","T_PLUGIN_VERSION"},{"T_ENGINE"},{"T_DOCUMENT","T_DOCUMENT_CHAPTER","T_DOCUMENT_MARKER"},{"T_RESOURCES"}};
			
			String[] listAndTitles[][]={{projectTitle,projectMemberTitle,projectAuthTitle,projectAllMemberTitle},{processTitle,processMemberTitle,processAuthTitle},
					{taskTitle,taskLeafTitle,taskMemberTitle,taskGroupTitle,taskGroupSortTitle,taskAuthTitle,taskCommentTitle,taskTagTitle},
					{bugTitle,bugMemberTitle,bugAuthTitle,bugMarkTitle,bugModuleTitle,appPatchTitle},
					{dynamicTitle},{appTitle,appVersionTitle,appChannelTitle,appPackageTitle,appPatchTitle,appWidgetTitle},
					{topicTitle,topicCommentTitle,topicMemberTitle,topicAuthTitle},
					{pluginTitle,pluginVersionTitle},{engineTitle},{documentTitle,documentChapterTitle,documentMarkerTitle},{resourceTitle}};
			for(int i=0;i<allFileNames.length;i++){
				String fileNames[]=allFileNames[i];
				for(int j=0;j<fileNames.length;j++){
					createProject(projectId, projectFolderPath[i],fileNames[j],lists[i][j],listAndTitles[i][j]);
				}
			}
			String zipPath = filePath+ ".zip";
	        ZipUtil.zip(filePath, zipPath);
	        //删除文件夹
	        FileSystemUtils.deleteRecursively(new File(filePath));
	        projectExportStatus.put(loginUserId+"_"+projectId, "over");
	         User user = userDao.findOne(loginUserId);
			 MailSenderInfo mailInfo = new MailSenderInfo();
			// System.out.println(emailSourceRootPath+"/projectExport/"+projectName);
			 StringBuffer center = new StringBuffer();
			 center.append("<p style=\"line-height:48px;margin-top:25px;margin-bottom:0\">Hi，"+user.getUserName()+"</p> <p style=\"line-")
			 .append("height:30px;margin:0\">您的项目："+project.getName()+"已成功导出。<a href='"+emailSourceRootPath+"/projectExport/"+projectName+".zip'>下载链接</a></p>");
			mailInfo.setContent(center.toString());
			mailInfo.setSubject("【AppCan-协同开发】邮箱通知");
			if(null!=user.getBindEmail()){
				mailInfo.setToAddress(user.getBindEmail());
				SendMailTools.setXtHost(xtHost);
				SendMailTools.setEmailSourceRootPath(emailSourceRootPath);
				sendMailTool.sendMailByAsynchronousMode(mailInfo);
			}
			noticeService.addNotice(loginUserId,
					new Long[] {loginUserId}, NOTICE_MODULE_TYPE.PROJECT_EXPORT,
					new Object[] { project, emailSourceRootPath+"/projectExport/"+projectName+".zip" });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ProjectExportThread() {
		super();
	}
	
	public ProjectExportThread(long loginUserId,long projectId,String rootPath,JdbcTemplate jdbcTpl,String emailTaskBaseLink,String emailSourceRootPath,String xtHost){
		this.loginUserId = loginUserId;
		this.projectId = projectId;
		this.rootPath=rootPath;
		this.jdbcTpl=jdbcTpl;
		this.emailTaskBaseLink=emailTaskBaseLink;
		this.emailSourceRootPath=emailSourceRootPath;
		this.xtHost=xtHost;
		projectExportStatus.put(loginUserId+"_"+projectId,"ing");
		
	}
	public void createProject(long projectId, String filePath,String fileName, List list, String[] title)
			throws IOException {
		HSSFWorkbook wb = ExportExcel.exportExcel(title, fileName, list);
		String putFileName = fileName + ".xls";
		File file = new File(filePath, putFileName);
		FileOutputStream fo = new FileOutputStream(file);
		wb.write(fo);
		fo.flush();
		fo.close();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}
	
	
}
