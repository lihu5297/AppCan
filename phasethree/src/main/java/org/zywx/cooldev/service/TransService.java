package org.zywx.cooldev.service;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DATA_STATUS;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.TRANS_MOVE;
import org.zywx.cooldev.commons.Enums.TRANS_NODE;
import org.zywx.cooldev.commons.Enums.TRANS_STATUS;
import org.zywx.cooldev.commons.Enums.TRANS_TYPE;
import org.zywx.cooldev.entity.EntityResourceRel;
import org.zywx.cooldev.entity.Resource;
import org.zywx.cooldev.entity.SEQ;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.app.AppVersion;
import org.zywx.cooldev.entity.datamodel.DataModel;
import org.zywx.cooldev.entity.filialeInfo.FilialeInfo;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectParent;
import org.zywx.cooldev.entity.tInterface.TInterFace;
import org.zywx.cooldev.entity.trans.ApproveModel;
import org.zywx.cooldev.entity.trans.Trans;
import org.zywx.cooldev.entity.trans.TransFlowHIS;
import org.zywx.cooldev.entity.trans.TransHis;
import org.zywx.cooldev.util.BeanToMapUtil;

@Service
public class TransService extends BaseService {
	
	@Autowired
	protected UserService userService;
	@Autowired
	protected UserAuthService userAuthService;
	
	
	/**
	 * 数模申请
	 *	1、保存数模表数据
	 *	2、保存交易表数据
	 * @param datamodel 数据模型
	 * @param trans	交易
	 * @param loginUserId 用户
	 * @return
	 * @throws ParseException 
	 */
	public Trans addTransByDataModel(
			DataModel datamodel,
			Trans trans,
			long loginUserId) throws ParseException {
		
		//1、通过交易申请号查询是否存在交易
		String applyNum = trans.getApplyNum();
		if(!StringUtils.isNotBlank(applyNum)){
			throw new RuntimeException("申请交易号不可为空");
		}
		List<Trans> translist = transDao.findByUserIdAndApplyNum(loginUserId,applyNum);
		if(translist != null && translist.size()>0){
			//存在，提示已提交不可重复提交
			throw new RuntimeException("交易："+applyNum+"，已提交不可重复提交！");
		}
		//查询项目是否存在
		//子项目id
		//Long projectId =  datamodel.getProjectId();
		//大项目id
		Long projectParentId =  datamodel.getProjectParentId();
		
		/*Project project  =  projectDao.findByIdAndDel(projectId, DELTYPE.NORMAL);
		if(project == null || project.getId() == 0){
			//子项目不存在
			throw new RuntimeException("交易："+applyNum+"，子项目不存在！");
		}*/
		//检查大项目，目前还没有
		ProjectParent pjp =  projectParentDao.findByIdAndDel(projectParentId, DELTYPE.NORMAL);
		if(pjp == null || pjp.getId() == 0){
			//大项目不存在
			throw new RuntimeException("交易号："+applyNum+"，大项目不存在！");
		}
		//------------------
		//2、不存在，则保存数模表，存在则报错,数模的校验都在ctrl层处理
		datamodel.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		datamodel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		datamodel.setDel(DELTYPE.NORMAL);
		
		DataModel dm = dataModelDao.save(datamodel);
		if(dm != null && dm.getId() != 0){
			//保存成功
		}else{
			throw new RuntimeException("数模数据保存失败！");
		}
		//3、保存数模表成功后，保存交易表
		//申请交易的节点为审批
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		//数模主键放入交易关键主键字段
		trans.setTransactionsId(dm.getId());
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}
	
	/**
	 * 数模重新申请
	 *	1、保存数模表数据
	 *	2、保存交易表数据
	 *	3、保存历史
	 * @param datamodel 数据模型
	 * @param trans	交易
	 * @param loginUserId 用户
	 * @return
	 * @throws ParseException 
	 */
	public Trans updateTransByDataModel(
			DataModel datamodel,
			Trans trans,
			long loginUserId) throws ParseException {
		//原交易
		Trans transold = new Trans();
		//1、通过交易申请号查询是否存在交易
		String applyNum = trans.getApplyNum();
		if(!StringUtils.isNotBlank(applyNum)){
			throw new RuntimeException("申请交易号不可为空");
		}
		
		//查询项目是否存在
		//子项目id
		//Long projectId =  datamodel.getProjectId();
		//大项目id
		Long projectParentId =  datamodel.getProjectParentId();
		
		/*Project project  =  projectDao.findByIdAndDel(projectId, DELTYPE.NORMAL);
		if(project == null || project.getId() == 0){
			//子项目不存在
			throw new RuntimeException("交易："+applyNum+"，子项目不存在！");
		}*/
		//检查大项目，目前还没有
		ProjectParent pjp =  projectParentDao.findByIdAndDel(projectParentId, DELTYPE.NORMAL);
		if(pjp == null || pjp.getId() == 0){
			//大项目不存在
			throw new RuntimeException("交易号："+applyNum+"，大项目不存在！");
		}
		List<Trans> translist = transDao.findByUserIdAndApplyNum(loginUserId,applyNum);
		if(translist != null && translist.size()>0){
			transold = translist.get(0);
			//如果交易的状态不为4，报错
			/*if(!"4".equals(transold.getStatus())){
				throw new RuntimeException("交易号："+applyNum+"，交易状态不是否决状态不可重新申请！");
			}*/
			//修改进入历史表
			try {
				this.addTransFlowHis(transold, 0, "重新申请", loginUserId);
			} catch (Exception e) {
				throw new RuntimeException("审核历史存入异常！");
			}
			
		}
		//------------------
		//2、不存在，则保存数模表，存在则报错,数模的校验都在ctrl层处理
		datamodel.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		datamodel.setId(transold.getTransactionsId());
		DataModel dm = dataModelDao.save(datamodel);
		if(dm != null && dm.getId() != 0){
			//保存成功
		}else{
			throw new RuntimeException("数模数据保存失败！");
		}
		//3、保存数模表成功后，保存交易表,重新申请
		//数模主键放入交易关键主键字段
		//审批时间
		Timestamp ts = new Timestamp(new Date().getTime());
		trans = transold;	
		//修改时间
		trans.setUpdatedAt(ts);
		trans.setMessage("");
		trans.setManageId(0);
		trans.setApprovalTime(null);
		trans.setSignTime(null);
		trans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
		trans.setSubTime(ts);
		//节点 0申请，1审批，2创建，3结束
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}

