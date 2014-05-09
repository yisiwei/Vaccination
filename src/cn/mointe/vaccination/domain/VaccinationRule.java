package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class VaccinationRule implements Serializable {

	private static final long serialVersionUID = -1135934184670952356L;

	private int id;
	private String vaccineName;// 疫苗名称
	private String vaccineCode;// 疫苗Code

	private String moonAge;// 月龄
	private String isCharge;// 是否收费 
	private String vaccinationNumber;// 接种剂次

	private String vaccineType;// 疫苗类型 一类、二类

	public VaccinationRule() {

	}

	public VaccinationRule(String vaccineName, String vaccineCode,
			String moonAge, String isCharge, String vaccinationNumber,
			String vaccineType) {
		this.vaccineName = vaccineName;
		this.vaccineCode = vaccineCode;
		this.moonAge = moonAge;
		this.isCharge = isCharge;
		this.vaccinationNumber = vaccinationNumber;
		this.vaccineType = vaccineType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVaccineName() {
		return vaccineName;
	}

	public void setVaccineName(String vaccineName) {
		this.vaccineName = vaccineName;
	}

	public String getVaccineCode() {
		return vaccineCode;
	}

	public void setVaccineCode(String vaccineCode) {
		this.vaccineCode = vaccineCode;
	}

	public String getMoonAge() {
		return moonAge;
	}

	public void setMoonAge(String moonAge) {
		this.moonAge = moonAge;
	}

	public String getIsCharge() {
		return isCharge;
	}

	public void setIsCharge(String isCharge) {
		this.isCharge = isCharge;
	}

	public String getVaccinationNumber() {
		return vaccinationNumber;
	}

	public void setVaccinationNumber(String vaccinationNumber) {
		this.vaccinationNumber = vaccinationNumber;
	}

	public String getVaccineType() {
		return vaccineType;
	}

	public void setVaccineType(String vaccineType) {
		this.vaccineType = vaccineType;
	}

}
