package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class Baby implements Serializable {

	private static final long serialVersionUID = -6278776828250207835L;

	private int id;
	private String name;
	private String birthdate;

	private String image;
	private String residence;
	private String sex;

	private String vaccination_place;
	private String vaccination_phone;
	private String is_default;

	private String cityCode;

	public Baby() {

	}

	public Baby(String name, String birthdate, String image, String residence,
			String sex, String vaccination_place, String vaccination_phone,
			String is_default, String cityCode) {
		this.name = name;
		this.birthdate = birthdate;
		this.image = image;
		this.residence = residence;
		this.sex = sex;
		this.vaccination_place = vaccination_place;
		this.vaccination_phone = vaccination_phone;
		this.is_default = is_default;
		this.cityCode = cityCode;
	}

	public Baby(int id, String name, String birthdate, String image,
			String residence, String sex, String vaccination_place,
			String vaccination_phone, String is_default, String cityCode) {
		this.id = id;
		this.name = name;
		this.birthdate = birthdate;
		this.image = image;
		this.residence = residence;
		this.sex = sex;
		this.vaccination_place = vaccination_place;
		this.vaccination_phone = vaccination_phone;
		this.is_default = is_default;
		this.cityCode = cityCode;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getResidence() {
		return residence;
	}

	public void setResidence(String residence) {
		this.residence = residence;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getVaccination_place() {
		return vaccination_place;
	}

	public void setVaccination_place(String vaccination_place) {
		this.vaccination_place = vaccination_place;
	}

	public String getVaccination_phone() {
		return vaccination_phone;
	}

	public void setVaccination_phone(String vaccination_phone) {
		this.vaccination_phone = vaccination_phone;
	}

	public String getIs_default() {
		return is_default;
	}

	public void setIs_default(String is_default) {
		this.is_default = is_default;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

}