	/**
	 * 数模申请详情
	 *	条件：applyNum，申请编号
	 *		loginUserId 登录用户
	 *	逻辑：1、通过申请编号和登录用户获取交易记录
	 *		2、通过交易中的transactionId获取数模表数据
	 *		3、通过项目
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> getDataModelDetail(Map<String, Object> requestMap,long loginUserId)  throws ParseException {
		Map<String, Object> dataModelMap = new HashMap<String, Object>();
		//1、通过申请编号和登录用户获取交易记录
		String applyNum = (String) requestMap.get("applyNum");
		long dmid = 0;
		long projectId = 0;
		String sql = 
				"SELECT"
				+" 	tr.id, "
				+" 	tr.applyNum,"
				+"	tr.transactionsId,"
				+" 	tr.subTime,"
				+" 	tr.status,"
				+" 	u.userName,"
				+" 	dm.dmDesc,"
				+" 	dm.file,"
				+" 	dm.projectId,"
				//+" 	pj.name as projectName,"
				+" 	dm.projectParentId,"
				+" 	mfi.filialeName,"
				+" 	mfi.filialeCode,"
				+" 	pjp.projectName as pjParentName"
				+" FROM"
				+" 	T_TRANS tr,"
				+" 	T_DATAMODEL dm,"
				+" 	T_PROJECT pj,"
				+" 	T_USER u,"
				+" 	T_MAN_FILIALE_INFO mfi,"
				+" 	T_PROJECT_PARENT pjp"
				+" WHERE 1=1"
				//+" 	dm.projectId = pj.id"
				+" 	AND tr.transactionsId = dm.id"
				+" 	AND u.id = tr.userId"
				+" 	AND u.filialeId = mfi.id"
				+" 	AND dm.projectParentId = pjp.id"
				+" 	AND tr.applyNum = '"+applyNum+"'"
				//+" 	AND tr.userId = "+loginUserId
				//+" 	AND dm.userId = "+loginUserId
				+" 	AND tr.del = 0";
				//+" 	AND dm.del = 0"
				//+" 	AND pj.del = 0";
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		if(list == null || list.size() <= 0){
			//未找到记录
			throw new RuntimeException("未找到记录,或记录异常！");
		}else{
			dataModelMap = list.get(0);
			dmid = (long) dataModelMap.get("transactionsId");
			projectId = (long) dataModelMap.get("projectId");
		}
		//获取审批列表
		List<Map<String, Object>> hislist = this.getApplylist(applyNum);
		if(projectId==0){
			dataModelMap.put("projectName", "");
		}else {
			Project project = projectDao.findOne(projectId);
			dataModelMap.put("projectName",project.getName());
			
		}
		dataModelMap.put("examineList", new ArrayList<>());
		if(hislist == null || hislist.size()<=0){
			//未找到不处理
		}else{
			dataModelMap.put("examineList", hislist);
		}
		
		dataModelMap.put("resources", new ArrayList<>());
		dataModelMap.put("resourcesTotal", 0);
		// 添加数模资源列表
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(dmid, ENTITY_TYPE.DATAMODEL, DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for(EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		if(resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			dataModelMap.put("resources", resources);
			dataModelMap.put("resourcesTotal", resources.size());
		}
		//资源处理完毕
		
		return dataModelMap;
	}
	
	/**
	 * 我的申请列表
	 *	条件：applyNum，申请编号
	 *		tranType 类型
	 *		projectName 子项目名称
	 *		pjProjectName 大项目名称
	 *	逻辑：1、通过申请编号和登录用户获取交易记录
	 *		2、通过交易中的transactionId获取数模表数据
	 *		3、通过项目
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> applicationList(Map<String, Object> requestMap,long loginUserId,Map<String, Integer> authMap)  throws ParseException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String sqladd = "";
		//查询条件-申请编号
		String applyNum = (String) requestMap.get("applyNum");
		if(StringUtils.isNotBlank(applyNum)){
			sqladd += " and t.applyNum = '"+applyNum+"'";
		}
		//查询条件-申请类型
		String tranType = (String) requestMap.get("tranType");
		if(StringUtils.isNotBlank(tranType)){
			if(tranType.equals("4")){
				sqladd += " and t.tranType in (4,5)";
			}else{
				sqladd += " and t.tranType = '"+tranType+"'";
			}
		}
		//查询条件-子项目名称
		String projectName = (String) requestMap.get("projectName");
		if(StringUtils.isNotBlank(projectName)){
			sqladd += " and pj.`name` like   '%"+projectName+"%' ";
		}
		
		//查询条件-大项目名称
		String pjProjectName = (String) requestMap.get("pjProjectName");
		if(StringUtils.isNotBlank(pjProjectName)){
			sqladd += " and pjp.projectName like   '%"+pjProjectName+"%' ";
		}
		//查询条件-大项目id
		Integer parentId = (Integer) requestMap.get("parentId");
		if(parentId !=null ){
			sqladd += " and pjp.id ="+parentId;
		}
		boolean isFinsh = (boolean) requestMap.get("isFinsh");
		if(isFinsh){//已完成
			sqladd +=" and t.`node` = '3'";
		}else {
			sqladd +=" and t.`node` != '3'";
		}
		//当前页数
		Integer page = Integer.valueOf((String) requestMap.get("page"));
		
		//每页数量
		Integer size =Integer.valueOf( (String) requestMap.get("size"));
		if(size == 0){
			size = 10;
		}
		//起始数量
		Integer startNum =(page-1)*size;
		
		String sqldesc = " order by t.createdAt desc,t.id DESC ";
		
		String limitSql = " limit "+startNum+","+size;
		 
		Map<String,Object> sqlMap = this.translist("1", loginUserId, sqladd, sqldesc, limitSql, authMap);
		String sql  = (String)sqlMap.get("sql");
		String sqltotal  = (String)sqlMap.get("sqltotal");
		
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		if(list == null || list.size() <= 0){
			//未找到记录
			returnMap.put("total", 0);
		}else{
			for (Map<String, Object> map : list) {
				if (!map.get("node").equals("3")) {// 创建
					//map.put("updatedAt", "");
					if (!map.get("status").equals("1")) {
						map.put("status", "2");
					}
				} else if (map.get("node").equals("3")) { // 结束环节
					if (!map.get("status").equals("5")) {
						map.put("status", "3");
					}
				}  
			}
			List<Map<String, Object>> totallist = this.jdbcTpl.queryForList(sqltotal);
			Map<String, Object> totalMap = totallist.get(0);
			returnMap.put("total", (long)totalMap.get("total"));
		}
		returnMap.put("list", list);
		
		return returnMap;
	}
	
	/**
	 * 待办列表
	 *	条件：
	 *	applyNum	申请编号
		transType	交易类型
		projectName	子项目
		pjParentName	大项目
		status		状态 0申请， 1已签收，2未签收，3已完成，4未完成
		page	页数
		size	每页数量
		filialeId 网省id
	 *	逻辑：1、只有总部可以查询
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> todoList(Map<String, Object> requestMap, long loginUserId,Map<String, Integer> authMap) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		String sqladd = "";
		
		String userId = (String) requestMap.get("userId");
		if(StringUtils.isNotBlank(userId)){
			sqladd += " and t.userId = "+userId;
		}
		
		//查询条件-申请编号
		String applyNum = (String) requestMap.get("applyNum");
		if(StringUtils.isNotBlank(applyNum)){
			sqladd += " and t.applyNum = '"+applyNum+"'";
		}
		//查询条件-申请类型
		String tranType = (String) requestMap.get("tranType");
		if(StringUtils.isNotBlank(tranType)){
			if(tranType.equals("4")){
				sqladd += " and t.tranType in (4,5)";
			}else{
				sqladd += " and t.tranType = '"+tranType+"'";
			}
		}
		//查询条件-子项目名称
		String projectName = (String) requestMap.get("projectName");
		if(StringUtils.isNotBlank(projectName)){
			sqladd += " and pj.`name` like   '%"+projectName+"%' ";
		}
		
		//查询条件-大项目名称
		String pjProjectName = (String) requestMap.get("pjProjectName");
		if(StringUtils.isNotBlank(pjProjectName)){
			sqladd += " and pjp.projectName like   '%"+pjProjectName+"%' ";
		}
		//查询条件-网省id
		String filialeId = (String) requestMap.get("filialeId");
		if(StringUtils.isNotBlank(filialeId)){
			sqladd += " and u.filialeId =  "+filialeId;
		}
		
		//分页部分-----
		
		String limitSql = (String) requestMap.get("limitSql");
		
		//分页部分-------
		String sqldesc = " order by t.updatedAt desc ,t.id DESC";
		Map<String,Object> sqlMap = this.translist("2", loginUserId, sqladd, sqldesc, limitSql, authMap);
		String sql  = (String)sqlMap.get("sql");
		String sqltotal  = (String)sqlMap.get("sqltotal");
		
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		if(list == null || list.size() <= 0){
			//未找到记录
			returnMap.put("total", 0);
			returnMap.put("list", list);
		} else {
			// node节点为结束 则状态显示为已结束 否则 显示为未结束
			for (Map<String, Object> map : list) {
				if (map.get("node").equals("3")) {
					map.put("status", 3);
				} else {
					if (!map.get("status").equals("1")) {
						map.put("status", "2");
					}
				}
				//map.put("updatedAt", "");
			}
			returnMap.put("list", list);
			List<Map<String, Object>> totallist = this.jdbcTpl
					.queryForList(sqltotal);
			Map<String, Object> totalMap = totallist.get(0);
			returnMap.put("total", (long) totalMap.get("total"));
		}
		
		return returnMap;
	}
	
	/**
	 * 审批
	 *	条件：
	 *	applyNum	申请编号
		approveStatus	审批状态，0通过，1未通过
		approveMsg 	审批内容
		appApproveList 应用审批组，只有项目审核用
	 *	逻辑：
	 *	1、如果申请编号的申请类型是5，则appApproveList必须有值
	 *	2、申请类型如果是非5的申请，统一审核逻辑：
	 *		审核通过，修改交易状态：trans.status = 3,(除了项目审核，其他都是)node=3 ，对应表的status为0有效
	 *	3、申请类型是5的：
	 *		a、项目审核失败，则其他不考虑
	 *		b、项目审核通过，当前交易状态 trans.status = 3，项目表status = 1 有效 ，
	 *			检查appApproveList的审核状态，如果都通过，则应用的状态有效
	 *			如果有不通过的应用，则，每一个重新发起审批申请，审批状态为失败
	 *	4、审核失败：	（非4，5）修改交易状态：trans.status = 4，节点：node=3 结束
	 *				（4，5）修改交易状态：trans.status = 4，节点：node=2创建 
	 *	
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> approveAll(Map<String, Object> requestMap, long loginUserId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//审核状态
		String approveStatus = (String) requestMap.get("approveStatus");
		int hisType = -1;
		//1、通过申请编号获取申请内容
		String applyNum = (String) requestMap.get("applyNum");
		if(!StringUtils.isNotBlank(applyNum)){
			return returnMap;
		}
		Trans trans = this.transDao.findByApplyNum(applyNum);
		if(trans == null ||trans.getId()==0 ){
			throw new RuntimeException("未找到申请记录！");
		}
		String applyStatus = trans.getStatus();
		if(("0".equals(approveStatus) && "3".equals(applyStatus))
			|| ("1".equals(approveStatus) && "4".equals(applyStatus))
				){
			throw new RuntimeException("请不要重复提交审核！");
		}
		//审批信息
		String approveMsg = (String) requestMap.get("approveMsg");
		
		String appApproveList = (String) requestMap.get("appApproveList");
		
		
		//1数模，2接口，3应用，4子项目，5子项目和应用申请，6移动应用发版申请，7后端应用发版申请
		String tranType = trans.getTranType();
		//数据表状态，0有效，1无效
		String tsStatus = String.valueOf(DATA_STATUS.VALID.ordinal());
		
		//1、子项目和应用申请
		if(String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()).equals(tranType )){
			//判断应用列表是否存在
			if(StringUtils.isNotBlank(appApproveList)){
				JSONArray jsonArray = JSONArray.fromObject(appApproveList);
				ArrayList<ApproveModel> list =(ArrayList<ApproveModel>) jsonArray.toList(jsonArray, ApproveModel.class);
				for (ApproveModel approveModel : list) {
					//项目审核失败
					if(approveModel.getStatus().equals("YES")&&approveModel.getType().equals("PROJECT")){
						
					}
					//项目审核成功
				}
				//应用状态
			}else{
				//不存在则只提交项目，走项目审批流程不考虑应用
				
			}
		}else{
			//判断是否审核通过
			if(String.valueOf(TRANS_MOVE.PASS.ordinal()).equals(approveStatus)){
				hisType=3;
				//交易状态设置为3		TRANS_STATUS:交易状态 0申请， 1已签收，2未签收，3已完成，4未完成
				trans.setStatus(String.valueOf(TRANS_STATUS.FINSH.ordinal()));
				//如果是子项目申请，和项目应用申请，应用,则节点为创建		TRANS_TYPE:交易类型 0其他， 1数模，2接口，3应用，4子项目，5子项目和应用申请，6移动应用发版申请，7后端应用发版申请
				if(String.valueOf(TRANS_TYPE.PJ.ordinal()).equals(tranType)|| String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()).equals(tranType)||String.valueOf(TRANS_TYPE.APP.ordinal()).equals(tranType)){
					trans.setNode(String.valueOf(TRANS_NODE.CREATE.ordinal()));
				}else{
					//其他全是结束
					trans.setNode(String.valueOf(TRANS_NODE.FINSH.ordinal()));
				}
				tsStatus =String.valueOf(DATA_STATUS.VALID.ordinal());
			}else if(String.valueOf(TRANS_MOVE.NOTPASS.ordinal()).equals(approveStatus)){
				hisType=4;
				//不通过 交易状态设置为4
				trans.setStatus(String.valueOf(TRANS_STATUS.UNFINSH.ordinal()));
				trans.setNode(String.valueOf(TRANS_NODE.APPLY.ordinal()));
				tsStatus = String.valueOf(DATA_STATUS.INVALID.ordinal());
			}else{
				//状态异常
				throw new RuntimeException("审核状态异常！");
			}
			trans.setMessage(approveMsg);
			//审核人
			trans.setManageId(loginUserId);
			//审核时间
			trans.setApprovalTime(new Timestamp(System.currentTimeMillis()));
			//修改时间
			trans.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			//签收时间
			trans.setSignTime(new Timestamp(System.currentTimeMillis()));
			//保存
			Trans returntran = transDao.save(trans);
			returnMap.put("transNew", returntran);
			//修改关联表状态 1数模，2接口，3应用，4子项目，5子项目和应用申请，6移动应用发版申请，7后端应用发版申请
			if(String.valueOf(TRANS_TYPE.DM.ordinal()).equals(tranType)){
				//数模
				//通过transitionid获取数据
				DataModel dm = dataModelDao.findByIdAndDel(trans.getTransactionsId(), DELTYPE.NORMAL);
				dm.setDmStatus(tsStatus);
				dm.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				
				DataModel dmnew = dataModelDao.save(dm);
				returnMap.put("enty", dmnew);
				returnMap.put("projectId", dmnew.getProjectId());
				
			}else if(String.valueOf(TRANS_TYPE.INTERFACE.ordinal()).equals(tranType)){
				//接口
				//通过transitionid获取数据
				TInterFace ist = tInterFaceDao.findByIdAndDel(trans.getTransactionsId(), DELTYPE.NORMAL);
				ist.setInfStatus(tsStatus);
				ist.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				
				TInterFace istnew = tInterFaceDao.save(ist);
				returnMap.put("enty", istnew);
				returnMap.put("projectId", istnew.getProjectId());
			}else if(String.valueOf(TRANS_TYPE.APP.ordinal()).equals(tranType)){
				//应用
				//通过transitionid获取数据
				App app = appDao.findOne(trans.getTransactionsId());
				//app.setAppStatus(tsStatus);
				app.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				//app.setDel(DELTYPE.NORMAL);
				App appnew = appDao.save(app);
				returnMap.put("enty", appnew);
				returnMap.put("projectId", appnew.getProjectId());
			}else if(String.valueOf(TRANS_TYPE.PJ.ordinal()).equals(tranType)){
				//子项目
				Project pro = projectDao.findOne(trans.getTransactionsId());
				//pro.setDel(DELTYPE.NORMAL);
				//projectDao.save(pro);
				returnMap.put("enty", pro);
				returnMap.put("projectId", pro.getId());
			}else if(String.valueOf(TRANS_TYPE.MOVEAPP.ordinal()).equals(tranType) || String.valueOf(TRANS_TYPE.BACKEND.ordinal()).equals(tranType)){
				//移动应用或后端应用发版申请
				//应用发版申请审核暂时只改申请状态，其它什么都不用做
				AppVersion ver = appVersionDao.findOne(trans.getTransactionsId());
				if(ver != null){
					App app = appDao.findOne(ver.getAppId());
					returnMap.put("enty", ver);
					returnMap.put("projectId", app.getProjectId());
				}else{
					returnMap.put("enty", ver);
					returnMap.put("projectId", ver);
				}
			}else{
				
			}
		}
		//存入历史表
		try {
			TransHis transhis = new TransHis();
			Map<String,Object> tranMap = BeanToMapUtil.convertBean(trans);
			transhis = (TransHis) BeanToMapUtil.toJavaBean(transhis, tranMap);
			transhis.setDel(DELTYPE.NORMAL);
			transhis.setHisType(hisType);
			transhis.setOperationId(loginUserId);
			transhis.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			transhis.setStartTime(trans.getCreatedAt());
			transhis.setEndTime(new Timestamp(System.currentTimeMillis()));
			transhis.setTransId(trans.getId());
			//保存
			transHisDao.save(transhis);
			//保存流程历史
			this.addTransFlowHis(trans, hisType, approveMsg, loginUserId);
		} catch (Exception e) {
			throw new RuntimeException("审核历史存入异常！");
		}
		return returnMap;
	}
	
	/**
	 * 已办列表
	 *	条件：
	 *	applyNum	申请编号
		transType	交易类型
		projectName	子项目
		pjParentName	大项目
		status		状态 3已完成
		page	页数
		size	每页数量
		filialeId 网省id
	 *	逻辑：1、登录人为managerId加上状态通过查询
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> doFinshList(Map<String, Object> requestMap, long loginUserId, Map<String, Integer> authMap) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		
		String sqladd = "";
		
		String userId = (String) requestMap.get("userId");
		if(StringUtils.isNotBlank(userId)){
			sqladd += " and t.userId = "+userId;
		}
		
		//查询条件-申请编号
		String applyNum = (String) requestMap.get("applyNum");
		if(StringUtils.isNotBlank(applyNum)){
			sqladd += " and t.applyNum = '"+applyNum+"'";
		}
		//查询条件-申请类型
		String tranType = (String) requestMap.get("tranType");
		if(StringUtils.isNotBlank(tranType)){
			if(tranType.equals("4")){
				sqladd += " and t.tranType in (4,5)";
			}else{
				sqladd += " and t.tranType = '"+tranType+"'";
			}
		}
		//查询条件-子项目名称
		String projectName = (String) requestMap.get("projectName");
		if(StringUtils.isNotBlank(projectName)){
			sqladd += " and pj.`name` like   '%"+projectName+"%' ";
		}
		
		//查询条件-大项目名称
		String pjProjectName = (String) requestMap.get("pjProjectName");
		if(StringUtils.isNotBlank(pjProjectName)){
			sqladd += " and pjp.projectName like   '%"+pjProjectName+"%' ";
		}
		//查询条件-网省id
		String filialeId = (String) requestMap.get("filialeId");
		if(StringUtils.isNotBlank(filialeId)){
			sqladd += " and u.filialeId =  "+filialeId;
		}
		
		//分页部分-----
		
		String limitSql = (String) requestMap.get("limitSql");
		
		//分页部分-------
		String sqldesc = " order by t.updatedAt desc ";
		
		//Map<String,Object> sqlMap = this.translist("3", loginUserId, sqladd, sqldesc, limitSql,authMap);
		Map<String,Object> sqlMap = this.dofinshlist(loginUserId, sqladd, sqldesc, limitSql,authMap);
		String sql  = (String)sqlMap.get("sql");
		String sqltotal  = (String)sqlMap.get("sqltotal");
		
		
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		if(list == null || list.size() <= 0){
			//未找到记录
			returnMap.put("total", 0);
			returnMap.put("list", list);
		}else{
			// node节点为结束 则状态显示为已结束  否则 显示为未结束
			for (Map<String, Object> map : list) {
				Integer hisType= (Integer) map.get("hisType");
				if (hisType==1) {
					map.put("cnNode", "创建");
					map.put("cnStatus", "已完成");
				}else if(hisType==3||hisType==4){
					map.put("cnNode", "审批");
					map.put("cnStatus", "未完成");
				} else {
					map.put("cnNode", "待定");
					map.put("cnStatus", "待定");
				}
			}
			returnMap.put("list", list);
			List<Map<String, Object>> totallist = this.jdbcTpl.queryForList(sqltotal);
			Map<String, Object> totalMap = totallist.get(0);
			returnMap.put("total", (long)totalMap.get("total"));
		}
		
		return returnMap;
	}
	/**
	 * 新版  已办列表
	 * @param loginUserId
	 * @param sqladd
	 * @param sqldesc
	 * @param sqllimit
	 * @param authMap
	 * @return
	 */
	private Map<String, Object> dofinshlist(long loginUserId, String sqladd,
			String sqldesc, String sqllimit, Map<String, Integer> authMap) {
		String defsql = "AND   t.operationId = "+loginUserId;
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String sql = 
				"select * from (SELECT"
				+" 	t.*, dm.projectId,"
				+" 	dm.projectParentId,"
				+" 	pj.`name` AS projectName,"
				+" 	pjp.projectName AS pjParentName"
				+" FROM"
				+" 	T_TRANS_HIS t"
				+" LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 	T_DATAMODEL dm"
				+" LEFT JOIN T_PROJECT pj ON dm.projectId = pj.id"
				+" LEFT JOIN T_PROJECT_PARENT pjp ON dm.projectParentId = pjp.id"
				+" WHERE"
				+" 	t.transactionsId = dm.id"
				+" and t.tranType = '1' "
				+ 	defsql
				+	sqladd
				+" UNION ALL"
				+" 	SELECT"
				+" 		t.*, itf.projectId,"
				+" 		itf.projectParentId,"
				+" 		pj.`name` AS projectName,"
				+" 		pjp.projectName AS pjParentName"
				+" 	FROM"
				+" 		T_TRANS_HIS t"
				+" LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 		T_INTERFACE itf"
				+" 	LEFT JOIN T_PROJECT pj ON itf.projectId = pj.id"
				+" 	LEFT JOIN T_PROJECT_PARENT pjp ON itf.projectParentId = pjp.id"
				+" 	WHERE"
				+" 		t.transactionsId = itf.id"
				+" 	and t.tranType = '2' "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, app.projectId,"
				+"  		app.projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS_HIS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_APP app"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON  pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = app.id"
				+"  	and t.tranType = '3' "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, pj.id as projectId,"
				+"  		pj.parentId as projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS_HIS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_PROJECT pj"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = pj.id"
				+"  	and t.tranType in ('4','5') "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, pj.id as projectId,"
				+"  		pj.parentId as projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS_HIS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"       T_APP_VERSION ver"
				+"  	LEFT JOIN T_APP app ON ver.appId = app.id"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = ver.id"
				+"  	and t.tranType in ('6','7') "
				+ defsql
				+ sqladd
				+" ) t";
		sql += sqldesc;
		sql += sqllimit;
		
		String sqltotal = 
				"select count(*) AS total from (SELECT"
				+" 	t.*, dm.projectId,"
				+" 	dm.projectParentId,"
				+" 	pj.`name` AS projectName,"
				+" 	pjp.projectName AS pjParentName"
				+" FROM"
				+" 	T_TRANS_HIS t"
				+" LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 	T_DATAMODEL dm"
				+" LEFT JOIN T_PROJECT pj ON dm.projectId = pj.id"
				+" LEFT JOIN T_PROJECT_PARENT pjp ON dm.projectParentId = pjp.id"
				+" WHERE"
				+" 	t.transactionsId = dm.id"
				+" and t.tranType = '1' "
				+ 	defsql
				+	sqladd
				+" UNION ALL"
				+" 	SELECT"
				+" 		t.*, itf.projectId,"
				+" 		itf.projectParentId,"
				+" 		pj.`name` AS projectName,"
				+" 		pjp.projectName AS pjParentName"
				+" 	FROM"
				+" 		T_TRANS_HIS t"
				+" LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 		T_INTERFACE itf"
				+" 	LEFT JOIN T_PROJECT pj ON itf.projectId = pj.id"
				+" 	LEFT JOIN T_PROJECT_PARENT pjp ON itf.projectParentId = pjp.id"
				+" 	WHERE"
				+" 		t.transactionsId = itf.id"
				+" 	and t.tranType = '2' "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, app.projectId,"
				+"  		app.projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS_HIS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_APP app"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON  pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = app.id"
				+"  	and t.tranType = '3' "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, pj.id as projectId,"
				+"  		pj.parentId as projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS_HIS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_PROJECT pj"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = pj.id"
				+"  	and t.tranType in ('4','5') "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, pj.id as projectId,"
				+"  		pj.parentId as projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS_HIS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"       T_APP_VERSION ver"
				+"  	LEFT JOIN T_APP app ON ver.appId = app.id"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = ver.id"
				+"  	and t.tranType in ('6','7') "
				+ defsql
				+ sqladd
				+" ) t";
		returnMap.put("sql", sql);
		returnMap.put("sqltotal", sqltotal);
		return returnMap;
	}

