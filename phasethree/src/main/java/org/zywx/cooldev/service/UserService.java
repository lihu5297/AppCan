package org.zywx.cooldev.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DYNAMIC_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.NOTICE_MODULE_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.PROJECT_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_STATUS;
import org.zywx.cooldev.commons.Enums.PROJECT_TYPE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.commons.Enums.TEAMREALTIONSHIP;
import org.zywx.cooldev.commons.Enums.USER_JOINPLAT;
import org.zywx.cooldev.commons.Enums.USER_LEVEL;
import org.zywx.cooldev.commons.Enums.USER_STATUS;
import org.zywx.cooldev.commons.Enums.USER_TYPE;
import org.zywx.cooldev.commons.Enums.UserGender;
import org.zywx.cooldev.dao.filialeInfo.FilialeInfoDao;
import org.zywx.cooldev.entity.IdentityCode;
import org.zywx.cooldev.entity.User;
import org.zywx.cooldev.entity.app.App;
import org.zywx.cooldev.entity.auth.Role;
import org.zywx.cooldev.entity.filialeInfo.FilialeInfo;
import org.zywx.cooldev.entity.process.Process;
import org.zywx.cooldev.entity.process.ProcessConfig;
import org.zywx.cooldev.entity.project.Project;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.task.Task;
import org.zywx.cooldev.entity.task.TaskGroup;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.util.HttpUtil;
import org.zywx.cooldev.util.MD5Util;
import org.zywx.cooldev.util.mail.base.MailSenderInfo;

import net.sf.json.JSONObject;

@Service
public class UserService extends BaseService{
	
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
	
	@Autowired
	private ResourcesService resourceService;
	
	@Autowired
	protected ProjectService projectService;
	
	@Autowired
	protected BaseService baseService;
	
	@Autowired
	protected TeamService teamService;
	
	@Value("${user.icon}")
	private String userIcon;
	
	@Value("${sso.host}")
	private String ssoHost;

	@Autowired
	private AppService appService;

	@Autowired
	private DynamicService dynamicService;
	@Autowired
	private ProcessConfigService processConfigService;
	
	@Autowired
	private ProcessService processService;
	
	@Autowired
	private NoticeService noticeService;

	@Autowired
	private TaskService taskService;
	 

	/**
	 * 根据团队id标识,和团队的关系(1创建,2参与,3受邀)查询该团队下面的用户列表
	    * @Title: findUserListByids
	    * @Description:
	    * @param @param typeIds  如果是团队下面的已有用户用(0,1);如果是团队下面的受邀还没同意的用户用2 
	    * @param @param teamId
	    * @param @return    参数
	    * @return List<User>    返回类型
		* @user wjj
		* @date 2015年8月11日 下午5:17:02
	    * @throws
	 */
	public Page<User> findUserListByTeamIdAndRel(List<TEAMREALTIONSHIP> typeIds,long teamId,Pageable pageable,String type,String keywords){
		//团队成员列表显示禁用的，其他只显示正常的
		List<USER_STATUS> status=new ArrayList<USER_STATUS>();
		status.add(USER_STATUS.NORMAL);
		if(type!=null&&type.equals("memberList")){
			status.add(USER_STATUS.FORBIDDEN);
		}
		if(StringUtils.isNotBlank(keywords)){
			return userDao.findUserListByTeamIdAndByKeywords(typeIds, teamId,DELTYPE.NORMAL,keywords,pageable);
		}else{
			return userDao.findUserListByTeamId(typeIds, teamId,DELTYPE.NORMAL,pageable);
		}
	}
	
	
	public Page<User> findUserListByTeamIdAndRelAndSearch(List<TEAMREALTIONSHIP> typeIds,long teamId,String search,Pageable pageable){
		return userDao.findUserByTeamIdSearch(typeIds, teamId,search,DELTYPE.NORMAL,pageable);
	}
	
	/**
	 * 根据小组id标识,及人员和团队的关系(1创建,2参与,3受邀)查询该小组下面的用户列表
	    * @Title: findUserListByGroupId
	    * @Description: 
	    * @param @param typeIds  如果是团队小组下面的已有用户用(1,2);如果是团队小组下面的受邀还没同意的用户用3
	    * @param @param teamId
	    * @param @return    参数
	    * @return List<User>    返回类型
		* @user wjj
		* @date 2015年8月11日 下午5:22:43
	    * @throws
	 */
	public Page<User> findUserListByGroupIdAndRel(List<TEAMREALTIONSHIP> typeIds,long groupId,long teamId,Pageable pageable){
		return this.userDao.findUserListByGroupIdAndTeamId(typeIds, groupId,teamId,DELTYPE.NORMAL,pageable);
	}
	
