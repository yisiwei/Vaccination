package cn.mointe.vaccination.dao;

import java.util.ArrayList;
import java.util.List;

import cn.mointe.vaccination.domain.VaccineSpecfication;
import cn.mointe.vaccination.other.VaccinePullParseXml;

public class VaccineSpecificationDao {

	public VaccineSpecificationDao() {

	}

	/**
	 * 根据疫苗查询相关产品
	 * 
	 * @param name
	 * @return
	 */
	public List<VaccineSpecfication> queryVaccineSpecficationByName(String name) {
		List<VaccineSpecfication> vaccineSpecfications = new ArrayList<VaccineSpecfication>();
		VaccinePullParseXml xml = new VaccinePullParseXml();
		List<VaccineSpecfication> list = xml.pullParseXML();
		for (VaccineSpecfication vaccineSpecfication : list) {
			if (vaccineSpecfication.getVaccine().equals(name)) {
				vaccineSpecfications.add(vaccineSpecfication);
			}
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
	public VaccineSpecfication queryVaccineSpecfication(String productName,
			String manufacturers) {
		VaccinePullParseXml xml = new VaccinePullParseXml();
		List<VaccineSpecfication> list = xml.pullParseXML();
		for (VaccineSpecfication vaccineSpecfication : list) {
			if (vaccineSpecfication.getProduct_name().equals(productName)
					&& vaccineSpecfication.getManufacturers().equals(
							manufacturers)) {
				return vaccineSpecfication;
			}
		}
		return null;
	}

	public VaccineSpecfication getVaccineSpecficationByVaccineName(
			String vaccineName) {
		VaccinePullParseXml xml = new VaccinePullParseXml();
		List<VaccineSpecfication> list = xml.pullParseXML();
		for (VaccineSpecfication vaccineSpecfication : list) {
			if (vaccineSpecfication.getVaccine().equals(vaccineName)) {
				return vaccineSpecfication;
			}
		}
		return null;

	}

}
