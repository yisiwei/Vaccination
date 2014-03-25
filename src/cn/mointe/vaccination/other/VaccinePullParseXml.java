package cn.mointe.vaccination.other;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import cn.mointe.vaccination.domain.VaccineSpecfication;

import android.util.Xml;

public class VaccinePullParseXml {

	public List<VaccineSpecfication> pullParseXML() {

		List<VaccineSpecfication> list = null;
		VaccineSpecfication vaccineSpecfication = null;

		try {
			// 获取XmlPullParser的实例
			XmlPullParser xmlPullParser = Xml.newPullParser();

			InputStream xml = this.getClass().getClassLoader()
					.getResourceAsStream("vaccineSpecification.xml");
			// 设置输入流 xml文件
			xmlPullParser.setInput(xml, "UTF-8");
			// Thread.currentThread().getContextClassLoader()
			// .getResourceAsStream("vaccineSpecification.xml")

			// 开始
			int eventType = xmlPullParser.getEventType();

			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
				// 文档开始
				case XmlPullParser.START_DOCUMENT:
					list = new ArrayList<VaccineSpecfication>();
					break;
				// 开始节点
				case XmlPullParser.START_TAG:
					// 判断如果其节点为vaccineSpecification
					if ("vaccineSpecification".equals(nodeName)) {
						// 实例化student对象
						vaccineSpecfication = new VaccineSpecfication();
					} else if ("id".equals(nodeName)) {
						// 设置ID
						vaccineSpecfication.setId(xmlPullParser.nextText());
					} else if ("vaccine".equals(nodeName)) {
						vaccineSpecfication
								.setVaccine(xmlPullParser.nextText());
					} else if ("vaccine_name".equals(nodeName)) {
						// 设置vaccine_name
						vaccineSpecfication.setVaccine_name(xmlPullParser
								.nextText());
					} else if ("product_name".equals(nodeName)) {
						// 设置product_name
						vaccineSpecfication.setProduct_name(xmlPullParser
								.nextText());
					} else if ("manufacturers".equals(nodeName)) {
						// 设置manufacturers
						vaccineSpecfication.setManufacturers(xmlPullParser
								.nextText());

					} else if ("price".equals(nodeName)) {
						// 设置vaccine_price
						vaccineSpecfication.setPrice(xmlPullParser.nextText());
					} else if ("functionanduse".equals(nodeName)) {
						// 设置vaccine_functionanduse
						vaccineSpecfication.setFunctionanduse(xmlPullParser
								.nextText());
					} else if ("description".equals(nodeName)) {
						// 设置vaccine_description
						vaccineSpecfication.setDescription(xmlPullParser
								.nextText());
					} else if ("inoculation_object".equals(nodeName)) {
						// 设置inoculation_object
						vaccineSpecfication.setInoculation_object(xmlPullParser
								.nextText());
					} else if ("vaccination_image_rul".equals(nodeName)) {
						// vaccination_image_rul
						vaccineSpecfication
								.setVaccination_image_url(xmlPullParser
										.nextText());
					} else if ("product_specification".equals(nodeName)) {
						// 设置product_specification
						vaccineSpecfication
								.setProduct_specification(xmlPullParser
										.nextText());
					} else if ("immune_procedure".equals(nodeName)) {
						// 设置immune_procedure
						vaccineSpecfication.setImmune_procedure(xmlPullParser
								.nextText());
					} else if ("adverse_reaction".equals(nodeName)) {
						// 设置adverse_reaction
						vaccineSpecfication.setAdverse_reaction(xmlPullParser
								.nextText());
					} else if ("contraindication".equals(nodeName)) {
						// 设置contraindication
						vaccineSpecfication.setContraindication(xmlPullParser
								.nextText());
					} else if ("caution".equals(nodeName)) {
						// 设置caution
						vaccineSpecfication
								.setCaution(xmlPullParser.nextText());
					} else if ("license_number".equals(nodeName)) {
						// 设置license_number
						vaccineSpecfication.setLicense_number(xmlPullParser
								.nextText());
					} else if ("validity_period".equals(nodeName)) {
						// 设置validity_period
						vaccineSpecfication.setValidity_period(xmlPullParser
								.nextText());
					}
					break;
				// 结束节点
				case XmlPullParser.END_TAG:
					if ("vaccineSpecification".equals(nodeName)) {
						list.add(vaccineSpecfication);
						vaccineSpecfication = null;
					}
					break;
				default:
					break;
				}
				eventType = xmlPullParser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

}
