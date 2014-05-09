package cn.mointe.vaccination.tools;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class ImageUtils {

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param activity
	 * @param uri
	 * @param size
	 * @param requestCode
	 */
	public static void startPhotoZoom(Activity activity, Uri uri, int size,
			int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, requestCode);
	}
	
}
