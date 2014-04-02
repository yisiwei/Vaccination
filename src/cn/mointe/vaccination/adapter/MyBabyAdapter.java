package cn.mointe.vaccination.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
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
import cn.mointe.vaccination.tools.PublicMethod;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.CircleImageView;
import cn.mointe.vaccination.view.ListViewCompat.MessageItem;
import cn.mointe.vaccination.view.SlideView;
import cn.mointe.vaccination.view.SlideView.OnSlideListener;

public class MyBabyAdapter extends BaseAdapter {

	private List<MessageItem> mMessageItems;
	private Context mContext;
	private BabyDao mDao;
	private SlideView mLastSlideViewWithStatusOn;

	public MyBabyAdapter(Context context, List<MessageItem> messageItems) {
		this.mContext = context;
		this.mMessageItems = messageItems;
		mDao = new BabyDao(context);
	}

	@Override
	public int getCount() {
		return mMessageItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessageItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void select(int position) {

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		SlideView slideView = (SlideView) convertView;
		if (slideView == null) {
			View itemView = LayoutInflater.from(this.mContext).inflate(
					R.layout.baby_list_item, null);

			slideView = new SlideView(mContext);
			slideView.setContentView(itemView);

			holder = new ViewHolder(slideView);
			slideView.setOnSlideListener(new OnSlideListener() {

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
			});
			slideView.setTag(holder);
		} else {
			holder = (ViewHolder) slideView.getTag();
		}
		MessageItem item = mMessageItems.get(position);
		item.slideView = slideView;
		item.slideView.shrink();

		final Baby baby = mMessageItems.get(position).baby;
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

		holder.babyName.setText(baby.getName());
		holder.babyAge.setText(baby.getBirthdate() + "(" + moon_age + ")");

		String imgUri = baby.getImage();

		if (!StringUtils.isNullOrEmpty(imgUri)) {
			Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri, 100,
					100);
			holder.babyImg.setImageBitmap(bitmap);
		} else {
			holder.babyImg.setImageResource(R.drawable.default_img);
		}

		holder.deleteHolder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String isDefault = mDao.checkIsDefault(baby);
				if ("1".equals(isDefault)) {
					PublicMethod.showToast(mContext, "默认宝宝不能删除");
				} else {
					boolean b = mDao.deleteBaby(baby);
					if (b) {
						PublicMethod.showToast(mContext,
								R.string.delete_success);
					} else {
						PublicMethod.showToast(mContext, R.string.delete_fail);
					}
				}
			}
		});

		if ("1".equals(baby.getIs_default())) {
			holder.babyDefaultImgBtn
					.setImageResource(R.drawable.round_selector_checked);
		} else {
			holder.babyDefaultImgBtn
					.setImageResource(R.drawable.round_selector_normal);
		}

		holder.babyDefaultImgBtn.setOnClickListener(new BabyBtnOnClickListener(
				baby, holder.babyDefaultImgBtn));

		return slideView;
	}

	/**
	 * 切换宝宝按钮监听
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

	private static class ViewHolder {

		public ImageButton babyDefaultImgBtn;
		public CircleImageView babyImg;
		public TextView babyName;
		public TextView babyAge;

		public ViewGroup deleteHolder;

		ViewHolder(View view) {

			babyDefaultImgBtn = (ImageButton) view
					.findViewById(R.id.radioButton);
			babyImg = (CircleImageView) view.findViewById(R.id.baby_img);
			babyName = (TextView) view.findViewById(R.id.baby_name);
			babyAge = (TextView) view.findViewById(R.id.baby_age);

			deleteHolder = (ViewGroup) view.findViewById(R.id.holder);
		}
	}

}
