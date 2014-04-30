package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class VaccinationInfo implements Serializable {

	private static final long serialVersionUID = 7236733702497747745L;

	private int vacid;
	private String name;
	private String date;
	private String number;
	private String before;
	private String after;
	private String finishDate;

	public VaccinationInfo() {

	}

	public int getVacid() {
		return vacid;
	}

	public void setVacid(int vacid) {
		this.vacid = vacid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getBefore() {
		return before;
	}

	public void setBefore(String before) {
		this.before = before;
	}

	public String getAfter() {
		return after;
	}

	public void setAfter(String after) {
		this.after = after;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

}
