package cn.mointe.vaccination.receiver;

import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class VaccinationRemindReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("MainActivity", "onReceive...");
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker("宝宝即将接种，请注意时间");
		builder.setContentTitle("疫苗接种提醒");
		builder.setContentText("宝宝明天有接种，请注意安排时间");
		builder.setWhen(System.currentTimeMillis());

		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setAutoCancel(true);// 点击通知取消掉通知

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 10,
				new Intent(context, MainActivity.class), 0);
		builder.setContentIntent(pendingIntent);// 点击通知操作
		// 发送通知
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(100, builder.build());
	}

}