	/**
	 * 获取并修改编号
	 * updateAndGetSeqAll
	 *	条件：apptype:1申请编号,2项目编号，3子项目编号，4应用编号
	 *		loginUserId 登录用户
	 *	逻辑：1、通过申请编号和登录用户获取交易记录
	 *		2、通过交易中的transactionId获取数模表数据
	 *		3、通过项目
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public synchronized Map<String, Object> updateAndGetSeqAll(Map<String, Object> requestMap,long loginUserId)  throws ParseException {
		Map<String, Object> dataModelMap = new HashMap<String, Object>();
		//获取类型
		String apptype = (String) requestMap.get("apptype");
		//时间
		Timestamp nowts = new Timestamp(System.currentTimeMillis());
		//编号全
		String seqAll = "";
		
		//最新顺位数字
		long nowcode = 0;
		
		SEQ seq = new SEQ();
		//申请编号
		if("1".equals(apptype)){
			seq = seqDao.findByType(apptype);
			//针对数据库没有初始记录的情况
			if(null==seq){
				seq = new SEQ();
				seq.setType(apptype);
			}
			if(0==seq.getNowCode()){
				nowcode=1;
			}else{
				nowcode = seq.getNowCode()+1;
			}
			
			seq.setNowCode(nowcode);
			seq.setUpdatedAt(nowts);
			seqAll = String.valueOf(nowcode);
			//编号规则：日期+2位编号
			//DecimalFormat df=new DecimalFormat("0000000000000000");
		    //String str2=df.format(nowcode);
			//seqAll = str2;
		}else if("2".equals(apptype)){
			//项目编号
			//判断当前日期是否相同
			String sql = "SELECT"
					+" 	se.*"
					+" FROM"
					+" 	T_SEQ se"
					+" WHERE"
					+" 	se.type = '"+apptype+"'"
					+" AND DATE(se.nowDate) = DATE('"+nowts+"')";
			List<Map<String,Object>> codeMap =  this.jdbcTpl.queryForList(sql);
			
			if(codeMap == null || codeMap.size()==0 ){
				
				//未找到记录则修改，利用type编号修改
				nowcode = 1;
				seq.setNowCode(nowcode);
				seq.setNowDate(nowts);
				seq.setUpdatedAt(nowts);
				seq.setType(apptype);
			}else{
				seq = (SEQ) BeanToMapUtil.toJavaBean(seq, codeMap.get(0));
				seq.setId((Long) codeMap.get(0).get("id"));
				nowcode = seq.getNowCode()+1;
				//存在，则nowcode+1，保存
				seq.setNowCode(nowcode);
				seq.setUpdatedAt(nowts);
			}
			//编号规则：日期+2位编号
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			DecimalFormat df=new DecimalFormat("00");
		    String str2=df.format(nowcode);
			seqAll = sf.format(new Date()) +str2;
			
		}else if("3".equals(apptype)){
			//子项目编号，需要大项目id
			long parentId =  (long) requestMap.get("parentId");
			ProjectParent parent = projectParentDao.findOne(parentId);
			String pjProjectCode = parent.getProjectCode();
			seq = seqDao.findByTypeAndPjProjectCode(apptype, pjProjectCode);
			if(seq == null || seq.getId()<=0){
				nowcode = 1;
				//不存在
				seq = new SEQ();
				seq.setCreatedAt(nowts);
				seq.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				seq.setDel(DELTYPE.NORMAL);
				seq.setNowCode(nowcode);
				seq.setNowDate(nowts);
				seq.setPjProjectCode(pjProjectCode);
				seq.setType(apptype);
			}else{
				//存在+1
				nowcode = seq.getNowCode()+1;
				seq.setNowCode(nowcode);
				seq.setUpdatedAt(nowts);
			}
			User user = userDao.findOne(loginUserId);
			FilialeInfo filialeInfo = filialeInfoDao.findOne(user.getFilialeId());
			DecimalFormat df=new DecimalFormat("000");
		    String str2=df.format(nowcode);
			seqAll = pjProjectCode +filialeInfo.getFilialeCode()+str2;
		}else if("4".equals(apptype)){
			//应用编号
			String projectCode =  (String) requestMap.get("projectCode");
			seq = seqDao.findByTypeAndProjectCode(apptype, projectCode);
			if(seq == null || seq.getId()<=0){
				nowcode = 1;
				//不存在
				seq = new SEQ();
				seq.setCreatedAt(nowts);
				seq.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				seq.setDel(DELTYPE.NORMAL);
				seq.setNowCode(1);
				seq.setNowDate(nowts);
				seq.setProjectCode(projectCode);
				seq.setType(apptype);
			}else{
				//存在+1
				nowcode = seq.getNowCode()+1;
				seq.setNowCode(seq.getNowCode()+1);
				seq.setUpdatedAt(nowts);
			}
			DecimalFormat df=new DecimalFormat("0000");
		    String str2=df.format(nowcode);
			seqAll = projectCode +str2;
		}else if("5".equals(apptype)){
			seq = seqDao.findByType(apptype);
			//针对数据库没有初始记录的情况
			if(null==seq){
				seq = new SEQ();
				seq.setType(apptype);
			}
			if(0==seq.getNowCode()){
				nowcode=1;
			}else{
				nowcode = seq.getNowCode()+1;
			}
			
			seq.setNowCode(nowcode);
			seq.setUpdatedAt(nowts);
			seqAll = String.valueOf(nowcode);
			//编号规则：日期+2位编号
			//DecimalFormat df=new DecimalFormat("0000000000000000");
		    //String str2=df.format(nowcode);
			//seqAll = str2;
		}
		 seqDao.save(seq);
		dataModelMap.put("applyNum", seqAll);
		return dataModelMap;
	}

	/**
	 * 接口申请
	 *	1、保存数模表数据
	 *	2、保存交易表数据
	 * @param itf 接口
	 * @param trans	交易
	 * @param loginUserId 用户
	 * @return
	 * @throws ParseException 
	 */
	public Trans addTransByTInterFace(
			TInterFace itf,
			Trans trans,
			long loginUserId) throws ParseException {
		
		//检查
		//this.checkPJ(trans.getApplyNum(),loginUserId,itf.getProjectId(),itf.getProjectParentId());
		
		//------------------
		//2、不存在，则保存数模表，存在则报错,数模的校验都在ctrl层处理
		itf.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		itf.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		itf.setDel(DELTYPE.NORMAL);
		
		TInterFace titf = tInterFaceDao.save(itf);
		if(titf != null && titf.getId() != 0){
			//保存成功
		}else{
			throw new RuntimeException("数模数据保存失败！");
		}
		//3、保存数模表成功后，保存交易表
		//数模主键放入交易关键主键字段
		trans.setTransactionsId(titf.getId());
		//申请交易的节点为审批
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}
	/**
	 * 申请子项目时，检查交易编号是否被占用
	 * @param applyNum
	 * @return
	 */
	public String checkTransPrject(String applyNum){
		if(StringUtils.isBlank(applyNum)){
			return "申请交易号不可为空";
		}
		Trans trans = transDao.findByApplyNum(applyNum);
		if(trans != null){
			return "交易："+applyNum+"，已提交不可重复提交！";
		}
		return "ok";
	}
	
