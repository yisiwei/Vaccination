package cn.mointe.vaccination.activity;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.dao.VaccineSpecificationDao;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.domain.VaccineSpecfication;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.PackageUtil;
import cn.mointe.vaccination.tools.PublicMethod;

/**
 * 疫苗接种界面
 * 
 */
public class VaccinationDetailActivity extends ActionBarActivity implements
		OnClickListener {

	private VaccinationDao mVaccinationDao;
	private VaccineDao mVaccineDao;
	private VaccineSpecificationDao mSpecificationDao;

	private Vaccination mVaccination;

	private TextView mVaccineName;// 疫苗名称
	private Button mVaccinationTime;// 应接种时间
	private Button mVaccinationFinish;// 完成接种
	private TextView mVaccinationNumber;// 接种剂次
	private TextView mPreventDisease;// 预防疾病

	private TextView mVaccinationObject;// 接种对象
	private TextView mVaccinationAnnouncements;// 注意事项
	private TextView mVaccinationAdverseReaction;// 不良反应
	private TextView mVaccinationContraindication;// 禁忌
	private TextView mVccinationImmuneProcedure;// 免疫程序

	private LinearLayout mVaccineNameLayout;

	private String mVaccinationDate = null;// 实际接种时间

	private ActionBar mBar;
	//private boolean mFlag = false;
	private VaccinationPreferences mPreferences;
	
	private String mShouldVaccinationDate;//修改的应接种时间

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccination_detail);

		mVaccinationDao = new VaccinationDao(this);
		mVaccineDao = new VaccineDao(this);
		mSpecificationDao = new VaccineSpecificationDao();
		mPreferences = new VaccinationPreferences(this);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		// 主界面传过来的Vaccination对象
		mVaccination = (Vaccination) getIntent().getSerializableExtra(
				"Vaccination");

		// 初始化控件
		mVaccineNameLayout = (LinearLayout) this
				.findViewById(R.id.vac_detail_llay_vaccine_name);
		mVaccineName = (TextView) this
				.findViewById(R.id.vac_detail_tv_vaccina_name);// 疫苗名称
		mVaccinationTime = (Button) this
				.findViewById(R.id.vac_detail_btn_vaccina_date);// 应接种时间
		mVaccinationFinish = (Button) this
				.findViewById(R.id.vac_detail_btn_vaccina_finish);// 完成接种
		mVaccinationNumber = (TextView) this
				.findViewById(R.id.vac_detail_tv_vaccina_number);// 接种剂次
		mPreventDisease = (TextView) this
				.findViewById(R.id.vac_detail_tv_prevent_disease);// 预防疾病

		mVaccinationObject = (TextView) this
				.findViewById(R.id.vaccination_object);// 接种对象
		mVaccinationAnnouncements = (TextView) this
				.findViewById(R.id.vaccination_announcements);// 注意事项
		mVaccinationAdverseReaction = (TextView) this
				.findViewById(R.id.vaccination_adverse_reaction);// 不良反应
		mVaccinationContraindication = (TextView) this
				.findViewById(R.id.vaccination_contraindication);// 禁忌
		mVccinationImmuneProcedure = (TextView) this
				.findViewById(R.id.vaccination_immune_procedure);// 免疫程序

		mVaccineName.setText(mVaccination.getVaccine_name());
		mVaccinationTime.setText(mVaccination.getReserve_time());
		String vaccineFinish = mVaccination.getFinish_time();
		if (!TextUtils.isEmpty(vaccineFinish)) {
			mVaccinationFinish.setText("已接种");
		} else {
			mVaccinationFinish.setText("未接种");
		}

		Vaccine vaccine = mVaccineDao.queryVaccineByName(mVaccination
				.getVaccine_name());

		mVaccinationNumber.setText(mVaccination.getVaccination_number());
		mPreventDisease.setText(vaccine.getVaccine_intro());

		VaccineSpecfication specfication = mSpecificationDao
				.getVaccineSpecficationByVaccineName(mVaccination
						.getVaccine_name());
		if (null != specfication) {
			mVaccinationObject.setText(specfication.getInoculation_object());// 接种对象
			mVaccinationAnnouncements.setText(specfication.getCaution());// 注意事项
			mVaccinationAdverseReaction.setText(specfication
					.getAdverse_reaction());// 不良反应
			mVaccinationContraindication.setText(specfication
					.getContraindication());// 禁忌
			mVccinationImmuneProcedure.setText(specfication
					.getImmune_procedure());// 免疫程序
		}

		mVaccineNameLayout.setOnClickListener(this);
		mVaccinationTime.setOnClickListener(this);
		mVaccinationFinish.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Calendar calendar = Calendar.getInstance();
		String reserveTime = mVaccinationTime.getText().toString();
		Date date = null;
		try {
			date = DateUtils.stringToDate(reserveTime);
			calendar.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		final int year = calendar.get(Calendar.YEAR);
		final int monthOfYear = calendar.get(Calendar.MONDAY);
		final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		switch (v.getId()) {
		case R.id.vac_detail_btn_vaccina_date: // 修改接种时间
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.set_vaccination_datetime);// 设置应接种时间

			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.set_date, null);
			TextView dateLable = (TextView) view.findViewById(R.id.date_lable);
			dateLable.setVisibility(View.GONE);
			DatePicker datePicker = (DatePicker) view.findViewById(R.id.datePicker);
			datePicker.init(year, monthOfYear, dayOfMonth,
					new OnDateChangedListener() {
						public void onDateChanged(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {

							StringBuilder stringBuilder = new StringBuilder();
							stringBuilder.append(year);
							int newMonth = monthOfYear + 1;
							if (newMonth < 10) {
								stringBuilder.append("-0").append(newMonth);
							} else {
								stringBuilder.append("-").append(newMonth);
							}
							if (dayOfMonth < 10) {
								stringBuilder.append("-0").append(dayOfMonth);
							} else {
								stringBuilder.append("-").append(dayOfMonth);
							}

							mShouldVaccinationDate = stringBuilder.toString();
							Log.i(Constants.TAG, "应接种时间=" + mShouldVaccinationDate);
						}
					});

			builder.setView(view);
			//确定按钮
			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							//如果改变了应接种时间
							if (mShouldVaccinationDate != null) {
								mVaccinationTime.setText(mShouldVaccinationDate);
								// 修改接种时间
								mVaccinationDao.updateReserveTimeById(
										mVaccination.getId(),
										mShouldVaccinationDate);
								// 将修改的时间更新到Preferences
								mPreferences.setRemindDate(mShouldVaccinationDate);
								Intent remindService = new Intent(
										getApplicationContext(),
										VaccinationRemindService.class);
								// 如果服务在运行，重新启动
								if (PackageUtil
										.isServiceRunning(
												getApplicationContext(),
												Constants.REMIND_SERVICE)) {
									stopService(remindService);
								}
								startService(remindService);// 启动服务
							} 
						}

					});
			builder.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			builder.create();
			builder.show();
			
			//---------------------------------
