package cn.mointe.vaccination.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.StringUtils;
import cn.mointe.vaccination.view.RoundImageView;

public class DrawerBabyAdapter extends BaseAdapter {

	private Baby mBaby;
	private List<Baby> mBabys;
	private Context mContext;

	public DrawerBabyAdapter(Context context, List<Baby> babys) {
		this.mContext = context;
		this.mBabys = babys;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RoundImageView imageView = null;
		TextView textView = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.baby_gridview_item, null);
			imageView = (RoundImageView) convertView
					.findViewById(R.id.grid_baby_image);
			textView = (TextView) convertView.findViewById(R.id.grid_baby_name);

			ViewCache cache = new ViewCache();
			cache.imageView = imageView;
			cache.textView = textView;

			convertView.setTag(cache);
		} else {
			ViewCache cache = (ViewCache) convertView.getTag();
			imageView = cache.imageView;
			textView = cache.textView;
		}

		mBaby = this.mBabys.get(position);
		
		//Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/baby_name.ttf");
		//textView.setTypeface(typeface);
		textView.setText(mBaby.getName());

		if (mBaby.getName().equals("新宝宝")) {
			imageView.setImageResource(R.drawable.add_baby);
		} else {

			String imageUri = mBaby.getImage();
			if (!StringUtils.isNullOrEmpty(imageUri)) {
				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(
						imageUri, 100, 100);
				Bitmap greyBitmap = null;
				if (!mBaby.getIs_default().equals("1")) {
					greyBitmap = BitmapUtil.convertGreyImg(bitmap);
					imageView.setImageBitmap(greyBitmap);
				} else {
					imageView.setImageBitmap(bitmap);
				}
			} else {
				Drawable drawable = mContext.getResources().getDrawable(
						R.drawable.default_img);
				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				if (!mBaby.getIs_default().equals("1")) {
					imageView.setImageBitmap(BitmapUtil.convertGreyImg(bitmap));
				} else {
					imageView.setImageResource(R.drawable.default_img);
				}
			}

		}

		return convertView;
	}

	private final class ViewCache {
		public RoundImageView imageView;
		public TextView textView;
	}
}
