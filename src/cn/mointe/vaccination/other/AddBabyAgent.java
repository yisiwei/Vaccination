package cn.mointe.vaccination.other;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class AddBabyAgent extends Application {
	private List<Activity> activities = new ArrayList<Activity>();
	private static AddBabyAgent instance;

	private AddBabyAgent() {

	}

	// 单例模式中获取唯一的application
	public static AddBabyAgent getInstance() {
		if (null == instance) {
			instance = new AddBabyAgent();
		}
		return instance;
	}

	// 存放Activity到list中
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	public void exit() {
		for (Activity activity : activities) {
			activity.finish();
		}
	}

	// 遍历存放在list中的Activity并退出
	// @Override
	// public void onTerminate() {
	// super.onTerminate();
	// for (Activity activity : activities) {
	// activity.finish();
	// }
	// //android.os.Process.killProcess(android.os.Process.myPid());
	// System.exit(0);
	// }
}
