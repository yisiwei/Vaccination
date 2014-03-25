package cn.mointe.vaccination.activity;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.PublicMethod;
import cn.mointe.vaccination.tools.WebServiceUtil;
import cn.mointe.vaccination.view.CircleImageView;

public class FirstAddBabyActivity extends Activity implements OnClickListener {

	private Button mBirthdate;// 出生日期
	private BabyDao mBabyDao;
	private VaccinationDao mVaccinationDao;
	private VaccineDao mVaccineDao;

	private Button mSure; // 保存按钮
	private Button mCancel;// 取消按钮

	private EditText mBabyName;// 宝宝昵称
	private RadioGroup mBabySex;
	private RadioButton mRadioButton;
	private RadioButton mBoyRadioBtn;// 男

	private Button mResidence;// 居住地
	private EditText mPlace;// 接种地
	private EditText mPhone;// 接种地电话

	private CircleImageView mBabyImage;// 宝宝头像

	private Spinner mProvinceSpinner;// 省
	private Spinner mCitySpinner;// 市
	private List<String> mProvinces;
	private List<String> mCitys;
	private String mCity;

	private String[] items = new String[] { "选择本地图片", "拍照" };

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	private Uri mOutputFileUri;
	private boolean mFlag = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_baby);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		mBabyDao = new BabyDao(this);
		mVaccinationDao = new VaccinationDao(this);
		mVaccineDao = new VaccineDao(this);

		mBabyImage = (CircleImageView) findViewById(R.id.imgv_baby_image);
		mSure = (Button) findViewById(R.id.btn_baby_sure);

		mBabyName = (EditText) findViewById(R.id.et_baby_name);
		mBabySex = (RadioGroup) findViewById(R.id.rg_baby_sex);
		mBoyRadioBtn = (RadioButton) this.findViewById(R.id.rb_boy_baby);

		mResidence = (Button) findViewById(R.id.et_baby_residence);
		mPlace = (EditText) findViewById(R.id.et_baby_place);
		mPhone = (EditText) findViewById(R.id.et_baby_phone);

		mBirthdate = (Button) findViewById(R.id.btn_baby_birth);
		mCancel = (Button) findViewById(R.id.btn_baby_cancel);

		// 选择性别
		mBabySex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int id = group.getCheckedRadioButtonId();
				mRadioButton = (RadioButton) FirstAddBabyActivity.this
						.findViewById(id);
			}
		});

		mBoyRadioBtn.setChecked(true);// 默认性别男

		mBirthdate.setOnClickListener(this);// 弹出日期Dialog
		mBabyImage.setOnClickListener(this);
		mResidence.setOnClickListener(this);
		mSure.setOnClickListener(this);// 提交按钮
		mCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgv_baby_image: // 设置头像
			showSetImgDialog();
			break;
		case R.id.et_baby_residence:// 选择居住地
			showCityDialog();
			break;
		case R.id.btn_baby_birth:// 选择出生日期
			showDateDialog();
			break;
		case R.id.btn_baby_sure: // 保存按钮
			saveBaby();
			break;
		case R.id.btn_baby_cancel: // 返回按钮
			this.finish();
			break;
		default:
			break;
		}
	}

	// 设置日期对话框
	private void showDateDialog() {
		Calendar calendar = Calendar.getInstance();
		String birthdate = mBirthdate.getText().toString();
		if (!TextUtils.isEmpty(birthdate)) {
			Date date = null;
			try {
				date = DateUtils.stringToDate(birthdate);
				calendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONDAY);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog datePickerDialog = new DatePickerDialog(this,
				new OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						if (mFlag) {
							StringBuilder stringBuilder = new StringBuilder();
							stringBuilder.append(year);
							int newMonth = monthOfYear + 1;
							if (newMonth < 10) {
								stringBuilder.append("-0").append(newMonth);
							} else {
								stringBuilder.append("-").append(newMonth);
							}
							if (dayOfMonth < 10) {
								stringBuilder.append("-0").append(dayOfMonth);
							} else {
								stringBuilder.append("-").append(dayOfMonth);
							}
							mBirthdate.setText(stringBuilder.toString());
							mFlag = false;
						}
					}
				}, year, monthOfYear, dayOfMonth);
		datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
				getResources().getString(R.string.confirm),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mFlag = true;
					}
				});
		datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mFlag = false;
					}
				});
		datePickerDialog.show();
	}

	/**
	 * 保存baby
	 */
	private void saveBaby() {
		String birthdate = mBirthdate.getText().toString();
		int resultDate = 0;
		if (!TextUtils.isEmpty(birthdate)) {
			try {
				resultDate = DateUtils.compareDateToToday(birthdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		if (TextUtils.isEmpty(mBabyName.getText().toString())) {
			// 昵称不能为空
			PublicMethod.showToast(getApplicationContext(),
					R.string.nickname_is_not_null);
		} else if (TextUtils.isEmpty(birthdate)) {
			// 出生日期不能为空
			PublicMethod.showToast(getApplicationContext(),
					R.string.birthdate_is_not_null);
		} else if (TextUtils.isEmpty(mResidence.getText().toString())) {
			// 居住地不能为空
			PublicMethod.showToast(getApplicationContext(),
					R.string.residence_is_not_null);
		} else if (resultDate == 1) {
			// 出生日期不能超出今天
			PublicMethod.showToast(getApplicationContext(),
					R.string.birthdate_is_not_overtop_today);
		} else {

			String imgUri = null;
			if (mOutputFileUri != null) {
				imgUri = mOutputFileUri.getPath();
				Log.i("MainActivity", "imgUri=" + imgUri);
			}

			boolean result = mBabyDao.saveBaby(new Baby(mBabyName.getText()
					.toString(), mBirthdate.getText().toString(), imgUri,
					mResidence.getText().toString(), mRadioButton.getText()
							.toString(), mPlace.getText().toString(), mPhone
							.getText().toString(), "1"));
			if (result) {
				// 生成接种列表
				mVaccinationDao.savaVaccinations(mBirthdate.getText()
						.toString(), mBabyName.getText().toString());
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(intent);
				setIsExistBaby();// 下次启动直接进入主界面
				PublicMethod.showToast(getApplicationContext(),
						R.string.operation_success);
				mVaccineDao.savaVaccines();// 初始化疫苗库
				FirstAddBabyActivity.this.finish();
			} else {
				PublicMethod.showToast(getApplicationContext(),
						R.string.operation_fail);
			}

		}
	}

	// 选择居住地对话框
	private void showCityDialog() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.weather_city_list, null);
		// 省份Spinner
		mProvinceSpinner = (Spinner) view
				.findViewById(R.id.wea_province_spinner);
		// 城市Spinner
		mCitySpinner = (Spinner) view.findViewById(R.id.wea_city_spinner);
		// 省份列表
		mProvinces = WebServiceUtil.getProvinceList();
		ArrayAdapter<String> provincesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mProvinces);
		provincesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 设置adapter
		mProvinceSpinner.setAdapter(provincesAdapter);
		// 省份Spinner监听
		mProvinceSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						mCitys = WebServiceUtil
								.getCityListByProvince(mProvinces.get(position));
						ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(
								getApplicationContext(),
								android.R.layout.simple_spinner_item, mCitys);
						cityAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						mCitySpinner.setAdapter(cityAdapter);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
		// 城市Spinner监听
		mCitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mCity = mCitys.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		// 选择城市对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_city);// 标题：请选择城市
		builder.setView(view);
		// 添加确定按钮
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mResidence.setText(mCity);
					}
				});
		// 添加取消按钮
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create();
		// 显示Dialog
		builder.show();

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
	private void showSetImgDialog() {

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
								File file = new File(path, "IMG_"
										+ System.currentTimeMillis() + ".jpg");// 图片名称
								mOutputFileUri = Uri.fromFile(file);
								intentFromCapture
										.putExtra(MediaStore.EXTRA_OUTPUT,
												mOutputFileUri);
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
					// File path = Environment
					// .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
					// File tempFile = new File(path, "IMG_"
					// + System.currentTimeMillis() + ".jpg");
					startPhotoZoom(mOutputFileUri);
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
