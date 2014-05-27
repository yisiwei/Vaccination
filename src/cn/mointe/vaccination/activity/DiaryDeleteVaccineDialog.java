package cn.mointe.vaccination.activity;

import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DiaryDeleteVaccineDialog extends Activity {

	private BabyDao mBabyDao;
	private VaccinationDao mVaccinationDao;

	private Button mConfirmBtn;
	private Button mCancelBtn;

	private String mVaccineName;
	private String mVaccineNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_delete_vaccine_dialog);

		mBabyDao = new BabyDao(this);
		mVaccinationDao = new VaccinationDao(this);

		mCancelBtn = (Button) this.findViewById(R.id.diary_delete_cancel_btn);
		mConfirmBtn = (Button) this.findViewById(R.id.diary_delete_confirm_btn);

		mVaccineName = getIntent().getStringExtra("vaccineName");
		mVaccineNumber = getIntent().getStringExtra("vaccineNumber");

		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DiaryDeleteVaccineDialog.this.finish();
			}
		});

		mConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean result = mVaccinationDao.cancelVaccination(mBabyDao
						.getDefaultBaby().getName(), mVaccineName,
						mVaccineNumber, null);
				if (result) {
					DiaryDeleteVaccineDialog.this.finish();
					Toast.makeText(DiaryDeleteVaccineDialog.this, "成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(DiaryDeleteVaccineDialog.this, "失败",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

}
