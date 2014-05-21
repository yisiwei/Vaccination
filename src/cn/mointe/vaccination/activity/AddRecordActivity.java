package cn.mointe.vaccination.activity;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.DatePicker.OnDateChangedListener;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.ChooseAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.DiaryDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Diary;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

public class AddRecordActivity extends Activity implements OnClickListener {

	private Button mDateBtn;
	private AlertDialog mDateDialog;
	private String mFinishDate;

	private Button mFinishBtn;

	private ListView mListView;
	private ChooseAdapter mAdapter;

	private BabyDao mBabyDao;
	private VaccinationDao mVaccinationDao;
	private DiaryDao mDiaryDao;

	private Baby mDefaultBaby;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_record);

		mBabyDao = new BabyDao(this);
		mVaccinationDao = new VaccinationDao(this);
		mDiaryDao = new DiaryDao(this);
		
		mDefaultBaby = mBabyDao.getDefaultBaby();

		mDateBtn = (Button) this.findViewById(R.id.add_record_date_btn);
		mFinishBtn = (Button) this.findViewById(R.id.add_record_finish_btn);
		mListView = (ListView) this.findViewById(R.id.add_record_list);

		mAdapter = new ChooseAdapter(this, getVaccinations(),
				new ChooseAdapter.OnSelectedItemChanged() {

					@Override
					public void getSelectedItem(Vaccination vaccination) {
						Toast.makeText(
								getApplicationContext(),
								vaccination.getVaccine_name() + "("
										+ vaccination.getVaccination_number()
										+ ")", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void getSelectedCount(int count) {

					}
				});
		mListView.setAdapter(mAdapter);

		mDateBtn.setOnClickListener(this);
		mFinishBtn.setOnClickListener(this);
	}

	/**
	 * 数据
	 * @return
	 */
	private List<Vaccination> getVaccinations() {
		List<Vaccination> vaccinations = mVaccinationDao
				.queryNotVaccinations(mDefaultBaby.getName());

		return vaccinations;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.add_record_date_btn) {
			// TODO 选择时间
			showDateDialog();
		} else if (v.getId() == R.id.add_record_finish_btn) {
			// TODO 完成补录
			if (StringUtils.isNullOrEmpty(mDateBtn.getText().toString())) {
				Toast.makeText(this, "选择接种时间", Toast.LENGTH_SHORT).show();
				return;
			}
			List<Vaccination> vaccinations = mAdapter.currentSelect();
			if (vaccinations == null || vaccinations.size()<=0) {
				Toast.makeText(this, "选择补录疫苗", Toast.LENGTH_SHORT).show();
				return;
			}
			for(Vaccination vac : vaccinations){
				vac.setFinish_time(mDateBtn.getText().toString());
				mVaccinationDao.updateFinishTimeById(vac);
			}
			Toast.makeText(this, "补录成功", Toast.LENGTH_SHORT).show();
			Diary diary = mDiaryDao.queryDiary(mDefaultBaby.getName(), mDateBtn.getText().toString());
			if (diary == null) {
				mDiaryDao.saveDiary(new Diary(mDefaultBaby.getName(), mDateBtn.getText().toString(), null));
			}
			this.finish();
		}
	}

	@SuppressLint("NewApi")
	private void showDateDialog() {
		if (mDateDialog != null && mDateDialog.isShowing()) {
			return;
		}

		Calendar calendar = Calendar.getInstance();

		String finishDate = mDateBtn.getText().toString();
		if (!StringUtils.isNullOrEmpty(finishDate)) {
			Date date = null;
			try {
				date = DateUtils.stringToDate(finishDate);
				calendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		final int year = calendar.get(Calendar.YEAR);
		final int monthOfYear = calendar.get(Calendar.MONDAY);
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_icon);
		builder.setTitle("接种时间");

		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.set_date, null);
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			Calendar maxCalendar = Calendar.getInstance();
			datePicker.setMaxDate(maxCalendar.getTimeInMillis());
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

							mFinishDate = stringBuilder.toString();
							Log.i(Constants.TAG, "mFinishDate=" + mFinishDate);
						}
					});
		} else {
			final Calendar cal = Calendar.getInstance();
			final int maxYear = cal.get(Calendar.YEAR);
			final int maxMonth = cal.get(Calendar.MONTH);
			final int maxDay = cal.get(Calendar.DAY_OF_MONTH);
			datePicker.init(year, monthOfYear, dayOfMonth,
					new OnDateChangedListener() {
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

							Calendar newDate = Calendar.getInstance();
							newDate.set(year, monthOfYear, dayOfMonth);

							if (cal.after(newDate)) {
								view.init(maxYear, maxMonth, maxDay, this);
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

							mFinishDate = stringBuilder.toString();
							Log.i(Constants.TAG, "mFinishDate=" + mFinishDate);
						}
					});
		}
		builder.setView(view);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mFinishDate != null) {
							mDateBtn.setText(mFinishDate);
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
							mDateBtn.setText(stringBuilder.toString());
						}
					}
				});
		builder.setNegativeButton(R.string.cancel, null);

		mDateDialog = builder.create();
		mDateDialog.show();
	}

}
