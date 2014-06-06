package cn.mointe.vaccination.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.activity.VaccinationDetailActivity;
import cn.mointe.vaccination.adapter.VaccinationCategoryAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.VaccinationCategory;
import cn.mointe.vaccination.provider.BabyProvider;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.view.CircleImageView;

/**
 * 疫苗接种列表界面
 * 
 * @author yi_siwei
 * 
 */
public class VaccineListFragment extends Fragment implements OnClickListener {

	private static final String TAG = "MainActivity";

	private ListView mListView;// 接种ListView
	private VaccinationCategoryAdapter mCategoryAdapter;

	private BabyDao mBabyDao;

	private CircleImageView mBabyImg;// 宝宝头像
	private TextView mBabyName; // baby昵称
	private TextView mBabyAge;// baby月龄
	private Baby mDefaultBaby; // 默认baby

	private LoaderManager mManager;
	private LoaderManager mBabyLoaderManager;

	private List<Vaccination> mVaccinations;

	private List<VaccinationCategory> mListData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBabyDao = new BabyDao(getActivity());
		mManager = getLoaderManager();
		mBabyLoaderManager = getLoaderManager();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "VaccineListFragment...onCreateView");
		View view = inflater.inflate(R.layout.fragment_vaccine_list, null);

		// 初始化控件
		mBabyImg = (CircleImageView) view
				.findViewById(R.id.vac_list_iv_babyImg);// baby头像
		mBabyName = (TextView) view.findViewById(R.id.vac_list_tv_babyName);// baby昵称
		mBabyAge = (TextView) view.findViewById(R.id.vac_list_tv_babyAge);// baby月龄

		mListView = (ListView) view.findViewById(R.id.vaccine_list_lv);// 接种ListView

		mBabyImg.setOnClickListener(this);// baby头像点击

		// ListView Item 点击监听
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						VaccinationDetailActivity.class);
				// Vaccination vaccination = (Vaccination) mAdapter
				// .getItem(position);
				// Toast.makeText(getActivity(), "position=" + position,
				// Toast.LENGTH_SHORT).show();
				Vaccination vaccination = (Vaccination) mCategoryAdapter
						.getItem(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Vaccination", vaccination);
				intent.putExtras(bundle);
				intent.putExtra("birthdate", mDefaultBaby.getBirthdate());
				startActivity(intent);
			}
		});

		mBabyLoaderManager.initLoader(100, null, mBabyLoaderCallBacks);

		return view;
	}

	/**
	 * 设置baby相关信息
	 */
	private void setBabyInfo() {
		if (mDefaultBaby != null) {
			mBabyName.setText(mDefaultBaby.getName());
			// mCity.setText(mDefaultBaby.getResidence());
			String birthdate = mDefaultBaby.getBirthdate();
			try {
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
						Locale.getDefault());
				String dateString = format.format(date);
				Date today = format.parse(dateString);
				long month_number = DateUtils.getMonth(birthdate, today);
				if (month_number == 0) {
					mBabyAge.setText("未满月");
				} else if (month_number < 12) {
					mBabyAge.setText(month_number + "月龄");
				} else if (month_number == 12) {
					mBabyAge.setText("1周岁");
				} else if (month_number > 12 && month_number < 24) {
					mBabyAge.setText(month_number + "月龄");
				} else if (month_number >= 24 && month_number < 36) {
					mBabyAge.setText("2周岁");
				} else if (month_number >= 36 && month_number < 48) {
					mBabyAge.setText("3周岁");
				} else if (month_number >= 48 && month_number < 60) {
					mBabyAge.setText("4周岁");
				} else if (month_number >= 60 && month_number < 72) {
					mBabyAge.setText("5周岁");
				} else if (month_number >= 72) {
					mBabyAge.setText("6周岁");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String imgUri = mDefaultBaby.getImage();
			if (!TextUtils.isEmpty(imgUri)) {
				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri,
						100, 100);
				mBabyImg.setImageBitmap(bitmap);
			} else {
				mBabyImg.setImageResource(R.drawable.default_img);
			}
		}
	}

	@Override
	public void onResume() {
		Log.i(TAG, "VaccineListFragment...onResume");
		super.onResume();
		MobclickAgent.onPageStart("VaccineListFragment"); //统计页面
	}

	@Override
	public void onPause() {
		Log.i(TAG, "VaccineListFragment...onPause");
		super.onPause();
		MobclickAgent.onPageEnd("VaccineListFragment"); 
	}

	// 点击监听
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.vac_list_iv_babyImg:
			intent = new Intent(getActivity(), RegisterBabyActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("baby", mDefaultBaby);
			intent.putExtras(mBundle);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private LoaderCallbacks<Cursor> mVaccinationCallBacks = new LoaderCallbacks<Cursor>() {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(getActivity());
			loader.setUri(VaccinationProvider.CONTENT_URI);
			loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
					+ "=?");
			loader.setSelectionArgs(new String[] { mDefaultBaby.getName() });
			loader.setSortOrder("case "
					+ DBHelper.VACCINATION_COLUMN_MOON_AGE
					+ " when '出生24小时内' then 1 when '1月龄' then 2 when '2月龄' then 3 when '3月龄' then 4 "
					+ " when '4月龄' then 5 when '5月龄' then 6 when '6月龄' then 7 when '7月龄' then 8 "
					+ " when '8月龄' then 9 when '9月龄' then 10 when '1周岁' then 11 when '14月龄' then 12 "
					+ " when '1岁半' then 13 when '2周岁' then 14 when '3周岁' then 15 when '4周岁' then 16 "
					+ " when '6周岁' then 17 end,"
					+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mVaccinations = new ArrayList<Vaccination>();
			while (data.moveToNext()) {
				Vaccination vaccination = cursorToVaccination(data);
				mVaccinations.add(vaccination);
			}

			mListData = new ArrayList<VaccinationCategory>();
			List<String> moonAgeList = new ArrayList<String>();
			String[] moonAge = Constants.MAIN_MOON_AGE;
			for (int i = 0; i < moonAge.length; i++) {
				moonAgeList.add(moonAge[i]);
			}
			for (String categoryName : moonAgeList) {
				VaccinationCategory category = new VaccinationCategory(
						categoryName);
				for (Vaccination vaccination : mVaccinations) {
					if (categoryName.equals(vaccination.getMoon_age())) {
						category.addItem(vaccination);
					}
				}
				mListData.add(category);
			}

			mCategoryAdapter = new VaccinationCategoryAdapter(getActivity(),
					mListData);
			mListView.setAdapter(mCategoryAdapter);
			mCategoryAdapter.notifyDataSetChanged();

		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

	/**
	 * 将Cursor转化为Vaccination
	 * 
	 * @param data
	 * @return
	 */
	public static Vaccination cursorToVaccination(Cursor data) {

		int _id = data.getInt(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_ID));
		String name = data.getString(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME));
		String moon_age = data.getString(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_MOON_AGE));

		String charge_standard = data.getString(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_CHARGE_STANDARD));
		String finish_time = data.getString(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_FINISH_TIME));
		String reserve_time = data.getString(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));

		String vaccination_number = data
				.getString(data
						.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER));
		String vaccine_type = data.getString(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE));
		String baby_nickname = data.getString(data
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME));

		Vaccination vaccination = new Vaccination(_id, name, reserve_time,
				finish_time, moon_age, vaccine_type, charge_standard,
				vaccination_number, baby_nickname);
		return vaccination;
	}

	/**
	 * 查询默认宝宝
	 */
	private LoaderCallbacks<Cursor> mBabyLoaderCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(getActivity());
			loader.setUri(BabyProvider.CONTENT_URI);
			loader.setSelection(DBHelper.BABY_COLUMN_IS_DEFAULT + "=?");
			loader.setSelectionArgs(new String[] { "1" });
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (data.moveToFirst()) {
				mDefaultBaby = mBabyDao.cursorToBaby(data);
				setBabyInfo();
				mManager.restartLoader(1001, null, mVaccinationCallBacks);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

}
