package cn.mointe.vaccination.activity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.fragment.ITitleChangeListener;
import cn.mointe.vaccination.fragment.MainFragment;
import cn.mointe.vaccination.fragment.MainTodayFragment;
import cn.mointe.vaccination.fragment.SlidingMenuFragment;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.slidingmenu.app.SlidingFragmentActivity;
import cn.mointe.vaccination.slidingmenu.lib.SlidingMenu;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.NetworkUtil;

import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.ConversationActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

/**
 * 主Activity
 * 
 * @author yi_siwei
 * 
 */
public class MainActivity extends SlidingFragmentActivity implements
		ITitleChangeListener {

	// 系统菜单
	private PopupWindow mPopMore;
	private View mMoreParent; // xml布局
	private int[] mMoreIcons = { R.drawable.actionbar_setting_icon,
			R.drawable.actionbar_update_icon, R.drawable.actionbar_mail_icon,
			R.drawable.actionbar_about_us_icon, R.drawable.logout };// 系统菜单图标
	private String[] mMoreTitle;// 系统菜单标题

	private RelativeLayout mTitleView; // title布局layout
	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	private LinearLayout mMainLayout;

	private ProgressDialog mProgressDialog;

	private long mTouchTime = 0;
	private long mWaitTime = 2000;

	private BabyDao mBabyDao;
	private VaccinationDao mVaccinationDao;

	private Tencent mTencent;
	private static final String APP_ID = "101080056";
	private VaccinationPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		int state = NetworkUtil.getAPNType(this);
		if (state == -1) {
			Toast.makeText(this, "无法连接到服务器，请检查您的网络", Toast.LENGTH_SHORT).show();
		}

		mTencent = Tencent.createInstance(APP_ID, this.getApplicationContext());
		mPreferences = new VaccinationPreferences(this);

		// setSlidingActionBarEnabled(true);
		mBabyDao = new BabyDao(this);
		mVaccinationDao = new VaccinationDao(this);

		mMainLayout = (LinearLayout) this.findViewById(R.id.main_layout);

		mTitleView = (RelativeLayout) this.findViewById(R.id.title_view);
		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);

		// 系统菜单选项
		mMoreTitle = getResources().getStringArray(R.array.system_menu);

		mTitleLeftImgbtn.setBackgroundResource(R.drawable.actionbar_menu_icon);
		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggle();// SlidingMenu的打开与关闭
			}
		});

		mTitleRightImgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mPopMore.showAtLocation(mTitleView,
						Gravity.TOP | Gravity.RIGHT, 10, getTitleHeight());
			}
		});

		// 系统菜单相关
		mMoreParent = getLayoutInflater().inflate(R.layout.popupwindow_menu,
				null);
		ListView moreList = (ListView) mMoreParent
				.findViewById(R.id.pop_list_more);
		moreList.setAdapter(getMoreAdapter());
		moreList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				switch (position) {
				case 0:
					intent = new Intent(MainActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;
				case 1:
					mProgressDialog = ProgressDialog.show(MainActivity.this,
							"", "正在获取最新版本信息...");
					// new Thread(new CheckVersionTask()).start();
					checkVersion();
					break;
				case 2:
					intent = new Intent(MainActivity.this,
							ConversationActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

					break;
				case 3:
					intent = new Intent(MainActivity.this,
							AboutUsActivity.class);
					startActivity(intent);
					break;
				case 4:
					// TODO 退出登录，清除登录信息
					mTencent.logout(MainActivity.this);
					mPreferences.setOpenid(null);
					mPreferences.setAccessToken(null);
					mPreferences.setExpiresIn(null);

					MainActivity.this.finish();
					intent = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(intent);
				default:
					break;
				}
				if (mPopMore.isShowing()) {
					mPopMore.dismiss();// 关闭
				}
			}
		});

		mPopMore = new PopupWindow(mMoreParent,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, true);
		ColorDrawable dw = new ColorDrawable(0000000000);
		mPopMore.setBackgroundDrawable(dw);
		// 设置动画
		mPopMore.setAnimationStyle(R.style.mystyle);
		mMoreParent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMoreParent.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						mPopMore.dismiss();
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
					mPopMore.dismiss();// 这里写明模拟menu的PopupWindow退出就行
					return true;
				}
				return false;
			}
		});

		// 初始化滑动菜单
		initSlidingMenu(savedInstanceState);

		// mTencent = Tencent.createInstance("101080056",
		// this.getApplicationContext());

		boolean flag = getIntent().getBooleanExtra("notificationFlag", false);
		Log.i("MainActivity", "是否点击通知进入：" + flag);
		if (flag) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					startActivity(new Intent(MainActivity.this,
							InboxActivity.class));
				}
			}).start();
		}
	}

	/**
	 * 初始化滑动菜单
	 */
	private void initSlidingMenu(Bundle savedInstanceState) {
		// 设置滑动菜单的视图
		setBehindContentView(R.layout.sliding_menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SlidingMenuFragment()).commit();
		// 实例化滑动菜单对象
		SlidingMenu sm = getSlidingMenu();
		// 设置滑动阴影的宽度
		sm.setShadowWidthRes(R.dimen.shadow_width);
		// 设置滑动阴影的图像资源
		// sm.setShadowDrawable(R.drawable.shadow);
		// 设置滑动菜单视图的宽度
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// 设置渐入渐出效果的值
		sm.setFadeDegree(0.35f);
		// 设置触摸屏幕的模式
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	/**
	 * 初始化系统菜单adapter
	 * 
	 * @return
	 */
	private ListAdapter getMoreAdapter() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mMoreIcons.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("icon", mMoreIcons[i]);
			item.put("title", mMoreTitle[i]);
			data.add(item);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, data,
				R.layout.popupwindow_item, new String[] { "icon", "title" },
				new int[] { R.id.pop_item_img, R.id.pop_item_text });
		return adapter;
	}

	/**
	 * 获取状态栏+标题栏高度
	 * 
	 * @return
	 */
	public int getTitleHeight() {
		Rect rect = new Rect();
		MainActivity.this.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rect);
		int topS = rect.top;// 状态栏高度
		Log.i("MainActivity", "title height:" + mTitleView.getHeight()
				+ "--状态栏高度：" + topS);
		return topS + mTitleView.getHeight();
	}

	/**
	 * 
	 * 版本检测
	 */
	private void checkVersion() {
		UmengUpdateAgent.setUpdateOnlyWifi(true);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // 有更新
					UmengUpdateAgent.showUpdateDialog(getApplicationContext(),
							updateInfo);
					// showUpdateDialog(updateInfo.path, updateInfo.updateLog);
					break;
				case 1: // 无更新
					// OnNotice("当前已是最新版.");
					Toast.makeText(getApplicationContext(), "您当前已是最新版本",
							Toast.LENGTH_SHORT).show();
					break;
				case 2: // 如果设置为wifi下更新且wifi无法打开时调用
					break;
				case 3: // 连接超时
					Toast.makeText(getApplicationContext(), "连接超时，请稍候重试",
							Toast.LENGTH_SHORT).show();
					// OnNotice("连接超时，请稍候重试");
					break;
				}
				mProgressDialog.dismiss();
			}
		});

		UmengUpdateAgent.update(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 得到ActionBar高度
	 * 
	 * @return
	 */
	public int getActionBarHeight() {
		Rect rect = new Rect();
		MainActivity.this.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(rect);
		View view = MainActivity.this.getWindow().findViewById(
				Window.ID_ANDROID_CONTENT);
		int topS = rect.top;// 状态栏高度
		int topT = rect.height() - view.getHeight();// 标题栏高度
		Log.i("MainActivity", "title高度:" + topT + "--状态栏高度：" + topS);
		return topS + topT;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 返回键监听
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mTitleText.getText().toString().equals("首页")) {
				long currentTime = System.currentTimeMillis();
				if ((currentTime - mTouchTime) > mWaitTime) {
					Toast.makeText(getApplicationContext(), "再按一次退出程序",
							Toast.LENGTH_SHORT).show();
					mTouchTime = currentTime;
				} else {
					this.finish();
					// System.exit(0);
				}
			} else {
				mTitleText.setText("首页");
				FragmentManager mManager = getSupportFragmentManager();
				FragmentTransaction mTransaction = mManager.beginTransaction();
				Fragment fragment = null;
				if (getDefaultBabyAndCalculateCount()) {
					fragment = new MainTodayFragment();
				} else {
					fragment = new MainFragment();
				}
				mTransaction.replace(R.id.main_content, fragment);
				mTransaction.commit();
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if (mPopMore.isShowing()) {
				mPopMore.dismiss();
			} else {
				mPopMore.showAtLocation(mMainLayout, Gravity.TOP
						| Gravity.RIGHT, 10, getTitleHeight());
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void setTitle(String title) {
		mTitleText.setText(title);
	}

	/**
	 * 查询默认宝宝并计算距下次接种日期天数
	 * 
	 * @return
	 */
	private boolean getDefaultBabyAndCalculateCount() {
		Baby defaultBaby = mBabyDao.getDefaultBaby();// 查询默认宝宝
		try {
			String nextDate = mVaccinationDao.findNextDate(defaultBaby
					.getName());
			if (null != nextDate
					&& nextDate.equals(DateUtils.getCurrentFormatDate())) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

}
