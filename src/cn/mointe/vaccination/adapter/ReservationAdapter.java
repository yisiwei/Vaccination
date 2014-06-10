package cn.mointe.vaccination.adapter;

import java.util.List;

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
import cn.mointe.vaccination.tools.StringUtils;

public class ReservationAdapter extends BaseAdapter {

	private List<Vaccination> mVaccinations;
	private LayoutInflater mInflater;
	private OnSelectedItemChanged mCallBack;// 内部接口:监听是否选择了

	public ReservationAdapter(Context context, List<Vaccination> vaccinations,
			OnSelectedItemChanged callBack) {
		mInflater = LayoutInflater.from(context);
		this.mVaccinations = vaccinations;
		this.mCallBack = callBack;
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

		if (!StringUtils.isNullOrEmpty(vaccination.getReserve_time())) {
			cb.setChecked(true);
		} else {
			cb.setChecked(false);
		}

		cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb.isChecked()) {
					mCallBack.getSelectedItem(vaccination);
				} else {
					mCallBack.getUnSelectedItem(vaccination);
				}
			}
		});

		return convertView;
	}
	
	/**
	 * 内部监听接口
	 */
	public interface OnSelectedItemChanged {

		public void getSelectedItem(Vaccination vaccination);

		public void getUnSelectedItem(Vaccination vaccination);
		
	}

	public class ViewHolder {
		public TextView vaccineName;
		public CheckBox checkBox;
	}

}
