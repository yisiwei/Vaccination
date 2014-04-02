package cn.mointe.vaccination.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import cn.mointe.vaccination.domain.VaccineSpecfication;
import cn.mointe.vaccination.other.VaccinePullParseXml;

public class VaccineSpecificationDao {

	private Context mContext;

	public VaccineSpecificationDao(Context context) {
		this.mContext = context;
	}

	/**
	 * 根据疫苗查询相关产品
	 * 
	 * @param vaccineName
	 *            疫苗名称
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public List<VaccineSpecfication> getVaccineSpecficationByName(
			String vaccineName) throws IOException, XmlPullParserException {
		List<VaccineSpecfication> vaccineSpecfications = null;
		InputStream vaccineXml = null;
		if (vaccineName.equals("A群流脑疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_aqun_liunao.xml");
		} else if (vaccineName.equals("A+C群流脑疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_acqun_liunao.xml");
		} else if (vaccineName.equals("百白破疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_baibaipo.xml");
		} else if (vaccineName.equals("白破疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_baipo.xml");
		} else if (vaccineName.equals("出血热疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_chuxuere.xml");
		} else if (vaccineName.equals("23价肺炎疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_23jiafeiyan.xml");
		} else if (vaccineName.equals("Hib结合疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_hibjiehe.xml");
		} else if (vaccineName.equals("霍乱疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_huoluan.xml");
		} else if (vaccineName.equals("甲肝减毒活疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_jiaganjianduhuo.xml");
		} else if (vaccineName.equals("甲肝灭活疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_jiaganmiehuo.xml");
		} else if (vaccineName.equals("甲乙肝联合疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_jiayilianhe.xml");
		} else if (vaccineName.equals("脊灰疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_jihui.xml");
		} else if (vaccineName.equals("脊灰灭活疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_jihuimiehuo.xml");
		} else if (vaccineName.equals("卡介苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_kajiemiao.xml");
		} else if (vaccineName.equals("狂犬病疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_kuangquanbing.xml");
		} else if (vaccineName.equals("流感疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_liugan.xml");
		} else if (vaccineName.equals("轮状病毒活疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_lunzhuangbingduhuo.xml");
		} else if (vaccineName.equals("麻腮风疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_masaifeng.xml");
		} else if (vaccineName.equals("麻疹疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_mazhen.xml");
		} else if (vaccineName.equals("麻疹风疹疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_mazhenfengzhen.xml");
		} else if (vaccineName.equals("7价肺炎结合疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_7jiafeiyanjiehe.xml");
		} else if (vaccineName.equals("伤寒疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_shanghan.xml");
		} else if (vaccineName.equals("水痘疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_shuidou.xml");
		} else if (vaccineName.equals("五联疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_wulian.xml");
		} else if (vaccineName.equals("乙肝疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_yigan.xml");
		} else if (vaccineName.equals("乙脑灭活疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_yinaomiehuo.xml");
		} else if (vaccineName.equals("乙脑减毒活疫苗")) {
			vaccineXml = mContext.getResources().getAssets()
					.open("vac_yinaojianduhuo.xml");
		}

		if (null != vaccineXml) {
			vaccineSpecfications = VaccinePullParseXml
					.getVaccineSpecfications(vaccineXml);
		}
		return vaccineSpecfications;
	}

	/**
	 * 查询产品
	 * 
	 * @param productName
	 * @param manufacturers
	 * @return
	 */
	// public VaccineSpecfication queryVaccineSpecfication(String productName,
	// String manufacturers) {
	// VaccinePullParseXml xml = new VaccinePullParseXml();
	// List<VaccineSpecfication> list = xml.pullParseXML();
	// for (VaccineSpecfication vaccineSpecfication : list) {
	// if (vaccineSpecfication.getProduct_name().equals(productName)
	// && vaccineSpecfication.getManufacturers().equals(
	// manufacturers)) {
	// return vaccineSpecfication;
	// }
	// }
	// return null;
	// }

	// public VaccineSpecfication getVaccineSpecficationByVaccineName(
	// String vaccineName) {
	// VaccinePullParseXml xml = new VaccinePullParseXml();
	// List<VaccineSpecfication> list = xml.pullParseXML();
	// for (VaccineSpecfication vaccineSpecfication : list) {
	// if (vaccineSpecfication.getVaccine().equals(vaccineName)) {
	// return vaccineSpecfication;
	// }
	// }
	// return null;

	// }

	/**
	 * 根据产品名称和厂商查询产品
	 * 
	 * @param vaccineName
	 *            疫苗名称
	 * @param productName
	 *            产品名称
	 * @param manufacturers
	 *            厂商
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public VaccineSpecfication getVaccineSpecfication(String vaccineName,
			String productName, String manufacturers) throws IOException,
			XmlPullParserException {
		List<VaccineSpecfication> vaccineSpecfications = getVaccineSpecficationByName(vaccineName);

		for (VaccineSpecfication vaccineSpecfication : vaccineSpecfications) {
			if (vaccineSpecfication.getProduct_name().equals(productName)
					&& vaccineSpecfication.getManufacturers().equals(
							manufacturers)) {
				return vaccineSpecfication;
			}
		}

		return null;
	}

}
