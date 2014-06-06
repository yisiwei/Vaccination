package cn.mointe.vaccination.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.support.v4.app.NotificationCompat;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.InboxActivity;
import cn.mointe.vaccination.activity.MainActivity;
import cn.mointe.vaccination.dao.InboxDao;
import cn.mointe.vaccination.domain.Inbox;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private InboxDao mInboxDao;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("MinaActivity", "收到广播");
		Bundle bundle = intent.getExtras();
		int requestCode = bundle.getInt("requestCode");
		String babyName = bundle.getString("babyName");
		Log.i("MainActivity", "requestCode=" + requestCode + "--babyName="
				+ babyName);

		// 发送通知栏
		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);
		builder.setSmallIcon(R.drawable.app_icon);

		builder.setWhen(System.currentTimeMillis());
		builder.setTicker("好妈妈疫苗发来一条消息");
		builder.setContentTitle("疫苗接种提醒");

		if (requestCode == Constants.REMIND_WEEK) {// 周提醒
			builder.setContentText("宝宝一周后有接种，在这一周内一定要...");
		} else if (requestCode == Constants.REMIND_DAY) {// 前一天提醒
			builder.setContentText("宝宝明天有接种，接种前需要注意...");
		} else if(requestCode == Constants.REMIND_TODAY) {// 当天提醒
			builder.setContentText("宝宝今天要去接种了，今天风大，出门记得给宝宝...");
		}else{
			builder.setContentText("test test ...");
		}

		builder.setDefaults(Notification.DEFAULT_ALL);
		builder.setAutoCancel(true);// 点击通知取消掉通知

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 10,
				new Intent(context, MainActivity.class), 0);
		//pendingIntent.
		builder.setContentIntent(pendingIntent);// 点击通知操作
		// 发送通知
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(100, builder.build());

		// TODO 向收件箱添加消息
		mInboxDao = new InboxDao(context);
		Inbox inbox = new Inbox();
		inbox.setUsername(babyName);
		inbox.setTitle("疫苗接种提醒");
		if (requestCode == Constants.REMIND_WEEK) {// 周提醒
			inbox.setContent("宝宝一周后有接种，在这一周内一定要...");
		} else if (requestCode == Constants.REMIND_DAY) {// 前一天提醒
			inbox.setContent("宝宝明天有接种，接种前需要注意...");
		} else if(requestCode == Constants.REMIND_TODAY){// 当天提醒
			inbox.setContent("宝宝今天要去接种了，今天风大，出门记得给宝宝...");
		}
		inbox.setDate(DateUtils.getCurrentFormatDate());
		inbox.setIsRead("未读");
		inbox.setType("提醒");

		boolean result = mInboxDao.saveInbox(inbox);
		Log.i("MainActivity", "保存inbox：" + result);
		
		//context.startActivity(new Intent(context, InboxActivity.class));
	}

}
