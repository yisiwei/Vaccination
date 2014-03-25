package cn.mointe.vaccination.tools;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtil {

	public PackageUtil() {

	}

	/**
	 * 获取版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo packageInfo = manager.getPackageInfo(
					context.getPackageName(), 0);
			versionCode = packageInfo.versionCode;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	/**
	 * 获取版本名称
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo packageInfo = manager.getPackageInfo(
					context.getPackageName(), 0);
			versionName = packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return versionName;
	}

	/**
	 * 判断服务是否正在运行
	 * 
	 * @param context
	 * @param className
	 *            服务的全名(包名+类名)
	 * @return true在运行,false不在运行
	 */
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
