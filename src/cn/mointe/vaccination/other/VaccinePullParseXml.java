package cn.mointe.vaccination.other;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import cn.mointe.vaccination.domain.Vaccine;
import cn.mointe.vaccination.domain.VaccineSpecfication;

public class VaccinePullParseXml {

	/**
	 * 解析疫苗产品说明书
	 * 
	 * @param xml
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static List<VaccineSpecfication> getVaccineSpecfications(
			InputStream xml) throws XmlPullParserException, IOException {
		List<VaccineSpecfication> vaccineSpecfications = null;
		VaccineSpecfication vaccineSpecfication = null;
		XmlPullParser pullParser = Xml.newPullParser();
		// 为Pull解析器设置要解析的XML数据
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				vaccineSpecfications = new ArrayList<VaccineSpecfication>();
				break;
			case XmlPullParser.START_TAG:
				if ("vaccineSpecification".equals(pullParser.getName())) {
					vaccineSpecfication = new VaccineSpecfication();
				} else if ("id".equals(pullParser.getName())) {
					String id = pullParser.nextText();
					vaccineSpecfication.setId(id);
				} else if ("vaccine".equals(pullParser.getName())) {
					String vaccine = pullParser.nextText();
					vaccineSpecfication.setVaccine(vaccine);
				} else if ("vaccine_name".equals(pullParser.getName())) {
					String vaccine_name = pullParser.nextText();
					vaccineSpecfication.setVaccine_name(vaccine_name);
				} else if ("product_name".equals(pullParser.getName())) {
					String product_name = pullParser.nextText();
					vaccineSpecfication.setProduct_name(product_name);
				} else if ("manufacturers".equals(pullParser.getName())) {
					String manufacturers = pullParser.nextText();
					vaccineSpecfication.setManufacturers(manufacturers);
				} else if ("price".equals(pullParser.getName())) {
					String price = pullParser.nextText();
					vaccineSpecfication.setPrice(price);
				} else if ("functionanduse".equals(pullParser.getName())) {
					String functionanduse = pullParser.nextText();
					vaccineSpecfication.setFunctionanduse(functionanduse);
				} else if ("description".equals(pullParser.getName())) {
					String description = pullParser.nextText();
					vaccineSpecfication.setDescription(description);
				} else if ("inoculation_object".equals(pullParser.getName())) {
					String inoculation_object = pullParser.nextText();
					vaccineSpecfication
							.setInoculation_object(inoculation_object);
				} else if ("vaccination_image_url".equals(pullParser.getName())) {
					String vaccination_image_url = pullParser.nextText();
					vaccineSpecfication
							.setVaccination_image_url(vaccination_image_url);
				} else if ("product_specification".equals(pullParser.getName())) {
					String product_specification = pullParser.nextText();
					vaccineSpecfication
							.setProduct_specification(product_specification);
				} else if ("immune_procedure".equals(pullParser.getName())) {
					String immune_procedure = pullParser.nextText();
					vaccineSpecfication.setImmune_procedure(immune_procedure);
				} else if ("adverse_reaction".equals(pullParser.getName())) {
					String adverse_reaction = pullParser.nextText();
					vaccineSpecfication.setAdverse_reaction(adverse_reaction);
				} else if ("contraindication".equals(pullParser.getName())) {
					String contraindication = pullParser.nextText();
					vaccineSpecfication.setContraindication(contraindication);
				} else if ("caution".equals(pullParser.getName())) {
					String caution = pullParser.nextText();
					vaccineSpecfication.setCaution(caution);
				} else if ("license_number".equals(pullParser.getName())) {
					String license_number = pullParser.nextText();
					vaccineSpecfication.setLicense_number(license_number);
				} else if ("validity_period".equals(pullParser.getName())) {
					String validity_period = pullParser.nextText();
					vaccineSpecfication.setValidity_period(validity_period);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("vaccineSpecification".equals(pullParser.getName())) {
					vaccineSpecfications.add(vaccineSpecfication);
					vaccineSpecfication = null;
				}
				break;

			}
			event = pullParser.next();
		}
		return vaccineSpecfications;
	}

	/**
	 * 解析疫苗库
	 * 
	 * @param xml
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static List<Vaccine> getVaccines(InputStream xml)
			throws XmlPullParserException, IOException {
		List<Vaccine> vaccines = null;
		Vaccine vaccine = null;
		XmlPullParser pullParser = Xml.newPullParser();
		// 为Pull解析器设置要解析的XML数据
		pullParser.setInput(xml, "UTF-8");
		int event = pullParser.getEventType();
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				vaccines = new ArrayList<Vaccine>();
				break;
			case XmlPullParser.START_TAG:
				if ("vaccine".equals(pullParser.getName())) {
					int id = Integer.valueOf(pullParser.getAttributeValue(0));
					vaccine = new Vaccine();
					vaccine.setId(id);
				} else if ("vaccine_name".equals(pullParser.getName())) {
					String vaccine_name = pullParser.nextText();
					vaccine.setVaccine_name(vaccine_name);
				} else if ("vaccine_code".equals(pullParser.getName())) {
					String vaccine_code = pullParser.nextText();
					vaccine.setVaccine_code(vaccine_code);
				} else if ("vaccine_type".equals(pullParser.getName())) {
					String vaccine_type = pullParser.nextText();
					vaccine.setVaccine_type(vaccine_type);
				} else if ("vaccine_prevent_disease".equals(pullParser
						.getName())) {
					String vaccine_prevent_disease = pullParser.nextText();
					vaccine.setVaccine_prevent_disease(vaccine_prevent_disease);
				} else if ("inoculation_object".equals(pullParser.getName())) {
					String inoculation_object = pullParser.nextText();
					vaccine.setInoculation_object(inoculation_object);
				} else if ("caution".equals(pullParser.getName())) {
					String caution = pullParser.nextText();
					vaccine.setCaution(caution);
				} else if ("adverse_reaction".equals(pullParser.getName())) {
					String adverse_reaction = pullParser.nextText();
					vaccine.setAdverse_reaction(adverse_reaction);
				} else if ("contraindication".equals(pullParser.getName())) {
					String contraindication = pullParser.nextText();
					vaccine.setContraindication(contraindication);
				} else if ("immune_procedure".equals(pullParser.getName())) {
					String immune_procedure = pullParser.nextText();
					vaccine.setImmune_procedure(immune_procedure);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("vaccine".equals(pullParser.getName())) {
					vaccines.add(vaccine);
					vaccine = null;
				}
				break;

			}
			event = pullParser.next();
		}
		return vaccines;
	}
}
