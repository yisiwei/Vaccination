package cn.mointe.vaccination.activity;

import java.io.IOException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.dao.VaccineSpecificationDao;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.domain.VaccineSpecfication;
import cn.mointe.vaccination.tools.Log;

import com.umeng.analytics.MobclickAgent;

/**
 * 疫苗简介界面
 * 
 * @author Livens
 * 
 */
public class VaccineIntroActivity extends Activity {

	private VaccineDao mVaccineDao;
	private VaccineSpecificationDao mSpecificationDao;

	private TextView mPrevent;
	private TextView mKind;

	private ListView mRelationalProduction;
	private IntroAdapter mIntroAdapter;
	private List<VaccineSpecfication> mList;
	private String mVaccineName;

	private TextView mTitleText;
	private ImageButton mTitleLeftImgbtn;// title左边图标
	private ImageButton mTitleRightImgbtn;// title右边图标

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vaccine_intro);

		mTitleText = (TextView) this.findViewById(R.id.title_text);
		mTitleLeftImgbtn = (ImageButton) this
				.findViewById(R.id.title_left_imgbtn);
		mTitleRightImgbtn = (ImageButton) this
				.findViewById(R.id.title_right_imgbtn);

		mTitleLeftImgbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				VaccineIntroActivity.this.finish();
			}
		});
		mTitleRightImgbtn.setVisibility(View.GONE);

		mRelationalProduction = (ListView) findViewById(R.id.lv_vaccine_intro);
		// mPrevent = (TextView) findViewById(R.id.tv_vaccine_prevent);
		// mKind = (TextView) findViewById(R.id.tv_vaccine_kind);

		View header = LayoutInflater.from(this).inflate(
				R.layout.vaccine_intro_header, null);
		mPrevent = (TextView) header.findViewById(R.id.tv_vaccine_prevent);
		mKind = (TextView) header.findViewById(R.id.tv_vaccine_kind);
		mRelationalProduction.addHeaderView(header, null, false);

		mVaccineDao = new VaccineDao(this);
		mSpecificationDao = new VaccineSpecificationDao(this);

		mVaccineName = getIntent().getStringExtra("VaccineName");
		Log.i("MainActivity", "mVaccineName==" + mVaccineName);
		mTitleText.setText(mVaccineName);

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

		mList = getVaccineSpecfications();
		if (mList != null) {
			mIntroAdapter = new IntroAdapter(this, mList);
			mRelationalProduction.setAdapter(mIntroAdapter);
		}

		mRelationalProduction.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Log.i(Constants.TAG, "position=" + position);
				VaccineSpecfication specfication = (VaccineSpecfication) parent
						.getAdapter().getItem(position);
				if (position > 0) {
					Intent intent = new Intent(getApplicationContext(),
							SpecificationActivity.class);
					intent.putExtra("vaccineName", mVaccineName);
					intent.putExtra("product_name",
							specfication.getProduct_name());
					intent.putExtra("manufacturers",
							specfication.getManufacturers());
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("VaccineIntroActivity"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("VaccineIntroActivity");
		MobclickAgent.onPause(this);
	}

	/**
	 * 查询产品
	 * 
	 * @return
	 */
	public List<VaccineSpecfication> getVaccineSpecfications() {
		List<VaccineSpecfication> list = null;
		try {
			list = mSpecificationDao.getVaccineSpecficationByName(mVaccineName);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return list;
	}

	private class IntroAdapter extends BaseAdapter {

		List<VaccineSpecfication> vaccineSpecfications;
		LayoutInflater inflater;

		public IntroAdapter(Context context,
				List<VaccineSpecfication> vaccineSpecfications) {
			this.vaccineSpecfications = vaccineSpecfications;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return vaccineSpecfications.size();
		}

		@Override
		public Object getItem(int position) {
			return vaccineSpecfications.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			TextView productNameView;
			TextView manufacturersView;
			TextView priceView;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.vaccine_intro_item,
						null);
				productNameView = (TextView) convertView
						.findViewById(R.id.tv_vaccine_name_item);
				manufacturersView = (TextView) convertView
						.findViewById(R.id.tv_manufacturers_item);
				priceView = (TextView) convertView
						.findViewById(R.id.tv_price_item);

				ViewCache cache = new ViewCache();
				cache.productNameView = productNameView;
				cache.manufacturersView = manufacturersView;
				cache.priceView = priceView;

				convertView.setTag(cache);
			} else {
				ViewCache cache = (ViewCache) convertView.getTag();

				productNameView = cache.productNameView;
				manufacturersView = cache.manufacturersView;
				priceView = cache.priceView;
			}

			VaccineSpecfication vaccineSpecfication = vaccineSpecfications
					.get(position);
			productNameView.setText(vaccineSpecfication.getProduct_name());
			manufacturersView.setText(vaccineSpecfication.getManufacturers());
			priceView.setText(vaccineSpecfication.getPrice());

			return convertView;
		}

	}

	private final class ViewCache {
		public TextView productNameView;
		public TextView manufacturersView;
		public TextView priceView;
	}

}
