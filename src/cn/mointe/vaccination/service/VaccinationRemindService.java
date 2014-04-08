package cn.mointe.vaccination.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.receiver.VaccinationRemindReceiver;

public class VaccinationRemindService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		VaccinationPreferences preferences = new VaccinationPreferences(this);
		String remindDate = preferences.getRemindDate();//取得提醒日期
		//String remindDate = "2014-03-26";
		String[] arr = remindDate.split("-");
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
		Log.e("MainActivity", "服务启动...onStartCommand..." + arr[0] + "-" + month
				+ "-" + day);
		int remindTime = preferences.getRemindTime();// 取得提醒时间
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(arr[0]));
		calendar.set(Calendar.MONTH, month-1);
		calendar.set(Calendar.DAY_OF_MONTH, day-remindTime);
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent remindIntent = new Intent(this, VaccinationRemindReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				remindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 30秒后发送广播
		// long sendTime = 30 * 1000;
		// int triggerAtTime = (int) (SystemClock.elapsedRealtime() + sendTime);
		// 只发送一次
		alarmManager.set(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(), pendingIntent);
		Log.e("MainActivity",
				"date..."
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
								.getDefault()).format(calendar.getTime()));
		Log.e("MainActivity", "Calendar:" + calendar.getTimeInMillis());
		// 每隔30秒重复发送
		// int interval = 30 * 1000;
		// alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
		// triggerAtTime, interval, pendingIntent);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("MainActivity", "服务停止..onDestroy");
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent remindIntent = new Intent(this, VaccinationRemindReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				remindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}
}
