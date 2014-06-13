package cn.mointe.vaccination.activity;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.ChooseAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

import com.umeng.analytics.MobclickAgent;

public class ReserveActivity extends Activity {

	private Button mReserveDate;

	private Button mFinishBtn;
	private Baby mDefaultBaby;

	private String mNextDate;// 预约的下次接种时间

	private ListView mReserveView;
	private ChooseAdapter mReserveAdapter;

	private VaccinationDao mVaccinationDao;
	private BabyDao mBabyDao;

	private List<Vaccination> mSelectVaccinations;// 选择预约的下次接种的疫苗List

	private ProgressDialog mProgressDialog;

	private TextView mTitleText;
//	private ImageButton mTitleLeftImgbtn;// title左边图标
	private LinearLayout mTitleLeft;
	private ImageView mTitleRightImgbtn;// title右边图标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reserve);

		mVaccinationDao = new VaccinationDao(this);
		mBabyDao = new BabyDao(this);

		mTitleText = (TextView) this.findViewById(R.id.title_text);
//		mTitleLeftImgbtn = (ImageButton) this
//				.findViewById(R.id.title_left_imgbtn);
		mTitleLeft = (LinearLayout) this.findViewById(R.id.title_left);
		mTitleRightImgbtn = (ImageView) this
				.findViewById(R.id.title_right_imgbtn);

		mTitleText.setText(R.string.reserve_vaccination);
		mTitleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReserveActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		mReserveDate = (Button) this.findViewById(R.id.reserve_date);
		mFinishBtn = (Button) this.findViewById(R.id.reserve_finish_btn);
		mReserveView = (ListView) this.findViewById(R.id.reserve_vac_list);

		mDefaultBaby = mBabyDao.getDefaultBaby();

		mReserveAdapter = new ChooseAdapter(this, getVaccinations(),
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
		mReserveView.setAdapter(mReserveAdapter);

		mReserveDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 弹出日期选择框，让用户选择下次接种时间
				showDateDialog();
			}
		});

		mFinishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSelectVaccinations = mReserveAdapter.currentSelect();// 选择的疫苗List
				if (mSelectVaccinations != null
						&& mSelectVaccinations.size() != 0) {
					if (StringUtils.isNullOrEmpty(mReserveDate.getText()
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

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ReserveActivity"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ReserveActivity");
		MobclickAgent.onPause(this);
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

			mVaccinationDao.reserveNextVaccinations(mSelectVaccinations,
					mReserveDate.getText().toString());
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ReserveActivity.this.finish();
			mProgressDialog.dismiss();
		}
	}

	/**
	 * 未预约的疫苗List,让用户预约下次要打的疫苗
	 * 
	 * @return
	 */
	private List<Vaccination> getVaccinations() {
		List<Vaccination> vaccinations = mVaccinationDao
				.getNoReserveVaccinations(mDefaultBaby.getName());
		return vaccinations;
	}

	/**
	 * 选择预约时间对话框
	 */
	@SuppressLint("NewApi")
	private void showDateDialog() {

		Calendar calendar = Calendar.getInstance();

		String reserveDate = mReserveDate.getText().toString();
		if (!StringUtils.isNullOrEmpty(reserveDate)) {
			Date date = null;
			try {
				date = DateUtils.stringToDate(reserveDate);
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
							mReserveDate.setText(mNextDate);
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
							mReserveDate.setText(stringBuilder.toString());
						}
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		builder.create().show();
	}
}
