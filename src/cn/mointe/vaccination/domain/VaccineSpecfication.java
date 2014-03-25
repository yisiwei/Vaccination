package cn.mointe.vaccination.domain;

import java.io.Serializable;

public class VaccineSpecfication implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String vaccine; //疫苗
	private String vaccine_name; // 疫苗名称
	
	private String product_name; // 商品名称
	private String manufacturers; // 生产厂家
	private String price; // 疫苗价格
	
	private String functionanduse; // 作用与用途
	private String description; // 疫苗性状
	private String inoculation_object; // 接种对象
	
	private String vaccination_image_url; // 疫苗产品图片
	private String product_specification; // 产品规格
	private String immune_procedure; // 免疫程序
	
	private String adverse_reaction; // 不良反应
	private String contraindication; // 禁忌
	private String caution; // 注意事项
	
	private String license_number; // 批准文号
	private String validity_period; // 有效期

	public VaccineSpecfication() {

	}

	public VaccineSpecfication(String id, String vaccine, String vaccine_name,
			String product_name, String manufacturers, String price,
			String functionanduse, String description,
			String inoculation_object, String vaccination_image_url,
			String product_specification, String immune_procedure,
			String adverse_reaction, String contraindication, String caution,
			String license_number, String validity_period) {
		this.id = id;
		this.vaccine = vaccine;
		this.vaccine_name = vaccine_name;
		this.product_name = product_name;
		this.manufacturers = manufacturers;
		this.price = price;
		this.functionanduse = functionanduse;
		this.description = description;
		this.inoculation_object = inoculation_object;
		this.vaccination_image_url = vaccination_image_url;
		this.product_specification = product_specification;
		this.immune_procedure = immune_procedure;
		this.adverse_reaction = adverse_reaction;
		this.contraindication = contraindication;
		this.caution = caution;
		this.license_number = license_number;
		this.validity_period = validity_period;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVaccine() {
		return vaccine;
	}

	public void setVaccine(String vaccine) {
		this.vaccine = vaccine;
	}

	public String getVaccine_name() {
		return vaccine_name;
	}

	public void setVaccine_name(String vaccine_name) {
		this.vaccine_name = vaccine_name;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getManufacturers() {
		return manufacturers;
	}

	public void setManufacturers(String manufacturers) {
		this.manufacturers = manufacturers;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getFunctionanduse() {
		return functionanduse;
	}

	public void setFunctionanduse(String functionanduse) {
		this.functionanduse = functionanduse;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInoculation_object() {
		return inoculation_object;
	}

	public void setInoculation_object(String inoculation_object) {
		this.inoculation_object = inoculation_object;
	}

	public String getVaccination_image_url() {
		return vaccination_image_url;
	}

	public void setVaccination_image_url(String vaccination_image_url) {
		this.vaccination_image_url = vaccination_image_url;
	}

	public String getProduct_specification() {
		return product_specification;
	}

	public void setProduct_specification(String product_specification) {
		this.product_specification = product_specification;
	}

	public String getImmune_procedure() {
		return immune_procedure;
	}

	public void setImmune_procedure(String immune_procedure) {
		this.immune_procedure = immune_procedure;
	}

	public String getAdverse_reaction() {
		return adverse_reaction;
	}

	public void setAdverse_reaction(String adverse_reaction) {
		this.adverse_reaction = adverse_reaction;
	}

	public String getContraindication() {
		return contraindication;
	}

	public void setContraindication(String contraindication) {
		this.contraindication = contraindication;
	}

	public String getCaution() {
		return caution;
	}

	public void setCaution(String caution) {
		this.caution = caution;
	}

	public String getLicense_number() {
		return license_number;
	}

	public void setLicense_number(String license_number) {
		this.license_number = license_number;
	}

	public String getValidity_period() {
		return validity_period;
	}

	public void setValidity_period(String validity_period) {
		this.validity_period = validity_period;
	}

}
