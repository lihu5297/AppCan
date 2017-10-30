package org.zywx.cooldev.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.NOTICE_READ_TYPE;
import org.zywx.cooldev.service.NoticeService;

@Controller
@RequestMapping(value = "/notice")
public class NoticeController extends BaseController {

	@Autowired
	private NoticeService noticeService;

	/**
	 * 
	 * @describe 获取通知列表 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月19日 上午10:07:19 <br>
	 * @param request
	 * @param response
	 * @param loginUserId
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "", method = { RequestMethod.GET })
	public ModelAndView getNoticeList(HttpServletRequest request, HttpServletResponse response,
			@RequestHeader(value = "loginUserId") Long loginUserId) {
		try {
			HashMap<String, Object> map = new HashMap<>();
			String noread = request.getParameter("noread");
			int ipageNo = 1;
			int ipageSize = 15;
			if (request.getParameter("pageNo") != null) {
				ipageNo = Integer.parseInt(request.getParameter("pageNo"));
				ipageSize = Integer.parseInt(request.getParameter("pageSize"));
			}
			StringBuffer typeQ = new StringBuffer();
			String[] types = noread != null ? noread.split(",") : new String[] {};
			if (types.length > 0) {
				for (int a = 0; a < types.length; a++) {
					typeQ.append(types[a].equals(NOTICE_READ_TYPE.READ.name()) ? NOTICE_READ_TYPE.READ.ordinal() + ","
							: NOTICE_READ_TYPE.UNREAD.ordinal() + ",");
				}
				typeQ = new StringBuffer(typeQ.substring(0, typeQ.length() - 1));
			} else {
				typeQ.append(NOTICE_READ_TYPE.READ.ordinal() + "," + NOTICE_READ_TYPE.UNREAD.ordinal());
			}

			map = this.noticeService.getNoticeList(loginUserId, typeQ, ipageNo, ipageSize);
			return this.getSuccessModel(map);
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}

	}

	/**
	 * 
	 * @describe 设置已读通知 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月19日 上午10:07:33 <br>
	 * @param request
	 * @param response
	 * @param noId
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "", method = { RequestMethod.PUT })
	public ModelAndView readNotice(HttpServletRequest request, HttpServletResponse response, @RequestParam String noId,
			@RequestHeader(value = "loginUserId") Long loginUserId) {
		try {
			this.noticeService.updateReadNotice(loginUserId, noId);
			return this.getAffectModel();
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}

	/**
	 * 
	 * @describe 删除通知 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 上午9:54:56 <br>
	 * @param request
	 * @param response
	 * @param noId
	 * @param loginUserId
	 * @return <br>
	 * @returnType ModelAndView
	 *
	 */
	@ResponseBody
	@RequestMapping(value = "", method = { RequestMethod.DELETE })
	public ModelAndView deleteNotice(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "noId") List<Long> noId, @RequestHeader(value = "loginUserId") Long loginUserId) {
		try {
			this.noticeService.deleteNotice(loginUserId, noId);
			return this.getAffectModel();
		} catch (Exception e) {
			return this.getFailedModel(e.getMessage());
		}
	}
}
