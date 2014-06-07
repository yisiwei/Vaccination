package cn.mointe.vaccination.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.AddDiaryActivity;
import cn.mointe.vaccination.activity.DiaryDeleteVaccineDialog;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Diary;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.FileUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.view.MyGridView;

public class DiaryAdapter extends BaseAdapter {

	private List<Diary> mDiaries;
	private LayoutInflater mInflater;
	private Context mContext;

	private AlertDialog mDeleteDialog;
	private VaccinationDao mVaccinationDao;
	private BabyDao mBabyDao;

	public DiaryAdapter(Context context, List<Diary> diaries) {
		this.mContext = context;
		this.mDiaries = diaries;
		this.mInflater = LayoutInflater.from(mContext);
		mVaccinationDao = new VaccinationDao(mContext);
		mBabyDao = new BabyDao(mContext);
	}

	@Override
	public int getCount() {
		return mDiaries.size();
	}

	@Override
	public Object getItem(int position) {
		return mDiaries.get(position);
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

			holder.diaryDate = (TextView) convertView
					.findViewById(R.id.diary_date);
//			holder.diaryContent = (TextView) convertView
//					.findViewById(R.id.diary_content);
			holder.gridView = (MyGridView) convertView
					.findViewById(R.id.diary_vaccine_list);

//			holder.share = (ImageButton) convertView
//					.findViewById(R.id.diary_share);
//			holder.edit = (ImageButton) convertView
//					.findViewById(R.id.diary_edit);
//
//			holder.imageView1 = (ImageView) convertView
//					.findViewById(R.id.diary_image1);
//			holder.imageView2 = (ImageView) convertView
//					.findViewById(R.id.diary_image2);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Diary diary = mDiaries.get(position);

		holder.diaryDate.setText(diary.getDate());
		holder.diaryContent.setText(diary.getDiaryContent());

		List<String> vaccines = diary.getVaccines();

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

		//List<String> images = diary.getImages();

//		if (images.size() >= 2) {
//			String image1 = images.get(0);
//			if (FileUtils.fileIsExist(image1)) {// 判断图片是否存在
//				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(image1,
//						100, 100);
//				holder.imageView1.setImageBitmap(bitmap);
//			}
//			String image2 = images.get(1);
//			if (FileUtils.fileIsExist(image2)) {
//				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(image2,
//						100, 100);
//				holder.imageView2.setImageBitmap(bitmap);
//			}
//		}
//		if (images.size() == 1) {
//			String image = images.get(0);
//			if (FileUtils.fileIsExist(image)) {
//				Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(image,
//						100, 100);
//				holder.imageView1.setImageBitmap(bitmap);
//			}
//		}

//		holder.edit.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO 编辑日记
//				mContext.startActivity(new Intent(mContext,
//						AddDiaryActivity.class));
//			}
//		});

		return convertView;
	}

	public class ViewHolder {
		public TextView diaryDate;
		public TextView diaryContent;
		public MyGridView gridView;

//		public ImageButton share;
//		public ImageButton edit;

//		public ImageView imageView1;
//		public ImageView imageView2;

	}

	private void showDeleteDialog(final String vaccineName,
			final String vaccineNumber) {
		if (mDeleteDialog != null && mDeleteDialog.isShowing()) {
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(R.drawable.app_icon);
		builder.setTitle("删除?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mVaccinationDao.cancelVaccination(mBabyDao.getDefaultBaby()
						.getName(), vaccineName, vaccineNumber, null);
			}
		});
		builder.setNegativeButton("取消", null);
		mDeleteDialog = builder.create();
		mDeleteDialog.show();
	}

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
