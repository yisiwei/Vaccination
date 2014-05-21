package cn.mointe.vaccination.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

public class ReservationBeforeTodayActivity extends Activity {

	private String mDate;

	private TextView mTextView;
	private ListView mListView;

	private MyAdapter mAdapter;

	private VaccinationDao mVaccinationDao;
	private BabyDao mBabyDao;
	
	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation_before_today);

		mVaccinationDao = new VaccinationDao(this);
		mBabyDao = new BabyDao(this);
		
		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);
		
		mTitleText.setText(R.string.reserve_record);
		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReservationBeforeTodayActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		mTextView = (TextView) this
				.findViewById(R.id.reservation_before_today_hint);
		mListView = (ListView) this
				.findViewById(R.id.reservation_before_today_list);

		mDate = getIntent().getStringExtra("date");
		Log.i("MainActivity", "今天之前日期：" + mDate);

		if (getData() != null && getData().size() > 0) {
			mAdapter = new MyAdapter(this, getData());
			mListView.setAdapter(mAdapter);
		} else {
			mTextView.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 数据
	 * 
	 * @return
	 */
	private List<Vaccination> getData() {
		List<Vaccination> vaccinations = mVaccinationDao
				.getVaccinationByReservationDate(mBabyDao.getDefaultBaby()
						.getName(), mDate);

		return vaccinations;
	}

	/**
	 * Adapter
	 * 
	 */
	private class MyAdapter extends BaseAdapter {

		private List<Vaccination> vaccinations;
		private LayoutInflater mInflater;

		public MyAdapter(Context context, List<Vaccination> vaccinations) {
			this.vaccinations = vaccinations;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return vaccinations.size();
		}

		@Override
		public Object getItem(int position) {
			return vaccinations.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.reservation_before_today_item, null);

				holder = new ViewHolder();
				holder.button = (Button) convertView
						.findViewById(R.id.reservation_before_today_item_btn);
				holder.type = (TextView) convertView
						.findViewById(R.id.reservation_before_today_item_type);
				holder.name = (TextView) convertView
						.findViewById(R.id.reservation_before_today_item_name);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Vaccination vaccination = vaccinations.get(position);

			holder.type.setText(vaccination.getVaccine_type());
			holder.name.setText(vaccination.getVaccine_name() + "("
					+ vaccination.getVaccination_number() + ")");
			if (!StringUtils.isNullOrEmpty(vaccination.getFinish_time())) {
				holder.button.setText("已接种");
				holder.button.setTextColor(Color.BLUE);
			} else {
				holder.button.setText("未接种");
				holder.button.setTextColor(Color.RED);
			}

			return convertView;
		}

		public class ViewHolder {
			public Button button;
			public TextView type;
			public TextView name;
		}

	}

}
