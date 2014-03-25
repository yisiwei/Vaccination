package cn.mointe.vaccination.activity;

import java.io.File;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.PublicMethod;
import cn.mointe.vaccination.tools.WebServiceUtil;
import cn.mointe.vaccination.view.CircleImageView;

public class RegisterBabyActivity extends ActionBarActivity implements
		OnClickListener {

	private BabyDao mDao;
	private VaccinationDao mVaccinationDao;
	private VaccineDao mVaccineDao;

	private Button mSure;// 保存按钮
	private Button mCancel;// 返回按钮

	private Button mBirthdate; // 出生日期控件
	private EditText mBabyName;// 宝宝昵称控件
	private RadioGroup mBabySex;// 性别组控件
	private RadioButton mRadioButton;
	private RadioButton mBoyRadioBtn;// 男孩
	private RadioButton mGirlRadioBtn;// 女孩

	private String mBirthDay;// 选择的出生日期

	private Button mResidence;// 居住地控件
	private EditText mPlace;// 接种地控件
	private EditText mPhone;// 接种地电话控件
	private Baby mBaby;// baby对象

	private CircleImageView mBabyImage;// 自定义宝宝头像控件

	private Spinner mProvinceSpinner;// 省 下拉框
	private Spinner mCitySpinner;// 市 下拉框
	private List<String> mProvinces; // 省列表
	private List<String> mCitys;// 市 列表
	private String mCity; // 选择的城市

	private String[] items = new String[] { "选择本地图片", "拍照" };

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	private Uri mOutputFileUri; // 宝宝头像Uri
	// private boolean mFlag = false;

	private VaccinationPreferences mPreferences;

	private ActionBar mBar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 加载布局文件
		setContentView(R.layout.activity_register_baby);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		// 接收传过来的baby对象，修改baby信息时用到
		mBaby = (Baby) getIntent().getSerializableExtra("baby");

		mPreferences = new VaccinationPreferences(this);
		mDao = new BabyDao(this);
		mVaccineDao = new VaccineDao(this);
		mVaccinationDao = new VaccinationDao(this);

		// 初始化控件
		mBabyImage = (CircleImageView) findViewById(R.id.imgv_baby_image);// 宝宝头像
		mBabyName = (EditText) findViewById(R.id.et_baby_name);// 宝宝昵称
		mBabySex = (RadioGroup) findViewById(R.id.rg_baby_sex);// 性别组
		mBoyRadioBtn = (RadioButton) this.findViewById(R.id.rb_boy_baby);// 男
		mGirlRadioBtn = (RadioButton) this.findViewById(R.id.rb_girl_baby);// 女
		mBirthdate = (Button) findViewById(R.id.btn_baby_birth);// 出生日期
		mResidence = (Button) findViewById(R.id.et_baby_residence);// 居住地
		mPlace = (EditText) findViewById(R.id.et_baby_place);// 接种地
		mPhone = (EditText) findViewById(R.id.et_baby_phone);// 接种地电话
		mSure = (Button) findViewById(R.id.btn_baby_sure);// 保存按钮
		mCancel = (Button) findViewById(R.id.btn_baby_cancel);// 返回按钮

		// 选择性别
		mBabySex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int id = group.getCheckedRadioButtonId();
				mRadioButton = (RadioButton) RegisterBabyActivity.this
						.findViewById(id);
			}
		});

		mBoyRadioBtn.setChecked(true);// 默认性别男
		mBirthdate.setOnClickListener(this);// 日期Dialog
		mResidence.setOnClickListener(this);
		mSure.setOnClickListener(this);
		mBabyImage.setOnClickListener(this);
		mCancel.setOnClickListener(this);

		// 修改宝宝信息时，填上对应的信息
		if (mBaby != null) {
			mBabyName.setText(mBaby.getName());
			String sex = mBaby.getSex();
			if (sex != null) {
				if (sex.equals("男")) {
					mBoyRadioBtn.setChecked(true);
				} else {
					mGirlRadioBtn.setChecked(true);
				}
			}
			mResidence.setText(mBaby.getResidence());
			mPlace.setText(mBaby.getVaccination_place());
			mPhone.setText(mBaby.getVaccination_phone());
			mBirthdate.setText(mBaby.getBirthdate());

			String imgUri = mBaby.getImage();
			if (!TextUtils.isEmpty(imgUri)) {
				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri,
						100, 100);
				mBabyImage.setImageBitmap(bitmap);
			} else {
				mBabyImage.setImageResource(R.drawable.default_img);
			}
		}
	}

	/**
	 * 保存baby
	 */
	private void saveBaby() {
		String birthdate = mBirthdate.getText().toString();
		int resultDate = 0;
		boolean isExist = mDao.checkBabyIsExist(mBabyName.getText().toString());
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
		} else if (isExist) {
			// 昵称已经存在
			PublicMethod.showToast(getApplicationContext(),
					R.string.baby_nickname_is_exist);
		} else {

			String imgUri = null;
			if (mOutputFileUri != null) {
				imgUri = mOutputFileUri.getPath();
				Log.i("MainActivity", "imgUri=" + imgUri);
			}
			if (mBaby != null) {
				Log.i("MainActivity", "更新");
				boolean result = mDao.updateBaby(new Baby(mBaby.getId(),
						mBabyName.getText().toString(), mBirthdate.getText()
								.toString(), imgUri, mResidence.getText()
								.toString(), mRadioButton.getText().toString(),
						mPlace.getText().toString(), mPhone.getText()
								.toString(), null));
				if (result) {
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_success);
					// 如果修改了baby昵称，对应的接种列表也需要修改
					if (!mBaby.getName().equals(mBabyName.getText().toString())) {
						mVaccinationDao.updateBabyNickName(mBaby.getName(),
								mBabyName.getText().toString());
					}
				} else {
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_fail);
				}
			} else {
				Log.i("MainActivity", "新增");

				if (!mPreferences.getIsExistBaby()) { // 如果是第一次进入
					Log.i("MainActivity", "首次进入");
					boolean result = mDao.saveBaby(new Baby(mBabyName.getText()
							.toString(), mBirthdate.getText().toString(),
							imgUri, mResidence.getText().toString(),
							mRadioButton.getText().toString(), mPlace.getText()
									.toString(), mPhone.getText().toString(),
							"1"));
					if (result) {
						// 生成接种列表
						mVaccinationDao.savaVaccinations(mBirthdate.getText()
								.toString(), mBabyName.getText().toString());
						Intent intent = new Intent(getApplicationContext(),
								MainActivity.class);
						startActivity(intent);
						mPreferences.setIsExistBaby(true);// 下次启动直接进入主界面
						PublicMethod.showToast(getApplicationContext(),
								R.string.operation_success);
						mVaccineDao.savaVaccines();// 初始化疫苗库
						this.finish();
					} else {
						PublicMethod.showToast(getApplicationContext(),
								R.string.operation_fail);
					}
				} else {
					boolean result = mDao.saveBaby(new Baby(mBabyName.getText()
							.toString(), mBirthdate.getText().toString(),
							imgUri, mResidence.getText().toString(),
							mRadioButton.getText().toString(), mPlace.getText()
									.toString(), mPhone.getText().toString(),
							"0"));
					if (result) {
						// 生成接种列表
						mVaccinationDao.savaVaccinations(mBirthdate.getText()
								.toString(), mBabyName.getText().toString());
						PublicMethod.showToast(getApplicationContext(),
								R.string.operation_success);
					} else {
						PublicMethod.showToast(getApplicationContext(),
								R.string.operation_fail);
					}
				}
			}
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
		final int year = calendar.get(Calendar.YEAR);
		final int monthOfYear = calendar.get(Calendar.MONDAY);
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.set_baby_birth);// 提示

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.set_date, null);
		TextView dateLable = (TextView) view.findViewById(R.id.date_lable);
		dateLable.setVisibility(View.GONE);
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		datePicker.init(year, monthOfYear, dayOfMonth,
				new OnDateChangedListener() {
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

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

						mBirthDay = stringBuilder.toString();
						Log.i(Constants.TAG, "birthday=" + mBirthDay);
					}
				});

		builder.setView(view);

		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mBirthDay != null) {
							mBirthdate.setText(mBirthDay);
						} else {
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
						}
					}

				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create();
		builder.show();
		// DatePickerDialog datePickerDialog = new DatePickerDialog(this,
		// new OnDateSetListener() {
		//
		// @Override
		// public void onDateSet(DatePicker view, int year,
		// int monthOfYear, int dayOfMonth) {
		// if (mFlag) {
		// StringBuilder stringBuilder = new StringBuilder();
		// stringBuilder.append(year);
		// int newMonth = monthOfYear + 1;
		// if (newMonth < 10) {
		// stringBuilder.append("-0").append(newMonth);
		// } else {
		// stringBuilder.append("-").append(newMonth);
		// }
		// if (dayOfMonth < 10) {
		// stringBuilder.append("-0").append(dayOfMonth);
		// } else {
		// stringBuilder.append("-").append(dayOfMonth);
		// }
		// mBirthdate.setText(stringBuilder.toString());
		// mFlag = false;
		// }
		// }
		// }, year, monthOfYear, dayOfMonth);
		// datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
		// getResources().getString(R.string.confirm),
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// mFlag = true;
		// }
		// });
		// datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
		// getResources().getString(R.string.cancel),
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// mFlag = false;
		// }
		// });
		// datePickerDialog.show();
	}

	/**
	 * 点击监听
	 */
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
			if (mBaby != null) {
				PublicMethod.showToast(this, R.string.birthdate_is_not_update);
			} else {
				showDateDialog();
			}
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
						case 0:// 选择本地照片
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:// 拍照
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
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
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
				R.layout.select_item, mProvinces);
		provincesAdapter.setDropDownViewResource(R.layout.select_item);
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
								getApplicationContext(), R.layout.select_item,
								mCitys);
						cityAdapter
								.setDropDownViewResource(R.layout.select_item);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
