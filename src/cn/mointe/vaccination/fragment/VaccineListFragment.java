package cn.mointe.vaccination.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
public class VaccineListFragment extends Fragment implements OnClickListener,
		LoaderCallbacks<Cursor> {

	private static final String TAG = "MainActivity";

	//private Button mNextVaccinationBtn;// 距离下次接种时间

	private ListView mListView;// 接种ListView
	// private VaccinationAdapter mAdapter;// 接种adapter
	private VaccinationCategoryAdapter mCategoryAdapter;

	private BabyDao mBabyDao;

	//private long mRemindDay;// 距离下次接种天数

	// private int mCurrent = 0;// 用于定位到下次接种的疫苗

	private CircleImageView mBabyImg;// 宝宝头像
	private TextView mBabyName; // baby昵称
	private TextView mBabyAge;// baby月龄
	private Baby mDefaultBaby; // 默认baby

	// private ImageView mWeatherImg;// 天气图片
	//private TextView mTemp;// 天气温度
	//private TextView mCity;
	//private TextView mWindStrength;// 风力

	private LoaderManager mManager;
	List<Vaccination> mVaccinations;

	private List<VaccinationCategory> mListData;
	//private int mMoonAgeLable;

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
		mBabyImg = (CircleImageView) view
				.findViewById(R.id.vac_list_iv_babyImg);// baby头像
		mBabyName = (TextView) view.findViewById(R.id.vac_list_tv_babyName);// baby昵称
		mBabyAge = (TextView) view.findViewById(R.id.vac_list_tv_babyAge);// baby月龄

//		mCity = (TextView) view.findViewById(R.id.vac_list_tv_city);
//		mTemp = (TextView) view.findViewById(R.id.vac_list_temperature);// 天气温度
//		mWindStrength = (TextView) view
//				.findViewById(R.id.vac_list_wind_strength);// 天气风力
		// mWeatherImg = (ImageView)
		// view.findViewById(R.id.vac_list_iv_weather);// 天气图片

//		mNextVaccinationBtn = (Button) view
//				.findViewById(R.id.vaccine_btn_next_vaccination);// 下次接种Button
		mListView = (ListView) view.findViewById(R.id.vaccine_list_lv);// 接种ListView

		// 设置button点击监听
		// mWeatherImg.setOnClickListener(this);
		//mNextVaccinationBtn.setOnClickListener(this);// 距下次接种点击
		mBabyImg.setOnClickListener(this);// baby头像点击

		mManager = getLoaderManager();
		// mManager.initLoader(1000, null, this);

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

		return view;
	}

	/**
	 * 设置baby相关信息
	 */
	private void setBabyInfo() {
		if (mDefaultBaby != null) {
			mBabyName.setText(mDefaultBaby.getName());
			//mCity.setText(mDefaultBaby.getResidence());
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

	/**
	 * 计算距离下次接种时间
	 */
	// private void getNextVaccinationTime() {
	// Date date = new Date();
	// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
	// Locale.getDefault());
	// try {
	// int index = 0;
	// for (Vaccination vaccination : mVaccinations) {
	// index++;
	// String reserveTime = vaccination.getReserve_time();// 取得预约接种时间
	// Date vaccineFormatDate = format.parse(reserveTime);// 转化为date类型
	// String todayString = format.format(date);
	// Date today = format.parse(todayString);
	//
	// int result = today.compareTo(vaccineFormatDate);// 与当前时间比较
	// if (TextUtils.isEmpty(vaccination.getFinish_time())
	// && result <= 0) {
	// VaccinationPreferences preferences = new VaccinationPreferences(
	// getActivity());
	// // 只有首次进入主界面才会执行
	// if (TextUtils.isEmpty(preferences.getRemindDate())) {
	// // 将下次接种日期存入preferences
	// preferences
	// .setRemindDate(vaccination.getReserve_time());
	// // 启动服务
	// Intent remindService = new Intent(getActivity(),
	// VaccinationRemindService.class);
	// getActivity().startService(remindService);
	// // 设置提醒
	// preferences.setNotify(true);
	// }
	// mRemindDay = (vaccineFormatDate.getTime() - today.getTime())
	// / (1000 * 60 * 60 * 24);
	// if (mRemindDay == 1) {
	// mNextVaccinationBtn.setText("明天有接种");
	// } else if (mRemindDay == 0) {
	// mNextVaccinationBtn.setText("今天有接种");
	// } else {
	// String remind = String.format(
	// getResources().getString(R.string.distance),
	// mRemindDay);
	// mNextVaccinationBtn.setText(remind);
	// }
	// mCurrent = index - 1;// 下标从0开始，所以-1
	// break;
	// }
	//
	// }
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 计算距离下次接种时间
	 */
//	private void getNextVaccinationTime2() {
//		Date date = new Date();
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
//				Locale.getDefault());
//		try {
//			// int index = 0;
//			int age = 0;
//			boolean found = false;
//			for (int i = 0; i < mListData.size() && !found; i++) {
//				// index++;
//				List<Vaccination> vaccinations = mListData.get(i)
//						.getCategoryItem();
//				age += mListData.get(i).getItemCount();
//				for (Vaccination vaccination : vaccinations) {
//					// index++;
//					String reserveTime = vaccination.getReserve_time();// 取得预约接种时间
//					Date vaccineFormatDate = format.parse(reserveTime);// 转化为date类型
//					String todayString = format.format(date);
//					Date today = format.parse(todayString);
//
//					int result = today.compareTo(vaccineFormatDate);// 与当前时间比较
//
//					if (StringUtils.isNullOrEmpty(vaccination.getFinish_time())
//							&& result <= 0) {
//						VaccinationPreferences preferences = new VaccinationPreferences(
//								getActivity());
//						// 只有首次进入主界面才会执行
//						if (StringUtils.isNullOrEmpty(preferences.getRemindDate())) {
//							// 将下次接种日期存入preferences
//							preferences.setRemindDate(vaccination
//									.getReserve_time());
//							// 启动服务
//							Intent remindService = new Intent(getActivity(),
//									VaccinationRemindService.class);
//							getActivity().startService(remindService);
//							// 设置提醒
//							preferences.setNotify(true);
//						}
//						mRemindDay = (vaccineFormatDate.getTime() - today
//								.getTime()) / (1000 * 60 * 60 * 24);
//						if (mRemindDay == 1) {
//							mNextVaccinationBtn.setText("明天有接种");
//						} else if (mRemindDay == 0) {
//							mNextVaccinationBtn.setText("今天有接种");
//						} else {
//							String remind = String.format(getResources()
//									.getString(R.string.distance), mRemindDay);
//							mNextVaccinationBtn.setText(remind);
//						}
//						// mCurrent = index - 1;// 下标从0开始，所以-1
//						// Log.i(TAG, "i==" + i + "---index=" + index);
//						found = true;
//						age -= mListData.get(i).getItemCount();
//						mMoonAgeLable = age;
//						break;
//					}
//				}
//
//			}
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 天气后台任务
	 * 
	 */
//	private class WeatherTask extends AsyncTask<String, String, Object> {
//
//		@Override
//		protected Object doInBackground(String... params) {
//			Weather weather = null;
//			try {
//				String result = WeatherUtils.getWeatherByCityId(params[0]);
//				if (!StringUtils.isNullOrEmpty(result)) {
//					Gson gson = new Gson();
//					JSONObject jsonObject = new JSONObject(result)
//					.getJSONObject("sk_info");
//					Log.i("MainActivity", jsonObject.toString());
//					weather = gson.fromJson(jsonObject.toString(),
//							new TypeToken<Weather>() {
//						private static final long serialVersionUID = 1L;
//					}.getType());
//				}
//
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//			return weather;
//		}
//		
//		@Override
//		protected void onPostExecute(Object result) {
//			super.onPostExecute(result);
//			if (result != null) {
//				Log.i("MainActivity", ((Weather) result).getCityName());
//				Weather weather = (Weather) result;
//				
//				//mTemp.setText(weather.getTemp());
//				//mWindStrength.setText(weather.getWd() + " " + weather.getWs());
//			}else{
//				PublicMethod.showToast(getActivity(), "获取天气信息失败，亲，网络不给力哦");
//			}
//		}
//	}

	@Override
	public void onResume() {
		Log.i(TAG, "VaccineListFragment...onResume");
		mDefaultBaby = mBabyDao.getDefaultBaby();
		// 用AsyncTask查询天气
		//WeatherTask task = new WeatherTask();
		//task.execute(mDefaultBaby.getCityCode());
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
		// case R.id.vac_list_iv_weather: // 查看未来3天天气
		// intent = new Intent(getActivity(), WeatherActivity.class);
		// intent.putExtra("city", mDefaultBaby.getResidence());
		// startActivity(intent);
		// break;
//		case R.id.vaccine_btn_next_vaccination:
//			mListView.setSelectionFromTop(mMoonAgeLable, 0);// 定位到指定item
//			// mAdapter.setSelectItem(mCurrent);
//			// mAdapter.notifyDataSetInvalidated();
//			break;
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
		// mCursorAdapter.swapCursor(data);
		mVaccinations = new ArrayList<Vaccination>();
		while (data.moveToNext()) {
			Vaccination vaccination = cursorToVaccination(data);
			mVaccinations.add(vaccination);
		}
		// mAdapter = new VaccinationAdapter(getActivity(), mVaccinations);

		mListData = new ArrayList<VaccinationCategory>();
		List<String> moonAgeList = new ArrayList<String>();
		String[] moonAge = Constants.MAIN_MOON_AGE;
		for (int i = 0; i < moonAge.length; i++) {
			moonAgeList.add(moonAge[i]);
		}
		for (String categoryName : moonAgeList) {
			VaccinationCategory category = new VaccinationCategory(categoryName);
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

		// mListView.setAdapter(mAdapter);
		// mAdapter.notifyDataSetChanged();
		//getNextVaccinationTime2();// 距离下次接种时间
		//Log.i(TAG, "mMoonAgePosition=" + mMoonAgeLable);
		//mListView.setSelectionFromTop(mMoonAgeLable, 0);// 定位到指定item
		// Log.i(TAG, "mCurrent=" + mCurrent);
		//mCategoryAdapter.setSelectItem(mMoonAgeLable);
		//mCategoryAdapter.notifyDataSetInvalidated();
		// mAdapter.setSelectItem(mCurrent);
		// mAdapter.notifyDataSetInvalidated();
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

}
