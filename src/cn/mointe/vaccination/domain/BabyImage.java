package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class BabyImage implements Serializable {

	private static final long serialVersionUID = 7960925044677610476L;

	private int id;
	private String babyNickName;
	private String date;
	private String imgPath;

	public BabyImage() {

	}

	public BabyImage(String babyNickName, String date, String imgPath) {
		this.babyNickName = babyNickName;
		this.date = date;
		this.imgPath = imgPath;
	}

	public BabyImage(int id, String babyNickName, String date, String imgPath) {
		this.id = id;
		this.babyNickName = babyNickName;
		this.date = date;
		this.imgPath = imgPath;
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

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

}
