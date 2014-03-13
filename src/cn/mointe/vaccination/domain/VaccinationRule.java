package cn.mointe.vaccination.domain;

public class VaccinationRule {

	private int id;
	private String vaccineCode;
	private int moonAge;

	private String isCharge;// 是否收费 0.免费 1.收费
	private String vaccinationNumber;
	private String vaccineType;

	public VaccinationRule() {

	}

	public VaccinationRule(String vaccineCode, int moonAge, String isCharge,
			String vaccinationNumber, String vaccineType) {
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

	public String getVaccineCode() {
		return vaccineCode;
	}

	public void setVaccineCode(String vaccineCode) {
		this.vaccineCode = vaccineCode;
	}

	public int getMoonAge() {
		return moonAge;
	}

	public void setMoonAge(int moonAge) {
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