	/**
	 * 检查项目，申请编号（申请用）
	 * @param applyNum
	 * @param loginUserId
	 * @param projectId
	 * @param pjProjectId
	 * @throws ParseException
	 */
	public void checkPJ(String applyNum , long loginUserId,long projectId,long pjProjectId) throws ParseException {
		//1、通过交易申请号查询是否存在交易
		if(!StringUtils.isNotBlank(applyNum)){
			throw new RuntimeException("申请交易号不可为空");
		}
		List<Trans> translist = transDao.findByUserIdAndApplyNum(loginUserId,applyNum);
		if(translist != null && translist.size()>0){
			//存在，提示已提交不可重复提交
			throw new RuntimeException("交易："+applyNum+"，已提交不可重复提交！");
		}
		//查询项目是否存在
		//子项目id
		//大项目id
		Project project  =  projectDao.findOne(projectId);
		if(project == null || project.getId() == 0){
			//子项目不存在
			throw new RuntimeException("交易："+applyNum+"，子项目不存在！");
		}
		//检查大项目，目前还没有
		ProjectParent pjp =  projectParentDao.findOne(pjProjectId);
		if(pjp == null || pjp.getId() == 0){
			//大项目不存在
			throw new RuntimeException("交易号："+applyNum+"，大项目不存在！");
		}
	}
	/**
	 * 检查项目，申请编号（重新提交）
	 * @param applyNum
	 * @param loginUserId
	 * @param projectId
	 * @param pjProjectId
	 * @throws ParseException
	 */
	public void checkPJRep(String applyNum , long loginUserId,long projectId,long pjProjectId) throws ParseException {
		//1、通过交易申请号查询是否存在交易
		if(!StringUtils.isNotBlank(applyNum)){
			throw new RuntimeException("申请交易号不可为空");
		}
		List<Trans> translist = transDao.findByUserIdAndApplyNum(loginUserId,applyNum);
		if(translist == null || translist.size()<=0){
			//交易不存在不可重新提交
			throw new RuntimeException("交易："+applyNum+"，交易不存在，不可重新提交！");
		}
		Project project  =  projectDao.findByIdAndDel(projectId, DELTYPE.NORMAL);
		if(project == null || project.getId() == 0){
			//子项目不存在
			throw new RuntimeException("交易："+applyNum+"，子项目不存在！");
		}
		//检查大项目，目前还没有
		ProjectParent pjp =  projectParentDao.findByIdAndDel(pjProjectId, DELTYPE.NORMAL);
		if(pjp == null || pjp.getId() == 0){
			//大项目不存在
			throw new RuntimeException("交易号："+applyNum+"，大项目不存在！");
		}
	}
	
