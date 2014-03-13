package cn.mointe.vaccination.tools;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapCompressTools {

	public BitmapCompressTools() {

	}

	/**
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromFile(String pathName,
			int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName);

		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * 
	 * @param resources
	 * @param resId
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources resources,
			int resId, int reqWidth, int reqHeight) {
		// 给定的BitmapFactory设置解码的参数
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 从解码器中获得原始图片的宽高，而避免申请内存空间
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(resources, resId, options);
	}

	/**
	 * 指定输出图片的缩放比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 获得原始图片的宽高
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		int inSimpleSize = 1;
		if (imageHeight > reqHeight || imageWidth > reqWidth) {

			// 计算压缩的比例：分为宽高比例
			final int heightRatio = Math.round((float) imageHeight
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) imageWidth
					/ (float) reqWidth);

			inSimpleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSimpleSize;
	}
}