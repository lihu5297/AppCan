package org.zywx.cooldev.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.BUG_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.bug.Bug;
import org.zywx.cooldev.entity.bug.BugMark;
import org.zywx.cooldev.entity.bug.BugMember;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.service.EntityService;
import org.zywx.cooldev.util.ExportExcel;

/**
 * bug相关处理控制器
 * 
 * @author yongwen.wang
 * @date 2015-04-20
 * 
 */
@Controller
@RequestMapping(value = "/bug")
public class BugController extends BaseController {
	@Autowired
	private EntityService entityService;
	@Value("${downExcel.path}")
	private String downExcelPath;

	/**
	 * 新建bug
	 * 
	 * @param bug
	 * @param loginUserId
	 * */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> addBug(
			Bug bug,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId,
			@RequestParam(value = "copy", required = false) String copy) {
		try {
			if(bug.getTitle()!=null&&bug.getTitle().length()>1000){
				return this.getFailedMap("Bug标题不能超过1000个字符");
			}
			if(bug.getDetail()!=null&&bug.getDetail().length()>1000){
				return this.getFailedMap("Bug描述不能超过1000个字符");
			}
			if (copy != null && !copy.equals("true")) {
				return this.getFailedMap("参数传入错误");
			}
			log.info("add bug-->loginUserId:" + loginUserId
					+ ",assignedUserId:" + bug.getAssignedUserId()
					+ ",resourceIdList:" + bug.getResourceIdList()
					+ ",memberuserIdList:" + bug.getMemberUserIdList());
			// 必须有对应的流程
			if (0 == bug.getProcessId()) {
				return this.getFailedMap("processId: " + bug.getProcessId()
						+ " is not available!");
			}
			// bug默认状态为未解决
			if (bug.getStatus() == null) {
				bug.setStatus(Enums.BUG_STATUS.NOTFIX);
			}
			// bug默认优先权为普通
			if (bug.getPriority() == null) {
				bug.setPriority(Enums.BUG_PRIORITY.NORMAL);
			}
			// 最后修改人
			bug.setLastModifyUserId(loginUserId);
			this.bugService.addBug(bug, loginUserId);
			User user = this.userService.findUserById(loginUserId);
			Process p = this.processService.findOne(bug.getProcessId());
			if (copy == null) {
				// 创建bug添加动态
				this.dynamicService.addPrjDynamic(loginUserId,
						DYNAMIC_MODULE_TYPE.BUG_CREATE, p.getProjectId(),
						new Object[] { bug });
			} else {
				// 复制bug添加动态
				this.dynamicService.addPrjDynamic(loginUserId,
						DYNAMIC_MODULE_TYPE.BUG_COPY, p.getProjectId(),
						new Object[] { bug });
			}
			// 添加通知
			Long[] recievedIds = new Long[1];
			if (bug.getAssignedUserId() != loginUserId) {
				recievedIds[0] = bug.getAssignedUserId();
			}
			// 通知指派人
			this.noticeService.addNotice(loginUserId, recievedIds,
					NOTICE_MODULE_TYPE.BUG_ADD_MANAGER, new Object[] { user,
							bug });
			// 发送邮件给指派人
			this.baseService.sendEmail(loginUserId, recievedIds,
					NOTICE_MODULE_TYPE.BUG_ADD_MANAGER, new Object[] { user,
							bug });
			// 通知参与人
			this.noticeService.addNotice(loginUserId, bug.getMemberUserIdList()
					.toArray(new Long[] {}), NOTICE_MODULE_TYPE.BUG_ADD_MEMBER,
					new Object[] { user, bug });
			// 发送邮件给参与人
			this.baseService.sendEmail(loginUserId, bug.getMemberUserIdList()
					.toArray(new Long[] {}), NOTICE_MODULE_TYPE.BUG_ADD_MEMBER,
					new Object[] { user, bug });
			return this.getSuccessMap(bug);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 获取bug详情
	 * 
	 * @param bugId
	 * @param loginUserId
	 */
	@ResponseBody
	@RequestMapping(value = "/{bugId}", method = RequestMethod.GET)
	public Map<String, Object> getBugDetail(
			@PathVariable(value = "bugId") long bugId,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId,
			@RequestParam(value = "dynamicPageNo", required = false) String dynamicPageNo,
			@RequestParam(value = "dynamicPageSize", required = false) String dynamicPageSize) {
		try {
			log.info("get bug Detail-->bugId:" + bugId + ",loginUserId:"
					+ loginUserId);
			Direction direction = Direction.DESC;
			int dyPageNo = 0;
			int dyPageSize = 15;
			if (dynamicPageNo != null) {
				dyPageNo = Integer.parseInt(dynamicPageNo) - 1;
			}
			if (dynamicPageSize != null) {
				dyPageSize = Integer.parseInt(dynamicPageSize);
			}
			Pageable pageable = new PageRequest(dyPageNo, dyPageSize,
					direction, "id");
			Map<String, Object> map = bugService.getBugDetail(bugId,
					loginUserId, pageable);

			if (map != null && !map.containsKey("status")) {
				return this.getSuccessMap(map);

			} else {
				// return this.getFailedMap("not found Task with id=" + taskId);
				return this.getFailedMap(map.get("message"));

			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 编辑bug
	 * 
	 * @param bugId
	 * @param bug
	 * @param loginUserId
	 */
	@ResponseBody
	@RequestMapping(value = "/{bugId}", method = RequestMethod.PUT)
	public Map<String, Object> editBug(
			Bug bug,
			@PathVariable(value = "bugId") long bugId,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId) {
		if(bug.getTitle()!=null&&bug.getTitle().length()>1000){
			return this.getFailedMap("Bug标题不能超过1000个字符");
		}
		if(bug.getDetail()!=null&&bug.getDetail().length()>1000){
			return this.getFailedMap("Bug描述不能超过1000个字符");
		}
		List<BugMember> oldAllMembers = this.bugService.getBugMemberList(bugId,
				null, null);
		BugMember oldAssignedPersonObj = this.bugService
				.getBugAssignPerson(bugId);
		HashMap<String, Integer> ret = null;
		Bug bugOld = null;
		try {
			log.info("edit task-->taskId:" + bugId + ",bug:" + bug);
			bugOld = this.bugService.findOne(bugId);
			bug.setId(bugId);
			// 最后修改人
			bug.setLastModifyUserId(loginUserId);
			int affected = this.bugService.editBug(bug, bugOld, loginUserId);
			ret = new HashMap<>();
			ret.put("affected", affected);

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		// 添加动态,添加通知
		Bug newBug = this.bugService.findOne(bugId);
		Process p = this.processService.findOne(newBug.getProcessId());
		User user = this.userService.findUserById(loginUserId);
		Set<Long> members = new java.util.HashSet<Long>();
		Set<Long> assignedPerson = new java.util.HashSet<Long>();
		Set<Long> creator = new java.util.HashSet<Long>();
		Set<Long> resolver = new java.util.HashSet<Long>();
		// if,else if else 去重
		for (BugMember member : oldAllMembers) {
			if (member.getUserId() == loginUserId) {
				continue;
			}
			if (member.getType().equals(BUG_MEMBER_TYPE.CREATOR)) {
				creator.add(member.getUserId());
				//解决者不是创建者
				if(newBug.getResolveUserId()!=member.getUserId()){
					resolver.add(newBug.getResolveUserId());
				}
			} else if (oldAssignedPersonObj.getUserId() == member.getUserId()) {
				assignedPerson.add(oldAssignedPersonObj.getUserId());
			} else {
				members.add(member.getUserId());
			}
		}
		if (!bugOld.getStatus().equals(Enums.BUG_STATUS.CLOSED)
				&& newBug.getStatus().equals(Enums.BUG_STATUS.CLOSED)) {
			// 关闭添加动态
			this.dynamicService.addPrjDynamic(loginUserId,
					DYNAMIC_MODULE_TYPE.BUG_CLOSE, p.getProjectId(),
					new Object[] { newBug });
			// 关闭发送通知
			this.noticeService.addNotice(loginUserId,
					creator.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_CLOSE_TO_CREATOR, new Object[] {
							newBug, user });
			// 给解决者发通知
			this.noticeService.addNotice(loginUserId,
					resolver.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_CLOSE_TO_ASSIGNEDPERSON,
					new Object[] { newBug, user });
			this.noticeService.addNotice(loginUserId,
					members.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_CLOSE_TO_MEMBER, new Object[] {
							user, newBug });
			// 关闭添加邮件
			this.baseService.sendEmail(loginUserId,
					creator.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_CLOSE_TO_CREATOR, new Object[] {
							newBug,user});
			// 给解决者发邮件
			this.baseService.sendEmail(loginUserId,
					resolver.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_CLOSE_TO_ASSIGNEDPERSON,
					new Object[] { newBug, user });
			this.baseService.sendEmail(loginUserId,
					members.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_CLOSE_TO_MEMBER, new Object[] {
							user, newBug });

		} else if (!bugOld.getStatus().equals(Enums.BUG_STATUS.FIXED)
				&& newBug.getStatus().equals(Enums.BUG_STATUS.FIXED)) {
			Map<String, Object> solutionsMap = new HashMap<String, Object>() {
				{
					put("BYDESIGN", "设计如此");
					put("DUPLICATE", "重复");
					put("NOTREPRO", "无法复现");
					put("FIXED", "已修复");
					put("EXTERNAL", "外部原因");
					put("POSTPONED", "暂搁置");
					put("NOTFIX", "不值得修复");
				}
			};
			// 解决添加动态
			this.dynamicService.addPrjDynamic(
					loginUserId,
					DYNAMIC_MODULE_TYPE.BUG_SOLVE,
					p.getProjectId(),
					new Object[] { newBug,
							solutionsMap.get(newBug.getSolution().name()) });
			// 解决发送邮件
			this.noticeService.addNotice(
					loginUserId,
					creator.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_SOLVE_TO_CREATOR,
					new Object[] { user, newBug,
							solutionsMap.get(newBug.getSolution().name()) });
			//指派者
			this.noticeService.addNotice(
					loginUserId,
					assignedPerson.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_SOLVE_TO_ASSIGNEDPERSON,
					new Object[] { user, newBug,
							solutionsMap.get(newBug.getSolution().name()) });
			//成員
			this.noticeService.addNotice(
					loginUserId,
					members.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_SOLVE_TO_MEMBER,
					new Object[] { user, newBug,
							solutionsMap.get(newBug.getSolution().name()) });
			// 解决添加通知
			this.baseService.sendEmail(
					loginUserId,
					creator.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_SOLVE_TO_CREATOR,
					new Object[] { user, newBug,
							solutionsMap.get(newBug.getSolution().name()) });
			this.baseService.sendEmail(
					loginUserId,
					assignedPerson.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_SOLVE_TO_ASSIGNEDPERSON,
					new Object[] { user, newBug,
							solutionsMap.get(newBug.getSolution().name()) });
			this.baseService.sendEmail(
					loginUserId,
					members.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_SOLVE_TO_MEMBER,
					new Object[] { user, newBug,
							solutionsMap.get(newBug.getSolution().name()) });
		} else if (!bugOld.getStatus().equals(Enums.BUG_STATUS.NOTFIX)
				&& newBug.getStatus().equals(Enums.BUG_STATUS.NOTFIX)) {
			// 激活添加动态
			this.dynamicService.addPrjDynamic(loginUserId,
					DYNAMIC_MODULE_TYPE.BUG_ACTIVE, p.getProjectId(),
					new Object[] { newBug });
			// 激活发送邮件
			this.noticeService.addNotice(loginUserId,
					creator.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_ACTIVE_TO_CREATOR, new Object[] {
							user, newBug });
			// 给解决者发通知
			this.noticeService.addNotice(loginUserId,
					resolver.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_ACTIVE_TO_ASSIGNEDPERSON,
					new Object[] { user, newBug });
			this.noticeService.addNotice(loginUserId,
					members.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_ACTIVE_TO_MEMBER, new Object[] {
							user, newBug });
			// 添加通知
			this.baseService.sendEmail(loginUserId,
					creator.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_ACTIVE_TO_CREATOR, new Object[] {
							user, newBug });
			// 给解决者发邮件
			this.baseService.sendEmail(loginUserId,
					resolver.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_ACTIVE_TO_ASSIGNEDPERSON,
					new Object[] { user, newBug });
			this.baseService.sendEmail(loginUserId,
					members.toArray(new Long[] {}),
					NOTICE_MODULE_TYPE.BUG_ACTIVE_TO_MEMBER, new Object[] {
							user, newBug });
		} else if (bug.getResourceIdList() != null) {
			String resources = "";
			for (long resourceId : bug.getResourceIdList()) {
				Resource resource = this.resourcesService.findOne(resourceId);
				resources += resource.getName() + ',';
			}
			this.dynamicService.addPrjDynamic(loginUserId,
					DYNAMIC_MODULE_TYPE.BUG_ADD_RESOURCE, p.getProjectId(),
					new Object[] { newBug, resources });
		} else {
			// 更新了Title
			if(!bugOld.getTitle().equals(newBug.getTitle())){
				this.dynamicService.addPrjDynamic(loginUserId,
						DYNAMIC_MODULE_TYPE.BUG_UPDATE_TITLE, p.getProjectId(),
						new Object[] { newBug,bugOld.getTitle(),newBug.getTitle() });
			//更新了描述	
			}else if(!bugOld.getDetail().equals(newBug.getDetail())){
				this.dynamicService.addPrjDynamic(loginUserId,
						DYNAMIC_MODULE_TYPE.BUG_UPDATE_DETAIL, p.getProjectId(),
						new Object[] { newBug,bugOld.getDetail(),newBug.getDetail() });
			//更新了其他	
			}else{
				this.dynamicService.addPrjDynamic(loginUserId,
						DYNAMIC_MODULE_TYPE.BUG_UPDATE, p.getProjectId(),
						new Object[] { newBug });
			}
		}
		return this.getSuccessMap(ret);

	}

	/**
	 * 增加bug备注
	 * 
	 * @param bugId
	 * @param info
	 * @param loginUserId
	 */
	@ResponseBody
	@RequestMapping(value = "/mark", method = RequestMethod.POST)
	public Map<String, Object> addMark(
			BugMark bugMark,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId) {
		try {
			if(bugMark.getInfo()!=null&&bugMark.getInfo().length()>500){
				return this.getFailedMap("Bug备注不能超过500个字符");
			}
			log.info("add bug mark-->bugId:" + bugMark.getBugId()
					+ ",loginUserId:" + loginUserId);
			BugMark bugMarkNew = this.bugService.addMark(bugMark, loginUserId);
			Bug bug = this.bugService.findOne(bugMark.getBugId());
			Process p = this.processService.findOne(bug.getProcessId());
			this.dynamicService.addPrjDynamic(loginUserId,
					DYNAMIC_MODULE_TYPE.BUG_ADD_MARK, p.getProjectId(),
					new Object[] { bug });
			return this.getSuccessMap(bugMarkNew);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}

	/**
	 * 查询bug列表
	 * 
	 * @param bug
	 * @param loginUserId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */

	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Object> bugList(
			Bug bug,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "type", required = false) String type,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			if (pageNo == null) {
				pageNo = 1;
				pageSize = 15;
			}
			List<Object> bugList = this.bugService.getBugList(bug, pageNo,
					pageSize, loginUserId);
			if (type != null && type.equals("downloadExcel")) {
				log.info("this bugList is downloadExcel");
				String[] title = new String[] { "ID|id", "状态|status",
						"Bug标题|title","Bug描述|detail","优先级|priority", "所属应用|appName",
						"所属模块|moduleName", "创建者|creatorName",
						"指派给|assignedPersonName", "解决者|resolveName",
						"解决方案|solution", "最后操作日期|updatedAt" };
				String fileName = "bug列表";
				Map<String, Object> solutionsMap = new HashMap<String, Object>() {
					{
						put("BYDESIGN", "设计如此");
						put("DUPLICATE", "重复");
						put("NOTREPRO", "无法复现");
						put("FIXED", "已修复");
						put("EXTERNAL", "外部原因");
						put("POSTPONED", "暂搁置");
						put("NOTFIX", "不值得修复");
					}
				};
				Map<String, Object> statusMap = new HashMap<String, Object>() {
					{
						put("NOTFIX", "未解决");
						put("FIXED", "已解决");
						put("CLOSED", "已关闭");
					}
				};
				Map<String, Object> priorityMap = new HashMap<String, Object>() {
					{
						put("NORMAL", "正常");
						put("URGENT", "紧急");
						put("VERY_URGENT", "非常紧急");
					}
				};
				for (Map<String, Object> single : (ArrayList<Map<String, Object>>) ((Map<String, Object>) bugList
						.get(0)).get("data")) {
					single.put("status",
							statusMap.get(single.get("status").toString()));
					single.put("priority",
							priorityMap.get(single.get("priority").toString()));
					if (!single.get("solution").equals("无")) {
						single.put("solution", solutionsMap.get(single.get(
								"solution").toString()));
					}
				}
				// return this.getSuccessMap(bugList);
				HSSFWorkbook wb = ExportExcel.exportExcel(title, fileName,
						(ArrayList<Object>) ((Map<String, Object>) bugList
								.get(0)).get("data"));
				String putFileName = new Date().getTime() + ".xls";
				File file = new File(downExcelPath, putFileName);
				FileOutputStream fo = new FileOutputStream(file);
				wb.write(fo);
				fo.flush();
				fo.close();
				// response.setContentType("application/x-msdownload;charset=utf-8");
				// response.setHeader("Content-disposition",
				// "attachment; filename=" + fileName + ".xls");
				// ServletOutputStream out = response.getOutputStream();
				// wb.write(out);
				// out.flush();
				// out.close();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("path", downExcelPath + "/" + putFileName);
				return this.getSuccessMap(map);
			} else {
				return this.getSuccessMap(bugList);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 更新bug列表順序
	 * 
	 * @param projectId
	 * @param loginUserId
	 */
	@ResponseBody
	@RequestMapping(value = "/update/sort", method = RequestMethod.PUT)
	public Map<String, Object> editBugSort(
			@RequestHeader(value = "loginUserId") long loginUserId,
			@RequestParam(value = "projectId") long projectId,
			@RequestParam(required = true, value = "sortString") String sortString) {
		try {
			JSONObject obj = JSONObject.fromObject(sortString);
			int affected = this.bugService.updateBugStatusSort(loginUserId,
					obj, projectId);
			Map<String, Object> ret = new HashMap<>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