	/**
	 * 接口申请详情
	 *	条件：applyNum，申请编号
	 *		loginUserId 登录用户
	 *	逻辑：1、通过申请编号和登录用户获取交易记录
	 *		2、通过交易中的transactionId获取数模表数据
	 *		3、通过项目
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> getInterFaceDetail(Map<String, Object> requestMap,long loginUserId)  throws ParseException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//1、通过申请编号和登录用户获取交易记录
		String applyNum = (String) requestMap.get("applyNum");
		long dmid = 0;
		long projectId = 0;
		
		String sql = 
				"SELECT"
				+" 	tr.id, "
				+" 	tr.applyNum,"
				+" 	tr.subTime,"
				+" 	tr.status,"
				+" 	tr.transactionsId,"
				+" 	u.userName,"
				+" 	inf.infDesc,"
				+" 	inf.infFile,"
				+" 	inf.projectId,"
				//+" 	pj.name as projectName,"
				+" 	inf.projectParentId,"
				+" 	mfi.filialeName,"
				+" 	mfi.filialeCode,"
				+" 	pjp.projectName as pjParentName"
				+" FROM"
				+" 	T_TRANS tr,"
				+" 	T_INTERFACE inf,"
				//+" 	T_PROJECT pj,"
				+" 	T_USER u,"
				+" 	T_MAN_FILIALE_INFO mfi,"
				+" 	T_PROJECT_PARENT pjp"
				+" WHERE  1=1"
				//+" 	inf.projectId = pj.id"
				+" 	AND tr.transactionsId = inf.id"
				+" 	AND u.id = tr.userId"
				+" 	AND u.filialeId = mfi.id"
				+" 	AND inf.projectParentId = pjp.id"
				+" 	AND tr.applyNum = '"+applyNum+"'"
				//+" 	AND tr.userId = "+loginUserId
				//+" 	AND inf.userId = "+loginUserId
				+" 	AND tr.del = 0"
				+" 	AND inf.del = 0";
				//+" 	AND pj.del = 0";
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		if(list == null || list.size() <= 0){
			//未找到记录
			throw new RuntimeException("未找到记录,或记录异常！");
		}else{
			returnMap = list.get(0);
			dmid = (long) returnMap.get("transactionsId");
			projectId = (long) returnMap.get("projectId");
		}
		if(projectId==0){
			returnMap.put("projectName", "");
		}else {
			Project project = projectDao.findOne(projectId);
			returnMap.put("projectName",project.getName());
			
		}
		
		//获取审批列表
		List<Map<String, Object>> hislist = this.getApplylist(applyNum);
		
		returnMap.put("examineList", new ArrayList<>());
		if(hislist == null || hislist.size()<=0){
			//未找到不处理
		}else{
			returnMap.put("examineList", hislist);
		}
		
		returnMap.put("resources", new ArrayList<>());
		returnMap.put("resourcesTotal", 0);
		// 添加数模资源列表
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(dmid, ENTITY_TYPE.INTERFACE, DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for(EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		if(resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			returnMap.put("resources", resources);
			returnMap.put("resourcesTotal", resources.size());
		}
		//资源处理完毕
		
		return returnMap;
	}

	/**
	 * 接口重新申请
	 *	1、保存接口数据
	 *	2、保存交易表数据
	 *	3、保存历史
	 * @param itf 接口数据
	 * @param trans	交易
	 * @param loginUserId 用户
	 * @return
	 * @throws ParseException 
	 */
	public Trans updateTransByInterFace(
			TInterFace itf,
			Trans trans,
			long loginUserId) throws ParseException {
		//原交易
		Trans transold = new Trans();
		
		//1、通过交易申请号查询是否存在交易
		String applyNum = trans.getApplyNum();
		//this.checkPJRep(applyNum, loginUserId, itf.getProjectId(), itf.getProjectParentId());
		
		List<Trans> translist = transDao.findByUserIdAndApplyNum(loginUserId,applyNum);
		if(translist != null && translist.size()>0){
			transold = translist.get(0);
			//如果交易的状态不为4，报错
			if(!"4".equals(transold.getStatus())){
				throw new RuntimeException("交易号："+applyNum+"，交易状态不是否决状态不可重新申请！");
			}
			//修改进入历史表
			try {
				//保存流程历史
				this.addTransFlowHis(transold, 0, "重新申请", loginUserId);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("审核历史存入异常！");
			}
			
		}
		//------------------
		//2、不存在，则保存数模表，存在则报错,数模的校验都在ctrl层处理
		itf.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		itf.setId(transold.getTransactionsId());
		TInterFace titf = tInterFaceDao.save(itf);
		if(titf != null && titf.getId() != 0){
			//保存成功
		}else{
			throw new RuntimeException("接口保存失败！");
		}
		//3、保存成功后，保存交易表,重新申请
		
		//审批时间
		Timestamp ts = new Timestamp(new Date().getTime());
		trans = transold;	
		//修改时间
		trans.setUpdatedAt(ts);
		trans.setMessage("");
		trans.setManageId(0);
		trans.setApprovalTime(null);
		trans.setSignTime(null);
		trans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
		trans.setSubTime(ts);
		//节点 0申请，1审批，2创建，3结束
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}
	/**
	 * 处理待办已办已完结sql
	 * type :1 我的申请，2待办，3已完结
	 * @return
	 */
	public Map<String,Object> translist(String type,long userId , String sqladd ,String sqldesc ,String sqllimit,Map<String, Integer> authMap ){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String defsql = "";
		boolean createPermission = false;	//创建权限
		boolean approvalPermission = false;	//审批权限
		 String approvalAuth="";
		 String createAuth="";
		//user_project_app_approval 审批子项目/应用
		if((authMap.get("user_project_app_approval")!=null&&authMap.get("user_project_app_approval")==1)){
			approvalAuth+="3,4,5,";
			approvalPermission=true;
		}
		//审批数模
		if((authMap.get("user_digifax_approval") != null && authMap.get("user_digifax_approval")==1) ){
			approvalAuth+="1,";
			approvalPermission=true;
		}
		//审批接口
		if((authMap.get("user_interface_approval")!=null && authMap.get("user_interface_approval")==1) ){
			approvalAuth+="2,";
			approvalPermission=true;
		}
		//审批发版
		if((authMap.get("user_publish_approval")!=null&&authMap.get("user_publish_approval")==1) ){
			approvalAuth+="6,7,";
			approvalPermission=true;
		}
		//项目创建
		if(authMap.get("project_create") != null && authMap.get("project_create")==1){
			createPermission = true;
		}
		//审批类型过滤
		String typeSql=this.getApprovalType(approvalAuth);
		log.info("translist-->typeSql :"+typeSql);
		if("1".equals(type)){
			typeSql="";
			//我的申请添加内容
			defsql = " AND t.userId = "+userId;
		}else if("2".equals(type)){
			//待办
			if(approvalPermission && createPermission){
				String and = " AND ";
				String or = " OR ";
				String defsql2 = "";
				 
				//审批和创建权限全有
				//1 
				defsql = "("
						+"("
						+"	t.`status` = '1'"
						+"	AND t.manageId = "+userId 
						+"	 AND t.node=1"
						+") "
						+"OR("
						+"	t.`status` = '1'"
						+"	AND t.markId = "+userId 
						+"	AND t.node=2"
						+") "
						+"OR (t.`status` != '1' AND  t.node IN(1,2))"
					+")";
				defsql2 = " ("
						+"("
						+"	t.`status` = '1'"
						+"	AND t.markId = "+userId 
						+"	AND t.node=2"
						+") "
						+"OR (t.`status` != '1' AND  t.node =2)"
					+")";
				typeSql += or +defsql2;
				defsql=and +defsql;
				/*defsql = " AND ("
						+" ("
						+" t.`status` = '1'"			//1代表已签收
						+" AND t.manageId = "+userId    //谁签收谁审批
						+" )"
						+" OR t.`status` = '2'"		//2 代表未签收
						+"OR ("
								+" t.`node` = '2'"		//node=2代表到创建环节
								+" )"
						+" )" ;*/
			}else if(approvalPermission){
				//有审批权限
				// 1 ,本人签收的
				//2 , 所有未签收的
				defsql = "AND ("
						+"("
						+"	t.`status` = '1'"
						+"	AND t.manageId = "+userId 
						+"	 AND t.node=1"
						+") "
						
						+"OR (t.`status` != '1' AND  t.node =1)"
					+")";
				/*defsql = " AND ("
						+" ("
						+" t.`status` = '1'"			//1代表已签收
						+" AND t.manageId = "+userId    //谁签收谁审批
						+" )"
						+" OR t.`status` = '2'"		//2 代表未签收
						+" )";*/
			}else if(createPermission){
				//有创建项目的权限
				defsql = "AND ("
						+"("
						+"	t.`status` = '1'"
						+"	AND t.markId = "+userId 
						+"	AND t.node=2"
						+") "
						+"OR (t.`status` != '1' AND  t.node =2)"
					+")";
				/*defsql = " AND ("
						+" t.`node` = '2'"		//node=2代表到创建环节
						+" )";*/
			}else{
				typeSql=" where t.tranType =-1";
			}
		}else if("3".equals(type)){
			//已办
			if(approvalPermission && createPermission){
				//审批权限和创建权限都有
				defsql = " AND ((t.`status` in ('3','4','5') AND t.manageId = " + userId + ")"		//3、4代表 完成和未完成（审核拒绝）
						+" or (t.`node` = '3' AND t.markId = " + userId + "))";			//node=2代表到创建环节，即我审核通过的已 到创建环节
				
			}else if(approvalPermission){
				//有审批权限
				defsql = " AND (t.`status` in ('3','4','5') AND t.manageId = "+userId+")";
						//+" or (t.`node` = '2' AND t.markId = " + userId + ")";			//node=2代表到创建环节，即我审核通过的已 到创建环节
				
			}else if(createPermission){
				//有创建项目的权限
				defsql = " AND (t.`node` = '3' AND tranType in ('3','4','5') AND t.markId = "+userId+")";
				
			}
		}
		String sql = 
				"select * from (SELECT"
				+" 	t.*, dm.projectId,"
				+" 	dm.projectParentId,"
				+" 	pj.`name` AS projectName,"
				+" 	pjp.projectName AS pjParentName"
				+" FROM"
				+" 	T_TRANS t"
				+" LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 	T_DATAMODEL dm"
				+" LEFT JOIN T_PROJECT pj ON dm.projectId = pj.id"
				+" LEFT JOIN T_PROJECT_PARENT pjp ON dm.projectParentId = pjp.id"
				+" WHERE"
				+" 	t.transactionsId = dm.id"
				+" and t.tranType = '1' "
				+ 	defsql
				+	sqladd
				+" UNION ALL"
				+" 	SELECT"
				+" 		t.*, itf.projectId,"
				+" 		itf.projectParentId,"
				+" 		pj.`name` AS projectName,"
				+" 		pjp.projectName AS pjParentName"
				+" 	FROM"
				+" 		T_TRANS t"
				+" LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 		T_INTERFACE itf"
				+" 	LEFT JOIN T_PROJECT pj ON itf.projectId = pj.id"
				+" 	LEFT JOIN T_PROJECT_PARENT pjp ON itf.projectParentId = pjp.id"
				+" 	WHERE"
				+" 		t.transactionsId = itf.id"
				+" 	and t.tranType = '2' "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, app.projectId,"
				+"  		app.projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_APP app"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON  pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = app.id"
				+"  	and t.tranType = '3' "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, pj.id as projectId,"
				+"  		pj.parentId as projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_PROJECT pj"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = pj.id"
				+"  	and t.tranType in ('4','5') "
				+ defsql
				+ sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*, pj.id as projectId,"
				+"  		pj.parentId as projectParentId,"
				+"  		pj.`name` AS projectName,"
				+"  		pjp.projectName AS pjParentName"
				+"  	FROM"
				+"  		T_TRANS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"       T_APP_VERSION ver"
				+"  	LEFT JOIN T_APP app ON ver.appId = app.id"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = ver.id"
				+"  	and t.tranType in ('6','7') "
				+ defsql
				+ sqladd
				+" ) t"+ typeSql;
		sql += sqldesc;
		sql += sqllimit;
		
		String sqltotal = 
				"SELECT"
				+" 	count(1) AS total"
				+" FROM"
				+" 	("
				+" 		SELECT"
				+" 			t.*"
				+" 		FROM"
				+" 			T_TRANS t"
				+" 		LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 		T_DATAMODEL dm"
				+" 	LEFT JOIN T_PROJECT pj ON dm.projectId = pj.id"
				+" 	LEFT JOIN T_PROJECT_PARENT pjp ON dm.projectParentId = pjp.id"
				+" 	WHERE"
				+" 		t.transactionsId = dm.id"
				+" 	and t.tranType = '1' "
				+ 	defsql
				+	sqladd
				+" 	UNION ALL"
				+" 		SELECT"
				+" 			t.*"
				+" 		FROM"
				+" 			T_TRANS t"
				+" 		LEFT JOIN T_USER u ON u.id = t.userId,"
				+" 		T_INTERFACE itf"
				+" 	LEFT JOIN T_PROJECT pj ON itf.projectId = pj.id"
				+" 	LEFT JOIN T_PROJECT_PARENT pjp ON itf.projectParentId = pjp.id"
				+" 	WHERE"
				+" 		t.transactionsId = itf.id"
				+" 	and t.tranType = '2' "
				+ 	defsql
				+	sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*"
				+"  	FROM"
				+"  		T_TRANS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_APP app"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON app.projectParentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = app.id"
				+"  	and t.tranType = '3' "
				+ 	defsql
				+	sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*"
				+"  	FROM"
				+"  		T_TRANS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"  		T_PROJECT pj"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = pj.id"
				+"  	and t.tranType in ('4','5') "
				+ defsql
				+sqladd
				+" UNION ALL"
				+" SELECT"
				+"  		t.*"
				+"  	FROM"
				+"  		T_TRANS t"
				+"  LEFT JOIN T_USER u ON u.id = t.userId,"
				+"       T_APP_VERSION ver"
				+"  	LEFT JOIN T_APP app ON ver.appId = app.id"
				+"  	LEFT JOIN T_PROJECT pj ON app.projectId = pj.id"
				+"  	LEFT JOIN T_PROJECT_PARENT pjp ON pj.parentId = pjp.id"
				+"  	WHERE"
				+"  		t.transactionsId = ver.id"
				+"  	and t.tranType in ('6','7') "
				+ defsql
				+ sqladd
				+" 	) t"+typeSql;
		
		returnMap.put("sql", sql);
		returnMap.put("sqltotal", sqltotal);
		
		return returnMap;
	}
	private String getApprovalType(String str) {
		//去除最后一个逗号
		if(str==null||str.equals("")){return "";}
		char charAt = str.charAt(str.length()-1);
		 if(",".equals(String.valueOf(charAt))){
			 str=str.substring(0,str.length()-1);
		 } 
		 
		 return " where t.tranType in ("+str+")";
	}
public static void main(String[] args) {
	String str ="12,1,";
	char charAt = str.charAt(str.length()-1);
	System.out.println("charAt  "+charAt);
	 if(",".equals(String.valueOf(charAt))){
		 System.out.println("you,");
		 String string = str.substring(0,str.length()-1);
		 System.out.println(str);
		 System.out.println(string);
	 }else{
		 System.out.println("wu,");
	 }
}
	/**
	 * 通过申请编号查询审批记录
	 */
	public List<Map<String, Object>> getApplylist(String applyNum){
		//2、获取历史审核记录
/*		String sqlhis = 
				"SELECT"
				+"	td.*, ud.userName AS manageName,"
				+"	ud.filialeName AS manageFilialeName,"
				+"	ud.filialeCode AS manageFilialeCode"
				+" FROM"
				+"	("
				+"		SELECT"
				+"			tr.createdAt,"
				+"			tr.message,"
				+"			tr.manageId,"
				+"			tr.approvalTime,"
				+"			tr.status,"
				+"			tr.transactionsId,"
				+"			tr.subTime,"
				+"			tr.node,"
				+"			tr.applyNum"
				+"		FROM"
				+"			T_TRANS tr"
				+"		WHERE"
				+"			tr.applyNum = '"+applyNum+"'"
				+"			and tr.manageId <> 0"
				+"		UNION ALL"
				+"			SELECT"
				+"				trs.createdAt,"
				+"				trs.message,"
				+"				trs.manageId,"
				+"				trs.approvalTime,"
				+"				trs.status,"
				+"				trs.transactionsId,"
				+"				trs.subTime,"
				+"				trs.node,"
				+"				trs.applyNum"
				+"			FROM"
				+"				T_TRANS_HIS trs"
				+"			WHERE"
				+"				trs.applyNum = '"+applyNum+"'"
				+"				and trs.manageId <> 0"
				+"	) td"
				+" LEFT JOIN ("
				+"	SELECT"
				+"		u.id,"
				+"		u.userName,"
				+"		mfi.filialeName,"
				+"		mfi.filialeCode"
				+"	FROM"
				+"		T_USER u,"
				+"		T_MAN_FILIALE_INFO mfi"
				+"	WHERE"
				+"		u.filialeId = mfi.id"
				+"	AND u.del = 0"
				+") ud ON ud.id = td.manageId WHERE td.`STATUS` NOT in (1,2)"
				//+" GROUP BY td.status,td.node"
				+" ORDER BY"
				+"	td.createdAt DESC";
		
		log.info(" JDBCTEMPLE :" + sqlhis);*/
		
		String flowSql="SELECT his.*,u.userName,u.account FROM T_TRANS_FLOW_HIS his "
					   +" LEFT JOIN T_USER u ON u.id=his.operationId "
                       +" WHERE his.applyNum = "+applyNum+" ORDER BY his.createdAt DESC";
		log.info(" JDBCTEMPLE :" + flowSql);
		List<Map<String, Object>> hislist = this.jdbcTpl.queryForList(flowSql);
		return hislist ;
	}
	//添加通知
	//this.noticeService.addNotice(loginUserId, (Long[])memberUserIdArr.toArray(new Long[memberUserIdArr.size()]), NOTICE_MODULE_TYPE.PROJECT_ADD_MEMBER, new Object[]{loginUser,project});
	
