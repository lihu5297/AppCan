	/**  
     * @author jingjian.wu
     * @date 2015年11月5日 下午4:57:02
     */
    
package org.zywx.cooldev.util.mail.base;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.zywx.cooldev.util.PropertiesLoader;


    /**
 * @author jingjian.wu
 * @date 2015年11月5日 下午4:57:02
 */

public class SendMailTools {
	
	Logger log = Logger.getLogger(SendMailTools.class);
	
	private static String sendMailSwitch;
	
	private TaskExecutor taskExecutor;
	
	private static String emailSourceRootPath;//http://zymobitest.appcan.cn/zymobiResource
	
	private static String xtHost;//http://zymobitest.appcan.cn
	

	public String getSendMailSwitch() {
		return sendMailSwitch;
	}

	public void setSendMailSwitch(String sendMailSwitch) {
		this.sendMailSwitch = sendMailSwitch;
	}

	public static String getEmailSourceRootPath() {
		return emailSourceRootPath;
	}

	public static void setEmailSourceRootPath(String emailSourceRootPath) {
		SendMailTools.emailSourceRootPath = emailSourceRootPath;
	}

	public static String getXtHost() {
		return xtHost;
	}

	public static void setXtHost(String xtHost) {
		SendMailTools.xtHost = xtHost;
	}

