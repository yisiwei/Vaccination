package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class Vaccine implements Serializable {

	private static final long serialVersionUID = 6669508526486978228L;

	private int id;
	private String vaccine_name; // 疫苗名称
	private String vaccine_code;// 疫苗Code
	private String vaccine_type;// 疫苗种类
	private String vaccine_intro;// 疫苗简介

	public Vaccine() {

	}

	public Vaccine(int id, String vaccine_name, String vaccine_code,
			String vaccine_type, String vaccine_intro) {
		this.id = id;
		this.vaccine_name = vaccine_name;
		this.vaccine_code = vaccine_code;
		this.vaccine_type = vaccine_type;
		this.vaccine_intro = vaccine_intro;
	}

	public Vaccine(String vaccine_name, String vaccine_code,
			String vaccine_type, String vaccine_intro) {
		this.vaccine_name = vaccine_name;
		this.vaccine_code = vaccine_code;
		this.vaccine_type = vaccine_type;
		this.vaccine_intro = vaccine_intro;
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

	public String getVaccine_intro() {
		return vaccine_intro;
	}

	public void setVaccine_intro(String vaccine_intro) {
		this.vaccine_intro = vaccine_intro;
	}

}