	/**
	 * 应用申请
	 *	1、保存数模表数据
	 *	2、保存交易表数据
	 * @param app 
	 * @param trans	交易
	 * @param loginUserId 用户
	 * @return
	 * @throws ParseException 
	 */
	public Trans addTransByApp(App app, Trans trans, long loginUserId) throws Exception {
		
		//检查
		this.checkPJ(trans.getApplyNum(),loginUserId,app.getProjectId(),app.getProjectParentId());
		
		//------------------
		//2、不存在，则保存数模表，存在则报错,数模的校验都在ctrl层处理
//		app.setCreatedAt(new Timestamp(System.currentTimeMillis()));
//		app.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//		app.setDel(DELTYPE.NORMAL);
//		AppService appService = new AppService();
//		App appnew = appService.addApp(app, loginUserId);
//		
//		//App appnew = appDao.save(app);
//		if(appnew != null && appnew.getId() != 0){
//			//保存成功
//		}else{
//			throw new RuntimeException("数模数据保存失败！");
//		}
//		//3、保存数模表成功后，保存交易表
//		//数模主键放入交易关键主键字段
//		trans.setTransactionsId(appnew.getId());
		
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
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		//数模是 1
		trans.setTranType(String.valueOf(TRANS_TYPE.APP.ordinal()));
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}

