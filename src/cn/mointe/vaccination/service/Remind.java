package cn.mointe.vaccination.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import cn.mointe.vaccination.receiver.AlarmReceiver;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

public class Remind {

	/**
	 * 新建提醒
	 * 
	 * @param context
	 * @param requestCode
	 * @param date
	 * @param remindTime
	 * @param hour
	 * @param minute
	 * @param babyName
	 */
	public static void newRemind(Context context, int requestCode, String date,
			int remindTime, int hour, int minute, String babyName) {

		if (!StringUtils.isNullOrEmpty(date)) {

			String[] arr = date.split("-");
			int month = 0;
			if (arr[1].charAt(0) == '0') {
				char c = arr[1].charAt(1);
				month = Integer.parseInt(c + "");
			} else {
				month = Integer.parseInt(arr[1]);
			}
			int day = 0;
			if (arr[2].charAt(0) == '0') {
				char c = arr[2].charAt(1);
				day = Integer.parseInt(c + "");
			} else {
				day = Integer.parseInt(arr[2]);
			}

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(arr[0]));
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH, day - remindTime);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);

			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("requestCode", requestCode);
			intent.putExtra("babyName", babyName);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), pendingIntent);
			} else {
				Log.i("MainActivity", "时间已经过去");
			}

			Log.e("MainActivity",
					"date..."
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
									Locale.getDefault()).format(calendar
									.getTime()) + "--"
							+ calendar.getTimeInMillis());
		}
	}

	/**
	 * 新建提醒
	 * 
	 * @param context
	 * @param requestCode
	 * @param date
	 *            接种日期
	 * @param remindDay
	 *            什么时候提醒(7:一周前；1:前一天过；0:当天)
	 * @param remindTime
	 *            提醒时间(09:00)
	 * @param babyName
	 */
	public static void newRemind(Context context, int requestCode, String date,
			int remindDay, String remindTime, String babyName) {

		if (!StringUtils.isNullOrEmpty(date)) {

			String[] arr = date.split("-");
			int month = 0;
			if (arr[1].charAt(0) == '0') {
				char c = arr[1].charAt(1);
				month = Integer.parseInt(c + "");
			} else {
				month = Integer.parseInt(arr[1]);
			}
			int day = 0;
			if (arr[2].charAt(0) == '0') {
				char c = arr[2].charAt(1);
				day = Integer.parseInt(c + "");
			} else {
				day = Integer.parseInt(arr[2]);
			}

			String[] arr_time = remindTime.split(":");
			int hour = 0;
			if (arr_time[0].charAt(0) == '0') {
				char c = arr_time[0].charAt(1);
				hour = Integer.parseInt(c + "");
			} else {
				hour = Integer.parseInt(arr_time[0]);
			}
			int minute = 0;
			if (arr_time[1].charAt(0) == '0') {
				char c = arr_time[1].charAt(1);
				minute = Integer.parseInt(c + "");
			} else {
				minute = Integer.parseInt(arr_time[1]);
			}
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.parseInt(arr[0]));
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DAY_OF_MONTH, day - remindDay);
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);

			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("requestCode", requestCode);
			intent.putExtra("babyName", babyName);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
				alarmManager.set(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), pendingIntent);
			} else {
				Log.i("MainActivity", "时间已经过去");
			}

			Log.e("MainActivity",
					"date..."
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
									Locale.getDefault()).format(calendar
									.getTime()) + "--"
							+ calendar.getTimeInMillis());
		}
	}

	/**
	 * 取消提醒
	 * 
	 * @param context
	 * @param requestCode
	 */
	public static void cancelRemind(Context context, int requestCode) {
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
		Log.i("MainActivity", "取消提醒");
	}

}
