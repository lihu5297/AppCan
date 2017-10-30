	/**  
     * @author jingjian.wu
     * @date 2015年11月5日 下午4:57:02
     */
    
package org.zywx.coopman.util.mail.base;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.service.SettingService;
import org.zywx.coopman.system.InitBean;
import org.zywx.coopman.util.PropertiesLoader;


    /**
 * @author jingjian.wu
 * @date 2015年11月5日 下午4:57:02
 */

public class SendMailTools {
	
	Logger log = Logger.getLogger(SendMailTools.class);
	
	private TaskExecutor taskExecutor;
	

	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public static void main(String[] args) throws UnsupportedEncodingException{   
		
//		String mailSubject = PropertiesLoader.getText("mail.subject", "mail.properties");
//		System.out.println(mailSubject);
//		System.exit(0);
        //这个类主要是设置邮件   
     MailSenderInfo mailInfo = new MailSenderInfo();    
     mailInfo.setMailServerHost("smtp.qq.com");    
     mailInfo.setMailServerPort("25");    
     mailInfo.setValidate(true);    
     mailInfo.setUserName("jingjian.wu@zymobi.com");    
     mailInfo.setPassword("sepoct09");//您的邮箱密码    
     mailInfo.setFromAddress("jingjian.wu@zymobi.com");    
     mailInfo.setToAddress("wjj_005@126.com");    
     mailInfo.setSubject("设置邮箱标题 如http://www.guihua.org 中国桂花网");    
     mailInfo.setContent("设置邮箱内容 如http://www.guihua.org 中国桂花网 是中国最大桂花网站==");    
     mailInfo.setNick("协同开发");
        //这个类主要来发送邮件   
     SimpleMailSender sms = new SimpleMailSender();   
//         sms.sendTextMail(mailInfo);//发送文体格式    
         sms.sendHtmlMail(mailInfo);//发送html格式   
   }  
	
	
	
	public  void sendMailByAsynchronousMode(final MailSenderInfo  mailBean ) {  
        taskExecutor.execute(new Runnable() {  
            public void run() {  
                try {  
                	boolean sendResult = sendMail(mailBean);  
                	if(!sendResult){
                		sendResult = sendMail(mailBean); 
                		if(!sendResult){
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.error("发送邮件内容:["+mailBean.getContent()+"]给"+mailBean.getToAddress()+"失败");
                		}
                	}
                } catch (Exception e) {  
                	e.printStackTrace();
                }  
            }  
        });  
    }  
	
	public static boolean sendMail(MailSenderInfo mailInfo) throws UnsupportedEncodingException{
		if(null==mailInfo || null==mailInfo.getContent() || mailInfo.getContent().equals("")){
			throw new RuntimeException("邮件内容不可以为空");
		}
		SettingService service = (SettingService) InitBean.getApplicationContext().getBean("settingService");
		Setting setting = service.getSetting();
		String mailHost = setting.getEmailServerUrl();
		String mailPort = setting.getEmailServerPort();
		String mailFrom = setting.getEmailAccount();
		String mailPwd = setting.getEmailPassword();
		String mailValid = "true";
		String mailSubject = PropertiesLoader.getText("mail.subject", "coopMan.properties");
		String mailUserName = PropertiesLoader.getText("mail.username", "coopMan.properties");
		
		System.out.println("send Mail to -->"+mailInfo.getToAddress());
		System.out.println("mail Host -- >"+mailHost);
		System.out.println("mail port -- >"+mailPort);
		System.out.println("mail subject -- >"+mailSubject);
		System.out.println("mail nick -- >"+mailUserName);
		
		mailInfo.setMailServerHost(mailHost);
		mailInfo.setMailServerPort(mailPort);
		mailInfo.setValidate(Boolean.parseBoolean(mailValid));
		mailInfo.setUserName(mailFrom);
		mailInfo.setPassword(mailPwd);
		mailInfo.setNick(null!=mailInfo.getNick()?mailInfo.getNick():mailUserName);
		mailInfo.setFromAddress(mailFrom);
		mailInfo.setToAddress(mailInfo.getToAddress());
		mailInfo.setSubject(null!=mailInfo.getSubject()?mailInfo.getSubject():mailSubject);
		SimpleMailSender sms = new SimpleMailSender();   
		return sms.sendHtmlMail(mailInfo);//发送html格式   
	}
}
