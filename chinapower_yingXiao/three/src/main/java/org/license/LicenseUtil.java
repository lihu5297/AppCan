/**
 * $Id$
 * Copyright: 正益无线2014 版权所有
 */

package org.license;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zywx.appdo.common.utils.crypt.RC4;


public class LicenseUtil {

	protected Log logger = LogFactory.getLog(getClass());

	// private String shUrl;

	public LicenseUtil() {
	}

	/*
	 * public LicenseUtil(String shUrl){ this.shUrl = shUrl; }
	 */

	/**
	 * 
	 * license文件是否存在
	 *
	 * @param filePath
	 * @return
	 */
	public boolean hasLicenseFile(String filePath) {
		File file = new File(filePath);
		return (file.exists() && file.isFile());
	}

	/**
	 * 
	 * 解析license文件
	 *
	 * @param paramInputStream
	 * @return
	 */
	public JSONObject initLicenseParser(String filePath) {
		Reader reader = null;
		BufferedReader br = null;
		InputStream paramInputStream = null;
		try {
			paramInputStream = new FileInputStream(filePath);
			reader = new InputStreamReader(paramInputStream, "GBK");
			br = new BufferedReader(reader);
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				logger.debug("line====>" + line);
				sb.append(line);
			}
			return decryLicense(sb.toString());
		} catch (Exception e) {
			logger.error("initLicenseParser is error!===>", e);
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (br != null) {
					br.close();
				}
				if (paramInputStream != null) {
					paramInputStream.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 
	 * 解密license
	 *
	 * @param licenseStr
	 * @return
	 */
	private JSONObject decryLicense(String licenseStr) {
		String key1 = getTryMD5Key();
		List<String> keys = getMD5Key();

		String key1Result = RC4.decry_RC4(licenseStr, key1);
		JSONObject jsObj1 = null;
		JSONObject jsObj2 = null;
		try {
			jsObj1 = JSONObject.fromObject(key1Result);
		} catch (Exception e) {
		}
		if (jsObj1 == null) {
			for (int i = 0; i < keys.size(); i++) {
				logger.debug("===================filter");
				try {
					jsObj2 = JSONObject.fromObject(RC4.decry_RC4(licenseStr,
							keys.get(i)));
					logger.debug("---------------------"+keys.get(i));
					return jsObj2;
				} catch (Exception e) {

					e.printStackTrace();
					continue;
				}
				
			}logger.debug("===================filter  end");

			return jsObj2;
		} else {
			return jsObj1;
		}
	}

	private List<String> getMD5Key() {
		String k = "å½åé¢å";
		List<String> ls = new ArrayList<String>();
		 Set<String> ips = RuntimeProcessUtils.getIPAddress();
		logger.debug("get ip addr in getMD5Key is" + ips.toString());

		Set<String> macs = RuntimeProcessUtils.getMACAddressSet();
		Iterator<String> it = macs.iterator();
		while (it.hasNext()) {
			String mac = it.next();
			Iterator<String> ipit = ips.iterator();
			while (ipit.hasNext()) {
				String	ip=ipit.next();
				if(StringUtils.isNotBlank(ip)){
					logger.debug("get ip MAC in getMD5Key is" + mac);
					String origin = ipToHexStr(ip) + mac.toUpperCase() + "EMM" + k;
					logger.debug("md5 origin=====>" + origin);
					String md5 = Md5Encrypt.md5(origin);
					ls.add(md5.toUpperCase());
				}
				
			}
	
		}

		return ls;
	}

	private String getTryMD5Key() {
		String k = "å½åé¢å";
		String ip = "255.255.255.255";
		logger.debug("get tryVersion ip addr in getTryMD5Key is" + ip);
		String mac = "FF:FF:FF:FF:FF:FF";
		logger.debug("get tryVersion ip MAC in getTryMD5Key is" + mac);
		String origin = ipToHexStr(ip) + mac.toUpperCase() + "EMM" + k;
		logger.debug("trymd5 origin=====>" + origin);
		String md5 = Md5Encrypt.md5(origin);
		return md5.toUpperCase();
	}

	/**
	 * 
	 * 调用shell获取ip和mac
	 *
	 * @return
	 */
	/*
	 * private String getNetworkInfo(){ Process process =null; InputStream is
	 * =null; InputStreamReader isr =null; try { process =
	 * Runtime.getRuntime().exec(shUrl); is = process.getInputStream(); isr=new
	 * InputStreamReader(is); BufferedReader br = new BufferedReader(isr);
	 * String line=null; StringBuilder sb = new StringBuilder(); while ((line =
	 * br.readLine()) != null) { sb.append(line); } return sb.toString(); }
	 * catch (Exception e) { logger.error("get ip and mac error!",e); return
	 * null; }finally{ try { if(is != null) is.close(); if(isr != null)
	 * isr.close(); if(process != null) process.destroy(); } catch (IOException
	 * e) { logger.error(e); } } }
	 */

	private String ipToHexStr(String ip) {
		String desIp = "";
		String temp = "";
		if (null == ip) {
			ip = "0.0.0.0";
		}
		String[] ips = ip.split("[.]");
		for (int i = 0; i < ips.length; i++) {
			temp = ips[i];
			temp = Integer.toHexString(Integer.parseInt(temp));
			temp = temp.length() == 1 ? ("0" + temp) : temp;
			if (i < (ips.length - 1))
				desIp += temp + ":";
			else
				desIp += temp;
		}
		return desIp.toUpperCase();
	}

	/**
	 * 
	 * 判断过期时间 true == 过期
	 * 
	 * @param paramString
	 * @return
	 */
	public boolean checkEndDate(String paramString) {
		paramString = paramString + " 00:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long l1 = 0L;
		try {
			l1 = sdf.parse(paramString).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date localDate = new Date();
		long l2 = localDate.getTime();
		logger.debug("l2:" + l2 + "-----l1:" + l1);
		return (l2 > l1);
	}

	public static void main(String[] args) {
		LicenseUtil util =  new LicenseUtil();
		//JSONObject a = util.decryLicense("1C4B274322DA4E883B3EC6748D797A5C62AFBA466922BF3897D8B369DDEE038CD11AEA82238065400B1CF2AF2388DDF6FEC8319D65352CB66826F5CDCECEDB987C1541A48BC8A3EAB3CE14604A68AA9D62E5178FA2DDE8E9963E607FC33FBEFBBEE729F54F9B831CE8B28D24B79EB587A1CECB09FA6C7A133F6F0A763EA517307256986F3DC97C679DF3FDB269249804DC10A3358A");
		//JSONObject a1 = util.decryLicense("C1DF3556E5DF330376E41BF095995CDF72A0F7313F3FA701C8B164C00BE7F82CC37FD2023732F838E96300CF9B66E0DB25E8C314EA5A4542E3EC8551FAF144D8DCAC3291713ACE7C3FA9A2218BDF4061E770115A6FCA8E88DB847E978751586B3B272237AC1C3E6EA20DDCEB1A0B01A089AA2BA5ADD6AF8379B8E4EE4401374BA566DDB714606A59B8F02174593631500D3BAE3588");
		JSONObject a2 = util.decryLicense("1C4B274322DA4E883B3EC6748C797B5A62AEBE466922BF3897D8B369DDEE038CD11AEA82238165400F1CF1A92388DDF6FEC8319D65352CB66826F5CDCECEDB987C1541A48BC8A3EAB3CE14604A68AA9D62E5178FA2DDE8E9963E607FC33FBEFBBEE729F54F9B831CE8B28D24B79EB587A1CECB09FA6C7A133F6F0A763EA517307256986F3DC97C679DF3FDB269249804DC10A3358A");
		System.out.println(a2.toString());
	}

	public JSONObject initLicenseParser(String fileUrl, String ip, String mac) {

		Reader reader = null;
		BufferedReader br = null;
		InputStream paramInputStream = null;
		try {
			paramInputStream = new FileInputStream(fileUrl);
			reader = new InputStreamReader(paramInputStream, "GBK");
			br = new BufferedReader(reader);
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				logger.debug("line====>" + line);
				sb.append(line);
			}
			return decryLicense(sb.toString(),ip,mac);
		} catch (Exception e) {
			logger.error("initLicenseParser is error!===>", e);
			return null;
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
				if (br != null) {
					br.close();
				}
				if (paramInputStream != null) {
					paramInputStream.close();
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
	
	}

	private JSONObject decryLicense(String licenseStr, String ip, String mac) {

		String key1 = getTryMD5Key();
		String keys = getMD5Key(ip,mac);

		String key1Result = RC4.decry_RC4(licenseStr, key1);
		JSONObject jsObj1 = null;
		JSONObject jsObj2 = null;
		try {
			jsObj1 = JSONObject.fromObject(key1Result);
		} catch (Exception e) {
		}
		if (jsObj1 == null) {
			logger.debug("===================filter");
			try {
				jsObj2 = JSONObject.fromObject(RC4.decry_RC4(licenseStr,keys));
				logger.debug("---------------------"+keys);
				return jsObj2;
			} catch (Exception e) {
	
			}
				
			logger.debug("===================filter  end");

			return jsObj2;
		} else {
			return jsObj1;
		}
	
	}

	private String getMD5Key(String ip, String mac) {
		String k = "å½åé¢å";
		logger.debug("get ip addr in getMD5Key is" + ip);
		logger.debug("get ip MAC in getMD5Key is" + mac);
		String origin = ipToHexStr(ip) + mac.toUpperCase() + "EMM" + k;
		logger.debug("md5 origin=====>" + origin);
		String md5 = Md5Encrypt.md5(origin);
		return md5.toUpperCase();
	
	}

}
