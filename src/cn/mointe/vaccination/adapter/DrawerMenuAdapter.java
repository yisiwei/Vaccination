package cn.mointe.vaccination.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.mointe.vaccination.R;

public class DrawerMenuAdapter extends BaseAdapter {

	private Map<String, Object> mItem;
	private List<Map<String, Object>> mData;
	private Context mContext;

	public DrawerMenuAdapter(Context context, List<Map<String, Object>> data) {
		this.mContext = context;
		this.mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = null;
		TextView textView = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.menu_gridview_item, null);
			imageView = (ImageView) convertView
					.findViewById(R.id.grid_menu_icon);
			textView = (TextView) convertView.findViewById(R.id.grid_menu_name);

			ViewCache cache = new ViewCache();
			cache.imageView = imageView;
			cache.textView = textView;

			convertView.setTag(cache);
		} else {
			ViewCache cache = (ViewCache) convertView.getTag();
			imageView = cache.imageView;
			textView = cache.textView;
		}

		mItem = this.mData.get(position);
		//Typeface typeface = Typeface.createFromAsset(mContext.getAssets(),
		//		"fonts/baby_name.ttf");
		//textView.setTypeface(typeface);
		textView.setText(mItem.get("text").toString());
		imageView.setImageResource(Integer
				.valueOf(mItem.get("icon").toString()));

		return convertView;
	}

	private final class ViewCache {
		public ImageView imageView;
		public TextView textView;
	}
}
