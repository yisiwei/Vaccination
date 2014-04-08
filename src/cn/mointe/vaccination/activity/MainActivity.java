package cn.mointe.vaccination.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

	private static final int[] ICONS = { R.drawable.vac_list,
			R.drawable.vac_lib, R.drawable.babies, R.drawable.vac_news,
			R.drawable.vac_note };

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
			//FragmentManager manager = getSupportFragmentManager();
			mTransaction = mManager.beginTransaction();
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
			}else{
				mBar.setTitle(mMeunList[0]);
				mTransaction = mManager.beginTransaction();
				Fragment fragment = new VaccineListFragment();
				mTransaction.replace(R.id.content_frame, fragment);
				mTransaction.commit();
			}
			return true;
		}
//		if (keyCode == KeyEvent.KEYCODE_MENU) {
//			// 在这里做你想做的事情
//			super.openOptionsMenu(); // 调用这个，就可以弹出菜单
//
//			return true;
//		}

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
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

}
