package cn.mointe.vaccination.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.MainActivity;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PackageUtil;

public class VaccinationRemindReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("MainActivity", "onReceive...");
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setSmallIcon(R.drawable.app_icon);
		builder.setTicker("宝宝即将接种，请注意时间");
		builder.setContentTitle("疫苗接种提醒");
		builder.setContentText("妈妈，我即将要打疫苗了，要记得哦！");
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

		Intent remindService = new Intent(context,
				VaccinationRemindService.class);
		// 如果服务在运行，停止服务
		if (PackageUtil.isServiceRunning(context, Constants.REMIND_SERVICE)) {
			context.stopService(remindService);
		}
	}

}
