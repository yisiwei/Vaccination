package cn.mointe.vaccination.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
	private Paint mPaint = new Paint();

	private static final int STROKE_WIDTH = 8;

	public CircleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		// setOnClickListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (null != drawable) {
			Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
			Bitmap b = circleBitmap(scaleBitmap(bitmap));
			final Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());
			mPaint.reset();
			canvas.drawBitmap(b, rect, rect, mPaint);
		} else {
			super.onDraw(canvas);
		}
	}

	// 将矩形图片绘制成圆形图片并在最外围画一圈白色
	private Bitmap circleBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff000000;
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		mPaint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		mPaint.setColor(color);
		final int width = bitmap.getWidth();
		canvas.drawCircle(width / 2, width / 2, width / 2, mPaint);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, mPaint);// 将图片绘制成白色图片
		// 画白色圆圈
		mPaint.reset();
		mPaint.setColor(Color.argb(255, 139, 179, 142));
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(STROKE_WIDTH / 2);
		mPaint.setAntiAlias(true);
		canvas.drawCircle(width / 2, width / 2, width / 2 - STROKE_WIDTH / 2
				+ 2, mPaint);

		return output;
	}

	/**
	 * change the bitmap size
	 * 
	 * @param src
	 *            : Bitmap source
	 * @return : The scaled bitmap
	 */
	private Bitmap scaleBitmap(Bitmap src) {
		int width = src.getWidth();// 原来尺寸大小
		int height = src.getHeight();
		final float destSize = this.getWidth();// 缩放到这个大小

		// 图片缩放比例，新尺寸/原来的尺寸
		float scaleWidth = ((float) destSize) / width;
		float scaleHeight = ((float) destSize) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		// 返回缩放后的图片
		return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
	}

	// @Override
	// public void onClick(View v) {
	// // To change body of implemented methods use File | Settings | File
	// // Templates.
	// }
}