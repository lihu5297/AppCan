package org.zywx.cooldev.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DATA_STATUS;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TRANS_NODE;
import org.zywx.cooldev.commons.Enums.TRANS_STATUS;
import org.zywx.cooldev.commons.Enums.TRANS_TYPE;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppPackage;
import org.zywx.cooldev.entity.app.AppType;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.datamodel.DataModel;
import org.zywx.cooldev.entity.filialeInfo.FilialeInfo;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.project.ProjectParent;
import org.zywx.cooldev.entity.tInterface.TInterFace;
import org.zywx.cooldev.entity.trans.ApproveModel;
import org.zywx.cooldev.entity.trans.Trans;
import org.zywx.cooldev.service.TransService;
import org.zywx.cooldev.system.Cache;

import com.alibaba.dubbo.common.json.ParseException;



/**
 * 申请审核交易处理
 * @author zhouxx
 * @date 20170808
 *
 */
@Controller
@RequestMapping(value = "/trans")
public class TransController extends BaseController {
	
	//针对企业版有固定的企业简称
	@Value("${enterpriseId}")
	private String enterpriseId;
		
	//针对企业版有固定的企业全称
	@Value("${enterpriseName}")
	private String enterpriseName;
	
	@Autowired
	private TransService transService;
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

	private String string;

	
	/**
	 * 模板申请
	 * @param datamobel
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/addTransByDataModel",method=RequestMethod.POST)
	public Map<String, Object> addTransByDataModel(
			DataModel md ,
			//@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		try {
			/*if(StringUtils.isBlank(String.valueOf(md.getProjectId()))){
				return getFailedMap("项目ID不可为空");
			}*/
			if(StringUtils.isBlank(String.valueOf(md.getProjectParentId()))){
				return getFailedMap("大项目ID不可为空");
			}
			//针对没有的数据手动写入 1有效，2无效
			md.setDmStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			//放入用户id
			md.setUserId(loginUserId);
			
			Trans trans = new Trans();
			//获取申请编号
			Map<String, Object> returnMap = new HashMap<String, Object>();
			//数模申请apptype参数，值始终为1
			returnMap = this.getAppNum("1", loginUserId,null,null);
			String applyNum = (String) returnMap.get("applyNum");
			
			//申请编号放入
			trans.setApplyNum(applyNum);
			//审批时间
			Timestamp ts = new Timestamp(new Date().getTime());
			//trans.setApprovalTime(ts);
			//状态 提交为未签收2
			trans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
			//发布人
			trans.setUserId(loginUserId);
			//申请时间
			trans.setSubTime(ts);
			//节点 0申请，1审批，2创建，3结束
			trans.setNode(String.valueOf(TRANS_NODE.APPLY.ordinal()));
			//数模是 1
			trans.setTranType(String.valueOf(TRANS_TYPE.DM.ordinal()));
			
			Trans transone = transService.addTransByDataModel(md, trans, loginUserId);
			if(transone != null ){
				returnMsg.put("id", transone.getTransactionsId()) ;
			}
			