	public TaskExecutor getTaskExecutor() {
		return taskExecutor;
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public static String getEmailHeader(){
		StringBuffer header = new StringBuffer();
/*		header.append("<!DOCTYPE html> <html lang=\"en\"> <head> <meta charset=\"UTF-8\"> <title>协同开发-邮件通知</title> <style type=\"text/css\"> body{height: 100%; } .main{height:auto; margin-")
	     .append("bottom: 20px; width:600px; background-color: #fff; vertical-align: center; margin:30px auto 30px; } .header{height:45px; width:560px; background-color: #224e84; padding:0 20")
	     .append("px; } .xt_content{min-height: 396px; padding:0 20px; width:560px; background-color: #fff; } .footer{height:190px; width:600px; background-color: #fff; } .footer .cont{height:")
	     .append("160px; width:560px; margin:0 20px; border-top:1px dashed #9d9d9d; } .footer .cont .leftcont{float:left; width:380px; margin-top:50px; } .footer .cont .rightcont{width:112px;")
	     .append("float:right; margin-top:20px; margin-left:68px; } .footer .foot{clear:both; height:30px; background-color:#f9f9f9; text-align: center; font-size:14px; line-height:30px; ")
	     .append("color:#8b8b8b; font-family: '\\\\5FAE\\\\8F6F\\\\96C5\\\\9ED1'; } a{color:#224e84; cursor:pointer; text-decoration:underline; } </style> </head> <body style=\"background-")
	     .append("color:#f3f3f3;margin:0;\"> <div class=\"main\" style=\"height:auto; margin-bottom: 20px; width:600px; background-color: #fff; vertical-align: center; margin:30px auto 30px;\"> <div class=\"header\" style=\"height:45px; width:560px; background-color: #224e84; padding:0 20px\"> <img id=\"headerImg\" src=\""+emailSourceRootPath+"/emailResource/image/logo.png\" width=\"27\" height=\"27\" alt=\"协同开发\" class=\"logo\"")
	     .append("style=\"margin:7px 5px 0 0\"> <img src=\""+emailSourceRootPath+"/emailResource/image/logo1.png\" width=\"83\" height=\"19\" alt=\"协同开发\" class=\"logo\" style=\"margin-top:15px\"> ")
	     .append("<span style=\"color: white;float: right;line-height: 45px;\">正益工场－移动互联网创新创业平台</span>")
	     .append(" </div> <div style=\"min-height: 396px; padding:0 20px; width:560px; background-color: #fff;\"><div>");*/
		header.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>协同开发-邮件通知</title>")
		.append("<style type=\"text/css\">body{height: 100%; } .main{height:auto; margin-bottom: 20px; width:600px;")
		.append(" background-color: #fff; vertical-align: center; margin:30px auto 30px; } .header{height:45px; width:560px; background-color: #224e84; padding:0 20px; } ")
		.append(".xt_content{min-height: 396px; padding:0 20px; width:560px; background-color: #fff; } .footer{height:190px; width:600px; background-color: #fff; }") 
		.append(".footer .cont{height:160px; width:560px; margin:0 20px; border-top:1px dashed #9d9d9d; } .footer .cont .leftcont{float:left; width:380px; margin-top:50px; } ") 
		.append(".footer .cont .rightcont{width:112px;float:right; margin-top:20px; margin-left:68px; } .footer .foot{clear:both; height:30px; background-color:#f9f9f9;") 
		.append("text-align: center; font-size:14px; line-height:30px; color:#8b8b8b; font-family: '\\5FAE\\8F6F\\96C5\\9ED1'; } a{color:#224e84; cursor:pointer; text-decoration:underline; }</style></head>")
		.append("<body style=\"background-color:#f3f3f3;margin:0;\"><div class=\"main\" style=\"height:auto; margin-bottom: 20px; width:600px; background-color: #fff; vertical-align: center; margin:30px auto 30px;\">")
		.append("<div class=\"header\" style=\"height:45px; width:560px; background-color: #224e84; padding:0 20px\"><span style=\"color: white;float: right;line-height: 45px;\">协同开发平台</span></div>")
		.append("<div style=\"min-height: 396px; padding:0 20px; width:560px; background-color: #fff;\"><div>");
		return header.toString();
	}
	
	public static String getEmailFooter(){
		StringBuffer footer = new StringBuffer();
		/*footer.append("<p>祝您使用愉快！</p> <")
	     .append("div style=\"float:right;\">AppCan_协同开发</div> </div></div> <div class=\"footer\" style=\"font-family: '\\\\5FAE\\\\8F6F\\\\96C5\\\\9ED1';height:190px; width:600px; background-color: #fff;\"> <div class=\"cont\" style=\"height:160px; width:560px; margin:0 20px; border-top:1px dashed #9d9d9d;\"> <div class=\"leftcont\" style=\"float:left; width:380px; margin-top:50px;\"> <span")
	     .append(">官网：</span><a style=\"font-size:16px;\" href=\"http://www.zyhao.com\">www.zyhao.com</a> <p style=\"font-size:14px;color:#8b8b8b\">")
	     .append("本邮件由AppCan_协同开发系统自动发出，请勿直接回复。 如果您不想再接收此类邮件，您可以在<a href=\""+xtHost+"\">协同开发</a>中解除绑定。</p> </div> <div class=\"")
	     .append("rightcont\" style=\"width:112px;float:right; margin-top:20px; margin-left:68px; \"> <img src=\""+emailSourceRootPath+"/emailResource/image/erweima.jpg\" width=\"104\" height=\"104\" alt=\"正益工场官方微信\"> <div style=\"font-size:14px;color:#8b8b8b;font-family: ")
	     .append("'\\\\5FAE\\\\8F6F\\\\96C5\\\\9ED1'\">正益工场官方微信</div> </div> <span style=\"color:#8b8b8b; clear:both; height:30px; background-color:#f9f9f9; text-align: center; font-size:14px; line-height:30px;display:block;width:100%\"><p style=\"font-family: '\\\\5FAE\\\\8F6F\\\\96C5\\\\9ED1'; \">Powered by AppCan</p></span></div>  </div> </div> </body> </html>")
	     ;*/
		footer.append("<p>祝您使用愉快！</p><div style=\"float:right;\">协同开发</div>")
	     .append(" </div></div> <div class=\"footer\" style=\"font-family: '\\\\5FAE\\\\8F6F\\\\96C5\\\\9ED1';height:190px; width:600px; background-color: #fff;\"> <div class=\"cont\" style=\"height:160px; width:560px; margin:0 20px; border-top:1px dashed #9d9d9d;\"> <div class=\"leftcont\" style=\"float:left; width:380px; margin-top:0px;\">")
	     .append("<p style=\"font-size:14px;color:#8b8b8b\">")
	     .append("本邮件由协同开发系统自动发出，请勿直接回复。 如果您不想再接收此类邮件，您可以在<a href=\""+xtHost+"\">协同开发</a>中解除绑定。</p> </div>  ")
	     .append(" <span style=\"color:#8b8b8b; clear:both; height:30px; background-color:#f9f9f9; text-align: center; font-size:14px; line-height:30px;display:block;width:100%\"><p style=\"font-family: '\\\\5FAE\\\\8F6F\\\\96C5\\\\9ED1'; \"></p></span></div>  </div> </div> </body> </html>")
	     ;
		return footer.toString();
	}
	 
	public static String getEmailCenter(String targetUser,String coreContent){
		StringBuffer center = new StringBuffer();
		center.append("<p style=\"line-height:48px;margin-top:25px;margin-bottom:0\">Hi，"+targetUser+"</p> <p style=\"line-")
		.append("height:30px;margin:0;white-space:normal;word-break:break-all;overflow:hidden;\"> "+coreContent+" </p>");
		return center.toString();
	}
	public static String neirong = "<!DOCTYPE html>"
			+"<html lang='en'>"
			+"<head>"
			+"<meta charset='utf-8' />"
			+"<link rel='stylesheet' href='http://www.appcan.cn/css/bootstrap.min.css'>"
			+"<style>"
			+"	body{"
			+"		width: 800px;"
			+"		margin: 0 auto;"
			+"		font-family: '微软雅黑';"
			+"	}"
			+"	#header{"
			+"		background: url(http://bbs.appcan.cn/data/attachment/album/201702/20/110347wt378hz788neb8o0.png) no-repeat;"
			+"		height: 230px;"
			+"		position: relative;"
			+"	}"
			+"	#header span{"
			+"		position: absolute;"
			+"		bottom: 50px;"
			+"    	right: 210px;"
			+"    	color: #1e526c;"
			+"	}"
			+"	.guide span{"
			+"		color: #838383;"
			+"		line-height: 25px;"
			+"		letter-spacing: 1.3px;"
			+"	}"
			+"	.ide,.engineP,.sdk{"
			+"		margin-top: 15px;"
			+"	}"
			+"	.ide span{"
			+"		color: #838383;"
			+"		line-height: 25px;"
			+"		letter-spacing: 1.3px;"
			+"	}"
			+"	.sdk span{"
			+"		color: #838383;"
			+"		line-height: 25px;"
			+"		letter-spacing: 1.3px;"
			+"	}"
			+"	.engineP{"
			+"		color: #838383;"
			+"		line-height: 25px;"
			+"		letter-spacing: 1.3px;	"
			+"	}"
			+"	.footer{"
			+"		background-color: #f4f9fc;"
			+"		height: 200px;"
			+"	}"
			+"	.bottom{"
			+"		background-color: #81a9bd;"
			+"		height: 56px;"
			+"		text-align: center;"
			+"		color: #fff;"
			+""
			+"	}"
			+"	.linkus{"
			+"		height: 149px;"
			+"		width: 630px;margin:0 auto;"
			+"		color: #838383;"
			+"		line-height: 25px;"
			+"		letter-spacing: 1.3px;	"
			+"	}"
			+"</style>"
			+"</head>"
			+"<body>"
			+"<div id='contain'>"
			+"	<div id='header'>"
			+"		<span>【2017年02月13日-2017年02月21日】</span>"
			+"	</div>"
			+"	<div style='width: 630px;margin:30px auto;'>"
			+"		<div class='guide'>"
			+"			<span>尊敬的用户，您好！</span><br/>"
			+"			<span>AppCan产品更新周刊:开发工具IDE、插件引擎和企业版SDK开发平台发布新版本，详细更新日志请到以下内容查看即可并及时下载获取最新版本使用。您的关注是我们最大的动力！</span>"
			+"		</div>"
			+"		<br/>"
			+"		<div class='ide'>"
			+"			<span style='font-weight: bold;'>IDE（企业版）</span><br/>"
			+"			<span>MAC IDE下载地址：<a style='text-decoration: underline;' target='_blank' href='http://pan.baidu.com/s/1kUU0Er1'>http://pan.baidu.com/s/1kUU0Er1</a></span>"
			+"			<span style='color:#d66161;margin-left:36px;'>密码：</span><span>46z3</span>"
			+"			<br/>"
			+"			<span>WIN IDE下载地址：<a style='text-decoration: underline;' target='_blank' href='http://pan.baidu.com/s/1bSbc7O'>http://pan.baidu.com/s/1bSbc7O</a></span>"
			+"			<span style='color:#d66161;margin-left:36px;'>密码：</span><span>s9kg</span>"
			+"			<br/>"
			+"			<span>企业版开发指南：<a style='text-decoration: underline;' target='_blank' href='http://pan.baidu.com/s/1sl30rE1'>http://pan.baidu.com/s/1sl30rE1</a></span>"
			+"			<span style='color:#d66161;margin-left:36px;'>密码：</span><span>f7ys</span>"
			+"		</div>"
			+"		<br/>"
			+"		<div class='ide'>"
			+"			<span style='font-weight: bold;'>IDE（大众版）</span><br/>"
			+"			<span style='color:#d66161'>下载地址及操作手册："
			+"			<a style='text-decoration: underline;' target='_blank' href='http://newdocx.appcan.cn/IDE/download'>"
			+"			http://newdocx.appcan.cn/IDE/download"
			+"			</a>"
			+"			</span>"
			+"		</div>"
			+"		<br/>"
			+"		<div class='sdk'>"
			+"			<span style='font-weight: bold;'>SDK应用开发平台（即企业版4.0.0）</span><br/>"
			+"			<span>更新内容：支持4.0的引擎、插件 支持IDE4.0.0版本 支持MVVM框架 适配IOS10及支持ATS UI设计器 UI框架</span><br/>"
			+"		</div>"
			+"		<br/>"
			+"		<div class='engineP'>"
			+"			<span style='font-weight: bold;'>引擎和插件</span><br/>"
			+"			<span style='color:#d66161'>4.0引擎和插件下载地址："
			+"				<a style='text-decoration: underline;' target='_blank' href='http://plugin.appcan.cn/download/appcan-4.0-release/'>"
			+"				http://plugin.appcan.cn/download/appcan-4.0-release/"
			+"				</a>"
			+"			</span><br/>"
			+"			<span style='color:#d66161'>测试用例下载地址："
			+"				<a style='text-decoration: underline;' target='_blank' href='http://plugin.appcan.cn/'>"
			+"				http://plugin.appcan.cn/"
			+"				</a>"
			+"			</span><br/>"
			+"			<span style='color:#d66161'>3.X引擎和插件下载地址：	"
			+"			<a style='text-decoration: underline;' target='_blank' href=' http://plugin.appcan.cn/download/appcan-3.0old-release/ '>"
			+"			 http://plugin.appcan.cn/download/appcan-3.0old-release/ "
			+"			</a>"
			+"			</span>"
			+"		</div>"
			+"		<div class='sdk'>"
			+"			<span style='font-weight: bold;'>iOS引擎</span><br/>"
			+"			<span>更新版本号（有更新）:iOS_Engine_4.1.1_170216_00_Xcode8.1</span><br/>"
			+"			<span>更新内容:增加接口uexWidgetOne.restart;"
			+"			修复一个导致应用自动更新无效的bug；重构侧滑关闭组件;重构引擎动画;支持WidgetSDK；</span><br/>"
			+"			<span style='color:#d66161'>引擎文档：	"
			+"			<a style='text-decoration: underline;' target='_blank' href='http://newdocx.appcan.cn/app-engine/uexWindow#-setloadingimagepath-'>"
			+"			http://newdocx.appcan.cn/app-engine/uexWindow#-setloadingimagepath-"
			+"			</a>"
			+"			</span>"
			+"		</div>"
			+"		<div class='sdk'>"
			+"			<span style='font-weight: bold;'>iOS插件</span><br/>"
			+"			<span>1、插件名称及版本号：uexChart-iOS-4.1.0</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容：（需要依赖最新的引擎4.1.0_170209版本打包）更新依赖的引擎库</span><br/>"
			+"			<span>2、插件名称及版本号：uexBaiduMap-iOS-4.0.3</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容： 修复窗口关闭导致后续的搜索功能异常的问题</span><br/>"
			+"			<span>3、插件名称及版本号：uexAliPay-iOS-4.0.2</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容：添加登录相关接口 </span><br/>"
			+"			<!-- <span>4、插件名称及版本号：uexUnionPay-iOS-4.0.1</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容：SDK更新,支持银联钱包支付,支持ATS</span><br/> -->"
			+"		</div>"
			+"		<div class='sdk'>"
			+"			<span style='font-weight: bold;'>Android引擎：</span><br/>"
			+"			<span>更新版本号（有更新）:android_Engine_4.1.2_170210_01（包含system、x5、crosswork内核）</span><br/>"
			+"			<span>更新内容：X5内核修复，推送相关bug修复：</span><br/>"
			+"			<span>1.增加接口setPopoverVisibility；</span><br/>"
			+"			<span>2.支持沉浸式通知栏（仅安卓系统），详细配置见config文档；</span><br/>"
			+"			<span>3.uexWindow添加setLoadingImagePath 接口支持动态启动图；</span><br/>"
			+"			<span>4.支持自定义错误页面，详细配置参考<a target='_blank' href='http://newdocx.appcan.cn/dev-guide/config.xml'>config文档</a>；</span><br/>"
			+"			<span style='color:#d66161'>引擎文档：	"
			+"			<a style='text-decoration: underline;' target='_blank' href='http://newdocx.appcan.cn/app-engine/uexWindow'>"
			+"			http://newdocx.appcan.cn/app-engine/uexWindow"
			+"			</a>"
			+"			</span>"
			+"		</div>"
			+"		<div class='sdk'>"
			+"			<span style='font-weight: bold;'>Android插件</span><br/>"
			+"			<span>1、插件名称及版本号：uexTencentLVB-android-4.0.2</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容：修复支付成功之后没有回调的问题</span><br/>"
			+"			<span>2、插件名称及版本号：uexEasemob-android-4.1.3</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容： .(依赖引擎4.1)支持华为推送，查看<a target='_blank' href='http://newdocx.appcan.cn/plugin-API/SDK/uexEasemob'>环信文档</a></span><br/>"
			+"			<span>3、插件名称及版本号：uexHuaweiPush-android-4.0.0</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容： 新增华为推送插件, 不能单独使用，必须配合环信使用，查看<a target='_blank' href='http://newdocx.appcan.cn/plugin-API/SDK/uexEasemob'>环信文档</a></span><br/>"
			+"			<span>4、插件名称及版本号：uexAliPay-android-4.0.3</span><br/>"
			+"			<span style='margin-left:25px;'>更新内容：添加登录相关接口</span><br/>"
			+"		</div>"
			+"	</div>"
			+"	<div class='footer'>"
			+"		<div class='linkus'>"
			+"			<div style='float:right;margin-right: 95px;margin-top: 25px;'>"
			+"				<span style='font-weight: bold;vertical-align: top;'>我们的微信</span>"
			+"				<img style='margin-left: 15px;' src='http://bbs.appcan.cn/data/attachment/album/201702/20/153255igasf2fr6xxw85gf.png'>"
			+"			</div>"
			+"			<span style='font-weight: bold;padding-top: 25px;display: inline-block;'>我们的服务</span><br/>"
			+"			<span style='padding-top: 15px;display: inline-block;'>客服系统："
			+"			<a style='text-decoration: underline;' target='_blank' href='http://service.appcan.cn'>"
			+"			 service.appcan.cn"
			+"			</a>"
			+"			</span><br/>"
			+"			<span>产品咨询："
			+"			<a style='text-decoration: underline;'>"
			+"			 400-040-1766"
			+"			</a>"
			+"			</span>"
			+"		</div>"
			+"		<div class='bottom'>"
			+"			<div style='padding-top: 20px;'>"
			+"			正益移动互联科技股份有限公司版权所有&nbsp;&nbsp;&nbsp;&nbsp;京ICP备11006447号&nbsp;&nbsp;&nbsp;&nbsp;京公网安备:11010802018489"
			+"			</div>"
			+"		</div>"
			+"	</div>"
			+"</div>"
			+"</body>"
			+"</html>";
	public static void main(String[] args) throws UnsupportedEncodingException{   
		
//		String mailSubject = PropertiesLoader.getText("mail.subject", "mail.properties");
//		log.info(mailSubject);
//		System.exit(0);
        //这个类主要是设置邮件   
     MailSenderInfo mailInfo = new MailSenderInfo();    
     mailInfo.setMailServerHost("smtp.qq.com");    
     mailInfo.setMailServerPort("25");    
     mailInfo.setValidate(true);    
//     mailInfo.setUserName("jingjian.wu@zymobi.com");  
     mailInfo.setUserName("xt@zymobi.com");
//     mailInfo.setPassword("sepoct09");//您的邮箱密码    
     mailInfo.setPassword("XTKF@3g2win.com");
//     mailInfo.setFromAddress("jingjian.wu@zymobi.com");    
     mailInfo.setFromAddress("xt@zymobi.com");  
//     mailInfo.setToAddress("wjj_005@126.com");
     mailInfo.setToAddress("ying.zhang@zymobi.com");
//     mailInfo.setToAddress("289306290@qq.com");
     mailInfo.setSubject("发送邮件QQ邮箱图片不显示的问题,有时间帮着瞅一瞅哈");    
//     mailInfo.setContent("设置邮箱内容 如http://www.guihua.org 中国桂花网 是<a href='http://www.baidu.com'>中国最大桂花网站</a>==");    
     StringBuffer content = new StringBuffer();
     
     content.append(getEmailHeader())
     .append(getEmailCenter("张三李四", "这里是内容<a href=\"http://www.baidu.com\">点击的地方</a>这里还是内容"))
     .append(getEmailFooter());
     
//     mailInfo.setContent(content.toString());
     mailInfo.setContent(neirong);
     mailInfo.setNick("协同开发");
     
    /* //-------------------
     String mailHost = PropertiesLoader.getText("mail.host", "mail.properties");
		String mailPort = PropertiesLoader.getText("mail.port", "mail.properties");
		String mailFrom = PropertiesLoader.getText("mail.from", "mail.properties");
		String mailPwd = PropertiesLoader.getText("mail.password", "mail.properties");
		String mailValid = PropertiesLoader.getText("mail.smtp.auth", "mail.properties");
		String mailSubject = PropertiesLoader.getText("mail.subject", "mail.properties");
		String mailUserName = PropertiesLoader.getText("mail.username", "mail.properties");
		
		mailInfo.setContent(SendMailTools.getEmailHeader()+"这里是内容"+SendMailTools.getEmailFooter());;
		
		
		mailInfo.setMailServerHost(mailHost);
		mailInfo.setMailServerPort(mailPort);
		mailInfo.setValidate(Boolean.parseBoolean(mailValid));
		mailInfo.setUserName(mailFrom);
		mailInfo.setPassword(mailPwd);
		mailInfo.setNick(mailUserName);
		mailInfo.setFromAddress(mailFrom);
		mailInfo.setToAddress(mailInfo.getToAddress());
		mailInfo.setSubject(null==mailInfo.getSubject()||"".equals(mailInfo.getSubject())?mailSubject:mailInfo.getSubject());
     //--------------------
     */
     String s="<p style=\"line-height:48px;margin-top:25px;margin-bottom:0\">Hi，fhx-网省</p> <p style=\"line-height:30px;margin:0\"> <span>test总部</span>给您指派了Bug：【<span><a href='http://xttest16.appcan.cn/coopDevelopment/myBug?bugId=31'>laosd</a></span>】，请知晓。 </p>";
     System.out.println(getEmailHeader()+s+getEmailFooter());
        //这个类主要来发送邮件   
     SimpleMailSender sms = new SimpleMailSender();   
//         sms.sendTextMail(mailInfo);//发送文体格式    
         sms.sendHtmlMail(mailInfo);//发送html格式   
   }  
	
	
	
	public  void sendMailByAsynchronousMode(final MailSenderInfo  mailBean ) {  
		log.info("prepare sendMail to-->"+mailBean.getToAddress());
        taskExecutor.execute(new Runnable() {  
            public void run() {  
                try {  
                	mailBean.setContent(SendMailTools.getEmailHeader()+mailBean.getContent()+SendMailTools.getEmailFooter());
                	boolean sendResult = sendMail(mailBean);  
                	if(!sendResult){
                		log.info("try sendMail to-->"+mailBean.getToAddress()+" 2 times");
                		sendResult = sendMail(mailBean); 
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 3 times");
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 4 times");
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 5 times");
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 6 times");
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 7 times");
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 8 times");
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 9 times");
                    		sendResult = sendMail(mailBean); 
                    	}
                		if(!sendResult){
                			log.info("try sendMail to-->"+mailBean.getToAddress()+" 10 times");
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
		if(sendMailSwitch.equals("off")){
			return true;
		}
		if(null==mailInfo || null==mailInfo.getContent() || mailInfo.getContent().equals("")){
			throw new RuntimeException("邮件内容不可以为空");
		}
		String mailHost = PropertiesLoader.getText("mail.host", "mail.properties");
		String mailPort = PropertiesLoader.getText("mail.port", "mail.properties");
		String mailFrom = PropertiesLoader.getText("mail.from", "mail.properties");
		String mailPwd = PropertiesLoader.getText("mail.password", "mail.properties");
		String mailValid = PropertiesLoader.getText("mail.smtp.auth", "mail.properties");
		String mailSubject = PropertiesLoader.getText("mail.subject", "mail.properties");
		String mailUserName = PropertiesLoader.getText("mail.username", "mail.properties");
		
		
		mailInfo.setMailServerHost(mailHost);
		mailInfo.setMailServerPort(mailPort);
		mailInfo.setValidate(Boolean.parseBoolean(mailValid));
		mailInfo.setUserName(mailFrom);
		mailInfo.setPassword(mailPwd);
		mailInfo.setNick(mailUserName);
		mailInfo.setFromAddress(mailFrom);
		mailInfo.setToAddress(mailInfo.getToAddress());
		mailInfo.setSubject(null==mailInfo.getSubject()||"".equals(mailInfo.getSubject())?mailSubject:mailInfo.getSubject());
		SimpleMailSender sms = new SimpleMailSender();   
		return sms.sendHtmlMail(mailInfo);//发送html格式   
	}
}
