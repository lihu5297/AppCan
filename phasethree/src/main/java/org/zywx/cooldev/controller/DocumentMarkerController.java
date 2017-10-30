package org.zywx.cooldev.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.document.Document;
import org.zywx.cooldev.entity.document.DocumentChapter;
import org.zywx.cooldev.entity.document.DocumentMarker;
import org.zywx.cooldev.service.DocumentMarkerService;
import org.zywx.cooldev.util.RequestParamValidate;

@Controller
@RequestMapping(value = "/marker")
public class DocumentMarkerController extends BaseController {

	@Autowired
	private DocumentMarkerService documentMarkerService;

	/**
	 * 
	 * @describe 添加文档批注 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月26日 下午5:59:51 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docCId
	 * @param taget
	 * @param content
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "", method = { RequestMethod.POST })
	public ModelAndView addDocumentMarker(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, DocumentMarker docM,
			@RequestParam("projectId") Long projectId) {
		try {
			docM.setUserId(loginUserId);
			HashMap<Object, Object> map = new HashMap<>();
			map = RequestParamValidate.ValidatePrama(request, response, new String[] { "docCId", "content", "target" });
			if (!map.isEmpty()) {
				return this.getFailedModel(map.get("message"));
			}
			docM = this.documentMarkerService.addMarker(loginUserId, docM);
			DocumentChapter docC = this.documentChapterService.findOne(docM.getDocCId());
			Document doc = this.documentService.getDoc(docC.getDocumentId());
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_MARKER, projectId,
					new Object[] { docC });
			User user = this.userService.findUserById(loginUserId);
			String docPath = doc.getName()+">"+this.documentService.getDocPath(docC);
			this.noticeService.addNotice(loginUserId, new Long[]{doc.getUserId()}, NOTICE_MODULE_TYPE.DOCUMENT_MARKER, new Object[]{user,docPath,docM});
			return this.getSuccessModel(docM);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 删除批注 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月28日 下午6:23:53 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docM
	 * @param projectId
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "/{docMId}", method = { RequestMethod.DELETE })
	public ModelAndView deleteDocumentMarker(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, @RequestParam("projectId") Long projectId,
			@PathVariable("docMId") Long docMId) {
		try {
			DocumentMarker docM = this.documentMarkerService.findOne(docMId);

			this.documentMarkerService.deleteDocumentMarker(docMId);

			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.DOCUMENT_MARKER_DELETE, projectId,
					new Object[] { docM });
			return this.getAffectModel();
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 获取批注 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月28日 下午8:08:42 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @param docM
	 * @param projectId
	 * @param docCId
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "/{docCId}", method = { RequestMethod.GET })
	public ModelAndView getMarker(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, DocumentMarker docM,
			@RequestParam("projectId") Long projectId, @PathVariable("docCId") Long docCId,
			@RequestParam("target") String target) {
		try {
			List<DocumentMarker> list = this.documentMarkerService.getMarkerByDocCIdAndUserId(docCId, loginUserId,
					target);
			return this.getSuccessModel(list);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 获取批注数量 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月28日 下午8:09:20 <br>
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@RequestMapping(value = "/count/{docCId}", method = { RequestMethod.GET })
	public ModelAndView getMarkerCount(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId, DocumentMarker docM,
			@RequestParam("projectId") Long projectId, @PathVariable("docCId") Long docCId,
			@RequestParam("target") String target) {
		try {
			int count = this.documentMarkerService.getCountByDocCId(docCId, loginUserId, target);
			return this.getSuccessModel(count);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}
}
