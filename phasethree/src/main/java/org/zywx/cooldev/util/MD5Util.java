	/**  
     * @author jingjian.wu
     * @date 2015年9月17日 下午7:19:50
     */
    
package org.zywx.cooldev.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.util.StringUtils;


    /**
 * @author jingjian.wu
 * @date 2015年9月17日 下午7:19:50
 */

public class MD5Util {
	
	 private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
         '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	
	public static String characterEncoding;
	
	public static String MD5(String password) {
		if (password == null) {
			return null;
		}
	
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		
			if (StringUtils.hasText(characterEncoding)) {
				messageDigest.update(password.getBytes(characterEncoding));
			} else {
				messageDigest.update(password.getBytes());
			}
			final byte[] digest = messageDigest.digest();
		
			return getFormattedText(digest);
		} catch (final NoSuchAlgorithmException e) {
			throw new SecurityException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	* Takes the raw bytes from the digest and formats them correct.
	*
	* @param bytes the raw bytes from the digest.
	* @return the formatted bytes.
	*/
	private static String getFormattedText(final byte[] bytes) {
		final StringBuilder buf = new StringBuilder(bytes.length * 2);
		
		for (int j = 0; j < bytes.length; j++) {
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}
	
	public void setCharacterEncoding(final String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	/*public final static String MD5(String s) {  
        char hexDigits[] = { '0', '1', '2', '3', '4',  
                             '5', '6', '7', '8', '9',  
                             'A', 'B', 'C', 'D', 'E', 'F' };  
        try {  
            byte[] btInput = s.getBytes();  
     //获得MD5摘要算法的 MessageDigest 对象  
            MessageDigest mdInst = MessageDigest.getInstance("MD5");  
     //使用指定的字节更新摘要  
            mdInst.update(btInput);  
     //获得密文  
            byte[] md = mdInst.digest();  
     //把密文转换成十六进制的字符串形式  
            int j = md.length;  
            char str[] = new char[j * 2];  
            int k = 0;  
            for (int i = 0; i < j; i++) {  
                byte byte0 = md[i];  
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
                str[k++] = hexDigits[byte0 & 0xf];  
            }  
            return new String(str);  
        }  
        catch (Exception e) {  
            e.printStackTrace();  
            return null;  
        }  
    }  */
    public static void main(String[] args) {  
        System.out.print(MD5Util.MD5("123456").toLowerCase());  
    }  
}
