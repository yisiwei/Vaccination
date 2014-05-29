package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.ChooseAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.other.AddBabyAgent;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.CircleImageView;

public class ReservationActivity extends Activity {

	private CircleImageView mBabyImage;
	private Button mReservationDate;

	private Button mNextBtn;
	private Baby mBaby;

	private String mNextDate;// 预约的下次接种时间

	private ListView mReservationView;
	private ChooseAdapter mReservationAdapter;

	private VaccinationDao mVaccinationDao;
	private BabyDao mBabyDao;
	private VaccinationPreferences mPreferences;

	private List<Vaccination> mChooseVaccinations;// 上一步传过来的已接种的疫苗List
	private List<Vaccination> mSelectVaccinations;// 选择的下次接种的疫苗List

	private ProgressDialog mProgressDialog;
	private String mFirstAdd;
	
	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation);

		AddBabyAgent.getInstance().addActivity(this);
		mVaccinationDao = new VaccinationDao(this);
		mBabyDao = new BabyDao(this);
		mPreferences = new VaccinationPreferences(this);
		
		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);
		
		mTitleText.setText(R.string.next);
		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReservationActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		mBabyImage = (CircleImageView) this
				.findViewById(R.id.reservation_baby_image);
		mReservationDate = (Button) this.findViewById(R.id.reservation_date);
		mNextBtn = (Button) this.findViewById(R.id.reservation_next);
		mReservationView = (ListView) this
				.findViewById(R.id.reservation_vac_list);

		Intent intent = getIntent();
		mBaby = (Baby) intent.getSerializableExtra("baby");
		mChooseVaccinations = (List<Vaccination>) intent
				.getSerializableExtra("selectVaccinations");
		mFirstAdd = intent.getStringExtra("firstAdd");
		Log.i("MainActivity", mFirstAdd + "--babyName:" + mBaby.getName());
		if (mChooseVaccinations != null && mChooseVaccinations.size() != 0) {
			Log.i("MainActivity",
					"vaccinations:"
							+ mChooseVaccinations.size()
							+ "--"
							+ mChooseVaccinations.get(
									mChooseVaccinations.size() - 1)
									.getReserve_time());
		}

		if (mBaby != null) {
			Log.i("MainActivity", mBaby.getName());
			String imgUri = mBaby.getImage();
			if (!StringUtils.isNullOrEmpty(imgUri)) {
				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri,
						100, 100);
				mBabyImage.setImageBitmap(bitmap);
			} else {
				mBabyImage.setImageResource(R.drawable.default_img);
			}
		}

		mReservationAdapter = new ChooseAdapter(this, getVaccinations(),
				new ChooseAdapter.OnSelectedItemChanged() {

					@Override
					public void getSelectedItem(Vaccination vaccination) {
						// Toast.makeText(
						// getApplicationContext(),
						// vaccination.getVaccine_name() + "("
						// + vaccination.getVaccination_number()
						// + ")", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void getSelectedCount(int count) {

					}
				});
		mReservationView.setAdapter(mReservationAdapter);

		mReservationDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 弹出日期选择框，让用户选择下次接种时间
				showDateDialog();
			}
		});

		mNextBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSelectVaccinations = mReservationAdapter.currentSelect();// 选择的疫苗List
				if (mSelectVaccinations != null
						&& mSelectVaccinations.size() != 0) {
					if (StringUtils.isNullOrEmpty(mReservationDate.getText()
							.toString())) {
						Toast.makeText(getApplicationContext(), "选择预约时间",
								Toast.LENGTH_SHORT).show();
					} else {
						new MyTask().execute();
					}
				} else {
					Toast.makeText(getApplicationContext(), "预约下次接种疫苗",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(
				R.string.loading_wait));
		mProgressDialog.setTitle(R.string.hint);
	}

	/**
	 * 完成处理任务
	 */
	private class MyTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {

			// TODO 点击完成 1.添加宝宝 2. 生成接种记录
			// 3.选择已经接种的疫苗为已接种，选择的下次接种的疫苗为未接种，其余为未预约
			boolean result = mBabyDao.saveBaby(mBaby);// 添加宝宝
			if (result) {
				try {
					mVaccinationDao.createVaccinations(mBaby.getName());// 生成接种记录
					mVaccinationDao.updateHaveToFinish(mChooseVaccinations);// 将上一步选择的疫苗该为已接种
					if (mSelectVaccinations != null
							&& mSelectVaccinations.size() != 0) {
						mVaccinationDao.updateChooseToNoVaccination(
								mSelectVaccinations, mReservationDate.getText()
										.toString());
					}
					
					// 只有首次进入才会执行
					if (!StringUtils.isNullOrEmpty(mFirstAdd)) {
						// 将下次接种日期存入preferences
						mPreferences.setRemindDate(mReservationDate.getText()
								.toString());
						// 设置提醒
						mPreferences.setNotify(true);
						// 启动服务
						Intent remindService = new Intent(ReservationActivity.this,
								VaccinationRemindService.class);
						startService(remindService);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (!StringUtils.isNullOrEmpty(mFirstAdd)) {
				//首次进入跳转到主界面
				startActivity(new Intent(ReservationActivity.this, MainActivity.class));
				mPreferences.setIsExistBaby(true);//下次启动直接进入主界面
			}
			AddBabyAgent.getInstance().exit();
			mProgressDialog.dismiss();
		}
	}

	/**
	 * 未接种的疫苗List,让用户选择下次要打的疫苗
	 * 
	 * @return
	 */
	private List<Vaccination> getVaccinations() {
		List<Vaccination> vaccinations = null;
		if (mBaby != null) {
			try {
				vaccinations = mVaccinationDao.getVaccinationsOfReservation(
						mBaby.getName(), mBaby.getBirthdate());
				if (mChooseVaccinations != null
						&& mChooseVaccinations.size() != 0) {
					Iterator<Vaccination> iterator = vaccinations.iterator();
					while (iterator.hasNext()) {
						Vaccination vac = iterator.next();
						for (Vaccination vaccination : mChooseVaccinations) {
							if (vac.getVaccine_name().equals(
									vaccination.getVaccine_name())
									&& vac.getVaccination_number()
											.equals(vaccination
													.getVaccination_number())) {
								iterator.remove();
								Log.i("MainActivity",
										"size:" + vaccinations.size());
								continue;
							}
						}
					}
				}
				// if (mChooseVaccinations != null && mChooseVaccinations.size()
				// != 0) {
				// boolean flag = vaccinations.removeAll(mChooseVaccinations);
				// Log.i("MainActivity", flag+"--"+vaccinations.size());
				// }
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return vaccinations;
	}

	/**
	 * 选择下次接种时间对话框
	 */
	@SuppressLint("NewApi")
	private void showDateDialog() {

		Calendar calendar = Calendar.getInstance();

		String reservationDate = mReservationDate.getText().toString();
		if (!StringUtils.isNullOrEmpty(reservationDate)) {
			Date date = null;
			try {
				date = DateUtils.stringToDate(reservationDate);
				calendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		final int year = calendar.get(Calendar.YEAR);
		final int monthOfYear = calendar.get(Calendar.MONDAY);
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.set_date, null);
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			datePicker.setMinDate(calendar.getTimeInMillis());
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

							mNextDate = stringBuilder.toString();
							Log.i(Constants.TAG, "mNextDate=" + mNextDate);
						}
					});
		} else {
			final Calendar cal = Calendar.getInstance();
			final int minYear = cal.get(Calendar.YEAR);
			final int minMonth = cal.get(Calendar.MONTH);
			final int minDay = cal.get(Calendar.DAY_OF_MONTH);
			datePicker.init(year, monthOfYear, dayOfMonth,
					new OnDateChangedListener() {
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

							Calendar newDate = Calendar.getInstance();
							newDate.set(year, monthOfYear, dayOfMonth);

							if (cal.after(newDate)) {
								view.init(minYear, minMonth, minDay, this);
							}

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

							mNextDate = stringBuilder.toString();
							Log.i(Constants.TAG, "mNextDate=" + mNextDate);
						}
					});
		}
		builder.setView(view);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mNextDate != null) {
							mReservationDate.setText(mNextDate);
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
							mReservationDate.setText(stringBuilder.toString());
						}
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}
}
