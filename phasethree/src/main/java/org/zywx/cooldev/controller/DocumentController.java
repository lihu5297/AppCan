package org.zywx.cooldev.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DOC_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.DOC_PUB_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.document.Document;
import org.zywx.cooldev.entity.document.DocumentChapter;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.query.DOCQuery;
import org.zywx.cooldev.service.DocumentChapterService;
import org.zywx.cooldev.service.DocumentService;
import org.zywx.cooldev.system.Cache;
/**
 * 
 * @describe 文档controller <br>
 * @author jiexiong.liu <br>
 * @date 2015年8月12日 下午6:37:23 <br>
 *
 */
@Controller
@RequestMapping(value = "/document")
public class DocumentController extends BaseController {

	@Autowired
	protected DocumentService documentService;

	@Autowired
	protected DocumentChapterService documentChapterService;

	/**
	 * 
	 * @describe 创建文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月12日 下午8:23:46 <br>
	 * @param request
	 * @param response
	 * @param doc
	 * @param loginUserId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "", method = { RequestMethod.POST })
	public ModelAndView createDoc(HttpServletRequest request, HttpServletResponse response, Document doc,
			@RequestHeader(value = "loginUserId") Long loginUserId) {
		try {
			//文档列表字符限制
			if(doc.getName().matches(".*[/]+.*")){
				return this.getFailedModel("文档标题不能包含特殊字符左斜扛");
			}
			if(doc.getName()!=null&&doc.getName().length()>1000){
				return this.getFailedModel("文档标题不能超过1000个字符");
			}
			if (null == doc.getName() || "".equals(doc.getName())) {
				return this.getFailedModel("name is null");
			}
			if (null == doc.getProjectId() || "".equals(doc.getProjectId())) {
				return this.getFailedModel("projectId is null");
			}

			doc.setUserId(loginUserId);
			doc = this.documentService.addDoc(doc);

		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_CREATE, doc.getProjectId(),
				new Object[] { doc });
		return this.getSuccessModel(doc);
	}

	/**
	 * 
	 * @describe 更新文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 上午10:13:19 <br>
	 * @param request
	 * @param response
	 * @param doc
	 * @param loginUserId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "", method = { RequestMethod.PUT })
	public ModelAndView updateDoc(HttpServletRequest request, HttpServletResponse response, Document doc,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId) {
		Document document = null;
		try {
			if(doc.getName()!=null&&doc.getName().length()>1000){
				return this.getFailedModel("文档标题不能超过1000个字符");
			}
			if (null == loginUserId || "".equals(loginUserId.toString())) {

				return this.getFailedModel("loginUserId is null");
			}
			if (null == doc.getId() || "".equals(doc.getId())) {
				return this.getFailedModel("id is null");
			}
			if (null == doc.getName() || "".equals(doc.getName())) {
				return this.getFailedModel("name is null");
			}

			document = this.documentService.getDoc(doc.getId());
			if (null == document) {
				return this.getFailedModel("the document does not exist or is not you document");
			}

			document.setName(doc.getName());
			if(null!=doc.getDescrib()){
				document.setDescrib(doc.getDescrib());
			}
			if (null != doc.getProjectId() && 0 != doc.getProjectId()) {
				document.setProjectId(doc.getProjectId());
			}
			document.setUpdatedAt(new Timestamp(new Date().getTime()));
			document = this.documentService.updateDoc(document);

		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_UPDATE, projectId,
				new Object[] { document });

		return this.getSuccessModel(document);

	}

	/**
	 * 
	 * @describe 删除文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 上午10:39:39 <br>
	 * @param request
	 * @param response
	 * @param docId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{docId}", method = { RequestMethod.DELETE })
	public ModelAndView deleteDoc(HttpServletRequest request, HttpServletResponse response, @PathVariable Long docId,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId) {
		try {
			if (null == loginUserId || "".equals(loginUserId.toString())) {
				return this.getFailedModel("loginUserId is null");
			}
			if (null == docId || -1 == docId.intValue()) {
				return this.getFailedModel("id is null");
			}
			this.documentService.deleteDoc(docId);

		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		Document document = this.documentService.findDocument(docId);
		// 添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_DELETE, projectId,
				new Object[] { document });
		return this.getAffectModel();
	}

	/**
	 * 
	 * @describe 发布/回收文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 下午1:45:07 <br>
	 * @param request
	 * @param response
	 * @param docId
	 * @param loginUserId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/pub/{docId}", method = { RequestMethod.PUT })
	public ModelAndView publishOrRetrieveDoc(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long docId, @RequestParam DOC_PUB_TYPE opertion,
			@RequestHeader("loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId) {

		try {
			if (DOC_PUB_TYPE.PUBLISHED != opertion && DOC_PUB_TYPE.RETRIEVED != opertion) {
				return this.getFailedModel("publish or retrieve document failed");
			}
			this.documentService.upgradePubOrRetDoc(docId, opertion);

		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		Document document = this.documentService.findDocument(docId);
		// 添加动态
		if (DOC_PUB_TYPE.PUBLISHED.compareTo(opertion)==0) {
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_PUBLISH, projectId,
					new Object[] { document });
		} else
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_RETRIEVED, projectId,
					new Object[] { document });
		return this.getAffectModel();
	}

	/**
	 * 
	 * @describe 获取单个文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 上午11:35:14 <br>
	 * @param request
	 * @param response
	 * @param docId
	 * @param loginUserId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{docId}", method = RequestMethod.GET )
	public ModelAndView getDoc(HttpServletRequest request, HttpServletResponse response, @PathVariable Long docId,
			@RequestHeader("loginUserId") Long loginUserId) {
		try {
			Document doc = this.documentService.getDoc(docId);
			String required = (ENTITY_TYPE.DOCUMENT + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
			// 项目成员权限
			Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,
					loginUserId,doc.getProjectId());
			if (null != pMapAsProjectMember && pMapAsProjectMember.containsKey(doc.getProjectId())) {
				List<String> pListAsProjectMember = pMapAsProjectMember.get(doc.getProjectId());
				Map<String, Integer> pMap = new HashMap<>();
				if (pListAsProjectMember != null) {
					for (String p : pListAsProjectMember) {
						pMap.put(p, 1);
					}
				}
				if(doc.getUserId().equals(loginUserId)){
					Role role = Cache.getRole(ENTITY_TYPE.DOCUMENT+"_"+Enums.ROLE_TYPE.CREATOR);
					List<Permission> perms = role.getPermissions();
					for(Permission perm : perms){
						pMap.put(perm.getEnName(), 1);
					}
				}
				Map<String, Object> element = new HashMap<>();
				Project pro = this.projectService.getProject(doc.getProjectId());
				element.put("object", doc);
				element.put("project", pro);
				User user = this.userService.findUserById(doc.getUserId());
				element.put("userName", user.getUserName());
				element.put("permissions", pMap);
				return this.getSuccessModel(element);
			} else
				return this.getFailedModel("no more data");
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 获取文档列表 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 上午11:46:17 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.GET )
	public ModelAndView getDocList(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, @RequestParam(value = "type", required = false) String type,
			DOCQuery query) {
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
				return this.getFailedModel(nfe.getMessage());
			}

			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
			
			Project pro = null;
			Map<String, Object> map = new HashMap<>();
			if(null!=query.getProjectId()){
				pro = this.projectService.getProject(query.getProjectId());
				map.put("project", pro);
			}
			List<DOC_MEMBER_TYPE> typeQ = new ArrayList<>();
			List<Map<String, Object>> message = new ArrayList<>();
			String required = (ENTITY_TYPE.DOCUMENT + "_" + CRUD_TYPE.RETRIEVE).toLowerCase();
			// 项目成员权限
			Map<Long, List<String>> pMapAsProjectMember=null;
			if(null != query.getProjectId()){
				pMapAsProjectMember= projectService.permissionMapAsMemberWithAndOnlyByProjectId(required,loginUserId,query.getProjectId());
			}else{
			    pMapAsProjectMember = projectService.permissionMap(required,loginUserId);
			}
			//Map<Long, List<String>> pMapAsProjectMemberWithOnePermission = new HashMap<Long, List<String>>();
			List<Long> projectIds = new ArrayList<Long>();
			List<Long> teamIds = new ArrayList<>();
			teamIds.add(-99L);
			//team筛选条件
			if(null!=query.getTeamId()){
				teamIds.add(query.getTeamId());
			}
			List<Long> projectIdsFromTeams = new ArrayList<Long>();
			if(null!=query.getTeamId() || (StringUtils.isNotBlank(query.getTeamName()) && !"%%".equals(query.getTeamName())
					&& !"%null%".equals(query.getTeamName())     )){
				projectIdsFromTeams = this.projectService.getProjectIdsByTeam(teamIds, query.getTeamName());
			}
			//项目筛选条件
			if (null != pMapAsProjectMember && null==query.getProjectId() && pMapAsProjectMember.keySet().size()>0) {
				projectIds = new ArrayList<>(pMapAsProjectMember.keySet());
			} else if (null != query.getProjectId() && pMapAsProjectMember.containsKey(query.getProjectId())) {
				projectIds = new ArrayList<>();
				projectIds.add(query.getProjectId());
			} else{
				List<String> list = new ArrayList<>();
				map.put("list", list);
				map.put("total", 0);
				map.put("permission", new HashMap<>());
				return this.getSuccessModel(map);
			}
			
			if(null!=query.getTeamId() || (StringUtils.isNotBlank(query.getTeamName()) && !"%%".equals(query.getTeamName())  )){
				projectIds = this.getAllIn(projectIds, projectIdsFromTeams);
				if(projectIds.size()==0){
					List<String> list = new ArrayList<>();
					map.put("list", list);
					map.put("total", 0);
					map.put("permission", new HashMap<>());
					return this.getSuccessModel(map);
				}
			}
			
			if(StringUtils.isNotBlank(query.getProjectName()) && !"%%".equals(query.getProjectName())){
				projectIds = projectService.findByIdInAndNameLikeAndDel(projectIds, query.getProjectName(), DELTYPE.NORMAL);
			}
			
			String[] types = type != null && type != "" ? type.split(",") : new String[] {};
			if (types.length > 0) {
				for (int a = 0; a < types.length; a++) {
					typeQ.add(types[a].equals(DOC_MEMBER_TYPE.SPONSOR.name()) ? DOC_MEMBER_TYPE.SPONSOR
							: DOC_MEMBER_TYPE.ACTOR);
				}
			} else {
				typeQ.add(DOC_MEMBER_TYPE.SPONSOR);
				typeQ.add(DOC_MEMBER_TYPE.ACTOR);
			}
			
			//添加查询实体参数
			Map<String, Object> mapList = this.documentService.getDocList(loginUserId,projectIds, typeQ,query,pageable);
			if(null==mapList){
				List<String> list = new ArrayList<>();
				map.put("list", list);
				map.put("total", 0);
				map.put("permission", new HashMap<>());
				return this.getSuccessModel(map);
			}
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) mapList.get("list");
			if(null!=list){
				for (Map<String, Object> map1 : list) {
					Map<String, Integer> pMap = new HashMap<>();
					//添加创建者权限
					if(map1.get("userId").equals(loginUserId)){
						Role role = Cache.getRole(ENTITY_TYPE.DOCUMENT+"_"+Enums.ROLE_TYPE.CREATOR);
						List<Permission> perms = role.getPermissions();
						for(Permission perm : perms){
							pMap.put(perm.getEnName(), 1);
						}
					}
					//添加参与者权限
					List<String> pListAsProjectMember = pMapAsProjectMember.get(map1.get("projectId"));
					if (pListAsProjectMember != null) {
						for (String p : pListAsProjectMember) {
							pMap.put(p, 1);
						}
					}
					Map<String, Object> element = new HashMap<>();
					element.put("object", map1);
					element.put("permissions", pMap);
					message.add(element);
				}
			}
			
//			Map<Long, List<String>> pListAsProjectMember = this.projectService.permissionMapAsMemberWith(required,loginUserId);			
			
			HashMap<String,Long> map1 = new HashMap<>();
			if(null!=pMapAsProjectMember){
				List<String> lis = pMapAsProjectMember.get(query.getProjectId());
					if(null!=lis){
						for(String str : lis){
							map1.put(str, 1L);
						}
					}
			}
			map.put("permission", map1);
			
			map.put("total", mapList.get("count"));
			map.put("list", message);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	private List<Long> getAllIn(List<Long> projectIds, List<Long> projectIdsFromTeams) {
		List<Long> allIn = new ArrayList<Long>();
		for(Long proId : projectIds){
			for(Long pId : projectIdsFromTeams){
				if(proId.equals(pId)){
					allIn.add(proId);
				}
			}
		}
		return allIn;
	}

	/**
	 * 
	 * @describe 导出markdown 文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月17日 下午5:23:29 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docId
	 * @return <br>
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/export/{docId}", method = RequestMethod.GET )
	public ModelAndView exportDocument(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, @PathVariable Long docId) {
		try {
			String obj = null;
			obj = this.documentService.exportDocument(loginUserId, docId);
			
			Document document = this.documentService.findDocument(docId);
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_EXPORT, document.getProjectId(), new Object[]{document});
			
			return this.getSuccessModel(obj);
		} catch (IOException e) {
			return this.getFailedModel(e.getMessage());
		}

	}

	/**
	 * 
	 * @describe 导入markdown文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月26日 上午9:18:57 <br>
	 * @return <br>
	 * @returnType Map<?,?>
	 *
	 */
	@RequestMapping(value = "/import", method = RequestMethod.GET )
	public ModelAndView importDocumentChapter(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, @RequestParam("filename") String filename,
			@RequestParam("projectId") Long projectId , @RequestParam("docId")Long docId,
			@RequestParam("parentId")Long parentId,@RequestParam(value="forceImport",required=false,defaultValue="false")boolean forceImport) {
		DocumentChapter docC = null;
		try {
			log.info(String.format("import markdown document loginUserId[%d] filename[%s] projectId[%d] docId[%d] parentId[%d] forceImport[%s]", 
					loginUserId,filename,projectId,docId,parentId,forceImport));
			String name = filename.substring(0,filename.lastIndexOf("."));
			if(!forceImport && this.documentChapterService.isExistDocC(name,docId,parentId)){
				Map<String, Object> ret = new HashMap<>();
				ret.put("status", "replace");
				ret.put("message", name+"内容已存在，是否仍需导入，导入后会覆盖原有内容");
				return new ModelAndView("",ret);
			}
			docC = this.documentService.importDocumentChapter(loginUserId, projectId, filename,docId,parentId);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_IMPORT_CREATE, projectId,
				new Object[] { docC });
		return this.getSuccessModel(docC);

	}

