package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.ReservationAdapter;
import cn.mointe.vaccination.adapter.ReservationAdapter.OnSelectedItemChanged;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.service.Remind;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

import com.umeng.analytics.MobclickAgent;

public class ReservationVaccineActivity extends FragmentActivity {

	private TextView mTitleText;
//	private ImageButton mTitleLeftImgbtn;// title左边图标
	private LinearLayout mTitleLeft;
	private ImageView mTitleRightImgbtn;// title右边图标

	private ViewPager mViewPager;// 页卡内容
	private List<View> mViews;// Tab页面列表

	private ListView mListViewType1;
	private ListView mListViewType2;

	private List<Vaccination> mType1Vaccinations;
	private List<Vaccination> mType2Vaccinations;

	private ReservationAdapter mAdapter1;
	private ReservationAdapter mAdapter2;

	private LoaderManager mLoaderType1Manager;
	private LoaderManager mLoaderType2Manager;

	private TextView mVaccineType1;
	private TextView mVaccineType2;

	private VaccinationDao mVaccinationDao;
	private BabyDao mBabyDao;

	private Baby mDefaultBaby;
	private String mDate;
	private String mNoFormatDate;

	private VaccinationPreferences mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation_vaccine);

		mBabyDao = new BabyDao(this);
		mVaccinationDao = new VaccinationDao(this);
		mPreferences = new VaccinationPreferences(this);

		mLoaderType1Manager = getSupportLoaderManager();
		mLoaderType2Manager = getSupportLoaderManager();

		// 初始化控件
		mTitleText = (TextView) this.findViewById(R.id.title_text);
//		mTitleLeftImgbtn = (ImageButton) this
//				.findViewById(R.id.title_left_imgbtn);
		mTitleLeft = (LinearLayout) this.findViewById(R.id.title_left);
		mTitleRightImgbtn = (ImageView) this
				.findViewById(R.id.title_right_imgbtn);
		// mListView = (ListView)
		// this.findViewById(R.id.reservation_vaccine_list);

		mVaccineType1 = (TextView) this.findViewById(R.id.vaccine_type1);
		mVaccineType2 = (TextView) this.findViewById(R.id.vaccine_type2);

		mDate = getIntent().getStringExtra("date");
		mNoFormatDate = getIntent().getStringExtra("noFormatDate");
		Log.i("MainActivity", "日期：" + mDate + "--" + mNoFormatDate);

		// 设置title
		mTitleText.setText(getResources().getString(
				R.string.reserve_vaccination));
