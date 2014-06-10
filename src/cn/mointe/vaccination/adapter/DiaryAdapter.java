package cn.mointe.vaccination.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.DiaryDeleteVaccineDialog;
import cn.mointe.vaccination.domain.VaccinationRecord;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.view.MyGridView;

public class DiaryAdapter extends BaseAdapter {

	private List<VaccinationRecord> mVaccinationRecords;
	private LayoutInflater mInflater;
	private Context mContext;

	//private AlertDialog mDeleteDialog;
	//private VaccinationDao mVaccinationDao;
	//private BabyDao mBabyDao;

	public DiaryAdapter(Context context, List<VaccinationRecord> vaccinationRecords) {
		this.mContext = context;
		this.mVaccinationRecords = vaccinationRecords;
		this.mInflater = LayoutInflater.from(mContext);
		//mVaccinationDao = new VaccinationDao(mContext);
		//mBabyDao = new BabyDao(mContext);
	}

	@Override
	public int getCount() {
		return mVaccinationRecords.size();
	}

	@Override
	public Object getItem(int position) {
		return mVaccinationRecords.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.diary_item, null);

			holder = new ViewHolder();

			holder.date = (TextView) convertView
					.findViewById(R.id.diary_date);
			holder.gridView = (MyGridView) convertView
					.findViewById(R.id.diary_vaccine_list);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		VaccinationRecord vaccinationRecord = mVaccinationRecords.get(position);

		holder.date.setText(vaccinationRecord.getDate());

		List<String> vaccines = vaccinationRecord.getVaccines();

		GridViewAdapter adapter;
		if (vaccines != null && vaccines.size() > 0) {
			Log.i("MainActivity", ">>>>>>>>>>>" + vaccines.get(0));
			adapter = new GridViewAdapter(vaccines);
			// if (holder.gridView.getAdapter() == null) {
			holder.gridView.setAdapter(adapter);
			// }
		} else {
			holder.gridView.setAdapter(null);
		}

		return convertView;
	}

	public class ViewHolder {
		public TextView date;
		public MyGridView gridView;
	}

//	private void showDeleteDialog(final String vaccineName,
//			final String vaccineNumber) {
//		if (mDeleteDialog != null && mDeleteDialog.isShowing()) {
//			return;
//		}
//		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//		builder.setIcon(R.drawable.app_icon);
//		builder.setTitle("删除?");
//		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				mVaccinationDao.cancelVaccination(mBabyDao.getDefaultBaby()
//						.getName(), vaccineName, vaccineNumber, null);
//			}
//		});
//		builder.setNegativeButton("取消", null);
//		mDeleteDialog = builder.create();
//		mDeleteDialog.show();
//	}

	private class GridViewAdapter extends BaseAdapter {

		private List<String> list;

		public GridViewAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.diary_vaccine_item,
						null);

				holder = new ViewHolder();
				holder.button = (Button) convertView
						.findViewById(R.id.diary_vaccine_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final String vaccine = list.get(position);
			holder.button.setText(vaccine);
			holder.button.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					Log.i("MainActivity", "vaccine:" + vaccine);
					String vaccineSub = vaccine.substring(0,
							vaccine.length() - 1);
					// Log.i("MainActivity", "vaccineSub:" + vaccineSub);
					String[] vaccineArr = vaccineSub.split("\\(");
					Log.i("MainActivity", "vaccineArr:" + vaccineArr[0] + "--"
							+ vaccineArr[1]);
					//showDeleteDialog(vaccineArr[0], vaccineArr[1]);
					Intent intent = new Intent(mContext, DiaryDeleteVaccineDialog.class);
					intent.putExtra("vaccineName", vaccineArr[0]);
					intent.putExtra("vaccineNumber", vaccineArr[1]);
					mContext.startActivity(intent);
					return false;
				}
			});

			return convertView;
		}

		public class ViewHolder {
			public Button button;
		}

	}

}
