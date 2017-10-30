
    /**  
     * @Description: 
     * @author jingjian.wu
     * @date 2015年8月22日 下午5:44:44
     */
    
package org.zywx.cooldev.util;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


    /**
 * @Description: 
 * @author jingjian.wu
 * @date 2015年8月22日 下午5:44:44
 *
 */

public class Tools {
	private static Logger log = Logger.getLogger(Tools.class.getName());

	/**
	 * 判断一个字符串是否是数字
	 * @param str
	 * @return boolean
	 * @user jingjian.wu
	 * @date 2015年8月22日 下午5:45:43
	 * @throws
	 */
	public static boolean isNum(String str){
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 判断文件名称是否合法,在linux系统中的文件命名
	 * @param fileName
	 * @return boolean
	 * @user jingjian.wu
	 * @date 2015年8月29日 下午1:44:20
	 * @throws
	 */
	public static boolean isFileName(String fileName){
//		String expression = "^([a-zA-Z0-9_\\.]|[\u4e00-\u9fa5])+$";
//		return matchingText(expression, fileName);
		return true;
//		return fileName.matches( 
//		           "[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
	}
	
	/**
	 * 正則匹配
	 * @param expression
	 * @param text
	 * @return boolean
	 * @user jingjian.wu
	 * @date 2015年8月29日 下午2:02:33
	 * @throws
	 */
	public static boolean matchingText(String expression, String text) {
		boolean bool = false;
		if (expression != null && !"".equals(expression) && text != null
				&& !"".equals(text)) {
		   Pattern p = Pattern.compile(expression); // 正则表达式
		   Matcher m = p.matcher(text); // 操作的字符串
		   bool = m.matches();
		}
		return bool;
	 }
	
	// GENERAL_PUNCTUATION 判断中文的“号  
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号  
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号  
    private static final boolean isChinese(char c) {  
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
            return true;  
        }  
        return false;  
    }  
  
    /**
     * 判断字符串中是否包含中文
     * @user jingjian.wu
     * @date 2015年10月17日 下午3:37:54
     */
    public static final boolean isChinese(String strName) {  
        char[] ch = strName.toCharArray();  
        for (int i = 0; i < ch.length; i++) {  
            char c = ch[i];  
            if (isChinese(c)) {  
                return true;  
            }  
        }  
        return false;  
    }  
	
	public static void main(String[] args) {
//		log.info(isFileName("1"));
//		log.info(isFileName("东"));
//		log.info(isFileName("东@方时代"));
//		log.info(isFileName("东方时代_abc.doc"));
//		log.info(isFileName("东方时代ffda.jpg"));
		log.info(isChinese("f的"));
	}
	
	public static String getRequestSrcIp(HttpServletRequest request) {
        
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null) {
            final int idx = ip.indexOf(',');
            if (idx > -1) {
                ip = ip.substring(0, idx);
            }
        }
        return ip;
    }
	
	public static boolean isValidRequestIp(String srcIp, List<String> validIps) {
	        if (validIps == null || validIps.size() == 0) {
	            return false;
	        }
	        Iterator<String> iter = validIps.iterator();
	        while (iter.hasNext()) {
	            String ipRegx = iter.next();
	            if (srcIp.matches(ipRegx)) {
	                return true;
	            }
	        }
	        return false;
	    }

	public static String sqlFormat(String sql){
		if(null==sql){
			return "";
		}
		return sql.trim().replace("%", "\\%").replace("_", "\\_");
	}
	
	public static String sqlFormatPerCent(String sql){
		if(null==sql){
			return "";
		}
		return sql.trim().replace("%", "\\%%");
	}
}
