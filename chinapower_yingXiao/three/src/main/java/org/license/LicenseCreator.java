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
	 public static void main(String[] args) {
	
	 // String keyMD51 = getKeyMD5("192.168.1.4", "00:15:17:D3:C5:7C","","MAM");
	 String keyMD51 = getKeyMD5("192.168.1.83", "40:16:7E:25:04:7E", "MDM");
	 System.out.println(keyMD51);
	
	 String licenseStr = "BD7A9352C31DCD3A0755B7BFC805CA373679EB81AB767D387893532E544B1E8187CD64D0A6FCDD705B2EDA0319815695084248C273EB4CAE1AA4E29AC52CF3F86CB573E7A3D080E4C53BE840E26E7EEC60D330EDF21F5223835C6887DDB7978FC58F3804A978719783070E540D6FC96EB89D06BA4C0C72B6289F51920C2186ED01C75E5E28464CE8DA48FCAEF3E389D601A19E1C9F";
	 String keyMD5 = "86D9B5A812166C594D341EFE937F3F9C";
	 String decLicense = LicenseCreator.decLicense(licenseStr, keyMD51);
	 System.out.println("明文license:" + decLicense);
	 }

}
