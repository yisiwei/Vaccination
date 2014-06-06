package cn.mointe.vaccination.network;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.NetworkUtil;

public class CacheUtil {

	private static final String TAG = "MainActivity";

	public static final int CONFIG_CACHE_MOBILE_TIMEOUT = 60 * 60 * 1000; // 1小时
	public static final int CONFIG_CACHE_WIFI_TIMEOUT = 5 * 60 * 1000; // 5分钟

	public static String mSDCardDataDir;

	public CacheUtil() {
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			File file = new File(Environment.getExternalStorageDirectory()
					.getPath() + "/vaccination/config/");
			if (!file.exists()) {
				if (file.mkdirs()) {
					mSDCardDataDir = file.getAbsolutePath();
				}
			} else {
				mSDCardDataDir = file.getAbsolutePath();
			}
		}
	}

	public static String getUrlCache(Context context, String url) {
		if (url == null) {
			return null;
		}

		String result = null;
		File file = new File(mSDCardDataDir + "/" + getCacheDecodeString(url));
		if (file.exists() && file.isFile()) {
			long expiredTime = System.currentTimeMillis() - file.lastModified();
			Log.d(TAG, file.getAbsolutePath() + " expiredTime:" + expiredTime
					/ 60000 + "min");
			if (NetworkUtil.getAPNType(context) != -1 && expiredTime < 0) {
				return null;
			}
			if (NetworkUtil.getAPNType(context) == NetworkUtil.WIFI
					&& expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
				return null;
			} else if (NetworkUtil.getAPNType(context) == NetworkUtil.CMWAP
					&& expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
				return null;
			}
			try {
				result = FileUtils.readTextFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static void setUrlCache(String data, String url) {
		File file = new File(mSDCardDataDir + "/" + getCacheDecodeString(url));
		try {
			// 创建缓存数据到磁盘，就是创建文件
			FileUtils.writeTextFile(file, data);
		} catch (IOException e) {
			Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
			e.printStackTrace();
		}
	}

	public static String getCacheDecodeString(String url) {
		// 1. 处理特殊字符
		// 2. 去除后缀名带来的文件浏览器的视图凌乱(特别是图片更需要如此类似处理，否则有的手机打开图库，全是我们的缓存图片)
		if (url != null) {
			return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
		}
		return null;
	}

}
