package cn.mointe.vaccination.test;

import java.io.InputStream;
import java.util.List;

import android.test.AndroidTestCase;
import android.util.Log;
import cn.mointe.vaccination.domain.VaccineSpecfication;
import cn.mointe.vaccination.other.VaccinePullParseXml;

public class VaccineSpecficationTest extends AndroidTestCase {

	public void testGetVaccineSpecfications() throws Exception{
		InputStream vaccineXml = mContext.getResources().getAssets()
				.open("vac_7jiafeiyanjiehe.xml");
		List<VaccineSpecfication> vaccineSpecfications =  VaccinePullParseXml.getVaccineSpecfications(vaccineXml);
		for(VaccineSpecfication specfication : vaccineSpecfications){
			Log.i("MainActivity", vaccineSpecfications.size()+"--"+specfication.getVaccine()+"--"
					+specfication.getVaccine_name()+"--"+specfication.getProduct_name());
		}
	}
}
