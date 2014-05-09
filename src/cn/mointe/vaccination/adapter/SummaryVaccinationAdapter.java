package cn.mointe.vaccination.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.Vaccination;

public class SummaryVaccinationAdapter extends BaseAdapter {

	private Vaccination mVaccination;
	private List<Vaccination> mVaccinationList;
	private Context mContext;

	//private String mVaccinationDate = null;

	//private VaccinationDao mVaccinationDao;
	//private VaccinationPreferences mPreferences;

	public SummaryVaccinationAdapter(Context context,
			List<Vaccination> vaccinationList) {
		this.mContext = context;
		this.mVaccinationList = vaccinationList;
		//mVaccinationDao = new VaccinationDao(mContext);
		//mPreferences = new VaccinationPreferences(mContext);
	}

	@Override
	public int getCount() {
		return mVaccinationList.size();
	}

	@Override
	public Object getItem(int position) {
		return mVaccinationList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView vaccineName = null;
		TextView vaccineDate = null;
		TextView isHave = null;
		ImageView chargeStandard = null;

		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.summary_list_item, null);

			vaccineName = (TextView) convertView
					.findViewById(R.id.summary_item_vaccine_name);
			vaccineDate = (TextView) convertView
					.findViewById(R.id.summary_item_vaccine_date);
			isHave = (TextView) convertView.findViewById(R.id.summary_item_isHave);
			chargeStandard = (ImageView) convertView
					.findViewById(R.id.summary_item_img);

			ViewCache cache = new ViewCache();

			cache.vaccineName = vaccineName;
			cache.vaccineDate = vaccineDate;
			cache.isHave = isHave;
			cache.chargeStandard = chargeStandard;

			convertView.setTag(cache);
		} else {
			ViewCache cache = (ViewCache) convertView.getTag();

			vaccineName = cache.vaccineName;
			vaccineDate = cache.vaccineDate;
			isHave = cache.isHave;
			chargeStandard = cache.chargeStandard;
		}
		mVaccination = (Vaccination) this.mVaccinationList.get(position);

		vaccineName.setText(mVaccination.getVaccine_name());
		vaccineDate.setText(mVaccination.getReserve_time());

		String chargeStandardText = mVaccination.getCharge_standard();
		if (chargeStandardText.equals("免费")) {
			chargeStandard.setImageResource(R.drawable.summary_1);
		} else {
			chargeStandard.setImageResource(R.drawable.summary_2);
		}

		isHave.setTextColor(Color.BLACK);
		isHave.setText("未接种");

//		isHave.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				Calendar calendar = Calendar.getInstance();
//				final String reserveTime = mVaccination.getReserve_time();
//				Date date = null;
//				try {
//					date = DateUtils.stringToDate(reserveTime);
//					calendar.setTime(date);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				final int year = calendar.get(Calendar.YEAR);
//				final int monthOfYear = calendar.get(Calendar.MONDAY);
//				final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//				dialog.setTitle("选择完成时间");// 提示
//
//				LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//				View view = layoutInflater.inflate(R.layout.set_date, null);
//
//				DatePicker datePicker = (DatePicker) view
//						.findViewById(R.id.datePicker);
//				datePicker.init(year, monthOfYear, dayOfMonth,
//						new OnDateChangedListener() {
//							public void onDateChanged(DatePicker view,
//									int year, int monthOfYear, int dayOfMonth) {
//
//								StringBuilder stringBuilder = new StringBuilder();
//								stringBuilder.append(year);
//								int newMonth = monthOfYear + 1;
//								if (newMonth < 10) {
//									stringBuilder.append("-0").append(newMonth);
//								} else {
//									stringBuilder.append("-").append(newMonth);
//								}
//								if (dayOfMonth < 10) {
//									stringBuilder.append("-0").append(
//											dayOfMonth);
//								} else {
//									stringBuilder.append("-")
//											.append(dayOfMonth);
//								}
//
//								mVaccinationDate = stringBuilder.toString();
//								Log.i(Constants.TAG, "完成接种时间="
//										+ mVaccinationDate);
//							}
//						});
//
//				dialog.setView(view);
//				// 确定按钮
//				dialog.setPositiveButton(R.string.confirm,
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Vaccination vaccination = new Vaccination();
//								vaccination.setId(mVaccination.getId());
//								int result = 0;
//								try {
//									result = DateUtils
//											.compareDateToToday(reserveTime);
//								} catch (ParseException e) {
//									e.printStackTrace();
//								}
//								if (result == 0 || result == -1) {
//									if (mVaccinationDate != null) {
//										vaccination
//												.setFinish_time(mVaccinationDate);
//									} else {
//										StringBuilder stringBuilder = new StringBuilder();
//										stringBuilder.append(year);
//										int newMonth = monthOfYear + 1;
//										if (newMonth < 10) {
//											stringBuilder.append("-0").append(
//													newMonth);
//										} else {
//											stringBuilder.append("-").append(
//													newMonth);
//										}
//										if (dayOfMonth < 10) {
//											stringBuilder.append("-0").append(
//													dayOfMonth);
//										} else {
//											stringBuilder.append("-").append(
//													dayOfMonth);
//										}
//										vaccination
//												.setFinish_time(stringBuilder
//														.toString());
//									}
//
//									String nextRemindDate = null; // 下次提醒日期
//									try {
//										// 查询下次提醒日期
//										nextRemindDate = mVaccinationDao.findNextVaccinationDate(
//												mVaccination.getBaby_nickname(),
//												mVaccination.getReserve_time());
//									} catch (ParseException e) {
//										e.printStackTrace();
//									}
//									// 将下次提醒日期存到Preferences
//									mPreferences.setRemindDate(nextRemindDate);
//									// 如果服务正在运行，重启服务
//									if (PackageUtil.isServiceRunning(mContext,
//											Constants.REMIND_SERVICE)) {
//										mContext.stopService(new Intent(
//												mContext,
//												VaccinationRemindService.class));
//									}
//									mContext.startService(new Intent(mContext,
//											VaccinationRemindService.class));
//
//								} else {
//									PublicMethod.showToast(mContext,
//											"未到应接种时间不能完成接种");
//									return;
//								}
//								mVaccinationDao
//										.updateFinishTimeById(vaccination);
//							}
//						});
//				dialog.setNegativeButton(R.string.cancel,
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								dialog.dismiss();
//							}
//						});
//				dialog.create();
//				dialog.show();
//			}
//		});

		return convertView;
	}

	private final class ViewCache {
		public TextView vaccineName;
		public TextView vaccineDate;
		public TextView isHave;
		public ImageView chargeStandard;
	}
}
