package org.zywx.coopman.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.commons.Enums.USER_EMAIL_STATUS;
import org.zywx.coopman.commons.Enums.USER_JOINPLAT;
import org.zywx.coopman.commons.Enums.USER_LEVEL;
import org.zywx.coopman.commons.Enums.USER_STATUS;
import org.zywx.coopman.commons.Enums.USER_TYPE;
import org.zywx.coopman.commons.Enums.UserGender;

/**
 * 用户信息
 * @author 东元
 *
 */
@Entity
@Table(name="T_USER")
public class User extends BaseEntity{

	private static final long serialVersionUID = 1418954230337683952L;
	
	//账号
	private String account;
	
	//密码
	private String password;

	//头像
	private String icon;
	
	//昵称
	private String nickName;
	
	private String userName;
	
	private String email;
	
	//备注
	private String remark;
	
	private String bindEmail;//用户绑定的邮箱
	
	private long filialeId;		//分公司主键
	@Transient
	private List<Long> initPer;	//用户初始化权限
	
	@Transient
	private String filialeName;
	
	public String getBindEmail() {
		return bindEmail;
	}

	public void setBindEmail(String bindEmail) {
		this.bindEmail = bindEmail;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Column(columnDefinition="tinyint")
	private UserGender gender;
	
	
	public UserGender getGender() {
		return gender;
	}

	public void setGender(UserGender gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	//用户级别  ADVANCE 高级用户  NORMAL 普通用户
	private USER_LEVEL userlevel=USER_LEVEL.NORMAL;
	
	//用户状态
	@Column(columnDefinition="tinyint")
	private USER_STATUS status = USER_STATUS.NORMAL;
	
	//用户类型（身份）
	@Column(columnDefinition="tinyint")
	private USER_TYPE type = USER_TYPE.AUTHENTICATION;
	
	private String cellphone;
	
	private String qq;
	
	private String address;
	
	//接入平台
	private USER_JOINPLAT joinPlat=USER_JOINPLAT.INNER;
	
	//是否接收邮件
	private USER_EMAIL_STATUS receiveMail=USER_EMAIL_STATUS.PERMIT;
	

	public USER_EMAIL_STATUS getReceiveMail() {
		return receiveMail;
	}

	public void setReceiveMail(USER_EMAIL_STATUS receiveMail) {
		this.receiveMail = receiveMail;
	}


	public USER_LEVEL getUserlevel() {
		return userlevel;
	}

	public void setUserlevel(USER_LEVEL userlevel) {
		this.userlevel = userlevel;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public USER_JOINPLAT getJoinPlat() {
		return joinPlat;
	}

	public void setJoinPlat(USER_JOINPLAT joinPlat) {
		this.joinPlat = joinPlat;
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
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	/*public String getNickName() {
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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public long getFilialeId() {
		return filialeId;
	}

	public void setFilialeId(long filialeId) {
		this.filialeId = filialeId;
	}

	public String getFilialeName() {
		return filialeName;
	}

	public void setFilialeName(String filialeName) {
		this.filialeName = filialeName;
	}
	

	public List<Long> getInitPer() {
		return initPer;
	}

	public void setInitPer(List<Long> initPer) {
		this.initPer = initPer;
	}

	@Override
	public String toString() {
		return "User [account=" + account + ", password=" + password
				+ ", icon=" + icon + ", status="
				+ status + ", type=" + type + ", cellphone=" + cellphone
				+ ", qq=" + qq + ", address=" + address + "]" + super.toString();
	}

}
