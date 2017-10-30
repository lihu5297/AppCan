package org.zywx.cooldev.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.SOURCE_TYPE;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.query.ResourceQuery;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.Tools;
import org.zywx.cooldev.util.ZipUtil;


@Controller
@RequestMapping(value = "/resource")
public class ResourcesController extends BaseController{
	
	
	@Value("${resource.baseDir}")
	private String baseDir;
	
	@Value("${shellPath}")
	private String shellPath;
	/**
	 * 根据路径判断文件是否已经存在
	 * @param filePath
	 * @param loginUserId
	 * @return Map<String,Object>
	 * @user jingjian.wu
	 * @date 2015年8月22日 下午3:51:45
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value="/exist",method = RequestMethod.GET)
	public Map<String, Object> exist(String filePath,String fileName,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		try {
			log.info("judge resource if exist:filePath -->"+filePath+",fileName -->"+fileName);
			boolean exist= this.resourcesService.exist(filePath,fileName);
			//根据路径判断文件是否已经存在
			if(exist){
				return this.getSuccessMap("yes");
			}
			return this.getSuccessMap("no");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.getFailedMap("error!");
	}
	
	/**
	 * 创建资源 
	 * @param res
	 * @param loginUserId
	 * @return Map<String,Object>
	 * @user jingjian.wu
	 * @date 2015年8月21日 上午11:19:23
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> addResource(Resource res,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId,@RequestParam(required=false,defaultValue="true") boolean recordDynanmic){
		try {
			if(res.getName()!=null&&res.getName().length()>50){
				this.getFailedMap("资源名称不能超过50个字符");
			}
			log.info("add res "+res.toStr()+",名称长度:"+res.getName().length());
			if(null==res.getSourceType()){
				res.setSourceType(SOURCE_TYPE.NORMAL);
			}
			
			if(res.getProjectId()<1){
				return this.getFailedMap("param projectId is not available!");
			}
			boolean isFile = Tools.isFileName(res.getName());
			if(!isFile){
				return this.getFailedMap("file Name is not available!");//名称不合法
			}
			if(StringUtils.isBlank(res.getFilePath())){//路径不能为空
				return this.getFailedMap("filePath can't be null!");
			}
			if(res.getParentId()==-1){//不存在父路径,往根目录下传
				String filePath = "/"+res.getProjectId()+"/";
				if(!filePath.equals(res.getFilePath())){
					return this.getFailedMap("file Path is not available!");	
				}
			}else{//存在父级目录
				Resource parentRes = this.resourcesService.findOne(res.getParentId());
				if(!(parentRes.getFilePath()+parentRes.getName()+"/").equals(res.getFilePath())){
					return this.getFailedMap("file Path is not match with parent path!");
				}
			}
			boolean exist= this.resourcesService.exist(res.getFilePath(),res.getName());
			if(exist){
				return this.getFailedMap("file already exist!");
			}
			
			User user = this.userService.findUserById(loginUserId);
			res.setUserId(loginUserId);
			res.setUserName(user.getUserName()==null?user.getAccount():user.getUserName());
			Resource resource = this.resourcesService.addResources(res);
			//增加动态
			if(recordDynanmic){
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.RESOURCE_ADD,res.getProjectId(), new Object[]{resource});
			}
			return this.getSuccessMap(resource);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
     * @Description: 查询资源列表/点击某一个资源查看下一级资源
     * @param @param projectId
     * @param @param relation   我创建,我参与    CREATE,ACTOR
     * @param @param loginUserId
     * @param @param parentId 父亲节点,默认为-1
     * @param @return 
     * @return Map<String,Object>    返回类型
	 * @user jingjian.wu
	 * @date 2015年8月20日 下午8:07:36
     * @throws
	 */
//	@Cacheable(value="Resource_findResourceList",key="#relation+'_'+#loginUserId+'_'+#parentId+'_'+#sourceType")
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView findResourceList(String relation,
			HttpServletRequest request,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId,Long parentId,
			@RequestParam(value="sourceType",defaultValue="NORMAL")SOURCE_TYPE sourceType,
			ResourceQuery query){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> parentmap = new HashMap<String, Object>();
		parentmap.put("parentName", "");//
		parentmap.put("parentId", parentId);//父亲节点ID
		parentmap.put("filePath", "");
		parentmap.put("projectId", query.getProjectId());//项目id
		try {
			
			String sPageNo      = request.getParameter("pageNo");
			String sPageSize    = request.getParameter("pageSize");

			int pageNo       = 0;
			int pageSize     = 20;
			
			try {
				if(sPageNo != null) {
					pageNo		= Integer.parseInt(sPageNo)-1;
				}
				if(sPageSize != null) {
					pageSize	= Integer.parseInt(sPageSize);
				}
				
			} catch (NumberFormatException nfe) {
				nfe.printStackTrace();
				return this.getFailedModel(nfe.getMessage());
			}

			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "type","id");
			
			log.info("search resource list ,params: pageNo ="+pageNo+",pageSize = "+pageSize+",sourceType ="+sourceType+",projectId="+query.getProjectId()+",relation="+relation+",parentId="+parentId+",loginUserId="+loginUserId);
			long startTime = System.currentTimeMillis();
			String required = (ENTITY_TYPE.RESOURCE + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
			// 项目成员权限
			Map<Long, List<String>> pMapAsProjectMember=null;
			if(query.getProjectId()==null || -1==query.getProjectId()){
				pMapAsProjectMember= projectService.permissionMap(required, loginUserId);
			}else{
				 pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required, loginUserId,query.getProjectId());
			}
			//1.取出资源创建者的所有权限集合
			Role role = Cache.getRole(ENTITY_TYPE.RESOURCE+"_"+ROLE_TYPE.CREATOR);
			List<Permission> pers = role.getPermissions();
			//2.判断1中取出的权限集合中是否有required权限
			boolean createAlready = false;//标识当前人是否已经创建过资源
			List<Long> createResprojectIds=this.projectService.getProjectByCreater(loginUserId);
			if (pers != null && pers.size() > 0) {
				for (Permission p : pers) {
					//3.没有.继续
					//4.如果有,查看当前登录人创建过资源的项目createResprojectIds. 如果createResprojectIds不为空,将createAlready值改为true
					if(p.getEnName().equals("resource_retrieve")){
						if(createResprojectIds != null && createResprojectIds.size()>0){
							createAlready=true;
							break;
						}
					}
				}
			}
			
			if(pMapAsProjectMember.keySet().size()<1 && !createAlready){
				//return  this.getFailedModel("permission denied!");
				map.put("list", list);
				map.put("total", 0);
				map.put("sourceType", sourceType);
				map.put("parent", parentmap);
				map.put("project", this.projectService.getProject(null==query.getProjectId()?-1L:query.getProjectId(),loginUserId));
				return this.getSuccessModel(map);
			}
			//标识当前人是否在查询的项目下(query.getProjectId())创建过资源
			boolean createAlreadyInQueryProject = false;
			//5. 判断createResprojectIds中是否有query.getProjectId().如果有,将createAlreadyInQueryProject改为true
			if (createResprojectIds != null && createResprojectIds.size() > 0) {
				for(int i=0;i<createResprojectIds.size();i++){
					if(createResprojectIds.get(i) == query.getProjectId()){
						createAlreadyInQueryProject=true;
						break;
					}
				}
			}
			
			if(null!=query.getProjectId() && -1!=query.getProjectId() && ( !pMapAsProjectMember.containsKey(query.getProjectId())  &&  !createAlreadyInQueryProject )){
				//return this.getFailedModel("permission denied,you have no permission for project with projectId:"+projectId);
				map.put("list", list);
				map.put("total", 0);
				map.put("sourceType", sourceType);
				map.put("parent", parentmap);
				Map<String, Integer> permissionMap = new HashMap<>();
				map.put("permissions", permissionMap);
				map.put("project", this.projectService.getProject(null==query.getProjectId()?-1L:query.getProjectId(),loginUserId));
				return this.getSuccessModel(map);
			}
			//查询某个文件夹下面的资源
			
			if(null !=parentId && -1 !=parentId){
				Resource res = this.resourcesService.findOne(parentId);
				if(null==res){
					//return this.getFailedModel("parentId "+parentId+" is not available!");
					map.put("list", list);
					map.put("total", 0);
					map.put("sourceType", sourceType);
					map.put("parent", parentmap);
					map.put("project", this.projectService.getProject(null==query.getProjectId()?-1L:query.getProjectId(),loginUserId));
					return this.getSuccessModel(map);
				}
				parentmap.put("filePath", res.getFilePath()+res.getName());//父亲节点路径+文件名;返回给前端,	前端在此文件夹下创建文件,filePath需要传这个.
				parentmap.put("parentId", res.getId());//父亲节点ID
				parentmap.put("parentName", res.getName());//父亲节点名称
				parentmap.put("projectId", res.getProjectId());//项目id
				
				String fullPathIdStr = this.resourcesService.findFullPath(parentId);
				parentmap.put("fullPathIds", fullPathIdStr);//项目id
				
				List<String> permissions = pMapAsProjectMember.get(query.getProjectId());
				Map<String, Integer> permissionMap = new HashMap<>();
				if(permissions != null && permissions.size() > 0) {
					for(String str : permissions) {
						permissionMap.put(str, 1);
					}
				}
				map.put("permissions", permissionMap);
			}else{//父亲节点名称
				if(null!=query.getProjectId()){//如果有项目的话,则知道路径是哪里
					parentmap.put("filePath", "/"+query.getProjectId()+"/");//父亲节点路径+文件名;返回给前端,	前端在此文件夹下创建文件,filePath需要传这个.
				}
				List<String> permissions = pMapAsProjectMember.get(query.getProjectId());
				Map<String, Integer> permissionMap = new HashMap<>();
				if(permissions != null && permissions.size() > 0) {
					for(String str : permissions) {
						permissionMap.put(str, 1);
					}
				}
				map.put("permissions", permissionMap);
			}
			Map<String,Object> page = this.resourcesService.findList(loginUserId,relation,parentId,pMapAsProjectMember,sourceType,query,createResprojectIds,pageable);
			list = (List<Map<String, Object>>) page.get("list");
			map.put("list", list);
			map.put("total", page.get("total"));
			map.put("parent", parentmap);
			map.put("sourceType", sourceType);
			map.put("defaultDir", page.get("defaultDir"));
			map.put("project", this.projectService.getProject(null==query.getProjectId()?-1L:query.getProjectId(),loginUserId));
			long endTime = System.currentTimeMillis();
			
			log.info("resource search total Time--->"+(endTime-startTime));
//			log.info("------------result---------->"+((null!=map)?map.toString():""));
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	
	/**
	 * 上传时候,列出可选的上传目录列表
     * @Description: 查询一个项目下的,我创建的目录信息,方便上传资源的时候选择放到哪个目录下面
     * @param @param projectId
     * @param @param loginUserId
     * @param @return 
     * @return Map<String,Object>    返回类型
	 * @user jingjian.wu
	 * @date 2015年8月20日 下午8:08:51
     * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/dir",method = RequestMethod.GET)
	public Map<String, Object> findResourceDir(Long projectId,
			@RequestParam(value="sourceType",defaultValue="NORMAL") SOURCE_TYPE sourceType,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		List<Resource> list = null;
		try {
			log.info("show dir : projectId="+projectId+",loginUserId="+loginUserId);
			if(null ==projectId){
				return this.getFailedMap("projectId can't be null");
			}
			list = this.resourcesService.findDir(loginUserId, projectId,sourceType);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(list);
	}
	
	/**
	 * 上传时候选择目录,需要select选择列表(不是树状结构)
	 * @user jingjian.wu
	 * @date 2015年9月10日 上午10:18:02
	 */
	@ResponseBody
	@RequestMapping(value = "/dirlist",method = RequestMethod.GET)
	public Map<String, Object> findResourceDirList(Long projectId,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		List<Resource> list = null;
		try {
			log.info("show dir list: projectId="+projectId+",loginUserId="+loginUserId);
			if(null ==projectId){
				return this.getFailedMap("projectId can't be null");
			}
			list = this.resourcesService.findDirList(loginUserId, projectId);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(list);
	}
	
	/**
	 * 打包下载
	 * @param id
	 * @param loginUserId
	 * @return Map<String,Object>
	 * @user jingjian.wu
	 * @date 2015年8月21日 下午5:20:09
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/down/{resourceId}",method = RequestMethod.GET)
	public Map<String, Object> pkgDown(@PathVariable("resourceId")Long resourceId,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId,
			HttpServletRequest request ,HttpServletResponse response){
		
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			
			log.info("download resource for id:"+resourceId+",loginUserId="+loginUserId);
			Resource res = resourcesService.findOne(resourceId);
			if(null==res){
				return this.getFailedMap("请求的资源不存在");
			}
			if(res.getSourceType().compareTo(SOURCE_TYPE.PROJECT)== 0 || res.getSourceType().compareTo(SOURCE_TYPE.APP) == 0 || res.getSourceType().compareTo( SOURCE_TYPE.DATAMODEL) == 0 || res.getSourceType().compareTo( SOURCE_TYPE.INTERFACE) == 0){
				//申请类的业务中，关联资源的查看不判断权限
			}else{
				String required = (ENTITY_TYPE.RESOURCE + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
				
				// 项目成员权限
	//			Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required, loginUserId);			
	//			if(null==pMapAsProjectMember || pMapAsProjectMember.size()<1){
	//				return this.getFailedMap("对不起,您没有权限");
	//			}
	//			if(!pMapAsProjectMember.containsKey(res.getProjectId())){
	//				return this.getFailedMap("对不起,您没有权限");
	//			}
				Set<Long> projectIds = this.projectService.getProjectsByRequiredAndLoginUserId(required, loginUserId);
				if(null==projectIds || projectIds.size()<1){
					return this.getFailedMap("对不起,您没有权限");
				}
				if(!projectIds.contains(res.getProjectId())){
					return this.getFailedMap("对不起,您没有权限");
				}
			}
			String filename = res.getName();
			if(res.getType().equals("dir")){
		        long time = System.currentTimeMillis();  
		        String t = String.valueOf(time);
		        filename=res.getProjectId()+"_"+res.getUserId()+"_"+t+".zip";
				String src=baseDir+res.getFilePath()+res.getName();
				String dest=baseDir+res.getFilePath()+filename;
				ZipUtil.zip(src, dest);
				String cmd = "sh "+shellPath+"coopdev_file/writeFileName.sh "+dest;
				String ret = this.execShell(cmd);
				log.info(String.format("write fileName to txt cmd[%s] ret[%s]", cmd, ret));
			}
			log.info("origin name -->"+filename);
			
			//资源下载nginx方法
			
			String url=res.getFilePath()+filename;
			log.info("=======>资源下载路径："+url);
			log.info("=======>资源下载路径：/nginxdown"+url);
			map.put("filePath", res.getFilePath());
			map.put("name", filename);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(map);
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String filename = "书本";
		System.out.println(filename);
		filename  = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
		System.out.println(filename);
	}
	
	//转移
	@ResponseBody
	@RequestMapping(value = "/transfer/{srcId}/{targetId}",method = RequestMethod.PUT)
	public Map<String, Object> transfer(@PathVariable("srcId")Long srcId,
			@PathVariable("targetId")Long targetId,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId,
			@RequestParam(value="targetType",defaultValue="NORMAL")SOURCE_TYPE targetType){
		try {
			log.info("transfer resource  srcId-->:"+srcId+",targetId -->"+targetId);
			String msg = this.resourcesService.updateTransfer(srcId, targetId,targetType);
			if(!"".equals(msg)){
				return this.getFailedMap(msg);
			}
			Resource srcRes = this.resourcesService.findOne(srcId);
			Resource tarRes = this.resourcesService.findOne(targetId);
			//增加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.RESOURCE_TRANSFER,srcRes.getProjectId(), new Object[]{srcRes,targetId==-1?"根目录":tarRes});
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getAffectMap();
	}
	//删除
	@ResponseBody
	@RequestMapping(value = "/{resourceId}",method = RequestMethod.DELETE)
	public Map<String, Object> delete(@PathVariable("resourceId")Long resourceId,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		try {
			log.info("delete resource : resourceId-->"+resourceId+",loginUserId-->"+loginUserId);
			Resource res = this.resourcesService.deleteResource(resourceId,loginUserId);
			if(null==res){
				return this.getFailedMap("所选资源不存在");
			}
			//增加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.RESOURCE_DEL,res.getProjectId(), new Object[]{res});
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getAffectMap();
	}
	
	//详情
	@ResponseBody
	@RequestMapping(value = "/{resourceId}",method = RequestMethod.GET)
	public Map<String, Object> detail(@PathVariable("resourceId")Long resourceId,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		Resource res = null;
		try {
			log.info("resource detail: resourceId:"+resourceId+",loginUserId:"+loginUserId);
			res = this.resourcesService.findOne(resourceId);
			Long projectId= resourcesService.findProjectIdByResourceId(resourceId);
			Project p = this.projectService.getProject(projectId);
			res.setProjectName(p==null||p.getName()==null?"":p.getName());
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(res);
	}
	
	/**
	 * 通过parentId 获取文件要传到的目标文件夹路径
	 * @param resourceId
	 * @param loginUserId
	 * @return Map<String,Object>
	 * @user jingjian.wu
	 * @date 2015年9月6日 下午2:20:57
	 * @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/getUploadDir/{projectId}/{resourceId}",method = RequestMethod.GET)
	public Map<String, Object> getUpDirById(@PathVariable("resourceId")Long resourceId,@PathVariable("projectId")Long projectId,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		Resource res = null;
		try {
			log.info("resource getUploadDir: projectId="+projectId+" resourceId:"+resourceId+",loginUserId:"+loginUserId);
			if(-1==resourceId){
				return this.getSuccessMap("/"+projectId+"/");
			}
			res = this.resourcesService.findOne(resourceId);
			if(null==res){
				return this.getFailedMap("resourceId :"+resourceId+" is not exist.");
			}
			if(!res.getType().equals("dir")){
				return this.getFailedMap("resourceId :"+resourceId+" is not a dir");
			}
			return this.getSuccessMap(res.getFilePath()+res.getName()+"/");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		
	}
	
	
	/**
	 * 
	 * @describe 资源预览	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年12月28日 上午11:33:06	<br>
	 * @param loginUserId
	 * @param sessionId
	 * @param resourceId
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@RequestMapping(value="/preview/{resourceId}",method=RequestMethod.GET)
	public Map<String,Object> preViewResource(@RequestHeader(value="loginUserId")long loginUserId,
			@RequestParam(value="sessionId")String sessionId,
			@PathVariable(value="resourceId")long resourceId){
		try{
			Map<String,Object> map = this.resourcesService.preViewResource(loginUserId,sessionId,resourceId);			
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("预览失败："+e.getMessage());
		}
	}
	
	/**
	 * 一鹏的插件,app创建资源
	 * @user jingjian.wu
	 * @date 2015年10月23日 下午8:51:32
	 */
	/*@ResponseBody
	@RequestMapping(value = "/mobileResource/{appId}",method = RequestMethod.POST)
	public Map<String, Object> mobileResource(MultipartFile file,
			@PathVariable("appId")Long appId,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId){
		try {
			String fileName = file.getOriginalFilename() ;
			App app = appService.findOne(appId);
			boolean exist = this.resourcesService.exist(resourceDir+"/"+app.getProjectId()+"/",fileName);
			if(exist){
				return this.getFailedMap("fileName "+fileName + "is already exist!");
			}
			FileOutputStream os = null;  
	    	byte[] bytes = file.getBytes();
	        os = new FileOutputStream(resourceDir+"/"+app.getProjectId()+"/"+fileName);
	        os.write(bytes);  
	        os.flush() ; 
	    	os.close() ;  
			User u = userService.findUserById(loginUserId);
//			if(null==u){
//				String result = HttpUtil.httpGet("");
//			}
			Resource res = new Resource();
			res.setProjectId(app.getProjectId());
			res.setFilePath(resourceDir+"/"+app.getProjectId()+"/");
			res.setType(fileName.substring(fileName.lastIndexOf(".")+1));
			res.setFileSize(new File(resourceDir+"/"+app.getProjectId()+"/"+fileName).length());
			res.setName(fileName);
			res.setParentId(-1L);
			res.setUserId(u.getId());
			res.setUserName(u.getUserName());
			resourcesService.addResources(res);
			//增加动态
//			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.RESOURCE_TRANSFER,srcRes.getProjectId(), new Object[]{srcRes,tarRes});
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getAffectMap();
	}*/
	/**
	 * @describe 修改资源名称	<br>
	 * @author haijun.cheng	<br>
	 * @date 2016年07月27日	<br>
	 * @param loginUserId
	 * @param name
	 * @param resourceId
	 * @returnType Map<String,Object>
	 */
	@RequestMapping(value="/updateName/{resourceId}",method=RequestMethod.PUT)
	public Map<String,Object> updateName(@RequestHeader(value="loginUserId")long loginUserId,
			@RequestParam(value="name")String name,
			@PathVariable(value="resourceId")long resourceId){
		try{
			Map<String,Object> map = this.resourcesService.updateName(loginUserId,name,resourceId);		
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("修改资源名称失败："+e.getMessage());
		}
	}
	/**
	 * @describe 批量删除资源	<br>
	 * @author haijun.cheng	<br>
	 * @date 2016年08月01日	<br>
	 * @param loginUserId
	 * @param name
	 * @param resourceId
	 * @returnType Map<String,Object>
	 */
	@RequestMapping(value="/batchDelete",method=RequestMethod.PUT)
	public Map<String,Object> batchDelete(
			@RequestHeader(value="loginUserId")long loginUserId,
			@RequestParam(value="resourceId")String resourceIds,
			@RequestParam(value="targetType",defaultValue="NORMAL")SOURCE_TYPE targetType){
		try{
			Map<String,Object> map = this.resourcesService.deleteBatch(loginUserId,resourceIds,targetType);			
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("资源批量删除失败："+e.getMessage());
		}
	}
	/**
	 * 把资源设为公开或私有
	 * @param loginUserId
	 * @param resourceIds
	 * @auth chj
	 * @Date 2016-08-16
	 * @return
	 */
	@RequestMapping(value="/downPublic/{resourceId}",method=RequestMethod.PUT)
	public Map<String,Object> downPublic(
			@RequestHeader(value="loginUserId") long loginUserId,
			@PathVariable(value="resourceId") long resourceId,
			@RequestParam(value="state") String state){
		log.info("========>resourceId:"+resourceId);
		try{
			Map<String,Object> map = this.resourcesService.setResourcePublic(resourceId,loginUserId,state);			
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("资源设置失败："+e.getMessage());
		}
	}
	/**
	 * 下载公开资源
	 * @param loginUserId
	 * @param resourceIds
	 * @auth chj
	 * @Date 2016-08-16
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/downPubResource/{uuid}",method=RequestMethod.GET)
	public String downPubResource(
			@PathVariable(value="uuid")String uuid,
			HttpServletRequest request ,HttpServletResponse response){
		try{
			log.info("========>downLoadResource:"+uuid);
			Resource res = resourcesService.findByUuid(uuid);
			if(null==res){
				String mes="该资源还未公开！";
				byte[] bs = mes.getBytes();
	            return new String(bs, "iso8859-1"); 
			}
			String filename=res.getName();
			if(res.getType().equals("dir")){
				filename=this.resourcesService.findAllPath(uuid);
			}
		    filename=res.getFilePath()+filename;
			//资源下载nginx方法
		    filename = new String(filename.getBytes(),"iso8859-1");
			response.setHeader("Content-Disposition", "attachment; filename="+filename);
			response.setHeader("Content-Type","application/octet-stream");
			log.info("=======>资源下载路径："+filename);
			response.setHeader("X-Accel-Redirect","/nginxpubdown"+filename);
			log.info("=======>资源下载路径：/nginxpubdown"+filename);
		}catch(Exception e){
			e.printStackTrace();
			return "下载资源失败："+e.getMessage();
		}
		return null;
	}
	
}
 