package cn.mointe.vaccination.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.DiaryDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Diary;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.VaccinationInfo;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

public class VaccinationInfoAdapter extends BaseAdapter {

	private List<VaccinationInfo> mVaccinationInfos;
	private Context mContext;
	private VaccinationDao mVaccinationDao;
	private DiaryDao mDiaryDao;

	public VaccinationInfoAdapter(Context context,
			List<VaccinationInfo> vaccinationInfos) {
		this.mContext = context;
		this.mVaccinationInfos = vaccinationInfos;
		mVaccinationDao = new VaccinationDao(mContext);
		mDiaryDao = new DiaryDao(mContext);
	}

	@Override
	public int getCount() {
		return mVaccinationInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mVaccinationInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.main_vaccine_today_item_old, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.main_today_vaccine_item_name);
			holder.before = (TextView) convertView
					.findViewById(R.id.main_vac_before);
			holder.after = (TextView) convertView
					.findViewById(R.id.main_vac_after);
			holder.finishBtn = (ImageButton) convertView
					.findViewById(R.id.main_vaccine_today_item_btn);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final VaccinationInfo info = (VaccinationInfo) this.mVaccinationInfos
				.get(position);

		holder.name.setText(info.getName());
		holder.before.setText(info.getBefore());
		holder.after.setText(info.getAfter());

		if (StringUtils.isNullOrEmpty(info.getFinishDate())) {
			holder.finishBtn.setImageResource(R.drawable.main_finish_normal);
			holder.finishBtn.setTag("0");
		} else {
			holder.finishBtn.setImageResource(R.drawable.main_finish);
			holder.finishBtn.setTag("1");
		}

		final ImageButton btn = holder.finishBtn;
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (btn.getTag().toString().equals("0")) {
					Vaccination vaccination = new Vaccination();
					vaccination.setId(info.getVacid());
					vaccination.setFinish_time(info.getDate());
					mVaccinationDao.updateFinishTimeById(vaccination);
					Log.i("MainActivity", "接种完成");
					Toast.makeText(mContext, "接种完成", Toast.LENGTH_SHORT).show();
					// 接种完成后想日记表添加一条数据
					Diary diary = mDiaryDao.queryDiary(info.getBabyName(),
							info.getDate());
					if (diary == null) {
						mDiaryDao.saveDiary(new Diary(info.getBabyName(), info
								.getDate(), null));
					}
				} else {
					Vaccination vaccination = new Vaccination();
					vaccination.setId(info.getVacid());
					vaccination.setFinish_time(null);
					mVaccinationDao.updateFinishTimeById(vaccination);
					Log.i("MainActivity", "取消");
					Toast.makeText(mContext, "取消", Toast.LENGTH_SHORT).show();
				}
			}
		});

		return convertView;
	}

	private final class ViewHolder {
		TextView name;
		TextView before;
		TextView after;
		ImageButton finishBtn;
	}

}
