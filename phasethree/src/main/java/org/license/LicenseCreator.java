package org.license;

public class LicenseCreator {

	public static String createLicense(License license, String key) {
		String jsonStr = license.getJsonStr();
		return Rc4Encrypt.encry_RC4_string(jsonStr, key).toUpperCase();
	}

	public static String decLicense(String license, String key) {
		return Rc4Encrypt.decry_RC4(license, key);
	}

	public static String getKeyMD5(String ip, String mac, String productName) {
		String keyMD5 = "";

		String desIp = "";
		String desMac = "";
		String salt = "";
		desIp = CommonTools.ipToHexStr(ip);
		desMac = CommonTools.fomatMac(mac);
		salt = CommonTools.getSalt(productName);

		// System.out.println("md5 or:"+desIp + desMac + code + productName +
		// salt);

		// keyMD5 = Md5Encrypt.md5(desIp + desMac + code + productName + salt);
		// SDK不限制ip
		if ("SDK".equals(productName)) {
			keyMD5 = Md5Encrypt.md5(desMac + productName + salt);
		} else {
			keyMD5 = Md5Encrypt.md5(desIp + desMac + productName + salt);
		}
		return keyMD5.toUpperCase();
	}
	// public static void main(String[] args) {
	//
	// // String keyMD51 = getKeyMD5("192.168.1.4", "00:15:17:D3:C5:7C",
	// "","MAM");
	// String keyMD51 = getKeyMD5("192.168.1.166", "40:16:7E:25:04:7E", "MDM");
	// System.out.println(keyMD51);
	//
	// String licenseStr =
	// "433EB1BEF7ECC2453D2626B261324D9ECE6C562D07AD82793AF828E528EEEA4DC7784870EF2D428A69C8101CD8F7A70CCB61BAFFBA892FD534BBDBAFD9391098357760A436FA1C18941521E11D3FD3EB5AB545EB3E5EF464B3C81AAD3A0540DD51A4C926B72C6674AC1A0FB10AA71C0BFEEC907EF541A9D19C6FF0D75AAF84FE76CC05A1A9";
	// String keyMD5 = "86D9B5A812166C594D341EFE937F3F9C";
	// String decLicense = LicenseCreator.decLicense(licenseStr, keyMD51);
	// System.out.println("明文license:" + decLicense);
	// }

}