	public void saveUser(User user){
		userDao.save(user);
	}

	
	/**
	    * @Title: addUser
	    * @Description: 增加用户
	    * @param @param user
	    * @param @return    参数
	    * @return User    返回类型
		* @user jingjian.wu
		* @date 2015年8月13日 下午4:21:03
	    * @throws
	 */
	public User addUser(User user){
		// 填充默认值
		if(user.getStatus() == null) {
			user.setStatus(USER_STATUS.NORMAL);
		}
		if(user.getType() == null) {
			user.setType(USER_TYPE.NOREGISTER);
		}
		if(user.getEmail() == null) {
			user.setEmail("");
		}
		if(user.getGender() == null) {
			user.setGender(UserGender.UNKNOWN);
		}
		if(StringUtils.isNotBlank(user.getUserName())) {
			user.setPinYinName(ChineseToEnglish.getPingYin(user.getUserName()));
			user.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(user.getUserName()));
			user.setGender(UserGender.UNKNOWN);
		}
		
		return this.userDao.save(user);
	}
	
	/**
	 * 团队中查看成员信息时候用到了
	    * @Title: findUserById
	    * @Description: 根据用户ID查找对应的用户信息
	    * @param @param userId
	    * @param @return    参数
	    * @return User    返回类型
		* @user jingjian.wu
		* @date 2015年8月15日 下午8:20:31
	    * @throws
	 */
	public User findUserById(long userId){
		User user = this.userDao.findOne(userId);
		if(user!=null&&user.getFilialeId()>0){
			FilialeInfo filialeInfo = filialeInfoDao.findOne(user.getFilialeId());
			user.setFilialeName(filialeInfo.getFilialeName());
			user.setFilialeCode(filialeInfo.getFilialeCode());
			return user;
		}
		return user;
	}
	
	/**
	 * 根据账户密码查询用户<br>
	 * 使用单点登录之前，临时采用的登录认证方案
	 * 
	 * @author yang.li
	 * @date 2015年8月20日
	 * 
	 * @param account
	 * @param password
	 * @return
	 */
	public User findUserByAccountAndPassword(String account, String password) {
		List<User> userList = this.userDao.findByAccountAndPasswordAndDel(account, password, DELTYPE.NORMAL);
		if(userList != null && userList.size() > 0) {
			User user = userList.get(0);
			FilialeInfo filiale = filialeInfoDao.findOne(user.getFilialeId());
			if(filiale != null){
				user.setFilialeCode(filiale.getFilialeCode());
				user.setFilialeName(filiale.getFilialeName());
			}
			return user;
		} else {
			return null;
		}
	}
	
	public User findUserByAccountAndDel(String account, DELTYPE del) {
		User user= this.userDao.findByAccountAndDel(account, DELTYPE.NORMAL);
		if(user != null && user.getFilialeId() > 0){
			FilialeInfo filiale = filialeInfoDao.findOne(user.getFilialeId());
			if(filiale != null){
				user.setFilialeCode(filiale.getFilialeCode());
				user.setFilialeName(filiale.getFilialeName());
			}
		}
		return user;
	}
	
	/**
	 * 将用户从被邀请状态,变为正式成员
	 *synchronized 防止同一个人并发创建多个示例项目
	 * @throws Exception 
	 * @user jingjian.wu
	 * @date 2015年9月11日 下午11:18:46
	 */
	public synchronized boolean updateUserTypeByUserId(long userId,USER_TYPE userType,boolean firstLogin) throws Exception{
		try {
				Thread current = Thread.currentThread(); 
				log.info("current :"+ current.toString()+",firstLogin:"+firstLogin);
				User user = this.userDao.findOne(userId);
				if(user==null){
					return false;
				}else{
					user.setType(userType);
					userDao.save(user);
				}
				
				teamService.updateMemberType(userId);
				projectService.updateMemberType(userId);
				/**
				 * 登录时候,同意为正式成员之后,顺便需要从线上更新信息到本地
				 */
				saveUserIfNotExist(user.getAccount());
				/**
				 * 首次登录要给用户创建默认的项目,流程,任务等等
				 */
			
				if(firstLogin){
					saveInit(userId);
				}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return true;
	}
	
	public int updateUser(User user) {
		//设置默认头像
		if(user.getIcon() == null || "".equals(user.getIcon())) {
			user.setIcon(userIcon);
		}
		String settings = "";
		if(user.getIcon() != null) {
			settings += String.format(",icon='%s'", user.getIcon());
		}
		if(user.getUserName() != null) {
			settings += String.format(",userName='%s'", user.getUserName());
			settings += String.format(",pinYinName='%s'", ChineseToEnglish.getPingYin(user.getUserName()));
			settings += String.format(",pinYinHeadChar='%s'",ChineseToEnglish.getPinYinHeadChar(user.getUserName()));
			resourceService.updateUserNameByUserId(user.getId(), user.getUserName());
		}
		if(user.getCellphone() != null) {
			settings += String.format(",cellphone='%s'", user.getCellphone());
		}
		if(user.getQq() != null) {
			settings += String.format(",qq='%s'", user.getQq());
		}
		if(user.getAddress() != null) {
			settings += String.format(",address='%s'", user.getAddress());
		}
		if(user.getEmail() != null) {
			settings += String.format(",email='%s'", user.getEmail());
		}
		if(user.getGender() != null) {
			settings += String.format(",gender=%d", user.getGender().ordinal());
		}
		
		if(settings.length() > 0) {
			settings = settings.substring(1);
			String sql = "update T_USER set " + settings + " where id=" + user.getId();
			return this.jdbcTpl.update(sql);
			
		} else {
			return 0;
		}
	}

	public int updatePassword(String previous, String current, String repeat, long loginUserId) {
		if(previous == null || current == null || repeat == null) {
			return 0;
		}
		if(previous.equals(current)) {
			return 0;
		}
		if( ! current.equals(repeat) ) {
			return 0;
		}
		User user = userDao.findOne(loginUserId);
		if(user == null) {
			return 0;
		}
		
		String md5Previous = MD5Util.MD5(previous).toLowerCase();

		if( ! user.getPassword().equals(md5Previous)) {
			return 0;
		}
		// 原密码正确，next
		
		String md5Current = MD5Util.MD5(current).toLowerCase();
		
		String sql = String.format("update T_USER set password='%s' where id=%d", md5Current, loginUserId);
		return this.jdbcTpl.update(sql);


	}

	
	public Page<User> findUserListByStatusAndDelAndKeyWords(USER_STATUS status,DELTYPE del,String keyWords,String queryType,Long queryId,Pageable pageable, Long loginUserId) {
		Page<User> user = null;
		User one = userDao.findOne(loginUserId);
		if("PROJECT".equals(queryType)){
			if(one!=null){
				if(one.getFilialeId()==1){//往项目添加成员时,判断是否总部成员
					user = this.userDao.findByDelAndStatusAndKeyWordsAndProject(del,status,"%"+keyWords+"%",queryId,pageable);
				}else{
					user = this.userDao.findByDelAndStatusAndKeyWordsAndProjectAndFilialeId(del,status,"%"+keyWords+"%",queryId,pageable,one.getFilialeId());
				}
			}
		}else if("TEAM".equals(queryType)){//往团队添加成员时,判断是否总部成员
			if(one!=null){
				if(one.getFilialeId()==1){
					user = this.userDao.findByDelAndStatusAndKeyWordsAndTeam(del,status,"%"+keyWords+"%",queryId,pageable);
				}else{
					user = this.userDao.findByDelAndStatusAndKeyWordsAndTeamAndfilialeId(del,status,"%"+keyWords+"%",queryId,pageable,one.getFilialeId());
				}
			}
		}else{
			user = this.userDao.findByDelAndStatusAndKeyWords(del,status,"%"+keyWords+"%",pageable);
		}
		return user;
	}

	public List<User> findUserByIds(List<Long> userIds){
		return userDao.findByIdIn(userIds);
	}
	
	/**
	 * 根据邮箱获取本地用户的信息,如果本地不存在,去SSO找到该用户插入本地数据库
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user jingjian.wu
	 * @date 2015年10月26日 下午5:14:12
	 */
	public User saveUserIfNotExist(String loginName) throws ClientProtocolException, IOException{
		log.info("loginSuccess--->loginName:"+loginName);
		User u =userDao.findByAccountAndDel(loginName, DELTYPE.NORMAL);
		if(null!=u){
			User tmpUser = getLocalOrAppCanUser(loginName);
			if(null!=tmpUser){
				if(null!=u.getUserName() && null!=tmpUser.getUserName() && !u.getUserName().equals(tmpUser.getUserName())){
					resourceService.updateUserNameByUserId(u.getId(), tmpUser.getUserName());
				}
				tmpUser.setId(u.getId());
				tmpUser.setBindEmail(u.getBindEmail());
				userDao.save(tmpUser);
				return tmpUser;
			}
			return u;
		}else{
			User tmpUser = getLocalOrAppCanUser(loginName);
			if(null!=tmpUser){
				userDao.save(tmpUser);
				return tmpUser;
			}
			return null;
		}
	}
	
	public User saveUserIfNotExist(String loginName,String mobilePhone,String userName) throws ClientProtocolException, IOException{
		User u =userDao.findByAccountAndDel(loginName, DELTYPE.NORMAL);
		if(null!=u){
			User tmpUser = getLocalOrAppCanUser(loginName);
			if(null!=tmpUser){
				tmpUser.setId(u.getId());
				tmpUser.setBindEmail(u.getBindEmail());
				userDao.save(tmpUser);
				return tmpUser;
			}
			return u;
		}else{
			User tmpUser = getLocalOrAppCanUser(loginName);
			if(null!=tmpUser){
				userDao.save(tmpUser);
				return tmpUser;
			}else{
				User usr = new User();
				usr.setAccount(loginName);
				usr.setEmail(loginName);
				usr.setBindEmail(loginName);
				usr.setCellphone(mobilePhone);
				usr.setUserName(userName);
				usr.setUserlevel(USER_LEVEL.ADVANCE);
				usr.setType(USER_TYPE.NOREGISTER);
				userDao.save(usr);
				return usr;
			}
		}
	}
	
	
	
	/**
	 * 通过登录账号获取appcan的邮箱账号,获取协同用户,如果协同不存在此用户,则从appcan获取该用户
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user jingjian.wu
	 * @date 2015年10月27日 下午5:56:01
	 */
	public User getLocalOrAppCanUser(String loginName) throws ClientProtocolException, IOException{
		String str = HttpUtil.httpGet(ssoHost+"?loginName="+loginName);
		log.info("sso Info-->"+str);
		if(!StringUtils.isBlank(str)){
			str = str.replace("(", "");
			str = str.replace(")", "");
		}
		JSONObject sinupJson = JSONObject.fromObject(str);
		//SSO注册用户失败
		if ("fail".equals(sinupJson.getString("retCode"))) {
			log.info("reg sso return ==> "+sinupJson.getString("retMsg"));
			return null;
		}else{
			String userStr = sinupJson.getString("retData");
			JSONObject userObj = JSONObject.fromObject(userStr);
			String qq = null;
			if(userObj.containsKey("qq")){
				qq = userObj.getString("qq");
			}
			String mobile_phone =null;
			if(userObj.containsKey("mobile_phone")){
				mobile_phone = userObj.getString("mobile_phone");
			}
			String user_name = null;
			if(userObj.containsKey("user_name")){
				user_name = userObj.getString("user_name");
			}
			String user_pic = null;
			if(userObj.containsKey("user_pic")){
				user_pic = userObj.getString("user_pic");
			}
			//"province":"北京","city":"市辖区","area":"西城区","street":"第三段"
			String province = null;
			if(userObj.containsKey("province")){
				province = userObj.getString("province");
			}
			String city = null;
			if(userObj.containsKey("city")){
				city = userObj.getString("city");
			}
			String area = null;
			if(userObj.containsKey("area")){
				area = userObj.getString("area");
			}
			String street = null;
			if(userObj.containsKey("street")){
				street = userObj.getString("street");
			}
			
			String address =  (province==null?"":province+"-") + (city==null?"":city+"-") + (area==null?"":area+"-") + (street==null?"":street);
			
			User user = new User();
			user.setAccount(loginName);
			if(null!=mobile_phone && !"".equals(mobile_phone)){
				user.setCellphone(mobile_phone);
			}
			user.setEmail(loginName);
			user.setBindEmail(loginName);
			if(null!=user_pic && !"".equals(user_pic)){
				user.setIcon(user_pic);
			}
			if(null!=user_name && !"".equals(user_name)){
				user.setUserName(user_name);
				user.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(user.getUserName()==null?"":user.getUserName()));
				user.setPinYinName(ChineseToEnglish.getPingYin(user.getUserName()==null?"":user.getUserName()));
			}
			if(null!=qq && !"".equals(qq)){
				user.setQq(qq);
			}
			user.setAddress(address);
			user.setJoinPlat(USER_JOINPLAT.APPCAN);
			user.setStatus(USER_STATUS.NORMAL);//审核通过
			user.setUserlevel(USER_LEVEL.ADVANCE);
			user.setType(USER_TYPE.AUTHENTICATION);
			//昵称
			user.setNickName(userObj.getString("nickname"));
			return user;
		}
	}
	
	/**
	 * 协同人员第一次进入协同的时候,需要创建协同开发里面的默认项目
	 *  用户第一次进入协同开发，需要按照当前日期创建一个默认项目。
       项目名称：默认项目
       项目描述：这是一个默认项目。
       项目类型：个人项目
       项目类别：参考
       项目创建者：当前进入协同的用户
       项目管理员：无
       项目成员：无
       项目观察员：无
       项目设置：允许编辑项目名称、描述，允许转让项目、删除项目
       项目成员：允许通过邮箱邀请成员
       项目应用：以第一次进入协同的日期，创建一个默认的移动应用，支持代码、版本功能，可以删除。
       应用名称：默认应用
       应用描述：这是一个默认的移动应用
       应用git地址：显示一个git地址
       项目流程：以第一次进入协同的日期，分别创建8个官方定义的流程阶段，每个阶段周期为一周，开始/结束时间依次累计，负责人参与人都是当前用户，权重依次为：10％，10％，10％，30％，20％，10％，10％，相关资源为空。
       项目任务：以第一次进入协同的日期，分别创建3条待进行任务
       1）任务标题：这是一条正常任务；
            描述：这是一条正常任务；
            项目：默认项目
            流程阶段：项目计划
            应用：不关联
            优先级：正常；
            重复：不重复；
            截止日期：一周后；
            标签：无；
            相关资源：无；
            完成进度：0%；
            负责人和参与人：当前用户；
            动态：用户名（当前用户）创建了 xxx任务
            评论：无
       2）任务标题：这是一条即将延迟的任务；
            描述：这一条即将延迟的任务；
            项目：默认项目
            流程阶段：项目计划
            应用：不关联
            优先级：正常；
            重复：不重复；
            截止日期：明天；
            标签：无；
            相关资源：无；
            完成进度：0%；
            负责人和参与人：当前用户；
            动态：用户名（当前用户）创建了 xxx任务
            评论：无
       3）任务标题：这是一条己延迟任务；（待定。。。）
       项目讨论：无
       项目代码：默认移动应用代码，目录结构按照IDE创建默认项目的代码结构，可以发布或生成测试包
       项目版本：无
       项目引擎、插件：无
       项目文档：无
       项目资源：无
       项目动态：xx时间（第一次进入协同的时间）用户名（当前用户）创建了项目；xx时间xxx创建了应用；xx时间xxx创建了流程阶段；xx时间xxx创建了任务。
       团队：无
	 * @throws Exception 
	 * @user jingjian.wu
	 * @date 2015年11月2日 下午6:54:11
	 */
	public void saveInit(Long loginUserId) throws Exception{
		log.info("init users -- >"+loginUserId);
		User validUser = userDao.findOne(loginUserId);
		/*User validUser1 = userDao.findOne(loginUserId);
		log.info("-->"+validUser);
		log.info("-->"+validUser1);
		log.info("-->"+validUser.equals(validUser1));
		log.info("-->"+(validUser==validUser1));*/
		if(null==validUser){
			return;
		}
		
//		synchronized(this) {
			boolean firstLogin = true;
			List<ProjectMember> listMember = projectMemberDao.findByUserIdAndType(loginUserId, PROJECT_MEMBER_TYPE.CREATOR);
			if(null!=listMember && listMember.size()>0){
				log.info("------------listMember.size()-->"+listMember.size());
				for(ProjectMember pm:listMember){
					Project p = projectDao.findOne(pm.getProjectId());
					if(null!=p){
						log.info("created project name -- >"+p.getName()+",p.getCategoryId()-->"+p.getCategoryId());
						if(p!=null && p.getCategoryId()==19 && p.getName().equals("示例项目")){
							firstLogin = false;
							return ;
						}
						log.info("judge firstLogin---->"+firstLogin);
					}
				}
			}
			if(!firstLogin){
				return ;
			}
//		}
		log.info("create default project. for UserId-->"+loginUserId+",firstLogin-->"+firstLogin);
		Project project = new Project();
		project.setName("示例项目");
		project.setDetail("这是一个示例项目");
		project.setType(PROJECT_TYPE.PERSONAL);
		project.setCategoryId(19);//类型为参考,固定的
		project.setBizLicense(PROJECT_BIZ_LICENSE.NOT_AUTHORIZED);
		project.setStatus(PROJECT_STATUS.ONGOING);
		project.setTeamId(-1);
		
		this.projectService.addProject(project);
		ProjectMember member = new ProjectMember();
		member.setUserId(loginUserId);
		member.setProjectId(project.getId());
		member.setType(PROJECT_MEMBER_TYPE.CREATOR);
		this.projectService.saveProjectMember(member,validUser);
		ProjectAuth auth = new ProjectAuth();
		auth.setMemberId(member.getId());
		auth.setRoleId(Cache.getRole(ENTITY_TYPE.PROJECT+"_"+ROLE_TYPE.CREATOR).getId());
		this.projectService.saveProjectAuth(auth);
		if(null == project.getType() || project.getType().compareTo(PROJECT_TYPE.TEAM)!=0){
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROJECT_ADD, project.getId(), project.getName());
		}else{
			this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TEAM_CREATE_PRJ, project.getId(), project.getName());
		}
		
		
		App app = new App();
		app.setName("示例应用");
		app.setDetail("这是一个示例移动应用");
		app.setAppType(0l);
		app.setUserId(loginUserId);
		app.setProjectId(project.getId());
		this.appService.addApp(app, loginUserId);
		//添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.APP_ADD, app.getProjectId(), new Object[]{app});
		
		
		List<ProcessConfig> listProcessConfig = processConfigService.getEnableProcessConfigList();
		Calendar cal = Calendar.getInstance();
		int processSeq = 1;
		User user = findUserById(loginUserId);
		if(null!=listProcessConfig && listProcessConfig.size()>0){
			Process taskProcess = new Process();//在此流程下创建任务
			for(ProcessConfig processConfig:listProcessConfig){
				//流程负责人
				Long leaderUserId=loginUserId;
				//流程参与人
				List<Long> memberUserIdList=new ArrayList<Long>();
				memberUserIdList.add(loginUserId);
				//资源
				List<Long> resourceIdList=null;
				
				
				Process process = new Process();
				process.setCreatedAt(new Timestamp(cal.getTimeInMillis()));
				process.setUpdatedAt(new Timestamp(cal.getTimeInMillis()));
				process.setName(processConfig.getName());
//				process.setDetail("");
				switch (processSeq) {
				case 1:
					process.setWeight(10);
					break;
				case 2:
					process.setWeight(10);
					break;
				case 3:
					process.setWeight(10);
					break;
				case 4:
					process.setWeight(30);
					break;
				case 5:
					process.setWeight(20);
					break;
				case 6:
					process.setWeight(10);
					break;
				case 7:
					process.setWeight(10);
					break;
				default:
					break;
				}
				
				process.setProjectId(project.getId());
				process.setStartDate(cal.getTime());
				cal.add(Calendar.DATE, 7);
				process.setEndDate(cal.getTime());
				cal.add(Calendar.DATE, 1);
				
				Process savedP = this.processService.createProcess(process, loginUserId, leaderUserId, memberUserIdList, resourceIdList);
				
				//添加动态
				this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.PROCESS_CREATE, savedP.getProjectId(), new Object[]{savedP.getName()});
				
				
				if(loginUserId!=leaderUserId){
					//添加通知
					noticeService.addNotice(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.PROCESS_ADD_LEADER, new Object[]{user,savedP});
				}
				memberUserIdList.remove(loginUserId);
				//添加通知
				this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.PROCESS_ADD_MEMBER, new Object[]{user,savedP});
				
				
				//创建任务
				
				if(processSeq==1){//项目计划流程
					
					taskProcess = savedP;
					/*Calendar calT1 = Calendar.getInstance();
					calT1.add(Calendar.DATE, 7);
					createTask(loginUserId, memberUserIdList, savedP, user,"这是一条正常任务",new Timestamp(System.currentTimeMillis()),calT1.getTime(),project.getId());	
					
					calT1 = Calendar.getInstance();
					calT1.add(Calendar.DATE, 1);
					createTask(loginUserId, memberUserIdList, savedP, user,"这是一条即将延迟的任务",new Timestamp(System.currentTimeMillis()),calT1.getTime(),project.getId());
					
					calT1 = Calendar.getInstance();
					calT1.add(Calendar.DATE, -3);
					createTask(loginUserId, memberUserIdList, savedP, user,"这是一条己延迟任务",new Timestamp(calT1.getTimeInMillis()),calT1.getTime(),project.getId());
					*/
				}
				
				processSeq++;
			}
			
			//流程创建完的时候,再去创建任务,这样查看动态的时候,就不会出现,流程和任务穿插的现象
			
			Calendar calT1 = Calendar.getInstance();
			calT1.add(Calendar.DATE, 7);
			createTask(loginUserId, new ArrayList<Long>(), taskProcess, user,"这是一条正常任务",new Timestamp(System.currentTimeMillis()),calT1.getTime(),project.getId());	
			
			calT1 = Calendar.getInstance();
			calT1.add(Calendar.DATE, 1);
			createTask(loginUserId, new ArrayList<Long>(), taskProcess, user,"这是一条即将延迟的任务",new Timestamp(System.currentTimeMillis()),calT1.getTime(),project.getId());
			
			calT1 = Calendar.getInstance();
			calT1.add(Calendar.DATE, -3);
			createTask(loginUserId, new ArrayList<Long>(), taskProcess, user,"这是一条己延迟任务",new Timestamp(calT1.getTimeInMillis()),calT1.getTime(),project.getId());
			
			log.info("create default project over for userId---->"+loginUserId);
		}
		
		
	}
	
	
	private void createTask(long loginUserId,List<Long> memberUserIdList,Process savedP,User user,String detail,Timestamp createAt,Date deadLine,Long projectId) throws ParseException{
		long leaderUserId=loginUserId;
		memberUserIdList=new ArrayList<Long>();
		memberUserIdList.add(loginUserId);
		Task task = new Task();
		task.setProjectId(projectId);
		task.setDetail(detail);
		task.setProcessId(savedP.getId());
		task.setDeadline(deadLine);
		task.setCreatedAt(createAt);
		TaskGroup tg = taskGroupDao.findByProjectIdAndName(projectId, "待进行");
		task.setGroupId(tg.getId());
		this.taskService.addTask(task, null, null, leaderUserId, memberUserIdList, loginUserId);

		//添加动态
		this.dynamicService.addPrjDynamic(loginUserId, DYNAMIC_MODULE_TYPE.TASK_CREATE, savedP.getProjectId(), new Object[]{task});
		//添加通知
		Calendar calOther = Calendar.getInstance();
		if((calOther.getTimeInMillis()-task.getDeadline().getTime())/(24L * 60 * 60 * 1000)<=2){
			this.noticeService.addNotice(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.TASK_ADD_LEADER_WARNNING, new Object[]{user,task});
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_MEMBER_WARNNING, new Object[]{user,task});
			//发送邮件
			this.baseService.sendEmail(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.TASK_ADD_LEADER_WARNNING, new Object[]{user,task});
			this.baseService.sendEmail(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_MEMBER_WARNNING, new Object[]{user,task});
		}else{
			this.noticeService.addNotice(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.TASK_ADD_LEADER, new Object[]{user,task});
			this.noticeService.addNotice(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_MEMBER, new Object[]{user,task});
			//发送邮件
			this.baseService.sendEmail(loginUserId, memberUserIdList.toArray(new Long[]{}), NOTICE_MODULE_TYPE.TASK_ADD_MEMBER, new Object[]{user,task});
			this.baseService.sendEmail(loginUserId, new Long[]{leaderUserId}, NOTICE_MODULE_TYPE.TASK_ADD_LEADER, new Object[]{user,task});
		}
	}


	/**
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @user jingjian.wu
	 * @date 2015年11月4日 上午11:34:35
	 */
	    
	public int saveUserListIfNotExist(List<String> loginNames) throws ClientProtocolException, IOException {
		int i  =0;
		if(null!=loginNames && loginNames.size()>0){
			for(String loginName:loginNames){
				User u =userDao.findByAccountAndDel(loginName, DELTYPE.NORMAL);
				if(null!=u){
					User tmpUser = getLocalOrAppCanUser(loginName);
					if(null!=tmpUser){
						tmpUser.setId(u.getId());
						if(null==tmpUser.getCellphone()){
							tmpUser.setCellphone(u.getCellphone());
						}
						if(null==tmpUser.getIcon()){
							tmpUser.setIcon(u.getIcon());
						}
						if(null==tmpUser.getUserName()){
							tmpUser.setUserName(u.getUserName());
						}
						if(null==tmpUser.getQq()){
							tmpUser.setQq(u.getQq());
						}
						tmpUser.setBindEmail(u.getBindEmail());
						userDao.save(tmpUser);
						i++;
					}
				}else{
					User tmpUser = getLocalOrAppCanUser(loginName);
					if(null!=tmpUser){
						userDao.save(tmpUser);
						i++;
					}
				}
			}
		}
		return i;
	}

	
	public Map<String, Object> createIdentityCode(long loginUserId, String email, String type) {
		log.info("UserService --> createIdentityCode,loginUserId:"+loginUserId+",email:"+email+",type:"+type);
		//在这之前的验证码设置无效
		this.jdbcTpl.update("update T_IDENTITY_CODE set del = 1 where userId = "+loginUserId+" and email ='"+email+"'");
		
		Boolean result = EMAIL_PATTERN.matcher(email).matches();
		if(!result){
			return this.getFailedMap("邮箱格式错误！"); 
		}
		IdentityCode entity = new IdentityCode();
		entity.setEmail(email);
		entity.setUserId(loginUserId);
		
		long Temp; //不能设定为int,必须设定为long
		//产生100000到999999的随机数
		Temp=Math.round(Math.random()*899999+100000);
		entity.setCode(Temp+"");
		
		
		this.identityCodeDao.save(entity);
		
		User user = this.userDao.findOne(loginUserId);
		MailSenderInfo mailInfo = new MailSenderInfo();
		StringBuffer content = new StringBuffer();
		content.append("<p style='padding-left:8px'>Hi，"+user.getUserName()+"：</p>");
		if("bind".equals(type)){
			mailInfo.setSubject("【AppCan-协同开发】邮箱绑定通知");
			content.append("<p style='padding:4px;word-wrap:break-word'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;感谢您使用协同开发，您正在进行绑定邮箱验证，本次请求的验证码为：<span style='color:#FF9900'>"+Temp+"</span> （为保证您账号的安全性，请在1小时内完成验证）</p>");
		}else{
			mailInfo.setSubject("【AppCan-协同开发】邮箱解绑通知");
			content.append("<p style='padding:4px;word-wrap:break-word'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 感谢您使用协同开发，您正在进行邮箱解绑验证，本次请求的验证码为：<span style='color:#FF9900'>"+Temp+"</span> （为保证您账号的安全性，请在1小时内完成验证）</p>");
		}
		content.append("<p style='padding:4px;'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;祝您使用愉快！</p>");
		content.append("<p style='text-align:right;padding:4px 55px;'>AppCan</p>");
		mailInfo.setContent(content.toString());
		mailInfo.setToAddress(email);
		sendMailTool.sendMailByAsynchronousMode(mailInfo);
		
		return this.getSuccessMap("验证码发送成功！");
	}


	public Map<String, Object> updateBindEmail(long loginUserId, String email, String code) {
		
		log.info("UserService --> updateBindEmail,loginUserId:"+loginUserId+",email:"+email+",code:"+code);
		
		IdentityCode entity = this.identityCodeDao.findByUserIdAndEmailAndCodeAndDel(loginUserId, email, code,DELTYPE.NORMAL);
		if(null!=entity){
			User user = this.userDao.findOne(loginUserId);
			user.setBindEmail(email);
			this.userDao.save(user);
			
			//在这之前的验证码设置无效
			this.jdbcTpl.update("update T_IDENTITY_CODE set del = 1 where userId = "+loginUserId+" and email ='"+email+"'");
			return this.getSuccessMap("恭喜你，邮箱绑定成功！");
			
		}else{
			return this.getFailedMap("验证码错误，请重新填写");
		}
	}


	public Map<String, Object> updateUnbindEmail(long loginUserId, String email, String code) {
		
		log.info("UserService --> updateUnbindEmail,loginUserId:"+loginUserId+",email:"+email+",code:"+code);
		
		IdentityCode entity = this.identityCodeDao.findByUserIdAndEmailAndCodeAndDel(loginUserId, email, code,DELTYPE.NORMAL);
		if(null!=entity){
			User user = this.userDao.findOne(loginUserId);
			user.setBindEmail(null);
			this.userDao.save(user);
			
			//在这之前的验证码设置无效
			this.jdbcTpl.update("update T_IDENTITY_CODE set del = 1 where userId = "+loginUserId+" and email ='"+email+"'");
			return this.getSuccessMap("恭喜你，邮箱解绑成功！");
			
		}else{
			return this.getFailedMap("验证码错误，请重新填写");
		}
	}


	
	public ModelAndView updateOrImportUser(User user, String tryAgain) {

		User fuser = this.findUserByAccountAndDel(user.getAccount(), DELTYPE.NORMAL);
		//再次申请的用户
		if(null!=tryAgain && tryAgain.equals("true") ){
			fuser.setStatus(USER_STATUS.AUTHSTR);
			fuser.setUserlevel(USER_LEVEL.NORMAL);
			this.addUser(fuser);
			return this.getFailedModel(fuser.getStatus());
		}
	
		if(null==fuser){
			if(user.getIcon() == null || "".equals(user.getIcon())) {
				user.setIcon(userIcon);
			}
			
			//用户不存在，插入数据，待审核 
			user.setStatus(USER_STATUS.AUTHSTR);
			user.setJoinPlat(USER_JOINPLAT.APPCAN);
			user.setType(USER_TYPE.AUTHENTICATION);
			user.setUserlevel(USER_LEVEL.NORMAL);
			user.setEmail(user.getAccount());
			user.setBindEmail(user.getAccount());
			this.addUser(user);
			return this.getFailedModel(user.getStatus());
		}else if(fuser.getStatus().equals(USER_STATUS.NORMAL)){
			//用户存在，且状态是正常状态，则登录成功
			Map<String,Object> map = new HashMap<>();
			map.put("userid",fuser.getId());
			map.put("account", fuser.getAccount());
			map.put("userName", fuser.getUserName());
			Map<String, Object> res = new HashMap<>();
//			Map<String,Integer> permissions = this.userAuthService.findAuth(fuser.getId());
			res.put("object", map);
			Map<String,Integer> permissions = new HashMap<String, Integer>();
			if(fuser.getUserlevel().compareTo(USER_LEVEL.ADVANCE)==0){
				permissions.put("project_create", 1);
				permissions.put("team_create", 1);
			}
			res.put("permissions", permissions);
			return this.getSuccessModel(res);
		}else{
			//用户审核不通过或者停用状态
			return this.getFailedModel(fuser.getStatus());
		}
		
	}

	
	public Map<String, Object> updatePinyin() {
		List<User> users = this.userDao.findByStatusAndDel(USER_STATUS.NORMAL,DELTYPE.NORMAL);
		for(User user : users){
			user.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(user.getUserName()==null?"":user.getUserName()));
			user.setPinYinName(ChineseToEnglish.getPingYin(user.getUserName()==null?"":user.getUserName()));
		}
		this.userDao.save(users);
		return this.getSuccessMap("affected "+users.size());
	}


	/**
	 * 返回某个人创建的/管理的所有团队下面的所有成员
	 * @user jingjian.wu
	 * @date 2016年3月7日 上午11:06:58
	 */
	    
	public Map<String, Object> findUsersBelongSomeOneMgrTeam(Long loginUserId,String keyWords) {
		List<Long> roleId = new ArrayList<Long>();
		Role roleCreat = Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.CREATOR);
		Role roleMgr = Cache.getRole(ENTITY_TYPE.TEAM+"_"+ROLE_TYPE.ADMINISTRATOR);
		roleId.add(roleCreat.getId());
		roleId.add(roleMgr.getId());
		List<User> listUser = this.userDao.findUsersBelongSomeOneMgrTeam(loginUserId, roleId,keyWords);
		return this.getSuccessMap(listUser);
	}


	/**
	 * 查询项目下的人(只查询前10个,根据关键字查询),此接口为了协同二期开发中,创建者,参与者等等
	 * 如果没有项目id,则查询所有的协同用户中的匹配10个
	 * @user jingjian.wu
	 * @date 2016年3月7日 下午5:31:46
	 */
	    
	public List<User> findUser(Long projectId,String keyWords) {
		List<User> users = new ArrayList<User>();
		if(null==projectId){
			users = userDao.findByStatusAndDelAndKeyWordLike(USER_STATUS.NORMAL.ordinal(), DELTYPE.NORMAL.ordinal(),keyWords);
		}else{
			users = userDao.findUserForProject(projectId, keyWords);
		}
		return users;
	}


	public User saveUserIfNotExistByEmail(String email) throws ClientProtocolException, IOException {
		User u =userDao.findByEmailAndDel(email, DELTYPE.NORMAL);
		if(null!=u){
			User tmpUser = getLocalOrAppCanUser(u.getAccount());
			if(null!=tmpUser){
				tmpUser.setId(u.getId());
				tmpUser.setBindEmail(u.getBindEmail());
				userDao.save(tmpUser);
				return tmpUser;
			}
			return u;
		}else{
			User tmpUser = getLocalOrAppCanUser(email);
			if(null!=tmpUser){
				userDao.save(tmpUser);
				return tmpUser;
			}else{
				User usr = new User();
				usr.setAccount(email);
				usr.setEmail(email);
				usr.setBindEmail(email);
				usr.setCellphone("");
				usr.setUserName(email);
				usr.setUserlevel(USER_LEVEL.ADVANCE);
				usr.setType(USER_TYPE.NOREGISTER);
				userDao.save(usr);
				return usr;
			}
		}
	}


	public Map<String, Object> updatePwdChange(String loginName, String oldPwd,
			String newPwd) {
		List<User> userList = userDao.findByAccountAndPasswordAndDel(loginName, oldPwd, DELTYPE.NORMAL);
		if(userList != null && userList.size() > 0) {
			User user = userList.get(0);
			 user.setPassword(newPwd);
			 user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
			 userDao.save(user);
			 return this.getSuccessMap("修改密码成功");
		} else {
			return this.getFailedMap("旧用户名或密码错误");
		}
		
	}
}
