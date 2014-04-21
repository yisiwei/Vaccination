package cn.mointe.vaccination.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.CircleImageView;

public class VaccineChooseActivity extends ActionBarActivity {

	private static final String TAG = "MainActivity";

	private VaccinationDao mVaccinationDao;

	private CircleImageView mBabyImage;
	private Button mBtn;

	private Spinner mSpinner;
	private ArrayAdapter<String> mAdapter;
	private List<String> mVaccines;

	private Baby mBaby;
	private String mChooseItem;
	private String mFirstAdd;

	private static final String NO_VACCINATION = "还未接种过";

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccine_choose);

		mVaccinationDao = new VaccinationDao(getApplicationContext());

		mBabyImage = (CircleImageView) this.findViewById(R.id.cho_baby_image);
		mSpinner = (Spinner) this.findViewById(R.id.cho_spn);
		mBtn = (Button) this.findViewById(R.id.cho_next);

		mBaby = (Baby) getIntent().getSerializableExtra("baby");
		mFirstAdd = getIntent().getStringExtra("firstAdd");// 首次添加
		if (mBaby != null) {
			Log.i(TAG, mBaby.getName());
			String imgUri = mBaby.getImage();
			if (!StringUtils.isNullOrEmpty(imgUri)) {
				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri,
						100, 100);
				mBabyImage.setImageBitmap(bitmap);
			} else {
				mBabyImage.setImageResource(R.drawable.default_img);
			}
		}

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(
				R.string.loading_wait));
		mProgressDialog.setTitle(R.string.hint);

		mVaccines = getVaccines();
		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mVaccines);
		mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(mAdapter);

		// 设置默认选项，默认选中最后一项
		// mSpinner.setSelection(mVaccines.size() - 1, true);

		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG,
						"position:" + position + "--"
								+ mAdapter.getItem(position));

				mChooseItem = mAdapter.getItem(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		mBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 查询选择的疫苗，并将选择的疫苗以及之前的疫苗状态改为已完成
				new MyTask().execute();
			}
		});
	}

	private class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			if (!StringUtils.isNullOrEmpty(mChooseItem)
					&& !mChooseItem.equals(NO_VACCINATION)) {
				String[] arr = mChooseItem.split("-");
				String vaccineName = arr[0];
				String vaccineNumber = arr[1];
				String reserveDate = mVaccinationDao
						.getReserveDateByChooseVaccine(mBaby.getName(),
								vaccineName, vaccineNumber);
				List<Vaccination> vaccinations = mVaccinationDao
						.getVaccinationsByReserveTime(mBaby.getName(),
								reserveDate);
				mVaccinationDao.updateAllDueToFinish(vaccinations);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// 首次添加跳转到主界面
			if (mFirstAdd != null) {
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				startActivity(intent);
			}
			VaccineChooseActivity.this.finish();
			mProgressDialog.dismiss();
		}
	}

	/**
	 * 根据宝宝注册时间查询已过期的疫苗
	 * 
	 * @return
	 */
	private List<String> getVaccines() {
		List<String> list = new ArrayList<String>();
		List<Vaccination> vaccinations = null;
		if (mBaby != null) {
			String addDate = DateUtils.getCurrentFormatDate();
			vaccinations = mVaccinationDao
					.getVaccinationsByBabyNameAndAddBabyDate(mBaby.getName(),
							addDate);
		}
		list.add(0, NO_VACCINATION);
		for (Vaccination vac : vaccinations) {
			list.add(vac.getVaccine_name() + "-" + vac.getVaccination_number());
		}

		return list;
	}
}
