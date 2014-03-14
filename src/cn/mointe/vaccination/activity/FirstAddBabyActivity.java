package cn.mointe.vaccination.activity;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.PackageUtil;

public class FirstAddBabyActivity extends Activity {

	private Button mBirthdate;
	private BabyDao mDao;

	private Button mSure;
	private Button mCancel;

	private EditText mBabyName;
	private RadioGroup mBabySex;
	private EditText mResidence;
	private EditText mPlace;
	private EditText mPhone;

	private ImageButton mBabyImage;

	private String[] items = new String[] { "选择本地图片", "拍照" };
	/* 头像名称 */
	private static final String IMAGE_FILE_NAME = "faceImage.jpg";

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_baby);

		mDao = new BabyDao(this);

		mBabyImage = (ImageButton) findViewById(R.id.imgv_baby_image);
		mSure = (Button) findViewById(R.id.btn_baby_sure);

		mBabyName = (EditText) findViewById(R.id.et_baby_name);
		mBabySex = (RadioGroup) findViewById(R.id.rg_baby_sex);
		mResidence = (EditText) findViewById(R.id.et_baby_residence);
		mPlace = (EditText) findViewById(R.id.et_baby_place);
		mPhone = (EditText) findViewById(R.id.et_baby_phone);

		mBirthdate = (Button) findViewById(R.id.btn_baby_birth);
		mCancel = (Button) findViewById(R.id.btn_baby_cancel);

		mCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FirstAddBabyActivity.this.finish();
			}
		});

		Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final int monthOfYear = calendar.get(Calendar.MONDAY);
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		// 弹出日期Dialog
		mBirthdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				new DatePickerDialog(FirstAddBabyActivity.this,
						new OnDateSetListener() {

							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								mBirthdate.setText(year + "-"
										+ (monthOfYear + 1) + "-" + dayOfMonth);
							}
						}, year, monthOfYear, dayOfMonth).show();
			}
		});

		// 提交按钮
		mSure.setOnClickListener(new ButtonOnClickListener());

		mBabyImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog();
			}
		});

	}

	private class ButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mBabyName.getText().toString() == null
					|| "".equals(mBabyName.getText().toString())) {
				Toast.makeText(getApplicationContext(), "昵称不能为空",
						Toast.LENGTH_SHORT).show();
			} else if (mBirthdate.getText().toString() == null
					|| "".equals(mBirthdate.getText().toString())) {
				Toast.makeText(getApplicationContext(), "出生日期不能为空",
						Toast.LENGTH_SHORT).show();
			} else {
				boolean result = mDao.saveBaby(new Baby(mBabyName.getText()
						.toString(), mBirthdate.getText().toString(), null,
						mResidence.getText().toString(), null, mPlace.getText()
								.toString(), mPhone.getText().toString(), "1"));
				if (result) {
					FirstAddBabyActivity.this.finish();
					Intent intent = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(intent);
					setIsExistBaby();
					Toast.makeText(getApplicationContext(), "操作成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "未完成",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 添加SharedPreferences下次启动直接进入主界面
	 */
	private void setIsExistBaby() {
		SharedPreferences preferences = this.getSharedPreferences(
				WelcomeActivity.SHAREDPREFERENCES, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 将IsExistBaby存入SharedPreferences
		editor.putBoolean("IsExistBaby", true);
		editor.commit();
	}

	/**
	 * 显示选择对话框
	 */
	private void showDialog() {

		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (FileUtils.hasSdcard()) {
								File path = Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
								File file = new File(path, IMAGE_FILE_NAME);
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(file));
							}

							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {

			switch (requestCode) {
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				if (FileUtils.hasSdcard()) {
					File path = Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
					File tempFile = new File(path, IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_LONG)
							.show();
				}
				break;
			case RESULT_REQUEST_CODE: // 图片缩放完成后
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(this.getResources(), photo);
			mBabyImage.setImageDrawable(drawable);
		}
	}

}
