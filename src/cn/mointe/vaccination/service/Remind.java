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
	 * @param data
	 */
	public static void newRemind(Context context, int requestCode, String date,
			int remindTime,int hour,int minute, String data) {

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
			intent.putExtra("data", data);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), pendingIntent);
			
			Log.e("MainActivity",
					"date..."
							+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
									.getDefault()).format(calendar.getTime()));
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
	}

}
