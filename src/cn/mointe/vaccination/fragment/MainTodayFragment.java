package cn.mointe.vaccination.fragment;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.AddDiaryActivity;
import cn.mointe.vaccination.activity.AddTwoTypeVaccineActivity;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.activity.ReservationCalendarActivity;
import cn.mointe.vaccination.adapter.VaccinationInfoAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.VaccinationInfo;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.domain.Weather;
import cn.mointe.vaccination.provider.BabyProvider;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PublicMethod;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.tools.WeatherUtils;
import cn.mointe.vaccination.view.CircleImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainTodayFragment extends Fragment {

	private ListView mVaccineView;
	private List<VaccinationInfo> mVaccinationInfos;
	private VaccinationInfoAdapter mVaccinatioInfoAdapter;

	private TextView mVaccinationDateView;
	private CircleImageView mBabyImageView;

	private Baby mDefaultBaby;
	private BabyDao mBabyDao;
	private VaccineDao mVaccineDao;
	private VaccinationDao mVaccinationDao;

	private LoaderManager mBabyLoaderManager;
	private LoaderManager mVaccineLoaderManager;

	private PopupWindow mPopupWindow;
	private View mParentLayout;
	private View mContentView;
	private ImageButton mOptionView;

	private TextView mWeather;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBabyDao = new BabyDao(getActivity());
		mVaccineDao = new VaccineDao(getActivity());
		mVaccinationDao = new VaccinationDao(getActivity());
		mBabyLoaderManager = getLoaderManager();
		mVaccineLoaderManager = getLoaderManager();
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_main_today, null);

		mWeather = (TextView) view
				.findViewById(R.id.main_today_vaccine_weather);
		mVaccinationDateView = (TextView) view
				.findViewById(R.id.main_today_vaccination_date);
		mVaccineView = (ListView) view
				.findViewById(R.id.main_today_vaccine_list);
		mBabyImageView = (CircleImageView) view
				.findViewById(R.id.main_today_baby_image);

		mParentLayout = view.findViewById(R.id.main_parent_layout);

		mOptionView = (ImageButton) view
				.findViewById(R.id.main_today_option_menu);

		if (android.os.Build.VERSION.SDK_INT >= 9) {
			mVaccineView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}

		mBabyLoaderManager.initLoader(100, null, mBabyLoaderCallBacks);

		mContentView = LayoutInflater.from(getActivity()).inflate(
				R.layout.pop_main_foot_option, null);
		ImageButton addType2 = (ImageButton) mContentView
				.findViewById(R.id.main_option_1);
		ImageButton addDiaryBtn = (ImageButton)mContentView.findViewById(R.id.main_option_2);
		
		mPopupWindow = new PopupWindow(mContentView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setAnimationStyle(R.style.main_pop_animation);
		mContentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// int height = mContentView.findViewById(R.id.main_pop_layout)
				// .getTop();
				// int y = (int) event.getY();
				// if (event.getAction() == MotionEvent.ACTION_UP) {
				// if (y < height) {
				// mPopupWindow.dismiss();
				// mOptionView.setVisibility(View.VISIBLE);
				// }
				// }
				mPopupWindow.dismiss();
				return true;
			}

		});

		mOptionView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				} else {
					mPopupWindow.showAtLocation(mParentLayout, Gravity.BOTTOM,
							0, 0);
				}
			}
		});

		mBabyImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						RegisterBabyActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("baby", mDefaultBaby);
				intent.putExtras(mBundle);
				startActivity(intent);
			}
		});
		
		addType2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						AddTwoTypeVaccineActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("baby", mDefaultBaby);
				mBundle.putString("reserveDate", DateUtils.getCurrentFormatDate());
				intent.putExtras(mBundle);
				startActivity(intent);
				mPopupWindow.dismiss();
			}
		});
		
		addDiaryBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), AddDiaryActivity.class));
				mPopupWindow.dismiss();
			}
		});

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * 天气后台任务
	 * 
	 */
	private class WeatherTask extends AsyncTask<String, String, Object> {

		@Override
		protected Object doInBackground(String... params) {
			Weather weather = null;
			try {
				String result = WeatherUtils.getWeatherByCityId(params[0]);
				if (!StringUtils.isNullOrEmpty(result)) {
					Gson gson = new Gson();
					JSONObject jsonObject = new JSONObject(result)
							.getJSONObject("sk_info");
					Log.i("MainActivity", jsonObject.toString());
					weather = gson.fromJson(jsonObject.toString(),
							new TypeToken<Weather>() {
							}.getType());
				}

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
			if (result != null) {
				Log.i("MainActivity", ((Weather) result).getCityName());
				Weather weather = (Weather) result;
				mWeather.setText(weather.getTemp() + "  " + weather.getWd()
						+ " " + weather.getWs());
			} else {
				PublicMethod.showToast(getActivity(), "获取天气信息失败，亲，网络不给力哦");
			}
		}
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
				String imgUri = mDefaultBaby.getImage();
				if (!StringUtils.isNullOrEmpty(imgUri)) {
					Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(
							imgUri, 100, 100);
					mBabyImageView.setImageBitmap(bitmap);
				} else {
					mBabyImageView.setImageResource(R.drawable.default_img);
				}
				mVaccineLoaderManager.restartLoader(101, null,
						mVaccineLoaderCallBacks);
				// mVaccineLoaderManager.restartLoader(102, null,
				// mFindNextDateCallBacks);
				new WeatherTask().execute(mDefaultBaby.getCityCode());
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

	/**
	 * 查询下次接种日期
	 * 
	 */
	private String mNextDate;
	private LoaderCallbacks<Cursor> mFindNextDateCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(getActivity());
			loader.setUri(VaccinationProvider.CONTENT_URI);
			loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
					+ "=? and " + DBHelper.VACCINATION_COLUMN_FINISH_TIME
					+ " is null and "
					+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + " is not null");
			loader.setSelectionArgs(new String[] { mDefaultBaby.getName() });
			loader.setSortOrder(DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (!data.moveToFirst()) {
				showReservationDialog();
			} else {
				String reserveTime = data
						.getString(data
								.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
				int result = 0;
				try {
					result = DateUtils.compareDateToToday(reserveTime);
					if (result >= 0) {
						Log.i("MainActivity", "预约时间1：" + reserveTime);
						mNextDate = reserveTime;
						mVaccineLoaderManager.restartLoader(101, null,
								mVaccineLoaderCallBacks);
					} else {
						while (data.moveToNext()) {
							reserveTime = data
									.getString(data
											.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
							Log.i("MainActivity", "预约时间2：" + reserveTime);
							result = DateUtils.compareDateToToday(reserveTime);
							if (result >= 0) {
								mNextDate = reserveTime;
								mVaccineLoaderManager.restartLoader(101, null,
										mVaccineLoaderCallBacks);
								break;
							}
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}

	};

	/**
	 * 查询下次接种疫苗
	 */
	private LoaderCallbacks<Cursor> mVaccineLoaderCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// String nextDate = mNextDate;
			String nextDate = DateUtils.getCurrentFormatDate();
			Log.i("MainActivity", "---" + nextDate);
			CursorLoader loader = null;
			if (null != nextDate) {
				mVaccinationDateView.setText(nextDate);
				loader = new CursorLoader(getActivity());
				loader.setUri(VaccinationProvider.CONTENT_URI);
				String selection = DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
						+ "=? and " + DBHelper.VACCINATION_COLUMN_RESERVE_TIME
						+ "=?";
				loader.setSelection(selection);
				loader.setSelectionArgs(new String[] { mDefaultBaby.getName(),
						nextDate });
				loader.setSortOrder(DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
			}
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// mVaccineCursorAdapter.swapCursor(data);
			mVaccinationInfos = new ArrayList<VaccinationInfo>();
			while (data.moveToNext()) {
				try {
					Vaccination vaccination = mVaccinationDao
							.cursorToVaccination(data);
					Vaccine vaccine = mVaccineDao.getVaccineByName(vaccination
							.getVaccine_name());
					VaccinationInfo info = new VaccinationInfo();
					info.setVacid(vaccination.getId());
					info.setName(vaccination.getVaccine_name());
					info.setDate(vaccination.getReserve_time());
					info.setBefore(vaccine.getContraindication());// 禁忌
					info.setAfter(vaccine.getAdverse_reaction());// 不良反应
					info.setFinishDate(vaccination.getFinish_time());
					info.setBabyName(vaccination.getBaby_nickname());
					mVaccinationInfos.add(info);
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			mVaccinatioInfoAdapter = new VaccinationInfoAdapter(getActivity(),
					mVaccinationInfos);
			mVaccineView.setAdapter(mVaccinatioInfoAdapter);
			mVaccinatioInfoAdapter.notifyDataSetChanged();
			
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// mVaccineCursorAdapter.swapCursor(null);
		}

	};

	private void showReservationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.app_icon);
		builder.setTitle(R.string.hint);
		builder.setMessage("是否预约下次接种？");
		builder.setNegativeButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 跳转到预约接种
						// startActivity(new Intent(getActivity(),
						// ReserveActivity.class));
						startActivity(new Intent(getActivity(),
								ReservationCalendarActivity.class));
					}
				});
		builder.setPositiveButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}

}