//		// 设置icon
//		mTitleLeftImgbtn.setBackgroundResource(R.drawable.actionbar_icon);
		// 设置点击监听事件
		mTitleLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("date", mNoFormatDate);
				setResult(RESULT_OK, intent);
				ReservationVaccineActivity.this.finish();
			}
		});
		// 隐藏右边图标
		mTitleRightImgbtn.setVisibility(View.GONE);

		mDefaultBaby = mBabyDao.getDefaultBaby();

		mViewPager = (ViewPager) this
				.findViewById(R.id.reservation_vaccine_viewPager);
		mViews = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		View view1 = inflater.inflate(
				R.layout.fragment_reservation_vaccine_type1, null);
		mListViewType1 = (ListView) view1.findViewById(R.id.vaccine_type1_list);

		View view2 = inflater.inflate(
				R.layout.fragment_reservation_vaccine_type2, null);
		mListViewType2 = (ListView) view2.findViewById(R.id.vaccine_type2_list);

		mViews.add(view1);
		mViews.add(view2);

		mViewPager.setAdapter(new MyPagerAdapter(mViews));
		mViewPager.setCurrentItem(0);// 默认显示一类
		mViewPager.setOnPageChangeListener(new MyPageChangeListener());
		mVaccineType1.setBackgroundResource(R.drawable.tab_type);
		mVaccineType2.setBackgroundResource(R.drawable.tab_type_normal);

		mVaccineType1.setOnClickListener(new MyOnClickListener(0));
		mVaccineType2.setOnClickListener(new MyOnClickListener(1));

		mLoaderType1Manager.initLoader(200, null, mType1LoaderCallBacks);
		mLoaderType2Manager.initLoader(300, null, mType2LoaderCallBacks);
	}

	/**
	 * 一类疫苗
	 */
	private LoaderCallbacks<Cursor> mType1LoaderCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(
					ReservationVaccineActivity.this);
			loader.setUri(VaccinationProvider.CONTENT_URI);
			loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
					+ "=? and " + DBHelper.VACCINATION_COLUMN_VACCINE_TYPE
					+ "=? and " + DBHelper.VACCINATION_COLUMN_FINISH_TIME
					+ " is null and ("
					+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + " is null or "
					+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "=?)");
			loader.setSelectionArgs(new String[] { mDefaultBaby.getName(),
					"一类", mDate });
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			mType1Vaccinations = new ArrayList<Vaccination>();
			while (data.moveToNext()) {
				Vaccination vaccination = mVaccinationDao
						.cursorToVaccination(data);
				mType1Vaccinations.add(vaccination);
			}
			mAdapter1 = new ReservationAdapter(ReservationVaccineActivity.this,
					mType1Vaccinations, new OnSelectedItemChanged() {

						@Override
						public void getUnSelectedItem(Vaccination vaccination) {
							Log.i("MainActivity", vaccination.getVaccine_name()
									+ "(" + vaccination.getVaccination_number()
									+ ")");
							// TODO 取消预约
							boolean result = mVaccinationDao.reservationType1(
									vaccination, null);
							if (result) {
								Toast.makeText(getApplicationContext(), "取消成功",
										Toast.LENGTH_SHORT).show();
								// 查询是否还有其它预约
								List<Vaccination> list = mVaccinationDao
										.getVaccinationByReservationDate(
												mDefaultBaby.getName(), mDate);
								// 如果没有，清除之前的预约时间
								if (list == null || list.size() <= 0) {
									mPreferences.setRemindDate(null);
									Log.i("MainActivity", "没有预约疫苗了，取消提醒");
									// 取消提醒
									Remind.cancelRemind(
											ReservationVaccineActivity.this,
											Constants.REMIND_WEEK);
									Remind.cancelRemind(
											ReservationVaccineActivity.this,
											Constants.REMIND_DAY);
									Remind.cancelRemind(
											ReservationVaccineActivity.this,
											Constants.REMIND_TODAY);
								}
							} else {
								Toast.makeText(getApplicationContext(), "取消失败",
										Toast.LENGTH_SHORT).show();
							}
						}

						@Override
						public void getSelectedItem(Vaccination vaccination) {
							Log.i("MainActivity", vaccination.getVaccine_name()
									+ "(" + vaccination.getVaccination_number()
									+ ")");
							// TODO 预约
							boolean result = mVaccinationDao.reservationType1(
									vaccination, mDate);
							if (result) {
								Toast.makeText(getApplicationContext(), "预约成功",
										Toast.LENGTH_SHORT).show();
								String remindDate = mPreferences.getRemindDate();
								// 提醒
								if (StringUtils.isNullOrEmpty(remindDate)) {
									Log.i("MainActivity", "新预约，新建提醒");
									mPreferences.setRemindDate(mDate);
									if (mPreferences.getWeekBeforeIsRemind()) {
										Remind.newRemind(
												ReservationVaccineActivity.this,
												Constants.REMIND_WEEK, mDate,
												Constants.REMIND_WEEK,
												mPreferences.getWeekBeforeRemindTime(),
												mDefaultBaby.getName());
									}
									if (mPreferences.getDayBeforeIsRemind()) {
										Remind.newRemind(
												ReservationVaccineActivity.this,
												Constants.REMIND_DAY, mDate,
												Constants.REMIND_DAY,
												mPreferences.getDayBeforeRemindTime(),
												mDefaultBaby.getName());
									}
									if (mPreferences.getTodayIsRemind()) {
										Remind.newRemind(
												ReservationVaccineActivity.this,
												Constants.REMIND_TODAY, mDate,
												Constants.REMIND_TODAY,
												mPreferences.getTodayRemindTime(),
												mDefaultBaby.getName());
									}
								}else{
									try {
										int b = DateUtils.compareDate(mDate, remindDate);
										if (b < 0) {
											Log.i("MainActivity", "当前预约时间比之前预约时间小");
											mPreferences.setRemindDate(mDate);
											//判断提醒是否开启了
											if (mPreferences.getWeekBeforeIsRemind()) {
												Remind.cancelRemind(
														ReservationVaccineActivity.this,
														Constants.REMIND_WEEK);
												Remind.newRemind(
														ReservationVaccineActivity.this,
														Constants.REMIND_WEEK, mDate,
														Constants.REMIND_WEEK,
														mPreferences.getWeekBeforeRemindTime(),
														mDefaultBaby.getName());
											}
											if (mPreferences.getDayBeforeIsRemind()) {
												Remind.cancelRemind(
														ReservationVaccineActivity.this,
														Constants.REMIND_DAY);
												Remind.newRemind(
														ReservationVaccineActivity.this,
														Constants.REMIND_DAY, mDate,
														Constants.REMIND_DAY,
														mPreferences.getDayBeforeRemindTime(),
														mDefaultBaby.getName());
											}
											if (mPreferences.getTodayIsRemind()) {
												Remind.cancelRemind(
														ReservationVaccineActivity.this,
														Constants.REMIND_TODAY);
												Remind.newRemind(
														ReservationVaccineActivity.this,
														Constants.REMIND_TODAY, mDate,
														Constants.REMIND_TODAY,
														mPreferences.getTodayRemindTime(),
														mDefaultBaby.getName());
											}
											
										}
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
							} else {
								Toast.makeText(getApplicationContext(), "预约失败",
										Toast.LENGTH_SHORT).show();
							}
						}
					});
			mListViewType1.setAdapter(mAdapter1);
			mAdapter1.notifyDataSetChanged();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

	@Override
	protected void onResume() {
		super.onPause();
		MobclickAgent.onPageStart("ReservationVaccineActivity"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	};

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ReservationVaccineActivity");
		MobclickAgent.onPause(this);
	}

	/*
	 * private List<Vaccination> getNoVaccinationType2() { List<Vaccination>
	 * list = null; try { list =
	 * mVaccinationDao.queryType2NotVaccination(mDefaultBaby .getName()); }
	 * catch (ParseException e) { e.printStackTrace(); } catch
	 * (XmlPullParserException e) { e.printStackTrace(); } catch (IOException e)
	 * { e.printStackTrace(); } return list; }
	 */

	/**
	 * 初始化未接种二类数据
	 * 
	 * @return
	 */
	private LoaderCallbacks<Cursor> mType2LoaderCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(
					ReservationVaccineActivity.this);
			loader.setUri(VaccinationProvider.CONTENT_URI);
			loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME
					+ "=? and " + DBHelper.VACCINATION_COLUMN_VACCINE_TYPE
					+ "=? and " + DBHelper.VACCINATION_COLUMN_FINISH_TIME
					+ " is null and ("
					+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + " is null or "
					+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "=?)");
			loader.setSelectionArgs(new String[] { mDefaultBaby.getName(),
					"二类", mDate });
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			try {
				List<Vaccination> alreadyReservationType2List = new ArrayList<Vaccination>();
				List<Vaccination> allAlreadyReservationType2List = mVaccinationDao
						.queryTwoTypeVaccinesOfDB(mDefaultBaby.getName());
				while (data.moveToNext()) {
					Vaccination vaccination = mVaccinationDao
							.cursorToVaccination(data);
					alreadyReservationType2List.add(vaccination);
				}
				List<Vaccination> allType2List = mVaccinationDao
						.queryTwoTypeVaccinesOfXml();
				if (allAlreadyReservationType2List != null
						&& allAlreadyReservationType2List.size() != 0) {
					Iterator<Vaccination> iterator = allType2List.iterator();
					while (iterator.hasNext()) {
						Vaccination vac = iterator.next();
						for (Vaccination vaccination : allAlreadyReservationType2List) {
							if (vac.getVaccine_name().equals(
									vaccination.getVaccine_name())
									&& vac.getVaccination_number()
											.equals(vaccination
													.getVaccination_number())) {
								iterator.remove();// 已预约过，将其移除
								continue;
							}
						}
					}
				}

				// mType2Vaccinations = allType2List;
				mType2Vaccinations = new ArrayList<Vaccination>();
				mType2Vaccinations.addAll(alreadyReservationType2List);
				mType2Vaccinations.addAll(allType2List);

				mAdapter2 = new ReservationAdapter(
						ReservationVaccineActivity.this, mType2Vaccinations,
						new OnSelectedItemChanged() {

							@Override
							public void getUnSelectedItem(
									Vaccination vaccination) {
								Log.i("MainActivity",
										vaccination.getVaccine_name()
												+ "("
												+ vaccination
														.getVaccination_number()
												+ ")");
								// TODO 取消预约
								boolean result = mVaccinationDao
										.reservationType2(vaccination, null);
								if (result) {
									Toast.makeText(getApplicationContext(),
											"取消成功", Toast.LENGTH_SHORT).show();
									// 查询是否还有其它预约
									List<Vaccination> list = mVaccinationDao
											.getVaccinationByReservationDate(
													mDefaultBaby.getName(), mDate);
									// 如果没有，清除之前的预约时间
									if (list == null || list.size() <= 0) {
										mPreferences.setRemindDate(null);
										Log.i("MainActivity", "没有预约疫苗了，取消提醒");
										// 取消提醒
										Remind.cancelRemind(
												ReservationVaccineActivity.this,
												Constants.REMIND_WEEK);
										Remind.cancelRemind(
												ReservationVaccineActivity.this,
												Constants.REMIND_DAY);
										Remind.cancelRemind(
												ReservationVaccineActivity.this,
												Constants.REMIND_TODAY);
									}
								} else {
									Toast.makeText(getApplicationContext(),
											"取消失败", Toast.LENGTH_SHORT).show();
								}
							}

							@Override
							public void getSelectedItem(Vaccination vaccination) {
								vaccination.setBaby_nickname(mDefaultBaby
										.getName());
								Log.i("MainActivity",
										vaccination.getVaccine_name()
												+ "("
												+ vaccination
														.getVaccination_number()
												+ ")");
								// TODO 预约
								boolean result = mVaccinationDao
										.reservationType2(vaccination, mDate);
								if (result) {
									Toast.makeText(getApplicationContext(),
											"预约成功", Toast.LENGTH_SHORT).show();
									String remindDate = mPreferences.getRemindDate();
									// 提醒
									if (StringUtils.isNullOrEmpty(remindDate)) {
										Log.i("MainActivity", "新预约，新建提醒");
										mPreferences.setRemindDate(mDate);
										if (mPreferences.getWeekBeforeIsRemind()) {
											Remind.newRemind(
													ReservationVaccineActivity.this,
													Constants.REMIND_WEEK, mDate,
													Constants.REMIND_WEEK,
													mPreferences.getWeekBeforeRemindTime(),
													mDefaultBaby.getName());
										}
										if (mPreferences.getDayBeforeIsRemind()) {
											Remind.newRemind(
													ReservationVaccineActivity.this,
													Constants.REMIND_DAY, mDate,
													Constants.REMIND_DAY,
													mPreferences.getDayBeforeRemindTime(),
													mDefaultBaby.getName());
										}
										if (mPreferences.getTodayIsRemind()) {
											Remind.newRemind(
													ReservationVaccineActivity.this,
													Constants.REMIND_TODAY, mDate,
													Constants.REMIND_TODAY,
													mPreferences.getTodayRemindTime(),
													mDefaultBaby.getName());
										}
									}else{
										try {
											int b = DateUtils.compareDate(mDate, remindDate);
											if (b < 0) {
												Log.i("MainActivity", "当前预约时间比之前预约时间小");
												mPreferences.setRemindDate(mDate);
												//判断提醒是否开启了
												if (mPreferences.getWeekBeforeIsRemind()) {
													Remind.cancelRemind(
															ReservationVaccineActivity.this,
															Constants.REMIND_WEEK);
													Remind.newRemind(
															ReservationVaccineActivity.this,
															Constants.REMIND_WEEK, mDate,
															Constants.REMIND_WEEK,
															mPreferences.getWeekBeforeRemindTime(),
															mDefaultBaby.getName());
												}
												if (mPreferences.getDayBeforeIsRemind()) {
													Remind.cancelRemind(
															ReservationVaccineActivity.this,
															Constants.REMIND_DAY);
													Remind.newRemind(
															ReservationVaccineActivity.this,
															Constants.REMIND_DAY, mDate,
															Constants.REMIND_DAY,
															mPreferences.getDayBeforeRemindTime(),
															mDefaultBaby.getName());
												}
												if (mPreferences.getTodayIsRemind()) {
													Remind.cancelRemind(
															ReservationVaccineActivity.this,
															Constants.REMIND_TODAY);
													Remind.newRemind(
															ReservationVaccineActivity.this,
															Constants.REMIND_TODAY, mDate,
															Constants.REMIND_TODAY,
															mPreferences.getTodayRemindTime(),
															mDefaultBaby.getName());
												}
												
											}
										} catch (ParseException e) {
											e.printStackTrace();
										}
									}
								} else {
									Toast.makeText(getApplicationContext(),
											"预约失败", Toast.LENGTH_SHORT).show();
								}
							}
						});
				mListViewType2.setAdapter(mAdapter2);
				mAdapter2.notifyDataSetChanged();

			} catch (ParseException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}

	};

	/**
	 * 点击监听事件(一类/二类)
	 * 
	 */
	private class MyOnClickListener implements OnClickListener {

		private int index = 0;

		public MyOnClickListener(int index) {
			this.index = index;
		}

		public void onClick(View v) {
			mViewPager.setCurrentItem(index);
			if (index == 0) {
				mVaccineType1.setBackgroundResource(R.drawable.tab_type);
				mVaccineType2.setBackgroundResource(R.drawable.tab_type_normal);
			} else {
				mVaccineType1.setBackgroundResource(R.drawable.tab_type_normal);
				mVaccineType2.setBackgroundResource(R.drawable.tab_type);
			}
		}

	}

	/**
	 * 自定义PagerAdapter
	 * 
	 */
	private class MyPagerAdapter extends PagerAdapter {

		private List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	/**
	 * ViewPager监听
	 * 
	 */
	public class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			if (position == 0) {
				mVaccineType1.setBackgroundResource(R.drawable.tab_type);
				mVaccineType2.setBackgroundResource(R.drawable.tab_type_normal);
			} else {
				mVaccineType1.setBackgroundResource(R.drawable.tab_type_normal);
				mVaccineType2.setBackgroundResource(R.drawable.tab_type);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 返回键监听
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent();
			intent.putExtra("date", mNoFormatDate);
			setResult(RESULT_OK, intent);
			this.finish();
		}

		return super.onKeyDown(keyCode, event);
	}

}
