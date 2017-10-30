package org.zywx.cooldev.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.RESOURCE_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.SOURCE_TYPE;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.query.ResourceQuery;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.thread.EnginePushToGitRepo;
import org.zywx.cooldev.thread.ResourceTransferThread;
import org.zywx.cooldev.util.ZipUtil;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;

@Service
public class ResourcesService extends BaseService{
	@Autowired
	protected DynamicService dynamicService;
	
	@Autowired
	protected ProjectService projectService;
	
	@Autowired
	protected TeamService teamService;

	@Value("${resource.baseDir}")
	private String baseDir;
	
	@Value("${resource.publicDir}")
	private String publicDir;
	
	@Value("${xtHost}")
	private String xtHost;
	
	@Value("${file}")
	private String file;
	
	@Value("${picture.type}")
	private String pictureType;
	
	@Value("${file.type}")
	private String fileType;

	@Value("${office.supportTypes}")
	private String officeType;
	
	@Value("${root.path}")
	private String rootPath;
	
	@Value("${openoffice.host}")
	private String openofficeHost;
	
	
	@Value("${openoffice.port}")
	private String openofficePort;
	
	@Value("${office.destinateTypes}")
	private String officeDestinateTypes;
	
	@Value("${file.destinateTypes}")
	private String fileDestinateTypes;
	
	@Value("${shellPath}")
	private String shellPath;
	
	@Value("${resource.preview.url}")
	private String resourcePreviewUrl;
	
	/*@Value("${host}")
	private String host;
	
	@Value("${port}")
	private String port;*/
	
	/**
	 * 根据路径判断文件是否已经存在
	 * @param filePath
	 * @return boolean
	 * @user jingjian.wu
	 * @date 2015年8月22日 下午3:48:11
	 * @throws
	 */
	public boolean exist(String filePath,String name){
		Resource res = this.resourcesDao.findByFilePathAndName(filePath, name);
		if(null !=res){//如果需要上传的文件已经存在,则返回null
			return true;
		}
		return false;
	}
	
	public Resource addResources(Resource resources){
		if(!resources.getFilePath().endsWith("/")){
			resources.setFilePath(resources.getFilePath()+"/");
		}
		//创建文件夹也由前段mas来执行
		/*if(resources.getType().equals("dir")){
			File file = new File(baseDir+resources.getFilePath()+resources.getName());
			boolean flag = file.mkdirs();
			if(!flag){
				throw new  RuntimeException("mkdir "+file.getName()+" failed!");
			}
		}*/
		return resourcesDao.save(resources);
//		String url = this.getPictureUrl(resources);
//		resources.setSrc(url);
	}
	/**
	 * 查询资源列表
	 * @param userId
	 * @param projectId
	 * @param relation CREATE,ACTOR  或者是其中一个,或者不传值
	 * @return
	 * @throws Exception List<Resources>
	 * @user jingjian.wu
	 * @date 2015年8月21日 下午4:42:31
	 * @throws
	 */
	public Map<String, Object> findList(long userId,String relation,
			Long parentId,Map<Long, List<String>> pMapAsProjectMember,
			SOURCE_TYPE sourceType,ResourceQuery query,
			List<Long> creatorPrjId,//userId作为资源创建者能查看资源的项目
			Pageable pageable) throws Exception{
		List<Long> creatorPrjIds=new ArrayList<Long>();
		boolean unionFlag_resourceCreator = true;//是否要Union查询资源创建者单独能看的资源信息
		if(null==creatorPrjId ||creatorPrjId.size()==0){
			unionFlag_resourceCreator = false;
		}
		//返回的结果集
		List< Map<String, Object> > message = new ArrayList<>();
		
		List< Map<String, Object> > messageDir = new ArrayList<>();
		
		List< Map<String, Object> > messageFile = new ArrayList<>();
		
		int startNum = pageable.getPageNumber()*pageable.getPageSize();
		if(pageable.getPageNumber()!=0 && (null==parentId || parentId == -1) && sourceType.equals(SOURCE_TYPE.NORMAL)){
			startNum = pageable.getPageNumber()*pageable.getPageSize()-4;
		}
		int number = pageable.getPageSize();
		
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCreator = new StringBuilder();//资源创建者相关的SQL条件
		sql.append(" from T_RESOURCES r left join T_PROJECT p on projectId = p.id where 1=1 ");
		sqlCreator.append(" from T_RESOURCES r left join T_PROJECT p on projectId = p.id where 1=1 ");
		if(null!=parentId && -1!=parentId){
			sql.append(" and r.parentId = "+parentId.intValue());
			sqlCreator.append(" and r.parentId = "+parentId.intValue());
		}else{
			sql.append(" and r.parentId = -1 ");
			sqlCreator.append(" and r.parentId = -1 ");
		}
		sql.append(" and r.sourceType = "+sourceType.ordinal());
		sqlCreator.append(" and r.sourceType = "+sourceType.ordinal());
		
		List<Long> projectIds  = new ArrayList<Long>();
		List<Long> teamIds = new ArrayList<>();
		teamIds.add(-99L);
		//team筛选条件
		if(null!=query.getTeamId()){
			teamIds.add(query.getTeamId());
			//将该团队下没有的项目Id移除
			String sql1="select id from T_PROJECT where teamId="+query.getTeamId()+" and del=0";
			final List<Long> prolist=new ArrayList<Long>();
			this.jdbcTpl.query(sql1.toString(),
					new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					prolist.add(rs.getLong("id"));
				}
			});
			if(unionFlag_resourceCreator){
				for(int i=0;i<creatorPrjId.size();i++){
					for(int j=0;j<prolist.size();j++){
						if(creatorPrjId.get(i).longValue()==prolist.get(j).longValue()){
							creatorPrjIds.add(creatorPrjId.get(i));
							continue;
						}
					}
				}
			}
			if(creatorPrjIds.size()==0){
				unionFlag_resourceCreator=false;
			}
		}
		List<Long> projectIdsFromTeams = new ArrayList<Long>();
		if((null!=query.getTeamName() && !"%%".equals(query.getTeamName())) || null!=query.getTeamId()){
			projectIdsFromTeams = this.projectService.getProjectIdsByTeam(teamIds,query.getTeamName());
		}
		//项目筛选条件
		if(null!=query.getProjectId() && -1!=query.getProjectId()){//选取了某个项目
			if(pMapAsProjectMember.keySet().contains(query.getProjectId())){
				projectIds.add(query.getProjectId());
			}
			if(unionFlag_resourceCreator){
				for(int i=0;i<creatorPrjId.size();i++){
					if(creatorPrjId.get(i).longValue()==query.getProjectId().longValue()){
						log.info("====>查询的projectId:"+creatorPrjId.get(i));
						creatorPrjIds.add(creatorPrjId.get(i));
					}
				}
				if(creatorPrjIds.size()==0){
					unionFlag_resourceCreator=false;
				}
			}
		}else if(null!=query.getProjectName() && !"%%".equals(query.getProjectName())){
			List<Project> prjlist = projectDao.findByNameLikeAndDelOrderByCreatedAtDesc(query.getProjectName(), DELTYPE.NORMAL);
			List<Long> tmpPrjId = new ArrayList<Long>();
			for(Project p:prjlist){
				tmpPrjId.add(p.getId());
			}
			projectIds = this.getAllIn(tmpPrjId,new ArrayList<Long>(pMapAsProjectMember.keySet()));
			unionFlag_resourceCreator=false;
		}else{//我有权限查询的项目
			projectIds = new ArrayList<Long>(pMapAsProjectMember.keySet());				
		}
		
		if((null!=query.getTeamName() &&  !"%%".equals(query.getTeamName())) || null!=query.getTeamId()){
			projectIds = this.getAllIn(projectIds,projectIdsFromTeams);
		}
		
		StringBuilder projectIdsBuffer = new StringBuilder();
		for(Long id:projectIds){
			projectIdsBuffer.append(",").append(id);
		}
		if(projectIdsBuffer.length()>0){
			projectIdsBuffer.deleteCharAt(0);
			sql.append(" and r.projectId in (")
//			.append(" select projectId from T_PROJECT_MEMBER where del=0 and userId =  ").append(userId)
		    .append(projectIdsBuffer.toString()).append(")");
		}
		
		if(creatorPrjIds.size()!=0){
			creatorPrjId.clear();
			creatorPrjId=creatorPrjIds;
		}
		StringBuilder creatorPrjIdsBuffer = new StringBuilder();
		for(Long id:creatorPrjId){
			creatorPrjIdsBuffer.append(",").append(id);
		}
		if(creatorPrjIdsBuffer.length()>0){
			creatorPrjIdsBuffer.deleteCharAt(0);
			sqlCreator.append(" and r.projectId in (")
			.append(creatorPrjIdsBuffer.toString()).append(")");
		}
		if(StringUtils.isNotBlank(relation)){
			String [] tmp = relation.split(",");
			if(tmp.length==1){
				if(RESOURCE_TYPE.CREATE == RESOURCE_TYPE.valueOf(tmp[0])){
					sql.append(" and r.userId = "+userId +" and r.userName like '"+query.getCreator()+"'");
					sqlCreator.append(" and r.userId = "+userId +" and r.userName like '"+query.getCreator()+"'");
				}else if(RESOURCE_TYPE.ACTOR == RESOURCE_TYPE.valueOf(tmp[0])){
					sql.append(" and r.userId != "+userId +" and r.userName like '"+query.getCreator()+"'");
					sqlCreator.append(" and r.userId != "+userId +" and r.userName like '"+query.getCreator()+"'");
				}
			}else{
				for(String str:tmp){
					if(StringUtils.isNotEmpty(str)){
						if(RESOURCE_TYPE.CREATE != RESOURCE_TYPE.valueOf(str) && RESOURCE_TYPE.ACTOR != RESOURCE_TYPE.valueOf(str)){
							throw new Exception("parameter relation is not available!");
						}
					}
				}
			}
		}
		if(!"%%".equals(query.getCreator())){
			sql.append(" and r.userName like '"+query.getCreator()+"'");	
			sqlCreator.append(" and r.userName like '"+query.getCreator()+"'");	
		}
		if(!"%%".equals(query.getActor())){
			StringBuilder sqlActor = new StringBuilder("select id from T_PROJECT where del=0 and  id in (")
	    			.append("select projectId from T_PROJECT_MEMBER where del=0 and type in (0,1) and userId in (")
	    			.append("select id from T_USER where del=0 and userName like '").append(query.getActor()).append("'")
	    			.append("))");
			sql.append(" and r.projectId in("+sqlActor.toString()+")").append(" and r.userName !='").append(query.getActor().replace("%", "")).append("' ");
			sqlCreator.append(" and r.projectId in("+sqlActor.toString()+")").append(" and r.userName !='").append(query.getActor().replace("%", "")).append("' ");
		}
		
		if(null!=query.getCreatedAtStart()){
			sql.append(" and DATE_FORMAT(r.createdAt, '%Y-%m-%d %k:%i:%s') >= '"+query.getCreatedAtStart()+"' ");
			sqlCreator.append(" and DATE_FORMAT(r.createdAt, '%Y-%m-%d %k:%i:%s') >= '"+query.getCreatedAtStart()+"' ");
		}
		if(null!=query.getCreatedAtEnd()){
			sql.append(" and DATE_FORMAT(r.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ");
			sqlCreator.append(" and DATE_FORMAT(r.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ");
		}
