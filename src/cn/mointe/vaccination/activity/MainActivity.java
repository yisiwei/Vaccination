package cn.mointe.vaccination.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.fragment.BabyListFragment;
import cn.mointe.vaccination.fragment.VaccineDiaryFragment;
import cn.mointe.vaccination.fragment.VaccineLibraryFragment;
import cn.mointe.vaccination.fragment.VaccineListFragment;
import cn.mointe.vaccination.fragment.VaccineNewsFragment;

/**
 * 主Activity
 * 
 * @author yi_siwei
 * 
 */
public class MainActivity extends FragmentActivity {

	private ActionBar mBar;

	private ListView mDrawerListView;
	private DrawerLayout mDrawerLayout;
	private String[] mMeunList;// 侧滑菜单列表

	private long mTouchTime = 0;
	private long mWaitTime = 2000;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mBar = getActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView) this.findViewById(R.id.left_drawer);

		mMeunList = getResources().getStringArray(R.array.menu_list);

		mDrawerListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mMeunList));

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

		setTitle(mMeunList[0]);
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Fragment fragment = new VaccineListFragment();
		transaction.add(R.id.content_frame, fragment);
		transaction.commit();

	}

	// ListView监听
	private class DrawerItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			setTitle(mMeunList[position]);
			FragmentManager manager = getSupportFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			Fragment fragment = null;
			switch (position) {
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
			case 4:// 接种日记
				fragment = new VaccineDiaryFragment();
				break;
			default:
				break;
			}
			transaction.replace(R.id.content_frame, fragment);
			transaction.commit();

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
			long currentTime = System.currentTimeMillis();
			if ((currentTime - mTouchTime) > mWaitTime) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				mTouchTime = currentTime;
			} else {
				this.finish();
				// System.exit(0);
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			// 在这里做你想做的事情
			super.openOptionsMenu(); // 调用这个，就可以弹出菜单
			
			return true;
		}

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
		case R.id.action_add_baby:
			intent = new Intent(this, RegisterBabyActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}
