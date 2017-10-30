package org.license;

import java.security.MessageDigest;

public class Md5Encrypt {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	// 将字节数组转换为十六进制字符串
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	// 将字节转换为十六进制字符
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static String md5(String origin) {
		String resultString = null;
		try {
			// MessageDigest 类为应用程序提供信息摘要算法的功能，如 MD5 或 SHA 算法
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(origin
					.getBytes("GBK")));
		} catch (Exception ex) {
		}
		return resultString;
	}

	public static void main(String[] args) {
		// System.out.println(md5("SDKçè·¨å¹³å°"));

	}

}
