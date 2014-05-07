package cn.mointe.vaccination.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.City;
import cn.mointe.vaccination.domain.CityItem;
import cn.mointe.vaccination.domain.Province;
import cn.mointe.vaccination.other.CityPullParseXml;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.ImageUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PublicMethod;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.CircleImageView;

/**
 * 编辑宝宝界面
 * 
 */
public class RegisterBabyActivity extends ActionBarActivity implements
		OnClickListener {

	private RelativeLayout mRelativeLayout;

	private BabyDao mDao;
	private VaccinationDao mVaccinationDao;
	// private VaccineDao mVaccineDao;

	private Button mSure;// 保存按钮
	// private Button mCancel;// 返回按钮

	private AlertDialog mBirthdateDialog; // 选择日期对话框
	private AlertDialog mCityDialog; // 选择居住地对话框

	private TextView mBirthdayHint;
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

	private Baby mAddBaby;// 新增baby对象

	private CircleImageView mBabyImage;// 自定义宝宝头像控件

	private Spinner mProvinceSpinner;// 省 下拉框
	private Spinner mCitySpinner;// 市 下拉框
	private Spinner mCountySpinner;// 县 下拉框
	private List<String> mProvinces; // 省列表
	private List<String> mCitys;// 市 列表
	private List<CityItem> mCountys;// 县 列表
	private String mCityCode;

	private List<City> mCityList = null;
	private String mCity; // 选择的城市
	private String mCounty; // 选择的区县

	private String[] items = new String[] { "选择本地图片", "拍照" };

	/* 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int IMAGE_REQUEST_CODE_KITKAT = 4;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;

	private File mImagePath = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);// 头像存储目录
	private File mImageFile;

	private Uri mOutputFileUri; // 宝宝头像Uri
	private String babyImgPath;// 选择本地图片路径

	private VaccinationPreferences mPreferences;

	private ActionBar mBar;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 加载布局文件
		setContentView(R.layout.activity_register_baby);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		// 接收传过来的baby对象，修改baby信息时用到
		mBaby = (Baby) getIntent().getSerializableExtra("baby");

		mPreferences = new VaccinationPreferences(this);
		mDao = new BabyDao(this);
		// mVaccineDao = new VaccineDao(this);
		mVaccinationDao = new VaccinationDao(this);

		// 初始化控件
		mBirthdayHint = (TextView) this.findViewById(R.id.textView_hint);
		mRelativeLayout = (RelativeLayout) this.findViewById(R.id.baby_rlay);
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
		// mCancel = (Button) findViewById(R.id.btn_baby_cancel);// 返回按钮

		// 选择性别
		mBabySex.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int id = group.getCheckedRadioButtonId();
				mRadioButton = (RadioButton) RegisterBabyActivity.this
						.findViewById(id);
			}
		});

		mRelativeLayout.setOnClickListener(this);
		mBoyRadioBtn.setChecked(true);// 默认性别男
		mBirthdate.setOnClickListener(this);// 日期Dialog
		mResidence.setOnClickListener(this);
		mSure.setOnClickListener(this);
		mBabyImage.setOnClickListener(this);
		// mCancel.setOnClickListener(this);

		// 修改宝宝信息时，填上对应的信息
		if (mBaby != null) {
			mBar.setTitle("宝宝信息");
			mSure.setText(R.string.baby_sure);
			mBirthdayHint.setVisibility(View.GONE);
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
			if (!StringUtils.isNullOrEmpty(imgUri)) {
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

		long monthCount = DateUtils.getMonth(birthdate, new Date());

		if (StringUtils.isNullOrEmpty(mBabyName.getText().toString())) {
			// 昵称不能为空
			PublicMethod.showToast(getApplicationContext(),
					R.string.nickname_is_not_null);
		} else if (StringUtils.isNullOrEmpty(birthdate)) {
			// 出生日期不能为空
			PublicMethod.showToast(getApplicationContext(),
					R.string.birthdate_is_not_null);
		} else if (StringUtils.isNullOrEmpty(mResidence.getText().toString())) {
			// 居住地不能为空
			PublicMethod.showToast(getApplicationContext(),
					R.string.residence_is_not_null);
		} else if (resultDate == 1) {
			// 出生日期不能超出今天
			PublicMethod.showToast(getApplicationContext(),
					R.string.birthdate_is_not_overtop_today);
		} else if (monthCount >= 72) {
			// 宝宝超过6岁，暂不提供接种提醒
			PublicMethod.showToast(getApplicationContext(),
					R.string.baby_more_than_six_years_old);
		} else if (mBaby == null && isExist) {
			// 昵称已经存在
			PublicMethod.showToast(getApplicationContext(),
					R.string.baby_nickname_is_exist);
		} else {

			mProgressDialog = ProgressDialog.show(this, getResources()
					.getString(R.string.hint),
					getResources().getString(R.string.loading_wait));
			SaveBabyTask task = new SaveBabyTask();
			task.execute();
		}
	}

	/**
	 * 保存宝宝
	 * 
	 */
	private class SaveBabyTask extends AsyncTask<String, Object, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {
			String imgUri = null;
			if (mOutputFileUri != null) {
				imgUri = mOutputFileUri.getPath();
				Log.i("MainActivity", "imgUri=" + imgUri);
			} else if (null != babyImgPath) {
				imgUri = babyImgPath;
			}
			if (mBaby != null) {
				Log.i("MainActivity", "更新");
				if (mOutputFileUri == null && babyImgPath == null) {
					if (mBaby.getImage() != null) {
						imgUri = mBaby.getImage();
					}
				}
				if (StringUtils.isNullOrEmpty(mCityCode)) {
					mCityCode = mBaby.getCityCode();
				}
				boolean result = mDao.updateBaby(new Baby(mBaby.getId(),
						mBabyName.getText().toString(), mBirthdate.getText()
								.toString(), imgUri, mResidence.getText()
								.toString(), mRadioButton.getText().toString(),
						mPlace.getText().toString(), mPhone.getText()
								.toString(), null, mCityCode));
				publishProgress("update", result);
			} else {
				Log.i("MainActivity", "新增");
				if (!mPreferences.getIsExistBaby()) { // 如果是第一次进入
					Log.i("MainActivity", "首次进入");
					mAddBaby = new Baby(mBabyName.getText().toString(),
							mBirthdate.getText().toString(), imgUri, mResidence
									.getText().toString(), mRadioButton
									.getText().toString(), mPlace.getText()
									.toString(), mPhone.getText().toString(),
							"1", mCityCode);
					boolean result = mDao.saveBaby(mAddBaby);
					publishProgress("firstAdd", result);
				} else {// 非第一次进入
					mAddBaby = new Baby(mBabyName.getText().toString(),
							mBirthdate.getText().toString(), imgUri, mResidence
									.getText().toString(), mRadioButton
									.getText().toString(), mPlace.getText()
									.toString(), mPhone.getText().toString(),
							"0", mCityCode);
					boolean result = mDao.saveBaby(mAddBaby);
					publishProgress("add", result);
				}
			}

			return null;
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			super.onProgressUpdate(values);
			if (values[0].toString().equals("update")) {
				if ((Boolean) values[1]) {
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_success);
					// 如果修改了baby昵称，对应的接种列表也需要修改
					if (!mBaby.getName().equals(mBabyName.getText().toString())) {
						mVaccinationDao.updateBabyNickName(mBaby.getName(),
								mBabyName.getText().toString());
					}
					RegisterBabyActivity.this.finish();
				} else {
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_fail);
				}
			} else if (values[0].toString().equals("firstAdd")) {
				if ((Boolean) values[1]) {
					// 生成接种列表
					mVaccinationDao.savaVaccinations(mBirthdate.getText()
							.toString(), mBabyName.getText().toString());
					// Intent intent = new Intent(getApplicationContext(),
					// MainActivity.class);
					// startActivity(intent);
					mPreferences.setIsExistBaby(true);// 下次启动直接进入主界面
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_success);

					Intent intent = new Intent(RegisterBabyActivity.this,
							VaccineChooseActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("baby", mAddBaby);
					intent.putExtra("firstAdd", "firstAdd");
					intent.putExtras(bundle);
					startActivity(intent);

					RegisterBabyActivity.this.finish();
				} else {
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_fail);
				}
			} else if (values[0].toString().equals("add")) {
				if ((Boolean) values[1]) {
					// 生成接种列表
					mVaccinationDao.savaVaccinations(mBirthdate.getText()
							.toString(), mBabyName.getText().toString());
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_success);
					Intent intent = new Intent(RegisterBabyActivity.this,
							VaccineChooseActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("baby", mAddBaby);
					intent.putExtras(bundle);
					startActivity(intent);
					RegisterBabyActivity.this.finish();
				} else {
					PublicMethod.showToast(getApplicationContext(),
							R.string.operation_fail);
				}
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();// 取消对话框
		}

	}

	// 设置日期对话框
	@SuppressLint("NewApi")
	private void showDateDialog() {
		if (mBirthdateDialog != null && mBirthdateDialog.isShowing()) {
			return;
		}
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

		AlertDialog.Builder birthdateDialog = new AlertDialog.Builder(this);
		birthdateDialog.setTitle(R.string.set_baby_birth);// 提示

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.set_date, null);
		// TextView dateLable = (TextView) view.findViewById(R.id.date_lable);
		// dateLable.setVisibility(View.GONE);
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			datePicker.setMaxDate(new Date().getTime());
		}
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

		birthdateDialog.setView(view);

		birthdateDialog.setPositiveButton(R.string.confirm,
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
		birthdateDialog.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		mBirthdateDialog = birthdateDialog.create();
		mBirthdateDialog.show();
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
		// case R.id.btn_baby_cancel: // 返回按钮
		// this.finish();
		// break;
		case R.id.baby_rlay:
			InputMethodManager imm = (InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
				.setTitle(R.string.set_head_img)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:// 选择本地照片
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);

							// android4.4
							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
								startActivityForResult(intentFromGallery,
										IMAGE_REQUEST_CODE_KITKAT);
							} else {
								startActivityForResult(intentFromGallery,
										IMAGE_REQUEST_CODE);
							}
							break;
						case 1:// 拍照
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							if (FileUtils.hasSdcard()) {
								// File path = Environment
								// .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
								mImageFile = new File(mImagePath, "IMG_"
										+ System.currentTimeMillis() + ".jpg");// 图片名称

								if (!mImageFile.exists()) {
									try {
										// 创建文件
										mImageFile.createNewFile();
									} catch (IOException e) {
										Toast.makeText(
												RegisterBabyActivity.this,
												"照片存储失败！", Toast.LENGTH_SHORT)
												.show();
									}
								}

								mOutputFileUri = Uri.fromFile(mImageFile);
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
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).show();

	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			Bitmap bitmap = null;
			switch (requestCode) {
			case IMAGE_REQUEST_CODE: // 选择本地图片
				Log.i("MainActivity", "本地图片imagePath="
						+ data.getData().getPath());
				ImageUtils.startPhotoZoom(this, data.getData(), 340,
						RESULT_REQUEST_CODE);
				mImageFile = new File(mImagePath, "IMG_"
						+ System.currentTimeMillis() + ".jpg");// 图片名称
				// 如果存在，则删除
				if (mImageFile.exists()) {
					mImageFile.delete();// 删除文件
				}
				// Uri uri = data.getData();
				// Log.e("MainActivity", "imgUri=" + data.getData().getPath()
				// + "--uri=" + uri);
				// String[] projection = { MediaStore.Images.Media.DATA };
				// Cursor cursor = getContentResolver().query(data.getData(),
				// projection, null, null, null);
				// if (cursor.moveToFirst()) {
				// int column_index = cursor
				// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				// babyImgPath = cursor.getString(column_index);
				// Log.i("MainActivity", "imgPath=" + babyImgPath);
				// bitmap = BitmapUtil.decodeSampledBitmapFromFile(
				// babyImgPath, 100, 100);
				// mBabyImage.setImageBitmap(bitmap);
				// }
				// cursor.close();
				break;
			case IMAGE_REQUEST_CODE_KITKAT: // 4.4
				Uri uri_KITKAT = data.getData();
				if (DocumentsContract.isDocumentUri(getApplicationContext(),
						uri_KITKAT)) {

					Log.e("MainActivity", "imgUri==="
							+ data.getData().getPath() + "--uri=" + uri_KITKAT);
					String wholeID = DocumentsContract
							.getDocumentId(uri_KITKAT);
					String id = wholeID.split(":")[1];
					String[] column = { MediaStore.Images.Media.DATA };
					String sel = MediaStore.Images.Media._ID + "=?";
					Cursor cursor2 = getContentResolver().query(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							column, sel, new String[] { id }, null);
					int columnIndex = cursor2.getColumnIndex(column[0]);
					if (cursor2.moveToFirst()) {
						babyImgPath = cursor2.getString(columnIndex);
						Log.i("MainActivity", "imgPath====" + babyImgPath);
						startPhotoZoom(babyImgPath);
						mImageFile = new File(mImagePath, "IMG_"
								+ System.currentTimeMillis() + ".jpg");// 图片名称
						// 如果存在，则删除
						if (mImageFile.exists()) {
							mImageFile.delete();// 删除文件
						}
						// bitmap = BitmapUtil.decodeSampledBitmapFromFile(
						// babyImgPath, 100, 100);
						// mBabyImage.setImageBitmap(bitmap);
					}
					cursor2.close();
				} else {
					Log.e("MainActivity", "imgUri4.4="
							+ data.getData().getPath() + "--uri4.4="
							+ uri_KITKAT);
					String[] column = { MediaStore.Images.Media.DATA };
					Cursor cursor2 = getContentResolver().query(data.getData(),
							column, null, null, null);
					if (cursor2.moveToFirst()) {
						int column_index = cursor2
								.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						babyImgPath = cursor2.getString(column_index);
						Log.i("MainActivity", "imgPath4.4=" + babyImgPath);
						startPhotoZoom(babyImgPath);
						mImageFile = new File(mImagePath, "IMG_"
								+ System.currentTimeMillis() + ".jpg");// 图片名称
						// 如果存在，则删除
						if (mImageFile.exists()) {
							mImageFile.delete();// 删除文件
						}
						// bitmap = BitmapUtil.decodeSampledBitmapFromFile(
						// babyImgPath, 100, 100);
						// mBabyImage.setImageBitmap(bitmap);
					}
					cursor2.close();
				}
				break;
			case CAMERA_REQUEST_CODE:// 拍照
				// startPhotoZoom(mOutputFileUri);
				// bitmap = BitmapUtil.decodeSampledBitmapFromFile(
				// mOutputFileUri.getPath(), 100, 100);
				// mBabyImage.setImageBitmap(bitmap);

				ImageUtils.startPhotoZoom(this, mOutputFileUri, 340,
						RESULT_REQUEST_CODE);
				Log.i("MainActivity",
						"拍照imagePath=" + mImageFile.getAbsolutePath());
				break;
			case RESULT_REQUEST_CODE: // 图片缩放完成后
				Bundle extras = data.getExtras();
				if (extras != null) {
					// 获取Bitmap对象
					bitmap = extras.getParcelable("data");
				}
				// 压缩图片
				try {
					Log.i("MainActivity", "------"+mImageFile.getAbsolutePath());
					// 创建FileOutputStream对象
					FileOutputStream fos = new FileOutputStream(mImageFile);
					// 开始压缩图片
					if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos)) {
						fos.flush();
						// 关闭流对象
						fos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 将图片显示到ImageView中
				mBabyImage.setImageBitmap(bitmap);
				Log.i("MainActivity", mImageFile.getAbsolutePath());
				babyImgPath = mImageFile.getAbsolutePath();
				break;
			}
		}
	}

	public void startPhotoZoom(String uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		try {
			intent.setData(Uri.parse(android.provider.MediaStore.Images.Media
					.insertImage(getContentResolver(), uri, null, null)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// intent.setDataAndType(uri, "image/*");
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
	// private void getImageToView(Intent data) {
	// Bundle extras = data.getExtras();
	// if (extras != null) {
	// Bitmap photo = extras.getParcelable("data");
	// Drawable drawable = new BitmapDrawable(this.getResources(), photo);
	// mBabyImage.setImageDrawable(drawable);
	// }
	// }

	// public static void setSpinnerItemSelectedByValue(Spinner spinner,String
	// value){
	// SpinnerAdapter apsAdapter= spinner.getAdapter(); //得到SpinnerAdapter对象
	// int k= apsAdapter.getCount();
	// for(int i=0;i<k;i++){
	// if(value.equals(apsAdapter.getItem(i).toString())){
	// spinner.setSelection(i,true);// 默认选中项
	// break;
	// }
	// }
	// }

	// 选择居住地对话框
	private void showCityDialog() {
		if (mCityDialog != null && mCityDialog.isShowing()) {
			return;
		}
		View view = LayoutInflater.from(this).inflate(
				R.layout.weather_city_list, null);
		// 省份Spinner
		mProvinceSpinner = (Spinner) view
				.findViewById(R.id.wea_province_spinner);
		// 城市Spinner
		mCitySpinner = (Spinner) view.findViewById(R.id.wea_city_spinner);
		// 区县Spinner
		mCountySpinner = (Spinner) view.findViewById(R.id.wea_county_spinner);
		// 省份列表
		// mProvinces = WebServiceUtil.getProvinceList();
		try {
			InputStream provincesXml = getResources().getAssets().open(
					"province.xml");
			List<Province> provinces = CityPullParseXml
					.getProvinces(provincesXml);
			mProvinces = new ArrayList<String>();
			for (Province province : provinces) {
				mProvinces.add(province.getProviceName());
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
						// mCitys = WebServiceUtil
						// .getCityListByProvince(mProvinces.get(position));
						String province = mProvinces.get(position);

						try {
							mCityList = CityPullParseXml.getCitysByProvince(
									getApplicationContext(), province);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (XmlPullParserException e) {
							e.printStackTrace();
						}
						mCitys = new ArrayList<String>();
						for (City city : mCityList) {
							mCitys.add(city.getCityName());
						}
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
				mCountys = getCountysByCity(mCity);
				ArrayAdapter<CityItem> countyAdapter = new ArrayAdapter<CityItem>(
						getApplicationContext(), R.layout.select_item, mCountys);
				countyAdapter.setDropDownViewResource(R.layout.select_item);
				mCountySpinner.setAdapter(countyAdapter);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		// 区县监听
		mCountySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// mCounty = mCountys.get(position);
				mCounty = mCountySpinner.getSelectedItem().toString();
				mCityCode = ((CityItem) mCountySpinner.getSelectedItem())
						.getCode();
				Log.i("MainActivity", mCounty + ":" + mCityCode);
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
						mResidence.setText(mCounty);
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

		mCityDialog = builder.create();
		// 显示Dialog
		mCityDialog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 根据城市查询区县
	 * 
	 * @param city
	 * @return
	 */
	public List<CityItem> getCountysByCity(String city) {
		List<CityItem> list = new ArrayList<CityItem>();
		Map<String, String> map = null;
		for (City ct : mCityList) {
			if (ct.getCityName().equals(city)) {
				map = ct.getCountys();
				for (Map.Entry<String, String> entry : map.entrySet()) {
					list.add(new CityItem(entry.getKey(), entry.getValue()));
				}
				return list;
			}
		}
		return null;
	}

}
