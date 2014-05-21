package cn.mointe.vaccination.domain;

import java.io.Serializable;
import java.util.List;

public class Diary implements Serializable {

	private static final long serialVersionUID = 2415667505375473760L;

	private int id;
	private String babyNickName;
	private String date;
	private String diaryContent;

	private List<String> vaccines;
	private List<String> images;

	public Diary() {

	}

	public Diary(String babyNickName, String date, String diaryContent) {
		this.babyNickName = babyNickName;
		this.date = date;
		this.diaryContent = diaryContent;
	}

	public Diary(int id, String babyNickName, String date, String diaryContent) {
		this.id = id;
		this.babyNickName = babyNickName;
		this.date = date;
		this.diaryContent = diaryContent;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBabyNickName() {
		return babyNickName;
	}

	public void setBabyNickName(String babyNickName) {
		this.babyNickName = babyNickName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDiaryContent() {
		return diaryContent;
	}

	public void setDiaryContent(String diaryContent) {
		this.diaryContent = diaryContent;
	}

	public List<String> getVaccines() {
		return vaccines;
	}

	public void setVaccines(List<String> vaccines) {
		this.vaccines = vaccines;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

}
