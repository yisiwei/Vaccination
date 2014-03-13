package cn.mointe.vaccination.dao;

import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.VaccinationRule;
import cn.mointe.vaccination.provider.VaccinationProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

public class VaccinationRuleDao {

	private ContentResolver mResolver;

	public VaccinationRuleDao(Context context) {
		mResolver = context.getContentResolver();
	}

	public void addVaccinationRule(VaccinationRule rule) {
		ContentValues values = new ContentValues();

		values.put(DBHelper.RULE_COLUMN_VACCINE_CODE, rule.getVaccineCode());
		values.put(DBHelper.RULE_COLUMN_MOON_AGE, rule.getMoonAge());
		values.put(DBHelper.RULE_COLUMN_IS_CHARGE, rule.getIsCharge());

		values.put(DBHelper.RULE_COLUMN_VACCINE_TYPE, rule.getVaccineType());
		values.put(DBHelper.RULE_COLUMN_VACCINATION_NUMBER,
				rule.getVaccinationNumber());

		mResolver.insert(VaccinationProvider.CONTENT_URI, values);
	}
	
	public void loadVaccinationRule(){
		addVaccinationRule(new VaccinationRule("V01", 0, "0", "第1/3剂", "必打"));
		addVaccinationRule(new VaccinationRule("V02", 0, "0", "共1剂", "必打"));

		addVaccinationRule(new VaccinationRule("V01", 1, "0", "第2/3剂", "必打"));
		
		addVaccinationRule(new VaccinationRule("V03", 2, "0", "第1/4剂", "必打"));
		addVaccinationRule(new VaccinationRule("V04", 2, "1", "第1/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V06", 2, "1", "第1/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V07", 2, "1", "第1/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V05", 2, "1", "第1/3剂", "可选"));
		
		addVaccinationRule(new VaccinationRule("V08", 3, "0", "第1/4剂", "必打"));
		addVaccinationRule(new VaccinationRule("V03", 3, "0", "第2/4剂", "必打"));
		addVaccinationRule(new VaccinationRule("V07", 3, "1", "第2/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V09", 3, "1", "第1/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V04", 3, "1", "第2/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V06", 3, "1", "第2/4剂", "推荐"));
		
		addVaccinationRule(new VaccinationRule("V08", 4, "0", "第2/4剂", "必打"));
		addVaccinationRule(new VaccinationRule("V03", 4, "0", "第3/4剂", "必打"));
		addVaccinationRule(new VaccinationRule("V07", 4, "1", "第3/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V09", 4, "1", "第2/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V04", 4, "1", "第3/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V06", 4, "1", "第3/4剂", "推荐"));
		
		addVaccinationRule(new VaccinationRule("V08", 5, "0", "第3/4剂", "必打"));
		addVaccinationRule(new VaccinationRule("V09", 5, "1", "第3/4剂", "推荐"));
		
		addVaccinationRule(new VaccinationRule("V10", 6, "0", "第1/2剂", "必打"));
		addVaccinationRule(new VaccinationRule("V01", 6, "0", "第3/3剂", "必打"));
		addVaccinationRule(new VaccinationRule("V11", 6, "1", "第1/2剂", "可选"));
		
		addVaccinationRule(new VaccinationRule("V11", 7, "1", "第2/2剂", "可选"));

		addVaccinationRule(new VaccinationRule("V12", 8, "0", "共1剂", "必打"));
	
		addVaccinationRule(new VaccinationRule("V10", 9, "0", "第2/2剂", "必打"));
	
		addVaccinationRule(new VaccinationRule("V13", 12, "0", "第1/3剂", "必打"));
		addVaccinationRule(new VaccinationRule("V09", 12, "1", "第4/4剂", "推荐"));

		addVaccinationRule(new VaccinationRule("V05", 14, "1", "第2/3剂", "可选"));

		addVaccinationRule(new VaccinationRule("V08", 18, "0", "第4/4剂", "必打"));
		addVaccinationRule(new VaccinationRule("V14", 18, "0", "第1/2剂", "必打"));
		addVaccinationRule(new VaccinationRule("V15", 18, "0", "第1/2剂", "必打"));
		addVaccinationRule(new VaccinationRule("V04", 18, "1", "第4/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V06", 18, "1", "第4/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V07", 18, "1", "第4/4剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V16", 18, "1", "第1/2剂", "可选"));
		
		addVaccinationRule(new VaccinationRule("V13", 24, "0", "第2/3剂", "必打"));
		addVaccinationRule(new VaccinationRule("V15", 24, "0", "第2/2剂", "必打"));
		addVaccinationRule(new VaccinationRule("V17", 24, "1", "共1剂", "推荐"));
		addVaccinationRule(new VaccinationRule("V05", 24, "1", "第3/3剂", "可选"));
		
		addVaccinationRule(new VaccinationRule("V18", 36, "0", "共1剂", "必打"));
	
		addVaccinationRule(new VaccinationRule("V03", 48, "0", "第4/4剂", "必打"));

	
	}

}
