package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class Vaccination implements Serializable {

	private static final long serialVersionUID = -7181758319131164138L;

	private int id;
	private String vaccine_name;// 疫苗名称
	private String reserve_time;// 预约时间

	private String finish_time;// 完成时间
	private String moon_age;// 月龄
	private String vaccine_type;// 疫苗类型(一类/二类)

	private String charge_standard;// 收费情况（收费/免费）
	private String vaccination_number;// 第几次接种（第1/3剂）
	private String baby_nickname;// 宝宝昵称

	public Vaccination() {

	}

	public Vaccination(String vaccine_name, String reserve_time,
			String finish_time, String moon_age, String vaccine_type,
			String charge_standard, String vaccination_number,
			String baby_nickname) {
		this.vaccine_name = vaccine_name;
		this.reserve_time = reserve_time;
		this.finish_time = finish_time;
		this.moon_age = moon_age;
		this.vaccine_type = vaccine_type;
		this.charge_standard = charge_standard;
		this.vaccination_number = vaccination_number;
		this.baby_nickname = baby_nickname;
	}

	public Vaccination(String vaccine_name, String reserve_time,
			String moon_age, String vaccine_type, String charge_standard,
			String vaccination_number, String baby_nickname) {
		this.vaccine_name = vaccine_name;
		this.reserve_time = reserve_time;
		this.moon_age = moon_age;
		this.vaccine_type = vaccine_type;
		this.charge_standard = charge_standard;
		this.vaccination_number = vaccination_number;
		this.baby_nickname = baby_nickname;
	}

	public Vaccination(int id, String vaccine_name, String reserve_time,
			String finish_time, String moon_age, String vaccine_type,
			String charge_standard, String vaccination_number,
			String baby_nickname) {
		super();
		this.id = id;
		this.vaccine_name = vaccine_name;
		this.reserve_time = reserve_time;
		this.finish_time = finish_time;
		this.moon_age = moon_age;
		this.vaccine_type = vaccine_type;
		this.charge_standard = charge_standard;
		this.vaccination_number = vaccination_number;
		this.baby_nickname = baby_nickname;
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

	public String getReserve_time() {
		return reserve_time;
	}

	public void setReserve_time(String reserve_time) {
		this.reserve_time = reserve_time;
	}

	public String getFinish_time() {
		return finish_time;
	}

	public void setFinish_time(String finish_time) {
		this.finish_time = finish_time;
	}

	public String getMoon_age() {
		return moon_age;
	}

	public void setMoon_age(String moon_age) {
		this.moon_age = moon_age;
	}

	public String getVaccine_type() {
		return vaccine_type;
	}

	public void setVaccine_type(String vaccine_type) {
		this.vaccine_type = vaccine_type;
	}

	public String getCharge_standard() {
		return charge_standard;
	}

	public void setCharge_standard(String charge_standard) {
		this.charge_standard = charge_standard;
	}

	public String getVaccination_number() {
		return vaccination_number;
	}

	public void setVaccination_number(String vaccination_number) {
		this.vaccination_number = vaccination_number;
	}

	public String getBaby_nickname() {
		return baby_nickname;
	}

	public void setBaby_nickname(String baby_nickname) {
		this.baby_nickname = baby_nickname;
	}

}
