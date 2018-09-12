package t.backstage.models.context;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/***
 * 字符串处理的工具类
 * @author zhangj
 * @date 2018年8月23日 下午6:35:12
 * @email zhangjin0908@hotmail.com
 */
public class StringUtils extends t.sql.utils.StringUtils {
	
	
	/**
	 * 判断当前字符串是否为空
	 * @param txt
	 * @return 如果为空返回true,否则返回false
	 */
	public static boolean isNull(String txt) {
		if(txt == null || "".equals(txt)) {
			return true;
		}else {
			return false;
		}
	}
	public static String getPinYinHeadChar(String zn_str) {
		return getPinYinHeadChar(zn_str,true);
	}
	/**
	 * 将中文字符串转换为首字母拼音
	 * @param zn_str   要转换的字符串
	 * @param capital  是否大写,如果为true表示大写,false表示小写
	 * @return  返回转换好的结果集
	 */
	public static String getPinYinHeadChar(String zn_str, boolean capital) {
		if (zn_str != null && !"".equalsIgnoreCase(zn_str.trim())) {
			char[] strChar = zn_str.toCharArray();
			HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
			if (capital) {
				hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
			} else {
				hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			}
			hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
			StringBuffer pyStringBuffer = new StringBuffer();
			for (int i = 0; i < strChar.length; i++) {
				char c = strChar[i];
				char pyc = strChar[i];
				if (String.valueOf(c).matches("[\\u4E00-\\u9FA5]+")) {
					try {
						String[] pyStirngArray = PinyinHelper.toHanyuPinyinStringArray(strChar[i],
								hanYuPinOutputFormat);
						if (null != pyStirngArray && pyStirngArray[0] != null) {
							pyc = pyStirngArray[0].charAt(0);
							pyStringBuffer.append(pyc);
						}
					} catch (BadHanyuPinyinOutputFormatCombination e) {
						e.printStackTrace();
					}
				}else {
					pyStringBuffer.append(strChar[i]);
				}
			}
			return pyStringBuffer.toString();
		}else {
			return zn_str;
		}
	}
}

