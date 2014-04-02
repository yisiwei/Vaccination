package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import cn.mointe.vaccination.tools.Constants;

/**
 * 疫苗简介界面
 * 
 * @author Livens
 * 
 */
public class VaccineIntroActivity extends ActionBarActivity {

	private VaccineDao mVaccineDao;
	private VaccineSpecificationDao mSpecificationDao;

	private TextView mPrevent;
	private TextView mKind;

	private ListView mRelationalProduction;
	private SimpleAdapter mSimpleAdapter;
	private List<VaccineSpecfication> mlist;
	private String mVaccineName;

	private ActionBar mBar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vaccine_intro);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		mRelationalProduction = (ListView) findViewById(R.id.lv_vaccine_intro);

		View header = LayoutInflater.from(this).inflate(
				R.layout.vaccine_intro_header, null);
		mPrevent = (TextView) header.findViewById(R.id.tv_vaccine_prevent);
		mKind = (TextView) header.findViewById(R.id.tv_vaccine_kind);
		mRelationalProduction.addHeaderView(header);

		mVaccineDao = new VaccineDao(this);
		mSpecificationDao = new VaccineSpecificationDao(this);

		mVaccineName = getIntent().getStringExtra("VaccineName");
		Log.i("MainActivity", "mVaccineName==" + mVaccineName);
		mBar.setTitle(mVaccineName);

		Vaccine vaccine = null;
		try {
			vaccine = mVaccineDao.getVaccineByName(mVaccineName);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (null != vaccine) {
			mPrevent.setText(vaccine.getVaccine_prevent_disease());
			mKind.setText(vaccine.getVaccine_type());
		}

		String[] from = new String[] { "product_name", "price", "manufacturers" };
		int[] to = new int[] { R.id.tv_vaccine_name_item, R.id.tv_price_item,
				R.id.tv_manufacturers_item };

		List<Map<String, Object>> list = getVaccineSpecfications();
		if (list != null) {
			mSimpleAdapter = new SimpleAdapter(this, getVaccineSpecfications(),
					R.layout.vaccine_intro_item, from, to);
			mRelationalProduction.setAdapter(mSimpleAdapter);
		}
		mRelationalProduction.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				
				// Map<String, Object> map = (Map<String, Object>)
				// mRelationalProduction
				// .getItemAtPosition(position);
				Log.i(Constants.TAG, "position=" + position);
				Map<String, Object> map = (Map<String, Object>) parent
						.getAdapter().getItem(position);
				
				if (position > 0 ) {
					Intent intent = new Intent(getApplicationContext(),
							SpecificationActivity.class);
					intent.putExtra("vaccineName", mVaccineName);
					intent.putExtra("product_name", map.get("product_name")
							.toString());
					intent.putExtra("manufacturers", map.get("manufacturers")
							.toString());
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 查询产品
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getVaccineSpecfications() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			mlist = mSpecificationDao
					.getVaccineSpecficationByName(mVaccineName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		if (null != mlist) {
			for (VaccineSpecfication specfication : mlist) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("product_name", specfication.getProduct_name());
				map.put("price", specfication.getPrice());
				map.put("manufacturers", specfication.getManufacturers());
				list.add(map);
			}
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