	/**
	 * 
	 * @describe 文档中心内搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午4:21:39 <br>
	 * @return <br>
	 * @returnType Map<?,?>
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/search/{docId}", method = { RequestMethod.GET })
	public ModelAndView searchDocument(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId, @RequestParam String query,
			@PathVariable(value = "docId") Long docId) {
		try {
			log.info("document search content:"+query);
			String querySC = "";
			Map<String, Object> map = new HashMap<String, Object>();
			// 页码
			String pageNo = request.getParameter("pageNo");
			// 页尺寸
			String pageSize = request.getParameter("pageSize");
			int ipageNo = 1;
			int ipageSize = 15;
			if (null != pageNo && null != pageSize) {
				try {
					ipageNo = Integer.parseInt(pageNo);
					ipageSize = Integer.parseInt(pageSize);
				} catch (NumberFormatException nfe) {
					return this.getFailedModel("pageNo or pageSize is null");
				}
			}
			if (null == query || "" == query) {
				return this.getFailedModel("query is null");
			}else
				querySC = this.documentChapterService.getSpecialCharQuery(query);
			Page<DocumentChapter> docCs = this.documentChapterService.SearchDocumentPart("%" + querySC + "%", docId,
					ipageNo, ipageSize);
//			int count = this.documentChapterService.SearchDocumentPartCount("%" + query + "%", docId);
			List<Map<String, String>> list = this.documentChapterService.getWrapResult(docCs.getContent(), query);
			map.put("total", docCs.getTotalElements());
			map.put("list", list);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/project/list", method = { RequestMethod.GET })
	public ModelAndView projectCreatDocument(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader("loginUserId") Long loginUserId) {
		try {
			String required = (ENTITY_TYPE.DOCUMENT + "_" + CRUD_TYPE.CREATE).toLowerCase();
			// 项目成员权限
//			Map<Long, List<String>> pMapAsProjectMember = projectService.permissionMapAsMemberWith(required,loginUserId);
//			Set<Long> proIds = pMapAsProjectMember.keySet();
			Set<Long> proIds = projectService.getProjectsByRequiredAndLoginUserId(required,loginUserId);
			List<Project> list = new ArrayList<>();
			if(null!=proIds && proIds.size()>0){
				list = this.projectService.findProjectListByIds(proIds);
			}
			return this.getSuccessModel(list);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		
	}
//	
//	public static void main(String args[]){
//		
//		Properties properties = System.getProperties();
//		for(Object obj : properties.keySet()){
//			String result = properties.getProperty(obj.toString());
//			System.out.println("key:"+obj.toString()+",value:"+result);
//		}
//		
//		System.setProperty("sun.jnu.encoding","utf-8");
//		String filename = "我的文档.md";
//		filename = filename.substring(0,filename.indexOf("."));
//		
//		String filepath = "/mnt/glfs/coopDevelopment_online/downloadDoc/";
//		
//		try {
//			File file = new File(filepath,new String(filename.getBytes(System.getProperty("sun.jnu.encoding")),"utf-8"));
//			System.out.println("sun.jnu.encoding:"+System.getProperty("sun.jnu.encoding"));
//			System.out.println("filepath:"+filepath);
//			System.out.println("filename:"+filename);
//			if(!file.exists()){
//				file.createNewFile();
//			}
//			FileOutputStream out = new FileOutputStream(file);
//			String str =new String(filename.getBytes(),"utf-8");
//			byte[] b = str.getBytes("utf-8");
//			out.write(b);
//			out.close();
//			
//			FileInputStream in = new FileInputStream(file);
//			byte[] bb=new byte[in.available()];//新建一个字节数组
//			in.read(bb);//将文件中的内容读取到字节数组中
//			in.close();
//			String str2=new String(b);//再将字节数组中的内容转化成字符串形式输出
//			System.out.println(str2);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	/**
	 * 初始化拼音字段值
	 */
    @ResponseBody
    @RequestMapping(value="/addPinYin",method=RequestMethod.GET)
    public Map<String,Object> addPinYin(){
    	try{
    	   int affected=this.documentService.addPinYin();
    	   Map<String,Object> affectedMap=new HashMap<String,Object>();
    	   affectedMap.put("affected", affected);
    	   return this.getSuccessMap(affectedMap);
    	}catch(Exception e){
    		e.getStackTrace();
    		return this.getFailedMap(e.getMessage());
    	}
    }
}
   