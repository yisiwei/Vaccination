package cn.mointe.vaccination.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import cn.mointe.vaccination.db.DBHelper;
import cn.mointe.vaccination.domain.VaccinationRule;
import cn.mointe.vaccination.other.VaccinationRulePullParseXml;
import cn.mointe.vaccination.provider.VaccinationProvider;
import cn.mointe.vaccination.tools.Constants;

public class VaccinationRuleDao {

	private ContentResolver mResolver;
	private Context mContext;

	public VaccinationRuleDao(Context context) {
		mResolver = context.getContentResolver();
		this.mContext = context;
	}

	public void addVaccinationRule(VaccinationRule rule) {
		ContentValues values = new ContentValues();

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
					Constants.MOON_AGE[i], Constants.CHARGE_STANDARD[i],
					Constants.VACCINATION_NUMBER[i], Constants.VACCINE_TYPE2[i]));
		}
	}

	/**
	 * 查询疫苗基础规则表
	 * 
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public List<VaccinationRule> getVaccinationRules()
			throws XmlPullParserException, IOException {
		InputStream inputStream = mContext.getResources().getAssets()
				.open("vaccination_rule.xml");
		List<VaccinationRule> vaccinationRules = VaccinationRulePullParseXml
				.getVaccinationRules(inputStream);
		return vaccinationRules;
	}

}
