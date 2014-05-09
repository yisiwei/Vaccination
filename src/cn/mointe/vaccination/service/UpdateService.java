package cn.mointe.vaccination.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.Log;

/**
 * 版本更新
 * 
 */

public class UpdateService extends Service {

	private static final int TIMEOUT = 10 * 1000;// 超时
	private static String DOWN_URL = "http://192.168.1.109:3000/app/Vaccination_1.0.3.apk";
	private static final int DOWN_OK = 1;
	private static final int DOWN_ERROR = 0;

	private String mVersion;
	private String mAppName;
	private String mApkName;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotificationCompat;

	// private Intent mUpdateIntent;
	// private PendingIntent mPendingIntent;

	private int mNotificationId = 0;

	// private RemoteViews views;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("MainActivity", "创建通知栏");
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationCompat = new NotificationCompat.Builder(
				getApplicationContext());

		mNotificationCompat.setSmallIcon(R.drawable.app_icon);
		mNotificationCompat.setContentTitle("正在下载...");
		mNotificationCompat.setContentText("0%");
		mNotificationCompat.setProgress(100, 0, false);

		mNotificationManager.notify(mNotificationId,
				mNotificationCompat.build());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("MainActivity", "服务开始");
		mAppName = intent.getStringExtra("app_name");
		mVersion = intent.getStringExtra("version");
		DOWN_URL = intent.getStringExtra("download_url");
		
		mApkName = mAppName + "_" + mVersion;
		new Thread(new DownLoadThread()).start();
		return super.onStartCommand(intent, flags, startId);
	}

	public class DownLoadThread implements Runnable {

		@Override
		public void run() {
			try {
				long totalSize = 0;// 文件总大小
				int downLoadCount = 0;// 已经下载好的大小
				InputStream inputStream = null;

				URL url = new URL(DOWN_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setConnectTimeout(TIMEOUT);
				connection.setReadTimeout(TIMEOUT);
				// 获取下载文件的size
				totalSize = connection.getContentLength();
				if (connection.getResponseCode() == 404) {
					throw new Exception("fail!");
				}
				inputStream = connection.getInputStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int readSize = 0;
				Log.i("MainActivity", "totalSize:" + totalSize);
				while ((readSize = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, readSize);
					downLoadCount += readSize;
					int value = (int) ((downLoadCount / (float) totalSize) * 100);
					Log.i("MainActivity", "downLoadCount:" + downLoadCount
							+ "--readSize:" + readSize + "--value:" + value);
					Message msg = new Message();
					msg.what = 100;
					msg.arg1 = value;
					mHandler.sendMessage(msg);
				}
				if (FileUtils.saveToDisk(mApkName + ".apk",
						outputStream.toByteArray())) {
					mHandler.sendEmptyMessage(DOWN_OK);
				}
				if (connection != null) {
					connection.disconnect();
				}
				outputStream.close();
				inputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(DOWN_ERROR);
			}
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == DOWN_OK) {

				mNotificationCompat.setContentTitle(mApkName);
				mNotificationCompat.setContentText("下载成功，点击安装");
				mNotificationCompat.setAutoCancel(true);// 点击通知取消掉通知

				Uri uri = Uri.fromFile(FileUtils.sApkPath);
				Log.i("MainActivity",
						"sApkPath==" + FileUtils.sApkPath.getAbsolutePath());
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(uri,
						"application/vnd.android.package-archive");

				PendingIntent pendingIntent = PendingIntent.getActivity(
						UpdateService.this, 0, intent, 0);
				mNotificationCompat.setContentIntent(pendingIntent);

				mNotificationManager.notify(mNotificationId,
						mNotificationCompat.build());
				Log.i("MainActivity", "下载成功");
				stopSelf();
			} else if (msg.what == DOWN_ERROR) {
				mNotificationCompat.setContentTitle(mAppName);
				mNotificationCompat.setContentText("下载失败");
				Log.i("MainActivity", "下载失败");
				stopSelf();
			} else if (msg.what == 100) {
				mNotificationCompat.setContentText(msg.arg1 + "%");
				mNotificationCompat.setProgress(100, msg.arg1, false);
				mNotificationManager.notify(mNotificationId,
						mNotificationCompat.build());
				Log.i("MainActivity", "msg.arg1===" + msg.arg1);
			}

		}
	};

	@Override
	public void onDestroy() {
		Log.i("MainActivity", "服务停止");
		super.onDestroy();
	}
}
