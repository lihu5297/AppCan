package org.zywx.cooldev.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.entity.Dynamic;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.TeamAuth;
import org.zywx.cooldev.entity.TeamMember;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppChannel;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.app.AppPatch;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.app.AppWidget;
import org.zywx.cooldev.entity.auth.Role;
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
import org.zywx.cooldev.entity.task.TaskLeaf;
import org.zywx.cooldev.entity.task.TaskMember;
import org.zywx.cooldev.entity.task.TaskTag;
import org.zywx.cooldev.entity.topic.Topic;
import org.zywx.cooldev.entity.topic.TopicAuth;
import org.zywx.cooldev.entity.topic.TopicComment;
import org.zywx.cooldev.entity.topic.TopicMember;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.thread.ProjectExportThread;
import org.zywx.cooldev.thread.ProjectImportThread;
import org.zywx.cooldev.util.ReflectUtil;
import org.zywx.cooldev.util.ZipUtil;
import org.zywx.cooldev.util.mail.base.MailSenderInfo;
import org.zywx.cooldev.util.mail.base.SendMailTools;


@Service
public class ProjectExportService extends BaseService {
	
	@Autowired
	private AppService appService;
	
	@Value("${emailTaskBaseLink}")
	private String emailTaskBaseLink;
	
	@Value("${git.localGitRoot}")
	private String localGitRoot;
	
	@Value("${emailSourceRootPath}")
	private String emailSourceRootPath;
	
	@Value("${xtHost}")
	private String xtHost;
	
	@Value("${root.path}")
	private String rootPath;
	
	@Autowired
	protected NoticeService noticeService;
	
