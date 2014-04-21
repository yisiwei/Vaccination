package cn.mointe.vaccination.test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.test.AndroidTestCase;
import cn.mointe.vaccination.domain.City;
import cn.mointe.vaccination.domain.Province;
import cn.mointe.vaccination.other.CityPullParseXml;
import cn.mointe.vaccination.tools.Constants;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.PackageUtil;

public class ProvinceTest extends AndroidTestCase {

	public void testGetProvinces() throws Exception {
		InputStream xml = this.getContext().getResources().getAssets()
				.open("province.xml");
		List<Province> provinces = CityPullParseXml.getProvinces(xml);
		for (Province province : provinces) {
			Log.i("MainActivity", "provinceName=" + province.getProviceName());
		}
	}

	public void testGetCitys() throws Exception {
		InputStream xml = this.getContext().getResources().getAssets()
				.open("city_beijing.xml");
		List<City> citys = CityPullParseXml.getCitys(xml);
		for (City city : citys) {
			Log.i("MainActivity", "provinceName=" + city.getCityName());
			// List<String> countys = city.getCountys();
			Map<String, String> countys = city.getCountys();
			for (Map.Entry<String, String> entry : countys.entrySet()) {
				Log.i("MainActivity", "Code=" + entry.getKey() + "--City="
						+ entry.getValue());
			}
		}
	}

	public void testIsServiceRunning() {
		boolean b = PackageUtil.isServiceRunning(getContext(),
				Constants.REMIND_SERVICE);
		Log.i("MainActivity", "run:" + b);
	}
}
