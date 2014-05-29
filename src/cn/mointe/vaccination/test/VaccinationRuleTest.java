package cn.mointe.vaccination.test;

import java.io.InputStream;
import java.util.List;

import android.test.AndroidTestCase;
import cn.mointe.vaccination.dao.VaccinationDao;
import cn.mointe.vaccination.domain.Vaccination;
import cn.mointe.vaccination.domain.VaccinationRule;
import cn.mointe.vaccination.other.VaccinationRulePullParseXml;
import cn.mointe.vaccination.tools.Log;

public class VaccinationRuleTest extends AndroidTestCase {

	public void testVaccinationRule() throws Exception {
		InputStream vaccineXml = mContext.getResources().getAssets()
				.open("vaccination_rule.xml");
		List<VaccinationRule> list = VaccinationRulePullParseXml
				.getVaccinationRules(vaccineXml);
		for (VaccinationRule rule : list) {
			Log.i("MainActivity",
					rule.getVaccineName() + "(" + rule.getVaccinationNumber()
							+ ")");
		}
	}

	public void testCreateVaccinations() throws Exception {
		VaccinationDao mDao = new VaccinationDao(getContext());
		List<Vaccination> list = mDao.createVaccinations("2014-01-01", "宝宝");
		for (Vaccination vac : list) {
			Log.i("MainActivity",vac.getMoon_age()+"-"+ vac.getVaccine_name()+"("+vac.getVaccination_number()+")"+"-"+vac.getReserve_time());
		}
	}
}
