package cn.mointe.vaccination.fragment;

import java.io.File;
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
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.AddTwoTypeVaccineActivity;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.activity.ReserveActivity;
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

import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.sso.UMWXHandler;

public class MainFragment extends Fragment implements OnClickListener,
		ToolTipView.OnToolTipViewClickedListener {

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

	private PopupWindow mPopupWindow;
	private View mParentLayout;
	private View mContentView;
	private ImageButton mOptionView;

	private TextView mVaccineMark;

	private ToolTipRelativeLayout mToolTipFrameLayout;
	private ToolTipView mImgToolTipView;

	private UMSocialService mController;

	private ImageButton mShareBtn;

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

		mToolTipFrameLayout = (ToolTipRelativeLayout) view
				.findViewById(R.id.activity_main_tooltipframelayout);

		mParentLayout = view.findViewById(R.id.main_parent_layout);

		mRemindHint = (TextView) view.findViewById(R.id.main_remind_hint);
		mRemindCount = (TextView) view.findViewById(R.id.main_remind_count);
		mBabyImageView = (CircleImageView) view
				.findViewById(R.id.main_baby_image);
		mBabyName = (TextView) view.findViewById(R.id.main_baby_name);
		mBabyAge = (TextView) view.findViewById(R.id.main_baby_age);

		mVaccineView = (ListView) view.findViewById(R.id.main_vaccine_list);
		mVaccineView.setOnItemClickListener(null);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!mPreferences.getIsTip()) {
					addImgToolTipView();
					mPreferences.setIsTip(true);
				}
			}

		}, 1000);

		mVaccineView.getOnItemClickListener();

		if (android.os.Build.VERSION.SDK_INT >= 9) {
			mVaccineView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		}

		mOptionView = (ImageButton) view.findViewById(R.id.main_vaccine_option);

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

		mContentView = LayoutInflater.from(getActivity()).inflate(
				R.layout.main_popup, null);

		ImageButton addBtn = (ImageButton) mContentView
				.findViewById(R.id.main_pop_add);
		mShareBtn = (ImageButton) mContentView
				.findViewById(R.id.main_pop_share);
		mShareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				share();
			}
		});

		mPopupWindow = new PopupWindow(mContentView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setAnimationStyle(R.style.main_pop_animation);

		/*
		 * mContentView = (View)view.findViewById(R.id.main_pop_share);
		 * mContentView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub share(); } });
		 */

		mContentView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mContentView.findViewById(R.id.main_pop_layout)
						.getTop();

				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						mPopupWindow.dismiss();
						mOptionView.setVisibility(View.VISIBLE);
					}
				}
				return true;
			}

		});

		addBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Toast.makeText(getActivity(), "跳转到添加二类疫苗",
				// Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(getActivity(),
						AddTwoTypeVaccineActivity.class);
				Bundle mBundle = new Bundle();
				mBundle.putSerializable("baby", mDefaultBaby);
				mBundle.putString("reserveDate", mNextDate);
				intent.putExtras(mBundle);
				startActivity(intent);
				mPopupWindow.dismiss();
				mOptionView.setVisibility(View.VISIBLE);
			}
		});

		mOptionView.setOnClickListener(this);

		return view;
	}

	private void addImgToolTipView() {
		mVaccineMark = (TextView) (mVaccineView.getChildAt(mVaccineView
				.getFirstVisiblePosition()).findViewById(1000001));
		mImgToolTipView = mToolTipFrameLayout.showToolTipForView(
				new ToolTip().withText("带有绿色方块标示的是二类疫苗哦~！")
						.withColor(getResources().getColor(R.color.holo_white))
						.withShadow(true), mVaccineMark);
		mImgToolTipView.setOnToolTipViewClickedListener(this);
	}

	public void share() {

		// 需要添加如下变量
		mController = UMServiceFactory.getUMSocialService(
				"cn.mointe.vaccination", RequestType.SOCIAL);

		// 设置分享内容
		mController.setShareContent("好妈妈疫苗");
		QZoneSsoHandler.setTargetUrl("http://sns.whalecloud.com/app/IF5PIl");

		// 分享到QQ
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareImage(new UMImage(getActivity(), new File(
				mDefaultBaby.getImage())));

		qqShareContent.setShareContent("好妈妈疫苗：关爱宝宝健康");

		qqShareContent.setTargetUrl("http://sns.whalecloud.com/app/IF5PIl");
		mController.setShareMedia(qqShareContent);

		// 微信开发平台注册应用的APP
		String appID = "wx7fb17eb502cd4b36";

		// 设置分享图片，参数2为本地图片的绝对路径
		mController.setShareMedia(new UMImage(getActivity(), new File(
				mDefaultBaby.getImage())));

		// 微信图文分享必须设置一个url
		String contentUrl = "http://sns.whalecloud.com/app/IF5PIl";

		// 添加微信平台，参数1为当前的Activity,参数2为用户申请的APPID，参数3为点击分享内容跳转到的目标url
		UMWXHandler wxHandler = mController.getConfig().supportWXPlatform(
				getActivity(), appID, contentUrl);

		wxHandler.setWXTitle("好妈妈疫苗：关爱宝宝健康");

		// 支持微信朋友圈
		UMWXHandler circleHandler = mController.getConfig()
				.supportWXCirclePlatform(getActivity(), appID, contentUrl);
		circleHandler.setCircleTitle("好妈妈疫苗：关爱宝宝健康");

		// 为了避免每次都从服务器获取appid，请设置APP ID
		// 参数1为当前Activity,参数2为APP ID，参数3为用户点击分享内容时跳转到的目标地址
		mController.getConfig().supportQQPlatform(getActivity(), "101080056",
				"http://sns.whalecloud.com/app/IF5PIl");

		// 选择平台
		mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN, SHARE_MEDIA.EMAIL, SHARE_MEDIA.SMS,
				SHARE_MEDIA.SINA);

		mController.openShare(getActivity(), false);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
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
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.main_baby_image) {
			/*
			 * Intent intent = new Intent(Intent.ACTION_SEND);
			 * intent.setType("image/*"); intent.putExtra(Intent.EXTRA_SUBJECT,
			 * "分享"); intent.putExtra(Intent.EXTRA_TEXT, "终于可以了!!!");
			 * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 * startActivity(Intent .createChooser(intent,
			 * getActivity().getTitle()));
			 */

			Intent intent = new Intent(getActivity(),
					RegisterBabyActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("baby", mDefaultBaby);
			intent.putExtras(mBundle);
			startActivity(intent);
		} else if (v.getId() == R.id.main_vaccine_option) {
			mOptionView.setVisibility(View.GONE);
			mPopupWindow.showAtLocation(mParentLayout, Gravity.BOTTOM, 0, 0);
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
				setBabyInfo();
				// mVaccineLoaderManager.restartLoader(101, null,
				// mVaccineLoaderCallBacks);
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
			// try {
			// Log.i("MainActivity", "查询下次接种时间");
			// Log.i("MainActivity",data.moveToFirst()+"");
			if (!data.moveToFirst()) {
				// Toast.makeText(getActivity(), "上次完成接种后没有预约下次接种",
				// Toast.LENGTH_SHORT).show();
				showReservationDialog();
			} else {
				String reserveTime = data
						.getString(data
								.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME));
				mNextDate = reserveTime;
				mVaccineLoaderManager.restartLoader(101, null,
						mVaccineLoaderCallBacks);

				// while (data.moveToNext()) {
				//
				// Log.i("MainActivity", "预约时间："+reserveTime);
				// int result = DateUtils.compareDateToToday(reserveTime);
				// if (result >= 0) {
				// mNextDate = reserveTime;
				// mVaccineLoaderManager.restartLoader(101, null,
				// mVaccineLoaderCallBacks);
				// break;
				// }
				// }
			}
			// } catch (ParseException e) {
			// e.printStackTrace();
			// }
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
			// String nextDate = findNextDate();
			String nextDate = mNextDate;
			Log.i("MainActivity", "---" + nextDate);
			CursorLoader loader = null;
			if (null != nextDate) {
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

	private void showReservationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setIcon(R.drawable.app_icon);
		builder.setTitle(R.string.hint);
		builder.setMessage("已完成全部预约接种，是否预约下次接种？");
		builder.setNegativeButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO 跳转到预约接种
						startActivity(new Intent(getActivity(),
								ReserveActivity.class));
					}
				});
		builder.setPositiveButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mRemindHint.setText("未预约下次接种");
					}
				});
		builder.create().show();
	}

	@Override
	public void onToolTipViewClicked(ToolTipView toolTipView) {
		// TODO Auto-generated method stub
		if (mImgToolTipView == toolTipView) {
			mImgToolTipView = null;
		}
	}
}
