package cn.mointe.vaccination.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.Vaccination;

public class ChooseAdapter extends BaseAdapter {

	private List<Vaccination> mVaccinations;
	private LayoutInflater mInflater;
	private OnSelectedItemChanged mCallBack;// 内部接口:监听选中项的个数（随着item被点击而改变）

	private Map<Integer, Boolean> mMap;// 保存被选中与否的状态的集合
	private List<Vaccination> mSelectedItems;// 被选中项的集合

	@SuppressLint("UseSparseArrays")
	public ChooseAdapter(Context context, List<Vaccination> vaccinations,
			OnSelectedItemChanged callBack) {
		mInflater = LayoutInflater.from(context);
		this.mVaccinations = vaccinations;
		this.mCallBack = callBack;

		// 初始化被选中项
		mMap = new HashMap<Integer, Boolean>();
		for (int i = 0; i < mVaccinations.size(); i++) {
			mMap.put(i, false);
		}
		// 初始化被选中项的集合
		mSelectedItems = new ArrayList<Vaccination>();
	}

	@Override
	public int getCount() {
		return mVaccinations.size();
	}

	@Override
	public Object getItem(int position) {
		return mVaccinations.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.choose_list_item, null);

			holder = new ViewHolder();
			holder.vaccineName = (TextView) convertView
					.findViewById(R.id.choose_vac_name);
			holder.checkBox = (CheckBox) convertView
					.findViewById(R.id.is_checked);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final Vaccination vaccination = mVaccinations.get(position);
		final CheckBox cb = holder.checkBox;

		holder.vaccineName.setText(vaccination.getVaccine_name() + "("
				+ vaccination.getVaccination_number() + ")");
		cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb.isChecked()) {
					mMap.put(position, true);
					mCallBack.getSelectedCount(getSelectedCount());
					mCallBack.getSelectedItem(vaccination);
				} else {
					if (mMap.containsKey(position)) {
						if (mMap.get(position) == true) {
							mMap.put(position, false);
							mCallBack.getSelectedCount(getSelectedCount());
						}
					}
				}
			}
		});
		cb.setChecked(mMap.get(position));

		return convertView;
	}

	/**
	 * 被选中项的个数
	 * 
	 * @return
	 */
	private int getSelectedCount() {
		int i = 0;
		for (Entry<Integer, Boolean> entry : mMap.entrySet()) {
			if (entry.getValue())
				i++;
		}
		return i;
	}

	/**
	 * 内部监听接口：向Activity暴露选择了多少项
	 */
	public interface OnSelectedItemChanged {
		/**
		 * 回调：处理被选中项个数
		 * 
		 * @param count
		 */
		public void getSelectedCount(int count);

		public void getSelectedItem(Vaccination vaccination);

	}

	/**
	 * 全选:改变状态+更新item界面+个数监听
	 */
	public void selectAll() {
		for (int i = 0; i < mMap.size(); i++) {
			mMap.put(i, true);
		}
		notifyDataSetChanged();
		mCallBack.getSelectedCount(getSelectedCount());
	}

	/**
	 * 全不选：改变状态+更新item界面+个数监听
	 */
	public void disSelectAll() {
		for (int i = 0; i < mMap.size(); i++) {
			mMap.put(i, false);
		}
		notifyDataSetChanged();
		mCallBack.getSelectedCount(getSelectedCount());
	}

	/**
	 * 当前被选中项
	 * 
	 * @return
	 */
	public List<Vaccination> currentSelect() {
		mSelectedItems.clear();
		for (int i = 0; i < mMap.size(); i++) {
			if (mMap.get(i))
				this.mSelectedItems.add(mVaccinations.get(i));
		}
		return this.mSelectedItems;
	}

	public class ViewHolder {
		public TextView vaccineName;
		public CheckBox checkBox;
	}

}
