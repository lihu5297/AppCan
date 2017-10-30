package org.zywx.cooldev.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.zywx.cooldev.commons.Enums.INIT_DEMO_STATUS;
import org.zywx.cooldev.commons.Enums.USER_EMAIL_STATUS;
import org.zywx.cooldev.commons.Enums.USER_JOINPLAT;
import org.zywx.cooldev.commons.Enums.USER_LEVEL;
import org.zywx.cooldev.commons.Enums.USER_STATUS;
import org.zywx.cooldev.commons.Enums.USER_TYPE;
import org.zywx.cooldev.commons.Enums.UserGender;
import org.zywx.cooldev.util.ReflectUtil;

@Entity
@Table(name="T_USER")
public class User extends BaseEntity{

	private static final long serialVersionUID = 1418954230337683952L;
	//***************************************************
	//    User fieds                                    *
	//***************************************************
	//账号
	@Column(unique=true)
	private String account;
	
	//密码
	private String password;

	//头像
	private String icon;
	
	//昵称
//	private String nickName;
	
	//用户状态
	@Column(columnDefinition="tinyint")
	private USER_STATUS status;
	
	//用户类型（身份）
	@Column(columnDefinition="tinyint")
	private USER_TYPE type;
	
	private String cellphone;
	
	private String qq;
	
	private String address;
	
	@Column(unique=true)
	private String email;

	@Column(columnDefinition="tinyint")
	private UserGender gender;
	
	//接入平台
	private USER_JOINPLAT joinPlat=USER_JOINPLAT.INNER;
	
	//是否接收邮件
	private USER_EMAIL_STATUS receiveMail=USER_EMAIL_STATUS.PERMIT;
	
	private String userName;
	
	//用户级别  ADVANCE 高级用户  NORMAL 普通用户
	private USER_LEVEL userlevel=USER_LEVEL.NORMAL;
	
	//备注
	private String remark;
	
	private INIT_DEMO_STATUS initDemoStatus;
	
	private String bindEmail;
	
	private String pinYinName;
	
	private String pinYinHeadChar;
	
	private String nickName;
	private long filialeId;		//分公司主键
	
	//***************************************************
	//    Related fieds                                 *
	//***************************************************
	@Transient
	private Long teamMemberId;//团队成员ID
	
	@Transient
	private Long teamAuthId;//团队成员权限ID
	
	@Transient
	private boolean teamCreator=false;//团队授权成功后,需要返回团队下面的所有人,标示此人是否是团队创建者
	
	@Transient
	private boolean proejectCreator=false;//标示此人是否是项目创建者
	
	
	@Transient
	private String roleNameInTeam;//返回此成员在团队中的角色(团队成员列表时候需要)
	@Transient
	private String filialeCode;		//分公司代码
	@Transient
	private String filialeName;		//分公司名称

	//***************************************************
	//    Getters & Setters                             *
	//***************************************************
	public Long getTeamMemberId() {
		return teamMemberId;
	}

	public void setTeamMemberId(Long teamMemberId) {
		this.teamMemberId = teamMemberId;
	}

	public Long getTeamAuthId() {
		return teamAuthId;
	}