	/**
	 * 应用申请详情
	 *	条件：applyNum，申请编号
	 *		loginUserId 登录用户
	 *	逻辑：1、通过申请编号和登录用户获取交易记录
	 *		2、通过交易中的transactionId获取应用数据
	 *		3、通过项目
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> getAppDetail(Map<String, Object> requestMap,long loginUserId)  throws ParseException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//1、通过申请编号和登录用户获取交易记录
		String applyNum = (String) requestMap.get("applyNum");
		long dmid = 0;
		String sql = 
				"SELECT"
				+" 	tr.id, "
				+" 	tr.applyNum,"
				+" 	tr.subTime,"
				+" 	tr.status,"
				+" 	tr.transactionsId,"
				+" 	u.userName,"
				+" 	app.detail,"
				+" 	app.projectId,"
				+" 	app.appType,"
				+" 	app.name,"
				+" 	app.appCode,"
				+" 	atype.typeName,"
				+" 	pj.name as projectName,"
				+" 	app.projectParentId,"
				+" 	mfi.filialeName,"
				+" 	mfi.filialeCode,"
				+" 	pjp.projectName as pjParentName"
				+" FROM"
				+" 	T_TRANS tr,"
				+" 	T_APP app,"
				+" 	T_APP_TYPE atype,"
				+" 	T_PROJECT pj,"
				+" 	T_USER u,"
				+" 	T_MAN_FILIALE_INFO mfi,"
				+" 	T_PROJECT_PARENT pjp"
				+" WHERE"
				+" 	app.projectId = pj.id"
				+" 	AND tr.transactionsId = app.id"
				+" 	AND u.id = tr.userId"
				+" 	AND u.filialeId = mfi.id"
				+" 	AND pj.parentId = pjp.id"
				+" 	AND app.appType = atype.id"
				+" 	AND tr.applyNum = '"+applyNum+"'"
				//+" 	AND tr.userId = "+loginUserId
				//+" 	AND app.userId = "+loginUserId
				+" 	AND tr.del = 0"/*
				+" 	AND pj.del = 0"*/;
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		if(list == null || list.size() <= 0){
			//未找到记录
			throw new RuntimeException("未找到记录,或记录异常！");
		}else{
			returnMap = list.get(0);
			dmid = (long) returnMap.get("transactionsId");
		}
		//获取审批列表
		List<Map<String, Object>> hislist = this.getApplylist(applyNum);
		
		returnMap.put("examineList", new ArrayList<>());
		if(hislist == null || hislist.size()<=0){
			//未找到不处理
		}else{
			returnMap.put("examineList", hislist);
		}
		
		returnMap.put("resources", new ArrayList<>());
		returnMap.put("resourcesTotal", 0);
		// 添加数模资源列表
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(dmid, ENTITY_TYPE.APP, DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for(EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		if(resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			returnMap.put("resources", resources);
			returnMap.put("resourcesTotal", resources.size());
		}
		//资源处理完毕
		
		return returnMap;
	}
	
	/**
	 * 应用重新申请
	 *	1、保存接口数据
	 *	2、保存交易表数据
	 *	3、保存历史
	 * @param app 接口数据
	 * @param trans	交易
	 * @param loginUserId 用户
	 * @return
	 * @throws ParseException 
	 */
	public Trans updateTransByApp(
			App app,
			Trans trans,
			long loginUserId) throws ParseException {
		//原交易
		Trans transold = new Trans();
		
		//1、通过交易申请号查询是否存在交易
		String applyNum = trans.getApplyNum();
		//this.checkPJRep(applyNum, loginUserId, app.getProjectId(), app.getProjectParentId());
		
		List<Trans> translist = transDao.findByUserIdAndApplyNum(loginUserId,applyNum);
		if(translist != null && translist.size()>0){
			transold = translist.get(0);
			//如果交易的状态不为4，报错
			if(!"4".equals(transold.getStatus())){
				//throw new RuntimeException("交易号："+applyNum+"，交易状态不是否决状态不可重新申请！");
			}
			//修改进入历史表
			try {
				//保存流程历史
				this.addTransFlowHis(transold, 0, "重新申请", loginUserId);
			} catch (Exception e) {
				throw new RuntimeException("审核历史存入异常！");
			}
			
		}
		//------------------
		//2、不存在，则保存表，存在则报错,校验都在ctrl层处理
		app.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		App appnew = appDao.save(app);
		if(appnew != null && appnew.getId() != 0){
			//保存成功
		}else{
			throw new RuntimeException("接口保存失败！");
		}
		//3、保存成功后，保存交易表,重新申请
		
		//审批时间
		Timestamp ts = new Timestamp(new Date().getTime());
		trans = transold;	
		//修改时间
		trans.setUpdatedAt(ts);
		trans.setMessage("");
		trans.setManageId(0);
		trans.setApprovalTime(null);
		trans.setSignTime(null);
		trans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
		trans.setSubTime(ts);
		//节点 0申请，1审批，2创建，3结束
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}

	public Trans addTransByProject(Project pro, Trans trans, long loginUserId){
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
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		//数模是 1
		trans.setTransactionsId(pro.getId());
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		return returntrans;
	}
	
	public Trans addTransByProAndApp(Project pro, Trans trans, long loginUserId){
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
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		//数模是 1
		trans.setTranType(String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()));
		trans.setTransactionsId(pro.getId());
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		return returntrans;
	}
	
	public Trans findByApplyNum(String applyNum){
		return transDao.findByApplyNum(applyNum);
	}
	
	/**
	 * 子项目申请详情
	 *	条件：applyNum，申请编号
	 *		loginUserId 登录用户
	 *	逻辑：1、通过申请编号和登录用户获取交易记录
	 *		2、通过交易中的transactionId获取应用数据
	 *		3、通过项目
	 * @param requestMap
	 * @return
	 * @throws ParseException 
	 */
	public Map<String, Object> getProjectChildDetail(Map<String, Object> requestMap,long loginUserId)  throws ParseException {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//1、通过申请编号和登录用户获取交易记录
		String applyNum = (String) requestMap.get("applyNum");
		long dmid = 0;
		String tranType = "";
		String sql = 
				"SELECT"
				+" 	tr.id, "
				+" 	tr.applyNum,"
				+" 	tr.subTime,"
				+" 	tr.status,"
				+" 	tr.transactionsId,"
				+" 	tr.tranType,"
				+" 	u.userName,"
				+" 	pj.detail,"
				+" 	pj.id pjId,"
				+"	pj.projectCode,"
				+" 	pj.name as projectName,"
				+" 	mfi.filialeName,"
				+" 	mfi.filialeCode,"
				+" 	pjp.projectName as pjParentName"
				+", team.`name` teamName ,team.id teamId,pc.id categoryId,pjp.id pjParentId,pc.`name` categoryName"
				+" FROM"
				+" 	T_TRANS tr,"
				+" 	T_PROJECT pj,"
				+" 	T_USER u,"
				+" 	T_MAN_FILIALE_INFO mfi,"
				+" 	T_PROJECT_PARENT pjp,"
				+"	T_TEAM team,"
				+"	T_PROJECT_CATEGORY pc"
				+" WHERE 1=1"
				+" 	AND tr.transactionsId = pj.id"
				+" 	AND u.id = tr.userId"
				+" 	AND u.filialeId = mfi.id"
				+"	AND team.id=pj.teamId"
				+"  AND pj.categoryId=pc.id"
				+" 	AND pj.parentId = pjp.id"
				+" 	AND tr.applyNum = '"+applyNum+"'"
				//+" 	AND tr.userId = "+loginUserId
				//+" 	AND pj.creatorId = "+loginUserId
				+" 	AND tr.del = 0";
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		if(list == null || list.size() <= 0){
			//未找到记录
			throw new RuntimeException("未找到记录,或记录异常！");
		}else{
			returnMap = list.get(0);
			dmid = (long) returnMap.get("transactionsId");
			tranType = (String) returnMap.get("tranType");
		}
		//获取审批列表
		List<Map<String, Object>> hislist = this.getApplylist(applyNum);
		
		returnMap.put("examineList", new ArrayList<>());
		if(hislist == null || hislist.size()<=0){
			//未找到不处理
		}else{
			returnMap.put("examineList", hislist);
		}
		
		returnMap.put("resources", new ArrayList<>());
		returnMap.put("resourcesTotal", 0);
		// 添加数模资源列表
		System.out.println(dmid+"**"+ENTITY_TYPE.PROJECT+"**"+ DELTYPE.NORMAL);
		List<EntityResourceRel> relList = entityResourceRelDao.findByEntityIdAndEntityTypeAndDel(dmid, ENTITY_TYPE.PROJECT, DELTYPE.NORMAL);
		List<Long> resourceIdList = new ArrayList<>();
		for(EntityResourceRel rel : relList) {
			resourceIdList.add(rel.getResourceId());
		}
		if(resourceIdList.size() > 0) {
			List<Resource> resources = resourcesDao.findByIdIn(resourceIdList);
			returnMap.put("resources", resources);
			returnMap.put("resourcesTotal", resources.size());
		}
		//资源处理完毕
		
		//处理 项目应用类型 5 取所带的应用
		returnMap.put("appList", new ArrayList<>());
		if(tranType.equals("5")){
			List<App> appList=appDao.findByProjectIdAndIsProApp(dmid,true);
			returnMap.put("appList",appList);
		}
		//取所带的应用处理完毕
		return returnMap;
	}
	/**
	 * 应用重新申请
	 *	1、保存接口数据
	 *	2、保存交易表数据
	 *	3、保存历史
	 * @param app 接口数据
	 * @param trans	交易
	 * @param loginUserId 用户
	 * @return
	 * @throws ParseException 
	 */
	public Trans updateTransByChildProject(
			Project pro,
			Trans transold,
			long loginUserId) throws ParseException {
		
		//修改进入历史表
		try {
			//保存流程历史
			this.addTransFlowHis(transold, 0, "重新申请", loginUserId);
		} catch (Exception e) {
			throw new RuntimeException("审核历史存入异常！");
		}
			
		//------------------
		//2、不存在，则保存表，存在则报错,校验都在ctrl层处理
		pro.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		Project newPro = this.projectDao.save(pro);
		if(newPro != null && newPro.getId() != 0){
			//保存成功
		}else{
			throw new RuntimeException("子项目保存失败！");
		}
		//3、保存成功后，保存交易表,重新申请
		
		//审批时间
		Timestamp ts = new Timestamp(new Date().getTime());
		Trans trans = transold;	
		//修改时间
		trans.setUpdatedAt(ts);
		trans.setMessage("");
		trans.setManageId(0);
		trans.setApprovalTime(null);
		trans.setSignTime(null);
		trans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
		trans.setSubTime(ts);
		//节点 0申请，1审批，2创建，3结束
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}
	
