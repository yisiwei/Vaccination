package cn.mointe.vaccination.fragment;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.adapter.MyBabyAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.provider.BabyProvider;
import cn.mointe.vaccination.view.ListViewCompat;
import cn.mointe.vaccination.view.ListViewCompat.MessageItem;
import cn.mointe.vaccination.view.SlideView;
import cn.mointe.vaccination.view.SlideView.OnSlideListener;

/**
 * 宝宝列表界面
 * 
 * @author Livens
 */
public class BabyListFragment extends Fragment implements OnItemClickListener,
		OnSlideListener, OnItemLongClickListener, LoaderCallbacks<Cursor> {

	private ListViewCompat mBabyListView;
	private SlideView mLastSlideViewWithStatusOn;

	// private BabyAdapter mBabyAdapter;
	private MyBabyAdapter mMyBabyAdapter;
	private List<MessageItem> mMessageItems;

	private List<Baby> mBabys;
	private BabyDao mDao;

	private Baby mBaby;
	private LoaderManager mManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDao = new BabyDao(getActivity());
		mMessageItems = new ArrayList<MessageItem>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_baby_list, null);
		mBabyListView = (ListViewCompat) view.findViewById(R.id.lv_baby_list);

		mBabyListView.setOnItemClickListener(this);
		mBabyListView.setOnItemLongClickListener(this);

		mManager = getLoaderManager();
		mManager.initLoader(1000, null, this);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("BabyListFragment"); //统计页面
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("BabyListFragment"); 
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(getActivity(), RegisterBabyActivity.class);
		mBaby = mBabys.get(position);
		Bundle mBundle = new Bundle();
		mBundle.putSerializable("baby", mBaby);
		intent.putExtras(mBundle);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
//		final int index = position;
//		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//		builder.setTitle("提示");
//		builder.setMessage("确定要删除吗?");
//		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				String isDefault = mDao.checkIsDefault(mBabys.get(index));
//				if ("1".equals(isDefault)) {
//					PublicMethod.showToast(getActivity(), "默认宝宝不能删除");
//				} else {
//					boolean b = mDao.deleteBaby(mBabys.get(index));
//					if (b) {
//						PublicMethod.showToast(getActivity(),
//								R.string.delete_success);
//					} else {
//						PublicMethod.showToast(getActivity(),
//								R.string.delete_fail);
//					}
//				}
//			}
//		});
//		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//
//			}
//		});
//		builder.create();
//		builder.show();
		return false;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		CursorLoader loader = new CursorLoader(getActivity());
		loader.setUri(BabyProvider.CONTENT_URI);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mBabys = new ArrayList<Baby>();
		mMessageItems.clear();
		while (data.moveToNext()) {
			Baby baby = mDao.cursorToBaby(data);
			mBabys.add(baby);
			ListViewCompat.MessageItem item = new ListViewCompat.MessageItem();
			item.baby = baby;
			mMessageItems.add(item);
		}
		// mBabyAdapter = new BabyAdapter(getActivity(), getActivity(), mBabys);
		// mBabyListView.setAdapter(mBabyAdapter);
		// mBabyAdapter.notifyDataSetChanged();
		mMyBabyAdapter = new MyBabyAdapter(getActivity(), mMessageItems);
		mBabyListView.setAdapter(mMyBabyAdapter);
		mMyBabyAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> data) {

	}

	@Override
	public void onSlide(View view, int status) {
		if (mLastSlideViewWithStatusOn != null
				&& mLastSlideViewWithStatusOn != view) {
			mLastSlideViewWithStatusOn.shrink();
		}

		if (status == SLIDE_STATUS_ON) {
			mLastSlideViewWithStatusOn = (SlideView) view;
		}
	}

}
