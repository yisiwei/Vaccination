package cn.mointe.vaccination.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ksoap2.serialization.SoapObject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.activity.VaccinationDetailActivity;
import cn.mointe.vaccination.activity.WeatherActivity;
import cn.mointe.vaccination.adapter.VaccinationAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.WebServiceUtil;
import cn.mointe.vaccination.view.CircleImageView;

/**
 * 疫苗接种列表界面
 * 
 * @author yi_siwei
 * 
 */
public class VaccineListFragment extends Fragment implements OnClickListener,
		LoaderCallbacks<Cursor> {

	private static final String TAG = "MainActivity";

	private Button mNextVaccinationBtn;// 距离下次接种时间

	private ListView mListView;// 接种ListView
	private VaccinationAdapter mAdapter;// 接种adapter

	private BabyDao mBabyDao;

	private long mRemindDay;// 距离下次接种天数

	private int mCurrent = 0;// 用于定位到下次接种的疫苗

	private CircleImageView mBabyImg;// 宝宝头像
	private TextView mBabyName; // baby昵称
	private TextView mBabyAge;// baby月龄
	private Baby mDefaultBaby; // 默认baby

	private ImageView mWeatherImg;// 天气图片
	private TextView mWeather;// 天气温度

	private LoaderManager mManager;
	List<Vaccination> mVaccinations;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBabyDao = new BabyDao(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "VaccineListFragment...onCreateView");
		View view = inflater.inflate(R.layout.fragment_vaccine_list, null);

		// 查询默认baby
		mDefaultBaby = mBabyDao.getDefaultBaby();

		// 初始化控件
		mBabyImg = (CircleImageView) view.findViewById(R.id.vac_list_iv_babyImg);// baby头像
		mBabyName = (TextView) view.findViewById(R.id.vac_list_tv_babyName);// baby昵称
		mBabyAge = (TextView) view.findViewById(R.id.vac_list_tv_babyAge);// baby月龄
		mWeather = (TextView) view.findViewById(R.id.vac_list_temperature);// 天气温度
		mWeatherImg = (ImageView) view.findViewById(R.id.vac_list_iv_weather);// 天气图片

		mNextVaccinationBtn = (Button) view
				.findViewById(R.id.vaccine_btn_next_vaccination);// 下次接种Button
		mListView = (ListView) view.findViewById(R.id.vaccine_list_lv);// 接种ListView

		// 设置button点击监听
		mWeatherImg.setOnClickListener(this);
		mNextVaccinationBtn.setOnClickListener(this);// 距下次接种点击
		mBabyImg.setOnClickListener(this);// baby头像点击

		mManager = getLoaderManager();
		mManager.initLoader(1000, null, this);

		// 用AsyncTask查询天气
		MyTask task = new MyTask();
		task.execute(mDefaultBaby.getResidence());

		// ListView Item 点击监听
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						VaccinationDetailActivity.class);
				Vaccination vaccination = (Vaccination) mAdapter
						.getItem(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Vaccination", vaccination);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		return view;
	}

	/**
	 * 设置baby相关信息
	 */
	private void setBabyInfo() {
		if (mDefaultBaby != null) {
			mBabyName.setText(mDefaultBaby.getName());
			String birthdate = mDefaultBaby.getBirthdate();
			try {
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
						Locale.getDefault());
				String dateString = format.format(date);
				Date today = format.parse(dateString);
				long month_number = DateUtils.getMonth(birthdate, today);
				if (month_number < 12) {
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

	/**
	 * 计算距离下次接种时间
	 */
	private void getNextVaccinationTime() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
				Locale.getDefault());
		try {
			int index = 0;
			for (Vaccination vaccination : mVaccinations) {
				index++;
				String reserveTime = vaccination.getReserve_time();// 取得预约接种时间
				Date vaccineFormatDate = format.parse(reserveTime);// 转化为date类型
				String todayString = format.format(date);
				Date today = format.parse(todayString);

				int result = today.compareTo(vaccineFormatDate);// 与当前时间比较
				if (TextUtils.isEmpty(vaccination.getFinish_time())
						&& result <= 0) {
					VaccinationPreferences preferences = new VaccinationPreferences(
							getActivity());
					// 只有首次进入主界面才会执行
					if (TextUtils.isEmpty(preferences.getRemindDate())) {
						// 将下次接种日期存入preferences
						preferences
								.setRemindDate(vaccination.getReserve_time());
						// 启动服务
						Intent remindService = new Intent(getActivity(),
								VaccinationRemindService.class);
						getActivity().startService(remindService);
						// 设置提醒
						preferences.setNotify(true);
					}
					mRemindDay = (vaccineFormatDate.getTime() - today.getTime())
							/ (1000 * 60 * 60 * 24);
					if (mRemindDay == 1) {
						mNextVaccinationBtn.setText("明天有接种");
					}else if(mRemindDay == 0){
						mNextVaccinationBtn.setText("今天有接种");
					}else {
						String remind = String.format(
								getResources().getString(R.string.distance),
								mRemindDay);
						mNextVaccinationBtn.setText(remind);
					}
					mCurrent = index - 1;// 下标从0开始，所以-1
					break;
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 天气后台任务
	 * 
	 */
	private class MyTask extends AsyncTask<String, Void, SoapObject> {

		@Override
		protected SoapObject doInBackground(String... params) {

			SoapObject detail = WebServiceUtil.getWeatherByCity(params[0]);

			return detail;
		}

		@Override
		protected void onPostExecute(SoapObject result) {
			super.onPostExecute(result);
			if (result != null) {
				mWeather.setText(result.getProperty(8).toString());

				int icon = WeatherActivity.parseIcon(result.getProperty(10)
						.toString());
				mWeatherImg.setImageResource(icon);
			}
		}

	}

	@Override
	public void onResume() {
		Log.i(TAG, "VaccineListFragment...onResume");
		mDefaultBaby = mBabyDao.getDefaultBaby();
		setBabyInfo();// 设置宝宝相关信息
		mManager.restartLoader(1001, null, this);
		super.onResume();
	}

	@Override
	public void onPause() {
		Log.i(TAG, "VaccineListFragment...onPause");
		super.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "VaccineListFragment...onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 点击监听
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.vac_list_iv_weather: // 查看未来3天天气
			intent = new Intent(getActivity(), WeatherActivity.class);
			intent.putExtra("city", mDefaultBaby.getResidence());
			startActivity(intent);
			break;
		case R.id.vaccine_btn_next_vaccination:
			mListView.setSelectionFromTop(mCurrent, 20);// 定位到指定item
			mAdapter.setSelectItem(mCurrent);
			mAdapter.notifyDataSetInvalidated();
			break;
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

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(getActivity());
		loader.setUri(VaccinationProvider.CONTENT_URI);
		loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=?");
		loader.setSelectionArgs(new String[] { mDefaultBaby.getName() });
		loader.setSortOrder(DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// mCursorAdapter.swapCursor(data);
		mVaccinations = new ArrayList<Vaccination>();
		while (data.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(data);
			mVaccinations.add(vaccination);
		}
		mAdapter = new VaccinationAdapter(getActivity(), mVaccinations);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		getNextVaccinationTime();// 距离下次接种时间
		mListView.setSelectionFromTop(mCurrent, 20);// 定位到指定item
		mAdapter.setSelectItem(mCurrent);
		mAdapter.notifyDataSetInvalidated();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// mCursorAdapter.swapCursor(null);
	}

	/**
	 * 将Cursor转化为Vaccination
	 * 
	 * @param data
	 * @return
	 */
	public Vaccination cursorToVaccination(Cursor data) {
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

}
