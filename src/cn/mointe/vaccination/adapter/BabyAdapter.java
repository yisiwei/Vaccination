package cn.mointe.vaccination.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.view.CircleImageView;

public class BabyAdapter extends BaseAdapter {

	private List<Baby> mBabys;
	private Context mContext;
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
		ImageButton babyDefaultImgBtn = null;
		CircleImageView babyImg = null;
		TextView babyName = null;
		TextView babyAge = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.baby_list_item, null);
			babyDefaultImgBtn = (ImageButton) convertView
					.findViewById(R.id.radioButton);
			babyImg = (CircleImageView) convertView.findViewById(R.id.baby_img);
			babyName = (TextView) convertView.findViewById(R.id.baby_name);
			babyAge = (TextView) convertView.findViewById(R.id.baby_age);

			ViewCache cache = new ViewCache();
			cache.babyDefaultImgBtn = babyDefaultImgBtn;
			cache.babyImg = babyImg;
			cache.babyName = babyName;
			cache.babyAge = babyAge;

			convertView.setTag(cache);

		} else {
			ViewCache cache = (ViewCache) convertView.getTag();
			babyDefaultImgBtn = cache.babyDefaultImgBtn;
			babyImg = cache.babyImg;
			babyName = cache.babyName;
			babyAge = cache.babyAge;
		}

		final Baby baby = mBabys.get(position);

		babyName.setText(baby.getName());
		String birthdate = baby.getBirthdate();
		String moon_age = null;
		try {
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
					Locale.getDefault());
			String dateString = format.format(date);
			Date today = format.parse(dateString);
			long month_number = DateUtils.getMonth(birthdate, today);
			if (month_number < 12) {
				moon_age = month_number + "月龄";
			} else if (month_number == 12) {
				moon_age = "1周岁";
			} else if (month_number > 12 && month_number < 24) {
				moon_age = month_number + "月龄";
			} else if (month_number >= 24 && month_number < 36) {
				moon_age = "2周岁";
			} else if (month_number >= 36 && month_number < 48) {
				moon_age = "3周岁";
			} else if (month_number >= 48 && month_number < 60) {
				moon_age = "4周岁";
			} else if (month_number >= 60 && month_number < 72) {
				moon_age = "5周岁";
			} else if (month_number >= 72) {
				moon_age = "6周岁";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		babyAge.setText(baby.getBirthdate() + "(" + moon_age + ")");
		String imgUri = baby.getImage();

		if (!TextUtils.isEmpty(imgUri)) {
			Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri, 100,
					100);
			babyImg.setImageBitmap(bitmap);
		} else {
			babyImg.setImageResource(R.drawable.default_img);
		}

		if ("1".equals(baby.getIs_default())) {
			babyDefaultImgBtn
					.setImageResource(R.drawable.round_selector_checked);
		} else {
			babyDefaultImgBtn
					.setImageResource(R.drawable.round_selector_normal);
		}

		babyDefaultImgBtn.setOnClickListener(new BabyBtnOnClickListener(baby,
				babyDefaultImgBtn));

		return convertView;
	}

	/**
	 * 按钮监听
	 * 
	 */
	private class BabyBtnOnClickListener implements OnClickListener {

		private Baby baby;
		private ImageButton babyDefaultImgBtn;

		public BabyBtnOnClickListener(Baby baby, ImageButton babyDefaultImgBtn) {
			this.baby = baby;
			this.babyDefaultImgBtn = babyDefaultImgBtn;
		}

		@Override
		public void onClick(View v) {
			if (!"1".equals(baby.getIs_default())) {
				Baby defaultBaby = mDao.getDefaultBaby();
				mDao.updateBabyIsDefault(defaultBaby);// 将默认的修改为非默认的
				mDao.updateBabyIsDefault(baby);// 将选中的修改为默认的
				babyDefaultImgBtn
						.setImageResource(R.drawable.round_selector_checked);
			}
		}

	}

	private final class ViewCache {
		public ImageButton babyDefaultImgBtn;
		public CircleImageView babyImg;
		public TextView babyName;
		public TextView babyAge;
	}
}
