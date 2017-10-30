package org.zywx.cooldev.util.mail;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * spring发送mail工具
     * @author jingjian.wu
     * @date 2015年9月14日 上午11:00:35
 */
public class MailUtil {

	Logger log = Logger.getLogger(MailUtil.class);
	
	private JavaMailSenderImpl javaMailSender;
//	private VelocityEngine velocityEngine;			//模板解析
	private TaskExecutor taskExecutor;
	
	
	public void sendMailByAsynchronousMode(final MailBean mailBean ) {  
        taskExecutor.execute(new Runnable() {  
            public void run() {  
                try {  
                	send(mailBean);  
                } catch (Exception e) {  
                    log.info("try 2 ===");
                    try {
						send(mailBean);
					} catch (Exception e1) {
						log.info("try 3==");
						try {
							send(mailBean);
						} catch (Exception e2) {
							e2.printStackTrace();
							log.info("发送邮件给【"+mailBean.getToEmails()[0]+"】失败");
						}
					}
                }  
            }  
        });  
    }  
	
	/**
	 * @param mailBean
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException 
	 */
	public boolean send(MailBean mailBean) throws MessagingException, UnsupportedEncodingException {

		MimeMessage msg = createMimeMessage(mailBean);
		if(msg == null){
			return false;
		}
		
		this.sendMail(msg, mailBean);
		
		return true;
	}
	
	private void sendMail(MimeMessage msg, MailBean mailBean){
				javaMailSender.send(msg);
				log.info("$$$ Send mail Subject:" +  mailBean.getSubject() 
						+ ", TO:" + arrayToStr(mailBean.getToEmails()) );

	}
	
	/*
	 * 记日记用的
	 */
	private String arrayToStr(String[] array){
		if(array == null || array.length == 0){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for(String str : array){
			sb.append(str+" , ") ;
		}
		return sb.toString();
	}

	/* 
	 * 根据 mailBean 创建 MimeMessage
	 */
	private MimeMessage createMimeMessage(MailBean mailBean) throws MessagingException, UnsupportedEncodingException {
		if (!checkMailBean(mailBean)) {
			return null;
		}
//		String text = getMessage(mailBean);
		String text = mailBean.getTemplate();
		log.info("mail text-->"+text);
		if(text == null){
			log.warn("@@@ warn mail text is null (Thread name=" 
					+ Thread.currentThread().getName() + ") @@@ " +  mailBean.getSubject());
			return null;
		}
				
		/**/Properties properties = new Properties(); 
		properties.setProperty("mail.debug", "true");
		//是否显示调试信息(可选测试用) 
		properties.setProperty("mail.transport.protocol", "smtp");
		//协议 
		properties.setProperty("mail.smtp.auth", "true");
		//设置用户验证 
		properties.setProperty("mail.smtp.timeout","250000");
		//超时时间 
		javaMailSender.setJavaMailProperties(properties); 
		javaMailSender.setSession(Session.getInstance(properties));
		MimeMessage msg = javaMailSender.createMimeMessage();
//		SimpleMailMessage msg = new SimpleMailMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper(msg, true, "UTF-8");
		//messageHelper.setFrom(mailBean.getFrom());
//		JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();  
//		  MimeMessage mailMessage = senderImpl.createMimeMessage();  
		  //设置utf-8或GBK编码，否则邮件会有乱码  
//		  MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage,true,"utf-8");
		try {
			messageHelper.setFrom(mailBean.getFrom(), mailBean.getFromName());
		} catch (UnsupportedEncodingException e) {
			log.error(e);

		}
		messageHelper.setSubject(mailBean.getSubject());
		messageHelper.setTo(mailBean.getToEmails());
		messageHelper.setText(text, true); // html: true
		
		//测试一下附件效果
		/*FileSystemResource img1 = new FileSystemResource( new File("D:/usr/logo.png")); 
		FileSystemResource img2 = new FileSystemResource( new File("D:/usr/gongzhonghao.jpg")); 
		messageHelper.addInline("a",img1);   
		   messageHelper.addInline("b", img2);    
		   File file=new File("D:/usr/bootstrap-3.3.5-dist.zip");     
		   // 这里的方法调用和插入图片是不同的，使用MimeUtility.encodeWord()来解决附件名称的中文问题   
		   messageHelper.addAttachment(MimeUtility.encodeWord(file.getName()), file);    
		*/
		return msg;
	}
	
	/*
	 * 模板解析
	 * @param mailBean
	 * @return
	 */
	/*private String getMessage(MailBean mailBean) {
		StringWriter writer = null;
		try {

			writer = new StringWriter();
			VelocityContext context = new VelocityContext(mailBean.getData());

			velocityEngine.evaluate(context, writer, "", mailBean.getTemplate());

			return writer.toString();
		} catch (VelocityException e) {
			log.error(" VelocityException : " + mailBean.getSubject() + "\n" + e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					log.error("###StringWriter close error ... ");
				}
			}
		}
		return null;
	}*/
	
	/*
	 * check 邮件
	 */
	private boolean checkMailBean(MailBean mailBean){
		if (mailBean == null) {
			log.warn("@@@ warn mailBean is null (Thread name=" 
					+ Thread.currentThread().getName() + ") ");
			return false;
		}
		if (mailBean.getSubject() == null) {
			log.warn("@@@ warn mailBean.getSubject() is null (Thread name=" 
					+ Thread.currentThread().getName() + ") ");
			return false;
		}
		if (mailBean.getToEmails() == null) {
			log.warn("@@@ warn mailBean.getToEmails() is null (Thread name=" 
					+ Thread.currentThread().getName() + ") ");
			return false;
		}
		if (mailBean.getTemplate() == null) {
			log.warn("@@@ warn mailBean.getTemplate() is null (Thread name=" 
					+ Thread.currentThread().getName() + ") ");
			return false;
		}
		return true;
	}

	
	/*===================== setter & getter =======================*/
	public void setJavaMailSender(JavaMailSenderImpl javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public JavaMailSender getJavaMailSender() {
		return javaMailSender;
	}


	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	
	/*public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}*/

}