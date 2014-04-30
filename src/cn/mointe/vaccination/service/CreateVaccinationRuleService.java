package cn.mointe.vaccination.service;

import cn.mointe.vaccination.dao.VaccinationRuleDao;
import cn.mointe.vaccination.tools.Log;
import android.app.IntentService;
import android.content.Intent;

public class CreateVaccinationRuleService extends IntentService {
	

	public CreateVaccinationRuleService() {
		super("CreateVaccinationRuleService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		VaccinationRuleDao mRuleDao = new VaccinationRuleDao(this);
		mRuleDao.createVaccinationRule();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("MainActivity", "createVaccinationRule服务停止");
	}
	
}
