package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.adapter.ChooseAdapter;
import cn.mointe.vaccination.adapter.ChooseAdapter.OnSelectedItemChanged;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.StringUtils;

public class AddTwoTypeVaccineActivity extends ActionBarActivity {

	private Button mReserveBtn;
	private Button mCancelBtn;

	private ListView mVaccineList;
	private ChooseAdapter mReserveAdapter;

	private List<Vaccination> mSelectVaccinations;

	private VaccinationDao mVaccinationDao;

	private Baby mDefaultBaby;
	private String mReserveDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_twotype_vaccine);

		mVaccinationDao = new VaccinationDao(this);

		mDefaultBaby = (Baby) getIntent().getSerializableExtra("baby");
		mReserveDate = getIntent().getStringExtra("reserveDate");

		mReserveBtn = (Button) this.findViewById(R.id.add_two_type_btn_reserve);
		mCancelBtn = (Button) this.findViewById(R.id.add_two_type_btn_cancel);

		mVaccineList = (ListView) this.findViewById(R.id.add_two_type_list);

		mReserveAdapter = new ChooseAdapter(this, getVaccinations(),
				new OnSelectedItemChanged() {

					@Override
					public void getSelectedItem(Vaccination vaccination) {

					}

					@Override
					public void getSelectedCount(int count) {

					}
				});
		mVaccineList.setAdapter(mReserveAdapter);

		mReserveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSelectVaccinations = mReserveAdapter.currentSelect();
				for (Vaccination vaccination : mSelectVaccinations) {
					Log.i("MainActivity", vaccination.getVaccine_name() + "("
							+ vaccination.getVaccination_number() + ")");
				}
				if (!StringUtils.isNullOrEmpty(mReserveDate)) {
					mVaccinationDao.addTwoTypeVaccine(mSelectVaccinations,
							mDefaultBaby.getName(), mReserveDate);
					AddTwoTypeVaccineActivity.this.finish();
				}
			}
		});

		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddTwoTypeVaccineActivity.this.finish();
			}
		});
	}

	/**
	 * 得到二类疫苗List(不包括已接种或已预约的)
	 * 
	 * @return
	 */
	private List<Vaccination> getVaccinations() {
		List<Vaccination> vaccinations = null;
		List<Vaccination> vaccinationsDB = null;
		try {
			vaccinations = mVaccinationDao.queryTwoTypeVaccinesOfXml();
			vaccinationsDB = mVaccinationDao
					.queryTwoTypeVaccinesOfDB(mDefaultBaby.getName());
			if (vaccinationsDB != null && vaccinationsDB.size() != 0) {
				Iterator<Vaccination> iterator = vaccinations.iterator();
				while (iterator.hasNext()) {
					Vaccination vac = iterator.next();

					for (Vaccination vaccination : vaccinationsDB) {
						if (vac.getVaccine_name().equals(
								vaccination.getVaccine_name())
								&& vac.getVaccination_number().equals(
										vaccination.getVaccination_number())) {
							iterator.remove();
							Log.i("MainActivity", "size:" + vaccinations.size());
							continue;
						}
					}
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return vaccinations;
	}

}
