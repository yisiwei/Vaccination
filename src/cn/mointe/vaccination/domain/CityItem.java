package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class CityItem implements Serializable {

	private static final long serialVersionUID = 3449431368928702704L;

	private String code;
	private String cityName;

	public CityItem() {

	}

	public CityItem(String code, String cityName) {
		super();
		this.code = code;
		this.cityName = cityName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	@Override
	public String toString() {
		return cityName;
	}

}
