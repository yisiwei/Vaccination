package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class VaccineRemind implements Serializable {

	private static final long serialVersionUID = -4569040655240356086L;

	private int id;
	private String vaccineName;
	private String vaccinePreventableDisease;

	private String weekBeforeTip;
	private String dayBeforeTip;
	private String todayTipPrecautions;

	private String todayTipAdverseReaction;

	public VaccineRemind() {

	}

	public VaccineRemind(int id, String vaccineName,
			String vaccinePreventableDisease, String weekBeforeTip,
			String dayBeforeTip, String todayTipPrecautions,
			String todayTipAdverseReaction) {
		this.id = id;
		this.vaccineName = vaccineName;
		this.vaccinePreventableDisease = vaccinePreventableDisease;
		this.weekBeforeTip = weekBeforeTip;
		this.dayBeforeTip = dayBeforeTip;
		this.todayTipPrecautions = todayTipPrecautions;
		this.todayTipAdverseReaction = todayTipAdverseReaction;
	}

	public VaccineRemind(String vaccineName, String vaccinePreventableDisease,
			String weekBeforeTip, String dayBeforeTip,
			String todayTipPrecautions, String todayTipAdverseReaction) {
		this.vaccineName = vaccineName;
		this.vaccinePreventableDisease = vaccinePreventableDisease;
		this.weekBeforeTip = weekBeforeTip;
		this.dayBeforeTip = dayBeforeTip;
		this.todayTipPrecautions = todayTipPrecautions;
		this.todayTipAdverseReaction = todayTipAdverseReaction;
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

	public String getVaccinePreventableDisease() {
		return vaccinePreventableDisease;
	}

	public void setVaccinePreventableDisease(String vaccinePreventableDisease) {
		this.vaccinePreventableDisease = vaccinePreventableDisease;
	}

	public String getWeekBeforeTip() {
		return weekBeforeTip;
	}

	public void setWeekBeforeTip(String weekBeforeTip) {
		this.weekBeforeTip = weekBeforeTip;
	}

	public String getDayBeforeTip() {
		return dayBeforeTip;
	}

	public void setDayBeforeTip(String dayBeforeTip) {
		this.dayBeforeTip = dayBeforeTip;
	}

	public String getTodayTipPrecautions() {
		return todayTipPrecautions;
	}

	public void setTodayTipPrecautions(String todayTipPrecautions) {
		this.todayTipPrecautions = todayTipPrecautions;
	}

	public String getTodayTipAdverseReaction() {
		return todayTipAdverseReaction;
	}

	public void setTodayTipAdverseReaction(String todayTipAdverseReaction) {
		this.todayTipAdverseReaction = todayTipAdverseReaction;
	}

}