	public static Map<String,Object> projectImportProjectId=new HashMap<String,Object>();
	public String projectExport(long loginUserId, long projectId)
			throws IOException {
		Project p=projectDao.findByIdAndDel(projectId, DELTYPE.NORMAL);
		if(p==null){
			throw new RuntimeException("项目不存在");
		}
		boolean creatorOrAdm=false;
		List<ProjectMember> pms=projectMemberDao.findByProjectIdAndUserIdAndDel(projectId,loginUserId,DELTYPE.NORMAL);
		for(ProjectMember pm:pms){
			List<ProjectAuth> pas=projectAuthDao.findByMemberIdAndDel(pm.getId(), DELTYPE.NORMAL);
			for(ProjectAuth pa:pas){
				Role r=Cache.getRole(pa.getId());
				if(r.getEnName().equals(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR)){
					creatorOrAdm=true;break;
				}
			}
		}
		if(!creatorOrAdm){
			TeamMember tm=teamMemberDao.findByTeamIdAndUserIdAndDel(p.getTeamId(), loginUserId, DELTYPE.NORMAL);
			if(tm!=null){
				TeamAuth ta=teamAuthDao.findByMemberIdAndDel(tm.getId(), DELTYPE.NORMAL);
				Role r=Cache.getRole(ta.getRoleId());
				if(r.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR)||r.getEnName().equals(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR)){
					creatorOrAdm=true;
				}
			}
		}
		if(!creatorOrAdm){
			throw new RuntimeException("无权限导出项目");
		}
		Thread thread = new Thread(new ProjectExportThread(loginUserId,projectId,rootPath,this.jdbcTpl,emailTaskBaseLink,emailSourceRootPath,xtHost));
		thread.start();
	    return "项目导出中,请耐心等待，下载地址会以邮件和通知的的形式发送给您，请及时查看！";
	}
	public String saveProjectImport(long loginUserId, long teamId,
			String unzipFileName,List<Map<String,Object>> userList) {
		String unzipPath=rootPath+File.separator+"projectImport"+File.separator+unzipFileName;
		Thread thread = new Thread(new ProjectImportThread(loginUserId,teamId,unzipPath,userList));
		thread.start();
		return "项目正在导入中，项目导入情况会以通知和邮件的形式发送给您，请及时查看！";
	}
	public boolean judgeExportOver(long loginUserId, long projectId) {
		if(ProjectExportThread.projectExportStatus.get(loginUserId+"_"+projectId)!=null){
			if(ProjectExportThread.projectExportStatus.get(loginUserId+"_"+projectId).equals("ing")){
				return false;
			}else if(ProjectExportThread.projectExportStatus.get(loginUserId+"_"+projectId).equals("over")){
				return true;
			}else{
				return false;
			}
		}else{
			throw new RuntimeException("该项目未曾导出");
		}
	}
	public void addTablesAndResource(long teamId, String unzipPath,long loginUserId,List<Map<String,Object>> userList) throws IOException {
		Project project=new Project();
		String tablePath=unzipPath+File.separator+"tables"+File.separator;
		this.readProjectXls(new File(tablePath+"project"+File.separator+"T_PROJECT.xls"),project,teamId,tablePath,loginUserId,userList);
		String projectId= projectImportProjectId.get(loginUserId+"_"+project.getId()).toString();
		//插件，引擎，资源拷贝
		copyRecursivelyFiles(unzipPath,projectId);
		//删除子文件及文件夹
		FileSystemUtils.deleteRecursively(new File(unzipPath));
		
	}
	public void copyRecursivelyFiles(String filePath,String projectId) throws IOException{
		    //资源文件的迁移
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
			if (os.toLowerCase().startsWith("win")) {
				resourceSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\upload\\"+projectId;
				resourceTargetFileDir=filePath+"\\file\\resource\\";
				pluginAndroidSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\plugin\\project\\android\\"+projectId;
				pluginIosSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\plugin\\project\\ios\\"+projectId;
				pluginAndroidTargetFileDir=filePath+"\\file\\plugin\\project\\android\\";
				pluginIosTargetFileDir=filePath+"\\file\\plugin\\project\\ios\\";
				engineAndroidSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\engine\\project\\android\\"+projectId;
				engineIosSrcFileDir="C:\\mas_upload\\coopDevelopment_private\\engine\\project\\ios\\"+projectId;
				engineAndroidTargetFileDir=filePath+"\\file\\engine\\project\\android\\";
				engineIosTargetFileDir=filePath+"\\file\\engine\\project\\ios\\";
			} else {
				resourceSrcFileDir=rootPath+"/upload/"+projectId;
				resourceTargetFileDir=filePath+"/file/resource/";
				pluginAndroidSrcFileDir=rootPath+"/plugin/project/android/"+projectId;
				pluginIosSrcFileDir=rootPath+"/plugin/project/ios/"+projectId;
				pluginAndroidTargetFileDir=filePath+"/file/plugin/project/android/"+projectId;
				pluginIosTargetFileDir=filePath+"/file/plugin/project/ios/"+projectId;
				engineAndroidSrcFileDir=rootPath+"/engine/project/android/"+projectId;
				engineIosSrcFileDir=rootPath+"/engine/project/ios/"+projectId;
				engineAndroidTargetFileDir=filePath+"/file/engine/project/android/"+projectId;
				engineIosTargetFileDir=filePath+"/file/engine/project/ios/"+projectId;
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
			//将目录下所有文件复制
			if( resourceTargetFile.exists()){
				FileSystemUtils.copyRecursively(resourceTargetFile,resourceSrcFile);
			}
			if(pluginAndroidTargetFile.exists()){
				FileSystemUtils.copyRecursively(pluginAndroidTargetFile,pluginAndroidSrcFile);
			}
			if(pluginIosTargetFile.exists()){
				FileSystemUtils.copyRecursively(pluginIosTargetFile,pluginIosSrcFile);
			}
			if(engineAndroidTargetFile.exists()){
				FileSystemUtils.copyRecursively(engineAndroidTargetFile,engineAndroidSrcFile);
			}
			if(engineIosTargetFile.exists()){
				FileSystemUtils.copyRecursively(engineIosTargetFile,engineIosSrcFile);
			}
	}
	 /**
     * 读取xls文件内容
	 * @param loginUserId 
     * 
     * @return List<XlsDto>对象
     * @throws IOException
     *             输入/输出(i/o)异常
     */
    private void readProjectXls(File xlxFile,Project project,long teamId,String tablePath, long loginUserId,List<Map<String,Object>> userList) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRowField=hssfSheet.getRow(0);
            	HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow == null) {
                    continue;
                }
                // 循环列Cell
                 for (int cellNum = 1; cellNum <=hssfRow.getLastCellNum(); cellNum++) {
                	 HSSFCell hssfRowFieldCell=hssfRowField.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 break;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(hssfRow.getCell(cellNum));
                    	 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("teamId")){
                    			 hssfRowFieldValue=String.valueOf(teamId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(project,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 projectDao.save(project);
                 projectImportProjectId.put(loginUserId+"_"+project.getId(),project.getId());
                 this.readProcessXls(new File(tablePath+"process"+File.separator+"T_PROCESS.xls"),project.getId(),tablePath);
                 this.readTaskGroupXls(new File(tablePath+"task"+File.separator+"T_TASK_GROUP.xls"),project.getId(),tablePath);
                 this.readBugModuleXls(new File(tablePath+"bug"+File.separator+"T_BUG_MODULE.xls"),project.getId(),tablePath);
                 this.readAppXls(new File(tablePath+"app"+File.separator+"T_APP.xls"),project.getId(),tablePath);
                 this.readDynamicXls(new File(tablePath+"dynamic"+File.separator+"T_DYNAMIC.xls"),project.getId(),tablePath);
                 this.readEngineXls(new File(tablePath+"engine"+File.separator+"T_ENGINE.xls"),project.getId(),tablePath);
                 this.readPluginXls(new File(tablePath+"plugin"+File.separator+"T_PLUGIN.xls"),project.getId(),tablePath);
                 this.readResourceXls(new File(tablePath+"resource"+File.separator+"T_RESOURCES.xls"),project.getId(),tablePath);
                 this.readTopicXls(new File(tablePath+"topic"+File.separator+"T_TOPIC.xls"),project.getId(),tablePath);
                 this.readDocumentXls(new File(tablePath+"document"+File.separator+"T_DOCUMENT.xls"),project.getId(),tablePath);
                 this.readProjectMemberXls(new File(tablePath+"project"+File.separator+"T_PROJECT_MEMBER.xls"),project.getId(),tablePath);
                 this.readProjectAllMemberXls(new File(tablePath+"project"+File.separator+"T_PROJECT_ALL_MEMBER.xls"),project.getId(),tablePath,userList);
                 //发送邮件
                 User user = this.userDao.findOne(loginUserId);
				 MailSenderInfo mailInfo = new MailSenderInfo();
				 StringBuffer center = new StringBuffer();
				 center.append("<p style=\"line-height:48px;margin-top:25px;margin-bottom:0\">Hi，"+user.getUserName()+"</p> <p style=\"line-")
				 .append("height:30px;margin:0\">您的项目："+project.getName()+"已成功导入。<a href='"+emailTaskBaseLink+"projectHome?projectId="+project.getId()+"'>访问链接</a></p>");
				mailInfo.setContent(center.toString());
				mailInfo.setSubject("【AppCan-协同开发】邮箱通知");
				if(null!=user.getBindEmail()){
					mailInfo.setToAddress(user.getBindEmail());
					SendMailTools.setXtHost(xtHost);
					SendMailTools.setEmailSourceRootPath(emailSourceRootPath);
					sendMailTool.sendMailByAsynchronousMode(mailInfo);
				}
				this.noticeService.addNotice(loginUserId,
						new Long[] {loginUserId}, NOTICE_MODULE_TYPE.PROJECT_IMPORT,
						new Object[] { project, emailTaskBaseLink+"projectHome?projectId="+project.getId() });
            }
        }
    }
    private void readResourceXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // String oldPluginId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                //oldPluginId=getValue(dinhang.getCell(0));
                 // 循环列Cell
           	    Resource resource=new Resource();
                 for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                		 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(resource,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 resourcesDao.save(resource);
            }
        }
    }
    private void readPluginXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
         String oldPluginId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                oldPluginId=getValue(dinhang.getCell(0));
                 // 循环列Cell
           	    Plugin plugin=new Plugin();
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                		 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(plugin,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 pluginDao.save(plugin);
                 this.readPluginVersionXls(new File(tablePath+"plugin"+File.separator+"T_PLUGIN_VERSION.xls"),plugin.getId(),oldPluginId,tablePath);
            }
        }
    }
    private void readPluginVersionXls(File xlxFile,long pluginId,String oldPluginId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                PluginVersion pluginVersion=new PluginVersion();
                //表格里的processId放在了第二列 
                //taskId
                String oldPluginIdXls=getValue(dinhang.getCell(0));
                if(oldPluginId.equals(oldPluginIdXls)){
                	// 循环列Cell
                    for (int cellNum =0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("pluginId")){
                       			 hssfRowFieldValue=String.valueOf(pluginId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(pluginVersion,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    pluginVersionDao.save(pluginVersion);
                }
            }
        }
    }
    private void readEngineXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
         //String oldAppId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldAppId=getValue(dinhang.getCell(0));
                 // 循环列Cell
           	    Engine engine=new Engine();
                 for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                		 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(engine,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 engineDao.save(engine);
            }
        }
    }
    private void readDynamicXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
         //String oldAppId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldAppId=getValue(dinhang.getCell(0));
                 // 循环列Cell
           	    Dynamic dynamic=new Dynamic();
                 for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                		 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("relationId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(dynamic,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 dynamicDao.save(dynamic);
            }
        }
    }
    private void readAppXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        String oldAppId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    oldAppId=getValue(dinhang.getCell(0));
                 // 循环列Cell
           	    App app=new App();
           	    long creatorUserId = 0;
           	    String relativeRepoPath="";
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                		 if(!hssfRowFieldValue.equals("null")){
                			 app.setProjectId(projectId);
                			 if(hssfRowFieldName.equals("name")){
                     			app.setName(hssfRowFieldValue);
                    		 }else if(hssfRowFieldName.equals("detail")){
                    			 app.setDetail(hssfRowFieldValue);
                    		 }else if(hssfRowFieldName.equals("appType")){
                    			 app.setAppType(Long.parseLong(hssfRowFieldValue));
                    		 }else if(hssfRowFieldName.equals("userId")){
                    			 creatorUserId=Long.parseLong(hssfRowFieldValue);
                    		 }else if(hssfRowFieldName.equals("relativeRepoPath")){
                    			 relativeRepoPath=hssfRowFieldValue;
                    		 }
                    	 }
                	 }
                 }
                 //重新创建一个的应用
                 try {
			       appService.addApp(app,creatorUserId);
			       System.out.println(localGitRoot+app.getRelativeRepoPath()+"======================");
			       //删除新生成的代码
			       FileSystemUtils.deleteRecursively(new File(localGitRoot+app.getRelativeRepoPath()));
			       //复制一份代码在新路径中
			       FileSystemUtils.copyRecursively(new File(localGitRoot+relativeRepoPath),new File(localGitRoot+app.getRelativeRepoPath()));
			      
				} catch (Exception e) {
					e.printStackTrace();
				}
                 this.readAppVersionXls(new File(tablePath+"app"+File.separator+"T_APP_VERSION.xls"),app.getId(),oldAppId,tablePath);
                 this.readAppChannelXls(new File(tablePath+"app"+File.separator+"T_APP_CHANNEL.xls"),app.getId(),oldAppId,tablePath);
                 //更新任务的appId
                 StringBuffer taskSql=new StringBuffer();
                 taskSql.append("update T_TASK set appId=").append(app.getId())
                 .append(" where id in (select id from (select id from T_TASK where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=")
                 .append(projectId).append(") and appId=").append(oldAppId).append(") xxx)");
                 this.jdbcTpl.execute(taskSql.toString());
                 //更新子任务的appId
                 StringBuffer taskLeafSql=new StringBuffer();
                 taskLeafSql.append("update T_TASK_LEAF set appId=").append(app.getId())
                 .append(" where id in (select id from (select id from T_TASK_LEAF where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=")
                 .append(projectId).append(") and appId=").append(oldAppId).append(") xxx)");
                 this.jdbcTpl.execute(taskLeafSql.toString());
                 //更新bug的appId
                 StringBuffer bugSql=new StringBuffer();
                 bugSql.append("update T_BUG set appId=").append(app.getId())
                 .append(" where id in (select id from (select id from T_BUG where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=")
                 .append(projectId).append(") and appId=").append(oldAppId).append(") xxx)");
                 this.jdbcTpl.execute(bugSql.toString());
            }
        }
    }
    private void readAppChannelXls(File xlxFile,long appId,String oldAppId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        //String oldAppVersionId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                //oldAppVersionId=getValue(dinhang.getCell(0));
                AppChannel appChannel=new AppChannel();
                //表格里的memberId放在了第一列
                String oldAppIdXls=getValue(dinhang.getCell(0));
                if(oldAppId.equals(oldAppIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("appId")){
                       			 hssfRowFieldValue=String.valueOf(appId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(appChannel,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    appChannelDao.save(appChannel);
                }
            }
        }
    }
    private void readAppVersionXls(File xlxFile,long appId,String oldAppId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        String oldAppVersionId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                oldAppVersionId=getValue(dinhang.getCell(0));
                AppVersion appVersion=new AppVersion();
                //表格里的memberId放在了第一列
                String oldAppIdXls=getValue(dinhang.getCell(1));
                if(oldAppId.equals(oldAppIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("appId")){
                       			 hssfRowFieldValue=String.valueOf(appId);
                       		 }
                       		 log.info("hssfRowFieldName===>"+hssfRowFieldName);
                       		 ReflectUtil.invokeSetMethod(appVersion,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    appVersionDao.save(appVersion);
                    this.readAppPackageXls(new File(tablePath+"app"+File.separator+"T_APP_PACKAGE.xls"),appVersion.getId(),oldAppVersionId,tablePath);
                    this.readAppWidgetXls(new File(tablePath+"app"+File.separator+"T_APP_WIDGET.xls"),appVersion.getId(),oldAppVersionId,tablePath);
                    this.readAppPatchXls(new File(tablePath+"app"+File.separator+"T_APP_PATCH.xls"),appVersion.getId(),oldAppVersionId,tablePath);
                }
            }
        }
    }
    private void readAppPatchXls(File xlxFile,long appVersionId,String oldAppVersionId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        //String oldAppVersionId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                //oldAppVersionId=getValue(dinhang.getCell(0));
                AppPatch appPatch=new AppPatch();
                //表格里的memberId放在了第一列
                String oldAppVersionIdXls=getValue(dinhang.getCell(0));
                if(oldAppVersionId.equals(oldAppVersionIdXls)){
                	// 循环列Cell
                    for (int cellNum =0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("baseAppVersionId")){
                       			 hssfRowFieldValue=String.valueOf(appVersionId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(appPatch,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    appPatchDao.save(appPatch);
                }
            }
        }
    }
    private void readAppWidgetXls(File xlxFile,long appVersionId,String oldAppVersionId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        //String oldAppVersionId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                //oldAppVersionId=getValue(dinhang.getCell(0));
                AppWidget appWidget=new AppWidget();
                //表格里的memberId放在了第一列
                String oldAppVersionIdXls=getValue(dinhang.getCell(0));
                if(oldAppVersionId.equals(oldAppVersionIdXls)){
                	// 循环列Cell
                    for (int cellNum =0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("appVersionId")){
                       			 hssfRowFieldValue=String.valueOf(appVersionId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(appWidget,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    appWidgetDao.save(appWidget);
                }
            }
        }
    }
    private void readAppPackageXls(File xlxFile,long appVersionId,String oldAppVersionId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        //String oldAppVersionId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                //oldAppVersionId=getValue(dinhang.getCell(0));
                AppPackage appPackage=new AppPackage();
                //表格里的memberId放在了第一列
                String oldAppVersionIdXls=getValue(dinhang.getCell(0));
                if(oldAppVersionId.equals(oldAppVersionIdXls)){
                	// 循环列Cell
                    for (int cellNum =0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("appVersionId")){
                       			 hssfRowFieldValue=String.valueOf(appVersionId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(appPackage,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    appPackageDao.save(appPackage);
                }
            }
        }
    }
    private void readBugModuleXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                String oldBugModuleId=getValue(dinhang.getCell(0));
                 // 循环列Cell
                 BugModule bugModule=new BugModule();
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                    	 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(bugModule,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 bugModuleDao.save(bugModule);
                 StringBuffer bugSql=new StringBuffer();
                 bugSql.append("update T_BUG set moduleId=").append(bugModule.getId())
                 .append(" where del=0 and moduleId=").append(oldBugModuleId)
                 .append(" and id in (select id from (select id from T_BUG where del=0 and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")) xxx)");
                this.jdbcTpl.update(bugSql.toString());
            }
        }
    }
    private void readTaskGroupXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                 // 循环列Cell
                 TaskGroup taskGroup=new TaskGroup();
                 String oldTaskGroupId=getValue(dinhang.getCell(0));
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                    	 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(taskGroup,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 taskGroupDao.save(taskGroup);
                 StringBuffer taskGroupSql=new StringBuffer();
                 taskGroupSql.append("update T_TASK set groupId=").append(taskGroup.getId())
                 .append(" where del=0 and id in (select id from (select id from T_TASK where del=0 and groupId=")
                 .append(oldTaskGroupId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=")
                 .append(projectId).append(")) xxx)");
                 this.jdbcTpl.execute(taskGroupSql.toString());
            }
        }
    }
    private void readProcessXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        String oldProcessId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                oldProcessId=getValue(dinhang.getCell(0));
                 // 循环列Cell
                 Process process=new Process();
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                    	 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 if(hssfRowFieldName.equals("endDate")||hssfRowFieldName.equals("startDate")){
                    			 JSONObject jsonobject = JSONObject.fromObject(hssfRowFieldValue);
                    			 long time=(long) jsonobject.get("time");
                    			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    			 hssfRowFieldValue=sdf.format(new Date(time)).toString();
                    		 }
                    		 ReflectUtil.invokeSetMethod(process,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 processDao.save(process);
                 this.readProcessMemberXls(new File(tablePath+"process"+File.separator+"T_PROCESS_MEMBER.xls"),process.getId(),oldProcessId,tablePath);
                 this.readTaskXls(new File(tablePath+"task"+File.separator+"T_TASK.xls"),process.getId(),oldProcessId,tablePath);
                 this.readBugXls(new File(tablePath+"bug"+File.separator+"T_BUG.xls"),process.getId(),oldProcessId,tablePath);
            }
        }
    }
    private void readDocumentXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        String oldDocumentId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                oldDocumentId=getValue(dinhang.getCell(0));
                 // 循环列Cell
                Document document=new Document();
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                    	 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(document,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 documentDao.save(document);
                 this.readDocumentChapterXls(new File(tablePath+"document"+File.separator+"T_DOCUMENT_CHAPTER.xls"),document.getId(),oldDocumentId,tablePath);
            }
        }
    }
    private void readDocumentChapterXls(File xlxFile,long documentId,String oldDocumentId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                DocumentChapter documentChapter=new DocumentChapter();
                //表格里的processId放在了第二列
                String oldDocumentIdXls=getValue(dinhang.getCell(1));
                //processMemberId
                String oldDocCId=getValue(dinhang.getCell(0));
                if(oldDocumentId.equals(oldDocumentIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("documentId")){
                       			 hssfRowFieldValue=String.valueOf(documentId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(documentChapter,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    documentChapterDao.save(documentChapter);
                    this.readDocumentMarkerXls(new File(tablePath+"document"+File.separator+"T_DOCUMENT_MARKER.xls"),documentChapter.getId(),oldDocCId,tablePath);
                }
            }
        }
    }
     private void readDocumentMarkerXls(File xlxFile,long docCId,String oldDocCId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                DocumentMarker documentMarker=new DocumentMarker();
                //表格里的processId放在了第二列 
                String oldDocCIdXls=getValue(dinhang.getCell(0));
                //taskId
                // String oldBugId=getValue(dinhang.getCell(0));
                if(oldDocCId.equals(oldDocCIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("docCId")){
                       			 hssfRowFieldValue=String.valueOf(docCId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(documentMarker,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    documentMarkerDao.save(documentMarker);
                }
            }
        }
    }
    private void readTopicXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        String oldTopicId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
                oldTopicId=getValue(dinhang.getCell(0));
                 // 循环列Cell
                 Topic topic=new Topic();
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                    	 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(topic,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 topicDao.save(topic);
                 this.readTopicMemberXls(new File(tablePath+"topic"+File.separator+"T_TOPIC_MEMBER.xls"),topic.getId(),oldTopicId,tablePath);
                 this.readTopicCommentXls(new File(tablePath+"topic"+File.separator+"T_TOPIC_COMMENT.xls"),topic.getId(),oldTopicId,tablePath);
                // this.readTaskXls(new File(tablePath+"task"+File.separator+"T_TASK.xls"),process.getId(),oldProcessId,tablePath);
                // this.readBugXls(new File(tablePath+"bug"+File.separator+"T_BUG.xls"),process.getId(),oldProcessId,tablePath);
            }
        }
    }
    private void readTopicCommentXls(File xlxFile,long topicId,String oldTopicId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TopicComment topicComment=new TopicComment();
                //表格里的processId放在了第二列 
                String oldTopicIdXls=getValue(dinhang.getCell(0));
                //taskId
                // String oldBugId=getValue(dinhang.getCell(0));
                if(oldTopicId.equals(oldTopicIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("topicId")){
                       			 hssfRowFieldValue=String.valueOf(topicComment);
                       		 }
                       		 ReflectUtil.invokeSetMethod(topicComment,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    topicCommentDao.save(topicComment);
                }
            }
        }
    }
    private void readBugXls(File xlxFile,long processId,String oldProcessId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                Bug bug=new Bug();
                //表格里的processId放在了第二列 
                String oldProcessIdXls=getValue(dinhang.getCell(1));
                //taskId
                String oldBugId=getValue(dinhang.getCell(0));
                if(oldProcessId.equals(oldProcessIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("processId")){
                       			 hssfRowFieldValue=String.valueOf(processId);
                       		 }
                       		if(hssfRowFieldName.equals("resolveAt")||hssfRowFieldName.equals("closeAt")){
                   			 JSONObject jsonobject = JSONObject.fromObject(hssfRowFieldValue);
                   			 long time=(long) jsonobject.get("time");
                   			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                   			 hssfRowFieldValue=sdf.format(new Date(time)).toString();
                   		     }
                       		 ReflectUtil.invokeSetMethod(bug,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    bugDao.save(bug);
                    this.readBugMemberXls(new File(tablePath+"bug"+File.separator+"T_BUG_MEMBER.xls"),bug.getId(),oldBugId,tablePath);
                    this.readBugMarkXls(new File(tablePath+"bug"+File.separator+"T_BUG_MARK.xls"),bug.getId(),oldBugId,tablePath);
                }
            }
        }
    }
    private void readBugMarkXls(File xlxFile,long bugId,String oldBugId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                BugMark bugMark=new BugMark();
                //表格里的processId放在了第二列
                String oldBugIdXls=getValue(dinhang.getCell(0));
                //processMemberId
                //String oldBugMemberId=getValue(dinhang.getCell(0));
                if(oldBugId.equals(oldBugIdXls)){
                	// 循环列Cell
                    for (int cellNum =0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("bugId")){
                       			 hssfRowFieldValue=String.valueOf(bugId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(bugMark,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    bugMarkDao.save(bugMark);
                }
            }
        }
    }
    private void readBugMemberXls(File xlxFile,long bugId,String oldBugId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                BugMember bugMember=new BugMember();
                //表格里的processId放在了第二列
                String oldBugIdXls=getValue(dinhang.getCell(1));
                //processMemberId
                String oldBugMemberId=getValue(dinhang.getCell(0));
                if(oldBugId.equals(oldBugIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("bugId")){
                       			 hssfRowFieldValue=String.valueOf(bugId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(bugMember,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    bugMemberDao.save(bugMember);
                    this.readBugAuthXls(new File(tablePath+"bug"+File.separator+"T_BUG_AUTH.xls"),bugMember.getId(),oldBugMemberId,tablePath);
                }
            }
        }
    }
    private void readTaskXls(File xlxFile,long processId,String oldProcessId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                Task task=new Task();
                //表格里的processId放在了第二列 
                String oldProcessIdXls=getValue(dinhang.getCell(1));
                //taskId
                String oldTaskId=getValue(dinhang.getCell(0));
                if(oldProcessId.equals(oldProcessIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("processId")){
                       			 hssfRowFieldValue=String.valueOf(processId);
                       		 }
                       		if(hssfRowFieldName.equals("deadline")||hssfRowFieldName.equals("lastStatusUpdateTime")||hssfRowFieldName.equals("finishDate")){
                   			 JSONObject jsonobject = JSONObject.fromObject(hssfRowFieldValue);
                   			 long time=(long) jsonobject.get("time");
                   			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                   			 hssfRowFieldValue=sdf.format(new Date(time)).toString();
                   		     }
                       		 ReflectUtil.invokeSetMethod(task,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    taskDao.save(task);
                    this.readTaskMemberXls(new File(tablePath+"task"+File.separator+"T_TASK_MEMBER.xls"),task.getId(),oldTaskId,tablePath);
                    this.readTaskCommentXls(new File(tablePath+"task"+File.separator+"T_TASK_COMMENT.xls"),task.getId(),oldTaskId,tablePath);
                    this.readTaskTagXls(new File(tablePath+"task"+File.separator+"T_TASK_TAG.xls"),task.getId(),oldTaskId,tablePath);
                    this.readTaskLeafXls(new File(tablePath+"task"+File.separator+"T_TASK_LEAF.xls"),task.getId(),oldTaskId,processId,tablePath);
                }
            }
        }
    }
    private void readTaskLeafXls(File xlxFile,long taskId,String oldTaskId,long processId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TaskLeaf taskLeaf=new TaskLeaf();
                //表格里的processId放在了第二列
                String topTaskId=getValue(dinhang.getCell(0));
                //processMemberId
                if(oldTaskId.equals(topTaskId)){
                	// 循环列Cell
                    for (int cellNum =0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("topTaskId")){
                       			hssfRowFieldValue=String.valueOf(taskId);
                       		 }
                       		 if(hssfRowFieldName.equals("processId")){
                       			hssfRowFieldValue=String.valueOf(processId);
                       		 }
                       		 if(hssfRowFieldName.equals("deadline")||hssfRowFieldName.equals("lastStatusUpdateTime")||hssfRowFieldName.equals("finishDate")){
                      			 JSONObject jsonobject = JSONObject.fromObject(hssfRowFieldValue);
                      			 long time=(long) jsonobject.get("time");
                      			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                      			 hssfRowFieldValue=sdf.format(new Date(time)).toString();
                      		     }
                       		 ReflectUtil.invokeSetMethod(taskLeaf,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    taskLeafDao.save(taskLeaf);
                }
            }
        }
    }
    private void readTaskCommentXls(File xlxFile,long taskId,String oldTaskId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TaskComment taskComment=new TaskComment();
                //表格里的processId放在了第二列
                String oldTaskIdXls=getValue(dinhang.getCell(0));
                //processMemberId
                //String oldTaskMemberId=getValue(dinhang.getCell(0));
                if(oldTaskId.equals(oldTaskIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("taskId")){
                       			 hssfRowFieldValue=String.valueOf(taskId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(taskComment,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    taskCommentDao.save(taskComment);
                }
            }
        }
    }
    private void readTaskMemberXls(File xlxFile,long taskId,String oldTaskId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TaskMember taskMember=new TaskMember();
                //表格里的processId放在了第二列
                String oldTaskIdXls=getValue(dinhang.getCell(1));
                //processMemberId
                String oldTaskMemberId=getValue(dinhang.getCell(0));
                if(oldTaskId.equals(oldTaskIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("taskId")){
                       			 hssfRowFieldValue=String.valueOf(taskId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(taskMember,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    taskMemberDao.save(taskMember);
                    this.readTaskAuthXls(new File(tablePath+"task"+File.separator+"T_TASK_AUTH.xls"),taskMember.getId(),oldTaskMemberId,tablePath);
                }
            }
        }
    }
    private void readTaskTagXls(File xlxFile,long taskId,String oldTaskId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TaskTag taskTag=new TaskTag();
                //表格里的processId放在了第二列
                String oldTaskIdXls=getValue(dinhang.getCell(0));
                if(oldTaskId.equals(oldTaskIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("taskId")){
                       			 hssfRowFieldValue=String.valueOf(taskId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(taskTag,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    taskTagDao.save(taskTag);
                }
            }
        }
    }
    private void readTaskAuthXls(File xlxFile,long taskMemberId,String oldTaskMemberId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TaskAuth taskAuth=new TaskAuth();
                //表格里的memberId放在了第一列
                String oldmemberIdXls=getValue(dinhang.getCell(0));
                if(oldTaskMemberId.equals(oldmemberIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("memberId")){
                       			 hssfRowFieldValue=String.valueOf(taskMemberId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(taskAuth,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    taskAuthDao.save(taskAuth);
                }
            }
        }
    }
    private void readBugAuthXls(File xlxFile,long bugMemberId,String oldBugMemberId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                BugAuth bugAuth=new BugAuth();
                //表格里的memberId放在了第一列
                String oldmemberIdXls=getValue(dinhang.getCell(0));
                if(oldBugMemberId.equals(oldmemberIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("memberId")){
                       			 hssfRowFieldValue=String.valueOf(bugMemberId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(bugAuth,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    bugAuthDao.save(bugAuth);
                }
            }
        }
    }
    private void readTopicMemberXls(File xlxFile,long topicId,String oldTopicId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TopicMember topicMember=new TopicMember();
                //表格里的processId放在了第二列
                String oldTopicIdXls=getValue(dinhang.getCell(1));
                //processMemberId
                String oldTopicMemberId=getValue(dinhang.getCell(0));
                if(oldTopicId.equals(oldTopicIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("topicId")){
                       			 hssfRowFieldValue=String.valueOf(topicId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(topicMember,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    topicMemberDao.save(topicMember);
                    this.readTopicAuthXls(new File(tablePath+"topic"+File.separator+"T_TOPIC_AUTH.xls"),topicMember.getId(),oldTopicMemberId,tablePath);
                }
            }
        }
    }
    private void readProcessMemberXls(File xlxFile,long processId,String oldProcessId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                ProcessMember processMember=new ProcessMember();
                //表格里的processId放在了第二列
                String oldProcessIdXls=getValue(dinhang.getCell(1));
                //processMemberId
                String oldProcessMemberId=getValue(dinhang.getCell(0));
                if(oldProcessId.equals(oldProcessIdXls)){
                	// 循环列Cell
                    for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("processId")){
                       			 hssfRowFieldValue=String.valueOf(processId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(processMember,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    processMemberDao.save(processMember);
                    this.readProcessAuthXls(new File(tablePath+"process"+File.separator+"T_PROCESS_AUTH.xls"),processMember.getId(),oldProcessMemberId,tablePath);
                }
            }
        }
    }
    private void readTopicAuthXls(File xlxFile,long topicMemberId,String oldTopicMemberId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                TopicAuth topicAuth=new TopicAuth();
                //表格里的memberId放在了第一列
                String oldmemberIdXls=getValue(dinhang.getCell(0));
                if(oldTopicMemberId.equals(oldmemberIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("memberId")){
                       			 hssfRowFieldValue=String.valueOf(topicMemberId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(topicAuth,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    topicAuthDao.save(topicAuth);
                }
            }
        }
    }
    private void readProcessAuthXls(File xlxFile,long processMemberId,String oldProcessMemberId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                ProcessAuth processAuth=new ProcessAuth();
                //表格里的memberId放在了第一列
                String oldmemberIdXls=getValue(dinhang.getCell(0));
                if(oldProcessMemberId.equals(oldmemberIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("memberId")){
                       			 hssfRowFieldValue=String.valueOf(processMemberId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(processAuth,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    processAuthDao.save(processAuth);
                }
            }
        }
    }
    private void readProjectMemberXls(File xlxFile,long projectId,String tablePath) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        String oldMemberId="";
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    oldMemberId=getValue(dinhang.getCell(0));
                 // 循环列Cell
           	    ProjectMember projectMember=new ProjectMember();
                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                	 //第一行的第n个值没有了，向下循环
                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                	 if(hssfRowFieldCell==null){
                		 continue;
                	 }else{
                		 String hssfRowFieldName=hssfRowFieldCell.toString();
                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                    	 if(!hssfRowFieldValue.equals("null")){
                    		 if(hssfRowFieldName.equals("projectId")){
                    			 hssfRowFieldValue=String.valueOf(projectId);
                    		 }
                    		 ReflectUtil.invokeSetMethod(projectMember,hssfRowFieldName,hssfRowFieldValue);
                    	 }
                	 }
                 }
                 projectMemberDao.save(projectMember);
                 this.readProjectAuthXls(new File(tablePath+"project"+File.separator+"T_PROJECT_AUTH.xls"),projectMember.getId(),oldMemberId,tablePath);
                
            }
        }
    }
    private void readProjectAllMemberXls(File xlxFile,long projectId,String tablePath,List<Map<String,Object>> userList) throws IOException {
        //InputStream is = new FileInputStream(xlxFile);
        //HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
//        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
//            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
//            if (hssfSheet == null) {
//                break;
//            }
//            // 循环行Row
//            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
//            	//第一行目录
//                HSSFRow diyihang=hssfSheet.getRow(0);
//                //第n行
//            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
//                if (dinhang == null) {
//                    break;
//                }
//                //第n行的第一个值
//                 // 循环列Cell
//           	    long oldUserId=0;
//           	    long newUserId=0;
//                 for (int cellNum = 1; cellNum <=dinhang.getLastCellNum(); cellNum++) {
//                	 //第一行的第n个值没有了，向下循环
//                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
//                	 if(hssfRowFieldCell==null){
//                		 continue;
//                	 }else{
//                		 String hssfRowFieldName=hssfRowFieldCell.toString();
//                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
//                    	 if(!hssfRowFieldValue.equals("null")){
//                    		 if(hssfRowFieldName.equals("oldUserId")){
//                    			 oldUserId=Long.parseLong(hssfRowFieldValue);
//                    		 }
//							 if(hssfRowFieldName.equals("newUserId")){
//							     newUserId=Long.parseLong(hssfRowFieldValue);continue;                			 
//							 }
//                    	 }
//                	 }
//                 }
    	        for(int i=0;i<userList.size();i++){
    	         long oldUserId= Long.parseLong(userList.get(i).get("oldUserId").toString());
            	 long newUserId=Long.parseLong(userList.get(i).get("newUserId").toString());
                 User newUser=userDao.findByIdAndDel(newUserId, DELTYPE.NORMAL);
                 if(newUser==null){
                	 throw new RuntimeException("userId 为："+newUserId+"的成员不存在");
                 }
                 String newUserName=newUser.getUserName();
                 List<String> sqlList=new ArrayList<String>();
                 //更新项目成员
                 StringBuffer projectMemberSql=new StringBuffer();
                 projectMemberSql.append("update T_PROJECT_MEMBER set userId =").append(newUserId).append(" where del=0 and  userId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(projectMemberSql.toString());
                 //更新流程成员
                 StringBuffer processMemberSql=new StringBuffer();
                 processMemberSql.append("update T_PROCESS_MEMBER set userId =").append(newUserId).append(" where del=0 and  userId=").append(oldUserId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(processMemberSql.toString());
                 //更新task完成人
                 StringBuffer taskSql=new StringBuffer();
                 taskSql.append("update T_TASK set finishUserId=").append(newUserId).append(" where del=0 and finishUserId=").append(oldUserId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(taskSql.toString());
                 //更新子任务完成人
                 StringBuffer taskLeafSql1=new StringBuffer();
                 taskLeafSql1.append("update T_TASK_LEAF set finishUserId=").append(newUserId).append(" where del=0 and finishUserId=").append(oldUserId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(taskLeafSql1.toString());
                 //更新子任务负责人
                 StringBuffer taskLeafSql2=new StringBuffer();
                 taskLeafSql2.append("update T_TASK_LEAF set managerUserId=").append(newUserId).append(" where del=0 and managerUserId=").append(oldUserId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(taskLeafSql2.toString());
                 //更新任务成员
                 StringBuffer taskMemberSql=new StringBuffer();
                 taskMemberSql.append("update T_TASK_MEMBER set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and taskId in (select id from T_TASK where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(taskMemberSql.toString()); 
                 //更新任务分组排序成员
                 StringBuffer taskGroupSortSql=new StringBuffer();
                 taskGroupSortSql.append("update T_TASK_GROUP_SORT set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(taskGroupSortSql.toString()); 
                 //更新任务评论成员
                 StringBuffer taskCommentSql=new StringBuffer();
                 taskCommentSql.append("update T_TASK_COMMENT set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and taskId in (select id from T_TASK where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(taskCommentSql.toString()); 
                 //更新bug解决人
                 StringBuffer bugSql1=new StringBuffer();
                 bugSql1.append("update T_BUG set resolveUserId=").append(newUserId).append(" where del=0 and resolveUserId=").append(oldUserId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(bugSql1.toString());
                 //更新bug关闭人
                 StringBuffer bugSql2=new StringBuffer();
                 bugSql2.append("update T_BUG set closeUserId=").append(newUserId).append(" where del=0 and closeUserId=").append(oldUserId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(bugSql2.toString());
                 //更新bug最后修改人
                 StringBuffer bugSql3=new StringBuffer();
                 bugSql3.append("update T_BUG set lastModifyUserId=").append(newUserId).append(" where del=0 and lastModifyUserId=").append(oldUserId).append(" and processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(bugSql3.toString());
                 //更新bug成员
                 StringBuffer bugMemberSql=new StringBuffer();
                 bugMemberSql.append("update T_BUG_MEMBER set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and bugId in (select id from T_BUG where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(bugMemberSql.toString()); 
                 //更新bug备注成员
                 StringBuffer bugMarkSql=new StringBuffer();
                 bugMarkSql.append("update T_BUG_MARK set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and bugId in (select id from T_BUG where del=0 and  processId in (select id from T_PROCESS where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(bugMarkSql.toString());
                 //更新bug模块创建者
                 StringBuffer bugModuleSql1=new StringBuffer();
                 bugModuleSql1.append("update T_BUG_MODULE set creatorId=").append(newUserId).append(" where del=0 and creatorId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(bugModuleSql1.toString()); 
                 //更新bug模块负责人
                 StringBuffer bugModuleSql2=new StringBuffer();
                 bugModuleSql2.append("update T_BUG_MODULE set managerId=").append(newUserId).append(" where del=0 and managerId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(bugModuleSql2.toString());
                 //修改动态操作人
                 StringBuffer dynamicSql=new StringBuffer();
                 dynamicSql.append("update T_DYNAMIC set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and relationId=").append(projectId);
                 sqlList.add(dynamicSql.toString());
                 //修改应用创建者
                 StringBuffer appSql=new StringBuffer();
                 appSql.append("update T_APP set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(appSql.toString());
                 //修改应用版本创建者
                 StringBuffer appVersionSql=new StringBuffer();
                 appVersionSql.append("update T_APP_VERSION set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(appVersionSql.toString());
                 //修改应用包创建者
                 StringBuffer appPackageSql=new StringBuffer();
                 appPackageSql.append("update T_APP_PACKAGE set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and appVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(appPackageSql.toString());
                 //修改widget创建者
                 StringBuffer appWidgetSql=new StringBuffer();
                 appWidgetSql.append("update T_APP_WIDGET set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and appVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(appWidgetSql.toString());
                 //修改补丁包创建者
                 StringBuffer appPatchSql=new StringBuffer();
                 appPatchSql.append("update T_APP_PATCH set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and baseAppVersionId in (select id from T_APP_VERSION where del=0 and appId in (select id from T_APP where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(appPatchSql.toString());
                 //修改讨论创建者
                 StringBuffer topicSql=new StringBuffer();
                 topicSql.append("update T_TOPIC set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(topicSql.toString());
                 //修改讨论评论
                 StringBuffer topicCommentSql=new StringBuffer();
                 topicCommentSql.append("update T_TOPIC_COMMENT set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and topicId in (select id from T_TOPIC where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(topicCommentSql.toString());
                 //修改讨论成员
                 StringBuffer topicMemberSql=new StringBuffer();
                 topicMemberSql.append("update T_TOPIC_MEMBER set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and topicId in (select id from T_TOPIC where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(topicMemberSql.toString());
                 //修改文档创建者
                 StringBuffer documentSql=new StringBuffer();
                 documentSql.append("update T_DOCUMENT set userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(documentSql.toString());
                 //修改文档章节创建者
                 StringBuffer documentChapterSql=new StringBuffer();
                 documentChapterSql.append("update T_DOCUMENT_CHAPTER set userId=").append(newUserId).append(" where userId=").append(oldUserId).append(" and documentId in (select id from T_DOCUMENT where del=0 and projectId=").append(projectId).append(")");
                 sqlList.add(documentChapterSql.toString());
                 //修改文档标记创建者
                 StringBuffer documentMarkSql=new StringBuffer();
                 documentMarkSql.append("update T_DOCUMENT_MARKER set userName='").append(newUserName).append("',userId=").append(newUserId).append(" where userId=").append(oldUserId).append(" and docCId in (select id from T_DOCUMENT_CHAPTER where del=0 and documentId in (select id from T_DOCUMENT where del=0 and projectId=").append(projectId).append("))");
                 sqlList.add(documentMarkSql.toString());
                 //修改资源创建
                 StringBuffer resourceSql=new StringBuffer();
                 resourceSql.append("update T_RESOURCES set userName='").append(newUserName).append("',userId=").append(newUserId).append(" where del=0 and userId=").append(oldUserId).append(" and projectId=").append(projectId);
                 sqlList.add(resourceSql.toString());
                 for(String sql:sqlList){
                	 this.jdbcTpl.execute(sql);
                 }
            }
    }
    private void readProjectAuthXls(File xlxFile,long memberId,String oldmemberId,String tablePath) throws IOException {
    	InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                break;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
            	//第一行目录
                HSSFRow diyihang=hssfSheet.getRow(0);
                //第n行
            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
                if (dinhang == null) {
                    break;
                }
                //第n行的第一个值
           	    //oldMemberId=;
                ProjectAuth projectAuth=new ProjectAuth();
                //表格里的memberId放在了第一列
                String oldmemberIdXls=getValue(dinhang.getCell(0));
                if(oldmemberId.equals(oldmemberIdXls)){
                	// 循环列Cell
                    for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
                   	 //第一行的第n个值没有了，向下循环
                   	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
                   	 if(hssfRowFieldCell==null){
                   		 continue;
                   	 }else{
                   		 String hssfRowFieldName=hssfRowFieldCell.toString();
                   		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
                       	 if(!hssfRowFieldValue.equals("null")){
                       		 if(hssfRowFieldName.equals("memberId")){
                       			 hssfRowFieldValue=String.valueOf(memberId);
                       		 }
                       		 ReflectUtil.invokeSetMethod(projectAuth,hssfRowFieldName,hssfRowFieldValue);
                       	 }
                   	 }
                    }
                    projectAuthDao.save(projectAuth);
                }
            }
        }
    }
    /**
     * 得到Excel表中的值
     * 
     * @param hssfCell
     *            Excel中的每一个格子
     * @return Excel中每一个格子中的值
     */
    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
//    	 return String.valueOf(hssfCell.getStringCellValue());
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
   
	public Map<String,Object> saveProjectImportFile(long loginUserId, MultipartFile srcZip) {
		String os = System.getProperty("os.name");
		String projectImportDir="";
		//获取文件名称
		String fileName =System.currentTimeMillis()+srcZip.getOriginalFilename();
		if (os.toLowerCase().startsWith("win")) {
			projectImportDir="C:\\mas_upload\\coopDevelopment_private\\projectImport\\";
		}else{
			projectImportDir=rootPath+"/projectImport/";
		}
		File targetFile = new File(projectImportDir);  
        if(!targetFile.exists()){  
            targetFile.mkdirs();  
        }
        //获取绝对路径
        File destDir =new File(targetFile.getAbsolutePath()+File.separator+fileName);
    	//复制文件
    	try {
			srcZip.transferTo(destDir);
			//获取上一级目录
			String unzipPath=destDir.getParentFile().getAbsolutePath()+File.separator+fileName.replace(".zip","");
			ZipUtil.unzip(destDir.getAbsolutePath(),unzipPath);
			//删除上传的压缩包
			destDir.delete();
			String tablePath=unzipPath+File.separator+"tables"+File.separator;
			String xlxFile=tablePath+"project"+File.separator+"T_PROJECT_ALL_MEMBER.xls";
			//this.readProjectAllMemberXls(new File(tablePath+"project"+File.separator+"T_PROJECT_ALL_MEMBER.xls"),project.getId(),tablePath);
			InputStream is = new FileInputStream(xlxFile);
	        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
	        List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
	        // 循环工作表Sheet
	        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
	            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
	            if (hssfSheet == null) {
	                break;
	            }
	            // 循环行Row
	            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
	            	//第一行目录
	                HSSFRow diyihang=hssfSheet.getRow(0);
	                //第n行
	            	HSSFRow dinhang = hssfSheet.getRow(rowNum);
	                if (dinhang == null) {
	                    break;
	                }
	                Map<String,Object> map=new HashMap<String,Object>();
	                //第n行的第一个值
	                 // 循环列Cell
	           	    long oldUserId=0;
	           	    long newUserId=0;
	           	    String oldAccount;
	                 for (int cellNum = 0; cellNum <=dinhang.getLastCellNum(); cellNum++) {
	                	 //第一行的第n个值没有了，向下循环
	                	 HSSFCell hssfRowFieldCell=diyihang.getCell(cellNum);
	                	 if(hssfRowFieldCell==null){
	                		 continue;
	                	 }else{
	                		 String hssfRowFieldName=hssfRowFieldCell.toString();
	                		 String hssfRowFieldValue=getValue(dinhang.getCell(cellNum));
	                    	 if(!hssfRowFieldValue.equals("null")){
	                    		 if(hssfRowFieldName.equals("oldUserId")){
	                    			 oldUserId=Long.parseLong(hssfRowFieldValue);
	                    			 map.put("oldUserId", oldUserId);
	                    		 }
								 if(hssfRowFieldName.equals("newUserId")){
								     newUserId=Long.parseLong(hssfRowFieldValue);continue;
								     //map.put("newUserId", oldUserId);
								 }
								 if(hssfRowFieldName.equals("oldAccount")){
									 oldAccount=hssfRowFieldValue;
									 map.put("oldAccount", oldAccount);
								 }
	                    	 }
	                	 }
	                 }
	                 list.add(map);
	            }
	        }
	        Map<String,Object> map=new HashMap<String,Object>();
	        map.put("object",list);
	        map.put("unzipFileName",fileName.replace(".zip",""));
	        return map;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
	}
}
    