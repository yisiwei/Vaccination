package cn.mointe.vaccination.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.activity.RegisterBabyActivity;
import cn.mointe.vaccination.activity.VaccinationDetailActivity;
import cn.mointe.vaccination.activity.WeatherActivity;
import cn.mointe.vaccination.adapter.VaccinationAdapter;
import cn.mointe.vaccination.dao.BabyDao;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Baby;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.tools.BitmapUtil;
import cn.mointe.vaccination.tools.WebServiceUtil;

/**
 * 疫苗接种列表界面
 * 
 * @author yi_siwei
 * 
 */
public class VaccineListFragment extends Fragment implements OnClickListener {

	private Button mLoadWeatherBtn;
	private Button mNextVaccinationBtn;// 距离下次接种时间

	private ListView mListView;
	private VaccinationAdapter mAdapter;

	private BabyDao mBabyDao;
	private VaccinationDao mVaccinationDao;

	private long mRemindDay;// 距离下次接种天数

	private int mCurrent = 0;// 用于定位到下次接种的疫苗

	private ImageButton mBabyImg;
	private TextView mBabyName; // baby昵称
	private TextView mBabyAge;// baby月龄
	private Baby mDefaultBaby; // 默认baby

	private ImageView mWeatherImg;
	private TextView mWeather;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mVaccinationDao = new VaccinationDao(getActivity());
		mBabyDao = new BabyDao(getActivity());
	}

	@SuppressLint({ "SimpleDateFormat", "NewApi" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		View view = inflater.inflate(R.layout.fragment_vaccine_list, null);

		mDefaultBaby = mBabyDao.getDefaultBaby();

		mLoadWeatherBtn = (Button) view.findViewById(R.id.btn_load_weather);
		mLoadWeatherBtn.setOnClickListener(this);

		mWeather = (TextView) view.findViewById(R.id.vac_list_temperature);
		mWeatherImg = (ImageView) view.findViewById(R.id.vac_list_iv_weather);

		mBabyImg = (ImageButton) view.findViewById(R.id.vac_list_iv_babyImg);
		mBabyName = (TextView) view.findViewById(R.id.vac_list_tv_babyName);
		mBabyAge = (TextView) view.findViewById(R.id.vac_list_tv_babyAge);

		mNextVaccinationBtn = (Button) view
				.findViewById(R.id.vaccine_btn_next_vaccination);
		mNextVaccinationBtn.setOnClickListener(this);

		List<Vaccination> vaccinations = getVaccinations();
		mListView = (ListView) view.findViewById(R.id.vaccine_list_lv);
		mAdapter = new VaccinationAdapter(getActivity(), vaccinations);
		mListView.setAdapter(mAdapter);

		// getWeather("固安");

		// 计算距离下次接种时间
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			int index = 0;
			for (Vaccination vaccination : vaccinations) {
				index++;
				String reserve_time = vaccination.getReserve_time();// 取得预约接种时间
				Date vaccineFormatDate = format.parse(reserve_time);// 转化为date类型

				String todayString = format.format(date);
				Date today = format.parse(todayString);

				int result = today.compareTo(vaccineFormatDate);// 与当前时间比较
				if (result <= 0) {
					mRemindDay = (vaccineFormatDate.getTime() - today.getTime())
							/ (1000 * 60 * 60 * 24);
					String remind = String.format(
							getResources().getString(R.string.distance),
							mRemindDay);
					mNextVaccinationBtn.setText(remind);
					mCurrent = index - 1;// 下标从0开始，所以-1
					break;
				}

			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		mListView.setSelectionFromTop(mCurrent, 20);// 定位到指定item
		mAdapter.setSelectItem(mCurrent);
		mAdapter.notifyDataSetInvalidated();

		if (mDefaultBaby != null) {
			mBabyName.setText(mDefaultBaby.getName());
			mBabyAge.setText(mDefaultBaby.getBirthdate());
			String imgUri = mDefaultBaby.getImage();
			Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imgUri, 96,
					120);

			if (!TextUtils.isEmpty(imgUri)) {
				mBabyImg.setImageBitmap(bitmap);
			} else {
				mBabyImg.setImageResource(R.drawable.head);
			}
		}

		// ListView Item 点击监听
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),
						VaccinationDetailActivity.class);
				Vaccination vaccination = (Vaccination) mAdapter
						.getItem(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("Vaccination", vaccination);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// baby头像点击
		mBabyImg.setOnClickListener(this);

		mAdapter.notifyDataSetChanged();
		return view;
	}

	// 得到Vaccination List
	private List<Vaccination> getVaccinations() {

		return mVaccinationDao.loadVaccinations(mDefaultBaby.getName());
	}

	private void getWeather(String city) {
		SoapObject detail = WebServiceUtil.getWeatherByCity(city);
		mWeather.setText(detail.getProperty(8).toString());

		int icon = WeatherActivity.parseIcon(detail.getProperty(10).toString());
		mWeatherImg.setImageResource(icon);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// 点击监听
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_load_weather: // 查看未来3天天气
			intent = new Intent(getActivity(), WeatherActivity.class);
			startActivity(intent);
			break;
		case R.id.vaccine_btn_next_vaccination:
			mListView.setSelectionFromTop(mCurrent, 20);// 定位到指定item
			mAdapter.setSelectItem(mCurrent);
			mAdapter.notifyDataSetInvalidated();
			break;
		case R.id.vac_list_iv_babyImg:
			intent = new Intent(getActivity(), RegisterBabyActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putSerializable("baby", mDefaultBaby);
			intent.putExtras(mBundle);
			startActivity(intent);

			break;
		default:
			break;
		}
	}

}
