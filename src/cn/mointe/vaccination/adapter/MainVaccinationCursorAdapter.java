package cn.mointe.vaccination.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.db.DBHelper;

public class MainVaccinationCursorAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public MainVaccinationCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.main_vaccine_item_old, null);

		ViewCache cache = new ViewCache();

		cache.vaccineName = (TextView) view
				.findViewById(R.id.main_vaccine_item_name);
		cache.vaccineNumber = (TextView) view
				.findViewById(R.id.main_vaccine_item_number);
		cache.vaccineType2Img = (TextView) view
				.findViewById(R.id.main_vaccine_item_type2_img);

		view.setTag(cache);

		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewCache cache = (ViewCache) view.getTag();
		cache.vaccineName.setText(cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_NAME)));
		cache.vaccineNumber
				.setText("("
						+ cursor.getString(cursor
								.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINATION_NUMBER))
						+ ")");
		String type = cursor.getString(cursor
				.getColumnIndex(DBHelper.VACCINATION_COLUMN_VACCINE_TYPE));
		if (type.equals("一类")) {
			cache.vaccineType2Img.setVisibility(View.INVISIBLE);
		}else{
			cache.vaccineType2Img.setVisibility(View.VISIBLE);
		}
	}

	private final class ViewCache {
		public TextView vaccineName;
		public TextView vaccineNumber;
		public TextView vaccineType2Img;
	}

}
