package org.zywx.cooldev.entity;

import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年11月11日 上午9:43:11	<br>
 * 
 */
@Entity
@Table(name="T_ENTERPRISE")
public class Enterprise extends BaseEntity{

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

	private static final Pattern QQ_PATTERN = Pattern.compile("^[0-9]{1,}$");
	
	
	private static final long serialVersionUID = 1L;
	
	private String linkMan;
	
	private Long telephone;
	
	private String emailOrQQ;

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public Long getTelephone() {
		return telephone;
	}

	public void setTelephone(Long telephone) {
		this.telephone = telephone;
	}

	public String getEmailOrQQ() {
		return emailOrQQ;
	}

	public void setEmailOrQQ(String emailOrQQ) {
		this.emailOrQQ = emailOrQQ;
	}
	
	public boolean isQQNum(){
		if(QQ_PATTERN.matcher(emailOrQQ).matches()){
			return true;
		}
		return false;
	}
	
	public boolean isEmail(){
		if(EMAIL_PATTERN.matcher(emailOrQQ).matches()){
			return true;
		}
		return false;
	}

}
