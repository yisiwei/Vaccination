package cn.mointe.vaccination.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.db.DBHelper;

public class VaccinationCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public VaccinationCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.vaccination_item, null);
		ViewCache cache = new ViewCache();
		cache.vaccine_date = (TextView) view
				.findViewById(R.id.vaccination_item_tv_date);
		cache.vaccine_age = (TextView) view
				.findViewById(R.id.vaccination_item_tv_age);
		cache.vaccine_name = (TextView) view
				.findViewById(R.id.vaccination_item_tv_name);
		cache.isHave = (TextView) view
				.findViewById(R.id.vaccination_item_tv_ishave);
		view.setTag(cache);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewCache cache = (ViewCache) view.getTag();
		cache.vaccine_date.setText(cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME)));
		cache.vaccine_age.setText("("
				+ cursor.getString(cursor
						.getColumnIndex(DBHelper.VACCINATION_COLUMN_MOON_AGE))
				+ ")");
		cache.vaccine_name.setText(cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME)));
		cache.vaccine_name.setText(cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME)));

		try {
			Date date = new Date();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",
					Locale.getDefault());
			Date reserveDate = format.parse(cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_RESERVE_TIME)));// 转换为时间

			String todayString = format.format(date);
			Date today = format.parse(todayString);

			int result = today.compareTo(reserveDate);
			if (!TextUtils.isEmpty(cursor.getString(cursor
					.getColumnIndex(DBHelper.VACCINATION_COLUMN_FINISH_TIME)))) {
				cache.isHave.setTextColor(Color.BLUE);
				cache.isHave.setText(R.string.finish_vaccination);// 已接种
			} else {
				if (result > 0) {
					cache.isHave.setTextColor(Color.RED);
					cache.isHave.setText(R.string.expire_vaccination);// 已过期
				} else {
					cache.isHave.setTextColor(Color.BLACK);
					cache.isHave.setText(R.string.not_vaccination);// 未接种
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private final class ViewCache {
		public TextView vaccine_date;
		public TextView vaccine_age;
		public TextView vaccine_name;
		public TextView isHave;
	}

}
