package cn.mointe.vaccination.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

public class ReservationBeforeTodayActivity extends FragmentActivity {

	private String mDate;
	private String mNoFormatDate;

	private TextView mTextView;
	private ListView mListView;

	private MyAdapter mAdapter;

	private VaccinationDao mVaccinationDao;
	private BabyDao mBabyDao;

	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	private AlertDialog mReserveDialog;
	
	private LoaderManager mLoaderManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reservation_before_today);

		mVaccinationDao = new VaccinationDao(this);
		mBabyDao = new BabyDao(this);
		
		mLoaderManager = getSupportLoaderManager();
				
		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);

		mTextView = (TextView) this
				.findViewById(R.id.reservation_before_today_hint);
		mListView = (ListView) this
				.findViewById(R.id.reservation_before_today_list);

		mDate = getIntent().getStringExtra("date");
		mNoFormatDate = getIntent().getStringExtra("noFormatDate");
		Log.i("MainActivity", "今天之前日期：" + mDate);
		
		mTitleText.setText(R.string.reserve_record);
		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("date", mNoFormatDate);
				setResult(RESULT_OK, intent);
				ReservationBeforeTodayActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

//		if (getData() != null && getData().size() > 0) {
//			mAdapter = new MyAdapter(this, getData());
//			mListView.setAdapter(mAdapter);
//		} else {
//			mTextView.setVisibility(View.VISIBLE);
//		}

		mLoaderManager.initLoader(500, null, mDiaryCallBacks);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Vaccination vaccination = (Vaccination) mAdapter
						.getItem(position);
				if (StringUtils.isNullOrEmpty(vaccination.getFinish_time())) {
					showReserveDialog(vaccination);
				}
			}

		});
	}

	/**
	 * 重新预约
	 * 
	 * @param vaccination
	 */
	private void showReserveDialog(final Vaccination vaccination) {
		if (mReserveDialog != null && mReserveDialog.isShowing()) {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.app_icon);
		builder.setTitle("重新预约");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mVaccinationDao.cancelVaccination(mBabyDao
								.getDefaultBaby().getName(), vaccination
								.getVaccine_name(), vaccination
								.getVaccination_number(), null);
						mAdapter.notifyDataSetChanged();
					}
				});
		builder.setNegativeButton(R.string.cancel, null);
		mReserveDialog = builder.create();
		mReserveDialog.show();
	}

	/**
	 * 数据
	 * 
	 * @return
	 */
//	private List<Vaccination> getData() {
//		List<Vaccination> vaccinations = mVaccinationDao
//				.getVaccinationByReservationDate(mBabyDao.getDefaultBaby()
//						.getName(), mDate);
//
//		return vaccinations;
//	}
	
	private LoaderCallbacks<Cursor> mDiaryCallBacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			CursorLoader loader = new CursorLoader(ReservationBeforeTodayActivity.this);
			loader.setUri(VaccinationProvider.CONTENT_URI);
			loader.setSelection(DBHelper.VACCINATION_COLUMN_BABY_NICKNAME + "=? and "
					+ DBHelper.VACCINATION_COLUMN_RESERVE_TIME + "=?");
			loader.setSelectionArgs(new String[] { mBabyDao.getDefaultBaby()
					.getName(), mDate });
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			List<Vaccination> vaccinations = new ArrayList<Vaccination>();
			while (data.moveToNext()) {
				Vaccination vaccination = mVaccinationDao.cursorToVaccination(data);
				vaccinations.add(vaccination);
			}
			
			if (vaccinations.size() > 0 ) {
				mAdapter = new MyAdapter(ReservationBeforeTodayActivity.this, vaccinations);
				mListView.setAdapter(mAdapter);
			}else{
				mTextView.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			
		}
		
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 返回键监听
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Intent intent = new Intent();
			intent.putExtra("date", mNoFormatDate);
			setResult(RESULT_OK, intent);
			this.finish();
		}

		return super.onKeyDown(keyCode, event);
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
