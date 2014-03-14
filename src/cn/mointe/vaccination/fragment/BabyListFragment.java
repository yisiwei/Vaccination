package cn.mointe.vaccination.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.adapter.BabyAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.domain.Baby;

/**
 * 宝宝列表界面
 * 
 * @author Livens
 */
public class BabyListFragment extends Fragment implements OnItemClickListener,
		OnItemLongClickListener {

	private ListView mBabyListView;
	private BabyAdapter mBabyAdapter;
	private List<Baby> mBabys;
	private ActionBar mBar;
	private View mView;
	private BabyDao mDao;

	private ImageButton mBtn;
	private Baby mBaby;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDao = new BabyDao(getActivity());

	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_baby_list, null);
		mBabyListView = (ListView) view.findViewById(R.id.lv_baby_list);

		mBabys = mDao.queryBabys();
		mBabyAdapter = new BabyAdapter(getActivity(), getActivity(), mBabys);
		mBabyListView.setAdapter(mBabyAdapter);
		mBabyListView.setOnItemClickListener(this);
		mBabyListView.setOnItemLongClickListener(this);

		mView = inflater.inflate(R.layout.baby_add_btn, null);
		mBtn = (ImageButton) mView.findViewById(R.id.img_add_baby);
		mBar = getActivity().getActionBar();
		mBar.setDisplayShowCustomEnabled(true);
		mBar.setCustomView(mView, new ActionBar.LayoutParams(80, 80,
				Gravity.RIGHT));

		mBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						RegisterBabyActivity.class);
				startActivity(intent);
			}
		});

		mBabyAdapter.notifyDataSetChanged();
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
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
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		final int index = position;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示");
		builder.setMessage("确定删除?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				boolean result = mDao.deleteBaby(mBabys.get(index));

				if (result) {
					Toast.makeText(getActivity(), "已删除", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(getActivity(), "操作失败", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

			}
		});
		builder.create();
		builder.show();
		return false;
	}

}
