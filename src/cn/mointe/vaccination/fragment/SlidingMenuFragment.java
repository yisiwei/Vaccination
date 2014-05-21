package cn.mointe.vaccination.fragment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.MainActivity;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.activity.ReservationCalendarActivity;
import cn.mointe.vaccination.adapter.DrawerBabyAdapter;
import cn.mointe.vaccination.adapter.DrawerMenuAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.provider.BabyProvider;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.PackageUtil;

public class SlidingMenuFragment extends Fragment{

	// 侧滑菜单列表
	private GridView mMenuGridView;
	private String[] mMeunList;

	// 侧滑宝宝列表
	private List<Baby> mBabys;
	private Baby mBaby;
	private BabyDao mBabyDao;
	private GridView mBabyGridView;
	private DrawerBabyAdapter mBabyAdapter;
	private VaccinationDao mVaccinationDao;

	private LoaderManager mBabyLoaderManager;

	private FragmentManager mManager;
	private FragmentTransaction mTransaction;
	
	private ITitleChangeListener mTitleChangeListener;
	
	private static final int[] ICONS = { R.drawable.home, R.drawable.vac_list,
			R.drawable.vac_lib, R.drawable.babies, R.drawable.vac_news,R.drawable.vac_note,R.drawable.vac_pretime };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBabyLoaderManager = getLoaderManager();
		mBabyDao = new BabyDao(getActivity());
		mVaccinationDao = new VaccinationDao(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.sliding_menu, null);

		// 侧滑菜单选项
		mMeunList = getResources().getStringArray(R.array.menu_list);

		mMenuGridView = (GridView) view.findViewById(R.id.left_menu_gridview);
		mBabyGridView = (GridView) view.findViewById(R.id.left_baby_gridview);

		mBabyGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mBaby = (Baby) mBabyAdapter.getItem(position);
				// Toast.makeText(getApplicationContext(), "" + mBaby.getName(),
				// Toast.LENGTH_SHORT).show();
				if (mBaby.getName().equals("新宝宝")) {
					// TODO 跳转到添加宝宝界面
					startActivity(new Intent(getActivity(),
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
									getActivity());
							preferences.setRemindDate(reserveTime);
							// 如果服务正在运行，重启服务
							if (PackageUtil.isServiceRunning(getActivity(),
									Constants.REMIND_SERVICE)) {
								getActivity()
										.stopService(
												new Intent(
														getActivity(),
														VaccinationRemindService.class));
							}
							getActivity().startService(
									new Intent(getActivity(),
											VaccinationRemindService.class));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					mTitleChangeListener.setTitle(mMeunList[0]);
					mManager = getFragmentManager();
					mTransaction = mManager.beginTransaction();
					Fragment fragment = null;
					if (getDefaultBabyAndCalculateCount()) {
						fragment = new MainTodayFragment();
					} else {
						fragment = new MainFragment();
					}
					mTransaction.replace(R.id.main_content, fragment);
					mTransaction.commit();
				}
				((MainActivity) getActivity()).getSlidingMenu().toggle();
			}
		});
		mBabyLoaderManager.initLoader(1, null, mBabyLoaderCallBacks);

		DrawerMenuAdapter menuAdapter = new DrawerMenuAdapter(getActivity(),
				getData());
		mMenuGridView.setAdapter(menuAdapter);
		mMenuGridView.setOnItemClickListener(new DrawerItemClickListener());

		mTitleChangeListener.setTitle(mMeunList[0]);
		mManager = getFragmentManager();
		mTransaction = mManager.beginTransaction();
		Fragment fragment = null;
		if (getDefaultBabyAndCalculateCount()) {
			fragment = new MainTodayFragment();
		} else {
			fragment = new MainFragment();
		}
		mTransaction.replace(R.id.main_content, fragment);
		mTransaction.commit();
		((MainActivity) getActivity()).getSlidingMenu().toggle();

		return view;
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
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("icon", ICONS[1]);
//		map.put("text", "预约");
//		list.add(map);
		return list;
	}

	private class DrawerItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 6) {
				startActivity(new Intent(getActivity(), ReservationCalendarActivity.class));
			}else{
			mTitleChangeListener.setTitle(mMeunList[position]);
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
			case 5:
				fragment = new VaccineDiaryFragment();
				break;
			default:
				fragment = new VaccineListFragment();
				break;
			}
			mTransaction.replace(R.id.main_content, fragment);
			mTransaction.commit();
			}
			((MainActivity) getActivity()).getSlidingMenu().toggle();
		}

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

	/**
	 * 查询默认宝宝
	 */
	private LoaderCallbacks<Cursor> mBabyLoaderCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(getActivity());
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
			mBabyAdapter = new DrawerBabyAdapter(getActivity(), mBabys);
			mBabyGridView.setAdapter(mBabyAdapter);
			mBabyAdapter.notifyDataSetChanged();
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};
	
	@Override
	public void onAttach(Activity activity) {
		try {         
			mTitleChangeListener = (ITitleChangeListener) activity;  
        } catch (Exception e) {  
            throw new ClassCastException(activity.toString() + "must implement ITitleChangeListener");  
        }  
          
        super.onAttach(activity);  
	}


	
}
