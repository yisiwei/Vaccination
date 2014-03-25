package cn.mointe.vaccination.tools;

import android.content.Context;
import android.widget.Toast;

public class PublicMethod {

	public PublicMethod() {

	}

	public static void showToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void showToast(Context context, int resId) {
		Toast.makeText(context, context.getResources().getString(resId),
				Toast.LENGTH_SHORT).show();
	}

}
