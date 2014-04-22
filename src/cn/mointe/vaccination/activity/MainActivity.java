package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.UpdataInfo;
import cn.mointe.vaccination.fragment.BabyListFragment;
import cn.mointe.vaccination.fragment.VaccineLibraryFragment;
import cn.mointe.vaccination.fragment.VaccineListFragment;
import cn.mointe.vaccination.fragment.VaccineNewsFragment;
import cn.mointe.vaccination.service.UpdateService;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PackageUtil;

/**
 * 主Activity
 * 
 * @author yi_siwei
 * 
 */
public class MainActivity extends ActionBarActivity {

	private ActionBar mBar;

	private ListView mDrawerListView;
	private DrawerLayout mDrawerLayout;
	private String[] mMeunList;// 侧滑菜单列表

	private FragmentManager mManager;
	private FragmentTransaction mTransaction;

	private long mTouchTime = 0;
	private long mWaitTime = 2000;
	private SimpleAdapter mSimpleAdapter;

	// R.drawable.vac_summary,R.drawable.vac_note
	private static final int[] ICONS = { R.drawable.vac_list,
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

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView) this.findViewById(R.id.left_drawer);

		mMeunList = getResources().getStringArray(R.array.menu_list);

		mSimpleAdapter = new SimpleAdapter(this, getData(),
				R.layout.main_drawerlayout_item,
				new String[] { "icon", "text" }, new int[] {
						R.id.main_list_item_img, R.id.main_list_item_text });
		mDrawerListView.setAdapter(mSimpleAdapter);
		// mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, mMeunList));

		mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());

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
		Fragment fragment = new VaccineListFragment();
		// Fragment fragment = new SummaryFragment();
		mTransaction.replace(R.id.content_frame, fragment);
		mTransaction.commit();

	}

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
			// case 0:// 首页
			// fragment = new SummaryFragment();
			// break;
			case 0:// 疫苗列表
				fragment = new VaccineListFragment();
				break;
			case 1:// 疫苗库
				fragment = new VaccineLibraryFragment();
				break;
			case 2:// 宝宝列表
				fragment = new BabyListFragment();
				break;
			case 3:// 疫苗资讯
				fragment = new VaccineNewsFragment();
				break;
			// case 5:// 接种日记
			// fragment = new VaccineDiaryFragment();
			// break;
			default:
				// fragment = new SummaryFragment();
				fragment = new VaccineListFragment();
				break;
			}
			mTransaction.replace(R.id.content_frame, fragment);
			mTransaction.commit();

			mDrawerLayout.closeDrawer(mDrawerListView);// 关闭DrawerLayout
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
				mTransaction = mManager.beginTransaction();
				// Fragment fragment = new SummaryFragment();
				Fragment fragment = new VaccineListFragment();
				mTransaction.replace(R.id.content_frame, fragment);
				mTransaction.commit();
			}
			return true;
		}
		// if (keyCode == KeyEvent.KEYCODE_MENU) {
		// // 在这里做你想做的事情
		// super.openOptionsMenu(); // 调用这个，就可以弹出菜单
		//
		// return true;
		// }

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {

		case android.R.id.home:// 点击应用程序图标打开/关闭Menu
			if (mDrawerLayout.isDrawerOpen(mDrawerListView)) {
				mDrawerLayout.closeDrawer(mDrawerListView);
			} else {
				mDrawerLayout.openDrawer(mDrawerListView);
			}
			break;
		case R.id.action_setting:// 设置
			intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.action_add_baby:// 添加宝宝
			intent = new Intent(this, RegisterBabyActivity.class);
			startActivity(intent);
			break;
		case R.id.action_versions_update:// 版本更新
			mDProgressDialog = ProgressDialog.show(this, "", "正在获取最新版本信息...");
			new Thread(new CheckVersionTask()).start();
			break;
		case R.id.action_feedback:// 用户反馈
			intent = new Intent(this, FeedBackActivity.class);
			startActivity(intent);
			break;
		case R.id.action_about_us:// 关于我们
			intent = new Intent(this, AboutUsActivity.class);
			startActivity(intent);
			break;
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

}
