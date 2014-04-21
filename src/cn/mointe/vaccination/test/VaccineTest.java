package cn.mointe.vaccination.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.test.AndroidTestCase;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.other.VaccinePullParseXml;
import cn.mointe.vaccination.tools.Log;
import cn.mointe.vaccination.tools.WeatherUtils;

public class VaccineTest extends AndroidTestCase {

	public final static String TAG = "MainActivity";

	public void testLoad() {
		VaccineDao dao = new VaccineDao(getContext());
		dao.savaVaccines();
	}

	public void testGetVaccines() throws Exception {
		InputStream vaccineXml = mContext.getResources().getAssets()
				.open("vaccine.xml");
		List<Vaccine> vaccines = VaccinePullParseXml.getVaccines(vaccineXml);
		Log.i("MainActivity", "" + vaccines.size());
		for (Vaccine vaccine : vaccines) {
			Log.i("MainActivity", vaccine.getVaccine_name()+"--"+vaccine.getVaccine_prevent_disease());
		}
	}

	public void testWeather() {
		try {
			String result = WeatherUtils.getWeatherByCityId("101190406");
			Log.i("MainActivity", "=====" + result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
