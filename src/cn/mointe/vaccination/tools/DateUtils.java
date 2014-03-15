package cn.mointe.vaccination.tools;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public DateUtils() {

	}

	/**
	 * 计算开始时间距离当天相差多少个月
	 * 
	 * @param startDateString
	 * @param today
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static long getMonth(String startDateString, Date today) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		long monthday = 0;
		try {
			// 转化为日期类型
			Date startDate = f.parse(startDateString);

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

			monthday = ((eYear - sYear) * 12 + (eMonth - sMonth));

			if (sDay < eDay) {
				monthday = monthday + 1;
			}
			return monthday;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return monthday;
	}


}
