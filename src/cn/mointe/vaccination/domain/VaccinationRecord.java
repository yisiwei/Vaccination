package cn.mointe.vaccination.domain;

import java.io.Serializable;
import java.util.List;

public class VaccinationRecord implements Serializable {

	private static final long serialVersionUID = -7120646512018712572L;

	private String id;
	private String date;
	private List<String> vaccines;

	public VaccinationRecord() {

	}

	public VaccinationRecord(String id, String date, List<String> vaccines) {
		this.id = id;
		this.date = date;
		this.vaccines = vaccines;
	}

	public VaccinationRecord(String date, List<String> vaccines) {
		this.date = date;
		this.vaccines = vaccines;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<String> getVaccines() {
		return vaccines;
	}

	public void setVaccines(List<String> vaccines) {
		this.vaccines = vaccines;
	}

}