//		sql.append(" and substring_index(r.name,'.',1) like '"+query.getName()+"' order by decode(r.type,'dir'),r.createdAt desc ,r.filePath asc");//增加了一个根据时间倒叙排列
		sql.append(" and substring_index(r.name,'.',1) like '"+query.getName()+"' ");//增加了一个根据时间倒叙排列
		sqlCreator.append(" and substring_index(r.name,'.',1) like '"+query.getName()+"' ");//增加了一个根据时间倒叙排列
		if(unionFlag_resourceCreator){
			sql.append(" UNION select r.*,p.name projectName ,if(r.type='dir','9999-99-99',r.createdAt) orderColumn");
			sql.append(sqlCreator);
		}
		String sqlCount = "select count(1) from ( select r.*,p.name projectName ,if(r.type='dir','9999-99-99',r.createdAt) orderColumn"+sql.toString()+") t";
		sql.append(" order by orderColumn  desc,createdAt desc ,filePath asc ");
		sql.append(" limit " + startNum +","+ number);
		sql.insert(0, "select r.*,p.name projectName ,if(r.type='dir','9999-99-99',r.createdAt) orderColumn");
		long count = this.jdbcTpl.queryForLong(sqlCount);
		
		log.info("find resource_SQL:"+sql.toString());
		final List<Resource> volist = new ArrayList<Resource>();
		this.jdbcTpl.query(sql.toString(),
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						Resource vo  = new Resource();
						vo.setCreatedAt(rs.getTimestamp("createdAt"));
						vo.setFilePath(rs.getString("filePath"));
						vo.setFileSize(rs.getLong("fileSize"));
						vo.setId(rs.getLong("id"));
						vo.setName(rs.getString("name"));
						vo.setParentId(rs.getLong("parentId"));
						vo.setProjectId(rs.getLong("projectId"));
						vo.setProjectName(rs.getString("projectName"));
						vo.setType(rs.getString("type"));
						vo.setUpdatedAt(rs.getTimestamp("updatedAt"));
						vo.setUserId(rs.getLong("userId"));
						vo.setUserName(rs.getString("userName"));
						vo.setIsPublic(rs.getInt("isPublic"));
						vo.setUuid(rs.getString("uuid"));
						vo.setDownLoadPath(xtHost+"/downPubResource/"+rs.getString("uuid"));
						SOURCE_TYPE sourceType = SOURCE_TYPE.values()[rs.getInt("sourceType")];
						vo.setSourceType(sourceType);
						
						volist.add(vo);
					}
				});
		
		
		for(Resource rs:volist){
			Map<String, Object> innerMapDir = new HashMap<String, Object>();
			Map<String, Object> innerMapFile = new HashMap<String, Object>();
			if(rs.getType().equals("dir")){
				long filesize = this.getFileSize(rs);
				rs.setFileSize(filesize);
				innerMapDir.put("object", rs);
				Map<String, Integer> mapPermission = new HashMap<String, Integer>();
				
				//查询当前人在项目下的权限信息
				List<String> listPermission = pMapAsProjectMember.get(rs.getProjectId());
				for(String permissionName :listPermission){
					mapPermission.put(permissionName,1);
				}
				innerMapDir.put("permissions",mapPermission );
				if(rs.getUserId()==userId){//有权限
//					innerMapDir.put("owner", "y");//我拥有这个权限
					List<Permission> listPtmp =  Cache.getRole(ENTITY_TYPE.RESOURCE+"_"+ROLE_TYPE.CREATOR).getPermissions();
					for(Permission permissionName :listPtmp){
						mapPermission.put(permissionName.getEnName(),1);
					}
				}/*else{//不是我的资源,需要依靠permissions来判断我是否可以操作
					innerMapDir.put("owner", "n");
				}*/
				messageDir.add(innerMapDir);
			}else{
				//任务资源、流程资源、讨论资源不显示在第一层
				if(sourceType.equals(SOURCE_TYPE.NORMAL) && !rs.getSourceType().equals(SOURCE_TYPE.NORMAL)){
					continue;
				}
				innerMapFile.put("object", rs);
				Map<String, Integer> mapPermission = new HashMap<String, Integer>();
				
				//查询当前人在项目下的权限信息
				List<String> listPermission = pMapAsProjectMember.get(rs.getProjectId());
				if(listPermission!=null && listPermission.size()>0){
					for(String permissionName :listPermission){
						mapPermission.put(permissionName,1);
					}
				}
				innerMapFile.put("permissions",mapPermission );
				if(rs.getUserId()==userId){//有权限
//					innerMapFile.put("owner", "y");//我拥有这个权限
					List<Permission> listPtmp =  Cache.getRole(ENTITY_TYPE.RESOURCE+"_"+ROLE_TYPE.CREATOR).getPermissions();
					for(Permission permissionName :listPtmp){
						mapPermission.put(permissionName.getEnName(),1);
					}
				}/*else{//不是我的资源,需要依靠permissions来判断我是否可以操作
					innerMapFile.put("owner", "n");
				}*/
				messageFile.add(innerMapFile);
			}
			
		}
		message.addAll(messageDir);
		message.addAll(messageFile);
		
		Map<String,Object> defaultDir = new HashMap<>();
		defaultDir.put("process", this.getDefaultFileSize(SOURCE_TYPE.PROCESS,projectIds));
		defaultDir.put("task", this.getDefaultFileSize(SOURCE_TYPE.TASK, projectIds));
		defaultDir.put("topic", this.getDefaultFileSize(SOURCE_TYPE.TOPIC,projectIds));
		defaultDir.put("bug",  this.getDefaultFileSize(SOURCE_TYPE.BUG,projectIds));
		Map<String,Object> result = new HashMap<>();
		result.put("list", message);
		result.put("total", count);
		result.put("defaultDir", defaultDir);
		
		return result;
	}
	
	private List<Long> getAllIn(List<Long> projectIds, List<Long> projectIdsFromTeams) {
		List<Long> allIn = new ArrayList<Long>();
		allIn.add(-99L);
		for(Long proId : projectIds){
			for(Long pId : projectIdsFromTeams){
				if(proId.equals(pId)){
					allIn.add(proId);
				}
			}
		}
		return allIn;
	}

