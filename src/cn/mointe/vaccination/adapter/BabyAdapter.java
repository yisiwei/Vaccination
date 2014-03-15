package cn.mointe.vaccination.adapter;

import java.util.List;

import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.tools.BitmapUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
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
	private Context mContext;
	private int temp = -1;
	Activity activity;
	private BabyDao mDao;

	public BabyAdapter(Activity activity, Context context, List<Baby> babys) {
		this.activity = activity;
		this.mContext = context;
		this.mBabys = babys;
		mDao = new BabyDao(context);
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
		
		babyName.setText(baby.getName());
		babyAge.setText(baby.getBirthdate());

		String imgUri = baby.getImage();
		Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri, 96, 120);

		if (!TextUtils.isEmpty(imgUri)) {
			babyImg.setImageBitmap(bitmap);
		} else {
			babyImg.setImageResource(R.drawable.head);
		}

		radioButton.setId(position);
		radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Log.i("MainActivity", "isChecked==" + isChecked);
					if (temp != -1) {
						Log.i("MainActivity", "temp=1=" + temp);
						mDao.updateBabyIsDefault(baby.getId());
						RadioButton tempButton = (RadioButton) activity
								.findViewById(temp);
						if (tempButton != null) {
							tempButton.setChecked(false);
						}
					}
					temp = buttonView.getId();
					Log.i("MainActivity", "temp=2=" + temp);
				}
			}
		});

		if (position == temp) {
			radioButton.setChecked(true);
		} else {
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
