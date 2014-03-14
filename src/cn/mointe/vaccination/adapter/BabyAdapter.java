package cn.mointe.vaccination.adapter;

import java.util.List;

import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.domain.Baby;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

public class BabyAdapter extends BaseAdapter {

	private List<Baby> mBabys;
	// private LayoutInflater mInflater;// 布局填充器，使用XML文件生成布局对象
	private Context mContext;
	private int temp = -1;
	Activity activity;
	private BabyDao mDao;

	public BabyAdapter(Activity activity, Context context, List<Baby> babys) {
		this.activity = activity;
		this.mContext = context;
		this.mBabys = babys;
		mDao = new BabyDao(context);
		// mInflater = (LayoutInflater) context
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mBabys.size();
	}

	@Override
	public Object getItem(int position) {
		return mBabys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void select(int position) {

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RadioButton radioButton = null;
		ImageView babyImg = null;
		TextView babyName = null;
		TextView babyAge = null;

		if (convertView == null) {
			// convertView = mInflater.inflate(R.layout.baby_list_item, null);
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.baby_list_item, null);
			radioButton = (RadioButton) convertView
					.findViewById(R.id.radioButton);
			babyImg = (ImageView) convertView.findViewById(R.id.baby_img);
			babyName = (TextView) convertView.findViewById(R.id.baby_name);
			babyAge = (TextView) convertView.findViewById(R.id.baby_age);

			ViewCache cache = new ViewCache();
			cache.radioButton = radioButton;
			cache.babyImg = babyImg;
			cache.babyName = babyName;
			cache.babyAge = babyAge;

			convertView.setTag(cache);

		} else {
			ViewCache cache = (ViewCache) convertView.getTag();
			radioButton = cache.radioButton;
			babyImg = cache.babyImg;
			babyName = cache.babyName;
			babyAge = cache.babyAge;
		}

		final Baby baby = mBabys.get(position);

		babyImg.setBackgroundResource(R.drawable.ic_launcher);
		babyName.setText(baby.getName());
		babyAge.setText(baby.getBirthdate());

		radioButton.setId(position);
		radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.i("MainActivity", "isChecked==" + isChecked);
					if (temp != -1) {
						Log.i("MainActivity", "temp=1=" + temp);
						Baby oldbaby = new Baby();
						oldbaby.setId(baby.getId());
						oldbaby.setIs_default("0");
						//mDao.updateBaby(oldbaby);
						RadioButton tempButton = (RadioButton) activity
								.findViewById(temp);
						if (tempButton != null) {
							tempButton.setChecked(false);
						}
					}
					temp = buttonView.getId();
					Log.i("MainActivity", "temp=2=" + temp);
					Baby newbaby = new Baby();
					newbaby.setId(baby.getId());
					newbaby.setIs_default("1");
					//mDao.updateBaby(newbaby);
				}
			}
		});

		if (position == temp) {
			Log.i("MainActivity", "11111=" + temp);
			radioButton.setChecked(true);
		} else {
			Log.i("MainActivity", "22222=" + temp);
			radioButton.setChecked(false);
		}

		if ("1".equals(baby.getIs_default())) {
			radioButton.setChecked(true);
		}

		return convertView;
	}

	private final class ViewCache {
		public RadioButton radioButton;
		public ImageView babyImg;
		public TextView babyName;
		public TextView babyAge;
	}
}