//	@Value("${BASEURI}")
//	private String BASEURI;
//	public String getPictureUrl(Resource resource) {
//		String [] picture = new String[]{".JPEG",".JPG",".PNG",".SWF",".SVG",".PCX",".DXF",".WMF",".EMF",".TIFF",".PSD",".GIF",".BMP"};
//		for(String str : picture){
//			if(str.toLowerCase().equals(resource.getType())){
//				return BASEURI+resource.getFilePath()+resource.getName();
//			}
//		}
//		return null;
//	}

	public List<Resource> findDir(long userId,Long projectId) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select * from T_RESOURCES where type = 'dir' and parentId= -1 ");
		if(null!=projectId){
			sql.append(" and projectId = "+projectId);
		}
//		sql.append(" and userId = ").append(userId);
		sql.append(" order by filePath asc");
//		List<Resource> list = this.jdbcTpl.queryForList(sql.toString(),Resource.class);
//		return list;
		
		final List<Resource> volist = new ArrayList<Resource>();
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						Resource vo  = new Resource();
						vo.setCreatedAt(rs.getTimestamp("createdAt"));
						String filePath = rs.getString("filePath");
//						filePath = filePath.substring(filePath.indexOf('/',1));
						vo.setFilePath(filePath);
						vo.setFileSize(rs.getLong("fileSize"));
						vo.setId(rs.getLong("id"));
						vo.setName(rs.getString("name"));
						vo.setParentId(rs.getLong("parentId"));
						vo.setProjectId(rs.getLong("projectId"));
						vo.setType(rs.getString("type"));
						vo.setUpdatedAt(rs.getTimestamp("updatedAt"));
						vo.setUserId(rs.getLong("userId"));
						vo.setUserName(rs.getString("userName"));
						volist.add(vo);
					}
				});
		for(Resource rs:volist){
			wrapResource(rs);
		}
		return volist;
	}
	public List<Resource> findDir(long userId,Long projectId,SOURCE_TYPE sourceType) throws Exception{
		StringBuilder sql = new StringBuilder();
		sql.append("select * from T_RESOURCES where type = 'dir' and parentId= -1 ");
		if(null!=projectId){
			sql.append(" and projectId = "+projectId);
		}
//		sql.append(" and sourceType = "+sourceType.ordinal());
//		sql.append(" and userId = ").append(userId);
		sql.append(" order by filePath asc");
//		List<Resource> list = this.jdbcTpl.queryForList(sql.toString(),Resource.class);
//		return list;
		
		final List<Resource> volist = new ArrayList<Resource>();
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
			
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Resource vo  = new Resource();
				vo.setCreatedAt(rs.getTimestamp("createdAt"));
				String filePath = rs.getString("filePath");
//						filePath = filePath.substring(filePath.indexOf('/',1));
				vo.setFilePath(filePath);
				vo.setFileSize(rs.getLong("fileSize"));
				vo.setId(rs.getLong("id"));
				vo.setName(rs.getString("name"));
				vo.setParentId(rs.getLong("parentId"));
				vo.setProjectId(rs.getLong("projectId"));
				vo.setType(rs.getString("type"));
				vo.setUpdatedAt(rs.getTimestamp("updatedAt"));
				vo.setUserId(rs.getLong("userId"));
				vo.setUserName(rs.getString("userName"));
				
				SOURCE_TYPE sourceType = SOURCE_TYPE.values()[rs.getInt("sourceType")];
				vo.setSourceType(sourceType);
				
				volist.add(vo);
			}
		});
		Resource voProcess  = new Resource();
		List<Resource> voProcessList  = new ArrayList<Resource>();
		voProcess.setId(-1L);
		voProcess.setName("流程附件");
		voProcess.setParentId(-1);
		voProcess.setProjectId(projectId);
		voProcess.setType("dir");
		voProcess.setSourceType(SOURCE_TYPE.PROCESS);
		
		Resource voTask  = new Resource();
		List<Resource> voTaskList  = new ArrayList<Resource>();
		voTask.setId(-2L);
		voTask.setName("任务附件");
		voTask.setParentId(-1);
		voTask.setProjectId(projectId);
		voTask.setType("dir");
		voTask.setSourceType(SOURCE_TYPE.TASK);
		
		Resource voTopic  = new Resource();
		List<Resource> voTopicList  = new ArrayList<Resource>();
		voTopic.setId(-3L);
		voTopic.setName("讨论附件");
		voTopic.setParentId(-1);
		voTopic.setProjectId(projectId);
		voTopic.setType("dir");
		voTopic.setSourceType(SOURCE_TYPE.TOPIC);
		
		Resource voBug  = new Resource();
		List<Resource> voBugList  = new ArrayList<Resource>();
		voBug.setId(-4L);
		voBug.setName("Bug附件");
		voBug.setParentId(-1);
		voBug.setProjectId(projectId);
		voBug.setType("dir");
		voBug.setSourceType(SOURCE_TYPE.BUG);
		for(Resource rs:volist){
			wrapResource(rs);
			if(rs.getSourceType().equals(SOURCE_TYPE.PROCESS)){
				voProcessList.add(rs);
			}else if(rs.getSourceType().equals(SOURCE_TYPE.TASK)){
				voTaskList.add(rs);
			}else if(rs.getSourceType().equals(SOURCE_TYPE.TOPIC)){
				voTopicList.add(rs);
			}else if(rs.getSourceType().equals(SOURCE_TYPE.BUG)){
				voBugList.add(rs);
			}
		}
		
		volist.removeAll(voProcessList);
		volist.removeAll(voTaskList);
		volist.removeAll(voTopicList);
		volist.removeAll(voBugList);
		voProcess.setChild(voProcessList);
		voTask.setChild(voTaskList);
		voTopic.setChild(voTopicList);
		voBug.setChild(voBugList);
		volist.add(0,voProcess);
		volist.add(1,voTask);
		volist.add(2,voTopic);
		volist.add(3,voBug);
		
		
		return volist;
	}
	
	
	
	
	private void wrapResource(Resource rs){
		String filePath = rs.getFilePath();
		filePath = filePath.substring(filePath.indexOf('/',1));
		rs.setFilePath(filePath);
		List<Resource> listChild = this.resourcesDao.findByParentIdAndType(rs.getId(), "dir");
		if(null==listChild || listChild.size()==0){
			rs.setChild(new ArrayList<Resource>());
			return;
		}else{
			rs.setChild(listChild);
			for(Resource r:listChild){
				wrapResource(r);
			}
		}
		
	}
	
	
	public List<Resource> findDirList(long userId,Long projectId) throws Exception{
		/*StringBuilder sql = new StringBuilder();
		sql.append("select * from T_RESOURCES where type = 'dir'  ");
		if(null!=projectId){
			sql.append(" and projectId = "+projectId);
		}
		sql.append(" order by filePath asc");
		
		final List<Resource> volist = new ArrayList<Resource>();
		this.jdbcTpl.query(sql.toString(), 
				new RowCallbackHandler() {
					
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						Resource vo  = new Resource();
						vo.setCreatedAt(rs.getTimestamp("createdAt"));
						String filePath = rs.getString("filePath");
						filePath = filePath.substring(filePath.indexOf('/',1));
						vo.setFilePath(filePath);
						vo.setFileSize(rs.getLong("fileSize"));
						vo.setId(rs.getLong("id"));
						vo.setName(rs.getString("name"));
						vo.setParentId(rs.getLong("parentId"));
						vo.setProjectId(rs.getLong("projectId"));
						vo.setType(rs.getString("type"));
						vo.setUpdatedAt(rs.getTimestamp("updatedAt"));
						vo.setUserId(rs.getLong("userId"));
						vo.setUserName(rs.getString("userName"));
						volist.add(vo);
					}
				});
		return volist;*/
		
		List<Resource> listResource = this.findDir(userId, projectId);
		
		List<Resource> result = new ArrayList<Resource>();
		if(null!=listResource){
			for(Resource rs :listResource){
				diguiResource(result, rs);
			}
			for(Resource rs :result){
				rs.setShowNameInSelect(replaceWith(rs.getFilePath())+rs.getName());
			}
		}
		
		return result;
	}
	
	private void diguiResource(List<Resource> result,Resource rs){
		result.add(rs);
		List<Resource> list = rs.getChild();
		if(null!=list && list.size()>0){
			for(Resource r:list){
				diguiResource(result, r);				
			}
		}
		rs.setChild(null);
	}
	
	//filepath中的斜杠/  替换为 -,但是因为根目录也有一个,所以- 比/少一个 
	private String replaceWith(String filepath){
		if(null==filepath || filepath.trim().equals("")){
			return "";
		}
		int num=-1;
		for(int i=0;i<filepath.length();i++){
			if(filepath.charAt(i)=='/'){
				num++;
			}
		}
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<num;i++){
			sb.append(" -- ");
		}
		return sb.toString();
	}
	
	public Resource findOne(Long resourceId){
		 Resource res = this.resourcesDao.findOne(resourceId);
		 if(null!=res && res.getType().equals("dir")){
			 res.setFileSize(getFileSize(res));
		 }
//		 String url = this.getPictureUrl(res);
//		 res.setSrc(url);
		 return res;
	}
	
	private long getFileSize(Resource res){
		long size = 0;
		List<Resource> list = this.resourcesDao.findByFilePathLike(res.getFilePath()+res.getName()+"/%");
		if(null!=list && list.size()>0){
			for(Resource re:list){
				size+=re.getFileSize();
			}
		}
		return size;
	}
	
	private String getDefaultFileSize(SOURCE_TYPE sourceType,List<Long> projectId){
		long size = 0;
		List<Resource> list = this.resourcesDao.findBySourceTypeAndProjectIdIn(sourceType, projectId);
		if(null!=list && list.size()>0){
			for(Resource re:list){
				size+=re.getFileSize();
			}
		}
		long kb = size/1024;
		long mb = size/1024/1024;
		return mb>1?mb+" MB":kb + " KB";
	}
	
	/**
	 * 判断是否可以转移
	 * @user jingjian.wu
	 * @date 2015年11月3日 下午7:35:15
	 */
	public boolean getJudgeTransfer(Long srcId,Long targetId) throws IOException{
		Resource srcRes = this.resourcesDao.findOne(srcId);
		Resource tarRes = this.resourcesDao.findOne(targetId);
		if(srcRes.getParentId()==tarRes.getId()){//src本来就是在tar下,所以不用移动了
			log.info(" resource already be the position!");
			return true;
		}
		if(!tarRes.getType().equals("dir")){//目标目录不是文件夹就报错,因为不可能转移到文件下面
			return false;
		}
		File targetFile = new File(baseDir+tarRes.getFilePath()+tarRes.getName()+"/"+srcRes.getName());
		File srcFile = new File(baseDir+srcRes.getFilePath()+srcRes.getName());
		log.info("srcFile-->"+srcFile);
		log.info("targetFile-->    "+targetFile);
		if(targetFile.exists()){
			return false;
		}
		return true;
	}
	/**
	 * @throws IOException 
	 * 将文件id为srcId的文件转移到targetId下面
	 * 返回空字符串  代表成功,否则返回错误信息
	 * @param srcId
	 * @param targetId void
	 * @user jingjian.wu
	 * @date 2015年8月22日 上午11:01:21
	 * @throws
	 */
	public String updateTransfer(Long srcId,Long targetId,SOURCE_TYPE targetType) throws IOException{
		Resource srcRes = this.resourcesDao.findOne(srcId);
		Resource tarRes = this.resourcesDao.findOne(targetId);
		if(targetId >-1 && srcRes.getParentId()==tarRes.getId()){//src本来就是在tar下,所以不用移动了
			log.info(" resource already be the position!");
			return "";
		}else if(targetId <=-1){
			if(srcRes.getParentId()==-1){
				log.info(" resource already be the position!");
				srcRes.setSourceType(targetType);
				this.resourcesDao.save(srcRes);
				return "";
			}
		}
		if(targetId>-1 && !tarRes.getType().equals("dir")){//目标目录不是文件夹就报错,因为不可能转移到文件下面
			return "目标位置不正确";
		}
		
		File targetFile =null;
		if(targetId>-1){
			targetFile = new File(baseDir+tarRes.getFilePath()+tarRes.getName()+"/"+srcRes.getName());
		}else{
			targetFile = new File(baseDir+"/"+srcRes.getProjectId()+"/"+srcRes.getName());
		}
		
		File srcFile = new File(baseDir+srcRes.getFilePath()+srcRes.getName());
		log.info("srcFile-->"+srcFile);
		log.info("targetFile-->    "+targetFile);
		if(targetFile.exists()){
			return "目标文件夹下已存在同名文件,请选择其他目录转移";
		}
		if(!srcFile.exists()){
			return "被转移文件夹或文件不存在";
		}
	
		// 被移走的文件及下属文件,的路径都需要修改
		if(srcRes.getType().equals("dir")){//如果是目录转移
			String originalPath = srcRes.getFilePath()+srcRes.getName();
			if(-1>=targetId){
				srcRes.setFilePath("/"+srcRes.getProjectId()+"/");//将要转移的目录的路径改变
				srcRes.setParentId(-1);
				srcRes.setSourceType(targetType);
			}else{
				srcRes.setFilePath(tarRes.getFilePath()+tarRes.getName()+"/");//将要转移的目录的路径改变
				srcRes.setParentId(tarRes.getId());
				srcRes.setSourceType(tarRes.getSourceType());
			}
			List<Resource> list = this.resourcesDao.findByFilePathLike(originalPath+"/%");
			for(Resource r:list){//修改filePath
				String newPath = r.getFilePath().replaceFirst(originalPath+"/", -1>=targetId?("/"+srcRes.getProjectId()+"/"+srcRes.getName()+"/"):(tarRes.getFilePath()+tarRes.getName()+"/"+srcRes.getName()+"/"));
				r.setFilePath(newPath);
				if(-1>=targetId){
					r.setSourceType(targetType);
				}else{
					r.setSourceType(tarRes.getSourceType());
				}
				this.resourcesDao.save(r);
			}
			for(Resource r:list){//修改parentId
				String path = r.getFilePath().substring(0, r.getFilePath().length()-1);
				int index = path.lastIndexOf("/");
				String parentPath = path.substring(0,index+1);
				String parentName = path.substring(index+1);
				Resource parent = resourcesDao.findByFilePathAndName(parentPath, parentName);
				r.setParentId(parent.getId());
				this.resourcesDao.save(r);
					
			}
		}else{//如果是单个文件转移
			if(-1>=targetId){
				srcRes.setFilePath("/"+srcRes.getProjectId()+"/");
				srcRes.setParentId(-1);
				srcRes.setSourceType(targetType);
			}else{
				srcRes.setFilePath(tarRes.getFilePath()+tarRes.getName()+"/");
				srcRes.setParentId(tarRes.getId());
				srcRes.setSourceType(tarRes.getSourceType());
			}
			
		}
		this.resourcesDao.save(srcRes);
		
		//以下改为异步执行,因为文件太大的话,转移的特别慢,前端页面会超时
		/*try {
			FileSystemUtils.copyRecursively(srcFile, targetFile);
			String [] picture = new String[]{".JPEG",".JPG",".PNG",".SWF",".SVG",".PCX",".DXF",".WMF",".EMF",".TIFF",".PSD",".GIF",".BMP"};
			boolean isPic = false;//是否是图片
			for(String str : picture){
				if(str.toLowerCase().equals(srcRes.getType().toLowerCase())){
					isPic = true;
					break;
				}
			}
			if(isPic){//如果是文件,并且有缩略图的话,需要将对应的缩略图也移过去
				if(new File(srcFile.getParent()+File.separator+"abbr_"+srcFile.getName()).exists()){
					//如果文件名以expectFileName开头,并且去掉expectFileName的文件也存在,标识此文件为需要排除的文件,则不需要压缩
					FileSystemUtils.copyRecursively(new File(srcFile.getParent()+File.separator+"abbr_"+srcFile.getName()), new File(targetFile.getParent()+File.separator+"abbr_"+targetFile.getName()));
					FileSystemUtils.deleteRecursively(new File(srcFile.getParent()+File.separator+"abbr_"+srcFile.getName()));
				}
			}
			boolean flag =FileSystemUtils.deleteRecursively(srcFile);
			log.info("delete file "+srcFile +" result:"+flag);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("转移文件失败");
		}*/
		Thread thread = new Thread(new ResourceTransferThread(srcFile,targetFile,srcRes));
		thread.start();
		return "";
	}
	
	/**
	 * @throws InterruptedException 
	 * @throws IOException 
	 * 删除资源
	 * @param resourceId void
	 * @user jingjian.wu
	 * @date 2015年8月22日 上午11:07:45
	 * @throws
	 */
	public Resource deleteResource(Long resourceId,Long loginUserId) throws IOException, InterruptedException{
		Resource srcRes = this.resourcesDao.findOne(resourceId);
		if(null==srcRes){
			return null;
		}
		//预览前 做资源权限校验
		boolean isOwnerPermissions = this.getResoucePermissions(ENTITY_TYPE.RESOURCE+"_"+CRUD_TYPE.REMOVE_FILE,resourceId,loginUserId);
		if(!isOwnerPermissions){
			throw new RuntimeException("没有删除资源文件权限,请联系管理员");
		}
		StringBuffer resourceIds = new StringBuffer();
		if(srcRes.getType().equals("dir")){
			boolean isOwnerPermissionsDir = this.getResoucePermissions(ENTITY_TYPE.RESOURCE+"_"+CRUD_TYPE.REMOVE_DIR,resourceId,loginUserId);
			if(!isOwnerPermissionsDir){
				throw new RuntimeException("没有删除资源文件夹权限,请联系管理员");
			}
			List<Resource> resourceList = this.findChildsByResource(srcRes.getFilePath()+srcRes.getName()+"/%");
			
			//如果删除的是目录,则需要将目录下面的文件全部删除
			String delSql = "delete from T_RESOURCES where filePath like '"+srcRes.getFilePath()+srcRes.getName()+"/%'";
			this.jdbcTpl.execute(delSql);
			
			for(Resource resource : resourceList){
				resourceIds.append(resource.getId()+",");
			}
			
		}
		resourceIds.append(resourceId);
		//删除流程 任务 讨论与资源及其下资源的关联
		String delEntitySql = "delete from T_ENTITY_RESOURCE_REL where resourceId in ("+resourceIds+")";
		log.info("=====================delete T_ENTITY_RESOURCE_REL --> resourceIds:"+resourceIds);
		this.jdbcTpl.execute(delEntitySql);
		
		//不管是目录,还是文件,都需要把自己删除
		this.resourcesDao.delete(resourceId);
		File f = new File(baseDir+srcRes.getFilePath()+srcRes.getName());
		log.info("=====================delete resource:"+f);
		FileSystemUtils.deleteRecursively(f);
		
		//如果文件格式是图片还需要把压缩图删掉
		String [] picture = new String[]{".JPEG",".JPG",".PNG",".SWF",".SVG",".PCX",".DXF",".WMF",".EMF",".TIFF",".PSD",".GIF",".BMP"};
		boolean isPic = false;//是否是图片
		for(String str : picture){
			if(str.toLowerCase().equals(srcRes.getType().toLowerCase())){
				isPic = true;
				break;
			}
		}
		if(isPic){
			File abbrf = new File(baseDir+srcRes.getFilePath()+"abbr_"+srcRes.getName());
			log.info("=====================delete abbrResource:"+abbrf);
			FileSystemUtils.deleteRecursively(abbrf);
		}

		return srcRes;
	}
	
	private List<Resource> findChildsByResource(String query) {
		return this.resourcesDao.findByNameLikeAndDelOrderByCreatedAtDesc(query, DELTYPE.NORMAL);
	}
	

	/**
	 * @throws IOException 
	 * 打包下载
	 * @param resourceId
	 * @return String
	 * @user jingjian.wu
	 * @date 2015年8月22日 上午11:18:05
	 * @throws
	 */
	/*public String updatePkg(Long resourceId) throws IOException{
		Resource res = this.resourcesDao.findOne(resourceId);
//		String uuidStr = UUID.randomUUID().toString();
		String targetFile;
		int fileNum = 0;
		String onlyName = res.getName().replace(res.getType(), "");
		if("dir".equals(res.getType())){
			targetFile =file+"/"+onlyName;
			while(new File(targetFile+".zip").exists()){
				fileNum++;
				targetFile = file+"/"+onlyName + "("+fileNum+")";//123(1).png or 123(1).zip
			}
			targetFile += ".zip";
			log.info("=====================pkg  resource:"+baseDir+res.getFilePath()+res.getName()+"    --to--    "+targetFile);
			ZipUtil.zipExceptByFileName(baseDir+res.getFilePath()+res.getName(), targetFile,"abbr_");//abbr_为王永文生成的缩略图文件
		}else{
//			targetFile =res.getName();
			targetFile =file+"/"+res.getName();
			File f = new File(baseDir+res.getFilePath()+res.getName());
			log.info("====================pkg resource:  copy->"+f+",targetFile:"+targetFile);
			FileSystemUtils.copyRecursively(f, new File(targetFile));
		}
		
		log.info("pkg resource: src File-->"+baseDir+res.getFilePath()+res.getName()+";targetFile-->"+targetFile);
		
		if("dir".equals(res.getType())){
			if(fileNum>0){
				return host+":"+port+"/"+onlyName+ "("+fileNum+").zip";
			}else{
				return host+":"+port+"/"+onlyName+".zip";
			}
			
		}else{
			return host+":"+port+"/"+res.getName();
		}
	}*/
	/**
	 * 打包下载,从上面的复制下来的,上面的不用了,要加权限,以后用下面这个,返回一个文件名
	 * @user jingjian.wu
	 * @date 2015年11月5日 下午8:08:33
	 */
	public String updatePkgFileName(Long resourceId) throws IOException{
		Resource res = this.resourcesDao.findOne(resourceId);
//		String uuidStr = UUID.randomUUID().toString();
		String targetFile;
		int fileNum = 0;
		String onlyName = res.getName().replace(res.getType(), "");
		if("dir".equals(res.getType())){
			targetFile =file+"/"+onlyName;
			while(new File(targetFile+".zip").exists()){
				fileNum++;
				targetFile = file+"/"+onlyName + "("+fileNum+")";//123(1).png or 123(1).zip
			}
			targetFile += ".zip";
			log.info("=====================pkg  resource:"+baseDir+res.getFilePath()+res.getName()+"    --to--    "+targetFile);
			ZipUtil.zipExceptByFileName(baseDir+res.getFilePath()+res.getName(), targetFile,"abbr_");//abbr_为王永文生成的缩略图文件
		}else{
//			targetFile =res.getName();
			targetFile =file+"/"+res.getName();
			File f = new File(baseDir+res.getFilePath()+res.getName());
			log.info("====================pkg resource:  copy->"+f+",targetFile:"+targetFile);
			FileSystemUtils.copyRecursively(f, new File(targetFile));
		}
		
		log.info("pkg resource: src File-->"+baseDir+res.getFilePath()+res.getName()+";targetFile-->"+targetFile);
		if("dir".equals(res.getType())){
			if(fileNum>0){
				return onlyName+ "("+fileNum+").zip";
			}else{
				return onlyName+".zip";
			}
			
		}else{
			return res.getName();
		}
	}

	/**
	 * 当用户名称修改了之后,需要同步修改资源中的用户名称
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午3:11:46
	 */
	public boolean updateUserNameByUserId(Long userId,String newName){
		List<Resource> listRes = resourcesDao.findByUserId(userId);
		if(null!=listRes){
			for(Resource r:listRes){
				r.setUserName(newName);
				resourcesDao.save(r);
			}
		}
		return true;
	}
	/**
	 * 根据资源ID获取路径的ID,用逗号分隔
	 * @user jingjian.wu
	 * @date 2015年12月8日 下午3:02:44
	 */
	public String  findFullPath(long resId){
		Resource res = resourcesDao.findOne(resId);
		String fullPath = res.getId().toString();
		while(res.getParentId()!=-1){
			res =  resourcesDao.findOne(res.getParentId());
			fullPath = res.getId().toString()+","+fullPath;
		}
		return fullPath;
	}
	
	public static void main(String[] args) {
	int size = 4891648;
		if(size/(1024*1024)>4 || size%(1024*1024)>0){
			System.out.println("那就对了");
		}
		System.out.println("abc".substring(0, "abc".length()-1));
	}

	
	public Map<String, Object> preViewResource(long loginUserId, String sessionId, long resourceId) {
		HashMap<String,Object> map = new HashMap<>();
		Resource resource = this.findOne(resourceId);
		if(null==resource || resource.getDel().equals(DELTYPE.DELETED)){
			return this.getFailedMap("资源不存在");
		}
		//预览前 做资源权限校验
		boolean isOwnerPermissions = this.getResoucePermissions(ENTITY_TYPE.RESOURCE+"_"+CRUD_TYPE.RETRIEVE,resourceId,loginUserId);
		if(!isOwnerPermissions){
			return this.getFailedMap("没有查看该资源的权限");
		}
		if(resource.getFileSize()/(1024*1024)>4 || (resource.getFileSize()%(1024*1024)>0 && resource.getFileSize()/(1024*1024)==4)){
			map.put("status", "failed");
			map.put("message", "资源文件超过4M暂不支持预览");
			map.put("action", "download");
			return this.getFailedMap(map);
		}
		
		try{
			//图片预览
			String[] picture = pictureType.split(";");
			for(String pic : picture){
				if(resource.getType().toUpperCase().equals(pic)){
					//图片原始路径
					String filePath = baseDir + resource.getFilePath()+resource.getName();
					String filename = resource.getName().replace(resource.getType(), "");//文件名称 不包括类型后缀
					//预览路径
					long time = System.currentTimeMillis();
					String preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+pic.toLowerCase();
					
					File resourceViewPath = new File(preViewPath);
					if(!resourceViewPath.exists()){
						resourceViewPath.mkdirs();
					}
					
					String cmd = "sh " + shellPath + "/coopdev_res/copyRes.sh " + filePath + " " + preViewPath;
					String result = this.execShell(cmd);
					log.info("copy file " + filePath +" to " + preViewPath + "result:"+result);
					
					map.put("time", time+"");
					map.put("type", pic.toLowerCase());
					map.put("oldType", resource.getType());
					map.put("filename", filename);
					return this.getSuccessMap(map);
				}
			}
			
			//文本预览
			String[] txtFile = fileType.split(";");
			for(String txt : txtFile){
				//String txtDestinateTypes = ".txt";
				if(resource.getType().toUpperCase().equals(txt)){
					//文本原始路径
					String filePath = baseDir + resource.getFilePath()+resource.getName();
					String filename = resource.getName().replace(resource.getType(), "");//文件名称 不包括类型后缀
					//预览路径
					long time = System.currentTimeMillis();
					String preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+txt.toLowerCase();
					
					File resourceViewPath = new File(preViewPath);
					if(!resourceViewPath.exists()){
						resourceViewPath.mkdirs();
					}
					
					String cmd = "sh " + shellPath + "/coopdev_res/copyRes.sh " + filePath + " " + preViewPath;
					String result = this.execShell(cmd);
					log.info("copy file " + filePath +" to " + preViewPath + "result:"+result);
					
					map.put("time", time+"");
					map.put("type", txt.toLowerCase());
					map.put("oldType", resource.getType());
					map.put("filename", filename);
					return this.getSuccessMap(map);
				}
			}
			//文本预览
//			String[] txtFile = fileType.split(";");
//			for(String txt : txtFile){
//				if(resource.getType().toUpperCase().equals(txt)){
//					OpenOfficeConnection connection = new SocketOpenOfficeConnection(openofficeHost, Integer.parseInt(openofficePort));
//					connection.connect();
//					DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
//					String filename = resource.getName().replace(resource.getType(), "");//文件名称 不包括类型后缀
//					//图片原始路径
//					String filePath = baseDir + resource.getFilePath()+resource.getName();
//					//预览路径
//					long time = System.currentTimeMillis();
//					String preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+fileDestinateTypes.toLowerCase();
//					File resourceViewPath = new File(preViewPath);
//					if(!resourceViewPath.exists()){
//						resourceViewPath.mkdirs();
//					}
//					File oldFile = new File(filePath);
//					File newFile = new File(preViewPath+"/"+filename+fileDestinateTypes.toLowerCase());
//					if(resource.getType().toUpperCase().equals("txt")){
//						log.info("file:"+filePath+" convert to:"+newFile);
//						converter.convert(oldFile, newFile);
//						
//						log.info("succeed converter file " + filePath +" to " + preViewPath+"/"+filename+fileDestinateTypes.toLowerCase());
//						
//						//预览路径
//						filePath = preViewPath +"/"+filename+fileDestinateTypes.toLowerCase();
//					}
//					time = System.currentTimeMillis();
//					preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+officeDestinateTypes.toLowerCase();
//					resourceViewPath = new File(preViewPath);
//					if(!resourceViewPath.exists()){
//						resourceViewPath.mkdirs();
//					}
//					oldFile = new File(filePath);
//					newFile = new File(preViewPath+"/"+filename+officeDestinateTypes.toLowerCase());
//					log.info("file:"+filePath+" convert to:"+newFile);
//					converter.convert(oldFile, newFile);
//					
//					log.info("succeed converter file " + filePath +" to " + preViewPath+"/"+filename+officeDestinateTypes.toLowerCase());
//					
//					connection.disconnect();
//					
//					map.put("time", time+"");
//					map.put("type", officeDestinateTypes.toLowerCase());
//					map.put("filename", filename);
//					return this.getSuccessMap(map);
//				}
//			}
			
			//office预览
			String[] officeFile = officeType.split(";");
			for(String office : officeFile){
				if(resource.getType().toUpperCase().equals(office)){
					OpenOfficeConnection connection = null;
					try{
						connection = new SocketOpenOfficeConnection(openofficeHost, Integer.parseInt(openofficePort));
						connection.connect();
					}catch(ConnectException e){
						log.info("文件转换出错，请检查OpenOffice服务是否启动。");
					    e.printStackTrace();
					    
					    log.info("尝试重新启动服务");
					    String cmd = "sh " + shellPath + "/coopdev_res/rebootOpenOfficeServices.sh "+ openofficeHost +" " + openofficePort;
						String result = this.execShell(cmd);
						log.info("尝试重新启动服务结果："+result);
					    
					}
					
//					DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
					DocumentConverter converter = new StreamOpenOfficeDocumentConverter(connection);   
					String filename = resource.getName().replace(resource.getType(), "");//文件名称 不包括类型后缀
					//图片原始路径
					String filePath = baseDir + resource.getFilePath()+resource.getName();
					//预览路径
					long time = System.currentTimeMillis();
					String preViewPath = rootPath + "/preViewResource/" +sessionId+"_"+time+officeDestinateTypes.toLowerCase();
					File resourceViewPath = new File(preViewPath);
					if(!resourceViewPath.exists()){
						resourceViewPath.mkdirs();
					}
					File oldFile = new File(filePath);
					File newFile = new File(preViewPath+"/"+filename+officeDestinateTypes.toLowerCase());
					log.info("file:"+filePath+" convert to:"+newFile);
					converter.convert(oldFile, newFile);
					
					log.info("succeed converter file " + filePath +" to " + preViewPath+"/"+filename+officeDestinateTypes.toLowerCase());
					
					connection.disconnect();
					
					map.put("time", time+"");
					map.put("type", officeDestinateTypes.toLowerCase());
					map.put("oldType", resource.getType());
					map.put("filename", filename);
					return this.getSuccessMap(map);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return this.getFailedMap("文档资源预览失败");
		}
		return this.getFailedMap("该类型文件不支持预览");
	}

	private boolean getResoucePermissions(String permissionName, long resourceId, long loginUserId) {
		Resource resource = this.resourcesDao.findOne(resourceId);
		if(resource.getUserId()==loginUserId){
			Role role = Cache.getRole(ENTITY_TYPE.RESOURCE+"_"+ROLE_TYPE.CREATOR);
			List<Permission> permissions = role.getPermissions();
			if (permissions != null && permissions.size() > 0) {
				for (Permission p : permissions) {
					if(p.getEnName().equals(permissionName.toLowerCase())){
						return true;
					}
				}
			}
		}
		//项目权限
		List<Permission> permissionsFromProject = this.projectService.getPermissionList(loginUserId, resource.getProjectId());
		for(Permission per : permissionsFromProject){
			if(per.getEnName().equals(permissionName.toLowerCase())){
				return true;
			}
		}
		return false;
	}
	/**
	 * @describe 修改资源名称	
	 * @author haijun.cheng	
	 * @date 2016年07月27日	
	 * @param loginUserId
	 * @param name
	 * @param resourceId
	 * @param targetType 
	 * @returnType Map<String,Object>
	 */
	public Map<String, Object> updateName(long loginUserId, String name, long resourceId) {
		if(name!=null&&name.length()>50){
			return this.getFailedMap("资源名称不能超过50个字符");
		}
		if(name.indexOf("\\")!=-1){
			return this.getFailedMap("名字中不能包含‘\\’字符");
		}
		name=name.replace("'", "\\'");
		Resource resource = this.findOne(resourceId);
		String oldName=resource.getName();
		if(null==resource || resource.getDel().equals(DELTYPE.DELETED)){
			return this.getFailedMap("资源不存在");
		}
		//修改前 做资源权限校验
		boolean isOwnerPermissions = this.getResoucePermissions("resource_rename",resourceId,loginUserId);
		if(!isOwnerPermissions){
			return this.getFailedMap("没有重命名该资源的权限");
		}
		//判断该文件夹下面是否有同样的名字
		String sameSql="select count(1) from T_RESOURCES where filePath='"+resource.getFilePath()+"' and parentId="+resource.getParentId()+" and name='"+name+"'"+" and id!="+resource.getId();
		@SuppressWarnings("deprecation")
		int count=this.jdbcTpl.queryForInt(sameSql);
		if(count>0){
			return this.getFailedMap("此位置已包含同名文件");
		}
		
		try {
			//修改服务器上的名字
			String filePath = baseDir + resource.getFilePath()+resource.getName();//资源路径
			String filePath1 = baseDir + resource.getFilePath()+name;
			String cmd = "mv " + filePath + " " + filePath1;
			Runtime run = Runtime.getRuntime();  
			Process p = run.exec(cmd);
			log.info("mv file " + filePath +" to " + filePath1 + ";result:"+ p);
			int status = p.waitFor();
	        log.info("=====>Process exitValue:"+status);
	        
	        //如果是图片修改修改缩略图
	        String [] picture = new String[]{".JPEG",".JPG",".PNG",".SWF",".SVG",".PCX",".DXF",".WMF",".EMF",".TIFF",".PSD",".GIF",".BMP"};
			boolean isPic = false;//是否是图片
			for(String str : picture){
				if(str.toLowerCase().equals(resource.getType().toLowerCase())){
					isPic = true;
					break;
				}
			}
			if(isPic){
				String abbrFilePath = baseDir + resource.getFilePath()+"abbr_"+resource.getName();//资源路径
				String abbrFilePath1 = baseDir +resource.getFilePath()+"abbr_"+name;
				String abbrcmd = "mv " + abbrFilePath + " " + abbrFilePath1;
				Runtime abbrrun = Runtime.getRuntime();  
				Process abbrp = abbrrun.exec(abbrcmd);
				log.info("=====>mv abbr file " + abbrFilePath +" to " + abbrFilePath1 + ";result:"+ abbrp);
				int abbrStatus = abbrp.waitFor();
		        log.info("=====>Process abbr exitValue:"+abbrStatus);
			}
	        
			//修改数据库里的名字
			User user=this.userDao.findOne(loginUserId);
			//修改当前目录或文件
//			String sql="update T_RESOURCES set updatedAt=now(),name='"+name+"' where id="+resourceId;
//		    log.info("修改当前目录或文件sql:"+sql);
//			int a = this.jdbcTpl.update(sql);
//		    log.info("====>修改状态为a值："+a);
			resource.setName(name);
			Date date=new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Timestamp goodsC_date = Timestamp.valueOf(sdf.format(date));//把时间转换
			resource.setUpdatedAt(goodsC_date);
			this.resourcesDao.save(resource);
		    //修改子目录
		    if(resource.getType().equals("dir")){
//		    	final List<Resource> list=new ArrayList<Resource>();
//				String listSql="select filePath,id from T_RESOURCES where filePath like '"+resource.getFilePath()+"%' and sourceType="+resource.getSourceType().ordinal() +" and id!="+resourceId+" and parentId!="+resource.getParentId();
//				log.info("修改子目录sql:"+listSql);
//				this.jdbcTpl.query(listSql.toString(),
//						new RowCallbackHandler() {
//							@Override
//							public void processRow(ResultSet rs) throws SQLException {
//								Resource vo  = new Resource();
//								vo.setFilePath(rs.getString("filePath"));
//								vo.setId(rs.getLong("id"));
//								list.add(vo);
//							}
//						});
//				for(Resource rs:list){
//					String oldfilePath=resource.getFilePath()+name;
//					String newFilePath=rs.getFilePath().replace(resource.getFilePath()+resource.getName(), oldfilePath);
//					String updateSql="update T_RESOURCES set filePath='"+ newFilePath +"',updatedAt= now() where id="+rs.getId();
//					this.jdbcTpl.update(updateSql);
//				}
		    	String childSql="update T_RESOURCES set updatedAt=now(),filePath=replace(filePath,'"+resource.getFilePath()+oldName+"/','"+resource.getFilePath()+name+"/') where filePath like '"+resource.getFilePath()+oldName+"%'";
		    	log.info("childSql===>"+childSql);
		    	int listnum = this.jdbcTpl.update(childSql);
			    log.info("子目录修改的个数："+listnum);
		    }
		    
			//增加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROCESS_RENAME_RESOURCE,resource.getProjectId(), new Object[]{resource,name});

		    return this.getSuccessMap("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("更新失败");
		}
			
	}

	/**
	 * @describe 批量删除资源	<br>
	 * @author haijun.cheng	<br>
	 * @date 2016年08月01日	<br>
	 * @param loginUserId
	 * @param name
	 * @param resourceId
	 * @throws InterruptedException 
	 * @returnType Map<String,Object>
	 */
	
	public Map<String, Object> deleteBatch(long loginUserId, String bactchIds, SOURCE_TYPE targetType) throws InterruptedException {
		
		String ids[]=bactchIds.split(",");
		log.info("--batchDelete resourceIds:-->"+bactchIds);
		for(String resourceId:ids){
			Resource srcRes = this.resourcesDao.findOne(Long.valueOf(resourceId));
			if(null==srcRes){
				continue;
			}
			//删除前 做资源权限校验
			boolean isOwnerPermissions = this.getResoucePermissions(ENTITY_TYPE.RESOURCE+"_"+CRUD_TYPE.REMOVE_FILE,Long.parseLong(resourceId),loginUserId);
			if(!isOwnerPermissions){
				throw new RuntimeException("没有删除资源文件权限,请联系管理员");
			}
			StringBuffer resourceIds = new StringBuffer();
			try {
				if(srcRes.getType().equals("dir")){
					//删除前 做资源权限校验
					boolean isOwnerPermissionsDir = this.getResoucePermissions(ENTITY_TYPE.RESOURCE+"_"+CRUD_TYPE.REMOVE_DIR,Long.parseLong(resourceId),loginUserId);
					if(!isOwnerPermissionsDir){
						throw new RuntimeException("没有删除资源文件夹权限,请联系管理员");
					}
					List<Resource> resourceList = this.findChildsByResource(srcRes.getFilePath()+srcRes.getName()+"/%");
					
					//如果删除的是目录,则需要将目录下面的文件全部删除
					String delSql = "delete from T_RESOURCES where filePath like '"+srcRes.getFilePath()+srcRes.getName()+"/%'";
					this.jdbcTpl.execute(delSql);
					
					for(Resource resource : resourceList){
						resourceIds.append(resource.getId()+",");
					}
				}
				resourceIds.append(resourceId);
				//删除流程 任务 讨论与资源及其下资源的关联
				String delEntitySql = "delete from T_ENTITY_RESOURCE_REL where resourceId in ("+resourceIds+")";
				log.info("=====================delete T_ENTITY_RESOURCE_REL --> resourceIds:"+resourceIds);
				this.jdbcTpl.execute(delEntitySql);
				
				//不管是目录,还是文件,都需要把自己删除
				this.resourcesDao.delete(Long.valueOf(resourceId));
				File f = new File(baseDir+srcRes.getFilePath()+srcRes.getName());
				log.info("=====================delete resource:"+f);
				FileSystemUtils.deleteRecursively(f);
				
				
				//删除服务器上的资源
				String [] picture = new String[]{".JPEG",".JPG",".PNG",".SWF",".SVG",".PCX",".DXF",".WMF",".EMF",".TIFF",".PSD",".GIF",".BMP"};
				boolean isPic = false;//是否是图片
				for(String str : picture){
					if(str.toLowerCase().equals(srcRes.getType().toLowerCase())){
						isPic = true;
						break;
					}
				}
				if(isPic){//如果是文件,并且有缩略图的话,需要将对应的缩略图删除
					String abbrFilePath = baseDir + srcRes.getFilePath()+"abbr_"+srcRes.getName();//资源路径
					String abbrCmd = "rm -rf " + abbrFilePath;
					Runtime abbrRun = Runtime.getRuntime();  
					Process abbrP;
					abbrP = abbrRun.exec(abbrCmd);
					log.info("rm -file " + abbrFilePath );
					int abbrStatus= abbrP.waitFor();
			        log.info("Process abbr exitValue:"+abbrStatus);
				}
//				String filePath = baseDir + srcRes.getFilePath()+srcRes.getName();//资源路径
//				String cmd = "rm -rf " + filePath;
//				Runtime run = Runtime.getRuntime();  
//				Process p;
//				
//				p = run.exec(cmd);
//				log.info("rm -file " + filePath );
//				int status= p.waitFor();
//		        log.info("Process exitValue:"+status);
			} catch (IOException e) {
				e.printStackTrace();
				return this.getFailedMap("批量删除失败");
			}

			//增加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.RESOURCE_DEL,srcRes.getProjectId(), new Object[]{srcRes});
		}
	      return this.getSuccessMap("批量删除成功");
	}
	/**
	 * 把资源设为公开
	 * @param loginUserId
	 * @param resourceIds
	 * @auth chj
	 * @serialData 2016-08-16
	 * @return
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public Map<String, Object> setResourcePublic(Long resourceId,Long loginUserId,String state) throws IOException, InterruptedException {
		Resource resource = this.findOne(resourceId);
		if(null==resource || resource.getDel().equals(DELTYPE.DELETED)){
			return this.getFailedMap("资源不存在");
		}
		//公开资源前 做资源权限校验
		boolean isOwnerPermissions = this.getResoucePermissions("resource_public",resourceId,loginUserId);
		if(!isOwnerPermissions){
			return this.getFailedMap("没有公开该资源的权限");
		}
		//将资源设为公开
		if(state.equals("on")){
//			String newFileName=randomUUID+resource.getName().substring(resource.getName().lastIndexOf("."), resource.getName().length());
//			String filePath = baseDir + resource.getFilePath()+resource.getName();//资源路径
//			String filePath1 = publicDir + newFileName;
//			String cmd = "cp " + filePath + " " + filePath1;
//			Runtime run = Runtime.getRuntime();  
//			Process p = run.exec(cmd);
//			log.info("=========>cmd： " + cmd);
//			int status = p.waitFor();
//	        log.info("Process exitValue:"+status);
			//更新本地数据库
			if(resource.getType().equals("dir")){
				String updatsql="update T_RESOURCES set isPublic=1 where del=0 and (filePath like '"+resource.getFilePath()+resource.getName()+"/%' or id="+resource.getId()+")";
				this.jdbcTpl.update(updatsql);
			}else{
				String sql="update T_RESOURCES set isPublic=1 where id="+resourceId;
				this.jdbcTpl.update(sql);
			}
			resource.setDownLoadPath(xtHost+"/downPubResource/"+resource.getUuid());
			
			//增加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.RESOURCE_OPEN,resource.getProjectId(), new Object[]{resource});

			return this.getSuccessMap(resource);
		}else{//将资源设为不公开
//			String filePath = publicDir + resource.getUserId();
//			String cmd = "rm -rf " + filePath;
//			Runtime run = Runtime.getRuntime();  
//			Process p = run.exec(cmd);
//			log.info("=========>cmd： " + cmd);
//			int status = p.waitFor();
//	        log.info("Process exitValue:"+status);
	      //更新本地数据库
			if(resource.getType().equals("dir")){
				String updatsql="update T_RESOURCES set isPublic=0 where filePath like '"+resource.getFilePath()+resource.getName()+"/%' and del=0";
				this.jdbcTpl.update(updatsql);
			}else{
				String sql="update T_RESOURCES set isPublic=0 where id="+resourceId;
				this.jdbcTpl.update(sql);
			}
			//增加动态
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.RESOURCE_CLOSE,resource.getProjectId(), new Object[]{resource});

			return this.getSuccessMap(resource);
		}
	}

	public Resource findByUuid(String uuid) {
		// TODO Auto-generated method stub
		return this.resourcesDao.findByUuidAndIsPublicAndDel(uuid,1,DELTYPE.NORMAL);
	}
/**
 * 查询文件夹下面所有公开的资源，打包成压缩文件下载
 * @author haijun.cheng
 * @date 2016-08-31
 * @param uuid
 * @return
 * @throws IOException 
 * @throws InterruptedException 
 */
	public String findAllPath(String uuid) throws IOException, InterruptedException {
			Resource res=findByUuid(uuid);
			String filename=res.getName();
			String excludeName="--exclude ";
//			long time = System.currentTimeMillis();  
//	        String t = String.valueOf(time);
//	        filename=res.getProjectId()+"_"+res.getUserId()+"_"+t+".zip";
//			String src=baseDir+res.getFilePath()+res.getName();
//			String dest=baseDir+res.getFilePath()+filename;
//			ZipUtil.zip(src, dest);
			
			final List<Map<String,String>> diffFileList=new ArrayList<Map<String,String>>();
			String sql="select filepath,name from T_RESOURCES where filePath like '"+res.getFilePath()+res.getName()+"/%' and del=0 and isPublic=0";
			this.jdbcTpl.query(sql.toString(), 
					new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					Map<String,String> map = new HashMap<>();
					map.put("filepath", rs.getString("filepath"));
					map.put("name", rs.getString("name"));
					diffFileList.add(map);
				}
			});
			
			if(diffFileList.size()!=0){
				for(Map<String,String> map:diffFileList){
					excludeName=excludeName+baseDir+map.get("filepath")+map.get("name")+",";
				}
				excludeName=excludeName.substring(0,excludeName.length()-1);
			}else{
				excludeName="";
			}
		
			String filePath = baseDir + res.getFilePath()+res.getName();//资源路径
			String cmd = "zip -r " + filename +".zip "+filePath+" "+excludeName;
			
			String [] sh_command={"/bin/sh","-c",cmd};
			String result = this.syncExecShell(sh_command, new File(baseDir+res.getFilePath()));
			Runtime run = Runtime.getRuntime();  
			Process p;
			p = run.exec(cmd);
			log.info("zip cmd= " + cmd );
			int status= p.waitFor();
	        log.info("Process result:"+result+",status:"+status);
			
			return filename;
	}

public Long findProjectIdByResourceId(Long resourceId) {
	EntityResourceRel rel=entityResourceRelDao.findByResourceIdAndDel(resourceId, DELTYPE.NORMAL);
	if(rel!=null){
		return rel.getEntityId();
	}
	return null;
}
	
	
}
