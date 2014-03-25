package cn.mointe.vaccination.activity;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.mointe.vaccination.R;
import cn.mointe.vaccination.tools.WebServiceUtil;

/**
 * 天气预报界面
 * 
 * @author yi_siwei
 * 
 */
public class WeatherActivity extends ActionBarActivity implements
		OnClickListener {

	public static final String TAG = "MainActivity";

	private ActionBar mBar;
	private View mView;// 自定义actionBar

	private TextView mWeaCityTv;
	private Button mWeaCityBtn;

	// private static final int CITY = 1;
	private Spinner mProvinceSpinner;// 省
	private Spinner mCitySpinner;// 市
	// private List<String> mProvinces;
	private String[] mProvinces;
	private List<String> mCitys;

	private String mCity;
	private MyTask mTask;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		String city = getIntent().getStringExtra("city");

		mView = getLayoutInflater().inflate(R.layout.weather_custom_actionbar,
				null);
		mBar = getSupportActionBar();
		mBar.setDisplayHomeAsUpEnabled(true);// 将应用程序图标设置为可点击的按钮，并在图标上添加左箭头
		mBar.setHomeButtonEnabled(true);// 将应用程序图标设置为可点击的按钮

		mBar.setDisplayShowCustomEnabled(true);// 显示自定义actionBar
		// 自定义actionBar
		mBar.setCustomView(mView, new ActionBar.LayoutParams(
				LayoutParams.WRAP_CONTENT, 80, Gravity.CENTER_VERTICAL
						| Gravity.RIGHT));

		mWeaCityTv = (TextView) mView.findViewById(R.id.wea_city_tv);
		mWeaCityBtn = (Button) mView.findViewById(R.id.wea_city_btn);
		mWeaCityBtn.setOnClickListener(this);

		mCity = city;
		mWeaCityTv.setText(mCity);
		// refresh(mCity);
		mTask = new MyTask();
		mTask.execute(mCity);
	}

	/**
	 * 创建菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "WeatherActivity...onDestroy");
	}

	// 点击监听
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.wea_city_btn:
			showCityDialog();
			break;

		default:
			break;
		}
	}

	private void showCityDialog() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.weather_city_list, null);
		// 省份Spinner
		mProvinceSpinner = (Spinner) view
				.findViewById(R.id.wea_province_spinner);
		// 城市Spinner
		mCitySpinner = (Spinner) view.findViewById(R.id.wea_city_spinner);
		// 省份列表
		// mProvinces = WebServiceUtil.getProvinceList();
		mProvinces = getResources().getStringArray(R.array.province);
		ArrayAdapter<String> provincesAdapter = new ArrayAdapter<String>(this,
				R.layout.select_item, mProvinces);
		provincesAdapter.setDropDownViewResource(R.layout.select_item);
		// 设置adapter
		mProvinceSpinner.setAdapter(provincesAdapter);
		// 省份Spinner监听
		mProvinceSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						mCitys = WebServiceUtil
								.getCityListByProvince(mProvinces[position]);
						ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(
								getApplicationContext(), R.layout.select_item,
								mCitys);
						cityAdapter
								.setDropDownViewResource(R.layout.select_item);
						mCitySpinner.setAdapter(cityAdapter);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
		// 城市Spinner监听
		mCitySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mCity = mCitys.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		// 选择城市对话框
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_city);// 标题：请选择城市
		builder.setView(view);
		// 添加确定按钮
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						mWeaCityTv.setText(mCity);
						// refresh(mCity);
						new MyTask().execute(mCity);
					}
				});
		// 添加取消按钮
		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create();
		// 显示Dialog
		builder.show();
	}

	private class MyTask extends AsyncTask<String, Void, SoapObject> {

		@Override
		protected SoapObject doInBackground(String... params) {
			SoapObject detail = WebServiceUtil.getWeatherByCity(params[0]);
			return detail;
		}

		@Override
		protected void onPostExecute(SoapObject detail) {
			super.onPostExecute(detail);
			try {
				// 取得<string>10月13日 中雨转小雨</string>中的数据
				String date = detail.getProperty(7).toString();
				// 将"10月13日 中雨转小雨"拆分成两个数组
				String[] date_array = date.split(" ");
				TextView today_text = (TextView) findViewById(R.id.wea_today_date);
				// 当日日期
				today_text.setText(date_array[0]);

				// 取得<string>江苏 无锡</string>中的数据
				TextView city_text = (TextView) findViewById(R.id.wea_city_text);
				city_text.setText(detail.getProperty(1).toString());

				TextView today_weather = (TextView) findViewById(R.id.wea_today_weather);
				today_weather.setText(date_array[1]);

				// 取得<string>15℃/21℃</string>中的数据
				TextView temperature = (TextView) findViewById(R.id.wea_temperature);// 气温
				temperature.setText(detail.getProperty(8).toString());

				// 取得<string>今日天气实况：气温：20℃；风向/风力：东南风
				// 2级；湿度：79%</string>中的数据,并通过":"拆分成数组
				TextView humidity = (TextView) findViewById(R.id.wea_humidity);// 湿度
				String date1 = detail.getProperty(4).toString();
				humidity.setText(date1.split("：")[4]);

				// 取得<string>东北风3-4级</string>中的数据
				TextView wind_force = (TextView) findViewById(R.id.wea_wind_force);// 风力
				wind_force.setText(detail.getProperty(9).toString());

				// 取得<string>空气质量：良；紫外线强度：最弱</string>中的数据,并通过";"拆分,
				// 再通过":"拆分,拆分两次,取得我们需要的数据
				String date2 = detail.getProperty(5).toString();
				String[] date2_array = date2.split("；");
				// 空气质量
				TextView air_quality = (TextView) findViewById(R.id.wea_air_quality);
				air_quality.setText(date2_array[0].split("：")[1]);

				// 紫外线强度
				TextView ultraviolet_intensity = (TextView) findViewById(R.id.wea_ultraviolet_intensity);
				ultraviolet_intensity.setText(date2_array[1].split("：")[1]);

				// 设置小贴士数据
				// <string>穿衣指数：较凉爽，建议着长袖衬衫加单裤等春秋过渡装。年老体弱者宜着针织长袖衬衫、马甲和长裤。
				// 感冒指数：虽然温度适宜但风力较大，仍较易发生感冒，体质较弱的朋友请注意适当防护。
				// 运动指数：阴天，较适宜开展各种户内外运动。洗车指数：较不宜洗车，路面少量积水，如果执意擦洗汽车，
				// 要做好溅上泥水的心理准备。晾晒指数：天气阴沉，不利于水分的迅速蒸发，不太适宜晾晒。若需要晾晒，请尽量选择通风的地点。
				// 旅游指数：阴天，风稍大，但温度适宜，总体来说还是好天气。这样的天气很适宜旅游，您可以尽情享受大自然的风光。
				// 路况指数：阴天，路面比较干燥，路况较好。舒适度指数：温度适宜，风力不大，您在这样的天气条件下，会感到比较清爽和舒适。
				// 空气污染指数：气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。紫外线指数：属弱紫外线辐射天气，
				// 无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。</string>
				String[] tips = detail.getProperty(6).toString().split("\n");
				TextView tips_text = (TextView) findViewById(R.id.wea_tips);
				tips_text.setText(tips[0]);

				// 设置当日图片
				ImageView image = (ImageView) findViewById(R.id.wea_today_img);
				int icon = parseIcon(detail.getProperty(10).toString());
				image.setImageResource(icon);

				// 取得第二天的天气情况
				String[] date_str = detail.getProperty(12).toString()
						.split(" ");
				TextView tomorrow_date = (TextView) findViewById(R.id.wea_tomorrow_date);
				tomorrow_date.setText(date_str[0]);

				TextView tomorrow_temperature = (TextView) findViewById(R.id.wea_tomorrow_temperature);
				tomorrow_temperature.setText(detail.getProperty(13).toString());

				TextView tomorrow_weather = (TextView) findViewById(R.id.wea_tomorrow_weather);
				tomorrow_weather.setText(date_str[1]);

				ImageView tomorrow_image = (ImageView) findViewById(R.id.wea_tomorrow_image);
				int icon1 = parseIcon(detail.getProperty(15).toString());
				tomorrow_image.setImageResource(icon1);

				// 取得第三天的天气情况
				String[] date_str1 = detail.getProperty(17).toString()
						.split(" ");
				TextView afterday_date = (TextView) findViewById(R.id.wea_afterday_date);
				afterday_date.setText(date_str1[0]);

				TextView afterday_temperature = (TextView) findViewById(R.id.wea_afterday_temperature);
				afterday_temperature.setText(detail.getProperty(18).toString());

				TextView afterday_weather = (TextView) findViewById(R.id.wea_afterday_weather);
				afterday_weather.setText(date_str1[1]);

				ImageView afterday_image = (ImageView) findViewById(R.id.wea_afterday_image);
				int icon2 = parseIcon(detail.getProperty(20).toString());
				afterday_image.setImageResource(icon2);

				// 取得第四天的天气情况
				String[] date_str3 = detail.getProperty(22).toString()
						.split(" ");
				TextView nextday_date = (TextView) findViewById(R.id.wea_nextday_date);
				nextday_date.setText(date_str3[0]);

				TextView nextday_temperature = (TextView) findViewById(R.id.wea_nextday_temperature);
				nextday_temperature.setText(detail.getProperty(23).toString());

				TextView nextday_weather = (TextView) findViewById(R.id.wea_nextday_weather);
				nextday_weather.setText(date_str3[1]);

				ImageView nextday_image = (ImageView) findViewById(R.id.wea_nextday_image);
				int icon3 = parseIcon(detail.getProperty(25).toString());
				nextday_image.setImageResource(icon3);

			} catch (Exception e) {
				showToast(detail.getProperty(0).toString().split("。")[0]);
			}
		}

	}

	protected void refresh(String city) {
		SoapObject detail = WebServiceUtil.getWeatherByCity(city);
		// Log.e(TAG, detail.toString());
		try {
			// 取得<string>10月13日 中雨转小雨</string>中的数据
			String date = detail.getProperty(7).toString();
			// 将"10月13日 中雨转小雨"拆分成两个数组
			String[] date_array = date.split(" ");
			TextView today_text = (TextView) this
					.findViewById(R.id.wea_today_date);
			// 当日日期
			today_text.setText(date_array[0]);

			// 取得<string>江苏 无锡</string>中的数据
			TextView city_text = (TextView) findViewById(R.id.wea_city_text);
			city_text.setText(detail.getProperty(1).toString());

			TextView today_weather = (TextView) findViewById(R.id.wea_today_weather);
			today_weather.setText(date_array[1]);

			// 取得<string>15℃/21℃</string>中的数据
			TextView temperature = (TextView) findViewById(R.id.wea_temperature);// 气温
			temperature.setText(detail.getProperty(8).toString());

			// 取得<string>今日天气实况：气温：20℃；风向/风力：东南风
			// 2级；湿度：79%</string>中的数据,并通过":"拆分成数组
			TextView humidity = (TextView) findViewById(R.id.wea_humidity);// 湿度
			String date1 = detail.getProperty(4).toString();
			humidity.setText(date1.split("：")[4]);

			// 取得<string>东北风3-4级</string>中的数据
			TextView wind_force = (TextView) findViewById(R.id.wea_wind_force);// 风力
			wind_force.setText(detail.getProperty(9).toString());

			// 取得<string>空气质量：良；紫外线强度：最弱</string>中的数据,并通过";"拆分,
			// 再通过":"拆分,拆分两次,取得我们需要的数据
			String date2 = detail.getProperty(5).toString();
			String[] date2_array = date2.split("；");
			// 空气质量
			TextView air_quality = (TextView) findViewById(R.id.wea_air_quality);
			air_quality.setText(date2_array[0].split("：")[1]);

			// 紫外线强度
			TextView ultraviolet_intensity = (TextView) findViewById(R.id.wea_ultraviolet_intensity);
			ultraviolet_intensity.setText(date2_array[1].split("：")[1]);

			// 设置小贴士数据
			// <string>穿衣指数：较凉爽，建议着长袖衬衫加单裤等春秋过渡装。年老体弱者宜着针织长袖衬衫、马甲和长裤。
			// 感冒指数：虽然温度适宜但风力较大，仍较易发生感冒，体质较弱的朋友请注意适当防护。
			// 运动指数：阴天，较适宜开展各种户内外运动。洗车指数：较不宜洗车，路面少量积水，如果执意擦洗汽车，
			// 要做好溅上泥水的心理准备。晾晒指数：天气阴沉，不利于水分的迅速蒸发，不太适宜晾晒。若需要晾晒，请尽量选择通风的地点。
			// 旅游指数：阴天，风稍大，但温度适宜，总体来说还是好天气。这样的天气很适宜旅游，您可以尽情享受大自然的风光。
			// 路况指数：阴天，路面比较干燥，路况较好。舒适度指数：温度适宜，风力不大，您在这样的天气条件下，会感到比较清爽和舒适。
			// 空气污染指数：气象条件有利于空气污染物稀释、扩散和清除，可在室外正常活动。紫外线指数：属弱紫外线辐射天气，
			// 无需特别防护。若长期在户外，建议涂擦SPF在8-12之间的防晒护肤品。</string>
			String[] tips = detail.getProperty(6).toString().split("\n");
			TextView tips_text = (TextView) findViewById(R.id.wea_tips);
			tips_text.setText(tips[0]);

			// 设置当日图片
			ImageView image = (ImageView) findViewById(R.id.wea_today_img);
			int icon = parseIcon(detail.getProperty(10).toString());
			image.setImageResource(icon);

			// 取得第二天的天气情况
			String[] date_str = detail.getProperty(12).toString().split(" ");
			TextView tomorrow_date = (TextView) findViewById(R.id.wea_tomorrow_date);
			tomorrow_date.setText(date_str[0]);

			TextView tomorrow_temperature = (TextView) findViewById(R.id.wea_tomorrow_temperature);
			tomorrow_temperature.setText(detail.getProperty(13).toString());

			TextView tomorrow_weather = (TextView) findViewById(R.id.wea_tomorrow_weather);
			tomorrow_weather.setText(date_str[1]);

			ImageView tomorrow_image = (ImageView) findViewById(R.id.wea_tomorrow_image);
			int icon1 = parseIcon(detail.getProperty(15).toString());
			tomorrow_image.setImageResource(icon1);

			// 取得第三天的天气情况
			String[] date_str1 = detail.getProperty(17).toString().split(" ");
			TextView afterday_date = (TextView) findViewById(R.id.wea_afterday_date);
			afterday_date.setText(date_str1[0]);

			TextView afterday_temperature = (TextView) findViewById(R.id.wea_afterday_temperature);
			afterday_temperature.setText(detail.getProperty(18).toString());

			TextView afterday_weather = (TextView) findViewById(R.id.wea_afterday_weather);
			afterday_weather.setText(date_str1[1]);

			ImageView afterday_image = (ImageView) findViewById(R.id.wea_afterday_image);
			int icon2 = parseIcon(detail.getProperty(20).toString());
			afterday_image.setImageResource(icon2);

			// 取得第四天的天气情况
			String[] date_str3 = detail.getProperty(22).toString().split(" ");
			TextView nextday_date = (TextView) findViewById(R.id.wea_nextday_date);
			nextday_date.setText(date_str3[0]);

			TextView nextday_temperature = (TextView) findViewById(R.id.wea_nextday_temperature);
			nextday_temperature.setText(detail.getProperty(23).toString());

			TextView nextday_weather = (TextView) findViewById(R.id.wea_nextday_weather);
			nextday_weather.setText(date_str3[1]);

			ImageView nextday_image = (ImageView) findViewById(R.id.wea_nextday_image);
			int icon3 = parseIcon(detail.getProperty(25).toString());
			nextday_image.setImageResource(icon3);

		} catch (Exception e) {
			showToast(detail.getProperty(0).toString().split("。")[0]);
		}
	}

	// 工具方法，该方法负责把返回的天气图标字符串，转换为程序的图片资源ID。
	public static int parseIcon(String strIcon) {
		if (strIcon == null)
			return -1;
		if ("0.gif".equals(strIcon))
			return R.drawable.weather_0;
		if ("1.gif".equals(strIcon))
			return R.drawable.a_1;
		if ("2.gif".equals(strIcon))
			return R.drawable.a_2;
		if ("3.gif".equals(strIcon))
			return R.drawable.a_3;
		if ("4.gif".equals(strIcon))
			return R.drawable.a_4;
		if ("5.gif".equals(strIcon))
			return R.drawable.a_5;
		if ("6.gif".equals(strIcon))
			return R.drawable.a_6;
		if ("7.gif".equals(strIcon))
			return R.drawable.a_7;
		if ("8.gif".equals(strIcon))
			return R.drawable.a_8;
		if ("9.gif".equals(strIcon))
			return R.drawable.a_9;
		if ("10.gif".equals(strIcon))
			return R.drawable.a_10;
		if ("11.gif".equals(strIcon))
			return R.drawable.a_11;
		if ("12.gif".equals(strIcon))
			return R.drawable.a_12;
		if ("13.gif".equals(strIcon))
			return R.drawable.a_13;
		if ("14.gif".equals(strIcon))
			return R.drawable.a_14;
		if ("15.gif".equals(strIcon))
			return R.drawable.a_15;
		if ("16.gif".equals(strIcon))
			return R.drawable.a_16;
		if ("17.gif".equals(strIcon))
			return R.drawable.a_17;
		if ("18.gif".equals(strIcon))
			return R.drawable.a_18;
		if ("19.gif".equals(strIcon))
			return R.drawable.a_19;
		if ("20.gif".equals(strIcon))
			return R.drawable.a_20;
		if ("21.gif".equals(strIcon))
			return R.drawable.a_21;
		if ("22.gif".equals(strIcon))
			return R.drawable.a_22;
		if ("23.gif".equals(strIcon))
			return R.drawable.a_23;
		if ("24.gif".equals(strIcon))
			return R.drawable.a_24;
		if ("25.gif".equals(strIcon))
			return R.drawable.a_25;
		if ("26.gif".equals(strIcon))
			return R.drawable.a_26;
		if ("27.gif".equals(strIcon))
			return R.drawable.a_27;
		if ("28.gif".equals(strIcon))
			return R.drawable.a_28;
		if ("29.gif".equals(strIcon))
			return R.drawable.a_29;
		if ("30.gif".equals(strIcon))
			return R.drawable.a_30;
		if ("31.gif".equals(strIcon))
			return R.drawable.a_31;
		return 0;
	}

	/**
	 * Toast
	 * 
	 * @param string
	 */
	public void showToast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();

	}
}
