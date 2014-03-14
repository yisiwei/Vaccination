package cn.mointe.vaccination.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.Vaccination;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class VaccinationAdapter extends BaseAdapter {

	private Vaccination mVaccination;
	private List<Vaccination> mVaccinationList;
	private Context mContext;

	public VaccinationAdapter(Context context, List<Vaccination> vaccinationList) {
		this.mContext = context;
		this.mVaccinationList = vaccinationList;
	}

	@Override
	public int getCount() {
		return mVaccinationList.size();
	}

	@Override
	public Object getItem(int position) {
		return mVaccinationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "NewApi", "SimpleDateFormat" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView vaccine_date = null;
		TextView vaccine_age = null;
		TextView vaccine_age2 = null;
		TextView vaccine_name = null;
		Button isHave = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.vaccination_item, null);

			vaccine_date = (TextView) convertView
					.findViewById(R.id.vaccination_item_tv_date);
			vaccine_age = (TextView) convertView
					.findViewById(R.id.vaccination_item_tv_age);
			vaccine_age2 = (TextView) convertView
					.findViewById(R.id.vaccination_item_tv_age2);
			vaccine_name = (TextView) convertView
					.findViewById(R.id.vaccination_item_tv_name);
			isHave = (Button) convertView
					.findViewById(R.id.vaccination_item_btn_ishave);

			ViewCache cache = new ViewCache();

			cache.vaccine_date = vaccine_date;
			cache.vaccine_age = vaccine_age;
			cache.vaccine_age2 = vaccine_age2;
			cache.vaccine_name = vaccine_name;
			cache.isHave = isHave;

			convertView.setTag(cache);
		} else {
			ViewCache cache = (ViewCache) convertView.getTag();
			vaccine_date = cache.vaccine_date;
			vaccine_age = cache.vaccine_age;
			vaccine_age2 = cache.vaccine_age2;
			vaccine_name = cache.vaccine_name;
			isHave = cache.isHave;
		}
		mVaccination = (Vaccination) this.mVaccinationList.get(position);

		vaccine_date.setText(mVaccination.getReserve_time());
		vaccine_age.setText("(" + mVaccination.getMoon_age() + ")");
		vaccine_age2.setText(mVaccination.getMoon_age());
		vaccine_name.setText(mVaccination.getVaccine_name());

		try {
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date reserveDate = format.parse(mVaccination.getReserve_time());// 转换为时间
			int result = date.compareTo(reserveDate);
			if (result > 0) {
				isHave.setTextColor(Color.RED);
				isHave.setText("已过期");
			} else {
				if (null != mVaccination.getFinish_time()
						&& !"".equalsIgnoreCase(mVaccination.getFinish_time())) {
					isHave.setText("已接种");
					isHave.setTextColor(Color.BLUE);
				} else {
					isHave.setText("未接种");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return convertView;
	}

	private final class ViewCache {
		public TextView vaccine_date;
		public TextView vaccine_age;
		public TextView vaccine_age2;
		public TextView vaccine_name;
		public Button isHave;
	}
}
