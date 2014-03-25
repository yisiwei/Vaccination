package cn.mointe.vaccination.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.dao.VaccineSpecificationDao;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.domain.VaccineSpecfication;

/**
 * 疫苗简介界面
 * 
 * @author Livens
 * 
 */
public class VaccineIntroActivity extends ActionBarActivity {

	private VaccineDao mVaccineDao;

	private TextView mPrevent;
	private TextView mKind;
	private ListView mRelationalProduction;
	private SimpleAdapter mSimpleAdapter;
	private List<VaccineSpecfication> mlist;
	private VaccineSpecificationDao mSpecificationDao;
	private String mVaccineName;

	private ActionBar mBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vaccine_intro);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		mPrevent = (TextView) findViewById(R.id.tv_vaccine_prevent);
		mKind = (TextView) findViewById(R.id.tv_vaccine_kind);
		mRelationalProduction = (ListView) findViewById(R.id.lv_vaccine_intro);

		mVaccineDao = new VaccineDao(this);
		mSpecificationDao = new VaccineSpecificationDao();

		mVaccineName = getIntent().getStringExtra("VaccineName");
		setTitle(mVaccineName);

		Vaccine vaccine = mVaccineDao.queryVaccineByName(mVaccineName);

		mPrevent.setText(vaccine.getVaccine_intro());
		mKind.setText(vaccine.getVaccine_type());

		String[] from = new String[] { "product_name", "price", "manufacturers" };
		int[] to = new int[] { R.id.tv_vaccine_name_item, R.id.tv_price_item,
				R.id.tv_manufacturers_item };
		mSimpleAdapter = new SimpleAdapter(this, getVaccineSpecfication(),
				R.layout.vaccine_intro_item, from, to);

		mRelationalProduction.setAdapter(mSimpleAdapter);

		mRelationalProduction.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(getApplicationContext(),
						SpecificationActivity.class);
				Map<String, Object> map = (Map<String, Object>) mRelationalProduction
						.getItemAtPosition(position);
				intent.putExtra("product_name", map.get("product_name")
						.toString());
				intent.putExtra("manufacturers", map.get("manufacturers")
						.toString());
				startActivity(intent);
			}
		});
	}

	public List<Map<String, Object>> getVaccineSpecfication() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		mlist = mSpecificationDao.queryVaccineSpecficationByName(mVaccineName);
		for (VaccineSpecfication specfication : mlist) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("product_name", specfication.getProduct_name());
			map.put("price", specfication.getPrice());
			map.put("manufacturers", specfication.getManufacturers());
			list.add(map);
		}
		return list;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
