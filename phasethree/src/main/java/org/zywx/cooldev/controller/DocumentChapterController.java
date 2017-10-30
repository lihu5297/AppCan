package org.zywx.cooldev.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DOC_CHAPTER_TYPE;
import org.zywx.cooldev.commons.Enums.DOC_PUB_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.entity.document.Document;
import org.zywx.cooldev.entity.document.DocumentChapter;
import org.zywx.cooldev.service.DocumentChapterService;
import org.zywx.cooldev.service.DocumentMarkerService;
import org.zywx.cooldev.util.RequestParamValidate;

@Controller
@RequestMapping(value = "/docChapter")
public class DocumentChapterController extends BaseController {

	@Autowired
	protected DocumentChapterService documentChapterService;
	@Autowired
	protected DocumentMarkerService documentMarkerService;

	/**
	 * 
	 * @describe 创建文档节点（包括目录和章节） <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月14日 上午9:22:26 <br>
	 * @param request
	 * @param response
	 * @param docC
	 * @param loginUserId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "", method = { RequestMethod.POST })
	public ModelAndView createDocChapter(HttpServletRequest request, HttpServletResponse response, DocumentChapter docC,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId) {
		log.info("run into create docChapter");
		try {
			if(docC.getName().matches(".*[/]+.*")){
				return this.getFailedModel("文档标题不能包含特殊字符左斜扛");
			}
			if(docC.getType()!=null&&docC.getType().equals(DOC_CHAPTER_TYPE.CHAPTER)){
				if(docC.getName()!=null&&docC.getName().length()>10){
					return this.getFailedModel("文档目录名称不能超过10个字符");
				}
			}else{
				if(docC.getName()!=null&&docC.getName().length()>1000){
					return this.getFailedModel("文档标题不能超过1000个字符");
				}
			}
			HashMap<Object, Object> map1 = new HashMap<>();

			if (DOC_CHAPTER_TYPE.PART.equals(docC.getType())) {
				map1 = RequestParamValidate.ValidatePrama(request, response,
						new String[] { "name", "documentId", "type", "parentId", "contentHTML", "contentMD" });
				if (!map1.isEmpty()) {
					return this.getFailedModel(map1.get("message"));
				}
			} else {
				map1 = RequestParamValidate.ValidatePrama(request, response,
						new String[] { "name", "documentId", "type", "parentId" });
				if (!map1.isEmpty()) {
					return this.getFailedModel(map1.get("message"));
				}
			}
			docC.setUserId(loginUserId);
			Boolean bool = this.documentChapterService.isExist(docC);
			if(bool){
				return this.getFailedModel("同一级别，不能创建同名的文档目录");
			}
			docC = this.documentChapterService.addDocC(docC);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENTCHAPTER_CREATE, projectId,
				new Object[] { docC });
		return this.getSuccessModel(docC);
	}

	/**
	 * 
	 * @describe 文档章节更新 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:09:57 <br>
	 * @param request
	 * @param response
	 * @param docC
	 * @param loginUserId
	 * @param docCId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{docCId}", method = { RequestMethod.PUT })
	public ModelAndView updateDocChapter(HttpServletRequest request, HttpServletResponse response, DocumentChapter docC,
			@RequestHeader(value = "loginUserId") Long loginUserId, @PathVariable Long docCId,
			@RequestParam("projectId") Long projectId) {
		log.info("run into update docChapter, loginUserId="+loginUserId);
		try {
			if(docC.getName().matches(".*[/]+.*")){
				return this.getFailedModel("文档标题不能包含特殊字符左斜扛");
			}
			if(docC.getType()!=null&&docC.getType().equals(DOC_CHAPTER_TYPE.CHAPTER)){
				if(docC.getName()!=null&&docC.getName().length()>10){
					return this.getFailedModel("文档目录名称不能超过10个字符");
				}
			}else{
				if(docC.getName()!=null&&docC.getName().length()>1000){
					return this.getFailedModel("文档标题不能超过1000个字符");
				}
			}
			HashMap<Object, Object> map1 = new HashMap<>();

			DocumentChapter docCC = this.documentChapterService.findOne(docCId);

			if (null == docCC.getId() || 0 == docCC.getId().intValue()) {
				return this.getFailedModel("the documentChapter does not exist");
			}

			if (DOC_CHAPTER_TYPE.PART.equals(docCC.getType())) {
				map1 = RequestParamValidate.ValidatePrama(request, response,
						new String[] { "name", "contentHTML", "contentMD" });
				if (!map1.isEmpty()) {
					return this.getFailedModel(map1.get("message"));
				}
				docCC.setName(docC.getName());
				docCC.setContentMD(docC.getContentMD());
				docCC.setContentHTML(docC.getContentHTML());
			} else {
				map1 = RequestParamValidate.ValidatePrama(request, response, new String[] { "name" });
				if (!map1.isEmpty()) {
					return this.getFailedModel(map1.get("message"));
				}
				docCC.setName(docC.getName());
			}
			if(null!=docC.getParentId()){
				docCC.setParentId(docC.getParentId());
			}
			docCC.setUpdatedAt(new Timestamp(new Date().getTime()));
			docC = this.documentChapterService.updateDocC(docCC);
			log.info(docC.toString());
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENTCHAPTER_UPDATE, projectId,
				new Object[] { docC });
		return this.getSuccessModel(docC);

	}

	/**
	 * 
	 * @describe 删除文档章节 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:38:02 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docCId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{docCId}", method = { RequestMethod.DELETE })
	public ModelAndView deleteDocChapter(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @PathVariable Long docCId,
			@RequestParam("projectId") Long projectId) {
		DocumentChapter docC = this.documentChapterService.findOne(docCId);
		log.info("run into delete docChapter, loginUserId="+loginUserId);
		//删除文档之前查询路径
		Document doc = this.documentService.getDoc(docC.getDocumentId());
		log.info("get docPath:"+docC);
		String docPath = doc.getName()+">"+this.documentService.getDocPath(docC);
		
		try {
			this.documentChapterService.deleteDocC(docCId);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENTCHAPTER_DELETE, projectId,
				new Object[] { docPath });
		return this.getAffectModel();
	}

	/**
	 * 
	 * @describe 获取单个章节详情 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:56:50 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docCId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/{docCId}", method = { RequestMethod.GET })
	public ModelAndView getDocChapter(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @PathVariable Long docCId) {
		log.info("run into query docChapter, loginUserId="+loginUserId);
		try {
			HashMap<String, Object> map = new HashMap<>();
			DocumentChapter docC = this.documentChapterService.findOne(docCId);
			if (null == docC) {
				return this.getFailedModel("文档内容不存在，或已被删除");
			}
			map.put("Object", docC);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}
	

	/**
	 * 
	 * @describe 发布、回收章节 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:56:50 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docCId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/pub/{docCId}", method = { RequestMethod.PUT })
	public ModelAndView pubDocChapter(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @PathVariable Long docCId,
			@RequestParam Enums.DOC_PUB_TYPE opertion, @RequestParam("projectId") Long projectId) {
		log.info("run into publish or retrieve docChapter, loginUserId="+loginUserId);
		try {

			if (DOC_PUB_TYPE.PUBLISHED != opertion && DOC_PUB_TYPE.RETRIEVED != opertion) {
				return this.getFailedModel("publish or retrieve documentChapter failed");
			}
			this.documentChapterService.upgradePubOrRetDocC(docCId, opertion);
			DocumentChapter docC = this.documentChapterService.findOne(docCId);
			if (DOC_PUB_TYPE.PUBLISHED.compareTo(opertion)==0) {
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENTCHAPTER_PUBLISH, projectId,
						new Object[] { docC });
			} else
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENTCHAPTER_RETRIEVED, projectId,
						new Object[] { docC });
			return this.getAffectModel();
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 获取文档下的章节目录 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午3:12:07 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/menu/{docId}", method = { RequestMethod.GET })
	public ModelAndView getChapterMenuList(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @PathVariable Long docId) {
		log.info("run into query menu from docChapter, loginUserId="+loginUserId);
		try {

			List<Map<String, Object>> list = this.documentChapterService.getAllChapterAndPart(docId);

			return this.getSuccessModel(list);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}

	}
	

	/**
	 * 
	 * @describe 获取目录列表 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月18日 下午5:12:39 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docId
	 * @return <br>
	 * @returnType Map<?,?>
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/select/{docId}", method = { RequestMethod.GET })
	public ModelAndView getChapterList(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @PathVariable Long docId) {
		log.info("run into query select from docChapter, loginUserId="+loginUserId);
		try {
			List<Map<String, Object>> list = this.documentChapterService.getAllChapter(docId);

			return this.getSuccessModel(list);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 章节排序 上下移动
	 * 
	 * @describe <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月17日 上午10:12:54 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docCId1
	 * @param docCId2
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/sort", method = { RequestMethod.PUT })
	public ModelAndView sortChapter(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId,
			@RequestParam(value = "docCId1", required = true) Long docCId1,
			@RequestParam(value = "docCId2", required = true) Long docCId2) {
		log.info("run into sort docChapter, loginUserId="+loginUserId);
		try {
			this.documentChapterService.upgradeSortChapter(docCId1, docCId2);
			
			DocumentChapter docc = this.documentChapterService.findOne(docCId1);
			Document document = this.documentService.findDocument(docc.getDocumentId());
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_SORT, document.getProjectId(), new Object[]{document});
			
			return this.getAffectModel();
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 下载单个文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月26日 下午5:17:59 <br>
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "/download", method = { RequestMethod.GET })
	public ModelAndView downloadDocument(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("docCId") Long docCId,
			@RequestParam("type") String type) {
		log.info("run into download docChapter, loginUserId="+loginUserId);
		try {
			DocumentChapter docC = this.documentChapterService.findOneByType(docCId, DOC_CHAPTER_TYPE.PART);
			
			if (docC == null) {
				this.getFailedModel("the documentChapter does not offered download");
			}
			if (type.equals("HTML"))
				return this.getSuccessModel(docC);
			else
				return this.getSuccessModel(docC);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}
	
	
	/**
	 * 
	 * @describe 获取已发布的章节内容 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午3:12:07 <br>
	 * @param request
	 * @param response
	 * @param docId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/pub/{docId}", method = { RequestMethod.GET })
	public ModelAndView getPubChapterList(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long docId) {
		log.info("run into query published docChapter");
		try {
			
			Map<String, Object> list = this.documentChapterService.getPubChapterAndPart(docId);
			
			return this.getSuccessModel(list);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
		
	}
	
	/**
	 * 
	 * @describe 获取已发布单个章节详情 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:56:50 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docCId
	 * @return
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/pubC/{docCId}", method = { RequestMethod.GET })
	public ModelAndView getDocChapter(HttpServletRequest request, HttpServletResponse response,
			 @PathVariable Long docCId) {
		log.info("run into query one published docChapter");
		try {
			HashMap<String, Object> map = new HashMap<>();
			DocumentChapter docC = this.documentChapterService.findOne(docCId);
			if (null == docC) {
				return this.getFailedModel("the documentChapter does not exist or has been deleted");
			}
			map.put("Object", docC);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @describe 下载已发布单个章节详情 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:56:50 <br>
	 * @param request
	 * @param response
	 * @param docCId
	 * @return
	 * @throws IOException 
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/download/{docCId}", method = { RequestMethod.GET })
	public void downloadDocChapter(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long docCId,@RequestParam("type") String type) throws IOException {
		log.info("run into download published docChapter");
		try {
			 this.documentChapterService.downloadDocumentChapter(docCId, type, response);
		} catch (Exception e) {
			e.printStackTrace();
			PrintWriter os = response.getWriter();  
			os.write(e.getMessage()); 
		}
	}
	
	/**
	 * 
	 * @describe 发布文档内搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:56:50 <br>
	 * @param request
	 * @param response
	 * @param docCId
	 * @return
	 * @throws IOException 
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "/search/{docId}", method = { RequestMethod.GET })
	public ModelAndView searchDocChapter(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String query,@PathVariable Long docId) throws IOException {
		log.info("run into search published docChapter");
		try {
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
				log.info("--------->querySC:"+querySC);
			Page<DocumentChapter> docCs = this.documentChapterService.PubSearchDocumentPart("%" + querySC + "%", docId,
					ipageNo, ipageSize);
//			int count = this.documentChapterService.PubSearchDocumentPartCount("%" + query + "%", docId);
			List<Map<String, String>> list = this.documentChapterService.getWrapResult(docCs.getContent(), query);
			map.put("total", docCs.getTotalElements());
			map.put("list", list);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}

}
