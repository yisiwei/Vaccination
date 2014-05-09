package cn.mointe.vaccination.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.VaccinationRule;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.Constants;

public class VaccinationRuleDao {

	private ContentResolver mResolver;

	public VaccinationRuleDao(Context context) {
		mResolver = context.getContentResolver();
	}

	public void addVaccinationRule(VaccinationRule rule) {
		ContentValues values = new ContentValues();

		values.put(DBHelper.RULE_COLUMN_VACCINE_CODE, rule.getVaccineCode());

		values.put(DBHelper.RULE_COLUMN_VACCINE_NAME, rule.getVaccineName());
		values.put(DBHelper.RULE_COLUMN_MOON_AGE, rule.getMoonAge());
		values.put(DBHelper.RULE_COLUMN_IS_CHARGE, rule.getIsCharge());

		values.put(DBHelper.RULE_COLUMN_VACCINE_TYPE, rule.getVaccineType());
		values.put(DBHelper.RULE_COLUMN_VACCINATION_NUMBER,
				rule.getVaccinationNumber());

		mResolver.insert(VaccinationProvider.CONTENT_URI, values);
	}

	// 生成疫苗规则表数据
	public void createVaccinationRule() {
		for (int i = 0; i < Constants.VACCINE_NAME.length; i++) {
			addVaccinationRule(new VaccinationRule(Constants.VACCINE_NAME[i],
					null, Constants.MOON_AGE[i], Constants.CHARGE_STANDARD[i],
					Constants.VACCINATION_NUMBER[i], Constants.VACCINE_TYPE2[i]));
		}

	}

}
