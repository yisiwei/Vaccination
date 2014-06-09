package cn.mointe.vaccination.tools;

import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	public NetworkUtil() {

	}

	public static final int WIFI = 1;
	public static final int CMWAP = 2;
	public static final int CMNET = 3;

	/**
	 * 获取当前的网络状态 -1：没有网络，1：WIFI网络，2：wap网络，3：net网络
	 * 
	 * @param context
	 * @return
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			if (networkInfo.getExtraInfo().toLowerCase(Locale.getDefault())
					.equals("cmnet")) {
				netType = CMNET;
			} else {
				netType = CMWAP;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = WIFI;
		}
		return netType;
	}
}
