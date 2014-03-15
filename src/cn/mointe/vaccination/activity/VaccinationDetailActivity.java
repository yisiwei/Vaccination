package cn.mointe.vaccination.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Vaccination;

/**
 * 疫苗接种详情界面
 * 
 */
public class VaccinationDetailActivity extends Activity implements
		OnClickListener {
	
	private VaccinationDao mVaccinationDao;

	private Vaccination mVaccination;

	private TextView mVaccineName;// 疫苗名称
	private Button mVaccinationTime;// 应接种时间
	private Button mVaccinationFinish;// 完成接种
	private TextView mVaccinationNumber;// 接种剂次
	private TextView mPreventDisease;// 预防疾病
	
	private LinearLayout mVaccineNameLayout;
	
	private ActionBar mBar;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccination_detail);
		
		mVaccinationDao = new VaccinationDao(this);
		
		mBar = getActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标

		mVaccination = (Vaccination) getIntent().getSerializableExtra(
				"Vaccination");
		
		mVaccineNameLayout = (LinearLayout) this.findViewById(R.id.vac_detail_llay_vaccine_name);

		mVaccineName = (TextView) this
				.findViewById(R.id.vac_detail_tv_vaccina_name);
		mVaccinationTime = (Button) this
				.findViewById(R.id.vac_detail_btn_vaccina_date);
		mVaccinationFinish = (Button) this
				.findViewById(R.id.vac_detail_btn_vaccina_finish);
		mVaccinationNumber = (TextView) this
				.findViewById(R.id.vac_detail_tv_vaccina_number);
		mPreventDisease = (TextView) this
				.findViewById(R.id.vac_detail_tv_prevent_disease);

		mVaccineName.setText(mVaccination.getVaccine_name());
		mVaccinationTime.setText(mVaccination.getReserve_time());
		String vaccineFinish = mVaccination.getFinish_time();
		if (!TextUtils.isEmpty(vaccineFinish)) {
			mVaccinationFinish.setText("已接种");
		} else {
			mVaccinationFinish.setText("未接种");
		}
		mVaccinationNumber.setText(mVaccination.getVaccination_number());
		mPreventDisease.setText("疫苗说明书查");

		mVaccineNameLayout.setOnClickListener(this);
		mVaccinationTime.setOnClickListener(this);
		mVaccinationFinish.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vac_detail_btn_vaccina_date: // 修改接种时间
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int monthOfYear = calendar.get(Calendar.MONDAY);
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			new DatePickerDialog(this, new OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,
						int monthOfYear, int dayOfMonth) {
					String month = null;
					String day = null;
					if ((monthOfYear + 1) < 10) {
						month = "0" + (monthOfYear + 1);
					} else {
						month = String.valueOf(monthOfYear + 1);
					}
					if (dayOfMonth < 10) {
						day = "0" + dayOfMonth;
					} else {
						day = String.valueOf(dayOfMonth);
					}
					mVaccinationTime.setText(year + "-" + month + "-" + day);

				}
			}, year, monthOfYear, dayOfMonth).show();
			break;
		case R.id.vac_detail_btn_vaccina_finish: // 完成接种
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			if (mVaccinationFinish.getText().toString().equals("未接种")) {
				builder.setMessage("确定完成接种？");
			} else {
				builder.setMessage("确定未完成接种？");
			}
			builder.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (mVaccinationFinish.getText().toString()
									.equals("未接种")) {
								mVaccinationFinish.setText("已接种");
								Vaccination vaccination = new Vaccination();
								vaccination.setId(mVaccination.getId());
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								String finish_time = format.format(new Date());
								vaccination.setFinish_time(finish_time);
								mVaccinationDao.updateFinishTimeById(vaccination);
							} else {
								mVaccinationFinish.setText("未接种");
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
		case R.id.vac_detail_llay_vaccine_name:
			Toast.makeText(this, "跳转到疫苗库", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

}
