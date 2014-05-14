package cn.mointe.vaccination.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.VaccinationCategory;
import cn.mointe.vaccination.tools.StringUtils;

public class VaccinationCategoryAdapter extends BaseAdapter {

	private static final int TYPE_CATEGORY_ITEM = 0;
	private static final int TYPE_ITEM = 1;

	private List<VaccinationCategory> mListData;
	private LayoutInflater mInflater;

	private Context mContext;

	public VaccinationCategoryAdapter(Context context,
			List<VaccinationCategory> data) {
		this.mContext = context;
		this.mListData = data;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {

		int count = 0;
		if (null != mListData) {
			// 所有分类中item的总和是ListVIew Item的总个数
			for (VaccinationCategory category : mListData) {
				count += category.getItemCount();
			}
		}

		return count;
	}

	@Override
	public Object getItem(int position) {
		if (null == mListData || position < 0 || position > getCount()) {
			return null;
		}
		// 同一分类内，第一个元素的索引值
		int categoryFirstIndex = 0;
		for (VaccinationCategory category : mListData) {
			int size = category.getItemCount();
			// 在当前分类中的索引值
			int categoryIndex = position - categoryFirstIndex;
			// item在当前分类内
			if (categoryIndex < size) {
				return category.getItem(categoryIndex);
			}
			// 索引移动到当前分类结尾，即下一个分类第一个元素索引
			categoryFirstIndex += size;
		}
		return null;
	}

	@Override
	public int getItemViewType(int position) {
		if (null == mListData || position < 0 || position > getCount()) {
			return TYPE_ITEM;
		}
		int categoryFirstIndex = 0;
		for (VaccinationCategory category : mListData) {
			int size = category.getItemCount();
			// 在当前分类中的索引值
			int categoryIndex = position - categoryFirstIndex;
			if (categoryIndex == 0) {
				return TYPE_CATEGORY_ITEM;
			}
			categoryFirstIndex += size;
		}

		return TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int itemViewType = getItemViewType(position);
		switch (itemViewType) {
		case TYPE_CATEGORY_ITEM:
			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.listview_item_header,
						null);
			}
			TextView textView = (TextView) convertView
					.findViewById(R.id.header);
			String itemValue = (String) getItem(position);
			textView.setText(itemValue);

			break;

		case TYPE_ITEM:
			ViewHolder viewHolder = null;
			if (null == convertView) {
				convertView = mInflater
						.inflate(R.layout.vaccination_item, null);
				viewHolder = new ViewHolder();
				viewHolder.vaccineDate = (TextView) convertView
						.findViewById(R.id.vaccination_item_tv_date);
				viewHolder.vaccineName = (TextView) convertView
						.findViewById(R.id.vaccination_item_tv_name);
				viewHolder.isHave = (TextView) convertView
						.findViewById(R.id.vaccination_item_tv_ishave);
				viewHolder.vaccineType = (TextView) convertView
						.findViewById(R.id.vaccination_item_tv_type);
				viewHolder.chargeStandard = (TextView) convertView
						.findViewById(R.id.vaccination_item_tv_is_charge);
				viewHolder.finishTime = (TextView) convertView
						.findViewById(R.id.vaccination_item_tv_finish_time);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Vaccination vaccination = (Vaccination) getItem(position);
			viewHolder.vaccineDate.setText(vaccination.getReserve_time());
			viewHolder.vaccineName.setText(vaccination.getVaccine_name() + "("
					+ vaccination.getVaccination_number() + ")");
			viewHolder.vaccineType.setText(vaccination.getVaccine_type());

			if (vaccination.getCharge_standard().equals("收费")) {
				viewHolder.chargeStandard.setTextColor(mContext.getResources()
						.getColor(R.color.tuijian));
			} else {
				viewHolder.chargeStandard.setTextColor(mContext.getResources()
						.getColor(R.color.mianfei));
			}

			// 将必打/推荐/可选改为一类/二类
			if (vaccination.getVaccine_type().equals("一类")) {
				viewHolder.vaccineType.setTextColor(Color.WHITE);
				viewHolder.vaccineType
						.setBackgroundResource(R.drawable.textview_vac_type1);
			} else if (vaccination.getVaccine_type().equals("二类")) {
				viewHolder.vaccineType.setTextColor(Color.WHITE);
				viewHolder.vaccineType
						.setBackgroundResource(R.drawable.textview_vac_type2);
			}
			viewHolder.chargeStandard.setText(vaccination.getCharge_standard());

			if (!StringUtils.isNullOrEmpty(vaccination.getFinish_time())) {
				viewHolder.finishTime.setText(vaccination.getFinish_time());
				viewHolder.isHave.setTextColor(Color.BLUE);
				viewHolder.isHave.setText("已接种");
			} else if (!StringUtils
					.isNullOrEmpty(vaccination.getReserve_time())
					&& StringUtils.isNullOrEmpty(vaccination.getFinish_time())) {
				viewHolder.finishTime.setText("");
				viewHolder.isHave.setTextColor(Color.WHITE);
				viewHolder.isHave.setText("未接种");
			} else {
				viewHolder.finishTime.setText("");
				viewHolder.isHave.setTextColor(Color.RED);
				viewHolder.isHave.setText("未预约");
			}

			break;
		}

		return convertView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return getItemViewType(position) != TYPE_CATEGORY_ITEM;
	}

	private class ViewHolder {
		public TextView vaccineDate;
		public TextView vaccineName;
		public TextView isHave;
		public TextView vaccineType;
		public TextView chargeStandard;
		public TextView finishTime;
	}

}
