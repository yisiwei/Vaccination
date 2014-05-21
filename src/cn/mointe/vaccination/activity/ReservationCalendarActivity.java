package cn.mointe.vaccination.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.calendar.CalendarAdapter;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;

public class ReservationCalendarActivity extends Activity implements
		OnGestureListener {

	private TextView mTopText;
	private Button mJumpBtn;
	private Button mTodayBtn;
	
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
		
		mJumpBtn = (Button) this.findViewById(R.id.calendar_btn_jump);
		mTodayBtn = (Button) this.findViewById(R.id.calendar_btn_today);
		
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
		mTitleRightImgbtn.setVisibility(View.GONE);

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
		
		mTodayBtn.setOnClickListener(new MyOnClickListener(0));
		mJumpBtn.setOnClickListener(new MyOnClickListener(1));
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
//				mYear_c = Integer.parseInt(mCurrentDate.split("-")[0]);
//				mMonth_c = Integer.parseInt(mCurrentDate.split("-")[1]);
//				mDay_c = Integer.parseInt(mCurrentDate.split("-")[2]);
				mYear_c = Integer.valueOf(arr[0]);
				mMonth_c = Integer.valueOf(arr[1]);
				mDay_c = Integer.valueOf(arr[2]);
				mCalendarAdapter = new CalendarAdapter(this, getResources(), sJumpMonth, sJumpYear,
						mYear_c, mMonth_c, mDay_c);
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
		//view.setBackgroundResource(R.drawable.top_day);
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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//
//		menu.add(0, Menu.FIRST, Menu.FIRST, "今天");
//		menu.add(0, Menu.FIRST + 1, Menu.FIRST + 1, "跳转");
//		return super.onCreateOptionsMenu(menu);
//	}
	
	private class MyOnClickListener implements OnClickListener{

		private int index;
		
		public MyOnClickListener(int index) {
			this.index = index;
		}
		@Override
		public void onClick(View v) {
			if(index == 0){
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
				mCalendarAdapter = new CalendarAdapter(getApplicationContext(), getResources(),
						sJumpMonth, sJumpYear, mYear_c, mMonth_c, mDay_c);
				mGridView.setAdapter(mCalendarAdapter);
				addTextToTopTextView(mTopText);
				gvFlag++;
				mFlipper.addView(mGridView, gvFlag);
				if (xMonth == 0 && xYear == 0) {
					// nothing to do
				} else if ((xYear == 0 && xMonth > 0) || xYear > 0) {
					mFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.push_left_in));
					mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.push_left_out));
					mFlipper.showNext();
				} else {
					mFlipper.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.push_right_in));
					mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
							getApplicationContext(), R.anim.push_right_out));
					mFlipper.showPrevious();
				}
				mFlipper.removeViewAt(0);
			}else if(index == 1){
				new DatePickerDialog(ReservationCalendarActivity.this, new OnDateSetListener() {

					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						// 1901-1-1 ----> 2049-12-31
						if (year < 1901 || year > 2049) {
							// 不在查询范围内
							new AlertDialog.Builder(
									ReservationCalendarActivity.this)
									.setTitle("错误日期")
									.setMessage("跳转日期范围(1901/1/1-2049/12/31)")
									.setPositiveButton("确认", null).show();
						} else {
							int gvFlag = 0;
							addGridView(); // 添加一个gridView
							mCalendarAdapter = new CalendarAdapter(
									ReservationCalendarActivity.this,
									ReservationCalendarActivity.this.getResources(),
									year, monthOfYear + 1, dayOfMonth);
							mGridView.setAdapter(mCalendarAdapter);
							addTextToTopTextView(mTopText);
							gvFlag++;
							mFlipper.addView(mGridView, gvFlag);
							if (year == mYear_c && monthOfYear + 1 == mMonth_c) {
								// nothing to do
							}
							if ((year == mYear_c && monthOfYear + 1 > mMonth_c)
									|| year > mYear_c) {
								ReservationCalendarActivity.this.mFlipper.setInAnimation(AnimationUtils
										.loadAnimation(
												ReservationCalendarActivity.this,
												R.anim.push_left_in));
								ReservationCalendarActivity.this.mFlipper.setOutAnimation(AnimationUtils
										.loadAnimation(
												ReservationCalendarActivity.this,
												R.anim.push_left_out));
								ReservationCalendarActivity.this.mFlipper
										.showNext();
							} else {
								ReservationCalendarActivity.this.mFlipper.setInAnimation(AnimationUtils
										.loadAnimation(
												ReservationCalendarActivity.this,
												R.anim.push_right_in));
								ReservationCalendarActivity.this.mFlipper.setOutAnimation(AnimationUtils
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
		}
		
	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case Menu.FIRST:
//			// 跳转到今天
//			int xMonth = sJumpMonth;
//			int xYear = sJumpYear;
//			int gvFlag = 0;
//			sJumpMonth = 0;
//			sJumpYear = 0;
//			addGridView(); // 添加一个gridView
//			mYear_c = Integer.parseInt(mCurrentDate.split("-")[0]);
//			mMonth_c = Integer.parseInt(mCurrentDate.split("-")[1]);
//			mDay_c = Integer.parseInt(mCurrentDate.split("-")[2]);
//			mCalendarAdapter = new CalendarAdapter(this, getResources(),
//					sJumpMonth, sJumpYear, mYear_c, mMonth_c, mDay_c);
//			mGridView.setAdapter(mCalendarAdapter);
//			addTextToTopTextView(mTopText);
//			gvFlag++;
//			mFlipper.addView(mGridView, gvFlag);
//			if (xMonth == 0 && xYear == 0) {
//				// nothing to do
//			} else if ((xYear == 0 && xMonth > 0) || xYear > 0) {
//				this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
//						R.anim.push_left_in));
//				this.mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
//						this, R.anim.push_left_out));
//				this.mFlipper.showNext();
//			} else {
//				this.mFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
//						R.anim.push_right_in));
//				this.mFlipper.setOutAnimation(AnimationUtils.loadAnimation(
//						this, R.anim.push_right_out));
//				this.mFlipper.showPrevious();
//			}
//			mFlipper.removeViewAt(0);
//			break;
//		case Menu.FIRST + 1:
//
//			new DatePickerDialog(this, new OnDateSetListener() {
//
//				public void onDateSet(DatePicker view, int year,
//						int monthOfYear, int dayOfMonth) {
//					// 1901-1-1 ----> 2049-12-31
//					if (year < 1901 || year > 2049) {
//						// 不在查询范围内
//						new AlertDialog.Builder(
//								ReservationCalendarActivity.this)
//								.setTitle("错误日期")
//								.setMessage("跳转日期范围(1901/1/1-2049/12/31)")
//								.setPositiveButton("确认", null).show();
//					} else {
//						int gvFlag = 0;
//						addGridView(); // 添加一个gridView
//						mCalendarAdapter = new CalendarAdapter(
//								ReservationCalendarActivity.this,
//								ReservationCalendarActivity.this.getResources(),
//								year, monthOfYear + 1, dayOfMonth);
//						mGridView.setAdapter(mCalendarAdapter);
//						addTextToTopTextView(mTopText);
//						gvFlag++;
//						mFlipper.addView(mGridView, gvFlag);
//						if (year == mYear_c && monthOfYear + 1 == mMonth_c) {
//							// nothing to do
//						}
//						if ((year == mYear_c && monthOfYear + 1 > mMonth_c)
//								|| year > mYear_c) {
//							ReservationCalendarActivity.this.mFlipper.setInAnimation(AnimationUtils
//									.loadAnimation(
//											ReservationCalendarActivity.this,
//											R.anim.push_left_in));
//							ReservationCalendarActivity.this.mFlipper.setOutAnimation(AnimationUtils
//									.loadAnimation(
//											ReservationCalendarActivity.this,
//											R.anim.push_left_out));
//							ReservationCalendarActivity.this.mFlipper
//									.showNext();
//						} else {
//							ReservationCalendarActivity.this.mFlipper.setInAnimation(AnimationUtils
//									.loadAnimation(
//											ReservationCalendarActivity.this,
//											R.anim.push_right_in));
//							ReservationCalendarActivity.this.mFlipper.setOutAnimation(AnimationUtils
//									.loadAnimation(
//											ReservationCalendarActivity.this,
//											R.anim.push_right_out));
//							ReservationCalendarActivity.this.mFlipper
//									.showPrevious();
//						}
//						mFlipper.removeViewAt(0);
//						// 跳转之后将跳转之后的日期设置为当期日期
//						mYear_c = year;
//						mMonth_c = monthOfYear + 1;
//						mDay_c = dayOfMonth;
//						sJumpMonth = 0;
//						sJumpYear = 0;
//					}
//				}
//			}, mYear_c, mMonth_c - 1, mDay_c).show();
//			break;
//		}
//		return super.onOptionsItemSelected(item);
//	}

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
						result = DateUtils.compareDateToToday(builder.toString());
						if (result>=0) {
							Intent intent = new Intent(
									ReservationCalendarActivity.this,
									ReservationVaccineActivity.class);
							intent.putExtra("date", builder.toString());
							intent.putExtra("noFormatDate", scheduleYear+"-"+scheduleMonth+"-"+scheduleDay);
							startActivityForResult(intent, 2000);
						}else {
							Intent intent = new Intent(
									ReservationCalendarActivity.this,
									ReservationBeforeTodayActivity.class);
							intent.putExtra("date", builder.toString());
							startActivity(intent);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
				}
			}
		});
		mGridView.setLayoutParams(params);
	}
}
