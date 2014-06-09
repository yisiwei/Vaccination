package cn.mointe.vaccination.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.calendar.CalendarAdapter;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;

import com.umeng.analytics.MobclickAgent;

public class ReservationCalendarActivity extends Activity implements
		OnGestureListener {

	private TextView mTopText;

	private ViewFlipper mFlipper = null;
	private GestureDetector mGestureDetector = null;
	private GridView mGridView = null;

	private CalendarAdapter mCalendarAdapter = null;
	private static int sJumpMonth = 0; // 每次滑动，增加或减去一个月,默认为0（即显示当前月）
	private static int sJumpYear = 0; // 滑动跨越一年，则增加或者减去一年,默认为0(即当前年)

	private int mYear_c = 0;
	private int mMonth_c = 0;
	private int mDay_c = 0;
	private String mCurrentDate = "";

	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	private PopupWindow mPopupWindow;
	private View mMoreParent; // xml布局
	private RelativeLayout mParentLayout;

	private View mTitle;

	public ReservationCalendarActivity() {

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d",
				Locale.getDefault());
		mCurrentDate = sdf.format(date); // 当期日期
		mYear_c = Integer.parseInt(mCurrentDate.split("-")[0]);
		mMonth_c = Integer.parseInt(mCurrentDate.split("-")[1]);
		mDay_c = Integer.parseInt(mCurrentDate.split("-")[2]);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation_calendar);

		mParentLayout = (RelativeLayout) this
				.findViewById(R.id.reservation_calendar_parent);

		mTitle = this.findViewById(R.id.title);
		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);

		mTitleText.setText(R.string.reserve_vaccination);
		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ReservationCalendarActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPopupWindow.showAtLocation(mParentLayout, Gravity.TOP
						| Gravity.RIGHT, 10, getTitleHeight());
			}

		});

		mGestureDetector = new GestureDetector(this, this);
		mFlipper = (ViewFlipper) findViewById(R.id.calendar_flipper);
		mFlipper.removeAllViews();

		mCalendarAdapter = new CalendarAdapter(this, getResources(),
				sJumpMonth, sJumpYear, mYear_c, mMonth_c, mDay_c);

		addGridView();
		mGridView.setAdapter(mCalendarAdapter);
		mCalendarAdapter.notifyDataSetChanged();

		mFlipper.addView(mGridView, 0);
		mTopText = (TextView) findViewById(R.id.calendar_top_text);
		addTextToTopTextView(mTopText);

		// PopupWindow相关
		mMoreParent = getLayoutInflater().inflate(R.layout.popupwindow_menu,
				null);
		ListView listView = (ListView) mMoreParent
				.findViewById(R.id.pop_list_more);
		listView.setAdapter(getAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					// 跳转到今天
					int xMonth = sJumpMonth;
					int xYear = sJumpYear;
					int gvFlag = 0;
					sJumpMonth = 0;
					sJumpYear = 0;
					addGridView(); // 添加一个gridView
					mYear_c = Integer.parseInt(mCurrentDate.split("-")[0]);
					mMonth_c = Integer.parseInt(mCurrentDate.split("-")[1]);
					mDay_c = Integer.parseInt(mCurrentDate.split("-")[2]);
					mCalendarAdapter = new CalendarAdapter(
							ReservationCalendarActivity.this, getResources(),
							sJumpMonth, sJumpYear, mYear_c, mMonth_c, mDay_c);
					mGridView.setAdapter(mCalendarAdapter);
					addTextToTopTextView(mTopText);
					gvFlag++;
					mFlipper.addView(mGridView, gvFlag);
					if (xMonth == 0 && xYear == 0) {
						// nothing to do
					} else if ((xYear == 0 && xMonth > 0) || xYear > 0) {
						mFlipper.setInAnimation(AnimationUtils.loadAnimation(
								ReservationCalendarActivity.this,
								R.anim.push_left_in));
						mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
								ReservationCalendarActivity.this,
								R.anim.push_left_out));
						mFlipper.showNext();
					} else {
						mFlipper.setInAnimation(AnimationUtils.loadAnimation(
								ReservationCalendarActivity.this,
								R.anim.push_right_in));
						mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
								ReservationCalendarActivity.this,
								R.anim.push_right_out));
						mFlipper.showPrevious();
					}
					mFlipper.removeViewAt(0);
				} else {
					new DatePickerDialog(ReservationCalendarActivity.this,
							new OnDateSetListener() {

								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									// 1901-1-1 ----> 2049-12-31
									if (year < 1901 || year > 2049) {
										// 不在查询范围内
										new AlertDialog.Builder(
												ReservationCalendarActivity.this)
												.setTitle("错误日期")
												.setMessage(
														"跳转日期范围(1901/1/1-2049/12/31)")
												.setPositiveButton("确认", null)
												.show();
									} else {
										int gvFlag = 0;
										addGridView(); // 添加一个gridView
										mCalendarAdapter = new CalendarAdapter(
												ReservationCalendarActivity.this,
												ReservationCalendarActivity.this
														.getResources(), year,
												monthOfYear + 1, dayOfMonth);
										mGridView.setAdapter(mCalendarAdapter);
										addTextToTopTextView(mTopText);
										gvFlag++;
										mFlipper.addView(mGridView, gvFlag);
										if (year == mYear_c
												&& monthOfYear + 1 == mMonth_c) {
											// nothing to do
										}
										if ((year == mYear_c && monthOfYear + 1 > mMonth_c)
												|| year > mYear_c) {
											ReservationCalendarActivity.this.mFlipper
													.setInAnimation(AnimationUtils
															.loadAnimation(
																	ReservationCalendarActivity.this,
																	R.anim.push_left_in));
											ReservationCalendarActivity.this.mFlipper
													.setOutAnimation(AnimationUtils
															.loadAnimation(
																	ReservationCalendarActivity.this,
																	R.anim.push_left_out));
											ReservationCalendarActivity.this.mFlipper
													.showNext();
										} else {
											ReservationCalendarActivity.this.mFlipper
													.setInAnimation(AnimationUtils
															.loadAnimation(
																	ReservationCalendarActivity.this,
																	R.anim.push_right_in));
											ReservationCalendarActivity.this.mFlipper
													.setOutAnimation(AnimationUtils
															.loadAnimation(
																	ReservationCalendarActivity.this,
																	R.anim.push_right_out));
											ReservationCalendarActivity.this.mFlipper
													.showPrevious();
										}
										mFlipper.removeViewAt(0);
										// 跳转之后将跳转之后的日期设置为当期日期
										mYear_c = year;
										mMonth_c = monthOfYear + 1;
										mDay_c = dayOfMonth;
										sJumpMonth = 0;
										sJumpYear = 0;
									}
								}
							}, mYear_c, mMonth_c - 1, mDay_c).show();
				}
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();// 关闭
				}
			}
		});

		mPopupWindow = new PopupWindow(mMoreParent,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		ColorDrawable dw = new ColorDrawable(0000000000);
		mPopupWindow.setBackgroundDrawable(dw);
		// 设置动画
		mPopupWindow.setAnimationStyle(R.style.mystyle);
		mMoreParent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMoreParent.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						mPopupWindow.dismiss();
					}
				}
				return true;
			}

		});
		// 为了使点击menu键打开或关闭系统菜单
		mMoreParent.setFocusableInTouchMode(true);
		mMoreParent.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_MENU) {
					mPopupWindow.dismiss();// 这里写明模拟menu的PopupWindow退出就行
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * Pop Adapter
	 * 
	 * @return
	 */
	private ListAdapter getAdapter() {
		List<String> list = new ArrayList<String>();
		list.add("今天");
		list.add("跳转");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.pop_calendar_item, R.id.pop_calendar_item_tv, list);
		return adapter;
	}

	/**
	 * 获取状态栏+标题栏高度
	 * 
	 * @return
	 */
	public int getTitleHeight() {
		Rect rect = new Rect();
		ReservationCalendarActivity.this.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rect);
		int topS = rect.top;// 状态栏高度
		Log.i("MainActivity", "title height:" + mTitle.getHeight() + "--状态栏高度："
				+ topS);
		return topS + mTitle.getHeight();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("ReservationCalendarActivity"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("ReservationCalendarActivity");
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 2000) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String date = bundle.getString("date");
				String[] arr = date.split("-");
				Log.i("MainActivity", date);
				int gvFlag = 0;
				sJumpYear = 0;
				sJumpMonth = 0;
				addGridView(); // 添加一个gridView
				// mYear_c = Integer.parseInt(mCurrentDate.split("-")[0]);
				// mMonth_c = Integer.parseInt(mCurrentDate.split("-")[1]);
				// mDay_c = Integer.parseInt(mCurrentDate.split("-")[2]);
				mYear_c = Integer.valueOf(arr[0]);
				mMonth_c = Integer.valueOf(arr[1]);
				mDay_c = Integer.valueOf(arr[2]);
				mCalendarAdapter = new CalendarAdapter(this, getResources(),
						sJumpMonth, sJumpYear, mYear_c, mMonth_c, mDay_c);
				mGridView.setAdapter(mCalendarAdapter);
				addTextToTopTextView(mTopText);
				gvFlag++;
				mFlipper.addView(mGridView, gvFlag);
				mFlipper.removeViewAt(0);

			}
		}

	}

	// 添加头部的年份 闰哪月等信息
	public void addTextToTopTextView(TextView view) {
		StringBuffer textDate = new StringBuffer();
		// draw = getResources().getDrawable(R.drawable.top_day);
		// view.setBackgroundDrawable(draw);
		// view.setBackgroundResource(R.drawable.top_day);
		textDate.append(mCalendarAdapter.getShowYear()).append("年")
				.append(mCalendarAdapter.getShowMonth()).append("月")
				.append("\t");
		if (!mCalendarAdapter.getLeapMonth().equals("")
				&& mCalendarAdapter.getLeapMonth() != null) {
			textDate.append("闰").append(mCalendarAdapter.getLeapMonth())
					.append("月").append("\t");
		}
		textDate.append(mCalendarAdapter.getAnimalsYear()).append("年")
				.append("(").append(mCalendarAdapter.getCyclical())
				.append("年)");
		view.setText(textDate);
		view.setTextColor(Color.BLACK);
		view.setTypeface(Typeface.DEFAULT_BOLD);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int gvFlag = 0; // 每次添加gridview到viewflipper中时给的标记
		if (e1.getX() - e2.getX() > 50) {
			// 像左滑动
			addGridView(); // 添加一个gridView
			sJumpMonth++; // 下一个月

			mCalendarAdapter = new CalendarAdapter(this, getResources(),
					sJumpMonth, sJumpYear, mYear_c, mMonth_c, mDay_c);
			mGridView.setAdapter(mCalendarAdapter);
			// flipper.addView(gridView);
			addTextToTopTextView(mTopText);
			gvFlag++;
			mFlipper.addView(mGridView, gvFlag);
			this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_in));
			this.mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_left_out));
			this.mFlipper.showNext();
			mFlipper.removeViewAt(0);
			return true;
		} else if (e1.getX() - e2.getX() < -50) {
			// 向右滑动
			addGridView(); // 添加一个gridView
			sJumpMonth--; // 上一个月

			mCalendarAdapter = new CalendarAdapter(this, getResources(),
					sJumpMonth, sJumpYear, mYear_c, mMonth_c, mDay_c);
			mGridView.setAdapter(mCalendarAdapter);
			gvFlag++;
			addTextToTopTextView(mTopText);
			// flipper.addView(gridView);
			mFlipper.addView(mGridView, gvFlag);

			this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_in));
			this.mFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_right_out));
			this.mFlipper.showPrevious();
			mFlipper.removeViewAt(0);
			return true;
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return this.mGestureDetector.onTouchEvent(event);
	}

	// 添加gridview
	private void addGridView() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// 取得屏幕的宽度和高度
		// WindowManager windowManager = getWindowManager();
		// Display display = windowManager.getDefaultDisplay();
		// int Width = display.getWidth();
		// int Height = display.getHeight();
		// DisplayMetrics dm =getResources().getDisplayMetrics();
		// int Width = dm.widthPixels;
		// int Height = dm.heightPixels;

		mGridView = new GridView(this);
		mGridView.setNumColumns(7);
		// gridView.setColumnWidth(46);
		// gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		// if (Width == 480 && Height == 800) {
		// gridView.setColumnWidth(69);
		// }
		mGridView.setGravity(Gravity.CENTER_VERTICAL);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT)); // 去除gridView边框
		mGridView.setVerticalSpacing(1);
		mGridView.setHorizontalSpacing(1);
		mGridView.setBackgroundResource(R.drawable.gridview_bk);
		mGridView.setOnTouchListener(new OnTouchListener() {
			// 将gridview中的触摸事件回传给gestureDetector

			public boolean onTouch(View v, MotionEvent event) {
				return ReservationCalendarActivity.this.mGestureDetector
						.onTouchEvent(event);
			}
		});

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			// gridView中的每一个item的点击事件

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 点击任何一个item，得到这个item的日期(排除点击的是周日到周六(点击不响应))
				int startPosition = mCalendarAdapter.getStartPositon();
				int endPosition = mCalendarAdapter.getEndPosition();
				if (startPosition <= position && position <= endPosition) {
					String scheduleDay = mCalendarAdapter.getDateByClickItem(
							position).split("\\.")[0]; // 这一天的阳历
					// String scheduleLunarDay =
					// calV.getDateByClickItem(position).split("\\.")[1];
					// //这一天的阴历
					String scheduleYear = mCalendarAdapter.getShowYear();
					String scheduleMonth = mCalendarAdapter.getShowMonth();

					StringBuilder builder = new StringBuilder();
					builder.append(scheduleYear).append("-");
					if (scheduleMonth.length() > 1) {
						builder.append(scheduleMonth).append("-");
					} else {
						builder.append("0").append(scheduleMonth).append("-");
					}
					if (scheduleDay.length() > 1) {
						builder.append(scheduleDay);
					} else {
						builder.append("0").append(scheduleDay);
					}

					int result;
					try {
						result = DateUtils.compareDateToToday(builder
								.toString());
						if (result >= 0) {
							Intent intent = new Intent(
									ReservationCalendarActivity.this,
									ReservationVaccineActivity.class);
							intent.putExtra("date", builder.toString());
							intent.putExtra("noFormatDate", scheduleYear + "-"
									+ scheduleMonth + "-" + scheduleDay);
							startActivityForResult(intent, 2000);
						} else {
							Intent intent = new Intent(
									ReservationCalendarActivity.this,
									ReservationBeforeTodayActivity.class);
							intent.putExtra("date", builder.toString());
							intent.putExtra("noFormatDate", scheduleYear + "-"
									+ scheduleMonth + "-" + scheduleDay);
							startActivityForResult(intent, 2000);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}

				}
			}
		});
		mGridView.setLayoutParams(params);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			} else {
				mPopupWindow.showAtLocation(mParentLayout, Gravity.TOP
						| Gravity.RIGHT, 10, getTitleHeight());
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
