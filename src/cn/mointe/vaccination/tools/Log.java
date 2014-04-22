package cn.mointe.vaccination.tools;

import cn.mointe.vaccination.BuildConfig;

public class Log {

	private static final boolean DEBUG = BuildConfig.DEBUG;

	public static void v(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.v(tag, msg, tr);
		}
	}

	public static void d(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.i(tag, msg, tr);
		}
	}

	public static void w(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.w(tag, msg, tr);
		}
	}

	public static void w(String tag, Throwable tr) {
		if (DEBUG) {
			android.util.Log.w(tag, tr);
		}
	}

	public static void e(String tag, String msg) {
		if (DEBUG) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable tr) {
		if (DEBUG) {
			android.util.Log.e(tag, msg, tr);
		}
	}

}