//			DatePickerDialog datePickerDialog = new DatePickerDialog(this,
//					new OnDateSetListener() {
//
//						@Override
//						public void onDateSet(DatePicker view, int year,
//								int monthOfYear, int dayOfMonth) {
//							if (mFlag) {
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
//								mVaccinationTime.setText(stringBuilder
//										.toString());
//								// 修改接种时间
//								mVaccinationDao.updateReserveTimeById(
//										mVaccination.getId(),
//										stringBuilder.toString());
//								// 将修改的时间更新到Preferences
//								mPreferences.setRemindDate(stringBuilder
//										.toString());
//								Intent remindService = new Intent(
//										getApplicationContext(),
//										VaccinationRemindService.class);
//								// 如果服务在运行，重新启动
//								if (PackageUtil
//										.isServiceRunning(
//												getApplicationContext(),
//												"cn.mointe.vaccination.service.VaccinationRemindService")) {
//									stopService(remindService);
//								}
//								startService(remindService);// 启动服务
//								mFlag = false;
//							}
//						}
//					}, year, monthOfYear, dayOfMonth);
//			datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,
//					getResources().getString(R.string.confirm),
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							mFlag = true;
//						}
//					});
//			datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
//					getResources().getString(R.string.cancel),
//					new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							mFlag = false;
//						}
//					});
//			datePickerDialog.show();
			break;
		case R.id.vac_detail_btn_vaccina_finish: // 完成接种
			AlertDialog.Builder finishDialog = new AlertDialog.Builder(this);
			finishDialog.setTitle(R.string.hint);// 提示
			if (mVaccinationFinish.getText().toString().equals("未接种")) {
				finishDialog.setMessage("确定完成接种？");

				LayoutInflater layoutInflater = LayoutInflater.from(this);
				View finishView = layoutInflater.inflate(R.layout.set_date, null);

				DatePicker finishDatePicker = (DatePicker) finishView
						.findViewById(R.id.datePicker);
				finishDatePicker.init(year, monthOfYear, dayOfMonth,
						new OnDateChangedListener() {
							public void onDateChanged(DatePicker view,
									int year, int monthOfYear, int dayOfMonth) {
								
								StringBuilder stringBuilder = new StringBuilder();
								stringBuilder.append(year);
								int newMonth = monthOfYear + 1;
								if (newMonth < 10) {
									stringBuilder.append("-0").append(newMonth);
								} else {
									stringBuilder.append("-").append(newMonth);
								}
								if (dayOfMonth < 10) {
									stringBuilder.append("-0").append(dayOfMonth);
								} else {
									stringBuilder.append("-").append(dayOfMonth);
								}

								mVaccinationDate = stringBuilder.toString();
								Log.i(Constants.TAG, "完成接种时间=" + mVaccinationDate);
							}
						});

				finishDialog.setView(finishView);
			} else {
				finishDialog.setMessage("确定改为未接种？");
			}
			finishDialog.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Vaccination vaccination = new Vaccination();
							vaccination.setId(mVaccination.getId());
							if (mVaccinationFinish.getText().toString()
									.equals("未接种")) {
								String reserveTime = mVaccinationTime.getText()
										.toString();// 应接种时间
								int result = 0;
								try {
									result = DateUtils
											.compareDateToToday(reserveTime);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if (result == 0 || result == -1) {
									mVaccinationFinish.setText("已接种");
									if (mVaccinationDate != null) {
										vaccination
										.setFinish_time(mVaccinationDate);
									}else{
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(year);
										int newMonth = monthOfYear + 1;
										if (newMonth < 10) {
											stringBuilder.append("-0").append(newMonth);
										} else {
											stringBuilder.append("-").append(newMonth);
										}
										if (dayOfMonth < 10) {
											stringBuilder.append("-0").append(dayOfMonth);
										} else {
											stringBuilder.append("-").append(dayOfMonth);
										}
										vaccination
										.setFinish_time(stringBuilder.toString());
									}
										
									String nextRemindDate = null; // 下次提醒日期
									try {
										// 查询下次提醒日期
										nextRemindDate = mVaccinationDao.findNextVaccinationDate(
												mVaccination.getBaby_nickname(),
												mVaccination.getReserve_time());
									} catch (ParseException e) {
										e.printStackTrace();
									}
									// 将下次提醒日期存到Preferences
									mPreferences.setRemindDate(nextRemindDate);
									// 如果服务正在运行，重启服务
									if (PackageUtil.isServiceRunning(
											getApplicationContext(),
											Constants.REMIND_SERVICE)) {
										stopService(new Intent(
												getApplicationContext(),
												VaccinationRemindService.class));
									}
									startService(new Intent(
											getApplicationContext(),
											VaccinationRemindService.class));

								} else {
									PublicMethod.showToast(
											getApplicationContext(),
											"未到应接种时间不能完成接种");
									return;
								}
							} else {
								mVaccinationFinish.setText("未接种");
								vaccination.setFinish_time(null);
							}
							mVaccinationDao.updateFinishTimeById(vaccination);
						}
					});
			finishDialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			finishDialog.create();
			finishDialog.show();
			break;
		case R.id.vac_detail_llay_vaccine_name:
			Intent intent = new Intent(this, VaccineIntroActivity.class);
			intent.putExtra("VaccineName", mVaccination.getVaccine_name());
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
