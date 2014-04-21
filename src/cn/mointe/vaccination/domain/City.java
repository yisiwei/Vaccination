package cn.mointe.vaccination.domain;

import java.io.Serializable;
import java.util.Map;

public class City implements Serializable {

	private static final long serialVersionUID = 4517611918758392731L;

	private String cityName;
	// private List<String> countys;

	private Map<String, String> countys;

	public City() {

	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public Map<String, String> getCountys() {
		return countys;
	}

	public void setCountys(Map<String, String> countys) {
		this.countys = countys;
	}

	// public List<String> getCountys() {
	// return countys;
	// }
	//
	// public void setCountys(List<String> countys) {
	// this.countys = countys;
	// }

}
