	/**  
     * @author jingjian.wu
     * @date 2015年9月10日 下午3:48:59
     */
    
package org.zywx.coopman.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zywx.coopman.commons.Enums.MANAGER_TYPE;
import org.zywx.coopman.commons.Enums.USER_STATUS;
import org.zywx.coopman.commons.Enums.USER_TYPE;
import org.zywx.coopman.entity.module.Module;


    /**
	 * @author jingjian.wu
	 * @date 2015年9月10日 下午3:48:59
	 */
@Entity
@Table(name="T_MAN_ADMIN")
public class Manager extends BaseEntity{
	
	
    /**
     * @Fields serialVersionUID :
     */
	    
	private static final long serialVersionUID = -500072056712276933L;

	// 账号
	private String account;

	// 密码
	private String password;

	// 头像
	private String icon;

	// 昵称
	private String userName;
	
	//email
	private String email;
	
	private String remarks;
	
	@Transient
	private List<Module> manageModule;
	@Transient
	private List<Long> modules;

	// 用户状态
	@Column(columnDefinition = "tinyint")
	private USER_STATUS status = USER_STATUS.NORMAL;

	// 用户类型（身份）
	@Column(columnDefinition = "tinyint")
	private MANAGER_TYPE type = MANAGER_TYPE.ADMIN;

	private long filialeId;		//分公司代码
	
	@Transient
	private String filialeName;
	
	private String cellphone;

	private String qq;

	private String address;

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


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public USER_STATUS getStatus() {
		return status;
	}

	public void setStatus(USER_STATUS status) {
		this.status = status;
	}

	public MANAGER_TYPE getType() {
		return type;
	}

	public void setType(MANAGER_TYPE type) {
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

	public List<Module> getManageModule() {
		return manageModule;
	}

	public void setManageModule(List<Module> manageModule) {
		this.manageModule = manageModule;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<Long> getModules() {
		return modules;
	}

	public void setModules(List<Long> modules) {
		this.modules = modules;
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

	
	
	
}
