package cn.mointe.vaccination.activity;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.dao.VaccineSpecificationDao;
import cn.mointe.vaccination.domain.VaccineSpecfication;

public class SpecificationActivity extends ActionBarActivity {

	private VaccineSpecificationDao mVaccineSpecificationDao;

	private TextView mVaccine_name;
	private TextView mProduct_name;
	private TextView mManufacturers;
	private TextView mPrice;
	private TextView mFunctionanduse;
	private TextView mDescription;
	private TextView mInoculation_object;
	// private TextView mVaccination_image_url;
	private TextView mProduct_specification;
	private TextView mImmune_procedure;
	private TextView mAdverse_reaction;
	private TextView mContraindication;
	private TextView mCaution;
	private TextView mLicense_number;
	private TextView mValidity_period;

	private ActionBar mBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specification);

		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 应用程序图标加上一个返回的图标
		mBar.setHomeButtonEnabled(true);

		// 初始化控件
		mVaccine_name = (TextView) findViewById(R.id.tv_vaccine_name_show);
		mProduct_name = (TextView) findViewById(R.id.tv_product_name_show);
		mManufacturers = (TextView) findViewById(R.id.tv_manufacturers_show);
		mPrice = (TextView) findViewById(R.id.tv_price_show);
		mFunctionanduse = (TextView) findViewById(R.id.tv_functionanduse_show);
		mDescription = (TextView) findViewById(R.id.tv_description_show);
		mInoculation_object = (TextView) findViewById(R.id.tv_inoculation_object_show);
		mProduct_specification = (TextView) findViewById(R.id.tv_product_specification_show);
		mImmune_procedure = (TextView) findViewById(R.id.tv_immune_procedure_show);
		mAdverse_reaction = (TextView) findViewById(R.id.tv_adverse_reaction_show);
		mContraindication = (TextView) findViewById(R.id.tv_contraindication_show);
		mCaution = (TextView) findViewById(R.id.tv_caution_show);
		mLicense_number = (TextView) findViewById(R.id.tv_license_number_show);
		mValidity_period = (TextView) findViewById(R.id.tv_validity_period_show);

		mVaccineSpecificationDao = new VaccineSpecificationDao(this);

		String vaccineName = getIntent().getStringExtra("vaccineName");
		String productName = getIntent().getStringExtra("product_name");
		String manufacturers = getIntent().getStringExtra("manufacturers");

		// 查询说明书
		VaccineSpecfication vaccineSpecfication = null;
		try {
			vaccineSpecfication = mVaccineSpecificationDao
					.getVaccineSpecfication(vaccineName, productName, manufacturers);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		mBar.setTitle(vaccineSpecfication.getVaccine());

		// 设置对应的值
		mVaccine_name.setText(vaccineSpecfication.getVaccine_name());
		mProduct_name.setText(vaccineSpecfication.getProduct_name());
		mManufacturers.setText(vaccineSpecfication.getManufacturers());
		mPrice.setText(vaccineSpecfication.getPrice());
		mFunctionanduse.setText(vaccineSpecfication.getFunctionanduse());
		mDescription.setText(vaccineSpecfication.getDescription());
		mInoculation_object
				.setText(vaccineSpecfication.getInoculation_object());
		mProduct_specification.setText(vaccineSpecfication
				.getProduct_specification());
		mImmune_procedure.setText(vaccineSpecfication.getImmune_procedure());
		mAdverse_reaction.setText(vaccineSpecfication.getAdverse_reaction());
		mContraindication.setText(vaccineSpecfication.getContraindication());
		mCaution.setText(vaccineSpecfication.getCaution());
		mLicense_number.setText(vaccineSpecfication.getLicense_number());
		mValidity_period.setText(vaccineSpecfication.getValidity_period());

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
