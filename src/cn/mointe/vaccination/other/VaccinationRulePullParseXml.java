package cn.mointe.vaccination.other;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import cn.mointe.vaccination.domain.VaccinationRule;

public class VaccinationRulePullParseXml {

	
	public static List<VaccinationRule> getVaccinationRules(InputStream xml)
			throws XmlPullParserException, IOException {
		List<VaccinationRule> vaccinationRules = null;
		VaccinationRule vaccinationRule = null;
		XmlPullParser pullParser = Xml.newPullParser();
		// 为Pull解析器设置要解析的XML数据
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				vaccinationRules = new ArrayList<VaccinationRule>();
				break;
			case XmlPullParser.START_TAG:
				if ("vaccination".equals(pullParser.getName())) {
					int id = Integer.valueOf(pullParser.getAttributeValue(0));
					vaccinationRule = new VaccinationRule();
					vaccinationRule.setId(id);
				} else if ("name".equals(pullParser.getName())) {
					String name = pullParser.nextText();
					vaccinationRule.setVaccineName(name);
				} else if ("moon_age".equals(pullParser.getName())) {
					String moonAge = pullParser.nextText();
					vaccinationRule.setMoonAge(moonAge);
				} else if ("type".equals(pullParser.getName())) {
					String type = pullParser.nextText();
					vaccinationRule.setVaccineType(type);
				} else if ("is_charge".equals(pullParser.getName())) {
					String isCharge = pullParser.nextText();
					vaccinationRule.setIsCharge(isCharge);
				} else if ("number".equals(pullParser.getName())) {
					String number = pullParser.nextText();
					vaccinationRule.setVaccinationNumber(number);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("vaccination".equals(pullParser.getName())) {
					vaccinationRules.add(vaccinationRule);
					vaccinationRule = null;
				}
				break;
			}
			event = pullParser.next();
		}
		return vaccinationRules;
	}
}
