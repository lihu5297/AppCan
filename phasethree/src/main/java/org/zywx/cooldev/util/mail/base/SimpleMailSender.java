	/**  
     * @author jingjian.wu
     * @date 2015年11月5日 下午4:53:42
     */
    
package org.zywx.cooldev.util.mail.base;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.springframework.mail.javamail.MimeMessageHelper;



    /**
     * 简单邮件（不带附件的邮件）发送器   
 * @author jingjian.wu
 * @date 2015年11月5日 下午4:53:42
 */

public class SimpleMailSender {
	/**   
	  * 以文本格式发送邮件   
	  * @param mailInfo 待发送的邮件的信息   
	  */    
	    public boolean sendTextMail(MailSenderInfo mailInfo) {    
	      // 判断是否需要身份认证    
	      MyAuthenticator authenticator = null;    
	      Properties pro = mailInfo.getProperties();   
	      if (mailInfo.isValidate()) {    
	      // 如果需要身份认证，则创建一个密码验证器    
	        authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());    
	      }   
	      // 根据邮件会话属性和密码验证器构造一个发送邮件的session    
	      Session sendMailSession = Session.getDefaultInstance(pro,authenticator);    
	      try {    
	      // 根据session创建一个邮件消息    
	      Message mailMessage = new MimeMessage(sendMailSession);    
	      // 创建邮件发送者地址    
	      Address from = new InternetAddress(mailInfo.getFromAddress());    
	      // 设置邮件消息的发送者    
	      mailMessage.setFrom(from);    
	      // 创建邮件的接收者地址，并设置到邮件消息中    
	      Address to = new InternetAddress(mailInfo.getToAddress());    
	      mailMessage.setRecipient(Message.RecipientType.TO,to);    
	      // 设置邮件消息的主题    
	      mailMessage.setSubject(mailInfo.getSubject());    
	      // 设置邮件消息发送的时间    
	      mailMessage.setSentDate(new Date());    
	      // 设置邮件消息的主要内容    
	      String mailContent = mailInfo.getContent();    
	      mailMessage.setText(mailContent);    
	      // 发送邮件    
	      Transport.send(mailMessage);   
	      return true;    
	      } catch (MessagingException ex) {    
	          ex.printStackTrace();    
	      }    
	      return false;    
	    }    
	       
	    /**   
	      * 以HTML格式发送邮件   
	      * @param mailInfo 待发送的邮件信息   
	     * @throws UnsupportedEncodingException 
	      */    
	    public static boolean sendHtmlMail(MailSenderInfo mailInfo) throws UnsupportedEncodingException{    
	      // 判断是否需要身份认证    
	      MyAuthenticator authenticator = null;   
	      Properties pro = mailInfo.getProperties();   
	      //如果需要身份认证，则创建一个密码验证器     
	      if (mailInfo.isValidate()) {    
	        authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());   
	      }    
	      // 根据邮件会话属性和密码验证器构造一个发送邮件的session    
	      Session sendMailSession = Session.getDefaultInstance(pro,authenticator);    
	      try {    
	      // 根据session创建一个邮件消息    
	      Message mailMessage = new MimeMessage(sendMailSession);    
	      // 创建邮件发送者地址    
	      Address from = new InternetAddress(mailInfo.getFromAddress()); 
	      if(null!=mailInfo.getNick()){
	    	  String nick = MimeUtility.encodeText(mailInfo.getNick());  
	    	  from = new InternetAddress(nick+ "<"+mailInfo.getFromAddress()+">");
	      }
	      // 设置邮件消息的发送者    
	      mailMessage.setFrom(from);    
	      // 创建邮件的接收者地址，并设置到邮件消息中    
	      Address to = new InternetAddress(mailInfo.getToAddress());    
	      // Message.RecipientType.TO属性表示接收者的类型为TO    
	      mailMessage.setRecipient(Message.RecipientType.TO,to);    
	      // 设置邮件消息的主题    
	      mailMessage.setSubject(mailInfo.getSubject());    
	      // 设置邮件消息发送的时间    
	      mailMessage.setSentDate(new Date());    
	      // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象    
	      Multipart mainPart = new MimeMultipart();    
	      // 创建一个包含HTML内容的MimeBodyPart    
	      BodyPart html = new MimeBodyPart();    
	      // 设置HTML内容    
	      html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");    
	      mainPart.addBodyPart(html);    
	      // 将MiniMultipart对象设置为邮件内容    
	      mailMessage.setContent(mainPart);    
	      // 发送邮件    
	      Transport.send(mailMessage);    
	      return true;    
	      } catch (MessagingException ex) {    
	          ex.printStackTrace();    
	      }    
	      return false;    
	    }    
	    
	    
	    
	    /**   
	      * 以HTML格式发送邮件   
	      * @param mailInfo 待发送的邮件信息   
	     * @throws UnsupportedEncodingException 
	      */    
	    public static boolean sendHtmlMailBeautiful(MailSenderInfo mailInfo) throws UnsupportedEncodingException{    
	      // 判断是否需要身份认证    
	      MyAuthenticator authenticator = null;   
	      Properties pro = mailInfo.getProperties();   
	      //如果需要身份认证，则创建一个密码验证器     
	      if (mailInfo.isValidate()) {    
	        authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());   
	      }    
	      // 根据邮件会话属性和密码验证器构造一个发送邮件的session    
	      Session sendMailSession = Session.getDefaultInstance(pro,authenticator);    
	      try {    
	      // 根据session创建一个邮件消息    
	      MimeMessage mailMessageBase = new MimeMessage(sendMailSession);    
	      MimeMessageHelper mailMessage = new MimeMessageHelper(mailMessageBase, true, "UTF-8");
	      // 设置邮件消息的发送者    
	      mailMessage.setFrom("jingjian.wu@3g2win.com");    
	      // 设置邮件消息的主题    
	      mailMessage.setSubject(mailInfo.getSubject());    
	      // 设置邮件消息发送的时间    
	      mailMessage.setSentDate(new Date());    
	      // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象    
	      Multipart mainPart = new MimeMultipart();    
	      // 创建一个包含HTML内容的MimeBodyPart    
	      BodyPart html = new MimeBodyPart();    
	      // 设置HTML内容    
	      html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");    
	      mainPart.addBodyPart(html);    
	      // 将MiniMultipart对象设置为邮件内容    
	      mailMessage.setText("ffffffffffffffffffffffffffffffffff");    
	      // 发送邮件    
	      Transport.send(mailMessageBase);    
	      return true;    
	      } catch (MessagingException ex) {    
	          ex.printStackTrace();    
	      }    
	      return false;    
	    }
}
