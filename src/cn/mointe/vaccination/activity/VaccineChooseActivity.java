package cn.mointe.vaccination.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.ChooseAdapter;
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

	private CircleImageView mBabyImage;// 宝宝头像
	private Button mBtn;// 下一步按钮

	private Button mAllBtn;// 全选按钮
	private Button mNoneBtn;// 全不选按钮
	private ChooseAdapter mChooseAdapter;
	private ListView mVaccinationView;
	private List<Vaccination> mSelectVaccinations;// 选择的疫苗List

	private Baby mBaby;// 宝宝
	private String mFirstAdd;// 首次添加宝宝

	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccine_choose);

		mVaccinationDao = new VaccinationDao(getApplicationContext());

		mBabyImage = (CircleImageView) this.findViewById(R.id.cho_baby_image);
		mBtn = (Button) this.findViewById(R.id.cho_next);
		mAllBtn = (Button) this.findViewById(R.id.choose_btn_all);
		mNoneBtn = (Button) this.findViewById(R.id.choose_btn_none);
		mVaccinationView = (ListView) this.findViewById(R.id.choose_vac_list);

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

		// 全选按钮监听
		mAllBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChooseAdapter.selectAll();
			}
		});
		// 全不选按钮监听
		mNoneBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mChooseAdapter.disSelectAll();
			}
		});

		// adapter
		mChooseAdapter = new ChooseAdapter(this, getVaccinations(),
				new ChooseAdapter.OnSelectedItemChanged() {

					@Override
					public void getSelectedItem(Vaccination vaccination) {
						// Toast.makeText(getApplicationContext(),
						// vaccination.getVaccine_name()+"("+vaccination.getVaccination_number()+")",
						// Toast.LENGTH_SHORT).show();
					}

					@Override
					public void getSelectedCount(int count) {

					}
				});
		mVaccinationView.setAdapter(mChooseAdapter);

		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getResources().getString(
				R.string.loading_wait));
		mProgressDialog.setTitle(R.string.hint);

		// 确认按钮监听
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
			mSelectVaccinations = mChooseAdapter.currentSelect();
			for (Vaccination vac : mSelectVaccinations) {
				Log.i("MainActivity",
						vac.getVaccine_name() + "("
								+ vac.getVaccination_number() + ")");
			}
			if (mSelectVaccinations != null && mSelectVaccinations.size() != 0) {
				mVaccinationDao.updateAllDueToFinish(mSelectVaccinations);
			}
			// if (!StringUtils.isNullOrEmpty(mChooseItem)
			// && !mChooseItem.equals(NO_VACCINATION)) {
			// String[] arr = mChooseItem.split("-");
			// String vaccineName = arr[0];
			// String vaccineNumber = arr[1];
			// String reserveDate = mVaccinationDao
			// .getReserveDateByChooseVaccine(mBaby.getName(),
			// vaccineName, vaccineNumber);
			// List<Vaccination> vaccinations = mVaccinationDao
			// .getVaccinationsByReserveTime(mBaby.getName(),
			// reserveDate);
			// mVaccinationDao.updateAllDueToFinish(vaccinations);
			// }
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
	// private List<String> getVaccines() {
	// List<String> list = new ArrayList<String>();
	// List<Vaccination> vaccinations = null;
	// if (mBaby != null) {
	// String addDate = DateUtils.getCurrentFormatDate();
	// vaccinations = mVaccinationDao
	// .getVaccinationsByBabyNameAndAddBabyDate(mBaby.getName(),
	// addDate);
	// }
	// list.add(0, NO_VACCINATION);
	// for (Vaccination vac : vaccinations) {
	// list.add(vac.getVaccine_name() + "-" + vac.getVaccination_number());
	// }
	// return list;
	// }

	/**
	 * 根据宝宝注册时间查询已过期的疫苗
	 * 
	 * @return
	 */
	private List<Vaccination> getVaccinations() {
		List<Vaccination> vaccinations = null;
		if (mBaby != null) {
			String addDate = DateUtils.getCurrentFormatDate();
			vaccinations = mVaccinationDao
					.getVaccinationsByBabyNameAndAddBabyDate(mBaby.getName(),
							addDate);
		}
		return vaccinations;
	}
}
