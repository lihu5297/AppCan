package org.zywx.cooldev.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.INIT_DEMO_STATUS;
import org.zywx.cooldev.commons.Enums.INTEGRATE_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.USER_LEVEL;
import org.zywx.cooldev.commons.Enums.USER_STATUS;
import org.zywx.cooldev.commons.Enums.USER_TYPE;
import org.zywx.cooldev.dao.project.ProjectDao;
import org.zywx.cooldev.dao.project.ProjectMemberDao;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.builder.Setting;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.service.UserActiveToolService;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.util.MD5Util;
import org.zywx.cooldev.util.UserListWrapUtil;

/**
 * 用户处理控制器
 * @author yang.li
 * @date 2015-08-10
 *
 */
@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController {
	
	@Autowired
	private UserActiveToolService userActiveToolService;
	
	@Autowired
	private ProjectMemberDao projectMemberDao;
	
	@Autowired
	private ProjectDao projectDao;

	@Value("${user.icon}")
	private String icon;
	/**
	 * 大众版单独买协同开发
	 * 获取登录用户(线下版本,密码保存到本地)
	 * 企业版EMM3.3也将密码存在本地
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="/offline",method=RequestMethod.GET)
	public Map<String, Object> getUserLists(User user,String u,String p) {
//			String account = user.getAccount();
//			String password = user.getPassword();
//			password = MD5Util.MD5(password).toLowerCase();
			p = MD5Util.MD5(p).toLowerCase();
			User loginUser = this.userService.findUserByAccountAndPassword(u, p);
			if(loginUser != null && loginUser.getStatus().equals(USER_STATUS.NORMAL) && loginUser.getDel().equals(DELTYPE.NORMAL)) {
				if(StringUtils.isBlank(loginUser.getPinYinHeadChar()) || StringUtils.isBlank(loginUser.getPinYinName())){
					loginUser.setPinYinName(ChineseToEnglish.getPingYin(loginUser.getUserName()));
					loginUser.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(loginUser.getUserName()));
					userService.saveUser(loginUser);
				}
				Map<String, Object> res = new HashMap<>();
				Map<String,Integer> permissions = this.userAuthService.findUserAuth(loginUser.getId());
				//Map<String,Integer> permissions = new HashMap<String, Integer>();
				loginUser.setPassword("");
				res.put("object", loginUser);
				res.put("permissions", permissions);
				
				return this.getSuccessMap(res);
			} else {
				if(null==loginUser ||loginUser.getDel().equals(DELTYPE.DELETED)){
					return this.getFailedMap("用户名或者密码错误");
				}else if(loginUser.getStatus().equals(USER_STATUS.FORBIDDEN)){
					return this.getFailedMap("用户被禁用");
				}
				return this.getFailedMap("");
			}
	}
	
	/**
	 * (企业版)用
	 * MAS获取登录用户
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.GET)
	public Map<String, Object> getUserList(User user) {
			String account = user.getAccount();
			User loginUser = this.userService.findUserByAccountAndDel(account, DELTYPE.NORMAL);
			if(loginUser != null && loginUser.getStatus().equals(USER_STATUS.NORMAL) && loginUser.getDel().equals(DELTYPE.NORMAL)) {
				if(StringUtils.isBlank(loginUser.getPinYinHeadChar()) || StringUtils.isBlank(loginUser.getPinYinName())){
					loginUser.setPinYinName(ChineseToEnglish.getPingYin(loginUser.getUserName()));
					loginUser.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(loginUser.getUserName()));
					userService.saveUser(loginUser);
				}
				Map<String, Object> res = new HashMap<>();
				Map<String,Integer> permissions = this.userAuthService.findUserAuth(loginUser.getId());
				
				loginUser.setPassword("");
				res.put("object", loginUser);
				/**
				Map<String,Integer> permissions = new HashMap<String, Integer>();
				//如果是高级用户，则有创建工程和团队的权限
				if(loginUser.getUserlevel().compareTo(USER_LEVEL.ADVANCE)==0){
					permissions.put("project_create", 1);
					permissions.put("team_create", 1);
				}
				**/
				res.put("permissions", permissions);
				
				return this.getSuccessMap(res);
			} else {
				if(null==loginUser ||loginUser.getDel().equals(DELTYPE.DELETED)){
					return this.getFailedMap("用户没有权限");
				}else if(loginUser.getStatus().equals(USER_STATUS.FORBIDDEN)){
					return this.getFailedMap("用户被禁用");
				}else if(loginUser.getStatus().equals(USER_STATUS.AUTHSTR)){
					return this.getFailedMap("信息审核中");
				}else if(loginUser.getStatus().equals(USER_STATUS.UNPASS)){
					return this.getFailedMap("用户审核未通过");
				}
				return this.getFailedMap("");
			}
	}
	
	/**
	 * MAS大众版登录成功后获取loginUserId用
	 * SSO登录成功之后,查询协同用户信息,将返回的user对象的id放入header中 loginUserId
	 * 获取登录用户(线上版本,用户全部来自appcan,本地无密码)
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="/online",method=RequestMethod.GET)
	public Map<String, Object> getOnLineUser(String loginName) {
			User loginUser = this.userService.findUserByAccountAndDel(loginName, DELTYPE.NORMAL);
			if(loginUser != null && loginUser.getStatus().equals(USER_STATUS.NORMAL)) {
				if(StringUtils.isBlank(loginUser.getPinYinHeadChar()) || StringUtils.isBlank(loginUser.getPinYinName())){
					loginUser.setPinYinName(ChineseToEnglish.getPingYin(loginUser.getUserName()));
					loginUser.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(loginUser.getUserName()));
					userService.saveUser(loginUser);
				}
				Map<String, Object> res = new HashMap<>();
				Map<String,Integer> permissions = new HashMap<String, Integer>();
				res.put("object", loginUser);
				if(loginUser.getUserlevel().compareTo(USER_LEVEL.ADVANCE)==0){
					permissions.put("project_create", 1);
					permissions.put("team_create", 1);
				}
				res.put("permissions", permissions);
				
				return this.getSuccessMap(res);
			} else if(null!=loginUser && loginUser.getStatus().equals(USER_STATUS.FORBIDDEN)) {
				return this.getFailedMap("账号未启用");
			}else{
				return this.getFailedMap("用户不存在");
			}
	}
	
	/**
	 * 暂时没用到(让wyw看了MAS接口没调用这块)
	 * 注册用户<br>
	 * @param user
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.POST)
	public Map<String, Object> createUser(User user) {
		try {
			if(user.getIcon() == null || "".equals(user.getIcon())) {
				user.setIcon(icon);
			}
			this.userService.addUser(user);
			
			return this.getSuccessMap(user);

		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 编辑用户
	 * @param user
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(method=RequestMethod.PUT)
	public Map<String, Object> updateUser(
			User user,
			@RequestHeader(value="loginUserId") long loginUserId
			) {
		try {
			user.setId(loginUserId);
			int affected = this.userService.updateUser(user);
			Map<String, Integer> map = new HashMap<>();
			map.put("affected", affected);
			
			return this.getSuccessMap(map);

		} catch (Exception e) {
			return this.getFailedMap(e.getMessage());
		}
	}

	@ResponseBody
	@RequestMapping(value="/password", method=RequestMethod.PUT)
	public Map<String, Object> updatePassword(
			@RequestParam(value="previous") String previous,
			@RequestParam(value="current") String current,
			@RequestParam(value="repeat") String repeat,
			@RequestHeader(value="loginUserId") long loginUserId
			) {
		int affected = this.userService.updatePassword(previous, current, repeat, loginUserId);
		
		Map<String, Integer> map = new HashMap<>();
		map.put("affected", affected);
		
		return this.getSuccessMap(map);
	}
	
	/**
	 * 用户详情
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value="/{userId}", method=RequestMethod.GET)
	public Map<String, Object> getProjectList(@PathVariable(value="userId") long userId) {
		Map<String, Object> res = new HashMap<>();
		User user = this.userService.findUserById(userId);
		if(user != null) {
			user.setPassword("");
			res.put("object", user);
			//查询用户初始化权限
			Map<String,Integer> permissions = this.userAuthService.findUserAuth(user.getId());
			/**
			Map<String,Integer> permissions = new HashMap<String, Integer>();
			if(user.getUserlevel().compareTo(USER_LEVEL.ADVANCE)==0){
				permissions.put("project_create", 1);
				permissions.put("team_create", 1);
			}
			**/
			res.put("permissions", permissions);
			return this.getSuccessMap(res);
		} else {
			return this.getSuccessMap(new HashMap<>());
		}
	}	

	/**
	 * 从团队邀请成员,激活方法
	 * 激活用户,从未注册变为未认证,将该用户的团队成员类别从被邀请改为成员
	 * @user jingjian.wu
	 * @date 2015年9月11日 下午11:54:00
	 */
	/*@ResponseBody
	@RequestMapping(value="/active/{uuid}", method=RequestMethod.GET)
	public Map<String, Object> active(@PathVariable(value="uuid") String uuid,HttpServletResponse response) {
		try {
			log.info("===================================User Active :userId->"+uuid);
			UserActiveTool userActiveTool= userActiveToolService.findValueById(uuid);
			if(null==userActiveTool){
				return this.getFailedMap("param is not available");
			}
			this.userService.updateUserTypeByUserId(Long.parseLong(userActiveTool.getValue()), USER_TYPE.AUTHENTICATION);
			log.info("user type changed!");
			if(userActiveTool.getEntityType().equals("team")){
				this.teamService.updateMemberType(Long.parseLong(userActiveTool.getValue()),userActiveTool.getEntityId());
			}else if(userActiveTool.getEntityType().equals("project")){
				projectService.updateMemberType(Long.parseLong(userActiveTool.getValue()),userActiveTool.getEntityId());
			}
			log.info("teamMember type changed!");
			return this.getAffectMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.getFailedMap("");
	}*/
	
	/**
	 * 大众版
	 * 用户登录成功后,需要将他在被邀请的团队,项目里的成员,变为正式成员
	 * 如果是首次登录,为用户创建默认项目，流程，任务等
	 * @user jingjian.wu
	 * @date 2015年10月27日 下午8:21:41
	 */
	@ResponseBody
	@RequestMapping(value="/active", method=RequestMethod.POST)
	public Map<String, Object> active(HttpServletResponse response,@RequestHeader(value="loginUserId") Long loginUserId) {
		try {
			log.info("===================================User Active :userId->"+loginUserId);
			User user = this.userService.findUserById(loginUserId);
			
			if(null==loginUserId || null==user){
				return this.getFailedMap("param is not available");
			}
			
			boolean firstLogin = true;
			List<ProjectMember> listMember = projectMemberDao.findByUserIdAndType(loginUserId, PROJECT_MEMBER_TYPE.CREATOR);
			if(null!=listMember && listMember.size()>0){
				for(ProjectMember pm:listMember){
					Project p = projectDao.findOne(pm.getProjectId());
					if(null!=p){
						if(p!=null && p.getCategoryId()==19 && p.getName().equals("示例项目")){
							firstLogin = false;
							break ;
						}
					}
				}
			}
			/*
			 * 根据用户初始化状态判断是否需要初始化 
			 * ongoning、没有示例项目=====不需要初始化
			 * ongoning、有示例项目=====不需要初始化
			 * null、有示例项目=====不需要初始化
			 * null、没有示例项目=====需要初始化
			 * succeed======不需要初始化
			 */
			
			if(null!=user.getInitDemoStatus() && user.getInitDemoStatus().equals(INIT_DEMO_STATUS.ONGOING)){
				firstLogin = false;
			}else if(firstLogin){
				user.setInitDemoStatus(INIT_DEMO_STATUS.ONGOING);
				this.userService.addUser(user);
			}
			
			log.info("userController active judge firstLogin---->"+firstLogin);
			this.userService.updateUserTypeByUserId(loginUserId, USER_TYPE.AUTHENTICATION,firstLogin);
			//完成初始化 修改状态
			user.setInitDemoStatus(INIT_DEMO_STATUS.SUCCEED);
			this.userService.addUser(user);
			
			return this.getAffectMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.getFailedMap("");
	}
	
	/**企业版
	 * appcan导入用户<br>
	 * @param user
	 * @param request
	 * @param loginUserId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/import",method=RequestMethod.POST)
	public ModelAndView importUser(User user,HttpServletRequest request,@RequestParam(required=false)String tryAgain) {
		try {
			log.info(String.format(" import user from appcan : [%s]", user));
			//appcan没有进入 则不能进行登录冰导入用户到协同开发
			Setting setting = this.settingService.getSetting();
			if(setting.getSYSStatus().compareTo(INTEGRATE_STATUS.FORBIDDEN)==0){
				return this.getFailedModel("the website http://www.appcan.cn is not integrated to CooperationDevelopment");
			}
			
			return this.userService.updateOrImportUser(user,tryAgain);
			
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedModel(e.getMessage());
		}
	}
	
	/**
	 * 获取用户列表
	 * 应该是往团队或者项目里面添加人的时候,私有部署情况,直接选用户表中的所有记录
	 * @user jingjian.wu
	 * @date 2015年10月15日 下午8:37:37
	 */
	@ResponseBody
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public Map<String, Object> getUserList(HttpServletRequest request,@RequestParam(defaultValue="")String keyWords,
			//为了过滤项目和团队中已有的成员而添加的筛选字段
			@RequestParam(value="queryType",defaultValue="ALL")String queryType,//项目 PROJECT  团队TEAM
			@RequestParam(value="queryId",defaultValue="-1")Long queryId ,//对应project、team的id
			@RequestHeader(value="loginUserId",required=true) Long loginUserId
			) {
		try {
			long start = System.currentTimeMillis();
//			keyWords = new String(keyWords.getBytes("iso8859-1"),"utf-8");
			
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
				return this.getFailedMap( nfe.getMessage() );
			}

			Pageable pageable = new PageRequest(pageNo, pageSize, Direction.DESC, "id");
			
			log.info("search user from coopMan by keys:"+keyWords);
			Page<User> user = this.userService.findUserListByStatusAndDelAndKeyWords(USER_STATUS.NORMAL,DELTYPE.NORMAL,keyWords,queryType,queryId,pageable,loginUserId);
			List<User> users = user.getContent();
			UserListWrapUtil.setNullForPwdFromUserList(users);
			long end = System.currentTimeMillis();
			log.info("search user from userTable use time--->"+(end-start)+" ms");
			if(users != null) {
				Map<String, Object> message = new HashMap<>();
				message.put("list", users);
				message.put("total", user.getTotalElements());
				return this.getSuccessMap(message);
			} else {
				Map<String, Object> message = new HashMap<>();
				message.put("list", new ArrayList<>());
				message.put("total", 0);
				return this.getSuccessMap(new HashMap<>());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * MAS大众版
	 * 第一步先获取人在不在.
	 * 第二步:调用online
	 * 第三步:调用active
	 * 线上部署,通过loginName(邮箱),获取本地用户表中的用户信息,如果用户不在本地表中,去SSO中获取该用户插入到本地并返回.
	 * @user jingjian.wu
	 * @date 2015年10月26日 下午5:41:54
	 */
	@ResponseBody
	@RequestMapping(value="/fetchUser", method=RequestMethod.GET)
	public Map<String, Object> getUserForLoginName(String loginName) {
		try {
			User user = userService.saveUserIfNotExist(loginName);
			if(user != null) {
				return this.getSuccessMap(user);
			} else {
				return this.getFailedMap("用户不存在");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 线上部署,通过loginName(邮箱),获取本地用户表中的用户信息,如果用户不在本地表中,去SSO中获取该用户插入到本地并返回.
	 * @user jingjian.wu
	 * @date 2015年10月26日 下午5:41:54
	 */
	@ResponseBody
	@RequestMapping(value="/fetchUserList", method=RequestMethod.GET)
	public Map<String, Object> getUserForLoginName(@RequestParam(value="loginName")List<String> loginName) {
		try {
			int resultCount = userService.saveUserListIfNotExist(loginName);
			return this.getSuccessMap(resultCount);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * EMM创建协协同团队时候,将团队创建者的邮箱，手机号，姓名传过来,获取在协同里面的用户信息
	 * 如果此邮箱为appcan账号,直接返回appcan账号信息,并更新appcan信息到本地
	 * 如果此邮箱非appcan账号,则为协同创建这么一个账号
	 * @user jingjian.wu
	 * @date 2015年11月4日 下午6:01:03
	 */
	@ResponseBody
	@RequestMapping(value="/emmFetchUser", method=RequestMethod.GET)
	public Map<String, Object> eMMgetUserForLoginName(String loginName,String mobilePhone,String userName) {
		try {
			User user = userService.saveUserIfNotExist(loginName,mobilePhone,userName);
			return this.getSuccessMap(user);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	/**
	 * EMM创建协协同项目时候,将团队创建者的邮箱传过来,获取在协同里面的用户信息
	 * 如果此邮箱为appcan账号,直接返回appcan账号信息
	 * 如果此邮箱非appcan账号,则为协同创建这么一个账号
	 * @user haijun.cheng
	 * @date 2016年07月07日 
	 */
	@ResponseBody
	@RequestMapping(value="/emmProjectFetchUser", method=RequestMethod.GET)
	public Map<String, Object> emmProjectFetchUser(String currentLoginAccount) {
		try {
			User user = userService.saveUserIfNotExistByEmail(currentLoginAccount);
			return this.getSuccessMap(user);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * 
	 * @describe 获取验证码	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2016年1月12日 下午3:48:00	<br>
	 * @param request
	 * @param loginUserId
	 * @param email
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/bind/identifyCode",method=RequestMethod.GET)
	public Map<String, Object> getIdentifyCode(HttpServletRequest request,
			@RequestHeader(value="loginUserId")long loginUserId,
			@RequestParam(value="email")String email,
			@RequestParam(value="type",defaultValue="bind")String type){
		
		Map<String,Object> map = this.userService.createIdentityCode(loginUserId,email,type);
		
		return map;
	}
	
	/**
	 * 
	 * @describe 绑定邮箱	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2016年1月12日 下午3:48:33	<br>
	 * @param request
	 * @param loginUserId
	 * @param email
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/bind/email",method=RequestMethod.PUT)
	public Map<String, Object> bindEmail(HttpServletRequest request,
			@RequestHeader(value="loginUserId")long loginUserId,
			@RequestParam(value="email")String email,
			@RequestParam(value="code")String code){
		
		Map<String,Object> map = this.userService.updateBindEmail(loginUserId,email,code);
		
		return map;
	}
	
	/**
	 * 
	 * @describe 解绑邮箱	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2016年1月12日 下午3:48:50	<br>
	 * @param request
	 * @param loginUserId
	 * @param email
	 * @return  <br>
	 * @returnType Map<String,Object>
	 *
	 */
	@ResponseBody
	@RequestMapping(value="/unbind/email",method=RequestMethod.PUT)
	public Map<String, Object> unBindEmail(HttpServletRequest request,
			@RequestHeader(value="loginUserId")long loginUserId,
			@RequestParam(value="email")String email,
			@RequestParam(value="code")String code){
		
		Map<String,Object> map = this.userService.updateUnbindEmail(loginUserId,email,code);
		return map;
	}
	
	public static void main(String[] args) {
		System.out.println(MD5Util.MD5("abc").toLowerCase());
	}
	
	@ResponseBody
	@RequestMapping(value="/update/pinyin",method=RequestMethod.GET)
	public Map<String, Object> updatePinyin(HttpServletRequest request){
		
		Map<String,Object> map = this.userService.updatePinyin();
		return map;
	}
	
	
	/**(当用户被禁用或者审核未通过等非正常状态的用户,一律返回用户不存在)
	 * 提供给正益工场,返回某个人创建的和管理的团队下面的所有的正式用户
	 * @user jingjian.wu
	 * @date 2016年3月7日 上午11:11:01
	 */
	@ResponseBody
	@RequestMapping(value="/belongSomeOneMgrTeam",method=RequestMethod.GET)
	public Map<String, Object> belongSomeOneMgrTeam(HttpServletRequest request,String account,String keyWords){
		
		Map<String, Object> map = null;
		try {
			User user = userService.findUserByAccountAndDel(account, DELTYPE.NORMAL);
			if(null==user || user.getDel().equals(DELTYPE.DELETED)){
				return this.getFailedMap("用户不存在");
			}
			if(null==user.getStatus() || user.getStatus().equals(USER_STATUS.AUTHSTR)){
				return this.getFailedMap("用户不存在");
			}
			if( user.getStatus().equals(USER_STATUS.FORBIDDEN)){
				return this.getFailedMap("用户不存在");
			}
			if( user.getStatus().equals(USER_STATUS.UNPASS)){
				return this.getFailedMap("用户不存在");
			}
			if(null==keyWords){
				keyWords="%%";
			}else{
				keyWords="%"+keyWords+"%";
			}
			map = this.userService.findUsersBelongSomeOneMgrTeam(user.getId(),keyWords);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		return map;
	}
	
	
	/**
	 * 根据项目ID找出项目下的前10个人(关键字匹配),如果没有项目ID则找出协同中根据关键字匹配的前10个人
	 * @user jingjian.wu
	 * @date 2016年3月7日 下午5:25:03
	 */
	@ResponseBody
	@RequestMapping(value="/allUser4Project",method=RequestMethod.GET)
	public Map<String, Object> allUser4Project(HttpServletRequest request,Long projectId,String keyWords){
		
		try {
			if(null==keyWords){
				keyWords="%%";
			}else{
				keyWords="%"+keyWords+"%";
			}
			//ios机器自动生成单引号，过滤单引号
			keyWords=keyWords.replaceAll("'", "");
			List<User> users = userService.findUser(projectId, keyWords);
			return this.getSuccessMap(users);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	
	/**
	 * emm3.3创建团队之前,需要调用此接口,根据uniqueFiled获取到userId,然后才能创建团队时候传过来loginUserId
	 * @param loginName
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emm3FetchUser", method=RequestMethod.GET)
	public Map<String, Object> emm3GetUserIdForLoginName(String uniqueField) {
		try {
			User user = userService.findUserByAccountAndDel(uniqueField, DELTYPE.NORMAL);
			if(null!=user){
				return this.getSuccessMap(user);
			}
			return this.getFailedMap("not exist");
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
	}
	
	/**
	 * IDE客户端登录,判断账号密码是否正确
	 * @param name   账号
	 * @param password   密码
	 * @return
	 */
	@ResponseBody
//	@RequestMapping(value="/pwdVerify",method=RequestMethod.GET)
	@RequestMapping(value="/confirmInfo",method=RequestMethod.GET)
	public Map<String, Object> getUserLists(String u,String p) {
		p = MD5Util.MD5(p).toLowerCase();
		Map<String, Object> ret = new HashMap<>();
		User loginUser = this.userService.findUserByAccountAndPassword(u, p);
		if(loginUser != null && loginUser.getStatus().equals(USER_STATUS.NORMAL) && loginUser.getDel().equals(DELTYPE.NORMAL)) {
			ret.put("retCode", "ok");
			ret.put("message", "校验正确");
		} else {
			ret.put("retCode", "fail");
			ret.put("message", "校验失败");
		}
		return ret;
	}
	
	@ResponseBody
	@RequestMapping(value="/innerPwdChange",method=RequestMethod.POST)
	public Map<String, Object> innerPwdChange(@RequestParam("loginName") String loginName,@RequestParam("newPwd") String newPwd,
			@RequestParam("oldPwd") String oldPwd) {
		Map<String, Object> ret = new HashMap<>();
		try {
			ret= userService.updatePwdChange(loginName, oldPwd,newPwd);
		} catch (Exception e) {
			e.printStackTrace();
			return this.getFailedMap(e.getMessage());
		}
		 
		return ret;
	}
}
