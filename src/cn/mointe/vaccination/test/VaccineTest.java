package cn.mointe.vaccination.test;

import java.io.InputStream;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;
import cn.mointe.vaccination.dao.VaccineDao;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.other.VaccinePullParseXml;

public class VaccineTest extends AndroidTestCase {

	public final static String TAG = "MainActivity";

	public void testLoad() {
		VaccineDao dao = new VaccineDao(getContext());
		dao.savaVaccines();
	}
	
	public void testGetVaccines() throws Exception{
		InputStream vaccineXml = mContext.getResources().getAssets()
				.open("vaccine.xml");
		List<Vaccine> vaccines = VaccinePullParseXml.getVaccines(vaccineXml);
		Log.i("MainActivity", ""+vaccines.size());
		for (Vaccine vaccine : vaccines) {
			Log.i("MainActivity", vaccine.getVaccine_name());
		}
	}

}