	public void setTeamAuthId(Long teamAuthId) {
		this.teamAuthId = teamAuthId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIcon() {
		if(StringUtils.isBlank(this.icon)){
			return "http://xttest16.appcan.cn/zymobiResource/headerImg/default.jpg";
		}
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	/*public String getNickName() {
		if(this.nickName==null||this.nickName==""){
			return this.account;
		}
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}*/

	public USER_STATUS getStatus() {
		return status;
	}

	public void setStatus(USER_STATUS status) {
		this.status = status;
	}

	public USER_TYPE getType() {
		return type;
	}

	public void setType(USER_TYPE type) {
		this.type = type;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserGender getGender() {
		return gender;
	}

	public void setGender(UserGender gender) {
		this.gender = gender;
	}

	
	public USER_JOINPLAT getJoinPlat() {
		return joinPlat;
	}

	public void setJoinPlat(USER_JOINPLAT joinPlat) {
		this.joinPlat = joinPlat;
	}

	public USER_EMAIL_STATUS getReceiveMail() {
		return receiveMail;
	}

	public void setReceiveMail(USER_EMAIL_STATUS receiveMail) {
		this.receiveMail = receiveMail;
	}

	public String getUserName() {
		if(StringUtils.isBlank(userName)){
			return account;
		}
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public USER_LEVEL getUserlevel() {
		return userlevel;
	}

	public void setUserlevel(USER_LEVEL userlevel) {
		this.userlevel = userlevel;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isTeamCreator() {
		return teamCreator;
	}

	public void setTeamCreator(boolean teamCreator) {
		this.teamCreator = teamCreator;
	}
	
	public boolean isProjectCreator() {
		return teamCreator;
	}

	public void setProjectCreator(boolean teamCreator) {
		this.teamCreator = teamCreator;
	}
	public INIT_DEMO_STATUS getInitDemoStatus() {
		return initDemoStatus;
	}

	public void setInitDemoStatus(INIT_DEMO_STATUS initDemoStatus) {
		this.initDemoStatus = initDemoStatus;
	}

	public String getBindEmail() {
		return bindEmail;
	}

	public void setBindEmail(String bindEmail) {
		this.bindEmail = bindEmail;
	}

	public String getPinYinName() {
//		if(null!=this.userName && !this.userName.equals("")){
//			this.pinYinName = ChineseToEnglish.getPingYin(this.userName);
//		}
		return this.pinYinName;
	}

	public void setPinYinName(String pinYinName) {
//		if(null!=this.userName && !this.userName.equals("")){
//			this.pinYinName = ChineseToEnglish.getPingYin(this.userName);
//		}else{
			this.pinYinName = pinYinName;
//		}
	}

	public String getPinYinHeadChar() {
//		if(null!=this.userName && !this.userName.equals("")){
//			this.pinYinHeadChar = ChineseToEnglish.getPinYinHeadChar(this.userName);
//		}
		return pinYinHeadChar;
	}

	public void setPinYinHeadChar(String pinYinHeadChar) {
//		if(null!=this.userName && !this.userName.equals("")){
//			this.pinYinHeadChar = ChineseToEnglish.getPinYinHeadChar(this.userName);
//		}else{
			this.pinYinHeadChar = pinYinHeadChar;
//		}
	}

	@Override
	public String toString() {
		/*return "User [account=" + account + ", password=" + password
				+ ", icon=" + icon + ", nickName=" + nickName + ", status="
				+ status + ", type=" + type + ", cellphone=" + cellphone
				+ ", qq=" + qq + ", address=" + address + "]" + super.toString();*/
		return this.userName;
	}

	public String getRoleNameInTeam() {
		return roleNameInTeam;
	}

	public void setRoleNameInTeam(String roleNameInTeam) {
		this.roleNameInTeam = roleNameInTeam;
	}
	
	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	

	public String getFilialeCode() {
		return filialeCode;
	}

	public void setFilialeCode(String filialeCode) {
		this.filialeCode = filialeCode;
	}

	public String getFilialeName() {
		return filialeName;
	}

	public void setFilialeName(String filialeName) {
		this.filialeName = filialeName;
	}
	
	public long getFilialeId() {
		return filialeId;
	}

	public void setFilialeId(long filialeId) {
		this.filialeId = filialeId;
	}

	public static void main(String[] args) {
		User u = new User();
		ReflectUtil.invokeSetMethod(u, "userlevel","ADVANCE");
		System.out.println(u.getUserlevel());
		
		ReflectUtil.invokeSetMethod(u, "joinPlat","APPCAN");
		System.out.println(u.getJoinPlat());
		
		ReflectUtil.invokeSetMethod(u, "joinPlat","INNER");
		System.out.println(u.getJoinPlat());
	}

}
