package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class Vaccine implements Serializable {

	private static final long serialVersionUID = 6669508526486978228L;

	private int id;
	private String vaccine_name; // 疫苗名称
	private String vaccine_code;// 疫苗Code
	
	private String vaccine_type;// 疫苗种类
	private String vaccine_prevent_disease;// 预防疾病
	private String inoculation_object; // 接种对象
	
	private String caution;// 注意事项
	private String adverse_reaction;// 不良反应
	private String contraindication;// 禁忌
	
	private String immune_procedure;// 免疫程序

	public Vaccine() {

	}

	public Vaccine(String vaccine_name, String vaccine_code,
			String vaccine_type, String vaccine_prevent_disease) {
		this.vaccine_name = vaccine_name;
		this.vaccine_code = vaccine_code;
		this.vaccine_type = vaccine_type;
		this.vaccine_prevent_disease = vaccine_prevent_disease;
	}

	public Vaccine(int id, String vaccine_name, String vaccine_code,
			String vaccine_type, String vaccine_prevent_disease) {
		this.id = id;
		this.vaccine_name = vaccine_name;
		this.vaccine_code = vaccine_code;
		this.vaccine_type = vaccine_type;
		this.vaccine_prevent_disease = vaccine_prevent_disease;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getVaccine_name() {
		return vaccine_name;
	}

	public void setVaccine_name(String vaccine_name) {
		this.vaccine_name = vaccine_name;
	}

	public String getVaccine_code() {
		return vaccine_code;
	}

	public void setVaccine_code(String vaccine_code) {
		this.vaccine_code = vaccine_code;
	}

	public String getVaccine_type() {
		return vaccine_type;
	}

	public void setVaccine_type(String vaccine_type) {
		this.vaccine_type = vaccine_type;
	}

	public String getVaccine_prevent_disease() {
		return vaccine_prevent_disease;
	}

	public void setVaccine_prevent_disease(String vaccine_prevent_disease) {
		this.vaccine_prevent_disease = vaccine_prevent_disease;
	}

	public String getInoculation_object() {
		return inoculation_object;
	}

	public void setInoculation_object(String inoculation_object) {
		this.inoculation_object = inoculation_object;
	}

	public String getCaution() {
		return caution;
	}

	public void setCaution(String caution) {
		this.caution = caution;
	}

	public String getAdverse_reaction() {
		return adverse_reaction;
	}

	public void setAdverse_reaction(String adverse_reaction) {
		this.adverse_reaction = adverse_reaction;
	}

	public String getContraindication() {
		return contraindication;
	}

	public void setContraindication(String contraindication) {
		this.contraindication = contraindication;
	}

	public String getImmune_procedure() {
		return immune_procedure;
	}

	public void setImmune_procedure(String immune_procedure) {
		this.immune_procedure = immune_procedure;
	}

}
