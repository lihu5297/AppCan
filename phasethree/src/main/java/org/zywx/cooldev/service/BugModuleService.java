package org.zywx.cooldev.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.dao.bug.BugAuthDao;
import org.zywx.cooldev.dao.bug.BugDao;
import org.zywx.cooldev.dao.bug.BugMarkDao;
import org.zywx.cooldev.dao.bug.BugMemberDao;
import org.zywx.cooldev.dao.bug.BugModuleDao;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.bug.BugModule;
import org.zywx.cooldev.util.ChineseToEnglish;

@Service
public class BugModuleService extends BaseService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private BugDao bugDao;
	@Autowired
	private BugMemberDao bugMemberDao; 
	@Autowired
	private BugAuthDao bugAuthDao; 
	@Autowired
	private BugMarkDao bugMarkDao; 
	@Autowired
	private BugModuleDao bugModuleDao;
	@Autowired
	private ProjectService projectService;
	
	/**
	 * 删除bugModule
	 * @param bugModuleId
	 */
	public void remove(long bugModuleId) {
		BugModule bugModule= this.bugModuleDao.findOne(bugModuleId);
		bugModule.setDel(DELTYPE.DELETED);
		this.bugModuleDao.save(bugModule);
	}
	public int edit(BugModule bugModule, long loginUserId) {
		String settings = "";
		if(bugModule.getName() != null) {
			settings += String.format(",name='%s',pinYinHeadChar='%s',pinYinName='%s'", bugModule.getName(),ChineseToEnglish.getPinYinHeadChar(bugModule.getName()==null?"":bugModule.getName()),ChineseToEnglish.getPingYin(bugModule.getName()==null?"":bugModule.getName()));
		}
		if(bugModule.getProjectId() != -1) {
			settings += String.format(",projectId=%d", bugModule.getProjectId());
		}
		if(bugModule.getManagerId() !=-1) {
			settings += String.format(",managerId=%d", bugModule.getManagerId());
		}
		if(settings.length() > 0) {
			settings = settings.substring(1);
			String sql = String.format("update T_BUG_MODULE set %s where id=%d", settings, bugModule.getId());
			log.info("execute Sql:"+sql);
			int a = this.jdbcTemplate.update(sql);
			return a;
		} else {
			return 0;
		}
		
	}
	//查看(搜索)bug列表
	public List<Object> getBugModuleList(BugModule bugModule, long loginUserId) {
		StringBuffer bugModuleListSql=new StringBuffer();
		bugModuleListSql.append("select count(b.id) as totalBug,sum(case when b.status=2 then 1 else 0 end) as closeBugTotal,bm.*,u.icon as userIcon,u.userName from T_BUG_MODULE bm left join T_BUG b on bm.id=b.moduleId left join T_USER u on bm.managerId=u.id where bm.del=0");
		if(bugModule.getProjectId()!=0){
			bugModuleListSql.append(" and bm.projectId=").append(bugModule.getProjectId());
		}
		if(bugModule.getManagerId()!=0){
			bugModuleListSql.append(" and bm.managerId=").append(bugModule.getManagerId());
		}
		if(bugModule.getName()!=null){
			bugModuleListSql.append(" and (bm.name like '%").append(bugModule.getName()).append("%' or bm.pinYinHeadChar like '").append(bugModule.getName()).append("%' or bm.pinYinName like '").append(bugModule.getName()).append("%')");
		}
		bugModuleListSql.append(" group by bm.id");
		log.info("bugModuleList-->"+bugModuleListSql.toString());
		List<Map<String,Object>> bugModuleList=jdbcTpl.queryForList(bugModuleListSql.toString());
		List<Object> result = new ArrayList<Object>();
		Map<String,Object> map = new HashMap<String, Object>();
		//查询这个人有哪些权限
		StringBuffer permissionSql=new StringBuffer();
//		permissionSql.append("select enName from T_PERMISSION where del=0 and id in (select premissionId from T_ROLE_AUTH where del=0 and roleId in ")
//		.append("(select roleId from T_PROJECT_AUTH where del=0 and memberId in ")
//		.append("(select id from T_PROJECT_MEMBER where del=0 and projectId=").append(bugModule.getProjectId()).append(" and userId=").append(loginUserId).append(")))");
		permissionSql.append("SELECT	enName FROM	T_PERMISSION WHERE 	del = 0 AND id IN ( select * from ( SELECT		premissionId	FROM		T_ROLE_AUTH	WHERE		del = 0	AND roleId IN (	")
		.append(" select * from (	SELECT roleId FROM	 T_PROJECT_AUTH	 WHERE del = 0	 AND memberId IN ( SELECT id FROM T_PROJECT_MEMBER 	WHERE del = 0 AND projectId = "+bugModule.getProjectId()+" AND userId = "+loginUserId+" )")
		.append(" ) tmp12	)) tmp222) ");
		log.info("bugModuleListForPermission-->"+permissionSql.toString());
		List<Map<String,Object>> permissionList = jdbcTpl.queryForList(permissionSql.toString());
		Map<String, Integer> pMap = new HashMap<>();
 		if(bugModuleList != null) {
 			for(Map<String, Object> p : permissionList) {
 				pMap.put((String) p.get("enName"), 1);
 			}
 		}
 		map.put("permission",pMap);
		map.put("data",bugModuleList);
		result.add(map);
		return result;
	}
	public boolean judgeProjectIdAndName(long projectId, String name) {
		BugModule bugModule=bugModuleDao.findExitByProjectIdAndNameAndDel(projectId,name,DELTYPE.NORMAL);
		if(bugModule!=null){
			return true;
		}else{
			return false;
		}
		
	}
	public int moveBugs(long oldModuleId, long newModuleId) {
		String sql ="update T_BUG set moduleId="+newModuleId+" where moduleId="+oldModuleId;
		log.info("execute Sql:"+sql);
		int a = this.jdbcTemplate.update(sql);
		return a;
	}
	public Map<String, Object> getBugModuleDetail(long bugModuleId,
			long loginUserId) {
		BugModule bmO=this.bugModuleDao.findByIdAndDel(bugModuleId,DELTYPE.NORMAL);
		if(bmO==null){
		   throw new RuntimeException("bug模块不存在或已删除");
		}
		Map<String,Object> map=new HashMap<String,Object>();
	    User userO=this.userDao.findByIdAndDel(bmO.getManagerId(),DELTYPE.NORMAL);
	    if(userO!=null){
	    	bmO.setManagerName(userO.getUserName());
		    bmO.setManagerIcon(userO.getIcon());
	    }
		map.put("data",bmO);
		return map;
	}
	public Map<String, Object> addPinYin() {
		List<BugModule> bugModules = this.bugModuleDao.findByDel(DELTYPE.NORMAL);
		for(BugModule bugModule : bugModules){
			bugModule.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(bugModule.getName()==null?"":bugModule.getName()));
			bugModule.setPinYinName(ChineseToEnglish.getPingYin(bugModule.getName()==null?"":bugModule.getName()));
		}
		this.bugModuleDao.save(bugModules);
		return this.getSuccessMap("affected "+bugModules.size());
	}
	
	
}
