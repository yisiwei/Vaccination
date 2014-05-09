package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.umeng.fb.ConversationActivity;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.DrawerBabyAdapter;
import cn.mointe.vaccination.adapter.DrawerMenuAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.UpdataInfo;
import cn.mointe.vaccination.fragment.BabyListFragment;
import cn.mointe.vaccination.fragment.MainFragment;
import cn.mointe.vaccination.fragment.MainTodayFragment;
import cn.mointe.vaccination.fragment.VaccineLibraryFragment;
import cn.mointe.vaccination.fragment.VaccineListFragment;
import cn.mointe.vaccination.fragment.VaccineNewsFragment;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.provider.BabyProvider;
import cn.mointe.vaccination.service.UpdateService;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PackageUtil;

/**
 * 主Activity
 * 
 * @author yi_siwei
 * 
 */
public class MainActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {

	// 系统菜单
	private PopupWindow mPopMore;
	private View mMoreParent;
	private int[] mMoreIcons = { R.drawable.actionbar_setting_icon,
			R.drawable.actionbar_update_icon, R.drawable.actionbar_mail_icon,
			R.drawable.actionbar_about_us_icon };// 系统菜单图标
	private String[] mMoreTitle;

	private ActionBar mBar;

	// private ListView mDrawerListView;
	private GridView mMenuGridView;
	private LinearLayout mDrawerLinearLayout;
	private DrawerLayout mDrawerLayout;
	private String[] mMeunList;// 侧滑菜单列表

	// 侧滑宝宝列表
	private List<Baby> mBabys;
	private Baby mBaby;
	private BabyDao mBabyDao;
	private GridView mBabyGridView;
	private DrawerBabyAdapter mBabyAdapter;
	private VaccinationDao mVaccinationDao;

	private LoaderManager mLoaderManager;

	private FragmentManager mManager;
	private FragmentTransaction mTransaction;

	private long mTouchTime = 0;
	private long mWaitTime = 2000;
	// private SimpleAdapter mSimpleAdapter;

	// R.drawable.vac_summary,R.drawable.vac_note
	private static final int[] ICONS = { R.drawable.home, R.drawable.vac_list,
			R.drawable.vac_lib, R.drawable.babies, R.drawable.vac_news };

	private ProgressDialog mDProgressDialog;
	public static final String SERVER_PATH = "http://www.mointe.cn/version";
	private UpdataInfo mInfo;

	private final int UPDATA_NO = 0; // 无更新
	private final int UPDATA_OK = 1; // 有更新
	private final int GET_UNDATAINFO_ERROR = 2; // 获取服务器信息失败

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBabyDao = new BabyDao(this);
		mVaccinationDao = new VaccinationDao(this);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		// 系统菜单选项
		mMoreTitle = getResources().getStringArray(R.array.system_menu);

		mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
		mDrawerLinearLayout = (LinearLayout) this
				.findViewById(R.id.left_drawer_llay);
		// mDrawerListView = (ListView) this.findViewById(R.id.left_drawer);

		mMenuGridView = (GridView) this.findViewById(R.id.left_menu_gridview);
		mBabyGridView = (GridView) this.findViewById(R.id.left_baby_gridview);

		mLoaderManager = getSupportLoaderManager();
		mLoaderManager.initLoader(1000, null, this);

		mBabyGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mBaby = (Baby) mBabyAdapter.getItem(position);
				// Toast.makeText(getApplicationContext(), "" + mBaby.getName(),
				// Toast.LENGTH_SHORT).show();
				if (mBaby.getName().equals("新宝宝")) {
					// TODO 跳转到添加宝宝界面
					startActivity(new Intent(MainActivity.this,
							RegisterBabyActivity.class));
				} else {
					if (!"1".equals(mBaby.getIs_default())) {
						Baby defaultBaby = mBabyDao.getDefaultBaby();
						mBabyDao.updateBabyIsDefault(defaultBaby);// 将默认的修改为非默认的
						mBabyDao.updateBabyIsDefault(mBaby);// 将选中的修改为默认的
						try {
							String reserveTime = mVaccinationDao
									.findNextDate(mBaby.getName());
							VaccinationPreferences preferences = new VaccinationPreferences(
									MainActivity.this);
							preferences.setRemindDate(reserveTime);
							// 如果服务正在运行，重启服务
							if (PackageUtil.isServiceRunning(MainActivity.this,
									Constants.REMIND_SERVICE)) {
								stopService(new Intent(MainActivity.this,
										VaccinationRemindService.class));
							}
							startService(new Intent(MainActivity.this,
									VaccinationRemindService.class));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					mBar.setTitle(mMeunList[0]);
					mManager = getSupportFragmentManager();
					mTransaction = mManager.beginTransaction();
					Fragment fragment = null;
					if (getDefaultBabyAndCalculateCount()) {
						fragment = new MainTodayFragment();
					} else {
						// Fragment fragment = new VaccineListFragment();
						fragment = new MainFragment();
					}
					mTransaction.replace(R.id.content_frame, fragment);
					mTransaction.commit();
				}
				mDrawerLayout.closeDrawer(mDrawerLinearLayout);// 关闭DrawerLayout
			}
		});

		// 侧滑菜单选项
		mMeunList = getResources().getStringArray(R.array.menu_list);

		// 侧滑菜单 adapter
		// mSimpleAdapter = new SimpleAdapter(this, getData(),
		// R.layout.menu_gridview_item,
		// new String[] { "icon", "text" }, new int[] {
		// R.id.grid_menu_icon, R.id.grid_menu_name });
		// mMenuGridView.setAdapter(mSimpleAdapter);
		// mDrawerListView.setOnItemClickListener(new
		// DrawerItemClickListener());
		DrawerMenuAdapter menuAdapter = new DrawerMenuAdapter(this, getData());
		mMenuGridView.setAdapter(menuAdapter);
		mMenuGridView.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(this,
				mDrawerLayout, R.drawable.ic_launcher, R.string.open,
				R.string.close) {
			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}
		});

		mBar.setTitle(mMeunList[0]);
		mManager = getSupportFragmentManager();
		mTransaction = mManager.beginTransaction();
		Fragment fragment = null;
		if (getDefaultBabyAndCalculateCount()) {
			fragment = new MainTodayFragment();
		} else {
			// Fragment fragment = new VaccineListFragment();
			fragment = new MainFragment();
		}
		mTransaction.replace(R.id.content_frame, fragment);
		mTransaction.commit();
		
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
					mDProgressDialog = ProgressDialog.show(MainActivity.this,
							"", "正在获取最新版本信息...");
