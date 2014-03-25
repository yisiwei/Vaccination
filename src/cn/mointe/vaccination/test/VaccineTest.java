package cn.mointe.vaccination.test;

import cn.mointe.vaccination.dao.VaccineDao;
import android.test.AndroidTestCase;

public class VaccineTest extends AndroidTestCase {

	public final static String TAG = "MainActivity";

	public void testLoad() {
		VaccineDao dao = new VaccineDao(getContext());
		dao.savaVaccines();
	}

}
