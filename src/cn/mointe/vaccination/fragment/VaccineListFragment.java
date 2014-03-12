package cn.mointe.vaccination.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.WeatherActivity;
import cn.mointe.vaccination.adapter.VaccinationAdapter;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Vaccination;

/**
 * 疫苗接种列表界面
 * 
 * @author yi_siwei
 * 
 */
public class VaccineListFragment extends Fragment implements OnClickListener {

	private Button mLoadWeatherBtn;
	private Button mFindNextVaccination;
	
	private ListView mListView;
	private VaccinationAdapter mAdapter;
	
	private VaccinationDao mDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDao = new VaccinationDao(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_vaccine_list, null);
		mLoadWeatherBtn = (Button) view.findViewById(R.id.btn_load_weather);
		mLoadWeatherBtn.setOnClickListener(this);
		mFindNextVaccination = (Button) view
				.findViewById(R.id.find_next_vaccination);
		mFindNextVaccination.setOnClickListener(this);
		
		mListView = (ListView) view.findViewById(R.id.vaccine_list_lv);
		mAdapter = new VaccinationAdapter(getActivity(), getVaccinations());

		return view;
	}
	
	// 得到Vaccination List
	private List<Vaccination> getVaccinations(){
		List<Vaccination> vaccinations = new ArrayList<Vaccination>();
		String[] vaccinationArray = getResources().getStringArray(R.array.vaccines);
		Vaccination vaccination = null;
		for (int i = 0; i < vaccinationArray.length; i++) {
			vaccination = new Vaccination();
			mDao.saveVaccination(vaccination);
		}
		return vaccinations;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_load_weather: // 查看未来3天天气
			Intent intent = new Intent(getActivity(), WeatherActivity.class);
			startActivity(intent);
			break;
		case R.id.find_next_vaccination:

			break;
		default:
			break;
		}
	}


}
