package org.zywx.cooldev.util;

import org.apache.log4j.Logger;

/**
 * 版本工具类
 * @author yang.li
 *
 */
public class VersionUtil {
	
	private static Logger log = Logger.getLogger(VersionUtil.class.getName());

	/**
	 * 返回预期顺序是否正确<br>
	 * formerVersionNo 小于 laterVersionNo 返回 true
	 * formerVersionNo 大于等于 laterVersionNo 返回 false
	 * 参数非法，返回false
	 * @param formerVersionNo 较前提交的版本
	 * @param laterVersionNo  较后提交的版本
	 * @return
	 */
	public static boolean compare(String formerVersionNo, String laterVersionNo) {
		
		if(formerVersionNo == null || laterVersionNo == null) {
			return false;
		}
		
		int formerLen = formerVersionNo.length();
		int laterLen = laterVersionNo.length();

		for(int i = 0; i < formerLen; i++) {
			int charFormerVal = formerVersionNo.charAt(i);
			if(laterLen > i) {
				int charLaterVal = laterVersionNo.charAt(i);
				if(charLaterVal > charFormerVal) {
					return true;
				} else if(charLaterVal == charFormerVal) {
					// 进行下一个字符的比较
					continue;
				} else {
					return false;
				}
				
			} else {
				// 前段比较相同，当formerVersionNo多出字符，则为靠后版本，返回false
				return false;
			}
		}
		
		return false;
	}
	
	/**
	 * 版本号增加1
	 * @param baseVersionNo
	 * @return
	 */
	public static String plusVersionNo(String baseVersionNo) {
		
		if(baseVersionNo == null) {
			return null;
		}
		
		if (!baseVersionNo.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
			return null;
		}

		int idx = baseVersionNo.lastIndexOf(".");
		if (idx != -1) {
			String suffix = baseVersionNo.substring(idx + 1);

			int minor = Integer.parseInt(suffix);
			minor++;
			if (minor <= 9999) {
				String clip = (minor < 10) ? "000" + minor
						: (minor < 100 ? "00" + minor : (minor < 1000 ? "0"
								+ minor : "" + minor));
				return baseVersionNo.substring(0, idx + 1) + clip;
			}

		}
		return null;
	}
	
	public static void main(String[] args) {
//		String formerVersionNo = "22.2242.0001";
//		String laterVersionNo = "22.2232.0002";
//		log.info(String.format("compare \"%s\" and \"%s\", result %s", formerVersionNo, laterVersionNo, VersionUtil.compare(formerVersionNo, laterVersionNo)));

		String baseVersionNo = "00.01.9998";
		log.info(VersionUtil.plusVersionNo(baseVersionNo));
	}
}
