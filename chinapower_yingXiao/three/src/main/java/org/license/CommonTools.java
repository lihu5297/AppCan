package org.license;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommonTools {
	
	public static String ipToHexStr(String ip){
		String desIp = "";
		String temp = "";
		if(null==ip){
			ip = "127.0.0.1";
		}
		String[] ips = ip.split("[.]");
		for(int i=0;i<ips.length;i++){
		    temp = ips[i];
		    temp = Integer.toHexString(Integer.parseInt(temp));
		    temp = temp.length()==1?("0"+temp):temp;
		    if(i<(ips.length-1))
		    	desIp += temp +":";
		    else
		    	desIp += temp;
		}
		return desIp.toUpperCase();
	}
	
	public static String fomatMac(String mac){
		if (null==mac) mac = "";
		return mac.toUpperCase();
	}
	
	public static String getSalt(String productName){
		String salt = "";
		if("MAM".equals(productName)){
			salt = Constants.SALT_MAM;
		} else if ("SDK".equals(productName)){
			salt = Constants.SALT_SDK;
		} else if ("MCM".equals(productName)){
			salt = Constants.SALT_MCM;
		}else if ("MAS".equals(productName)){
			salt = Constants.SALT_MAS;
		}else if ("MDM".equals(productName)){
			salt = Constants.SALT_MDM;
		}else if ("EMM".equals(productName)){
			salt = Constants.SALT_EMM;
		}else if ("MMS".equals(productName)){
			salt = Constants.SALT_MMS;
		}else if ("OMM".equals(productName)){
			salt = Constants.SALT_OMM;
		}else if ("MEM".equals(productName)){
			salt = Constants.SALT_MEM;
		}else if ("MBAAS".equals(productName)){
			salt = Constants.SALT_MBAAS;
		}else if ("APPIN".equals(productName)){
			salt = Constants.SALT_APPIN;
		}
		

		return salt;
	}
	
	public static String getCurIpAddr() {
		String addr = "127.0.0.1";
        InetAddress address;
		try {
			address = InetAddress.getLocalHost();
			addr = address.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        return addr;
    }
	
	public static String getCurMacAddr() {
		String addr = "00:11:22:33:44:55";
		Set<String> macSet = RuntimeProcessUtils.getMACAddressSet();
		if(macSet.size()>0){
			addr = macSet.iterator().next();
		}
        return addr;
    }
	
	public static boolean checkDate(String date){
		String regex = "^\\d{4}(-)\\d{2}(-)\\d{2}$";
		Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(date);
        return matcher.matches(); 
	}
	
	public static boolean checkMacAddr(String mac){
		//支持以空格,-,:分隔
		//String regex = "^([0-9a-fA-F]{2})(([/\\s:-][0-9a-fA-F]{2}){5})$";
		//只支持:分隔
		String regex = "^([0-9a-fA-F]{2})(([/:][0-9a-fA-F]{2}){5})$"; 
		Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(mac);
        return matcher.matches(); 
	}
	
	public static boolean checkIpAddr(String ip){
		String regex="(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\." +  
                     "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\." +  
                     "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\." +  
                     "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])";   
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(ip);
        return matcher.matches(); 
	}
	
	public static boolean checkNumber(String number){
		String regex="^(-?)[1-9]+\\d*|0";
		Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(number);
        return matcher.matches(); 
	}
	public static boolean checkChinese(String info){
		String regex = "[\u4e00-\u9fa5]";
		Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(info);
        return matcher.matches(); 
	}
	public static String getCurDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date());
	}
	
	 /**
	  * 获取当前日期的后三个月日期
	  * @return 三个月后的时间
	  * @author yang.lu
	  */
	 public static String getLastMonth(){
	  Date   date=new   Date();
	  Calendar   cal=Calendar.getInstance();
	  cal.setTime(date);
	  cal.add(Calendar.MONTH,3);
	  Date   otherDate=cal.getTime();
	  SimpleDateFormat   dateFormat=new   SimpleDateFormat("yyyy-MM-dd ");
	//  System.out.println( "today:   "+dateFormat.format(date)+ "   3   months   after:   "+dateFormat.format(otherDate));
	  return dateFormat.format(otherDate);
	 }
	
	public static String checkInfo(String ip, String mac,String  beginDate,String date, String appcount, String devcount, String info,String entcount){
		String result = "";
		if(!checkIpAddr(ip)){
			result = "IP输入格式不正确，例如：192.168.1.1！";
		} else if (!checkMacAddr(mac)){
			result = "MAC输入格式不正确，例如：00:11:22:33:44:55！";
		}else if (!checkDate(beginDate)){
			result = "起始日期输入格式不正确，格式因为yyyy-MM-dd！";
		}else if (!checkDate(date)){
			result = "失效日期输入格式不正确，格式因为yyyy-MM-dd！";
		} else if (!checkNumber(appcount)){
			result = "应用最大数输入格式不正确，应为整数！";
		} else if (!checkNumber(devcount)){
			result = "终端最大数输入格式不正确，应为整数！";
		} else if (info.length()>20){
			result = "授权信息不能超过20个字符!";
		}else if (!checkNumber(entcount)){
			result = "输入格式不正确，应为整数！";
		}
		return result;
	}
	
//	public static void main(String[] args) {
//		System.out.println(checkChinese("文"));
//	}
	
	public static String getInfo(String RC4info,String productName){
		String info = "";
	    String infoMd5key = Md5Encrypt.md5(productName+getSalt(productName));
        info = Rc4Encrypt.decry_RC4(RC4info, infoMd5key);		
		return info;
	}

}
