package cn.mointe.vaccination.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.tools.Log;

/**
 * 日历Gridview中的每一个item显示的textview
 * 
 */
@SuppressLint("ResourceAsColor")
public class CalendarAdapter extends BaseAdapter {

	private boolean isLeapyear = false; // 是否为闰年
	private int daysOfMonth = 0; // 某月的天数
	private int dayOfWeek = 0; // 具体某一天是星期几
	private int lastDaysOfMonth = 0; // 上一个月的总天数
	private Context context;
	private String[] dayNumber = new String[49]; // 一个gridview中的日期存入此数组中
	private static String week[] = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
	private SpecialCalendar sc = null;
	private LunarCalendar lc = null;
	// private Resources res = null;
	// private Drawable drawable = null;

	private String currentYear = "";
	private String currentMonth = "";
	// private String currentDay = "";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d",
			Locale.getDefault());
	private int currentFlag = -1; // 用于标记当天
	private int[] schDateTagFlag = null; // 存储当月所有的日程日期
	private int[] schDateTagFlag2;
	private int[] schDateTagFlag3;

	private String showYear = ""; // 用于在头部显示的年份
	private String showMonth = ""; // 用于在头部显示的月份
	private String animalsYear = "";
	private String leapMonth = ""; // 闰哪一个月
	private String cyclical = ""; // 天干地支
	// 系统当前时间
	private String sysDate = "";
	private String sys_year = "";
	private String sys_month = "";
	private String sys_day = "";

	// 日程时间(需要标记的日程日期)
	// private String sch_year = "";
	// private String sch_month = "";
	// private String sch_day = "";

	private VaccinationDao mDao;
	private BabyDao mBabyDao;
	
	public CalendarAdapter() {
		Date date = new Date();
		sysDate = sdf.format(date); // 当期日期
		sys_year = sysDate.split("-")[0];
		sys_month = sysDate.split("-")[1];
		sys_day = sysDate.split("-")[2];

	}
	
	public CalendarAdapter(Context context, Resources rs, int jumpMonth,
			int jumpYear, int year_c, int month_c, int day_c) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		// this.res = rs;

		int stepYear = year_c + jumpYear;
		int stepMonth = month_c + jumpMonth;
		if (stepMonth > 0) {
			// 往下一个月滑动
			if (stepMonth % 12 == 0) {
				stepYear = year_c + stepMonth / 12 - 1;
				stepMonth = 12;
			} else {
				stepYear = year_c + stepMonth / 12;
				stepMonth = stepMonth % 12;
			}
		} else {
			// 往上一个月滑动
			stepYear = year_c - 1 + stepMonth / 12;
			stepMonth = stepMonth % 12 + 12;
			if (stepMonth % 12 == 0) {

			}
		}

		currentYear = String.valueOf(stepYear); // 得到当前的年份
		currentMonth = String.valueOf(stepMonth); // 得到本月
													// （jumpMonth为滑动的次数，每滑动一次就增加一月或减一月）
		// currentDay = String.valueOf(day_c); //得到当前日期是哪天

		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth));

	}

	public CalendarAdapter(Context context, Resources rs, int year, int month,
			int day) {
		this();
		this.context = context;
		sc = new SpecialCalendar();
		lc = new LunarCalendar();
		// this.res = rs;
		currentYear = String.valueOf(year); // 得到跳转到的年份
		currentMonth = String.valueOf(month); // 得到跳转到的月份
		// currentDay = String.valueOf(day); //得到跳转到的天

		getCalendar(Integer.parseInt(currentYear),
				Integer.parseInt(currentMonth));
	}

	public int getCount() {
		return dayNumber.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.calendar, null);
		}
		TextView textView = (TextView) convertView.findViewById(R.id.tvtext);
		ImageView imageView = (ImageView) convertView.findViewById(R.id.calendar_icon);
		String d = dayNumber[position].split("\\.")[0];
		String dv = dayNumber[position].split("\\.")[1];
		// Typeface typeface = Typeface.createFromAsset(context.getAssets(),
		// "fonts/Helvetica.ttf");
		// textView.setTypeface(typeface);
		SpannableString sp = new SpannableString(d + "\n" + dv);
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
				d.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new RelativeSizeSpan(1.2f), 0, d.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if (dv != null || dv != "") {
			sp.setSpan(new RelativeSizeSpan(0.75f), d.length() + 1,
					dayNumber[position].length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		// sp.setSpan(new ForegroundColorSpan(Color.MAGENTA), 14, 16,
		// Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
		textView.setText(sp);
		textView.setTextColor(Color.GRAY);
		if (position < 7) {
			// 设置周
			textView.setTextColor(R.color.week_color);
			// drawable = res.getDrawable(R.drawable.week_top);
			// textView.setBackgroundDrawable(drawable);
			textView.setBackgroundResource(R.color.week_bg);
		}

		if (position < daysOfMonth + dayOfWeek + 7 && position >= dayOfWeek + 7) {
			// 当前月信息显示
			textView.setTextColor(Color.BLACK);// 当月字体设黑
			// Drawable drawable = res.getDrawable(R.drawable.item);
			// textView.setBackgroundDrawable(drawable);
			// textView.setBackgroundColor(Color.WHITE);

		}
		if (schDateTagFlag != null && schDateTagFlag.length > 0) {
			for (int i = 0; i < schDateTagFlag.length; i++) {
				if (schDateTagFlag[i] == position) {
					// 设置日程标记背景
//					textView.setBackgroundResource(R.drawable.mark_vac_time);
//					textView.setPadding(0, 0, 0, 0);
					imageView.setVisibility(View.GONE);
					textView.setBackgroundResource(R.color.vac_time);
					//imageView.setImageResource(R.drawable.mark_vac_time);
				}
			}
		}
		//预约全部完成的
		if (schDateTagFlag2 != null && schDateTagFlag2.length > 0) {
			for (int i = 0; i < schDateTagFlag2.length; i++) {
				if (schDateTagFlag2[i] == position) {
//					textView.setBackgroundResource(R.drawable.mark_vac_success);
//					textView.setPadding(0, 0, 0, 0);
					imageView.setVisibility(View.GONE);
					textView.setBackgroundColor(R.color.vac_success);
					//imageView.setImageResource(R.drawable.mark_vac_success);
				}
			}
		}
		if (schDateTagFlag3 != null && schDateTagFlag3.length > 0) {
			for (int i = 0; i < schDateTagFlag3.length; i++) {
				if (schDateTagFlag3[i] == position) {
//					textView.setBackgroundResource(R.drawable.mark_vac_out);
//					textView.setPadding(0, 0, 0, 0);
					imageView.setVisibility(View.GONE);
					textView.setBackgroundResource(R.color.vac_out);
					//imageView.setImageResource(R.drawable.mark_vac_out);
				}
			}
		}
		if (currentFlag == position) {
			// 设置当天的背景
			textView.setBackgroundResource(R.color.tuijian);
			textView.setTextColor(Color.WHITE);
		}
		return convertView;
	}

	// 得到某年的某月的天数且这月的第一天是星期几
	public void getCalendar(int year, int month) {
		isLeapyear = sc.isLeapYear(year); // 是否为闰年
		daysOfMonth = sc.getDaysOfMonth(isLeapyear, month); // 某月的总天数
		dayOfWeek = sc.getWeekdayOfMonth(year, month); // 某月第一天为星期几
		lastDaysOfMonth = sc.getDaysOfMonth(isLeapyear, month - 1); // 上一个月的总天数
		Log.d("DAY", isLeapyear + " ======  " + daysOfMonth
				+ "  ============  " + dayOfWeek + "  =========   "
				+ lastDaysOfMonth);
		getweek(year, month);
	}
	

	// 将一个月中的每一天的值添加入数组dayNuber中
	private void getweek(int year, int month) {
		int j = 1;
		int flag = 0;
		int flag2 = 0;
		int flag3 = 0;
		String lunarDay = "";

		// 得到当前月的所有日程日期(这些日期需要标记)
		// dao = new ScheduleDAO(context);
		// ArrayList<ScheduleDateTag> dateTagList = dao.getTagDate(year,month);
		// if(dateTagList != null && dateTagList.size() > 0){
		// schDateTagFlag = new int[dateTagList.size()];
		// }

		mDao = new VaccinationDao(context);
		mBabyDao = new BabyDao(context);
		Baby baby = mBabyDao.getDefaultBaby();
		List<Vaccination> dateTagList = mDao.getTagDate(year, month,
				baby.getName());
		List<Vaccination> dateTagList2 = mDao.getTagDate2(year, month,
				baby.getName());
		List<Vaccination> dateTagList3 = mDao.getTagDate3(year, month,
				baby.getName());
		if (dateTagList != null && dateTagList.size() > 0) {
			schDateTagFlag = new int[dateTagList.size()];
		}
		if (dateTagList2 != null && dateTagList2.size() > 0) {
			schDateTagFlag2 = new int[dateTagList2.size()];
		}
		if (dateTagList3 != null && dateTagList3.size() > 0) {
			schDateTagFlag3 = new int[dateTagList3.size()];
		}

		for (int i = 0; i < dayNumber.length; i++) {
			// 周一
			if (i < 7) {
				dayNumber[i] = week[i] + "." + " ";
			} else if (i < dayOfWeek + 7) { // 前一个月
				int temp = lastDaysOfMonth - dayOfWeek + 1 - 7;
				lunarDay = lc.getLunarDate(year, month - 1, temp + i, false);
				dayNumber[i] = (temp + i) + "." + lunarDay;
			} else if (i < daysOfMonth + dayOfWeek + 7) { // 本月
				String day = String.valueOf(i - dayOfWeek + 1 - 7); // 得到的日期
				lunarDay = lc.getLunarDate(year, month, i - dayOfWeek + 1 - 7,
						false);
				dayNumber[i] = i - dayOfWeek + 1 - 7 + "." + lunarDay;
				// 对于当前月才去标记当前日期
				if (sys_year.equals(String.valueOf(year))
						&& sys_month.equals(String.valueOf(month))
						&& sys_day.equals(day)) {
					// 标记当前日期
					currentFlag = i;
				}

				// 标记日程日期
				if (dateTagList != null && dateTagList.size() > 0) {
					for (int m = 0; m < dateTagList.size(); m++) {
						Vaccination dateTag = dateTagList.get(m);
						String date = dateTag.getReserve_time();
						String[] arr = date.split("-");
						// int matchYear = dateTag.getYear();
						// int matchMonth = dateTag.getMonth();
						// int matchDay = dateTag.getDay();
						int matchYear = Integer.valueOf(arr[0]);
						int matchMonth = Integer.valueOf(arr[1]);
						int matchDay = Integer.valueOf(arr[2]);
						if (matchYear == year && matchMonth == month
								&& matchDay == Integer.parseInt(day)) {
							schDateTagFlag[flag] = i;
							flag++;
						}
					}
				}
				
				//新增代码
				if (dateTagList2 != null && dateTagList2.size() > 0) {
					for (int k = 0; k < dateTagList2.size(); k++) {
						Vaccination dateTag = dateTagList2.get(k);
						String date = dateTag.getReserve_time();
						String[] arr = date.split("-");
						int matchYear = Integer.valueOf(arr[0]);
						int matchMonth = Integer.valueOf(arr[1]);
						int matchDay = Integer.valueOf(arr[2]);
						if (matchYear == year && matchMonth == month
								&& matchDay == Integer.parseInt(day)) {
							schDateTagFlag2[flag2] = i;
							flag2++ ;
						}
						
					}
				}
				if (dateTagList3 != null && dateTagList3.size() > 0) {
					for (int k = 0; k < dateTagList3.size(); k++) {
						Vaccination dateTag = dateTagList3.get(k);
						String date = dateTag.getReserve_time();
						String[] arr = date.split("-");
						int matchYear = Integer.valueOf(arr[0]);
						int matchMonth = Integer.valueOf(arr[1]);
						int matchDay = Integer.valueOf(arr[2]);
						if (matchYear == year && matchMonth == month
								&& matchDay == Integer.parseInt(day)) {
							schDateTagFlag3[flag3] = i;
							flag3++ ;
						}
						
					}
				}

				setShowYear(String.valueOf(year));
				setShowMonth(String.valueOf(month));
				setAnimalsYear(lc.animalsYear(year));
				setLeapMonth(lc.leapMonth == 0 ? "" : String
						.valueOf(lc.leapMonth));
				setCyclical(lc.cyclical(year));
			} else { // 下一个月
				lunarDay = lc.getLunarDate(year, month + 1, j, false);
				dayNumber[i] = j + "." + lunarDay;
				j++;
			}
		}

		String abc = "";
		for (int i = 0; i < dayNumber.length; i++) {
			abc = abc + dayNumber[i] + ":";
		}
		Log.d("DAYNUMBER", abc);

	}

	public void matchScheduleDate(int year, int month, int day) {

	}

	/**
	 * 点击每一个item时返回item中的日期
	 * 
	 * @param position
	 * @return
	 */
	public String getDateByClickItem(int position) {
		return dayNumber[position];
	}

	/**
	 * 在点击gridView时，得到这个月中第一天的位置
	 * 
	 * @return
	 */
	public int getStartPositon() {
		return dayOfWeek + 7;
	}

	/**
	 * 在点击gridView时，得到这个月中最后一天的位置
	 * 
	 * @return
	 */
	public int getEndPosition() {
		return (dayOfWeek + daysOfMonth + 7) - 1;
	}

	public String getShowYear() {
		return showYear;
	}

	public void setShowYear(String showYear) {
		this.showYear = showYear;
	}

	public String getShowMonth() {
		return showMonth;
	}

	public void setShowMonth(String showMonth) {
		this.showMonth = showMonth;
	}

	public String getAnimalsYear() {
		return animalsYear;
	}

	public void setAnimalsYear(String animalsYear) {
		this.animalsYear = animalsYear;
	}

	public String getLeapMonth() {
		return leapMonth;
	}

	public void setLeapMonth(String leapMonth) {
		this.leapMonth = leapMonth;
	}

	public String getCyclical() {
		return cyclical;
	}

	public void setCyclical(String cyclical) {
		this.cyclical = cyclical;
	}
}