			//写入动态和通知------------------通用start
			//添加动态
			//this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TRANS_DATAMODEL_ADD, md.getProjectId(), new Object[]{user,applyNum});
			//添加通知,申请不用
			 String enName = "user_digifax_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_DATAMODEL_ADD, new Object[] {user.getUserName()});
			//写入动态和通知------------------通用end
			 
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 模板编辑重新提交
	 * @param datamobel
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateTransByDataModel",method=RequestMethod.POST)
	public Map<String, Object> updateTransByDataModel(
			DataModel md ,
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		try {
			/*if(StringUtils.isBlank(String.valueOf(md.getProjectId()))){
				return getFailedMap("项目ID不可为空");
			}*/
			if(StringUtils.isBlank(String.valueOf(md.getProjectParentId()))){
				return getFailedMap("大项目ID不可为空");
			}
			//针对没有的数据手动写入 1有效，2无效
			md.setDmStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			//放入用户id
			md.setUserId(loginUserId);
			
			Trans trans = new Trans();
			//申请编号放入
			trans.setApplyNum(applyNum);
			
			
			Trans transone = transService.updateTransByDataModel(md, trans, loginUserId);
			if(transone != null ){
				returnMsg.put("id", transone.getTransactionsId()) ;
			}
			
			//写入动态和通知------------------通用start
			//添加通知,申请不用
			 String enName = "user_digifax_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_DATAMODEL_ADD, new Object[] {user.getUserName()});
			//写入动态和通知------------------通用end
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 申请编号,规则提供前用uuid
	 * apptype:1申请编号,2项目编号，3子项目编号，4应用编号
	 * pjProjectCode：大项目编号
	 * projectCode：子项目编号
	 * 
	 * zhouxx add 20180809
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/getTransCode",method=RequestMethod.POST)
	public Map<String, Object> getTransCode(
			@RequestParam(value="apptype",required=false) String apptype,
			@RequestParam(value="parentId",required=false) Long parentId,
			@RequestParam(value="projectCode",required=false) String projectCode,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		//String returnMsg = "申请成功   ";
		Map<String, Object> returnMap = new HashMap<String, Object>();

		Map<String, Object> requestMap = new HashMap<String, Object>();
		try {
			if("3".equals(apptype) && parentId==null){
				return this.getFailedMap("大项目ID不能为空");
			}
			if("4".equals(apptype) && StringUtils.isBlank(projectCode)){
				return this.getFailedMap("子项目编号不能为空");
			}
			requestMap.put("apptype", apptype);
			requestMap.put("parentId", parentId );
			requestMap.put("projectCode", projectCode);
			returnMap = transService.updateAndGetSeqAll(requestMap, loginUserId);
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 数模申请详情
	 * @param requestMap
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/dataModelDetail",method=RequestMethod.POST)
	public Map<String, Object> dataModelDetail(
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			Map<String,Object> requestMap = new HashMap<String,Object>();
			if(!StringUtils.isNotBlank(applyNum)){
				return this.getFailedMap("请求申请编号不能为空！");
			}
			requestMap.put("applyNum", applyNum);
			//根据申请编号跟userid获取详情
			Map<String,Object> dataModelMap = transService.getDataModelDetail(requestMap,loginUserId);
			if(dataModelMap != null && (long)dataModelMap.get("id") != 0){
				returnMap = dataModelMap;
			}else{
				return this.getFailedMap("未找到详情！申请编号："+applyNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 我的申请列表
	 * 查询条件：
	 * 	applyNum	申请编号
		transType	交易类型
		projectId	子项目
		projectParentId	大项目编号
		page	页数
		size	每页数量
	 * @param requestMap
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/applicationList",method=RequestMethod.POST)
	public Map<String, Object> applicationList(
			@RequestParam(value="applyNum",required=false) String applyNum,
			@RequestParam(value="isFinsh",required=true) boolean isFinsh,
			@RequestParam(value="tranType",required=false) String tranType,
			@RequestParam(value="projectName",required=false) String projectName,
			@RequestParam(value="pjProjectName",required=false) String pjProjectName,
			@RequestParam(value="pjProjectId",required=false) Integer parentId,
			@RequestParam(value="page",required=false) String page,
			@RequestParam(value="size",required=false) String size,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> requestMap = new HashMap<String,Object>();
		try {
			if(loginUserId == 0 ){
				return this.getFailedMap("登录用户不能为空！");
			}
			Map<String, Integer> authMap = userAuthService.findUserCreateAndProAuth(loginUserId);
			/*if(authMap == null || authMap.isEmpty()){
				return getFailedMap("无创建和审批权限");
			}*/
			if(page == null || "".equals(page) || "0".equals(page)){
				page = "1";
			}
			if(size == null || "".equals(size)){
				size = "10";
			}

			requestMap.put("size", size);
			requestMap.put("page", page);
			requestMap.put("applyNum", applyNum);
			requestMap.put("tranType", tranType);
			requestMap.put("projectName", projectName);
			requestMap.put("pjProjectName",pjProjectName);
			requestMap.put("parentId",parentId);
			requestMap.put("isFinsh",isFinsh);
			
			//根据申请编号跟userid获取详情
			Map<String,Object> dataModelMap = transService.applicationList(requestMap,loginUserId, authMap);
			returnMap = dataModelMap;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 待办列表
	 * 查询条件：
	 * 	applyNum	申请编号
		transType	交易类型
		projectName	子项目
		pjParentName	大项目
		status		状态 0申请， 1已签收，2未签收，3已完成，4未完成
		page	页数
		size	每页数量
		filialeId 网省id
	 * @param requestMap
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/todoList",method=RequestMethod.POST)
	public Map<String, Object> todoList(
			@RequestParam(value="applyNum",required=false) String applyNum,
			@RequestParam(value="tranType",required=false) String tranType,
			@RequestParam(value="projectName",required=false) String projectName,
			@RequestParam(value="pjProjectName",required=false) String pjProjectName,
			@RequestParam(value="pjProjectId",required=false) Integer parentId,
			@RequestParam(value="filialeId",required=false) String filialeId,
			@RequestParam(value="page",required=false) String page,
			@RequestParam(value="size",required=false) String size,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		 Map<String,Object> requestMap = new HashMap<String,Object>();
		try {
			if(loginUserId == 0 ){
				return this.getFailedMap("登录用户不能为空！");
			}
			Map<String, Integer> authMap = userAuthService.findUserCreateAndProAuth(loginUserId);
			if(authMap == null || authMap.isEmpty()){
				returnMap.put("total", 0);
				returnMap.put("list", "");
				returnMap.put("message", "无创建和审批权限");
				return getSuccessMap(returnMap);
			}
			//拼分页语句
			String limitSql = this.getPageSql(page, size);
			
			requestMap.put("limitSql", limitSql);
			requestMap.put("applyNum", applyNum);
			requestMap.put("tranType", tranType);
			requestMap.put("projectName", projectName);
			requestMap.put("pjProjectName",pjProjectName);
			requestMap.put("filialeId",filialeId);
			
			//根据申请编号跟userid获取详情
			Map<String,Object> dataModelMap = transService.todoList(requestMap,loginUserId,authMap);
			returnMap = dataModelMap;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 已办列表doFinshList
	 * 查询条件：
	 * 	applyNum	申请编号
		transType	交易类型
		projectName	子项目
		pjParentName	大项目
		status		状态 3已完成
		page	页数
		size	每页数量
		filialeId 网省id
		逻辑：用登录用户id和状态为3完成的查询审批人是自己的记录
	 * @param requestMap
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/doFinshList",method=RequestMethod.POST)
	public Map<String, Object> doFinshList(
			@RequestParam(value="applyNum",required=false) String applyNum,
			@RequestParam(value="tranType",required=false) String tranType,
			@RequestParam(value="projectName",required=false) String projectName,
			@RequestParam(value="pjProjectName",required=false) String pjProjectName,
			@RequestParam(value="pjProjectId",required=false) Integer parentId,
			@RequestParam(value="filialeId",required=false) String filialeId,
			@RequestParam(value="page",required=false) String page,
			@RequestParam(value="size",required=false) String size,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		 Map<String,Object> requestMap = new HashMap<String,Object>();
		try {
			if(loginUserId == 0 ){
				return this.getFailedMap("登录用户不能为空！");
			}
			Map<String, Integer> authMap = userAuthService.findUserCreateAndProAuth(loginUserId);
			if(authMap == null || authMap.isEmpty()){
				returnMap.put("total", 0);
				returnMap.put("list", "");
				returnMap.put("message", "无创建和审批权限");
				return getSuccessMap(returnMap);
			}
			//拼分页语句
			String limitSql = this.getPageSql(page, size);
			
			requestMap.put("limitSql", limitSql);
			requestMap.put("applyNum", applyNum);
			requestMap.put("tranType", tranType);
			requestMap.put("projectName", projectName);
			requestMap.put("pjProjectName",pjProjectName);
			requestMap.put("filialeId",filialeId);
			
			//根据申请编号跟userid获取详情
			Map<String,Object> dataModelMap = transService.doFinshList(requestMap,loginUserId, authMap);
			returnMap = dataModelMap;
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 审核通过接口approve
	 * 查询条件：
	 * 	applyNum	申请编号
		approveStatus	审批状态，0通过，1未通过
		approveMsg 	审批内容
		appApproveList 应用审批组，只有项目审核用
	 *	逻辑：
	 *	1、申请编号不能为空，审核状态不能为空
	 *	2、如果申请编号的申请类型是5，则appApproveList必须有值
	 *	
	 * @param loginUserId
	 * @return
	 * @throws ParseException 
	 */
	@ResponseBody
	@RequestMapping(value="/approve",method=RequestMethod.POST)
	public Map<String, Object> approve(
			@RequestParam(value="applyNum",required=false) String applyNum,
			@RequestParam(value="approveStatus",required=false) String approveStatus,
			@RequestParam(value="approveMsg",required=false) String approveMsg,
			@RequestParam(value="appApproveList",required=false) String appApproveList,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		Map<String,Object> requestMap = new HashMap<String,Object>();
		try {
			if(loginUserId == 0 ){
				return this.getFailedMap("登录用户不能为空！");
			}
			if(StringUtils.isBlank(applyNum) ){
				return this.getFailedMap("申请流水号不能为空！");
			}
			if(StringUtils.isBlank(approveStatus)&& StringUtils.isBlank(appApproveList)){
				return this.getFailedMap("审批状态不能为空！");
			}
			//审批状态为1未通过，则审批信息必须有内容
			if("1".equals(approveStatus)){
				if(StringUtils.isBlank(approveMsg) ){
				//	return this.getFailedMap("审批意见不能为空！");
				}
			}
			requestMap.put("applyNum", applyNum);
			requestMap.put("approveStatus", approveStatus);
			requestMap.put("approveMsg", approveMsg);
			requestMap.put("appApproveList",appApproveList);
			Map<String,Object> dataModelMap=null;
			//审核
			//appApproveList 走项目应用审核
			if(StringUtils.isNotBlank(appApproveList)){
				  dataModelMap=transService.approveProApp(loginUserId,requestMap);
			}else{
				
				  dataModelMap = transService.approveAll(requestMap,loginUserId);
			}
			returnMap = dataModelMap;
			
			Trans transNew =  (Trans) dataModelMap.get("transNew");
			//审核类型
			String tranType = transNew.getTranType();
			
			//写入动态和通知------------------通用start
			User user = this.userService.findUserById(loginUserId);
			//添加动态
			//this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TRANS_DATAMODEL_APPROVAL, projectId, new Object[]{user,applyNum});
			//添加通知,申请不用
			//Long[] recievedIds = new Long[1];
			//recievedIds[0]=transNew.getId();
			List<Long> memberUserIdList = new ArrayList<Long>();
			memberUserIdList.add(transNew.getUserId());
			//通知信息
			Object[] noteMsg = new Object[]{};
			//通知状态
			NOTICE_MODULE_TYPE nmt = null; 
			
			 //数模
			if(String.valueOf(TRANS_TYPE.DM.ordinal()).equals(tranType)){
				//<span>%s</span> 审批了 您提交的 数模申请，请知晓
					nmt = NOTICE_MODULE_TYPE.TRANS_DATAMODEL_APPROVALFINSH;
					noteMsg = new Object[]{user.getUserName()};
			}else if(String.valueOf(TRANS_TYPE.INTERFACE.ordinal()).equals(tranType)){
				//接口<span>%s</span> 审批了 您提交的 接口申请，请知晓
					nmt = NOTICE_MODULE_TYPE.TRANS_INTEGERFACE_APPROVALFINSH;
					noteMsg = new Object[]{user.getUserName()};
			}else if(String.valueOf(TRANS_TYPE.APP.ordinal()).equals(tranType)){
				//应用<span>%s</span> 审批了 您提交的 <span>%s</span>  应用申请，请知晓
					App app = appService.findOne(transNew.getTransactionsId());
					nmt = NOTICE_MODULE_TYPE.TRANS_APP_APPROVALFINSH;
					noteMsg = new Object[]{user.getUserName(),app.getName()};
			}else if(String.valueOf(TRANS_TYPE.PJ.ordinal()).equals(tranType)||String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()).equals(tranType)){
				//子项目<span>%s</span> 审批了 您提交的 <span>%s</span>  子项目申请，请知晓
					Project project = projectService.findOne(transNew.getTransactionsId());
					nmt = NOTICE_MODULE_TYPE.TRANS_PROJECT_APPROVALFINSH;
					noteMsg = new Object[]{user.getUserName(),project.getName()};
			}else if(String.valueOf(TRANS_TYPE.MOVEAPP.ordinal()).equals(tranType)){
				//移动应用发版申请<span>%s</span> 审批了 您提交的 <span>%s</span>  发版申请，请知晓
				AppVersion appVersion=appService.findAppVersionByAppId(transNew.getTransactionsId());
				App app = appService.findOne(appVersion.getAppId());
				nmt = NOTICE_MODULE_TYPE.TRANS_PUBLISH_APPROVALFINSH;
				noteMsg = new Object[]{user.getUserName(),app.getName()};
			}else if(String.valueOf(TRANS_TYPE.BACKEND.ordinal()).equals(tranType)){
				//后端应用发版申请<span>%s</span> 审批了 您提交的 <span>%s</span>  发版申请，请知晓
				AppVersion appVersion=appService.findAppVersionByAppId(transNew.getTransactionsId());
				App app = appService.findOne(appVersion.getAppId());
				nmt = NOTICE_MODULE_TYPE.TRANS_PUBLISH_APPROVALFINSH;
				noteMsg = new Object[]{user.getUserName(),app.getName()};
			}else{
				
			}
			 
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), nmt, noteMsg);
			//写入动态和通知------------------通用end
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 获取申请编号
	 * @param apptype
	 * @param loginUserId
	 * @param parentId 
	 * @return
	 */
	private Map<String, Object> getAppNum(String apptype,long loginUserId, Long parentId,String projectCode){
//		if("3".equals(apptype) && parentId==null){
//			return this.getFailedMap("大项目ID不能为空");
//		}
//		if("4".equals(apptype) && StringUtils.isBlank(projectCode)){
//			return this.getFailedMap("子项目编号不能为空");
//		}
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> requestMap = new HashMap<String, Object>();
		try {
			requestMap.put("apptype", apptype);
//			requestMap.put("parentId", parentId);
//			requestMap.put("projectCode", projectCode);
			returnMap = transService.updateAndGetSeqAll(requestMap, loginUserId);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap("获取申请编号失败！");
		}
		return returnMap;
	}
	
	/**
	 * 接口申请addTransByInterFace
	 * @param interFace
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/addTransByInterFace",method=RequestMethod.POST)
	public Map<String, Object> addTransByInterFace(
			TInterFace itf ,
			//@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		try {
			/*if(StringUtils.isBlank(String.valueOf(itf.getProjectId()))){
				return getFailedMap("项目ID不可为空");
			}*/
			if(StringUtils.isBlank(String.valueOf(itf.getProjectParentId()))){
				return getFailedMap("大项目ID不可为空");
			}
			//针对没有的数据手动写入 1有效，2无效
			itf.setInfStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			//放入用户id
			itf.setUserId(loginUserId);
			
			Trans trans = new Trans();
			//获取申请编号
			Map<String, Object> returnMap = new HashMap<String, Object>();
			//接口申请apptype参数，值始终为1
			returnMap = this.getAppNum("1", loginUserId,null,null);
			String applyNum = (String) returnMap.get("applyNum");
			//申请编号放入
			trans.setApplyNum(applyNum);
			//审批时间
			Timestamp ts = new Timestamp(new Date().getTime());
			//trans.setApprovalTime(ts);
			//状态 提交为未签收2
			trans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
			//发布人
			trans.setUserId(loginUserId);
			//申请时间
			trans.setSubTime(ts);
			//节点 0申请，1审批，2创建，3结束
			trans.setNode(String.valueOf(TRANS_NODE.APPLY.ordinal()));
			//数模是 1
			trans.setTranType(String.valueOf(TRANS_TYPE.INTERFACE.ordinal()));
			
			Trans transone = transService.addTransByTInterFace(itf, trans, loginUserId);
			if(transone != null ){
				returnMsg.put("id", transone.getTransactionsId()) ;
			}
			
			//写入动态和通知------------------通用start
			//添加通知,申请不用
			 String enName = "user_interface_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_INTEGERFACE_ADD, new Object[] {user.getUserName()});
			//写入动态和通知------------------通用end
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 接口申请详情
	 * @param applyNum 申请编号
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/interfaceDetail",method=RequestMethod.POST)
	public Map<String, Object> interfaceDetail(
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			Map<String,Object> requestMap = new HashMap<String,Object>();
			if(!StringUtils.isNotBlank(applyNum)){
				return this.getFailedMap("请求申请编号不能为空！");
			}
			requestMap.put("applyNum", applyNum);
			//根据申请编号跟userid获取详情
			Map<String,Object> interMap = transService.getInterFaceDetail(requestMap,loginUserId);
			if(interMap != null && (long)interMap.get("id") != 0){
				returnMap = interMap;
			}else{
				return this.getFailedMap("未找到详情！申请编号："+applyNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 接口编辑重新提交
	 * @param itf
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateTransByInterFace",method=RequestMethod.POST)
	public Map<String, Object> updateTransByInterFace(
			TInterFace itf ,
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		try {
			/*if(StringUtils.isBlank(String.valueOf(itf.getProjectId()))){
				return getFailedMap("项目ID不可为空");
			}*/
			if(StringUtils.isBlank(String.valueOf(itf.getProjectParentId()))){
				return getFailedMap("大项目ID不可为空");
			}
			//针对没有的数据手动写入 1有效，2无效
			itf.setInfStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			//放入用户id
			itf.setUserId(loginUserId);
			
			Trans trans = new Trans();
			//申请编号放入
			trans.setApplyNum(applyNum);
			
			
			Trans transone = transService.updateTransByInterFace(itf, trans, loginUserId);
			if(transone != null ){
				returnMsg.put("id", transone.getTransactionsId()) ;
			}
			
			//写入动态和通知------------------通用start
			//添加通知,申请不用
			 String enName = "user_interface_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_INTEGERFACE_ADD, new Object[] {user.getUserName()});
			//写入动态和通知------------------通用end
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 应用申请addTransByAPP
	 * @param interFace
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/addTransByApp",method=RequestMethod.POST)
	public Map<String, Object> addTransByAPP(
			App app,
			//@RequestParam(value="applyNum") String applyNum,
			//@RequestParam(value="projectCode",required=false) String projectCode,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(app.getName())){
				return getFailedMap("应用名称不可为空");
			}
			if(StringUtils.isBlank(String.valueOf(app.getProjectId()))){
				return getFailedMap("项目ID不可为空");
			}
			
			//针对没有的数据手动写入 1有效，2无效
			app.setAppStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			//放入用户id
			app.setUserId(loginUserId);
			if(app.getName()!=null&&app.getName().length()>100){
				return this.getFailedMap("应用名称不能超过100个字符");
			}
			if(app.getDetail()!=null&&app.getDetail().length()>1000){
				return this.getFailedMap("应用描述不能超过1000个字符");
			}
			//检查
			Project project = projectService.findOne(app.getProjectId());
			if(project == null){
				return this.getFailedMap("应用所属子项目不存在");
			}
			log.info("userid-->"+loginUserId+",create app["+app.getName()+","+app.getDetail()+" ]at:"+System.currentTimeMillis());
			app.setProjectParentId(project.getParentId());
			
			App appnew = this.appService.addApp(app, loginUserId);
			
			Trans trans = new Trans();
			//获取申请编号
			Map<String, Object> returnMap = new HashMap<String, Object>();
			//应用编号4
			returnMap = this.getAppNum("1", loginUserId, null, project.getProjectCode());
			String applyNum = (String) returnMap.get("applyNum");
			//申请编号放入
			trans.setApplyNum(applyNum);
			
			trans.setTransactionsId(appnew.getId());
			
			transService.checkPJ(trans.getApplyNum(),loginUserId,app.getProjectId(),project.getParentId());
			Trans transnew = transService.addTransByApp(appnew, trans, loginUserId);
			if(transnew != null ){
				returnMsg.put("id", transnew.getTransactionsId()) ;
			}
			
			//写入动态和通知------------------通用start
			//添加通知,申请不用
			 String enName = "user_project_app_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_APP_ADD, new Object[] {user.getUserName()});
			//写入动态和通知------------------通用end
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 应用申请详情
	 * @param applyNum 申请编号
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/appDetail",method=RequestMethod.POST)
	public Map<String, Object> appDetail(
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			Map<String,Object> requestMap = new HashMap<String,Object>();
			if(!StringUtils.isNotBlank(applyNum)){
				return this.getFailedMap("请求申请编号不能为空！");
			}
			requestMap.put("applyNum", applyNum);
			//根据申请编号跟userid获取详情
			Map<String,Object> appMap = transService.getAppDetail(requestMap,loginUserId);
			if(appMap != null && (long)appMap.get("id") != 0){
				returnMap = appMap;
			}else{
				return this.getFailedMap("未找到详情！申请编号："+applyNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 应用重新提交
	 * @param app
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateTransByApp",method=RequestMethod.POST)
	public Map<String, Object> updateTransByApp(
			App app ,
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		try {
			/*if(app.getId() == null || app.getId() < 1){
				return this.getFailedMap("应用ID不能为空");
			}*/
			if(StringUtils.isBlank(app.getName())){
				return getFailedMap("应用名称不可为空");
			}
			if(app.getAppType()<0){
				return getFailedMap("应用类型不可为空");
			}
			if(StringUtils.isBlank(String.valueOf(app.getProjectId()))){
				return getFailedMap("项目ID不可为空");
			}
			//针对没有的数据手动写入 1有效，2无效
			app.setAppStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			//放入用户id
			app.setUserId(loginUserId);
			if(app.getName()!=null&&app.getName().length()>100){
				return this.getFailedMap("应用名称不能超过100个字符");
			}
			if(app.getDetail()!=null&&app.getDetail().length()>1000){
				return this.getFailedMap("应用描述不能超过1000个字符");
			}
			Trans findByApplyNum = transService.findByApplyNum(applyNum);
			App orig_app = appService.findOne(findByApplyNum.getTransactionsId());
			if(!orig_app.getAppCode().equals(app.getAppCode())){
				return this.getFailedMap("应用编号错误");
			}
			Trans trans = new Trans();
			//申请编号放入
			trans.setApplyNum(applyNum);
			 app.setId(findByApplyNum.getTransactionsId());
			Trans transone = transService.updateTransByApp(app, trans, loginUserId);
			if(transone != null ){
				returnMsg.put("id", transone.getTransactionsId()) ;
			}
			
			//写入动态和通知------------------通用start
			//添加通知,申请不用
			 String enName = "user_project_app_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_APP_ADD, new Object[] {user.getUserName()});
			//写入动态和通知------------------通用end
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 子项目申请
	 * @param project		项目信息
	 * @param applyNum		申请编号
	 * @param loginUserId	申请人
	 * @return
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value="/addTransByProjectChild",method=RequestMethod.POST)
	public Map<String, Object> addTransByChildProject(
			Project project ,@RequestParam(value="appIds[]",required=false)long[] appIds,
			//@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) throws Exception {
		if(StringUtils.isBlank(project.getName())){
			return getFailedMap("名称不可为空");
		}
		if(StringUtils.isBlank(String.valueOf(project.getCategoryId()))){
			return getFailedMap("类型不可为空");
		}
		if(StringUtils.isBlank(String.valueOf(project.getParentId()))){
			return getFailedMap("大项目ID不可为空");
		}
		if(StringUtils.isBlank(String.valueOf(project.getTeamId()))){
			return getFailedMap("团队ID不可为空");
		}
		
		if(project.getName()!=null&&project.getName().length()>100){
			return this.getFailedMap("子项目名称不能超过100个字符");
		}
		if(project.getDetail()!=null&&project.getDetail().length()>1000){
			return this.getFailedMap("子项目描述不能超过1000个字符");
		}
		if(project.getParentId() == null || project.getParentId() < 1){
			return this.getFailedMap("子项目所属大项目不能为空");
		}
		if(project.getTeamId() < 1){
			return this.getFailedMap("子项目所属团队不能为空");
		}
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		project.setBizCompanyId(enterpriseId);
		project.setBizCompanyName(enterpriseName);
		project.setBizLicense(PROJECT_BIZ_LICENSE.AUTHORIZED);
		project.setType(PROJECT_TYPE.TEAM);//企业版不允许创建个人项目
		project.setStatus(Enums.PROJECT_STATUS.ONGOING);
		project.setCreatorId(loginUserId);
		//新申请的项目还未获批创建，为了不再项目列表中显示，先把项目置为删除状态
		project.setDel(DELTYPE.DELETED);
		//project.setType(PROJECT_TYPE.TEAM);
		
		log.info("userid-->"+loginUserId+",create project["+project.getName()+","+project.getDetail()+" ]at:"+System.currentTimeMillis());
		Project newPro = projectService.addProject(project);
		
		// 把项目创建者添加到项目成员表中并赋权
		ProjectMember member = new ProjectMember();
		member.setUserId(loginUserId);
		member.setProjectId(newPro.getId());
		member.setType(PROJECT_MEMBER_TYPE.CREATOR);
		User loginUser = this.userService.findUserById(loginUserId);
		this.projectService.saveProjectMember(member, loginUser);
		log.info(member);
		ProjectAuth auth = new ProjectAuth();
		auth.setMemberId(member.getId());
		auth.setRoleId(Cache.getRole(
				ENTITY_TYPE.PROJECT + "_" + ROLE_TYPE.CREATOR).getId());
		this.projectService.saveProjectAuth(auth);
		log.info("appIds:"+appIds);
		Trans trans = new Trans();
		if (appIds == null || appIds.length == 0) {// 判断类型
			trans.setTranType(String.valueOf(TRANS_TYPE.PJ.ordinal()));
		} else { // 项目应用一起提交 更改应用的项目id
			for (long appId : appIds) {
				// 修改app项目id参数
				appService.updateProAppByApp(appId, newPro);
			}

			trans.setTranType(String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()));
			log.info("appIds 不为空:"+trans.toStr());
		}
		
		//获取申请编号
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap=this.getAppNum("1", loginUserId,project.getParentId(),null);
		String applyNum = (String) returnMap.get("applyNum");
		// 申请编号放入
		trans.setApplyNum(applyNum);
		// 申请对象的主键
		trans.setTransactionsId(newPro.getId());
		// 检查
		String checkTrans = transService.checkTransPrject(trans.getApplyNum());
		if (!checkTrans.equals("ok")) {
			return this.getFailedMap(checkTrans);
		}
		Trans transnew = transService.addTransByProject(newPro, trans,
				loginUserId);

		if (transnew != null) {
			returnMsg.put("id", transnew.getTransactionsId());
		}

		//添加通知,申请不用
		 String enName = "user_project_app_approval";
		 User user = this.userService.findUserById(loginUserId);
		 Permission permission = PermissionService.getPermissionId(enName);
		 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
		this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_PROJECT_ADD, new Object[] {user.getUserName()});
		//写入动态和通知------------------通用end

		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 子项目申请详情
	 * @param applyNum 申请编号
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/projectChildDetail",method=RequestMethod.POST)
	public Map<String, Object> projectChildDetail(
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		try {
			Map<String,Object> requestMap = new HashMap<String,Object>();
			if(!StringUtils.isNotBlank(applyNum)){
				return this.getFailedMap("请求申请编号不能为空！");
			}
			requestMap.put("applyNum", applyNum);
			//根据申请编号跟userid获取详情
			Map<String,Object> proMap = transService.getProjectChildDetail(requestMap,loginUserId);
			if(proMap != null && (long)proMap.get("id") != 0){
				returnMap = proMap;
			}else{
				return this.getFailedMap("未找到详情！申请编号："+applyNum);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMap);
	}
	
	/**
	 * 子项目重新提交
	 * @param app
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateTransByProjectChild",method=RequestMethod.POST)
	public Map<String, Object> updateTransByProjectChild(
			Project project ,
			@RequestParam(value="appIds[]",required=false)long[] appIds,
			@RequestParam(value="appList",required=false)String appList,
			@RequestParam(value="applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		//如果交易的状态不为4，报错
		try {
			if(StringUtils.isBlank(project.getName())){
				return getFailedMap("名称不可为空");
			}
			if(StringUtils.isBlank(String.valueOf(project.getCategoryId()))){
				return getFailedMap("类型不可为空");
			}
			if(StringUtils.isBlank(String.valueOf(project.getParentId()))){
				return getFailedMap("大项目ID不可为空");
			}
			if(StringUtils.isBlank(String.valueOf(project.getTeamId()))){
				return getFailedMap("团队ID不可为空");
			}
			 
			if(project.getName()!=null&&project.getName().length()>100){
				return this.getFailedMap("子项目名称不能超过100个字符");
			}
			if(project.getDetail()!=null&&project.getDetail().length()>1000){
				return this.getFailedMap("子项目描述不能超过1000个字符");
			}
			if(project.getParentId() == null || project.getParentId() < 1){
				return this.getFailedMap("子项目所属大项目不能为空");
			}
			if(project.getTeamId() < 1){
				return this.getFailedMap("子项目所属团队不能为空");
			}
			
			Trans trans = new Trans();
			//申请编号放入
			trans.setApplyNum(applyNum);
			List<Trans> translist = transService.findByUserIdAndApplyNum(loginUserId,applyNum);
			if(translist == null){
				return this.getFailedMap("交易不存在");
			}
			
			if(!"4".equals(translist.get(0).getStatus())){
				return this.getFailedMap("交易号："+applyNum+"，交易状态不是否决状态不可重新申请！");
			}
			//如果是子项目应用类型的
			if("5".equals(translist.get(0).getTranType())){
				log.info(" updateTransByProjectChild -> appList"+appList);
				/*if(appIds==null||appIds.length<0){
					return this.getFailedMap("子应用不存在");
				}
				for (long appId : appIds) {
					App app = appService.findOne(appId);
					app.setProjectId(translist.get(0).getTransactionsId());
					appService.updateApp(app);
				}*/
				if(appList==null||appList.isEmpty()){
					return this.getFailedMap("子应用不存在");
				}
				JSONArray jsonArray = JSONArray.fromObject(appList);
				ArrayList<App> list =(ArrayList<App>) jsonArray.toList(jsonArray, App.class);
				
				for (App acode : list) {
					App app = appService.findOne(acode.getId());
					app.setProjectId(translist.get(0).getTransactionsId());
					app.setAppCode(acode.getAppCode());
					appService.updateApp(app);
				}
			}
			 
			Project pro = this.projectService.findOne(translist.get(0).getTransactionsId());
			//如果是项目应用类型
			
			if(pro == null){
				return this.getFailedMap("子项目不存在");
			}
			/*if(!"5".equals(translist.get(0).getTranType())&&!pro.getProjectCode().equals(project.getProjectCode())){
				return this.getFailedMap("子项目编号不能修改");
			}*/
			//对项目信息做修改
			pro.setProjectCode(project.getProjectCode());
			pro.setName(project.getName());
			pro.setCategoryId(project.getCategoryId());
			pro.setTeamId(project.getTeamId());
			pro.setDetail(project.getDetail());
			pro.setParentId(project.getParentId());
			projectService.saveProject(pro);
			
			Trans transone = transService.updateTransByChildProject(pro, translist.get(0), loginUserId);
			if(transone != null ){
				returnMsg.put("id", transone.getTransactionsId()) ;
			}
			
			//写入动态和通知------------------通用start
			//添加通知,申请不用
			 String enName = "user_project_app_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_PROJECT_ADD, new Object[] {user.getUserName()});
			//写入动态和通知------------------通用end
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	/**
	 * 签收
	 * @param applyNum
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/approveSign",method=RequestMethod.POST)
	public Map<String, Object> approveSign(
			@RequestParam("applyNum") String applyNum,
			@RequestHeader("loginUserId") long loginUserId
			) {
		try {
			Map<String,Object> requestMap = new HashMap<String,Object>();
			if(!StringUtils.isNotBlank(applyNum)){
				return this.getFailedMap("请求申请编号不能为空！");
			}
			requestMap.put("applyNum", applyNum);
			//根据申请编号跟userid获取详情
			Trans trans = transService.findByApplyNum(applyNum);
			if(trans==null){
				return this.getFailedMap("交易不存在");
			}
			 
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			//环节是审批  审批签收
			 if(trans.getNode().equals(String.valueOf(Enums.TRANS_NODE.APPROVAL.ordinal()))){
				 trans.setManageId(loginUserId);
				 trans.setSignTime(ts);
			 }else if(trans.getNode().equals(String.valueOf(Enums.TRANS_NODE.CREATE.ordinal()))){
				//环节是创建  创建 签收
				 trans.setMarkId(loginUserId);
				 trans.setSignMarkTime(ts);
			 }else{
				 return this.getFailedMap("不可签收");
			 }
			 trans.setUpdatedAt(ts);
			trans.setStatus(String.valueOf(Enums.TRANS_STATUS.SIGNED.ordinal()));
			transService.approveSign(trans);
			 
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(applyNum);
	}
	

	/**
	 * 创建工程或项目
	 * @param applyNum
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/createObject",method=RequestMethod.POST)
	public Map<String, Object> createObject(
			@RequestParam("applyNum") String applyNum,
			@RequestHeader("loginUserId") long loginUserId
			) {
		if(!StringUtils.isNotBlank(applyNum)){
			return this.getFailedMap("请求申请编号不能为空！");
		}
		String retMsg = transService.createProjectOrApp(applyNum,loginUserId);
		//写入动态和通知------------------通用start
		//通知信息
		Object[] noteMsg = new Object[]{};
		//通知状态
		NOTICE_MODULE_TYPE nmt = null; 
		Trans trans = transService.findByApplyNum(applyNum);
		String tranType = trans.getTranType();
		List<Long> memberUserIdList=new ArrayList<Long>();
		memberUserIdList.add(trans.getUserId());
		User user = userService.findUserById(loginUserId);
		if (String.valueOf(TRANS_TYPE.PJ.ordinal()).equals(tranType)
				|| String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()).equals(tranType)) {
			// <span>%s</span> 创建了 您申请的 <span>%s</span> 子项目，请知晓
			Project project = projectService.findOne(trans.getTransactionsId());
			nmt = NOTICE_MODULE_TYPE.TRANS_PROJECT_GREATE;
			noteMsg = new Object[] { user.getUserName(), project.getName() };
			//<span>%s</span> 申请了团队项目 <span>%s</span>
			this.dynamicService.addPrjDynamic(project.getCreatorId(), DYNAMIC_MODULE_TYPE.TRANS_PROJECT_ADD, project.getId(), new Object[]{project.getName()});
		}else if(String.valueOf(TRANS_TYPE.APP.ordinal()).equals(tranType)){
			App app = appService.findOne(trans.getTransactionsId());
			AppType appType = appService.findAppTypeById(app.getAppType());
			nmt = NOTICE_MODULE_TYPE.TRANS_PROJECT_GREATE;
			noteMsg = new Object[] { user.getUserName(), app.getName() };
			//<span>%s</span> 申请了 <span>%s</span> 应用 <span>%s</span>
			this.dynamicService.addPrjDynamic(app.getUserId(), DYNAMIC_MODULE_TYPE.TRANS_APP_ADD, app.getProjectId(), new Object[]{appType.getTypeName(),app.getName()});
		}
		this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), nmt, noteMsg);
		//写入动态和通知------------------通用end
		if(retMsg.equals("ok")){
			return this.getSuccessMap("创建成功");
		}else{
			return this.getFailedMap(retMsg);
		}
	}
	public static void main(String[] args) {
		System.out.println(false||true);
	}
	
	/**
	 * 项目应用申请---应用添加 addProAppByApp
	 * @param interFace
	 * @param request
	 * @param loginUserId
	 * @return 提交应用时调用
	 */
	@ResponseBody
	@RequestMapping(value="/addProAppByApp",method=RequestMethod.POST)
	public Map<String, Object> addProAppByApp(
			App app,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		try {
			if(StringUtils.isBlank(app.getName())){
				return getFailedMap("应用名称不可为空");
			}
			if(StringUtils.isBlank(app.getAppCode())){
				return getFailedMap("应用编号不可为空");
			}
			if(app.getAppType()<0){
				return getFailedMap("应用类型不可为空");
			}
			
			//针对没有的数据手动写入 1有效，2无效
			app.setAppStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			//放入用户id
			app.setUserId(loginUserId);
			app.setProApp(true); // 该应用为项目应用申请
			if(app.getName()!=null&&app.getName().length()>100){
				return this.getFailedMap("应用名称不能超过100个字符");
			}
			if(app.getDetail()!=null&&app.getDetail().length()>1000){
				return this.getFailedMap("应用描述不能超过1000个字符");
			}
			//检查
		 
			log.info("userid-->"+loginUserId+",create app["+app.getName()+","+app.getDetail()+" ]at:"+System.currentTimeMillis());
			//app.setProjectParentId(project.getParentId());
			//添加应用
			App appnew = this.appService.addProAppByApp(app, loginUserId);
			
			Trans trans = new Trans();
			//申请编号放入
			//trans.setApplyNum(applyNum);
			
			trans.setTransactionsId(appnew.getId());
			
			//transService.checkPJ(trans.getApplyNum(),loginUserId,app.getProjectId(),project.getParentId());
			//Trans transnew = transService.addTransByApp(appnew, trans, loginUserId);
			/*if(transnew != null ){
				returnMsg.put("id", transnew.getTransactionsId()) ;
			}*/
			
			//写入动态和通知------------------通用start
			//User user = this.userService.findUserById(loginUserId);
			//添加动态
			//this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TRANS_APP_ADD, app.getProjectId(), new Object[]{user,applyNum});
			//添加通知,申请不用
			//Long[] recievedIds = new Long[1];
			//recievedIds[0]=transid;
			//this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_TO_MEMBER, new Object[]{user,task,sdf.format(task.getDeadline())});
			//写入动态和通知------------------通用end
			returnMsg.put("appId", app.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	/**
	 * 删除项目应用中的 应用(物理删除)
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/delProAppByApp",method=RequestMethod.GET)
	public Map<String, Object> delProAppByApp(
			@RequestParam("appIds")String appIds,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
			try {
				String[] split = appIds.split(",");
				appService.delProAppByApp(split);
			} catch (Exception e) {
				e.printStackTrace();
				return getFailedMap(e.getMessage());
			}
		return getSuccessMap(appIds);
	}
	/**
	 * 编辑项目应用中的 应用
	 * @param appId
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/editProAppByApp",method=RequestMethod.GET)
	public Map<String, Object> editProAppByApp(
			App app,long appId,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		try {
			if(appId<0){
				return getFailedMap("应用ID不可为空");
			}
			if(StringUtils.isBlank(app.getName())){
				return getFailedMap("应用名称不可为空");
			}
			if(StringUtils.isBlank(app.getAppCode())){
				return getFailedMap("应用编号不可为空");
			}
			if(app.getAppType()<0){
				return getFailedMap("应用类型不可为空");
			}
			
			if(app.getName()!=null&&app.getName().length()>100){
				return this.getFailedMap("应用名称不能超过100个字符");
			}
			if(app.getDetail()!=null&&app.getDetail().length()>1000){
				return this.getFailedMap("应用描述不能超过1000个字符");
			}
			
			appService.editProAppByApp(app,appId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return getFailedMap(e.getMessage());
		}
		return getSuccessMap(appId);
	}
	/**
	 * 废弃交易
	 * @param applyNum
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/cutOut",method=RequestMethod.GET)
	public Map<String, Object> cutOut(
			@RequestParam("applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
			try {
				if(!StringUtils.isNotBlank(applyNum)){
					return this.getFailedMap("请求申请编号不能为空！");
				}
				Trans trans = transService.findByApplyNum(applyNum);
				if(!trans.getNode().equals(String.valueOf(Enums.TRANS_NODE.APPLY.ordinal()))){
					return this.getFailedMap("请求申请编号不能废弃！");
				}
				if(trans.getUserId()!=loginUserId){
					return this.getFailedMap("该用户不能废弃请求！");
				}
				
				transService.updateCutOut(trans,loginUserId);
			} catch (Exception e) {
				e.printStackTrace();
				return getFailedMap(e.getMessage());
			}
		return getSuccessMap(applyNum);
	}
	
	/**
	 * 版本发版申请
	 * @param applyNum   申请编号
	 * @param appId		应用 appcanId
	 * @param appPackageType app包类型
	 * @param id		申请发版的版本主键id
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/addTransByPublish",method=RequestMethod.GET)
	public Map<String, Object> addTransByPublish(
			//@RequestParam("applyNum") String applyNum,
			@RequestParam("appPackageType") String appPackageType, 
			@RequestParam("id") Long id,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		/*if(StringUtils.isBlank(applyNum)){
			return this.getFailedMap("申请编号不能为空");
		}*/
		if(id == null || id < 1){
			return this.getFailedMap("版本主键ID不能为空");
		}
		AppVersion version = appService.getAppVersion(id);
		if(version == null){
			return this.getFailedMap("版本信息不存在");
		}
		App app = appService.findOne(version.getAppId());
		if(app == null){
			return this.getFailedMap("版本所属应用信息不存在");
		}
		String transType = "6";
		int appType = TRANS_TYPE.MOVEAPP.ordinal();
		if(app.getAppType() == 6 || app.getAppType() == 7){
			transType = "7";
			appType = TRANS_TYPE.BACKEND.ordinal();
		}
		
		List<Trans> transList = transService.findByTransactionsIdAndTranType(version.getId(), transType);
		if(transList != null && !transList.isEmpty()){
			return this.getFailedMap("已提交发版申请");
		}
		try {
			Trans trans = new Trans();
			//获取申请编号
			Map<String, Object> returnMap = new HashMap<String, Object>();
			//接口申请apptype参数，值始终为1
			returnMap = this.getAppNum("1", loginUserId,null,null);
			String applyNum = (String) returnMap.get("applyNum");
			//申请编号放入
			trans.setApplyNum(applyNum);
			
			Trans transnew = transService.addTransByAppVersion(version.getId(), trans, appType , loginUserId);
			if(transnew != null ){
				returnMsg.put("id", transnew.getTransactionsId()) ;
			}
			version.setHaveApplyPublish(true);
			version.setApplyPackageProperties(appPackageType);
			version.setTransId(transnew.getId());
			appService.saveAppversion(version);
			//添加通知
			 String enName = "user_publish_approval";
			 User user = this.userService.findUserById(loginUserId);
			 Permission permission = PermissionService.getPermissionId(enName);
			 List<Long> memberUserIdList=userAuthService.findUserIdByPermissionId(permission.getId());
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TRANS_PUBLISH_ADD, new Object[] {user.getUserName()});
			 
			
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TRANS_PUBLISH_ADD, app.getProjectId(), new Object[] {app.getName()});
			//写入动态和通知------------------通用end
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	/**
	 * 发版重新申请
	 * @param applyNum
	 * @param id
	 * @param appPackageType app包类型
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/updateTransByPublish",method=RequestMethod.GET)
	public Map<String, Object> updateTransByPublish(
			@RequestParam("applyNum") String applyNum,
			@RequestParam("appPackageType") String appPackageType,
			@RequestHeader(value="loginUserId",required=true) long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		if(StringUtils.isBlank(applyNum)){
			return this.getFailedMap("申请编号不能为空");
		}
		Trans trans = transService.findByApplyNum(applyNum);
		if(trans == null){
			return this.getFailedMap("申请信息不存在");
		}
		AppVersion version = appService.getAppVersion(trans.getTransactionsId());
		if(version == null){
			return this.getFailedMap("版本信息不存在");
		}
		App app = appService.findOne(version.getAppId());
		if(app == null){
			return this.getFailedMap("版本所属应用信息不存在");
		}
		try {
			//申请编号放入
			trans.setApplyNum(applyNum);
			
			Trans transnew = transService.updateTransByAppVersion(trans, loginUserId);
			if(transnew != null ){
				returnMsg.put("id", transnew.getTransactionsId()) ;
			}
			if(StringUtils.isNoneBlank(appPackageType)){
				version.setApplyPackageProperties(appPackageType);
				appService.saveAppversion(version);
			}
			//写入动态和通知------------------通用start
			//User user = this.userService.findUserById(loginUserId);
			//添加动态
			//this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TRANS_APP_ADD, app.getProjectId(), new Object[]{user,applyNum});
			//写入动态和通知------------------通用end
			//returnMsg.put("appId", app.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 版本申请发布，查看详情
	 * @param applyNum
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/publishDetail",method=RequestMethod.GET)
	public Map<String, Object> publishDetail(
			@RequestParam("id") Long id,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		if(id == null || id < 1){
			return this.getFailedMap("版本ID不能为空");
		}
		if(loginUserId == null || loginUserId < 1){
			return this.getFailedMap("用户ID不正确");
		}
		User user = this.userService.findUserById(loginUserId);
		if(user == null){
			return this.getFailedMap("用户不存在");
		}
		FilialeInfo fi = null;
		if(user.getFilialeId()  > 0){
			fi = filialeInfoService.findById(user.getFilialeId());
		}
		
		AppVersion version = appService.getAppVersion(id);
		
		if(version == null){
			return this.getFailedMap("应用版本不存在");
		}
		
		App app = appService.findOne(version.getAppId());
		if(app == null){
			return this.getFailedMap("应用不存在");
		}
		//查询大项目和子项目信息
		Project p = null;
		ProjectParent pp = null;
		if(app.getProjectId() > 0){
			p = projectService.findOne(app.getProjectId());
			if(p != null && p.getParentId() != null && p.getParentId() > 0){
				pp = projectParentService.findById(p.getParentId());
			}else if(app.getProjectParentId() > 0){
				pp = projectParentService.findById(app.getProjectParentId());
			}
		}
		//查询应用类型
		AppType appType = appService.findAppTypeById(app.getAppType());
		//版本类型
		List<AppPackage> appPackageList = appService.getAppPackageList(version.getId());
		AppPackage appPac = null;
		if(appPackageList != null && !appPackageList.isEmpty()){
			appPac = appPackageList.get(0);
		}
		returnMsg.put("userName", user.getUserName());
		if(fi != null)
			returnMsg.put("filialeName", fi.getFilialeName());
		else
			returnMsg.put("filialeName", "");
		
		if(pp != null){
			returnMsg.put("bigProjectName", pp.getProjectName());
		}else{
			returnMsg.put("bigProjectName", "");
		}
		if(p != null){
			returnMsg.put("projectName", p.getName());
		}else{
			returnMsg.put("projectName", "");
		}
		returnMsg.put("appCode", app.getAppCode());
		returnMsg.put("appName", app.getName());
		if(appType != null){
			returnMsg.put("appType", appType.getTypeName());
		}else{
			returnMsg.put("appType", "");
		}
		returnMsg.put("appVersionNo", version.getVersionNo());
		if(appPac != null){
			returnMsg.put("appPackageType", appPac.getOsType());
		}else{
			returnMsg.put("appPackageType", "");
		}
		returnMsg.put("versionDesc", version.getVersionDescription());
		returnMsg.put("applyPackageProperties", version.getApplyPackageProperties());
		
		return this.getSuccessMap(returnMsg);
	}
	/**
	 * 发布版本成功，审批时，查看版本详情
	 * @param applyNum
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/publishProDetail",method=RequestMethod.POST)
	public Map<String, Object> publishProDetail(
			@RequestParam("applyNum") String applyNum,
			@RequestHeader(value="loginUserId",required=true) Long loginUserId
			) {
		Map<String, Object> returnMsg = new HashMap<String, Object>();
		if(StringUtils.isBlank(applyNum)){
			return this.getFailedMap("申请编号不能为空");
		}
		if(loginUserId == null || loginUserId < 1){
			return this.getFailedMap("用户ID不正确");
		}
		User user = this.userService.findUserById(loginUserId);
		if(user == null){
			return this.getFailedMap("登录用户不存在");
		}
		Trans trans = transService.findByApplyNum(applyNum);
		if(trans == null || trans.getTransactionsId() < 1){
			return this.getFailedMap("申请信息不存在或存在异常");
		}
		AppVersion version = appService.getAppVersion(trans.getTransactionsId());
		
		if(version == null){
			return this.getFailedMap("应用版本不存在");
		}
		
		App app = appService.findOne(version.getAppId());
		
		if(app == null){
			return this.getFailedMap("应用不存在");
		}
		user = this.userService.findUserById(trans.getUserId());
		//查询申请用户的所属网省
		FilialeInfo fi = null;
		if(user.getFilialeId()  > 0){
			fi = filialeInfoService.findById(user.getFilialeId());
		}
		//查询大项目和子项目信息
		Project p = null;
		ProjectParent pp = null;
		if(app.getProjectId() > 0){
			p = projectService.findOne(app.getProjectId());
			if(p != null && p.getParentId() != null && p.getParentId() > 0){
				pp = projectParentService.findById(p.getParentId());
			}else if(app.getProjectParentId() > 0){
				pp = projectParentService.findById(app.getProjectParentId());
			}
		}
		//查询应用类型
		AppType appType = appService.findAppTypeById(app.getAppType());
		//版本类型
		List<AppPackage> appPackageList = appService.getAppPackageList(version.getId());
		AppPackage appPac = null;
		if(appPackageList != null && !appPackageList.isEmpty()){
			appPac = appPackageList.get(0);
		}
		List<Map<String, Object>> hislist = transService.getApplylist(applyNum);
		returnMsg.put("examineList", hislist);
		returnMsg.put("applyNum", applyNum);
		returnMsg.put("date", trans.getCreatedAtStr());
		returnMsg.put("userName", user.getUserName());
		if(fi != null)
			returnMsg.put("filialeName", fi.getFilialeName());
		else
			returnMsg.put("filialeName", "");
		
		if(pp != null){
			returnMsg.put("bigProjectName", pp.getProjectName());
		}else{
			returnMsg.put("bigProjectName", "");
		}
		if(p != null){
			returnMsg.put("projectId", p.getId());
			returnMsg.put("projectName", p.getName());
		}else{
			returnMsg.put("projectName", "");
		}
		returnMsg.put("appId", app.getId());
		returnMsg.put("appCode", app.getAppCode());
		returnMsg.put("appName", app.getName());
		if(appType != null){
			returnMsg.put("appType", appType.getTypeName());
		}else{
			returnMsg.put("appType", "");
		}
		returnMsg.put("appVersionNo", version.getVersionNo());
		if(appPac != null){
			returnMsg.put("appPackageType", appPac.getOsType());
		}else{
			returnMsg.put("appPackageType", "");
		}
		returnMsg.put("applyPackageProperties", version.getApplyPackageProperties());
		returnMsg.put("versionDesc", version.getVersionDescription());
		returnMsg.put("status", trans.getStatus());
		
		return this.getSuccessMap(returnMsg);
	}
	
	/**
	 * 根据个数获取多个应用编号
	 * @param projectCode
	 * @param num
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getCodeByNum", method = RequestMethod.GET)
	public Map<String, Object> getCodeByNum(
			@RequestParam("projectCode") String projectCode,
			@RequestParam("num") Integer num,
			@RequestHeader(value = "loginUserId", required = true) Long loginUserId) {
		Map<String, Object> requestMap = new HashMap<String, Object>();
		requestMap.put("projectCode", projectCode);
		requestMap.put("apptype", "4");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			for (int i = 0; i < num; i++) {
				Map<String, Object> map = transService.updateAndGetSeqAll(requestMap, loginUserId);
				list.add(map);
			}
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.getFailedMap("获取失败");
		}
		return getSuccessMap(list);
	}
}