	public List<Trans>  findByUserIdAndApplyNum(long loginUserId, String applyNum){
		return transDao.findByUserIdAndApplyNum(loginUserId,applyNum);
	}
	public void approveSign(Trans trans) {
		transDao.save(trans);
	}
	public void updateCutOut(Trans trans,long loginUserId) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		trans.setUpdatedAt(ts);
		trans.setNode(String.valueOf(Enums.TRANS_NODE.FINSH.ordinal()));
		trans.setStatus(String.valueOf(Enums.TRANS_STATUS.CUTOUT.ordinal()));
		transDao.save(trans);
		//保存流程历史
		this.addTransFlowHis(trans, 2, "作废", loginUserId);
	}
	/**
	 * 子项目或应用审批通过后创建子项目或应用
	 * @param applyNum
	 * @return
	 */
	public String createProjectOrApp(String applyNum, long loginUserId){
		Trans trans = this.transDao.findByApplyNum(applyNum);
		if(trans == null ||trans.getId()==0 ){
			return "未找到申请记录！";
		}
		String tranType = trans.getTranType();
		if(String.valueOf(TRANS_TYPE.APP.ordinal()).equals(tranType)){
			//应用
			//通过transitionid获取数据
			App app = appDao.findOne(trans.getTransactionsId());
			Project project = projectDao.findByIdAndDel(app.getProjectId(), DELTYPE.NORMAL);
			log.info("创建子项目或应用-查找应用"+project);
			if(project==null){
				return "项目不存在！";
			}
			app.setAppStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
			app.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			app.setDel(DELTYPE.NORMAL);
			App appnew = appDao.save(app);
		}else if(String.valueOf(TRANS_TYPE.PJ.ordinal()).equals(tranType)){
			//子项目
			Project pro = projectDao.findOne(trans.getTransactionsId());
			pro.setDel(DELTYPE.NORMAL);
			projectDao.save(pro);
		} else if (String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()).equals(
				tranType)) {
			// 子项目和应用
			Project pro = projectDao.findOne(trans.getTransactionsId());
			pro.setDel(DELTYPE.NORMAL);
			projectDao.save(pro);
			List<App> appList = appDao.findByProjectIdAndIsProApp(pro.getId(), true);
			for (App app : appList) {
				app.setAppStatus(String.valueOf(DATA_STATUS.VALID.ordinal()));
				app.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
				app.setDel(DELTYPE.NORMAL);
				App appnew = appDao.save(app);
			}
		}
		trans.setStatus(String.valueOf(TRANS_STATUS.FINSH.ordinal()));
		trans.setNode(String.valueOf(TRANS_NODE.FINSH.ordinal()));
		trans.setMarkTime(new Timestamp(System.currentTimeMillis()));
		trans.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		trans.setMarkId(loginUserId);
		transDao.save(trans);
		try {
			TransHis transhis = new TransHis();
			Map<String,Object> tranMap = BeanToMapUtil.convertBean(trans);
			transhis = (TransHis) BeanToMapUtil.toJavaBean(transhis, tranMap);
			transhis.setDel(DELTYPE.NORMAL);
			transhis.setHisType(1);
			transhis.setOperationId(loginUserId);
			transhis.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			transhis.setStartTime(trans.getApprovalTime());
			transhis.setEndTime(new Timestamp(System.currentTimeMillis()));
			transhis.setTransId(trans.getId());
			//保存
			transHisDao.save(transhis);
			this.addTransFlowHis(trans, 1, "已创建", loginUserId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		//保存流程历史
		return "ok";
	}
	 
	//审核项目应用 5
	public Map<String,Object> approveProApp(long loginUserId, Map<String, Object> requestMap) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		int hisType=-1;
		// 审核状态
		//String approveStatus = (String) requestMap.get("approveStatus");
		// 1、通过申请编号获取申请内容
		String applyNum = (String) requestMap.get("applyNum");
		if(!StringUtils.isNotBlank(applyNum)){
			return returnMap;
		}
		// 审批信息
		String approveMsg = (String) requestMap.get("approveMsg");

		String appApproveList = (String) requestMap.get("appApproveList");
		Trans trans = this.transDao.findByApplyNum(applyNum);
		
		//判断应用列表是否存在
		if(String.valueOf(TRANS_TYPE.PJANDAPP.ordinal()).equals(trans.getTranType() )){
			
			if(StringUtils.isNotBlank(appApproveList)){
				JSONArray jsonArray = JSONArray.fromObject(appApproveList);
				ArrayList<ApproveModel> list =(ArrayList<ApproveModel>) jsonArray.toList(jsonArray, ApproveModel.class);
				boolean flag=false;
				for (ApproveModel approveModel : list) {
					if (approveModel.getType().equals("PROJECT")) {
						// 项目审核不通过
						if (approveModel.getStatus().equals("NO")) {
							hisType=4;
							// 不通过 交易状态设置为4
							trans.setStatus(String.valueOf(TRANS_STATUS.UNFINSH
									.ordinal()));
							trans.setNode(String.valueOf(TRANS_NODE.APPLY
									.ordinal()));
						}
						// 项目审核成功
						if (approveModel.getStatus().equals("YES")) {
							hisType=3;
							trans.setStatus(String.valueOf(TRANS_STATUS.FINSH
									.ordinal()));
							trans.setNode(String.valueOf(TRANS_NODE.CREATE
									.ordinal()));
							flag=true;
						}
						trans.setMessage(approveMsg);
						// 审核人
						trans.setManageId(loginUserId);
						// 审核时间
						trans.setApprovalTime(new Timestamp(System
								.currentTimeMillis()));
						// 修改时间
						trans.setUpdatedAt(new Timestamp(System
								.currentTimeMillis()));
						// 保存
						Trans returntran = transDao.save(trans);
						returnMap.put("transNew", returntran);
						returnMap.put("projectId",returntran.getTransactionsId());
					}
					if (approveModel.getType().equals("APP")&&flag) {
						// 不通过的应用
						if (approveModel.getStatus().equals("NO")) {
							// 应用抛出,修改应用isPRO
							App app = appDao.findOne(approveModel.getId());
							app.setProApp(false);
							appDao.save(app);
							// 新建申请
							// 申请编号放入
							Project project = projectDao.findOne(app.getProjectId());
							Map<String, Object> paramMap = new HashMap<String, Object>();
							paramMap.put("apptype", "1");
							paramMap.put("projectCode", String.valueOf(project.getProjectCode()));
							Map<String, Object> numMap = this.updateAndGetSeqAll(paramMap, loginUserId);
							
							// 新建应用申请
							Trans apptrans = new Trans();
							apptrans.setApplyNum((String) numMap.get("applyNum"));
							apptrans.setTransactionsId(app.getId());
							Timestamp ts = new Timestamp(new Date().getTime());
							//状态 提交为未签收2
							apptrans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
							//发布人
							apptrans.setUserId(app.getUserId());
							//申请时间
							apptrans.setSubTime(ts);
							//节点 0申请，1审批，2创建，3结束
							apptrans.setNode(String.valueOf(TRANS_NODE.APPLY.ordinal()));
							//数模是 1
							apptrans.setTranType(String.valueOf(TRANS_TYPE.APP.ordinal()));
							
							Trans apptransNew = transDao.save(apptrans);
							this.addTransFlowHis(apptransNew, 4, approveMsg, loginUserId);
							
							//this.addTransByApp(app, apptrans, app.getUserId());
						}// 应用通过
						if (approveModel.getStatus().equals("YES")) {
							 //跟着项目走
						}
					}
				}
				//应用状态
			}else{
				//不存在则只提交项目，走项目审批流程不考虑应用
				
			}
		}
		try {
			TransHis transhis = new TransHis();
			Map<String,Object> tranMap = BeanToMapUtil.convertBean(trans);
			transhis = (TransHis) BeanToMapUtil.toJavaBean(transhis, tranMap);
			transhis.setDel(DELTYPE.NORMAL);
			transhis.setHisType(hisType);
			transhis.setOperationId(loginUserId);
			transhis.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			transhis.setStartTime(trans.getCreatedAt());
			transhis.setEndTime(new Timestamp(System.currentTimeMillis()));
			transhis.setTransId(trans.getId());
			//保存
			transHisDao.save(transhis);
			//保存流程历史
			this.addTransFlowHis(trans, hisType, approveMsg, loginUserId);
		} catch (Exception e) {
			throw new RuntimeException("审核历史存入异常！");
		}
		return returnMap;
	}
 
 
	public Trans addTransByAppVersion(long transactionId, Trans trans, int tranType, long loginUserId) throws Exception {
		

//		//交易实体主键字段
		trans.setTransactionsId(transactionId);
		
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
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		
		trans.setTranType(String.valueOf(tranType));
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}
	
	public Trans findByTransactionsIdAndTranType(Long id, List<String> tranType){
		return transDao.findByTransactionsIdAndTranTypeIn(id, tranType);
	}
	
	public List<Trans> findByTransactionsIdAndTranType(Long id, String tranType){
		return transDao.findByTransactionsIdAndTranType(id, tranType);
	}
	/**
	 * 重新申请发版
	 * @param transold
	 * @param loginUserId
	 * @return
	 * @throws ParseException
	 */
	public Trans updateTransByAppVersion(
			Trans transold,
			long loginUserId) throws ParseException {
		
		//修改进入历史表
		try {
			//保存流程历史
			this.addTransFlowHis(transold, 0, "重新申请", loginUserId);
		} catch (Exception e) {
			throw new RuntimeException("审核历史存入异常！");
		}
			
		
		
		//审批时间
		Timestamp ts = new Timestamp(new Date().getTime());
		Trans trans = transold;	
		//修改时间
		trans.setUpdatedAt(ts);
		trans.setMessage("");
		trans.setManageId(0);
		trans.setApprovalTime(null);
		trans.setSignTime(null);
		trans.setStatus(String.valueOf(TRANS_STATUS.NOTSIGN.ordinal()));
		trans.setSubTime(ts);
		//节点 0申请，1审批，2创建，3结束
		trans.setNode(String.valueOf(TRANS_NODE.APPROVAL.ordinal()));
		
		Trans returntrans = transDao.save(trans);
		
		//4、保存失败报错
		
		return returntrans;
	}
	
	/**
	 * 保存流程历史
	 * @param trans
	 * @param hisType
	 * @param message
	 * @param operationId
	 */
	public void addTransFlowHis(Trans trans,Integer hisType,String message,long operationId){
		TransFlowHIS flowHis = new TransFlowHIS();
		flowHis.setHisType(hisType);
		flowHis.setMessage(message);
		flowHis.setOperationId(operationId);
		flowHis.setTransId(trans.getId());
		flowHis.setUserId(trans.getUserId());
		flowHis.setApplyNum(trans.getApplyNum());
		transFlowHISDao.save(flowHis);
	}
}
