package org.zywx.cooldev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.dao.bug.BugModuleDao;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.bug.BugModule;
import org.zywx.cooldev.service.EntityService;
import org.zywx.cooldev.util.ChineseToEnglish;

/**
 * bug相关处理控制器
 * 
 * @author yongwen.wang
 * @date 2015-04-20
 * 
 */
@Controller
@RequestMapping(value = "/bugModule")
public class BugModuleController extends BaseController {
	@Autowired
	private EntityService entityService;
	@Autowired
	private BugModuleDao bugModuleDao;

	/**
	 * 新建bug模块
	 * 
	 * @param bug
	 * @param loginUserId
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> addBugModule(
			BugModule bugModule,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId) {
		try {
			if(bugModule.getName()!=null&&bugModule.getName().length()>10){
				return this.getFailedMap("Bug模块名称不能超过10个字符");
			}
			boolean judgeValue=this.bugModuleService.judgeProjectIdAndName(bugModule.getProjectId(),bugModule.getName());
			if(judgeValue){
				return this.getFailedMap("同一个项目下,模块名称不能相同");
			}
			log.info("add bugModule-->loginUserId:" + loginUserId
					+ ",managerId:" + bugModule.getManagerId());
			if (0== bugModule.getProjectId()) {
				return this.getFailedMap("projectId: "
						+ bugModule.getProjectId() + " is not available!");
			}
			bugModule.setCreatorId(loginUserId);
			//增加拼音
			bugModule.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(bugModule.getName()==null?"":bugModule.getName()));
			bugModule.setPinYinName(ChineseToEnglish.getPingYin(bugModule.getName()==null?"":bugModule.getName()));
			bugModuleDao.save(bugModule);
			// 添加动态
			this.dynamicService.addPrjDynamic(loginUserId,
					DYNAMIC_MODULE_TYPE.BUG_MODULE_CREATE,
					bugModule.getProjectId(), new Object[] { bugModule });
//			User manager=this.userService.findUserById(bugModule.getManagerId());
//			if(manager!=null){
//				this.dynamicService.addPrjDynamic(loginUserId,
//						DYNAMIC_MODULE_TYPE.BUG_MODULE_ADD_MANAGER,
//						bugModule.getProjectId(), new Object[] {manager,bugModule});
//			}
			// 添加通知
//			User user = this.userService.findUserById(loginUserId);
//			Long[] recievedIds = new Long[1];
//			if (bugModule.getManagerId() != loginUserId) {
//				recievedIds[0] = bugModule.getManagerId();
//			}
//			this.noticeService.addNotice(loginUserId, recievedIds,
//					NOTICE_MODULE_TYPE.BUG_MODULE_ADD_MANAGER, new Object[] {
//							user, bugModule });
//			// 发送邮件
//			this.baseService.sendEmail(loginUserId, recievedIds,
//					NOTICE_MODULE_TYPE.BUG_MODULE_ADD_MANAGER, new Object[] {
//							user, bugModule });
			return this.getSuccessMap(bugModule);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		
	}

	/**
	 * 删除bug模块
	 * 
	 * @param taskId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{bugModuleId}", method = RequestMethod.DELETE)
	public Map<String, Object> removeBugModule(
			@PathVariable(value = "bugModuleId") long bugModuleId,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId) {

		try {
			this.bugModuleService.remove(bugModuleId);
			Map<String, Integer> affected = new HashMap<>();
			affected.put("affected", 1);

			// 添加动态
			BugModule bugModule = this.bugModuleDao.findOne(bugModuleId);
			this.dynamicService.addPrjDynamic(loginUserId,
					DYNAMIC_MODULE_TYPE.BUG_MODULE_DELETE,
					bugModule.getProjectId(), new Object[] { bugModule });
//			// 添加通知
//			User user = this.userService.findUserById(loginUserId);
//			Long[] managerId = new Long[1];
//			Long[] creatorId = new Long[1];
//			if (bugModule.getManagerId() != loginUserId) {
//				managerId[0] = bugModule.getManagerId();
//			}
//			if (bugModule.getCreatorId() != loginUserId) {
//				creatorId[0] = bugModule.getCreatorId();
//			}
//			this.noticeService.addNotice(loginUserId, creatorId,
//					NOTICE_MODULE_TYPE.BUG_MODULE_DELETE_TO_CREATOR,
//					new Object[] { user, bugModule });
//			// 发送邮件
//			this.baseService.sendEmail(loginUserId, creatorId,
//					NOTICE_MODULE_TYPE.BUG_MODULE_DELETE_TO_CREATOR,
//					new Object[] { user, bugModule });
//
//			this.noticeService.addNotice(loginUserId, managerId,
//					NOTICE_MODULE_TYPE.BUG_MODULE_DELETE_TO_MANAGER,
//					new Object[] { user, bugModule });
//			// 发送邮件
//			this.baseService.sendEmail(loginUserId, managerId,
//					NOTICE_MODULE_TYPE.BUG_MODULE_DELETE_TO_MANAGER,
//					new Object[] { user, bugModule });

			return this.getSuccessMap(affected);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 编辑bug模块
	 * 
	 * @param taskId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/{bugModuleId}", method = RequestMethod.PUT)
	public Map<String, Object> editBugModule(
			BugModule bugModule,
			@PathVariable(value = "bugModuleId") long bugModuleId,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId) {

		try {
			if(bugModule.getName()!=null&&bugModule.getName().length()>10){
				return this.getFailedMap("Bug模块名称不能超过10个字符");
			}
			BugModule bugModuleOld = this.bugModuleDao.findOne(bugModuleId);
			bugModule.setId(bugModuleId);
			int affected = this.bugModuleService.edit(bugModule, loginUserId);
			HashMap<String, Integer> ret = new HashMap<>();
			ret.put("affected", affected);
			// 添加动态
			BugModule bugModuleNew = this.bugModuleDao.findOne(bugModuleId);
			User bugModuleNewUser=this.userService.findUserById(bugModuleNew.getManagerId());
			//增加bug模块负责人
			if(bugModuleOld.getManagerId()==0&&bugModuleNew.getManagerId()!=0){
				this.dynamicService
				.addPrjDynamic(loginUserId,
						DYNAMIC_MODULE_TYPE.BUG_MODULE_ADD_MANAGER,
						bugModule.getProjectId(),
						new Object[] { bugModuleNewUser,bugModuleNew});
			}
			//变更模块负责人
			if(bugModuleOld.getManagerId()!=0&&bugModuleOld.getManagerId()!=bugModuleNew.getManagerId()){
				this.dynamicService
				.addPrjDynamic(loginUserId,
						DYNAMIC_MODULE_TYPE.BUG_MODULE_UPDATE_MANAGER,
						bugModule.getProjectId(),
						new Object[] { bugModuleNew,bugModuleNewUser});
			}
			return this.getSuccessMap(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}

	/**
	 * 查询bug模块列表
	 * 
	 * @param bugModule
	 * @param loginUserId
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET)
	public Map<String, Object> getBugModuleList(BugModule bugModule,
			@RequestHeader(value = "loginUserId", required = true) long loginUserId) {
		try {
			List<Object> bugList=this.bugModuleService.getBugModuleList(bugModule, loginUserId);
			return this.getSuccessMap(bugList);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	/**
	 * 转移bug模块下bug到另一个模块
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.PUT,value="/moveBugs")
	public Map<String, Object> moveBugs(
			@RequestHeader(value = "loginUserId", required = true) long loginUserId,
			@RequestParam(value="oldModuleId") long oldModuleId,@RequestParam(value="newModuleId") long newModuleId) {
		try {
			int affected = this.bugModuleService.moveBugs(oldModuleId,newModuleId);
			HashMap<String, Integer> ret = new HashMap<>();
			ret.put("affected", affected);
			return this.getSuccessMap(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}

	}
	/**
	 * 查询bug详情
	 */
	@ResponseBody
	@RequestMapping(value="/{bugModuleId}",method=RequestMethod.GET)
	public Map<String,Object> getBugModuleDetail(@RequestHeader(value="loginUserId") long loginUserId,
			@PathVariable(value = "bugModuleId") long bugModuleId){
		try{
			Map<String, Object> map = bugModuleService.getBugModuleDetail(bugModuleId,
					loginUserId);
			return this.getSuccessMap(map);
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * 增加拼音字段
	 */
	@ResponseBody
	@RequestMapping(value="addPinyin",method=RequestMethod.GET)
	public Map<String,Object> addPinYin(){
		try{
			Map<String,Object> map=this.bugModuleService.addPinYin();
			return map;
		}catch(Exception e){
			e.getStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
}
