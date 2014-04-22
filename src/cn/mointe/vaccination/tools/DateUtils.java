package cn.mointe.vaccination.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	public DateUtils() {

	}

	/**
	 * 计算指定日期与当前日期相差多少年
	 * 
	 * @param startDateString
	 *            指定日期
	 * @return 相差的年数
	 * @throws ParseException
	 */
	public static long getCountYear(String startDateString)
			throws ParseException {
		int countYear = 0;
		// 转化为日期类型
		Date startDate = stringToDate(startDateString);
		Calendar starCal = Calendar.getInstance();
		starCal.setTime(startDate);
		int sYear = starCal.get(Calendar.YEAR);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(formatDate(new Date()));// 当前日期日期格式化
		int eYear = endCal.get(Calendar.YEAR);

		countYear = eYear - sYear;

		return countYear;
	}

	/**
	 * 计算开始时间距离当天相差多少个月
	 * 
	 * @param startDateString
	 * @param today
	 * @return
	 */
	public static long getMonth(String startDateString, Date today) {
		long monthday = 0;
		try {
			// 转化为日期类型
			Date startDate = stringToDate(startDateString);

			Calendar starCal = Calendar.getInstance();
			starCal.setTime(startDate);

			int sYear = starCal.get(Calendar.YEAR);
			int sMonth = starCal.get(Calendar.MONTH);
			int sDay = starCal.get(Calendar.DATE);

			Calendar endCal = Calendar.getInstance();
			endCal.setTime(today);
			int eYear = endCal.get(Calendar.YEAR);
			int eMonth = endCal.get(Calendar.MONTH);
			int eDay = endCal.get(Calendar.DATE);

			if (sDay <= eDay) {
				monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));
			} else {
				monthday = ((eYear - sYear) * 12 + (eMonth - sMonth)) - 1;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return monthday;
	}

	/**
	 * 将String转化为Date
	 * 
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date stringToDate(String dateString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		Date date = format.parse(dateString);
		return date;
	}

	/**
	 * 将日期转化为"yyyy-MM-dd"格式
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date formatDate(Date date) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		String dateString = format.format(date);
		Date today = format.parse(dateString);
		return today;
	}

	/**
	 * 指定日期<String格式>与当前日期比较大小
	 * 
	 * @param dateString
	 * @return -1:比当前日期小 ;0:与当前日期相等;1:比当前日期大
	 * @throws ParseException
	 */
	public static int compareDateToToday(String dateString)
			throws ParseException {
		Date date = stringToDate(dateString);
		Date today = formatDate(new Date());
		int result = date.compareTo(today);
		return result;
	}

	/**
	 * 得到当前日期字符串，格式：yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getCurrentFormatDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		String dateString = format.format(new Date());
		return dateString;
	}

	/**
	 * 字符串格式日期比较
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return -1:开始日期小 ;0:日期相等;1:开始日期大
	 * @throws ParseException
	 */
	public static int compareDate(String startDate, String endDate)
			throws ParseException {
		Date sDate = stringToDate(startDate);
		Date eDate = stringToDate(endDate);
		int result = sDate.compareTo(eDate);
		return result;
	}
}
