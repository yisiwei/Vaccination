package cn.mointe.vaccination.tools;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

public class MediaFileUtils {

	public static final int MEDIA_TYPE_PICTURE = 1;
	public static final String TAG = "MainActivity";

	public File getOutputMediaFile(int type, String dir) {

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				dir);

		if (!mediaStorageDir.exists()) {
			Log.d(TAG, "mediaStorageDir not existed");
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;

		if (type == MEDIA_TYPE_PICTURE) {
			mediaFile = new File(mediaStorageDir.getParent() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}
}
