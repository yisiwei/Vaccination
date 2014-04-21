package cn.mointe.vaccination.fragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

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
import cn.mointe.vaccination.adapter.SummaryVaccinationAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.Weather;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.tools.WeatherUtils;
import cn.mointe.vaccination.view.CircleImageView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

/**
 * 首页
 * 
 */
public class SummaryFragment extends Fragment implements OnClickListener,
		LoaderCallbacks<Cursor> {

	private static final String TAG = "MainActivity";

	private BabyDao mBabyDao;
	private CircleImageView mBabyImg;// 宝宝头像
	private TextView mBabyNickName; // baby昵称
	private Baby mDefaultBaby; // 默认baby

	private TextView mWeaCityName;
	private TextView mWeaTemp;
	private TextView mWeaWs;

	// private static final int BABY_CURSOR_ID = 100;
	// private static final int VACCINE_CURSOR_ID = 200;

	private ListView mListView;
	private List<Vaccination> mVaccinations;// 下次要接种疫苗
	private SummaryVaccinationAdapter mAdapter;

	private long mRemindDay;// 距离下次接种天数
	private TextView mRemindText;// XX天后接种

	private LoaderManager mManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBabyDao = new BabyDao(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_summary, null);

		// 初始化控件
		mRemindText = (TextView) view.findViewById(R.id.summary_remind);
		mListView = (ListView) view.findViewById(R.id.summary_list);
		mBabyImg = (CircleImageView) view.findViewById(R.id.summary_baby_image);

		mWeaCityName = (TextView) view.findViewById(R.id.summary_wea_cityname);
		mWeaTemp = (TextView) view.findViewById(R.id.summary_wea_temp);
		mWeaWs = (TextView) view.findViewById(R.id.summary_wea_wind_strength);

		mBabyNickName = (TextView) view
				.findViewById(R.id.summary_baby_nickname);

		mBabyImg.setOnClickListener(this);// 宝宝头像点击监听

		// listView Item 点击监听
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						VaccinationDetailActivity.class);
				Vaccination vaccination = (Vaccination) mVaccinations
						.get(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Vaccination", vaccination);
				intent.putExtra("birthdate", mDefaultBaby.getBirthdate());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		WeatherTask mTask = new WeatherTask();
		mTask.execute("101010100");

		mManager = getLoaderManager();
		// mManager.initLoader(BABY_CURSOR_ID, null, this);

		return view;
	}

	public void onResume() {
		super.onResume();
		// XXX
		Log.i(TAG, "SummaryFragment...onResume");
		mDefaultBaby = mBabyDao.getDefaultBaby();
		setBabyInfo();// 设置宝宝相关信息
		mManager.restartLoader(1001, null, this);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.summary_baby_image) {
			Intent intent = new Intent(getActivity(),
					RegisterBabyActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("baby", mDefaultBaby);
			intent.putExtras(mBundle);
			startActivityForResult(intent, 1000);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == 1000) {
		// if (resultCode == Activity.RESULT_OK) {
		// Log.i(TAG, "restartLoader--Baby");
		// //mManager.restartLoader(BABY_CURSOR_ID, null, this);
		// }
		// }
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Log.i(TAG, "id==" + id);
		CursorLoader loader = null;
		// if (id == BABY_CURSOR_ID) {
		// loader = new CursorLoader(getActivity());
		// loader.setUri(BabyProvider.CONTENT_URI);
		// loader.setSelection(DBHelper.BABY_COLUMN_IS_DEFAULT + "=?");
		// loader.setSelectionArgs(new String[] { "1" });
		// } else if (id == VACCINE_CURSOR_ID) {
		loader = new CursorLoader(getActivity());
		loader.setUri(VaccinationProvider.CONTENT_URI);
		String selection = DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
				+ "=? and " + DBHelper.VACCINATION_COLUMN_RESERVE_TIME + ">=?";
		loader.setSelection(selection);
		loader.setSelectionArgs(new String[] { mDefaultBaby.getName(),
				DateUtils.getCurrentFormatDate() });
		loader.setSortOrder(DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
		// }
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int id = loader.getId();
		Log.i(TAG, "finished--id:" + id);
		// if (id == BABY_CURSOR_ID) {
		// if (data.moveToFirst()) {
		// mDefaultBaby = mBabyDao.cursorToBaby(data);
		// setBabyInfo();
		// mManager.initLoader(VACCINE_CURSOR_ID, null, this);
		// }
		// } else if (id == VACCINE_CURSOR_ID) {
		mVaccinations = new ArrayList<Vaccination>();
		List<Vaccination> list = new ArrayList<Vaccination>();
		while (data.moveToNext()) {
			Vaccination vaccination = VaccineListFragment
					.cursorToVaccination(data);
			if (StringUtils.isNullOrEmpty(vaccination.getFinish_time())) {
				list.add(vaccination);
			}
		}
		if (list.size() > 0) {
			String reseveTime = list.get(0).getReserve_time();
			Log.i(TAG, "reseveTime=" + reseveTime);
			for (Vaccination vac : list) {
				if (vac.getReserve_time().equals(reseveTime)) {
					mVaccinations.add(vac);
				}
			}
			mAdapter = new SummaryVaccinationAdapter(getActivity(),
					mVaccinations);
			mListView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();

			try {
				Date remindDate = DateUtils.stringToDate(reseveTime);
				Date today = DateUtils.formatDate(new Date());
				mRemindDay = (remindDate.getTime() - today.getTime())
						/ (1000 * 60 * 60 * 24);
				String remind = String.format(
						getResources().getString(R.string.remind), mRemindDay);

				if (mRemindDay == 1) {
					mRemindText.setText("明天有接种");
				} else if (mRemindDay == 0) {
					mRemindText.setText("今天有接种");
				} else {
					mRemindText.setText(remind);
				}

			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			mRemindText.setText("已完成全部接种");
		}
		// }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {

	}

	/**
	 * 设置宝宝相关信息
	 */
	private void setBabyInfo() {
		if (mDefaultBaby != null) {
			String babyMoonAge = null;
			String birthdate = mDefaultBaby.getBirthdate();
			try {
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
						Locale.getDefault());
				String dateString = format.format(date);
				Date today = format.parse(dateString);
				long monthNumber = DateUtils.getMonth(birthdate, today);
				if (monthNumber == 0) {
					babyMoonAge = "未满月";
				} else if (monthNumber < 12) {
					babyMoonAge = monthNumber + "月龄";
				} else if (monthNumber == 12) {
					babyMoonAge = "1周岁";
				} else if (monthNumber > 12 && monthNumber < 24) {
					babyMoonAge = monthNumber + "月龄";
				} else if (monthNumber >= 24 && monthNumber < 36) {
					babyMoonAge = "2周岁";
				} else if (monthNumber >= 36 && monthNumber < 48) {
					babyMoonAge = "3周岁";
				} else if (monthNumber >= 48 && monthNumber < 60) {
					babyMoonAge = "4周岁";
				} else if (monthNumber >= 60 && monthNumber < 72) {
					babyMoonAge = "5周岁";
				} else if (monthNumber >= 72) {
					babyMoonAge = "6周岁";
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			mBabyNickName.setText(mDefaultBaby.getName() + "(" + babyMoonAge
					+ ")");

			String imgUri = mDefaultBaby.getImage();
			if (!StringUtils.isNullOrEmpty(imgUri)) {
				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri,
						100, 100);
				mBabyImg.setImageBitmap(bitmap);
			} else {
				mBabyImg.setImageResource(R.drawable.default_img);
			}
		}

	}

	private class WeatherTask extends AsyncTask<String, Void, Object> {

		@Override
		protected Object doInBackground(String... params) {
			Weather weather = null;
			try {
				String result = WeatherUtils.getWeatherByCityId(params[0]);

				Gson gson = new Gson();
				JSONObject jsonObject = new JSONObject(result)
						.getJSONObject("sk_info");
				Log.i("MainActivity", jsonObject.toString());
				weather = gson.fromJson(jsonObject.toString(),
						new TypeToken<Weather>() {
							private static final long serialVersionUID = 1L;
						}.getType());

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return weather;
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			Log.i("MainActivity", ((Weather) result).getCityName());
			Weather weather = (Weather) result;
			mWeaCityName.setText(weather.getCityName());
			mWeaTemp.setText(weather.getTemp());
			mWeaWs.setText(weather.getWd() + " " + weather.getWs());
		}
	}
}
