package cn.mointe.vaccination.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.receiver.VaccinationRemindReceiver;
import cn.mointe.vaccination.tools.DateUtils;

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
		String remindDate = preferences.getRemindDate();
		Log.e("MainActivity", "onStartCommand..." + remindDate);
		int remindTime = preferences.getRemindTime();
		Calendar calendar = Calendar.getInstance();
		Date remind = null;
		try {
			remind = DateUtils.stringToDate(remindDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		calendar.setTime(remind);
		calendar.add(Calendar.DAY_OF_MONTH, -remindTime);
		calendar.add(Calendar.HOUR_OF_DAY, 10);
		Log.e("MainActivity",
				"date..."
						+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
								.getDefault()).format(calendar.getTime()));
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent remindIntent = new Intent(this, VaccinationRemindReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				remindIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 30秒后发送广播
		// long sendTime = 30 * 1000;
		// int triggerAtTime = (int) (SystemClock.elapsedRealtime() + sendTime);
		// 只发送一次
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				calendar.getTimeInMillis(), pendingIntent);

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
