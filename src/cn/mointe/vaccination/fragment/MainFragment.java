package cn.mointe.vaccination.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.activity.VaccinationDetailActivity;
import cn.mointe.vaccination.adapter.MainVaccinationCursorAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.provider.BabyProvider;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PackageUtil;
import cn.mointe.vaccination.tools.PublicMethod;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.CircleImageView;

public class MainFragment extends Fragment implements OnClickListener {

	private TextView mRemindHint;// 提醒提示文字 距离下次接种还有？

	private TextView mRemindCount;
	private long mRemindDay;
	private CircleImageView mBabyImageView;
	private TextView mBabyName;
	private TextView mBabyAge;

	// private ActionSlideExpandableListView mVaccineView;
	private ListView mVaccineView;
	// private SimpleCursorAdapter mVaccineCursorAdapter;
	private MainVaccinationCursorAdapter mVaccineCursorAdapter;

	private Baby mDefaultBaby;
	private BabyDao mBabyDao;
	private VaccinationDao mVaccinationDao;
	private VaccinationPreferences mPreferences;

	private LoaderManager mBabyLoaderManager;
	private LoaderManager mVaccineLoaderManager;

	private String mSetReserveDate;

	// private PopupWindow mPopupWindow;
	// private View mParentLayout;
	// private View mContentView;
	// private ImageButton mOptionView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBabyDao = new BabyDao(getActivity());
		mVaccinationDao = new VaccinationDao(getActivity());
		mBabyLoaderManager = getLoaderManager();
		mVaccineLoaderManager = getLoaderManager();
		mPreferences = new VaccinationPreferences(getActivity());
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_main, null);

		// mParentLayout = view.findViewById(R.id.main_parent_layout);

		mRemindHint = (TextView) view.findViewById(R.id.main_remind_hint);
		mRemindCount = (TextView) view.findViewById(R.id.main_remind_count);
		mBabyImageView = (CircleImageView) view
				.findViewById(R.id.main_baby_image);
		mBabyName = (TextView) view.findViewById(R.id.main_baby_name);
		mBabyAge = (TextView) view.findViewById(R.id.main_baby_age);

		// mVaccineView = (ActionSlideExpandableListView) view
		// .findViewById(R.id.main_vaccine_list);
		mVaccineView = (ListView) view.findViewById(R.id.main_vaccine_list);

		if (android.os.Build.VERSION.SDK_INT >= 9) {
			mVaccineView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}

		// mOptionView = (ImageButton)
		// view.findViewById(R.id.main_vaccine_option);

		// mVaccineCursorAdapter = new SimpleCursorAdapter(getActivity(),
		// R.layout.main_vaccine_item, null, new String[] {
		// DBHelper.VACCINATION_COLUMN_VACCINE_NAME,
		// DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER }, new int[] {
		// R.id.main_vaccine_item_name,
		// R.id.main_vaccine_item_number }, 0);
		// mVaccineCursorAdapter = new SimpleCursorAdapter(getActivity(),
		// R.layout.main_vaccine_item_old, null, new String[] {
		// DBHelper.VACCINATION_COLUMN_VACCINE_NAME,
		// DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER },
		// new int[] { R.id.main_vaccine_item_name,
		// R.id.main_vaccine_item_number }, 0);
		mVaccineCursorAdapter = new MainVaccinationCursorAdapter(getActivity(),
				null, true);

		mVaccineView.setAdapter(mVaccineCursorAdapter);
		mVaccineCursorAdapter.notifyDataSetChanged();
		// mVaccineView.setItemActionListener(
		// new ActionSlideExpandableListView.OnActionClickListener() {
		//
		// @Override
		// public void onClick(View listView, View buttonview,
		// int position) {
		// Cursor cursor = (Cursor) mVaccineCursorAdapter
		// .getItem(position);
		// Vaccination vaccination = mVaccinationDao
		// .cursorToVaccination(cursor);
		// if (buttonview.getId() == R.id.buttonA) {// 预约
		// setReserveDate(vaccination);
		// } else if (buttonview.getId() == R.id.buttonB) {// 完成
		// vaccination.setFinish_time(DateUtils
		// .getCurrentFormatDate());
		// mVaccinationDao.updateFinishTimeById(vaccination);
		//
		// // 查询下次提醒日期
		// Log.i("MainActivity", "nextDate:" + findNextDate());
		// // 将下次提醒日期存到Preferences
		// mPreferences.setRemindDate(findNextDate());
		// // 如果服务正在运行，重启服务
		// if (PackageUtil.isServiceRunning(getActivity(),
		// Constants.REMIND_SERVICE)) {
		// getActivity()
		// .stopService(
		// new Intent(
		// getActivity(),
		// VaccinationRemindService.class));
		// }
		// getActivity().startService(
		// new Intent(getActivity(),
		// VaccinationRemindService.class));
		//
		// mVaccineLoaderManager.restartLoader(101, null,
		// mVaccineLoaderCallBacks);
		// } else {// 详情
		// Intent intent = new Intent(getActivity(),
		// VaccinationDetailActivity.class);
		// Bundle bundle = new Bundle();
		// bundle.putSerializable("Vaccination", vaccination);
		// intent.putExtra("birthdate",
		// mDefaultBaby.getBirthdate());
		// intent.putExtras(bundle);
		// startActivity(intent);
		// }
		// }
		//
		// }, R.id.buttonA, R.id.buttonB, R.id.buttonC);

		mVaccineView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Cursor cursor = (Cursor) mVaccineCursorAdapter
						.getItem(position);
				Vaccination vaccination = mVaccinationDao
						.cursorToVaccination(cursor);
				Intent intent = new Intent(getActivity(),
						VaccinationDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Vaccination", vaccination);
				intent.putExtra("birthdate", mDefaultBaby.getBirthdate());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		mBabyLoaderManager.initLoader(100, null, mBabyLoaderCallBacks);
		mBabyImageView.setOnClickListener(this);

		// mContentView =
		// LayoutInflater.from(getActivity()).inflate(R.layout.main_popup,
		// null);
		// mPopupWindow = new PopupWindow(mContentView,
		// ViewGroup.LayoutParams.MATCH_PARENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT);
		// mPopupWindow.setFocusable(true);
		// mPopupWindow.setAnimationStyle(R.style.main_pop_animation);
		// mContentView.setOnTouchListener(new OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// int height =
		// mContentView.findViewById(R.id.main_pop_layout).getTop();
		// int y = (int) event.getY();
		// if (event.getAction() == MotionEvent.ACTION_UP) {
		// if (y < height) {
		// mPopupWindow.dismiss();
		// mOptionView.setVisibility(View.VISIBLE);
		// }
		// }
		// return true;
		// }
		//
		// });
		//
		// mOptionView.setOnClickListener(this);

		return view;
	}

	/**
	 * 设置预约时间
	 * 
	 * @param vac
	 */
	@SuppressWarnings("unused")
	private void setReserveDate(final Vaccination vac) {

		Calendar calendar = Calendar.getInstance();

		try {
			Date date = DateUtils.stringToDate(vac.getReserve_time());
			calendar.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		final int year = calendar.get(Calendar.YEAR);
		final int monthOfYear = calendar.get(Calendar.MONDAY);
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.set_vaccination_datetime);// 提示：设置预约时间

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.set_date, null);
		DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
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

						mSetReserveDate = stringBuilder.toString();
						Log.i(Constants.TAG, "应接种时间=" + mSetReserveDate);
					}
				});
		builder.setView(view);
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 如果改变了应接种时间
						if (mSetReserveDate != null) {
							int result = 0;
							try {
								result = DateUtils
										.compareDateToToday(mSetReserveDate);
							} catch (ParseException e) {
								e.printStackTrace();
							}
							if (result == 1 || result == 0) {
								// 修改接种时间
								mVaccinationDao.updateReserveTimeById(
										vac.getId(), mSetReserveDate);
								// 查询下次接种时间
								mPreferences.setRemindDate(findNextDate());
								Intent remindService = new Intent(
										getActivity(),
										VaccinationRemindService.class);
								// 如果服务在运行，重新启动
								if (PackageUtil.isServiceRunning(getActivity(),
										Constants.REMIND_SERVICE)) {
									getActivity().stopService(remindService);
								}
								getActivity().startService(remindService);// 启动服务
								mVaccineLoaderManager.restartLoader(101, null,
										mVaccineLoaderCallBacks);
								// String today =
								// DateUtils.getCurrentFormatDate();
								// if (mSetReserveDate.equals(today)) {
								// Intent intent = new Intent(getActivity(),
								// MainActivity.class);
								// getActivity().startActivity(intent);
								// getActivity().finish();
								// }
							} else {
								// 预约时间不能小于今天
								PublicMethod
										.showToast(
												getActivity(),
												R.string.vaccination_datetime_is_not_less_than_today);
							}

						}
					}

				});
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create();
		builder.show();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.main_baby_image) {
			// Intent intent = new Intent(Intent.ACTION_SEND);
			// intent.setType("image/*");
			// intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
			// intent.putExtra(Intent.EXTRA_TEXT, "终于可以了!!!");
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// startActivity(Intent
			// .createChooser(intent, getActivity().getTitle()));

			Intent intent = new Intent(getActivity(),
					RegisterBabyActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("baby", mDefaultBaby);
			intent.putExtras(mBundle);
			startActivity(intent);
		}
		// else if(v.getId() == R.id.main_vaccine_option){
		// mOptionView.setVisibility(View.GONE);
		// mPopupWindow.showAtLocation(mParentLayout, Gravity.BOTTOM, 0, 0);
		// }
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
//				mVaccineLoaderManager.restartLoader(101, null,
//						mVaccineLoaderCallBacks);
				mVaccineLoaderManager.restartLoader(102, null,
						mFindNextDateCallBacks);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

	/**
	 * 设置宝宝相关信息
	 */
	private void setBabyInfo() {
		mBabyName.setText(mDefaultBaby.getName());
		String imgUri = mDefaultBaby.getImage();
		if (!StringUtils.isNullOrEmpty(imgUri)) {
			Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri, 100,
					100);
			mBabyImageView.setImageBitmap(bitmap);
		} else {
			mBabyImageView.setImageResource(R.drawable.default_img);
		}
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
		mBabyAge.setText(babyMoonAge);
	}

	/**
	 * 查询下次接种日期
	 * 
	 * @return
	 */
	private String findNextDate() {
		String nextDate = null;
		try {
			nextDate = mVaccinationDao.findNextDate(mDefaultBaby.getName());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return nextDate;
	}

	private String mNextDate;
	private LoaderCallbacks<Cursor> mFindNextDateCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			try {
				while (data.moveToNext()) {
					String reserveTime = data
							.getString(data
									.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
					String finishTime = data
							.getString(data
									.getColumnIndex(DBHelper.VACCINATION_COLUMN_FINISH_TIME));
					int result = DateUtils.compareDateToToday(reserveTime);
					if (StringUtils.isNullOrEmpty(finishTime) && result >= 0) {
						mNextDate = reserveTime;
						mVaccineLoaderManager.restartLoader(101, null,
								mVaccineLoaderCallBacks);
						break;
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(getActivity());
			loader.setUri(VaccinationProvider.CONTENT_URI);
			loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
					+ "=?");
			loader.setSelectionArgs(new String[] { mDefaultBaby.getName() });
			return loader;
		}
	};

	/**
	 * 查询下次接种疫苗
	 */
	private LoaderCallbacks<Cursor> mVaccineLoaderCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//			String nextDate = findNextDate();
			String nextDate = mNextDate;
			Log.i("MainActivity", "---" + nextDate);
			CursorLoader loader = null;
			if (null != nextDate) {

				VaccinationPreferences preferences = new VaccinationPreferences(
						getActivity());
				// 只有首次进入主界面才会执行
				if (StringUtils.isNullOrEmpty(preferences.getRemindDate())) {
					// 将下次接种日期存入preferences
					preferences.setRemindDate(nextDate);
					// 启动服务
					Intent remindService = new Intent(getActivity(),
							VaccinationRemindService.class);
					getActivity().startService(remindService);
					// 设置提醒
					preferences.setNotify(true);
				}

				loader = new CursorLoader(getActivity());
				loader.setUri(VaccinationProvider.CONTENT_URI);
				String selection = DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
						+ "=? and " + DBHelper.VACCINATION_COLUMN_FINISH_TIME
						+ " is null and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + ">=? and "
						+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "<=?";
				loader.setSelection(selection);
				loader.setSelectionArgs(new String[] { mDefaultBaby.getName(),
						DateUtils.getCurrentFormatDate(), nextDate });
				loader.setSortOrder(DBHelper.VACCINATION_COLUMN_RESERVE_TIME);
			}
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mVaccineCursorAdapter.swapCursor(data);
			if (data.moveToFirst()) {
				Vaccination vac = mVaccinationDao.cursorToVaccination(data);
				String reseveTime = vac.getReserve_time();
				Log.i("MainActivity", "reseveTime=" + reseveTime);
				try {
					Date remindDate = DateUtils.stringToDate(reseveTime);
					Date today = DateUtils.formatDate(new Date());
					mRemindDay = (remindDate.getTime() - today.getTime())
							/ (1000 * 60 * 60 * 24);

					// if (mRemindDay == 0) {
					// mRemindCount.setText("今天有接种");
					// // TODO 接种当日显示的界面
					// } else {
					mRemindCount.setText(String.valueOf(mRemindDay));

					String reseveDate = String
							.format(getResources().getString(
									R.string.main_remind_hint), reseveTime);
					mRemindHint.setText(reseveDate);
					// }

				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				// TODO 完成全部接种后界面显示
				// mRemindCount.setText("已完成全部接种");
			}
			mVaccineCursorAdapter.notifyDataSetChanged();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			mVaccineCursorAdapter.swapCursor(null);
		}

	};
}
