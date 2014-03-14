package cn.mointe.vaccination.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.domain.Vaccination;

/**
 * 疫苗接种详情界面
 * 
 */
public class VaccinationDetailActivity extends Activity implements
		OnClickListener, OnItemClickListener {

	private ListView mVaccineListView;
	private ListView mSpecificationListView;
	private SimpleAdapter mVaccineAdapter;
	private SimpleAdapter mSpecificationAdapter;

	private Vaccination mVaccination;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vaccination_detail);

		mVaccination = (Vaccination) getIntent().getSerializableExtra(
				"Vaccination");

		mVaccineListView = (ListView) this
				.findViewById(R.id.vac_detail_lv_vaccine);
		mSpecificationListView = (ListView) this
				.findViewById(R.id.vac_detail_lv_specification);

		mVaccineAdapter = new SimpleAdapter(this, getVaccineData(),
				R.layout.vaccination_detail_vaccine_item, new String[] {
						"lable", "value" }, new int[] { R.id.tv_lable,
						R.id.tv_value });
		mVaccineListView.setAdapter(mVaccineAdapter);

		mSpecificationAdapter = new SimpleAdapter(this, getSpecificationData(),
				R.layout.vaccination_detail_specification_item, new String[] {
						"lable", "value" }, new int[] { R.id.tv_specification_lable,
						R.id.tv_specification_value });
		mSpecificationListView.setAdapter(mSpecificationAdapter);
	}

	private List<Map<String, Object>> getVaccineData() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lable", "疫苗名称");
		map.put("value", mVaccination.getVaccine_name());
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("lable", "应接种时间");
		map.put("value", mVaccination.getReserve_time());
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("lable", "接种剂次");
		map.put("value", mVaccination.getVaccination_number());
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("lable", "预防疾病");
		map.put("value", "疫苗说明书查");
		list.add(map);

		return list;
	}

	private List<Map<String, Object>> getSpecificationData() {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lable", "【接种对象】");
		map.put("value", "疫苗说明书查");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("lable", "【注意事项】");
		map.put("value", "疫苗说明书查");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("lable", "【不良反应】");
		map.put("value", "疫苗说明书查");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("lable", "【禁忌】");
		map.put("value", "疫苗说明书查");
		list.add(map);

		map = new HashMap<String, Object>();
		map.put("lable", "【免疫程序】");
		map.put("value", "疫苗说明书查");
		list.add(map);

		return list;
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (view.getId()) {
		case R.id.vac_detail_lv_vaccine:
			// 跳转到疫苗库
			break;

		default:
			break;
		}

	}

}
