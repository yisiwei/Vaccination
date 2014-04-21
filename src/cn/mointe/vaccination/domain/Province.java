package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class Province implements Serializable {

	private static final long serialVersionUID = 736457704214831055L;

	private int id;
	private String proviceName;

	public Province() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProviceName() {
		return proviceName;
	}

	public void setProviceName(String proviceName) {
		this.proviceName = proviceName;
	}

}