//					new Thread(new CheckVersionTask()).start();
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

	}

	/**
	 * 查询默认宝宝并计算距下次接种日期天数
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

	/**
	 * 侧滑菜单数据
	 * 
	 * @return
	 */
	private List<Map<String, Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < mMeunList.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("icon", ICONS[i]);
			map.put("text", mMeunList[i]);
			list.add(map);
		}
		return list;
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

	// ListView监听
	private class DrawerItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mBar.setTitle(mMeunList[position]);
			// FragmentManager manager = getSupportFragmentManager();
			mTransaction = mManager.beginTransaction();
			Fragment fragment = null;
			switch (position) {
			case 0:// 首页
				if (getDefaultBabyAndCalculateCount()) {
					fragment = new MainTodayFragment();
				} else {
					fragment = new MainFragment();
				}
				break;
			case 1:// 疫苗列表
				fragment = new VaccineListFragment();
				break;
			case 2:// 疫苗库
				fragment = new VaccineLibraryFragment();
				break;
			case 3:// 宝宝列表
				fragment = new BabyListFragment();
				break;
			case 4:// 疫苗资讯
				fragment = new VaccineNewsFragment();
				break;
			default:
				// fragment = new SummaryFragment();
				fragment = new VaccineListFragment();
				break;
			}
			mTransaction.replace(R.id.content_frame, fragment);
			mTransaction.commit();

			mDrawerLayout.closeDrawer(mDrawerLinearLayout);// 关闭DrawerLayout
		}

	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			if (mBar.getTitle().toString().equals(mMeunList[0])) {
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
				mBar.setTitle(mMeunList[0]);
				mManager = getSupportFragmentManager();
				mTransaction = mManager.beginTransaction();
				Fragment fragment = null;
				if (getDefaultBabyAndCalculateCount()) {
					fragment = new MainTodayFragment();
				} else {
					// Fragment fragment = new VaccineListFragment();
					fragment = new MainFragment();
				}
				mTransaction.replace(R.id.content_frame, fragment);
				mTransaction.commit();
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			// 在这里做你想做的事情
			// super.openOptionsMenu(); // 调用这个，就可以弹出菜单
			if (mPopMore.isShowing()) {
				mPopMore.dismiss();
			} else {
				mPopMore.showAtLocation(mDrawerLayout, Gravity.TOP
						| Gravity.RIGHT, 10, getActionBarHeight());
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Intent intent = null;
		switch (item.getItemId()) {

		case android.R.id.home:// 点击应用程序图标打开/关闭Menu
			if (mDrawerLayout.isDrawerOpen(mDrawerLinearLayout)) {
				mDrawerLayout.closeDrawer(mDrawerLinearLayout);
			} else {
				mDrawerLayout.openDrawer(mDrawerLinearLayout);
			}
			break;
		case R.id.action_mores:// 点击更多打开系统菜单
			mPopMore.showAtLocation(mDrawerLayout, Gravity.TOP | Gravity.RIGHT,
					10, getActionBarHeight());
			break;
		// case R.id.action_setting:// 设置
		// intent = new Intent(this, SettingActivity.class);
		// startActivity(intent);
		// break;
		// case R.id.action_add_baby:// 添加宝宝
		// intent = new Intent(this, RegisterBabyActivity.class);
		// startActivity(intent);
		// break;
		// case R.id.action_versions_update:// 版本更新
		// mDProgressDialog = ProgressDialog.show(this, "", "正在获取最新版本信息...");
		// new Thread(new CheckVersionTask()).start();
		// break;
		// case R.id.action_feedback:// 用户反馈
		// intent = new Intent(this, FeedBackActivity.class);
		// startActivity(intent);
		// break;
		// case R.id.action_about_us:// 关于我们
		// intent = new Intent(this, AboutUsActivity.class);
		// startActivity(intent);
		// break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 获取服务端版本号
	 * 
	 * @param xml
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static UpdataInfo getUpdataInfo(InputStream xml)
			throws XmlPullParserException, IOException {
		XmlPullParser pullParser = Xml.newPullParser();
		// 为Pull解析器设置要解析的XML数据
		pullParser.setInput(xml, "UTF-8");
		int type = pullParser.getEventType();
		UpdataInfo info = new UpdataInfo();// 实体
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("version".equals(pullParser.getName())) {
					info.setVersion(pullParser.nextText()); // 获取版本号
				} else if ("url".equals(pullParser.getName())) {
					info.setUrl(pullParser.nextText()); // 获取要升级的APK文件
				}
				break;
			}
			type = pullParser.next();
		}
		return info;
	}

	/**
	 * 检查新版本任务
	 * 
	 */
	public class CheckVersionTask implements Runnable {

		@Override
		public void run() {
			try {
				URL url = new URL(SERVER_PATH);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				InputStream xml = conn.getInputStream();
				mInfo = getUpdataInfo(xml);
				Log.i("MainActivity",
						"Info:" + mInfo.getUrl() + "--" + mInfo.getVersion());
				if (mInfo.getVersion().equals(
						PackageUtil.getVersionName(getApplicationContext()))) {
					Log.i("MainActivity", "版本号相同无需升级");
					Message msg = new Message();
					msg.what = UPDATA_NO;
					mHandler.sendMessage(msg);
				} else {
					Log.i("MainActivity", "版本号不同 ,提示用户升级 ");
					Message msg = new Message();
					msg.what = UPDATA_OK;
					mHandler.sendMessage(msg);
				}

			} catch (Exception e) {
				e.printStackTrace();
				Message msg = new Message();
				msg.what = GET_UNDATAINFO_ERROR;
				mHandler.sendMessage(msg);
			}
		}

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mDProgressDialog.dismiss();
			switch (msg.what) {
			case UPDATA_NO:
				Toast.makeText(getApplicationContext(), "您当前已是最新版本",
						Toast.LENGTH_SHORT).show();
				break;
			case UPDATA_OK:
				// 对话框通知用户升级程序
				showUpdataDialog();
				break;
			case GET_UNDATAINFO_ERROR:
				// 服务器超时
				Toast.makeText(getApplicationContext(), "获取服务器更新信息失败",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	/**
	 * 显示更新对话框
	 */
	protected void showUpdataDialog() {
		AlertDialog.Builder builer = new AlertDialog.Builder(this);
		builer.setTitle("软件更新");
		builer.setMessage("发现新版本，建议立即更新");
		// builer.setMessage(mInfo.getDescription());
		// 当点确定按钮时从服务器上下载 新的apk 然后安装
		builer.setPositiveButton("更新", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Log.i("MainActivity", "下载apk,更新");
				downLoadApk();
			}
		});
		// 当点取消按钮时进行登录
		builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builer.create();
		builer.show();
	}

	/**
	 * 下载APK
	 */
	private void downLoadApk() {
		// TODO 启动后台服务通过通知栏下载APK
		Intent intent = new Intent(getApplicationContext(), UpdateService.class);
		String packageName = PackageUtil
				.getPackageName(getApplicationContext());
		intent.putExtra("app_name", packageName);
		intent.putExtra("version", mInfo.getVersion());
		intent.putExtra("download_url", mInfo.getUrl());
		startService(intent);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(this);
		loader.setUri(BabyProvider.CONTENT_URI);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mBabys = new ArrayList<Baby>();
		while (data.moveToNext()) {
			Baby baby = mBabyDao.cursorToBaby(data);
			mBabys.add(baby);
		}
		if (mBabys.size() < 4) {
			Baby baby = new Baby();
			baby.setName("新宝宝");
			mBabys.add(baby);
		}
		mBabyAdapter = new DrawerBabyAdapter(this, mBabys);
		mBabyGridView.setAdapter(mBabyAdapter);
		mBabyAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}
	
	/** 版本检测 */
    private void checkVersion() {
        UmengUpdateAgent.setUpdateOnlyWifi(true);
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus,
                    UpdateResponse updateInfo) {
//                if (updateStatus == 0 && updateInfo != null) {
//                    showUpdateDialog(updateInfo.path, updateInfo.updateLog);
//                }
                switch (updateStatus) {
				case 0: // 有更新
					UmengUpdateAgent.showUpdateDialog(getApplicationContext(), updateInfo);
					//showUpdateDialog(updateInfo.path, updateInfo.updateLog);
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
                mDProgressDialog.dismiss();
            }
        });

        UmengUpdateAgent.update(this);
    }

    public void showUpdateDialog(final String downloadUrl, final String message) {
        AlertDialog.Builder updateAlertDialog = new AlertDialog.Builder(this);
        updateAlertDialog.setIcon(R.drawable.app_icon);
        updateAlertDialog.setTitle("软件更新");
        updateAlertDialog.setMessage(getString(R.string.update_hint, message));
        updateAlertDialog.setNegativeButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(downloadUrl)));
                        } catch (Exception ex) {

                        }
                    }
                }).setPositiveButton(R.string.cancel, null);
        if (!isFinishing())
            updateAlertDialog.show();
    }

}
