package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.other.VaccinationPreferences;
import cn.mointe.vaccination.service.VaccinationRemindService;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.DateUtils;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PackageUtil;
import cn.mointe.vaccination.tools.PublicMethod;
import cn.mointe.vaccination.tools.StringUtils;

/**
 * 疫苗接种界面
 * 
 */
public class VaccinationDetailActivity extends ActionBarActivity implements
		OnClickListener {

	private VaccinationDao mVaccinationDao;
	private VaccineDao mVaccineDao;

	private Vaccination mVaccination;

	private TextView mVaccineName;// 疫苗名称
	private Button mVaccinationTime;// 预约时间
	private Button mVaccinationFinish;// 完成接种
	private TextView mVaccinationNumber;// 接种剂次
	private TextView mPreventDisease;// 预防疾病

	private TextView mVaccinationObject;// 接种对象
	private TextView mVaccinationAnnouncements;// 注意事项
	private TextView mVaccinationAdverseReaction;// 不良反应
	private TextView mVaccinationContraindication;// 禁忌
//	private TextView mVccinationImmuneProcedure;// 免疫程序

	private LinearLayout mVaccineNameLayout;

	private String mVaccinationDate = null;// 实际接种时间

	private ActionBar mBar;
	private VaccinationPreferences mPreferences;

	private String mShouldVaccinationDate;// 修改的预约时间

	private String mBirthdate; // 宝宝出生日期

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccination_detail);
		
		mVaccinationDao = new VaccinationDao(this);
		mVaccineDao = new VaccineDao(this);
		mPreferences = new VaccinationPreferences(this);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		// 主界面传过来的Vaccination对象
		mVaccination = (Vaccination) getIntent().getSerializableExtra(
				"Vaccination");
		mBirthdate = getIntent().getStringExtra("birthdate");
		Log.i("MainActivity", "出生日期=" + mBirthdate);

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
//		mVccinationImmuneProcedure = (TextView) this
//				.findViewById(R.id.vaccination_immune_procedure);// 免疫程序

		mVaccineName.setText(mVaccination.getVaccine_name());
		mVaccinationTime.setText(mVaccination.getReserve_time());
		mVaccinationNumber.setText(mVaccination.getVaccination_number());
		String vaccineFinish = mVaccination.getFinish_time();
		if (!StringUtils.isNullOrEmpty(vaccineFinish)) {
			mVaccinationFinish.setText(R.string.finish_vaccination);//已接种
		} else {
			mVaccinationFinish.setText(R.string.not_vaccination);//未接种
		}

		Vaccine vaccine = null;
		try {
			vaccine = mVaccineDao.getVaccineByName(mVaccination
					.getVaccine_name());
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null != vaccine) {
			mPreventDisease.setText(vaccine.getVaccine_prevent_disease());// 预防疾病
			mVaccinationObject.setText(vaccine.getInoculation_object());// 接种对象
			mVaccinationAnnouncements.setText(vaccine.getCaution());// 注意事项
			mVaccinationAdverseReaction.setText(vaccine.getAdverse_reaction());// 不良反应
			mVaccinationContraindication.setText(vaccine.getContraindication());// 禁忌
//			mVccinationImmuneProcedure.setText(vaccine.getImmune_procedure());// 免疫程序
		}

		mVaccineNameLayout.setOnClickListener(this);
		mVaccinationTime.setOnClickListener(this);
		mVaccinationFinish.setOnClickListener(this);

	}

	/**
	 * 点击监听
	 */
	@SuppressLint("NewApi")
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
		case R.id.vac_detail_btn_vaccina_date: // 修改预约时间

			if (mVaccinationFinish.getText().toString().equals("已接种")) {
				PublicMethod.showToast(getApplicationContext(),
						R.string.finish_vaccine_is_not_update_time);
				return;
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.set_vaccination_datetime);// 提示：设置预约时间

			LayoutInflater inflater = LayoutInflater.from(this);
			View view = inflater.inflate(R.layout.set_date, null);
			DatePicker datePicker = (DatePicker) view
					.findViewById(R.id.datePicker);
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
							Log.i(Constants.TAG, "应接种时间="
									+ mShouldVaccinationDate);
						}
					});

			builder.setView(view);
			// 确定按钮
			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// 如果改变了应接种时间
							if (mShouldVaccinationDate != null) {
								int result = 0;
								try {
									result = DateUtils
											.compareDateToToday(mShouldVaccinationDate);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								if (result == 1 || result == 0) {
									mVaccinationTime
											.setText(mShouldVaccinationDate);
									// 修改接种时间
									mVaccinationDao.updateReserveTimeById(
											mVaccination.getId(),
											mShouldVaccinationDate);
									// 将修改的时间更新到Preferences
									mPreferences
											.setRemindDate(mShouldVaccinationDate);
									Intent remindService = new Intent(
											getApplicationContext(),
											VaccinationRemindService.class);
									// 如果服务在运行，重新启动
									if (PackageUtil.isServiceRunning(
											getApplicationContext(),
											Constants.REMIND_SERVICE)) {
										stopService(remindService);
									}
									startService(remindService);// 启动服务
								} else {
									// 预约时间不能小于今天
									PublicMethod
											.showToast(
													getApplicationContext(),
													R.string.vaccination_datetime_is_not_less_than_today);
								}

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

			break;
		case R.id.vac_detail_btn_vaccina_finish: // 完成接种
			AlertDialog.Builder finishDialog = new AlertDialog.Builder(this);
			// finishDialog.setTitle(R.string.hint);// 提示
			if (mVaccinationFinish.getText().toString().equals("未接种")) {
				finishDialog.setTitle(R.string.choose_finish_time);// 提示 选择完成时间

				LayoutInflater layoutInflater = LayoutInflater.from(this);
				View finishView = layoutInflater.inflate(R.layout.set_date,
						null);

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
									stringBuilder.append("-0").append(
											dayOfMonth);
								} else {
									stringBuilder.append("-")
											.append(dayOfMonth);
								}

								mVaccinationDate = stringBuilder.toString();
								Log.i(Constants.TAG, "完成接种时间="
										+ mVaccinationDate);
							}
						});

				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
					try {
						Date maxDate = DateUtils.formatDate(new Date());
						finishDatePicker.setMaxDate(maxDate.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				finishDialog.setView(finishView);
			} else {
				finishDialog.setTitle(R.string.hint);// 提示
				finishDialog.setMessage(R.string.sure_update_unvaccinated);//确定改为未接种？
			}
			finishDialog.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// XXX 各种条件判断 好乱啊
							Vaccination vaccination = new Vaccination();
							vaccination.setId(mVaccination.getId());
							if (mVaccinationFinish.getText().toString()
									.equals("未接种")) {//"未接种"
								try {
									String reserveTime = mVaccinationTime
											.getText().toString();// 预约时间

									if (mVaccinationDate == null) {
										StringBuilder stringBuilder = new StringBuilder();
										stringBuilder.append(year);
										int newMonth = monthOfYear + 1;
										if (newMonth < 10) {
											stringBuilder.append("-0").append(
													newMonth);
										} else {
											stringBuilder.append("-").append(
													newMonth);
										}
										if (dayOfMonth < 10) {
											stringBuilder.append("-0").append(
													dayOfMonth);
										} else {
											stringBuilder.append("-").append(
													dayOfMonth);
										}
										mVaccinationDate = stringBuilder
												.toString();
									}
									// 当前时间与预约时间比较
									int result = DateUtils
											.compareDateToToday(reserveTime);
									// 与出生日期比较
									int data = DateUtils.compareDate(
											mVaccinationDate, mBirthdate);
									// 与当前时间比较
									int nowData = DateUtils
											.compareDateToToday(mVaccinationDate);
									if (data == -1) {
										PublicMethod
												.showToast(
														getApplicationContext(),
														R.string.vaccinate_is_not_less_birthday);// "接种时间不能小于出生日期"
										return;
									}
									
									if (nowData == 1) {
										PublicMethod
												.showToast(
														getApplicationContext(),
														R.string.vaccinate_is_not_more_then_today);// 接种时间不能大于今天
										return;
									}

									if (result == 1) {
										// 未到预约时间 完成接种
										finishVaccination(mVaccinationDate);
										return;
									}
									vaccination
											.setFinish_time(mVaccinationDate);

									mVaccinationFinish.setText(R.string.finish_vaccination);//已接种
									String nextRemindDate = null; // 下次提醒日期
									// 查询下次提醒日期
									nextRemindDate = mVaccinationDao.findNextVaccinationDate(
											mVaccination.getBaby_nickname(),
											mVaccination.getReserve_time());
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

								} catch (ParseException e) {
									e.printStackTrace();
								}

							} else {
								mVaccinationFinish.setText(R.string.not_vaccination);//未接种
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

	/**
	 * 预约时间在今天后的情况下完成接种
	 */
	public void finishVaccination(final String vaccinationDate) {
		AlertDialog.Builder dLog = new AlertDialog.Builder(
				VaccinationDetailActivity.this);
		dLog.setTitle(R.string.hint);
		dLog.setMessage(getResources().getString(R.string.is_not_reserve_time));// 未到预约时间，确定要完成接种吗?
		dLog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Vaccination vaccination = new Vaccination();
				vaccination.setId(mVaccination.getId());

				mVaccinationFinish.setText(R.string.finish_vaccination);//已接种
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
				if (PackageUtil.isServiceRunning(getApplicationContext(),
						Constants.REMIND_SERVICE)) {
					stopService(new Intent(getApplicationContext(),
							VaccinationRemindService.class));
				}
				startService(new Intent(getApplicationContext(),
						VaccinationRemindService.class));
				vaccination.setFinish_time(vaccinationDate);
				mVaccinationDao.updateFinishTimeById(vaccination);

			}
		});
		dLog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Log.i("MainActivity", "未到预约时间，取消完成接种");
			}
		});
		dLog.create();
		dLog.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
