package cn.mointe.vaccination.tools;

public class StringUtils {

	public StringUtils() {

	}

	/**
	 * 判断字符串是否为null或空
	 * 
	 * @param string
	 * @return true:为空或null;false:不为空或null
	 */
	public static boolean isNullOrEmpty(String string) {
		boolean flag = false;
		if (null == string || string.trim().length() == 0) {
			flag = true;
		}
		return flag;
	}

}
